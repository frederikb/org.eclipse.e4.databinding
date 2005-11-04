package org.eclipse.jface.tests.binding.scenarios.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ModelObject {
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);
	private String id;

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}

	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue,
				newValue);
	}

	public void setId(String string) {
		Object oldValue = id;
		id = string;
		firePropertyChange("id", oldValue, id);
	}
	
}
