/*******************************************************************************
 * Copyright (c) 2007, 2009 Brad Reynolds and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brad Reynolds - initial API and implementation
 *     Matthew Hall - bug 246625, 194734
 ******************************************************************************/

package org.eclipse.jface.tests.databinding.preference;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.databinding.conformance.util.ValueChangeEventTracker;
import org.eclipse.jface.databinding.preference.PreferenceObservables;
import org.eclipse.jface.tests.databinding.AbstractDefaultRealmTestCase;
import org.osgi.service.prefs.Preferences;

/**
 * @since 3.3
 */
public class PreferenceObservableValueTest extends AbstractDefaultRealmTestCase {

	private PreferenceObservables prefObservable;
	private String qualifier1;

	protected void setUp() throws Exception {
		super.setUp();

		List<IScopeContext> scopes = Arrays.asList(new IScopeContext[] {
				InstanceScope.INSTANCE, ConfigurationScope.INSTANCE,
				DefaultScope.INSTANCE });

		qualifier1 = "org.eclipse.jface.tests.databinding";

		prefObservable = PreferenceObservables.observe(scopes, qualifier1);
	}

	public void testSimple() throws Exception {
		IObservableValue<String> key1Observable = prefObservable.observe(
				"key1", "default foo");

		ValueChangeEventTracker listener1 = new ValueChangeEventTracker();

		key1Observable.addValueChangeListener(listener1);
		assertEquals(0, listener1.count);

		IEclipsePreferences instanceNode = InstanceScope.INSTANCE
				.getNode(qualifier1);
		IEclipsePreferences configurationNode = ConfigurationScope.INSTANCE
				.getNode(qualifier1);
		IEclipsePreferences defaultNode = DefaultScope.INSTANCE
				.getNode(qualifier1);

		defaultNode.put("key0", "foo");
		assertEquals(0, listener1.count);
		assertEquals("default foo", key1Observable.getValue());
		defaultNode.put("key1", "foo");
		assertEquals(1, listener1.count);
		assertEquals("foo", key1Observable.getValue());

		instanceNode.put("key1", "bar");
		assertEquals(2, listener1.count);
		assertEquals("bar", key1Observable.getValue());

		configurationNode.put("key1", "low");
		assertEquals(2, listener1.count);
		assertEquals("bar", key1Observable.getValue());
	}

	public void testPathKey() throws Exception {
		IObservableValue<String> key1Observable = prefObservable.observe(
				"root1/child1/key1", "default foo");

		ValueChangeEventTracker listener1 = new ValueChangeEventTracker();

		key1Observable.addValueChangeListener(listener1);
		assertEquals(0, listener1.count);

		Preferences instanceNode = InstanceScope.INSTANCE.getNode(qualifier1)
				.node("root1/child1");
		Preferences configurationNode = ConfigurationScope.INSTANCE.getNode(
				qualifier1).node("root1/child1");
		Preferences defaultNode = DefaultScope.INSTANCE.getNode(qualifier1)
				.node("root1/child1");

		defaultNode.put("key0", "foo");
		assertEquals(0, listener1.count);
		assertEquals("default foo", key1Observable.getValue());
		defaultNode.put("key1", "foo");
		assertEquals(1, listener1.count);
		assertEquals("foo", key1Observable.getValue());

		instanceNode.put("key1", "bar");
		assertEquals(2, listener1.count);
		assertEquals("bar", key1Observable.getValue());

		configurationNode.put("key1", "low");
		assertEquals(2, listener1.count);
		assertEquals("bar", key1Observable.getValue());
	}
}
