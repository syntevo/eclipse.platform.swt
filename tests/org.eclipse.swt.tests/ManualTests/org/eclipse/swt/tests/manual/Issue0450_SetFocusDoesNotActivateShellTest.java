/*******************************************************************************
 * Copyright (c) 2023 Syntevo and others.
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import static org.junit.Assert.assertTrue;

public class Issue0450_SetFocusDoesNotActivateShellTest {

	public static void main(String[] args) {
		final Display display = new Display();

		final Window window1 = new Window("1", display);
		final Window window2 = new Window("2", display);

		window1.shell.open();
		display.timerExec(500, () -> {
			assertTrue(display.getActiveShell() == window1.shell);
			assertTrue(window1.text1.isFocusControl());

			window2.shell.open();

			display.timerExec(500, () -> {
				assertTrue(display.getActiveShell() == window2.shell);
				assertTrue(window2.text1.isFocusControl());

				window1.text2.setFocus();

				assertTrue(display.getActiveShell() == window2.shell);

				window1.shell.setActive();

				display.timerExec(500, () -> {
					assertTrue(display.getActiveShell() == window1.shell);
					assertTrue(window1.text2.isFocusControl());

					window1.shell.dispose();

					display.timerExec(500, () -> {
						assertTrue(display.getActiveShell() == window2.shell);
						assertTrue(window2.text1.isFocusControl());

						window2.shell.dispose();
					});
				});
			});
		});

		while (!display.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private static class Window {
		private final Shell shell;
		private final Text text1;
		private final Text text2;

		public Window(String title, Display display) {
			shell = new Shell(display);
			shell.setText(title);
			shell.setLayout(new GridLayout(1, false));
			shell.addListener(SWT.Dispose, event -> {
				final Shell[] shells = display.getShells();
				for (Shell aShell : shells) {
					if (aShell != shell) {
						return;
					}
				}
				display.dispose();
			});

			text1 = createText();
			text2 = createText();

			shell.setSize(400, 300);
		}

		private Text createText() {
			final Text text = new Text(shell, SWT.BORDER | SWT.SINGLE);
			text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			return text;
		}
	}
}
