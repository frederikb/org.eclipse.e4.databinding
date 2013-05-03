/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brad Reynolds - bugs 164247, 164134
 *     Matthew Hall - bug 302860
 *******************************************************************************/

package org.eclipse.jface.databinding.viewers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;

/**
 * A label provider based on one or more observable maps that track attributes
 * that this label provider uses for display. Clients may customize by
 * subclassing and overriding {@link #getColumnText(Object, int)},
 * {@link #getColumnImage(Object, int)}, for tables or trees with columns, or by
 * implementing additional mixin interfaces for colors, fonts etc.
 * 
 * @param <T>
 *            the basemost class of all elements expected
 * @param <E>
 *            the basemost class of all label values, which will often be
 *            <String> but no always because values are converted using
 *            toString() method
 * 
 * @since 1.1
 * 
 */
public class ObservableMapLabelProvider<T, E> extends LabelProvider implements
		ILabelProvider, ITableLabelProvider {

	/**
	 * Observable maps typically mapping from viewer elements to label values.
	 * Subclasses may reference these maps to provide custom labels.
	 * 
	 * @since 1.4
	 * @deprecated to access the maps use attributeMapsList instead
	 */
	protected IObservableMap<?, ?>[] attributeMaps;

	/**
	 * Observable maps typically mapping from viewer elements to label values.
	 * Subclasses may reference these maps to provide custom labels.
	 * 
	 * @since 1.7
	 */
	protected List<IObservableMap<T, E>> attributeMapsList;

	private IMapChangeListener<T, E> mapChangeListener = new IMapChangeListener<T, E>() {
		public void handleMapChange(MapChangeEvent<T, E> event) {
			Set<T> affectedElements = event.diff.getChangedKeys();
			LabelProviderChangedEvent newEvent = new LabelProviderChangedEvent(
					ObservableMapLabelProvider.this, affectedElements.toArray());
			fireLabelProviderChanged(newEvent);
		}
	};

	/**
	 * @param attributeMap
	 */
	public ObservableMapLabelProvider(IObservableMap<T, E> attributeMap) {
		this(Collections.singletonList(attributeMap));
	}

	/**
	 * @param attributeMaps
	 * @deprecated use the constructor that takes a List instead
	 */
	public ObservableMapLabelProvider(IObservableMap<T, E>[] attributeMaps) {
		this(Arrays.asList(attributeMaps));
	}

	/**
	 * @param attributeMapsList
	 * @since 1.7
	 */
	@SuppressWarnings("deprecation")
	// This class must initialize the deprecated field
	public ObservableMapLabelProvider(
			List<IObservableMap<T, E>> attributeMapsList) {
		this.attributeMapsList = new ArrayList<IObservableMap<T, E>>(
				attributeMapsList);

		// Also copy to the array for legacy reasons
		this.attributeMaps = attributeMapsList
				.toArray(new IObservableMap<?, ?>[attributeMapsList.size()]);

		for (IObservableMap<T, E> map : attributeMapsList) {
			map.addMapChangeListener(mapChangeListener);
		}
	}

	public void dispose() {
		for (IObservableMap<T, E> map : attributeMapsList) {
			map.removeMapChangeListener(mapChangeListener);
		}
		super.dispose();
		this.attributeMapsList = null;
		this.mapChangeListener = null;
	}

	public Image getImage(Object element) {
		return getColumnImage(element, 0);
	}

	public String getText(Object element) {
		return getColumnText(element, 0);
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex < attributeMapsList.size()) {
			Object result = attributeMapsList.get(columnIndex).get(element);
			return result == null ? "" : result.toString(); //$NON-NLS-1$
		}
		return null;
	}

}
