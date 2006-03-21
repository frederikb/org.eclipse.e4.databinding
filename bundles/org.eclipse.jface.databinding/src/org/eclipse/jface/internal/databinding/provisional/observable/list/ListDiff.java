/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.provisional.observable.list;

import org.eclipse.jface.internal.databinding.provisional.observable.IDiff;

/**
 * Object describing a diff between two lists.
 * 
 * @since 1.0
 */
public abstract class ListDiff implements IDiff {

	/**
	 * Returns a list of ListDiffEntry
	 * 
	 * @return a list of ListDiffEntry
	 */
	public abstract ListDiffEntry[] getDifferences();
}