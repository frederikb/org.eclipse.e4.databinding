/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 247997)
 *     Matthew Hall - bugs 264307, 264619
 ******************************************************************************/

package org.eclipse.core.internal.databinding.beans;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.masterdetail.MasterDetailObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.DelegatingValueProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;

/**
 * @param <S>
 * @param <T>
 * @since 3.3
 * 
 */
public class AnonymousBeanValueProperty<S, T> extends
		DelegatingValueProperty<S, T> {
	private final String propertyName;

	private Map<Class<? extends S>, IValueProperty<S, T>> delegates;

	/**
	 * @param propertyName
	 * @param valueType
	 */
	public AnonymousBeanValueProperty(String propertyName, Class<T> valueType) {
		super(valueType);
		this.propertyName = propertyName;
		this.delegates = new HashMap<Class<? extends S>, IValueProperty<S, T>>();
	}

	protected IValueProperty<S, T> doGetDelegate(S source) {
		Class<? extends S> beanClass = Util.getClass(source);
		return getClassDelegate(beanClass);
	}

	private IValueProperty<S, T> getClassDelegate(Class<? extends S> beanClass) {
		if (delegates.containsKey(beanClass))
			return delegates.get(beanClass);

		IValueProperty<S, T> delegate;
		try {
			delegate = BeanProperties.<S, T> value(beanClass, propertyName,
					getValueClass());
		} catch (IllegalArgumentException noSuchProperty) {
			delegate = null;
		}
		delegates.put(beanClass, delegate);
		return delegate;
	}

	public <M extends S> IObservableValue<T> observeDetail(
			IObservableValue<M> master) {
		Object valueType = getValueType();
		if (valueType == null)
			valueType = inferValueType(master.getValueType());
		return MasterDetailObservables.detailValue(master,
				valueFactory(master.getRealm()), valueType);
	}

	private Object inferValueType(Object masterObservableValueType) {
		if (masterObservableValueType instanceof Class) {
			return getClassDelegate(
					(Class<? extends S>) masterObservableValueType)
					.getValueClass();
		}
		return null;
	}

	public String toString() {
		String s = "?." + propertyName; //$NON-NLS-1$
		Class<T> valueType = getValueClass();
		if (valueType != null)
			s += "<" + BeanPropertyHelper.shortClassName(valueType) + ">"; //$NON-NLS-1$//$NON-NLS-2$
		return s;
	}
}
