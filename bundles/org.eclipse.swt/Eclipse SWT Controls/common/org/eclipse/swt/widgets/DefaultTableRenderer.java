package org.eclipse.swt.widgets;

import org.eclipse.swt.graphics.*;

public class DefaultTableRenderer extends TableRenderer {

	private static final int HEADER_MARGIN_X = 6;
	private static final int HEADER_MARGIN_Y = 3;
	private static final int DEFAULT_MARGIN_DOWN = 1;

	protected DefaultTableRenderer(Table table) {
		super(table);
	}

	@Override
	public Point computeHeaderSize(TableColumn column, GC gc) {
		Point headerSize = gc.textExtent(column.getText());
		headerSize.x += 2 * HEADER_MARGIN_X;
		headerSize.y = Math.max(headerSize.y + HEADER_MARGIN_Y + DEFAULT_MARGIN_DOWN, 10);
		return headerSize;
	}

	@Override
	public int calculateColumnHeight() {
		int textHeight = table.guessTextHeight();
		return textHeight + 2 * HEADER_MARGIN_Y;
	}

	@Override
	public void paint(GC gc) {
		Rectangle ca = table.getClientArea();
		if (ca.width == 0 || ca.height == 0) return;

		gc.setBackground(table.getBackground());
		gc.fillRectangle(ca);

		if (table.getHeaderVisible()) {
			paintHeader(gc);
		}
		table.getItemsHandler().paint(gc);
	}

	private void paintHeader(GC gc) {
		final Color textColor = gc.getForeground();
		final Color lineColor = new Color(192, 192, 192);

		Rectangle ca = table.getClientArea();
		final int height = table.getHeaderHeight();
		gc.setForeground(lineColor);
		gc.drawLine(0, 0, ca.width, 0);
		gc.drawLine(0, height - 1, ca.width, height);

		final TableColumn[] columns = table.getColumns();
		for (int i = 0; i < columns.length; i++) {
			TableColumn column = columns[i];
			final int x = column.getX();
			final int width = column.getWidth();
			if (x + width < ca.x
					|| x >= ca.x + ca.width) {
				columns[i] = null;
				continue;
			}

			final int separatorX = x + width;
			gc.drawLine(separatorX, HEADER_MARGIN_Y,
					separatorX, height - HEADER_MARGIN_Y - 1);
		}

		gc.setForeground(textColor);
		for (TableColumn column : columns) {
			if (column == null) {
				continue;
			}

			int xPosition = column.getX() + HEADER_MARGIN_X;
			int yPosition = HEADER_MARGIN_Y;
			gc.drawText(column.getText(), xPosition, yPosition);
		}
	}
}
