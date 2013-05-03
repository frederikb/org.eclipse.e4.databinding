package org.eclipse.jface.databinding.util;

import java.lang.reflect.Array;

/**
 * @since 1.7
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

}
