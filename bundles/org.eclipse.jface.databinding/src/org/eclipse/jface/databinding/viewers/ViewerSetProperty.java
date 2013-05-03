/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bugs 263413, 264286
 ******************************************************************************/

package org.eclipse.jface.databinding.viewers;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.set.SimpleSetProperty;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.internal.databinding.viewers.ViewerObservableSetDecorator;
import org.eclipse.jface.viewers.Viewer;

/**
 * Abstract set property implementation for {@link Viewer} properties. This
 * class implements some basic behavior that viewer properties are generally
 * expected to have, namely:
 * <ul>
 * <li>Calling {@link #observe(Object)} should create the observable on the
 * display realm of the viewer's control, rather than the current default realm
 * <li>All <code>observe()</code> methods should return an
 * {@link IViewerObservableSet}
 * </ul>
 * 
 * @param <S>
 * @param <E>
 * 
 * @since 1.3
 */
public abstract class ViewerSetProperty<S extends Viewer, E> extends
		SimpleSetProperty<S, E> implements IViewerSetProperty<S, E> {

	/**
	 * @since 1.7
	 */
	public IViewerObservableSet<S, E> observe(Realm realm, S viewer) {
		IObservableSet<E> observable = super.observe(realm, viewer);
		return new ViewerObservableSetDecorator<S, E>(observable, viewer);
	}

	public IViewerObservableSet<S, E> observe(S viewer) {
		return observe(SWTObservables.getRealm(((Viewer) viewer).getControl()
				.getDisplay()), viewer);
	}
}
