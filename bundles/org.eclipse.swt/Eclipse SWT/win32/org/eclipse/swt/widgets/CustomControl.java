package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.win32.*;

public abstract class CustomControl extends Control implements ICustomWidget {

	private boolean visible = true;
	private boolean enabled = true;
	private int x;
	private int y;
	private int width;
	private int height;

	protected CustomControl(Composite parent, int style) {
		super(parent, style);
		parent.addCustomChild(this);
	}

	@Override
	void reskinWidget() {
	}

	@Override
	void createHandle() {
	}

	@Override
	void register() {
	}

	@Override
	void subclass() {
	}

	@Override
	void setDefaultFont() {
	}

	@Override
	void checkGesture() {
	}

	@Override
	public boolean getEnabled() {
		checkWidget();
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		checkWidget();
		if (enabled == this.enabled) {
			return;
		}
		this.enabled = enabled;
		redraw();
	}

	@Override
	public Point getSize() {
		return new Point(width, height);
	}

	@Override
	Point getSizeInPixels() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Point getLocation() {
		checkWidget();
		return new Point(x, y);
	}

	@Override
	Point getLocationInPixels() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Rectangle getBounds() {
		checkWidget();
		return new Rectangle(x, y, width, height);
	}

	@Override
	public void setBounds(Rectangle rect) {
		setBounds(rect.x, rect.y, rect.width, rect.height);
	}

	@Override
	Rectangle getBoundsInPixels() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		checkWidget();
		if (x == this.x
		    && y == this.y
		    && width == this.width
		    && height == this.height) {
			return;
		}

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		redraw();
	}

	@Override
	public void redraw() {
		if (!isVisible()) {
			return;
		}

		final Rectangle bounds = getBounds();
		if (bounds.width < 1 || bounds.height < 1) {
			return;
		}
		parent.redraw(bounds.x, bounds.y, bounds.width, bounds.height, false);
	}

	@Override
	void redrawInPixels(RECT rect, boolean all) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isVisible() {
		checkWidget();
		return getVisible() && parent.isVisible();
	}

	@Override
	public boolean getVisible() {
		return visible;
	}

	@Override
	boolean isShowing() {
		if (!isVisible()) {
			return false;
		}
		Control control = this;
		while (control != null) {
			Point size = control.getSize();
			if (size.x == 0 || size.y == 0) {
				return false;
			}
			control = control.parent;
		}
		return true;
	}
}
