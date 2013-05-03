/*******************************************************************************
 * Copyright (c) 2008, 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bugs 195222, 264307, 265561
 ******************************************************************************/

package org.eclipse.core.internal.databinding.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.set.SimpleSetProperty;
import org.eclipse.core.databinding.util.Policy;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @param <S>
 * @param <E>
 * @since 3.3
 * 
 */
public class PojoSetProperty<S, E> extends SimpleSetProperty<S, E> {
	private final PropertyDescriptor propertyDescriptor;
	private final Class<E> elementType;

	/**
	 * @param propertyDescriptor
	 * @param elementType
	 */
	public PojoSetProperty(PropertyDescriptor propertyDescriptor,
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

	public Class<E> getElementType() {
		return elementType;
	}

	public Class<E> getElementClass() {
		return elementType;
	}

	protected Set<E> doGetSet(S source) {
		return (Set<E>) asSet(BeanPropertyHelper.readProperty(source,
				propertyDescriptor));
	}

	private Set<?> asSet(Object propertyValue) {
		if (propertyValue == null)
			return Collections.emptySet();
		if (propertyDescriptor.getPropertyType().isArray())
			return new HashSet<Object>(Arrays.asList((Object[]) propertyValue));
		return (Set<?>) propertyValue;
	}

	protected void doSetSet(S source, Set<E> set, SetDiff<E> diff) {
		doSetSet(source, set);
	}

	protected void doSetSet(S source, Set<E> set) {
		BeanPropertyHelper.writeProperty(source, propertyDescriptor,
				convertSetToBeanPropertyType(set));
	}

	private Object convertSetToBeanPropertyType(Set<E> set) {
		Object propertyValue = set;
		if (propertyDescriptor.getPropertyType().isArray()) {
			Class<?> componentType = propertyDescriptor.getPropertyType()
					.getComponentType();
			Object[] array = (Object[]) Array.newInstance(componentType,
					set.size());
			propertyValue = set.toArray(array);
		}
		return propertyValue;
	}

	public INativePropertyListener<S> adaptListener(
			ISimplePropertyListener<SetDiff<E>> listener) {
		return null;
	}

	public String toString() {
		String s = BeanPropertyHelper.propertyName(propertyDescriptor) + "{}"; //$NON-NLS-1$
		if (elementType != null)
			s += "<" + BeanPropertyHelper.shortClassName(elementType) + ">"; //$NON-NLS-1$//$NON-NLS-2$
		return s;
	}
}
