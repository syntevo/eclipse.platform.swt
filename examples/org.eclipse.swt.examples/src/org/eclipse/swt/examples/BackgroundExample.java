package org.eclipse.swt.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Thomas Singer
 */
final class BackgroundExample {

	public static void main(String[] args) {
		final Display display = new Display();

		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(2, false));
		shell.setBackground(new Color(240, 230, 220));
		shell.setBackgroundMode(SWT.INHERIT_DEFAULT);

		new Button(shell, SWT.PUSH).setText("Push");

		new Button(shell, SWT.TOGGLE).setText("Toggle");

		new Button(shell, SWT.CHECK).setText("Checkbox");

		new Button(shell, SWT.RADIO).setText("Radio");

		shell.setSize(400, 300);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}
}
