/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boris Bokowski, IBM Corporation - initial API and implementation
 *     Matthew Hall - bug 212468
 *******************************************************************************/
package org.eclipse.core.internal.databinding.observable;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.IStaleListener;
import org.eclipse.core.databinding.observable.StaleEvent;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;

/**
 * An observable value that tracks the staleness of an {@link IObservable}.
 * 
 * @since 1.1
 */
public class StalenessObservableValue extends AbstractObservableValue<Boolean> {

	private class MyChangeListener implements IChangeListener {
		public void handleChange(ChangeEvent event) {
			if (stale && !event.getObservable().isStale()) {
				stale = false;
				fireValueChange(Diffs.createValueDiff(Boolean.TRUE,
						Boolean.FALSE));
			}
		}
	}

	private class MyStaleListener implements IStaleListener {
		public void handleStale(StaleEvent staleEvent) {
			if (!stale) {
				stale = true;
				fireValueChange(Diffs.createValueDiff(Boolean.FALSE,
						Boolean.TRUE));
			}
		}
	}

	private IObservable tracked;
	private boolean stale;
	private MyChangeListener changeListener = new MyChangeListener();
	private MyStaleListener staleListener = new MyStaleListener();

	/**
	 * Constructs a StalenessObservableValue that tracks the staleness of the
	 * given {@link IObservable}.
	 * 
	 * @param observable
	 *            the observable to track
	 */
	public StalenessObservableValue(IObservable observable) {
		super(observable.getRealm());
		this.tracked = observable;
		this.stale = observable.isStale();
		tracked.addChangeListener(changeListener);
		tracked.addStaleListener(staleListener);
	}

	protected Boolean doGetValue() {
		return tracked.isStale() ? Boolean.TRUE : Boolean.FALSE;
	}

	public Object getValueType() {
		return Boolean.TYPE;
	}

	public synchronized void dispose() {
		if (tracked != null) {
			tracked.removeChangeListener(changeListener);
			tracked.removeStaleListener(staleListener);
			tracked = null;
			changeListener = null;
			staleListener = null;
		}
		super.dispose();
	}

}
