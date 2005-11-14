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
package org.eclipse.jface.databinding.internal.viewers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.eclipse.jface.databinding.IChangeEvent;
import org.eclipse.jface.databinding.IChangeListener;
import org.eclipse.jface.databinding.IConverter;
import org.eclipse.jface.databinding.IValidationContext;
import org.eclipse.jface.databinding.IValidator;
import org.eclipse.jface.databinding.IdentityConverter;
import org.eclipse.jface.databinding.TableViewerDescription;
import org.eclipse.jface.databinding.TableViewerDescription.Column;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;

/**
 * @since 3.2
 * 
 */
public class TableViewerUpdatableCollectionExtended extends
		TableViewerUpdatableCollection {

	private final TableViewerDescription tableViewerDescription;
	
	private IValidationContext validationContext;

	private ITableLabelProvider tableLabelProvider = new ITableLabelProvider() {

		private Object getValue(Object element, Column column) {
			Object value = tableViewerDescription.getCellModifier().getValue(
					element, column.getPropertyName());
			return value;
		}

		private Object getConvertedValue(Object element, Column column) {
			Object value = getValue(element, column);
			Object convertedValue = column.getConverter().convertModelToTarget(
					value);
			return convertedValue;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			Column column = getColumn(columnIndex);
			IConverter converter = column.getConverter();
			if (converter.getTargetType().equals(ViewerLabel.class)) {
				ViewerLabel viewerLabel = (ViewerLabel) getConvertedValue(
						element, column);
				return viewerLabel.getImage();
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			Column column = getColumn(columnIndex);
			Object convertedValue = getConvertedValue(element, column);
			IConverter converter = column.getConverter();
			if (converter.getTargetType().equals(ViewerLabel.class)) {
				ViewerLabel viewerLabel = (ViewerLabel) convertedValue;
				return viewerLabel.getText();
			}
			return (String) convertedValue;
		}

		public void addListener(ILabelProviderListener listener) {
			// ignore
		}

		public void dispose() {
			// ignore
		}

		public boolean isLabelProperty(Object element, String property) {
			return true;
		}

		public void removeListener(ILabelProviderListener listener) {
			// ignore
		}
	};

	/**
	 * @param tableViewerDescription
	 * @param validationContext 
	 */
	public TableViewerUpdatableCollectionExtended(
			TableViewerDescription tableViewerDescription, IValidationContext validationContext) {
		super(tableViewerDescription.getTableViewer());
		this.tableViewerDescription = tableViewerDescription;
		this.validationContext = validationContext;
		fillDescriptionDefaults();
		TableViewer tableViewer = tableViewerDescription.getTableViewer();
		// TODO synchronize columns on the widget side (create missing columns,
		// set name if not already set)
		tableViewer.setLabelProvider(tableLabelProvider);
		tableViewer.setCellModifier(tableViewerDescription.getCellModifier());
		int columnCount = tableViewerDescription.getColumnCount();
		String[] columnProperties = new String[columnCount];
		CellEditor[] cellEditors = new CellEditor[columnCount];
		for (int i = 0; i < columnCount; i++) {
			Column column = tableViewerDescription.getColumn(i);
			columnProperties[i] = column.getPropertyName();
			cellEditors[i] = column.getCellEditor();
		}
		tableViewer.setColumnProperties(columnProperties);
		tableViewer.setCellEditors(cellEditors);
	}

	private void fillDescriptionDefaults() {
		CellEditor defaultCellEditor = null;
		for (int i = 0; i < tableViewerDescription.getColumnCount(); i++) {
			Column column = tableViewerDescription.getColumn(i);
			if (column.getConverter() == null) {
				column.setConverter(new IdentityConverter(String.class));
			}
			if (column.getValidator() == null) {
				column.setValidator(new IValidator() {

					public String isPartiallyValid(Object value) {
						return null;
					}

					public String isValid(Object value) {
						return null;
					}
				});
			}
			if (column.getCellEditor() == null) {
				if (defaultCellEditor == null) {
					defaultCellEditor = new TextCellEditor(
							tableViewerDescription.getTableViewer().getTable());
				}
				column.setCellEditor(defaultCellEditor);
			}
		}
		if (tableViewerDescription.getCellModifier() == null) {
			tableViewerDescription.setCellModifier(new ICellModifier() {

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
					return findColumn(property) != null;
				}

				private List getNestedPorperties (String properties) {	
					//TODO jUnit for nested column
					StringTokenizer stk = new StringTokenizer(properties,TableViewerDescription.COLUMN_PROPERTY_NESTING_SEPERATOR);
					List list = new ArrayList(stk.countTokens());
					while(stk.hasMoreTokens())
						list.add(stk.nextElement());
					return list;					
				}
				
				private Object getGetterValue(Object root, List properties) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
					Object result=root;					
					for (int i = 0; i < properties.size(); i++) {
						if (result==null) return null;
						String prop = (String) properties.get(i);
						Method getter = result.getClass().getMethod(
								"get"+ prop.substring(0, 1).toUpperCase(Locale.ENGLISH) + prop.substring(1), new Class[0]); //$NON-NLS-1$
						result=getter.invoke(result, new Object[0]);						
					}
					return result;
				}
				
				public Object getValue(Object element, String property) {
					Column column = findColumn(property);
					if (column == null) {
						return null;
					}
					try {						
						return getGetterValue(element,getNestedPorperties(property));
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
					IValidator columnValidator = column.getValidator();
					if(columnValidator != null){
						String errorMessage = columnValidator.isValid(value);
						if(errorMessage != null){
							validationContext.updateValidationError(new IChangeListener(){
								public void handleChange(IChangeEvent changeEvent) {									
								}								
							},errorMessage);
							return;
						}
					}
					try {
						List getters = getNestedPorperties(property);
						String setterSig;
						Object target;
						if (getters.size()>1) {
							setterSig = (String) getters.get(getters.size()-1);
							getters.remove(getters.size()-1);
							target=getGetterValue(element,getters);
						}
						else {
							setterSig = property;
							target = element;
						}
							
						Method setter = target
								.getClass()
								.getMethod(
										"set"	+ setterSig.substring(0, 1).toUpperCase(Locale.ENGLISH) + setterSig.substring(1), new Class[] { column.getConverter().getModelType() }); //$NON-NLS-1$
						setter.invoke(target, new Object[] { value });
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
			});
		}
	}

	protected Column getColumn(int columnIndex) {
		return tableViewerDescription.getColumn(columnIndex);
	}

}