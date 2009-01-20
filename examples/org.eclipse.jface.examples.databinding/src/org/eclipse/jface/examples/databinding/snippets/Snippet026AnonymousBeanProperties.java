/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 247997)
 ******************************************************************************/

package org.eclipse.jface.examples.databinding.snippets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.SetDiff;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.SimplePropertyEvent;
import org.eclipse.core.databinding.property.set.DelegatingSetProperty;
import org.eclipse.core.databinding.property.set.ISetProperty;
import org.eclipse.core.databinding.property.set.SimpleSetProperty;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @since 3.2
 * 
 */
public class Snippet026AnonymousBeanProperties {
	private ComboViewer statusViewer;
	private Combo combo;
	private Text nameText;
	private TreeViewer contactViewer;

	public static void main(String[] args) {
		Display display = new Display();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					Snippet026AnonymousBeanProperties window = new Snippet026AnonymousBeanProperties();
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private ApplicationModel model;
	private Shell shell;
	private Tree tree;

	// Minimal JavaBeans support
	public static abstract class AbstractModelObject {
		private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
				this);

		public void addPropertyChangeListener(PropertyChangeListener listener) {
			propertyChangeSupport.addPropertyChangeListener(listener);
		}

		public void addPropertyChangeListener(String propertyName,
				PropertyChangeListener listener) {
			propertyChangeSupport.addPropertyChangeListener(propertyName,
					listener);
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
	}

	public static class ContactGroup extends AbstractModelObject implements
			Comparable {
		private String name;
		private Set contacts = new TreeSet();

		ContactGroup(String name) {
			this.name = checkNull(name);
		}

		private String checkNull(String string) {
			if (string == null)
				throw new NullPointerException();
			return string;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			firePropertyChange("name", this.name, this.name = checkNull(name));
		}

		public Set getContacts() {
			return new TreeSet(contacts);
		}

		public void addContact(Contact contact) {
			Set oldValue = getContacts();
			contacts.add(contact);
			Set newValue = getContacts();
			firePropertyChange("contacts", oldValue, newValue);
		}

		public void removeContact(Contact contact) {
			Set oldValue = getContacts();
			contacts.remove(contact);
			Set newValue = getContacts();
			firePropertyChange("contacts", oldValue, newValue);
		}

		public int compareTo(Object o) {
			ContactGroup that = (ContactGroup) o;
			return this.name.compareTo(that.name);
		}
	}

	public static class Contact extends AbstractModelObject implements
			Comparable {
		private String name;
		private String status;

		private String checkNull(String string) {
			if (string == null)
				throw new NullPointerException();
			return string;
		}

		public Contact(String name, String status) {
			this.name = checkNull(name);
			this.status = checkNull(status);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			firePropertyChange("name", this.name, this.name = checkNull(name));
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			firePropertyChange("status", this.status,
					this.status = checkNull(status));
		}

		public int compareTo(Object o) {
			Contact that = (Contact) o;
			int result = this.name.compareTo(that.name);
			if (result == 0)
				result = this.status.compareTo(that.status);
			return result;
		}
	}

	public static class ApplicationModel extends AbstractModelObject {
		private Set groups = new TreeSet();

		public Set getGroups() {
			return new TreeSet(groups);
		}

		public void setGroups(Set groups) {
			Set oldValue = getGroups();
			this.groups = new TreeSet(groups);
			Set newValue = getGroups();
			firePropertyChange("groups", oldValue, newValue);
		}
	}

	/**
	 * Set property for the "contacts" property of a ContactGroup. Since
	 * ContactGroup does not have a setContacts() method we have to write our
	 * own property to apply set changes incrementally through the addContact
	 * and removeContact methods.
	 */
	public static class ContactGroupContactsProperty extends SimpleSetProperty {
		public Object getElementType() {
			return Contact.class;
		}

		protected Set doGetSet(Object source) {
			if (source == null)
				return Collections.EMPTY_SET;
			return ((ContactGroup) source).getContacts();
		}

		protected void doSetSet(Object source, Set set, SetDiff diff) {
			ContactGroup group = (ContactGroup) source;
			for (Iterator it = diff.getRemovals().iterator(); it.hasNext();) {
				Contact contact = (Contact) it.next();
				group.removeContact(contact);
			}
			for (Iterator it = diff.getAdditions().iterator(); it.hasNext();) {
				Contact contact = (Contact) it.next();
				group.addContact(contact);
			}
		}

		public INativePropertyListener adaptListener(
				final ISimplePropertyListener listener) {
			return new Listener(listener);
		}

		private class Listener implements INativePropertyListener,
				PropertyChangeListener {
			private final ISimplePropertyListener listener;

			Listener(ISimplePropertyListener listener) {
				this.listener = listener;
			}

			public void propertyChange(java.beans.PropertyChangeEvent evt) {
				listener.handlePropertyChange(new SimplePropertyEvent(evt
						.getSource(), ContactGroupContactsProperty.this, null));
			}
		}

		protected void doAddListener(Object source,
				INativePropertyListener listener) {
			if (source != null) {
				((ContactGroup) source).addPropertyChangeListener("contacts",
						(Listener) listener);
			}
		}

		protected void doRemoveListener(Object source,
				INativePropertyListener listener) {
			if (source != null) {
				((ContactGroup) source).removePropertyChangeListener(
						"contacts", (Listener) listener);
			}
		}
	}

	public void open() {
		model = createDefaultModel();

		final Display display = Display.getDefault();
		createContents();
		bindUI();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private static final String[] statuses = new String[] { "Online", "Idle",
			"Busy", "Offline" };

	/**
	 * @return
	 */
	private ApplicationModel createDefaultModel() {
		ContactGroup swtGroup = new ContactGroup("SWT");
		swtGroup.addContact(new Contact("Steve Northover", "Busy"));
		swtGroup.addContact(new Contact("Grant Gayed", "Online"));
		swtGroup.addContact(new Contact("Veronika Irvine", "Offline"));
		swtGroup.addContact(new Contact("Mike Wilson", "Online"));
		swtGroup.addContact(new Contact("Christophe Cornu", "Idle"));
		swtGroup.addContact(new Contact("Lynne Kues", "Online"));
		swtGroup.addContact(new Contact("Silenio Quarti", "Idle"));

		ContactGroup jdbGroup = new ContactGroup("JFace Data Binding");
		jdbGroup.addContact(new Contact("Boris Bokowski", "Online"));
		jdbGroup.addContact(new Contact("Matthew Hall", "Idle"));

		Set groups = new TreeSet();
		groups.add(swtGroup);
		groups.add(jdbGroup);
		ApplicationModel model = new ApplicationModel();
		model.setGroups(groups);

		return model;
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(379, 393);
		shell.setText("Snippet026AnonymousBeanProperties");
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		shell.setLayout(gridLayout);

		contactViewer = new TreeViewer(shell, SWT.BORDER);
		tree = contactViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));

		final TreeColumn nameColumn = new TreeColumn(tree, SWT.NONE);
		nameColumn.setWidth(163);
		nameColumn.setText("Name");

		final TreeColumn newColumnTreeColumn = new TreeColumn(tree, SWT.NONE);
		newColumnTreeColumn.setWidth(100);
		newColumnTreeColumn.setText("Status");

		final Label nameLabel = new Label(shell, SWT.NONE);
		nameLabel.setText("Name");

		nameText = new Text(shell, SWT.BORDER);
		final GridData gd_nameText = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		nameText.setLayoutData(gd_nameText);

		final Label statusLabel = new Label(shell, SWT.NONE);
		statusLabel.setLayoutData(new GridData());
		statusLabel.setText("Status");

		statusViewer = new ComboViewer(shell, SWT.READ_ONLY);
		combo = statusViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	}

	private void bindUI() {
		ISetProperty treeChildrenProperty = new DelegatingSetProperty() {
			ISetProperty modelGroups = BeanProperties.set(
					ApplicationModel.class, "groups");
			ISetProperty groupContacts = BeanProperties.set(ContactGroup.class,
					"contacts");

			protected ISetProperty doGetDelegate(Object source) {
				if (source instanceof ApplicationModel)
					return modelGroups;
				if (source instanceof ContactGroup)
					return groupContacts;
				return null;
			}
		};

		ObservableSetTreeContentProvider cp = new ObservableSetTreeContentProvider(
				treeChildrenProperty.setFactory(), null);
		contactViewer.setContentProvider(cp);

		IObservableMap[] labelMaps = BeansObservables.observeMaps(cp
				.getKnownElements(), new String[] { "name", "status" });
		contactViewer
				.setLabelProvider(new ObservableMapLabelProvider(labelMaps));

		contactViewer.setInput(model);

		contactViewer.expandAll();

		IObservableValue selection = ViewersObservables
				.observeSingleSelection(contactViewer);

		DataBindingContext dbc = new DataBindingContext();

		dbc.bindValue(SWTObservables.observeText(nameText, SWT.Modify),
				BeanProperties.value("name").observeDetail(selection), null,
				null);

		statusViewer.setContentProvider(new ArrayContentProvider());
		statusViewer.setInput(statuses);

		dbc.bindValue(ViewersObservables.observeSingleSelection(statusViewer),
				BeanProperties.value("status").observeDetail(selection), null,
				null);
	}
}
