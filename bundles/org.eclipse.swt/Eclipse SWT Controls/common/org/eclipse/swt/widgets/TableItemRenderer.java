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

	public void doPaint(GC gc, int index, boolean paintItemEvent) {
		final Table table = getParent();

		Rectangle itemBounds = item.getBounds();
		if (table.isSelected(index)) {
			this.selected = true;

			gc.setBackground(Table.SELECTION_COLOR);
			gc.fillRectangle(itemBounds);
		} else if (table.mouseHoverElement == item) {
			this.hovered = true;
			gc.setBackground(Table.HOVER_COLOR);
			gc.fillRectangle(itemBounds);
		} else {
			this.selected = false;
			this.hovered = false;
		}

		if (table.isFocusRow(index)) {
			gc.drawFocus(itemBounds.x, itemBounds.y, itemBounds.width - 1, itemBounds.height - 1);
		}

		if ((table.getStyle() & SWT.CHECK) != 0) {
			drawCheckbox(gc);
		}

		final Color background = gc.getBackground();
		final Color foreground = gc.getForeground();

		final int columnCount = table.getColumnCount();
		if (columnCount > 0) {
			for (int i = 0; i < columnCount; i++) {
				drawCell(i, gc);
				gc.setBackground(background);
				gc.setForeground(foreground);
			}
		} else {
			drawItem(gc, paintItemEvent, itemBounds);
			gc.setBackground(background);
			gc.setForeground(foreground);
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

	private void drawCell(int columnIndex, GC gc) {
		final Table table = getParent();

		Color background = gc.getBackground();

		Rectangle b = getBounds(columnIndex);
		gc.setClipping(b);
		try {
			Rectangle itemBounds = item.getBounds(columnIndex);
			Event event = new Event();
			event.item = item;
			event.index = columnIndex;
			event.gc = gc;
			event.setBounds(itemBounds);
			// TODO MeasureItem should happen in the bounds calculation logic...
			table.sendEvent(SWT.MeasureItem, event);

			event.detail = SWT.BACKGROUND | SWT.FOREGROUND;
			if (this.hovered) {
				event.detail |= SWT.HOT;
			}
			if (this.selected) {
				event.detail |= SWT.SELECTED;
			}
			table.sendEvent(SWT.EraseItem, event);

			if ((event.detail & SWT.BACKGROUND) != 0) {
				if ((event.detail & SWT.SELECTED) != 0) {
					gc.setBackground(Table.SELECTION_COLOR);
				} else if ((event.detail & SWT.HOT) != 0) {
					gc.setBackground(Table.HOVER_COLOR);
				} else {
					gc.setBackground(background);
				}
				gc.fillRectangle(itemBounds);
			}

			if ((event.detail & SWT.FOREGROUND) != 0) {
				int currentWidthPosition = b.x + leftMargin;

				int xPosition = currentWidthPosition;
				int yPosition = b.y + topMargin;

				Image image = item.getImage(columnIndex);
				if (image != null) {
					gc.drawImage(image, xPosition, yPosition);
					currentWidthPosition += image.getBounds().width + GAP;
				}

				Color fgCol = item.getForeground(columnIndex);
				if (fgCol != null && !this.selected && !this.hovered) {
					gc.setForeground(fgCol);
				}

				gc.drawText(item.getText(columnIndex), currentWidthPosition, b.y + topMargin);
			}
			table.sendEvent(SWT.PaintItem, event);
		} finally {
			gc.setClipping((Rectangle) null);
		}
	}

	private Rectangle getBounds(int columnIndex) {
		return item.getBounds(columnIndex);
	}

	private void drawItem(GC gc, boolean paintItemEvent, Rectangle bounds) {
		gc.setClipping(bounds);

		try {
			final Table parent = getParent();
			if (paintItemEvent) {
				Event event = new Event();
				event.item = item;
				event.index = 0;
				event.gc = gc;
				event.x = bounds.x;
				event.y = bounds.y;
				parent.sendEvent(SWT.MeasureItem, event);
				parent.sendEvent(SWT.EraseItem, event);
				parent.sendEvent(SWT.PaintItem, event);
				return;
			}

			Color prevBgColor = gc.getBackground();
			Color bgColor = item.getBackground();
			if (bgColor != null && !this.selected && !this.hovered) {
				gc.setBackground(bgColor);
				gc.fillRectangle(bounds);
			}

			int currentWidthPosition = bounds.x + leftMargin;

			int xPosition = currentWidthPosition;
			int yPosition = bounds.y + topMargin;

			Image image = item.getImage();
			if (image != null) {
				gc.drawImage(image, xPosition, yPosition);
				currentWidthPosition += image.getBounds().width + GAP;
			}

			Color prevFG = gc.getForeground();
			Color fgCol = item.getForeground();
			if (fgCol != null && !this.selected && !this.hovered) {
				gc.setForeground(fgCol);
			}

			gc.drawText(item.getText(), currentWidthPosition, bounds.y + topMargin);

			gc.setForeground(prevFG);
			gc.setBackground(prevBgColor);
		} finally {
			gc.setClipping((Rectangle) null);
		}
	}

	public Point computeCellSize(int colIndex) {
		final Point cellSize = computedCellSizes.get(colIndex);
		if (cellSize != null) {
			return cellSize;
		}

		var image = item.getImage(colIndex);

		int height = topMargin + bottomMargin;
		int width = leftMargin + rightMargin;

		if (image != null) {
			final Rectangle bounds = image.getBounds();
			var rec = new Rectangle(width, topMargin, bounds.width, bounds.height);
			internalComputedCellImage.put(colIndex, rec);
			height += bounds.height;
			width += bounds.width;
		}

		var text = item.getText(colIndex);
		if (text != null) {
			var size = getParent().computeTextExtent(text);

			var rec = new Rectangle(width, topMargin, size.x, size.y);
			internalComputedCellTextBounds.put(colIndex, rec);

			width += size.x;
			height += size.y;
		} else {
			internalComputedCellTextBounds.put(colIndex, new Rectangle(width, height, 0, 0));
		}

		if (image != null && text != null) {
			width += GAP;
		}

		var p = new Point(width, height);

		computedCellSizes.put(colIndex, p);

		return p;
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
			for (var i : item.images) {
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

	public Rectangle getTextBounds(int index) {
		if (internalComputedCellTextBounds.get(index) == null) {
			computeCellSize(index);
		}

		Rectangle internal = internalComputedCellTextBounds.get(index);
		Rectangle outer = getBounds(index);
		return new Rectangle(outer.x + internal.x, outer.y + internal.y, internal.width, internal.height);
	}

	public Rectangle getImageBounds(int index) {
		if (item.getImage(index) == null) {
			return new Rectangle(0, 0, 0, 0);
		}

		if (internalComputedCellImage.get(index) == null) {
			computeCellSize(index);
		}

		Rectangle internal = internalComputedCellImage.get(index);
		Rectangle outer = getBounds(index);
		return new Rectangle(outer.x + internal.x, outer.y + internal.y, internal.width, internal.height);
	}
}
