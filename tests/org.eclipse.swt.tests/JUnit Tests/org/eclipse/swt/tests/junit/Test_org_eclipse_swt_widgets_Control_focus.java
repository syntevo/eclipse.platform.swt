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
package org.eclipse.swt.tests.junit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class Test_org_eclipse_swt_widgets_Control_focus {

	private static final int TIMEOUT = 1000;
	private static final boolean LOGGING = false;

	@Test
	public void testSetFocusDoesNotActivateShell() {
		final Display display = new Display();

		try {
			final boolean[] exit = new boolean[1];
			final Window window1 = new Window("1", display, exit);
			final Window window2 = new Window("2", display, exit);

			onEventExecute(SWT.Activate, window1.shell, () -> {
				assertSame("expecting the 1st shell to be activated", display.getActiveShell(), window1.shell);
				assertTrue("expecting the 1st text field to be focused in the 1st shell", window1.text1.isFocusControl());

				onEventExecute(SWT.Activate, window2.shell, () -> {
					assertSame("expecting the 2nd shell to be activated", display.getActiveShell(), window2.shell);
					assertTrue("expecting the 1st text field in 2nd shell to be focused", window2.text1.isFocusControl());

					log("setting focus to 2nd text field in 1st (unactivated) shell");
					window1.text2.setFocus();

					assertSame("expecting the 2nd shell to remain activated", display.getActiveShell(), window2.shell);

					onEventExecute(SWT.FocusIn, window1.text2, () -> {
						assertSame("expecting the 1st shell to be activated", display.getActiveShell(), window1.shell);
						assertTrue("expecting the the 1st shell to have remembered the previous setFocus and with the shell activation setting it to the 2nd text field", window1.text2.isFocusControl());

						onEventExecute(SWT.Activate, window2.shell, () -> {
							assertSame("expecting the 2nd shell to be activated after the 1st, active has been disposed", display.getActiveShell(), window2.shell);
							assertTrue("expecting the 1st text field in the 2nd shell to still have the focus because it hasn't been changed", window2.text1.isFocusControl());

							onEventExecute(SWT.FocusIn, window2.text2, () -> {
								assertSame("expecting the 2nd shell to remain activated", display.getActiveShell(), window2.shell);
								assertTrue("expecting the 2nd text field to have received the focus", window2.text2.isFocusControl());

								log("disposing the 2nd shell, too");
								window2.shell.dispose();
							});
							log("setting the focus to the 2nd text field");
							window2.text2.setFocus();
						});

						log("disposing 1st shell");
						window1.shell.dispose();
					});

					log("activating 1st shell");
					window1.shell.setActive();
				});

				log("open 2nd shell");
				window2.shell.open();
			});

			log("open 1st shell");
			window1.shell.open();

			while (!display.isDisposed() && !exit[0]) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}
		finally {
			if (!display.isDisposed()) {
				display.dispose();
			}
		}
	}

	private static void log(String s) {
		if (LOGGING) {
			System.out.println(s);
		}
	}

	private static void onEventExecute(int eventType, Control control, Runnable runnable) {
		final Display display = control.getDisplay();
		final class MyListener implements Listener, Runnable {
			private boolean eventReceived;

			@Override
			public void handleEvent(Event event) {
				eventReceived = true;

				log("Received event " + eventType + " on " + control);
				control.removeListener(eventType, this);

				display.asyncExec(() -> {
					if (control.isDisposed()) {
						return;
					}

					runnable.run();
				});
			}

			@Override
			public void run() {
				if (!eventReceived) {
					log("timeout, disposing display");
					display.dispose();
				}
			}
		}
		final MyListener listener = new MyListener();
		control.addListener(eventType, listener);
		display.timerExec(TIMEOUT, listener);
	}

	private static class Window {
		private final Shell shell;
		private final Text text1;
		private final Text text2;

		public Window(String title, Display display, boolean[] exit) {
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
				exit[0] = true;
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
