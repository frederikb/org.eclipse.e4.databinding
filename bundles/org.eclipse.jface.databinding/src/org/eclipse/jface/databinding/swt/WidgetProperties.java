/*******************************************************************************
 * Copyright (c) 2008, 2009, 2011 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bugs 256543, 213893, 262320, 262946, 264286, 266563,
 *                    169876, 306203
 ******************************************************************************/

package org.eclipse.jface.databinding.swt;

import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.internal.databinding.swt.ButtonImageProperty;
import org.eclipse.jface.internal.databinding.swt.ButtonSelectionProperty;
import org.eclipse.jface.internal.databinding.swt.ButtonTextProperty;
import org.eclipse.jface.internal.databinding.swt.CComboEditableProperty;
import org.eclipse.jface.internal.databinding.swt.CComboItemsProperty;
import org.eclipse.jface.internal.databinding.swt.CComboSelectionProperty;
import org.eclipse.jface.internal.databinding.swt.CComboSingleSelectionIndexProperty;
import org.eclipse.jface.internal.databinding.swt.CComboTextProperty;
import org.eclipse.jface.internal.databinding.swt.CLabelImageProperty;
import org.eclipse.jface.internal.databinding.swt.CLabelTextProperty;
import org.eclipse.jface.internal.databinding.swt.CTabItemTooltipTextProperty;
import org.eclipse.jface.internal.databinding.swt.ComboItemsProperty;
import org.eclipse.jface.internal.databinding.swt.ComboSelectionProperty;
import org.eclipse.jface.internal.databinding.swt.ComboSingleSelectionIndexProperty;
import org.eclipse.jface.internal.databinding.swt.ComboTextProperty;
import org.eclipse.jface.internal.databinding.swt.ControlBackgroundProperty;
import org.eclipse.jface.internal.databinding.swt.ControlBoundsProperty;
import org.eclipse.jface.internal.databinding.swt.ControlEnabledProperty;
import org.eclipse.jface.internal.databinding.swt.ControlFocusedProperty;
import org.eclipse.jface.internal.databinding.swt.ControlFontProperty;
import org.eclipse.jface.internal.databinding.swt.ControlForegroundProperty;
import org.eclipse.jface.internal.databinding.swt.ControlLocationProperty;
import org.eclipse.jface.internal.databinding.swt.ControlSizeProperty;
import org.eclipse.jface.internal.databinding.swt.ControlTooltipTextProperty;
import org.eclipse.jface.internal.databinding.swt.ControlVisibleProperty;
import org.eclipse.jface.internal.databinding.swt.ItemImageProperty;
import org.eclipse.jface.internal.databinding.swt.ItemTextProperty;
import org.eclipse.jface.internal.databinding.swt.LabelImageProperty;
import org.eclipse.jface.internal.databinding.swt.LabelTextProperty;
import org.eclipse.jface.internal.databinding.swt.LinkTextProperty;
import org.eclipse.jface.internal.databinding.swt.ListItemsProperty;
import org.eclipse.jface.internal.databinding.swt.ListSelectionProperty;
import org.eclipse.jface.internal.databinding.swt.ListSingleSelectionIndexProperty;
import org.eclipse.jface.internal.databinding.swt.MenuEnabledProperty;
import org.eclipse.jface.internal.databinding.swt.MenuItemEnabledProperty;
import org.eclipse.jface.internal.databinding.swt.MenuItemSelectionProperty;
import org.eclipse.jface.internal.databinding.swt.ScaleMaximumProperty;
import org.eclipse.jface.internal.databinding.swt.ScaleMinimumProperty;
import org.eclipse.jface.internal.databinding.swt.ScaleSelectionProperty;
import org.eclipse.jface.internal.databinding.swt.ScrollBarEnabledProperty;
import org.eclipse.jface.internal.databinding.swt.ShellTextProperty;
import org.eclipse.jface.internal.databinding.swt.SliderMaximumProperty;
import org.eclipse.jface.internal.databinding.swt.SliderMinimumProperty;
import org.eclipse.jface.internal.databinding.swt.SpinnerMaximumProperty;
import org.eclipse.jface.internal.databinding.swt.SpinnerMinimumProperty;
import org.eclipse.jface.internal.databinding.swt.SpinnerSelectionProperty;
import org.eclipse.jface.internal.databinding.swt.StyledTextEditableProperty;
import org.eclipse.jface.internal.databinding.swt.StyledTextTextProperty;
import org.eclipse.jface.internal.databinding.swt.TabItemTooltipTextProperty;
import org.eclipse.jface.internal.databinding.swt.TableColumnTooltipTextProperty;
import org.eclipse.jface.internal.databinding.swt.TableSingleSelectionIndexProperty;
import org.eclipse.jface.internal.databinding.swt.TextEditableProperty;
import org.eclipse.jface.internal.databinding.swt.TextMessageProperty;
import org.eclipse.jface.internal.databinding.swt.TextTextProperty;
import org.eclipse.jface.internal.databinding.swt.ToolItemEnabledProperty;
import org.eclipse.jface.internal.databinding.swt.ToolItemTooltipTextProperty;
import org.eclipse.jface.internal.databinding.swt.ToolTipMessageProperty;
import org.eclipse.jface.internal.databinding.swt.TrayItemTooltipTextProperty;
import org.eclipse.jface.internal.databinding.swt.TreeColumnTooltipTextProperty;
import org.eclipse.jface.internal.databinding.swt.WidgetDelegatingListProperty;
import org.eclipse.jface.internal.databinding.swt.WidgetDelegatingValueProperty;
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
import org.eclipse.swt.widgets.DateTime;
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
 * A factory for creating properties of SWT {@link Widget widgets}.
 * 
 * @since 1.3
 */
public class WidgetProperties {
	/**
	 * Returns a value property for observing the background color of a
	 * {@link Control}.
	 * 
	 * @return a value property for observing the background color of a
	 *         {@link Control}.
	 */
	public static IWidgetValueProperty<Control, Color> background() {
		return new ControlBackgroundProperty();
	}

	/**
	 * Returns a value property for observing the bounds of a {@link Control}.
	 * 
	 * @return a value property for observing the bounds of a {@link Control}.
	 */
	public static IWidgetValueProperty<Control, Rectangle> bounds() {
		return new ControlBoundsProperty();
	}

	/**
	 * Returns a value property for observing the editable state of a
	 * {@link CCombo} (since 1.6), {@link StyledText} (since 1.6), or
	 * {@link Text}.
	 * 
	 * @return a value property for observing the editable state of a
	 *         {@link CCombo}, {@link StyledText}, or {@link Text}.
	 * @deprecated use one of the more specific methods below (editableCCombo,
	 *             editableStyledText, or editableText)
	 */
	// ok to ignore warnings in deprecated class
	@SuppressWarnings("rawtypes")
	public static IWidgetValueProperty editable() {
		return new org.eclipse.jface.internal.databinding.swt.WidgetEditableProperty();
	}

	/**
	 * Returns a value property for observing the editable state of a
	 * {@link CCombo}.
	 * 
	 * @return a value property for observing the editable state of a
	 *         {@link CCombo}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<CCombo, Boolean> editableCCombo() {
		return new WidgetDelegatingValueProperty<CCombo, Boolean>() {
			IValueProperty<CCombo, Boolean> ccombo = null;

			@Override
			protected IValueProperty<CCombo, Boolean> doGetDelegate(
					CCombo source) {
				if (ccombo == null) {
					ccombo = new CComboEditableProperty();
				}
				return ccombo;
			}
		};
	}

	/**
	 * Returns a value property for observing the editable state of a
	 * {@link StyledText}.
	 * 
	 * @return a value property for observing the editable state of a
	 *         {@link StyledText}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<StyledText, Boolean> editableStyledText() {
		return new WidgetDelegatingValueProperty<StyledText, Boolean>() {
			IValueProperty<StyledText, Boolean> styledText = null;

			@Override
			protected IValueProperty<StyledText, Boolean> doGetDelegate(
					StyledText source) {
				if (styledText == null) {
					styledText = new StyledTextEditableProperty();
				}
				return styledText;
			}
		};
	}

	/**
	 * Returns a value property for observing the editable state of a
	 * {@link Text}.
	 * 
	 * @return a value property for observing the editable state of a
	 *         {@link Text}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Text, Boolean> editableText() {
		return new WidgetDelegatingValueProperty<Text, Boolean>() {
			IValueProperty<Text, Boolean> text = null;

			@Override
			protected IValueProperty<Text, Boolean> doGetDelegate(Text source) {
				if (text == null) {
					text = new TextEditableProperty();
				}
				return text;
			}
		};
	}

	/**
	 * Returns a value property for observing the enablement state of a
	 * {@link Control}, {@link Menu} (since 1.5), {@link MenuItem} (since 1.5),
	 * {@link ScrollBar} (since 1.5) or {@link ToolItem} (since 1.5).
	 * 
	 * @return a value property for observing the enablement state of a
	 *         {@link Control}, {@link Menu}, {@link MenuItem},
	 *         {@link ScrollBar} or {@link ToolItem}.
	 * @since 1.6
	 * @deprecated use one of the more specific methods below (enabledControl,
	 *             enabledMenu, enabledMenuItem, enabledScrollBar, or
	 *             enabledToolItem)
	 */
	public static IWidgetValueProperty<Widget, Boolean> enabled() {
		return new org.eclipse.jface.internal.databinding.swt.WidgetEnabledProperty();
	}

	/**
	 * Returns a value property for observing the enablement state of a
	 * {@link Control}.
	 * 
	 * @return a value property for observing the enablement state of a
	 *         {@link Control}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Control, Boolean> enabledControl() {
		return new WidgetDelegatingValueProperty<Control, Boolean>(Boolean.TYPE) {

			IValueProperty<Control, Boolean> control = null;

			@Override
			protected IValueProperty<Control, Boolean> doGetDelegate(
					Control source) {
				if (control == null)
					control = new ControlEnabledProperty();
				return control;
			}
		};
	}

	/**
	 * Returns a value property for observing the enablement state of a
	 * {@link Menu}.
	 * 
	 * @return a value property for observing the enablement state of a
	 *         {@link Menu}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Menu, Boolean> enabledMenu() {
		return new WidgetDelegatingValueProperty<Menu, Boolean>(Boolean.TYPE) {

			IValueProperty<Menu, Boolean> menu = null;

			@Override
			protected IValueProperty<Menu, Boolean> doGetDelegate(Menu source) {
				if (menu == null)
					menu = new MenuEnabledProperty();
				return menu;
			}
		};
	}

	/**
	 * Returns a value property for observing the enablement state of a
	 * {@link MenuItem}.
	 * 
	 * @return a value property for observing the enablement state of a
	 *         {@link MenuItem}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<MenuItem, Boolean> enabledMenuItem() {
		return new WidgetDelegatingValueProperty<MenuItem, Boolean>(
				Boolean.TYPE) {

			IValueProperty<MenuItem, Boolean> menu = null;

			@Override
			protected IValueProperty<MenuItem, Boolean> doGetDelegate(
					MenuItem source) {
				if (menu == null)
					menu = new MenuItemEnabledProperty();
				return menu;
			}
		};
	}

	/**
	 * Returns a value property for observing the enablement state of a
	 * {@link ScrollBar}.
	 * 
	 * @return a value property for observing the enablement state of a
	 *         {@link ScrollBar}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<ScrollBar, Boolean> enabledScrollBar() {
		return new WidgetDelegatingValueProperty<ScrollBar, Boolean>(
				Boolean.TYPE) {

			IValueProperty<ScrollBar, Boolean> menu = null;

			@Override
			protected IValueProperty<ScrollBar, Boolean> doGetDelegate(
					ScrollBar source) {
				if (menu == null)
					menu = new ScrollBarEnabledProperty();
				return menu;
			}
		};
	}

	/**
	 * Returns a value property for observing the enablement state of a
	 * {@link ToolItem}.
	 * 
	 * @return a value property for observing the enablement state of a
	 *         {@link ToolItem}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<ToolItem, Boolean> enabledToolItem() {
		return new WidgetDelegatingValueProperty<ToolItem, Boolean>(
				Boolean.TYPE) {

			IValueProperty<ToolItem, Boolean> menu = null;

			@Override
			protected IValueProperty<ToolItem, Boolean> doGetDelegate(
					ToolItem source) {
				if (menu == null)
					menu = new ToolItemEnabledProperty();
				return menu;
			}
		};
	}

	/**
	 * Returns a value property for observing the focus state of a
	 * {@link Control}.
	 * 
	 * @return a value property for observing the focus state of a
	 *         {@link Control}.
	 */
	public static IWidgetValueProperty<Control, Boolean> focused() {
		return new ControlFocusedProperty();
	}

	/**
	 * Returns a value property for observing the font of a {@link Control}.
	 * 
	 * @return a value property for observing the font of a {@link Control}.
	 */
	public static IWidgetValueProperty<Control, Font> font() {
		return new ControlFontProperty();
	}

	/**
	 * Returns a value property for observing the foreground color of a
	 * {@link Control}.
	 * 
	 * @return a value property for observing the foreground color of a
	 *         {@link Control}.
	 */
	public static IWidgetValueProperty<Control, Color> foreground() {
		return new ControlForegroundProperty();
	}

	/**
	 * Returns a value property for observing the image of a {@link Button},
	 * {@link CLabel}, {@link Item} or {@link Label}.
	 * 
	 * @return a value property for observing the image of a {@link Button},
	 *         {@link CLabel}, {@link Item} or {@link Label}.
	 * @deprecated use one of the more specific methods below (imageButton,
	 *             imageCLabel, imageItem, or ImageLabel)
	 */
	// ok to ignore warnings in deprecated class
	@SuppressWarnings("rawtypes")
	public static IWidgetValueProperty image() {
		return new org.eclipse.jface.internal.databinding.swt.WidgetImageProperty();
	}

	/**
	 * Returns a value property for observing the image of a {@link Button}.
	 * 
	 * @return a value property for observing the image of a {@link Button}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Button, Image> imageButton() {
		return new WidgetDelegatingValueProperty<Button, Image>(Image.class) {

			IValueProperty<Button, Image> button = null;

			@Override
			protected IValueProperty<Button, Image> doGetDelegate(Button source) {
				if (button == null)
					button = new ButtonImageProperty();
				return button;
			}
		};
	}

	/**
	 * Returns a value property for observing the image of a {@link CLabel}.
	 * 
	 * @return a value property for observing the image of a {@link CLabel}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<CLabel, Image> imageCLabel() {
		return new WidgetDelegatingValueProperty<CLabel, Image>(Image.class) {

			IValueProperty<CLabel, Image> clabel = null;

			@Override
			protected IValueProperty<CLabel, Image> doGetDelegate(CLabel source) {
				if (clabel == null)
					clabel = new CLabelImageProperty();
				return clabel;
			}
		};
	}

	/**
	 * Returns a value property for observing the image of a {@link Item}.
	 * 
	 * @return a value property for observing the image of a {@link Item}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Item, Image> imageItem() {
		return new WidgetDelegatingValueProperty<Item, Image>(Image.class) {

			IValueProperty<Item, Image> item = null;

			@Override
			protected IValueProperty<Item, Image> doGetDelegate(Item source) {
				if (item == null)
					item = new ItemImageProperty();
				return item;
			}
		};
	}

	/**
	 * Returns a value property for observing the image of a {@link Label}.
	 * 
	 * @return a value property for observing the image of a {@link Label}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Label, Image> imageLabel() {
		return new WidgetDelegatingValueProperty<Label, Image>(Image.class) {

			IValueProperty<Label, Image> label = null;

			@Override
			protected IValueProperty<Label, Image> doGetDelegate(Label source) {
				if (label == null)
					label = new LabelImageProperty();
				return label;
			}
		};
	}

	/**
	 * Returns a list property for observing the items of a {@link CCombo},
	 * {@link Combo} or {@link List}.
	 * 
	 * @return a list property for observing the items of a {@link CCombo},
	 *         {@link Combo} or {@link List}.
	 * @deprecated use instead one of the more specific methods (itemsCCombo,
	 *             itemsCombo, or itemsList)
	 */
	// ok to ignore warnings in deprecated class
	@SuppressWarnings("rawtypes")
	public static IWidgetListProperty items() {
		return new org.eclipse.jface.internal.databinding.swt.WidgetItemsProperty();
	}

	/**
	 * 
	 * @return a value property for observing the items of a {@link Combo}.
	 * @since 1.7
	 */
	public static IWidgetListProperty<CCombo, String> itemsCCombo() {
		return new WidgetDelegatingListProperty<CCombo, String>(CCombo.class) {
			IListProperty<CCombo, String> cCombo = null;

			@Override
			protected IListProperty<CCombo, String> doGetDelegate(CCombo source) {
				if (cCombo == null)
					cCombo = new CComboItemsProperty();
				return cCombo;
			}
		};
	}

	/**
	 * 
	 * @return a value property for observing the items of a {@link Combo}.
	 * @since 1.7
	 */
	public static IWidgetListProperty<Combo, String> itemsCombo() {
		return new WidgetDelegatingListProperty<Combo, String>(Combo.class) {
			IListProperty<Combo, String> combo = null;

			@Override
			protected IListProperty<Combo, String> doGetDelegate(Combo source) {
				if (combo == null)
					combo = new ComboItemsProperty();
				return combo;
			}
		};
	}

	/**
	 * 
	 * @return a value property for observing the items of a {@link Combo}.
	 * @since 1.7
	 */
	public static IWidgetListProperty<List, String> itemsList() {
		return new WidgetDelegatingListProperty<List, String>(List.class) {
			IListProperty<List, String> combo = null;

			@Override
			protected IListProperty<List, String> doGetDelegate(List source) {
				if (combo == null)
					combo = new ListItemsProperty();
				return combo;
			}
		};
	}

	/**
	 * Returns a value property for observing the location of a {@link Control}.
	 * 
	 * @return a value property for observing the location of a {@link Control}.
	 */
	public static IWidgetValueProperty<Control, Point> location() {
		return new ControlLocationProperty();
	}

	/**
	 * Returns a value property for observing the maximum value of a
	 * {@link Scale}, {@link Slider} (since 1.5) or {@link Spinner}.
	 * 
	 * @return a value property for observing the maximum value of a
	 *         {@link Scale}, {@link Slider} (since 1.5) or {@link Spinner}.
	 * @deprecated use instead one of the more specific methods (maximumScale,
	 *             maximumSlider, or maximumSpinner)
	 */
	// ok to ignore warnings in deprecated class
	@SuppressWarnings("rawtypes")
	public static IWidgetValueProperty maximum() {
		return new org.eclipse.jface.internal.databinding.swt.WidgetMaximumProperty();
	}

	/**
	 * 
	 * @return a value property for observing the value of the maximum property
	 *         of a {@link Scale}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Scale, Integer> maximumScale() {
		return new WidgetDelegatingValueProperty<Scale, Integer>(Scale.class) {

			IValueProperty<Scale, Integer> scale = null;

			@Override
			protected IValueProperty<Scale, Integer> doGetDelegate(Scale source) {
				if (scale == null)
					scale = new ScaleMaximumProperty();
				return scale;
			}
		};
	}

	/**
	 * 
	 * @return a value property for observing the value of the maximum property
	 *         of a {@link Slider}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Slider, Integer> maximumSlider() {
		return new WidgetDelegatingValueProperty<Slider, Integer>(Slider.class) {

			IValueProperty<Slider, Integer> slider = null;

			@Override
			protected IValueProperty<Slider, Integer> doGetDelegate(
					Slider source) {
				if (slider == null)
					slider = new SliderMaximumProperty();
				return slider;
			}
		};
	}

	/**
	 * 
	 * @return a value property for observing the value of the maximum property
	 *         of a {@link Spinner}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Spinner, Integer> maximumSpinner() {
		return new WidgetDelegatingValueProperty<Spinner, Integer>(
				Spinner.class) {

			IValueProperty<Spinner, Integer> spinner = null;

			@Override
			protected IValueProperty<Spinner, Integer> doGetDelegate(
					Spinner source) {
				if (spinner == null)
					spinner = new SpinnerMaximumProperty();
				return spinner;
			}
		};
	}

	/**
	 * Returns a value property for observing the message of a {@link Text} or
	 * {@link ToolTip}.
	 * 
	 * @return a value property for observing the message of a {@link Text} or
	 *         {@link ToolTip}.
	 * @deprecated use instead one of the more specific methods (messageText or
	 *             messageToolTip)
	 */
	// ok to ignore warnings in deprecated class
	@SuppressWarnings("rawtypes")
	public static IWidgetValueProperty message() {
		return new org.eclipse.jface.internal.databinding.swt.WidgetMessageProperty();
	}

	/**
	 * Returns a value property for observing the message of a {@link Text}.
	 * 
	 * @return a value property for observing the message of a {@link Text}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Text, String> messageText() {
		return new WidgetDelegatingValueProperty<Text, String>(Text.class) {

			IValueProperty<Text, String> property = null;

			@Override
			protected IValueProperty<Text, String> doGetDelegate(Text source) {
				if (property == null)
					property = new TextMessageProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the message of a {@link ToolTip}.
	 * 
	 * @return a value property for observing the message of a {@link ToolTip}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<ToolTip, String> messageToolTip() {
		return new WidgetDelegatingValueProperty<ToolTip, String>(ToolTip.class) {

			IValueProperty<ToolTip, String> property = null;

			@Override
			protected IValueProperty<ToolTip, String> doGetDelegate(
					ToolTip source) {
				if (property == null)
					property = new ToolTipMessageProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the minimum value of a
	 * {@link Scale}, {@link Slider} (since 1.5) or {@link Spinner}.
	 * 
	 * @return a value property for observing the minimum value of a
	 *         {@link Scale}, {@link Slider} (since 1.5) or {@link Spinner}.
	 * @deprecated use instead one of the more specific methods (minimumScale,
	 *             minimumSlider, or minimumSpinner)
	 */
	// ok to ignore warnings in deprecated class
	@SuppressWarnings("rawtypes")
	public static IWidgetValueProperty minimum() {
		return new org.eclipse.jface.internal.databinding.swt.WidgetMinimumProperty();
	}

	/**
	 * 
	 * @return a value property for observing the value of the minimum property
	 *         of a {@link Scale}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Scale, Integer> minimumScale() {
		return new WidgetDelegatingValueProperty<Scale, Integer>(Scale.class) {

			IValueProperty<Scale, Integer> scale = null;

			@Override
			protected IValueProperty<Scale, Integer> doGetDelegate(Scale source) {
				if (scale == null)
					scale = new ScaleMinimumProperty();
				return scale;
			}
		};
	}

	/**
	 * 
	 * @return a value property for observing the value of the minimum property
	 *         of a {@link Slider}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Slider, Integer> minimumSlider() {
		return new WidgetDelegatingValueProperty<Slider, Integer>(Slider.class) {

			IValueProperty<Slider, Integer> slider = null;

			@Override
			protected IValueProperty<Slider, Integer> doGetDelegate(
					Slider source) {
				if (slider == null)
					slider = new SliderMinimumProperty();
				return slider;
			}
		};
	}

	/**
	 * 
	 * @return a value property for observing the value of the minimum property
	 *         of a {@link Spinner}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Spinner, Integer> minimumSpinner() {
		return new WidgetDelegatingValueProperty<Spinner, Integer>(
				Spinner.class) {

			IValueProperty<Spinner, Integer> spinner = null;

			@Override
			protected IValueProperty<Spinner, Integer> doGetDelegate(
					Spinner source) {
				if (spinner == null)
					spinner = new SpinnerMinimumProperty();
				return spinner;
			}
		};
	}

	/**
	 * Returns a value property for observing the selection state of a
	 * {@link Button}, {@link CCombo}, {@link Combo}, {@link DateTime},
	 * {@link List}, {@link MenuItem} (since 1.5), {@link Scale}, {@link Slider}
	 * (since 1.5) or {@link Spinner}.
	 * 
	 * @return a value property for observing the selection state of a
	 *         {@link Button}, {@link CCombo}, {@link Combo}, {@link DateTime},
	 *         {@link List}, {@link MenuItem}, {@link Scale}, {@link Slider} or
	 *         {@link Spinner}.
	 * @deprecated use instead one of the more specific methods
	 *             (selectionButton, selectionCCombo etc)
	 */
	// ok to ignore warnings in deprecated class
	@SuppressWarnings({ "rawtypes" })
	public static IWidgetValueProperty selection() {
		return new org.eclipse.jface.internal.databinding.swt.WidgetSelectionProperty();
	}

	/**
	 * 
	 * @return a value property for observing the selection state of a
	 *         {@link Combo}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Combo, String> selectionCombo() {
		return new WidgetDelegatingValueProperty<Combo, String>(Combo.class) {

			IValueProperty<Combo, String> combo = null;

			@Override
			protected IValueProperty<Combo, String> doGetDelegate(Combo source) {
				if (combo == null)
					combo = new ComboSelectionProperty();
				return combo;
			}
		};
	}

	/**
	 * 
	 * @return a value property for observing the selection state of a
	 *         {@link Button}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Button, Boolean> selectionButton() {
		return new WidgetDelegatingValueProperty<Button, Boolean>(Button.class) {

			IValueProperty<Button, Boolean> button = null;

			@Override
			protected IValueProperty<Button, Boolean> doGetDelegate(
					Button source) {
				if (button == null)
					button = new ButtonSelectionProperty();
				return button;
			}
		};
	}

	/**
	 * 
	 * @return a value property for observing the selection state of a
	 *         {@link CCombo}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<CCombo, String> selectionCCombo() {
		return new WidgetDelegatingValueProperty<CCombo, String>(CCombo.class) {

			IValueProperty<CCombo, String> cCombo = null;

			@Override
			protected IValueProperty<CCombo, String> doGetDelegate(CCombo source) {
				if (cCombo == null)
					cCombo = new CComboSelectionProperty();
				return cCombo;
			}
		};
	}

	/**
	 * 
	 * @return a value property for observing the selection state of a
	 *         {@link List}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<List, String> selectionList() {
		return new WidgetDelegatingValueProperty<List, String>(List.class) {

			IValueProperty<List, String> listControl = null;

			@Override
			protected IValueProperty<List, String> doGetDelegate(List source) {
				if (listControl == null)
					listControl = new ListSelectionProperty();
				return listControl;
			}
		};
	}

	/**
	 * 
	 * @return a value property for observing the selection state of a
	 *         {@link MenuItem}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<MenuItem, Boolean> selectionMenuItem() {
		return new WidgetDelegatingValueProperty<MenuItem, Boolean>(
				MenuItem.class) {

			IValueProperty<MenuItem, Boolean> menuItem = null;

			@Override
			protected IValueProperty<MenuItem, Boolean> doGetDelegate(
					MenuItem source) {
				if (menuItem == null)
					menuItem = new MenuItemSelectionProperty();
				return menuItem;
			}
		};
	}

	/**
	 * 
	 * @return a value property for observing the selection state of a
	 *         {@link Scale}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Scale, Integer> selectionScale() {
		return new WidgetDelegatingValueProperty<Scale, Integer>(Scale.class) {

			IValueProperty<Scale, Integer> scale = null;

			@Override
			protected IValueProperty<Scale, Integer> doGetDelegate(Scale source) {
				if (scale == null)
					scale = new ScaleSelectionProperty();
				return scale;
			}
		};
	}

	/**
	 * 
	 * @return a value property for observing the selection state of a
	 *         {@link Spinner}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Spinner, Integer> selectionSpinner() {
		return new WidgetDelegatingValueProperty<Spinner, Integer>(
				Spinner.class) {

			IValueProperty<Spinner, Integer> spinner = null;

			@Override
			protected IValueProperty<Spinner, Integer> doGetDelegate(
					Spinner source) {
				if (spinner == null)
					spinner = new SpinnerSelectionProperty();
				return spinner;
			}
		};
	}

	/**
	 * Returns a value property for observing the single selection index of a
	 * {@link CCombo}, {@link Combo}, {@link List} or {@link Table}.
	 * 
	 * @return a value property for the single selection index of a SWT Combo.
	 * @deprecated use one of the more specific methods below
	 *             (singleSelectionIndexCCombo, singleSelectionIndexCombo,
	 *             singleSelectionIndexList, or singleSelectionIndexTable)
	 */
	// ok to ignore warnings in deprecated class
	@SuppressWarnings("rawtypes")
	public static IWidgetValueProperty singleSelectionIndex() {
		return new org.eclipse.jface.internal.databinding.swt.WidgetSingleSelectionIndexProperty();
	}

	/**
	 * Returns a value property for observing the single selection index of a
	 * {@link CCombo}.
	 * 
	 * @return a value property for the single selection index of a SWT CCombo.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<CCombo, Integer> singleSelectionIndexCCombo() {
		return new WidgetDelegatingValueProperty<CCombo, Integer>(CCombo.class) {

			IValueProperty<CCombo, Integer> property = null;

			@Override
			protected IValueProperty<CCombo, Integer> doGetDelegate(
					CCombo source) {
				if (property == null)
					property = new CComboSingleSelectionIndexProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the single selection index of a
	 * {@link Combo}.
	 * 
	 * @return a value property for the single selection index of a SWT Combo.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Combo, Integer> singleSelectionIndexCombo() {
		return new WidgetDelegatingValueProperty<Combo, Integer>(Combo.class) {

			IValueProperty<Combo, Integer> property = null;

			@Override
			protected IValueProperty<Combo, Integer> doGetDelegate(Combo source) {
				if (property == null)
					property = new ComboSingleSelectionIndexProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the single selection index of a
	 * {@link List}.
	 * 
	 * @return a value property for the single selection index of a SWT List.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<List, Integer> singleSelectionIndexList() {
		return new WidgetDelegatingValueProperty<List, Integer>(List.class) {

			IValueProperty<List, Integer> property = null;

			@Override
			protected IValueProperty<List, Integer> doGetDelegate(List source) {
				if (property == null)
					property = new ListSingleSelectionIndexProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the single selection index of a
	 * {@link Table}.
	 * 
	 * @return a value property for the single selection index of a SWT Table.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Table, Integer> singleSelectionIndexTable() {
		return new WidgetDelegatingValueProperty<Table, Integer>(Table.class) {

			IValueProperty<Table, Integer> property = null;

			@Override
			protected IValueProperty<Table, Integer> doGetDelegate(Table source) {
				if (property == null)
					property = new TableSingleSelectionIndexProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the size of a {@link Control}.
	 * 
	 * @return a value property for observing the size of a {@link Control}.
	 */
	public static IWidgetValueProperty<Control, Point> size() {
		return new ControlSizeProperty();
	}

	/**
	 * Returns a value property for observing the text of a {@link Button},
	 * {@link CCombo}, {@link CLabel}, {@link Combo}, {@link Item},
	 * {@link Label}, {@link Link}, {@link Shell}, {@link StyledText} or
	 * {@link Text}.
	 * 
	 * @return a value property for observing the text of a {@link Button},
	 *         {@link CCombo}, {@link CLabel}, {@link Combo}, {@link Item},
	 *         {@link Label}, {@link Link}, {@link Shell}, {@link StyledText} or
	 *         {@link Text}.
	 * @deprecated use one of the more specific methods below (textButton,
	 *             textCCombo, textCLabel, textCombo, textItem, textLabel,
	 *             textLink, textShell, textStyledText, or textText)
	 */
	// ok to ignore warnings in deprecated class
	@SuppressWarnings("rawtypes")
	public static IWidgetValueProperty text() {
		return new org.eclipse.jface.internal.databinding.swt.WidgetTextProperty();
	}

	/**
	 * Returns a value property for observing the text of a {@link Button}.
	 * 
	 * @return a value property for observing the text of a {@link Button}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Button, String> textButton() {
		return new WidgetDelegatingValueProperty<Button, String>(Button.class) {

			IValueProperty<Button, String> property = null;

			@Override
			protected IValueProperty<Button, String> doGetDelegate(Button source) {
				if (property == null)
					property = new ButtonTextProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the text of a {@link CCombo}.
	 * 
	 * @return a value property for observing the text of a {@link CCombo}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<CCombo, String> textCCombo() {
		return new WidgetDelegatingValueProperty<CCombo, String>(CCombo.class) {

			IValueProperty<CCombo, String> property = null;

			@Override
			protected IValueProperty<CCombo, String> doGetDelegate(CCombo source) {
				if (property == null)
					property = new CComboTextProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the text of a {@link CLabel}.
	 * 
	 * @return a value property for observing the text of a {@link CLabel}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<CLabel, String> textCLabel() {
		return new WidgetDelegatingValueProperty<CLabel, String>(CLabel.class) {

			IValueProperty<CLabel, String> property = null;

			@Override
			protected IValueProperty<CLabel, String> doGetDelegate(CLabel source) {
				if (property == null)
					property = new CLabelTextProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the text of a {@link Combo}.
	 * 
	 * @return a value property for observing the text of a {@link Combo}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Combo, String> textCombo() {
		return new WidgetDelegatingValueProperty<Combo, String>(Combo.class) {

			IValueProperty<Combo, String> property = null;

			@Override
			protected IValueProperty<Combo, String> doGetDelegate(Combo source) {
				if (property == null)
					property = new ComboTextProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the text of a {@link Item}.
	 * 
	 * @return a value property for observing the text of a {@link Item}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Item, String> textItem() {
		return new WidgetDelegatingValueProperty<Item, String>(Item.class) {

			IValueProperty<Item, String> property = null;

			@Override
			protected IValueProperty<Item, String> doGetDelegate(Item source) {
				if (property == null)
					property = new ItemTextProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the text of a {@link Label}.
	 * 
	 * @return a value property for observing the text of a {@link Label}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Label, String> textLabel() {
		return new WidgetDelegatingValueProperty<Label, String>(Label.class) {

			IValueProperty<Label, String> property = null;

			@Override
			protected IValueProperty<Label, String> doGetDelegate(Label source) {
				if (property == null)
					property = new LabelTextProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the text of a {@link Link}.
	 * 
	 * @return a value property for observing the text of a {@link Link}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Link, String> textLink() {
		return new WidgetDelegatingValueProperty<Link, String>(Link.class) {

			IValueProperty<Link, String> property = null;

			@Override
			protected IValueProperty<Link, String> doGetDelegate(Link source) {
				if (property == null)
					property = new LinkTextProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the text of a {@link Shell}.
	 * 
	 * @return a value property for observing the text of a {@link Shell}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Shell, String> textShell() {
		return new WidgetDelegatingValueProperty<Shell, String>(Shell.class) {

			IValueProperty<Shell, String> property = null;

			@Override
			protected IValueProperty<Shell, String> doGetDelegate(Shell source) {
				if (property == null)
					property = new ShellTextProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the text of a {@link StyledText}.
	 * 
	 * @return a value property for observing the text of a {@link StyledText}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<StyledText, String> textStyledText() {
		return new WidgetDelegatingValueProperty<StyledText, String>(
				StyledText.class) {

			IValueProperty<StyledText, String> property = null;

			@Override
			protected IValueProperty<StyledText, String> doGetDelegate(
					StyledText source) {
				if (property == null)
					property = new StyledTextTextProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the text of a {@link Text}.
	 * 
	 * @return a value property for observing the text of a {@link Text}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Text, String> textText() {
		return new WidgetDelegatingValueProperty<Text, String>(Text.class) {

			IValueProperty<Text, String> property = null;

			@Override
			protected IValueProperty<Text, String> doGetDelegate(Text source) {
				if (property == null)
					property = new TextTextProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the text of a {@link StyledText}
	 * or {@link Text}.
	 * 
	 * @param event
	 *            the SWT event type to register for change events. May be
	 *            {@link SWT#None}, {@link SWT#Modify}, {@link SWT#FocusOut} or
	 *            {@link SWT#DefaultSelection}.
	 * 
	 * @return a value property for observing the text of a {@link StyledText}
	 *         or {@link Text}.
	 * @deprecated use instead one of the more specific methods textText(event)
	 *             or textStyledText(event)
	 */
	// ok to ignore warnings in deprecated method
	@SuppressWarnings("rawtypes")
	public static IWidgetValueProperty text(final int event) {
		return text(new int[] { event });
	}

	/**
	 * Returns a value property for observing the text of a {@link StyledText}.
	 * 
	 * @param event
	 *            the SWT event type to register for change events. May be
	 *            {@link SWT#None}, {@link SWT#Modify}, {@link SWT#FocusOut} or
	 *            {@link SWT#DefaultSelection}.
	 * 
	 * @return a value property for observing the text of a {@link StyledText}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<StyledText, String> textStyledText(
			int event) {
		return textStyledText(new int[] { event });
	}

	/**
	 * Returns a value property for observing the text of a {@link Text}.
	 * 
	 * @param event
	 *            the SWT event type to register for change events. May be
	 *            {@link SWT#None}, {@link SWT#Modify}, {@link SWT#FocusOut} or
	 *            {@link SWT#DefaultSelection}.
	 * 
	 * @return a value property for observing the text of a {@link Text}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Text, String> textText(int event) {
		return textText(new int[] { event });
	}

	/**
	 * Returns a value property for observing the text of a {@link StyledText}
	 * or {@link Text}.
	 * 
	 * @param events
	 *            array of SWT event types to register for change events. May
	 *            include {@link SWT#None}, {@link SWT#Modify},
	 *            {@link SWT#FocusOut} or {@link SWT#DefaultSelection}.
	 * 
	 * @return a value property for observing the text of a {@link StyledText}
	 *         or {@link Text}.
	 * @deprecated use instead one of the more specific methods textText() or
	 *             textStyledText()
	 */
	// ok to ignore warnings in deprecated method
	@SuppressWarnings("rawtypes")
	public static IWidgetValueProperty text(int[] events) {
		return new org.eclipse.jface.internal.databinding.swt.WidgetTextWithEventsProperty(
				events.clone());
	}

	/**
	 * Returns a value property for observing the text of a {@link Text}.
	 * 
	 * @param events
	 *            array of SWT event types to register for change events. May
	 *            include {@link SWT#None}, {@link SWT#Modify},
	 *            {@link SWT#FocusOut} or {@link SWT#DefaultSelection}.
	 * 
	 * @return a value property for observing the text of a {@link Text}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Text, String> textText(int[] events) {

		for (int event : events) {
			if (event != SWT.None && event != SWT.Modify
					&& event != SWT.FocusOut && event != SWT.DefaultSelection)
				throw new IllegalArgumentException("UpdateEventType [" //$NON-NLS-1$
						+ event + "] is not supported."); //$NON-NLS-1$
		}

		final int[] finalEvents = events.clone();

		return new WidgetDelegatingValueProperty<Text, String>() {
			private IValueProperty<Text, String> text = null;

			@Override
			protected IValueProperty<Text, String> doGetDelegate(Text source) {
				if (text == null)
					text = new TextTextProperty(finalEvents);
				return text;
			}
		};
	}

	/**
	 * Returns a value property for observing the text of a {@link StyledText}.
	 * 
	 * @param events
	 *            array of SWT event types to register for change events. May
	 *            include {@link SWT#None}, {@link SWT#Modify},
	 *            {@link SWT#FocusOut} or {@link SWT#DefaultSelection}.
	 * 
	 * @return a value property for observing the text of a {@link StyledText}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<StyledText, String> textStyledText(
			int[] events) {

		for (int event : events) {
			if (event != SWT.None && event != SWT.Modify
					&& event != SWT.FocusOut && event != SWT.DefaultSelection)
				throw new IllegalArgumentException("UpdateEventType [" //$NON-NLS-1$
						+ event + "] is not supported."); //$NON-NLS-1$
		}

		final int[] finalEvents = events.clone();

		return new WidgetDelegatingValueProperty<StyledText, String>() {
			private IValueProperty<StyledText, String> text = null;

			@Override
			protected IValueProperty<StyledText, String> doGetDelegate(
					StyledText source) {
				if (text == null)
					text = new StyledTextTextProperty(finalEvents);
				return text;
			}
		};
	}

	/**
	 * Returns a value property for observing the tooltip text of a
	 * {@link CTabItem}, {@link Control}, {@link TabItem}, {@link TableColumn},
	 * {@link ToolItem}, {@link TrayItem} or {@link TreeColumn}.
	 * 
	 * @return a value property for observing the tooltip text of a
	 *         {@link CTabItem}, {@link Control}, {@link TabItem},
	 *         {@link TableColumn}, {@link ToolItem}, {@link TrayItem} or
	 *         {@link TreeColumn}.
	 * @deprecated use instead one of the more specific methods
	 *             tooltipCTabItem(), tooltipControl() etc
	 */
	// ok to ignore warnings in deprecated method
	@SuppressWarnings({ "unchecked" })
	public static IWidgetValueProperty<Widget, Boolean> tooltipText() {
		return new org.eclipse.jface.internal.databinding.swt.WidgetTooltipTextProperty();
	}

	/**
	 * Returns a value property for observing the tooltip text of a
	 * {@link CTabItem}.
	 * 
	 * @return a value property for observing the tooltip text of a
	 *         {@link CTabItem}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<CTabItem, String> tooltipCTabItem() {
		return new WidgetDelegatingValueProperty<CTabItem, String>(String.class) {

			IValueProperty<CTabItem, String> property = null;

			@Override
			protected IValueProperty<CTabItem, String> doGetDelegate(
					CTabItem source) {
				if (property == null)
					property = new CTabItemTooltipTextProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the tooltip text of a
	 * {@link Control}.
	 * 
	 * @return a value property for observing the tooltip text of a
	 *         {@link Control}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<Control, String> tooltipControl() {
		return new WidgetDelegatingValueProperty<Control, String>(String.class) {

			IValueProperty<Control, String> property = null;

			@Override
			protected IValueProperty<Control, String> doGetDelegate(
					Control source) {
				if (property == null)
					property = new ControlTooltipTextProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the tooltip text of a
	 * {@link TabItem}.
	 * 
	 * @return a value property for observing the tooltip text of a
	 *         {@link TabItem}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<TabItem, String> tooltipTabItem() {
		return new WidgetDelegatingValueProperty<TabItem, String>(String.class) {

			IValueProperty<TabItem, String> property = null;

			@Override
			protected IValueProperty<TabItem, String> doGetDelegate(
					TabItem source) {
				if (property == null)
					property = new TabItemTooltipTextProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the tooltip text of a
	 * {@link TableColumn}.
	 * 
	 * @return a value property for observing the tooltip text of a
	 *         {@link TableColumn}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<TableColumn, String> tooltipTableColumn() {
		return new WidgetDelegatingValueProperty<TableColumn, String>(
				String.class) {

			IValueProperty<TableColumn, String> property = null;

			@Override
			protected IValueProperty<TableColumn, String> doGetDelegate(
					TableColumn source) {
				if (property == null)
					property = new TableColumnTooltipTextProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the tooltip text of a
	 * {@link ToolItem}.
	 * 
	 * @return a value property for observing the tooltip text of a
	 *         {@link ToolItem}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<ToolItem, String> tooltipToolItem() {
		return new WidgetDelegatingValueProperty<ToolItem, String>(String.class) {

			IValueProperty<ToolItem, String> property = null;

			@Override
			protected IValueProperty<ToolItem, String> doGetDelegate(
					ToolItem source) {
				if (property == null)
					property = new ToolItemTooltipTextProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the tooltip text of a
	 * {@link TrayItem}.
	 * 
	 * @return a value property for observing the tooltip text of a
	 *         {@link TrayItem}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<TrayItem, String> tooltipTrayItem() {
		return new WidgetDelegatingValueProperty<TrayItem, String>(String.class) {

			IValueProperty<TrayItem, String> property = null;

			@Override
			protected IValueProperty<TrayItem, String> doGetDelegate(
					TrayItem source) {
				if (property == null)
					property = new TrayItemTooltipTextProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the tooltip text of a
	 * {@link TreeColumn}.
	 * 
	 * @return a value property for observing the tooltip text of a
	 *         {@link TreeColumn}.
	 * @since 1.7
	 */
	public static IWidgetValueProperty<TreeColumn, String> tooltipTreeColumn() {
		return new WidgetDelegatingValueProperty<TreeColumn, String>(
				String.class) {

			IValueProperty<TreeColumn, String> property = null;

			@Override
			protected IValueProperty<TreeColumn, String> doGetDelegate(
					TreeColumn source) {
				if (property == null)
					property = new TreeColumnTooltipTextProperty();
				return property;
			}
		};
	}

	/**
	 * Returns a value property for observing the visibility state of a
	 * {@link Control}.
	 * 
	 * @return a value property for observing the visibility state of a
	 *         {@link Control}.
	 */
	public static IWidgetValueProperty<Control, Boolean> visible() {
		return new ControlVisibleProperty();
	}
}
