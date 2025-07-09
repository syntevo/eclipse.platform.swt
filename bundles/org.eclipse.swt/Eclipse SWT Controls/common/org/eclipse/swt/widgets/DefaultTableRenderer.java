package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

public class DefaultTableRenderer extends TableRenderer {

	private static final Color HOVER_COLOR = new Color(234, 244, 255);
	private static final Color SELECTION_COLOR = new Color(224, 238, 254);
	private static final Color HEADER_LINE_COLOR = new Color(192, 192, 192);
	private static final Color HEADER_SORT_INDICATOR_COLOR = new Color(160, 160, 160);

	private static final int DRAW_FLAGS = SWT.DRAW_MNEMONIC | SWT.DRAW_TAB | SWT.DRAW_TRANSPARENT | SWT.DRAW_DELIMITER;

	private static final int HEADER_MARGIN_X = 6;
	private static final int HEADER_MARGIN_Y = 3;
	private static final int DEFAULT_MARGIN_DOWN = 1;
	private static final int GAP = 3;
	private static final int MARGIN_X = 3;
	private static final int MARGIN_Y = 2;
	private static final int INITIAL_RIGHT_SHIFT = 3;
	private static final int CHECKBOX_RIGHT_SHIFT = 35;

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

		final int initialItemHeight = table.getItemHeight();

		paintItems(gc);

		if (table.getItemHeight() != initialItemHeight) {
			table.updateScrollBarWithTextSize();
			paintItems(gc);
		}
	}

	@Override
	public int guessItemHeight() {
		int textHeight = table.guessTextHeight();
		return textHeight + MARGIN_Y + MARGIN_Y;
	}

	@Override
	public boolean drawCell(TableItem item, int columnIndex, int detailDefault, Rectangle bounds, int left, GC gc) {
		final Rectangle cellRect = new Rectangle(left, bounds.y, Math.max(0, bounds.width + bounds.x - left), bounds.height);

		gc.setClipping(cellRect);
		try {
			Event event = table.sendMeasureItemMaybeExtendItemHeight(item, columnIndex, gc, cellRect);

			event.detail = detailDefault;
			table.sendEvent(SWT.EraseItem, event);

			if (!event.doit) {
				event.detail = 0;
			}
			event.detail &= detailDefault;

			if ((event.detail & SWT.BACKGROUND) != 0) {
				if ((event.detail & SWT.SELECTED) != 0) {
					gc.setBackground(SELECTION_COLOR);
				} else if ((event.detail & SWT.HOT) != 0) {
					gc.setBackground(HOVER_COLOR);
				}
				gc.fillRectangle(cellRect);
			}

			if ((event.detail & SWT.FOREGROUND) != 0) {
				int x = bounds.x + MARGIN_X;

				Image image = item.getImage(columnIndex);
				if (image != null) {
					final Rectangle imageSize = image.getBounds();
					gc.drawImage(image, x, bounds.y + (bounds.height - imageSize.height) / 2);
					x += imageSize.width + GAP;
				}

				Color foreground = item.getForeground(columnIndex);
				if (foreground != null) {
					gc.setForeground(foreground);
				}

				final String text = item.getText(columnIndex);
				if (text.length() > 0) {
					final int fontHeight = gc.getFontMetrics().getHeight();
					gc.drawText(text, x, bounds.y + (bounds.height - fontHeight) / 2);
				}
			}

			table.sendEvent(SWT.PaintItem, event);
			return (event.detail & SWT.FOCUSED) != 0;
		} finally {
			gc.setClipping((Rectangle) null);
		}
	}

	@Override
	public Point computeSize(TableItem item, GC gc) {
		int width = MARGIN_X + MARGIN_X;
		int imageHeight = 0;

		final int columnCount = table.getColumnCount();
		if (columnCount > 0) {
			for (int i = 0; i < columnCount; i++) {
				final Image image = item.getImage(i);
				if (image != null) {
					Rectangle imageBounds = image.getBounds();
					imageHeight = Math.max(imageBounds.height, imageHeight);
				}
			}

			width = table.getTotalColumnWidth();
		}
		else {
			final Image image = item.getImage();
			if (image != null) {
				Rectangle imageBounds = item.image.getBounds();
				imageHeight = Math.max(imageBounds.height, imageHeight);
				width += imageBounds.width;
				if (item.text != null) {
					width += GAP;
				}
			}

			final String text = item.getText();
			width += gc.textExtent(text, DRAW_FLAGS).x;
			width += getLeftIndent();
		}

		final int textHeight = table.guessTextHeight();
		final int height = MARGIN_Y + Math.max(textHeight, imageHeight) + MARGIN_Y;
		return new Point(width, height);
	}

	@Override
	public int getLeftIndent() {
		int indent = INITIAL_RIGHT_SHIFT;

		if ((table.getStyle() & SWT.CHECK) != 0) {
			indent = CHECKBOX_RIGHT_SHIFT;
		}
		return indent;
	}

	@Override
	public Point computeCellSize(TableItem item, int colIndex, GC gc, Rectangle imageBounds, Rectangle textBounds) {
		int height = MARGIN_Y + MARGIN_Y;
		int width = MARGIN_X + MARGIN_X;

		Image image = item.getImage(colIndex);
		if (image != null) {
			final Rectangle bounds = image.getBounds();
			if (imageBounds != null) {
				imageBounds.x = width;
				imageBounds.y = MARGIN_Y;
				imageBounds.width = bounds.width;
				imageBounds.height = bounds.height;
			}
			height += bounds.height;
			width += bounds.width;
		}

		String text = item.getText(colIndex);
		if (text != null) {
			Point textSize = gc.textExtent(text, DRAW_FLAGS);

			if (textBounds != null) {
				textBounds.x = width;
				textBounds.y = MARGIN_Y;
				textBounds.width = textSize.x;
				textBounds.height = textSize.y;
			}

			width += textSize.x;
			height += textSize.y;
		} else {
			if (textBounds != null) {
				textBounds.x = width;
				textBounds.y = height;
				textBounds.width = 0;
				textBounds.height = 0;
			}
		}

		if (image != null && text != null) {
			width += GAP;
		}

		return new Point(width, height);
	}

	private void paintHeader(GC gc) {
		final Color textColor = gc.getForeground();

		Rectangle ca = table.getClientArea();
		final int height = table.getHeaderHeight();
		gc.setForeground(HEADER_LINE_COLOR);
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
					gc.setForeground(HEADER_SORT_INDICATOR_COLOR);
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

	private void paintItems(GC gc) {
		Rectangle ca = table.getClientArea();
		final int maxY = ca.y + ca.height;

		for (int i = table.getTopIndex(); i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);

			if (table.isVirtual()) {
				table.checkData(item, i, false);
			}

			item.doPaint(gc, i);

			final Rectangle bounds = item.getFullBounds();
			if (bounds.y + bounds.height > maxY) {
				break;
			}
		}
	}

	private void drawHLine(GC gc, int x1, int x2, int y) {
		gc.drawLine(x1, y, x2, y);
	}

	private void drawVLine(GC gc, int x, int y1, int y2) {
		gc.drawLine(x, y1, x, y2);
	}
}
