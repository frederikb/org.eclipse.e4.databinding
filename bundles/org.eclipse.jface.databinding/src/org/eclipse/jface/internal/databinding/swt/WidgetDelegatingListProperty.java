/*******************************************************************************
 * Copyright (c) 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 264286)
 *******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.core.databinding.property.list.DelegatingListProperty;
import org.eclipse.jface.databinding.swt.ISWTObservableList;
import org.eclipse.jface.databinding.swt.IWidgetListProperty;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Widget;

public abstract class WidgetDelegatingListProperty<S extends Widget, E> extends
		DelegatingListProperty<S, E> implements IWidgetListProperty<S, E> {
	RuntimeException notSupported(Object source) {
		return new IllegalArgumentException(
				"Widget [" + source.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * 
	 * @param elementType
	 * @deprecated use the form which takes Class as a parameter. This is safer
	 *             because code in this plug-in fails anyway if a Class is not
	 *             passed.
	 */
	public WidgetDelegatingListProperty(Object elementType) {
		super(elementType);
	}

	public WidgetDelegatingListProperty(Class<E> elementType) {
		super(elementType);
	}

	public ISWTObservableList<E> observe(S widget) {
		return (ISWTObservableList<E>) observe(
				SWTObservables.getRealm(widget.getDisplay()), widget);
	}
}