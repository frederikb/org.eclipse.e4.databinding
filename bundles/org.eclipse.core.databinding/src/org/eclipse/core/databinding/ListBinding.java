/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matthew Hall - bugs 262221, 271148, 280341, 278550
 ******************************************************************************/

package org.eclipse.core.databinding;

import java.util.Collections;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.ObservableTracker;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffVisitor;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.internal.databinding.BindingStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

/**
 * @param <T>
 *            the type of the elements in the list on the target side
 * @param <M>
 *            the type of the elements in the list on the model side
 * @since 1.0
 * 
 */
public class ListBinding<M, T> extends
		Binding<IObservableList<M>, IObservableList<T>> {

	private UpdateListStrategy<T, M> targetToModel;
	private UpdateListStrategy<M, T> modelToTarget;
	private IObservableValue<IStatus> validationStatusObservable;
	private boolean updatingTarget;
	private boolean updatingModel;

	private IListChangeListener<T> targetChangeListener = new IListChangeListener<T>() {
		public void handleListChange(ListChangeEvent<T> event) {
			if (!updatingTarget) {
				doUpdate(getTarget(), getModel(), event.diff, targetToModel,
						false, false);
			}
		}
	};
	private IListChangeListener<M> modelChangeListener = new IListChangeListener<M>() {
		public void handleListChange(ListChangeEvent<M> event) {
			if (!updatingModel) {
				doUpdate(getModel(), getTarget(), event.diff, modelToTarget,
						false, false);
			}
		}
	};

	/**
	 * @param target
	 * @param model
	 * @param modelToTargetStrategy
	 * @param targetToModelStrategy
	 */
	public ListBinding(IObservableList<T> target, IObservableList<M> model,
			UpdateListStrategy<T, M> targetToModelStrategy,
			UpdateListStrategy<M, T> modelToTargetStrategy) {
		super(target, model);
		this.targetToModel = targetToModelStrategy;
		this.modelToTarget = modelToTargetStrategy;
		if ((targetToModel.getUpdatePolicy() & UpdateListStrategy.POLICY_UPDATE) != 0) {
			target.addListChangeListener(targetChangeListener);
		} else {
			targetChangeListener = null;
		}
		if ((modelToTarget.getUpdatePolicy() & UpdateListStrategy.POLICY_UPDATE) != 0) {
			model.addListChangeListener(modelChangeListener);
		} else {
			modelChangeListener = null;
		}
	}

	public IObservableValue<IStatus> getValidationStatus() {
		return validationStatusObservable;
	}

	protected void preInit() {
		ObservableTracker.setIgnore(true);
		try {
			validationStatusObservable = new WritableValue<IStatus>(
					context.getValidationRealm(), Status.OK_STATUS,
					IStatus.class);
		} finally {
			ObservableTracker.setIgnore(false);
		}
	}

	protected void postInit() {
		if (modelToTarget.getUpdatePolicy() == UpdateListStrategy.POLICY_UPDATE) {
			updateModelToTarget();
		}
		if (targetToModel.getUpdatePolicy() == UpdateListStrategy.POLICY_UPDATE) {
			validateTargetToModel();
		}
	}

	public void updateModelToTarget() {
		final IObservableList<M> modelList = getModel();
		modelList.getRealm().exec(new Runnable() {
			public void run() {
				ListDiff<M> diff = Diffs.computeListDiff(
						Collections.<M> emptyList(), modelList);
				doUpdate(modelList, getTarget(), diff, modelToTarget, true,
						true);
			}
		});
	}

	public void updateTargetToModel() {
		final IObservableList<T> targetList = getTarget();
		targetList.getRealm().exec(new Runnable() {
			public void run() {
				ListDiff<T> diff = Diffs.computeListDiff(
						Collections.<T> emptyList(), targetList);
				doUpdate(targetList, getModel(), diff, targetToModel, true,
						true);
			}
		});
	}

	public void validateModelToTarget() {
		// nothing for now
	}

	public void validateTargetToModel() {
		// nothing for now
	}

	/*
	 * This method may be moved to UpdateListStrategy in the future if clients
	 * need more control over how the two lists are kept in sync.
	 */
	private <S, D> void doUpdate(final IObservableList<S> source,
			final IObservableList<D> destination,
			final ListDiff<? extends S> diff,
			final UpdateListStrategy<S, D> updateListStrategy,
			final boolean explicit, final boolean clearDestination) {
		final int policy = updateListStrategy.getUpdatePolicy();
		if (policy != UpdateListStrategy.POLICY_NEVER) {
			if (policy != UpdateListStrategy.POLICY_ON_REQUEST || explicit) {
				destination.getRealm().exec(new Runnable() {
					public void run() {
						if (destination == getTarget()) {
							updatingTarget = true;
						} else {
							updatingModel = true;
						}
						final MultiStatus multiStatus = BindingStatus.ok();

						try {
							if (clearDestination) {
								destination.clear();
							}
							diff.accept(new ListDiffVisitor<S>() {
								boolean useMoveAndReplace = updateListStrategy
										.useMoveAndReplace();

								public void handleAdd(int index, S element) {
									IStatus setterStatus = updateListStrategy
											.doAdd(destination,
													updateListStrategy
															.convert(element),
													index);

									mergeStatus(multiStatus, setterStatus);
								}

								public void handleRemove(int index, S element) {
									IStatus setterStatus = updateListStrategy
											.doRemove(destination, index);

									mergeStatus(multiStatus, setterStatus);
								}

								public void handleMove(int oldIndex,
										int newIndex, S element) {
									if (useMoveAndReplace) {
										IStatus setterStatus = updateListStrategy
												.doMove(destination, oldIndex,
														newIndex);

										mergeStatus(multiStatus, setterStatus);
									} else {
										super.handleMove(oldIndex, newIndex,
												element);
									}
								}

								public void handleReplace(int index,
										S oldElement, S newElement) {
									if (useMoveAndReplace) {
										// TODO Code change to be reviewed
										IStatus setterStatus = updateListStrategy
												.doReplace(
														destination,
														index,
														updateListStrategy
																.convert(newElement));

										mergeStatus(multiStatus, setterStatus);
									} else {
										super.handleReplace(index, oldElement,
												newElement);
									}
								}
							});
							// TODO - at this point, the two lists will be out
							// of sync if an error occurred...
						} finally {
							validationStatusObservable.setValue(multiStatus);

							if (destination == getTarget()) {
								updatingTarget = false;
							} else {
								updatingModel = false;
							}
						}
					}
				});
			}
		}
	}

	/**
	 * Merges the provided <code>newStatus</code> into the
	 * <code>multiStatus</code>.
	 * 
	 * @param multiStatus
	 * @param newStatus
	 */
	/* package */void mergeStatus(MultiStatus multiStatus, IStatus newStatus) {
		if (!newStatus.isOK()) {
			multiStatus.add(newStatus);
		}
	}

	public void dispose() {
		if (targetChangeListener != null) {
			getTarget().removeListChangeListener(targetChangeListener);
			targetChangeListener = null;
		}
		if (modelChangeListener != null) {
			getModel().removeListChangeListener(modelChangeListener);
			modelChangeListener = null;
		}
		super.dispose();
	}
}
