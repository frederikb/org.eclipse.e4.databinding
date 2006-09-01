/*******************************************************************************
 * Copyright (c) 2006 The Pampered Chef, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Pampered Chef, Inc. - initial API and implementation
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.provisional.swt;

import org.eclipse.jface.databinding.observable.value.AbstractVetoableValue;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Widget;

/**
 * An abstract superclass for vetoable values that gurantees that the 
 * observable will be disposed when the control to which it is attached is
 * disposed.
 * 
 * @since 3.3
 */
public abstract class AbstractSWTVetoableValue extends AbstractVetoableValue {

	/**
	 * Standard constructor for an SWT VetoableValue.  Makes sure that
	 * the observable gets disposed when the SWT widget is disposed.
	 * 
	 * @param widget
	 */
	protected AbstractSWTVetoableValue(Widget widget) {
		if (widget == null) {
			throw new IllegalArgumentException("The widget parameter is null."); //$NON-NLS-1$
		}
		widget.addDisposeListener(disposeListener);
	}
	
	private DisposeListener disposeListener = new DisposeListener() {
		public void widgetDisposed(DisposeEvent e) {
			AbstractSWTVetoableValue.this.dispose();
		}
	};
}
