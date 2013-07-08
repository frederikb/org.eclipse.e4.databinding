/*******************************************************************************
 * Copyright (c) 2006-2008 Cerner Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brad Reynolds - initial API and implementation
 *     Matthew Hall - bug 208332, 237718
 ******************************************************************************/

package org.eclipse.core.databinding.observable.list;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.databinding.observable.DecoratingObservable;

/**
 * ObservableList implementation that prevents modification by consumers. Events
 * in the originating wrapped list are propagated and thrown from this instance
 * when appropriate. All mutators throw an UnsupportedOperationException.
 * 
 * @param <E>
 * 
 * @since 1.5
 */
public class UnmodifiableObservableList<E> extends DecoratingObservable
		implements IObservableList<E> {

	private IObservableList<? extends E> decorated;

	private IListChangeListener<E> listChangeListener;

	private Class<E> elementType;

	/**
	 * Constructs a UnmodifiableObservableList which cannot be modified.
	 * <P>
	 * Use this form of the constructor if the type of elements in the given
	 * underlying list and the type of elements in this list are the same.
	 * 
	 * @param decorated
	 */
	public UnmodifiableObservableList(IObservableList<E> decorated) {
		super(decorated, false);
		this.decorated = decorated;
		this.elementType = decorated.getElementClass();
	}

	/**
	 * Constructs a UnmodifiableObservableList which cannot be modified.
	 * <P>
	 * Use this form of the constructor if the type of elements in this list are
	 * to be typed to a class that is a super-type of the elements in the given
	 * underlying list.
	 * 
	 * @param decorated
	 * @param elementType
	 */
	public UnmodifiableObservableList(IObservableList<? extends E> decorated,
			Class<E> elementType) {
		super(decorated, false);
		this.decorated = decorated;
		this.elementType = elementType;
	}

	public void add(int index, Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean add(E o) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean contains(Object o) {
		getterCalled();
		return decorated.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		getterCalled();
		return decorated.containsAll(c);
	}

	public boolean isEmpty() {
		getterCalled();
		return decorated.isEmpty();
	}

	public Iterator<E> iterator() {
		getterCalled();
		final Iterator<? extends E> decoratedIterator = decorated.iterator();
		return new Iterator<E>() {
			public void remove() {
				throw new UnsupportedOperationException();
			}

			public boolean hasNext() {
				getterCalled();
				return decoratedIterator.hasNext();
			}

			public E next() {
				getterCalled();
				return decoratedIterator.next();
			}
		};
	}

	public E move(int oldIndex, int newIndex) {
		throw new UnsupportedOperationException();
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

	public E remove(int index) {
		throw new UnsupportedOperationException();
	}

	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		getterCalled();
		return decorated.size();
	}

	public Object[] toArray() {
		getterCalled();
		return decorated.toArray();
	}

	public <T> T[] toArray(T[] a) {
		getterCalled();
		return decorated.toArray(a);
	}

	/**
	 * @deprecated use getElementClass instead
	 */
	public Object getElementType() {
		return decorated.getElementType();
	}

	/**
	 * @since 1.5
	 */
	public Class<E> getElementClass() {
		return elementType;
	}

	public boolean equals(Object obj) {
		getterCalled();
		if (this == obj) {
			return true;
		}
		return decorated.equals(obj);
	}

	public int hashCode() {
		getterCalled();
		return decorated.hashCode();
	}

	public String toString() {
		getterCalled();
		return decorated.toString();
	}

	public synchronized void addListChangeListener(
			IListChangeListener<? super E> listener) {
		addListener(ListChangeEvent.TYPE, listener);
	}

	public synchronized void removeListChangeListener(
			IListChangeListener<? super E> listener) {
		removeListener(ListChangeEvent.TYPE, listener);
	}

	protected void fireListChange(ListDiff<E> diff) {
		// fire general change event first
		super.fireChange();
		fireEvent(new ListChangeEvent<E>(this, diff));
	}

	protected void fireChange() {
		throw new RuntimeException(
				"fireChange should not be called, use fireListChange() instead"); //$NON-NLS-1$
	}

	protected void firstListenerAdded() {
		if (listChangeListener == null) {
			listChangeListener = new IListChangeListener<E>() {
				public void handleListChange(ListChangeEvent<E> event) {
					UnmodifiableObservableList.this.handleListChange(event);
				}
			};
		}
		decorated.addListChangeListener(listChangeListener);
		super.firstListenerAdded();
	}

	protected void lastListenerRemoved() {
		super.lastListenerRemoved();
		if (listChangeListener != null) {
			decorated.removeListChangeListener(listChangeListener);
			listChangeListener = null;
		}
	}

	/**
	 * Called whenever a ListChangeEvent is received from the decorated
	 * observable. By default, this method fires the list change event again,
	 * with the decorating observable as the event source. Subclasses may
	 * override to provide different behavior.
	 * 
	 * @param event
	 *            the change event received from the decorated observable
	 */
	protected void handleListChange(final ListChangeEvent<E> event) {
		fireListChange(event.diff);
	}

	public E get(int index) {
		getterCalled();
		return decorated.get(index);
	}

	public int indexOf(Object o) {
		getterCalled();
		return decorated.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		getterCalled();
		return decorated.lastIndexOf(o);
	}

	public ListIterator<E> listIterator() {
		return listIterator(0);
	}

	public ListIterator<E> listIterator(int index) {
		getterCalled();
		final ListIterator<? extends E> iterator = decorated
				.listIterator(index);
		return new ListIterator<E>() {

			public void add(E o) {
				throw new UnsupportedOperationException();
			}

			public boolean hasNext() {
				getterCalled();
				return iterator.hasNext();
			}

			public boolean hasPrevious() {
				getterCalled();
				return iterator.hasPrevious();
			}

			public E next() {
				getterCalled();
				return iterator.next();
			}

			public int nextIndex() {
				getterCalled();
				return iterator.nextIndex();
			}

			public E previous() {
				getterCalled();
				return iterator.previous();
			}

			public int previousIndex() {
				getterCalled();
				return iterator.previousIndex();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

			public void set(E o) {
				throw new UnsupportedOperationException();
			}
		};
	}

	public List<E> subList(int fromIndex, int toIndex) {
		getterCalled();
		return Collections.unmodifiableList(decorated.subList(fromIndex,
				toIndex));
	}

	public synchronized void dispose() {
		if (decorated != null && listChangeListener != null) {
			decorated.removeListChangeListener(listChangeListener);
		}
		decorated = null;
		listChangeListener = null;
		super.dispose();
	}
}
