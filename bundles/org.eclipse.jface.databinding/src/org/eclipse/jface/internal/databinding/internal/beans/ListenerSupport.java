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
package org.eclipse.jface.internal.databinding.internal.beans;

import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * This is a helper that will hook up and listen for <code>PropertyChangeEvent</code> events
 * for a set of target JavaBeans
 * 
 * @since 1.0
 */
public class ListenerSupport {

	/**
	 * @since 1.0
	 */
	 static class IdentityWrapper {
		final Object o;
		IdentityWrapper(Object o) {
			this.o = o;
		}
		public boolean equals(Object obj) {
			if(obj.getClass()!=IdentityWrapper.class) {
				return false;
			}
			return o==((IdentityWrapper)obj).o;
		}
		public int hashCode() {
			return System.identityHashCode(o);
		}
	}
	
	private Set elementsListenedTo = new HashSet();
	
	private PropertyChangeListener listener;
	
	/**
	 * @param listener is the callback that will be called
	 * 		when a <code>PropertyChangeEvent</code> is fired on any
	 * 		of the target objects.
	 */
	public ListenerSupport (PropertyChangeListener listener) {
		this.listener=listener;
	}
	
	
	/**
	 * Start listen to target (if it supports the JavaBean property change listener pattern)
	 * 
	 * @param target
	 */
	public void hookListener(Object target) {
		Method addPropertyChangeListenerMethod = null;
		try {
			addPropertyChangeListenerMethod = target.getClass().getMethod(
					"addPropertyChangeListener", //$NON-NLS-1$
					new Class[] { PropertyChangeListener.class });
		} catch (SecurityException e) {
			// ignore
		} catch (NoSuchMethodException e) {
			// ignore
		}
		if (addPropertyChangeListenerMethod != null) {
			try {
				addPropertyChangeListenerMethod.invoke(target,
						new Object[] { listener });
				elementsListenedTo.add(new IdentityWrapper(target));
				return;
			} catch (IllegalArgumentException e) {
				// ignore
			} catch (IllegalAccessException e) {
				// ignore
			} catch (InvocationTargetException e) {
				// ignore
			}
		}
	}
		
	/**
	 * Add listeners for new targets (those this instance of<code>ListenerSupport</code> does not 
	 * already listen to),
	 * Stop to listen to those object that this instance listen to and is one of the object in targets 
	 * 
	 * @param targets 
	 */
	public void setHookTargets(Object[] targets) {		
		Set elementsToUnhook = new HashSet(elementsListenedTo);
		if (targets!=null) {
			for (int i = 0; i < targets.length; i++) {
				Object newValue = targets[i];
				IdentityWrapper identityWrapper = new IdentityWrapper(newValue);
				if(!elementsToUnhook.remove(identityWrapper)) 				
					hookListener(newValue);
			}
		}
			
		for (Iterator it = elementsToUnhook.iterator(); it.hasNext();) {
			Object o = it.next();
			if (o.getClass()!=IdentityWrapper.class)
				o = new IdentityWrapper(o);
			elementsListenedTo.remove(o);
			unhookListener(o);
		}							
	}
	
	/**
	 * Stop listen to target
	 * 
	 * @param target
	 */
	public void unhookListener(Object target) {
		if (target.getClass()==IdentityWrapper.class)
			target = ((IdentityWrapper)target).o;
		
		Method removePropertyChangeListenerMethod = null;
		try {
			removePropertyChangeListenerMethod = target.getClass().getMethod(
					"removePropertyChangeListener", //$NON-NLS-1$
					new Class[] { PropertyChangeListener.class });
		} catch (SecurityException e) {
			// ignore
		} catch (NoSuchMethodException e) {
			// ignore
		}
		if (removePropertyChangeListenerMethod != null) {
			try {
				removePropertyChangeListenerMethod.invoke(target,
						new Object[] { listener });
				elementsListenedTo.remove(new IdentityWrapper(target));
				return;
			} catch (IllegalArgumentException e) {
				// ignore
			} catch (IllegalAccessException e) {
				// ignore
			} catch (InvocationTargetException e) {
				// ignore
			}
		}
	}
	
	
	/**
	 * 
	 */
	public void dispose() {
		if (elementsListenedTo!=null) {
			Object[] targets = elementsListenedTo.toArray();		
			for (int i = 0; i < targets.length; i++) {		
				unhookListener(targets[i]);
			}			
			elementsListenedTo=null;
			listener=null;
		}
	}
	
	/**
	 * @return elements that were registred to
	 */
	public Object[] getHookedTargets() {
		Object[] targets = null;
		if (elementsListenedTo!=null && elementsListenedTo.size()>0) {
			Object[] identityList = elementsListenedTo.toArray();
			targets = new Object[identityList.length];
			for (int i = 0; i < identityList.length; i++) 
				targets[i]=((IdentityWrapper)identityList[i]).o;							
		}
		return targets;
	}
}
