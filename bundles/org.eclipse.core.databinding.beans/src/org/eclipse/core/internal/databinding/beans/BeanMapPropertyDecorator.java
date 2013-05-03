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
import java.util.Map;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanMapProperty;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapDiff;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.map.IMapProperty;
import org.eclipse.core.databinding.property.map.MapProperty;

/**
 * @param <S>
 * @param <K>
 * @param <V>
 * @since 3.3
 * 
 */
public class BeanMapPropertyDecorator<S, K, V> extends MapProperty<S, K, V>
		implements IBeanMapProperty<S, K, V> {
	private final IMapProperty<S, K, V> delegate;
	private final PropertyDescriptor propertyDescriptor;

	/**
	 * @param delegate
	 * @param propertyDescriptor
	 */
	public BeanMapPropertyDecorator(IMapProperty<S, K, V> delegate,
			PropertyDescriptor propertyDescriptor) {
		this.delegate = delegate;
		this.propertyDescriptor = propertyDescriptor;
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	/**
	 * @deprecated use getKeyClass instead
	 */
	public Object getKeyType() {
		return delegate.getKeyType();
	}

	/**
	 * @deprecated use getValueClass instead
	 */
	public Object getValueType() {
		return delegate.getValueType();
	}

	public Class<K> getKeyClass() {
		return delegate.getKeyClass();
	}

	public Class<V> getValueClass() {
		return delegate.getValueClass();
	}

	protected Map<K, V> doGetMap(S source) {
		return delegate.getMap(source);
	}

	protected void doSetMap(S source, Map<K, V> map) {
		delegate.setMap(source, map);
	}

	protected void doUpdateMap(S source, MapDiff<K, V> diff) {
		delegate.updateMap(source, diff);
	}

	public <T> IBeanMapProperty<S, K, T> values(String propertyName) {
		return values(propertyName, null);
	}

	public <T> IBeanMapProperty<S, K, T> values(String propertyName,
			Class<T> valueType) {
		Class<V> beanClass = delegate.getValueClass();
		return values(BeanProperties.value(beanClass, propertyName, valueType));
	}

	public <T> IBeanMapProperty<S, K, T> values(
			IBeanValueProperty<V, T> property) {
		return new BeanMapPropertyDecorator<S, K, T>(super.values(property),
				property.getPropertyDescriptor());
	}

	public IObservableMap<K, V> observe(S source) {
		return new BeanObservableMapDecorator<K, V>(delegate.observe(source),
				propertyDescriptor);
	}

	public IObservableMap<K, V> observe(Realm realm, S source) {
		return new BeanObservableMapDecorator<K, V>(delegate.observe(realm,
				source), propertyDescriptor);
	}

	public <U extends S> IObservableMap<K, V> observeDetail(
			IObservableValue<U> master) {
		return new BeanObservableMapDecorator<K, V>(
				delegate.observeDetail(master), propertyDescriptor);
	}

	public String toString() {
		return delegate.toString();
	}
}
