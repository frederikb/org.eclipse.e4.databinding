/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

/**
 * @since 3.3
 * 
 */
public class ButtonSelectionProperty extends WidgetBooleanValueProperty<Button> {
	/**
	 * 
	 */
	public ButtonSelectionProperty() {
		super(SWT.Selection);
	}

	boolean doGetBooleanValue(Button source) {
		return source.getSelection();
	}

	void doSetBooleanValue(Button source, boolean value) {
		source.setSelection(value);
	}

	public String toString() {
		return "Button.selection <Boolean>"; //$NON-NLS-1$
	}
}
