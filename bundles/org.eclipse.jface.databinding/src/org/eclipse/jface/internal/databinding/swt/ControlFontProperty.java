/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bugs 195222, 263413
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.jface.databinding.swt.WidgetValueProperty;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;

/**
 * @since 3.3
 * 
 */
public class ControlFontProperty extends WidgetValueProperty<Control, Font> {
	public Object getValueType() {
		return Font.class;
	}

	protected Font doGetValue(Control source) {
		return source.getFont();
	}

	protected void doSetValue(Control source, Font value) {
		source.setFont(value);
	}

	public String toString() {
		return "Control.font <Font>"; //$NON-NLS-1$
	}
}
