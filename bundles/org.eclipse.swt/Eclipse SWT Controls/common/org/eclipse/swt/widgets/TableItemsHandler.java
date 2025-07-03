package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

class TableItemsHandler {

	private static final int ITEMS_OVERLAY = 5;

	private final Table table;
	private int lastVisibleElementIndex;

	public TableItemsHandler(Table table) {
		this.table = table;
	}

	public void paint(GC gc) {
		Rectangle itemsArea = getItemsClientArea();

		this.lastVisibleElementIndex = -1;

		for (int i = table.getTopIndex(); i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);

			if (table.isVirtual()) {
				table.checkData(item, i, false);
			}

			item.doPaint(gc, i);

			final Rectangle bounds = item.getFullBounds();
			if (bounds.y + bounds.height > itemsArea.y + itemsArea.height) {
				this.lastVisibleElementIndex = i;
				break;
			}
		}

		if (this.lastVisibleElementIndex == -1) {
			this.lastVisibleElementIndex = table.getItemCount() - 1;
		}
	}

	public Rectangle getItemsClientArea() {
		Rectangle ca = table.getClientArea();
		final int headerHeight = table.getHeaderHeight();
		return new Rectangle(0, headerHeight + 1, ca.width, ca.height - headerHeight);
	}

	public void handleMouseMove(Event event) {
		Rectangle ica = getItemsClientArea();
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
			for (int i = topIndex; i < Math.min(this.lastVisibleElementIndex + ITEMS_OVERLAY,
					table.getItemCount()); i++) {
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

	public int getLastVisibleElementIndex() {
		return this.lastVisibleElementIndex;
	}

	public void handleDoubleClick(Event event) {
		Rectangle ica = getItemsClientArea();
		if (ica.width == 0 || ica.height == 0 || !table.isVisible()) return;

		Point p = new Point(event.x, event.y);
		if (!ica.contains(p)) {
			return;
		}

		final int max = Math.min(lastVisibleElementIndex + ITEMS_OVERLAY, table.getItemCount());
		for (int i = table.getTopIndex(); i < max; i++) {
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
