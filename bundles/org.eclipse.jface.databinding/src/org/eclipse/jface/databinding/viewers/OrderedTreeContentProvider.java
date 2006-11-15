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
package org.eclipse.jface.databinding.viewers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.set.AbstractObservableSet;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.core.databinding.observable.tree.IOrderedTreeProvider;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * Converts an IOrderedTreeProvider into an ITreeContentProvider that is
 * suitable for use with a JFace TreeViewer.
 * 
 * <p>
 * This content provider works correctly with trees containing duplicate
 * elements.
 * </p>
 * 
 * @since 3.3
 */
public class OrderedTreeContentProvider implements ITreePathContentProvider {

	private HashMap mapElementToTreeNode = new HashMap();

	private LinkedList enqueuedPrefetches = new LinkedList();

	class KnownElementsSet extends AbstractObservableSet {

		/**
		 * @param elementType
		 */
		protected KnownElementsSet() {
			super(provider.getRealm());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.internal.databinding.provisional.observable.set.AbstractObservableSet#getWrappedSet()
		 */
		protected Set getWrappedSet() {
			return mapElementToTreeNode.keySet();
		}

		void doFireDiff(Set added, Set removed) {
			fireSetChange(Diffs.createSetDiff(added, removed));
		}

		void doFireStale(boolean isStale) {
			if (isStale) {
				fireStale();
			} else {
				fireSetChange(Diffs.createSetDiff(Collections.EMPTY_SET,
						Collections.EMPTY_SET));
			}
		}

		public Object getElementType() {
			return Object.class;
		}

		protected void fireSetChange(SetDiff diff) {
			super.fireSetChange(diff);
		}
	}

	KnownElementsSet elements;

	private ITreeViewerListener expandListener = new ITreeViewerListener() {
		public void treeCollapsed(TreeExpansionEvent event) {
		}

		public void treeExpanded(TreeExpansionEvent event) {
		}
	};

	private IPrefetchingTree prefetchingTree;

	private IOrderedTreeProvider provider;

	private Object pendingNode;

	private int avoidViewerUpdates;

	private TreeViewer treeViewer;

	private int staleCount = 0;

	private boolean useRefresh;

	private int maxPrefetches = -1;

	/**
	 * Constructs a content provider that will render the given tree in a
	 * TreeViewer.
	 * 
	 * @param provider
	 *            IObservableTree that provides the contents of the tree. The
	 *            given provider may optionally implement IPrefetchingTree if it
	 *            wants to selectively enable or disable prefetching from
	 *            particular nodes.
	 * @param pendingNode
	 *            element to insert whenever a node is being fetched in the
	 *            background
	 */
	public OrderedTreeContentProvider(IOrderedTreeProvider provider,
			Object pendingNode) {
		this(provider, pendingNode, false);
	}

	/**
	 * Constructs a content provider that will render the given tree in a
	 * TreeViewer.
	 * 
	 * @param provider
	 *            IObservableTree that provides the contents of the tree
	 * @param pendingNode
	 *            element to insert whenever a node is being fetched in the
	 *            background
	 * @param useRefresh
	 *            true = notify the viewer of changes by calling refresh(...),
	 *            false = notify the viewer of changes by calling add(...) and
	 *            remove(...). Using false is more efficient, but may not work
	 *            with TreeViewer subclasses.
	 */
	public OrderedTreeContentProvider(IOrderedTreeProvider provider,
			Object pendingNode, boolean useRefresh) {
		this.provider = provider;
		this.prefetchingTree = PrefetchingTree.getPrefetchingTree(provider);
		this.pendingNode = pendingNode;
		this.useRefresh = useRefresh;
		elements = new KnownElementsSet();
	}

	/**
	 * Sets the maximum number of pending prefetches.
	 * 
	 * @param maxPrefetches
	 */
	public void setMaxPrefetches(int maxPrefetches) {
		this.maxPrefetches = maxPrefetches;
	}

	/* package */IObservableList createChildList(TreePath treePath) {
		Object[] segments = new Object[treePath.getSegmentCount()];
		for (int i = 0; i < segments.length; i++) {
			segments[i] = treePath.getSegment(i);
		}
		return provider.createChildList(new org.eclipse.core.databinding.observable.tree.TreePath(segments));
	}

	/* package */void remove(Object element, List removals, boolean lastElement) {
		if (avoidViewerUpdates == 0) {
			for (Iterator iter = removals.iterator(); iter.hasNext();) {
				Object next = iter.next();

				OrderedTreeNode nextNode = (OrderedTreeNode) mapElementToTreeNode
						.get(next);
				if (nextNode != null) {
					nextNode.removeParent(element);
					removeIfUnused(nextNode);
				}
			}

			if (lastElement || useRefresh) {
				treeViewer.refresh(element);
			} else {
				treeViewer.remove(element, removals.toArray());
			}
		}
	}

	/* package */void add(TreePath element, List additions) {
		if (avoidViewerUpdates == 0) {
			// Handle new parents
			addParent(element, additions);
			if (useRefresh) {
				treeViewer.refresh(element);
			} else {
				treeViewer.add(element, additions);
			}
		}
	}

	/* package */void insert(TreePath element, Object addition, int position) {
		if (avoidViewerUpdates == 0) {
			// Handle new parents
			addParent(element, Collections.singletonList(addition));
			if (useRefresh) {
				treeViewer.refresh(element);
			} else {
				treeViewer.insert(element, addition, position);
			}
		}
	}
	
	/**
	 * Ensures that the given set of children have the given parent as one of
	 * their parents.
	 * 
	 * @param parent
	 * @param children
	 */
	private void addParent(TreePath parentPath, List children) {
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			Object next = iter.next();

			OrderedTreeNode nextNode = getNode(parentPath.createChildPath(next));
			nextNode.addParent(parentPath);
		}
	}

	/**
	 * Returns the element that should be inserted into the tree when fetching
	 * the children of the node that is both stale and empty.
	 * 
	 * @return the element that should be inserted into the tree when fetching
	 *         the children of a node that is stale and empty
	 */
	public final Object getPendingNode() {
		return pendingNode;
	}

	/**
	 * Returns the IObservableList representing the children of the given node.
	 * Never null.
	 * 
	 * @param parent
	 *            parent element. Must be a valid node from the tree.
	 * @return the list of children of the given parent node
	 */
	public IObservableList getChildrenList(TreePath parent) {
		IObservableList result = getNode(parent).getChildrenList();

		return result;
	}

	public void dispose() {
		if (treeViewer != null) {
			try {
				avoidViewerUpdates++;
				enqueuedPrefetches.clear();
				Object[] keys = mapElementToTreeNode.keySet().toArray();

				for (int i = 0; i < keys.length; i++) {
					Object key = keys[i];

					OrderedTreeNode result = (OrderedTreeNode) mapElementToTreeNode.get(key);
					if (result != null) {
						result.dispose();
					}
				}
			} finally {
				avoidViewerUpdates--;
			}
		}
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// This should only ever be called for a single viewer
		setViewer(viewer);

		if (oldInput != null && newInput != null && oldInput.equals(newInput)) {
			return;
		}

		try {
			avoidViewerUpdates++;
			removeIfUnused(oldInput);
		} finally {
			avoidViewerUpdates--;
		}
	}

	private void removeIfUnused(Object element) {
		OrderedTreeNode result = (OrderedTreeNode) mapElementToTreeNode
				.get(element);
		if (result != null && result.getParent() == null) {
			mapElementToTreeNode.remove(element);
			elements.doFireDiff(Collections.EMPTY_SET, Collections
					.singleton(element));
			result.dispose();
		}
	}

	private void setViewer(Viewer viewer) {
		if (!(viewer instanceof TreeViewer)) {
			throw new IllegalArgumentException(
					"This content provider can only be used with TreeViewers"); //$NON-NLS-1$
		}
		TreeViewer newTreeViewer = (TreeViewer) viewer;

		if (newTreeViewer != treeViewer) {
			if (treeViewer != null) {
				treeViewer.removeTreeListener(expandListener);
			}

			this.treeViewer = newTreeViewer;
			if (newTreeViewer != null) {
				newTreeViewer.addTreeListener(expandListener);
			}
		}
	}

	public Object[] getChildren(TreePath parentElement) {
		List result = getNode(parentElement).getChildren();

		addParent(parentElement, result);

		return result.toArray();
	}

	private OrderedTreeNode getNode(TreePath parentElement) {
		OrderedTreeNode result = (OrderedTreeNode) mapElementToTreeNode
				.get(parentElement);
		if (result == null) {
			result = new OrderedTreeNode(parentElement, this);
			mapElementToTreeNode.put(parentElement, result);
			elements.fireSetChange(Diffs.createSetDiff(Collections
					.singleton(parentElement), Collections.EMPTY_SET));
		}
		return result;
	}

	/**
	 * Returns the set of all elements that have been discovered in this tree so
	 * far. Callers must not dispose this set. Never null.
	 * 
	 * @return the set of all elements that have been discovered in this tree so
	 *         far.
	 */
	public IObservableSet getKnownElements() {
		return elements;
	}

	/* package */void changeStale(int staleDelta) {
		staleCount += staleDelta;
		processPrefetches();
		elements.setStale(staleCount != 0);
	}

	/**
	 * Returns the associated tree viewer.
	 * 
	 * @return the associated tree viewer
	 */
	public TreeViewer getViewer() {
		return treeViewer;
	}

	/**
	 * Returns true iff the given element is stale.
	 * 
	 * @param element
	 *            the element to query for staleness. Must exist in the tree.
	 * @return true iff the given element is stale
	 */
	public boolean isDirty(TreePath element) {
		return getChildrenList(element).isStale();
	}

	/* package */void enqueuePrefetch(OrderedTreeNode node) {
		if (prefetchingTree.shouldPrefetch(node.getElement())) {
			if (staleCount == 0) {
				// Call node.getChildren()... this will cause us to start
				// listening to the
				// node and will trigger prefetching. Don't call prefetch since
				// this method
				// is intended to be called inside getters (which will simply
				// return the
				// fetched nodes) and prefetch() is intended to be called inside
				// an asyncExec,
				// which will notify the viewer directly of the newly discovered
				// nodes.
				node.getChildren();
			} else {
				enqueuedPrefetches.add(node);
				while (maxPrefetches >= 0
						&& enqueuedPrefetches.size() > maxPrefetches) {
					enqueuedPrefetches.removeFirst();
				}
			}
		}
	}

	private void processPrefetches() {
		while (staleCount == 0 && !enqueuedPrefetches.isEmpty()) {
			OrderedTreeNode next = (OrderedTreeNode) enqueuedPrefetches.removeLast();

			// Note that we don't remove nodes from the prefetch queue when they
			// are disposed,
			// so we may encounter disposed nodes at this time.
			if (!next.isDisposed()) {
				next.prefetch();
			}
		}
	}

	public TreePath[] getParents(Object element) {
		return new TreePath[0];
	}

	public boolean hasChildren(TreePath path) {
		return getNode(path).shouldShowPlus();
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(new TreePath(new Object[]{inputElement}));
	}

}