/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.component.ComponentBase;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.IEmptySpaceRenderer;
import java.util.function.Supplier;

public abstract class EmptySpace<T>
extends ComponentBase {
    protected Supplier<Integer> height;
    protected IEmptySpaceRenderer<T> renderer;

    public EmptySpace(ILabeled label, Supplier<Integer> height, IEmptySpaceRenderer<T> renderer) {
        super(label);
        this.height = height;
        this.renderer = renderer;
    }

    @Override
    public void render(Context context) {
        super.getHeight(context);
        this.renderer.renderSpace(context, this.isVisible(), this.getState());
    }

    @Override
    public void releaseFocus() {
    }

    @Override
    protected int getHeight() {
        return this.height.get();
    }

    protected abstract T getState();
}

