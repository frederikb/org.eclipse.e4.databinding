/*******************************************************************************
 * Copyright (c) 2006 Coconut Palm Software, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Coconut Palm Software, Inc. - initial API and implementation
 ******************************************************************************/

package org.eclipse.core.databinding.validation;

/**
 * A validator for domain model values.  If this validator is associated with
 * a binding (via an BindSpec), then it will be applied immediately before
 * a value is stored in the model object.  This validator operates in the
 * model's data type (after the conversion function object has been applied,
 * if applicable) and is responsible for applying range checks, special
 * formatting requirements, and so on.
 * <p>
 * 
 * FIXME: This is wrong.  See bug #128142.  Eventually this class will go away
 * and be replaced entirely by IValidator.
 * 
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will remain
 * unchanged during the 3.2 release cycle. Please do not use this API without
 * consulting with the Platform/UI team.
 * </p>
 * 
 * @since 1.0
 *
 */
public interface IDomainValidator {

	/**
	 * Determines if the given value is valid.
	 * 
	 * @param value
	 *            the value to validate
	 * @return the error message, or </code>null</code> if the value is valid.
	 */
	public ValidationError isValid(Object value);

}