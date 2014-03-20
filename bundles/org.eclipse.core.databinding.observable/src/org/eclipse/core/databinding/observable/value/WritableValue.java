/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brad Reynolds - bug 158687
 *     Brad Reynolds - bug 164653, 147515
 *     Boris Bokowski - bug 256422
 *     Matthew Hall - bug 256422
 *******************************************************************************/

package org.eclipse.core.databinding.observable.value;

import java.util.LinkedList;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IObservablesListener;
import org.eclipse.core.databinding.observable.ListenerList;
import org.eclipse.core.databinding.observable.ListenerListCopy;
import org.eclipse.core.databinding.observable.Realm;

/**
 * Mutable (writable) implementation of {@link IObservableValue} that will
 * maintain a value and fire change events when the value changes.
 * <p>
 * This class is thread safe. All state accessing methods must be invoked from
 * the {@link Realm#isCurrent() current realm}. Methods for adding and removing
 * listeners may be invoked from any thread.
 * </p>
 * 
 * @param <T>
 * @since 1.0
 */
public class WritableValue<T> extends AbstractObservableValue<T> {

	private final Object valueType;

	/**
	 * Constructs a new instance with the default realm, a <code>null</code>
	 * value type, and a <code>null</code> value.
	 */
	public WritableValue() {
		this(null, null);
	}

	/**
	 * Constructs a new instance with the default realm.
	 * 
	 * @param initialValue
	 *            can be <code>null</code>
	 * @param valueType
	 *            can be <code>null</code>
	 */
	public WritableValue(T initialValue, Object valueType) {
		this(new Realm() {
			@Override
			public boolean isCurrent() {
				return true;
			}
		}, initialValue, valueType);
	}

	/**
	 * Constructs a new instance with the provided <code>realm</code>, a
	 * <code>null</code> value type, and a <code>null</code> initial value.
	 * 
	 * @param realm
	 */
	public WritableValue(Realm realm) {
		this(realm, null, null);
	}

	/**
	 * Constructs a new instance.
	 * 
	 * @param realm
	 * @param initialValue
	 *            can be <code>null</code>
	 * @param valueType
	 *            can be <code>null</code>
	 */
	public WritableValue(Realm realm, T initialValue, Object valueType) {
		super(realm);
		this.valueType = valueType;
		this.value = initialValue;
	}

	private T value = null;

	public T doGetValue() {
		return value;
	}

	// public void doSetValue(T value) {
	// if (this.value != value) {
	// fireValueChange(Diffs.createValueDiff(this.value,
	// this.value = value));
	// }
	// }

	static class QueuedEvent<T> {
		public ValueDiff<T> diff;
		public final ListenerListCopy<IChangeListener> genericListenerList;
		public final ListenerListCopy<IValueChangeListener<T>> valueListenerList;

		QueuedEvent(ValueDiff<T> diff,
				ListenerListCopy<IChangeListener> genericChangeListeners,
				ListenerListCopy<IValueChangeListener<T>> valueChangeListeners) {
			this.diff = diff;
			this.genericListenerList = genericChangeListeners;
			this.valueListenerList = valueChangeListeners;
		}
	}

	private boolean isFiring = false;
	private LinkedList<QueuedEvent<T>> eventQueue = new LinkedList<QueuedEvent<T>>();

	/**
	 * @param value
	 *            The value to set.
	 */
	public void doSetValue(T value) {
		QueuedEvent<T> myOueuedEvent = null;

		synchronized (this) {
			if (value != this.value) {
				T oldValue = this.value;
				this.value = value;

				if ((genericListenerList != null && !genericListenerList
						.isEmpty())
						|| (valueListenerList != null && !valueListenerList
								.isEmpty())) {
					if (!isFiring) {
						// Nothing is currently firing.
						myOueuedEvent = new QueuedEvent<T>(
								Diffs.createValueDiff(oldValue, value),
								genericListenerList == null ? null
										: genericListenerList.getReadOnlyCopy(),
								valueListenerList == null ? null
										: valueListenerList.getReadOnlyCopy());
						isFiring = true;
					} else {
						// Events are currently firing.
						// So we need to put in the event queue to be fired
						// later after the currently
						// firing events have completed.
						if (eventQueue.isEmpty()) {
							QueuedEvent<T> queuedEvent = new QueuedEvent<T>(
									Diffs.createValueDiff(oldValue, value),
									genericListenerList == null ? null
											: genericListenerList
													.getReadOnlyCopy(),
									valueListenerList == null ? null
											: valueListenerList
													.getReadOnlyCopy());
							eventQueue.addLast(queuedEvent);
						} else {
							QueuedEvent<T> lastEvent = eventQueue.getLast();

							/*
							 * If the listener lists are identical, merge the
							 * diffs. Otherwise, because different listeners
							 * need to be told about each diff, queue
							 * separately.
							 */
							if (isIdentical(lastEvent.genericListenerList,
									genericListenerList)
									&& isIdentical(lastEvent.valueListenerList,
											valueListenerList)) {
								lastEvent.diff = Diffs.createValueDiff(
										lastEvent.diff.getOldValue(), value);
							} else {
								QueuedEvent<T> queuedEvent = new QueuedEvent<T>(
										Diffs.createValueDiff(oldValue, value),
										genericListenerList == null ? null
												: genericListenerList
														.getReadOnlyCopy(),
										valueListenerList == null ? null
												: valueListenerList
														.getReadOnlyCopy());
								eventQueue.addLast(queuedEvent);
							}
						}
					}
				}
			}
		}

		/*
		 * We're now outside the synchronization block, so no access to fields.
		 * If we have any event firing work then it is all in myQueuedEvent
		 */

		while (myOueuedEvent != null) {
			// Actually, as this doesn't take a diff, do we need a copy of
			// the listeners in the queue at all. Just fire each time?
			if (myOueuedEvent.genericListenerList != null) {
				myOueuedEvent.genericListenerList.fireEvent(new ChangeEvent(
						this));
			}
			if (myOueuedEvent.valueListenerList != null) {
				myOueuedEvent.valueListenerList
						.fireEvent(new ValueChangeEvent<T>(this,
								myOueuedEvent.diff));
			}

			/*
			 * Synchronize again so we can see if more changes have been made.
			 */
			synchronized (this) {
				if (eventQueue.isEmpty()) {
					// No, nothing. We're done with all events.
					myOueuedEvent = null;
					isFiring = false;
				} else {
					myOueuedEvent = eventQueue.removeFirst();
				}
			}
		}
	}

	/**
	 * Helper method to determine if two listener lists are the same. This
	 * supports null values which indicate that no listeners have been added.
	 * 
	 * @param listenerList1
	 * @param listenerList2
	 * @return true if no changes have been made to the original listener list
	 *         (listenerList2) since the copy in listenerList1 was taken, false
	 *         if listeners have been added or removed
	 */
	private <L extends IObservablesListener<L>> boolean isIdentical(
			ListenerListCopy<L> listenerList1, ListenerList<L> listenerList2) {
		if (listenerList1 == null) {
			return listenerList2 == null;
		} else if (listenerList2 == null) {
			return false;
		} else {
			return listenerList1.isIdenticalTo(listenerList2);
		}
	}

	public Object getValueType() {
		return valueType;
	}

	/**
	 * Adds a listener and returns the current value. This method synchronizes
	 * the getting of the current value with the adding of the listener so the
	 * caller can be sure that the first event fired on the listener will have a
	 * diff with an 'old value' that matches the value returned by this method.
	 * 
	 * @param listener
	 * @return the current value, which is guaranteed to be the starting value
	 *         for the diffs received by the listener
	 * @since 1.5
	 */
	public synchronized T addListenerAndGetValue(
			IValueChangeListener<T> listener) {
		addValueChangeListener(listener);
		return doGetValue();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		Object value = doGetValue();
		buffer.append((value == null) ? "null" : value.toString()); //$NON-NLS-1$
		if (this.isStale()) {
			buffer.append("(stale)"); //$NON-NLS-1$
		}
		if (this.isDisposed()) {
			buffer.append("(disposed)"); //$NON-NLS-1$
		}
		return buffer.toString();
	}

	/**
	 * @param <T2>
	 * @param elementType
	 *            can be <code>null</code>
	 * @return new instance with the default realm and a value of
	 *         <code>null</code>
	 */
	public static <T2> WritableValue<T2> withValueType(Object elementType) {
		return new WritableValue<T2>(Realm.getDefault(), null, elementType);
	}
}
