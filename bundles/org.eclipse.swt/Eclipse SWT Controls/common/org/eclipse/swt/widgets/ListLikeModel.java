package org.eclipse.swt.widgets;

import java.util.*;

import org.eclipse.swt.*;

/**
 * This class is internal API.
 * It is public only for being able to unit-test it.
 */
public final class ListLikeModel {

	private final Set<Integer> selection = new TreeSet<>();
	private final boolean singleSelection;

	private int topIndex;
	private int current = -1;
	private int anchor = -1;
	private int count;

	public ListLikeModel(boolean singleSelection) {
		this.singleSelection = singleSelection;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		if (count == this.count) {
			return;
		}

		if (count < this.count) {
			selection.removeIf(i -> i >= count);
		}
		this.count = count;
	}

	public void add(int index) {
		if (index < 0 || index > count) SWT.error(SWT.ERROR_INVALID_ARGUMENT);

		count++;

		if (index < current) {
			current++;
		}
		if (index < anchor) {
			anchor++;
		}
	}

	public void remove(int index) {
		if (count == 0) SWT.error(SWT.ERROR_UNSPECIFIED);
		checkIndex(index);

		if (index < current) {
			current--;
		}
		if (index < anchor) {
			anchor--;
		}
		// todo update selection
		count--;
		if (count == 0) {
			current = -1;
			anchor = -1;
		}
	}

	public int getCurrent() {
		return current;
	}

	public int getAnchor() {
		return anchor;
	}

	public int getTopIndex() {
		return topIndex;
	}

	public void setTopIndex(int topIndex) {
		if (topIndex < 0 || topIndex > count) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		this.topIndex = topIndex;
	}

	public int selectionCount() {
		return selection.size();
	}

	public boolean isSelected(int index) {
		if (isOutOfBounds(index)) {
			return false;
		}

		return selection.contains(index);
	}

	public void clearSelection() {
		selection.clear();
	}

	public void setSelection(int i) {
		checkIndex(i);
		selection.clear();
		selection.add(i);
		current = i;
		anchor = i;
	}

	public void toggleSelection(int i) {
		checkIndex(i);
		final Integer intObj = i;
		if (selection.contains(intObj)) {
			selection.remove(intObj);
		} else {
			selection.add(intObj);
		}
		current = i;
	}

	public boolean select(int[] indices) {
		if (indices == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);

		if (singleSelection) {
			if (indices.length > 1) {
				return false;
			}
			selection.clear();
		}

		boolean changed = false;
		for (int index : indices) {
			if (isOutOfBounds(index)) {
				continue;
			}

			if (selection.add(index)) {
				changed = true;
			}
		}
		return changed;
	}

	public boolean deselect(int[] indices) {
		if (indices == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);

		boolean changed = false;
		for (int index : indices) {
			if (selection.remove(index)) {
				changed = true;
			}
		}
		return changed;
	}

	public int getSelectionIndex() {
		if (selection.isEmpty()) {
			return -1;
		}
		return selection.iterator().next();
	}

	public int[] getSelectionIndices() {
		final int[] indices = new int[selection.size()];
		int i = 0;
		for (int index : selection) {
			indices[i++] = index;
		}
		return indices;
	}

	public void selectRangeTo(int index) {
		checkIndex(index);

		clearSelection();

		final int from = Math.min(anchor, index);
		final int to = Math.max(anchor, index);

		for (int i = from; i <= to; i++) {
			selection.add(i);
		}

		current = index;
		// don't change anchor
	}

	public void moveSelectionAbsolute(int index, boolean shiftPressed, boolean ctrlOrCmdPressed) {
		checkIndex(index);

		if (count == 0) {
			return;
		}

		if (ctrlOrCmdPressed) {
			current = index;
		} else if (shiftPressed) {
			selectRangeTo(index);
		} else {
			setSelection(index);
		}
	}

	public void moveSelectionRelative(int direction, boolean shiftPressed, boolean ctrlOrCmdPressed) {
		if (count == 0 || direction == 0) {
			return;
		}

		final int target;
		if (current < 0) {
			target = topIndex;
		} else {
			target = direction < 0
					? Math.max(current + direction, 0)
					: Math.min(current + direction, count - 1);
		}
		moveSelectionAbsolute(target, shiftPressed, ctrlOrCmdPressed);
	}

	public void setCurrent(int current) {
		if (current < 0) {
			this.current = current;
		}

		if (current >= count) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		this.current = current;
	}

	private boolean isOutOfBounds(int index) {
		return index < 0 || index >= count;
	}

	private void checkIndex(int i) {
		if (isOutOfBounds(i)) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
	}
}
