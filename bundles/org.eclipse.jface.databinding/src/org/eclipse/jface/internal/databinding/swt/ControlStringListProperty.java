/*******************************************************************************
 * Copyright (c) 2008, 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bugs 195222, 251611, 263413, 265561
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.jface.databinding.swt.WidgetListProperty;
import org.eclipse.swt.widgets.Control;

/**
 * @param <S>
 * @since 3.3
 * 
 */
public abstract class ControlStringListProperty<S extends Control> extends
		WidgetListProperty<S, String> {
	public Object getElementType() {
		return String.class;
	}

	public Class<String> getElementClass() {
		return String.class;
	}

	protected void doSetList(S source, List<String> list, ListDiff<String> diff) {
		doUpdateList(source, diff);
	}

	protected List<String> doGetList(S source) {
		String[] list = doGetStringList(source);
		return Arrays.asList(list);
	}

	abstract String[] doGetStringList(Control control);

	public INativePropertyListener<S> adaptListener(
			ISimplePropertyListener<ListDiff<String>> listener) {
		return null;
	}
}
