/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.core.databinding.observable.set;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;

/**
 * Observable set backed by an observable list. The wrapped list must not
 * contain duplicate elements.
 * 
 * <p>
 * This class is thread safe. All state accessing methods must be invoked from
 * the {@link Realm#isCurrent() current realm}. Methods for adding and removing
 * listeners may be invoked from any thread.
 * </p>
 * 
 * @param <E>
 * @since 1.0
 * 
 */
public class ListToSetAdapter<E> extends ObservableSet<E> {

	private final IObservableList<E> list;

	private IListChangeListener<E> listener = new IListChangeListener<E>() {

		public void handleListChange(ListChangeEvent<E> event) {
			SetDiff<? extends E> newDiff = createSetDiff(event.diff);
			fireSetChange(newDiff);
		}

		private <E2 extends E> SetDiff<E2> createSetDiff(ListDiff<E2> diff) {
			Set<E2> added = new HashSet<E2>();
			Set<E2> removed = new HashSet<E2>();
			List<ListDiffEntry<E2>> differences = diff.getDifferencesAsList();
			for (ListDiffEntry<E2> entry : differences) {
				E2 element = entry.getElement();
				if (entry.isAddition()) {
					if (wrappedSet.add(element)) {
						if (!removed.remove(element))
							added.add(element);
					}
				} else {
					if (wrappedSet.remove(element)) {
						removed.add(element);
						added.remove(element);
					}
				}
			}
			return Diffs.createSetDiff(added, removed);
		}
	};

	/**
	 * @param list
	 */
	public ListToSetAdapter(IObservableList<E> list) {
		super(list.getRealm(), new HashSet<E>(), list.getElementType());
		this.list = list;
		wrappedSet.addAll(list);
		this.list.addListChangeListener(listener);
	}

	public synchronized void dispose() {
		super.dispose();
		if (list != null && listener != null) {
			list.removeListChangeListener(listener);
			listener = null;
		}
	}

}
