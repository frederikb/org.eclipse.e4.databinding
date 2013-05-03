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
import org.eclipse.swt.widgets.Table;

/**
 * @since 3.3
 * 
 */
public class TableSingleSelectionIndexProperty extends
		WidgetIntValueProperty<Table> {
	/**
	 * 
	 */
	public TableSingleSelectionIndexProperty() {
		super(new int[] { SWT.Selection, SWT.DefaultSelection });
	}

	protected Integer doGetValue(Table source) {
		// Ideally we would return null when no selection but
		// that might break existing users so we stick with -1
		return source.getSelectionIndex();
	}

	protected void doSetValue(Table source, Integer value) {
		if (value == null || value.intValue() == -1) {
			source.deselectAll();
		} else {
			source.setSelection(value);
		}
	}

	public String toString() {
		return "Table.selectionIndex <int>"; //$NON-NLS-1$
	}
}
