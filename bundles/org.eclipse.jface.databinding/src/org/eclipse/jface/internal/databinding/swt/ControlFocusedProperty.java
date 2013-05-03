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
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.IProperty;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

/**
 * @since 3.3
 * 
 */
public class ControlFocusedProperty extends WidgetBooleanValueProperty<Control> {
	/**
	 * 
	 */
	public ControlFocusedProperty() {
		super();
	}

	public boolean doGetBooleanValue(Control source) {
		return source.isFocusControl();
	}

	public void doSetBooleanValue(Control source, boolean value) {
		if (value)
			source.setFocus();
	}

	public INativePropertyListener<Control> adaptListener(
			ISimplePropertyListener<ValueDiff<Boolean>> listener) {
		int[] events = { SWT.FocusIn, SWT.FocusOut };
		return new ControlFocusListener(this, listener, events, null);
	}

	private class ControlFocusListener extends
			WidgetListener<Control, ValueDiff<Boolean>> {
		/**
		 * @param property
		 * @param listener
		 * @param changeEvents
		 * @param staleEvents
		 */
		private ControlFocusListener(IProperty property,
				ISimplePropertyListener<ValueDiff<Boolean>> listener,
				int[] changeEvents, int[] staleEvents) {
			super(property, listener, changeEvents, staleEvents);
		}

		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.FocusIn:
				fireChange(event.widget,
						Diffs.createValueDiff(Boolean.FALSE, Boolean.TRUE));
				break;
			case SWT.FocusOut:
				fireChange(event.widget,
						Diffs.createValueDiff(Boolean.TRUE, Boolean.FALSE));
				break;
			}
		}
	}

	public String toString() {
		return "Control.focus <boolean>"; //$NON-NLS-1$
	}
}
