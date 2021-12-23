/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import java.awt.Rectangle;

public interface ISliderRenderer {
    public void renderSlider(Context var1, String var2, String var3, boolean var4, double var5);

    public int getDefaultHeight();

    default public Rectangle getSlideArea(Context context, String title, String state) {
        return context.getRect();
    }
}

