/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matthew Hall - bug 146397
 *******************************************************************************/

package org.eclipse.core.databinding.observable;

/**
 * Class that provides listener handling for three types: ChangeEvent,
 * StaleEvent, and DisposeEvent. This class can be used as-is or can be
 * extended. There are two reasons why you should extend this class:
 * <P>
 * 1. To add more listener types 2. To override the addFirstListener and
 * removeLastListener methods
 * <P>
 * This class can be inherited (e.g. AbstractObservable) or can be delegated to
 * (e.g. ObservableList).
 * 
 * @since 1.0
 * 
 */
public abstract class ChangeSupport {

	/**
	 * @since 1.5
	 */
	protected ListenerList<IChangeListener> genericListenerList = null;

	/**
	 * @since 1.5
	 */
	protected ListenerList<IStaleListener> staleListenerList = null;

	/**
	 * @since 1.5
	 */
	protected ListenerList<IDisposeListener> disposeListenerList = null;

	/**
	 * @param realm
	 * 
	 * @deprecated Realm is no longer used by this class, remove the parameter
	 */
	public ChangeSupport(Realm realm) {
		this();
	}

	/**
	 * @since 1.5
	 */
	public ChangeSupport() {
	}

	/**
	 * 
	 * @param listenerType
	 * @param listener
	 * @deprecated use one of the more specific methods, addChangeListener,
	 *             addStaleListener, addDisposeListener
	 */
	public void addListener(Object listenerType,
			IObservablesListener<?> listener) {
		if (listenerType == AbstractChangeEvent.TYPE) {
			addChangeListener((IChangeListener) listener);
		} else if (listenerType == StaleEvent.TYPE) {
			addStaleListener((IStaleListener) listener);
		} else if (listenerType == DisposeEvent.TYPE) {
			addDisposeListener((IDisposeListener) listener);
		} else {
			// This class does not fire any other types.
		}
	}

	/**
	 * @param listenerType
	 * @param listener
	 * @deprecated use one of the more specific methods, removeChangeListener,
	 *             removeStaleListener, removeDisposeListener
	 */
	public void removeListener(Object listenerType,
			IObservablesListener<?> listener) {
		if (listenerType == AbstractChangeEvent.TYPE) {
			removeChangeListener((IChangeListener) listener);
		} else if (listenerType == StaleEvent.TYPE) {
			removeStaleListener((IStaleListener) listener);
		} else if (listenerType == DisposeEvent.TYPE) {
			removeDisposeListener((IDisposeListener) listener);
		} else {
			// This class does not fire any other types.
		}
	}

	/**
	 * @param event
	 * @deprecated use one of the more specific methods, addChangeListener,
	 *             addStaleListener, addDisposeListener
	 */
	// It is OK to suppress warnings in deprecated methods
	@SuppressWarnings("unchecked")
	public <EV extends ObservableEvent<EV, L>, L extends IObservablesListener<L>> void fireEvent(
			ObservableEvent<EV, L> event) {
		if (event.getListenerType() == AbstractChangeEvent.TYPE) {
			if (genericListenerList != null) {
				genericListenerList
						.fireEvent((ObservableEvent<ChangeEvent, IChangeListener>) event);
			}
		} else if (event.getListenerType() == StaleEvent.TYPE) {
			if (staleListenerList != null) {
				staleListenerList
						.fireEvent((ObservableEvent<StaleEvent, IStaleListener>) event);
			}
		} else if (event.getListenerType() == DisposeEvent.TYPE) {
			if (disposeListenerList != null) {
				disposeListenerList
						.fireEvent((ObservableEvent<DisposeEvent, IDisposeListener>) event);
			}
		} else {
			// This class does not fire any other types.
			throw new IllegalArgumentException();
		}
	}

	/**
	 * @param listener
	 */
	public synchronized void addChangeListener(IChangeListener listener) {
		addListener(getGenericListenerList(), listener);
	}

	/**
	 * @param listener
	 */
	public synchronized void removeChangeListener(IChangeListener listener) {
		if (genericListenerList != null) {
			removeListener(genericListenerList, listener);
		}
	}

	/**
	 * @param listener
	 */
	public synchronized void addStaleListener(IStaleListener listener) {
		addListener(getStaleListenerList(), listener);
	}

	/**
	 * @param listener
	 */
	public synchronized void removeStaleListener(IStaleListener listener) {
		if (staleListenerList != null) {
			removeListener(staleListenerList, listener);
		}
	}

	/**
	 * @param listener
	 * @since 1.2
	 */
	public synchronized void addDisposeListener(IDisposeListener listener) {
		getDisposeListenerList().add(listener);
	}

	/**
	 * @param listener
	 * @since 1.2
	 */
	public synchronized void removeDisposeListener(IDisposeListener listener) {
		if (disposeListenerList != null) {
			disposeListenerList.remove(listener);
		}
	}

	private ListenerList<IChangeListener> getGenericListenerList() {
		if (genericListenerList == null) {
			genericListenerList = new ListenerList<IChangeListener>();
		}
		return genericListenerList;
	}

	private ListenerList<IStaleListener> getStaleListenerList() {
		if (staleListenerList == null) {
			staleListenerList = new ListenerList<IStaleListener>();
		}
		return staleListenerList;
	}

	private ListenerList<IDisposeListener> getDisposeListenerList() {
		if (disposeListenerList == null) {
			disposeListenerList = new ListenerList<IDisposeListener>();
		}
		return disposeListenerList;
	}

	/**
	 * @param event
	 * @since 1.5
	 */
	protected void fireChange(ChangeEvent event) {
		if (genericListenerList != null) {
			genericListenerList.fireEvent(event);
		}
	}

	/**
	 * @param event
	 * @since 1.5
	 */
	protected void fireStale(StaleEvent event) {
		if (staleListenerList != null) {
			staleListenerList.fireEvent(event);
		}
	}

	/**
	 * @param disposeEvent
	 * @since 1.5
	 */
	public void fireDispose(DisposeEvent disposeEvent) {
		if (disposeListenerList != null) {
			disposeListenerList.fireEvent(disposeEvent);
		}
	}

	/**
	 * @param listenerList
	 * @param listener
	 * @since 1.5
	 */
	protected <EV extends ObservableEvent<EV, L>, L extends IObservablesListener<L>> void addListener(
			ListenerList<L> listenerList, L listener) {
		boolean hadListeners = hasListeners();
		listenerList.add(listener);
		if (!hadListeners && hasListeners()) {
			firstListenerAdded();
		}
	}

	/**
	 * @param listenerList
	 * @param listener
	 * @since 1.5
	 */
	protected <EV extends ObservableEvent<EV, L>, L extends IObservablesListener<L>> void removeListener(
			ListenerList<L> listenerList, L listener) {
		boolean hadListeners = hasListeners();
		listenerList.remove(listener);
		if (hadListeners && !hasListeners()) {
			this.lastListenerRemoved();
		}
	}

	/**
	 * Note the 'dispose' listeners are not included. This is because this
	 * method is used to see if anyone still needs this observable. Listening
	 * for when an observable is disposed is not indicating use of the
	 * observable but merely a desire to clean things up.
	 * 
	 * @return true if there are listeners, false if no listeners or only
	 *         'dispose' listeners
	 */
	protected boolean hasListeners() {
		return ((genericListenerList != null && genericListenerList
				.hasListeners()) || (staleListenerList != null && staleListenerList
				.hasListeners()));
	}

	/**
	 * 
	 */
	protected void firstListenerAdded() {
	}

	/**
	 * 
	 */
	protected void lastListenerRemoved() {
	}

	/**
	 * 
	 */
	protected void dispose() {
		genericListenerList = null;
		staleListenerList = null;
		disposeListenerList = null;
	}

	/**
	 * Releases all listener references
	 */
	// protected void dispose() {
	// genericChangeManager = null;
	// staleChangeManager = null;
	// disposeChangeManager = null;
	// }
}