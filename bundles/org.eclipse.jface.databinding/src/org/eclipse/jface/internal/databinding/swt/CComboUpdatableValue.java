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
package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.jface.internal.provisional.databinding.ChangeEvent;
import org.eclipse.jface.internal.provisional.databinding.UpdatableValue;
import org.eclipse.jface.internal.provisional.databinding.swt.SWTProperties;
import org.eclipse.jface.internal.provisional.databinding.viewers.ViewersProperties;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

/**
 * @since 3.2
 * 
 */
public class CComboUpdatableValue extends UpdatableValue {

	/**
	 * 
	 */

	private final CCombo ccombo;

	private final String attribute;

	private boolean updating = false;

	/**
	 * @param ccombo
	 * @param attribute
	 */
	public CComboUpdatableValue(CCombo ccombo, String attribute) {
		this.ccombo = ccombo;
		this.attribute = attribute;
		if (attribute.equals(ViewersProperties.CONTENT))
			attribute = SWTProperties.TEXT;

		if (attribute.equals(SWTProperties.SELECTION) || 
			attribute.equals(SWTProperties.TEXT)) {
			ccombo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (!updating) {
						fireChangeEvent(ChangeEvent.CHANGE, null, null);
					}
				}
			});
		}
		else
			throw new IllegalArgumentException();
	}

	public void setValue(final Object value) {
		AsyncRunnable runnable = new AsyncRunnable() {
			public void run() {
				String oldValue = ccombo.getText();
				try {
					updating = true;
					if (attribute.equals(SWTProperties.TEXT)) {
						String stringValue = value != null ? value.toString()
								: ""; //$NON-NLS-1$
						ccombo.setText(stringValue);
					} else if (attribute.equals(SWTProperties.SELECTION)) {
						String items[] = ccombo.getItems();
						int index = -1;
						if (items != null && value != null) {
							for (int i = 0; i < items.length; i++) {
								if (value.equals(items[i])) {
									index = i;
									break;
								}
							}
							if (index == -1) {
								ccombo.setText((String) value);
							} else {
								ccombo.select(index); // -1 will not "unselect"
							}
						}
					}
				} finally {
					updating = false;
				}
				fireChangeEvent(ChangeEvent.CHANGE, oldValue, ccombo.getText());
			}
		};
		runnable.runOn(ccombo.getDisplay());
	}

	public Object computeValue() {
		SyncRunnable runnable = new SyncRunnable() {
			public Object run() {
				if (attribute.equals(SWTProperties.TEXT)) 
					return ccombo.getText();
				
				Assert.isTrue(attribute.equals(SWTProperties.SELECTION),
						"unexpected attribute: " + attribute); //$NON-NLS-1$
				// The problem with a ccombo, is that it changes the text and
				// fires before it update its selection index
				return ccombo.getText();
			}
		};
		return runnable.run();
	}

	public Class getValueType() {
		Assert.isTrue(attribute.equals(SWTProperties.TEXT)
				|| attribute.equals(SWTProperties.SELECTION),
				"unexpected attribute: " + attribute); //$NON-NLS-1$
		return String.class;
	}

}
