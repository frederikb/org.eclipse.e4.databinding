/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.core.internal.databinding.beans;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * @param <E>
 * @since 3.3
 * 
 */
public class TypedSet<E> implements Set<E> {

	final Set<?> wrappedSet;

	final Class<E> elementType;

	TypedSet(Set<?> wrappedSet, Class<E> elementType) {
		this.wrappedSet = wrappedSet;
		this.elementType = elementType;
	}

	public boolean add(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean contains(Object o) {
		return wrappedSet.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return wrappedSet.containsAll(c);
	}

	public boolean isEmpty() {
		return wrappedSet.isEmpty();
	}

	public Iterator<E> iterator() {
		final Iterator<?> wrappedIterator = wrappedSet.iterator();
		return new Iterator<E>() {
			public boolean hasNext() {
				return wrappedIterator.hasNext();
			}

			public E next() {
				Object next = wrappedIterator.next();
				return elementType.cast(next);
			}

			public void remove() {
				wrappedIterator.remove();
			}
		};
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return wrappedSet.size();
	}

	public Object[] toArray() {
		return toArray(new Object[wrappedSet.size()]);
	}

	public <E2> E2[] toArray(E2[] a) {
		int size = wrappedSet.size();
		Class<E2> componentType = Util.getComponentType(a);

		E2[] result = a;
		if (a.length < size) {
			result = Util.createArrayInstance(componentType, size);
		}

		int i = 0;
		for (Object element : wrappedSet) {
			result[i] = componentType.cast(element);
		}

		return result;
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || !(obj instanceof Set))
			return false;
		Set<?> that = (Set<?>) obj;
		return this.size() == that.size() && containsAll(that);
	}

	public int hashCode() {
		return wrappedSet.hashCode();
	}
}
