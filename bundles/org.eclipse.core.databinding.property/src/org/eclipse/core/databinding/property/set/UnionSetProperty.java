/*******************************************************************************
 * Copyright (c) 2009, 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 265727)
 ******************************************************************************/

package org.eclipse.core.databinding.property.set;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.core.databinding.observable.set.UnionSet;
import org.eclipse.core.internal.databinding.property.PropertyObservableUtil;

/**
 * A set property for observing the union of multiple set properties a combined
 * set.
 * 
 * @param <S>
 *            type of the source object
 * @param <E>
 *            type of the elements in the set
 * @since 1.2
 */
public class UnionSetProperty<S, E> extends SetProperty<S, E> {
	private final ISetProperty<S, E>[] properties;
	private final Object elementTypeAsObject;
	private final Class<E> elementType;

	/**
	 * @param properties
	 */
	public UnionSetProperty(ISetProperty<S, E>[] properties) {
		this(properties, null);
	}

	/**
	 * @param properties
	 * @param elementType
	 * @deprecated use the constuctor that takes a Class instead
	 */
	public UnionSetProperty(ISetProperty<S, E>[] properties, Object elementType) {
		this.properties = properties;
		this.elementTypeAsObject = elementType;
		this.elementType = null;
	}

	/**
	 * @param properties
	 * @param elementType
	 * @since 1.5
	 */
	public UnionSetProperty(ISetProperty<S, E>[] properties,
			Class<E> elementType) {
		this.properties = properties;
		this.elementTypeAsObject = elementType;
		this.elementType = elementType;
	}

	public Object getElementType() {
		return elementTypeAsObject;
	}

	/**
	 * @since 1.5
	 */
	public Class<E> getElementClass() {
		return elementType;
	}

	protected Set<E> doGetSet(S source) {
		Set<E> set = new HashSet<E>();
		for (int i = 0; i < properties.length; i++)
			set.addAll(properties[i].getSet(source));
		return set;
	}

	protected void doSetSet(S source, Set<E> set) {
		throw new UnsupportedOperationException(
				"UnionSetProperty is unmodifiable"); //$NON-NLS-1$
	}

	protected void doUpdateSet(S source, SetDiff<E> diff) {
		throw new UnsupportedOperationException(
				"UnionSetProperty is unmodifiable"); //$NON-NLS-1$
	}

	public IObservableSet<E> observe(Realm realm, S source) {
		Set<IObservableSet<? extends E>> sets = new HashSet<IObservableSet<? extends E>>(
				properties.length);
		for (ISetProperty<S, E> property : properties) {
			sets.add(property.observe(realm, source));
		}
		IObservableSet<E> unionSet = new UnionSet<E>(sets, elementType);

		for (IObservableSet<? extends E> set : sets) {
			PropertyObservableUtil.cascadeDispose(unionSet, set);
		}

		return unionSet;
	}
}
