/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bug 195222, 247997, 261843, 264307
 ******************************************************************************/

package org.eclipse.core.databinding.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.map.IMapProperty;
import org.eclipse.core.databinding.property.set.ISetProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.core.internal.databinding.beans.AnonymousPojoListProperty;
import org.eclipse.core.internal.databinding.beans.AnonymousPojoMapProperty;
import org.eclipse.core.internal.databinding.beans.AnonymousPojoSetProperty;
import org.eclipse.core.internal.databinding.beans.AnonymousPojoValueProperty;
import org.eclipse.core.internal.databinding.beans.BeanPropertyHelper;
import org.eclipse.core.internal.databinding.beans.PojoListProperty;
import org.eclipse.core.internal.databinding.beans.PojoListPropertyDecorator;
import org.eclipse.core.internal.databinding.beans.PojoMapProperty;
import org.eclipse.core.internal.databinding.beans.PojoMapPropertyDecorator;
import org.eclipse.core.internal.databinding.beans.PojoSetProperty;
import org.eclipse.core.internal.databinding.beans.PojoSetPropertyDecorator;
import org.eclipse.core.internal.databinding.beans.PojoValueProperty;
import org.eclipse.core.internal.databinding.beans.PojoValuePropertyDecorator;

/**
 * A factory for creating properties for POJOs (plain old java objects) that
 * conform to idea of an object with getters and setters but does not provide
 * {@link PropertyChangeEvent property change events} on change. This factory is
 * identical to {@link BeanProperties} except for this fact.
 * 
 * @since 1.2
 */
public class PojoProperties {
	/**
	 * Returns a value property for the given property name of an arbitrary bean
	 * class. Objects lacking the named property are treated the same as if the
	 * property always contains null.
	 * 
	 * @param propertyName
	 *            the property name. May be nested e.g. "parent.name"
	 * @return a value property for the given property name of an arbitrary bean
	 *         class.
	 */
	public static IBeanValueProperty<Object, Object> value(String propertyName) {
		String[] propertyNames = splitOffFirst(propertyName);

		if (propertyNames.length == 1) {
			return new PojoValuePropertyDecorator<Object, Object>(
					new AnonymousPojoValueProperty<Object, Object>(
							propertyNames[0], null), null);
		}

		IValueProperty<Object, Object> x = new AnonymousPojoValueProperty<Object, Object>(
				propertyNames[0], Object.class);

		IBeanValueProperty<Object, Object> remainder = value(propertyNames[1]);

		IValueProperty<Object, Object> y = x.value(remainder);

		return new PojoValuePropertyDecorator<Object, Object>(y, null);
	}

	/**
	 * Returns a value property for the given property name of an arbitrary bean
	 * class. Objects lacking the named property are treated the same as if the
	 * property always contains null.
	 * 
	 * @param propertyName
	 *            the property name. May be nested e.g. "parent.name"
	 * @param valueType
	 *            the value type of the returned value property
	 * @return a value property for the given property name of an arbitrary bean
	 *         class.
	 */
	public static <T> IBeanValueProperty<Object, T> value(String propertyName,
			Class<T> valueType) {
		String[] propertyNames = splitOffFirst(propertyName);

		if (propertyNames.length == 1) {
			return new PojoValuePropertyDecorator<Object, T>(
					new AnonymousPojoValueProperty<Object, T>(propertyNames[0],
							valueType), null);
		}

		IValueProperty<Object, Object> x = new AnonymousPojoValueProperty<Object, Object>(
				propertyNames[0], Object.class);

		IBeanValueProperty<Object, T> remainder = value(propertyNames[1],
				valueType);

		IValueProperty<Object, T> y = x.value(remainder);

		return new PojoValuePropertyDecorator<Object, T>(y, null);
	}

	/**
	 * Returns a value property for the given property name of the given bean
	 * class.
	 * <P>
	 * The property must be available in the class given by the
	 * <code>beanClass</code> parameter. For example suppose Foo is a class and
	 * Bar extends Foo. There are methods getMyProperty and setMyProperty in Bar
	 * but not in Foo. value(Foo.class, "myProperty") is called to get an
	 * IValueProperty instance. The IValueProperty is then used to get the
	 * property value from an object of type Bar. Null will always be returned.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyName
	 *            the property name. May be nested e.g. "parent.name"
	 * @return a value property for the given property name of the given bean
	 *         class.
	 */
	public static <S> IBeanValueProperty<S, ?> value(Class<S> beanClass,
			String propertyName) {
		String[] propertyNames = splitOffFirst(propertyName);

		if (beanClass == null) {
			// beanClass cannot be null.
			throw new IllegalArgumentException();
		}

		PropertyDescriptor propertyDescriptor = BeanPropertyHelper
				.getPropertyDescriptor(beanClass, propertyNames[0]);

		if (propertyNames.length == 1) {
			/*
			 * If a non-null valueType is provided by the caller then it must
			 * match the actual property type. If no valueType is provided by
			 * the caller then set it to the actual type from the property
			 * descriptor.
			 */
			Class<?> valueType = propertyDescriptor.getPropertyType();

			return valueUsingActualType(propertyDescriptor, valueType);
		}

		return valueGivenDescriptor(beanClass, propertyDescriptor,
				propertyDescriptor.getPropertyType(), propertyNames[1]);
	}

	private static <S, T> IBeanValueProperty<S, T> valueUsingActualType(
			PropertyDescriptor propertyDescriptor, Class<T> valueType) {
		IValueProperty<S, T> property = new PojoValueProperty<S, T>(
				propertyDescriptor, valueType);
		return new PojoValuePropertyDecorator<S, T>(property,
				propertyDescriptor);
	}

	/**
	 * Returns a value property for the given property name of the given bean
	 * class.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyName
	 *            the property name. May be nested e.g. "parent.name"
	 * @param valueType
	 *            the value type of the returned value property
	 * @return a value property for the given property name of the given bean
	 *         class.
	 */
	public static <S, T> IBeanValueProperty<S, T> value(
			Class<? extends S> beanClass, String propertyName,
			Class<T> valueType) {
		String[] propertyNames = splitOffFirst(propertyName);

		if (beanClass == null) {
			// beanClass cannot be null.
			throw new IllegalArgumentException();
		}
		if (valueType == null) {
			// valueType cannot be null.
			throw new IllegalArgumentException();
		}

		PropertyDescriptor propertyDescriptor = BeanPropertyHelper
				.getPropertyDescriptor(beanClass, propertyNames[0]);

		if (propertyNames.length == 1) {
			IValueProperty<S, T> property = new PojoValueProperty<S, T>(
					propertyDescriptor, valueType);
			return new PojoValuePropertyDecorator<S, T>(property,
					propertyDescriptor);
		}

		return valueGivenDescriptor(beanClass, propertyDescriptor,
				propertyDescriptor.getPropertyType(), propertyNames[1],
				valueType);
	}

	/**
	 * This is a private method used by the above to recursively chain
	 * IValueProperty objects when the bean property name has multiple parts
	 * ("parent.child").
	 * <P>
	 * This method is given the property descriptor for the getter method that
	 * gets one from the parent (class S) to the first level child (class I). It
	 * then makes a recursive call to get the IValueProperty that gets from the
	 * first level child to the final property (class T).
	 * 
	 * @param <S>
	 *            type of the parent object, being the object that contains the
	 *            property specified by propertyDescriptor
	 * @param <I>
	 *            type of the intermediate object, being the first level child
	 *            object and being the property type of the property specified
	 *            by propertyDescriptor
	 * @param sourceBeanClass
	 * @param propertyDescriptor
	 * @param childBeanClass
	 * @param propertyName
	 *            the property name. May be nested e.g. "parent.name"
	 * @return a value property that gets from S to T
	 */
	private static <S, I> IBeanValueProperty<S, ?> valueGivenDescriptor(
			Class<? extends S> sourceBeanClass,
			PropertyDescriptor propertyDescriptor, Class<I> childBeanClass,
			String propertyName) {

		IValueProperty<S, I> property = new PojoValueProperty<S, I>(
				propertyDescriptor, childBeanClass);
		IBeanValueProperty<S, I> decoratedProperty = new PojoValuePropertyDecorator<S, I>(
				property, propertyDescriptor);

		IBeanValueProperty<I, ?> remainder = value(childBeanClass, propertyName);

		return decoratedProperty.value(remainder);
	}

	/**
	 * This is a private method used by the above to recursively chain
	 * IValueProperty objects when the bean property name has multiple parts
	 * ("parent.child").
	 * <P>
	 * This method is given the property descriptor for the getter method that
	 * gets one from the parent (class S) to the first level child (class I). It
	 * then makes a recursive call to get the IValueProperty that gets from the
	 * first level child to the final property (class T).
	 * 
	 * @param <S>
	 *            type of the parent object, being the object that contains the
	 *            property specified by propertyDescriptor
	 * @param <I>
	 *            type of the intermediate object, being the first level child
	 *            object and being the property type of the property specified
	 *            by propertyDescriptor
	 * @param <T>
	 *            expected type of the final child given
	 * @param sourceBeanClass
	 * @param propertyDescriptor
	 * @param childBeanClass
	 * @param propertyName
	 *            the property name. May be nested e.g. "parent.name"
	 * @param valueType
	 *            the expected type of the final child property which may be
	 *            null if the caller does not know the type in advance
	 * @return a value property that gets from S to T
	 */
	private static <S, I, T> IBeanValueProperty<S, T> valueGivenDescriptor(
			Class<? extends S> sourceBeanClass,
			PropertyDescriptor propertyDescriptor, Class<I> childBeanClass,
			String propertyName, Class<T> valueType) {

		IValueProperty<S, I> property = new PojoValueProperty<S, I>(
				propertyDescriptor, childBeanClass);
		IBeanValueProperty<S, I> decoratedProperty = new PojoValuePropertyDecorator<S, I>(
				property, propertyDescriptor);

		IBeanValueProperty<I, T> remainder = value(childBeanClass,
				propertyName, valueType);

		return decoratedProperty.value(remainder);
	}

	/**
	 * Splits off the first part of a property name. For example, if
	 * "parent.child.child2" is passed in then { "parent", "child.child2" } is
	 * returned.
	 * 
	 * @param propertyName
	 *            the property name. May be nested e.g. "parent.name"
	 * @return a String array with either one element if there is no 'dot' in
	 *         the property name, or two elements split at the position of the
	 *         first 'dot'
	 */
	private static String[] splitOffFirst(String propertyName) {
		int index = propertyName.indexOf('.');
		if (index == -1) {
			return new String[] { propertyName };
		}
		return new String[] { propertyName.substring(0, index),
				propertyName.substring(index + 1) };
	}

	/**
	 * Returns a value property array for the given property names of the given
	 * bean class.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyNames
	 *            array of property names. May be nested e.g. "parent.name"
	 * @return a value property array for the given property names of the given
	 *         bean class.
	 * @deprecated use instead valuesAsList because that returns better typing
	 */
	public static <S> IBeanValueProperty<?, ?>[] values(Class<S> beanClass,
			String[] propertyNames) {
		IBeanValueProperty<?, ?>[] properties = new IBeanValueProperty[propertyNames.length];
		for (int i = 0; i < properties.length; i++)
			properties[i] = value(beanClass, propertyNames[i], null);
		return properties;
	}

	/**
	 * Returns a value property array for the given property names of the given
	 * bean class.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyNames
	 *            array of property names. May be nested e.g. "parent.name"
	 * @return a value property array for the given property names of the given
	 *         bean class.
	 * @since 1.5
	 */
	public static <S> List<IBeanValueProperty<S, Object>> valuesAsList(
			Class<S> beanClass, String[] propertyNames) {
		List<IBeanValueProperty<S, Object>> properties = new ArrayList<IBeanValueProperty<S, Object>>(
				propertyNames.length);
		for (int i = 0; i < propertyNames.length; i++)
			properties.add(value(beanClass, propertyNames[i], null));
		return properties;
	}

	/**
	 * Returns a value property array for the given property names of an
	 * arbitrary bean class.
	 * 
	 * @param propertyNames
	 *            array of property names. May be nested e.g. "parent.name"
	 * @return a value property array for the given property names of the given
	 *         bean class.
	 * @deprecated use instead valuesAsList because that returns better typing
	 */
	public static IBeanValueProperty<?, ?>[] values(String[] propertyNames) {
		return values(null, propertyNames);
	}

	/**
	 * Returns a value property array for the given property names of an
	 * arbitrary bean class.
	 * 
	 * @param propertyNames
	 *            array of property names. May be nested e.g. "parent.name"
	 * @return a value property array for the given property names of the given
	 *         bean class.
	 * @since 1.5
	 */
	public static <S> List<IBeanValueProperty<S, Object>> valuesAsList(
			String[] propertyNames) {
		return valuesAsList(null, propertyNames);
	}

	/**
	 * Returns a set property for the given property name of an arbitrary bean
	 * class. Objects lacking the named property are treated the same as if the
	 * property always contains an empty set.
	 * 
	 * @param propertyName
	 *            the property name
	 * @return a set property for the given property name of an arbitrary bean
	 *         class.
	 */
	public static IBeanSetProperty<Object, Object> set(String propertyName) {
		return set(null, propertyName, null);
	}

	/**
	 * Returns a set property for the given property name of an arbitrary bean
	 * class. Objects lacking the named property are treated the same as if the
	 * property always contains an empty set.
	 * 
	 * @param propertyName
	 *            the property name
	 * @param elementType
	 *            the element type of the returned set property
	 * @return a set property for the given property name of an arbitrary bean
	 *         class.
	 */
	public static <S, E> IBeanSetProperty<S, E> set(String propertyName,
			Class<E> elementType) {
		return set(null, propertyName, elementType);
	}

	/**
	 * Returns a set property for the given property name of the given bean
	 * class.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyName
	 *            the property name
	 * @return a set property for the given property name of the given bean
	 *         class.
	 */
	public static <S, E> IBeanSetProperty<S, E> set(Class<S> beanClass,
			String propertyName) {
		return set(beanClass, propertyName, null);
	}

	/**
	 * Returns a set property for the given property name of the given bean
	 * class.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyName
	 *            the property name
	 * @param elementType
	 *            the element type of the returned set property
	 * @return a set property for the given property name of the given bean
	 *         class.
	 */
	public static <S, E> IBeanSetProperty<S, E> set(
			Class<? extends S> beanClass, String propertyName,
			Class<E> elementType) {
		PropertyDescriptor propertyDescriptor;
		ISetProperty<S, E> property;
		if (beanClass == null) {
			propertyDescriptor = null;
			property = new AnonymousPojoSetProperty<S, E>(propertyName,
					elementType);
		} else {
			propertyDescriptor = BeanPropertyHelper.getPropertyDescriptor(
					beanClass, propertyName);
			property = new PojoSetProperty<S, E>(propertyDescriptor,
					elementType);
		}
		return new PojoSetPropertyDecorator<S, E>(property, propertyDescriptor);
	}

	/**
	 * Returns a list property for the given property name of an arbitrary bean
	 * class. Objects lacking the named property are treated the same as if the
	 * property always contains an empty list.
	 * 
	 * @param propertyName
	 *            the property name
	 * @return a list property for the given property name of an arbitrary bean
	 *         class.
	 */
	public static <E> IBeanListProperty<Object, E> list(String propertyName) {
		return list(null, propertyName, null);
	}

	/**
	 * Returns a list property for the given property name of an arbitrary bean
	 * class. Objects lacking the named property are treated the same as if the
	 * property always contains an empty list.
	 * 
	 * @param propertyName
	 *            the property name
	 * @param elementType
	 *            the element type of the returned list property
	 * @return a list property for the given property name of the given bean
	 *         class.
	 */
	public static <E> IBeanListProperty<Object, E> list(String propertyName,
			Class<E> elementType) {
		return list(null, propertyName, elementType);
	}

	/**
	 * Returns a list property for the given property name of the given bean
	 * class.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyName
	 *            the property name
	 * @return a list property for the given property name of the given bean
	 *         class.
	 */
	public static <S, E> IBeanListProperty<S, E> list(Class<S> beanClass,
			String propertyName) {
		return list(beanClass, propertyName, null);
	}

	/**
	 * Returns a list property for the given property name of the given bean
	 * class.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyName
	 *            the property name
	 * @param elementType
	 *            the element type of the returned list property
	 * @return a list property for the given property name of the given bean
	 *         class.
	 */
	public static <S, E> IBeanListProperty<S, E> list(
			Class<? extends S> beanClass, String propertyName,
			Class<E> elementType) {
		PropertyDescriptor propertyDescriptor;
		IListProperty<S, E> property;
		if (beanClass == null) {
			propertyDescriptor = null;
			property = new AnonymousPojoListProperty<S, E>(propertyName,
					elementType);
		} else {
			propertyDescriptor = BeanPropertyHelper.getPropertyDescriptor(
					beanClass, propertyName);
			property = new PojoListProperty<S, E>(propertyDescriptor,
					elementType);
		}
		return new PojoListPropertyDecorator<S, E>(property, propertyDescriptor);
	}

	/**
	 * Returns a map property for the given property name of an arbitrary bean
	 * class. Objects lacking the named property are treated the same as if the
	 * property always contains an empty map.
	 * 
	 * @param propertyName
	 *            the property name
	 * @return a map property for the given property name of an arbitrary bean
	 *         class.
	 */
	public static <S> IBeanMapProperty<S, Object, Object> map(
			String propertyName) {
		return map(null, propertyName, null, null);
	}

	/**
	 * Returns a map property for the given property name of an arbitrary bean
	 * class. Objects lacking the named property are treated the same as if the
	 * property always contains an empty map.
	 * 
	 * @param propertyName
	 *            the property name
	 * @param keyType
	 *            the key type for the returned map property
	 * @param valueType
	 *            the value type for the returned map property
	 * @return a map property for the given property name of an arbitrary bean
	 *         class.
	 */
	public static <S, K, V> IBeanMapProperty<S, K, V> map(String propertyName,
			Class<K> keyType, Class<V> valueType) {
		return map(null, propertyName, keyType, valueType);
	}

	/**
	 * Returns a map property for the given property name of the given bean
	 * class.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyName
	 *            the property name
	 * @return a map property for the given property name of the given bean
	 *         class.
	 */
	public static <S, K, V> IBeanMapProperty<S, K, V> map(Class<S> beanClass,
			String propertyName) {
		return map(beanClass, propertyName, null, null);
	}

	/**
	 * Returns a map property for the given property name of the given bean
	 * class.
	 * 
	 * @param beanClass
	 *            the bean class
	 * @param propertyName
	 *            the property name
	 * @param keyType
	 *            the key type of the returned map property
	 * @param valueType
	 *            the value type of the returned map property
	 * @return a map property for the given property name of the given bean
	 *         class.
	 */
	public static <S, K, V> IBeanMapProperty<S, K, V> map(
			Class<? extends S> beanClass, String propertyName,
			Class<K> keyType, Class<V> valueType) {
		PropertyDescriptor propertyDescriptor;
		IMapProperty<S, K, V> property;
		if (beanClass == null) {
			propertyDescriptor = null;
			property = new AnonymousPojoMapProperty<S, K, V>(propertyName,
					keyType, valueType);
		} else {
			propertyDescriptor = BeanPropertyHelper.getPropertyDescriptor(
					beanClass, propertyName);
			property = new PojoMapProperty<S, K, V>(propertyDescriptor,
					keyType, valueType);
		}
		return new PojoMapPropertyDecorator<S, K, V>(property,
				propertyDescriptor);
	}
}
