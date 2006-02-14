/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.internal.databinding;

import java.util.List;

import org.eclipse.jface.internal.provisional.databinding.ChangeEvent;
import org.eclipse.jface.internal.provisional.databinding.UpdatableCollection;

/**
 * @since 3.2
 *
 */
public class ListUpdatableCollection extends UpdatableCollection {

	private final List elements;

	private boolean updating;

	/**
	 * @param elements
	 * @param elementType
	 */
	public ListUpdatableCollection(List elements, Class elementType) {
		this.elements = elements;
	}

	public int computeSize() {
		return elements.size();
	}

	public int addElement(Object value, int index) {
		updating = true;
		try {
			elements.add(index, value);
			fireChangeEvent(ChangeEvent.ADD, null, value, index);
			return index;
		} finally {
			updating = false;
		}
	}

	public void removeElement(int index) {
		updating = true;
		try {
			Object oldValue = elements.remove(index);
			fireChangeEvent(ChangeEvent.REMOVE, oldValue, null, index);
		} finally {
			updating = false;
		}
	}

	public void setElement(int index, Object value) {
		updating = true;
		try {
			Object oldValue = elements.set(index, value);
			fireChangeEvent(ChangeEvent.CHANGE, oldValue, value, index);
		} finally {
			updating = false;
		}
	}

	public Object computeElement(int index) {
		return elements.get(index);
	}

	public Class getElementType() {
		return Object.class;
	}

}
