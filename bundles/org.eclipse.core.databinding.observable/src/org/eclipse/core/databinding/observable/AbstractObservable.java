/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brad Reynolds - bug 164653
 *     Matthew Hall - bugs 118516, 146397, 249526
 *******************************************************************************/

package org.eclipse.core.databinding.observable;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.AssertionFailedException;

/**
 * @since 1.0
 */
public abstract class AbstractObservable extends ChangeSupport implements
		IObservable {
	private final Realm realm;

	private boolean disposed = false;

	/**
	 * @param realm
	 */
	public AbstractObservable(Realm realm) {
		Assert.isNotNull(realm, "Realm cannot be null"); //$NON-NLS-1$
		this.realm = realm;

		ObservableTracker.observableCreated(this);
	}

	/**
	 * @return Returns the realm.
	 */
	public Realm getRealm() {
		return realm;
	}

	protected void fireChange() {
		checkRealm();
		if (genericListenerList != null) {
			genericListenerList.fireEvent(new ChangeEvent(this));
		}
	}

	protected void fireStale() {
		checkRealm();
		if (staleListenerList != null) {
			staleListenerList.fireEvent(new StaleEvent(this));
		}
	}

	/**
	 * @since 1.2
	 */
	public synchronized boolean isDisposed() {
		return disposed;

	}

	/**
	 * 
	 */
	public synchronized void dispose() {
		if (!disposed) {
			disposed = true;
			fireDispose(new DisposeEvent(this));
			genericListenerList = null;
			staleListenerList = null;
			disposeListenerList = null;
		}
	}

	/**
	 * Asserts that the realm is the current realm.
	 * 
	 * @see Realm#isCurrent()
	 * @throws AssertionFailedException
	 *             if the realm is not the current realm
	 */
	protected void checkRealm() {
		Assert.isTrue(getRealm().isCurrent(),
				"This operation must be run within the observable's realm"); //$NON-NLS-1$
	}
}
