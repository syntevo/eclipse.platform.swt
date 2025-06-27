package org.eclipse.swt.widgets;

import java.util.*;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

public class TableItemRenderer {

	/** Gap between icon and text */
	private static final int GAP = 3;
	/** Left and right margins */
	private static final int DEFAULT_MARGIN = 3;
	/** Left and right margins */
	private static final int DEFAULT_MARGIN_UP_DOWN = 2;
	/** a string inserted in the middle of text that has been shortened */

	private static final int leftMargin = 3;
	private static final int rightMargin = DEFAULT_MARGIN;
	private static final int topMargin = DEFAULT_MARGIN_UP_DOWN;
	private static final int bottomMargin = DEFAULT_MARGIN_UP_DOWN;

	private final TableItem item;

	private boolean selected;
	private boolean hovered;
	Rectangle checkboxBounds;

	private final Map<Integer, Point> computedCellSizes = new HashMap<>();
	private final Map<Integer, Rectangle> internalComputedCellTextBounds = new HashMap<>();
	private final Map<Integer, Rectangle> internalComputedCellImage = new HashMap<>();
	private Point computedSize;

	public TableItemRenderer(TableItem tableItem) {
		this.item = tableItem;
	}

	public void doPaint(GC gc, int index) {
		final Table table = getParent();

		Rectangle itemBounds = item.getBounds();
		this.selected = false;
		this.hovered = false;
		int detail = SWT.BACKGROUND | SWT.FOREGROUND;
		if (table.isSelected(index)) {
			this.selected = true;
			detail |= SWT.SELECTED;

			gc.setBackground(Table.SELECTION_COLOR);
			gc.fillRectangle(itemBounds);
		}

		if (table.mouseHoverElement == item) {
			this.hovered = true;
			detail |= SWT.HOT;
			gc.setBackground(Table.HOVER_COLOR);
			gc.fillRectangle(itemBounds);
		}

		if (table.isFocusRow(index)) {
			detail |= SWT.FOCUSED;
		}

		if ((table.getStyle() & SWT.CHECK) != 0) {
			drawCheckbox(gc);
		}

		final Color background = gc.getBackground();
		final Color foreground = gc.getForeground();

		final int columnCount = table.getColumnCount();
		boolean drawFocusRect = false;
		if (columnCount > 0) {
			for (int i = 0; i < columnCount; i++) {
				Rectangle cellBounds = item.getBounds(i);
				if (drawCell(i, detail, cellBounds, gc)) {
					drawFocusRect = true;
				}
				gc.setBackground(background);
				gc.setForeground(foreground);
			}
		} else {
			drawFocusRect = drawCell(0, detail, itemBounds, gc);
			gc.setBackground(background);
			gc.setForeground(foreground);
		}

		if (drawFocusRect) {
			gc.drawFocus(itemBounds.x, itemBounds.y, itemBounds.width - 1, itemBounds.height - 1);
		}
	}

	private Table getParent() {
		return item.getParent();
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
			Event event = new Event();
			event.item = item;
			event.index = columnIndex;
			event.gc = gc;
			event.setBounds(bounds);
			// TODO MeasureItem should happen in the bounds calculation logic...
			table.sendEvent(SWT.MeasureItem, event);

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
				int currentWidthPosition = bounds.x + leftMargin;

				int xPosition = currentWidthPosition;
				int yPosition = bounds.y + topMargin;

				Image image = item.getImage(columnIndex);
				if (image != null) {
					gc.drawImage(image, xPosition, yPosition);
					currentWidthPosition += image.getBounds().width + GAP;
				}

				Color foreground = item.getForeground(columnIndex);
				if (foreground != null) {
					gc.setForeground(foreground);
				}

				gc.drawText(item.getText(columnIndex), currentWidthPosition, bounds.y + topMargin);
			}

			table.sendEvent(SWT.PaintItem, event);
			return (event.detail & SWT.FOCUSED) != 0;
		} finally {
			gc.setClipping((Rectangle) null);
		}
	}

	private Rectangle getBounds(int columnIndex) {
		return item.getBounds(columnIndex);
	}

	public Point computeCellSize(int colIndex, GC gc) {
		final Point cellSize = computedCellSizes.get(colIndex);
		if (cellSize != null) {
			return cellSize;
		}

		int height = topMargin + bottomMargin;
		int width = leftMargin + rightMargin;

		Image image = item.getImage(colIndex);
		if (image != null) {
			final Rectangle bounds = image.getBounds();
			Rectangle rec = new Rectangle(width, topMargin, bounds.width, bounds.height);
			internalComputedCellImage.put(colIndex, rec);
			height += bounds.height;
			width += bounds.width;
		}

		final Table table = getParent();

		String text = item.getText(colIndex);
		if (text != null) {
			Point size = table.computeTextExtent(text);

			Rectangle rec = new Rectangle(width, topMargin, size.x, size.y);
			internalComputedCellTextBounds.put(colIndex, rec);

			width += size.x;
			height += size.y;
		} else {
			internalComputedCellTextBounds.put(colIndex, new Rectangle(width, height, 0, 0));
		}

		if (image != null && text != null) {
			width += GAP;
		}

		Point size = table.sendMeasureItem(item, colIndex, gc, width, height);

		computedCellSizes.put(colIndex, size);

		return size;
	}

	Point computeSize(boolean changed) {
		if (!changed && this.computedSize != null) {
			return computedSize;
		}

		final Table parent = getParent();

		int width = leftMargin + rightMargin;

		int lineHeight = guessItemHeight(parent);
		int imageHeight = 0;

		if (item.images != null) {
			for (Image i : item.images) {
				if (i == null) {
					continue;
				}
				Rectangle imageBounds = i.getBounds();
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

		int height = topMargin + Math.max(lineHeight, imageHeight) + bottomMargin;

		if (parent.getColumnCount() > 0) {
			width = parent.getTotalColumnWidth();
		} else {
			Point textExtent = parent.computeTextExtent(item.getText());
			lineHeight = textExtent.y;
			width += textExtent.x;
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
		return textHeight + topMargin + bottomMargin;
	}

	public Rectangle getTextBounds(int index, GC gc) {
		if (internalComputedCellTextBounds.get(index) == null) {
			computeCellSize(index, gc);
		}

		Rectangle internal = internalComputedCellTextBounds.get(index);
		Rectangle outer = getBounds(index);
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
		Rectangle outer = getBounds(index);
		return new Rectangle(outer.x + internal.x, outer.y + internal.y, internal.width, internal.height);
	}
}
