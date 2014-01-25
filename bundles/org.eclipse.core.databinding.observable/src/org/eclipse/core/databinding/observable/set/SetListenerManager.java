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

package org.eclipse.core.databinding.observable.set;

/**
 * 
 * @param <E2>
 * @since 1.5
 */
public abstract class SetListenerManager<E2> {
	/**
	 * @param decoratedSet
	 */
	public SetListenerManager(IObservableSet<E2> decoratedSet) {
		this.decoratedSet = decoratedSet;
	}

	/**
	 * 
	 */
	public IObservableSet<E2> decoratedSet;
	protected ISetChangeListener<E2> setChangeListener;

	/**
	 * 
	 */
	public void addListener() {
		if (setChangeListener == null) {
			setChangeListener = new ISetChangeListener<E2>() {
				public void handleSetChange(SetChangeEvent<E2> event) {
					SetListenerManager.this.handleSetChange(event);
				}
			};
		}
		decoratedSet.addSetChangeListener(setChangeListener);
	}

	/**
	 * @param event
	 */
	protected abstract void handleSetChange(SetChangeEvent<E2> event);

	/**
	 * 
	 */
	public void removeListener() {
		if (setChangeListener != null) {
			decoratedSet.removeSetChangeListener(setChangeListener);
			setChangeListener = null;
		}
	}
}