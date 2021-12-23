/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.theme.ISliderRenderer;
import java.awt.Rectangle;

@FunctionalInterface
public interface ISliderRendererProxy
extends ISliderRenderer {
    @Override
    default public void renderSlider(Context context, String title, String state, boolean focus, double value) {
        this.getRenderer().renderSlider(context, title, state, focus, value);
    }

    @Override
    default public int getDefaultHeight() {
        return this.getRenderer().getDefaultHeight();
    }

    @Override
    default public Rectangle getSlideArea(Context context, String title, String state) {
        return this.getRenderer().getSlideArea(context, title, state);
    }

    public ISliderRenderer getRenderer();
}

