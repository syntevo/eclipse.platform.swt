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

package org.eclipse.swt.tests.cocoa.snippets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public final class Bug577767_macOS_TableScrollsPastContent {
	public static void testTable(Button button, boolean isVirtual) {
		final Shell popup = new Shell (button.getDisplay (), SWT.ON_TOP | SWT.NO_FOCUS);
		popup.setLayout (new FillLayout ());

		int virtual = isVirtual ? SWT.VIRTUAL : 0;
		Table table = new Table (popup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | virtual);

		// For the problem to occur, Table's item must not fit (by width)
		// into initial size of Table. Since Bug 575259, initial size is
		// 100x100 px.
		table.addListener (SWT.MeasureItem, (arg) -> arg.width = 120);

		// Give items some content that makes it easy to see item boundaries
		table.addListener (SWT.PaintItem, (arg) -> {
			TableItem item = (TableItem)arg.item;
			int color = 3 + item.getParent ().indexOf (item) % 10;
			Rectangle rect = arg.getBounds ();
			rect.width = 50;
			arg.gc.setBackground (button.getDisplay ().getSystemColor (color));
			arg.gc.fillRectangle (rect);
		});

		table.setItemCount (20);

		// VIRTUAL is a bit more resilient for some reason; but TableItem.getBounds()
		// triggers the bug
		if (isVirtual)
			table.getItem (0).getBounds ();

		// A way to get rid of popup for convenience of testing
		table.addListener (SWT.MouseDown, e -> popup.dispose ());

		popup.setLocation (button.toDisplay (new Point (20, 20)));
		popup.setSize (200, 200);
		popup.open ();
	}

	public static void testTree(Button button, boolean isVirtual) {
		final Shell popup = new Shell (button.getDisplay (), SWT.ON_TOP | SWT.NO_FOCUS);
		popup.setLayout (new FillLayout ());

		int virtual = isVirtual ? SWT.VIRTUAL : 0;
		Tree tree = new Tree (popup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | virtual);

		// For the problem to occur, Tree's item must not fit (by width)
		// into initial size of Table. Since Bug 575259, initial size is
		// 100x100 px.
		tree.addListener (SWT.MeasureItem, (arg) -> arg.width = 120);

		// Give items some content that makes it easy to see item boundaries
		tree.addListener (SWT.PaintItem, (arg) -> {
			TreeItem item = (TreeItem)arg.item;
			int color = 3 + item.getParent ().indexOf (item) % 10;
			Rectangle rect = arg.getBounds ();
			rect.width = 50;
			arg.gc.setBackground (button.getDisplay ().getSystemColor (color));
			arg.gc.fillRectangle (rect);
		});

		tree.setItemCount (20);

		// Needed for problem to occur in VIRTUAL Tree.
		// Regular Tree doesn't have the problem for some reason.
		if (isVirtual)
			tree.getItem (0).getBounds ();

		// A way to get rid of popup for convenience of testing
		tree.addListener (SWT.MouseDown, e -> popup.dispose ());

		popup.setLocation (button.toDisplay (new Point (20, 20)));
		popup.setSize (200, 200);
		popup.open ();
	}

	public enum Options {
		WITH_new_TableItem_before_setItemCount,
		WITH_cacheItemWidth_before_setItemCount,
		WITH_TableItem_getBounds,
		NO_Table_setScrollWidth,
		WITH_NSTableView_tile_after_resize,
		WITH_early_Shell_setSize,
		WITH_VIRTUAL_Table,
	};

	public static boolean options[] = new boolean[Options.values ().length];

	public static boolean isOption(Options option) {
		return options[option.ordinal ()];
	}

	public static void runTest(Button button) {
		StringBuilder sb = new StringBuilder ();
		for (int iOption = 0; iOption < options.length; iOption++) {
			if (options[iOption]) {
				sb.append ((char)('A' + iOption));
			} else {
				sb.append ('-');
			}
		}
		sb.append (" | ");

		final Shell popup = new Shell (button.getDisplay (), SWT.ON_TOP | SWT.NO_FOCUS);
		popup.setLayout (new FillLayout ());

		int virtual = 0;
		if (isOption (Options.WITH_VIRTUAL_Table))
			virtual = SWT.VIRTUAL;

		Table table = new Table (popup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | virtual);

		if (isOption (Options.WITH_early_Shell_setSize))
			popup.setSize (200, 200);

		// For the problem to occur, Table's item must not fit (by width)
		// into Table at the moment of `TableItem.getBounds()` by width.
		// Bug 575259 recently changed Table's default size to 100x100.
		// In order to still see the problem, let's make item wider.
		table.addListener (SWT.MeasureItem, (arg) -> arg.width = 100);

		if (isOption (Options.WITH_new_TableItem_before_setItemCount))
			new TableItem(table, 0);

		if (isOption (Options.WITH_cacheItemWidth_before_setItemCount))
			table.testCacheItemWidth ();

		table.setItemCount (100);

		// This is what causes the problem
		if (isOption(Options.WITH_TableItem_getBounds))
			table.getItem (0).getBounds ();

		popup.setLocation (button.toDisplay (new Point (20, 20)));
		popup.setSize (200, 200);
		popup.open ();

		sb.append (table.getBoundsH ());
		System.out.println (sb.toString ());
		popup.dispose ();
	}

	public static void main(String[] args) {
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout (new GridLayout (1, true));

		Label hint = new Label (shell, 0);
		hint.setText(
			"1) Use macOS 10.15+\n" +
			"2) Click button to show popup\n" +
			"3) Use scroller thumb or mouse wheel to scroll to the end\n" +
			"4) There will be empty space at the end"
		);

		Button btnTestRegularTable = new Button(shell, SWT.PUSH);
		btnTestRegularTable.setText ("Test regular Table");
		btnTestRegularTable.addListener (SWT.Selection, e -> {
			testTable (btnTestRegularTable, false);
		});

		Button btnTestVirtualTable = new Button(shell, SWT.PUSH);
		btnTestVirtualTable.setText ("Test virtual Table");
		btnTestVirtualTable.addListener (SWT.Selection, e -> {
			testTable (btnTestVirtualTable, true);
		});

		Button btnTestRegularTree = new Button(shell, SWT.PUSH);
		btnTestRegularTree.setText ("Test regular Tree");
		btnTestRegularTree.addListener (SWT.Selection, e -> {
			testTree (btnTestRegularTree, false);
		});

		Button btnTestVirtualTree = new Button(shell, SWT.PUSH);
		btnTestVirtualTree.setText ("Test virtual Tree");
		btnTestVirtualTree.addListener (SWT.Selection, e -> {
			testTree (btnTestVirtualTree, true);
		});

		Button btnTestOne = new Button(shell, SWT.PUSH);
		btnTestOne.setText ("Test one");
		btnTestOne.addListener (SWT.Selection, e -> {
			String test = "--C---G";

			for (int iOption = 0; iOption < options.length; iOption++) {
				options[iOption] = (test.charAt(iOption) != '-');
			}

			runTest (btnTestOne);
		});

		Button btnTestAll = new Button(shell, SWT.PUSH);
		btnTestAll.setText ("Test all");
		btnTestAll.addListener (SWT.Selection, e -> {
			for (int iOption = 0; iOption < Options.values ().length; iOption++) {
				System.out.println ("[" + (char)('A' + iOption) + "] = " + Options.values ()[iOption].toString ());
			}

			long maxTests = 1 << options.length;
			for (long iTest = 0; iTest < maxTests; iTest++) {
				for (int iOption = 0; iOption < options.length; iOption++) {
					if (0 == (iTest & (1 << iOption))) {
						options[iOption] = false;
					} else {
						options[iOption] = true;
					}
				}

				runTest (btnTestAll);
			}
		});

		Button btnTestPerf = new Button(shell, SWT.PUSH);
		btnTestPerf.setText ("Test -[NSTableView tile] performance");
		btnTestPerf.addListener (SWT.Selection, e -> {
			Shell testShell = new Shell(display);
			testShell.setLayout (new GridLayout(1, true));

			final Table table = new Table (testShell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			table.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));

			table.setRedraw (false);
			for (int i = 0; i < 10000; i++) {
				new TableItem (table, SWT.LEFT).setText ("Table item#" + i);
			}
			table.setRedraw (true);

			final int measureItems[] = {0};
			table.addListener (SWT.MeasureItem, (arg) -> {
				measureItems[0]++;
			});

			Button btnTest = new Button(testShell, SWT.PUSH);
			btnTest.setText ("Test");
			btnTest.addListener (SWT.Selection, e2 -> {
				int iters = 5*26000;

				measureItems[0] = 0;
				long time0 = System.nanoTime ();
				for (int i = 0; i < iters; i++) {
					table.callTile ();
				}
				long time1 = System.nanoTime ();

				System.out.format (
					"%.2f/sec MeasureItem=%d%n",
					1_000_000_000.0 * iters / (time1 - time0),
					measureItems[0]
				);
			});

			testShell.setSize (200, 200);
			testShell.open ();
		});

		shell.pack ();
		shell.open ();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}
}
