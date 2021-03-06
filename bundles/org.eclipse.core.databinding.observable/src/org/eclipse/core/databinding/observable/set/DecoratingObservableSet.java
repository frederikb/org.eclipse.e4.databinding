/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 237718)
 *     Matthew Hall - bug 246626
 *******************************************************************************/

package org.eclipse.core.databinding.observable.set;

import org.eclipse.core.databinding.observable.DecoratingObservableCollection;
import org.eclipse.core.databinding.observable.ListenerList;

/**
 * An observable set which decorates another observable set.
 * 
 * @param <E>
 * 
 * @since 1.2
 */
public class DecoratingObservableSet<E> extends
		DecoratingObservableCollection<E> implements IObservableSet<E> {

	private IObservableSet<E> decorated;

	private ISetChangeListener<E> setChangeListener;

	protected ListenerList<ISetChangeListener<E>> setChangeListenerList = null;

	/**
	 * Constructs a DecoratingObservableSet which decorates the given
	 * observable.
	 * 
	 * @param decorated
	 *            the observable set being decorated
	 * @param disposeDecoratedOnDispose
	 */
	public DecoratingObservableSet(IObservableSet<E> decorated,
			boolean disposeDecoratedOnDispose) {
		super(decorated, disposeDecoratedOnDispose);
		this.decorated = decorated;
	}

	public void clear() {
		getterCalled();
		decorated.clear();
	}

	public synchronized void addSetChangeListener(ISetChangeListener<E> listener) {
		addListener(getSetListenerList(), listener);
	}

	public synchronized void removeSetChangeListener(
			ISetChangeListener<E> listener) {
		if (setChangeListenerList != null) {
			removeListener(setChangeListenerList, listener);
		}
	}

	protected boolean hasListeners() {
		return (setChangeListenerList != null && setChangeListenerList
				.hasListeners()) || super.hasListeners();
	}

	private ListenerList<ISetChangeListener<E>> getSetListenerList() {
		if (setChangeListenerList == null) {
			setChangeListenerList = new ListenerList<ISetChangeListener<E>>();
		}
		return setChangeListenerList;
	}

	protected void fireSetChange(SetDiff<? extends E> diff) {
		// fire general change event first
		super.fireChange();
		if (setChangeListenerList != null) {
			setChangeListenerList.fireEvent(new SetChangeEvent<E>(this, diff));
		}
	}

	protected void fireChange() {
		throw new RuntimeException(
				"fireChange should not be called, use fireSetChange() instead"); //$NON-NLS-1$
	}

	protected void firstListenerAdded() {
		if (setChangeListener == null) {
			setChangeListener = new ISetChangeListener<E>() {
				public void handleSetChange(SetChangeEvent<E> event) {
					DecoratingObservableSet.this.handleSetChange(event);
				}
			};
		}
		decorated.addSetChangeListener(setChangeListener);
		super.firstListenerAdded();
	}

	protected void lastListenerRemoved() {
		super.lastListenerRemoved();
		if (setChangeListener != null) {
			decorated.removeSetChangeListener(setChangeListener);
			setChangeListener = null;
		}
	}

	/**
	 * Called whenever a SetChangeEvent is received from the decorated
	 * observable. By default, this method fires the set change event again,
	 * with the decorating observable as the event source. Subclasses may
	 * override to provide different behavior.
	 * 
	 * @param event
	 *            the change event received from the decorated observable
	 */
	protected void handleSetChange(final SetChangeEvent<E> event) {
		fireSetChange(event.diff);
	}

	public synchronized void dispose() {
		if (decorated != null && setChangeListener != null) {
			decorated.removeSetChangeListener(setChangeListener);
		}
		decorated = null;
		setChangeListener = null;
		super.dispose();
	}
}
