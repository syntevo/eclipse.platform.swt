package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

final class TableColumnsHandler {

	private final Table table;

	private Rectangle columnsArea;
	private Point computedSize;
	private int columnResizePossible = -1;
	private int columnResizeActive = -1;

	TableColumnsHandler(Table table) {
		this.table = table;
	}

	private void calculateColumnsPositions() {
		boolean drawColumns = table.columnsExist();

		int tableColumnsHeight = 0;
		int width = 0;
		this.columnsArea = new Rectangle(0, 0, 0, 0);

		int horizontalShift = 0;
		if (table.getHorizontalBar() != null) {
			horizontalShift = table.getHorizontalBar().getSelection();
		}

		if (drawColumns) {
			for (TableColumn c : table.getColumns()) {
				width += c.getWidth();
				tableColumnsHeight = Math.max(c.getHeight(), tableColumnsHeight);
			}

			this.columnsArea = new Rectangle(-horizontalShift, 0, width, tableColumnsHeight);
		}

		this.computedSize = new Point(width, tableColumnsHeight);

		if (table.getHeaderVisible()) {
			this.computedSize.y = Math.max(1, this.computedSize.y);
		} else {
			this.columnsArea.height = 0;
			this.computedSize.y = 0;
		}
	}

	public void paint(GC gc) {
		if (!table.getHeaderVisible()) return;

		Rectangle ca = table.getClientArea();
		gc.fillRectangle(ca);

		for (TableColumn c : table.getColumns()) {
			if (!c.getBounds().intersects(ca)) {
				continue;
			}

			c.paint(gc);
		}
	}

	public Point getSize() {
		if (this.computedSize == null || !Table.USE_CACHES) {
			calculateColumnsPositions();
		}

		return this.computedSize;
	}

	public Rectangle getColumnsBounds() {
		if (columnsArea == null || !Table.USE_CACHES) {
			calculateColumnsPositions();
		}

		return columnsArea;
	}

	public void handleMouseMove(Event event) {
		if (columnsArea == null) return;

		if (this.columnResizeActive != -1) {
			TableColumn c = table.getColumn(this.columnResizeActive);
			int x = c.getBounds().x;
			c.setWidth(event.x - x);
			table.redraw();
			return;
		}

		this.columnResizePossible = mouseIsOnColumnSide(event.x, event.y);
		if (columnResizePossible >= 0) {
			table.setCursor(table.getDisplay().getSystemCursor(SWT.CURSOR_SIZEWE));
		}
		else {
			table.setCursor(null);
			if (!columnsArea.contains(event.x, event.y)) {
				if (table.mouseHoverElement instanceof TableColumn ti) {
					table.mouseHoverElement = null;
					ti.redraw();
				}
			}

			// TODO highlight columns if mouse over...
		}
	}

	private int mouseIsOnColumnSide(int x, int y) {
		if (!isInHeader(y)) {
			return -1;
		}

		final TableColumn[] columns = table.getColumns();
		if (columns == null) {
			return -1;
		}

		for (TableColumn c : columns) {
			final Rectangle bounds = c.getBounds();
			if (Math.abs(bounds.x + bounds.width - x) < 5) {
				return table.indexOf(c);
			}
		}
		return -1;
	}

	public boolean isColumnResizeActive() {
		return columnResizeActive >= 0;
	}

	public boolean handleMouseDown(Event event) {
		if (columnsArea == null) return false;
		if (event.button != 1) return false;
		if (!isInHeader(event.y)) return false;

		if (event.count > 1) {
			TableColumn column = table.getColumn(this.columnResizePossible);
			column.pack();
			return true;
		}

		this.columnResizeActive = this.columnResizePossible;
		table.setCapture(true);
		return true;
	}

	private boolean isInHeader(int y) {
		return y < columnsArea.y + columnsArea.height;
	}

	public void handleMouseUp(Event e) {
		this.columnResizeActive = -1;
		table.setCapture(false);
	}

	public void clearCache() {
		columnsArea = null;
		computedSize = null;
	}
}
