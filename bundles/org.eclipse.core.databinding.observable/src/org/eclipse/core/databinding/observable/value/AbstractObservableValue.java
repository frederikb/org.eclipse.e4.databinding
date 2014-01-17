/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brad Reynolds - bug 164653
 *     Matthew Hall - bugs 208332, 263691
 *******************************************************************************/

package org.eclipse.core.databinding.observable.value;

import org.eclipse.core.databinding.observable.AbstractObservable;
import org.eclipse.core.databinding.observable.ListenerList;
import org.eclipse.core.databinding.observable.ObservableTracker;
import org.eclipse.core.databinding.observable.Realm;

/**
 * 
 * <p>
 * This class is thread safe. All state accessing methods must be invoked from
 * the {@link Realm#isCurrent() current realm}. Methods for adding and removing
 * listeners may be invoked from any thread.
 * </p>
 * 
 * @param <T>
 * @since 1.0
 * 
 */
abstract public class AbstractObservableValue<T> extends AbstractObservable
		implements IObservableValue<T> {

	/**
	 * In addition to the three listener/event types supported by ChangeSupport,
	 * we add support for one more type.
	 */
	protected ListenerList<IValueChangeListener<T>> valueListenerList = null;

	/**
	 * Constructs a new instance with the default realm.
	 */
	public AbstractObservableValue() {
		this(Realm.getDefault());
	}

	/**
	 * @param realm
	 */
	public AbstractObservableValue(Realm realm) {
		super(realm);
	}

	public synchronized void addValueChangeListener(
			IValueChangeListener<T> listener) {
		addListener(getValueChangesetListenerList(), listener);
	}

	public synchronized void removeValueChangeListener(
			IValueChangeListener<T> listener) {
		if (valueListenerList != null) {
			removeListener(valueListenerList, listener);
		}
	}

	private ListenerList<IValueChangeListener<T>> getValueChangesetListenerList() {
		if (valueListenerList == null) {
			valueListenerList = new ListenerList<IValueChangeListener<T>>();
		}
		return valueListenerList;
	}

	@Override
	protected boolean hasListeners() {
		return ((valueListenerList != null && valueListenerList.hasListeners()) || super
				.hasListeners());
	}

	final public void setValue(T value) {
		checkRealm();
		doSetValue(value);
	}

	/**
	 * Template method for setting the value of the observable. By default the
	 * method throws an {@link UnsupportedOperationException}.
	 * 
	 * @param value
	 */
	protected void doSetValue(T value) {
		throw new UnsupportedOperationException();
	}

	protected void fireValueChange(ValueDiff<T> diff) {
		// fire general change event first
		super.fireChange();
		if (valueListenerList != null) {
			valueListenerList.fireEvent(new ValueChangeEvent<T>(this, diff));
		}
	}

	public final T getValue() {
		getterCalled();
		return doGetValue();
	}

	abstract protected T doGetValue();

	public boolean isStale() {
		getterCalled();
		return false;
	}

	private void getterCalled() {
		ObservableTracker.getterCalled(this);

	}

	protected void fireChange() {
		throw new RuntimeException(
				"fireChange should not be called, use fireValueChange() instead"); //$NON-NLS-1$
	}

	/**
	 * This is a default implementation that should ideally be overridden to use
	 * a properly typed Class field. This implementation checks to see if the
	 * value type is of type Class and, if it is, it assumes it is the class of
	 * the values and makes an unchecked cast.
	 * 
	 * @return Class to which values of this observable are constrained
	 * @since 1.5
	 */
	public Class<T> getValueClass() {
		Object valueType = getValueType();
		if (valueType instanceof Class) {
			return (Class<T>) valueType;
		}
		return null;
	}

	public synchronized void dispose() {
		valueListenerList = null;
		super.dispose();
	}

}
