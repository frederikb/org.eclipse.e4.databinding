package org.eclipse.jface.binding.internal.swt;

import org.eclipse.jface.binding.IChangeEvent;
import org.eclipse.jface.binding.IUpdatableCollection;
import org.eclipse.jface.binding.Updatable;
import org.eclipse.jface.binding.swt.SWTBindingConstants;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;

/**
 * @since 3.2
 *
 */
public class ComboUpdatableCollection extends Updatable implements IUpdatableCollection {
	
	private final Combo combo;

	private final String attribute;

	private boolean updating = false;

	/**
	 * @param combo
	 * @param attribute
	 */
	public ComboUpdatableCollection(Combo combo, String attribute) {
		this.combo = combo;
		
		
		if (attribute.equals(SWTBindingConstants.CONTENT))
			this.attribute = SWTBindingConstants.ITEMS;
		else
			this.attribute = attribute;
		
		if (this.attribute.equals(SWTBindingConstants.ITEMS)) {
			combo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (!updating) {
						fireChangeEvent(IChangeEvent.CHANGE, null, null);
					}
				}
			});
		}
		else
			throw new IllegalArgumentException();
	}

	public int getSize() {
		return combo.getItemCount();
	}

	public int addElement(Object value, int index) {
		updating=true;		
		try {
			if (index<0 || index>getSize())
				index=getSize();
			String[] newItems = new String[getSize()+1];
			System.arraycopy(combo.getItems(), 0, newItems,0, index);
			newItems[index]=(String)value;
			System.arraycopy(combo.getItems(), index, newItems,index+1, getSize()-index);
			combo.setItems(newItems);
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
			System.arraycopy(combo.getItems(), 0, newItems,0, index);			
			System.arraycopy(combo.getItems(), index, newItems,index-1, getSize()-index);
			combo.setItems(newItems);
		}
		finally{
			updating=false;
		}		
	}

	public void setElement(int index, Object value) {
		combo.setItem(index, (String)value);
	}

	public Object getElement(int index) {
		return combo.getItem(index);
	}

	public Class getElementType() {
		return String.class;
	}

}
