/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.provisional.observable.value;

import org.eclipse.jface.internal.databinding.provisional.observable.Diffs;
import org.eclipse.jface.internal.databinding.provisional.observable.IDiff;

/**
 * @since 1.0
 * 
 */
public abstract class ValueDiff implements IDiff {

	/**
	 * Creates a value diff.
	 */
	public ValueDiff() {
	}

	/**
	 * @return the old value
	 */
	public abstract Object getOldValue();

	/**
	 * @return the new value
	 */
	public abstract Object getNewValue();

	public boolean equals(Object obj) {
		if (obj instanceof ValueDiff) {
			ValueDiff val = (ValueDiff) obj;

			return Diffs.equals(val.getNewValue(), getNewValue())
					&& Diffs.equals(val.getOldValue(), getOldValue());

		}
		return false;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer
			.append(getClass().getName())
			.append("{oldValue [") //$NON-NLS-1$
			.append(getOldValue() != null ? getOldValue().toString() : "null") //$NON-NLS-1$
			.append("], newValue [") //$NON-NLS-1$
			.append(getNewValue() != null ? getNewValue().toString() : "null") //$NON-NLS-1$
			.append("]}"); //$NON-NLS-1$
		
		return buffer.toString();
	}
}
