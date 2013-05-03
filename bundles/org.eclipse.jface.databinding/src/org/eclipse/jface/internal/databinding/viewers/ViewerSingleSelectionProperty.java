/*******************************************************************************
 * Copyright (c) 2008, 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bugs 195222, 263413, 265561, 271080
 *     Ovidio Mallo - bug 270494
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.viewers;

import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.jface.databinding.viewers.IViewerValueProperty;
import org.eclipse.jface.databinding.viewers.ViewerValueProperty;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;

/**
 * Use this only when the source is not a viewer.
 * 
 * @param <S>
 * 
 * @since 3.3
 * 
 */
public class ViewerSingleSelectionProperty<S extends Viewer> extends
		ViewerValueProperty<S> implements IViewerValueProperty<S> {

	private final boolean isPostSelection;

	/**
	 * Constructor.
	 * 
	 * @param isPostSelection
	 *            Whether the post selection or the normal selection is to be
	 *            observed.
	 */
	public ViewerSingleSelectionProperty(boolean isPostSelection) {
		this.isPostSelection = isPostSelection;
	}

	public Object getValueType() {
		return null;
	}

	protected Object doGetValue(S source) {
		ISelection selection = source.getSelection();
		if (selection instanceof IStructuredSelection) {
			return ((IStructuredSelection) selection).getFirstElement();
		}
		return null;
	}

	protected void doSetValue(S source, Object value) {
		IStructuredSelection selection = value == null ? StructuredSelection.EMPTY
				: new StructuredSelection(value);
		source.setSelection(selection);
	}

	public INativePropertyListener<S> adaptListener(
			ISimplePropertyListener<ValueDiff<Object>> listener) {
		return new SelectionChangedListener<S, ValueDiff<Object>>(this,
				listener, isPostSelection);
	}

	public String toString() {
		return isPostSelection ? "IPostSelectionProvider.postSelection" //$NON-NLS-1$
				: "ISelectionProvider.selection"; //$NON-NLS-1$
	}
}
