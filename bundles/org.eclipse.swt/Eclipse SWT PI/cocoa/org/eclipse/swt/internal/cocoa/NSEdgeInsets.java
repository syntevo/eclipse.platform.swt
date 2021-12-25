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

public class NSEdgeInsets {
	/** @field cast=(CGFloat) */
	public double top;
	/** @field cast=(CGFloat) */
	public double left;
	/** @field cast=(CGFloat) */
	public double bottom;
	/** @field cast=(CGFloat) */
	public double right;
	public static final int sizeof = OS.NSEdgeInsets_sizeof();
}
