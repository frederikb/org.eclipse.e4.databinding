/*******************************************************************************
 * Copyright (c) 2006 Brad Reynolds and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brad Reynolds - initial API and implementation
 *     Brad Reynolds - bug 171616
 ******************************************************************************/

package org.eclipse.jface.tests.internal.databinding.internal.beans;

import java.beans.PropertyDescriptor;

import junit.framework.TestCase;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.internal.databinding.internal.beans.JavaBeanObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Display;

/**
 * @since 3.2
 */
public class JavaBeanObservableValueTest extends TestCase {
	private Realm realm;
	private Bean bean;
	private JavaBeanObservableValue observableValue;
	private PropertyDescriptor propertyDescriptor;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		realm = SWTObservables.getRealm(Display.getDefault());		
		bean = new Bean();
		propertyDescriptor = new PropertyDescriptor("value", Bean.class);
		observableValue = new JavaBeanObservableValue(realm, bean, propertyDescriptor, String.class);
	}
	
    public void testSetValue() throws Exception {
        String value = "value";
        assertNull(observableValue.getValue());
        observableValue.setValue(value);
        assertEquals("value", value, observableValue.getValue());
    }
    
    public void testGetObserved() throws Exception {
    	assertEquals(bean, observableValue.getObserved());
	}
    
    public void testGetPropertyDescriptor() throws Exception {
    	assertEquals(propertyDescriptor, observableValue.getPropertyDescriptor());
	}
}
