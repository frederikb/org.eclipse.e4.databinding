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

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.property.list.DelegatingListProperty;
import org.eclipse.core.databinding.property.list.IListProperty;

/**
 * @param <S>
 * @param <E>
 * @since 3.3
 * 
 */
public class AnonymousBeanListProperty<S, E> extends
		DelegatingListProperty<S, E> {
	private final String propertyName;

	private Map<Class<? extends S>, IListProperty<S, E>> delegates;

	/**
	 * @param propertyName
	 * @param elementType
	 */
	public AnonymousBeanListProperty(String propertyName, Class<E> elementType) {
		super(elementType);
		this.propertyName = propertyName;
		this.delegates = new HashMap<Class<? extends S>, IListProperty<S, E>>();
	}

	protected IListProperty<S, E> doGetDelegate(S source) {
		Class<? extends S> beanClass = Util.getClass(source);
		if (delegates.containsKey(beanClass))
			return delegates.get(beanClass);

		IListProperty<S, E> delegate;
		try {
			delegate = BeanProperties.<S, E> list(beanClass, propertyName,
					getElementClass());
		} catch (IllegalArgumentException noSuchProperty) {
			delegate = null;
		}
		delegates.put(beanClass, delegate);
		return delegate;
	}

	public String toString() {
		String s = "?." + propertyName + "[]"; //$NON-NLS-1$ //$NON-NLS-2$
		Class<E> elementType = getElementClass();
		if (elementType != null)
			s += "<" + BeanPropertyHelper.shortClassName(elementType) + ">"; //$NON-NLS-1$//$NON-NLS-2$
		return s;
	}
}
