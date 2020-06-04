/*******************************************************************************
 * Copyright (c) 2000, 2019 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.internal.cocoa;

public class NSFontDescriptor extends NSObject {

public NSFontDescriptor() {
	super();
}

public NSFontDescriptor(long id) {
	super(id);
}

public NSFontDescriptor(id id) {
	super(id);
}

public static NSFontDescriptor fontDescriptorWithFontAttributes(NSDictionary attributes) {
	long result = OS.objc_msgSend(OS.class_NSFontDescriptor, OS.sel_fontDescriptorWithFontAttributes_, attributes != null ? attributes.id : 0);
	return result != 0 ? new NSFontDescriptor(result) : null;
}

}
