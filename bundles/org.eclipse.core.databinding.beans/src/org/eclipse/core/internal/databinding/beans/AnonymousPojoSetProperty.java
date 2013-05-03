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
import org.eclipse.core.databinding.property.set.DelegatingSetProperty;
import org.eclipse.core.databinding.property.set.ISetProperty;

/**
 * @param <S>
 * @param <E>
 * @since 3.3
 * 
 */
public class AnonymousPojoSetProperty<S, E> extends DelegatingSetProperty<S, E> {
	private final String propertyName;

	private Map<Class<? extends S>, ISetProperty<S, E>> delegates;

	/**
	 * @param propertyName
	 * @param elementType
	 */
	public AnonymousPojoSetProperty(String propertyName, Class<E> elementType) {
		super(elementType);
		this.propertyName = propertyName;
		this.delegates = new HashMap<Class<? extends S>, ISetProperty<S, E>>();
	}

	protected ISetProperty<S, E> doGetDelegate(S source) {
		Class<? extends S> beanClass = Util.getClass(source);
		if (delegates.containsKey(beanClass))
			return delegates.get(beanClass);

		ISetProperty<S, E> delegate;
		try {
			delegate = PojoProperties.<S, E> set(beanClass, propertyName,
					getElementClass());
		} catch (IllegalArgumentException noSuchProperty) {
			delegate = null;
		}
		delegates.put(beanClass, delegate);
		return delegate;
	}

	public String toString() {
		String s = "?." + propertyName + "{}"; //$NON-NLS-1$ //$NON-NLS-2$
		Class<?> elementType = getElementClass();
		if (elementType != null)
			s += "<" + BeanPropertyHelper.shortClassName(elementType) + ">"; //$NON-NLS-1$//$NON-NLS-2$
		return s;
	}
}
