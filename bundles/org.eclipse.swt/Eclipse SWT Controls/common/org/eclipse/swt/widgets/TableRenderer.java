package org.eclipse.swt.widgets;

import org.eclipse.swt.graphics.GC;

public abstract class TableRenderer {

	public abstract void paint(GC gc);

	protected final Table table;

	protected TableRenderer(Table table) {
		this.table = table;
	}
}
