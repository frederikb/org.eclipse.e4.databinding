package org.eclipse.jface.tests.databinding;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.internal.databinding.internal.RandomAccessListIterator;

import junit.framework.TestCase;

public class RandomAccessListIteratorTest extends TestCase {

	private List emptyList = new LinkedList();
	private RandomAccessListIterator rali0 = new RandomAccessListIterator(emptyList);
	private List listWithOne;
	private RandomAccessListIterator rali1;
	private List list;
	private RandomAccessListIterator raliN;
	
	protected void setUp() throws Exception {
		listWithOne = new LinkedList();
		listWithOne.add("Uno");
		rali1 = new RandomAccessListIterator(listWithOne);
		list = new LinkedList();
		list.add("One");
		list.add("Two");
		list.add("three");
		list.add("four");
		raliN = new RandomAccessListIterator(list);
	}

	public void testGet_emptyList() {
		try {
			rali0.get(0);
			fail("Should have thrown exception");
		} catch (IndexOutOfBoundsException e) {
			// success
		}
	}

	public void testGet_withOne() {
		try {
			assertEquals("Uno", "Uno", rali1.get(0));
			
			rali1.get(1);
			fail("Should have thrown IndexOutOfBoundsException");
		} catch (Exception e) {
			// success
		}
	}
	
	public void testGet_alreadyAtItem() {
		assertEquals("one", "One", raliN.get(0));
	}
	
	public void testGet_moveForward() throws Exception {
		assertEquals("three", "three", raliN.get(2));
	}
	
	public void testGet_moveBackward() throws Exception {
		raliN.next();
		raliN.next();
		assertEquals("one", "One", raliN.get(0));
	}
	
	public void testGet_getLast() throws Exception {
		assertEquals("four", "four", raliN.get(3));
	}
	
	public void testGet_indexTooHigh() throws Exception {
		try {
			raliN.get(4);
			fail("Should have thrown exception");
		} catch (IndexOutOfBoundsException e) {
			// success
		}
	}

	public void testGet_indexTooLow() throws Exception {
		try {
			raliN.get(-100);
			fail("Should have thrown exception");
		} catch (IndexOutOfBoundsException e) {
			// success
		}
	}

}