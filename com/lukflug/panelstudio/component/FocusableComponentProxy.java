/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IComponentProxy;
import java.util.function.Consumer;

public abstract class FocusableComponentProxy<T extends IComponent>
implements IComponentProxy<T> {
    private final boolean initFocus;
    private boolean focus;
    private boolean requestFocus = false;

    public FocusableComponentProxy(boolean focus) {
        this.initFocus = focus;
        this.focus = focus;
    }

    @Override
    public void handleButton(Context context, int button) {
        IComponentProxy.super.handleButton(context, button);
        if (context.getInterface().getButton(button)) {
            this.focus = context.isHovered();
            if (this.focus) {
                context.requestFocus();
            }
        }
    }

    @Override
    public Context doOperation(Context context, Consumer<Context> operation) {
        if (this.requestFocus) {
            context.requestFocus();
        } else if (!context.hasFocus()) {
            this.focus = false;
        }
        this.requestFocus = false;
        return IComponentProxy.super.doOperation(context, operation);
    }

    @Override
    public void releaseFocus() {
        this.focus = false;
        IComponentProxy.super.releaseFocus();
    }

    @Override
    public void enter() {
        this.focus = this.initFocus;
        if (this.focus) {
            this.requestFocus = true;
        }
        IComponentProxy.super.enter();
    }

    @Override
    public void exit() {
        this.focus = this.initFocus;
        IComponentProxy.super.exit();
    }

    public boolean hasFocus(Context context) {
        return context.hasFocus() && this.focus;
    }
}

