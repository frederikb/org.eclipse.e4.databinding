/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brad Reynolds - bug 164653
 *     Matthew Hall - bugs 118516, 146397, 226289, 246103, 249526, 264307,
 *                    349038
 *******************************************************************************/

package org.eclipse.core.databinding.observable.map;

import java.util.AbstractMap;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.ChangeSupport;
import org.eclipse.core.databinding.observable.DisposeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IDisposeListener;
import org.eclipse.core.databinding.observable.IStaleListener;
import org.eclipse.core.databinding.observable.ListenerList;
import org.eclipse.core.databinding.observable.ObservableTracker;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.StaleEvent;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.AssertionFailedException;

/**
 * 
 * <p>
 * This class is thread safe. All state accessing methods must be invoked from
 * the {@link Realm#isCurrent() current realm}. Methods for adding and removing
 * listeners may be invoked from any thread.
 * </p>
 * 
 * @param <K>
 *            type of the keys to the map
 * @param <V>
 *            type of the values in the map
 * @since 1.0
 */
public abstract class AbstractObservableMap<K, V> extends AbstractMap<K, V>
		implements IObservableMap<K, V> {

	private final class PrivateChangeSupport extends ChangeSupport {
		protected ListenerList<IMapChangeListener<K, V>> mapChangeListenerList = null;

		protected void firstListenerAdded() {
			AbstractObservableMap.this.firstListenerAdded();
		}

		protected void lastListenerRemoved() {
			AbstractObservableMap.this.lastListenerRemoved();
		}

		public synchronized void addMapChangeListener(
				IMapChangeListener<K, V> listener) {
			addListener(getMapChangeListenerList(), listener);
		}

		/**
		 * @param listener
		 */
		public synchronized void removeMapChangeListener(
				IMapChangeListener<K, V> listener) {
			if (mapChangeListenerList != null) {
				removeListener(mapChangeListenerList, listener);
			}
		}

		private ListenerList<IMapChangeListener<K, V>> getMapChangeListenerList() {
			if (mapChangeListenerList == null) {
				mapChangeListenerList = new ListenerList<IMapChangeListener<K, V>>();
			}
			return mapChangeListenerList;
		}

		@Override
		protected boolean hasListeners() {
			return (mapChangeListenerList != null && mapChangeListenerList
					.hasListeners()) || super.hasListeners();
		}

		/**
		 * @param mapChangeEvent
		 */
		public void fireMapChange(MapChangeEvent<K, V> mapChangeEvent) {
			if (mapChangeListenerList != null) {
				mapChangeListenerList.fireEvent(mapChangeEvent);
			}
		}
	}

	private final Realm realm;
	private PrivateChangeSupport changeSupport;
	private volatile boolean disposed = false;

	private boolean stale;

	/**
	 */
	public AbstractObservableMap() {
		this(Realm.getDefault());
	}

	/**
	 * 
	 */
	protected void lastListenerRemoved() {
	}

	/**
	 * 
	 */
	protected void firstListenerAdded() {
	}

	/**
	 * @param realm
	 */
	public AbstractObservableMap(Realm realm) {
		Assert.isNotNull(realm, "Realm cannot be null"); //$NON-NLS-1$
		ObservableTracker.observableCreated(this);
		this.realm = realm;
		changeSupport = new PrivateChangeSupport();
	}

	/**
	 * @param listener
	 */
	public synchronized void addMapChangeListener(
			IMapChangeListener<K, V> listener) {
		if (!disposed) {
			changeSupport.addMapChangeListener(listener);
		}
	}

	public synchronized void removeMapChangeListener(
			IMapChangeListener<K, V> listener) {
		if (!disposed) {
			changeSupport.removeMapChangeListener(listener);
		}
	}

	public synchronized void addChangeListener(IChangeListener listener) {
		if (!disposed) {
			changeSupport.addChangeListener(listener);
		}
	}

	public synchronized void addStaleListener(IStaleListener listener) {
		if (!disposed) {
			changeSupport.addStaleListener(listener);
		}
	}

	/**
	 * @return whether the observable map has listeners registered
	 * @since 1.2
	 */
	protected synchronized boolean hasListeners() {
		return !disposed && changeSupport.hasListeners();
	}

	/**
	 * @since 1.2
	 */
	public synchronized void addDisposeListener(IDisposeListener listener) {
		if (!disposed) {
			changeSupport.addDisposeListener(listener);
		}
	}

	/**
	 * @since 1.2
	 */
	public synchronized void removeDisposeListener(IDisposeListener listener) {
		if (!disposed) {
			changeSupport.removeDisposeListener(listener);
		}
	}

	/**
	 * @since 1.2
	 */
	public synchronized boolean isDisposed() {
		return disposed;
	}

	public synchronized void dispose() {
		if (!disposed) {
			disposed = true;
			changeSupport.fireEvent(new DisposeEvent(this));
			// Fastest way to release all our listener references
			changeSupport = null;
		}
	}

	public Realm getRealm() {
		return realm;
	}

	public boolean isStale() {
		checkRealm();
		return stale;
	}

	/**
	 * @since 1.2
	 */
	public Object getKeyType() {
		return null;
	}

	/**
	 * @since 1.2
	 */
	public Object getValueType() {
		return null;
	}

	public synchronized void removeChangeListener(IChangeListener listener) {
		if (!disposed) {
			changeSupport.removeChangeListener(listener);
		}
	}

	public synchronized void removeStaleListener(IStaleListener listener) {
		if (!disposed) {
			changeSupport.removeStaleListener(listener);
		}
	}

	/**
	 * Sets the stale state. Must be invoked from the current realm.
	 * 
	 * @param stale
	 */
	public void setStale(boolean stale) {
		checkRealm();
		this.stale = stale;
		if (stale) {
			fireStale();
		}
	}

	/**
	 * Fires stale events. Must be invoked from current realm.
	 */
	protected void fireStale() {
		checkRealm();
		changeSupport.fireEvent(new StaleEvent(this));
	}

	/**
	 * Fires change events. Must be invoked from current realm.
	 */
	protected void fireChange() {
		checkRealm();
		changeSupport.fireEvent(new ChangeEvent(this));
	}

	/**
	 * Fires map change events. Must be invoked from current realm.
	 * 
	 * @param diff
	 */
	protected void fireMapChange(MapDiff<? extends K, ? extends V> diff) {
		checkRealm();
		fireChange();
		changeSupport.fireMapChange(new MapChangeEvent<K, V>(this, diff));
	}

	/**
	 * Asserts that the realm is the current realm.
	 * 
	 * @see Realm#isCurrent()
	 * @throws AssertionFailedException
	 *             if the realm is not the current realm
	 */
	protected void checkRealm() {
		Assert.isTrue(getRealm().isCurrent(),
				"This operation must be run within the observable's realm"); //$NON-NLS-1$
	}

	/**
	 * This is a default implementation that should ideally be overridden to use
	 * a properly typed Class field. This implementation checks to see if the
	 * key type is of type Class and, if it is, it assumes it is the class of
	 * the keys and makes an unchecked cast.
	 * <P>
	 * This method should always be overridden to provide an implementation that
	 * never returns null.
	 * 
	 * @return the class of the keys, if possible, or null if this is not
	 *         possible
	 * @since 1.5
	 */
	public Class<K> getKeyClass() {
		Object keyType = getKeyType();
		if (keyType instanceof Class) {
			return (Class<K>) keyType;
		}
		return null;
	}

	/**
	 * This is a default implementation that should ideally be overridden to use
	 * a properly typed Class field. This implementation checks to see if the
	 * value type is of type Class and, if it is, it assumes it is the class of
	 * the values and makes an unchecked cast.
	 * <P>
	 * This method should always be overridden to provide an implementation that
	 * never returns null.
	 * 
	 * @return the class of the values, if possible, or null if this is not
	 *         possible
	 * @since 1.5
	 */
	public Class<V> getValueClass() {
		Object valueType = getKeyType();
		if (valueType instanceof Class) {
			return (Class<V>) valueType;
		}
		return null;
	}
}
