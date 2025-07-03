package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

final class TableColumnsHandler {

	private final Table table;
	private final Point mouseDownLocation = new Point(0, 0);

	private Rectangle cachedHeaderBounds;
	private int columnResizePossible = -1;
	private int columnResizeActive = -1;
	private int mouseOverColumn = -1;

	TableColumnsHandler(Table table) {
		this.table = table;
	}

	public Point getSize() {
		if (cachedHeaderBounds == null) {
			cachedHeaderBounds = calculateBounds();
		}

		return new Point(cachedHeaderBounds.width, cachedHeaderBounds.height);
	}

	public Rectangle getHeaderBounds() {
		if (cachedHeaderBounds == null) {
			cachedHeaderBounds = calculateBounds();
		}

		return new Rectangle(cachedHeaderBounds.x, cachedHeaderBounds.y, cachedHeaderBounds.width, cachedHeaderBounds.height);
	}

	private Rectangle calculateBounds() {
		int horizontalShift = 0;
		final ScrollBar horizontalBar = table.getHorizontalBar();
		if (horizontalBar != null) {
			horizontalShift = horizontalBar.getSelection();
		}

		int width = 0;
		for (TableColumn c : table.getColumns()) {
			width += c.getWidth();
		}

		final int headerHeight = table.calculateColumnHeight();
		return new Rectangle(-horizontalShift, 0, width, headerHeight);
	}

	public void handleMouseMove(Event event) {
		if (cachedHeaderBounds == null) return;

		if (columnResizeActive >= 0) {
			TableColumn c = table.getColumn(columnResizeActive);
			int x = c.getX();
			c.setWidth(event.x - x);
			table.redraw();
			return;
		}

		columnResizePossible = -1;
		mouseOverColumn = -1;
		final boolean isInHeader = table.getHeaderVisible() && isInHeader(event.y, cachedHeaderBounds);
		if (isInHeader) {
			final int x = event.x;
			final TableColumn[] columns = table.getColumns();
			for (TableColumn column : columns) {
				final int columnX = column.getX();
				final int columnWidth = column.getWidth();
				if (Math.abs(columnX + columnWidth - x) < 5) {
					if (column.getResizable()) {
						columnResizePossible = table.indexOf(column);
					}
					break;
				}

				if (columnX <= x && x < columnX + columnWidth) {
					mouseOverColumn = table.indexOf(column);
					table.mouseHoverElement = column;
				}
			}
		}

		if (columnResizePossible >= 0) {
			table.setCursor(table.getDisplay().getSystemCursor(SWT.CURSOR_SIZEWE));
		} else {
			table.setCursor(null);
			if (!isInHeader
					&& table.mouseHoverElement instanceof TableColumn c) {
				table.mouseHoverElement = null;
				table.redrawColumnHeader(c);
			}
		}
	}

	public boolean handleMouseDown(Event event) {
		if (!table.getHeaderVisible()) return false;
		if (cachedHeaderBounds == null) return false;
		if (!isInHeader(event.y, cachedHeaderBounds)) return false;
		if (event.button != 1) return false;

		if (event.type == SWT.MouseDoubleClick) {
			if (columnResizePossible >= 0) {
				TableColumn column = table.getColumn(columnResizePossible);
				column.pack();
				event.type = 0;
			}
			return true;
		}

		if (event.count > 1) {
			return true;
		}

		this.columnResizeActive = columnResizePossible;
		mouseDownLocation.x = event.x;
		mouseDownLocation.y = event.y;
		table.setCapture(true);
		return true;
	}

	public void handleMouseUp(Event event) {
		if (event.count > 1) {
			return;
		}

		if (columnResizeActive >= 0) {
			columnResizeActive = -1;
		}
		if (mouseOverColumn >= 0) {
			TableColumn column = table.getColumn(mouseOverColumn);
			column.sendEvent(SWT.Selection);
		}
		table.setCapture(false);
	}

	public void clearCache() {
		cachedHeaderBounds = null;
	}

	private static boolean isInHeader(int y, Rectangle headerBounds) {
		return y < headerBounds.y + headerBounds.height;
	}
}
