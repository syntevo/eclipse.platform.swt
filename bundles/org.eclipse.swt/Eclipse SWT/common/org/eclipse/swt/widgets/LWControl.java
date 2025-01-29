package org.eclipse.swt.widgets;

import java.util.*;
import java.util.List;

import org.eclipse.swt.graphics.*;

/**
 * @author Thomas Singer
 */
public abstract class LWControl {

	public abstract void paint(IGraphicsContext gc);

	private final List<Runnable> listeners = new ArrayList<>();

	private int width;
	private int height;

	protected LWControl() {
	}

	public void setSize(int width, int height) {
		if (width == this.width && height == this.height) {
			return;
		}

		this.width = width;
		this.height = height;
		notifyListeners();
	}

	public void addListener(Runnable listener) {
		Objects.requireNonNull(listener);

		listeners.add(listener);
	}

	protected final void notifyListeners() {
		for (Runnable listener : new ArrayList<>(listeners)) {
			listener.run();
		}
	}
}
