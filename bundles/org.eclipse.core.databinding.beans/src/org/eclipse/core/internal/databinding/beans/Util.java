/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.core.internal.databinding.beans;

import java.lang.reflect.Array;

/**
 * @since 3.3
 * 
 */
public class Util {

	/**
	 * Checks whether the two objects are <code>null</code> -- allowing for
	 * <code>null</code>.
	 * 
	 * @param left
	 *            The left object to compare; may be <code>null</code>.
	 * @param right
	 *            The right object to compare; may be <code>null</code>.
	 * @return <code>true</code> if the two objects are equivalent;
	 *         <code>false</code> otherwise.
	 */
	public static final boolean equals(final Object left, final Object right) {
		return left == null ? right == null : ((right != null) && left
				.equals(right));
	}

	/**
	 * This method carries out an operation that the Java 5 compiler claims is
	 * an unchecked cast but if fact it should not be. No cast should be
	 * necessary in the following code because the getComponentType method
	 * should return a correctly typed result.
	 * 
	 * @param a
	 * @return the class of the elements of the given array
	 */
	@SuppressWarnings("unchecked")
	public static <E> Class<E> getComponentType(E[] a) {
		return (Class<E>) a.getClass().getComponentType();
	}

	/**
	 * This method carries out an operation that the Java 5 compiler claims is
	 * an unchecked cast but if fact it should not be. No cast should be
	 * necessary in the following code because the newInstance method should
	 * return a correctly typed result.
	 * 
	 * @param componentType
	 * @param size
	 * @return an array of the given element type and size
	 */
	@SuppressWarnings("unchecked")
	public static <E> E[] createArrayInstance(Class<E> componentType, int size) {
		return (E[]) Array.newInstance(componentType, size);
	}

	/**
	 * @param object
	 * @return the class of the object, correctly typed
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<? extends T> getClass(T object) {
		return (Class<? extends T>) object.getClass();
	}

	/**
	 * @param valueType
	 * @return a class which will not be a primitive
	 */
	public static Class<?> convertToObjectClass(Class<?> valueType) {
		if (valueType.isPrimitive()) {
			if (valueType == double.class) {
				valueType = Double.class;
			} else if (valueType == long.class) {
				valueType = Long.class;
			} else if (valueType == boolean.class) {
				valueType = Boolean.class;
			} else if (valueType == float.class) {
				valueType = Float.class;
			} else if (valueType == int.class) {
				valueType = Integer.class;
			} else if (valueType == char.class) {
				valueType = Character.class;
			} else if (valueType == short.class) {
				valueType = Short.class;
			}
		}

		return valueType;
	}
}
