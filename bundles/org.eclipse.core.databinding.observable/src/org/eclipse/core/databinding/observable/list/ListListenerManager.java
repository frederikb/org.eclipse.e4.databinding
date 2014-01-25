/*******************************************************************************
 * Copyright (c) 2013 Nigel Westbury and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nigel Westbury - initial API and implementation
 ******************************************************************************/

package org.eclipse.core.databinding.observable.list;

/**
 * This class was essentially created to aid with generics and type-safety. It
 * allows <?> to be bound to <E2>.
 * 
 * @param <E2>
 * @since 1.5
 */
public abstract class ListListenerManager<E2> {
	/**
	 * @param decoratedList
	 */
	public ListListenerManager(IObservableList<E2> decoratedList) {
		this.decoratedList = decoratedList;
	}

	/**
	 * 
	 */
	public IObservableList<E2> decoratedList;
	protected IListChangeListener<E2> listChangeListener;

	/**
	 * 
	 */
	public void addListener() {
		if (listChangeListener == null) {
			listChangeListener = new IListChangeListener<E2>() {
				public void handleListChange(ListChangeEvent<E2> event) {
					ListListenerManager.this.handleListChange(event);
				}
			};
		}
		decoratedList.addListChangeListener(listChangeListener);
	}

	/**
	 * @param event
	 */
	protected abstract void handleListChange(ListChangeEvent<E2> event);

	/**
	 * 
	 */
	public void removeListener() {
		if (listChangeListener != null) {
			decoratedList.removeListChangeListener(listChangeListener);
			listChangeListener = null;
		}
	}
}