/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.viewers;

import org.eclipse.core.databinding.observable.set.DecoratingObservableSet;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.jface.databinding.viewers.IViewerObservableSet;
import org.eclipse.jface.viewers.Viewer;

/**
 * @param <S>
 * @param <E>
 * @since 3.3
 * 
 */
public class ViewerObservableSetDecorator<S extends Viewer, E> extends
		DecoratingObservableSet<E> implements IViewerObservableSet<S, E> {
	private final S viewer;

	/**
	 * @param decorated
	 * @param viewer
	 */
	public ViewerObservableSetDecorator(IObservableSet<E> decorated, S viewer) {
		super(decorated, true);
		this.viewer = viewer;
	}

	public S getViewer() {
		return viewer;
	}

}
