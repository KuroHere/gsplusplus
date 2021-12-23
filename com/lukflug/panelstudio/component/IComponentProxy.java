/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.component.IComponent;
import java.util.function.Consumer;

@FunctionalInterface
public interface IComponentProxy<T extends IComponent>
extends IComponent {
    @Override
    default public String getTitle() {
        return this.getComponent().getTitle();
    }

    @Override
    default public void render(Context context) {
        this.doOperation(context, arg_0 -> this.getComponent().render(arg_0));
    }

    @Override
    default public void handleButton(Context context, int button) {
        this.doOperation(context, subContext -> this.getComponent().handleButton((Context)subContext, button));
    }

    @Override
    default public void handleKey(Context context, int scancode) {
        this.doOperation(context, subContext -> this.getComponent().handleKey((Context)subContext, scancode));
    }

    @Override
    default public void handleChar(Context context, char character) {
        this.doOperation(context, subContext -> this.getComponent().handleChar((Context)subContext, character));
    }

    @Override
    default public void handleScroll(Context context, int diff) {
        this.doOperation(context, subContext -> this.getComponent().handleScroll((Context)subContext, diff));
    }

    @Override
    default public void getHeight(Context context) {
        this.doOperation(context, arg_0 -> this.getComponent().getHeight(arg_0));
    }

    @Override
    default public void enter() {
        this.getComponent().enter();
    }

    @Override
    default public void exit() {
        this.getComponent().exit();
    }

    @Override
    default public void releaseFocus() {
        this.getComponent().releaseFocus();
    }

    @Override
    default public boolean isVisible() {
        return this.getComponent().isVisible();
    }

    public T getComponent();

    default public Context doOperation(Context context, Consumer<Context> operation) {
        Context subContext = this.getContext(context);
        operation.accept(subContext);
        if (subContext != context) {
            if (subContext.focusReleased()) {
                context.releaseFocus();
            } else if (subContext.foucsRequested()) {
                context.requestFocus();
            }
            context.setHeight(this.getHeight(subContext.getSize().height));
            if (subContext.getDescription() != null) {
                context.setDescription(subContext.getDescription());
            }
        }
        return subContext;
    }

    default public int getHeight(int height) {
        return height;
    }

    default public Context getContext(Context context) {
        return context;
    }
}

