/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation
 *     Tom Schindl - initial API and implementation
 *     Matthew Hall - bugs 195222, 263413
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.jface.databinding.swt.WidgetValueProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

/**
 * @since 3.3
 * 
 */
public class ControlBoundsProperty extends
		WidgetValueProperty<Control, Rectangle> {
	/**
	 * 
	 */
	public ControlBoundsProperty() {
		super(new int[] { SWT.Resize, SWT.Move });
	}

	public Object getValueType() {
		return Rectangle.class;
	}

	protected Rectangle doGetValue(Control source) {
		return source.getBounds();
	}

	protected void doSetValue(Control source, Rectangle value) {
		source.setBounds(value);
	}

	public String toString() {
		return "Control.bounds <Rectangle>"; //$NON-NLS-1$
	}
}
