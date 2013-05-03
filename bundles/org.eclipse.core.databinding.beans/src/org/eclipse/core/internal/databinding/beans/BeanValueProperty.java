/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bug 195222, 264307, 265561
 ******************************************************************************/

package org.eclipse.core.internal.databinding.beans;

import java.beans.PropertyDescriptor;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.value.SimpleValueProperty;

/**
 * @param <S>
 * @param <T>
 * @since 3.3
 * 
 */
public class BeanValueProperty<S, T> extends SimpleValueProperty<S, T> {
	private final PropertyDescriptor propertyDescriptor;
	private final Class<T> valueType;

	/**
	 * @param propertyDescriptor
	 * @param valueType
	 */
	public BeanValueProperty(PropertyDescriptor propertyDescriptor,
			Class<T> valueType) {
		if (valueType == null) {
			throw new IllegalArgumentException("valueType cannot be null."); //$NON-NLS-1$
		}
		Class<?> propertyType = propertyDescriptor.getPropertyType();
		propertyType = Util.convertToObjectClass(propertyType);
		if (valueType != propertyType) {
			throw new IllegalArgumentException(
					"valueType does not match the actual property type."); //$NON-NLS-1$
		}
		this.propertyDescriptor = propertyDescriptor;
		this.valueType = valueType;
	}

	public Class<T> getValueType() {
		return valueType;
	}

	public Class<T> getValueClass() {
		return valueType;
	}

	protected T doGetValue(S source) {
		Object value = BeanPropertyHelper.readProperty(source,
				propertyDescriptor);
		return valueType.cast(value);
	}

	protected void doSetValue(S source, T value) {
		BeanPropertyHelper.writeProperty(source, propertyDescriptor, value);
	}

	public INativePropertyListener<S> adaptListener(
			final ISimplePropertyListener<ValueDiff<T>> listener) {
		return new BeanPropertyListener<S, ValueDiff<T>>(this,
				propertyDescriptor, listener) {
			protected ValueDiff<T> computeDiff(Object oldValue, Object newValue) {
				return Diffs.createValueDiff(valueType.cast(oldValue),
						valueType.cast(newValue));
			}
		};
	}

	public String toString() {
		String s = BeanPropertyHelper.propertyName(propertyDescriptor);
		if (valueType != null)
			s += "<" + BeanPropertyHelper.shortClassName(valueType) + ">"; //$NON-NLS-1$//$NON-NLS-2$
		return s;
	}
}
