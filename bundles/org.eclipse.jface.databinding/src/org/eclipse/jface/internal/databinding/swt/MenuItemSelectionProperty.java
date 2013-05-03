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
import org.eclipse.swt.widgets.MenuItem;

/**
 * 
 */
public class MenuItemSelectionProperty extends
		WidgetBooleanValueProperty<MenuItem> {
	/**
	 * 
	 */
	public MenuItemSelectionProperty() {
		super(SWT.Selection);
	}

	boolean doGetBooleanValue(MenuItem source) {
		return source.getSelection();
	}

	void doSetBooleanValue(MenuItem source, boolean value) {
		source.setSelection(value);
	}

	public String toString() {
		return "MenuItem.selection <Boolean>"; //$NON-NLS-1$
	}
}
