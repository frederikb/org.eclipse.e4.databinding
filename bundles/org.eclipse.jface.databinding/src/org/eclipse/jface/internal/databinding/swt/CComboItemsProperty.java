/*******************************************************************************
 * Copyright (c) 2008, 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bug 251611
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import java.util.List;

import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffVisitor;
import org.eclipse.swt.custom.CCombo;

/**
 * @since 3.3
 * 
 */
public class CComboItemsProperty extends ControlStringListProperty<CCombo> {
	protected void doSetList(final CCombo control, List<String> listOfValues,
			ListDiff<String> diff) {
		diff.accept(new ListDiffVisitor<String>() {
			CCombo combo = control;

			public void handleAdd(int index, String element) {
				combo.add(element, index);
			}

			public void handleRemove(int index, String element) {
				combo.remove(index);
			}

			public void handleReplace(int index, String oldElement,
					String newElement) {
				combo.setItem(index, newElement);
			}
		});
	}

	public String[] doGetStringList(CCombo control) {
		return control.getItems();
	}

	public String toString() {
		return "CCombo.items[] <String>"; //$NON-NLS-1$
	}
}
