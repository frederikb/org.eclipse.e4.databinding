/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.internal.databinding.internal.swt;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTObservableValue;
import org.eclipse.swt.widgets.Label;

/**
 * @since 3.3
 * 
 */
public class LabelObservableValue extends AbstractSWTObservableValue {

	private final Label label;

	/**
	 * @param label
	 */
	public LabelObservableValue(Label label) {
		super(label);
		this.label = label;
	}

	public void setValue(final Object value) {
		String oldValue = label.getText();
		label.setText(value == null ? "" : value.toString()); //$NON-NLS-1$
		fireValueChange(Diffs.createValueDiff(oldValue, label.getText()));
	}

	public Object doGetValue() {
		return label.getText();
	}

	public Object getValueType() {
		return String.class;
	}

}
