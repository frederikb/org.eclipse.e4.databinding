/*******************************************************************************
 * Copyright (c) 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 263709)
 ******************************************************************************/

package org.eclipse.core.internal.databinding;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.value.SimpleValueProperty;

/**
 * @since 3.3
 * 
 */
public class BindingModelProperty extends
		SimpleValueProperty<Binding<?, ?>, IObservable> {
	public Object getValueType() {
		return IObservable.class;
	}

	public Class<IObservable> getValueClass() {
		return IObservable.class;
	}

	protected IObservable doGetValue(Binding<?, ?> source) {
		return source.getModel();
	}

	protected void doSetValue(Binding<?, ?> source, IObservable value) {
		// no setter API
	}

	public INativePropertyListener<Binding<?, ?>> adaptListener(
			ISimplePropertyListener<ValueDiff<IObservable>> listener) {
		// no listener API
		return null;
	}

	protected void doAddListener(Binding<?, ?> source,
			INativePropertyListener<Binding<?, ?>> listener) {
	}

	protected void doRemoveListener(Binding<?, ?> source,
			INativePropertyListener<Binding<?, ?>> listener) {
	}

	public String toString() {
		return "Binding#model <IObservable>"; //$NON-NLS-1$
	}
}
