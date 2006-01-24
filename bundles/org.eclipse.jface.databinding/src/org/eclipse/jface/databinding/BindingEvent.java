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

package org.eclipse.jface.databinding;

import java.util.HashMap;
import java.util.Map;

/**
 * The event that is passed to a #bindingEvent method of an IBindingListener.
 * 
 * @since 3.2
 */
public class BindingEvent {
	/**
	 * (Non-API Method) Construct a BindingEvent.
	 * 
	 * @param changeEvent
	 *            The ChangeEvent that is being processed.
	 * @param copyType
	 *            The type of copy that is being processed.
	 * @param pipelinePosition
	 *            The initial processing pipeline position.
	 */
	public BindingEvent(ChangeEvent changeEvent, int copyType,
			int pipelinePosition) {
		this.changeEvent = changeEvent;
		this.eventType = copyType;
		this.pipelinePosition = pipelinePosition;
		createSymbolTable();
	}
	
	/**
	 * A Map of Integer --> String mapping the integer constants for the event
	 * types defined in this class to their String symbols.
	 */
	public final Map eventConstants = new HashMap();
	
	/**
	 * A Map of Integer --> String mapping the integer constants for the pipeline
	 * events defined in this class to their String symbols.
	 */
	public final Map pipelineConstants = new HashMap();

	/**
	 * The ChangeEvent that is being processed.
	 */
	public final ChangeEvent changeEvent;

	/**
	 * The type of event that is occuring. One of the EVENT_* constants.
	 */
	public final int eventType;

	/**
	 * The position in the processing pipeline where this event is occuring. One
	 * of the PIPELINE_* constants. The order in which these events occur may be
	 * version or implementation dependent. The contract is that these events
	 * will accurately reflect the internal processing that the data binding
	 * framework is currently performing.
	 * <p>
	 * Although this value is not declared final, changing it does not have any
	 * effect.
	 */
	public int pipelinePosition;

	/**
	 * Holds the value that was retrieved from the source updatable. Setting the
	 * value of this field changes the value that will be processed by all
	 * subsequent steps in the data flow pipeline.
	 */
	public Object originalValue = null;

	/**
	 * Holds the value that will be copied into the final updatable. This value
	 * is null if the original value has not been converted into the final
	 * updatable's data type or if no conversion will be performed. Setting the
	 * value of this field changes the value that will be processed by all
	 * subsequent steps in the data flow pipeline.
	 */
	public Object convertedValue = null;

	/**
	 * A constant indicating that this event is occuring during a copy from
	 * model to target.
	 */
	public static final int EVENT_COPY_TO_TARGET = 0;

	/**
	 * A constant indicating that this event is occuring during a copy from
	 * target to model.
	 */
	public static final int EVENT_COPY_TO_MODEL = 1;

	/**
	 * A constant indicating that this event is occuring during a partial
	 * validation event.
	 */
	public static final int EVENT_PARTIAL_VALIDATE = 2;

	/**
	 * A constant indicating that this event is occuring during an element
	 * remove operation.
	 */
	public static final int EVENT_REMOVE = 3;

	/**
	 * A constant indicating that this event is occuring immedately after the
	 * value to copy has been gotten from its IUpdatable.
	 */
	public static final int PIPELINE_AFTER_GET = 0;

	/**
	 * A constant indicating that this event is occuring immedately after the
	 * value has been validated as being possible to convert to the other
	 * updatable's data type.
	 */
	public static final int PIPELINE_AFTER_VALIDATE = 1;

	/**
	 * A constant indicating that this event is occuring immedately after the
	 * original value has been converted to the other updatable's data type.
	 */
	public static final int PIPELINE_AFTER_CONVERT = 2;

	/**
	 * A constant indicating that this event is occuring immedately after the
	 * business rule validation has occured.
	 */
	public static final int PIPELINE_AFTER_BUSINESS_VALIDATE = 3;

	/**
	 * A constant indicating that this event is occuring immedately after the
	 * converted value has been set/changed on the updatable.
	 */
	public static final int PIPELINE_AFTER_CHANGE = 4;
	
	/**
	 * Creates a table of constants from this class.
	 */
	private void createSymbolTable() {
		eventConstants.put(new Integer(0), "EVENT_COPY_TO_TARGET"); //$NON-NLS-1$
		eventConstants.put(new Integer(1), "EVENT_COPY_TO_MODEL"); //$NON-NLS-1$
		eventConstants.put(new Integer(2), "EVENT_PARTIAL_VALIDATE"); //$NON-NLS-1$
		eventConstants.put(new Integer(3), "EVENT_REMOVE"); //$NON-NLS-1$
		
		pipelineConstants.put(new Integer(0), "PIPELINE_AFTER_GET"); //$NON-NLS-1$
		pipelineConstants.put(new Integer(1), "PIPELINE_AFTER_VALIDATE"); //$NON-NLS-1$
		pipelineConstants.put(new Integer(2), "PIPELINE_AFTER_CONVERT"); //$NON-NLS-1$
		pipelineConstants.put(new Integer(3), "PIPELINE_AFTER_BUSINESS_VALIDATE"); //$NON-NLS-1$
		pipelineConstants.put(new Integer(4), "PIPELINE_AFTER_CHANGE"); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("(" + eventConstants.get(new Integer(eventType)) + ", "); //$NON-NLS-1$ //$NON-NLS-2$
		result.append(pipelineConstants.get(new Integer(pipelinePosition))); //$NON-NLS-1$
		result.append("): ChangeEvent(" + changeEvent.getChangeType() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		return result.toString();
	}


}