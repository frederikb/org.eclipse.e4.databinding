/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.provisional.factories;

import junit.framework.TestCase;

import org.eclipse.jface.examples.databinding.ModelObject;
import org.eclipse.jface.internal.databinding.provisional.DataBindingContext;
import org.eclipse.jface.internal.databinding.provisional.beans.BeanObservableFactory;
import org.eclipse.jface.internal.databinding.provisional.description.Property;
import org.eclipse.jface.internal.databinding.provisional.swt.SWTObservableFactory;
import org.eclipse.jface.internal.databinding.provisional.viewers.ViewersBindingFactory;
import org.eclipse.jface.internal.databinding.provisional.viewers.ViewersObservableFactory;
import org.eclipse.swt.widgets.Widget;

public class DefaultBindSupportFactoryBytePrimitiveTest extends TestCase {
	private DataBindingContext ctx;
	private TestDataObject dataObject;

	public void setUp() {
		ctx = getDatabindingContext();
		dataObject = new TestDataObject();
		dataObject.setStringVal("0");
		dataObject.setBytePrimitiveVal((byte) 0);
		dataObject.setByteVal(new Byte((byte) 0));		
	}
	
	public void testStringToBytePrimitiveConverter() {
		ctx.bind(new Property(dataObject, "stringVal"), new Property(dataObject, "bytePrimitiveVal"), null);
		
		dataObject.setBytePrimitiveVal((byte)110);
		assertEquals("byte value does not match", 110, dataObject.getBytePrimitiveVal(), .001);
		assertEquals("String value does not match", "110", dataObject.getStringVal());
		assertNull("No errors should be found.", ctx.getValidationError().getValue());
		
		dataObject.setStringVal("70");
		assertEquals("byte value does not match", 70, dataObject.getBytePrimitiveVal(), .001);
		assertEquals("String value does not match", "70", dataObject.getStringVal());
		assertNull("No errors should be found.", ctx.getValidationError().getValue());		

		dataObject.setStringVal("");
		assertEquals("byte value does not match", 70, dataObject.getBytePrimitiveVal(), .001);
		assertEquals("String value does not match", "", dataObject.getStringVal());
		assertNotNull("Errors should be found.", ctx.getValidationError().getValue());		

		dataObject.setStringVal(null);
		assertEquals("byte value does not match", 70, dataObject.getBytePrimitiveVal(), .001);
		assertNull("String value does not match", dataObject.getStringVal());
		assertNotNull("Errors should be found.", ctx.getValidationError().getValue());			
	}

	public void testByteToBytePrimitiveConverter() {
		ctx.bind(new Property(dataObject, "byteVal"), new Property(dataObject, "bytePrimitiveVal"), null);
		
		dataObject.setBytePrimitiveVal((byte)110);
		assertEquals("byte value does not match", 110, dataObject.getBytePrimitiveVal(), .001);
		assertEquals("Byte value does not match", new Byte((byte)110), dataObject.getByteVal());
		assertNull("No errors should be found.", ctx.getValidationError().getValue());
		
		dataObject.setByteVal(new Byte((byte)70));
		assertEquals("byte value does not match", 70, dataObject.getBytePrimitiveVal(), .001);
		assertEquals("Byte value does not match", new Byte((byte)70), dataObject.getByteVal());
		assertNull("No errors should be found.", ctx.getValidationError().getValue());		

		dataObject.setByteVal(null);
		assertEquals("byte value does not match", 70, dataObject.getBytePrimitiveVal(), .001);
		assertNull("Byte value does not match", dataObject.getByteVal());
		assertNotNull("Errors should be found.", ctx.getValidationError().getValue());		
	}
	
	public void testObjectToBytePrimitiveConverter() {
		ctx.bind(new Property(dataObject, "objectVal"), new Property(dataObject, "bytePrimitiveVal"), null);
		
		dataObject.setBytePrimitiveVal((byte)110);
		assertEquals("byte value does not match", 110, dataObject.getBytePrimitiveVal(), .001);
		assertEquals("Object value does not match", new Byte((byte)110), dataObject.getObjectVal());
		assertNull("No errors should be found.", ctx.getValidationError().getValue());
		
		dataObject.setObjectVal(new Byte((byte)70));
		assertEquals("byte value does not match", 70, dataObject.getBytePrimitiveVal(), .001);
		assertEquals("Object value does not match", new Byte((byte)70), dataObject.getObjectVal());
		assertNull("No errors should be found.", ctx.getValidationError().getValue());		

		dataObject.setObjectVal(null);
		assertEquals("byte value does not match", 70, dataObject.getBytePrimitiveVal());
		assertNull("Object value does not match", dataObject.getObjectVal());
		assertNotNull("Errors should be found.", ctx.getValidationError().getValue());		

		Object object = new Object();
		dataObject.setObjectVal(object);
		assertEquals("byte value does not match", 70, dataObject.getBytePrimitiveVal());
		assertSame("Object value does not match", object, dataObject.getObjectVal());
		assertNotNull("Errors should be found.", ctx.getValidationError().getValue());			
	}
	
	public class TestDataObject extends ModelObject {
		private byte bytePrimitiveValue;
		private String stringVal;
		private Byte byteVal;
		private Object objectVal;
		
		public Byte getByteVal() {
			return byteVal;
		}
		public void setByteVal(Byte byteVal) {
			Object oldVal = this.byteVal;
			this.byteVal = byteVal;
			firePropertyChange("byteVal", oldVal, this.byteVal);
		}

		public byte getBytePrimitiveVal() {
			return bytePrimitiveValue;
		}
		public void setBytePrimitiveVal(byte bytePrimitiveValue) {
			byte oldVal = this.bytePrimitiveValue;
			this.bytePrimitiveValue = bytePrimitiveValue;
			firePropertyChange("bytePrimitiveVal", new Byte(oldVal), new Byte(this.bytePrimitiveValue));
		}
		
		public String getStringVal() {
			return stringVal;
		}
		public void setStringVal(String stringVal) {
			Object oldVal = this.stringVal;
			this.stringVal = stringVal;
			firePropertyChange("stringVal", oldVal, this.stringVal);
		}

		public Object getObjectVal() {
			return objectVal;
		}
		public void setObjectVal(Object objectVal) {
			Object oldVal = this.objectVal;
			this.objectVal = objectVal;
			firePropertyChange("objectVal", oldVal, this.objectVal);
		}
	}
	
	/**
	 * @param aControl
	 * @return
	 */
	public static DataBindingContext getDatabindingContext() {
		final DataBindingContext context = new DataBindingContext();
		context.addObservableFactory(new DefaultObservableFactory(context));
		context.addObservableFactory(new BeanObservableFactory(context, null, new Class[]{Widget.class}));
		context.addObservableFactory(new NestedObservableFactory(context));
		context.addObservableFactory(new SWTObservableFactory());
		context.addObservableFactory(new ViewersObservableFactory());
		context.addBindingFactory(new DefaultBindingFactory());
		context.addBindingFactory(new ViewersBindingFactory());
		context.addBindSupportFactory(new DefaultBindSupportFactory());
		return context;
	}	
}