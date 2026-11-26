/*******************************************************************************
 * Copyright (c) 2026 Syntevo and others.
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
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Compare a mouse and a tablet pen on GTK3 Wayland. Leave the mouse stationary,
 * then tap each painted button and drag from both the canvas and the table.
 * Motion must follow the pen and retain BUTTON1 while the tip is down. Each tap
 * must select its own button. A successful drag reports DragStart, DragEnter,
 * Drop and DragEnd in that order (with other events between).
 * Before the fixes, motion follows the stationary mouse and accepted DragStart
 * events are not followed by DragEnter for the tablet pointer.
 *
 * @author Wilfried Nesensohn
 */
public class GtkWaylandTabletInput {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("GTK3 Wayland tablet input");
		shell.setLayout(new GridLayout(2, true));
		Label instructions = new Label(shell, SWT.WRAP);
		instructions.setText("Keep the mouse still. With the pen, tap each button, then drag from the canvas and table to the drop target. Compare with the mouse; see console events.");
		instructions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		Label result = new Label(shell, SWT.NONE);
		result.setText("No button clicked");
		result.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Canvas buttons = new Canvas(shell, SWT.BORDER);
		GridData buttonData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		buttonData.heightHint = 50;
		buttons.setLayoutData(buttonData);
		int[] hoverAndPressed = {-1, -1};
		buttons.addListener(SWT.Paint, event -> {
			for (int i = 0; i < 3; i++) {
				event.gc.drawRectangle(i * 100, 0, 99, 49);
				event.gc.drawText("Button " + (i + 1), i * 100 + 15, 15);
			}
		});
		buttons.addListener(SWT.MouseMove, event -> hoverAndPressed[0] = buttonAt(event));
		buttons.addListener(SWT.MouseDown, event -> {
			hoverAndPressed[0] = buttonAt(event);
			hoverAndPressed[1] = hoverAndPressed[0];
		});
		buttons.addListener(SWT.MouseUp, event -> {
			result.setText(hoverAndPressed[0] == hoverAndPressed[1] && hoverAndPressed[0] >= 0
				? "Clicked button " + (hoverAndPressed[0] + 1) : "Click lost or moved to another button");
			hoverAndPressed[1] = -1;
		});

		Canvas canvas = new Canvas(shell, SWT.BORDER);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		canvas.addListener(SWT.Paint, event -> event.gc.drawText("Drag from this canvas", 10, 10));
		Table table = new Table(shell, SWT.BORDER | SWT.MULTI);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		for (int i = 0; i < 5; i++) {
			new TableItem(table, SWT.NONE).setText("Drag row " + (i + 1));
		}
		for (Control control : new Control[] {buttons, canvas, table}) {
			for (int type : new int[] {SWT.MouseDown, SWT.MouseMove, SWT.MouseUp, SWT.DragDetect}) {
				control.addListener(type, event -> System.out.println(control.getClass().getSimpleName() + " " + event));
			}
		}
		for (Control control : new Control[] {canvas, table}) {
			DragSource source = new DragSource(control, DND.DROP_COPY);
			source.setTransfer(TextTransfer.getInstance());
			source.addDragListener(new DragSourceAdapter() {
				@Override
				public void dragStart(DragSourceEvent event) {
					System.out.println("DragStart " + event);
				}
				@Override
				public void dragSetData(DragSourceEvent event) {
					event.data = "Tablet drag from " + control.getClass().getSimpleName();
				}
				@Override
				public void dragFinished(DragSourceEvent event) {
					System.out.println("DragEnd " + event);
				}
			});
		}
		Label target = new Label(shell, SWT.BORDER | SWT.CENTER);
		target.setText("Drop here");
		target.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		DropTarget drop = new DropTarget(target, DND.DROP_COPY);
		drop.setTransfer(TextTransfer.getInstance());
		drop.addDropListener(new DropTargetAdapter() {
			@Override
			public void dragEnter(DropTargetEvent event) {
				System.out.println("DragEnter " + event);
			}
			@Override
			public void drop(DropTargetEvent event) {
				System.out.println("Drop " + event);
				target.setText(String.valueOf(event.data));
			}
		});
		shell.setSize(650, 400);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}

	private static int buttonAt(Event event) {
		return event.x >= 0 && event.x < 300 && event.y >= 0 && event.y < 50 ? event.x / 100 : -1;
	}
}
