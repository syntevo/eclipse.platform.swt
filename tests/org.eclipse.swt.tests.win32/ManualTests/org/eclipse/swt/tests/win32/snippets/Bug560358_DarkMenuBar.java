/*******************************************************************************
 * Copyright (c) 2020 Syntevo and others.
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
package org.eclipse.swt.tests.win32.snippets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

public class Bug560358_DarkMenuBar {
	static final boolean useMenuBarColors = true;
	static final boolean paintMenuBarBorder = true;

	public static void main (String [] args) {
		Display display = new Display ();
		Shell shell = new Shell (display);

		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		layout.spacing = 10;
		shell.setLayout(layout);

		final Text labelInfo = new Text(shell, SWT.READ_ONLY | SWT.MULTI);
		labelInfo.setText(
			"The Menu bar is still light in Dark Theme"
		);

		Color backColor = new Color(display, 0x30, 0x30, 0x30);
		Color foreColor = new Color(display, 0xD0, 0xD0, 0xD0);
		Color menuBorderColor = new Color(display, 0x50, 0x50, 0x50);

		// Create menus. Nothing special here.
		Menu menu = new Menu(shell, SWT.BAR);
		{
			Menu menu1 = new Menu(menu);
			MenuItem item11 = new MenuItem(menu1, 0);
			item11.setText("item1:1");
			MenuItem item12 = new MenuItem(menu1, 0);
			item12.setText("item1:2");

			Menu menu2 = new Menu(menu);
			MenuItem item21 = new MenuItem(menu2, 0);
			item21.setText("item2:1");
			MenuItem item22 = new MenuItem(menu2, 0);
			item22.setText("item2:2");

			MenuItem item1 = new MenuItem(menu, SWT.CASCADE);
			item1.setText("item1");
			item1.setMenu(menu1);
			MenuItem item2 = new MenuItem(menu, SWT.CASCADE);
			item2.setText("item2");
			item2.setMenu(menu2);

			shell.setMenuBar(menu);
		}

		if (useMenuBarColors) {
			menu.setData("org.eclipse.swt.internal.win32.Menu.backgroundColor", backColor);
			menu.setData("org.eclipse.swt.internal.win32.Menu.foregroundColor", foreColor);
		}

		if (paintMenuBarBorder) {
			display.setData("org.eclipse.swt.internal.win32.Menu.Bar.borderColor", menuBorderColor);
		}

		// Set shell colors
		shell.setBackground(backColor);
		shell.setForeground(foreColor);
		labelInfo.setBackground(backColor);
		labelInfo.setForeground(foreColor);

		// Pack and show shell
		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}

		display.dispose ();
	}
}
