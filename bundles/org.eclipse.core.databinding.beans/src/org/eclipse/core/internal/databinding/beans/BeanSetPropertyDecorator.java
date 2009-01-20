/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 195222)
 ******************************************************************************/

package org.eclipse.core.internal.databinding.beans;

import java.beans.PropertyDescriptor;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanMapProperty;
import org.eclipse.core.databinding.beans.IBeanSetProperty;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.set.ISetProperty;
import org.eclipse.core.databinding.property.set.SetProperty;

/**
 * @since 3.3
 * 
 */
public class BeanSetPropertyDecorator extends SetProperty implements
		IBeanSetProperty {
	private final ISetProperty delegate;
	private final PropertyDescriptor propertyDescriptor;

	/**
	 * @param delegate
	 * @param propertyDescriptor
	 */
	public BeanSetPropertyDecorator(ISetProperty delegate,
			PropertyDescriptor propertyDescriptor) {
		this.delegate = delegate;
		this.propertyDescriptor = propertyDescriptor;
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	public Object getElementType() {
		return delegate.getElementType();
	}

	public IBeanMapProperty values(String propertyName) {
		return values(propertyName, null);
	}

	public IBeanMapProperty values(String propertyName, Class valueType) {
		Class beanClass = (Class) delegate.getElementType();
		return values(BeanProperties.value(beanClass, propertyName, valueType));
	}

	public IBeanMapProperty values(IBeanValueProperty property) {
		return new BeanMapPropertyDecorator(super.values(property), property
				.getPropertyDescriptor());
	}

	public IObservableSet observe(Object source) {
		return new BeanObservableSetDecorator(delegate.observe(source),
				propertyDescriptor);
	}

	public IObservableSet observe(Realm realm, Object source) {
		return new BeanObservableSetDecorator(delegate.observe(realm, source),
				propertyDescriptor);
	}

	public IObservableFactory setFactory() {
		return delegate.setFactory();
	}

	public IObservableFactory setFactory(Realm realm) {
		return delegate.setFactory(realm);
	}

	public IObservableSet observeDetail(IObservableValue master) {
		return new BeanObservableSetDecorator(delegate.observeDetail(master),
				propertyDescriptor);
	}
}
