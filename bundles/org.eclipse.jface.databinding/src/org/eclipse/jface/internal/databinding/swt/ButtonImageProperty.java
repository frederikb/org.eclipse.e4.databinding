/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 213893)
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;

/**
 * @since 3.3
 * 
 */
public class ButtonImageProperty extends WidgetImageValueProperty<Button> {
	Image doGetImageValue(Button source) {
		return source.getImage();
	}

	void doSetImageValue(Button source, Image value) {
		source.setImage(value);
	}

	public String toString() {
		return "Button.image <Image>"; //$NON-NLS-1$
	}
}
