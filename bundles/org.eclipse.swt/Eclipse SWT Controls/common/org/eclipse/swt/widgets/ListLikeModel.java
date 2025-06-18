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
	private int current;
	private int count;

	public ListLikeModel(boolean singleSelection) {
		this.singleSelection = singleSelection;
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

	public void setSelection(int i) {
		checkIndex(i);
		selection.clear();
		selection.add(i);
	}

	public void toggleSelection(int i) {
		checkIndex(i);
		final Integer intObj = i;
		if (selection.contains(intObj)) {
			selection.remove(intObj);
		} else {
			selection.add(intObj);
		}
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

	public void clearSelection() {
		selection.clear();
	}

	public int selectionCount() {
		return selection.size();
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

	public boolean isSelected(int index) {
		if (isOutOfBounds(index)) {
			return false;
		}

		return selection.contains(index);
	}

	public void remove(int index) {
		if (count == 0) SWT.error(SWT.ERROR_UNSPECIFIED);
		if (index < current) {
			current--;
		}
		count--;
	}

	public int getTopIndex() {
		return topIndex;
	}

	public void setTopIndex(int topIndex) {
		checkIndex(topIndex);
		this.topIndex = topIndex;
	}

	private boolean isOutOfBounds(int index) {
		return index < 0 || index >= count;
	}

	private void checkIndex(int i) {
		if (isOutOfBounds(i)) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
	}
}
