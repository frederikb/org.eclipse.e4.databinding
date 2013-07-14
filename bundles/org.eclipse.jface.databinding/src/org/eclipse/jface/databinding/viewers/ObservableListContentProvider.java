/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Tom Schindl<tom.schindl@bestsolution.at> - bugfix in: 214355
 *     Matthew Hall - bugs 215531, 226765, 222991, 238296, 226292, 266038,
 *                    283351
 *******************************************************************************/

package org.eclipse.jface.databinding.viewers;

import java.util.Set;

import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiffVisitor;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.internal.databinding.viewers.ObservableCollectionContentProvider;
import org.eclipse.jface.internal.databinding.viewers.ViewerElementSet;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.AbstractTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * A {@link IStructuredContentProvider content provider} for
 * {@link AbstractTableViewer} or {@link AbstractListViewer} that provides
 * elements of an {@link IObservableList} when set as the viewer's input.
 * Objects of this class listen for changes to the observable list, and will
 * insert and remove viewer elements to reflect observed changes.
 * 
 * @param <E>
 * @noextend This class is not intended to be subclassed by clients.
 * @since 1.1
 */
public class ObservableListContentProvider<E> implements
		IStructuredContentProvider {
	private ObservableCollectionContentProvider<E> impl;

	private class Impl extends ObservableCollectionContentProvider<E> implements
			IListChangeListener<E> {
		private Viewer viewer;

		Impl(Class<E> elementType, IViewerUpdater explicitViewerUpdater) {
			super(elementType, explicitViewerUpdater);
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			this.viewer = viewer;
			super.inputChanged(viewer, oldInput, newInput);
		}

		protected void checkInput(Object input) {
			Assert.isTrue(input instanceof IObservableList,
					"This content provider only works with input of type IObservableList"); //$NON-NLS-1$
		}

		protected void addCollectionChangeListener(
				IObservableCollection<?> collection) {
			((IObservableList<E>) collection).addListChangeListener(this);
		}

		protected void removeCollectionChangeListener(
				IObservableCollection<?> collection) {
			((IObservableList<E>) collection).removeListChangeListener(this);
		}

		public void handleListChange(ListChangeEvent<E> event) {
			if (isViewerDisposed())
				return;

			// Determine which elements were added and removed
			final Set<E> knownElementAdditions = ViewerElementSet
					.withComparer(comparer);
			final Set<E> knownElementRemovals = ViewerElementSet
					.withComparer(comparer);
			final boolean[] suspendRedraw = new boolean[] { false };
			event.diff.accept(new ListDiffVisitor<E>() {
				public void handleAdd(int index, E element) {
					knownElementAdditions.add(element);
				}

				public void handleRemove(int index, E element) {
					knownElementRemovals.add(element);
				}

				public void handleMove(int oldIndex, int newIndex, E element) {
					suspendRedraw[0] = true;
					super.handleMove(oldIndex, newIndex, element);
				}

				public void handleReplace(int index, E oldElement, E newElement) {
					suspendRedraw[0] = true;
					super.handleReplace(index, oldElement, newElement);
				}
			});
			knownElementAdditions.removeAll(knownElements);
			knownElementRemovals.removeAll(event.getObservableList());

			knownElements.addAll(knownElementAdditions);
			if (realizedElements != null) {
				realizedElements.removeAll(knownElementRemovals);
			}

			if (suspendRedraw[0])
				viewer.getControl().setRedraw(false);
			try {
				event.diff.accept(new ListDiffVisitor<E>() {
					public void handleAdd(int index, E element) {
						viewerUpdater.insert(element, index);
					}

					public void handleRemove(int index, E element) {
						viewerUpdater.remove(element, index);
					}

					public void handleReplace(int index, E oldElement,
							E newElement) {
						viewerUpdater.replace(oldElement, newElement, index);
					}

					public void handleMove(int oldIndex, int newIndex, E element) {
						viewerUpdater.move(element, oldIndex, newIndex);
					}
				});
			} finally {
				if (suspendRedraw[0])
					viewer.getControl().setRedraw(true);
			}

			if (realizedElements != null) {
				realizedElements.addAll(knownElementAdditions);
			}
			knownElements.removeAll(knownElementRemovals);
		}
	}

	/**
	 * Constructs an ObservableListContentProvider. Must be called from the
	 * display thread.
	 * 
	 * @deprecated use the form that takes the class of the elements provided by
	 *             the content provider
	 */
	// OK to ignore warnings on deprecated methods
	@SuppressWarnings("unchecked")
	public ObservableListContentProvider() {
		// We are not passed the element type class so we can't safely
		// cast the type. We do the best we can which is to set Object.class
		// so at least the cast won't fail.
		this((Class<E>) Object.class);
	}

	/**
	 * Constructs an ObservableListContentProvider. Must be called from the
	 * display thread.
	 * 
	 * @param elementType
	 * @since 1.7
	 */
	public ObservableListContentProvider(Class<E> elementType) {
		this(elementType, null);
	}

	/**
	 * Constructs an ObservableListContentProvider with the given viewer
	 * updater. Must be called from the display thread.
	 * 
	 * @param viewerUpdater
	 *            the viewer updater to use when elements are added, removed,
	 *            moved or replaced in the input observable list.
	 * @since 1.3
	 * @deprecated use the form that takes the class of the elements provided by
	 *             the content provider
	 */
	// OK to ignore warnings on deprecated methods
	@SuppressWarnings("unchecked")
	public ObservableListContentProvider(IViewerUpdater viewerUpdater) {
		// We are not passed the element type class so we can't safely
		// cast the type. We do the best we can which is to set Object.class
		// so at least the cast won't fail.
		impl = new Impl((Class<E>) Object.class, viewerUpdater);
	}

	/**
	 * Constructs an ObservableListContentProvider with the given viewer
	 * updater. Must be called from the display thread.
	 * 
	 * @param elementType
	 * @param viewerUpdater
	 *            the viewer updater to use when elements are added, removed,
	 *            moved or replaced in the input observable list.
	 * @since 1.7
	 */
	public ObservableListContentProvider(Class<E> elementType,
			IViewerUpdater viewerUpdater) {
		impl = new Impl(elementType, viewerUpdater);
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		impl.inputChanged(viewer, oldInput, newInput);
	}

	public Object[] getElements(Object inputElement) {
		return impl.getElements(inputElement);
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
