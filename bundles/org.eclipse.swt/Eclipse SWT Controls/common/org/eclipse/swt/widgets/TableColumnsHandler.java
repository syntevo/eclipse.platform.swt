package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

final class TableColumnsHandler {

	private final Table table;

	private Rectangle cachedHeaderBounds;
	private int columnResizePossible = -1;
	private int columnResizeActive = -1;

	TableColumnsHandler(Table table) {
		this.table = table;
	}

	public Point getSize() {
		if (cachedHeaderBounds == null) {
			calculateBounds();
		}

		return new Point(cachedHeaderBounds.width, cachedHeaderBounds.height);
	}

	public Rectangle getHeaderBounds() {
		if (cachedHeaderBounds == null) {
			calculateBounds();
		}

		return new Rectangle(cachedHeaderBounds.x, cachedHeaderBounds.y, cachedHeaderBounds.width, cachedHeaderBounds.height);
	}

	private void calculateBounds() {
		int horizontalShift = 0;
		final ScrollBar horizontalBar = table.getHorizontalBar();
		if (horizontalBar != null) {
			horizontalShift = horizontalBar.getSelection();
		}

		int width = 0;
		int headerHeight = 1;
		for (TableColumn c : table.getColumns()) {
			width += c.getWidth();
			headerHeight = Math.max(c.getHeight(), headerHeight);
		}

		this.cachedHeaderBounds = new Rectangle(-horizontalShift, 0, width, headerHeight);
	}

	public void handleMouseMove(Event event) {
		if (cachedHeaderBounds == null) return;

		if (this.columnResizeActive >= 0) {
			TableColumn c = table.getColumn(this.columnResizeActive);
			int x = c.getX();
			c.setWidth(event.x - x);
			table.redraw();
			return;
		}

		if (isInHeader(event.y, cachedHeaderBounds)) {
			this.columnResizePossible = mouseIsOnColumnSide(event.x);
		}

		if (columnResizePossible >= 0) {
			table.setCursor(table.getDisplay().getSystemCursor(SWT.CURSOR_SIZEWE));
		}
		else {
			table.setCursor(null);
			if (!cachedHeaderBounds.contains(event.x, event.y)
					&& table.mouseHoverElement instanceof TableColumn c) {
				table.mouseHoverElement = null;
				table.redrawColumnHeader(c);
			}

			// TODO highlight columns if mouse over...
		}
	}

	private int mouseIsOnColumnSide(int x) {
		final TableColumn[] columns = table.getColumns();
		for (TableColumn c : columns) {
			final int columnX = c.getX();
			final int columnWidth = c.getWidth();
			if (Math.abs(columnX + columnWidth - x) < 5) {
				return table.indexOf(c);
			}
		}
		return -1;
	}

	public boolean isColumnResizeActive() {
		return columnResizeActive >= 0;
	}

	public boolean handleMouseDown(Event event) {
		if (!table.getHeaderVisible()) return false;
		if (cachedHeaderBounds == null) return false;
		if (!isInHeader(event.y, cachedHeaderBounds)) return false;
		if (event.button != 1) return false;

		if (event.count > 1) {
			TableColumn column = table.getColumn(this.columnResizePossible);
			column.pack();
			return true;
		}

		this.columnResizeActive = this.columnResizePossible;
		table.setCapture(true);
		return true;
	}

	private static boolean isInHeader(int y, Rectangle headerBounds) {
		return y < headerBounds.y + headerBounds.height;
	}

	public void handleMouseUp(Event e) {
		this.columnResizeActive = -1;
		table.setCapture(false);
	}

	public void clearCache() {
		cachedHeaderBounds = null;
	}
}
