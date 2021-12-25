/*******************************************************************************
 * Copyright (c) 2000, 2021 IBM Corporation and others.
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

public class NSMenuItemCell extends NSObject {

public NSMenuItemCell() {
	super();
}

public NSMenuItemCell(long id) {
	super(id);
}

public NSMenuItemCell(id id) {
	super(id);
}

public void drawBorderAndBackgroundWithFrame(NSRect cellFrame, NSView controlView) {
	OS.objc_msgSend(this.id, OS.sel_drawBorderAndBackgroundWithFrame_inView_, cellFrame, controlView != null ? controlView.id : 0);
}

}
