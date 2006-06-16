/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brad Reynolds - bug 137877
 *******************************************************************************/

package org.eclipse.jface.internal.databinding.internal.viewers;

import org.eclipse.jface.internal.databinding.provisional.observable.Diffs;
import org.eclipse.jface.internal.databinding.provisional.observable.value.AbstractObservableValue;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * Observes single selection of an <code>ISelectionProvider</code>.
 * 
 * @since 1.1
 */
public class SelectionProviderSingleSelectionObservableValue extends
		AbstractObservableValue {

	private final ISelectionProvider selectionProvider;

	private boolean updating = false;

	private Object currentSelection;

	/**
	 * Constructs a new instance associated with the provided
	 * <code>selectionProvider</code>. In order to initialize itself properly
	 * the constructor invokes {@link #doGetValue()}. This could be dangerous
	 * for subclasses, see {@link #doGetValue()} for an explanation.
	 * 
	 * @param selectionProvider
	 * @see #doGetValue()
	 */
	public SelectionProviderSingleSelectionObservableValue(
			ISelectionProvider selectionProvider) {
		if (selectionProvider == null) {
			throw new IllegalArgumentException(
					"The 'selectionProvider' parameter is null."); //$NON-NLS-1$
		}

		this.selectionProvider = selectionProvider;
		this.currentSelection = doGetValue();

		selectionProvider
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						if (!updating) {
							Object oldSelection = currentSelection;
							currentSelection = doGetValue();
							fireValueChange(Diffs.createValueDiff(oldSelection,
									currentSelection));
						}
					}
				});
	}

	/**
	 * Sets the selection to the provided <code>value</code>. Value change
	 * events are fired after selection is set in the selection provider.
	 * 
	 * @param value
	 *            object to set as selected, <code>null</code> if wanting to
	 *            remove selection
	 */
	public void setValue(final Object value) {
		try {
			updating = true;

			Object oldSelection = currentSelection;
			selectionProvider
					.setSelection(value == null ? StructuredSelection.EMPTY
							: new StructuredSelection(value));
			currentSelection = doGetValue();
			fireValueChange(Diffs.createValueDiff(oldSelection,
					currentSelection));
		} finally {
			updating = false;
		}
	}

	/**
	 * Retrieves the current selection.
	 * <p>
	 * If a subclass overrides this method it must not depend upon the subclass
	 * to have been fully initialized before this method is invoked.
	 * <code>doGetValue()</code> is invoked by the
	 * {@link #SelectionProviderSingleSelectionObservableValue(ISelectionProvider) constructor}
	 * which means the subclass's constructor will not have fully executed
	 * before this method is invoked.
	 * </p>
	 * 
	 * @return selection will be an instance of
	 *         <code>IStructuredSelection</code> if a selection exists,
	 *         <code>null</code> if no selection
	 * @see #SelectionProviderSingleSelectionObservableValue(ISelectionProvider)
	 */
	protected Object doGetValue() {
		ISelection selection = selectionProvider.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			return sel.getFirstElement();
		}

		return null;
	}

	public Object getValueType() {
		return Object.class;
	}
}