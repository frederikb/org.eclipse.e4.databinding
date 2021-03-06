/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *         (through BidirectionalMap.java)
 *     Matthew Hall - bug 233306
 *******************************************************************************/
package org.eclipse.core.databinding.observable.map;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.internal.databinding.observable.Util;

/**
 * An
 * <p>
 * This class is thread safe. All state accessing methods must be invoked from
 * the {@link Realm#isCurrent() current realm}. Methods for adding and removing
 * listeners may be invoked from any thread.
 * </p>
 * 
 * @param <K>
 * @param <V>
 * 
 * @since 1.2
 */
public class BidiObservableMap<K, V> extends DecoratingObservableMap<K, V> {
	/**
	 * Inverse of wrapped map. When only a single key map to a given value, it
	 * is put in this set. This field is null when no listeners are registered
	 * on this observable.
	 */
	private Map<V, K> valuesToSingleKeys = null;

	/**
	 * Inverse of wrapped map. When multiple keys map to the same value, they
	 * are combined into a Set, put in this map, and any single key in the
	 * single key map is removed. This field is null when no listeners are
	 * registered on this observable.
	 */
	private Map<V, Set<K>> valuesToSetsOfKeys = null;

	/**
	 * Constructs a BidirectionalMap tracking the given observable map.
	 * 
	 * @param wrappedMap
	 *            the observable map to track
	 */
	public BidiObservableMap(IObservableMap<K, V> wrappedMap) {
		super(wrappedMap, false);
	}

	protected void firstListenerAdded() {
		valuesToSingleKeys = new HashMap<V, K>();
		valuesToSetsOfKeys = new HashMap<V, Set<K>>();
		for (Iterator<Entry<K, V>> it = entrySet().iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = it.next();
			addMapping(entry.getKey(), entry.getValue());
		}
		super.firstListenerAdded();
	}

	protected void lastListenerRemoved() {
		super.lastListenerRemoved();
		valuesToSingleKeys.clear();
		valuesToSetsOfKeys.clear();
		valuesToSingleKeys = null;
		valuesToSetsOfKeys = null;
	}

	protected void handleMapChange(MapChangeEvent<K, V> event) {
		MapDiff<? extends K, ? extends V> diff = event.diff;
		for (Iterator<? extends K> it = diff.getAddedKeys().iterator(); it
				.hasNext();) {
			K addedKey = it.next();
			addMapping(addedKey, diff.getNewValue(addedKey));
		}
		for (Iterator<? extends K> it = diff.getChangedKeys().iterator(); it
				.hasNext();) {
			K changedKey = it.next();
			removeMapping(changedKey, diff.getOldValue(changedKey));
			addMapping(changedKey, diff.getNewValue(changedKey));
		}
		for (Iterator<? extends K> it = diff.getRemovedKeys().iterator(); it
				.hasNext();) {
			K removedKey = it.next();
			removeMapping(removedKey, diff.getOldValue(removedKey));
		}
		super.handleMapChange(event);
	}

	public boolean containsValue(Object value) {
		getterCalled();
		// Faster lookup
		if (valuesToSingleKeys != null) {
			return (valuesToSingleKeys.containsKey(value) || valuesToSetsOfKeys
					.containsKey(value));
		}
		return super.containsValue(value);
	}

	/**
	 * Adds a mapping from value to key in the valuesToKeys map.
	 * 
	 * @param key
	 *            the key being mapped, which may be the key itself or it may be
	 *            a set of keys
	 * @param value
	 *            the value being mapped
	 */
	private void addMapping(K key, V value) {
		if (valuesToSingleKeys.containsKey(value)) {
			K element = valuesToSingleKeys.get(value);
			Set<K> set;
			set = new HashSet<K>(Collections.singleton(element));
			valuesToSingleKeys.remove(value);
			valuesToSetsOfKeys.put(value, set);
			set.add(key);
		} else if (valuesToSetsOfKeys.containsKey(value)) {
			Set<K> keySet = valuesToSetsOfKeys.get(value);
			keySet.add(key);
		} else {
			valuesToSingleKeys.put(value, key);
		}
	}

	/**
	 * Removes a mapping from value to key in the valuesToKeys map.
	 * 
	 * @param key
	 *            the key being unmapped
	 * @param value
	 *            the value being unmapped
	 */
	private void removeMapping(Object key, V value) {
		if (valuesToSingleKeys.containsKey(value)) {
			K element = valuesToSingleKeys.get(value);
			if (element == key || (element != null && element.equals(key))) {
				valuesToSingleKeys.remove(value);
			}
		} else if (valuesToSetsOfKeys.containsKey(value)) {
			Set<K> keySet = valuesToSetsOfKeys.get(value);
			keySet.remove(key);
			if (keySet.isEmpty()) {
				valuesToSetsOfKeys.remove(value);
			}
		}
	}

	/**
	 * Returns the Set of keys that currently map to the given value.
	 * 
	 * @param value
	 *            the value associated with the keys in the returned Set.
	 * @return the Set of keys that map to the given value. If no keys map to
	 *         the given value, an empty set is returned.
	 */
	public Set<K> getKeys(Object value) {
		// valuesToSingleKeys is null when no listeners are registered
		if (valuesToSingleKeys == null)
			return findKeys(value);

		if (valuesToSingleKeys.containsKey(value)) {
			return Collections.singleton(valuesToSingleKeys.get(value));
		} else if (valuesToSetsOfKeys.containsKey(value)) {
			return Collections.unmodifiableSet(valuesToSetsOfKeys.get(value));
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * Iterates the map and returns the set of keys which currently map to the
	 * given value.
	 * 
	 * @param value
	 *            the value to search for
	 * @return the set of keys which currently map to the specified value.
	 */
	private Set<K> findKeys(Object value) {
		Set<K> keys = new HashSet<K>();
		for (Iterator<Entry<K, V>> it = entrySet().iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = it.next();
			if (Util.equals(entry.getValue(), value))
				keys.add(entry.getKey());
		}
		return keys;
	}

	public synchronized void dispose() {
		if (valuesToSingleKeys != null) {
			valuesToSingleKeys.clear();
			valuesToSingleKeys = null;
		}
		if (valuesToSetsOfKeys != null) {
			valuesToSetsOfKeys.clear();
			valuesToSetsOfKeys = null;
		}
		super.dispose();
	}
}
