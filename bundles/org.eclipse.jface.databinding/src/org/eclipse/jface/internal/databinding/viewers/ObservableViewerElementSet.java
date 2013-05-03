/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 215531)
 *     Matthew Hall - bug 230267
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.viewers;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.AbstractObservableSet;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.StructuredViewer;

/**
 * An {@link IObservableSet} of elements in a {@link StructuredViewer}. Elements
 * of the set are compared using an {@link IElementComparer} instead of
 * {@link #equals(Object)}.
 * <p>
 * This class is <i>not</i> a strict implementation the {@link IObservableSet}
 * interface. It intentionally violates the {@link Set} contract, which requires
 * the use of {@link #equals(Object)} when comparing elements. This class is
 * designed for use with {@link StructuredViewer} which uses
 * {@link IElementComparer} for element comparisons.
 * 
 * @param <E>
 * 
 * 
 * @since 1.2
 */
public class ObservableViewerElementSet<E> extends AbstractObservableSet<E> {
	private Set<E> wrappedSet;
	private Object elementTypeAsObject;
	private Class<E> elementType;
	private IElementComparer comparer;

	/**
	 * Constructs an ObservableViewerElementSet on the given {@link Realm} which
	 * uses the given {@link IElementComparer} to compare elements.
	 * 
	 * @param realm
	 *            the realm of the constructed set.
	 * @param elementType
	 *            the element type of the constructed set.
	 * @param comparer
	 *            the {@link IElementComparer} used to compare elements.
	 * @deprecated use the form of the constructor that takes a Class object as
	 *             the elementType parameter
	 */
	public ObservableViewerElementSet(Realm realm, Object elementType,
			IElementComparer comparer) {
		super(realm);

		Assert.isNotNull(comparer);
		this.wrappedSet = new ViewerElementSet<E>(comparer);
		this.elementTypeAsObject = elementType;
		this.elementType = null;
		this.comparer = comparer;
	}

	/**
	 * Constructs an ObservableViewerElementSet on the given {@link Realm} which
	 * uses the given {@link IElementComparer} to compare elements.
	 * 
	 * @param realm
	 *            the realm of the constructed set.
	 * @param elementType
	 *            the element type of the constructed set.
	 * @param comparer
	 *            the {@link IElementComparer} used to compare elements.
	 */
	public ObservableViewerElementSet(Realm realm, Class<E> elementType,
			IElementComparer comparer) {
		super(realm);

		Assert.isNotNull(comparer);
		this.wrappedSet = new ViewerElementSet<E>(comparer);
		this.elementTypeAsObject = elementType;
		this.elementType = elementType;
		this.comparer = comparer;
	}

	protected Set<E> getWrappedSet() {
		return wrappedSet;
	}

	public Object getElementType() {
		return elementTypeAsObject;
	}

	public Class<E> getElementClass() {
		return elementType;
	}

	public Iterator<E> iterator() {
		getterCalled();
		final Iterator<E> wrappedIterator = wrappedSet.iterator();
		return new Iterator<E>() {
			E last;

			public boolean hasNext() {
				getterCalled();
				return wrappedIterator.hasNext();
			}

			public E next() {
				getterCalled();
				return last = wrappedIterator.next();
			}

			public void remove() {
				getterCalled();
				wrappedIterator.remove();
				fireSetChange(Diffs.createSetDiff(Collections.<E> emptySet(),
						Collections.singleton(last)));
			}
		};
	}

	public boolean add(E o) {
		getterCalled();
		boolean changed = wrappedSet.add(o);
		if (changed)
			fireSetChange(Diffs.createSetDiff(Collections.singleton(o),
					Collections.<E> emptySet()));
		return changed;
	}

	public boolean addAll(Collection<? extends E> c) {
		getterCalled();
		Set<E> additions = new ViewerElementSet<E>(comparer);
		for (Iterator<? extends E> iterator = c.iterator(); iterator.hasNext();) {
			E element = iterator.next();
			if (wrappedSet.add(element))
				additions.add(element);
		}
		boolean changed = !additions.isEmpty();
		if (changed)
			fireSetChange(Diffs.createSetDiff(additions,
					Collections.<E> emptySet()));
		return changed;
	}

	public boolean remove(Object o) {
		getterCalled();
		boolean changed = wrappedSet.remove(o);
		if (changed)
			fireSetChange(Diffs.createSetDiff(Collections.<E> emptySet(),
					Collections.singleton((E) o)));
		return changed;
	}

	public boolean removeAll(Collection<?> c) {
		getterCalled();
		Set<E> removals = new ViewerElementSet<E>(comparer);
		for (Iterator<?> iterator = c.iterator(); iterator.hasNext();) {
			Object element = iterator.next();
			if (wrappedSet.remove(element))
				removals.add((E) element);
		}
		boolean changed = !removals.isEmpty();
		if (changed)
			fireSetChange(Diffs.createSetDiff(Collections.<E> emptySet(),
					removals));
		return changed;
	}

	public boolean retainAll(Collection<?> c) {
		getterCalled();
		Set<E> removals = new ViewerElementSet<E>(comparer);
		Object[] toRetain = c.toArray();
		outer: for (Iterator<E> iterator = wrappedSet.iterator(); iterator
				.hasNext();) {
			E element = iterator.next();
			// Cannot rely on c.contains(element) because we must compare
			// elements using IElementComparer.
			for (int i = 0; i < toRetain.length; i++) {
				if (comparer.equals(element, toRetain[i]))
					continue outer;
			}
			iterator.remove();
			removals.add(element);
		}
		boolean changed = !removals.isEmpty();
		if (changed)
			fireSetChange(Diffs.createSetDiff(Collections.<E> emptySet(),
					removals));
		return changed;
	}

	public void clear() {
		getterCalled();
		if (!wrappedSet.isEmpty()) {
			Set<E> removals = wrappedSet;
			wrappedSet = new ViewerElementSet<E>(comparer);
			fireSetChange(Diffs.createSetDiff(Collections.<E> emptySet(),
					removals));
		}
	}

	/**
	 * Returns an {@link IObservableSet} for holding viewer elements, using the
	 * given {@link IElementComparer} for comparisons.
	 * 
	 * @param realm
	 *            the realm of the returned observable
	 * @param elementType
	 *            the element type of the returned set
	 * @param comparer
	 *            the element comparer to use in element comparisons (may be
	 *            null). If null, the returned set will compare elements
	 *            according to the standard contract for {@link Set} interface
	 *            contract.
	 * @return a Set for holding viewer elements, using the given
	 *         {@link IElementComparer} for comparisons.
	 */
	public static <E2> IObservableSet<E2> withComparer(Realm realm,
			Object elementType, IElementComparer comparer) {
		if (comparer == null)
			return new WritableSet<E2>(realm, Collections.<E2> emptySet(),
					elementType);
		return new ObservableViewerElementSet<E2>(realm, elementType, comparer);
	}
}