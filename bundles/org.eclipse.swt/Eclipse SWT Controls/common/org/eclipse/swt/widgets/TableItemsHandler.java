package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

class TableItemsHandler {

	private final Table table;
	private Point computedSize;
	private int lastVisibleElementIndex;
	private int itemsCountAtCalculation;

	final static int ITEMS_OVERLAY = 5;

	public TableItemsHandler(Table table) {
		this.table = table;
	}

	static int getItemsHeight(TableItem it) {
		return it.getSize().y + it.getParent().getGridSize();
	}

	public void paint(GC gc) {
		Rectangle itemsArea = getItemsClientArea();

		this.lastVisibleElementIndex = -1;

		final Color background = table.getBackground();

		for (int i = table.getTopIndex(); i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);

			if (table.isVirtual()) {
				table.checkData(item, i, false);
			}

			gc.setBackground(background);
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

	public Point getSize() {
		final int itemCount = table.getItemCount();
		if (computedSize == null || itemsCountAtCalculation != itemCount) {
			itemsCountAtCalculation = itemCount;
			computedSize = calculateSize();
		}

		return computedSize;
	}

	private Point calculateSize() {
		if (table.isVirtual()) {
			int gridLineSize = table.getGridSize();
			int heightPerLine = TableItemRenderer.guessItemHeight(table) + gridLineSize;

			Rectangle ca = table.getClientArea();
			return new Point(ca.width, table.getItemCount() * heightPerLine);
		}

		final int gridLineSize = table.getGridSize();
		int heightPerLine = TableItemRenderer.guessItemHeight(table) + gridLineSize;

		TableItem[] items = table.getItems();
		int width = 0;
		if (table.columnsExist()) {
			final int headerWidth = table.getHeaderBounds().width;
			width = headerWidth;
		} else {
			for (int i = 0; i < items.length; i++) {
				TableItem item = items[i];
				if (i == 0) {
					heightPerLine = getItemsHeight(item);
				}

				width = Math.max(width, item.getFullBounds().width);
			}
		}

		return new Point(width, heightPerLine * items.length);
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

		if (mouseHoverElement instanceof TableItem item) {
			if (item.getBounds().contains(p)) {
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
				if (item.getBounds().contains(p)) {
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

	public void clearCache() {
		computedSize = null;
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
