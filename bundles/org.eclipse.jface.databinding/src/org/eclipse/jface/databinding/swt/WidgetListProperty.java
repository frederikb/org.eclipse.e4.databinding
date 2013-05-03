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

package org.eclipse.jface.databinding.swt;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.property.list.SimpleListProperty;
import org.eclipse.jface.internal.databinding.swt.SWTObservableListDecorator;
import org.eclipse.swt.widgets.Widget;

/**
 * Abstract list property implementation for {@link Widget} properties. This
 * class implements some basic behavior that widget properties are generally
 * expected to have, namely:
 * <ul>
 * <li>Calling {@link #observe(Object)} should create the observable on the
 * display realm of the widget, rather than the current default realm
 * <li>All <code>observe()</code> methods should return an
 * {@link ISWTObservable}
 * </ul>
 * 
 * @param <S>
 * @param <E>
 * 
 * @since 1.3
 */
public abstract class WidgetListProperty<S extends Widget, E> extends
		SimpleListProperty<S, E> implements IWidgetListProperty<S, E> {

	/**
	 * @since 1.7
	 */
	public ISWTObservableList<E> observe(Realm realm, S widget) {
		return new SWTObservableListDecorator<E>(super.observe(realm, widget),
				widget);
	}

	public ISWTObservableList<E> observe(S widget) {
		return observe(SWTObservables.getRealm(widget.getDisplay()), widget);
	}
}