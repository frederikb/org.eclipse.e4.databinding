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
 *     Ovidio Mallo - bug 306633
 ******************************************************************************/

package org.eclipse.core.internal.databinding.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.list.SimpleListProperty;
import org.eclipse.core.databinding.util.Policy;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @param <S>
 * @param <E>
 * @since 3.3
 * 
 */
public class BeanListProperty<S, E> extends SimpleListProperty<S, E> {
	private final PropertyDescriptor propertyDescriptor;
	private final Class<E> elementType;

	/**
	 * @param propertyDescriptor
	 * @param elementType
	 */
	public BeanListProperty(PropertyDescriptor propertyDescriptor,
			Class<E> elementType) {
		if (elementType == null) {
			// elementType cannot be null.
			// For legacy reasons, we allow this through but log it.
			// Three cycles after Kepler this should be replaced by the
			// exception (currently
			// commented out) because it is not type safe.
			// throw new IllegalArgumentException("elementType cannot be null."); //$NON-NLS-1$
			Policy.getLog().log(
					new Status(IStatus.WARNING, Policy.JFACE_DATABINDING,
							"elementType cannot be null")); //$NON-NLS-1$

			if (propertyDescriptor.getPropertyType().isArray()) {
				elementType = (Class<E>) propertyDescriptor.getPropertyType()
						.getComponentType();
			} else {
				elementType = (Class<E>) Object.class;
			}
		}

		BeanPropertyHelper.checkCollectionPropertyElementType(
				propertyDescriptor, elementType);
		this.propertyDescriptor = propertyDescriptor;
		this.elementType = elementType;
	}

	public Object getElementType() {
		return elementType;
	}

	public Class<E> getElementClass() {
		return elementType;
	}

	protected List<E> doGetList(S source) {
		return (List<E>) asList(BeanPropertyHelper.readProperty(source,
				propertyDescriptor));
	}

	private List<?> asList(Object propertyValue) {
		if (propertyValue == null)
			return Collections.emptyList();
		if (propertyDescriptor.getPropertyType().isArray())
			return Arrays.asList((Object[]) propertyValue);
		return (List<?>) propertyValue;
	}

	protected void doSetList(S source, List<E> list, ListDiff<E> diff) {
		doSetList(source, list);
	}

	protected void doSetList(S source, List<E> list) {
		BeanPropertyHelper.writeProperty(source, propertyDescriptor,
				convertListToBeanPropertyType(list));
	}

	private Object convertListToBeanPropertyType(List<E> list) {
		Object propertyValue = list;
		if (propertyDescriptor.getPropertyType().isArray()) {
			Class<?> componentType = propertyDescriptor.getPropertyType()
					.getComponentType();
			Object[] array = (Object[]) Array.newInstance(componentType,
					list.size());
			list.toArray(array);
			propertyValue = array;
		}
		return propertyValue;
	}

	public INativePropertyListener<S> adaptListener(
			final ISimplePropertyListener<ListDiff<E>> listener) {
		return new BeanPropertyListener<S, ListDiff<E>>(this,
				propertyDescriptor, listener) {
			protected ListDiff<E> computeDiff(Object oldValue, Object newValue) {
				return Diffs.computeAndCastListDiff(asList(oldValue),
						asList(newValue), elementType);
			}
		};
	}

	public String toString() {
		String s = BeanPropertyHelper.propertyName(propertyDescriptor) + "[]"; //$NON-NLS-1$
		if (elementType != null)
			s += "<" + BeanPropertyHelper.shortClassName(elementType) + ">"; //$NON-NLS-1$//$NON-NLS-2$
		return s;
	}
}
