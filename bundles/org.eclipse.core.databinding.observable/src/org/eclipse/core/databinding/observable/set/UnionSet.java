/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matthew Hall - bugs 208332, 265727
 *******************************************************************************/

package org.eclipse.core.databinding.observable.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.internal.databinding.observable.IStalenessConsumer;
import org.eclipse.core.internal.databinding.observable.StalenessTracker;

/**
 * Represents a set consisting of the union of elements from one or more other
 * sets. This object does not need to be explicitly disposed. If nobody is
 * listening to the UnionSet, the set will remove its listeners.
 * 
 * <p>
 * This class is thread safe. All state accessing methods must be invoked from
 * the {@link Realm#isCurrent() current realm}. Methods for adding and removing
 * listeners may be invoked from any thread.
 * </p>
 * 
 * @param <E>
 *            type of the elements in the union set
 * @since 1.0
 */
public final class UnionSet<E> extends ObservableSet<E> {

	private class MyListenerManager<E2 extends E> extends
			SetListenerManager<E2> {

		/**
		 * @param decoratedSet
		 */
		public MyListenerManager(IObservableSet<E2> decoratedSet) {
			super(decoratedSet);
		}

		@Override
		public void handleSetChange(SetChangeEvent<E2> event) {
			processAddsAndRemoves(event.diff.getAdditions(),
					event.diff.getRemovals());
		}
	}

	/**
	 * child sets
	 */
	private Set<MyListenerManager<?>> childSets;

	private boolean stale = false;

	/**
	 * Map of elements to reference counts, being the number of child sets that
	 * contain the element. This map is constructed when the first listener is
	 * added to the union set. Null if nobody is listening to the UnionSet.
	 */
	private HashMap<E, Integer> refCounts = null;

	private StalenessTracker stalenessTracker;

	/**
	 * @param childSets
	 */
	public UnionSet(IObservableSet<? extends E>[] childSets) {
		this(new HashSet<IObservableSet<? extends E>>(Arrays.asList(childSets)));
	}

	/**
	 * @param childSets
	 * @since 1.5
	 */
	public UnionSet(Set<IObservableSet<? extends E>> childSets) {
		this(childSets, childSets.iterator().next().getElementType());
	}

	/**
	 * @param childSets
	 * @param elementType
	 * @since 1.2
	 */
	public UnionSet(IObservableSet<? extends E>[] childSets, Object elementType) {
		this(
				new HashSet<IObservableSet<? extends E>>(
						Arrays.asList(childSets)), elementType);
	}

	/**
	 * @param childSets
	 * @param elementType
	 * @since 1.5
	 */
	public UnionSet(Set<IObservableSet<? extends E>> childSets,
			Object elementType) {
		super(childSets.iterator().next().getRealm(), null, elementType);

		this.childSets = new HashSet<MyListenerManager<?>>();
		for (IObservableSet<? extends E> childSet : childSets) {
			addChildSet(childSet);
		}

		this.stalenessTracker = new StalenessTracker(
				childSets.toArray(new IObservableSet[0]), stalenessConsumer);
	}

	/**
	 * @param childSet
	 */
	private <E2 extends E> void addChildSet(IObservableSet<E2> childSet) {
		this.childSets.add(new MyListenerManager<E2>(childSet));
	}

	private IStalenessConsumer stalenessConsumer = new IStalenessConsumer() {
		public void setStale(boolean stale) {
			boolean oldStale = UnionSet.this.stale;
			UnionSet.this.stale = stale;
			if (stale && !oldStale) {
				fireStale();
			}
		}
	};

	public boolean isStale() {
		getterCalled();
		if (refCounts != null) {
			return stale;
		}

		for (MyListenerManager<?> childSet : childSets) {
			if (childSet.decoratedSet.isStale()) {
				return true;
			}
		}
		return false;
	}

	private void processAddsAndRemoves(Set<? extends E> adds,
			Set<? extends E> removes) {
		Set<E> addsToFire = new HashSet<E>();
		Set<E> removesToFire = new HashSet<E>();

		for (Iterator<? extends E> iter = adds.iterator(); iter.hasNext();) {
			E added = iter.next();

			Integer refCount = refCounts.get(added);
			if (refCount == null) {
				refCounts.put(added, new Integer(1));
				addsToFire.add(added);
			} else {
				int refs = refCount.intValue();
				refCount = new Integer(refs + 1);
				refCounts.put(added, refCount);
			}
		}

		for (Iterator<? extends E> iter = removes.iterator(); iter.hasNext();) {
			E removed = iter.next();

			Integer refCount = refCounts.get(removed);
			if (refCount != null) {
				int refs = refCount.intValue();
				if (refs <= 1) {
					removesToFire.add(removed);
					refCounts.remove(removed);
				} else {
					refCount = new Integer(refCount.intValue() - 1);
					refCounts.put(removed, refCount);
				}
			}
		}

		// just in case the removes overlapped with the adds
		addsToFire.removeAll(removesToFire);

		if (addsToFire.size() > 0 || removesToFire.size() > 0) {
			fireSetChange(Diffs.createSetDiff(addsToFire, removesToFire));
		}
	}

	protected void firstListenerAdded() {
		super.firstListenerAdded();

		refCounts = new HashMap<E, Integer>();
		for (MyListenerManager<?> childSet : childSets) {
			childSet.addListener();
			incrementRefCounts(childSet.decoratedSet);
		}
		stalenessTracker = new StalenessTracker(
				childSets.toArray(new IObservable[0]), stalenessConsumer);
		setWrappedSet(refCounts.keySet());
	}

	protected void lastListenerRemoved() {
		super.lastListenerRemoved();

		for (MyListenerManager<?> childSet : childSets) {
			childSet.removeListener();
			stalenessTracker.removeObservable(childSet.decoratedSet);
		}
		refCounts = null;
		stalenessTracker = null;
		setWrappedSet(null);
	}

	/**
	 * For all the elements given to this method, add one to the reference count
	 * in our map that maps elements to their reference count.
	 * 
	 * @param addedElements
	 * @return the elements for which the prior reference count was zero, this
	 *         collection of elements being a subset of the elements passed in
	 */
	private ArrayList<E> incrementRefCounts(
			Collection<? extends E> addedElements) {
		ArrayList<E> adds = new ArrayList<E>();

		for (Iterator<? extends E> iter = addedElements.iterator(); iter
				.hasNext();) {
			E addedElement = iter.next();

			Integer refCount = refCounts.get(addedElement);
			if (refCount == null) {
				adds.add(addedElement);
				refCount = new Integer(1);
				refCounts.put(addedElement, refCount);
			} else {
				refCount = new Integer(refCount.intValue() + 1);
				refCounts.put(addedElement, refCount);
			}
		}
		return adds;
	}

	protected void getterCalled() {
		super.getterCalled();
		if (refCounts == null) {
			// no listeners, recompute
			setWrappedSet(computeElements());
		}
	}

	private Set<E> computeElements() {
		// If there is no cached value, compute the union from scratch
		if (refCounts == null) {
			Set<E> result = new HashSet<E>();
			for (MyListenerManager<?> childSet : childSets) {
				result.addAll(childSet.decoratedSet);
			}
			return result;
		}

		// Else there is a cached value. Return it.
		return refCounts.keySet();
	}

}
