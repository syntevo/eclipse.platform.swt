package org.eclipse.swt.widgets;

import org.eclipse.swt.graphics.*;

public class DefaultTableRenderer extends TableRenderer {

	private final TableColumnRenderer renderer = new TableColumnRenderer();

	protected DefaultTableRenderer(Table table) {
		super(table);
	}

	@Override
	public Point computeSize(TableColumn column) {
		return renderer.computeSize(column, table);
	}

	@Override
	public void paint(GC gc) {
		Rectangle ca = table.getClientArea();
		if (ca.width == 0 || ca.height == 0) return;

		table.updateScrollBarWithTextSize();

		gc.fillRectangle(ca);

		paintHeader(gc);
		table.getItemsHandler().paint(gc);
	}

	private void paintHeader(GC gc) {
		if (!table.getHeaderVisible()) return;

		Rectangle ca = table.getClientArea();
		final int height = table.getHeaderHeight();
		gc.drawLine(0, height, ca.width, height);

		for (TableColumn c : table.getColumns()) {
			final int x = c.getX();
			final int width = c.getWidth();
			if (x + width < ca.x
					|| x >= ca.x + ca.width) {
				continue;
			}

			paintColumnHeader(gc, c, height);
		}
	}

	private void paintColumnHeader(GC gc, TableColumn c, int height) {
		renderer.doPaint(c, gc, height);
	}
}
