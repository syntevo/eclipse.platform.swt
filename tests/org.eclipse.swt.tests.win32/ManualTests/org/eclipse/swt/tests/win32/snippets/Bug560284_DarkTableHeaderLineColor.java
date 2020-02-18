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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

public class Bug560284_DarkTableHeaderLineColor {
	static final boolean useDarkTheme  = true;
	static final boolean useLinesColor = false;

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
				"Notice that table header delimiter lines are too bright"
		);

		final Table table = new Table(shell, SWT.BORDER);
		table.setHeaderVisible(true);
		TableColumn column1 = new TableColumn(table, 0);
		column1.setText("Column");
		column1.setWidth(100);
		TableColumn column2 = new TableColumn(table, 0);
		column2.setText("Column");
		column2.setWidth(100);
		TableItem item1 = new TableItem(table, 0);
		item1.setText("Item");

		if (useDarkTheme) {
			Color backColor = new Color(display, 0x30, 0x30, 0x30);
			Color foreColor = new Color(display, 0xD0, 0xD0, 0xD0);
			shell.setBackground(backColor);
			labelInfo.setBackground(backColor);
			labelInfo.setForeground(foreColor);
			table.setBackground(backColor);
			table.setForeground(foreColor);
			table.setHeaderBackground(backColor);
			table.setHeaderForeground(foreColor);
		}

		if (useLinesColor) {
			Color lineColor = new Color(display, 0x4F, 0x4F, 0x4F);
			table.setData("org.eclipse.swt.widgets.Table.headerLineColor", lineColor);
		}

		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}

		display.dispose ();
	}
}
