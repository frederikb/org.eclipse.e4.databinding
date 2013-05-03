/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 124684)
 *     Matthew Hall - bug 259380, 283204
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.viewers;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.AbstractObservableSet;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckable;
import org.eclipse.jface.viewers.IElementComparer;

/**
 * 
 * @since 1.2
 */
public class CheckableCheckedElementsObservableSet extends
		AbstractObservableSet<Object> {
	private ICheckable checkable;
	private Set<Object> wrappedSet;
	private Object elementType;
	private IElementComparer elementComparer;
	private ICheckStateListener listener;

	/**
	 * Constructs a new instance of the given realm, and checkable,
	 * 
	 * @param realm
	 *            the observable's realm
	 * @param wrappedSet
	 *            the set being wrapped
	 * @param elementType
	 *            type of elements in the set
	 * @param elementComparer
	 *            element comparer
	 * @param checkable
	 *            the ICheckable to track
	 */
	public CheckableCheckedElementsObservableSet(Realm realm,
			final Set<Object> wrappedSet, Object elementType,
			IElementComparer elementComparer, ICheckable checkable) {
		super(realm);
		Assert.isNotNull(checkable, "Checkable cannot be null"); //$NON-NLS-1$
		Assert.isNotNull(wrappedSet, "Wrapped set cannot be null"); //$NON-NLS-1$
		this.checkable = checkable;
		this.wrappedSet = wrappedSet;
		this.elementType = elementType;
		this.elementComparer = elementComparer;

		listener = new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object element = event.getElement();
				if (event.getChecked()) {
					if (wrappedSet.add(element))
						fireSetChange(Diffs.createSetDiff(
								Collections.singleton(element),
								Collections.<Object> emptySet()));
				} else {
					if (wrappedSet.remove(element))
						fireSetChange(Diffs.createSetDiff(
								Collections.<Object> emptySet(),
								Collections.singleton(element)));
				}
			}
		};
		checkable.addCheckStateListener(listener);
	}

	protected Set<Object> getWrappedSet() {
		return wrappedSet;
	}

	Set<Object> createDiffSet() {
		return ViewerElementSet.withComparer(elementComparer);
	}

	public Object getElementType() {
		return elementType;
	}

	public Class<Object> getElementClass() {
		return Object.class;
	}

	public boolean add(Object o) {
		getterCalled();
		boolean added = wrappedSet.add(o);
		if (added) {
			checkable.setChecked(o, true);
			fireSetChange(Diffs.createSetDiff(Collections.singleton(o),
					Collections.<Object> emptySet()));
		}
		return added;
	}

	public boolean remove(Object o) {
		getterCalled();
		boolean removed = wrappedSet.remove(o);
		if (removed) {
			checkable.setChecked(o, false);
			fireSetChange(Diffs.createSetDiff(Collections.<Object> emptySet(),
					Collections.singleton(o)));
		}
		return removed;
	}

	public boolean addAll(Collection<? extends Object> c) {
		getterCalled();
		Set<Object> additions = createDiffSet();
		for (Iterator<? extends Object> iterator = c.iterator(); iterator
				.hasNext();) {
			Object element = iterator.next();
			if (wrappedSet.add(element)) {
				checkable.setChecked(element, true);
				additions.add(element);
			}
		}
		boolean changed = !additions.isEmpty();
		if (changed)
			fireSetChange(Diffs
					.createSetDiff(additions, Collections.emptySet()));
		return changed;
	}

	public boolean removeAll(Collection<?> c) {
		getterCalled();
		Set<Object> removals = createDiffSet();
		for (Iterator<?> iterator = c.iterator(); iterator.hasNext();) {
			Object element = iterator.next();
			if (wrappedSet.remove(element)) {
				checkable.setChecked(element, false);
				removals.add(element);
			}
		}
		boolean changed = !removals.isEmpty();
		if (changed)
			fireSetChange(Diffs.createSetDiff(Collections.emptySet(), removals));
		return changed;
	}

	public boolean retainAll(Collection<?> c) {
		getterCalled();

		// To ensure that elements are compared correctly, e.g. ViewerElementSet
		Set<Object> toRetain = createDiffSet();
		toRetain.addAll(c);

		Set<Object> removals = createDiffSet();
		for (Iterator<Object> iterator = wrappedSet.iterator(); iterator
				.hasNext();) {
			Object element = iterator.next();
			if (!toRetain.contains(element)) {
				iterator.remove();
				checkable.setChecked(element, false);
				removals.add(element);
			}
		}
		boolean changed = !removals.isEmpty();
		if (changed)
			fireSetChange(Diffs.createSetDiff(Collections.emptySet(), removals));
		return changed;
	}

	public void clear() {
		getterCalled();
		Set<Object> removals = createDiffSet();
		removals.addAll(wrappedSet);
		removeAll(removals);
	}

	public Iterator<Object> iterator() {
		getterCalled();
		final Iterator<Object> wrappedIterator = wrappedSet.iterator();
		return new Iterator<Object>() {
			private Object last = null;

			public boolean hasNext() {
				getterCalled();
				return wrappedIterator.hasNext();
			}

			public Object next() {
				getterCalled();
				return last = wrappedIterator.next();
			}

			public void remove() {
				getterCalled();
				wrappedIterator.remove();
				checkable.setChecked(last, false);
				fireSetChange(Diffs.createSetDiff(Collections.emptySet(),
						Collections.singleton(last)));
			}
		};
	}

	public synchronized void dispose() {
		if (checkable != null) {
			checkable.removeCheckStateListener(listener);
			checkable = null;
			listener = null;
		}
		super.dispose();
	}
}
