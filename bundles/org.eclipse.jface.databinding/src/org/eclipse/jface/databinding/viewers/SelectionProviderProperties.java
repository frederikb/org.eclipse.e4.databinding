/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.jface.databinding.viewers;

import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.internal.databinding.viewers.SelectionProviderMultipleSelectionProperty;
import org.eclipse.jface.internal.databinding.viewers.SelectionProviderSingleSelectionProperty;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionProvider;

/**
 * @since 1.7
 * 
 */
public class SelectionProviderProperties {

	/**
	 * Returns a list property for observing the multiple selection of an
	 * {@link ISelectionProvider}.
	 * 
	 * @return a list property for observing the multiple selection of an
	 *         {@link ISelectionProvider}.
	 * @since 1.7
	 */
	public static IListProperty<ISelectionProvider, Object> multipleSelection() {
		return new SelectionProviderMultipleSelectionProperty<ISelectionProvider>(
				false);
	}

	/**
	 * Returns a list property for observing the multiple <i>post</i> selection
	 * of an {@link IPostSelectionProvider}.
	 * 
	 * @return a list property for observing the multiple <i>post</i> selection
	 *         of an {@link IPostSelectionProvider}.
	 * 
	 * @since 1.7
	 */
	public static IListProperty<ISelectionProvider, Object> multiplePostSelection() {
		return new SelectionProviderMultipleSelectionProperty<ISelectionProvider>(
				true);
	}

	/**
	 * Returns a value property for observing the single selection of a
	 * {@link ISelectionProvider}.
	 * 
	 * @return a value property for observing the single selection of a
	 *         {@link ISelectionProvider}.
	 * @since 1.7
	 */
	public static IValueProperty<ISelectionProvider, Object> singleSelection() {
		return new SelectionProviderSingleSelectionProperty<ISelectionProvider>(
				false);
	}

	/**
	 * Returns a value property for observing the single selection of a
	 * {@link IPostSelectionProvider}.
	 * 
	 * @return a value property for observing the single selection of a
	 *         {@link IPostSelectionProvider}.
	 * @since 1.7
	 */
	public static IValueProperty<ISelectionProvider, Object> singlePostSelection() {
		return new SelectionProviderSingleSelectionProperty<ISelectionProvider>(
				true);
	}

}
