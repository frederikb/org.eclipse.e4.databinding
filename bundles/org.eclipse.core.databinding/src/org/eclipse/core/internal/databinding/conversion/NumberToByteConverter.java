/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.core.internal.databinding.conversion;

import com.ibm.icu.text.NumberFormat;

/**
 * Converts from a Number to a Byte.
 * <p>
 * Class is thread safe.
 * </p>
 * 
 * @since 1.0
 */
public class NumberToByteConverter extends NumberToNumberConverter {
	/**
	 * @param numberFormat
	 * @param fromType
	 * @param primitive
	 */
	public NumberToByteConverter(NumberFormat numberFormat, Class<?> fromType,
			boolean primitive) {
		super(numberFormat, fromType, (primitive) ? Byte.TYPE : Byte.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.internal.databinding.conversion.NumberToNumberConverter
	 * #doConvert(java.lang.Number)
	 */
	protected Number doConvert(Number number) {
		if (StringToNumberParser.inByteRange(number)) {
			return new Byte(number.byteValue());
		}

		return null;
	}
}
