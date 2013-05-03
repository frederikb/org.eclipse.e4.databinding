/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 195222)
 ******************************************************************************/

package org.eclipse.core.databinding.beans;

import org.eclipse.core.databinding.property.list.IListProperty;

/**
 * An {@link IListProperty} extension interface with convenience methods for
 * creating nested bean properties.
 * 
 * @param <S>
 *            type of the source object
 * @param <E>
 *            type of the elements in the list
 * @since 1.2
 */
public interface IBeanListProperty<S, E> extends IBeanProperty,
		IListProperty<S, E> {
	/**
	 * Returns a master-detail combination of this property and the specified
	 * value property.
	 * 
	 * @param propertyName
	 *            the value property to observe. May be nested e.g.
	 *            "parent.name"
	 * @return a nested combination of this property and the specified value
	 *         property.
	 * @see #values(IBeanValueProperty)
	 */
	public <V> IBeanListProperty<S, V> values(String propertyName);

	/**
	 * Returns a master-detail combination of this property and the specified
	 * value property.
	 * 
	 * @param propertyName
	 *            the value property to observe. May be nested e.g.
	 *            "parent.name"
	 * @param valueType
	 *            the value type of the named property
	 * @return a master-detail combination of this property and the specified
	 *         value property.
	 * @see #values(IBeanValueProperty)
	 */
	public <V> IBeanListProperty<S, V> values(String propertyName,
			Class<V> valueType);

	/**
	 * Returns a master-detail combination of this property and the specified
	 * value property. The returned property will observe the specified value
	 * property for all elements observed by this list property.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * // Observes the list-typed &quot;children&quot; property of a Person object,
	 * // where the elements are Person objects
	 * IBeanListProperty children = BeanProperties.list(Person.class, &quot;children&quot;,
	 * 		Person.class);
	 * // Observes the string-typed &quot;name&quot; property of a Person object
	 * IBeanValueProperty name = BeanProperties.value(Person.class, &quot;name&quot;);
	 * // Observes the names of children of a Person object.
	 * IBeanListProperty childrenNames = children.values(name);
	 * </pre>
	 * 
	 * @param property
	 *            the detail property to observe
	 * @return a master-detail combination of this property and the specified
	 *         value property.
	 */
	public <V> IBeanListProperty<S, V> values(IBeanValueProperty<E, V> property);
}
