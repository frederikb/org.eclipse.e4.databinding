/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brad Reynolds - bug 164653, 147515
 *     Matthew Hall - bug 213145
 *     Nigel Westbury - bug 208434, 389394
 *******************************************************************************/

package org.eclipse.core.tests.databinding.observable.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.conformance.MutableObservableListContractTest;
import org.eclipse.jface.databinding.conformance.delegate.AbstractObservableCollectionContractDelegate;
import org.eclipse.jface.databinding.conformance.util.CurrentRealm;
import org.eclipse.jface.databinding.conformance.util.RealmTester;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Display;

/**
 * @since 3.2
 */
public class WritableListTest extends TestCase {
	protected void tearDown() throws Exception {
		RealmTester.setDefault(null);
	}

	public void testSetRealmChecks() throws Exception {
		RealmTester.exerciseCurrent(new Runnable() {
			public void run() {
				WritableList list = new WritableList();
				list.add("");
				list.set(0, "");
			}
		});
	}

	public void testAddRealmChecks() throws Exception {
		RealmTester.exerciseCurrent(new Runnable() {
			public void run() {
				WritableList list = new WritableList();
				list.add("");
			}
		});
	}

	public void testAddByIndexRealmChecks() throws Exception {
		RealmTester.exerciseCurrent(new Runnable() {
			public void run() {
				WritableList list = new WritableList();
				list.add(0, "");
			}
		});
	}

	public void testAddAllRealmChecks() throws Exception {
		RealmTester.exerciseCurrent(new Runnable() {
			public void run() {
				WritableList list = new WritableList();
				list.addAll(Collections.EMPTY_LIST);
			}
		});
	}

	public void testAddAllByIndexRealmChecks() throws Exception {
		RealmTester.exerciseCurrent(new Runnable() {
			public void run() {
				WritableList list = new WritableList();
				list.addAll(0, Collections.EMPTY_LIST);
			}
		});
	}

	public void testRemoveRealmChecks() throws Exception {
		RealmTester.setDefault(new CurrentRealm(true));
		final WritableList list = new WritableList();
		list.add("");
		list.add("");

		RealmTester.exerciseCurrent(new Runnable() {
			public void run() {
				list.remove("");
			}
		});
		RealmTester.setDefault(null);
	}

	public void testRemoveByIndexRealmChecks() throws Exception {
		RealmTester.setDefault(new CurrentRealm(true));
		final WritableList list = new WritableList();
		list.add("");
		list.add("");

		RealmTester.exerciseCurrent(new Runnable() {
			public void run() {
				list.remove(list.size() - 1);
			}
		});

		RealmTester.setDefault(null);
	}

	public void testRemoveAllRealmChecks() throws Exception {
		RealmTester.exerciseCurrent(new Runnable() {
			public void run() {
				WritableList list = new WritableList();
				list.removeAll(Collections.EMPTY_LIST);
			}
		});
	}

	public void testRetainAllRealmChecks() throws Exception {
		RealmTester.exerciseCurrent(new Runnable() {
			public void run() {
				WritableList list = new WritableList();
				list.retainAll(Collections.EMPTY_LIST);
			}
		});
	}

	public void testClearRealmChecks() throws Exception {
		RealmTester.exerciseCurrent(new Runnable() {
			public void run() {
				WritableList list = new WritableList();
				list.clear();
			}
		});
	}

	public void testNullElementType() throws Exception {
		RealmTester.setDefault(SWTObservables.getRealm(Display.getDefault()));
		WritableList writableList = new WritableList();
		assertNull(writableList.getElementType());

		writableList = new WritableList(Realm.getDefault());
		assertNull(writableList.getElementType());
	}

	public void testWithElementType() throws Exception {
		RealmTester.setDefault(SWTObservables.getRealm(Display.getDefault()));

		Object elementType = String.class;
		WritableList list = WritableList.withElementType(elementType);
		assertNotNull(list);
		assertEquals(Realm.getDefault(), list.getRealm());
		assertEquals(elementType, list.getElementType());
	}

	public void testListConstructorsDoNotCopy_1() {
		RealmTester.setDefault(new CurrentRealm(true));
		List list = new ArrayList(Arrays.asList(new Object[] { "a", "b", "c" }));
		WritableList wlist = new WritableList(list, Object.class);
		wlist.remove(1);
		assertEquals(2, list.size());
		list.add("d");
		assertEquals(3, wlist.size());
	}

	public void testListConstructorsDoNotCopy_2() {
		List list = new ArrayList(Arrays.asList(new Object[] { "a", "b", "c" }));
		WritableList wlist = new WritableList(new CurrentRealm(true), list,
				Object.class);
		wlist.remove(1);
		assertEquals(2, list.size());
		list.add("d");
		assertEquals(3, wlist.size());
	}

	public void testCollectionConstructorsCopy_1() {
		RealmTester.setDefault(new CurrentRealm(true));
		List list = new ArrayList(Arrays.asList(new Object[] { "a", "b", "c" }));
		WritableList wlist = new WritableList((Collection) list, Object.class);
		wlist.remove(1);
		assertEquals(3, list.size());
		list.add("d");
		assertEquals(2, wlist.size());
	}

	public void testCollectionConstructorsCopy_2() {
		List list = new ArrayList(Arrays.asList(new Object[] { "a", "b", "c" }));
		WritableList wlist = new WritableList(new CurrentRealm(true),
				(Collection) list, Object.class);
		wlist.remove(1);
		assertEquals(3, list.size());
		list.add("d");
		assertEquals(2, wlist.size());
	}

	public void testRemoveAll() {
		RealmTester.setDefault(new CurrentRealm(true));
		List<String> list = new ArrayList<String>(Arrays.asList(new String[] {
				"a", "b", "c" }));
		WritableList<String> wlist = new WritableList<String>(list,
				String.class);

		final boolean[] flags = new boolean[wlist.size()];

		wlist.addListChangeListener(new IListChangeListener<String>() {
			public void handleListChange(ListChangeEvent<String> event) {
				for (ListDiffEntry<? extends String> diffEntry : event.diff
						.getDifferencesAsList()) {
					if (flags[diffEntry.getPosition()]) {
						throw new RuntimeException("duplicate index in diff");
					}
					flags[diffEntry.getPosition()] = true;
				}

			}
		});

		List<String> removalList = new ArrayList<String>(
				Arrays.asList(new String[] { "a", "c" }));

		wlist.removeAll(removalList);

		assertEquals(true, flags[0]);
		assertEquals(false, flags[1]);
		assertEquals(true, flags[2]);
		assertEquals(1, wlist.size());
	}

	public void testIteratorRemoval() {
		RealmTester.setDefault(new CurrentRealm(true));
		List<String> list = new ArrayList<String>(Arrays.asList(new String[] {
				"a", "b", "c" }));
		WritableList<String> wlist = new WritableList<String>(list,
				String.class);
		Iterator<String> iter = wlist.iterator();
		String s1 = iter.next();
		String s2 = iter.next();
		iter.remove();
		String s3 = iter.next();
		assertEquals(s1, "a");
		assertEquals(s2, "b");
		assertEquals(s3, "c");
		assertEquals(2, wlist.size());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(WritableListTest.class.getName());
		suite.addTestSuite(WritableListTest.class);
		suite.addTest(MutableObservableListContractTest.suite(new Delegate()));
		return suite;
	}

	/* package */static class Delegate extends
			AbstractObservableCollectionContractDelegate {
		public Object createElement(IObservableCollection collection) {
			return String.valueOf(collection.size() + 1);
		}

		public Object getElementType(IObservableCollection collection) {
			return String.class;
		}

		public IObservableCollection createObservableCollection(Realm realm,
				final int itemCount) {
			WritableList observable = new WritableList(realm, new ArrayList(),
					String.class);

			for (int i = 0; i < itemCount; i++) {
				observable.add(String.valueOf(i));
			}

			return observable;
		}

		public void change(IObservable observable) {
			((WritableList) observable).add("");
		}
	}
}
