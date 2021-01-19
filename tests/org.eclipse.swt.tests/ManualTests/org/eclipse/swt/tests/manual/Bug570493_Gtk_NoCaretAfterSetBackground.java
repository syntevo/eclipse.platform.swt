/*******************************************************************************
 * Copyright (c) 2021 Syntevo and others.
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
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class Bug570493_Gtk_NoCaretAfterSetBackground {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, true));

		final Text hint = new Text(shell, SWT.READ_ONLY | SWT.MULTI);
		hint.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		hint.setText(
			"1) Run this snippet on GTK\n" +
			"2) Check checkbox below\n" +
			"3) Notice that caret is now missing in all controls"
		);

		final Text textS = new Text(shell, SWT.BORDER);
		textS.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		textS.setText("Single-line text");

		final Text textM = new Text(shell, SWT.MULTI | SWT.BORDER);
		textM.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		textM.setText("Multi-line text");

		final Combo combo = new Combo(shell, 0);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		combo.setText("Combo");

		final Control[] testControls = new Control[] {textS, textM, combo};

		Button button = new Button(shell, SWT.CHECK);
		button.setText("Set custom colors");
		button.addListener(SWT.Selection, e -> {
			final boolean isSelected = ((Button)e.widget).getSelection();

			for (Control control : testControls) {
				if (isSelected) {
					control.setForeground(new Color(0xCF, 0xCF, 0xCF));
					control.setBackground(new Color(0x41, 0x41, 0x41));
				} else {
					control.setForeground(null);
					control.setBackground(null);
				}
			}
		});

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
