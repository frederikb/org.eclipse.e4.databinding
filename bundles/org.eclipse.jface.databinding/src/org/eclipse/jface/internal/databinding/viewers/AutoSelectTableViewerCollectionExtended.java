/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.viewers;

import java.util.List;

import org.eclipse.jface.internal.provisional.databinding.IDataBindingContext;
import org.eclipse.jface.internal.provisional.databinding.viewers.TableViewerDescription;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * @since 3.2
 *
 */
public class AutoSelectTableViewerCollectionExtended extends TableViewerUpdatableCollectionExtended {
	
	public AutoSelectTableViewerCollectionExtended(TableViewerDescription tableViewerDescription, IDataBindingContext dataBindingContext, int updateTime) {
		super(tableViewerDescription, dataBindingContext, updateTime);
	}

	public void setElements(List elements) {
		super.setElements(elements);
		Object selection = getSelectedObject();
		if (selection == null && elements.size() > 0) {
			viewer.setSelection(new StructuredSelection(elements.get(0)));
		}
	}
}
