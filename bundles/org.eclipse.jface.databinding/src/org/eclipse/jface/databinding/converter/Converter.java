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
package org.eclipse.jface.databinding.converter;


/**
 * Abstract base class that can be used as a convenience for implementing custom
 * converters.
 * 
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will remain
 * unchanged during the 3.2 release cycle. Please do not use this API without
 * consulting with the Platform/UI team.
 * </p>
 * 
 * @since 3.2
 */
public abstract class Converter implements IConverter {

	private Class targetType;

	private Class modelType;

	/**
	 * @param targetType
	 * @param modelType
	 */
	public Converter(Class targetType, Class modelType) {
		this.targetType = targetType;
		this.modelType = modelType;
	}

	public Class getModelType() {
		return modelType;
	}

	public Class getTargetType() {
		return targetType;
	}

}