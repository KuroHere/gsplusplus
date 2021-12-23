/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.theme.IButtonRenderer;

@FunctionalInterface
public interface IButtonRendererProxy<T>
extends IButtonRenderer<T> {
    @Override
    default public void renderButton(Context context, String title, boolean focus, T state) {
        this.getRenderer().renderButton(context, title, focus, state);
    }

    @Override
    default public int getDefaultHeight() {
        return this.getRenderer().getDefaultHeight();
    }

    public IButtonRenderer<T> getRenderer();
}

