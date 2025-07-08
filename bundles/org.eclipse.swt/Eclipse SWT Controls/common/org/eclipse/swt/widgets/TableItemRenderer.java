package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

public class TableItemRenderer {

	private static final int DRAW_FLAGS = SWT.DRAW_MNEMONIC | SWT.DRAW_TAB | SWT.DRAW_TRANSPARENT | SWT.DRAW_DELIMITER;

	private static final int GAP = 3;
	private static final int MARGIN_X = 3;
	private static final int MARGIN_Y = 2;

	private final TableItem item;

	public TableItemRenderer(TableItem item) {
		this.item = item;
	}

	public void doPaint(GC gc, int index) {
		final Table table = getParent();

		final int detail = prepareEventDetail(index, table);

		final Rectangle itemBounds = item.getBounds();

		final int columnCount = table.getColumnCount();
		boolean drawFocusRect = false;
		if (columnCount > 0) {
			for (int i = 0; i < columnCount; i++) {
				final TableColumn column = table.getColumn(i);
				Rectangle cellBounds = item.getBounds(i);
				cellBounds.width = column.getWidth();

				final int x = column.getXScrolled();

				gc.setBackground(item.getBackground(i));
				gc.setForeground(item.getForeground(i));
				if (drawCell(i, detail, cellBounds, x, gc)) {
					drawFocusRect = true;
				}
				if (i == 0 && (table.getStyle() & SWT.CHECK) != 0) {
					drawCheckbox(gc, x, cellBounds.y, cellBounds.height);
				}
			}
		} else {
			gc.setBackground(item.getBackground());
			gc.setForeground(item.getForeground());
			drawFocusRect = drawCell(0, detail, itemBounds, 0, gc);

			if ((table.getStyle() & SWT.CHECK) != 0) {
				final Rectangle fullBounds = item.getFullBounds();
				drawCheckbox(gc, fullBounds.x, fullBounds.y, fullBounds.height);
			}
		}

		if (drawFocusRect) {
			gc.drawFocus(itemBounds.x, itemBounds.y, itemBounds.width - 1, itemBounds.height - 1);
		}
	}

	private Table getParent() {
		return item.getParent();
	}

	private int prepareEventDetail(int index, Table table) {
		int detail = SWT.BACKGROUND | SWT.FOREGROUND;
		if (table.isSelected(index)) {
			detail |= SWT.SELECTED;
		}

		if (table.mouseHoverElement == item) {
			detail |= SWT.HOT;
		}

		if (table.isFocusRow(index)) {
			detail |= SWT.FOCUSED;
		}
		return detail;
	}

	private void drawCheckbox(GC gc, int x, int y, int height) {
		final int size = 20;

		x += 5;
		y += (height - size) / 2;

		gc.setBackground(new Color(255, 255, 255));
		gc.fillRoundRectangle(x, y, size, size, 5, 5);
		gc.drawRoundRectangle(x, y, size, size, 5, 5);

		if (item.getChecked()) {
			final int lineWidth = gc.getLineWidth();
			gc.setLineWidth(2);
			final int inset = 5;
			gc.drawLine(x + inset, y + inset, x + size - inset,
					y + size - inset);
			gc.drawLine(x + size - inset, y + inset,
					x + inset, y + size - inset);
			gc.setLineWidth(lineWidth);
		}
	}

	private boolean drawCell(int columnIndex, int detailDefault, Rectangle bounds, int left, GC gc) {
		final Table table = getParent();

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
					gc.setBackground(Table.SELECTION_COLOR);
				} else if ((event.detail & SWT.HOT) != 0) {
					gc.setBackground(Table.HOVER_COLOR);
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

	public Point computeSize(GC gc) {
		final Table table = getParent();

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
			width += table.getLeftIndent();
		}

		final int textHeight = table.guessTextHeight();
		final int height = MARGIN_Y + Math.max(textHeight, imageHeight) + MARGIN_Y;
		return new Point(width, height);
	}

	public Point computeCellSize(int colIndex, GC gc) {
		return computeCellSize(colIndex, gc, null, null);
	}

	public Point computeCellSize(int colIndex, GC gc, Rectangle imageBounds, Rectangle textBounds) {
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

	public static int guessItemHeight(Table table) {
		int textHeight = table.guessTextHeight();
		return textHeight + MARGIN_Y + MARGIN_Y;
	}
}
