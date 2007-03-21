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

package org.eclipse.core.databinding;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.conversion.NumberToStringConverter;
import org.eclipse.core.databinding.conversion.StringToNumberConverter;
import org.eclipse.core.databinding.util.Policy;
import org.eclipse.core.internal.databinding.ClassLookupSupport;
import org.eclipse.core.internal.databinding.Pair;
import org.eclipse.core.internal.databinding.conversion.IdentityConverter;
import org.eclipse.core.internal.databinding.conversion.ObjectToStringConverter;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.ibm.icu.text.NumberFormat;

/**
 * @since 1.0
 * 
 */
/* package */class UpdateStrategy {

	private static final String BOOLEAN_TYPE = "java.lang.Boolean.TYPE"; //$NON-NLS-1$
	private static final String SHORT_TYPE = "java.lang.Short.TYPE"; //$NON-NLS-1$
	private static final String BYTE_TYPE = "java.lang.Byte.TYPE"; //$NON-NLS-1$
	private static final String DOUBLE_TYPE = "java.lang.Double.TYPE"; //$NON-NLS-1$
	private static final String FLOAT_TYPE = "java.lang.Float.TYPE"; //$NON-NLS-1$
	private static final String INTEGER_TYPE = "java.lang.Integer.TYPE"; //$NON-NLS-1$
	private static final String LONG_TYPE = "java.lang.Long.TYPE"; //$NON-NLS-1$
	private static Map converterMap;

	private static Class autoboxed(Class clazz) {
		if (clazz == Float.TYPE)
			return Float.class;
		else if (clazz == Double.TYPE)
			return Double.class;
		else if (clazz == Short.TYPE)
			return Short.class;
		else if (clazz == Integer.TYPE)
			return Integer.class;
		else if (clazz == Long.TYPE)
			return Long.class;
		else if (clazz == Byte.TYPE)
			return Byte.class;
		else if (clazz == Boolean.TYPE)
			return Boolean.class;
		return clazz;
	}

	final protected void checkAssignable(Object toType, Object fromType,
			String errorString) {
		Boolean assignableFromModelToModelConverter = isAssignableFromTo(
				fromType, toType);
		if (assignableFromModelToModelConverter != null
				&& !assignableFromModelToModelConverter.booleanValue()) {
			throw new BindingException(errorString
					+ " Expected: " + fromType + ", actual: " + toType); //$NON-NLS-1$//$NON-NLS-2$
		}
	}

	/**
	 * Tries to create a converter that can convert from values of type
	 * fromType. Returns <code>null</code> if no converter could be created.
	 * Either toType or modelDescription can be <code>null</code>, but not
	 * both.
	 * 
	 * @param fromType
	 * @param toType
	 * @return an IConverter, or <code>null</code> if unsuccessful
	 */
	protected IConverter createConverter(Object fromType, Object toType) {
		if (!(fromType instanceof Class) || !(toType instanceof Class)) {
			return new DefaultConverter(fromType, toType);
		}
		Class toClass = (Class) toType;
		if (toClass.isPrimitive()) {
			toClass = autoboxed(toClass);
		}
		Class fromClass = (Class) fromType;
		if (fromClass.isPrimitive()) {
			fromClass = autoboxed(fromClass);
		}
		if (!((Class) toType).isPrimitive()
				&& toClass.isAssignableFrom(fromClass)) {
			return new IdentityConverter(fromClass, toClass);
		}
		if (((Class) fromType).isPrimitive() && ((Class) toType).isPrimitive()
				&& fromType.equals(toType)) {
			return new IdentityConverter((Class) fromType, (Class) toType);
		}
		Map converterMap = getConverterMap();
		Class[] supertypeHierarchyFlattened = ClassLookupSupport
				.getTypeHierarchyFlattened(fromClass);
		for (int i = 0; i < supertypeHierarchyFlattened.length; i++) {
			Class currentFromClass = supertypeHierarchyFlattened[i];
			if (currentFromClass == toType) {
				// converting to toType is just a widening
				return new IdentityConverter(fromClass, toClass);
			}
			Pair key = new Pair(getKeyForClass(fromType, currentFromClass),
					getKeyForClass(toType, toClass));
			Object converterOrClassname = converterMap.get(key);
			if (converterOrClassname instanceof IConverter) {
				return (IConverter) converterOrClassname;
			} else if (converterOrClassname instanceof String) {
				String classname = (String) converterOrClassname;
				Class converterClass;
				try {
					converterClass = Class.forName(classname);
					IConverter result = (IConverter) converterClass
							.newInstance();
					converterMap.put(key, result);
					return result;
				} catch (Exception e) {
					Policy
							.getLog()
							.log(
									new Status(
											IStatus.ERROR,
											Policy.JFACE_DATABINDING,
											0,
											"Error while instantiating default converter", e)); //$NON-NLS-1$
				}
			}
		}
		// Since we found no converter yet, try a "downcast" converter;
		// the IdentityConverter will automatically check the actual types at
		// runtime.
		if (fromClass.isAssignableFrom(toClass)) {
			return new IdentityConverter(fromClass, toClass);
		}
		return new DefaultConverter(fromType, toType);
	}

	private static Map getConverterMap() {
		// using string-based lookup avoids loading of too many classes
		if (converterMap == null) {
			//NumberFormat to be shared across converters for the formatting of integer values
			NumberFormat integerFormat = NumberFormat.getIntegerInstance();
			//NumberFormat to be shared across converters for formatting non integer values
			NumberFormat numberFormat = NumberFormat.getNumberInstance();
			
			converterMap = new HashMap();
			converterMap
					.put(
							new Pair("java.util.Date", "java.lang.String"), "org.eclipse.core.internal.databinding.conversion.DateToStringConverter"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			converterMap
					.put(
							new Pair("java.lang.String", "java.lang.Boolean"), "org.eclipse.core.internal.databinding.conversion.StringToBooleanConverter"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			converterMap
					.put(
							new Pair("java.lang.String", "java.lang.Byte"), "org.eclipse.core.internal.databinding.conversion.StringToByteConverter"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			converterMap
					.put(
							new Pair("java.lang.String", "java.lang.Character"), "org.eclipse.core.internal.databinding.conversion.StringToCharacterConverter"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			converterMap
					.put(
							new Pair("java.lang.String", "java.util.Date"), "org.eclipse.core.internal.databinding.conversion.StringToDateConverter"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			converterMap
					.put(
							new Pair("java.lang.String", "java.lang.Short"), "org.eclipse.core.internal.databinding.conversion.StringToShortConverter"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			
			converterMap.put(new Pair("java.lang.String", "java.lang.Integer"), StringToNumberConverter.toInteger(integerFormat, false));  //$NON-NLS-1$//$NON-NLS-2$
			converterMap.put(new Pair("java.lang.String", "java.lang.Double"), StringToNumberConverter.toDouble(numberFormat, false));  //$NON-NLS-1$//$NON-NLS-2$
			converterMap.put(new Pair("java.lang.String", "java.lang.Long"), StringToNumberConverter.toLong(integerFormat, false));  //$NON-NLS-1$//$NON-NLS-2$
			converterMap.put(new Pair("java.lang.String", "java.lang.Float"), StringToNumberConverter.toFloat(numberFormat, false));  //$NON-NLS-1$//$NON-NLS-2$			
			converterMap.put(new Pair("java.lang.String", "java.math.BigInteger"), StringToNumberConverter.toBigInteger(integerFormat));  //$NON-NLS-1$//$NON-NLS-2$			
			converterMap.put(new Pair("java.lang.Integer", "java.lang.String"), NumberToStringConverter.fromInteger(integerFormat, false));  //$NON-NLS-1$//$NON-NLS-2$
			converterMap.put(new Pair("java.lang.Long", "java.lang.String"), NumberToStringConverter.fromLong(integerFormat, false));  //$NON-NLS-1$//$NON-NLS-2$
			converterMap.put(new Pair("java.lang.Double", "java.lang.String"), NumberToStringConverter.fromDouble(numberFormat, false));  //$NON-NLS-1$//$NON-NLS-2$
			converterMap.put(new Pair("java.lang.Float", "java.lang.String"), NumberToStringConverter.fromFloat(numberFormat, false));  //$NON-NLS-1$//$NON-NLS-2$
			converterMap.put(new Pair("java.math.BigInteger", "java.lang.String"), NumberToStringConverter.fromBigInteger(integerFormat));  //$NON-NLS-1$//$NON-NLS-2$
			
			converterMap
					.put(
							new Pair("java.lang.Object", "java.lang.String"), "org.eclipse.core.internal.databinding.conversion.ObjectToStringConverter"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

			// Integer.TYPE
			converterMap.put(new Pair("java.lang.String", INTEGER_TYPE), StringToNumberConverter.toInteger(integerFormat, true)); //$NON-NLS-1$
			converterMap
					.put(
							new Pair(INTEGER_TYPE, "java.lang.Integer"), new IdentityConverter(Integer.TYPE, Integer.class)); //$NON-NLS-1$
			converterMap
					.put(
							new Pair(INTEGER_TYPE, "java.lang.Object"), new IdentityConverter(Integer.TYPE, Object.class)); //$NON-NLS-1$
			converterMap.put(new Pair(INTEGER_TYPE, "java.lang.String"), NumberToStringConverter.fromInteger(integerFormat, true)); //$NON-NLS-1$

			// Byte.TYPE
			converterMap
					.put(
							new Pair("java.lang.String", BYTE_TYPE), "org.eclipse.core.internal.databinding.conversion.StringToBytePrimitiveConverter"); //$NON-NLS-1$ //$NON-NLS-2$
			converterMap
					.put(
							new Pair(BYTE_TYPE, "java.lang.Byte"), new IdentityConverter(Byte.TYPE, Byte.class)); //$NON-NLS-1$
			converterMap
					.put(
							new Pair(BYTE_TYPE, "java.lang.String"), new ObjectToStringConverter(Byte.TYPE)); //$NON-NLS-1$
			converterMap
					.put(
							new Pair(BYTE_TYPE, "java.lang.Object"), new IdentityConverter(Byte.TYPE, Object.class)); //$NON-NLS-1$

			// Double.TYPE
			converterMap.put(new Pair("java.lang.String", DOUBLE_TYPE), StringToNumberConverter.toDouble(numberFormat, true)); //$NON-NLS-1$
			converterMap.put(new Pair(DOUBLE_TYPE, "java.lang.String"), NumberToStringConverter.fromDouble(numberFormat, true)); //$NON-NLS-1$
			
			converterMap
					.put(
							new Pair(DOUBLE_TYPE, "java.lang.Double"), new IdentityConverter(Double.TYPE, Double.class)); //$NON-NLS-1$
			converterMap
					.put(
							new Pair(DOUBLE_TYPE, "java.lang.Object"), new IdentityConverter(Double.TYPE, Object.class)); //$NON-NLS-1$

			// Boolean.TYPE
			converterMap
					.put(
							new Pair("java.lang.String", BOOLEAN_TYPE), "org.eclipse.core.internal.databinding.conversion.StringToBooleanPrimitiveConverter"); //$NON-NLS-1$ //$NON-NLS-2$
			converterMap
					.put(
							new Pair(BOOLEAN_TYPE, "java.lang.Boolean"), new IdentityConverter(Boolean.TYPE, Boolean.class)); //$NON-NLS-1$
			converterMap
					.put(
							new Pair(BOOLEAN_TYPE, "java.lang.String"), new ObjectToStringConverter(Boolean.TYPE)); //$NON-NLS-1$
			converterMap
					.put(
							new Pair(BOOLEAN_TYPE, "java.lang.Object"), new IdentityConverter(Boolean.TYPE, Object.class)); //$NON-NLS-1$

			// Float.TYPE
			converterMap.put(new Pair("java.lang.String", FLOAT_TYPE), StringToNumberConverter.toFloat(numberFormat, true)); //$NON-NLS-1$
			converterMap.put(new Pair(FLOAT_TYPE, "java.lang.String"), NumberToStringConverter.fromFloat(numberFormat, true)); //$NON-NLS-1$
			converterMap
					.put(
							new Pair(FLOAT_TYPE, "java.lang.Float"), new IdentityConverter(Float.TYPE, Float.class)); //$NON-NLS-1$
			converterMap
					.put(
							new Pair(FLOAT_TYPE, "java.lang.Object"), new IdentityConverter(Float.TYPE, Object.class)); //$NON-NLS-1$		

			// Short.TYPE
			converterMap
					.put(
							new Pair("java.lang.String", SHORT_TYPE), "org.eclipse.core.internal.databinding.conversion.StringToShortPrimitiveConverter"); //$NON-NLS-1$ //$NON-NLS-2$
			converterMap
					.put(
							new Pair(SHORT_TYPE, "java.lang.Short"), new IdentityConverter(Short.TYPE, Short.class)); //$NON-NLS-1$
			converterMap
					.put(
							new Pair(SHORT_TYPE, "java.lang.String"), new ObjectToStringConverter(Short.TYPE)); //$NON-NLS-1$
			converterMap
					.put(
							new Pair(SHORT_TYPE, "java.lang.Object"), new IdentityConverter(Short.TYPE, Object.class)); //$NON-NLS-1$		

			// Long.TYPE
			converterMap.put(new Pair("java.lang.String", LONG_TYPE), StringToNumberConverter.toLong(integerFormat, true)); //$NON-NLS-1$
			converterMap.put(new Pair(LONG_TYPE, "java.lang.String"), NumberToStringConverter.fromLong(integerFormat, true)); //$NON-NLS-1$
			converterMap
					.put(
							new Pair(LONG_TYPE, "java.lang.Long"), new IdentityConverter(Long.TYPE, Long.class)); //$NON-NLS-1$
			converterMap
					.put(
							new Pair(LONG_TYPE, "java.lang.Object"), new IdentityConverter(Long.TYPE, Object.class)); //$NON-NLS-1$		

		}

		return converterMap;
	}

	private static String getKeyForClass(Object originalValue, Class filteredValue) {
		if (originalValue instanceof Class) {
			Class originalClass = (Class) originalValue;
			if (originalClass.equals(Integer.TYPE)) {
				return INTEGER_TYPE;
			} else if (originalClass.equals(Byte.TYPE)) {
				return BYTE_TYPE;
			} else if (originalClass.equals(Boolean.TYPE)) {
				return BOOLEAN_TYPE;
			} else if (originalClass.equals(Double.TYPE)) {
				return DOUBLE_TYPE;
			} else if (originalClass.equals(Float.TYPE)) {
				return FLOAT_TYPE;
			} else if (originalClass.equals(Long.TYPE)) {
				return LONG_TYPE;
			} else if (originalClass.equals(Short.TYPE)) {
				return SHORT_TYPE;
			}
		}
		return filteredValue.getName();
	}

	/**
	 * @param fromType
	 * @param toType
	 * @return whether fromType is assignable to toType
	 */
	protected Boolean isAssignableFromTo(Object fromType, Object toType) {
		if (fromType instanceof Class && toType instanceof Class) {
			Class toClass = (Class) toType;
			if (toClass.isPrimitive()) {
				toClass = autoboxed(toClass);
			}
			Class fromClass = (Class) fromType;
			if (fromClass.isPrimitive()) {
				fromClass = autoboxed(fromClass);
			}
			return toClass.isAssignableFrom(fromClass) ? Boolean.TRUE
					: Boolean.FALSE;
		}
		return null;
	}

	/*
	 * Default converter implementation, does not perform any conversion.
	 */
	protected static final class DefaultConverter implements IConverter {

		private final Object toType;

		private final Object fromType;

		/**
		 * @param fromType
		 * @param toType
		 */
		DefaultConverter(Object fromType, Object toType) {
			this.toType = toType;
			this.fromType = fromType;
		}

		public Object convert(Object fromObject) {
			return fromObject;
		}

		public Object getFromType() {
			return fromType;
		}

		public Object getToType() {
			return toType;
		}
	}

}
