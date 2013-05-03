/*******************************************************************************
 * Copyright (c) 2009, 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 259380)
 *     Matthew Hall - bug 283204
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.viewers;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.core.databinding.property.set.SetProperty;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ICheckable;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * @since 3.3
 * 
 */
public class CheckableCheckedElementsProperty extends
		SetProperty<ICheckable, Object> {
	private final Object elementType;

	/**
	 * @param elementType
	 */
	public CheckableCheckedElementsProperty(Object elementType) {
		this.elementType = elementType;
	}

	public Object getElementType() {
		return elementType;
	}

	public Class<Object> getElementClass() {
		return Object.class;
	}

	protected Set<Object> doGetSet(ICheckable source) {
		throw new UnsupportedOperationException(
				"Cannot query the checked elements on an ICheckable"); //$NON-NLS-1$
	}

	protected void doSetSet(ICheckable source, Set<Object> set) {
		throw new UnsupportedOperationException(
				"Cannot batch replace the checked elements on an ICheckable.  " + //$NON-NLS-1$
						"Use updateSet(SetDiff) instead"); //$NON-NLS-1$
	}

	protected void doUpdateSet(ICheckable source, SetDiff<Object> diff) {
		ICheckable checkable = source;
		for (Iterator<Object> it = diff.getAdditions().iterator(); it.hasNext();)
			checkable.setChecked(it.next(), true);
		for (Iterator<Object> it = diff.getRemovals().iterator(); it.hasNext();)
			checkable.setChecked(it.next(), false);
	}

	public IObservableSet<Object> observe(ICheckable source) {
		if (source instanceof Viewer) {
			return observe(SWTObservables.getRealm(((Viewer) source)
					.getControl().getDisplay()), source);
		}
		return super.observe(source);
	}

	public IObservableSet<Object> observe(Realm realm, ICheckable source) {
		IElementComparer comparer = null;
		if (source instanceof StructuredViewer)
			comparer = ((StructuredViewer) source).getComparer();
		Set<Object> wrappedSet = ViewerElementSet.withComparer(comparer);
		IObservableSet<Object> observable = new CheckableCheckedElementsObservableSet(
				realm, wrappedSet, elementType, comparer, source);
		if (source instanceof Viewer)
			observable = new ViewerObservableSetDecorator<Viewer, Object>(
					observable, (Viewer) source);
		return observable;
	}
}
