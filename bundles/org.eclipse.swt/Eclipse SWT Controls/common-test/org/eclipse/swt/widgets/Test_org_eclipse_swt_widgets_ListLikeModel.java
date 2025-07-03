package org.eclipse.swt.tests.junit;

import org.eclipse.swt.SWTError;
import org.eclipse.swt.widgets.ListLikeModel;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Singer
 */
public class Test_org_eclipse_swt_widgets_ListLikeModel {

	@Test
	public void test_selection() {
		final ListLikeModel model = new ListLikeModel(false);
		Assert.assertEquals(0, model.getTopIndex());
		Assert.assertEquals(0, model.selectionCount());
		Assert.assertEquals(-1, model.getSelectionIndex());
		Assert.assertArrayEquals(new int[0], model.getSelectionIndices());
		Assert.assertFalse(model.isSelected(-1));
		Assert.assertFalse(model.isSelected(0));
		Assert.assertFalse(model.select(new int[] {0}));
		Assert.assertFalse(model.deselect(new int[]{0}));
		assertException(() -> model.setTopIndex(-1));
		assertException(() -> model.setTopIndex(0));
		assertException(() -> model.setSelection(0));
		assertException(() -> model.toggleSelection(0));
		assertException(() -> model.remove(0));

		model.setCount(10);
		assertException(() -> model.select(null));
		assertException(() -> model.deselect(null));

		Assert.assertEquals(0, model.selectionCount());
		Assert.assertEquals(-1, model.getSelectionIndex());
		Assert.assertEquals(0, model.getTopIndex());
		Assert.assertArrayEquals(new int[0], model.getSelectionIndices());
		Assert.assertFalse(model.isSelected(-1));
		Assert.assertFalse(model.isSelected(0));
		assertException(() -> model.setTopIndex(-1));
		model.setTopIndex(0);

		model.setSelection(0);
		Assert.assertTrue(model.isSelected(0));
		Assert.assertEquals(0, model.getSelectionIndex());
		Assert.assertArrayEquals(new int[] {0}, model.getSelectionIndices());

		model.toggleSelection(0);
		Assert.assertFalse(model.isSelected(0));
		Assert.assertEquals(-1, model.getSelectionIndex());
		Assert.assertArrayEquals(new int[] {}, model.getSelectionIndices());

		model.toggleSelection(0);
		Assert.assertTrue(model.isSelected(0));
		Assert.assertEquals(0, model.getSelectionIndex());
		Assert.assertArrayEquals(new int[] {0}, model.getSelectionIndices());

		model.select(new int[]{1, 3});
		Assert.assertEquals(0, model.getSelectionIndex());
		Assert.assertArrayEquals(new int[] {0, 1, 3}, model.getSelectionIndices());

		model.deselect(new int[]{0, 1});
		Assert.assertEquals(3, model.getSelectionIndex());
		Assert.assertArrayEquals(new int[] {3}, model.getSelectionIndices());
	}

	private void assertException(Runnable test) {
		try {
			test.run();
			Assert.fail();
		} catch (IllegalArgumentException | SWTError ignored) {
		}
	}
}
