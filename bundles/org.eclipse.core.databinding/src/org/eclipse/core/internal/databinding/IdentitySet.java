/*******************************************************************************
 * Copyright (c) 2008, 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 215531)
 *     Matthew Hall - bug 124684
 *         (through ViewerElementSet.java)
 *     Matthew Hall - bugs 262269, 303847
 ******************************************************************************/

package org.eclipse.core.internal.databinding;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A {@link Set} of elements where elements are added, removed and compared by
 * identity. Elements of the set are compared using <code>==</code> instead of
 * {@link #equals(Object)}.
 * <p>
 * This class is <i>not</i> a strict implementation the {@link Set} interface.
 * It intentionally violates the {@link Set} contract, which requires the use of
 * {@link #equals(Object)} when comparing elements.
 * 
 * @param <E>
 * 
 * @since 1.2
 */
public class IdentitySet<E> implements Set<E> {
	private IdentityWrapper<E> NULL_WRAPPER;

	private final Set<IdentityWrapper<E>> wrappedSet;

	/**
	 * Constructs an IdentitySet.
	 */
	public IdentitySet() {
		this.wrappedSet = new HashSet<IdentityWrapper<E>>();
	}

	/**
	 * Constructs an IdentitySet containing all the unique instances in the
	 * specified collection.
	 * 
	 * @param collection
	 *            the collection whose elements are to be added to this set.
	 */
	public IdentitySet(Collection<? extends E> collection) {
		this();
		addAll(collection);
	}

	public boolean add(E o) {
		return wrappedSet.add(wrap(o));
	}

	public boolean addAll(Collection<? extends E> c) {
		boolean changed = false;
		for (Iterator<? extends E> iterator = c.iterator(); iterator.hasNext();)
			changed |= wrappedSet.add(wrap(iterator.next()));
		return changed;
	}

	public void clear() {
		wrappedSet.clear();
	}

	public boolean contains(Object o) {
		return wrappedSet.contains(wrapAny(o));
	}

	public boolean containsAll(Collection<?> c) {
		for (Iterator<?> iterator = c.iterator(); iterator.hasNext();)
			if (!wrappedSet.contains(wrapAny(iterator.next())))
				return false;
		return true;
	}

	public boolean isEmpty() {
		return wrappedSet.isEmpty();
	}

	public Iterator<E> iterator() {
		final Iterator<IdentityWrapper<E>> wrappedIterator = wrappedSet
				.iterator();
		return new Iterator<E>() {
			public boolean hasNext() {
				return wrappedIterator.hasNext();
			}

			public E next() {
				return wrappedIterator.next().unwrap();
			}

			public void remove() {
				wrappedIterator.remove();
			}
		};
	}

	public boolean remove(Object o) {
		return wrappedSet.remove(wrapAny(o));
	}

	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Iterator<?> iterator = c.iterator(); iterator.hasNext();)
			changed |= remove(iterator.next());
		return changed;
	}

	public boolean retainAll(Collection<?> c) {
		// Have to do this the slow way to ensure correct comparisons. i.e.
		// cannot delegate to c.contains(it) since we can't be sure will
		// compare elements the way we want.
		boolean changed = false;
		Object[] retainAll = c.toArray();
		outer: for (Iterator<?> iterator = iterator(); iterator.hasNext();) {
			Object element = iterator.next();
			for (int i = 0; i < retainAll.length; i++) {
				if (element == retainAll[i]) {
					continue outer;
				}
			}
			iterator.remove();
			changed = true;
		}
		return changed;
	}

	public int size() {
		return wrappedSet.size();
	}

	public Object[] toArray() {
		return toArray(new Object[wrappedSet.size()]);
	}

	public <T> T[] toArray(T[] a) {
		int size = wrappedSet.size();
		IdentityWrapper<?>[] wrappedArray = wrappedSet
				.toArray(new IdentityWrapper[size]);
		T[] result = a;
		if (a.length < size) {
			result = (T[]) Array.newInstance(a.getClass().getComponentType(),
					size);
		}
		for (int i = 0; i < size; i++)
			result[i] = (T) wrappedArray[i].unwrap();
		return result;
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof Set))
			return false;
		Set<?> that = (Set<?>) obj;
		return size() == that.size() && containsAll(that);
	}

	public int hashCode() {
		int hash = 0;
		for (Iterator<E> iterator = iterator(); iterator.hasNext();) {
			E element = iterator.next();
			hash += element == null ? 0 : element.hashCode();
		}
		return hash;
	}

	/**
	 * @return a wrapper of the null value, appropriately typed
	 */
	private IdentityWrapper<E> getNullWrapper() {
		if (NULL_WRAPPER == null) {
			NULL_WRAPPER = new IdentityWrapper<E>(null);
		}
		return NULL_WRAPPER;
	}

	/**
	 * @param obj
	 *            the object to wrap
	 * @return an IdentityWrapper wrapping the specified object
	 */
	private IdentityWrapper<E> wrap(E obj) {
		return obj == null ? getNullWrapper() : new IdentityWrapper<E>(obj);
	}

	private IdentityWrapper<?> wrapAny(Object obj) {
		return obj == null ? getNullWrapper()
				: new IdentityWrapper<Object>(obj);
	}
}
