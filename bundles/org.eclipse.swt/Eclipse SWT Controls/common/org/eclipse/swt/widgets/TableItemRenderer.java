package org.eclipse.swt.widgets;

import java.util.*;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

public class TableItemRenderer {

	private static final int GAP = 3;
	private static final int MARGIN_X = 3;
	private static final int MARGIN_Y = 2;

	private final TableItem item;

	Rectangle checkboxBounds;

	private final Map<Integer, Point> computedCellSizes = new HashMap<>();
	private final Map<Integer, Rectangle> internalComputedCellTextBounds = new HashMap<>();
	private final Map<Integer, Rectangle> internalComputedCellImage = new HashMap<>();
	private Point computedSize;

	public TableItemRenderer(TableItem item) {
		this.item = item;
	}

	public void doPaint(GC gc, int index) {
		final Table table = getParent();

		final int detail = prepareEventDetail(index, table);

		if ((table.getStyle() & SWT.CHECK) != 0) {
			drawCheckbox(gc);
		}

		final Rectangle itemBounds = item.getBounds();
		final int height = table.getItemHeight();
		itemBounds.height = height;

		final int columnCount = table.getColumnCount();
		boolean drawFocusRect = false;
		if (columnCount > 0) {
			for (int i = 0; i < columnCount; i++) {
				final TableColumn column = table.getColumn(i);
				Rectangle cellBounds = item.getBounds(i);
				cellBounds.width = column.getWidth();
				cellBounds.height = height;

				gc.setBackground(item.getBackground(i));
				gc.setForeground(item.getForeground(i));
				if (drawCell(i, detail, cellBounds, gc)) {
					drawFocusRect = true;
				}
			}
		} else {
			gc.setBackground(item.getBackground());
			gc.setForeground(item.getForeground());
			drawFocusRect = drawCell(0, detail, itemBounds, gc);
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

	private void drawCheckbox(GC gc) {
		Rectangle itemBounds = item.getFullBounds();

		this.checkboxBounds = new Rectangle(itemBounds.x + 5, itemBounds.y + 3, 20, 20);

		gc.drawRectangle(this.checkboxBounds);
		if (item.getChecked()) {
			gc.drawLine(this.checkboxBounds.x, this.checkboxBounds.y, this.checkboxBounds.x + this.checkboxBounds.width,
					this.checkboxBounds.y + this.checkboxBounds.height);
			gc.drawLine(this.checkboxBounds.x + this.checkboxBounds.width, this.checkboxBounds.y, this.checkboxBounds.x,
					this.checkboxBounds.y + this.checkboxBounds.height);
		}
	}

	private boolean drawCell(int columnIndex, int detailDefault, Rectangle bounds, GC gc) {
		final Table table = getParent();

		gc.setClipping(bounds);
		try {
			Event event = table.sendMeasureItem(item, columnIndex, gc, bounds);

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
				gc.fillRectangle(bounds);
			}

			if ((event.detail & SWT.FOREGROUND) != 0) {
				int currentWidthPosition = bounds.x + MARGIN_X;

				int xPosition = currentWidthPosition;
				int yPosition = bounds.y + MARGIN_Y;

				Image image = item.getImage(columnIndex);
				if (image != null) {
					gc.drawImage(image, xPosition, yPosition);
					currentWidthPosition += image.getBounds().width + GAP;
				}

				Color foreground = item.getForeground(columnIndex);
				if (foreground != null) {
					gc.setForeground(foreground);
				}

				gc.drawText(item.getText(columnIndex), currentWidthPosition, bounds.y + MARGIN_Y);
			}

			table.sendEvent(SWT.PaintItem, event);
			return (event.detail & SWT.FOCUSED) != 0;
		} finally {
			gc.setClipping((Rectangle) null);
		}
	}

	public Point computeCellSize(int colIndex, GC gc) {
		final Point cellSize = computedCellSizes.get(colIndex);
		if (cellSize != null) {
			return cellSize;
		}

		int height = MARGIN_Y + MARGIN_Y;
		int width = MARGIN_X + MARGIN_X;

		Image image = item.getImage(colIndex);
		if (image != null) {
			final Rectangle bounds = image.getBounds();
			Rectangle rec = new Rectangle(width, MARGIN_Y, bounds.width, bounds.height);
			internalComputedCellImage.put(colIndex, rec);
			height += bounds.height;
			width += bounds.width;
		}

		final Table table = getParent();

		String text = item.getText(colIndex);
		if (text != null) {
			Point size = table.computeTextExtent(text);

			Rectangle rec = new Rectangle(width, MARGIN_Y, size.x, size.y);
			internalComputedCellTextBounds.put(colIndex, rec);

			width += size.x;
			height += size.y;
		} else {
			internalComputedCellTextBounds.put(colIndex, new Rectangle(width, height, 0, 0));
		}

		if (image != null && text != null) {
			width += GAP;
		}

		final Rectangle bounds = item.getBounds(colIndex);
		bounds.width = width;
		bounds.height = height;
		Event event = table.sendMeasureItem(item, colIndex, gc, bounds);
		final Point size = new Point(event.width, event.height);

		computedCellSizes.put(colIndex, size);

		return size;
	}

	Point computeSize() {
		if (computedSize != null) {
			return computedSize;
		}

		final Table table = getParent();

		int width = MARGIN_X + MARGIN_X;

		int lineHeight = guessItemHeight(table);
		int imageHeight = 0;

		if (item.images != null) {
			for (Image image : item.images) {
				if (image == null) {
					continue;
				}
				Rectangle imageBounds = image.getBounds();
				imageHeight = Math.max(imageBounds.height, imageHeight);
			}
		} else if (item.image != null) {
			Rectangle imageBounds = item.image.getBounds();
			imageHeight = Math.max(imageBounds.height, imageHeight);
			width += imageBounds.width;
			if (item.text != null) {
				width += GAP;
			}
		}

		int height = MARGIN_Y + Math.max(lineHeight, imageHeight) + MARGIN_Y;

		if (table.getColumnCount() > 0) {
			width = table.getTotalColumnWidth();
		} else {
			width += table.computeTextExtent(item.getText()).x;
		}

		this.computedSize = new Point(width, height);

		return this.computedSize;
	}

	public void clearCache() {
		this.computedCellSizes.clear();
		this.internalComputedCellTextBounds.clear();
		this.internalComputedCellImage.clear();
		computedSize = null;
	}

	public static int guessItemHeight(Table table) {
		int textHeight = table.guessTextHeight();
		return textHeight + MARGIN_Y + MARGIN_Y;
	}

	public Rectangle getTextBounds(int index, GC gc) {
		if (internalComputedCellTextBounds.get(index) == null) {
			computeCellSize(index, gc);
		}

		Rectangle internal = internalComputedCellTextBounds.get(index);
		Rectangle outer = item.getBounds(index);
		return new Rectangle(outer.x + internal.x, outer.y + internal.y, internal.width, internal.height);
	}

	public Rectangle getImageBounds(int index, GC gc) {
		if (item.getImage(index) == null) {
			return new Rectangle(0, 0, 0, 0);
		}

		if (internalComputedCellImage.get(index) == null) {
			computeCellSize(index, gc);
		}

		Rectangle internal = internalComputedCellImage.get(index);
		Rectangle outer = item.getBounds(index);
		return new Rectangle(outer.x + internal.x, outer.y + internal.y, internal.width, internal.height);
	}
}
