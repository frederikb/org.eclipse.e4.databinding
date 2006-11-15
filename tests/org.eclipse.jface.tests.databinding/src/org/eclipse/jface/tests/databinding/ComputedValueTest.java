/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brad Reynolds - bug 116920
 ******************************************************************************/

package org.eclipse.jface.tests.databinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Display;

/**
 * @since 1.0
 * 
 */
public class ComputedValueTest extends TestCase {
    public void testValueType() throws Exception {
        ComputedValue cv = new ComputedValue(Integer.TYPE) {
            protected Object calculate() {
                return new Integer(42);
            }
        };
        assertEquals("value type should be the type that was set", Integer.TYPE, cv.getValueType());

        cv = new ComputedValue() {
            protected Object calculate() {
                // TODO Auto-generated method stub
                return null;
            }
        };

        assertEquals("when type is not set it defaults to Object.cass", Object.class, cv.getValueType());
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        Realm.setDefault(SWTObservables.getRealm(Display.getDefault()));
    }

    public void test_getValue() throws Exception {
        ComputedValue cv = new ComputedValue() {
            protected Object calculate() {
                return new Integer(42);
            }
        };
        assertEquals("Calculated value should be 42", new Integer(42), cv.getValue());
    }

    public void testDependencyValueChange() throws Exception {
        final WritableValue value = new WritableValue(Integer.TYPE, new Integer(42));

        ComputedValue cv = new ComputedValue() {
            protected Object calculate() {
                return value.getValue();
            }
        };

        assertEquals("calculated value should have been that of the writable value", value.getValue(), cv.getValue());

        value.setValue(new Integer(44));

        assertEquals("calculated value should have been that of the writable value", value.getValue(), cv.getValue());
    }

    private static class WritableValueExt extends WritableValue {
        public WritableValueExt(Object valueType, Object initialValue) {
            super(valueType, initialValue);
        }

        public boolean hasListeners() {
            return super.hasListeners();
        }
    }

    public void testHookAndUnhookDependantObservables() throws Exception {
        final List values = new ArrayList();

        ComputedValue cv = new ComputedValue() {
            protected Object calculate() {
                int sum = 0;
                for (Iterator it = values.iterator(); it.hasNext();) {
                    WritableValue value = (WritableValue) it.next();
                    sum += ((Integer) value.getValue()).intValue();

                }

                return new Integer(sum);
            }
        };

        WritableValueExt value1 = new WritableValueExt(Integer.TYPE, new Integer(1));
        WritableValueExt value2 = new WritableValueExt(Integer.TYPE, new Integer(1));
        values.add(value1);
        values.add(value2);
        
        assertFalse(value1.hasListeners());
        assertFalse(value2.hasListeners());
        cv.getValue();
        assertTrue(value1.hasListeners());
        assertTrue(value2.hasListeners());
        
        //force the computed value to be stale
        value2.setValue(new Integer(2));
        //remove value2 from the values that are used to compute the value
        values.remove(value2);
        
        //force the value to be computed
        cv.getValue();
        assertEquals(new Integer(1), cv.getValue());
        assertTrue(value1.hasListeners());
        assertFalse("because value2 is not a part of the calculation the listeners should have been removed", value2.hasListeners());
    }
}