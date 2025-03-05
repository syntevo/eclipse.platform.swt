package org.eclipse.swt.widgets.renderers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Thomas Singer
 */
public class RendererMain {

	private static final Color BLUE = new Color(0, 0, 128);
	private static final Color PINK = new Color(255, 180, 180);
	private static final Color YELLOW = new Color(255, 255, 0);
	private static final Color CYAN = new Color(0, 192, 255);

	public static void main(String[] args) {
		final Display display = new Display();

		final Shell shell = new Shell(display);
		final RendererContainer root = new RendererContainer(RendererContainer.Layout.LEFT_TO_RIGHT);
		root.setSize(1600, Renderer.FIT);
		root.setBackground(BLUE);
		root.setPadding(32);
		root.setChildGap(32);

		final Renderer child1 = new Renderer(root);
		child1.setBackground(PINK);
		child1.setSize(300, 300);

		final Renderer child2 = new Renderer(root);
		child2.setBackground(YELLOW);
		child2.setSize(Renderer.GROW, 200);

		final Renderer child3 = new Renderer(root);
		child3.setBackground(CYAN);
		child3.setSize(300, 300);

		root.layout();

		shell.addListener(SWT.Paint, e -> root.paint(e.gc));

		shell.setSize(1700, 700);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
