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
package org.eclipse.core.databinding.validation;

import org.eclipse.core.internal.databinding.BindingMessages;


/**
 * DoubleValidator.  Verify data input for doubles
 *
 * @author djo
 */
public class String2DoublePrimitiveValidator implements IValidator {
    
	/* (non-Javadoc)
	 * @see org.eclipse.core.databinding.validator.IValidator#isPartiallyValid(java.lang.Object)
	 */
	public ValidationError isPartiallyValid(Object fragment) {
		if (((String)fragment).matches("\\-?[0-9]*\\.?[0-9]*([0-9]+[e|E]\\-?([0-9]+\\.)?[0-9]*)?")) //$NON-NLS-1$
            return null;

        return ValidationError.error(getHint());
	}
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ICellEditorValidator#isValid(java.lang.Object)
     */
    public ValidationError isValid(Object value) {
        try {
            Double.parseDouble((String)value);
            return null;
        } catch (Throwable t) {
            return ValidationError.error(getHint());
        }
    }

	private String getHint() {
		return BindingMessages.getString("Validate_Like") +  //$NON-NLS-1$
			BindingMessages.getString("Validate_Number_Examples") //$NON-NLS-1$
			+ Double.MIN_VALUE + 
			", " + Double.MAX_VALUE + "."; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
