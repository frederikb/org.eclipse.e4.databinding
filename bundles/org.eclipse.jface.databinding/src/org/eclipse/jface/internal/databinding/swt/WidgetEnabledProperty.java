/*******************************************************************************
 * Copyright (c) 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 264286)
 *******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

/**
 * @deprecated use static methods in WidgetProperties instead (the method names
 *             will start with 'enabled')
 */
// ok to ignore warnings in deprecated class
@SuppressWarnings({ "rawtypes", "unchecked" })
public class WidgetEnabledProperty extends
		WidgetDelegatingValueProperty<Widget, Boolean> {
	IValueProperty<Control, Boolean> control;
	IValueProperty<Menu, Boolean> menu;
	IValueProperty<MenuItem, Boolean> menuItem;
	IValueProperty<ScrollBar, Boolean> scrollBar;
	IValueProperty<ToolItem, Boolean> toolItem;

	/**
	 * 
	 */
	public WidgetEnabledProperty() {
		super(Boolean.TYPE);
	}

	protected IValueProperty doGetDelegate(Widget source) {
		if (source instanceof Control) {
			if (control == null)
				control = new ControlEnabledProperty();
			return control;
		}
		if (source instanceof Menu) {
			if (menu == null)
				menu = new MenuEnabledProperty();
			return menu;
		}
		if (source instanceof MenuItem) {
			if (menuItem == null)
				menuItem = new MenuItemEnabledProperty();
			return menuItem;
		}
		if (source instanceof ScrollBar) {
			if (scrollBar == null)
				scrollBar = new ScrollBarEnabledProperty();
			return scrollBar;
		}
		if (source instanceof ToolItem) {
			if (toolItem == null)
				toolItem = new ToolItemEnabledProperty();
			return toolItem;
		}
		throw notSupported(source);
	}
}