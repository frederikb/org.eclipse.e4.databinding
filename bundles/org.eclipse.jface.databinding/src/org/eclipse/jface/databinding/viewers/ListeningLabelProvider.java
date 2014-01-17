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

package org.eclipse.jface.databinding.viewers;

import java.util.Iterator;

import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.eclipse.jface.internal.databinding.provisional.viewers.ViewerLabelProvider;

/**
 * @since 1.1
 * 
 * @param <T>
 *            the base class of all elements expected
 */
public abstract class ListeningLabelProvider<T> extends ViewerLabelProvider {

	private ISetChangeListener<T> listener = new ISetChangeListener<T>() {
		public void handleSetChange(SetChangeEvent<T> event) {
			for (Iterator<? extends T> it = event.diff.getAdditions()
					.iterator(); it.hasNext();) {
				addListenerTo(it.next());
			}
			for (Iterator<? extends T> it = event.diff.getRemovals().iterator(); it
					.hasNext();) {
				removeListenerFrom(it.next());
			}
		}
	};

	private IObservableSet<T> items;

	/**
	 * @param itemsThatNeedLabels
	 */
	public ListeningLabelProvider(IObservableSet<T> itemsThatNeedLabels) {
		this.items = itemsThatNeedLabels;
		items.addSetChangeListener(listener);
		for (Iterator<T> it = items.iterator(); it.hasNext();) {
			addListenerTo(it.next());
		}
	}

	/**
	 * @param next
	 */
	protected abstract void removeListenerFrom(Object next);

	/**
	 * @param next
	 */
	protected abstract void addListenerTo(Object next);

	public void dispose() {
		for (Iterator<T> iter = items.iterator(); iter.hasNext();) {
			removeListenerFrom(iter.next());
		}
		items.removeSetChangeListener(listener);
		super.dispose();
	}
}
