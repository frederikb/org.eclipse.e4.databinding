/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matthew Hall - bugs 208332, 146397, 249526
 *******************************************************************************/

package org.eclipse.core.internal.databinding.observable;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IDisposeListener;
import org.eclipse.core.databinding.observable.IStaleListener;
import org.eclipse.core.databinding.observable.ObservableTracker;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.runtime.Assert;

/**
 * Singleton empty set
 * 
 * @param <E>
 */
public class EmptyObservableSet<E> implements IObservableSet<E> {

	private final Set<E> emptySet = Collections.emptySet();

	private final Realm realm;

	/**
	 * @deprecated use getElementClass() instead
	 */
	private Object elementType;

	/**
	 * @since 1.5
	 */
	private Class<E> elementClass;

	/**
	 * Creates a singleton empty set. This set may be disposed multiple times
	 * without any side-effects.
	 * 
	 * @param realm
	 *            the realm of the constructed set
	 */
	public EmptyObservableSet(Realm realm) {
		this(realm, null);
	}

	/**
	 * Creates a singleton empty set. This set may be disposed multiple times
	 * without any side-effects.
	 * 
	 * @param realm
	 *            the realm of the constructed set
	 * @param elementType
	 *            the element type of the constructed set
	 * @since 1.1
	 * @deprecated use instead the form of the constructor that takes Class as
	 *             the parameter type for the element type
	 */
	// OK to suppress warnings in deprecated method
	@SuppressWarnings("unchecked")
	public EmptyObservableSet(Realm realm, Object elementType) {
		this.realm = realm;
		this.elementType = elementType;
		if (elementType instanceof Class) {
			this.elementClass = (Class<E>) elementType;
		} else {
			this.elementClass = null;
		}
		ObservableTracker.observableCreated(this);
	}

	/**
	 * Creates a singleton empty set. This set may be disposed multiple times
	 * without any side-effects.
	 * 
	 * @param realm
	 *            the realm of the constructed set
	 * @param elementType
	 *            the element type of the constructed set
	 * @since 1.1
	 */
	public EmptyObservableSet(Realm realm, Class<E> elementType) {
		this.realm = realm;
		this.elementType = elementType;
		this.elementClass = elementType;
		ObservableTracker.observableCreated(this);
	}

	public void addSetChangeListener(ISetChangeListener<E> listener) {
	}

	public void removeSetChangeListener(ISetChangeListener<E> listener) {
	}

	/**
	 * @deprecated use getElementClass instead
	 */
	public Object getElementType() {
		return elementType;
	}

	/**
	 * @since 1.5
	 */
	public Class<E> getElementClass() {
		return elementClass;
	}

	public int size() {
		checkRealm();
		return 0;
	}

	private void checkRealm() {
		Assert.isTrue(realm.isCurrent(),
				"Observable cannot be accessed outside its realm"); //$NON-NLS-1$
	}

	public boolean isEmpty() {
		checkRealm();
		return true;
	}

	public boolean contains(Object o) {
		checkRealm();
		return false;
	}

	public Iterator<E> iterator() {
		checkRealm();
		return emptySet.iterator();
	}

	public Object[] toArray() {
		checkRealm();
		return emptySet.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return emptySet.toArray(a);
	}

	public boolean add(E o) {
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean containsAll(Collection<?> c) {
		checkRealm();
		return c.isEmpty();
	}

	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public void addChangeListener(IChangeListener listener) {
	}

	public void removeChangeListener(IChangeListener listener) {
	}

	public void addStaleListener(IStaleListener listener) {
	}

	public void removeStaleListener(IStaleListener listener) {
	}

	public void addDisposeListener(IDisposeListener listener) {
	}

	public void removeDisposeListener(IDisposeListener listener) {
	}

	public boolean isStale() {
		checkRealm();
		return false;
	}

	public boolean isDisposed() {
		return false;
	}

	public void dispose() {
	}

	public Realm getRealm() {
		return realm;
	}

	public boolean equals(Object obj) {
		checkRealm();
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Set))
			return false;

		return ((Set<?>) obj).isEmpty();
	}

	public int hashCode() {
		checkRealm();
		return 0;
	}
}
