/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.theme.IEmptySpaceRenderer;

@FunctionalInterface
public interface IEmptySpaceRendererProxy<T>
extends IEmptySpaceRenderer<T> {
    @Override
    default public void renderSpace(Context context, boolean focus, T state) {
        this.getRenderer().renderSpace(context, focus, state);
    }

    public IEmptySpaceRenderer<T> getRenderer();
}

