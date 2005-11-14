/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.databinding;

import org.eclipse.jface.databinding.internal.DataBindingContext;
import org.eclipse.jface.databinding.internal.beans.BeanUpdatableFactory;
import org.eclipse.jface.databinding.internal.swt.SWTUpdatableFactory;
import org.eclipse.jface.databinding.internal.viewers.JFaceUpdatableFactory;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;

/**
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will remain
 * unchanged during the 3.2 release cycle. Please do not use this API without
 * consulting with the Platform/UI team.
 * </p>
 * 
 * @since 3.2
 * 
 */
public class DataBinding {

	/**
	 * Applies to Viewers
	 */
	public static final String CONTENT = "content"; //$NON-NLS-1$

	/**
	 * Applies to Control
	 */
	public static final String ENABLED = "enabled"; //$NON-NLS-1$
	
	/**
	 * Updatable factory supporting JFace Components
	 */
	private static final IUpdatableFactory jFaceFactory = new JFaceUpdatableFactory();
	
	/**
	 * Updatable factory supporting SWT widgets
	 */
	private static final IUpdatableFactory swtFactory = new SWTUpdatableFactory();
	
	/**
	 * Updatable factory supporting POJO
	 */
	private static final IUpdatableFactory javaBeanFactory = new BeanUpdatableFactory();
	

	/**
	 * Constant denoting the factory that supports binding to SWT controls. This
	 * factory supports the following description objects:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Text - denotes the text's text property</li>
	 * <li>org.eclipse.swt.widgets.Button - denotes the button's selection
	 * property</li>
	 * <li>org.eclipse.swt.widgets.Combo - denotes the combo's items collection</li>
	 * <li>org.eclipse.swt.widgets.CCombo - denotes the ccombo's items
	 * collection</li>
	 * <li>org.eclipse.swt.widgets.List - denotes the list's items collection</li>
	 * <li>org.eclipse.jface.databinding.PropertyDescription - depending on the
	 * property description's object and property ID:
	 * <ul>
	 * <li>object instanceof Widget, property ID is SWT_ENABLED - denoting the
	 * widget's enabled property</li>
	 * <li>object instanceof Spinner, property ID is SWT_SELECTION - denoting
	 * the spinner's selection property</li>
	 * <li>object instanceof Spinner, property ID is SWT_MINIMUM - denoting the
	 * spinner's minimum property</li>
	 * <li>object instanceof Spinner, property ID is SWT_MAXIMUM - denoting the
	 * spinner's maximum property</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * TODO complete the list
	 */

	/**
	 * Applies to
	 */
	public static final String ITEMS = "items"; //$NON-NLS-1$

	/**
	 * Applies to Spinner
	 */
	public static final String MAX = "max"; //$NON-NLS-1$

	/**
	 * Applies to Spinner
	 */
	public static final String MIN = "min"; //$NON-NLS-1$

	/**
	 * Applies to Spinner, Button
	 */
	public static final String SELECTION = "selection"; //$NON-NLS-1$

	/**
	 * Applies to Text, Label, Combo
	 */
	public static final String TEXT = "text"; //$NON-NLS-1$

	/**
	 * @param factories
	 * @return a data binding context
	 */
	public static IDataBindingContext createContext(IUpdatableFactory[] factories) {
		DataBindingContext result = new DataBindingContext();
		if (factories!=null)
		  for (int i = 0; i < factories.length; i++) {			
				result.addUpdatableFactory(factories[i]);
		  }
		return result;
	}

	/**
	 * Creates and returns a data binding context
	 * 
	 * @param control
	 * @return
	 */
	public static IDataBindingContext createContext(Control control) {
		return createContext(control, new IUpdatableFactory[] {getJavaBeanFactory(), getSwtFactory(), getJFaceFactory()});
	}

	/**	
	 * @param control
	 * @param factories
	 * @return
	 */
	public static IDataBindingContext createContext(Control control, IUpdatableFactory[] factories) {
		final IDataBindingContext result = createContext(factories);
		control.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				result.dispose();
			}
		});
		return result;
	}

	public static IUpdatableFactory getJFaceFactory() {
		return jFaceFactory;
	}

	public static IUpdatableFactory getSwtFactory() {
		return swtFactory;
	}

	public static IUpdatableFactory getJavaBeanFactory() {
		return javaBeanFactory;
	}

}
