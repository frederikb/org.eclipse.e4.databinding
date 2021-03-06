/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bug 195222, 263413
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.viewers;

import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.jface.databinding.viewers.ViewerValueProperty;
import org.eclipse.jface.viewers.Viewer;

/**
 * @since 3.3
 * 
 */
public class ViewerInputProperty extends ViewerValueProperty<Viewer> {
	public Object getValueType() {
		return null;
	}

	protected Object doGetValue(Viewer source) {
		return source.getInput();
	}

	protected void doSetValue(Viewer source, Object value) {
		source.setInput(value);
	}

	public INativePropertyListener<Viewer> adaptListener(
			ISimplePropertyListener<ValueDiff<Object>> listener) {
		return null;
	}

	protected void doAddListener(Viewer source,
			INativePropertyListener<Viewer> listener) {
	}

	protected void doRemoveListener(Viewer source,
			INativePropertyListener<Viewer> listener) {
	}

	public String toString() {
		return "Viewer.input"; //$NON-NLS-1$
	}
}
