/*******************************************************************************
 * Copyright (c) 2008, 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bug 259380
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.viewers;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.jface.viewers.CheckboxTableViewer;

/**
 * @since 3.3
 * 
 */
public class CheckboxTableViewerCheckedElementsProperty extends
		CheckboxViewerCheckedElementsProperty<CheckboxTableViewer> {
	/**
	 * @param elementType
	 */
	public CheckboxTableViewerCheckedElementsProperty(Object elementType) {
		super(elementType);
	}

	protected Set<Object> doGetSet(CheckboxTableViewer source) {
		Set<Object> set = createElementSet(source);
		set.addAll(Arrays.asList(source.getCheckedElements()));
		return set;
	}

	protected void doSetSet(CheckboxTableViewer source, Set<Object> set,
			SetDiff<Object> diff) {
		doSetSet(source, set);
	}

	protected void doSetSet(CheckboxTableViewer source, Set<Object> set) {
		source.setCheckedElements(set.toArray());
	}

	public String toString() {
		String s = "CheckboxTableViewer.checkedElements{}"; //$NON-NLS-1$
		if (getElementType() != null)
			s += " <" + getElementType() + ">"; //$NON-NLS-1$//$NON-NLS-2$
		return s;
	}
}
