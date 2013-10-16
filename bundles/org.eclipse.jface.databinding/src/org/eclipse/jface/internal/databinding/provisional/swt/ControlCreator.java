package org.eclipse.jface.internal.databinding.provisional.swt;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.widgets.Control;

/**
 * This class operates in conjunction with UpdatingComposite. The code in
 * UpdatingComposite::createControls method implementations must not create
 * child controls directly. It must call the <code>create</code> method of an
 * implementation of this class.
 * 
 * @param <T>
 *            the type of control being created
 * @author Nigel Westbury
 */
public abstract class ControlCreator<T extends Control> {

	UpdatingComposite parent;

	Set<T> controls = new HashSet<T>();

	protected ControlCreator(UpdatingComposite parent) {
		this.parent = parent;
	}

	/**
	 * This method should be called only by the user's implementation of the
	 * createControls method in UpdatingComposite.
	 * <P>
	 * We perhaps don't need this at all and users could just call the create
	 * method in the composite class directly.
	 * 
	 * @return an appropriate control, which may be created by this method or
	 *         may be re-used
	 */
	public T create() {
		// pass this request on to the composite.
		return parent.create(this);

	}

	/**
	 * This method should be called only by internal UpdatingComposite code.
	 * <P>
	 * This method creates a new control and will only be called when no control
	 * can be re-used.
	 * 
	 * @return a control of appropriate type
	 */
	protected abstract T createControl();

	/**
	 * This method is called by the UpdatingComposite to tell this class that
	 * the control has been disposed.
	 * 
	 * @param control
	 */
	void remove(Control control) {
		controls.remove(control);
	}

	/**
	 * Given that this control was created by this control creator, return it
	 * typed accordingly.
	 * 
	 * @param control
	 *            the control to be returned
	 * @return the same object as <code>control</code>
	 */
	public T typeControl(Control control) {
		for (T eachControl : controls) {
			if (eachControl == control) {
				return eachControl;
			}
		}
		throw new RuntimeException("Control not in managed list"); //$NON-NLS-1$
	}
}
