/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matthew Hall - bugs 118516, 255734
 *     Chris Audley - bug 273265
 *******************************************************************************/

package org.eclipse.core.databinding.observable;

/**
 * Listener management implementation. Exposed to subclasses in form of
 * {@link AbstractObservable} and {@link ChangeSupport}.
 * 
 * @param <EV>
 * @param <L>
 * 
 * @since 1.0
 * 
 * @deprecated Use ListenerList directly
 * 
 */
public/* package */class ChangeManager<EV extends ObservableEvent<EV, L>, L extends IObservablesListener<L>> {

	ListenerList<L> listenerList = null;

	/**
	 * @param listener
	 */
	public void addListener(L listener) {
		listenerList.add(listener);
	}

	/**
	 * @param listener
	 */
	public void removeListener(L listener) {
		if (listenerList != null) {
			listenerList.remove(listener);
		}
	}

	/**
	 * @return true if there any any listeners in the list, false if no
	 *         listeners
	 */
	public boolean hasListeners() {
		return (listenerList.size() > 0);
	}

	/**
	 * @param event
	 */
	public void fireEvent(ObservableEvent<EV, L> event) {
		for (L listener : listenerList.getListeners()) {
			event.dispatch(listener);
		}
	}

	/**
	 * 
	 */
	public void dispose() {
		listenerList = null;
	}

	protected Object clone() throws CloneNotSupportedException {
		ChangeManager duplicate = (ChangeManager) super.clone();
		duplicate.listenerList = null;
		return duplicate;
	}
}
