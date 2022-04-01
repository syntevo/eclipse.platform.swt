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

public class Issue0002_Win11_SlowTreeGetSelection {
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());

		final Label hint = new Label(shell, 0);
		hint.setText(
			"1) Use Win11 Preview #22581\n" +
			"2) Press button below and check console output\n" +
			"3) View all submenus on menubar (just see their contents once)\n" +
			"4) Issue#10: Press button below - it will be a lot slower\n" +
			"5) Repeat steps 3,4 - it will be slower each time\n"
		);

		Menu menubar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menubar);
		for (int iMenu = 0; iMenu < 10; iMenu++) {
			Menu menu = new Menu(shell, SWT.DROP_DOWN);
			MenuItem cascade = new MenuItem(menubar, SWT.CASCADE);
			cascade.setText("menuItem#" + iMenu);
			cascade.setMenu(menu);

			for (int iMenuItem = 0; iMenuItem < 10; iMenuItem++) {
				MenuItem menuItem = new MenuItem(menu, 0);
				menuItem.setText("item#" + iMenu + ":" + iMenuItem);
			}
		}

		Tree tree = new Tree(shell, SWT.MULTI);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		TreeItem root1 = new TreeItem(tree, 0);
		root1.setText("Root1");
		TreeItem root2 = new TreeItem(root1, 0);
		root2.setText("Root2");
		for (int i = 0; i < 200; i++) {
			TreeItem item = new TreeItem(root2, 0);
			item.setText("Item#" + i);
		}
		root1.setExpanded(true);
		root2.setExpanded(true);

		Button button = new Button(shell, 0);
		button.setText("Test performance");
		button.addListener(SWT.Selection, e -> {
			int numTests = 100;
			long time0 = System.nanoTime();
			for (int iTest = 0; iTest < numTests; iTest++) {
				tree.getSelection();
			}
			long time1 = System.nanoTime();
			System.out.format(
				"Tree.getSelection(): %.2f/sec%n",
				numTests / ((time1-time0) / 1_000_000_000.0));
		});

		shell.setSize(400, 300);
		shell.open();

		while (!shell.isDisposed()) {
			if (display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}
}
