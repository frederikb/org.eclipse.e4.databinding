/*******************************************************************************
 * Copyright (c) 2008, 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bug 195222, 263413, 265561
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.viewers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.jface.databinding.viewers.ViewerSetProperty;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @since 3.3
 * 
 */
public class StructuredViewerFiltersProperty extends
		ViewerSetProperty<StructuredViewer, ViewerFilter> {
	public Object getElementType() {
		return ViewerFilter.class;
	}

	public Class<ViewerFilter> getElementClass() {
		return ViewerFilter.class;
	}

	protected Set<ViewerFilter> doGetSet(StructuredViewer source) {
		return new HashSet<ViewerFilter>(Arrays.asList(source.getFilters()));
	}

	public void doSetSet(StructuredViewer source, Set<ViewerFilter> set,
			SetDiff<ViewerFilter> diff) {
		doSetSet(source, set);
	}

	protected void doSetSet(StructuredViewer source, Set<ViewerFilter> set) {
		StructuredViewer viewer = source;
		viewer.getControl().setRedraw(false);
		try {
			viewer.setFilters(set.toArray(new ViewerFilter[set.size()]));
		} finally {
			viewer.getControl().setRedraw(true);
		}
	}

	public INativePropertyListener<StructuredViewer> adaptListener(
			ISimplePropertyListener<SetDiff<ViewerFilter>> listener) {
		return null;
	}

	public String toString() {
		return "StructuredViewer.filters{} <ViewerFilter>"; //$NON-NLS-1$
	}
}
