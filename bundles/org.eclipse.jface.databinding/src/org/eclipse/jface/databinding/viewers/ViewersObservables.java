/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matthew Hall - bug 206839, 124684, 239302, 245647
 *******************************************************************************/

package org.eclipse.jface.databinding.viewers;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.internal.databinding.swt.SWTDelayedObservableValueDecorator;
import org.eclipse.jface.internal.databinding.viewers.CheckableCheckedElementsObservableSet;
import org.eclipse.jface.internal.databinding.viewers.CheckboxViewerCheckedElementsObservableSet;
import org.eclipse.jface.internal.databinding.viewers.SelectionProviderMultipleSelectionObservableList;
import org.eclipse.jface.internal.databinding.viewers.SelectionProviderSingleSelectionObservableValue;
import org.eclipse.jface.internal.databinding.viewers.ViewerFiltersObservableSet;
import org.eclipse.jface.internal.databinding.viewers.ViewerInputObservableValue;
import org.eclipse.jface.internal.databinding.viewers.ViewerMultipleSelectionObservableList;
import org.eclipse.jface.internal.databinding.viewers.ViewerObservableValueDecorator;
import org.eclipse.jface.internal.databinding.viewers.ViewerSingleSelectionObservableValue;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckable;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

/**
 * Factory methods for creating observables for JFace viewers
 * 
 * @since 1.1
 */
public class ViewersObservables {
	/**
	 * Returns an observable which delays notification of value change events
	 * from <code>observable</code> until <code>delay</code> milliseconds have
	 * passed since the last change event, or until a FocusOut event is received
	 * from the underlying viewer control (whichever happens earlier). This
	 * class helps to delay validation until the user stops changing the value
	 * (e.g. until a user stops changing a viewer selection). To notify about
	 * pending changes, the returned observable value will fire a stale event
	 * when the wrapped observable value fires a change event, but this change
	 * is being delayed.
	 * 
	 * @param delay
	 *            the delay in milliseconds
	 * @param observable
	 *            the observable being delayed
	 * @return an observable which delays notification of value change events
	 *         from <code>observable</code> until <code>delay</code>
	 *         milliseconds have passed since the last change event.
	 * 
	 * @since 1.3
	 */
	public static IViewerObservableValue observeDelayedValue(int delay,
			IViewerObservableValue observable) {
		Viewer viewer = observable.getViewer();
		return new ViewerObservableValueDecorator(
				new SWTDelayedObservableValueDecorator(Observables
						.observeDelayedValue(delay, observable), viewer
						.getControl()), viewer);
	}

	/**
	 * Returns an observable value that tracks the current selection of the
	 * given selection provider. If the selection provider provides selections
	 * of type {@link IStructuredSelection}, the observable value will be the
	 * first element of the structured selection as returned by
	 * {@link IStructuredSelection#getFirstElement()}.
	 * 
	 * @param selectionProvider
	 * @return the observable value tracking the (single) selection of the given
	 *         selection provider
	 */
	public static IObservableValue observeSingleSelection(
			ISelectionProvider selectionProvider) {
		if (selectionProvider instanceof Viewer) {
			return observeSingleSelection((Viewer) selectionProvider);
		}
		return new SelectionProviderSingleSelectionObservableValue(
				SWTObservables.getRealm(Display.getDefault()),
				selectionProvider);
	}

	/**
	 * Returns an observable list that tracks the current selection of the
	 * given selection provider. Assumes that the selection provider provides
	 * selections of type {@link IStructuredSelection}. Note that the
	 * observable list will not honor the full contract of
	 * <code>java.util.List</code> in that it may delete or reorder elements
	 * based on what the selection provider returns from
	 * {@link ISelectionProvider#getSelection()} after having called
	 * {@link ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)}
	 * based on the requested change to the observable list. The affected
	 * methods are <code>add</code>, <code>addAll</code>, and
	 * <code>set</code>.
	 * 
	 * @param selectionProvider
	 * @return the observable value tracking the (multi) selection of the given
	 *         selection provider
	 * 
	 * @since 1.2
	 */
	public static IObservableList observeMultiSelection(
			ISelectionProvider selectionProvider) {
		if (selectionProvider instanceof Viewer) {
			return observeMultiSelection((Viewer) selectionProvider);
		}
		return new SelectionProviderMultipleSelectionObservableList(
				SWTObservables.getRealm(Display.getDefault()),
				selectionProvider, Object.class);
	}

	/**
	 * Returns an observable value that tracks the current selection of the
	 * given viewer. If the viewer provides selections of type
	 * {@link IStructuredSelection}, the observable value will be the first
	 * element of the structured selection as returned by
	 * {@link IStructuredSelection#getFirstElement()}.
	 * 
	 * @param viewer
	 *            the viewer
	 * @return the observable value tracking the (single) selection of the given
	 *         viewer
	 * @since 1.2
	 */
	public static IViewerObservableValue observeSingleSelection(Viewer viewer) {
		return new ViewerSingleSelectionObservableValue(
				SWTObservables.getRealm(Display.getDefault()),
				viewer);
	}
	
	/**
	 * Returns an observable list that tracks the current selection of the
	 * given viewer. Assumes that the viewer provides
	 * selections of type {@link IStructuredSelection}. Note that the
	 * observable list will not honor the full contract of
	 * <code>java.util.List</code> in that it may delete or reorder elements
	 * based on what the viewer returns from
	 * {@link ISelectionProvider#getSelection()} after having called
	 * {@link ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)}
	 * based on the requested change to the observable list. The affected
	 * methods are <code>add</code>, <code>addAll</code>, and
	 * <code>set</code>.
	 * 
	 * @param viewer
	 * @return the observable value tracking the (multi) selection of the given
	 *         selection provider
	 * 
	 * @since 1.2
	 */
	public static IViewerObservableList observeMultiSelection(
			Viewer viewer) {
		return new ViewerMultipleSelectionObservableList(
				SWTObservables.getRealm(Display.getDefault()),
				viewer, Object.class);
	}
	
	/**
	 * Returns an observable value that tracks the input of the given viewer.
	 * <p>
	 * The returned observer is blind to changes in the viewer's input unless
	 * its {@link IObservableValue#setValue(Object)} method is called directly.
	 * 
	 * @param viewer
	 *            the viewer to observe
	 * @return an observable value tracking the input of the given viewer
	 * @since 1.2
	 */
	public static IObservableValue observeInput(Viewer viewer) {
		return new ViewerInputObservableValue(SWTObservables.getRealm(viewer
				.getControl().getDisplay()), viewer);
	}

	/**
	 * Returns an observable set that tracks the checked elements of the given
	 * <code>ICheckable</code>.
	 * 
	 * @param checkable
	 *            {@link ICheckable} containing the checked elements to track
	 * @param elementType
	 *            element type of the returned set
	 * @return an observable set tracking the checked elements of the given
	 *         checkable.
	 * @since 1.2
	 */
	public static IObservableSet observeCheckedElements(ICheckable checkable,
			Object elementType) {
		if (checkable instanceof CheckboxTableViewer) {
			return observeCheckedElements((CheckboxTableViewer) checkable,
					elementType);
		}
		if (checkable instanceof CheckboxTreeViewer) {
			return observeCheckedElements((CheckboxTreeViewer) checkable,
					elementType);
		}
		return new CheckableCheckedElementsObservableSet(SWTObservables
				.getRealm(Display.getDefault()), checkable, elementType);
	}

	/**
	 * Returns an observable set that tracks the checked elements of the given
	 * viewer. Assumes that the viewer implements {@link ICheckable}.
	 * 
	 * @param viewer
	 *            {@link CheckboxTableViewer} containing the checked elements to
	 *            track.
	 * @param elementType
	 *            element type of the returned set
	 * @return an observable set that tracks the checked elements of the given
	 *         viewer.
	 * @since 1.2
	 */
	public static IViewerObservableSet observeCheckedElements(
			CheckboxTableViewer viewer, Object elementType) {
		return new CheckboxViewerCheckedElementsObservableSet(SWTObservables
				.getRealm(viewer.getControl().getDisplay()), viewer,
				elementType);
	}

	/**
	 * Returns an observable set that tracks the checked elements of the given
	 * viewer. Assumes that the viewer implements {@link ICheckable}.
	 * 
	 * @param viewer
	 *            {@link CheckboxTreeViewer} containing the checked elements to
	 *            track.
	 * @param elementType
	 *            element type of the returned set
	 * @return an observable set that tracks the checked elements of the given
	 *         viewer.
	 * @since 1.2
	 */
	public static IViewerObservableSet observeCheckedElements(
			CheckboxTreeViewer viewer, Object elementType) {
		return new CheckboxViewerCheckedElementsObservableSet(SWTObservables
				.getRealm(viewer.getControl().getDisplay()), viewer,
				elementType);
	}

	/**
	 * Returns an observable set that tracks the filters of the given viewer.
	 * Note that the returned set will not track changes that are made using
	 * direct API on StructuredViewer (by calling
	 * {@link StructuredViewer#addFilter(org.eclipse.jface.viewers.ViewerFilter)
	 * addFilter()},
	 * {@link StructuredViewer#removeFilter(org.eclipse.jface.viewers.ViewerFilter)
	 * removeFilter()}, or
	 * {@link StructuredViewer#setFilters(org.eclipse.jface.viewers.ViewerFilter[])
	 * setFilters()}) -- it is assumed that filters are only changed through the
	 * returned set.
	 * 
	 * @param viewer
	 *            viewer containing the filters to be tracked
	 * @return an observable set that tracks the filters of the given viewer.
	 * @since 1.3
	 */
	public static IViewerObservableSet observeFilters(StructuredViewer viewer) {
		return new ViewerFiltersObservableSet(SWTObservables.getRealm(viewer
				.getControl().getDisplay()), viewer);
	}
}
