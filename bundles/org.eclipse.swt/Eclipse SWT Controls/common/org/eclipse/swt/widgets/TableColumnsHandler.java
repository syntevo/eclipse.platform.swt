package org.eclipse.swt.widgets;

import java.util.*;
import java.util.List;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

final class TableColumnsHandler {

	private static final int CLICK_ALLOWED_DRAG = 3;

	private final Table table;

	private Point mouseDownLocation;
	private Point cachedHeaderSize;
	private int columnResizePossible = -1;
	private int columnResizeActive = -1;
	private TableColumn mouseDownColumn;
	private boolean drag;

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

		if (mouseDownLocation != null
		    && isDragStart(mouseDownLocation.x, mouseDownLocation.y, event.x, event.y)) {
			drag = true;
		}

		if (columnResizeActive >= 0) {
			TableColumn c = table.getColumn(columnResizeActive);
			int x = c.getXScrolled();
			c.setWidth(event.x - x);
			table.redraw();
			return;
		}

		columnResizePossible = -1;
		final boolean isInHeader = table.getHeaderVisible() && event.y < cachedHeaderSize.y;
		if (isInHeader && event.stateMask == 0) {
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

	private boolean isDragStart(int x1, int y1, int x2, int y2) {
		final int dx = x1 - x2;
		final int dy = y1 - y2;
		return dx * dx + dy * dy > CLICK_ALLOWED_DRAG * CLICK_ALLOWED_DRAG;
	}

	public boolean handleMouseDown(Event event) {
		if (!table.getHeaderVisible()) return false;
		if (cachedHeaderSize == null) return false;
		if (event.y >= cachedHeaderSize.y) return false;
		if (event.button != 1) return false;

		mouseDownColumn = null;
		mouseDownLocation = null;
		drag = false;

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

		mouseDownColumn = getColumnAt(event.x);
		if (mouseDownColumn != null) {
			mouseDownLocation = new Point(event.x, event.y);
		}

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
		if (mouseDownColumn != null) {
			if (drag) {
				final TableColumn mouseUpColumn = getColumnAt(event.x);
				if (mouseUpColumn != null) {
					final int left = mouseUpColumn.getXScrolled();
					final int width = mouseUpColumn.getWidth();
					final boolean behindTargetColumn = event.x > left + width / 2;
					table.moveColumn(mouseDownColumn, mouseUpColumn, behindTargetColumn);
				}
			}
			else {
				mouseDownColumn.sendEvent(SWT.Selection);
			}
		}
		table.setCapture(false);
	}

	public void clearCache() {
		cachedHeaderSize = null;
	}

	private TableColumn getColumnAt(int x) {
		final TableColumn[] columns = table.getColumns();
		for (TableColumn column : columns) {
			final int columnX = column.getXScrolled();
			final int columnWidth = column.getWidth();
			if (columnX <= x && x < columnX + columnWidth) {
				return column;
			}
		}
		return null;
	}
}
