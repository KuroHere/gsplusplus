/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.theme.IColorPickerRenderer;
import java.awt.Color;
import java.awt.Point;

@FunctionalInterface
public interface IColorPickerRendererProxy
extends IColorPickerRenderer {
    @Override
    default public void renderPicker(Context context, boolean focus, Color color) {
        this.getRenderer().renderPicker(context, focus, color);
    }

    @Override
    default public Color transformPoint(Context context, Color color, Point point) {
        return this.getRenderer().transformPoint(context, color, point);
    }

    @Override
    default public int getDefaultHeight(int width) {
        return this.getRenderer().getDefaultHeight(width);
    }

    public IColorPickerRenderer getRenderer();
}

