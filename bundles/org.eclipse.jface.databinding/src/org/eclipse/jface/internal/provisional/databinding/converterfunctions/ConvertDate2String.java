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
package org.eclipse.jface.internal.provisional.databinding.converterfunctions;

import java.util.Date;

import org.eclipse.jface.internal.provisional.databinding.converterfunction.IConversionFunction;


/**
 * Convert a Java.util.Date to a String using the current locale.  Null date
 * values are converted to an empty string.
 * 
 * @since 3.2
 */
public class ConvertDate2String extends DateConversionSupport implements IConversionFunction {	
	public Object convert(Object source) {
		if (source != null) {
			return format((Date)source);
		}
		return ""; //$NON-NLS-1$
	}	
}
