package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

class TableItemsHandler {

	private final Table table;

	public TableItemsHandler(Table table) {
		this.table = table;
	}

	public void paint(GC gc, int maxY) {
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

	public void handleMouseMove(Event event) {
		Rectangle ica = table.getItemsClientArea();
		if (ica.width == 0 || ica.height == 0 || !table.isVisible()) return;

		Item mouseHoverElement = table.mouseHoverElement;
		if (!ica.contains(event.x, event.y)) {
			if (mouseHoverElement instanceof TableItem ti) {
				table.mouseHoverElement = null;
				ti.redraw();
			}

			return;
		}

		Point p = new Point(event.x, event.y);

		final int itemHeight = table.getItemHeight();

		if (mouseHoverElement instanceof TableItem item) {
			final Rectangle bounds = item.getFullBounds();
			// todo
			bounds.height = itemHeight;
			if (bounds.contains(p)) {
				return;
			}

			table.mouseHoverElement = null;
			item.redraw();
		}

		if ((event.stateMask & SWT.BUTTON_MASK) != 0) {
			return;
		}

		TableItem[] items = table.getItems();
		int topIndex = table.getTopIndex();
		if (items != null) {
			for (int i = topIndex, max = table.getLastVisibleIndex(); i <= max; i++) {
				TableItem item = table.getItem(i);
				final Rectangle bounds = item.getFullBounds();
				bounds.height = itemHeight;
				if (bounds.contains(p)) {
					table.mouseHoverElement = item;
					item.redraw();
					return;
				}
			}
		}
	}

	public void handleDoubleClick(Event event) {
		Rectangle ica = table.getItemsClientArea();
		if (ica.width == 0 || ica.height == 0 || !table.isVisible()) return;

		Point p = new Point(event.x, event.y);
		if (!ica.contains(p)) {
			return;
		}

		for (int i = table.getTopIndex(), max = table.getLastVisibleIndex(); i <= max; i++) {
			TableItem it = table.getItem(i);
			if (it.getBounds().contains(p)) {
				Event e = new Event();
				e.item = it;
				e.type = SWT.DefaultSelection;
				e.count = event.count;
				e.button = event.button;
				e.doit = event.doit;
				e.stateMask = event.stateMask;
				e.time = event.time;
				e.x = event.x;
				e.y = event.y;

				table.postEvent(SWT.DefaultSelection, e);
				return;
			}
		}
	}
}
