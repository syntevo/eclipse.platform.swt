package org.eclipse.swt.widgets.renderers;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

/**
 * @author Thomas Singer
 */
public class Renderer {

	/**
	 * If a container's width or height is set to this (= default),
	 * it will match the child's preferred size.
	 */
	public static final int FIT = 0;

	/**
	 * If a child's with or height is set to GROW,
	 * it can take parent's remaining space for this direction.
	 */
	public static final int GROW = -1;

	protected int x;
	protected int y;
	protected int setWidth;
	protected int setHeight;
	protected int width;
	protected int height;
	protected Color background;

	protected Renderer() {
	}

	protected Renderer(RendererContainer parent) {
		parent.add(this);
	}

	protected void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setSize(int width, int height) {
		this.setWidth = width;
		this.setHeight = height;
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}

	public void paint(GC gc) {
		if (width < 1 || height < 1) {
			return;
		}
		if (background != null) {
			gc.setBackground(background);
			gc.fillRectangle(x, y, width, height);
		}
	}

	public void layout() {
	}

	protected void calculateSize() {
		width = Math.max(0, setWidth);
		height = Math.max(0, setHeight);
	}

	protected void calculatePositions() {
	}
}
