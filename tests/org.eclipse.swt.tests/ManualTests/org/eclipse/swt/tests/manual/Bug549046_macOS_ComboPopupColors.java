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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;

public class Bug549046_macOS_ComboPopupColors {
	enum Colors {
		Default,
		Light,
		Dark,
		Gray,
		Blue,
	};

	static void setColors(Control control, Colors color) {
		switch (color) {
			case Default:
				control.setBackground(null);
				control.setForeground(null);
				break;
			case Light:
				control.setBackground(new Color(0xEC, 0xEC, 0xEC));
				control.setForeground(new Color(0x00, 0x00, 0x00));
				break;
			case Dark:
				control.setBackground(new Color(0x31, 0x31, 0x31));
				control.setForeground(new Color(0xFF, 0xFF, 0xFF));
				break;
			case Gray:
				control.setBackground(control.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
				control.setForeground(control.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				break;
			case Blue:
				control.setBackground(control.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
				control.setForeground(control.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				break;
		}
	}

	public static void main(String[] args) {
		// macOS's dark theme has significantly different Combos
		// System.setProperty("org.eclipse.swt.display.useSystemTheme", "true");
		
		final Display display = new Display();
		final ArrayList<Control> coloredControls = new ArrayList<>();
		final ArrayList<Control> otherControls = new ArrayList<>();

		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));
		coloredControls.add(shell);

		final Label hint = new Label (shell, 0);
		hint.setText(
			"1) Run on macOS\n" +
			"2) Bug 549046: Combo(SWT.READ_ONLY) does not support background color\n" +
			"3) Bug 549046: Popups of both Combos ignore background color\n"
		);
		coloredControls.add(hint);

		{
			final Composite composite = new Composite(shell, 0);
			composite.setLayout(new GridLayout(3, false));
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			coloredControls.add(composite);

			final Label label1 = new Label(composite, 0);
			label1.setText("");
			coloredControls.add(label1);

			final Label label2 = new Label(composite, 0);
			label2.setText("default");
			coloredControls.add(label2);

			final Label label3 = new Label(composite, 0);
			label3.setText("SWT.READ_ONLY");
			coloredControls.add(label3);

			for (int iColor = 0; iColor < 2; iColor++) {
				final Label label4 = new Label(composite, 0);
				if (0 == iColor) {
					label4.setText("default");
				} else {
					label4.setText("with colors");
				}
				coloredControls.add(label4);

				for (int iReadonly = 0; iReadonly < 2; iReadonly++) {
					final int readonly = (0 == iReadonly) ? 0 : SWT.READ_ONLY;
					final Combo combo = new Combo(composite, SWT.BORDER | readonly);
					combo.setItems("hello", "world");
					combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

					if (0 != iColor) {
						coloredControls.add(combo);
					}

					if (0 == iReadonly) {
						combo.setText("hello");
					} else {
						combo.select(0);
					}
				}
			}
		}

		Text text = new Text(shell, SWT.BORDER);
		text.setText("A text field to put focus away");
		coloredControls.add(text);
		text.setFocus();
		text.setSelection(0, 0);

		{
			Composite composite = new Composite(shell, 0);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			composite.setLayout(new GridLayout(Colors.values().length, false));
			coloredControls.add(composite);

			Listener listener = event -> {
				Button button = (Button)event.widget;
				if (!button.getSelection())
					return;

				Colors color = (Colors)button.getData();

				for (Control control : coloredControls) {
					setColors(control, color);
				}

				for (Control control : otherControls) {
					control.redraw();
				}
			};

			for (Colors color : Colors.values()) {
				final Button radio = new Button(composite, SWT.RADIO);
				radio.setText(color.name());
				radio.setData(color);
				radio.addListener(SWT.Selection, listener);
				otherControls.add(radio);

				if (color == Colors.Gray) {
					radio.setSelection(true);
					for (Control control : coloredControls) {
						setColors(control, color);
					}
				}

				coloredControls.add(radio);
			}
		}

		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}
}
