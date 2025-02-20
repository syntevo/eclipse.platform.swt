/*******************************************************************************
 * Copyright (c) 2025 Syntevo GmbH and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Thomas Singer (Syntevo) - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.widgets;

import java.util.*;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

public class DefaultColorProvider implements ColorProvider {
	private final Map<String, Color> map = new HashMap<>();

	public DefaultColorProvider() {
		final Color selection = new Color(0, 95, 184);

		final Color black = new Color(0, 0, 0);
		final Color gray128 = new Color(128, 128, 128);
		final Color gray160 = new Color(160, 160, 160);
		final Color gray192 = new Color(192, 192, 192);
		final Color gray204 = new Color(204, 204, 204);
		final Color gray227 = new Color(227, 227, 227);
		final Color white = new Color(255, 255, 255);

		final Color disabled = gray128;

		map.put(Label.KEY_DISABLED, disabled);
		map.put(Label.KEY_SHADOW_IN_LIGHT, white);
		map.put(Label.KEY_SHADOW_IN_DARK, gray160);
		map.put(Label.KEY_SHADOW_OUT_LIGHT, gray227);
		map.put(Label.KEY_SHADOW_OUT_DARK, gray160);

		map.put(Button.KEY_BUTTON, white);
		map.put(Button.KEY_HOVER, new Color(224, 238, 254));
		map.put(Button.KEY_TOGGLE, new Color(204, 228, 247));
		map.put(Button.KEY_SELECTION, selection);
		map.put(Button.KEY_GRAYED, gray128);
		map.put(Button.KEY_OUTLINE, gray160);
		map.put(Button.KEY_TEXT, black);
		map.put(Button.KEY_DISABLE, disabled);
		map.put(Button.KEY_ARROW, gray128);

		map.put(ScaleRenderer.KEY_HANDLE_IDLE, selection);
		map.put(ScaleRenderer.KEY_HANDLE_HOVER, black);
		map.put(ScaleRenderer.KEY_HANDLE_DRAG, gray204);
		map.put(ScaleRenderer.KEY_HANDLE_OUTLINE, gray160);
		map.put(ScaleRenderer.KEY_NOTCH, gray160);

		map.put(CSimpleText.KEY_BACKGROUND, white);
		map.put(CSimpleText.KEY_BACKGROUND_READONLY, gray192);
		map.put(CSimpleText.KEY_FOREGROUND, black);
		map.put(CSimpleText.KEY_DISABLED, disabled);
		map.put(CSimpleText.KEY_SELECTION_BACKGROUND, selection);
		map.put(CSimpleText.KEY_SELECTION_FOREGROUND, white);
		map.put(CSimpleText.KEY_BORDER, gray128);
	}

	@Override
	public Color getColor(String key) {
		if (key == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		final Color color = map.get(key);
		if (color == null) {
			SWT.error(SWT.ERROR_UNSPECIFIED);
		}
		return color;
	}
}
