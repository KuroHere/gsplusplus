/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.Description;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.setting.ILabeled;

public abstract class ComponentBase
implements IComponent {
    protected final ILabeled label;

    public ComponentBase(ILabeled label) {
        this.label = label;
    }

    @Override
    public String getTitle() {
        return this.label.getDisplayName();
    }

    @Override
    public void render(Context context) {
        this.getHeight(context);
        if (context.isHovered() && this.label.getDescription() != null) {
            context.setDescription(new Description(context.getRect(), this.label.getDescription()));
        }
    }

    @Override
    public void handleButton(Context context, int button) {
        this.getHeight(context);
    }

    @Override
    public void handleKey(Context context, int scancode) {
        this.getHeight(context);
    }

    @Override
    public void handleChar(Context context, char character) {
        this.getHeight(context);
    }

    @Override
    public void handleScroll(Context context, int diff) {
        this.getHeight(context);
    }

    @Override
    public void getHeight(Context context) {
        context.setHeight(this.getHeight());
    }

    @Override
    public void enter() {
    }

    @Override
    public void exit() {
    }

    @Override
    public boolean isVisible() {
        return this.label.isVisible().isOn();
    }

    protected abstract int getHeight();
}

