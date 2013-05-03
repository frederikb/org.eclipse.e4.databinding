/*******************************************************************************
 * Copyright (c) 2008, 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bug 264286
 *     Ovidio Mallo - bug 270494
 ******************************************************************************/

package org.eclipse.jface.databinding.viewers;

import org.eclipse.core.databinding.property.set.ISetProperty;
import org.eclipse.jface.internal.databinding.viewers.CheckableCheckedElementsProperty;
import org.eclipse.jface.internal.databinding.viewers.CheckboxTableViewerCheckedElementsProperty;
import org.eclipse.jface.internal.databinding.viewers.CheckboxTreeViewerCheckedElementsProperty;
import org.eclipse.jface.internal.databinding.viewers.StructuredViewerFiltersProperty;
import org.eclipse.jface.internal.databinding.viewers.ViewerInputProperty;
import org.eclipse.jface.internal.databinding.viewers.ViewerMultipleSelectionProperty;
import org.eclipse.jface.internal.databinding.viewers.ViewerSingleSelectionProperty;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckable;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * A factory for creating properties of JFace {@link Viewer viewers}.
 * 
 * @since 1.3
 */
public class ViewerProperties {
	/**
	 * Returns a set property for observing the checked elements of a
	 * {@link CheckboxTableViewer}, {@link CheckboxTreeViewer} or
	 * {@link ICheckable}.
	 * 
	 * @param elementType
	 *            the element type of the returned property
	 * 
	 * @return a set property for observing the checked elements of an
	 *         {@link ICheckable}, for example {@link CheckboxTableViewer} or
	 *         {@link CheckboxTreeViewer}.
	 * @deprecated use one of checkableElements, checkboxTableElements,
	 *             checkboxTreeElements
	 */
	@SuppressWarnings("rawtypes")
	// ok to ignore warnings on deprecated method
	public static IViewerSetProperty checkedElements(Object elementType) {
		return new org.eclipse.jface.internal.databinding.viewers.ViewerCheckedElementsProperty(
				elementType);
	}

	/**
	 * Returns a set property for observing the checked elements of a
	 * {@link CheckboxTableViewer}.
	 * 
	 * @param elementType
	 *            the element type of the returned property
	 * 
	 * @return a set property for observing the checked elements of an
	 *         {@link CheckboxTableViewer}
	 * @since 1.7
	 */
	public static IViewerSetProperty<CheckboxTableViewer, Object> checkboxTableElements(
			Object elementType) {
		return new CheckboxTableViewerCheckedElementsProperty(elementType);
	}

	/**
	 * Returns a set property for observing the checked elements of a
	 * {@link CheckboxTreeViewer}.
	 * 
	 * @param elementType
	 *            the element type of the returned property
	 * 
	 * @return a set property for observing the checked elements of a
	 *         {@link CheckboxTreeViewer}.
	 * @since 1.7
	 */
	public static IViewerSetProperty<CheckboxTreeViewer, Object> checkboxTreeElements(
			Object elementType) {
		return new CheckboxTreeViewerCheckedElementsProperty(elementType);
	}

	/**
	 * Returns a set property for observing the checked elements of a
	 * {@link ICheckable}.
	 * 
	 * @param elementType
	 *            the element type of the returned property
	 * 
	 * @return a set property for observing the checked elements of an
	 *         {@link ICheckable}
	 * @since 1.7
	 */
	public static ISetProperty<ICheckable, Object> checkableElements(
			Object elementType) {
		return new CheckableCheckedElementsProperty(elementType);
	}

	/**
	 * Returns a value property for observing the input of a
	 * {@link StructuredViewer}.
	 * 
	 * @return a value property for observing the input of a
	 *         {@link StructuredViewer}.
	 */
	public static IViewerSetProperty<StructuredViewer, ViewerFilter> filters() {
		return new StructuredViewerFiltersProperty();
	}

	/**
	 * Returns a value property for observing the input of a {@link Viewer}.
	 * 
	 * @return a value property for observing the input of a {@link Viewer}.
	 */
	public static IViewerValueProperty<Viewer> input() {
		return new ViewerInputProperty();
	}

	/**
	 * Returns a value property for observing the single selection of a
	 * {@link ISelectionProvider}.
	 * 
	 * @return a value property for observing the single selection of a
	 *         {@link ISelectionProvider}.
	 * @since 1.7
	 */
	public static IViewerListProperty<Viewer> multipleSelection() {
		return new ViewerMultipleSelectionProperty<Viewer>(false);
	}

	/**
	 * Returns a value property for observing the single <i>post</i> selection
	 * of a {@link IPostSelectionProvider}.
	 * 
	 * @return a value property for observing the single <i>post</i> selection
	 *         of a {@link IPostSelectionProvider}.
	 * 
	 * @since 1.7
	 */
	public static IViewerListProperty<Viewer> multiplePostSelection() {
		return new ViewerMultipleSelectionProperty<Viewer>(true);
	}

	/**
	 * Returns a value property for observing the single selection of a
	 * {@link ISelectionProvider}.
	 * 
	 * @return a value property for observing the single selection of a
	 *         {@link ISelectionProvider}.
	 * @since 1.7
	 */
	public static IViewerValueProperty<Viewer> singleSelection() {
		return new ViewerSingleSelectionProperty<Viewer>(false);
	}

	/**
	 * Returns a value property for observing the single <i>post</i> selection
	 * of a {@link IPostSelectionProvider}.
	 * 
	 * @return a value property for observing the single <i>post</i> selection
	 *         of a {@link IPostSelectionProvider}.
	 * 
	 * @since 1.7
	 */
	public static IViewerValueProperty<Viewer> singlePostSelection() {
		return new ViewerSingleSelectionProperty<Viewer>(true);
	}
}
