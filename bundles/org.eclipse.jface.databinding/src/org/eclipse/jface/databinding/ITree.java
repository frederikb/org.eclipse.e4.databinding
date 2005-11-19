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
package org.eclipse.jface.databinding;


/**
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will remain
 * unchanged during the 3.2 release cycle. Please do not use this API without
 * consulting with the Platform/UI team.
 * </p>
 * 
 * @since 3.2
 * 
 */
public interface ITree {
		
	/**
	 * Returns the child elements of the given parent element.
	 *
	 * @param parentElement the parent element
	 * @return an array of child elements
	 */

	public Object[] getChildren(Object parentElement);
	
	/**
	 * @param parentElement
	 * @param children
	 */
	public void setChildren(Object parentElement, Object[] children);

	/**
	 * Returns the parent for the given element, or <code>null</code> 
	 * indicating that the parent can't be computed. 
	 *
	 * @param element the element
	 * @return the parent element, or <code>null</code> if it
	 *   has none or if the parent cannot be computed
	 */
	public Object getParent(Object element);

	/**
	 * Returns whether the given element has children.
	 *
	 * @param element the element
	 * @return <code>true</code> if the given element has children,
	 *  and <code>false</code> if it has no children
	 */
	public boolean hasChildren(Object element);

}