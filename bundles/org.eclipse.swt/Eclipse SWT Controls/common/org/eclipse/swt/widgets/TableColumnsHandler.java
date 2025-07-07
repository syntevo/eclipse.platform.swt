package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

final class TableColumnsHandler {

	private final Table table;
	private final Point mouseDownLocation = new Point(0, 0);

	private Point cachedHeaderSize;
	private int columnResizePossible = -1;
	private int columnResizeActive = -1;
	private int mouseOverColumn = -1;

	TableColumnsHandler(Table table) {
		this.table = table;
	}

	public Point getSize() {
		if (cachedHeaderSize == null) {
			cachedHeaderSize = calculateSize();
		}

		return new Point(cachedHeaderSize.x, cachedHeaderSize.y);
	}

	private Point calculateSize() {
		int width = 0;
		for (TableColumn c : table.getColumns()) {
			width += c.getWidth();
		}

		final int height = table.calculateColumnHeight();
		return new Point(width, height);
	}

	public void handleMouseMove(Event event) {
		if (cachedHeaderSize == null) return;

		if (columnResizeActive >= 0) {
			TableColumn c = table.getColumn(columnResizeActive);
			int x = c.getXScrolled();
			c.setWidth(event.x - x);
			table.redraw();
			return;
		}

		columnResizePossible = -1;
		mouseOverColumn = -1;
		final boolean isInHeader = table.getHeaderVisible() && event.y < cachedHeaderSize.y;
		if (isInHeader) {
			final int x = event.x;
			final TableColumn[] columns = table.getColumns();
			for (TableColumn column : columns) {
				final int columnX = column.getXScrolled();
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
		if (cachedHeaderSize == null) return false;
		if (event.y >= cachedHeaderSize.y) return false;
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
		cachedHeaderSize = null;
	}
}
