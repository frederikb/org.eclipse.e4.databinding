/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.jface.databinding.viewers;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.viewers.Viewer;

/**
 * {@link IObservableList} observing a JFace Viewer.
 * 
 * @param <S>
 * 
 * @since 1.2
 * 
 */
public interface IViewerObservableList<S extends Viewer> extends
		IObservableList<Object>, IViewerObservable<S> {
}
