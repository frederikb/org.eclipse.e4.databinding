/*******************************************************************************
 * Copyright (c) 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 260337)
 *     Matthew Hall - bug 283428
 ******************************************************************************/

package org.eclipse.jface.databinding.viewers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.set.ISetProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.AbstractTableViewer;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.StructuredViewer;

/**
 * Helper methods for binding observables to a {@link StructuredViewer} or
 * {@link AbstractTableViewer}.
 * 
 * @since 1.3
 */
public class ViewerSupport {
	/**
	 * Binds the viewer to the specified input, using the specified label
	 * property to generate labels.
	 * 
	 * @param viewer
	 *            the viewer to set up
	 * @param input
	 *            the input to set on the viewer
	 * @param labelProperty
	 *            the property to use for labels
	 */
	public static <E> void bind(StructuredViewer viewer,
			IObservableList<E> input, IValueProperty<? super E, ?> labelProperty) {
		List<IValueProperty<? super E, ?>> singletonList = new ArrayList<IValueProperty<? super E, ?>>();
		singletonList.add(labelProperty);
		bind(viewer, input, singletonList);
	}

	/**
	 * Binds the viewer to the specified input, using the specified label
	 * properties to generate labels.
	 * 
	 * @param viewer
	 *            the viewer to set up
	 * @param input
	 *            the input to set on the viewer
	 * @param labelProperties
	 *            the respective properties to use for labels in each of the
	 *            viewer's columns
	 * @deprecated use the form that takes labelProperties as a List rather than
	 *             an array because List allows for better typing than using
	 *             arrays
	 */
	// OK to suppress warnings in deprecated methods
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <E> void bind(StructuredViewer viewer,
			IObservableList<?> input, IValueProperty[] labelProperties) {
		ObservableListContentProvider<E> contentProvider = new ObservableListContentProvider<E>();
		if (viewer.getInput() != null)
			viewer.setInput(null);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new ObservableMapLabelProvider<E>(Properties
				.observeEach(contentProvider.getKnownElements(),
						labelProperties)));
		if (input != null)
			viewer.setInput(input);
	}

	/**
	 * Binds the viewer to the specified input, using the specified label
	 * properties to generate labels.
	 * 
	 * @param viewer
	 *            the viewer to set up
	 * @param input
	 *            the input to set on the viewer
	 * @param labelProperties
	 *            the respective properties to use for labels in each of the
	 *            viewer's columns
	 * @since 1.7
	 */
	public static <E> void bind(StructuredViewer viewer,
			IObservableList<E> input,
			List<IValueProperty<? super E, ?>> labelProperties) {
		ObservableListContentProvider<E> contentProvider = new ObservableListContentProvider<E>();
		if (viewer.getInput() != null)
			viewer.setInput(null);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new ObservableMapLabelProvider<E>(Properties
				.<Object, E> observeEach(contentProvider.getKnownElements(),
						labelProperties)));
		if (input != null)
			viewer.setInput(input);
	}

	/**
	 * Binds the viewer to the specified input, using the specified label
	 * property to generate labels.
	 * 
	 * @param viewer
	 *            the viewer to set up
	 * @param input
	 *            the input to set on the viewer
	 * @param labelProperty
	 *            the property to use for labels
	 */
	public static <E> void bind(StructuredViewer viewer,
			IObservableSet<E> input, IValueProperty<? super E, ?> labelProperty) {

		List<IValueProperty<? super E, ?>> singletonList = new ArrayList<IValueProperty<? super E, ?>>();
		singletonList.add(labelProperty);

		bind(viewer, input, singletonList);
	}

	/**
	 * Binds the viewer to the specified input, using the specified label
	 * properties to generate labels.
	 * 
	 * @param viewer
	 *            the viewer to set up
	 * @param input
	 *            the input to set on the viewer
	 * @param labelProperties
	 *            the respective properties to use for labels in each of the
	 *            viewer's columns
	 * @deprecated use the form that takes labelProperties as a List rather than
	 *             an array because List allows for better typing than using
	 *             arrays
	 */
	// OK to suppress warnings in deprecated methods
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <E> void bind(StructuredViewer viewer,
			IObservableSet<E> input, IValueProperty[] labelProperties) {
		ObservableSetContentProvider<E> contentProvider = new ObservableSetContentProvider<E>();
		if (viewer.getInput() != null)
			viewer.setInput(null);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new ObservableMapLabelProvider<E>(Properties
				.observeEach(contentProvider.getKnownElements(),
						labelProperties)));
		if (input != null)
			viewer.setInput(input);
	}

	/**
	 * Binds the viewer to the specified input, using the specified label
	 * properties to generate labels.
	 * 
	 * @param viewer
	 *            the viewer to set up
	 * @param input
	 *            the input to set on the viewer
	 * @param labelProperties
	 *            the respective properties to use for labels in each of the
	 *            viewer's columns
	 */
	public static <E> void bind(StructuredViewer viewer,
			IObservableSet<E> input,
			List<IValueProperty<? super E, ?>> labelProperties) {
		ObservableSetContentProvider<E> contentProvider = new ObservableSetContentProvider<E>();
		if (viewer.getInput() != null)
			viewer.setInput(null);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new ObservableMapLabelProvider<E>(Properties
				.observeEach(contentProvider.getKnownElements(),
						labelProperties)));
		if (input != null)
			viewer.setInput(input);
	}

	/**
	 * Binds the viewer to the specified input, using the specified children
	 * property to generate child nodes, and the specified label property to
	 * generate labels.
	 * 
	 * @param viewer
	 *            the tree viewer to set up
	 * @param input
	 *            the input to set on the viewer
	 * @param childrenProperty
	 *            the property to use as the children of an element
	 * @param labelProperty
	 *            the property to use for labels
	 */
	public static <E> void bind(AbstractTreeViewer viewer, Object input,
			IListProperty<?, E> childrenProperty,
			IValueProperty<? super E, ?> labelProperty) {

		List<IValueProperty<? super E, ?>> singletonList = new ArrayList<IValueProperty<? super E, ?>>();
		singletonList.add(labelProperty);

		bind(viewer, input, childrenProperty, singletonList);
	}

	/**
	 * Binds the viewer to the specified input, using the specified children
	 * property to generate child nodes, and the specified label properties to
	 * generate labels.
	 * 
	 * @param viewer
	 *            the tree viewer to set up
	 * @param input
	 *            the input to set on the viewer
	 * @param childrenProperty
	 *            the property to use as the children of an element
	 * @param labelProperties
	 *            the respective properties to use for labels in each of the
	 *            viewer's columns
	 * @deprecated use the form that takes labelProperties as a List rather than
	 *             an array because List allows for better typing than using
	 *             arrays
	 */
	// OK to suppress warnings in deprecated methods
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <E> void bind(AbstractTreeViewer viewer, Object input,
			IListProperty<?, E> childrenProperty,
			IValueProperty[] labelProperties) {
		Realm realm = SWTObservables.getRealm(viewer.getControl().getDisplay());
		IObservableFactory<?, IObservableList<E>> listFactory = childrenProperty
				.listFactory(realm);
		ObservableListTreeContentProvider<E> contentProvider = new ObservableListTreeContentProvider<E>(
				listFactory, null);
		if (viewer.getInput() != null)
			viewer.setInput(null);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new ObservableMapLabelProvider<E>(Properties
				.observeEach(contentProvider.getKnownElements(),
						labelProperties)));
		if (input != null)
			viewer.setInput(input);
	}

	/**
	 * Binds the viewer to the specified input, using the specified children
	 * property to generate child nodes, and the specified label properties to
	 * generate labels.
	 * 
	 * @param viewer
	 *            the tree viewer to set up
	 * @param input
	 *            the input to set on the viewer
	 * @param childrenProperty
	 *            the property to use as the children of an element
	 * @param labelProperties
	 *            the respective properties to use for labels in each of the
	 *            viewer's columns
	 * @since 1.7
	 */
	public static <E> void bind(AbstractTreeViewer viewer, Object input,
			IListProperty<?, E> childrenProperty,
			List<IValueProperty<? super E, ?>> labelProperties) {
		Realm realm = SWTObservables.getRealm(viewer.getControl().getDisplay());
		IObservableFactory<?, IObservableList<E>> listFactory = childrenProperty
				.listFactory(realm);
		ObservableListTreeContentProvider<E> contentProvider = new ObservableListTreeContentProvider<E>(
				listFactory, null);
		if (viewer.getInput() != null)
			viewer.setInput(null);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new ObservableMapLabelProvider<E>(Properties
				.observeEach(contentProvider.getKnownElements(),
						labelProperties)));
		if (input != null)
			viewer.setInput(input);
	}

	/**
	 * Binds the viewer to the specified input, using the specified children
	 * property to generate child nodes, and the specified label property to
	 * generate labels.
	 * 
	 * @param viewer
	 *            the tree viewer to set up
	 * @param input
	 *            the input to set on the viewer
	 * @param childrenProperty
	 *            the property to use as the children of an element
	 * @param labelProperty
	 *            the property to use for labels
	 */
	public static <E> void bind(AbstractTreeViewer viewer, Object input,
			ISetProperty<?, E> childrenProperty,
			IValueProperty<? super E, ?> labelProperty) {

		List<IValueProperty<? super E, ?>> singletonList = new ArrayList<IValueProperty<? super E, ?>>();
		singletonList.add(labelProperty);

		bind(viewer, input, childrenProperty, singletonList);
	}

	/**
	 * Binds the viewer to the specified input, using the specified children
	 * property to generate child nodes, and the specified label properties to
	 * generate labels.
	 * 
	 * @param viewer
	 *            the tree viewer to set up
	 * @param input
	 *            the input to set on the viewer
	 * @param childrenProperty
	 *            the property to use as the children of an element
	 * @param labelProperties
	 *            the respective properties to use for labels in each of the
	 *            viewer's columns
	 * @deprecated use the form that takes labelProperties as a List rather than
	 *             an array because List allows for better typing than using
	 *             arrays
	 */
	// OK to suppress warnings in deprecated methods
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <E> void bind(AbstractTreeViewer viewer, Object input,
			ISetProperty<?, E> childrenProperty,
			IValueProperty[] labelProperties) {
		Realm realm = SWTObservables.getRealm(viewer.getControl().getDisplay());
		IObservableFactory<?, IObservableSet<E>> setFactory = childrenProperty
				.setFactory(realm);
		ObservableSetTreeContentProvider<E> contentProvider = new ObservableSetTreeContentProvider<E>(
				setFactory, null);
		if (viewer.getInput() != null)
			viewer.setInput(null);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new ObservableMapLabelProvider<E>(Properties
				.observeEach(contentProvider.getKnownElements(),
						labelProperties)));
		if (input != null)
			viewer.setInput(input);
	}

	/**
	 * Binds the viewer to the specified input, using the specified children
	 * property to generate child nodes, and the specified label properties to
	 * generate labels.
	 * 
	 * @param viewer
	 *            the tree viewer to set up
	 * @param input
	 *            the input to set on the viewer
	 * @param childrenProperty
	 *            the property to use as the children of an element
	 * @param labelProperties
	 *            the respective properties to use for labels in each of the
	 *            viewer's columns
	 */
	public static <E> void bind(AbstractTreeViewer viewer, Object input,
			ISetProperty<?, E> childrenProperty,
			List<IValueProperty<? super E, ?>> labelProperties) {
		Realm realm = SWTObservables.getRealm(viewer.getControl().getDisplay());
		IObservableFactory<?, IObservableSet<E>> setFactory = childrenProperty
				.setFactory(realm);
		ObservableSetTreeContentProvider<E> contentProvider = new ObservableSetTreeContentProvider<E>(
				setFactory, null);
		if (viewer.getInput() != null)
			viewer.setInput(null);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new ObservableMapLabelProvider<E>(Properties
				.observeEach(contentProvider.getKnownElements(),
						labelProperties)));
		if (input != null)
			viewer.setInput(input);
	}
}
