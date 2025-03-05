package org.eclipse.swt.widgets.renderers;

import org.eclipse.swt.graphics.GC;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Singer
 */
public class RendererContainer extends Renderer {

	private final Layout layout;

	private Renderer[] children = new Renderer[4];
	private int childCount;

	private int paddingLeft;
	private int paddingTop;
	private int paddingRight;
	private int paddingBottom;
	private int childGap;

	public RendererContainer(Layout layout) {
		this.layout = layout;
	}

	public RendererContainer(RendererContainer parent, Layout layout) {
		super(parent);
		this.layout = layout;
	}

	public void setPadding(int padding) {
		setPadding(padding, padding);
	}

	public void setPadding(int leftRight, int topBottom) {
		setPadding(leftRight, topBottom, leftRight, topBottom);
	}

	public void setPadding(int left, int top, int right, int bottom) {
		this.paddingLeft = left;
		this.paddingTop = top;
		this.paddingRight = right;
		this.paddingBottom = bottom;
	}

	public int getChildGap() {
		return childGap;
	}

	public void setChildGap(int childGap) {
		this.childGap = childGap;
	}

	public void paint(GC gc) {
		super.paint(gc);
		for (int i = 0; i < childCount; i++) {
			final Renderer child = children[i];
			child.paint(gc);
		}
	}

	public void layout() {
		calculateSize();
		calculatePositions();
	}

	protected void calculateSize() {
		super.calculateSize();

		fitSize();
		growSizeX();
		growSizeY();
	}

	private void fitSize() {
		int alongAxis = 0;
		int acrossAxis = 0;
		for (int i = 0; i < childCount; i++) {
			final Renderer child = children[i];
			child.calculateSize();
			if (layout == Layout.LEFT_TO_RIGHT) {
				alongAxis += child.width;
				acrossAxis = Math.max(acrossAxis, child.height);
			} else {
				alongAxis += child.height;
				acrossAxis = Math.max(acrossAxis, child.width);
			}
		}

		if (setWidth <= 0) {
			width = paddingLeft + paddingRight +
					(layout == Layout.LEFT_TO_RIGHT
							? alongAxis + (childCount - 1) * childGap
							: acrossAxis);
		}

		if (setHeight <= 0) {
			height = paddingTop + paddingBottom +
					(layout == Layout.TOP_TO_BOTTOM
							? alongAxis + (childCount - 1) * childGap
							: acrossAxis);
		}
	}

	private void growSizeX() {
		if (layout == Layout.LEFT_TO_RIGHT) {
			final List<Renderer> growableChildren = new ArrayList<>(childCount);
			int remaining = width - paddingLeft - paddingRight - (childCount - 1) * childGap;
			for (int i = 0; i < childCount; i++) {
				final Renderer child = children[i];
				remaining -= child.width;
				if (isGrow(child.setWidth)) {
					growableChildren.add(child);
				}
			}

			if (growableChildren.isEmpty()) {
				return;
			}

			while (remaining > 0) {
				int smallestSize = 0;
				int secondSmallestSize = Integer.MAX_VALUE;
				int widthToAdd = remaining;
				for (Renderer child : growableChildren) {
					if (child.width < smallestSize) {
						secondSmallestSize = smallestSize;
						smallestSize = child.width;
					} else if (child.width > smallestSize) {
						secondSmallestSize = Math.min(secondSmallestSize, child.width);
						widthToAdd = secondSmallestSize - smallestSize;
					}
				}

				widthToAdd = Math.min(widthToAdd, remaining / growableChildren.size());

				for (Renderer child : growableChildren) {
					if (child.width == smallestSize) {
						child.width += widthToAdd;
						remaining -= widthToAdd;
						if (remaining <= 0) {
							break;
						}
					}
				}
			}
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private void growSizeY() {
		if (layout == Layout.LEFT_TO_RIGHT) {
			final int available = height - paddingTop - paddingBottom;
			for (int i = 0; i < childCount; i++) {
				final Renderer child = children[i];
				if (isGrow(child.setHeight)) {
					child.height = available;
				}
			}
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private boolean isGrow(int coordinate) {
		return coordinate < 0;
	}

	protected void calculatePositions() {
		int alongAxis = layout == Layout.LEFT_TO_RIGHT ? paddingLeft : paddingTop;
		for (int i = 0; i < childCount; i++) {
			final Renderer child = children[i];
			child.calculatePositions();
			if (layout == Layout.LEFT_TO_RIGHT) {
				child.setPosition(alongAxis, paddingTop);
				alongAxis += child.width;
			} else {
				child.setPosition(paddingLeft, alongAxis);
				alongAxis += child.height;
			}
			alongAxis += childGap;
		}
	}

	protected void add(Renderer child) {
		if (childCount == children.length) {
			final Renderer[] newChildren = new Renderer[children.length * 2];
			System.arraycopy(children, 0, newChildren, 0, childCount);
			children = newChildren;
		}
		children[childCount] = child;
		childCount++;
	}

	public enum Layout {
		LEFT_TO_RIGHT, TOP_TO_BOTTOM
	}
}
