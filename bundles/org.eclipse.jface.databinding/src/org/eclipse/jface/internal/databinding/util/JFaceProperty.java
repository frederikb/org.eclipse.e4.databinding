/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matthew Hall - bug 278314
 *******************************************************************************/
package org.eclipse.jface.internal.databinding.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.NativePropertyListener;
import org.eclipse.core.databinding.property.value.SimpleValueProperty;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * Class that supports the use of {@link IObservableValue} with objects that
 * follow standard bean method naming conventions but notify an
 * {@link IPropertyChangeListener} when the property changes.
 * 
 * @param <S>
 * @param <T>
 */
public class JFaceProperty<S, T> extends SimpleValueProperty<S, T> {

	private Class<T> returnType;
	private Method setterMethod;
	private Method getterMethod;
	private final String property;
	private Method removePropertyListenerMethod;
	private Method addPropertyListenerMethod;

	private static String getSetterName(String fieldName) {
		return "set" + toMethodSuffix(fieldName); //$NON-NLS-1$
	}

	private static String getGetterName(String fieldName) {
		return "get" + toMethodSuffix(fieldName); //$NON-NLS-1$
	}

	private static String getBooleanGetterName(String fieldName) {
		return "is" + toMethodSuffix(fieldName); //$NON-NLS-1$
	}

	private static String toMethodSuffix(String fieldName) {
		if (Character.isLowerCase(fieldName.charAt(0))) {
			return Character.toUpperCase(fieldName.charAt(0))
					+ fieldName.substring(1);
		}
		return fieldName;
	}

	class Listener extends NativePropertyListener<S, ValueDiff<T>> implements
			IPropertyChangeListener {
		public Listener(ISimplePropertyListener<ValueDiff<T>> listener) {
			super(JFaceProperty.this, listener);
		}

		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(JFaceProperty.this.property)) {
				fireChange(event.getSource(), null);
			}
		}

		protected void doAddTo(Object model) {
			try {
				addPropertyListenerMethod.invoke(model, new Object[] { this });
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage());
			}
		}

		protected void doRemoveFrom(Object model) {
			try {
				removePropertyListenerMethod.invoke(model,
						new Object[] { this });
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage());
			}
		}
	}

	/**
	 * @param fieldName
	 * @param property
	 * @param clazz
	 */
	public JFaceProperty(String fieldName, String property, Class<S> clazz) {
		this.property = property;
		// Create all the necessary method ahead of time to ensure they are
		// available
		try {
			try {
				String getterName = getGetterName(fieldName);
				getterMethod = clazz.getMethod(getterName, new Class[] {});
			} catch (NoSuchMethodException e) {
				String getterName = getBooleanGetterName(fieldName);
				getterMethod = clazz.getMethod(getterName, new Class[] {});
			}
			returnType = (Class<T>) getterMethod.getReturnType();
			setterMethod = clazz.getMethod(getSetterName(fieldName),
					new Class[] { returnType });
			addPropertyListenerMethod = clazz
					.getMethod(
							"addPropertyChangeListener", new Class[] { IPropertyChangeListener.class }); //$NON-NLS-1$
			removePropertyListenerMethod = clazz
					.getMethod(
							"removePropertyChangeListener", new Class[] { IPropertyChangeListener.class }); //$NON-NLS-1$
		} catch (SecurityException e) {
			throw new IllegalArgumentException();
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException();
		}
	}

	public INativePropertyListener<S> adaptListener(
			ISimplePropertyListener<ValueDiff<T>> listener) {
		return new Listener(listener);
	}

	protected T doGetValue(S model) {
		try {
			return (T) getterMethod.invoke(model, new Object[] {});
		} catch (InvocationTargetException e) {
			throw new IllegalStateException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e.getMessage());
		}
	}

	protected void doSetValue(Object model, Object value) {
		try {
			setterMethod.invoke(model, new Object[] { value });
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e.getMessage());
		} catch (InvocationTargetException e) {
			throw new IllegalStateException(e.getMessage());
		}
	}

	public Object getValueType() {
		return returnType;
	}

}
