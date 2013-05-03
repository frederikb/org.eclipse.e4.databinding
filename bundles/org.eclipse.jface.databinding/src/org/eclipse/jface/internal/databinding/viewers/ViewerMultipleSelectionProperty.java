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

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.IViewerListProperty;
import org.eclipse.jface.databinding.viewers.IViewerObservableList;
import org.eclipse.jface.viewers.Viewer;

/**
 * @param <S>
 * @since 3.3
 * 
 */
public class ViewerMultipleSelectionProperty<S extends Viewer> extends
		SelectionProviderMultipleSelectionProperty<S> implements
		IViewerListProperty<S> {

	/**
	 * Constructor.
	 * 
	 * @param isPostSelection
	 *            Whether the post selection or the normal selection is to be
	 *            observed.
	 */
	public ViewerMultipleSelectionProperty(boolean isPostSelection) {
		super(isPostSelection);
	}

	public IViewerObservableList<S> observe(S source) {
		return observe(
				SWTObservables.getRealm(source.getControl().getDisplay()),
				source);
	}

	public IViewerObservableList<S> observe(Realm realm, S source) {
		IObservableList<Object> observable = super.observe(realm, source);
		return new ViewerObservableListDecorator<S>(observable, source);
	}
}
