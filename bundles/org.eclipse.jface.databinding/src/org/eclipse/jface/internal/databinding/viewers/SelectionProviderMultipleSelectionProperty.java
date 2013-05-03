/*******************************************************************************
 * Copyright (c) 2008, 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bugs 195222, 263413, 265561
 *     Ovidio Mallo - bug 270494
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.viewers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.list.SimpleListProperty;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * @param <S>
 * @since 3.3
 * 
 */
public class SelectionProviderMultipleSelectionProperty<S extends ISelectionProvider>
		extends SimpleListProperty<S, Object> {

	private final boolean isPostSelection;

	/**
	 * Constructor.
	 * 
	 * @param isPostSelection
	 *            Whether the post selection or the normal selection is to be
	 *            observed.
	 */
	public SelectionProviderMultipleSelectionProperty(boolean isPostSelection) {
		this.isPostSelection = isPostSelection;
	}

	public Object getElementType() {
		return Object.class;
	}

	public Class<Object> getElementClass() {
		return Object.class;
	}

	protected List<Object> doGetList(S source) {
		ISelection selection = source.getSelection();
		if (selection instanceof IStructuredSelection) {
			ArrayList<Object> result = new ArrayList<Object>();
			for (Object element : ((IStructuredSelection) selection).toList()) {
				result.add(element);
			}
			return result;
		}
		return Collections.emptyList();
	}

	protected void doSetList(S source, List<Object> list, ListDiff<Object> diff) {
		doSetList(source, list);
	}

	protected void doSetList(S source, List<Object> list) {
		source.setSelection(new StructuredSelection(list));
	}

	public INativePropertyListener<S> adaptListener(
			ISimplePropertyListener<ListDiff<Object>> listener) {
		return new SelectionChangedListener<S, ListDiff<Object>>(this,
				listener, isPostSelection);
	}

	public String toString() {
		return isPostSelection ? "IPostSelectionProvider.postSelection[]" //$NON-NLS-1$
				: "ISelectionProvider.selection[]"; //$NON-NLS-1$
	}
}
