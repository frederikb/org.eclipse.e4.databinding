/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation
 *     Matthew Hall - bugs 263413, 264286
 ******************************************************************************/

package org.eclipse.jface.databinding.viewers;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.property.list.SimpleListProperty;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.internal.databinding.viewers.ViewerObservableListDecorator;
import org.eclipse.jface.viewers.Viewer;

/**
 * Abstract list property implementation for {@link Viewer} properties. This
 * class implements some basic behavior that viewer properties are generally
 * expected to have, namely:
 * <ul>
 * <li>Calling {@link #observe(Object)} should create the observable on the
 * display realm of the viewer's control, rather than the current default realm
 * <li>All <code>observe()</code> methods should return an
 * {@link IViewerObservableList}
 * </ul>
 * 
 * @param <S>
 * 
 * @since 1.3
 */
public abstract class ViewerListProperty<S extends Viewer> extends
		SimpleListProperty<S, Object> implements IViewerListProperty<S> {
	/**
	 * @since 1.7
	 */
	public IViewerObservableList<S> observe(Realm realm, S source) {
		IObservableList<Object> observable = super.observe(realm, source);
		return new ViewerObservableListDecorator<S>(observable, source);
	}

	public IViewerObservableList<S> observe(S viewer) {
		return observe(
				SWTObservables.getRealm(viewer.getControl().getDisplay()),
				viewer);
	}
}
