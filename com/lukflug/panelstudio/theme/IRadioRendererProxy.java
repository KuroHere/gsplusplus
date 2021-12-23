/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.IRadioRenderer;
import java.awt.Rectangle;

@FunctionalInterface
public interface IRadioRendererProxy
extends IRadioRenderer {
    @Override
    default public void renderItem(Context context, ILabeled[] items, boolean focus, int target, double state, boolean horizontal) {
        this.getRenderer().renderItem(context, items, focus, target, state, horizontal);
    }

    @Override
    default public int getDefaultHeight(ILabeled[] items, boolean horizontal) {
        return this.getRenderer().getDefaultHeight(items, horizontal);
    }

    @Override
    default public Rectangle getItemRect(Context context, ILabeled[] items, int index, boolean horizontal) {
        return this.getRenderer().getItemRect(context, items, index, horizontal);
    }

    public IRadioRenderer getRenderer();
}

