/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     db4objects - Initial API and implementation
 */
package org.eclipse.jface.databinding.converterfunctions;

import java.math.BigDecimal;

import org.eclipse.jface.databinding.converterfunction.IConversionFunction;



/**
 * ConvertString2BigDecimal.
 */
public class ConvertString2BigDecimal implements IConversionFunction {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.converter.IConverter#convert(java.lang.Object)
	 */
	public Object convert(Object source) {
        try {
        	return new BigDecimal((String)source);
        } catch (Exception e) {
            throw new IllegalArgumentException("String2BigDecimal: " + e.getMessage() + ": " + source); //$NON-NLS-1$ //$NON-NLS-2$
        }
	}

}
