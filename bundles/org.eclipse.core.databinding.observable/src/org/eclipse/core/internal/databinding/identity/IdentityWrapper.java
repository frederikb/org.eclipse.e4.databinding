/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Daniel Kruegler - bug 137435
 *     Matthew Hall - bug 303847
 ******************************************************************************/

package org.eclipse.core.internal.databinding.identity;

/**
 * Used for wrapping objects that define their own implementations of equals()
 * and hashCode() when putting them in sets or hashmaps to ensure identity
 * comparison.
 * 
 * @param <T>
 * @since 1.0
 * 
 */
public class IdentityWrapper<T> {

	/**
	 * @param <T>
	 * @param o
	 *            the object to wrap
	 * @return an IdentityWrapper wrapping the specified object
	 */
	public static <T> IdentityWrapper<T> wrap(T o) {
		return o == null ? new IdentityWrapper<T>(null)
				: new IdentityWrapper<T>(o);
	}

	final T o;

	/**
	 * @param o
	 */
	private IdentityWrapper(T o) {
		this.o = o;
	}

	/**
	 * @return the unwrapped object
	 */
	public T unwrap() {
		return o;
	}

	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != IdentityWrapper.class) {
			return false;
		}
		return o == ((IdentityWrapper<?>) obj).o;
	}

	public int hashCode() {
		return System.identityHashCode(o);
	}
}
