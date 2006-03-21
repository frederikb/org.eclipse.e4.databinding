/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.internal.databinding.provisional.observable;

/**
 * An object with state that allows to listen for state changes.
 * 
 * @since 1.0
 * 
 */
public interface IObservable {

	/**
	 * Adds the given change listener to the list of change listeners.
	 * @param listener
	 */
	public void addChangeListener(IChangeListener listener);

	/**
	 * Removes the given change listener from the list of change listeners.
	 * Has no effect if the given listener is not registered as a change listener.
	 * @param listener
	 */
	public void removeChangeListener(IChangeListener listener);

	/**
	 * Adds the given stale listener to the list of stale listeners.
	 * @param listener
	 */
	public void addStaleListener(IStaleListener listener);

	/**
	 * Removes the given stale listener from the list of stale listeners.
	 * Has no effect if the given listener is not registered as a stale listener.
	 * @param listener
	 */
	public void removeStaleListener(IStaleListener listener);

	/**
	 * Returns whether the state of this observable is stale. A non-stale object
	 * that becomes stale will notify its stale listeners. A stale object that
	 * becomes non-stale does so by changing its state and notifying its change
	 * listeners. Clients that do not expect asynchronous changes may ignore
	 * staleness of observable objects.
	 * 
	 * @return true if this observable's state is stale and will change soon.
	 * 
	 * @TrackedGetter - implementers must call {@link ObservableTracker#getterCalled(IObservable)}.
	 */
	public boolean isStale();
	
	/**
	 * Disposes of this observable object, removing all listeners registered with this
	 * object, and all listeners this object might have registered on other objects. 
	 */
	public void dispose();
}