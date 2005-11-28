/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.tests.databinding.scenarios;

import org.eclipse.jface.databinding.Property;
import org.eclipse.jface.tests.databinding.scenarios.model.Adventure;
import org.eclipse.jface.tests.databinding.scenarios.model.SampleData;
import org.eclipse.jface.tests.databinding.scenarios.model.Transportation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

/**
 * To run the tests in this class, right-click and select "Run As JUnit Plug-in
 * Test". This will also start an Eclipse instance. To clean up the launch
 * configuration, open up its "Main" tab and select "[No Application] - Headless
 * Mode" as the application to run.
 */

public class TextControlScenario extends ScenariosTestCase {

	private Adventure adventure;
	private Transportation transportation;	

	protected void setUp() throws Exception {
		super.setUp();
		// do any setup work here
		adventure = SampleData.WINTER_HOLIDAY;
		transportation = SampleData.EXECUTIVE_JET;
	}

	protected void tearDown() throws Exception {
		// do any teardown work here
		super.tearDown();
	}

	public void testScenario01() {
		// Bind the adventure "name" property to a text field
		// Change the UI and verify the model changes
		// Change the model and verify the UI changes
		final Text text = new Text(getComposite(), SWT.BORDER);
		getDbc().bind(text, new Property(adventure, "name"), null);
		assertEquals(adventure.getName(), text.getText());
		text.setText("England");
		text.notifyListeners(SWT.FocusOut, null);		
		assertEquals("England", adventure.getName());
		adventure.setName("France");
		assertEquals("France", text.getText());
		invokeNonUI(new Runnable(){
			public void run(){
				adventure.setName("Germany");			
			}
		});
		spinEventLoop(0);
		assertEquals("Germany",text.getText());
	}
	
	public void testScenario02() {
		// Bind the transportation "price" property to a text field
		// This is a Double.TYPE so we check that conversion and validation occurs
		// Change the UI and verify the model changes
		// Change the model and verify the UI changes
		Text text = new Text(getComposite(), SWT.BORDER);
		getDbc().bind(text, new Property(transportation, "price"), null);
		assertEquals(Double.toString(transportation.getPrice()), text.getText());
		text.setText("9876.54");
		text.notifyListeners(SWT.FocusOut, null);		
		assertEquals(9876.54, transportation.getPrice(),0);
		transportation.setPrice(1234.56);
		assertEquals("1234.56", text.getText());
	}	
}
