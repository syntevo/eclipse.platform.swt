package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

/**
 * @author Thomas Singer
 */
public class LWRadioButton extends LWControl {

	private static final int BOX_SIZE = 12;

	private boolean selected;
	private boolean hovered;

	public LWRadioButton() {
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		if (selected == this.selected) {
			return;
		}

		this.selected = selected;
		notifyListeners();
	}

	public boolean isHovered() {
		return hovered;
	}

	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}

	@Override
	public void paint(IGraphicsContext gc) {
		if (isSelected()) {
			gc.setBackground(SELECTION_COLOR);
			int partialBoxBorder = 2;
			gc.fillOval(x + partialBoxBorder, y + partialBoxBorder,
			            BOX_SIZE - 2 * partialBoxBorder + 1, BOX_SIZE - 2 * partialBoxBorder + 1);
		}
		if (hasMouseEntered) {
			gc.setBackground(HOVER_COLOR);
			int partialBoxBorder = getSelection() ? 4 : 0;
			gc.fillOval(x + partialBoxBorder, y + partialBoxBorder,
			            BOX_SIZE - 2 * partialBoxBorder + 1, BOX_SIZE - 2 * partialBoxBorder + 1);
		}
		if (!isEnabled()) {
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
		}
		gc.drawOval(x, y, BOX_SIZE, BOX_SIZE);

	}
}
