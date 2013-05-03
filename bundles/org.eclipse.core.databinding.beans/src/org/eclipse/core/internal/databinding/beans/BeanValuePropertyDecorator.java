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

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanListProperty;
import org.eclipse.core.databinding.beans.IBeanMapProperty;
import org.eclipse.core.databinding.beans.IBeanSetProperty;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.core.databinding.property.value.ValueProperty;

/**
 * @param <S>
 * @param <T>
 * @since 3.3
 * 
 */
public class BeanValuePropertyDecorator<S, T> extends ValueProperty<S, T>
		implements IBeanValueProperty<S, T> {
	private final IValueProperty<S, T> delegate;
	private final PropertyDescriptor propertyDescriptor;

	/**
	 * @param delegate
	 * @param propertyDescriptor
	 */
	public BeanValuePropertyDecorator(IValueProperty<S, T> delegate,
			PropertyDescriptor propertyDescriptor) {
		this.delegate = delegate;
		this.propertyDescriptor = propertyDescriptor;
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	/**
	 * @deprecated use getValueClass instead
	 */
	public Object getValueType() {
		return delegate.getValueType();
	}

	public Class<T> getValueClass() {
		return delegate.getValueClass();
	}

	protected T doGetValue(S source) {
		return delegate.getValue(source);
	}

	protected void doSetValue(S source, T value) {
		delegate.setValue(source, value);
	}

	public <V> IBeanValueProperty<S, V> value(String propertyName) {
		return value(propertyName, null);
	}

	/**
	 * @param propertyName
	 * @param valueType
	 * @return x
	 */
	public <V> IBeanValueProperty<S, V> value(String propertyName,
			Class<V> valueType) {
		Class<T> beanClass = delegate.getValueClass();
		return value(BeanProperties.value(beanClass, propertyName, valueType));
	}

	public <V> IBeanValueProperty<S, V> value(IBeanValueProperty<T, V> property) {
		return new BeanValuePropertyDecorator<S, V>(super.value(property),
				property.getPropertyDescriptor());
	}

	public <E> IBeanListProperty<S, E> list(String propertyName) {
		return list(propertyName, null);
	}

	public <E> IBeanListProperty<S, E> list(String propertyName,
			Class<E> elementType) {
		Class<T> beanClass = delegate.getValueClass();
		return list(BeanProperties.list(beanClass, propertyName, elementType));
	}

	public <E> IBeanListProperty<S, E> list(IBeanListProperty<T, E> property) {
		return new BeanListPropertyDecorator<S, E>(super.list(property),
				property.getPropertyDescriptor());
	}

	public <E> IBeanSetProperty<S, E> set(String propertyName) {
		return set(propertyName, null);
	}

	public <E> IBeanSetProperty<S, E> set(String propertyName,
			Class<E> elementType) {
		Class<T> beanClass = delegate.getValueClass();
		return set(BeanProperties.set(beanClass, propertyName, elementType));
	}

	public <E> IBeanSetProperty<S, E> set(IBeanSetProperty<T, E> property) {
		return new BeanSetPropertyDecorator<S, E>(super.set(property),
				property.getPropertyDescriptor());
	}

	public <K, V> IBeanMapProperty<S, K, V> map(String propertyName) {
		return map(propertyName, null, null);
	}

	public <K, V> IBeanMapProperty<S, K, V> map(String propertyName,
			Class<K> keyType, Class<V> valueType) {
		Class<T> beanClass = delegate.getValueClass();
		return map(BeanProperties.map(beanClass, propertyName, keyType,
				valueType));
	}

	public <K, V> IBeanMapProperty<S, K, V> map(
			IBeanMapProperty<? super T, K, V> property) {
		return new BeanMapPropertyDecorator<S, K, V>(super.map(property),
				property.getPropertyDescriptor());
	}

	public IObservableValue<T> observe(S source) {
		return new BeanObservableValueDecorator<T>(delegate.observe(source),
				propertyDescriptor);
	}

	public IObservableValue<T> observe(Realm realm, S source) {
		return new BeanObservableValueDecorator<T>(delegate.observe(realm,
				source), propertyDescriptor);
	}

	public <M extends S> IObservableValue<T> observeDetail(
			IObservableValue<M> master) {
		return new BeanObservableValueDecorator<T>(
				delegate.observeDetail(master), propertyDescriptor);
	}

	public <M extends S> IObservableList<T> observeDetail(
			IObservableList<M> master) {
		return new BeanObservableListDecorator<T>(
				delegate.observeDetail(master), propertyDescriptor);
	}

	public <M extends S> IObservableMap<M, T> observeDetail(
			IObservableSet<M> master) {
		return new BeanObservableMapDecorator<M, T>(
				delegate.observeDetail(master), propertyDescriptor);
	}

	public <K, M extends S> IObservableMap<K, T> observeDetail(
			IObservableMap<K, M> master) {
		return new BeanObservableMapDecorator<K, T>(
				delegate.observeDetail(master), propertyDescriptor);
	}

	public String toString() {
		return delegate.toString();
	}
}
