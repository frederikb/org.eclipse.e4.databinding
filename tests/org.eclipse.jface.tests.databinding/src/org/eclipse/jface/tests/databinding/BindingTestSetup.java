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

package org.eclipse.jface.tests.databinding;

import java.util.Locale;

import junit.extensions.TestSetup;
import junit.framework.Test;

import org.eclipse.core.databinding.util.ILogger;
import org.eclipse.core.databinding.util.Policy;
import org.eclipse.core.runtime.IStatus;

/**
 * @since 3.2
 * 
 */
public class BindingTestSetup extends TestSetup {

	private Locale oldLocale;
	private ILogger oldLogger;
	private org.eclipse.jface.util.ILogger oldJFaceLogger;

	public BindingTestSetup(Test test) {
		super(test);
	}

	protected void setUp() throws Exception {
		super.setUp();
		oldLocale = Locale.getDefault();
		Locale.setDefault(Locale.US);
		oldLogger = Policy.getLog();
		Policy.setLog(new ILogger() {
			public void log(IStatus status) {
				/*
				 * We are not expecting anything in the log while we test.
				 * However there are some tests that result in a warning being
				 * logged. We didn't want to change the tests when generics were
				 * added because we want to be sure that legacy code continues
				 * to run, so we log warnings when 'not recommended' API usage
				 * is found.
				 */
				if (status.getSeverity() == IStatus.WARNING) {
					return;
				}

				if (status.getException() != null) {
					throw new RuntimeException(status.getException());
				}
				fail();
			}
		});
		oldJFaceLogger = org.eclipse.jface.util.Policy.getLog();
		org.eclipse.jface.util.Policy
				.setLog(new org.eclipse.jface.util.ILogger() {
					public void log(IStatus status) {
						// we are not expecting anything in the log while we
						// test.
						if (status.getException() != null) {
							throw new RuntimeException(status.getException());
						}
						fail();
					}
				});
	}

	protected void tearDown() throws Exception {
		Locale.setDefault(oldLocale);
		Policy.setLog(oldLogger);
		org.eclipse.jface.util.Policy.setLog(oldJFaceLogger);
		super.tearDown();
	}
}
