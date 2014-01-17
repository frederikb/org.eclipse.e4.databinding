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
 *            the base class of all elements expected
 * 
 * @since 1.1
 * 
 */
public class ObservableMapLabelProvider<T> extends LabelProvider implements
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
	// protected List<IObservableMap<T, ?>> attributeMapsList;

	class MapWrapper<E> {

		IObservableMap<T, E> attributeMap;

		/**
		 * @param map
		 */
		public MapWrapper(IObservableMap<T, E> map) {
			this.attributeMap = map;
		}

		private IMapChangeListener<T, E> mapChangeListener = new IMapChangeListener<T, E>() {
			public void handleMapChange(MapChangeEvent<T, E> event) {
				Set<? extends T> affectedElements = event.diff.getChangedKeys();
				LabelProviderChangedEvent newEvent = new LabelProviderChangedEvent(
						ObservableMapLabelProvider.this,
						affectedElements.toArray());
				fireLabelProviderChanged(newEvent);
			}
		};

		/**
		 * 
		 */
		public void removeListener() {
			attributeMap.removeMapChangeListener(mapChangeListener);
		}
	}

	List<MapWrapper<?>> attributeMapsList;

	// private IMapChangeListener<T, ?> mapChangeListener = new
	// IMapChangeListener<T, ?>() {
	// public void handleMapChange(MapChangeEvent<T, ?> event) {
	// Set<T> affectedElements = event.diff.getChangedKeys();
	// LabelProviderChangedEvent newEvent = new LabelProviderChangedEvent(
	// ObservableMapLabelProvider.this, affectedElements.toArray());
	// fireLabelProviderChanged(newEvent);
	// }
	// };

	/**
	 * @param attributeMap
	 */
	public <E> ObservableMapLabelProvider(IObservableMap<T, E> attributeMap) {
		this(singletonList(attributeMap));
	}

	/**
	 * @param attributeMap
	 * @return a singleton list with the given attribute map
	 */
	private static <T2, E> List<IObservableMap<T2, ?>> singletonList(
			IObservableMap<T2, E> attributeMap) {
		List<IObservableMap<T2, ?>> singletonList = new ArrayList<IObservableMap<T2, ?>>();
		singletonList.add(attributeMap);
		return singletonList;
	}

	/**
	 * @param attributeMaps
	 * @deprecated use the constructor that takes a List instead
	 */
	// OK to supress warnings on deprecated method
	@SuppressWarnings("unchecked")
	public ObservableMapLabelProvider(IObservableMap<?, ?>[] attributeMaps) {
		this(Arrays.asList((IObservableMap<T, ?>[]) attributeMaps));
	}

	/**
	 * @param attributeMapsList
	 * @since 1.7
	 */
	// This class must initialize the deprecated field, so ok to ignore
	// the warning we get when setting the value of the deprecated field.
	@SuppressWarnings("deprecation")
	public ObservableMapLabelProvider(
			List<IObservableMap<T, ?>> attributeMapsList) {
		// this.attributeMapsList = new ArrayList<IObservableMap<T, ?>>(
		// attributeMapsList);
		this.attributeMapsList = new ArrayList<MapWrapper<?>>(
				attributeMapsList.size());

		// Also copy to the deprecated array for legacy reasons.
		// There may be old code out there that reads from this array.
		this.attributeMaps = attributeMapsList
				.toArray(new IObservableMap<?, ?>[attributeMapsList.size()]);

		for (IObservableMap<T, ?> map : attributeMapsList) {
			this.attributeMapsList.add(createMapWrapper(map));
		}
	}

	/**
	 * @param map
	 * @return a wrapper around the given attribute map that manages its own
	 *         listener
	 */
	private <E> MapWrapper<E> createMapWrapper(IObservableMap<T, E> map) {
		return new MapWrapper<E>(map);
	}

	public void dispose() {
		// for (IObservableMap<T, ?> map : attributeMapsList) {
		// map.removeMapChangeListener(mapChangeListener);
		// }
		for (MapWrapper<?> wrapper : attributeMapsList) {
			wrapper.removeListener();
		}
		super.dispose();
		this.attributeMapsList = null;
		// this.mapChangeListener = null;
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
			Object result = attributeMapsList.get(columnIndex).attributeMap
					.get(element);
			return result == null ? "" : result.toString(); //$NON-NLS-1$
		}
		return null;
	}

}
