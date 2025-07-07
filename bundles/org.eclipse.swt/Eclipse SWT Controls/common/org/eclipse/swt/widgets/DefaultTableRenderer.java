package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
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
		if (table.sortDirection != SWT.NONE && column == table.sortColumn) {
			// don't simplify
			headerSize.x += headerSize.y / 8 * 4 + 2;
		}
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

		gc.setAntialias(SWT.ON);

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
		drawHLine(gc, 0, ca.width, height - 1);

		final TableColumn[] columns = table.getColumns();
		for (int i = 0; i < columns.length; i++) {
			TableColumn column = columns[i];
			final int x = column.getXScrolled();
			final int width = column.getWidth();
			if (x + width < ca.x
					|| x >= ca.x + ca.width) {
				columns[i] = null;
				continue;
			}

			drawVLine(gc, x + width, HEADER_MARGIN_Y, height - HEADER_MARGIN_Y - 1);
		}

		final Rectangle clipping = gc.getClipping();

		gc.setForeground(textColor);
		for (TableColumn column : columns) {
			if (column == null) {
				continue;
			}

			final String text = column.getText();
			final int x = column.getXScrolled();
			final int left = x + HEADER_MARGIN_X;
			int right = x + column.getWidth() - HEADER_MARGIN_X;

			if (table.sortColumn == column && table.sortDirection != SWT.NONE) {
				final int y = height / 2;
				final int textHeight = gc.textExtent(text).y;
				final int size = textHeight / 8;
				final int sizeY = table.sortDirection == SWT.UP ? size : -size;
				if (right - left > 10 * size) {
					final int lineWidth = gc.getLineWidth();
					final int antialias = gc.getAntialias();
					gc.setForeground(new Color(160, 160, 160));
					gc.setAntialias(SWT.ON);
					gc.setLineWidth(textHeight > 20 ? 2 : 1);
					gc.drawLine(right - 4 * size, y + sizeY,
					            right - 2 * size, y - sizeY);
					gc.drawLine(right - 2 * size, y - sizeY,
					            right, y + sizeY);
					gc.setLineWidth(lineWidth);
					gc.setAntialias(antialias);

					right -= 4 * size + 2;
					gc.setForeground(textColor);
				}
			}

			final int spaceForText = right - left;
			gc.setClipping(new Rectangle(left, 0, spaceForText, height));

			int textX = left;

			final int style = column.getStyle() & (SWT.RIGHT | SWT.CENTER);
			if (style != 0) {
				final int textWidth = gc.textExtent(text).x;
				if ((style & SWT.RIGHT) != 0) {
					textX += Math.max(0, spaceForText - textWidth);
				}
				else {
					textX += Math.max(0, (spaceForText - textWidth) / 2);
				}
			}

			gc.drawText(text, textX, HEADER_MARGIN_Y, true);
			gc.setClipping(clipping);
		}
	}

	private void drawHLine(GC gc, int x1, int x2, int y) {
		gc.drawLine(x1, y, x2, y);
	}

	private void drawVLine(GC gc, int x, int y1, int y2) {
		gc.drawLine(x, y1, x, y2);
	}
}
