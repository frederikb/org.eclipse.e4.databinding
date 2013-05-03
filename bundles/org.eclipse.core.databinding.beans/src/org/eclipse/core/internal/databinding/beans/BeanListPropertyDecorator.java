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
import java.util.List;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanListProperty;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.list.ListProperty;

/**
 * @param <S>
 * @param <E>
 * @since 3.3
 * 
 */
public class BeanListPropertyDecorator<S, E> extends ListProperty<S, E>
		implements IBeanListProperty<S, E> {
	private final IListProperty<S, E> delegate;
	private final PropertyDescriptor propertyDescriptor;

	/**
	 * @param delegate
	 * @param propertyDescriptor
	 */
	public BeanListPropertyDecorator(IListProperty<S, E> delegate,
			PropertyDescriptor propertyDescriptor) {
		this.delegate = delegate;
		this.propertyDescriptor = propertyDescriptor;
	}

	/**
	 * @deprecated use getElementClass instead
	 */
	public Object getElementType() {
		return delegate.getElementType();
	}

	public Class<E> getElementClass() {
		return delegate.getElementClass();
	}

	protected List<E> doGetList(S source) {
		return delegate.getList(source);
	}

	protected void doSetList(S source, List<E> list) {
		delegate.setList(source, list);
	}

	protected void doUpdateList(S source, ListDiff<E> diff) {
		delegate.updateList(source, diff);
	}

	public <V> IBeanListProperty<S, V> values(String propertyName) {
		return values(propertyName, null);
	}

	public <V> IBeanListProperty<S, V> values(String propertyName,
			Class<V> valueType) {
		Class<E> beanClass = delegate.getElementClass();
		return values(BeanProperties.value(beanClass, propertyName, valueType));
	}

	public <V> IBeanListProperty<S, V> values(IBeanValueProperty<E, V> property) {
		return new BeanListPropertyDecorator<S, V>(super.values(property),
				property.getPropertyDescriptor());
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	public IObservableList<E> observe(S source) {
		return new BeanObservableListDecorator<E>(delegate.observe(source),
				propertyDescriptor);
	}

	public IObservableList<E> observe(Realm realm, S source) {
		return new BeanObservableListDecorator<E>(delegate.observe(realm,
				source), propertyDescriptor);
	}

	public <M extends S> IObservableList<E> observeDetail(
			IObservableValue<M> master) {
		return new BeanObservableListDecorator<E>(
				delegate.observeDetail(master), propertyDescriptor);
	}

	public String toString() {
		return delegate.toString();
	}
}
