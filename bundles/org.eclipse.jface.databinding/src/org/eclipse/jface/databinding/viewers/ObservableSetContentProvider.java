/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brad Reynolds - bug 116920
 *     Matthew Hall - bugs 215531, 226765, 222991, 238296, 266038, 283351
 *******************************************************************************/
package org.eclipse.jface.databinding.viewers;

import java.util.Set;

import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.internal.databinding.viewers.ObservableCollectionContentProvider;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.AbstractTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * A {@link IStructuredContentProvider content provider} for
 * {@link AbstractTableViewer} or {@link AbstractListViewer} that provides
 * elements of an {@link IObservableSet} when set as the viewer's input. Objects
 * of this class listen for changes to the observable set, and will insert and
 * remove viewer elements to reflect observed changes.
 * 
 * @param <E>
 * @noextend This class is not intended to be subclassed by clients.
 * @since 1.1
 */
public class ObservableSetContentProvider<E> implements
		IStructuredContentProvider {
	private ObservableCollectionContentProvider<E> impl;

	private class Impl extends ObservableCollectionContentProvider<E> implements
			ISetChangeListener<E> {
		protected Impl(Class<E> elementType,
				IViewerUpdater explicitViewerUpdater) {
			super(elementType, explicitViewerUpdater);
		}

		protected void checkInput(Object input) {
			Assert.isTrue(input instanceof IObservableSet,
					"This content provider only works with input of type IObservableSet"); //$NON-NLS-1$
		}

		protected void addCollectionChangeListener(
				IObservableCollection<?> collection) {
			((IObservableSet<E>) collection).addSetChangeListener(this);
		}

		protected void removeCollectionChangeListener(
				IObservableCollection<?> collection) {
			((IObservableSet<E>) collection).removeSetChangeListener(this);
		}

		public void handleSetChange(SetChangeEvent<E> event) {
			if (isViewerDisposed())
				return;

			Set<? extends E> removals = event.diff.getRemovals();
			Set<? extends E> additions = event.diff.getAdditions();

			knownElements.addAll(additions);
			if (realizedElements != null)
				realizedElements.removeAll(removals);

			viewerUpdater.remove(removals.toArray());
			viewerUpdater.add(additions.toArray());

			if (realizedElements != null)
				realizedElements.addAll(additions);
			knownElements.removeAll(removals);
		}
	}

	/**
	 * Constructs an ObservableSetContentProvider. Must be called from the
	 * display thread.
	 * 
	 * @deprecated use the form that takes the class of the elements provided by
	 *             the content provider
	 */
	// OK to ignore warnings in deprecated method
	@SuppressWarnings("unchecked")
	public ObservableSetContentProvider() {
		// We are not passed the element type class so we can't safely
		// cast the type. We do the best we can which is to set Object.class
		// so at least the cast won't fail.
		this((Class<E>) Object.class, null);
	}

	/**
	 * Constructs an ObservableSetContentProvider. Must be called from the
	 * display thread.
	 * 
	 * @param elementType
	 * @since 1.7
	 */
	public ObservableSetContentProvider(Class<E> elementType) {
		this(elementType, null);
	}

	/**
	 * Constructs an ObservableSetContentProvider with the given viewer updater.
	 * Must be called from the display thread.
	 * 
	 * @param viewerUpdater
	 *            the viewer updater to use when elements are added or removed
	 *            from the input observable set.
	 * @since 1.3
	 * @deprecated use the form that takes the class of the elements provided by
	 *             the content provider
	 */
	// OK to ignore warnings in deprecated method
	@SuppressWarnings("unchecked")
	public ObservableSetContentProvider(IViewerUpdater viewerUpdater) {
		// We are not passed the element type class so we can't safely
		// cast the type. We do the best we can which is to set Object.class
		// so at least the cast won't fail.
		impl = new Impl((Class<E>) Object.class, viewerUpdater);
	}

	/**
	 * Constructs an ObservableSetContentProvider with the given viewer updater.
	 * Must be called from the display thread.
	 * 
	 * @param elementType
	 * @param viewerUpdater
	 *            the viewer updater to use when elements are added or removed
	 *            from the input observable set.
	 * @since 1.7
	 */
	public ObservableSetContentProvider(Class<E> elementType,
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
	 * @return unmodifiable set of items that will need labels
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
