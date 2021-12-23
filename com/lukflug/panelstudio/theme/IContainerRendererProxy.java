/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.theme.IContainerRenderer;

@FunctionalInterface
public interface IContainerRendererProxy
extends IContainerRenderer {
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

    public IContainerRenderer getRenderer();
}

