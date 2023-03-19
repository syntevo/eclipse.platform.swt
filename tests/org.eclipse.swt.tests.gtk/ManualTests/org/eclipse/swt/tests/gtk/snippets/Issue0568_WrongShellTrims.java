/*******************************************************************************
 * Copyright (c) 2023 Syntevo and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Syntevo - initial API and implementation
 *******************************************************************************/

package org.eclipse.swt.tests.gtk.snippets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.gtk.GDK;
import org.eclipse.swt.internal.gtk.GTK;
import org.eclipse.swt.internal.gtk.GdkRectangle;
import org.eclipse.swt.internal.gtk.GtkAllocation;
import org.eclipse.swt.internal.gtk3.GTK3;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public final class Issue0568_WrongShellTrims {
	public static void main(String[] args) {
		final Display display = new Display();

		int[] styles = new int[] {
			SWT.CLOSE | SWT.TITLE | SWT.RESIZE,     // Test#0 - see Display.TRIM_TITLE_RESIZE
			SWT.CLOSE | SWT.TITLE | SWT.BORDER,     // Test#1 - see Display.TRIM_TITLE_BORDER
			SWT.CLOSE | SWT.TITLE | 0,              // Test#2 - see Display.TRIM_TITLE
			SWT.CLOSE | SWT.RESIZE,                 // Test#3 - see Display.TRIM_RESIZE
			SWT.CLOSE | SWT.BORDER,                 // Test#4 - see Display.TRIM_BORDER
			SWT.CLOSE | 0,                          // Test#5 - see Display.TRIM_NONE
			SWT.CLOSE | SWT.RESIZE | SWT.ON_TOP,    // Test#6 - see Shell#isCustomResize()
		};

		for (int iStyle = 0; iStyle < styles.length; iStyle++)
		{
			String caption = "Test #" + iStyle;
			Shell testShell = new Shell(display, styles[iStyle]);
			testShell.setText(caption);
			testShell.setLayout (new GridLayout (1, true));

			testShell.addListener(SWT.Dispose, e -> {
				// Let SWT write trims file
				display.dispose();
				// For convenience of debugging
				System.exit(0);
			});

			final boolean[] hasReported = new boolean[1];
			testShell.addListener(SWT.Paint, e -> {
				if (hasReported[0]) return;
				hasReported[0] = true;

				GtkAllocation allocation = new GtkAllocation();
				GTK.gtk_widget_get_allocation(testShell.handle, allocation);

				GdkRectangle rect = new GdkRectangle();
				long window = GTK3.gtk_widget_get_window(testShell.handle);
				GDK.gdk_window_get_frame_extents(window, rect);

				int windowW = rect.width;
				int windowH = rect.height;
				int allocW  = allocation.width;
				int allocH  = allocation.height;
				System.out.format(
					"%s: window=(%dx%d) alloc=(%dx%d) delta=(%dx%d)\n",
					caption,
					windowW, windowH,
					allocW, allocH,
					windowW - allocW, windowH - allocH
				);
			});

			Label testLabel = new Label(testShell, 0);
			testLabel.setText(
				"1. Run on Linux\n" +
				"2. Delete file before running:\n" +
				"   ~/.swt/trims.prefs\n" +
				"3. Some Shells will have wrong size\n"
			);

			Point size = testShell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
			testShell.setSize(size);
			testShell.open();

			// Order shells for convenience of debugging
			while (!hasReported[0]) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}

		while (!display.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
