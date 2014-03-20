/*******************************************************************************
 * Copyright (c) 2006, 2008 Brad Reynolds
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brad Reynolds - bug 158687
 *     Brad Reynolds - bug 164653
 *     Matthew Hall - bug 213145
 ******************************************************************************/

package org.eclipse.core.tests.databinding.observable.value;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.tests.databinding.TestBarrier;
import org.eclipse.jface.databinding.conformance.MutableObservableValueContractTest;
import org.eclipse.jface.databinding.conformance.delegate.AbstractObservableValueContractDelegate;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.tests.databinding.AbstractDefaultRealmTestCase;
import org.eclipse.swt.widgets.Display;

/**
 * @since 3.2
 */
public class WritableValueTest extends AbstractDefaultRealmTestCase {
	/**
	 * All constructors delegate to the 3 arg constructor.
	 * 
	 * @throws Exception
	 */
	public void testConstructor() throws Exception {
		WritableValue value = new WritableValue(SWTObservables.getRealm(Display
				.getDefault()));
		assertNull(value.getValue());
		assertNull(value.getValueType());
	}

	public void testWritableValueSimple() throws Throwable {
		final TestBarrier barrier = new TestBarrier();

		final WritableValue<String> value = new WritableValue<String>();
		assertNull(value.getValue());
		assertNull(value.getValueType());

		final String[] latestReceivedByListener = new String[1];

		new Thread(new Runnable() {

			public void run() {
				value.setValue("A");
				barrier.setStatus(1001);
				barrier.waitForStatus(1002);
				value.setValue("B");
				assertEquals("B", latestReceivedByListener[0]);
				barrier.setStatus(1003);
			}
		}).start();

		new Thread(new Runnable() {

			public void run() {
				barrier.waitForStatus(1001);
				assertEquals("A", value.getValue());
				value.addValueChangeListener(new IValueChangeListener<String>() {
					public void handleValueChange(ValueChangeEvent<String> event) {
						assertEquals("A", event.diff.getOldValue());
						assertEquals("B", event.diff.getNewValue());
						latestReceivedByListener[0] = event.diff.getNewValue();
					}
				});
				barrier.setStatus(1002);
			}
		}).start();

		barrier.waitForStatus(1003);
	}

	public void testWithValueType() throws Exception {
		Object elementType = String.class;
		WritableValue value = WritableValue.withValueType(elementType);
		assertNotNull(value);
		assertEquals(Realm.getDefault(), value.getRealm());
		assertEquals(elementType, value.getValueType());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(WritableValueTest.class.getName());
		suite.addTestSuite(WritableValueTest.class);
		suite.addTest(MutableObservableValueContractTest.suite(new Delegate()));
		return suite;
	}

	/* package */static class Delegate extends
			AbstractObservableValueContractDelegate {
		public IObservableValue createObservableValue(Realm realm) {
			return new WritableValue(realm, "", String.class);
		}

		public void change(IObservable observable) {
			IObservableValue observableValue = (IObservableValue) observable;
			observableValue.setValue(createValue(observableValue));
		}

		public Object getValueType(IObservableValue observable) {
			return String.class;
		}

		public Object createValue(IObservableValue observable) {
			return observable.getValue() + "a";
		}
	}
}
