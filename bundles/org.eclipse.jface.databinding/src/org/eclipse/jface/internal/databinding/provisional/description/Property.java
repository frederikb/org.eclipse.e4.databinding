/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.internal.databinding.provisional.description;

/**
 * A standard description type representing a property of an object. Description
 * objects can be passed to <code>DataBindingContext.bind()</code> or
 * <code>DataBindingContext.createUpdatable()</code>. They are passed to
 * <code>IUpdatableFactory.createUpdatable()</code> to create an updatable
 * object. It is up to the IUpdatableFactory objects to interpret this
 * description.
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will remain
 * unchanged during the 3.2 release cycle. Please do not use this API without
 * consulting with the Platform/UI team.
 * </p>
 * 
 * @since 1.0
 * 
 */
public class Property {

	private final Object object;

	private final Object propertyID;

	private final Class propertyType;

	private final Boolean isCollectionProperty;

	/**
	 * Creates a new property description from the given object and property
	 * identifier.
	 * 
	 * @param object
	 * @param propertyID
	 */
	public Property(Object object, Object propertyID) {
		this(object, propertyID, null, null);
	}

	/**
	 * Creates a new property description from the given object, property
	 * identifier, property type, and information whether the property is a
	 * collection property.
	 * 
	 * @param object
	 *            the object that has the given property
	 * @param propertyID
	 *            the property identifier
	 * @param propertyType
	 *            the property type, or <code>null</code> if unknown
	 * @param isCollectionProperty
	 *            <code>Boolean.TRUE</code> if the property is a collection
	 *            property, <code>Boolean.FALSE</code> if it is a simple
	 *            property, or <code>null</code> if unknown
	 */
	public Property(Object object, Object propertyID,
			Class propertyType, Boolean isCollectionProperty) {
		this.object = object;
		this.propertyID = propertyID;
		this.propertyType = propertyType;
		this.isCollectionProperty = isCollectionProperty;
	}

	/**
	 * Returns the object of this property description.
	 * 
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * Returns the property identifier of this property description.
	 * 
	 * @return the property identifier
	 */
	public Object getPropertyID() {
		return propertyID;
	}

	/**
	 * Returns the property type of this property description, or
	 * <code>null</code> if unknown.
	 * 
	 * @return the property identifier, or <code>null</code>
	 */
	public Class getPropertyType() {
		return propertyType;
	}

	/**
	 * Returns whether the property is a collection property, or
	 * <code>null</code> if unknown. If the property is a collection property,
	 * getPropertyType() returns the element type of the collection.
	 * 
	 * @return <code>Boolean.TRUE</code> if the property is a collection
	 *         property, <code>null</code> if it is a simple property, or
	 *         <code>null</code> if unknown
	 */
	public Boolean isCollectionProperty() {
		return isCollectionProperty;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Property(" + object.getClass().getName() + ", " + propertyID.toString() + ", ...)"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
