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
 *     Matthew Hall - bug 195222, 263413
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.jface.databinding.swt.WidgetValueProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

/**
 * @since 3.3
 * 
 */
public class ControlSizeProperty extends WidgetValueProperty<Control, Point> {
	/**
	 * 
	 */
	public ControlSizeProperty() {
		super(SWT.Resize);
	}

	public Object getValueType() {
		return Point.class;
	}

	protected Point doGetValue(Control source) {
		return source.getSize();
	}

	protected void doSetValue(Control source, Point value) {
		source.setSize(value);
	}

	public String toString() {
		return "Control.size <Point>"; //$NON-NLS-1$
	}
}
