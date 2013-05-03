/*******************************************************************************
 * Copyright (c) 2008, 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 247997)
 *     Matthew Hall - bug 264306
 ******************************************************************************/

package org.eclipse.core.databinding.property.set;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;

/**
 * @param <S>
 *            type of the source object
 * @param <E>
 *            type of the elements in the set
 * @since 1.2
 * 
 */
public abstract class DelegatingSetProperty<S, E> extends SetProperty<S, E> {
	private final Class<E> elementType;
	private final Object elementTypeAsObject;
	private final ISetProperty<S, E> nullProperty = new NullSetProperty();

	protected DelegatingSetProperty() {
		this(null);
	}

	/**
	 * 
	 * @param elementType
	 * @deprecated use the constructor which takes Class as a parameter. This is
	 *             safer because code in this plug-in fails anyway if a Class is
	 *             not passed.
	 * 
	 */
	protected DelegatingSetProperty(Object elementType) {
		this.elementType = null;
		this.elementTypeAsObject = elementType;
	}

	/**
	 * @param elementType
	 * @since 1.5
	 */
	protected DelegatingSetProperty(Class<E> elementType) {
		this.elementType = elementType;
		this.elementTypeAsObject = elementType;
	}

	/**
	 * Returns the property to delegate to for the specified source object.
	 * Repeated calls to this method with the same source object returns the
	 * same delegate instance.
	 * 
	 * @param source
	 *            the property source (may be null)
	 * @return the property to delegate to for the specified source object.
	 */
	protected final ISetProperty<S, E> getDelegate(S source) {
		if (source == null)
			return nullProperty;
		ISetProperty<S, E> delegate = doGetDelegate(source);
		if (delegate == null)
			delegate = nullProperty;
		return delegate;
	}

	/**
	 * Returns the property to delegate to for the specified source object.
	 * Implementers must ensure that repeated calls to this method with the same
	 * source object returns the same delegate instance.
	 * 
	 * @param source
	 *            the property source
	 * @return the property to delegate to for the specified source object.
	 */
	protected abstract ISetProperty<S, E> doGetDelegate(S source);

	/**
	 * @deprecated use getElementClass instead
	 */
	public Object getElementType() {
		return elementTypeAsObject;
	}

	/**
	 * @return the class of the elements in the set
	 * @since 1.5
	 */
	public Class<E> getElementClass() {
		return elementType;
	}

	protected Set<E> doGetSet(S source) {
		return getDelegate(source).getSet(source);
	}

	protected void doSetSet(S source, Set<E> set) {
		getDelegate(source).setSet(source, set);
	}

	protected void doUpdateSet(S source, SetDiff<E> diff) {
		getDelegate(source).updateSet(source, diff);
	}

	public IObservableSet<E> observe(S source) {
		return getDelegate(source).observe(source);
	}

	public IObservableSet<E> observe(Realm realm, S source) {
		return getDelegate(source).observe(realm, source);
	}

	private class NullSetProperty extends SimpleSetProperty<S, E> {
		public Object getElementType() {
			return elementTypeAsObject;
		}

		public Class<E> getElementClass() {
			return elementType;
		}

		protected Set<E> doGetSet(S source) {
			return Collections.emptySet();
		}

		protected void doSetSet(S source, Set<E> set, SetDiff<E> diff) {
		}

		protected void doSetSet(S source, Set<E> set) {
		}

		protected void doUpdateSet(S source, SetDiff<E> diff) {
		}

		public INativePropertyListener<S> adaptListener(
				ISimplePropertyListener<SetDiff<E>> listener) {
			return null;
		}
	}
}
