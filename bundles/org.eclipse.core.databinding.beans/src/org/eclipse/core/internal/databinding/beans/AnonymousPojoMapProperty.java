/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 247997)
 *     Matthew Hall - bug 264307
 ******************************************************************************/

package org.eclipse.core.internal.databinding.beans;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.property.map.DelegatingMapProperty;
import org.eclipse.core.databinding.property.map.IMapProperty;

/**
 * @param <S>
 * @param <K>
 * @param <V>
 * @since 3.3
 * 
 */
public class AnonymousPojoMapProperty<S, K, V> extends
		DelegatingMapProperty<S, K, V> {
	private final String propertyName;

	private Map<Class<? extends S>, IMapProperty<S, K, V>> delegates;

	/**
	 * @param propertyName
	 * @param keyType
	 * @param valueType
	 */
	public AnonymousPojoMapProperty(String propertyName, Class<K> keyType,
			Class<V> valueType) {
		super(keyType, valueType);
		this.propertyName = propertyName;
		this.delegates = new HashMap<Class<? extends S>, IMapProperty<S, K, V>>();
	}

	protected IMapProperty<S, K, V> doGetDelegate(S source) {
		Class<? extends S> beanClass = Util.getClass(source);
		if (delegates.containsKey(beanClass))
			return delegates.get(beanClass);

		IMapProperty<S, K, V> delegate;
		try {
			delegate = PojoProperties.<S, K, V> map(beanClass, propertyName,
					getKeyClass(), getValueClass());
		} catch (IllegalArgumentException noSuchProperty) {
			delegate = null;
		}
		delegates.put(beanClass, delegate);
		return delegate;
	}

	public String toString() {
		String s = "?." + propertyName + "{:}"; //$NON-NLS-1$ //$NON-NLS-2$
		Class<K> keyType = getKeyClass();
		Class<V> valueType = getValueClass();
		if (keyType != null || valueType != null) {
			s += "<" + BeanPropertyHelper.shortClassName(keyType) + ", " //$NON-NLS-1$//$NON-NLS-2$
					+ BeanPropertyHelper.shortClassName(valueType) + ">"; //$NON-NLS-1$
		}
		return s;
	}
}
