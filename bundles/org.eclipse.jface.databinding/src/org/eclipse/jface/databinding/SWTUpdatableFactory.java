/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.jface.databinding;

import java.util.Map;

import org.eclipse.jface.databinding.internal.swt.ButtonUpdatableValue;
import org.eclipse.jface.databinding.internal.swt.CComboUpdatableCollection;
import org.eclipse.jface.databinding.internal.swt.CComboUpdatableValue;
import org.eclipse.jface.databinding.internal.swt.ComboUpdatableCollection;
import org.eclipse.jface.databinding.internal.swt.ComboUpdatableValue;
import org.eclipse.jface.databinding.internal.swt.ControlUpdatableValue;
import org.eclipse.jface.databinding.internal.swt.LabelUpdatableValue;
import org.eclipse.jface.databinding.internal.swt.ListUpdatableCollection;
import org.eclipse.jface.databinding.internal.swt.ListUpdatableValue;
import org.eclipse.jface.databinding.internal.swt.SpinnerUpdatableValue;
import org.eclipse.jface.databinding.internal.swt.TableUpdatableValue;
import org.eclipse.jface.databinding.internal.swt.TextUpdatableValue;
import org.eclipse.jface.databinding.internal.viewers.StructuredViewerUpdatableValue;
import org.eclipse.jface.databinding.internal.viewers.TableViewerUpdatableCollection;
import org.eclipse.jface.databinding.internal.viewers.TableViewerUpdatableCollectionExtended;
import org.eclipse.jface.databinding.internal.viewers.UpdatableCollectionViewer;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * @since 3.2
 *
 */
final public class SWTUpdatableFactory implements IUpdatableFactory {

	public IUpdatable createUpdatable(Map properties,
			Object description, IValidationContext validationContext) {
		if (description instanceof PropertyDescription) {
			Object object = ((PropertyDescription) description)
					.getObject();
			Object attribute = ((PropertyDescription) description)
					.getPropertyID();
			if (object instanceof Control
					&& SWTProperties.ENABLED.equals(attribute)) {
				return new ControlUpdatableValue((Control) object,
						(String) attribute);
			}
			if (object instanceof Spinner
					&& SWTProperties.SELECTION.equals(attribute)
					|| SWTProperties.MIN.equals(attribute)
					|| SWTProperties.MAX.equals(attribute)) {
				return new SpinnerUpdatableValue((Spinner) object,
						(String) attribute);
			}
			if (object instanceof Text
					&& SWTProperties.TEXT.equals(attribute)) {
				return new TextUpdatableValue((Text) object,
						SWT.Modify, SWT.Modify);
			}
			if (object instanceof Label
					&& SWTProperties.TEXT.equals(attribute)) {
				return new LabelUpdatableValue((Label) object);
			}
			if (object instanceof Button
					&& SWTProperties.SELECTION.equals(attribute)) {
				return new ButtonUpdatableValue((Button) object,
						SWT.Selection);
			}
			if (object instanceof Combo
					&& (SWTProperties.TEXT.equals(attribute) || SWTProperties.SELECTION
							.equals(attribute))) {
				return new ComboUpdatableValue((Combo) object,
						(String) attribute);
			} else if (object instanceof Combo
					&& SWTProperties.ITEMS.equals(attribute)) {
				return new ComboUpdatableCollection((Combo) object,
						(String) attribute);
			}
			if (object instanceof CCombo
					&& (SWTProperties.TEXT.equals(attribute) || SWTProperties.SELECTION
							.equals(attribute))) {
				return new CComboUpdatableValue((CCombo) object,
						(String) attribute);
			} else if (object instanceof CCombo
					&& SWTProperties.ITEMS.equals(attribute)) {
				return new CComboUpdatableCollection((CCombo) object,
						(String) attribute);
			}
			if (object instanceof List
					&& SWTProperties.SELECTION.equals(attribute)) {
				// SWT.SINGLE selection only
				return new ListUpdatableValue((List) object,
						(String) attribute);
			} else if (object instanceof List
					&& SWTProperties.ITEMS.equals(attribute)) {
				return new ListUpdatableCollection((List) object,
						(String) attribute);
			}
			if (object instanceof StructuredViewer
					&& SWTProperties.SELECTION.equals(attribute)) {
				return new StructuredViewerUpdatableValue(
						(StructuredViewer) object, (String) attribute);
			}
			if (object instanceof AbstractListViewer
					&& SWTProperties.SELECTION.equals(attribute))
				return new StructuredViewerUpdatableValue(
						(AbstractListViewer) object, (String) attribute);
			else if (object instanceof AbstractListViewer
					&& ViewersProperties.CONTENT.equals(attribute))
				return new UpdatableCollectionViewer(
						(AbstractListViewer) object);
			if (object instanceof TableViewer
					&& ViewersProperties.CONTENT.equals(attribute)) {
				return new TableViewerUpdatableCollection(
						(TableViewer) object);
			}
			if (object instanceof Table) {
				return new TableUpdatableValue((Table) object,
						(String) attribute);
			}
		}
		if (description instanceof AbstractListViewer) {
			// binding to a Viewer directly implies binding to its
			// content
			return new UpdatableCollectionViewer(
					(AbstractListViewer) description);
		} else if (description instanceof Text) {
			int validatePolicy = getPolicy(properties,
					IDataBindingContext.VALIDATION_TIME, SWT.Modify);
			int updatePolicy = getPolicy(properties,
					IDataBindingContext.UPDATE_TIME, SWT.FocusOut);
			return new TextUpdatableValue((Text) description,
					validatePolicy, updatePolicy);
		} else if (description instanceof Button) {
			int updatePolicy = getPolicy(properties,
					IDataBindingContext.UPDATE_TIME, SWT.FocusOut);
			return new ButtonUpdatableValue((Button) description,
					updatePolicy);
		} else if (description instanceof Combo) {
			return new ComboUpdatableCollection((Combo) description,
					ViewersProperties.CONTENT);
		} else if (description instanceof Spinner) {
			return new SpinnerUpdatableValue((Spinner) description,
					SWTProperties.SELECTION);
		} else if (description instanceof CCombo) {
			return new CComboUpdatableCollection((CCombo) description,
					ViewersProperties.CONTENT);
		} else if (description instanceof List) {
			return new ListUpdatableCollection((List) description,
					ViewersProperties.CONTENT);
		} else if (description instanceof TableViewerDescription) {
			return new TableViewerUpdatableCollectionExtended(
					(TableViewerDescription) description,
					validationContext);
		}
		return null;
	}

	private int getPolicy(Map properties, String propertyName,
			int defaultPolicy) {
		int policy = defaultPolicy;
		Integer time = (Integer) properties.get(propertyName);
		if (time != null) {
			switch (time.intValue()) {
			case IDataBindingContext.TIME_EARLY:
				policy = SWT.Modify;
				break;
			case IDataBindingContext.TIME_LATE:
				policy = SWT.FocusOut;
				break;
			case IDataBindingContext.TIME_NEVER:
				policy = SWT.None;
				break;
			}
		}
		return policy;
	}
}