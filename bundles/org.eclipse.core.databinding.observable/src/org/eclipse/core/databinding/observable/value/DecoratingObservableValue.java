/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 237718)
 *     Matthew Hall - but 246626
 ******************************************************************************/

package org.eclipse.core.databinding.observable.value;

import org.eclipse.core.databinding.observable.DecoratingObservable;
import org.eclipse.core.databinding.observable.ListenerList;

/**
 * An observable value which decorates another observable value.
 * 
 * @param <T>
 * 
 * @since 1.2
 */
public class DecoratingObservableValue<T> extends DecoratingObservable
		implements IObservableValue<T> {
	private IObservableValue<T> decorated;

	private IValueChangeListener<T> valueChangeListener;

	/**
	 * In addition to the three listener/event types supported by ChangeSupport,
	 * we add support for one more type.
	 */
	protected ListenerList<IValueChangeListener<T>> valueChangeListenerList = null;

	/**
	 * Constructs a DecoratingObservableValue which decorates the given
	 * observable.
	 * 
	 * @param decorated
	 *            the observable value being decorated
	 * @param disposeDecoratedOnDispose
	 */
	public DecoratingObservableValue(IObservableValue<T> decorated,
			boolean disposeDecoratedOnDispose) {
		super(decorated, disposeDecoratedOnDispose);
		this.decorated = decorated;
	}

	public synchronized void addValueChangeListener(
			IValueChangeListener<T> listener) {
		addListener(getValueChangeListenerList(), listener);
	}

	public synchronized void removeValueChangeListener(
			IValueChangeListener<T> listener) {
		removeListener(getValueChangeListenerList(), listener);
	}

	private ListenerList<IValueChangeListener<T>> getValueChangeListenerList() {
		if (valueChangeListenerList == null) {
			valueChangeListenerList = new ListenerList<IValueChangeListener<T>>();
		}
		return valueChangeListenerList;
	}

	protected boolean hasListeners() {
		return (valueChangeListenerList != null && valueChangeListenerList
				.hasListeners()) || super.hasListeners();
	}

	protected void fireValueChange(ValueDiff<T> diff) {
		// fire general change event first
		super.fireChange();
		if (valueChangeListenerList != null) {
			valueChangeListenerList.fireEvent(new ValueChangeEvent<T>(this,
					diff));
		}
	}

	protected void fireChange() {
		throw new RuntimeException(
				"fireChange should not be called, use fireValueChange() instead"); //$NON-NLS-1$
	}

	protected void firstListenerAdded() {
		if (valueChangeListener == null) {
			valueChangeListener = new IValueChangeListener<T>() {
				public void handleValueChange(ValueChangeEvent<T> event) {
					DecoratingObservableValue.this.handleValueChange(event);
				}
			};
		}
		decorated.addValueChangeListener(valueChangeListener);
		super.firstListenerAdded();
	}

	protected void lastListenerRemoved() {
		super.lastListenerRemoved();
		if (valueChangeListener != null) {
			decorated.removeValueChangeListener(valueChangeListener);
			valueChangeListener = null;
		}
	}

	/**
	 * Called whenever a ValueChangeEvent is received from the decorated
	 * observable. By default, this method fires the value change event again,
	 * with the decorating observable as the event source. Subclasses may
	 * override to provide different behavior.
	 * 
	 * @param event
	 *            the change event received from the decorated observable
	 */
	protected void handleValueChange(final ValueChangeEvent<T> event) {
		fireValueChange(event.diff);
	}

	public T getValue() {
		getterCalled();
		return decorated.getValue();
	}

	public void setValue(T value) {
		checkRealm();
		decorated.setValue(value);
	}

	public Object getValueType() {
		return decorated.getValueType();
	}

	/**
	 * @since 1.5
	 */
	public Class<T> getValueClass() {
		return decorated.getValueClass();
	}

	public synchronized void dispose() {
		if (decorated != null && valueChangeListener != null) {
			decorated.removeValueChangeListener(valueChangeListener);
		}
		decorated = null;
		valueChangeListener = null;
		super.dispose();
	}
}
