package org.eclipse.jface.binding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import org.eclipse.jface.binding.swt.TableViewerDescription;
import org.eclipse.jface.binding.swt.TableViewerDescription.Column;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Item;

public class DefaultCellModifier implements ICellModifier{
	
	private TableViewerDescription tableViewerDescription;

	public DefaultCellModifier(TableViewerDescription tableViewerDescription){
		this.tableViewerDescription = tableViewerDescription;
	}

	private Column findColumn(String property) {
		for (int i = 0; i < tableViewerDescription.getColumnCount(); i++) {
			Column column = tableViewerDescription.getColumn(i);
			if (column.getPropertyName().equals(property)) {
				return column;
			}
		}
		return null;
	}

	public boolean canModify(Object element, String property) {
		return true;
	}

	public Object getValue(Object element, String property) {
		Column column = findColumn(property);
		if (column == null) {
			return null;
		}
		try {
			Method getter = element
					.getClass()
					.getMethod(
							"get"	+ property.substring(0, 1).toUpperCase(Locale.ENGLISH) + property.substring(1), new Class[0]); //$NON-NLS-1$
			return getter.invoke(element, new Object[0]);
		} catch (SecurityException e) {
			// TODO log
		} catch (NoSuchMethodException e) {
			// TODO log
		} catch (IllegalArgumentException e) {
			// TODO log
		} catch (IllegalAccessException e) {
			// TODO log
		} catch (InvocationTargetException e) {
			// TODO log
		}
		return null;
	}

	public void modify(Object element, String property, Object value) {
		Column column = findColumn(property);
		if (column == null) {
			return;
		}
		if (element instanceof Item) {
			element = ((Item) element).getData();
		}
		try {
			Method setter = element
					.getClass()
					.getMethod(
							"set"	+ property.substring(0, 1).toUpperCase(Locale.ENGLISH) + property.substring(1), new Class[] { column.getConverter().getTargetType() }); //$NON-NLS-1$
			setter.invoke(element, new Object[] { value });
			return;
		} catch (SecurityException e) {
			// TODO log
		} catch (NoSuchMethodException e) {
			// TODO log
		} catch (IllegalArgumentException e) {
			// TODO log
		} catch (IllegalAccessException e) {
			// TODO log
		} catch (InvocationTargetException e) {
			// TODO log
		}
	}
}