/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 207858)
 *     Matthew Hall - bugs 226765, 222991, 266038, 283351
 *******************************************************************************/

package org.eclipse.jface.databinding.viewers;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.IObservablesListener;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.eclipse.jface.internal.databinding.viewers.ObservableCollectionTreeContentProvider;
import org.eclipse.jface.internal.databinding.viewers.ViewerElementSet;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * An {@link ITreeContentProvider} for use with an {@link AbstractTreeViewer},
 * which uses the provided {@link IObservableFactory set factory} to obtain the
 * elements of a tree. Objects of this class listen for changes to each
 * {@link IObservableSet} created by the factory, and will insert and remove
 * viewer elements to reflect the observed changes.
 * 
 * @param <E>
 * @noextend This class is not intended to be subclassed by clients.
 * @since 1.2
 */
public class ObservableSetTreeContentProvider<E> implements
		ITreeContentProvider {
	private final ObservableCollectionTreeContentProvider<E> impl;

	private class Impl extends ObservableCollectionTreeContentProvider<E> {
		Impl(IObservableFactory<?, IObservableSet<E>> setFactory,
				TreeStructureAdvisor structureAdvisor) {
			super(setFactory, structureAdvisor);
		}

		private class SetChangeListener implements ISetChangeListener<E> {
			final Object parentElement;

			public SetChangeListener(Object parentElement) {
				this.parentElement = parentElement;
			}

			public void handleSetChange(SetChangeEvent<E> event) {
				if (isViewerDisposed())
					return;

				Set<? extends E> localAdditions = event.diff.getAdditions();
				Set<? extends E> localRemovals = event.diff.getRemovals();

				Set<E> knownElementAdditions = ViewerElementSet
						.withComparer(comparer);
				knownElementAdditions.addAll(localAdditions);
				knownElementAdditions.removeAll(knownElements);

				Set<E> knownElementRemovals = findPendingRemovals(
						parentElement, localRemovals);
				knownElementRemovals.retainAll(knownElements);

				knownElements.addAll(knownElementAdditions);
				if (realizedElements != null) {
					realizedElements.removeAll(knownElementRemovals);
				}

				for (Iterator<? extends E> iterator = localAdditions.iterator(); iterator
						.hasNext();) {
					E child = iterator.next();
					getOrCreateNode(child).addParent(parentElement);
				}

				viewerUpdater.add(parentElement, localAdditions.toArray());
				viewerUpdater.remove(parentElement, localRemovals.toArray());

				for (Iterator<? extends E> iterator = localRemovals.iterator(); iterator
						.hasNext();) {
					Object child = iterator.next();
					TreeNode childNode = getExistingNode(child);
					if (childNode != null)
						childNode.removeParent(parentElement);
				}

				if (realizedElements != null) {
					realizedElements.addAll(knownElementAdditions);
				}
				knownElements.removeAll(knownElementRemovals);
			}
		}

		protected IObservablesListener createCollectionChangeListener(
				Object parentElement) {
			return new SetChangeListener(parentElement);
		}

		protected void addCollectionChangeListener(
				IObservableCollection<E> collection,
				IObservablesListener listener) {
			IObservableSet<E> set = (IObservableSet<E>) collection;
			ISetChangeListener<E> setListener = (ISetChangeListener<E>) listener;
			set.addSetChangeListener(setListener);
		}

		protected void removeCollectionChangeListener(
				IObservableCollection<E> collection,
				IObservablesListener listener) {
			IObservableSet<E> set = (IObservableSet<E>) collection;
			ISetChangeListener<E> setListener = (ISetChangeListener<E>) listener;
			set.removeSetChangeListener(setListener);
		}
	}

	/**
	 * Constructs an ObservableListTreeContentProvider using the given list
	 * factory. Must be called from the display thread.
	 * 
	 * @param setFactory
	 *            observable factory that produces an IObservableSet of children
	 *            for a given parent element. Observable sets created by this
	 *            factory must be on the realm of the current display.
	 * @param structureAdvisor
	 *            an advisor that will be consulted from the implementations of
	 *            the {@link #getParent(Object)} and
	 *            {@link #hasChildren(Object)} methods, or <code>null</code> if
	 *            no advisor is available. It is recommended that clients pass a
	 *            non-null advisor if they can provide additional structural
	 *            information about the tree.
	 */
	public ObservableSetTreeContentProvider(
			IObservableFactory<?, IObservableSet<E>> setFactory,
			TreeStructureAdvisor structureAdvisor) {
		impl = new Impl(setFactory, structureAdvisor);
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		impl.inputChanged(viewer, oldInput, newInput);
	}

	public Object[] getElements(Object inputElement) {
		return impl.getElements(inputElement);
	}

	public boolean hasChildren(Object element) {
		return impl.hasChildren(element);
	}

	public Object[] getChildren(Object parentElement) {
		return impl.getChildren(parentElement);
	}

	public Object getParent(Object element) {
		return impl.getParent(element);
	}

	/**
	 * Disposes of this content provider. This is called by the viewer when a
	 * content provider is replaced, or when the viewer itself is disposed.
	 * <p>
	 * The viewer should not be updated during this call, as it is in the
	 * process of being disposed.
	 * </p>
	 * <p>
	 * <em>Note:</em> Data binding content providers become unusable on
	 * disposal.
	 * </p>
	 */
	public void dispose() {
		impl.dispose();
	}

	/**
	 * Returns the set of elements known to this content provider. Label
	 * providers may track this set if they need to be notified about additions
	 * before the viewer sees the added element, and notified about removals
	 * after the element was removed from the viewer. This is intended for use
	 * by label providers, as it will always return the items that need labels.
	 * 
	 * @return readableSet of items that will need labels
	 */
	public IObservableSet<E> getKnownElements() {
		return impl.getKnownElements();
	}

	/**
	 * Returns the set of known elements which have been realized in the viewer.
	 * Clients may track this set in order to perform custom actions on elements
	 * while they are known to be present in the viewer.
	 * 
	 * @return the set of known elements which have been realized in the viewer.
	 * @since 1.3
	 */
	public IObservableSet<E> getRealizedElements() {
		return impl.getRealizedElements();
	}
}