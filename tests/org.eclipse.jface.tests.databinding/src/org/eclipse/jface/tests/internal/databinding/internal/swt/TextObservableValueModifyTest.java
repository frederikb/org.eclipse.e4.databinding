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

package org.eclipse.jface.tests.internal.databinding.internal.swt;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.conformance.delegate.AbstractObservableValueContractDelegate;
import org.eclipse.jface.databinding.conformance.swt.SWTMutableObservableValueContractTest;
import org.eclipse.jface.databinding.conformance.swt.SWTObservableValueContractTest;
import org.eclipse.jface.databinding.conformance.util.SuiteBuilder;
import org.eclipse.jface.internal.databinding.internal.swt.TextObservableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @since 3.2
 */
public class TextObservableValueModifyTest extends TestCase {
	public static Test suite() {
		Delegate delegate = new Delegate();
		return new SuiteBuilder().addObservableContractTest(
				SWTObservableValueContractTest.class, delegate)
				.addObservableContractTest(
						SWTMutableObservableValueContractTest.class, delegate)
				.build();
	}

	/* package */static class Delegate extends
			AbstractObservableValueContractDelegate {
		private Shell shell;

		private Text text;
		
		public void setUp() {
			shell = new Shell();
			text = new Text(shell, SWT.NONE);
		}

		public void tearDown() {
			shell.dispose();
		}

		public IObservableValue createObservableValue(Realm realm) {
			return new TextObservableValue(realm, text, SWT.Modify);
		}

		public Object getValueType(IObservableValue observable) {
			return String.class;
		}

		public void change(IObservable observable) {
			text.setFocus();
			
			IObservableValue observableValue = (IObservableValue) observable;
			text.setText((String) createValue(observableValue));
		}

		public Object createValue(IObservableValue observable) {
			String value = (String) observable.getValue();
			return value + "a";
		}
	}
}
