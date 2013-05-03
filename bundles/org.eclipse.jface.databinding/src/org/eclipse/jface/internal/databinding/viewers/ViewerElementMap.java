/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 215531)
 *     Matthew Hall - bug 228125
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.viewers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.databinding.util.Util;
import org.eclipse.jface.internal.databinding.viewers.ObservableCollectionTreeContentProvider.TreeNode;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.StructuredViewer;

/**
 * A {@link Map} whose keys are elements in a {@link StructuredViewer}. The keys
 * in the map are compared using an {@link IElementComparer} instead of
 * {@link #equals(Object)}.
 * <p>
 * This class is <i>not</i> a strict implementation the {@link Map} interface.
 * It intentionally violates the {@link Map} contract, which requires the use of
 * {@link #equals(Object)} when comparing keys. This class is designed for use
 * with {@link StructuredViewer} which uses {@link IElementComparer} for element
 * comparisons.
 * 
 * @param <K>
 * @param <V>
 * 
 * @since 1.2
 */
public class ViewerElementMap<K, V> implements Map<K, V> {
	private Map<ViewerElementWrapper<K>, V> wrappedMap;
	private IElementComparer comparer;

	/**
	 * Constructs a ViewerElementMap using the given {@link IElementComparer}.
	 * 
	 * @param comparer
	 *            the {@link IElementComparer} used for comparing keys.
	 */
	public ViewerElementMap(IElementComparer comparer) {
		Assert.isNotNull(comparer);
		this.wrappedMap = new HashMap<ViewerElementWrapper<K>, V>();
		this.comparer = comparer;
	}

	/**
	 * Constructs a ViewerElementMap containing all the entries in the specified
	 * map.
	 * 
	 * @param map
	 *            the map whose entries are to be added to this map.
	 * @param comparer
	 *            the {@link IElementComparer} used for comparing keys.
	 */
	public ViewerElementMap(Map<? extends K, ? extends V> map,
			IElementComparer comparer) {
		this(comparer);
		Assert.isNotNull(map);
		putAll(map);
	}

	public void clear() {
		wrappedMap.clear();
	}

	public boolean containsKey(Object key) {
		return wrappedMap.containsKey(new ViewerElementWrapper<Object>(key,
				comparer));
	}

	public boolean containsValue(Object value) {
		return wrappedMap.containsValue(value);
	}

	public Set<Entry<K, V>> entrySet() {
		final Set<Entry<ViewerElementWrapper<K>, V>> wrappedEntrySet = wrappedMap
				.entrySet();
		return new Set<Entry<K, V>>() {
			public boolean add(Entry<K, V> o) {
				throw new UnsupportedOperationException();
			}

			public boolean addAll(Collection<? extends Entry<K, V>> c) {
				throw new UnsupportedOperationException();
			}

			public void clear() {
				wrappedEntrySet.clear();
			}

			public boolean contains(Object o) {
				for (Iterator<Entry<K, V>> iterator = iterator(); iterator
						.hasNext();)
					if (iterator.next().equals(o))
						return true;
				return false;
			}

			public boolean containsAll(Collection<?> c) {
				for (Iterator<?> iterator = c.iterator(); iterator.hasNext();)
					if (!contains(iterator.next()))
						return false;
				return true;
			}

			public boolean isEmpty() {
				return wrappedEntrySet.isEmpty();
			}

			public Iterator<Entry<K, V>> iterator() {
				final Iterator<Entry<ViewerElementWrapper<K>, V>> wrappedIterator = wrappedEntrySet
						.iterator();
				return new Iterator<Entry<K, V>>() {
					public boolean hasNext() {
						return wrappedIterator.hasNext();
					}

					public Entry<K, V> next() {
						final Entry<ViewerElementWrapper<K>, V> wrappedEntry = wrappedIterator
								.next();
						return new Map.Entry<K, V>() {
							public K getKey() {
								return wrappedEntry.getKey().unwrap();
							}

							public V getValue() {
								return wrappedEntry.getValue();
							}

							public V setValue(V value) {
								return wrappedEntry.setValue(value);
							}

							public boolean equals(Object obj) {
								if (obj == this)
									return true;
								if (obj == null || !(obj instanceof Map.Entry))
									return false;
								Map.Entry<?, ?> that = (Map.Entry<?, ?>) obj;
								return comparer.equals(this.getKey(),
										that.getKey())
										&& Util.equals(this.getValue(),
												that.getValue());
							}

							public int hashCode() {
								return wrappedEntry.hashCode();
							}
						};
					}

					public void remove() {
						wrappedIterator.remove();
					}
				};
			}

			public boolean remove(Object o) {
				final Map.Entry<?, ?> unwrappedEntry = (Map.Entry<?, ?>) o;
				final ViewerElementWrapper<?> wrappedKey = new ViewerElementWrapper<Object>(
						unwrappedEntry.getKey(), comparer);
				Map.Entry<?, ?> wrappedEntry = new Map.Entry<Object, Object>() {
					public Object getKey() {
						return wrappedKey;
					}

					public Object getValue() {
						return unwrappedEntry.getValue();
					}

					public Object setValue(Object value) {
						throw new UnsupportedOperationException();
					}

					public boolean equals(Object obj) {
						if (obj == this)
							return true;
						if (obj == null || !(obj instanceof Map.Entry))
							return false;
						Map.Entry<?, ?> that = (Map.Entry<?, ?>) obj;
						return Util.equals(wrappedKey, that.getKey())
								&& Util.equals(this.getValue(), that.getValue());
					}

					public int hashCode() {
						return wrappedKey.hashCode()
								^ (getValue() == null ? 0 : getValue()
										.hashCode());
					}
				};
				return wrappedEntrySet.remove(wrappedEntry);
			}

			public boolean removeAll(Collection<?> c) {
				boolean changed = false;
				for (Iterator<?> iterator = c.iterator(); iterator.hasNext();)
					changed |= remove(iterator.next());
				return changed;
			}

			public boolean retainAll(Collection<?> c) {
				boolean changed = false;
				Object[] toRetain = c.toArray();
				outer: for (Iterator<?> iterator = iterator(); iterator
						.hasNext();) {
					Object entry = iterator.next();
					for (int i = 0; i < toRetain.length; i++)
						if (entry.equals(toRetain[i]))
							continue outer;
					iterator.remove();
					changed = true;
				}
				return changed;
			}

			public int size() {
				return wrappedEntrySet.size();
			}

			public Object[] toArray() {
				return toArray(new Object[size()]);
			}

			public <E2> E2[] toArray(E2[] a) {
				int size = size();
				Class<E2> componentType = Util.getComponentType(a);

				E2[] result = a;
				if (a.length < size) {
					result = Util.createArrayInstance(componentType, size);
				}

				int i = 0;
				for (Object element : this) {
					result[i] = componentType.cast(element);
				}

				return result;
			}

			public boolean equals(Object obj) {
				if (obj == this)
					return true;
				if (obj == null || !(obj instanceof Set))
					return false;
				Set<?> that = (Set<?>) obj;
				return this.size() == that.size() && containsAll(that);
			}

			public int hashCode() {
				return wrappedEntrySet.hashCode();
			}
		};
	}

	public V get(Object key) {
		return wrappedMap.get(new ViewerElementWrapper<Object>(key, comparer));
	}

	public boolean isEmpty() {
		return wrappedMap.isEmpty();
	}

	public Set<K> keySet() {
		final Set<ViewerElementWrapper<K>> wrappedKeySet = wrappedMap.keySet();
		return new Set<K>() {
			public boolean add(K o) {
				throw new UnsupportedOperationException();
			}

			public boolean addAll(Collection<? extends K> c) {
				throw new UnsupportedOperationException();
			}

			public void clear() {
				wrappedKeySet.clear();
			}

			public boolean contains(Object o) {
				return wrappedKeySet.contains(new ViewerElementWrapper<Object>(
						o, comparer));
			}

			public boolean containsAll(Collection<?> c) {
				for (Iterator<?> iterator = c.iterator(); iterator.hasNext();)
					if (!wrappedKeySet
							.contains(new ViewerElementWrapper<Object>(iterator
									.next(), comparer)))
						return false;
				return true;
			}

			public boolean isEmpty() {
				return wrappedKeySet.isEmpty();
			}

			public Iterator<K> iterator() {
				final Iterator<ViewerElementWrapper<K>> wrappedIterator = wrappedKeySet
						.iterator();
				return new Iterator<K>() {
					public boolean hasNext() {
						return wrappedIterator.hasNext();
					}

					public K next() {
						return wrappedIterator.next().unwrap();
					}

					public void remove() {
						wrappedIterator.remove();
					}
				};
			}

			public boolean remove(Object o) {
				return wrappedKeySet.remove(new ViewerElementWrapper<Object>(o,
						comparer));
			}

			public boolean removeAll(Collection<?> c) {
				boolean changed = false;
				for (Iterator<?> iterator = c.iterator(); iterator.hasNext();)
					changed |= wrappedKeySet
							.remove(new ViewerElementWrapper<Object>(iterator
									.next(), comparer));
				return changed;
			}

			public boolean retainAll(Collection<?> c) {
				boolean changed = false;
				Object[] toRetain = c.toArray();
				outer: for (Iterator<K> iterator = iterator(); iterator
						.hasNext();) {
					K element = iterator.next();
					for (int i = 0; i < toRetain.length; i++)
						if (comparer.equals(element, toRetain[i]))
							continue outer;
					// element not contained in collection, remove.
					remove(element);
					changed = true;
				}
				return changed;
			}

			public int size() {
				return wrappedKeySet.size();
			}

			public Object[] toArray() {
				return toArray(new Object[wrappedKeySet.size()]);
			}

			public <E2> E2[] toArray(E2[] a) {
				int size = wrappedKeySet.size();
				Class<E2> componentType = Util.getComponentType(a);

				E2[] result = a;
				if (a.length < size) {
					result = Util.createArrayInstance(componentType, size);
				}

				int i = 0;
				for (ViewerElementWrapper<K> element : wrappedKeySet) {
					result[i] = componentType.cast(element.unwrap());
				}

				return result;
			}

			public boolean equals(Object obj) {
				if (obj == this)
					return true;
				if (obj == null || !(obj instanceof Set))
					return false;
				Set<?> that = (Set<?>) obj;
				return this.size() == that.size() && containsAll(that);
			}

			public int hashCode() {
				return wrappedKeySet.hashCode();
			}
		};
	}

	public V put(K key, V value) {
		return wrappedMap
				.put(new ViewerElementWrapper<K>(key, comparer), value);
	}

	public void putAll(Map<? extends K, ? extends V> other) {
		for (Map.Entry<? extends K, ? extends V> entry : other.entrySet()) {
			wrappedMap.put(
					new ViewerElementWrapper<K>(entry.getKey(), comparer),
					entry.getValue());
		}
	}

	public V remove(Object key) {
		return wrappedMap
				.remove(new ViewerElementWrapper<Object>(key, comparer));
	}

	public int size() {
		return wrappedMap.size();
	}

	public Collection<V> values() {
		return wrappedMap.values();
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || !(obj instanceof Map))
			return false;
		Map<?, ?> that = (Map<?, ?>) obj;
		return this.entrySet().equals(that.entrySet());
	}

	public int hashCode() {
		return wrappedMap.hashCode();
	}

	/**
	 * Returns a Map for mapping viewer elements as keys to values, using the
	 * given {@link IElementComparer} for key comparisons.
	 * 
	 * @param comparer
	 *            the element comparer to use in key comparisons. If null, the
	 *            returned map will compare keys according to the standard
	 *            contract for {@link Map} interface contract.
	 * @return a Map for mapping viewer elements as keys to values, using the
	 *         given {@link IElementComparer} for key comparisons.
	 */
	public static <K> Map<K, TreeNode> withComparer(IElementComparer comparer) {
		if (comparer == null)
			return new HashMap<K, TreeNode>();
		return new ViewerElementMap<K, TreeNode>(comparer);
	}
}
