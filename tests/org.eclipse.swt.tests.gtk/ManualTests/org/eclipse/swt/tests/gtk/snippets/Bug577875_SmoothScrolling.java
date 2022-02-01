/*******************************************************************************
 * Copyright (c) 2022 Syntevo and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Syntevo - initial API and implementation
 *******************************************************************************/

package org.eclipse.swt.tests.gtk.snippets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.gtk.GDK;
import org.eclipse.swt.internal.gtk.OS;
import org.eclipse.swt.internal.gtk3.GTK3;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public final class Bug577875_SmoothScrolling {
	private static int pos;

	static boolean postSmoothScrollEvent(Control control, double delta_x, double delta_y) {
		long gdkDisplay = GDK.gdk_display_get_default();
		long gdkSeat = GDK.gdk_display_get_default_seat(gdkDisplay);
		final long gdkPointerDevice = GDK.gdk_seat_get_pointer(gdkSeat);
		final long gdkWindow = GTK3.gtk_widget_get_window(control.handle);

		OS.g_object_ref(gdkWindow);

		final int GDK_SCROLL = 31;
		final long eventPtr = GDK.gdk_event_new(GDK_SCROLL);

		// SWT doesn't have a binding for GdkEventScroll, so it has to be
		// constructed in a more complex way. 'GdkEventScroll' is defined as:
		//    offset= 0 len=4 | GdkEventType type;
		//    offset= 8 len=8 | GdkWindow *window;
		//    offset=16 len=1 | gint8 send_event;
		//    offset=20 len=4 | guint32 time;
		//    offset=24 len=8 | gdouble x;
		//    offset=32 len=8 | gdouble y;
		//    offset=40 len=4 | guint state;
		//    offset=44 len=4 | GdkScrollDirection direction;
		//    offset=48 len=8 | GdkDevice *device;
		//    offset=56 len=8 | gdouble x_root;
		//    offset=64 len=8 | gdouble y_root;
		//    offset=72 len=8 | gdouble delta_x;
		//    offset=80 len=8 | gdouble delta_y;
		//    offset=88 len=4 | guint is_stop : 1;

		int struct_size = 96;

		// Populate byte fields
		{
			int field_size = 1;
			byte[] fields = new byte[struct_size / field_size];
			OS.memmove(fields, eventPtr, struct_size);
			{
				// send_event
				fields[16 / field_size] = 1;
			}
			OS.memmove(eventPtr, fields, struct_size);
		}

		// Populate int fields
		{
			int field_size = 4;
			int[] fields = new int[struct_size / field_size];
			OS.memmove(fields, eventPtr, struct_size);
			{
				// type
				fields[ 0 / field_size] = GDK_SCROLL;
				// time
				fields[20 / field_size] = GDK.GDK_CURRENT_TIME;
				// state
				fields[40 / field_size] = 0;
				// direction
				fields[44 / field_size] = GDK.GDK_SCROLL_SMOOTH;
			}
			OS.memmove(eventPtr, fields, struct_size);
		}

		// Populate pointer fields
		{
			int field_size = 8;
			long[] fields = new long[struct_size / field_size];
			OS.memmove(fields, eventPtr, struct_size);
			{
				// window
				fields[ 8 / field_size] = gdkWindow;
				// device
				fields[48 / field_size] = gdkPointerDevice;
			}
			OS.memmove(eventPtr, fields, struct_size);
		}

		// Populate double fields
		{
			int field_size = 8;
			double[] fields = new double[struct_size / field_size];
			OS.memmove(fields, eventPtr, struct_size);
			{
				// x
				fields[24 / field_size] = 0;
				// y
				fields[32 / field_size] = 0;
				// x_root
				fields[56 / field_size] = 0;
				// y_root
				fields[64 / field_size] = 0;
				// delta_x
				fields[72 / field_size] = delta_x;
				// delta_y
				fields[80 / field_size] = -delta_y;
			}
			OS.memmove(eventPtr, fields, struct_size);
		}

		GDK.gdk_event_set_device(eventPtr, gdkPointerDevice);
		GDK.gdk_event_put(eventPtr);
		GDK.gdk_event_free(eventPtr);
		return true;
	}

	public static void main(String[] args) {
		final Display display = new Display();

		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, true));

		new Label(shell, 0).setText(
			"1) Run on Linux\n" +
			"2) Scroll \"table\" below slowly:\n" +
			"   If you have smooth scrolling mouse - use it\n" +
			"   If you have trackpad - drag two fingers slowly\n" +
			"   If you only see +3/-3 on console, your device doesn't\n" +
			"   support smooth scrolling. In this case, use emulation below\n" +
			"3) You can also use emulated smooth trackpad below\n" +
			"   To use it, move mouse over it with a button pressed\n" +
			"   It converts mouse movements into artificial smooth scrolling\n" +
			"3) Bug 577875: Scrolling below speed of 0.33 is ignored\n" +
			"4) Bug 577875: Such scrolling generate useless events with count=0\n" +
			"5) Bug 577875: When actual human scrolls slowly, speed will fluctuate\n" +
			"   around 0.33, causing \"jumpy\" scrolling (because anything\n" +
			"   below 0.33 is ignored)"
		);

		final Composite composite = new Composite(shell, 0);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(2, true));

		new Label(composite, 0).setText(
			"Composite that handles SWT.MouseWheel"
		);

		new Label(composite, 0).setText(
			"Emulated smooth trackpad"
		);

		final Composite table = new Composite(composite, SWT.BORDER);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		table.addListener(SWT.MouseWheel, event -> {
			pos += event.count;
			System.out.println(System.currentTimeMillis() + " SWT.MouseWheel: event.count = " + event.count);
			table.redraw();
		});

		GC tableGC = new GC(table);
		final int lineHeight = tableGC.getFontMetrics().getHeight();
		tableGC.dispose();

		table.addListener(SWT.Paint, event -> {
			final Point size = table.getSize();
			int i = pos;
			for (int y = 0; y < size.y; y += lineHeight) {
				event.gc.drawString(String.valueOf(i++), 0, y);
			}
		});

		class EmulateListener implements Listener {
			Point lastPoint = new Point(0, 0);
			boolean isDragging = false;
			int delta_y = 0;
			double scale = lineHeight;

			public double takeDelta_y() {
				if (!isDragging) return 0;
				double result = delta_y / scale;
				delta_y = 0;
				return result;
			}

			@Override
			public void handleEvent(Event event) {
				switch (event.type) {
					case SWT.MouseDown:
						isDragging = true;
						lastPoint.x = 0;
						lastPoint.y = 0;
						delta_y = 0;
						break;
					case SWT.MouseUp:
					case SWT.MouseExit:
						isDragging = false;
						break;
					case SWT.MouseMove:
						if ((0 != lastPoint.x) && (0 != lastPoint.y))
							delta_y += (event.y - lastPoint.y);

						lastPoint.x = event.x;
						lastPoint.y = event.y;
						break;
				}
			}
		};

		EmulateListener emulateListener = new EmulateListener();

		Composite emulatedTrackpad = new Composite(composite, SWT.BORDER);
		emulatedTrackpad.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		emulatedTrackpad.addListener(SWT.MouseDown, emulateListener);
		emulatedTrackpad.addListener(SWT.MouseUp, emulateListener);
		emulatedTrackpad.addListener(SWT.MouseExit, emulateListener);
		emulatedTrackpad.addListener(SWT.MouseMove, emulateListener);

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (table.isDisposed()) return;
				display.timerExec(200, this);

				double delta_y = emulateListener.takeDelta_y();
				if (0 != delta_y) {
					System.out.format(System.currentTimeMillis() + " Emulating delta_y=%.2f%n", delta_y);
					postSmoothScrollEvent(table, 0, delta_y);
				}
			}
		};

		runnable.run();

		shell.setSize(600, 600);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}
}
