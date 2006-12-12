/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brad Reynolds - bug 164653
 *     Brad Reynolds - bug 167204
 ******************************************************************************/

package org.eclipse.core.databinding.observable.list;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.ChangeSupport;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IStaleListener;
import org.eclipse.core.databinding.observable.ObservableTracker;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.StaleEvent;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.AssertionFailedException;

/**
 * Subclasses should override at least get(int index) and size().
 * 
 * <p>
 * This class is thread safe. All state accessing methods must be invoked from
 * the {@link Realm#isCurrent() current realm}. Methods for adding and removing
 * listeners may be invoked from any thread.
 * </p>
 * 
 * @since 1.0
 * 
 */
public abstract class AbstractObservableList extends AbstractList implements
		IObservableList {

	private ChangeSupport changeSupport = new ChangeSupport(null){
		protected void firstListenerAdded() {
			AbstractObservableList.this.firstListenerAdded();
		}
		protected void lastListenerRemoved() {
			AbstractObservableList.this.lastListenerRemoved();
		}
	};

	private Realm realm;
	
	/**
	 * @param realm 
	 * 
	 */
	public AbstractObservableList(Realm realm) {
		Assert.isNotNull(realm);
		this.realm = realm;
	}

	/**
	 * 
	 */
	public AbstractObservableList() {
		this(Realm.getDefault());
	}
	
	public boolean isStale() {
		return false;
	}

	public synchronized void addListChangeListener(IListChangeListener listener) {
		changeSupport.addListener(ListChangeEvent.TYPE, listener);
	}

	public synchronized void removeListChangeListener(IListChangeListener listener) {
		changeSupport.removeListener(ListChangeEvent.TYPE, listener);
	}

	protected void fireListChange(ListDiff diff) {
		// fire general change event first
		fireChange();
		changeSupport.fireEvent(new ListChangeEvent(this, diff));
	}

	public synchronized void addChangeListener(IChangeListener listener) {
		changeSupport.addChangeListener(listener);
	}

	public synchronized void removeChangeListener(IChangeListener listener) {
		changeSupport.removeChangeListener(listener);
	}

	public synchronized void addStaleListener(IStaleListener listener) {
		changeSupport.addStaleListener(listener);
	}

	public synchronized void removeStaleListener(IStaleListener listener) {
		changeSupport.removeStaleListener(listener);
	}

	/**
	 * Fires change event. Must be invoked from the current realm.
	 */
	protected void fireChange() {
		checkRealm();
		changeSupport.fireEvent(new ChangeEvent(this));
	}

	/**
	 * Fires stale event. Must be invoked from the current realm.
	 */
	protected void fireStale() {
		checkRealm();
		changeSupport.fireEvent(new StaleEvent(this));
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
	public synchronized void dispose() {
		changeSupport = null;
		lastListenerRemoved();
	}

	public final int size() {
		getterCalled();
		return doGetSize();
	}

	/**
	 * @return the size
	 */
	protected abstract int doGetSize();

	/**
	 * 
	 */
	private void getterCalled() {
		ObservableTracker.getterCalled(this);
	}

	public boolean isEmpty() {
		getterCalled();
		return super.isEmpty();
	}

	public boolean contains(Object o) {
		getterCalled();
		return super.contains(o);
	}

	public Iterator iterator() {
		getterCalled();
		final Iterator wrappedIterator = super.iterator();
		return new Iterator() {
			public void remove() {
				wrappedIterator.remove();
			}

			public boolean hasNext() {
				return wrappedIterator.hasNext();
			}

			public Object next() {
				return wrappedIterator.next();
			}
		};
	}

	public Object[] toArray() {
		getterCalled();
		return super.toArray();
	}

	public Object[] toArray(Object a[]) {
		getterCalled();
		return super.toArray(a);
	}

	// Modification Operations

	public boolean add(Object o) {
		getterCalled();
		return super.add(o);
	}

	public boolean remove(Object o) {
		getterCalled();
		return super.remove(o);
	}

	// Bulk Modification Operations

	public boolean containsAll(Collection c) {
		getterCalled();
		return super.containsAll(c);
	}

	public boolean addAll(Collection c) {
		getterCalled();
		return super.addAll(c);
	}

	public boolean addAll(int index, Collection c) {
		getterCalled();
		return super.addAll(c);
	}

	public boolean removeAll(Collection c) {
		getterCalled();
		return super.removeAll(c);
	}

	public boolean retainAll(Collection c) {
		getterCalled();
		return super.retainAll(c);
	}

	// Comparison and hashing

	public boolean equals(Object o) {
		getterCalled();
		return super.equals(o);
	}

	public int hashCode() {
		getterCalled();
		return super.hashCode();
	}

	public int indexOf(Object o) {
		getterCalled();
		return super.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		getterCalled();
		return super.lastIndexOf(o);
	}

	public Realm getRealm() {
		return realm;
	}
	
	/**
	 * Asserts that the realm is the current realm.
	 * 
	 * @see Realm#isCurrent()
	 * @throws AssertionFailedException
	 *             if the realm is not the current realm
	 */
	protected void checkRealm() {
		Assert.isTrue(realm.isCurrent());
	}
}
