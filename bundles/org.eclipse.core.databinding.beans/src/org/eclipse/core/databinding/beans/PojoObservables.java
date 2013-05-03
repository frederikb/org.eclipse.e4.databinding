/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matthew Hall - bugs 221704, 234686, 246625, 226289, 246782, 194734,
 *                    195222, 247997
 *******************************************************************************/

package org.eclipse.core.databinding.beans;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.masterdetail.MasterDetailObservables;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.internal.databinding.beans.BeanObservableListDecorator;
import org.eclipse.core.internal.databinding.beans.BeanObservableMapDecorator;
import org.eclipse.core.internal.databinding.beans.BeanObservableSetDecorator;
import org.eclipse.core.internal.databinding.beans.BeanObservableValueDecorator;
import org.eclipse.core.internal.databinding.beans.BeanPropertyHelper;
import org.eclipse.core.internal.databinding.beans.Util;

/**
 * A factory for creating observable objects for POJOs (plain old java objects)
 * that conform to idea of an object with getters and setters but does not
 * provide {@link PropertyChangeEvent property change events} on change. This
 * factory is identical to {@link BeansObservables} except for this fact.
 * 
 * @since 1.1
 * @deprecated use methods in PojoProperties
 */
final public class PojoObservables {

	/**
	 * Returns an observable value in the default realm tracking the current
	 * value of the named property of the given pojo.
	 * 
	 * @param pojo
	 *            the object
	 * @param propertyName
	 *            the name of the property. May be nested e.g. "parent.name"
	 * @return an observable value tracking the current value of the named
	 *         property of the given pojo
	 */
	public static <S> IObservableValue<?> observeValue(S pojo,
			String propertyName) {
		return observeValue(Realm.getDefault(), pojo, propertyName);
	}

	/**
	 * Returns an observable value in the given realm tracking the current value
	 * of the named property of the given pojo.
	 * 
	 * @param realm
	 *            the realm
	 * @param pojo
	 *            the object
	 * @param propertyName
	 *            the name of the property. May be nested e.g. "parent.name"
	 * @return an observable value tracking the current value of the named
	 *         property of the given pojo
	 */
	public static <S> IObservableValue<?> observeValue(Realm realm, S pojo,
			String propertyName) {
		return observeValue(realm, Util.getClass(pojo), pojo, propertyName);
	}

	private static <S, S2 extends S> IObservableValue<?> observeValue(
			Realm realm, Class<S2> pojoClass, S pojo, String propertyName) {
		return PojoProperties.<S2> value(pojoClass, propertyName).observe(
				realm, pojoClass.cast(pojo));
	}

	/**
	 * Returns an observable map in the given observable set's realm tracking
	 * the current values of the named property for the beans in the given set.
	 * Elements in the set which do not have the named property will have null
	 * values, and attempts to {@link IObservableMap#put(Object, Object) put}
	 * values to these elements will be ignored.
	 * 
	 * @param domain
	 *            the set of bean objects
	 * @param propertyName
	 *            the name of the property. May be nested e.g. "parent.name"
	 * @return an observable map tracking the current values of the named
	 *         property for the beans in the given domain set
	 * @since 1.2
	 */
	public static <S> IObservableMap<S, Object> observeMap(
			IObservableSet<S> domain, String propertyName) {
		return PojoProperties.value(propertyName).observeDetail(domain);
	}

	/**
	 * Returns an observable map in the given observable set's realm tracking
	 * the current values of the named property for the pojos in the given set.
	 * 
	 * @param domain
	 *            the set of pojo objects
	 * @param pojoClass
	 *            the common base type of pojo objects that may be in the set
	 * @param propertyName
	 *            the name of the property. May be nested e.g. "parent.name"
	 * @return an observable map tracking the current values of the named
	 *         property for the pojos in the given domain set
	 */
	public static <S> IObservableMap<S, ?> observeMap(IObservableSet<S> domain,
			Class<S> pojoClass, String propertyName) {
		return PojoProperties.<S> value(pojoClass, propertyName).observeDetail(
				domain);
	}

	/**
	 * Returns an array of observable maps in the given observable set's realm
	 * tracking the current values of the named properties for the beans in the
	 * given set. Elements in the set which do not have the named property will
	 * have null values, and attempts to
	 * {@link IObservableMap#put(Object, Object) put} values to these elements
	 * will be ignored.
	 * 
	 * @param domain
	 *            the set of objects
	 * @param propertyNames
	 *            the array of property names. May be nested e.g. "parent.name"
	 * @return an array of observable maps tracking the current values of the
	 *         named properties for the beans in the given domain set
	 * @deprecated use instead observeMaps( IObservableSet<S> domain,
	 *             List<String> propertyNames) because that method has better
	 *             type safety
	 * @since 1.2
	 */
	public static <S> IObservableMap<?, ?>[] observeMaps(
			IObservableSet<S> domain, String[] propertyNames) {
		IObservableMap<?, ?>[] result = new IObservableMap[propertyNames.length];
		for (int i = 0; i < propertyNames.length; i++) {
			result[i] = observeMap(domain, propertyNames[i]);
		}
		return result;
	}

	/**
	 * Returns an array of observable maps in the given observable set's realm
	 * tracking the current values of the named properties for the beans in the
	 * given set. Elements in the set which do not have the named property will
	 * have null values, and attempts to
	 * {@link IObservableMap#put(Object, Object) put} values to these elements
	 * will be ignored.
	 * 
	 * @param domain
	 *            the set of objects
	 * @param propertyNames
	 *            the array of property names. May be nested e.g. "parent.name"
	 * @return an array of observable maps tracking the current values of the
	 *         named properties for the beans in the given domain set
	 * @since 1.5
	 */
	public static <S> List<IObservableMap<S, ?>> observeMaps2(
			IObservableSet<S> domain, String[] propertyNames) {
		List<IObservableMap<S, ?>> result = new ArrayList<IObservableMap<S, ?>>(
				propertyNames.length);
		for (int i = 0; i < propertyNames.length; i++) {
			result.add(observeMap(domain, propertyNames[i]));
		}
		return result;
	}

	/**
	 * Returns an array of observable maps in the given observable set's realm
	 * tracking the current values of the named properties for the pojos in the
	 * given set.
	 * 
	 * @param domain
	 *            the set of objects
	 * @param pojoClass
	 *            the common base type of objects that may be in the set
	 * @param propertyNames
	 *            the array of property names. May be nested e.g. "parent.name"
	 * @return an array of observable maps tracking the current values of the
	 *         named properties for the pojos in the given domain set
	 * @deprecated use instead observeMaps( IObservableSet<S> domain, Class<S>
	 *             beanClass, List<String> propertyNames) because that method
	 *             has better type safety
	 */
	public static <S> IObservableMap<?, ?>[] observeMaps(
			IObservableSet<S> domain, Class<S> pojoClass, String[] propertyNames) {
		IObservableMap<?, ?>[] result = new IObservableMap[propertyNames.length];
		for (int i = 0; i < propertyNames.length; i++) {
			result[i] = observeMap(domain, pojoClass, propertyNames[i]);
		}
		return result;
	}

	/**
	 * Returns an array of observable maps in the given observable set's realm
	 * tracking the current values of the named properties for the pojos in the
	 * given set.
	 * 
	 * @param domain
	 *            the set of objects
	 * @param pojoClass
	 *            the common base type of objects that may be in the set
	 * @param propertyNames
	 *            the array of property names. May be nested e.g. "parent.name"
	 * @return an array of observable maps tracking the current values of the
	 *         named properties for the pojos in the given domain set
	 * @since 1.5
	 */
	public static <S> List<IObservableMap<S, ?>> observeMaps2(
			IObservableSet<S> domain, Class<S> pojoClass, String[] propertyNames) {
		List<IObservableMap<S, ?>> result = new ArrayList<IObservableMap<S, ?>>(
				propertyNames.length);
		for (int i = 0; i < propertyNames.length; i++) {
			result.add(observeMap(domain, pojoClass, propertyNames[i]));
		}
		return result;
	}

	/**
	 * Returns an observable map in the given realm tracking the map-typed named
	 * property of the given pojo object.
	 * 
	 * @param realm
	 *            the realm
	 * @param pojo
	 *            the pojo object
	 * @param propertyName
	 *            the name of the property
	 * @return an observable map tracking the map-typed named property of the
	 *         given pojo object
	 */
	public static <S> IObservableMap<?, ?> observeMap(Realm realm, S pojo,
			String propertyName) {
		return observeMap(realm, pojo, propertyName, null, null);
	}

	/**
	 * Returns an observable map in the given realm tracking the map-typed named
	 * property of the given pojo object.
	 * 
	 * @param realm
	 *            the realm
	 * @param pojo
	 *            the pojo object
	 * @param propertyName
	 *            the name of the property
	 * @param keyType
	 *            the element type of the observable map's key set, or
	 *            <code>null</code> if untyped
	 * @param valueType
	 *            the element type of the observable map's values collection, or
	 *            <code>null</code> if untyped
	 * @return an observable map tracking the map-typed named property of the
	 *         given pojo object
	 * @since 1.2
	 */
	public static <S, K, V> IObservableMap<K, V> observeMap(Realm realm,
			S pojo, String propertyName, Class<K> keyType, Class<V> valueType) {
		return PojoProperties.map(pojo.getClass(), propertyName, keyType,
				valueType).observe(realm, pojo);
	}

	/**
	 * Returns an observable map in the default realm tracking the map-typed
	 * named property of the given pojo object.
	 * 
	 * @param pojo
	 *            the pojo object
	 * @param propertyName
	 *            the name of the property
	 * @return an observable map tracking the map-typed named property of the
	 *         given pojo object
	 * @since 1.2
	 */
	public static <S> IObservableMap<?, ?> observeMap(S pojo,
			String propertyName) {
		return observeMap(Realm.getDefault(), pojo, propertyName, null, null);
	}

	/**
	 * Returns an observable map in the default realm tracking the map-typed
	 * named property of the given pojo object.
	 * 
	 * @param pojo
	 *            the pojo object
	 * @param propertyName
	 *            the name of the property
	 * @param keyType
	 *            the element type of the observable map's key set, or
	 *            <code>null</code> if untyped
	 * @param valueType
	 *            the element type of the observable map's values collection, or
	 *            <code>null</code> if untyped
	 * @return an observable map tracking the map-typed named property of the
	 *         given pojo object
	 * @since 1.2
	 */
	public static <S, K, V> IObservableMap<K, V> observeMap(S pojo,
			String propertyName, Class<K> keyType, Class<V> valueType) {
		return observeMap(Realm.getDefault(), pojo, propertyName, keyType,
				valueType);
	}

	/**
	 * Returns an observable list in the given realm tracking the
	 * collection-typed named property of the given pojo object. The returned
	 * list is mutable.
	 * 
	 * @param realm
	 *            the realm
	 * @param pojo
	 *            the object
	 * @param propertyName
	 *            the name of the collection-typed property
	 * @return an observable list tracking the collection-typed named property
	 *         of the given pojo object
	 * @see #observeList(Realm, Object, String, Class)
	 */
	public static <S> IObservableList<?> observeList(Realm realm, S pojo,
			String propertyName) {
		return observeList(realm, pojo, propertyName, null);
	}

	/**
	 * Returns an observable list in the default realm tracking the
	 * collection-typed named property of the given pojo object. The returned
	 * list is mutable.
	 * 
	 * @param pojo
	 *            the object
	 * @param propertyName
	 *            the name of the collection-typed property
	 * @return an observable list tracking the collection-typed named property
	 *         of the given pojo object
	 * @see #observeList(Realm, Object, String, Class)
	 * @since 1.2
	 */
	public static <S> IObservableList<?> observeList(S pojo, String propertyName) {
		return observeList(Realm.getDefault(), pojo, propertyName);
	}

	/**
	 * Returns an observable list in the given realm tracking the
	 * collection-typed named property of the given bean object. The returned
	 * list is mutable. When an item is added or removed the setter is invoked
	 * for the list on the parent bean to provide notification to other
	 * listeners via <code>PropertyChangeEvents</code>. This is done to provide
	 * the same behavior as is expected from arrays as specified in the bean
	 * spec in section 7.2.
	 * 
	 * @param realm
	 *            the realm
	 * @param pojo
	 *            the bean object
	 * @param propertyName
	 *            the name of the property
	 * @param elementType
	 *            type of the elements in the list. If <code>null</code> and the
	 *            property is an array the type will be inferred. If
	 *            <code>null</code> and the property type cannot be inferred
	 *            element type will be <code>null</code>.
	 * @return an observable list tracking the collection-typed named property
	 *         of the given bean object
	 */
	public static <S, E> IObservableList<E> observeList(Realm realm, S pojo,
			String propertyName, Class<E> elementType) {
		return PojoProperties.list(pojo.getClass(), propertyName, elementType)
				.observe(realm, pojo);
	}

	/**
	 * Returns an observable list in the default realm tracking the
	 * collection-typed named property of the given bean object. The returned
	 * list is mutable. When an item is added or removed the setter is invoked
	 * for the list on the parent bean to provide notification to other
	 * listeners via <code>PropertyChangeEvents</code>. This is done to provide
	 * the same behavior as is expected from arrays as specified in the bean
	 * spec in section 7.2.
	 * 
	 * @param pojo
	 *            the bean object
	 * @param propertyName
	 *            the name of the property
	 * @param elementType
	 *            type of the elements in the list. If <code>null</code> and the
	 *            property is an array the type will be inferred. If
	 *            <code>null</code> and the property type cannot be inferred
	 *            element type will be <code>null</code>.
	 * @return an observable list tracking the collection-typed named property
	 *         of the given bean object
	 * @since 1.2
	 */
	public static <S, E> IObservableList<E> observeList(S pojo,
			String propertyName, Class<E> elementType) {
		return observeList(Realm.getDefault(), pojo, propertyName, elementType);
	}

	/**
	 * Returns an observable set in the given realm tracking the
	 * collection-typed named property of the given pojo object.
	 * 
	 * @param realm
	 *            the realm
	 * @param pojo
	 *            the pojo object
	 * @param propertyName
	 *            the name of the property
	 * @return an observable set tracking the collection-typed named property of
	 *         the given pojo object
	 */
	public static <S> IObservableSet<?> observeSet(Realm realm, S pojo,
			String propertyName) {
		return observeSet(realm, pojo, propertyName, null);
	}

	/**
	 * Returns an observable set in the default realm tracking the
	 * collection-typed named property of the given pojo object.
	 * 
	 * @param pojo
	 *            the pojo object
	 * @param propertyName
	 *            the name of the property
	 * @return an observable set tracking the collection-typed named property of
	 *         the given pojo object
	 * @since 1.2
	 */
	public static <S> IObservableSet<?> observeSet(S pojo, String propertyName) {
		return observeSet(Realm.getDefault(), pojo, propertyName);
	}

	/**
	 * Returns an observable set in the given realm tracking the
	 * collection-typed named property of the given pojo object.
	 * 
	 * @param realm
	 *            the realm
	 * @param pojo
	 *            the pojo object
	 * @param propertyName
	 *            the name of the property
	 * @param elementType
	 *            type of the elements in the set. If <code>null</code> and the
	 *            property is an array the type will be inferred. If
	 *            <code>null</code> and the property type cannot be inferred
	 *            element type will be <code>null</code>.
	 * @return an observable set that tracks the current value of the named
	 *         property for given pojo object
	 */
	public static <S, E> IObservableSet<E> observeSet(Realm realm, S pojo,
			String propertyName, Class<E> elementType) {
		return PojoProperties.set(pojo.getClass(), propertyName, elementType)
				.observe(realm, pojo);
	}

	/**
	 * Returns an observable set in the default realm, tracking the
	 * collection-typed named property of the given pojo object.
	 * 
	 * @param pojo
	 *            the pojo object
	 * @param propertyName
	 *            the name of the property
	 * @param elementType
	 *            type of the elements in the set. If <code>null</code> and the
	 *            property is an array the type will be inferred. If
	 *            <code>null</code> and the property type cannot be inferred
	 *            element type will be <code>null</code>.
	 * @return an observable set that tracks the current value of the named
	 *         property for given pojo object
	 * @since 1.2
	 */
	public static <S, E> IObservableSet<E> observeSet(S pojo,
			String propertyName, Class<E> elementType) {
		return observeSet(Realm.getDefault(), pojo, propertyName, elementType);
	}

	/**
	 * Returns a factory for creating observable values in the given realm,
	 * tracking the given property of a particular pojo object
	 * 
	 * @param realm
	 *            the realm to use
	 * @param propertyName
	 *            the name of the property. May be nested e.g. "parent.name"
	 * @return an observable value factory
	 */
	public static IObservableFactory<Object, IObservableValue<Object>> valueFactory(
			final Realm realm, final String propertyName) {
		return PojoProperties.value(propertyName).valueFactory(realm);
	}

	/**
	 * Returns a factory for creating observable values in the current default
	 * realm, tracking the given property of a particular pojo object
	 * 
	 * @param propertyName
	 *            the name of the property. May be nested e.g. "parent.name"
	 * @return an observable value factory
	 * @since 1.2
	 */
	public static IObservableFactory<Object, IObservableValue<Object>> valueFactory(
			String propertyName) {
		return valueFactory(Realm.getDefault(), propertyName);
	}

	/**
	 * Returns a factory for creating observable lists in the given realm,
	 * tracking the given property of a particular pojo object
	 * 
	 * @param realm
	 *            the realm to use
	 * @param propertyName
	 *            the name of the property
	 * @param elementType
	 * @return an observable list factory
	 */
	public static <E> IObservableFactory<Object, IObservableList<E>> listFactory(
			final Realm realm, final String propertyName,
			final Class<E> elementType) {
		return PojoProperties.list(propertyName, elementType)
				.listFactory(realm);
	}

	/**
	 * Returns a factory for creating observable lists in the current default
	 * realm, tracking the given property of a particular pojo object
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param elementType
	 * @return an observable list factory
	 * @since 1.2
	 */
	public static <E> IObservableFactory<Object, IObservableList<E>> listFactory(
			String propertyName, Class<E> elementType) {
		return listFactory(Realm.getDefault(), propertyName, elementType);
	}

	/**
	 * Returns a factory for creating observable sets in the given realm,
	 * tracking the given property of a particular pojo object
	 * 
	 * @param realm
	 *            the realm to use
	 * @param propertyName
	 *            the name of the property
	 * @return an observable set factory
	 */
	public static IObservableFactory<Object, IObservableSet<Object>> setFactory(
			final Realm realm, final String propertyName) {
		return PojoProperties.set(propertyName).setFactory(realm);
	}

	/**
	 * Returns a factory for creating observable sets in the current default
	 * realm, tracking the given property of a particular pojo object
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @return an observable set factory
	 * @since 1.2
	 */
	public static IObservableFactory<Object, IObservableSet<Object>> setFactory(
			String propertyName) {
		return setFactory(Realm.getDefault(), propertyName);
	}

	/**
	 * Returns a factory for creating observable set in the given realm,
	 * tracking the given property of a particular pojo object
	 * 
	 * @param realm
	 *            the realm to use
	 * @param propertyName
	 *            the name of the property
	 * @param elementType
	 *            type of the elements in the set. If <code>null</code> and the
	 *            property is an array the type will be inferred. If
	 *            <code>null</code> and the property type cannot be inferred
	 *            element type will be <code>null</code>.
	 * @return an observable set factory for creating observable sets
	 */
	public static <E> IObservableFactory<Object, IObservableSet<E>> setFactory(
			final Realm realm, final String propertyName,
			final Class<E> elementType) {
		return PojoProperties.set(propertyName, elementType).setFactory(realm);
	}

	/**
	 * Returns a factory for creating observable set in the current default
	 * realm, tracking the given property of a particular pojo object
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param elementType
	 *            type of the elements in the set. If <code>null</code> and the
	 *            property is an array the type will be inferred. If
	 *            <code>null</code> and the property type cannot be inferred
	 *            element type will be <code>null</code>.
	 * @return an observable set factory for creating observable sets
	 * @since 1.2
	 */
	public static <E> IObservableFactory<Object, IObservableSet<E>> setFactory(
			String propertyName, Class<E> elementType) {
		return setFactory(Realm.getDefault(), propertyName, elementType);
	}

	/**
	 * Returns a factory for creating an observable map. The factory, when
	 * provided with a pojo object, will create an {@link IObservableMap} in the
	 * given realm that tracks the map-typed named property for the specified
	 * pojo.
	 * 
	 * @param realm
	 *            the realm assigned to observables created by the returned
	 *            factory.
	 * @param propertyName
	 *            the name of the property
	 * @return a factory for creating {@link IObservableMap} objects.
	 */
	public static IObservableFactory<Object, IObservableMap<Object, Object>> mapPropertyFactory(
			final Realm realm, final String propertyName) {
		return PojoProperties.map(propertyName).mapFactory(realm);
	}

	/**
	 * Returns a factory for creating an observable map. The factory, when
	 * provided with a pojo object, will create an {@link IObservableMap} in the
	 * current default realm that tracks the map-typed named property for the
	 * specified pojo.
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @return a factory for creating {@link IObservableMap} objects.
	 * @since 1.2
	 */
	public static IObservableFactory<Object, IObservableMap<Object, Object>> mapPropertyFactory(
			String propertyName) {
		return mapPropertyFactory(Realm.getDefault(), propertyName);
	}

	/**
	 * Helper method for
	 * <code>MasterDetailObservables.detailValue(master, valueFactory(realm,
	 propertyName), propertyType)</code>
	 * 
	 * @param realm
	 * @param master
	 * @param propertyName
	 *            the property name. May be nested e.g. "parent.name"
	 * @param propertyType
	 *            can be <code>null</code>
	 * @return an observable value that tracks the current value of the named
	 *         property for the current value of the master observable value
	 * 
	 * @see MasterDetailObservables
	 * @deprecated Use
	 *             {@link #observeDetailValue(IObservableValue, String, Class)}
	 *             instead
	 */
	public static <M, T> IObservableValue<T> observeDetailValue(Realm realm,
			IObservableValue<M> master, String propertyName,
			Class<T> propertyType) {
		BeansObservables.warnIfDifferentRealms(realm, master.getRealm());

		IObservableValue<T> value = MasterDetailObservables.detailValue(
				master,
				PojoProperties.value(propertyName, propertyType).valueFactory(
						realm), propertyType);
		return new BeanObservableValueDecorator<T>(value,
				BeanPropertyHelper.getValueTypePropertyDescriptor(master,
						propertyName));
	}

	/**
	 * Helper method for
	 * <code>MasterDetailObservables.detailValue(master, valueFactory(master.getRealm, propertyName), propertyType)</code>
	 * 
	 * @param master
	 * @param propertyName
	 *            the property name. May be nested e.g. "parent.name"
	 * @param propertyType
	 *            can be <code>null</code>
	 * @return an observable value that tracks the current value of the named
	 *         property for the current value of the master observable value
	 * 
	 * @see MasterDetailObservables
	 * @since 1.2
	 */
	public static <M, T> IObservableValue<T> observeDetailValue(
			IObservableValue<M> master, String propertyName,
			Class<T> propertyType) {
		return PojoProperties.value(master.getValueClass(), propertyName,
				propertyType).observeDetail(master);
	}

	/**
	 * Helper method for
	 * <code>MasterDetailObservables.detailList(master, listFactory(realm,
	 propertyName, propertyType), propertyType)</code>
	 * 
	 * @param realm
	 * @param master
	 * @param propertyName
	 * @param propertyType
	 *            can be <code>null</code>
	 * @return an observable list that tracks the named property for the current
	 *         value of the master observable value
	 * 
	 * @see MasterDetailObservables
	 * @deprecated Use
	 *             {@link #observeDetailList(IObservableValue, String, Class)}
	 *             instead
	 */
	public static <M, E> IObservableList<E> observeDetailList(Realm realm,
			IObservableValue<M> master, String propertyName,
			Class<E> propertyType) {
		BeansObservables.warnIfDifferentRealms(realm, master.getRealm());
		IObservableList<E> observableList = MasterDetailObservables.detailList(
				master, PojoProperties.list(propertyName, propertyType)
						.listFactory(realm), propertyType);
		return new BeanObservableListDecorator<E>(observableList,
				BeanPropertyHelper.getValueTypePropertyDescriptor(master,
						propertyName));
	}

	/**
	 * Helper method for
	 * <code>MasterDetailObservables.detailList(master, listFactory(master.getRealm(), propertyName, propertyType), propertyType)</code>
	 * 
	 * @param master
	 * @param propertyName
	 * @param propertyType
	 *            can be <code>null</code>
	 * @return an observable list that tracks the named property for the current
	 *         value of the master observable value
	 * 
	 * @see MasterDetailObservables
	 * @since 1.2
	 */
	public static <M, E> IObservableList<E> observeDetailList(
			IObservableValue<M> master, String propertyName,
			Class<E> propertyType) {
		return PojoProperties.<M, E> list(master.getValueClass(), propertyName)
				.observeDetail(master);
	}

	/**
	 * Helper method for
	 * <code>MasterDetailObservables.detailSet(master, setFactory(realm,
	 propertyName), propertyType)</code>
	 * 
	 * @param realm
	 * @param master
	 * @param propertyName
	 * @param propertyType
	 *            can be <code>null</code>
	 * @return an observable set that tracks the named property for the current
	 *         value of the master observable value
	 * 
	 * @see MasterDetailObservables
	 * @deprecated Use
	 *             {@link #observeDetailSet(IObservableValue, String, Class)}
	 *             instead.
	 */
	public static <M, E> IObservableSet<E> observeDetailSet(Realm realm,
			IObservableValue<M> master, String propertyName,
			Class<E> propertyType) {
		BeansObservables.warnIfDifferentRealms(realm, master.getRealm());

		IObservableSet<E> observableSet = MasterDetailObservables.detailSet(
				master, PojoProperties.set(propertyName, propertyType)
						.setFactory(realm), propertyType);
		return new BeanObservableSetDecorator<E>(observableSet,
				BeanPropertyHelper.getValueTypePropertyDescriptor(master,
						propertyName));
	}

	/**
	 * Helper method for
	 * <code>MasterDetailObservables.detailSet(master, setFactory(master.getRealm(), propertyName), propertyType)</code>
	 * 
	 * @param master
	 * @param propertyName
	 * @param propertyType
	 *            can be <code>null</code>
	 * @return an observable set that tracks the named property for the current
	 *         value of the master observable value
	 * 
	 * @see MasterDetailObservables
	 * @since 1.2
	 */
	public static <M, E> IObservableSet<E> observeDetailSet(
			IObservableValue<M> master, String propertyName,
			Class<E> propertyType) {
		return PojoProperties.set(master.getValueClass(), propertyName,
				propertyType).observeDetail(master);
	}

	/**
	 * Helper method for
	 * <code>MasterDetailObservables.detailMap(master, mapFactory(realm, propertyName))</code>
	 * 
	 * @param realm
	 * @param master
	 * @param propertyName
	 * @return an observable map that tracks the map-type named property for the
	 *         current value of the master observable value.
	 * @deprecated Use {@link #observeDetailMap(IObservableValue, String)}
	 *             instead
	 */
	public static <M> IObservableMap<Object, Object> observeDetailMap(
			Realm realm, IObservableValue<M> master, String propertyName) {
		BeansObservables.warnIfDifferentRealms(realm, master.getRealm());
		IObservableMap<Object, Object> observableMap = MasterDetailObservables
				.<M, Object, Object> detailMap(master,
						PojoProperties.<M> map(propertyName).mapFactory(realm));
		return new BeanObservableMapDecorator<Object, Object>(observableMap,
				BeanPropertyHelper.getValueTypePropertyDescriptor(master,
						propertyName));
	}

	/**
	 * Helper method for
	 * <code>MasterDetailObservables.detailMap(master, mapFactory(master.getRealm(), propertyName))</code>
	 * 
	 * @param master
	 * @param propertyName
	 * @return an observable map that tracks the map-type named property for the
	 *         current value of the master observable value.
	 * @since 1.2
	 */
	public static <M> IObservableMap<Object, Object> observeDetailMap(
			IObservableValue<M> master, String propertyName) {
		return PojoProperties.map(master.getValueClass(), propertyName)
				.observeDetail(master);
	}
}
