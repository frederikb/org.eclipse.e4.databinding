/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.internal.databinding;

import org.eclipse.jface.databinding.BindingEvent;
import org.eclipse.jface.databinding.BindingException;
import org.eclipse.jface.databinding.ChangeEvent;
import org.eclipse.jface.databinding.IBindSpec;
import org.eclipse.jface.databinding.IChangeListener;
import org.eclipse.jface.databinding.IUpdatableValue;
import org.eclipse.jface.databinding.converter.IConverter;
import org.eclipse.jface.databinding.validator.IValidator;

/**
 * @since 3.2
 * 
 */
public class ValueBinding extends Binding {

	private final IUpdatableValue target;

	private final IUpdatableValue model;

	private IValidator validator;

	private IConverter converter;

	private boolean updating = false;

	/**
	 * @param context
	 * @param target
	 * @param model
	 * @param bindSpec
	 */
	public ValueBinding(DataBindingContext context, IUpdatableValue target,
			IUpdatableValue model, IBindSpec bindSpec) {
		super(context);
		this.target = target;
		this.model = model;
		converter = bindSpec.getConverter();
		if (converter == null) {
			throw new BindingException("Missing converter from " + target.getValueType() + " to " + model.getValueType()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (!converter.getModelType().isAssignableFrom(model.getValueType())) {
			throw new BindingException(
					"Converter does not apply to model type. Expected: " + model.getValueType() + ", actual: " + converter.getModelType()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (!converter.getTargetType().isAssignableFrom(target.getValueType())) {
			throw new BindingException(
					"Converter does not apply to target type. Expected: " + target.getValueType() + ", actual: " + converter.getTargetType()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		validator = bindSpec.getValidator();
		if (validator == null) {
			throw new BindingException("Missing validator"); //$NON-NLS-1$
		}
		target.addChangeListener(targetChangeListener);
		model.addChangeListener(modelChangeListener);
	}

	private final IChangeListener targetChangeListener = new IChangeListener() {
		public void handleChange(ChangeEvent changeEvent) {
			if (updating) 
				return;
			if (changeEvent.getChangeType() == ChangeEvent.VERIFY) {
				// we are notified of a pending change, do validation
				// and veto the change if it is not valid
				Object value = changeEvent.getNewValue();
				String partialValidationError = validator
						.isPartiallyValid(value);
				context.updatePartialValidationError(this,
						partialValidationError);
				if (partialValidationError != null) {
					changeEvent.setVeto(true);
				}
			} else {
				// the target (usually a widget) has changed, validate
				// the value and update the source
				updateModelFromTarget();
			}
		}
	};
	
	private IChangeListener modelChangeListener = new IChangeListener() {
		public void handleChange(ChangeEvent changeEvent) {
			if (updating) 
				return;
			// The model has changed so we must update the target
			if (changeEvent.getChangeType() == ChangeEvent.VERIFY) {
			} else {
				updateTargetFromModel();
			}
		}
	};

	/**
	 * This also does validation.
	 */
	public void updateModelFromTarget() {
		BindingEvent e = new BindingEvent();
		e.copyType = BindingEvent.COPY_TO_MODEL;
		e.originalValue = target.getValue();
		e.pipelinePosition = BindingEvent.PIPELINE_AFTER_GET;
		fireBindingEvent(e);
		
		String validationError = doValidate(e.originalValue);
		if (validationError != null) {
			context.updatePartialValidationError(targetChangeListener, validationError);
			return;
		}
		e.pipelinePosition = BindingEvent.PIPELINE_AFTER_VALIDATE;
		fireBindingEvent(e);
		
		try {
			updating = true;
			
			e.convertedValue = converter.convertTargetToModel(e.originalValue);
			e.pipelinePosition = BindingEvent.PIPELINE_AFTER_CONVERT;
			fireBindingEvent(e);
			
			// FIXME: Need to add business validation
			e.pipelinePosition = BindingEvent.PIPELINE_AFTER_BUSINESS_VALIDATE;
			fireBindingEvent(e);
			
			model.setValue(e.convertedValue);
			e.pipelinePosition = BindingEvent.PIPELINE_AFTER_SET;
			fireBindingEvent(e);
		} catch (Exception ex) {
			context.updateValidationError(targetChangeListener, BindingMessages
					.getString("ValueBinding_ErrorWhileSettingValue")); //$NON-NLS-1$
		} finally {
			updating = false;
		}
	}

	private String doValidate(Object value) {
		String validationError = validator.isValid(value);
		context.updatePartialValidationError(targetChangeListener, null);
		context.updateValidationError(targetChangeListener, validationError);
		return validationError;
	}

	/**
	 * 
	 */
	public void updateTargetFromModel() {
		try {
			updating = true;
			BindingEvent e = new BindingEvent();
			e.copyType = BindingEvent.COPY_TO_TARGET;
			e.originalValue = model.getValue();
			e.pipelinePosition = BindingEvent.PIPELINE_AFTER_GET;
			fireBindingEvent(e);
			
			e.convertedValue = converter.convertModelToTarget(e.originalValue);
			e.pipelinePosition = BindingEvent.PIPELINE_AFTER_CONVERT;
			fireBindingEvent(e);
			
			target.setValue(e.convertedValue);
			e.pipelinePosition = BindingEvent.PIPELINE_AFTER_SET;
			fireBindingEvent(e);
			
			doValidate(target.getValue());
			e.pipelinePosition = BindingEvent.PIPELINE_AFTER_VALIDATE;
			fireBindingEvent(e);
		} finally {
			updating = false;
		}
	}
}