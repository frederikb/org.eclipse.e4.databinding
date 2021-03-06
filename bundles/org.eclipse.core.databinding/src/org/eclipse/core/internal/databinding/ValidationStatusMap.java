/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matthew Hall - bug 226289
 *******************************************************************************/

package org.eclipse.core.internal.databinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.MapDiff;
import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;

/**
 * @since 1.0
 * 
 */
public class ValidationStatusMap extends ObservableMap<Binding<?, ?>, IStatus> {

	private boolean isDirty = true;

	private final WritableList<Binding<?, ?>> bindings;

	private List<IObservableValue<IStatus>> dependencies = new ArrayList<IObservableValue<IStatus>>();

	private IChangeListener markDirtyChangeListener = new IChangeListener() {
		public void handleChange(ChangeEvent event) {
			markDirty();
		}
	};

	/**
	 * @param realm
	 * @param bindings
	 */
	public ValidationStatusMap(Realm realm, WritableList<Binding<?, ?>> bindings) {
		super(realm, new HashMap<Binding<?, ?>, IStatus>());
		this.bindings = bindings;
		bindings.addChangeListener(markDirtyChangeListener);
	}

	public Object getKeyType() {
		return Binding.class;
	}

	public Object getValueType() {
		return IStatus.class;
	}

	protected void getterCalled() {
		recompute();
		super.getterCalled();
	}

	private void markDirty() {
		// since we are dirty, we don't need to listen anymore
		removeElementChangeListener();
		final Map<Binding<?, ?>, IStatus> oldMap = wrappedMap;
		// lazy computation of diff
		MapDiff<Binding<?, ?>, IStatus> mapDiff = new MapDiff<Binding<?, ?>, IStatus>() {
			private MapDiff<Binding<?, ?>, IStatus> cachedDiff = null;

			private void ensureCached() {
				if (cachedDiff == null) {
					recompute();
					cachedDiff = Diffs.computeMapDiff(oldMap, wrappedMap);
				}
			}

			public Set<Binding<?, ?>> getAddedKeys() {
				ensureCached();
				return cachedDiff.getAddedKeys();
			}

			public Set<Binding<?, ?>> getChangedKeys() {
				ensureCached();
				return cachedDiff.getChangedKeys();
			}

			public IStatus getNewValue(Object key) {
				ensureCached();
				return cachedDiff.getNewValue(key);
			}

			public IStatus getOldValue(Object key) {
				ensureCached();
				return cachedDiff.getOldValue(key);
			}

			public Set<Binding<?, ?>> getRemovedKeys() {
				ensureCached();
				return cachedDiff.getRemovedKeys();
			}
		};
		wrappedMap = new HashMap<Binding<?, ?>, IStatus>();
		isDirty = true;
		fireMapChange(mapDiff);
	}

	private void recompute() {
		if (isDirty) {
			Map<Binding<?, ?>, IStatus> newContents = new HashMap<Binding<?, ?>, IStatus>();
			for (Iterator<Binding<?, ?>> it = bindings.iterator(); it.hasNext();) {
				Binding<?, ?> binding = it.next();
				IObservableValue<IStatus> validationError = binding
						.getValidationStatus();
				dependencies.add(validationError);
				validationError.addChangeListener(markDirtyChangeListener);
				IStatus validationStatusValue = validationError.getValue();
				newContents.put(binding, validationStatusValue);
			}
			wrappedMap.putAll(newContents);
			isDirty = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.databinding.observable.list.ObservableList#dispose()
	 */
	public synchronized void dispose() {
		bindings.removeChangeListener(markDirtyChangeListener);
		removeElementChangeListener();
		super.dispose();
	}

	private void removeElementChangeListener() {
		for (Iterator<IObservableValue<IStatus>> it = dependencies.iterator(); it
				.hasNext();) {
			IObservableValue<IStatus> observableValue = it.next();
			observableValue.removeChangeListener(markDirtyChangeListener);
		}
	}

	public synchronized void addChangeListener(IChangeListener listener) {
		// this ensures that the next change will be seen by the new listener.
		recompute();
		super.addChangeListener(listener);
	}

	public synchronized void addMapChangeListener(
			IMapChangeListener<Binding<?, ?>, IStatus> listener) {
		// this ensures that the next change will be seen by the new listener.
		recompute();
		super.addMapChangeListener(listener);
	}

}
