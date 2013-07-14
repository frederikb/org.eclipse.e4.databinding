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

import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffVisitor;
import org.eclipse.swt.widgets.List;

/**
 * @since 3.3
 * 
 */
public class ListItemsProperty extends ControlStringListProperty<List> {
	protected void doSetList(final List listControl,
			java.util.List<String> listOfValues, ListDiff<String> diff) {
		diff.accept(new ListDiffVisitor<String>() {
			public void handleAdd(int index, String element) {
				listControl.add(element, index);
			}

			public void handleRemove(int index, String element) {
				listControl.remove(index);
			}

			public void handleReplace(int index, String oldElement,
					String newElement) {
				listControl.setItem(index, newElement);
			}
		});
	}

	public String[] doGetStringList(List control) {
		return control.getItems();
	}

	public String toString() {
		return "List.items[] <String>"; //$NON-NLS-1$
	}
}
