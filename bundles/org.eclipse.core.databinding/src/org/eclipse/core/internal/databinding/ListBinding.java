/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.databinding;

import org.eclipse.core.databinding.BindSpec;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.BindingEvent;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;

/**
 * 
 */
public class ListBinding extends Binding {

	private boolean updating = false;

	private IObservableList modelList;

	private final IObservableList targetList;

	/**
	 * @param context
	 * @param targetList
	 * @param target
	 * @param modelList
	 * @param model
	 * @param bindSpec
	 */
	public ListBinding(DataBindingContext context, IObservableList targetList,
			IObservableList modelList, BindSpec bindSpec) {
		super(context);
		this.targetList = targetList;
		this.modelList = modelList;
		partialValidationErrorObservable = new WritableValue(context
				.getValidationRealm(), null);
		validationErrorObservable = new WritableValue(context
				.getValidationRealm(), null);
		// TODO validation/conversion as specified by the bindSpec
		targetList.addListChangeListener(targetChangeListener);
		modelList.addListChangeListener(modelChangeListener);
		updateTargetFromModel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.internal.databinding.provisional.Binding#dispose()
	 */
	public void dispose() {
		targetList.removeListChangeListener(targetChangeListener);
		modelList.removeListChangeListener(modelChangeListener);
		super.dispose();
	}

	private final IListChangeListener targetChangeListener = new IListChangeListener() {
		public void handleListChange(IObservableList source, ListDiff diff) {
			if (updating) {
				return;
			}
			// TODO validation
			BindingEvent e = new BindingEvent(modelList, targetList, diff,
					BindingEvent.EVENT_COPY_TO_MODEL,
					BindingEvent.PIPELINE_AFTER_GET);
			if (failure(errMsg(fireBindingEvent(e)))) {
				return;
			}
			updating = true;
			try {
				// get setDiff from event object - might have been modified by a
				// listener
				ListDiff setDiff = (ListDiff) e.diff;
				ListDiffEntry[] differences = setDiff.getDifferences();
				for (int i = 0; i < differences.length; i++) {
					ListDiffEntry entry = differences[i];
					if (entry.isAddition()) {
						modelList.add(entry.getPosition(), entry.getElement());
					} else {
						modelList.remove(entry.getPosition());
					}
				}
				e.pipelinePosition = BindingEvent.PIPELINE_AFTER_CHANGE;
				if (failure(errMsg(fireBindingEvent(e)))) {
					return;
				}
			} finally {
				updating = false;
			}
		}
	};

	private IListChangeListener modelChangeListener = new IListChangeListener() {
		public void handleListChange(IObservableList source, ListDiff diff) {
			if (updating) {
				return;
			}
			// TODO validation
			BindingEvent e = new BindingEvent(modelList, targetList, diff,
					BindingEvent.EVENT_COPY_TO_TARGET,
					BindingEvent.PIPELINE_AFTER_GET);
			if (failure(errMsg(fireBindingEvent(e)))) {
				return;
			}
			updating = true;
			try {
				// get setDiff from event object - might have been modified by a
				// listener
				ListDiff setDiff = (ListDiff) e.diff;
				ListDiffEntry[] differences = setDiff.getDifferences();
				for (int i = 0; i < differences.length; i++) {
					ListDiffEntry entry = differences[i];
					if (entry.isAddition()) {
						targetList.add(entry.getPosition(), entry.getElement());
					} else {
						targetList.remove(entry.getPosition());
					}
				}
				e.pipelinePosition = BindingEvent.PIPELINE_AFTER_CHANGE;
				if (failure(errMsg(fireBindingEvent(e)))) {
					return;
				}
			} finally {
				updating = false;
			}
		}
	};

	private WritableValue partialValidationErrorObservable;

	private WritableValue validationErrorObservable;

	private IStatus errMsg(IStatus validationStatus) {
		partialValidationErrorObservable.setValue(null);
		validationErrorObservable.setValue(validationStatus);
		return validationStatus;
	}

	private boolean failure(IStatus errorMessage) {
		// FIXME: Need to fire a BindingEvent here
		return !errorMessage.isOK();
	}

	public void updateTargetFromModel() {
		updating = true;
		try {
			targetList.clear();
			targetList.addAll(modelList);
		} finally {
			updating = false;
		}
	}

	public IObservableValue getValidationStatus() {
		return validationErrorObservable;
	}

	public IObservableValue getPartialValidationStatus() {
		return partialValidationErrorObservable;
	}

	public void updateModelFromTarget() {
		updating = true;
		try {
			modelList.clear();
			modelList.addAll(targetList);
		} finally {
			updating = false;
		}
	}
}