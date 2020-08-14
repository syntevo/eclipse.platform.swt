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
package org.eclipse.swt.tests.manual;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class Bug565679_Borders {
	public interface CreateCrontrols {
		void createControls(Composite parent, int style);
	}

	static void createControls(Composite parent, CreateCrontrols creator) {
		creator.createControls(parent, SWT.BORDER);
		creator.createControls(parent, 0);
	}

	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 10;
		shell.setLayout(layout);

		new Label(shell, 0).setText(
			"Here's a collection of various controls. " +
			"This patch slightly changes borders for some of them.\n"
		);

		final Composite compControls = new Composite(shell, 0);
		compControls.setLayout(new GridLayout(5, true));

		new Label(compControls, 0).setText("");
		new Label(compControls, 0).setText("Contents\nSWT.BORDER");
		new Label(compControls, 0).setText("Contents\nno border");
		new Label(compControls, 0).setText("Empty\nSWT.BORDER");
		new Label(compControls, 0).setText("Empty\nno border");

		new Label(compControls, 0).setText("Button");
		createControls(compControls, (parent, style) -> {
			Button button = new Button(parent, style | SWT.PUSH);
			button.setText("Test");
		});
		createControls(compControls, (parent, style) -> {
			new Button(parent, style | SWT.PUSH);
		});

		new Label(compControls, 0).setText("Canvas");
		createControls(compControls, (parent, style) -> {
			new Canvas(parent, style);
		});
		createControls(compControls, (parent, style) -> {
			new Canvas(parent, style);
		});

		new Label(compControls, 0).setText("Composite");
		createControls(compControls, (parent, style) -> {
			new Composite(parent, style);
		});
		createControls(compControls, (parent, style) -> {
			new Composite(parent, style);
		});

		new Label(compControls, 0).setText("Combo");
		createControls(compControls, (parent, style) -> {
			Combo combo = new Combo(parent, style);
			combo.add("Item");
			combo.select(0);
		});
		createControls(compControls, (parent, style) -> {
			new Combo(parent, style);
		});

		new Label(compControls, 0).setText("CoolBar");
		createControls(compControls, (parent, style) -> {
			CoolBar coolBar = new CoolBar(parent, style);

			Label label = new Label (coolBar, 0);
			label.setText("Test label");
			CoolItem coolItem = new CoolItem(coolBar, 0);
			coolItem.setControl(label);

			Point size = coolItem.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);
			Point coolSize = coolItem.computeSize(size.x, size.y);
			coolItem.setPreferredSize(coolSize);
		});
		createControls(compControls, (parent, style) -> {
			new CoolBar(parent, style);
		});

		new Label(compControls, 0).setText("DateTime");
		createControls(compControls, (parent, style) -> {
			DateTime dateTime = new DateTime(parent, style);
			dateTime.setDate(2020, 8, 13);
		});
		createControls(compControls, (parent, style) -> {
			new DateTime(parent, style);
		});

		new Label(compControls, 0).setText("ExpandBar");
		createControls(compControls, (parent, style) -> {
			ExpandBar expandBar = new ExpandBar(parent, style);
			Composite composite = new Composite(expandBar, SWT.NONE);
			composite.setLayout(new FillLayout());
			new Label(composite, 0).setText("Some contents");
			ExpandItem item = new ExpandItem(expandBar, SWT.NONE, 0);
			item.setText("Test expand");
			item.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			item.setControl(composite);
		});
		createControls(compControls, (parent, style) -> {
			new ExpandBar(parent, style);
		});

		new Label(compControls, 0).setText("Group");
		createControls(compControls, (parent, style) -> {
			Group group = new Group(parent, style);
			group.setText("Group");
		});
		createControls(compControls, (parent, style) -> {
			new Group(parent, style);
		});

		new Label(compControls, 0).setText("Spinner");
		createControls(compControls, (parent, style) -> {
			Spinner spinner = new Spinner(parent, style);
			spinner.setSelection(100);
		});
		createControls(compControls, (parent, style) -> {
			new Spinner(parent, style);
		});

		new Label(compControls, 0).setText("StyledText");
		createControls(compControls, (parent, style) -> {
			StyledText text = new StyledText(parent, style);
			text.setText("Some text");
		});
		createControls(compControls, (parent, style) -> {
			new StyledText(parent, style);
		});

		new Label(compControls, 0).setText("TabFolder");
		createControls(compControls, (parent, style) -> {
			TabFolder tabFolder = new TabFolder(parent, style);
			new TabItem(tabFolder, 0).setText("Item");
			new TabItem(tabFolder, 0).setText("Item");
		});
		createControls(compControls, (parent, style) -> {
			new TabFolder(parent, style);
		});

		new Label(compControls, 0).setText("Table (with header)");
		createControls(compControls, (parent, style) -> {
			Table table = new Table(parent, style);
			TableColumn tableColumn = new TableColumn(table, 0);
			tableColumn.setText("Column");
			tableColumn.setWidth(100);
			new TableItem(table, 0).setText("Item");
			table.setHeaderVisible(true);
		});
		createControls(compControls, (parent, style) -> {
			Table table = new Table(parent, style);
			table.setHeaderVisible(true);
		});

		new Label(compControls, 0).setText("Table (no header)");
		createControls(compControls, (parent, style) -> {
			Table table = new Table(parent, style);
			new TableItem(table, 0).setText("Item");
		});
		createControls(compControls, (parent, style) -> {
			new Table(parent, style);
		});

		new Label(compControls, 0).setText("Text");
		createControls(compControls, (parent, style) -> {
			Text text = new Text(parent, style);
			text.setText("Test text");
		});
		createControls(compControls, (parent, style) -> {
			new Text(parent, style);
		});

		new Label(compControls, 0).setText("ToolBar");
		createControls(compControls, (parent, style) -> {
			ToolBar toolbar = new ToolBar(parent, style);
			ToolItem item = new ToolItem (toolbar, SWT.DROP_DOWN);
			item.setText("Item");
		});
		createControls(compControls, (parent, style) -> {
			new ToolBar(parent, style);
		});

		new Label(compControls, 0).setText("Tree (with header)");
		createControls(compControls, (parent, style) -> {
			Tree tree = new Tree(parent, style);
			tree.setHeaderVisible(true);
			TreeColumn column = new TreeColumn(tree, 0);
			column.setText("Column");
			column.setWidth(100);
			TreeItem treeItem1 = new TreeItem(tree, 0);
			treeItem1.setText("Item");
			TreeItem treeItem2 = new TreeItem(treeItem1, 0);
			treeItem2.setText("Item");
			treeItem1.setExpanded(true);
		});
		createControls(compControls, (parent, style) -> {
			Tree tree = new Tree(parent, style);
			tree.setHeaderVisible(true);
		});

		new Label(compControls, 0).setText("Tree (no header)");
		createControls(compControls, (parent, style) -> {
			Tree tree = new Tree(parent, style);
			TreeItem treeItem1 = new TreeItem(tree, 0);
			treeItem1.setText("Item");
			TreeItem treeItem2 = new TreeItem(treeItem1, 0);
			treeItem2.setText("Item");
			treeItem1.setExpanded(true);
		});
		createControls(compControls, (parent, style) -> {
			new Tree(parent, style);
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
