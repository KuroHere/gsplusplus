/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.theme.IScrollBarRenderer;

@FunctionalInterface
public interface IScrollBarRendererProxy<T>
extends IScrollBarRenderer<T> {
    @Override
    default public int renderScrollBar(Context context, boolean focus, T state, boolean horizontal, int height, int position) {
        return this.getRenderer().renderScrollBar(context, focus, state, horizontal, height, position);
    }

    @Override
    default public int getThickness() {
        return this.getRenderer().getThickness();
    }

    public IScrollBarRenderer<T> getRenderer();
}

