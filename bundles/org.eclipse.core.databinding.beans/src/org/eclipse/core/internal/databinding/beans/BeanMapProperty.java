/*******************************************************************************
 * Copyright (c) 2008, 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bugs 195222, 264307, 265561, 301774
 ******************************************************************************/

package org.eclipse.core.internal.databinding.beans;

import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.map.MapDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.map.SimpleMapProperty;

/**
 * @param <S>
 * @param <K>
 * @param <V>
 * @since 3.3
 * 
 */
public class BeanMapProperty<S, K, V> extends SimpleMapProperty<S, K, V> {
	private final PropertyDescriptor propertyDescriptor;
	private final Class<K> keyType;
	private final Class<V> valueType;

	/**
	 * @param propertyDescriptor
	 * @param keyType
	 * @param valueType
	 */
	public BeanMapProperty(PropertyDescriptor propertyDescriptor,
			Class<K> keyType, Class<V> valueType) {
		this.propertyDescriptor = propertyDescriptor;
		this.keyType = keyType;
		this.valueType = valueType;
	}

	public Object getKeyType() {
		return keyType;
	}

	public Object getValueType() {
		return valueType;
	}

	public Class<K> getKeyClass() {
		return keyType;
	}

	public Class<V> getValueClass() {
		return valueType;
	}

	protected Map<K, V> doGetMap(S source) {
		return (Map<K, V>) asMap(BeanPropertyHelper.readProperty(source,
				propertyDescriptor));
	}

	private Map<?, ?> asMap(Object propertyValue) {
		if (propertyValue == null)
			return Collections.emptyMap();
		return (Map<?, ?>) propertyValue;
	}

	protected void doSetMap(S source, Map<K, V> map, MapDiff<K, V> diff) {
		doSetMap(source, map);
	}

	protected void doSetMap(S source, Map<K, V> map) {
		BeanPropertyHelper.writeProperty(source, propertyDescriptor, map);
	}

	public INativePropertyListener<S> adaptListener(
			final ISimplePropertyListener<MapDiff<K, V>> listener) {
		return new BeanPropertyListener<S, MapDiff<K, V>>(this,
				propertyDescriptor, listener) {
			protected MapDiff<K, V> computeDiff(Object oldValue, Object newValue) {
				return Diffs.computeAndCastMapDiff(asMap(oldValue),
						asMap(newValue), keyType, valueType);
			}
		};
	}

	public String toString() {
		String s = BeanPropertyHelper.propertyName(propertyDescriptor) + "{:}"; //$NON-NLS-1$

		if (keyType != null || valueType != null)
			s += "<" + BeanPropertyHelper.shortClassName(keyType) + ", " //$NON-NLS-1$ //$NON-NLS-2$
					+ BeanPropertyHelper.shortClassName(valueType) + ">"; //$NON-NLS-1$
		return s;
	}
}
