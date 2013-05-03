/*******************************************************************************
 * Copyright (c) 2008, 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 195222)
 *     Matthew Hall - bug 264307
 ******************************************************************************/

package org.eclipse.core.internal.databinding.beans;

import java.beans.PropertyDescriptor;
import java.util.Set;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanMapProperty;
import org.eclipse.core.databinding.beans.IBeanSetProperty;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.set.ISetProperty;
import org.eclipse.core.databinding.property.set.SetProperty;

/**
 * @param <S>
 * @param <E>
 * @since 3.3
 * 
 */
public class BeanSetPropertyDecorator<S, E> extends SetProperty<S, E> implements
		IBeanSetProperty<S, E> {
	private final ISetProperty<S, E> delegate;
	private final PropertyDescriptor propertyDescriptor;

	/**
	 * @param delegate
	 * @param propertyDescriptor
	 */
	public BeanSetPropertyDecorator(ISetProperty<S, E> delegate,
			PropertyDescriptor propertyDescriptor) {
		this.delegate = delegate;
		this.propertyDescriptor = propertyDescriptor;
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	/**
	 * @deprecated use getElementClass instead
	 */
	public Object getElementType() {
		return delegate.getElementType();
	}

	public Class<E> getElementClass() {
		return delegate.getElementClass();
	}

	protected Set<E> doGetSet(S source) {
		return delegate.getSet(source);
	}

	protected void doSetSet(S source, Set<E> set) {
		delegate.setSet(source, set);
	}

	protected void doUpdateSet(S source, SetDiff<E> diff) {
		delegate.updateSet(source, diff);
	}

	public <T> IBeanMapProperty<S, E, T> values(String propertyName) {
		return values(propertyName, null);
	}

	public <T> IBeanMapProperty<S, E, T> values(String propertyName,
			Class<T> valueType) {
		Class<E> beanClass = delegate.getElementClass();
		return values(BeanProperties.value(beanClass, propertyName, valueType));
	}

	public <T> IBeanMapProperty<S, E, T> values(
			IBeanValueProperty<E, T> property) {
		return new BeanMapPropertyDecorator<S, E, T>(super.values(property),
				property.getPropertyDescriptor());
	}

	public IObservableSet<E> observe(S source) {
		return new BeanObservableSetDecorator<E>(delegate.observe(source),
				propertyDescriptor);
	}

	public IObservableSet<E> observe(Realm realm, S source) {
		return new BeanObservableSetDecorator<E>(
				delegate.observe(realm, source), propertyDescriptor);
	}

	public <M extends S> IObservableSet<E> observeDetail(
			IObservableValue<M> master) {
		return new BeanObservableSetDecorator<E>(
				delegate.observeDetail(master), propertyDescriptor);
	}

	public String toString() {
		return delegate.toString();
	}
}
