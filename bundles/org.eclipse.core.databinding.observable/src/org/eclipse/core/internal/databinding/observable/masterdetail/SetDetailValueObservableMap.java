/*******************************************************************************
 * Copyright (c) 2010 Ovidio Mallo and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ovidio Mallo - initial API and implementation (bug 305367)
 ******************************************************************************/

package org.eclipse.core.internal.databinding.observable.masterdetail;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.observable.IObserving;
import org.eclipse.core.databinding.observable.IStaleListener;
import org.eclipse.core.databinding.observable.ObservableTracker;
import org.eclipse.core.databinding.observable.StaleEvent;
import org.eclipse.core.databinding.observable.map.ComputedObservableMap;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.internal.databinding.identity.IdentitySet;

/**
 * @param <M>
 *            type of the master observables in the master set
 * @param <E>
 *            type of the detail elements
 * @since 1.4
 */
public class SetDetailValueObservableMap<M, E> extends
		ComputedObservableMap<M, E> implements IObserving {

	private IObservableFactory<? super M, IObservableValue<E>> observableValueFactory;

	private Map<M, IObservableValue<E>> detailObservableValueMap = new HashMap<M, IObservableValue<E>>();

	private IdentitySet<IObservableValue<?>> staleDetailObservables = new IdentitySet<IObservableValue<?>>();

	private IStaleListener detailStaleListener = new IStaleListener() {
		public void handleStale(StaleEvent staleEvent) {
			addStaleDetailObservable((IObservableValue<?>) staleEvent
					.getObservable());
		}
	};

	/**
	 * @param masterKeySet
	 * @param observableValueFactory
	 * @param detailValueType
	 */
	public SetDetailValueObservableMap(
			IObservableSet<M> masterKeySet,
			IObservableFactory<? super M, IObservableValue<E>> observableValueFactory,
			Object detailValueType) {
		super(masterKeySet, detailValueType);
		this.observableValueFactory = observableValueFactory;
	}

	protected void hookListener(final M addedKey) {
		final IObservableValue<E> detailValue = getDetailObservableValue(addedKey);

		detailValue.addValueChangeListener(new IValueChangeListener<E>() {
			public void handleValueChange(ValueChangeEvent<E> event) {
				if (!event.getObservableValue().isStale()) {
					staleDetailObservables.remove(detailValue);
				}

				fireSingleChange(addedKey, event.diff.getOldValue(),
						event.diff.getNewValue());
			}
		});

		detailValue.addStaleListener(detailStaleListener);
	}

	protected void unhookListener(Object removedKey) {
		if (isDisposed()) {
			return;
		}

		IObservableValue<E> detailValue = detailObservableValueMap
				.remove(removedKey);
		staleDetailObservables.remove(detailValue);
		detailValue.dispose();
	}

	private IObservableValue<E> getDetailObservableValue(M masterKey) {
		IObservableValue<E> detailValue = detailObservableValueMap
				.get(masterKey);

		if (detailValue == null) {
			ObservableTracker.setIgnore(true);
			try {
				detailValue = observableValueFactory
						.createObservable(masterKey);

				detailObservableValueMap.put(masterKey, detailValue);

				if (detailValue.isStale()) {
					addStaleDetailObservable(detailValue);
				}
			} finally {
				ObservableTracker.setIgnore(false);
			}
		}

		return detailValue;
	}

	private void addStaleDetailObservable(IObservableValue<?> detailObservable) {
		boolean wasStale = isStale();
		staleDetailObservables.add(detailObservable);
		if (!wasStale) {
			fireStale();
		}
	}

	protected E doGet(M key) {
		IObservableValue<E> detailValue = getDetailObservableValue(key);
		return detailValue.getValue();
	}

	protected E doPut(M key, E value) {
		IObservableValue<E> detailValue = getDetailObservableValue(key);
		E oldValue = detailValue.getValue();
		detailValue.setValue(value);
		return oldValue;
	}

	public boolean containsKey(Object key) {
		getterCalled();

		return keySet().contains(key);
	}

	public E remove(Object key) {
		checkRealm();

		if (!containsKey(key)) {
			return null;
		}

		IObservableValue<E> detailValue = getDetailObservableValue((M) key);
		E oldValue = detailValue.getValue();

		keySet().remove(key);

		return oldValue;
	}

	public int size() {
		getterCalled();

		return keySet().size();
	}

	public boolean isStale() {
		return super.isStale() || staleDetailObservables != null
				&& !staleDetailObservables.isEmpty();
	}

	public Object getObserved() {
		return keySet();
	}

	public synchronized void dispose() {
		super.dispose();

		observableValueFactory = null;
		detailObservableValueMap = null;
		detailStaleListener = null;
		staleDetailObservables = null;
	}

	private void getterCalled() {
		ObservableTracker.getterCalled(this);
	}
}
