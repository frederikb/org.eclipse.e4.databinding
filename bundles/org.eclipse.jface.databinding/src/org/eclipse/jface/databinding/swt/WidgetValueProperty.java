/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bugs 263413, 264286, 265561, 262287, 281723
 ******************************************************************************/

package org.eclipse.jface.databinding.swt;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.value.SimpleValueProperty;
import org.eclipse.jface.internal.databinding.swt.SWTObservableValueDecorator;
import org.eclipse.jface.internal.databinding.swt.WidgetListener;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

/**
 * Abstract value property implementation for {@link Widget} properties. This
 * class implements some basic behavior that widget properties are generally
 * expected to have, namely:
 * <ul>
 * <li>Calling {@link #observe(Object)} should create the observable on the
 * display realm of the widget, rather than the current default realm
 * <li>All <code>observe()</code> methods should return an
 * {@link ISWTObservableValue}
 * </ul>
 * This class also provides a default widget listener implementation using SWT's
 * {@link Listener untyped listener API}. Subclasses may pass one or more SWT
 * event type constants to the super constructor to indicate which events signal
 * a property change.
 * 
 * @param <S>
 * @param <T>
 * @since 1.3
 */
public abstract class WidgetValueProperty<S extends Widget, T> extends
		SimpleValueProperty<S, T> implements IWidgetValueProperty<S, T> {
	private int[] changeEvents;
	private int[] staleEvents;

	/**
	 * Constructs a WidgetValueProperty which does not listen for any SWT
	 * events.
	 */
	protected WidgetValueProperty() {
		this(null, null);
	}

	/**
	 * Constructs a WidgetValueProperty with the specified SWT event type
	 * 
	 * @param changeEvent
	 *            SWT event type constant of the event that signifies a property
	 *            change.
	 */
	protected WidgetValueProperty(int changeEvent) {
		this(new int[] { changeEvent }, null);
	}

	/**
	 * Constructs a WidgetValueProperty with the specified SWT event type(s).
	 * 
	 * @param changeEvents
	 *            array of SWT event type constants of the events that signify a
	 *            property change.
	 */
	protected WidgetValueProperty(int[] changeEvents) {
		this(changeEvents, null);
	}

	/**
	 * Constructs a WidgetValueProperty with the specified SWT event types.
	 * 
	 * @param changeEvents
	 *            array of SWT event type constants of the events that signify a
	 *            property change.
	 * @param staleEvents
	 *            array of SWT event type constants of the events that signify a
	 *            property became stale.
	 */
	public WidgetValueProperty(int[] changeEvents, int[] staleEvents) {
		this.changeEvents = changeEvents;
		this.staleEvents = staleEvents;
	}

	public INativePropertyListener<S> adaptListener(
			ISimplePropertyListener<ValueDiff<T>> listener) {
		if (changeEvents == null && staleEvents == null)
			return null;
		return new WidgetListener<S, ValueDiff<T>>(this, listener,
				changeEvents, staleEvents);
	}

	/**
	 * @return observable
	 * @since 1.7
	 */
	public ISWTObservableValue<T> observe(Realm realm, S source) {
		return wrapObservable(super.observe(realm, source), source);
	}

	/**
	 * @param observable
	 * @param widget
	 * @return observable value
	 * @since 1.7
	 */
	protected ISWTObservableValue<T> wrapObservable(
			IObservableValue<T> observable, S widget) {
		return new SWTObservableValueDecorator<T>(observable, widget);
	}

	public ISWTObservableValue<T> observe(S widget) {
		return observe(SWTObservables.getRealm(widget.getDisplay()), widget);
	}

	public ISWTObservableValue<T> observeDelayed(int delay, S widget) {
		return SWTObservables.observeDelayedValue(delay, observe(widget));
	}
}
