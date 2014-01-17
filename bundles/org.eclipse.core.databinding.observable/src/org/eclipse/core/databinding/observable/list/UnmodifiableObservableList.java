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
import org.eclipse.core.databinding.observable.ListenerList;

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

	private class MyListenerManager<E2 extends E> extends
			ListListenerManager<E2> {

		/**
		 * @param decoratedList
		 */
		public MyListenerManager(IObservableList<E2> decoratedList) {
			super(decoratedList);
		}

		@Override
		public void handleListChange(ListChangeEvent<E2> event) {
			ListChangeEvent<E> event2 = new ListChangeEvent<E>(
					UnmodifiableObservableList.this, event.diff);
			UnmodifiableObservableList.this.handleListChange(event2);
		}
	}

	private MyListenerManager<?> wrapper;

	private Class<E> elementType;

	/**
	 * The list of list-change listeners, or null if no listeners have yet been
	 * added
	 */
	private ListenerList<IListChangeListener<E>> listChangeListenerList = null;

	/**
	 * Constructs a UnmodifiableObservableList which cannot be modified.
	 * <P>
	 * Use this form of the constructor if the type of elements in the given
	 * underlying list and the type of elements in this list are the same.
	 * 
	 * @param decoratedList
	 */
	public UnmodifiableObservableList(IObservableList<E> decoratedList) {
		super(decoratedList, false);

		this.wrapper = wrap(decoratedList);
		this.elementType = decoratedList.getElementClass();
	}

	/**
	 * @param decoratedList
	 */
	private <E2 extends E> MyListenerManager<E2> wrap(
			IObservableList<E2> decoratedList) {
		return new MyListenerManager<E2>(decoratedList);
	}

	/**
	 * Constructs a UnmodifiableObservableList which cannot be modified.
	 * <P>
	 * Use this form of the constructor if the type of elements in this list are
	 * to be typed to a class that is a super-type of the elements in the given
	 * underlying list.
	 * 
	 * @param decoratedList
	 * @param elementType
	 */
	public UnmodifiableObservableList(
			IObservableList<? extends E> decoratedList, Class<E> elementType) {
		super(decoratedList, false);
		this.wrapper = wrap(decoratedList);
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
		return wrapper.decoratedList.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		getterCalled();
		return wrapper.decoratedList.containsAll(c);
	}

	public boolean isEmpty() {
		getterCalled();
		return wrapper.decoratedList.isEmpty();
	}

	public Iterator<E> iterator() {
		getterCalled();
		final Iterator<? extends E> decoratedIterator = wrapper.decoratedList
				.iterator();
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
		return wrapper.decoratedList.size();
	}

	public Object[] toArray() {
		getterCalled();
		return wrapper.decoratedList.toArray();
	}

	public <T> T[] toArray(T[] a) {
		getterCalled();
		return wrapper.decoratedList.toArray(a);
	}

	/**
	 * @deprecated use getElementClass instead
	 */
	public Object getElementType() {
		return wrapper.decoratedList.getElementType();
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
		return wrapper.decoratedList.equals(obj);
	}

	public int hashCode() {
		getterCalled();
		return wrapper.decoratedList.hashCode();
	}

	public String toString() {
		getterCalled();
		return wrapper.decoratedList.toString();
	}

	public synchronized void addListChangeListener(
			IListChangeListener<E> listener) {
		addListener(getListChangeListenerList(), listener);
	}

	/**
	 * @param listener
	 */
	public synchronized void removeListChangeListener(
			IListChangeListener<E> listener) {
		if (listChangeListenerList != null) {
			removeListener(listChangeListenerList, listener);
		}
	}

	private ListenerList<IListChangeListener<E>> getListChangeListenerList() {
		if (listChangeListenerList == null) {
			listChangeListenerList = new ListenerList<IListChangeListener<E>>();
		}
		return listChangeListenerList;
	}

	@Override
	protected boolean hasListeners() {
		return (listChangeListenerList != null && listChangeListenerList
				.hasListeners()) || super.hasListeners();
	}

	protected void fireListChange(ListDiff<? extends E> diff) {
		// fire general change event first
		super.fireChange();
		if (listChangeListenerList != null) {
			listChangeListenerList
					.fireEvent(new ListChangeEvent<E>(this, diff));
		}
	}

	protected void fireChange() {
		throw new RuntimeException(
				"fireChange should not be called, use fireListChange() instead"); //$NON-NLS-1$
	}

	protected void firstListenerAdded() {
		wrapper.addListener();
		super.firstListenerAdded();
	}

	protected void lastListenerRemoved() {
		super.lastListenerRemoved();
		wrapper.removeListener();
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
		return wrapper.decoratedList.get(index);
	}

	public int indexOf(Object o) {
		getterCalled();
		return wrapper.decoratedList.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		getterCalled();
		return wrapper.decoratedList.lastIndexOf(o);
	}

	public ListIterator<E> listIterator() {
		return listIterator(0);
	}

	public ListIterator<E> listIterator(int index) {
		getterCalled();
		final ListIterator<? extends E> iterator = wrapper.decoratedList
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
		return Collections.unmodifiableList(wrapper.decoratedList.subList(
				fromIndex, toIndex));
	}

	public synchronized void dispose() {
		if (wrapper != null) {
			wrapper.removeListener();
		}
		wrapper = null;
		super.dispose();
	}
}
