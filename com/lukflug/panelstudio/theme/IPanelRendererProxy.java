/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.theme.IPanelRenderer;

@FunctionalInterface
public interface IPanelRendererProxy<T>
extends IPanelRenderer<T> {
    @Override
    default public void renderBackground(Context context, boolean focus) {
        this.getRenderer().renderBackground(context, focus);
    }

    @Override
    default public int getBorder() {
        return this.getRenderer().getBorder();
    }

    @Override
    default public int getLeft() {
        return this.getRenderer().getLeft();
    }

    @Override
    default public int getRight() {
        return this.getRenderer().getRight();
    }

    @Override
    default public int getTop() {
        return this.getRenderer().getTop();
    }

    @Override
    default public int getBottom() {
        return this.getRenderer().getBottom();
    }

    @Override
    default public void renderPanelOverlay(Context context, boolean focus, T state, boolean open) {
        this.getRenderer().renderPanelOverlay(context, focus, state, open);
    }

    @Override
    default public void renderTitleOverlay(Context context, boolean focus, T state, boolean open) {
        this.getRenderer().renderTitleOverlay(context, focus, state, open);
    }

    public IPanelRenderer<T> getRenderer();
}

