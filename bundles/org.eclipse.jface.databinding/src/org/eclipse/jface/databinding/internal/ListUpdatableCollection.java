/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.databinding.internal;

import java.util.List;

import org.eclipse.jface.databinding.IChangeEvent;
import org.eclipse.jface.databinding.IUpdatableCollection;
import org.eclipse.jface.databinding.Updatable;

/**
 * @since 3.2
 *
 */
public class ListUpdatableCollection extends Updatable implements
		IUpdatableCollection {

	private final List elements;

	private boolean updating;

	/**
	 * @param elements
	 * @param elementType
	 */
	public ListUpdatableCollection(List elements, Class elementType) {
		this.elements = elements;
	}

	public int getSize() {
		return elements.size();
	}

	public int addElement(Object value, int index) {
		updating = true;
		try {
			elements.add(index, value);
			fireChangeEvent(IChangeEvent.ADD, null, value, index);
			return index;
		} finally {
			updating = false;
		}
	}

	public void removeElement(int index) {
		updating = true;
		try {
			Object oldValue = elements.remove(index);
			fireChangeEvent(IChangeEvent.REMOVE, oldValue, null, index);
		} finally {
			updating = false;
		}
	}

	public void setElement(int index, Object value) {
		updating = true;
		try {
			Object oldValue = elements.set(index, value);
			fireChangeEvent(IChangeEvent.CHANGE, oldValue, value, index);
		} finally {
			updating = false;
		}
	}

	public Object getElement(int index) {
		return elements.get(index);
	}

	public Class getElementType() {
		return Object.class;
	}

}