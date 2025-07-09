package org.eclipse.swt.widgets;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public abstract class TableRenderer {

	public abstract Point computeHeaderSize(TableColumn column, GC gc);

	public abstract int calculateColumnHeight();

	public abstract void paint(GC gc);

	public abstract int guessItemHeight();

	public abstract int getLeftIndent();

	public abstract Point computeSize(TableItem item, GC gc);

	public abstract Point computeCellSize(TableItem item, int colIndex, GC gc, Rectangle imageBounds, Rectangle textBounds);

	protected final Table table;

	protected TableRenderer(Table table) {
		this.table = table;
	}
}
