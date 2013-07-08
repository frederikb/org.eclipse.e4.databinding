/*******************************************************************************
 * Copyright (c) 2005, 2009, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matt Carter - bug 170668
 *     Brad Reynolds - bug 170848
 *     Matthew Hall - bugs 180746, 207844, 245647, 248621, 232917, 194734,
 *                    195222, 256543, 213893, 262320, 264286, 266563, 306203
 *     Michael Krauter - bug 180223
 *     Boris Bokowski - bug 245647
 *     Tom Schindl - bug 246462
 *******************************************************************************/
package org.eclipse.jface.databinding.swt;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IVetoableValue;
import org.eclipse.core.databinding.observable.value.ValueChangingEvent;
import org.eclipse.jface.internal.databinding.swt.SWTDelayedObservableValueDecorator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Widget;

/**
 * A factory for creating observables for SWT widgets
 * 
 * @since 1.1
 */
public class SWTObservables {

	private static java.util.List<DisplayRealm> realms = new ArrayList<DisplayRealm>();

	/**
	 * Returns the realm representing the UI thread for the given display.
	 * 
	 * @param display
	 * @return the realm representing the UI thread for the given display
	 */
	public static Realm getRealm(final Display display) {
		synchronized (realms) {
			for (Iterator<DisplayRealm> it = realms.iterator(); it.hasNext();) {
				DisplayRealm displayRealm = it.next();
				if (displayRealm.display == display) {
					return displayRealm;
				}
			}
			DisplayRealm result = new DisplayRealm(display);
			realms.add(result);
			return result;
		}
	}

	/**
	 * Returns an observable which delays notification of value change events
	 * from <code>observable</code> until <code>delay</code> milliseconds have
	 * elapsed since the last change event, or until a FocusOut event is
	 * received from the underlying widget (whichever happens first). This
	 * observable helps to boost performance in situations where an observable
	 * has computationally expensive listeners (e.g. changing filters in a
	 * viewer) or many dependencies (master fields with multiple detail fields).
	 * A common use of this observable is to delay validation of user input
	 * until the user stops typing in a UI field.
	 * <p>
	 * To notify about pending changes, the returned observable fires a stale
	 * event when the wrapped observable value fires a change event, and remains
	 * stale until the delay has elapsed and the value change is fired. A call
	 * to {@link IObservableValue#getValue() getValue()} while a value change is
	 * pending will fire the value change immediately, short-circuiting the
	 * delay.
	 * <p>
	 * Note that this observable will not forward {@link ValueChangingEvent}
	 * events from a wrapped {@link IVetoableValue}.
	 * 
	 * @param delay
	 *            the delay in milliseconds
	 * @param observable
	 *            the observable being delayed
	 * @return an observable which delays notification of value change events
	 *         from <code>observable</code> until <code>delay</code>
	 *         milliseconds have elapsed since the last change event.
	 * 
	 * @since 1.2
	 */
	public static <T> ISWTObservableValue<T> observeDelayedValue(int delay,
			ISWTObservableValue<T> observable) {
		return new SWTDelayedObservableValueDecorator<T>(
				Observables.observeDelayedValue(delay, observable),
				observable.getWidget());
	}

	/**
	 * Returns an observable value tracking the enabled state of the given
	 * widget. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Control</li>
	 * <li>org.eclipse.swt.widgets.Menu</li>
	 * <li>org.eclipse.swt.widgets.MenuItem</li>
	 * <li>org.eclipse.swt.widgets.ScrollBar</li>
	 * <li>org.eclipse.swt.widgets.ToolItem</li>
	 * </ul>
	 * 
	 * @param widget
	 * @return an observable value tracking the enabled state of the given
	 *         widget.
	 * @since 1.5
	 * @deprecated use instead one of the more specific methods
	 *             observeEnabled(Control), observeEnabled(Menu),
	 *             observeEnabled(MenuItem), observeEnabled(ScrollBar),
	 *             observeEnabled(ToolItem)
	 */
	public static ISWTObservableValue<Boolean> observeEnabled(Widget widget) {
		return WidgetProperties.enabled().observe(widget);
	}

	/**
	 * Returns an observable value tracking the enabled state of the given
	 * control
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the enabled state of the given
	 *         control
	 */
	public static ISWTObservableValue<Boolean> observeEnabled(Control control) {
		return WidgetProperties.enabledControl().observe(control);
	}

	/**
	 * Returns an observable value tracking the enabled state of the given
	 * control
	 * 
	 * @param menu
	 *            the control to observe
	 * @return an observable value tracking the enabled state of the given
	 *         control
	 * @since 1.7
	 */
	public static ISWTObservableValue<Boolean> observeEnabled(Menu menu) {
		return WidgetProperties.enabledMenu().observe(menu);
	}

	/**
	 * Returns an observable value tracking the enabled state of the given
	 * control
	 * 
	 * @param menuItem
	 *            the menu item to observe
	 * @return an observable value tracking the enabled state of the given menu
	 *         item
	 * @since 1.7
	 */
	public static ISWTObservableValue<Boolean> observeEnabled(MenuItem menuItem) {
		return WidgetProperties.enabledMenuItem().observe(menuItem);
	}

	/**
	 * Returns an observable value tracking the enabled state of the given
	 * control
	 * 
	 * @param scrollBar
	 *            the scroll bar to observe
	 * @return an observable value tracking the enabled state of the given
	 *         scroll bar
	 * @since 1.7
	 */
	public static ISWTObservableValue<Boolean> observeEnabled(
			ScrollBar scrollBar) {
		return WidgetProperties.enabledScrollBar().observe(scrollBar);
	}

	/**
	 * Returns an observable value tracking the enabled state of the given tool
	 * item
	 * 
	 * @param toolItem
	 *            the control to observe
	 * @return an observable value tracking the enabled state of the given tool
	 *         item
	 * @since 1.7
	 */
	public static ISWTObservableValue<Boolean> observeEnabled(ToolItem toolItem) {
		return WidgetProperties.enabledToolItem().observe(toolItem);
	}

	/**
	 * Returns an observable value tracking the visible state of the given
	 * control
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the visible state of the given
	 *         control
	 */
	public static ISWTObservableValue<Boolean> observeVisible(Control control) {
		return WidgetProperties.visible().observe(control);
	}

	/**
	 * Returns an observable tracking the tooltip text of the given item. The
	 * supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Control</li>
	 * <li>org.eclipse.swt.custom.CTabItem</li>
	 * <li>org.eclipse.swt.widgets.TabItem</li>
	 * <li>org.eclipse.swt.widgets.TableColumn</li>
	 * <li>org.eclipse.swt.widgets.ToolItem</li>
	 * <li>org.eclipse.swt.widgets.TrayItem</li>
	 * <li>org.eclipse.swt.widgets.TreeColumn</li>
	 * </ul>
	 * 
	 * @param widget
	 * @return an observable value tracking the tooltip text of the given item
	 * @since 1.3
	 * @deprecated use instead one of the more specific methods
	 *             observeTooltipText(Control), observeTooltipText(CTabItem),
	 *             observeTooltipText(TabItem), observeTooltipText(TableColumn),
	 *             observeTooltipText(ToolItem), observeTooltipText(TrayItem),
	 *             observeTooltipText(TreeColumn)
	 */
	public static ISWTObservableValue<Boolean> observeTooltipText(Widget widget) {
		return WidgetProperties.tooltipText().observe(widget);
	}

	/**
	 * Returns an observable value tracking the tool-tip text of the given
	 * control
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the tool-tip text of the given
	 *         control
	 */
	public static ISWTObservableValue<String> observeTooltipText(Control control) {
		return WidgetProperties.tooltipControl().observe(control);
	}

	/**
	 * Returns an observable value tracking the tool-tip text of the given
	 * custom tab item
	 * 
	 * @param cTabItem
	 *            the control to observe
	 * @return an observable value tracking the tool-tip text of the given
	 *         custom tab item
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeTooltipText(
			CTabItem cTabItem) {
		return WidgetProperties.tooltipCTabItem().observe(cTabItem);
	}

	/**
	 * Returns an observable value tracking the tool-tip text of the given tab
	 * item
	 * 
	 * @param tabItem
	 *            the control to observe
	 * @return an observable value tracking the tool-tip text of the given tab
	 *         item
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeTooltipText(TabItem tabItem) {
		return WidgetProperties.tooltipTabItem().observe(tabItem);
	}

	/**
	 * Returns an observable value tracking the tool-tip text of the given table
	 * column
	 * 
	 * @param tableColumn
	 *            the control to observe
	 * @return an observable value tracking the tool-tip text of the given table
	 *         column
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeTooltipText(
			TableColumn tableColumn) {
		return WidgetProperties.tooltipTableColumn().observe(tableColumn);
	}

	/**
	 * Returns an observable value tracking the tool-tip text of the given tool
	 * item
	 * 
	 * @param toolItem
	 *            the control to observe
	 * @return an observable value tracking the tool-tip text of the given tool
	 *         item
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeTooltipText(
			ToolItem toolItem) {
		return WidgetProperties.tooltipToolItem().observe(toolItem);
	}

	/**
	 * Returns an observable value tracking the tool-tip text of the given tray
	 * item
	 * 
	 * @param trayItem
	 *            the control to observe
	 * @return an observable value tracking the tool-tip text of the given tray
	 *         item
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeTooltipText(
			TrayItem trayItem) {
		return WidgetProperties.tooltipTrayItem().observe(trayItem);
	}

	/**
	 * Returns an observable value tracking the tool-tip text of the given tree
	 * column
	 * 
	 * @param treeColumn
	 *            the control to observe
	 * @return an observable value tracking the tool-tip text of the given tree
	 *         column
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeTooltipText(
			TreeColumn treeColumn) {
		return WidgetProperties.tooltipTreeColumn().observe(treeColumn);
	}

	/**
	 * Returns an observable observing the selection attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Spinner</li>
	 * <li>org.eclipse.swt.widgets.Button</li>
	 * <li>org.eclipse.swt.widgets.Combo</li>
	 * <li>org.eclipse.swt.custom.CCombo</li>
	 * <li>org.eclipse.swt.widgets.List</li>
	 * <li>org.eclipse.swt.widgets.MenuItem (since 1.5)</li>
	 * <li>org.eclipse.swt.widgets.Scale</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 * @deprecated use instead one of the more specific methods
	 *             (observeSelection(Button), observeSelection(Combo) etc)
	 */
	// It's ok to supress warnings on deprecated methods
	@SuppressWarnings("unchecked")
	public static ISWTObservableValue<?> observeSelection(Widget control) {
		return WidgetProperties.selection().observe(control);
	}

	/**
	 * Returns an observable observing the selection attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Spinner</li>
	 * <li>org.eclipse.swt.widgets.Button</li>
	 * <li>org.eclipse.swt.widgets.Combo</li>
	 * <li>org.eclipse.swt.custom.CCombo</li>
	 * <li>org.eclipse.swt.widgets.List</li>
	 * <li>org.eclipse.swt.widgets.MenuItem (since 1.5)</li>
	 * <li>org.eclipse.swt.widgets.Scale</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 * @deprecated use instead one of the more specific methods
	 *             (observeSelection(Button), observeSelection(Combo) etc)
	 */
	// It's ok to supress warnings on deprecated methods
	@SuppressWarnings("unchecked")
	public static ISWTObservableValue<?> observeSelection(Control control) {
		return WidgetProperties.selection().observe(control);
	}

	/**
	 * Returns an observable observing the selection attribute of the provided
	 * <code>org.eclipse.swt.widgets.Button</code>.
	 * 
	 * @param control
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<Boolean> observeSelection(Button control) {
		return WidgetProperties.selectionButton().observe(control);
	}

	/**
	 * Returns an observable observing the selection attribute of the provided
	 * <code>org.eclipse.swt.widgets.Combo</code>.
	 * 
	 * @param control
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeSelection(Combo control) {
		return WidgetProperties.selectionCombo().observe(control);
	}

	/**
	 * Returns an observable observing the selection attribute of the provided
	 * <code>org.eclipse.swt.custom.CCombo</code>.
	 * 
	 * @param control
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeSelection(CCombo control) {
		return WidgetProperties.selectionCCombo().observe(control);
	}

	/**
	 * Returns an observable observing the selection attribute of the provided
	 * <code>org.eclipse.swt.widgets.List</code>.
	 * 
	 * @param control
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeSelection(List control) {
		return WidgetProperties.selectionList().observe(control);
	}

	/**
	 * Returns an observable observing the selection attribute of the provided
	 * <code>org.eclipse.swt.widgets.MenuItem</code>.
	 * 
	 * @param control
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<Boolean> observeSelection(MenuItem control) {
		return WidgetProperties.selectionMenuItem().observe(control);
	}

	/**
	 * Returns an observable observing the selection attribute of the provided
	 * <code>org.eclipse.swt.widgets.Scale</code>.
	 * 
	 * @param control
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<Integer> observeSelection(Scale control) {
		return WidgetProperties.selectionScale().observe(control);
	}

	/**
	 * Returns an observable observing the selection attribute of the provided
	 * <code>org.eclipse.swt.widgets.Spinner</code>.
	 * 
	 * @param control
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<Integer> observeSelection(Spinner control) {
		return WidgetProperties.selectionSpinner().observe(control);
	}

	/**
	 * Returns an observable observing the minimum attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Spinner</li>
	 * <li>org.eclipse.swt.widgets.Slider (since 1.5)</li>
	 * <li>org.eclipse.swt.widgets.Scale</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 * @deprecated use instead one of the more specific methods
	 *             (observeMin(Spinner), observeMin(Slider), observeMin(Scale))
	 */
	// ok to ignore warnings in deprecated method
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ISWTObservableValue observeMin(Control control) {
		return WidgetProperties.minimum().observe(control);
	}

	/**
	 * Returns an observable observing the <code>minimum</code> attribute of the
	 * provided <code>org.eclipse.swt.widgets.Scale</code>.
	 * 
	 * @param control
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<Integer> observeMin(Scale control) {
		return WidgetProperties.minimumScale().observe(control);
	}

	/**
	 * Returns an observable observing the <code>minimum</code> attribute of the
	 * provided <code>org.eclipse.swt.widgets.Scale</code>.
	 * 
	 * @param control
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<Integer> observeMin(Slider control) {
		return WidgetProperties.minimumSlider().observe(control);
	}

	/**
	 * Returns an observable observing the <code>minimum</code> attribute of the
	 * provided <code>org.eclipse.swt.widgets.Spinner</code>.
	 * 
	 * @param control
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<Integer> observeMin(Spinner control) {
		return WidgetProperties.minimumSpinner().observe(control);
	}

	/**
	 * Returns an observable observing the maximum attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Spinner</li>
	 * <li>org.eclipse.swt.widgets.Slider (since 1.5)</li>
	 * <li>org.eclipse.swt.widgets.Scale</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 * @deprecated use instead one of the more specific methods
	 *             (observeMax(Spinner), observeMax(Slider), observeMax(Scale))
	 */
	// ok to ignore warnings in deprecated method
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ISWTObservableValue observeMax(Control control) {
		return WidgetProperties.maximum().observe(control);
	}

	/**
	 * Returns an observable observing the <code>maximum</code> attribute of the
	 * provided <code>org.eclipse.swt.widgets.Scale</code>.
	 * 
	 * @param control
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<Integer> observeMax(Scale control) {
		return WidgetProperties.maximumScale().observe(control);
	}

	/**
	 * Returns an observable observing the <code>maximum</code> attribute of the
	 * provided <code>org.eclipse.swt.widgets.Scale</code>.
	 * 
	 * @param control
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<Integer> observeMax(Slider control) {
		return WidgetProperties.maximumSlider().observe(control);
	}

	/**
	 * Returns an observable observing the <code>maximum</code> attribute of the
	 * provided <code>org.eclipse.swt.widgets.Spinner</code>.
	 * 
	 * @param control
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<Integer> observeMax(Spinner control) {
		return WidgetProperties.maximumSpinner().observe(control);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Text</li>
	 * <li>org.eclipse.swt.custom.StyledText (as of 1.3)</li>
	 * </ul>
	 * 
	 * @param control
	 * @param events
	 *            array of SWT event types to register for change events. May
	 *            include {@link SWT#None}, {@link SWT#Modify},
	 *            {@link SWT#FocusOut} or {@link SWT#DefaultSelection}.
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 * @since 1.3
	 * @deprecated use instead one of the more specific methods
	 *             (observeText(Text), observeText(StyledText))
	 */
	// ok to ignore warnings in deprecated method
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ISWTObservableValue observeText(Control control, int[] events) {
		return WidgetProperties.text(events).observe(control);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>Text</code>.
	 * 
	 * @param control
	 * @param events
	 *            array of SWT event types to register for change events. May
	 *            include {@link SWT#None}, {@link SWT#Modify},
	 *            {@link SWT#FocusOut} or {@link SWT#DefaultSelection}.
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeText(Text control,
			int[] events) {
		return WidgetProperties.textText(events).observe(control);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>StyledText</code>.
	 * 
	 * @param control
	 * @param events
	 *            array of SWT event types to register for change events. May
	 *            include {@link SWT#None}, {@link SWT#Modify},
	 *            {@link SWT#FocusOut} or {@link SWT#DefaultSelection}.
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeText(StyledText control,
			int[] events) {
		return WidgetProperties.textStyledText(events).observe(control);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Text</li>
	 * <li>org.eclipse.swt.custom.StyledText (as of 1.3)</li>
	 * </ul>
	 * 
	 * @param control
	 * @param event
	 *            event type to register for change events
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 * @deprecated use instead one of the more specific methods
	 *             observeText(Text), observeText(Styled)
	 */
	// It's ok to suppress warnings on deprecated methods
	@SuppressWarnings("unchecked")
	public static ISWTObservableValue<String> observeText(Control control,
			int event) {
		return WidgetProperties.text(event).observe(control);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>org.eclipse.swt.widgets.Text</code>.
	 * 
	 * @param control
	 * @param event
	 *            event type to register for change events
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeText(Text control,
			int event) {
		return WidgetProperties.textText(event).observe(control);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>org.eclipse.swt.custom.StyledText</code>.
	 * 
	 * @param control
	 * @param event
	 *            event type to register for change events
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeText(StyledText control,
			int event) {
		return WidgetProperties.textStyledText(event).observe(control);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>widget</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Button (as of 1.3)</li>
	 * <li>org.eclipse.swt.custom.CCombo</li>
	 * <li>org.eclipse.swt.custom.CLabel</li>
	 * <li>org.eclipse.swt.widgets.Combo</li>
	 * <li>org.eclipse.swt.widgets.Item</li>
	 * <li>org.eclipse.swt.widgets.Label</li>
	 * <li>org.eclipse.swt.widgets.Link (as of 1.2)</li>
	 * <li>org.eclipse.swt.widgets.Shell</li>
	 * <li>org.eclipse.swt.widgets.StyledText (as of 1.3)</li>
	 * <li>org.eclipse.swt.widgets.Text (as of 1.3)</li>
	 * </ul>
	 * 
	 * @param widget
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if the type of <code>widget</code> is unsupported
	 * @since 1.3
	 * @deprecated use instead one of the more specific methods
	 *             observeText(Button), observeText(CCombo) etc
	 */
	// It's ok to suppress warnings on deprecated methods
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ISWTObservableValue observeText(Widget widget) {
		return WidgetProperties.text().observe(widget);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>Button</code>.
	 * 
	 * @param widget
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeText(Button widget) {
		return WidgetProperties.textButton().observe(widget);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>CCombo</code>.
	 * 
	 * @param widget
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeText(CCombo widget) {
		return WidgetProperties.textCCombo().observe(widget);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>CLabel</code>.
	 * 
	 * @param widget
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeText(CLabel widget) {
		return WidgetProperties.textCLabel().observe(widget);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>Combo</code>.
	 * 
	 * @param widget
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeText(Combo widget) {
		return WidgetProperties.textCombo().observe(widget);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>Item</code>.
	 * 
	 * @param widget
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeText(Item widget) {
		return WidgetProperties.textItem().observe(widget);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>Label</code>.
	 * 
	 * @param widget
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeText(Label widget) {
		return WidgetProperties.textLabel().observe(widget);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>Link</code>.
	 * 
	 * @param widget
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeText(Link widget) {
		return WidgetProperties.textLink().observe(widget);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>Shell</code>.
	 * 
	 * @param widget
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeText(Shell widget) {
		return WidgetProperties.textShell().observe(widget);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>StyledText</code>.
	 * 
	 * @param widget
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeText(StyledText widget) {
		return WidgetProperties.textStyledText().observe(widget);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>Text</code>.
	 * 
	 * @param widget
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeText(Text widget) {
		return WidgetProperties.textText().observe(widget);
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Button (as of 1.3)</li>
	 * <li>org.eclipse.swt.custom.CCombo</li>
	 * <li>org.eclipse.swt.custom.CLabel</li>
	 * <li>org.eclipse.swt.widgets.Combo</li>
	 * <li>org.eclipse.swt.widgets.Label</li>
	 * <li>org.eclipse.swt.widgets.Link (as of 1.2)</li>
	 * <li>org.eclipse.swt.widgets.Shell</li>
	 * <li>org.eclipse.swt.custom.StyledText (as of 1.3)</li>
	 * <li>org.eclipse.swt.widgets.Text (as of 1.3)</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 * @deprecated use instead one of the more specific methods
	 *             observeText(Button), observeText(CCombo) etc
	 */
	// It's ok to suppress warnings on deprecated methods
	@SuppressWarnings({ "rawtypes" })
	public static ISWTObservableValue observeText(Control control) {
		return observeText((Widget) control);
	}

	/**
	 * Returns an observable observing the message attribute of the provided
	 * <code>widget</code>. the supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Text</li>
	 * <li>org.eclipse.swt.widgets.ToolTip</li>
	 * <ul>
	 * 
	 * @param widget
	 * @return an observable observing the message attribute of the provided
	 *         <code>widget</code>.
	 * @since 1.3
	 * @deprecated use instead one of the more specific methods
	 *             observeMessage(Text), observeMessage(ToolTip)
	 */
	// It's ok to suppress warnings on deprecated methods
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ISWTObservableValue observeMessage(Widget widget) {
		return WidgetProperties.message().observe(widget);
	}

	/**
	 * Returns an observable observing the message attribute of the provided
	 * <code>Text</code>.
	 * 
	 * @param widget
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeMessage(Text widget) {
		return WidgetProperties.messageText().observe(widget);
	}

	/**
	 * Returns an observable observing the message attribute of the provided
	 * <code>Text</code>.
	 * 
	 * @param widget
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<String> observeMessage(ToolTip widget) {
		return WidgetProperties.messageToolTip().observe(widget);
	}

	/**
	 * Returns an observable observing the image attribute of the provided
	 * <code>widget</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Button</li>
	 * <li>org.eclipse.swt.custom.CLabel</li>
	 * <li>org.eclipse.swt.widgets.Item</li>
	 * <li>org.eclipse.swt.widgets.Label</li>
	 * </ul>
	 * 
	 * @param widget
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>widget</code> type is unsupported
	 * @since 1.3
	 * @deprecated use instead one of the more specific methods
	 *             observeImage(Button), observeImage(CLabel),
	 *             observeImage(Item), observeImage(Label)
	 */
	// It's ok to suppress warnings on deprecated methods
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ISWTObservableValue observeImage(Widget widget) {
		return WidgetProperties.image().observe(widget);
	}

	/**
	 * Returns an observable observing the image attribute of the provided
	 * <code>Button</code>.
	 * 
	 * @param widget
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<Image> observeImage(Button widget) {
		return WidgetProperties.imageButton().observe(widget);
	}

	/**
	 * Returns an observable observing the image attribute of the provided
	 * <code>CLabel</code>.
	 * 
	 * @param widget
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<Image> observeImage(CLabel widget) {
		return WidgetProperties.imageCLabel().observe(widget);
	}

	/**
	 * Returns an observable observing the image attribute of the provided
	 * <code>Item</code>.
	 * 
	 * @param widget
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<Image> observeImage(Item widget) {
		return WidgetProperties.imageItem().observe(widget);
	}

	/**
	 * Returns an observable observing the image attribute of the provided
	 * <code>Label</code>.
	 * 
	 * @param widget
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<Image> observeImage(Label widget) {
		return WidgetProperties.imageLabel().observe(widget);
	}

	/**
	 * Returns an observable observing the items attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Combo</li>
	 * <li>org.eclipse.swt.custom.CCombo</li>
	 * <li>org.eclipse.swt.widgets.List</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable list
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 * @deprecated use instead one of the more specific methods
	 *             observeItems(CCombo), observeItems(Combo), observeItems(List)
	 */
	// It's ok to suppress warnings on deprecated methods
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static IObservableList observeItems(Control control) {
		return WidgetProperties.items().observe(control);
	}

	/**
	 * Returns an observable observing the items attribute of the provided
	 * <code>CCombo</code>.
	 * 
	 * @param widget
	 * @return observable list of items
	 * @since 1.7
	 */
	public static ISWTObservableList<String> observeItems(CCombo widget) {
		return WidgetProperties.itemsCCombo().observe(widget);
	}

	/**
	 * Returns an observable observing the items attribute of the provided
	 * <code>Combo</code>.
	 * 
	 * @param widget
	 * @return observable list of items
	 * @since 1.7
	 */
	public static ISWTObservableList<String> observeItems(Combo widget) {
		return WidgetProperties.itemsCombo().observe(widget);
	}

	/**
	 * Returns an observable observing the items attribute of the provided
	 * <code>List</code>.
	 * 
	 * @param widget
	 * @return observable list of items
	 * @since 1.7
	 */
	public static ISWTObservableList<String> observeItems(List widget) {
		return WidgetProperties.itemsList().observe(widget);
	}

	/**
	 * Returns an observable observing the single selection index attribute of
	 * the provided <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Table</li>
	 * <li>org.eclipse.swt.widgets.Combo</li>
	 * <li>org.eclipse.swt.custom.CCombo</li>
	 * <li>org.eclipse.swt.widgets.List</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 * @deprecated use instead one of the more specific methods
	 *             observeSingleSelection(CCombo),
	 *             observeSingleSelection(Combo), observeSingleSelection(List),
	 *             observeSingleSelection(Table)
	 */
	// It's ok to suppress warnings on deprecated methods
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ISWTObservableValue observeSingleSelectionIndex(
			Control control) {
		return WidgetProperties.singleSelectionIndex().observe(control);
	}

	/**
	 * Returns an observable observing the selection index of the provided
	 * <code>CCombo</code>.
	 * 
	 * @param widget
	 * @return observable value of the integer index
	 * @since 1.7
	 */
	public static ISWTObservableValue<Integer> observeSingleSelection(
			CCombo widget) {
		return WidgetProperties.singleSelectionIndexCCombo().observe(widget);
	}

	/**
	 * Returns an observable observing the selection index of the provided
	 * <code>Combo</code>.
	 * 
	 * @param widget
	 * @return observable value of the integer index
	 * @since 1.7
	 */
	public static ISWTObservableValue<Integer> observeSingleSelection(
			Combo widget) {
		return WidgetProperties.singleSelectionIndexCombo().observe(widget);
	}

	/**
	 * Returns an observable observing the selection index of the provided
	 * <code>List</code>.
	 * 
	 * @param widget
	 * @return observable value of the integer index
	 * @since 1.7
	 */
	public static ISWTObservableValue<Integer> observeSingleSelection(
			List widget) {
		return WidgetProperties.singleSelectionIndexList().observe(widget);
	}

	/**
	 * Returns an observable observing the selection index of the provided
	 * <code>Table</code>.
	 * 
	 * @param widget
	 * @return observable value of the integer index
	 * @since 1.7
	 */
	public static ISWTObservableValue<Integer> observeSingleSelection(
			Table widget) {
		return WidgetProperties.singleSelectionIndexTable().observe(widget);
	}

	/**
	 * Returns an observable value tracking the foreground color of the given
	 * control
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the foreground color of the given
	 *         control
	 */
	public static ISWTObservableValue<Color> observeForeground(Control control) {
		return WidgetProperties.foreground().observe(control);
	}

	/**
	 * Returns an observable value tracking the background color of the given
	 * control
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the background color of the given
	 *         control
	 */
	public static ISWTObservableValue<Color> observeBackground(Control control) {
		return WidgetProperties.background().observe(control);
	}

	/**
	 * Returns an observable value tracking the font of the given control.
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the font of the given control
	 */
	public static ISWTObservableValue<Font> observeFont(Control control) {
		return WidgetProperties.font().observe(control);
	}

	/**
	 * Returns an observable value tracking the size of the given control.
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the size of the given control
	 * @since 1.3
	 */
	public static ISWTObservableValue<Point> observeSize(Control control) {
		return WidgetProperties.size().observe(control);
	}

	/**
	 * Returns an observable value tracking the location of the given control.
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the location of the given control
	 * @since 1.3
	 */
	public static ISWTObservableValue<Point> observeLocation(Control control) {
		return WidgetProperties.location().observe(control);
	}

	/**
	 * Returns an observable value tracking the focus of the given control.
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the focus of the given control
	 * @since 1.3
	 */
	public static ISWTObservableValue<Boolean> observeFocus(Control control) {
		return WidgetProperties.focused().observe(control);
	}

	/**
	 * Returns an observable value tracking the bounds of the given control.
	 * 
	 * @param control
	 *            the control to observe
	 * @return an observable value tracking the bounds of the given control
	 * @since 1.3
	 */
	public static ISWTObservableValue<Rectangle> observeBounds(Control control) {
		return WidgetProperties.bounds().observe(control);
	}

	/**
	 * Returns an observable observing the editable attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.custom.CCombo (since 1.6)</li>
	 * <li>org.eclipse.swt.custom.StyledText (since 1.6)</li>
	 * <li>org.eclipse.swt.widgets.Text</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 * @deprecated use instead one of the more specific methods
	 *             observeEditable(CCombo), observeEditable(StyledText),
	 *             observeEditable(Text)
	 */
	// It's ok to suppress warnings on deprecated methods
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ISWTObservableValue observeEditable(Control control) {
		return WidgetProperties.editable().observe(control);
	}

	/**
	 * Returns an observable observing the editable attribute of the provided
	 * <code>CCombo</code>.
	 * 
	 * @param control
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<Boolean> observeEditable(CCombo control) {
		return WidgetProperties.editableCCombo().observe(control);
	}

	/**
	 * Returns an observable observing the editable attribute of the provided
	 * <code>StyledText</code>.
	 * 
	 * @param control
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<Boolean> observeEditable(
			StyledText control) {
		return WidgetProperties.editableStyledText().observe(control);
	}

	/**
	 * Returns an observable observing the editable attribute of the provided
	 * <code>Text</code>.
	 * 
	 * @param control
	 * @return observable value
	 * @since 1.7
	 */
	public static ISWTObservableValue<Boolean> observeEditable(Text control) {
		return WidgetProperties.editableText().observe(control);
	}

	private static class DisplayRealm extends Realm {
		private Display display;

		/**
		 * @param display
		 */
		private DisplayRealm(Display display) {
			this.display = display;
		}

		public boolean isCurrent() {
			return Display.getCurrent() == display;
		}

		public void asyncExec(final Runnable runnable) {
			Runnable safeRunnable = new Runnable() {
				public void run() {
					safeRun(runnable);
				}
			};
			if (!display.isDisposed()) {
				display.asyncExec(safeRunnable);
			}
		}

		public void timerExec(int milliseconds, final Runnable runnable) {
			if (!display.isDisposed()) {
				Runnable safeRunnable = new Runnable() {
					public void run() {
						safeRun(runnable);
					}
				};
				display.timerExec(milliseconds, safeRunnable);
			}
		}

		public int hashCode() {
			return (display == null) ? 0 : display.hashCode();
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final DisplayRealm other = (DisplayRealm) obj;
			if (display == null) {
				if (other.display != null)
					return false;
			} else if (!display.equals(other.display))
				return false;
			return true;
		}
	}
}
