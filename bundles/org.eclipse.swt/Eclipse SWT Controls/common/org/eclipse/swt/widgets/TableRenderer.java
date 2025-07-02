package org.eclipse.swt.widgets;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public abstract class TableRenderer {

	public abstract Point computeHeaderSize(TableColumn column, GC gc);

	public abstract int calculateColumnHeight();

	public abstract void paint(GC gc);

	protected final Table table;

	protected TableRenderer(Table table) {
		this.table = table;
	}
}
