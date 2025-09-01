/*******************************************************************************
 * Copyright (c) 2007, 2012 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.dnd;

import java.util.*;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

/**
 * This class provides a default drag under effect (eg. select, insert and scroll)
 * when a drag occurs over a <code>Table</code>.
 *
 * <p>Classes that wish to provide their own drag under effect for a <code>Table</code>
 * can extend the <code>TableDropTargetEffect</code> and override any applicable methods
 * in <code>TableDropTargetEffect</code> to display their own drag under effect.</p>
 *
 * Subclasses that override any methods of this class must call the corresponding
 * <code>super</code> method to get the default drag under effect implementation.
 *
 * <p>The feedback value is either one of the FEEDBACK constants defined in
 * class <code>DND</code> which is applicable to instances of this class,
 * or it must be built by <em>bitwise OR</em>'ing together
 * (that is, using the <code>int</code> "|" operator) two or more
 * of those <code>DND</code> effect constants.
 * </p>
 * <dl>
 * <dt><b>Feedback:</b></dt>
 * <dd>FEEDBACK_SELECT, FEEDBACK_SCROLL</dd>
 * </dl>
 *
 * @see DropTargetAdapter
 * @see DropTargetEvent
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 *
 * @since 3.3
 */
public class TableDropTargetEffect extends DropTargetEffect {

	private static final int INITIAL_DELAY = 500;
	private static final int REPEAT_DELAY = 30;

	private long scrollBeginTime;
	private Point lastScrollPosition;

	/**
	 * Creates a new <code>TableDropTargetEffect</code> to handle the drag under effect on the specified
	 * <code>Table</code>.
	 *
	 * @param table the <code>Table</code> over which the user positions the cursor to drop the data
	 */
	public TableDropTargetEffect(Table table) {
		super(table);
	}

	int checkEffect(int effect) {
		// Some effects are mutually exclusive.  Make sure that only one of the mutually exclusive effects has been specified.
		if ((effect & DND.FEEDBACK_SELECT) != 0) effect = effect & ~DND.FEEDBACK_INSERT_AFTER & ~DND.FEEDBACK_INSERT_BEFORE;
		if ((effect & DND.FEEDBACK_INSERT_BEFORE) != 0) effect = effect & ~DND.FEEDBACK_INSERT_AFTER;
		return effect;
	}

	/**
	 * This implementation of <code>dragEnter</code> provides a default drag under effect
	 * for the feedback specified in <code>event.feedback</code>.
	 *
	 * For additional information see <code>DropTargetAdapter.dragEnter</code>.
	 *
	 * Subclasses that override this method should call <code>super.dragEnter(event)</code>
	 * to get the default drag under effect implementation.
	 *
	 * @param event  the information associated with the drag enter event
	 *
	 * @see DropTargetAdapter
	 * @see DropTargetEvent
	 */
	@Override
	public void dragEnter(DropTargetEvent event) {
		Table table = (Table) control;
		table.setDropInsertBefore(-1);
		restartScrolling();
	}

	/**
	 * This implementation of <code>dragLeave</code> provides a default drag under effect
	 * for the feedback specified in <code>event.feedback</code>.
	 *
	 * For additional information see <code>DropTargetAdapter.dragLeave</code>.
	 *
	 * Subclasses that override this method should call <code>super.dragLeave(event)</code>
	 * to get the default drag under effect implementation.
	 *
	 * @param event  the information associated with the drag leave event
	 *
	 * @see DropTargetAdapter
	 * @see DropTargetEvent
	 */
	@Override
	public void dragLeave(DropTargetEvent event) {
		Table table = (Table) control;
		table.setDropInsertBefore(-1);
		restartScrolling();
	}

	/**
	 * This implementation of <code>dragOver</code> provides a default drag under effect
	 * for the feedback specified in <code>event.feedback</code>. The class description
	 * lists the FEEDBACK constants that are applicable to the class.
	 *
	 * For additional information see <code>DropTargetAdapter.dragOver</code>.
	 *
	 * Subclasses that override this method should call <code>super.dragOver(event)</code>
	 * to get the default drag under effect implementation.
	 *
	 * @param event  the information associated with the drag over event
	 *
	 * @see DropTargetAdapter
	 * @see DropTargetEvent
	 * @see DND#FEEDBACK_SELECT
	 * @see DND#FEEDBACK_SCROLL
	 */
	@Override
	public void dragOver(DropTargetEvent event) {
		final Table table = (Table) getControl();
		final int feedback = checkEffect(event.feedback);
		final Point coordinates = table.toControl(event.x, event.y);
		final TableItem item = table.getItem(coordinates);
		final int itemIndex = item != null ? table.indexOf(item) : -1;
		if ((feedback & DND.FEEDBACK_SCROLL) == 0 || item == null || !handleScrolling(table, coordinates, itemIndex)) {
			restartScrolling();
		}

		if (item != null) {
			if ((feedback & DND.FEEDBACK_SELECT) != 0) {
				table.setDropHighlight(item);
				return;
			}

			if ((feedback & (DND.FEEDBACK_INSERT_BEFORE | DND.FEEDBACK_INSERT_AFTER)) != 0) {
				if ((feedback & DND.FEEDBACK_INSERT_AFTER) != 0) {
					table.setDropInsertBefore(itemIndex + 1);
				} else {
					table.setDropInsertBefore(itemIndex);
				}
				return;
			}
		} else if ((feedback & DND.FEEDBACK_INSERT_AFTER) != 0) {
			final int itemCount = table.getItemCount();
			final int y;
			if (itemCount > 0) {
				final TableItem lastItem = table.getItem(itemCount - 1);
				final Rectangle lastItemBounds = lastItem.getBounds();
				y = lastItemBounds.y + lastItemBounds.height;
			} else {
				y = table.getHeaderHeight();
			}

			if (coordinates.y >= y) {
				table.setDropInsertBefore(itemCount);
				return;
			}
		}

		table.setDropInsertBefore(-1);
	}

	private boolean handleScrolling(Table table, Point coordinates, int itemIndex) {
		final long now = System.currentTimeMillis();
		// initial delay not yet configured?
		if (scrollBeginTime == 0) {
			scrollBeginTime = now + INITIAL_DELAY;
			lastScrollPosition = coordinates;
			return true;
		}

		// initial delay not expired?
		if (now < scrollBeginTime) {
			return Objects.equals(lastScrollPosition, coordinates);
		}

		scrollBeginTime = now + REPEAT_DELAY;

		if (itemIndex < 0) {
			return false;
		}

		final int topIndex = table.getTopIndex();
		final int index;
		// upper part of the table?
		if (itemIndex - 1 < topIndex) {
			index = Math.max(0, topIndex - 1);
		} else {
			// lower part
			index = Math.min(table.getItemCount() - 1, itemIndex + 1);
		}

		if (itemIndex == index) {
			return false;
		}

		table.showItem(index);
		if (table.getTopIndex() != topIndex) {
			// avoid drawing glitches by the drag image
			table.redraw();
		}
		return true;
	}

	private void restartScrolling() {
		scrollBeginTime = 0;
		lastScrollPosition = null;
	}
}
