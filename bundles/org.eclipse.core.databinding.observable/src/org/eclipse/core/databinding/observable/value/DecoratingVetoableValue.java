/*******************************************************************************
 * Copyright (c) 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 263691)
 ******************************************************************************/

package org.eclipse.core.databinding.observable.value;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.ListenerList;

/**
 * An {@link IVetoableValue} decorator for an observable value.
 * 
 * @param <T>
 * @since 1.2
 */
public class DecoratingVetoableValue<T> extends DecoratingObservableValue<T>
		implements IVetoableValue<T> {

	/**
	 * In addition to the listener/event types supported by
	 * AbstractObservableValue, we add support for one more type.
	 */
	protected ListenerList<IValueChangingListener<T>> valueChangingListListener = null;

	/**
	 * @param decorated
	 * @param disposeDecoratedOnDispose
	 */
	public DecoratingVetoableValue(IObservableValue<T> decorated,
			boolean disposeDecoratedOnDispose) {
		super(decorated, disposeDecoratedOnDispose);
	}

	public void setValue(T value) {
		checkRealm();
		T currentValue = getValue();
		ValueDiff<T> diff = Diffs.createValueDiff(currentValue, value);
		boolean okToProceed = fireValueChanging(diff);
		if (!okToProceed) {
			throw new ChangeVetoException("Change not permitted"); //$NON-NLS-1$
		}
		super.setValue(value);
	}

	public synchronized void addValueChangingListener(
			IValueChangingListener<T> listener) {
		if (valueChangingListListener == null) {
			valueChangingListListener = new ListenerList<IValueChangingListener<T>>();
		}
		addListener(valueChangingListListener, listener);
	}

	public synchronized void removeValueChangingListener(
			IValueChangingListener<T> listener) {
		if (valueChangingListListener != null) {
			removeListener(valueChangingListListener, listener);
		}
	}

	@Override
	protected boolean hasListeners() {
		return ((valueChangingListListener != null && valueChangingListListener
				.hasListeners()) || super.hasListeners());
	}

	/**
	 * Notifies listeners about a pending change, and returns true if no
	 * listener vetoed the change.
	 * 
	 * @param diff
	 * @return false if the change was vetoed, true otherwise
	 */
	protected boolean fireValueChanging(ValueDiff<T> diff) {
		checkRealm();

		ValueChangingEvent<T> event = new ValueChangingEvent<T>(this, diff);
		if (valueChangingListListener != null) {
			valueChangingListListener.fireEvent(event);
		}
		return !event.veto;
	}
}
