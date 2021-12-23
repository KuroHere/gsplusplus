/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;

public interface IScrollBarRenderer<T> {
    default public int renderScrollBar(Context context, boolean focus, T state, boolean horizontal, int height, int position) {
        return position;
    }

    default public int getThickness() {
        return 0;
    }
}

