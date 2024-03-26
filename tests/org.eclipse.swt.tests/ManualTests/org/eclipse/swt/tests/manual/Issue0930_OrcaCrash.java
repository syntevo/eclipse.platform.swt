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

import java.lang.reflect.*;
import java.util.List;
import java.util.*;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class Issue0930_OrcaCrash {
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout (new GridLayout (1, true));

		Label hint = new Label(shell, 0);
		hint.setText(
				"1. Run on Ubuntu 23.04 or later, or some other modern Linux\n" +
						"2. Press the button\n" +
						"3. Issue 930: JVM will crash or Gtk-CRITICAL occurs\n" +
						"   to make crash more reliable, run with:\n" +
						"   export G_SLICE=always-malloc\n" +
						"   export MALLOC_PERTURB_=204"
		);

		Text text = new Text(shell, 0);
		text.forceFocus();

		Button button = new Button(shell, 0);
		button.setText("Test");
		button.addListener(SWT.Selection, e -> {
			StyledText styledText = new StyledText(shell, 0);
			styledText.setText("Test StyledText");
			styledText.forceFocus();

			display.timerExec(1000, () -> {
				styledText.dispose();
			});
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
