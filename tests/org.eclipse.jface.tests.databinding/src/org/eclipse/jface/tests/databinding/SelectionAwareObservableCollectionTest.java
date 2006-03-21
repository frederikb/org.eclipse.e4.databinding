/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.tests.databinding;

import junit.framework.TestCase;

public class SelectionAwareObservableCollectionTest extends TestCase {

	/*
	 * FIXME: Rewrite to the new java.util.List API
	 */
	public void testGetElementsAndSetElements() {
//		TestSelectionObservableCollection collection = new TestSelectionObservableCollection();
//		String string1 = "string1";
//		String string2 = "string2";
//		String string3 = "string3";
//		String string4 = "string4";
//		String string5 = "string5";
//		
//		collection.addElement(string1, 0);
//		collection.addElement(string2, 1);
//		collection.setSelectedObject(string1);
//		
//		List elementsToUpdate = new ArrayList();
//		elementsToUpdate.add(string3);
//		elementsToUpdate.add(string4);
//		elementsToUpdate.add(string5);
//		
//		collection.setElements(elementsToUpdate);
//		
//		assertEquals("The collection was not updated to the right size.", 3, collection.getSize());
//		assertEquals("The wrong element was found in position 0", string3, collection.getElement(0));
//		assertEquals("The wrong element was found in position 1", string4, collection.getElement(1));
//		assertEquals("The wrong element was found in position 2", string5, collection.getElement(2));
//		assertNull("The selected object was not reset as expected", collection.getSelectedObject());
//		
//		assertNotSame("The all elements list instance should not be the one provided in the set.", elementsToUpdate, collection.getElements());
//		
//		List returnedList = collection.getElements();
//		assertEquals("The wrong element was found in position 0 of the returned list", string3, returnedList.get(0));
//		assertEquals("The wrong element was found in position 1 of the returned list", string4, returnedList.get(1));
//		assertEquals("The wrong element was found in position 2 of the returned list", string5, returnedList.get(2));
//		
//		collection.setSelectedObject(string3);
//		
//		elementsToUpdate = new ArrayList();
//		elementsToUpdate.add(string1);
//		elementsToUpdate.add(string2);
//		elementsToUpdate.add(string3);
//		
//		collection.setElements(elementsToUpdate);
//		
//		assertSame("The selected element was not retained.", string3, collection.getSelectedObject());
	}

//	private class TestSelectionObservableCollection extends SelectionAwareObservableCollection {
//		private List elements = new ArrayList();
//		private Object selected;
//		
//		public int addElement(Object value, int index) {
//			elements.add(index, value);
//			return index;
//		}
//
//		public Object computeElement(int index) {
//			return elements.get(index);
//		}
//
//		public Class getElementType() {
//			return Object.class;
//		}
//
//		public int computeSize() {
//			return elements.size();
//		}
//
//		public void removeElement(int index) {
//			elements.remove(index);
//		}
//
//		public void setElement(int index, Object value) {
//			elements.set(index, value);
//		}
//
//		public Object getSelectedObject() {
//			return selected;
//		}
//
//		public void setSelectedObject(Object object) {
//			this.selected = object;
//		}
//		
//		
//	}
}