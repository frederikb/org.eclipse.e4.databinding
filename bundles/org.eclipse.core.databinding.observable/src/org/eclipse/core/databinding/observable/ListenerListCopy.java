/*******************************************************************************
 * Copyright (c) 2014 Nigel Westbury and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nigel Westbury - initial API and implementation
 ******************************************************************************/

package org.eclipse.core.databinding.observable;

import java.util.List;

/**
 * An immutable copy of a listener list.
 * <P>
 * Often when events are fired on a <code>ListenerList</code>, it is necessary
 * to take a fixed copy of the listener list while inside a synchronization
 * block and then fire the event on those listeners when outside the
 * synchronization block.
 * <P>
 * It is expected that only one event would ever be fired on an instance of this
 * class. This is because an instance of this class may contain an out-of-date
 * listener list the moment the code exits the synchronization block in which
 * the change for the event was made.
 * <P>
 * This class is an immutable copy of the listener list that can be instantiated
 * only by calling the <code>getReadOnlyCopy</code> method of the
 * <code>ListenerList</code> class.
 * 
 * @param <L>
 *            type of the listeners in the list
 * @since 1.5
 * @author Nigel Westbury
 */
public class ListenerListCopy<L extends IObservablesListener<L>> {

	private final List<L> listeners;

	/**
	 * The listener list passed to this class must never be changed by anyone,
	 * inside or outside this class. The list is passed to this class by
	 * ListenerList, and that class and this class never change the list and
	 * never expose the list outside these two classes.
	 * 
	 * @param listeners
	 *            the listener list, which this constructor takes a copy of, so
	 *            further changes to the list will not affect this class
	 */
	public ListenerListCopy(List<L> listeners) {
		this.listeners = listeners;
	}

	/**
	 * @param event
	 */
	public void fireEvent(ObservableEvent<?, L> event) {
		for (L listener : listeners) {
			event.dispatch(listener);
		}
	}

	/**
	 * This method is used to determine if this copy of the listener list is
	 * still the current list for the given listener list passed as a parameter.
	 * <P>
	 * Note that if the listener list was changed and then went back to the same
	 * list, it will indicate as a different list. This behavior enables this
	 * method to be executed very efficiently. This method is designed to be
	 * used to optimize code when the listener list has not changed, so
	 * returning <code>false</code> when the lists happen to be the same is
	 * fine.
	 * 
	 * @param originalListenerList
	 * @return true if the given original listener list has not been changed
	 *         since this copy was taken, false if it has been changed
	 */
	public boolean isIdenticalTo(ListenerList<L> originalListenerList) {
		return originalListenerList.isListStillCurrent(listeners);
	}
}
