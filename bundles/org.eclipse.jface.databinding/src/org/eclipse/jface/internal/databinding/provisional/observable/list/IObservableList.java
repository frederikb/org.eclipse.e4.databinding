/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.provisional.observable.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jface.internal.databinding.provisional.observable.IObservableCollection;

/**
 * A list whose changes can be tracked by change listeners. 
 * 
 * @since 1.0
 */
public interface IObservableList extends List, IObservableCollection {
	
	/**
	 * Adds the given list change listener to the list of list change listeners.
	 * @param listener
	 */
	public void addListChangeListener(IListChangeListener listener);
	
	/**
	 * Removes the given list change listener from the list of list change listeners.
	 * Has no effect if the given listener is not registered as a list change listener.
	 * 
	 * @param listener
	 */
	public void removeListChangeListener(IListChangeListener listener);

	/**
	 * @TrackedGetter
	 */
    public int size();

	/**
	 * @TrackedGetter
	 */
    public boolean isEmpty();

	/**
	 * @TrackedGetter
	 */
    public boolean contains(Object o);

	/**
	 * @TrackedGetter
	 */
    public Iterator iterator();

	/**
	 * @TrackedGetter
	 */
    public Object[] toArray();

	/**
	 * @TrackedGetter
	 */
    public Object[] toArray(Object a[]);

	/**
	 * @TrackedGetter because of the returned boolean
	 */
    public boolean add(Object o);

	/**
	 * @TrackedGetter
	 */
    public boolean remove(Object o);

	/**
	 * @TrackedGetter
	 */
    public boolean containsAll(Collection c);

	/**
	 * @TrackedGetter
	 */
    public boolean addAll(Collection c);

	/**
	 * @TrackedGetter
	 */
    public boolean addAll(int index, Collection c);

	/**
	 * @TrackedGetter
	 */
    public boolean removeAll(Collection c);

	/**
	 * @TrackedGetter
	 */
    public boolean retainAll(Collection c);

	/**
	 * @TrackedGetter
	 */
    public boolean equals(Object o);

	/**
	 * @TrackedGetter
	 */
    public int hashCode();

	/**
	 * @TrackedGetter
	 */
    public Object get(int index);

	/**
	 * @TrackedGetter because of the returned object
	 */
    public Object set(int index, Object element);

	/**
	 * @TrackedGetter
	 */
    public Object remove(int index);

	/**
	 * @TrackedGetter
	 */
    public int indexOf(Object o);

	/**
	 * @TrackedGetter
	 */
    public int lastIndexOf(Object o);

	/**
	 * @TrackedGetter
	 */
    public ListIterator listIterator();

	/**
	 * @TrackedGetter
	 */
    public ListIterator listIterator(int index);

	/**
	 * @TrackedGetter
	 */
    public List subList(int fromIndex, int toIndex);

	/**
	 * @return the type of the elements
	 */
	Object getElementType();
}