/*******************************************************************************
 * Copyright (c) 2022 Syntevo and others.
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

package org.eclipse.swt.tests.manual;

import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public final class Bug575712_ShellCantActivateUbuntu2104 {
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell0 = new Shell(display);
		shell0.setText("Shell #0");
		shell0.setLayout (new GridLayout (1, true));
		shell0.addListener(SWT.Activate, event3 -> System.out.println("Shell #0 Activated"));

		new Label(shell0, 0).setText (
			"1) Use Ubuntu 21.04; Ubuntu 20.04 is not enough\n" +
			"2) Run with\n" +
			"   export GDK_BACKEND=x11\n" +
			"3) Click the button below\n" +
			"4) Close the first Shell\n" +
			"5) Second Shell will fail to activate\n"
		);

		Button btnTest = new Button(shell0, 0);
		btnTest.setText ("Press me");
		btnTest.addListener(SWT.Selection, event -> {
			final Shell shell1 = new Shell(shell0, SWT.DIALOG_TRIM);
			shell1.setLayout(new GridLayout());
			shell1.setText("Shell #1");

			new Label(shell1, 0).setText ("Close me");

			shell1.addListener(SWT.Dispose, event2 -> {
				final Shell shell2 = new Shell(shell0, SWT.DIALOG_TRIM);
				shell2.setLayout(new GridLayout(10, true));
				shell2.setText("Shell #2");

				// It seems to cause 'display.lastUserEventTime' to be stale in 'Shell.bringToTop()'
				// and GTK seems to ignore Shell activation
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				shell2.addListener(SWT.Activate, event3 -> System.out.println("Shell #2 Activated"));

				shell2.setSize(200, 200);
				shell2.open();
				shell2.forceActive ();
			});

			shell1.setSize(100, 100);
			shell1.open();
		});

		shell0.pack();
		shell0.open();

		while (!shell0.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}
}
