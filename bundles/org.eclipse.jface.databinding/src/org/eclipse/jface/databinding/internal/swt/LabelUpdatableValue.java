/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.databinding.internal.swt;

import org.eclipse.jface.databinding.IChangeEvent;
import org.eclipse.jface.databinding.UpdatableValue;
import org.eclipse.swt.widgets.Label;

/**
 * @since 3.2
 *
 */
public class LabelUpdatableValue extends UpdatableValue {

	private final Label label;

	/**
	 * @param label
	 */
	public LabelUpdatableValue(Label label) {
		this.label = label;
	}

	public void setValue(Object value) {
		String oldValue = label.getText();
		label.setText(value == null ? "" : value.toString()); //$NON-NLS-1$
		fireChangeEvent(IChangeEvent.CHANGE, oldValue, label.getText());
	}

	public Object getValue() {
		return label.getText();
	}

	public Class getValueType() {
		return String.class;
	}
}