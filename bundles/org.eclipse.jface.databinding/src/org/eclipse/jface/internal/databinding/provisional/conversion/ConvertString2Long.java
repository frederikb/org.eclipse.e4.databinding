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
package org.eclipse.jface.internal.databinding.provisional.conversion;




/**
 * ConvertString2Long.
 */
public class ConvertString2Long implements IConverter {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.converter.IConverter#convert(java.lang.Object)
	 */
	public Object convert(Object source) {
        try {
            return new Long(Long.parseLong((String) source));
        } catch (Exception e) {
            throw new IllegalArgumentException("String2Long: " + e.getMessage() + ": " + source); //$NON-NLS-1$ //$NON-NLS-2$
        }
	}

	public Object getFromType() {
		return String.class;
	}

	public Object getToType() {
		return Long.class;
	}

}