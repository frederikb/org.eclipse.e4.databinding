/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.tests.databinding.scenarios;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.eclipse.jface.examples.databinding.model.Account;
import org.eclipse.jface.examples.databinding.model.Adventure;
import org.eclipse.jface.examples.databinding.model.Cart;
import org.eclipse.jface.examples.databinding.model.SampleData;
import org.eclipse.jface.internal.databinding.provisional.BindSpec;
import org.eclipse.jface.internal.databinding.provisional.Binding;
import org.eclipse.jface.internal.databinding.provisional.DataBindingContext;
import org.eclipse.jface.internal.databinding.provisional.conversion.Converter;
import org.eclipse.jface.internal.databinding.provisional.conversion.IConverter;
import org.eclipse.jface.internal.databinding.provisional.conversion.IdentityConverter;
import org.eclipse.jface.internal.databinding.provisional.description.Property;
import org.eclipse.jface.internal.databinding.provisional.observable.IChangeListener;
import org.eclipse.jface.internal.databinding.provisional.observable.IObservable;
import org.eclipse.jface.internal.databinding.provisional.observable.value.IObservableValue;
import org.eclipse.jface.internal.databinding.provisional.swt.SWTProperties;
import org.eclipse.jface.internal.databinding.provisional.validation.IValidator;
import org.eclipse.jface.internal.databinding.provisional.validation.ValidationError;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * To run the tests in this class, right-click and select "Run As JUnit Plug-in
 * Test". This will also start an Eclipse instance. To clean up the launch
 * configuration, open up its "Main" tab and select "[No Application] - Headless
 * Mode" as the application to run.
 */

public class PropertyScenarios extends ScenariosTestCase {

	private Adventure adventure;

	protected void setUp() throws Exception {
		super.setUp();
		// do any setup work here
		adventure = SampleData.WINTER_HOLIDAY;
	}

	protected void tearDown() throws Exception {
		// do any teardown work here
		super.tearDown();
	}

	public void testEnterText() {
		// just to make sure enterText() generates a FocusOut event.
		Text text = new Text(getComposite(), SWT.BORDER);
		final boolean[] focusLostHolder = { false };
		text.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				// only interested in focus lost events
			}

			public void focusLost(FocusEvent e) {
				focusLostHolder[0] = true;
			}
		});
		enterText(text, "hallo");
		assertTrue(focusLostHolder[0]);
	}

	public void testScenario01() {
		Text text = new Text(getComposite(), SWT.BORDER);
		getDbc().bind(text, new Property(adventure, "name"), null);
		// uncomment the following line to see what's happening
		// spinEventLoop(1);
		assertEquals(adventure.getName(), text.getText());
		enterText(text, "foobar");
		// uncomment the following line to see what's happening
		// spinEventLoop(1);
		assertEquals("foobar", adventure.getName());
		adventure.setName("barfoo");
		// uncomment the following line to see what's happening
		// spinEventLoop(1);
		assertEquals("barfoo", text.getText());
	}

	public void testScenario02() {
		// Binding the name property of an Adventure object to the contents of
		// Text controls, no conversion, no validation. The Text widget editable
		// is set to false.by the developer (can not change the name)
		Text text = new Text(getComposite(), SWT.READ_ONLY);
		getDbc().bind(text, new Property(adventure, "name"), null);
		assertEquals(adventure.getName(), text.getText());
	}

	public void testScenario03() {
		// Binding of a read-only property of an Adventure object to the
		// contents of Text controls, no conversion, no validation. Text control
		// is not editable as a side effect of binding to a read-only property..
		Cart cart = SampleData.CART;
		cart.setAdventureDays(42);
		// bind to the lodgingDays feature, which is read-only and always one
		// less than the number of adventure days.
		Text text = new Text(getComposite(), SWT.BORDER);
		getDbc().bind(text, new Property(cart, "lodgingDays"),
				new BindSpec(new IConverter() {
					public Object getFromType() {
						return int.class;
					}

					public Object getToType() {
						return String.class;
					}

					public Object convert(Object object) {
						return object.toString();
					}
				}, new IConverter() {
					public Object getFromType() {
						return String.class;
					}

					public Object getToType() {
						return int.class;
					}

					public Object convert(Object object) {
						return new Integer((String) object);
					}
				}, null, null));
		assertEquals(new Integer(cart.getLodgingDays()).toString(), text
				.getText());
		// TODO API extension needed: getChangeable() and setChangeable() on
		// IObservableValue or IObservable
		// assertEquals(
		// "Needs API extension: getChangeable() and setChangeable() on
		// IObservableValue or IObservable.",
		// false, text.getEditable());
	}

	public void testScenario04() {
		// Binding a nested property of an Adventure object to the content of a
		// Text control, no conversion, no validation.
		Text text = new Text(getComposite(), SWT.BORDER);
		// TODO Scenario needs to be more specific - I'm binding to the default
		// lodging's description of an adventure. What do we expect to happen
		// when the default lodging changes? If we expect no change, then this
		// scenario does not introduce anything new. If we expect the binding to
		// be to the new default lodging's description, shouldn't we move this
		// scenario to the master/detail section? I'm assuming the latter for
		// now.
		IObservableValue defaultLodging = (IObservableValue) getDbc()
				.createObservable(new Property(adventure, "defaultLodging"));
		getDbc().bind(
				text,
				new Property(defaultLodging, "description", String.class,
						Boolean.FALSE), null);

		// test changing the description
		assertEquals(adventure.getDefaultLodging().getDescription(), text
				.getText());
		enterText(text, "foobar");
		assertEquals("foobar", adventure.getDefaultLodging().getDescription());
		adventure.getDefaultLodging().setDescription("barfoo");
		assertEquals(adventure.getDefaultLodging().getDescription(), text
				.getText());

		// test changing the default lodging
		adventure.setDefaultLodging(SampleData.CAMP_GROUND);
		assertEquals(adventure.getDefaultLodging().getDescription(), text
				.getText());
		adventure.getDefaultLodging().setDescription("barfo");
		assertEquals(adventure.getDefaultLodging().getDescription(), text
				.getText());

		adventure.setDefaultLodging(null);
		assertEquals("", text.getText());

		adventure.setDefaultLodging(SampleData.FIVE_STAR_HOTEL);
		assertEquals(adventure.getDefaultLodging().getDescription(), text
				.getText());
		adventure.getDefaultLodging().setDescription("barf");
		assertEquals(adventure.getDefaultLodging().getDescription(), text
				.getText());

	}

	public void testScenario05() {
		// Binding the name property of an Adventure object to the contents of
		// Text controls where conversion occurs � the model data is held all
		// in
		// uppercase and displayed in lowercase with the first letter
		// capitalized.
		Text text = new Text(getComposite(), SWT.BORDER);
		adventure.setName("UPPERCASE");
		getDbc().bind(text, new Property(adventure, "name"),
				new BindSpec(new IConverter() {
					public Object getFromType() {
						return String.class;
					}

					public Object getToType() {
						return String.class;
					}

					public Object convert(Object toObject) {
						String modelValue = (String) toObject;
						if (modelValue == null || modelValue.equals("")) {
							return modelValue;
						}
						String firstChar = modelValue.substring(0, 1);
						String remainingChars = modelValue.substring(1);
						return firstChar.toUpperCase()
								+ remainingChars.toLowerCase();
					}
				}, new IConverter() {
					public Object getFromType() {
						return String.class;
					}

					public Object getToType() {
						return String.class;
					}

					public Object convert(Object fromObject) {
						return ((String) fromObject).toUpperCase();
					}
				}, null, null));
		// spinEventLoop(1);
		assertEquals("Uppercase", text.getText());
		enterText(text, "lowercase");
		// spinEventLoop(1);
		// TODO If we wanted to "canonicalize" the value in the text field, how
		// could we do that?
		assertEquals("LOWERCASE", adventure.getName());
	}

	public void testScenario06() {
		// Binding the name property of an Adventure object to the contents of
		// Text controls where validation occurs and the name cannot be longer
		// than 15 characters and cannot contain spaces
		Text text = new Text(getComposite(), SWT.BORDER);
		final String noSpacesMessage = "Name must not contain spaces.";
		final String max15CharactersMessage = "Maximum length for name is 15 characters.";
		adventure.setName("ValidValue");
		getDbc().bind(
				text,
				new Property(adventure, "name"),
				new BindSpec(new IdentityConverter(String.class),
						new IdentityConverter(String.class), new IValidator() {
							public ValidationError isPartiallyValid(Object value) {
								return isValid(value);
							}

							public ValidationError isValid(Object value) {
								String stringValue = (String) value;
								if (stringValue.length() > 15) {
									return ValidationError
											.error(max15CharactersMessage);
								} else if (stringValue.indexOf(' ') != -1) {
									return ValidationError
											.error(noSpacesMessage);
								} else {
									return null;
								}
							}
						}, null));
		// no validation message
		assertEquals(null, getDbc().getPartialValidationError().getValue());
		enterText(text, "Invalid Value");
		assertEquals(noSpacesMessage, ((ValidationError) getDbc()
				.getPartialValidationError().getValue()).message);
		assertEquals("ValidValue", text.getText());
		text.setText("InvalidValueBecauseTooLong");
		assertEquals(max15CharactersMessage, ((ValidationError) getDbc()
				.getPartialValidationError().getValue()).message);
		assertEquals("ValidValue", text.getText());
		enterText(text, "anothervalid");
		assertEquals(null, getDbc().getPartialValidationError().getValue());
		assertEquals("anothervalid", text.getText());
		assertEquals("anothervalid", adventure.getName());
	}

	public void testScenario07() {
		// Binding the price property of an Adventure to a Text control. Price
		// is a double and Text accepts String so conversion will have to occur.
		// Validation ensure that the value is positive
		Text text = new Text(getComposite(), SWT.BORDER);
		adventure.setPrice(5.0);
		final String cannotBeNegativeMessage = "Price cannot be negative.";
		final String mustBeCurrencyMessage = "Price must be a currency.";
		getDbc().bind(text, new Property(adventure, "price"),
				new BindSpec(new Converter(double.class, String.class) {
					public Object convert(Object toObject) {
						return ((Double) toObject).toString();
					}
				}, new Converter(String.class, double.class) {

					public Object convert(Object fromObject) {
						return new Double((String) fromObject);
					}
				}, new IValidator() {
					public ValidationError isPartiallyValid(Object value) {
						return null;
					}

					public ValidationError isValid(Object value) {
						String stringValue = (String) value;
						try {
							double doubleValue = new Double(stringValue)
									.doubleValue();
							if (doubleValue < 0.0) {
								return ValidationError
										.error(cannotBeNegativeMessage);
							}
							return null;
						} catch (NumberFormatException ex) {
							return ValidationError.error(mustBeCurrencyMessage);
						}
					}
				}, null));
		assertEquals("5.0", text.getText());
		assertEquals(null, getDbc().getValidationError().getValue());
		enterText(text, "0.65");
		assertEquals(null, getDbc().getValidationError().getValue());
		assertEquals(0.65, adventure.getPrice(), 0.0001);
		adventure.setPrice(42.24);
		assertEquals("42.24", text.getText());
		assertEquals(null, getDbc().getValidationError().getValue());
		enterText(text, "jygt");
		assertEquals(mustBeCurrencyMessage, ((ValidationError) getDbc()
				.getValidationError().getValue()).message);
		enterText(text, "-23.9");
		assertEquals(cannotBeNegativeMessage, ((ValidationError) getDbc()
				.getValidationError().getValue()).message);
		assertEquals(42.24, adventure.getPrice(), 0.0001);
		adventure.setPrice(0.0);
		assertEquals(null, getDbc().getValidationError().getValue());
	}

	public void testScenario08() {
		// Binding the price property of an Adventure to a Text control but with
		// custom conversion � the double will be validated to only have two
		// decimal places and displayed with a leading currency symbol, and can
		// be entered with or without the currency symbol.
		Text text = new Text(getComposite(), SWT.BORDER);
		adventure.setPrice(5.0);
		final String cannotBeNegativeMessage = "Price cannot be negative.";
		final String mustBeCurrencyMessage = "Price must be a currency.";
		final NumberFormat currencyFormat = NumberFormat
				.getCurrencyInstance(Locale.CANADA);
		getDbc().bind(text, new Property(adventure, "price"),
				new BindSpec(new Converter(double.class, String.class) {
					public Object convert(Object toObject) {
						return currencyFormat.format(((Double) toObject)
								.doubleValue());
					}
				}, new Converter(String.class, double.class) {

					public Object convert(Object fromObject) {
						try {
							return currencyFormat.parse((String) fromObject);
						} catch (ParseException e) {
							// TODO throw something like
							// IllegalConversionException?
							return new Double(0);
						}
					}
				}, new IValidator() {
					public ValidationError isPartiallyValid(Object value) {
						return null;
					}

					public ValidationError isValid(Object value) {
						String stringValue = (String) value;
						try {
							double doubleValue = currencyFormat.parse(
									stringValue).doubleValue();
							if (doubleValue < 0.0) {
								return ValidationError
										.error(cannotBeNegativeMessage);
							}
							return null;
						} catch (ParseException e) {
							return ValidationError.error(mustBeCurrencyMessage);
						}
					}
				}, null));
		assertEquals("$5.00", text.getText());
		assertEquals(null, getDbc().getValidationError().getValue());
		enterText(text, "$0.65");
		assertEquals(null, getDbc().getValidationError().getValue());
		assertEquals(0.65, adventure.getPrice(), 0.0001);
		adventure.setPrice(42.24);
		assertEquals("$42.24", text.getText());
		assertEquals(null, getDbc().getValidationError().getValue());
		enterText(text, "jygt");
		assertEquals(mustBeCurrencyMessage, ((ValidationError) getDbc()
				.getValidationError().getValue()).message);
		enterText(text, "-$23.9");
		assertEquals(cannotBeNegativeMessage, ((ValidationError) getDbc()
				.getValidationError().getValue()).message);
		assertEquals(42.24, adventure.getPrice(), 0.0001);
		adventure.setPrice(0.0);
		assertEquals(null, getDbc().getValidationError().getValue());
	}

	public void testScenario09() {
		// Binding a boolean property to a CheckBox. Adventure will have a
		// Boolean property �petsAllowed�
		Button checkbox = new Button(getComposite(), SWT.CHECK);
		// checkbox.setText("Pets allowed");
		// checkbox.setLayoutData(new GridData(SWT.LEFT,SWT.TOP, false,false));
		adventure.setPetsAllowed(true);
		getDbc().bind(checkbox, new Property(adventure, "petsAllowed"), null);
		assertEquals(true, checkbox.getSelection());
		setButtonSelectionWithEvents(checkbox, false);
		assertEquals(false, adventure.isPetsAllowed());
		adventure.setPetsAllowed(true);
		assertEquals(true, checkbox.getSelection());
	}

	public void testScenario10() {
		// Binding a Transportation departure time to a Text control that
		// formats and validates the time to and from a String. There are
		// property bindings that bind elements of the GUI to elements to GUI
		// and also elements of the domain to elements of the domain
		// TODO fail("not implemented");
	}

	public void testScenario11() {
		// Binding the max value of a spinner to another spinner.
		Spinner spinner1 = new Spinner(getComposite(), SWT.NONE);
		spinner1.setSelection(10);
		spinner1.setMinimum(1);
		spinner1.setMaximum(100);
		Spinner spinner2 = new Spinner(getComposite(), SWT.NONE);
		spinner2.setMaximum(1);
		getDbc()
				.bind(spinner1, new Property(spinner2, SWTProperties.MAX), null);
		assertEquals(1, spinner1.getSelection());
		spinner1.setSelection(10);
		spinner1.notifyListeners(SWT.Modify, new Event());
		assertEquals(10, spinner2.getMaximum());
	}

	public void testScenario12() {
		// Binding the enabled state of several Text controls to a check box.
		// There will be two check boxes, so as each is enabled/disabled the
		// other one follows as do the states of the Text controls.
		Button checkbox1 = new Button(getComposite(), SWT.CHECK);
		checkbox1.setSelection(false);
		Button checkbox2 = new Button(getComposite(), SWT.CHECK);
		checkbox2.setSelection(false);
		Text text1 = new Text(getComposite(), SWT.NONE);
		Text text2 = new Text(getComposite(), SWT.NONE);
		IObservableValue checkbox1Selected = (IObservableValue) getDbc()
				.createObservable(checkbox1);
		IObservableValue checkbox2Selected = (IObservableValue) getDbc()
				.createObservable(checkbox2);
		// bind the two checkboxes so that if one is checked, the other is not
		// and vice versa.
		Converter negatingConverter = new Converter(boolean.class,
				boolean.class) {
			private Boolean negated(Boolean booleanObject) {
				return new Boolean(!booleanObject.booleanValue());
			}

			public Object convert(Object targetObject) {
				return negated((Boolean) targetObject);
			}
		};
		getDbc().bind(checkbox1Selected, checkbox2Selected,
				new BindSpec(negatingConverter, negatingConverter, null, null));
		// bind the enabled state of the two text widgets to one of the
		// checkboxes each.
		getDbc().bind(new Property(text1, SWTProperties.ENABLED),
				checkbox1Selected, null);
		getDbc().bind(new Property(text2, SWTProperties.ENABLED),
				checkbox2Selected, null);
		assertEquals(true, text1.getEnabled());
		assertEquals(false, text2.getEnabled());
		assertEquals(true, checkbox1.getSelection());
		setButtonSelectionWithEvents(checkbox1, false);
		assertEquals(false, text1.getEnabled());
		assertEquals(true, text2.getEnabled());
		assertEquals(true, checkbox2.getSelection());
		setButtonSelectionWithEvents(checkbox2, false);
		assertEquals(true, text1.getEnabled());
		assertEquals(false, text2.getEnabled());
		assertEquals(true, checkbox1.getSelection());
	}

	public void testScenario13() {
		// Changing the update policy to be not automatic, but on explicit
		// method call (e.g. triggered by a button click).
		getSWTObservableFactory().setUpdateTime(DataBindingContext.TIME_LATE);
		Text text = new Text(getComposite(), SWT.BORDER);
		getDbc().bind(text, new Property(adventure, "name"), null);
		// uncomment the following line to see what's happening
		// happening
		// spinEventLoop(1);
		String adventureName = adventure.getName();
		assertEquals(adventureName, text.getText());
		enterText(text, "foobar");
		// uncomment the following line to see what's happening
		// spinEventLoop(1);
		assertEquals("foobar", adventure.getName());
		adventure.setName("barfoo");
		// uncomment the following line to see what's happening
		// spinEventLoop(1);
		assertEquals("barfoo", text.getText());
	}

	public void testScenario14() {
		Text t1 = new Text(getComposite(), SWT.BORDER);
		Text t2 = new Text(getComposite(), SWT.BORDER);

		getSWTObservableFactory().setUpdateTime(DataBindingContext.TIME_EARLY);
		getDbc().bind(t1, new Property(adventure, "name"), null);
		getDbc().bind(t2, new Property(adventure, "name"), null);

		final int[] counter = { 0 };
		IObservableValue uv = (IObservableValue) getDbc().createObservable(
				new Property(adventure, "name"));
		uv.addChangeListener(new IChangeListener() {
			public void handleChange(IObservable source) {
				// Count how many times adventure has changed
				counter[0]++;
			}
		});

		String name = adventure.getName() + "Foo";
		enterText(t1, name);
		assertEquals(name, adventure.getName());
		assertEquals(name, t2.getText());
		assertTrue(counter[0] == 1);

		name = name + "Bar";
		uv.setValue(name);
		assertEquals(t1.getText(), adventure.getName());
		assertEquals(2, counter[0]);

	}

	public void testScenario15() {
		Text text = new Text(getComposite(), SWT.NONE);
		Account account = new Account();
		account.setExpiryDate(new Date());
		Binding b = getDbc().bind(text, new Property(account, "expiryDate"),
				null);
		Text errorText = new Text(getComposite(), SWT.NONE);
		getDbc().bind(errorText, b.getValidationError(),
				new BindSpec().setUpdateModel(false));
		assertEquals(null, b.getValidationError().getValue());
		enterText(text, "foo");
		assertNotNull(b.getValidationError().getValue());
	}
}
