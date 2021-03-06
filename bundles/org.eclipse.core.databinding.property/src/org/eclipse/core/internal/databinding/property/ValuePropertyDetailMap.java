/*******************************************************************************
 * Copyright (c) 2008, 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bug 278550
 ******************************************************************************/

package org.eclipse.core.internal.databinding.property;

import java.util.Map;

import org.eclipse.core.databinding.observable.ObservableTracker;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapDiff;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.map.IMapProperty;
import org.eclipse.core.databinding.property.map.MapProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;

/**
 * @param <S>
 *            type of the source object
 * @param <M>
 *            type of the property of the source object this type being the type
 *            that has the map as a property
 * @param <K>
 *            type of the keys to the map
 * @param <V>
 *            type of the values in the map
 * @since 3.3
 * 
 */
public class ValuePropertyDetailMap<S, M, K, V> extends MapProperty<S, K, V> {
	private final IValueProperty<S, M> masterProperty;
	private final IMapProperty<? super M, K, V> detailProperty;

	/**
	 * @param masterProperty
	 * @param detailProperty
	 */
	public ValuePropertyDetailMap(IValueProperty<S, M> masterProperty,
			IMapProperty<? super M, K, V> detailProperty) {
		this.masterProperty = masterProperty;
		this.detailProperty = detailProperty;
	}

	/**
	 * @deprecated use getKeyClass instead
	 */
	public Object getKeyType() {
		return detailProperty.getKeyType();
	}

	/**
	 * @deprecated use getValueClass instead
	 */
	public Object getValueType() {
		return detailProperty.getValueType();
	}

	public Class<K> getKeyClass() {
		return detailProperty.getKeyClass();
	}

	public Class<V> getValueClass() {
		return detailProperty.getValueClass();
	}

	protected Map<K, V> doGetMap(S source) {
		M masterValue = masterProperty.getValue(source);
		return detailProperty.getMap(masterValue);
	}

	protected void doSetMap(S source, Map<K, V> map) {
		M masterValue = masterProperty.getValue(source);
		detailProperty.setMap(masterValue, map);
	}

	protected void doUpdateMap(S source, MapDiff<K, V> diff) {
		M masterValue = masterProperty.getValue(source);
		detailProperty.updateMap(masterValue, diff);
	}

	public IObservableMap<K, V> observe(Realm realm, S source) {
		IObservableValue<M> masterValue;

		ObservableTracker.setIgnore(true);
		try {
			masterValue = masterProperty.observe(realm, source);
		} finally {
			ObservableTracker.setIgnore(false);
		}

		IObservableMap<K, V> detailMap = detailProperty
				.observeDetail(masterValue);
		PropertyObservableUtil.cascadeDispose(detailMap, masterValue);
		return detailMap;
	}

	public <U extends S> IObservableMap<K, V> observeDetail(
			IObservableValue<U> master) {
		IObservableValue<M> masterValue;

		ObservableTracker.setIgnore(true);
		try {
			masterValue = masterProperty.observeDetail(master);
		} finally {
			ObservableTracker.setIgnore(false);
		}

		IObservableMap<K, V> detailMap = detailProperty
				.observeDetail(masterValue);
		PropertyObservableUtil.cascadeDispose(detailMap, masterValue);
		return detailMap;
	}

	public String toString() {
		return masterProperty + " => " + detailProperty; //$NON-NLS-1$
	}
}
