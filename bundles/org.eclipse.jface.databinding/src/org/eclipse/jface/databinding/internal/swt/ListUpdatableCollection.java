/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.databinding.internal.swt;

import org.eclipse.jface.databinding.DataBinding;
import org.eclipse.jface.databinding.IChangeEvent;
import org.eclipse.jface.databinding.IUpdatableCollection;
import org.eclipse.jface.databinding.Updatable;
import org.eclipse.swt.widgets.List;

/**
 * @since 3.2
 *
 */
public class ListUpdatableCollection extends Updatable implements IUpdatableCollection {
	
	private final List list;

	private final String attribute;

	private boolean updating = false;

	/**
	 * @param list
	 * @param attribute
	 */
	public ListUpdatableCollection(List list, String attribute) {
		this.list = list;
		
		
		if (attribute.equals(DataBinding.CONTENT))
			this.attribute = DataBinding.ITEMS;
		else
			this.attribute = attribute;
		
		if (this.attribute.equals(DataBinding.ITEMS)) {
			//TODO List does not fire any event when items are changed.
//			list.addModifyListener(new ModifyListener() {
//				public void modifyText(ModifyEvent e) {
//					if (!updating) {
//						fireChangeEvent(IChangeEvent.CHANGE, null, null);
//					}
//				}
//			});
		}
		else
			throw new IllegalArgumentException();
	}

	public int getSize() {
		return list.getItemCount();
	}

	public int addElement(Object value, int index) {
		updating=true;		
		try {
			if (index<0 || index>getSize())
				index=getSize();
			String[] newItems = new String[getSize()+1];			
			System.arraycopy(list.getItems(), 0, newItems,0, index);
			newItems[index]=(String)value;
			System.arraycopy(list.getItems(), index, newItems,index+1, getSize()-index);
			list.setItems(newItems);
			fireChangeEvent(IChangeEvent.ADD, null, value, index);
		}
		finally{
			updating=false;
		}
		return index;
	}

	public void removeElement(int index) {
		updating=true;		
		try {
			if (index<0 || index>getSize())
				index=getSize();
			String[] newItems = new String[getSize()-1];
			String old = list.getItem(index);
			System.arraycopy(list.getItems(), 0, newItems,0, index);			
			System.arraycopy(list.getItems(), index, newItems,index-1, getSize()-index);			
			list.setItems(newItems);
			fireChangeEvent(IChangeEvent.REMOVE, old, null, index);
		}
		finally{
			updating=false;
		}		
	}

	public void setElement(int index, Object value) {
		String old = list.getItem(index);
		list.setItem(index, (String)value);
		fireChangeEvent(IChangeEvent.CHANGE, old, value, index);
	}

	public Object getElement(int index) {
		return list.getItem(index);
	}

	public Class getElementType() {
		return String.class;
	}

}
