/*******************************************************************************
 * Copyright (c) 2006 The Pampered Chef and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Pampered Chef - initial API and implementation
 ******************************************************************************/

package org.eclipse.jface.examples.databinding.compositetable.timeeditor;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;

/**
 * This class represents an event that can be displayed on a calendar.
 * 
 * @since 3.2
 */
public class Calendarable {

	private Date startTime = null;
	
	/**
	 * Gets the event's start time.
	 * 
	 * @return the start time for the event.
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * Sets the event's start time.
	 * 
	 * @param startTime the event's start time.
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	private Date endTime = null;


	/**
	 * Returns the event's end time.
	 * 
	 * @return the event's end time.
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * Sets the event's end time.
	 * 
	 * @param endTime the event's end time.
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	private Image image;

	/**
	 * Return the IEvent's image or <code>null</code>.
	 * 
	 * @return the image of the label or null
	 */
	public Image getImage() {
		return this.image;
	}

	/**
	 * Set the IEvent's Image.
	 * The value <code>null</code> clears it.
	 * 
	 * @param image the image to be displayed in the label or null
	 * 
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	private String text = null;

	/**
	 * Returns the widget text.
	 * <p>
	 * The text for a text widget is the characters in the widget, or
	 * an empty string if this has never been set.
	 * </p>
	 *
	 * @return the widget text
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the contents of the receiver to the given string. If the receiver has style
	 * SINGLE and the argument contains multiple lines of text, the result of this
	 * operation is undefined and may vary from platform to platform.
	 *
	 * @param string the new text
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setText(String string) {
		this.text = string;
	}
	
	/**
	 * Reset the event object to its default state.  This method does not
	 * dispose any Color or Image objects that may be set into it.
	 */
	public void reset() {
		text = null;
		startTime = null;
		endTime = null;
		image = null;
	}

	/**
	 * Disposes of the operating system resources associated with
	 * the receiver and all its descendents. After this method has
	 * been invoked, the receiver and all descendents will answer
	 * <code>true</code> when sent the message <code>isDisposed()</code>.
	 * Any internal connections between the widgets in the tree will
	 * have been removed to facilitate garbage collection.
	 *
	 * @see #addDisposeListener
	 * @see #removeDisposeListener
	 */
	public void dispose() {
		fireDisposeEvent();
	}
	
	private List disposeListeners = new ArrayList();

	private void fireDisposeEvent() {
		for (Iterator disposeListenerIter = disposeListeners.iterator(); disposeListenerIter.hasNext();) {
			DisposeListener listener = (DisposeListener) disposeListenerIter.next();
			listener.widgetDisposed(this);
		}
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notifed when the widget is disposed. When the widget is
	 * disposed, the listener is notified by sending it the
	 * <code>widgetDisposed()</code> message.
	 *
	 * @param listener the listener which should be notified when the receiver is disposed
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see DisposeListener
	 * @see #removeDisposeListener
	 */
	public void addDisposeListener(DisposeListener listener) {
		disposeListeners.add(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notifed when the widget is disposed.
	 * @param listener the listener which should no longer be notified when the receiver is disposed
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 *
	 * @see DisposeListener
	 * @see #addDisposeListener
	 */	
	public void removeDisposeListener(DisposeListener listener) {
		disposeListeners.remove(listener);
	}

}