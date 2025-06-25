package org.eclipse.swt.widgets;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class TableColumnRenderer {

	/** Left and right margins */
	private static final int HEADER_MARGIN_X = 6;

	/** up margin */
	private static final int HEADER_MARGIN_Y = 3;

	/** down margin */
	private static final int DEFAULT_MARGIN_DOWN = 1;

	public TableColumnRenderer() {
	}

	public void doPaint(TableColumn column, GC gc, int height) {
		final int x = column.getX();
		final int separatorX = x + column.getWidth();
		gc.drawLine(separatorX, HEADER_MARGIN_Y,
				separatorX, height - HEADER_MARGIN_Y);

		int xPosition = x + HEADER_MARGIN_X;
		int yPosition = HEADER_MARGIN_Y;
		gc.drawText(column.getText(), xPosition, yPosition);
	}

	static int guessColumnHeight(TableColumn column) {
		int textHeight = Table.guessTextHeight(column.getParent());
		return textHeight + 2 * HEADER_MARGIN_Y;
	}

	public Point computeSize(TableColumn column, Table parent) {
		final GC gc = new GC(parent);
		try {
			int colIndex = parent.indexOf(column);
			Point fin = new Point(0, 0);
			int width = 0;
			final TableItem[] items = parent.getItems();
			if (items != null) {
				final boolean virtual = parent.isVirtual();
				for (TableItem item : items) {
					if (virtual && !item.cached) {
						continue;
					}
					Point p = item.computeCellSize(colIndex);
					width = Math.max(width, p.x);
					item.clearCache();
				}
			}

			Point headerExt = gc.textExtent(column.getText());
			fin.x = Math.max(headerExt.x + 2 * HEADER_MARGIN_X, width);
			fin.y = Math.max(headerExt.y + HEADER_MARGIN_Y + DEFAULT_MARGIN_DOWN, 10);
			return fin;
		} finally {
			gc.dispose();
		}
	}
}
