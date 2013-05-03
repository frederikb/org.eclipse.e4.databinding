/*******************************************************************************
 * Copyright (c) 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 263868)
 ******************************************************************************/

package org.eclipse.core.internal.databinding.property.value;

import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.value.SimpleValueProperty;

/**
 * @param <T>
 *            type of the value of the property
 * @since 3.3
 * 
 */
public final class SelfValueProperty<T> extends SimpleValueProperty<T, T> {
	private final Object valueTypeAsObject;
	private final Class<T> valueType;

	/**
	 * @param valueType
	 * @deprecated use the constructor that takes a Class instead
	 */
	public SelfValueProperty(Object valueType) {
		this.valueTypeAsObject = valueType;
		this.valueType = null;
	}

	/**
	 * @param valueType
	 */
	public SelfValueProperty(Class<T> valueType) {
		this.valueTypeAsObject = valueType;
		this.valueType = valueType;
	}

	public Object getValueType() {
		return valueTypeAsObject;
	}

	public Class<T> getValueClass() {
		return valueType;
	}

	protected T doGetValue(T source) {
		return source;
	}

	protected void doSetValue(T source, T value) {
	}

	public INativePropertyListener<T> adaptListener(
			ISimplePropertyListener<ValueDiff<T>> listener) {
		return null;
	}

	protected void doAddListener(T source, INativePropertyListener<T> listener) {
	}

	protected void doRemoveListener(T source,
			INativePropertyListener<T> listener) {
	}
}