package org.eclipse.swt.widgets;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public abstract class TableRenderer {

	public abstract Point computeHeaderSize(TableColumn column, GC gc);

	public abstract int calculateColumnHeight();

	public abstract void paint(GC gc);

	public abstract int guessItemHeight();

	public abstract boolean drawCell(TableItem item, int columnIndex, int detail, Rectangle bounds, int x, GC gc);

	public abstract Point computeSize(TableItem item, GC gc);

	protected final Table table;

	protected TableRenderer(Table table) {
		this.table = table;
	}

	public abstract Point computeCellSize(TableItem item, int colIndex, GC gc, Rectangle imageBounds, Rectangle textBounds);
}
