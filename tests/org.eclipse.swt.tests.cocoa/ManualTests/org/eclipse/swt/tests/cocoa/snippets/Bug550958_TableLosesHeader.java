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
package org.eclipse.swt.tests.cocoa.snippets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class Bug550958_TableLosesHeader {
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, true));

		final Label hint = new Label(shell, 0);
		hint.setText(
			"1) Use macOS 10.14 or 10.15\n" +
			"2) Use java with SDK < 10.14, see:\n" +
			"   otool -l `which java` | grep sdk\n" +
			"3) Click button below\n" +
			"4) Table's header will become invisible"
		);
		
		final Composite hiddenComposite = new Composite(shell, SWT.NONE);
		hiddenComposite.setVisible(false);

		final Table table = new Table(shell, SWT.V_SCROLL);
		final TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setText("Column");
		column.setWidth(100);
		new TableItem(table, 0).setText("Item");
		table.setHeaderVisible(true);

		final Button btnTest = new Button(shell, SWT.PUSH);
		btnTest.setText("Test!");
		btnTest.addListener(SWT.Selection, event -> {
			table.setParent(hiddenComposite);
			table.setParent(shell);
		});

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
