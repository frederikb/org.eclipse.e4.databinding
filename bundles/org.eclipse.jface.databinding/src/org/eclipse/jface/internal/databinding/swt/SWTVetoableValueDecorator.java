/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bug 256543, 190881, 263691, 281723
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.DecoratingVetoableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

/**
 * @param <S>
 * @since 3.3
 * 
 */
public class SWTVetoableValueDecorator<S extends Widget> extends
		DecoratingVetoableValue<String> implements ISWTObservableValue<String> {
	private S widget;
	private WidgetStringValueProperty<S> property;

	private Listener verifyListener = new Listener() {
		public void handleEvent(Event event) {
			String currentText = property.getValue(widget);
			String newText = currentText.substring(0, event.start) + event.text
					+ currentText.substring(event.end);
			if (!fireValueChanging(Diffs.createValueDiff(currentText, newText))) {
				event.doit = false;
			}
		}
	};

	private Listener disposeListener = new Listener() {
		public void handleEvent(Event event) {
			SWTVetoableValueDecorator.this.dispose();
		}
	};

	/**
	 * @param widget
	 * @param property
	 * @param decorated
	 */
	public SWTVetoableValueDecorator(S widget,
			WidgetStringValueProperty<S> property,
			IObservableValue<String> decorated) {
		super(decorated, true);
		this.property = property;
		this.widget = widget;
		Assert.isTrue(
				decorated.getValueType().equals(String.class),
				"SWTVetoableValueDecorator can only decorate observable values of String value type"); //$NON-NLS-1$
		WidgetListenerUtil.asyncAddListener(widget, SWT.Dispose,
				disposeListener);
	}

	protected void firstListenerAdded() {
		super.firstListenerAdded();
		WidgetListenerUtil.asyncAddListener(widget, SWT.Verify, verifyListener);
	}

	protected void lastListenerRemoved() {
		WidgetListenerUtil.asyncRemoveListener(widget, SWT.Verify,
				verifyListener);
		super.lastListenerRemoved();
	}

	public synchronized void dispose() {
		WidgetListenerUtil.asyncRemoveListener(widget, SWT.Verify,
				verifyListener);
		WidgetListenerUtil.asyncRemoveListener(widget, SWT.Dispose,
				disposeListener);
		this.widget = null;
		super.dispose();
	}

	public S getWidget() {
		return widget;
	}
}
