/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.theme.IResizeBorderRenderer;

@FunctionalInterface
public interface IResizeBorderRendererProxy
extends IResizeBorderRenderer {
    @Override
    default public void drawBorder(Context context, boolean focus) {
        this.getRenderer().drawBorder(context, focus);
    }

    @Override
    default public int getBorder() {
        return this.getRenderer().getBorder();
    }

    public IResizeBorderRenderer getRenderer();
}

