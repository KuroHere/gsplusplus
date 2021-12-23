/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.AnimatedToggleable;
import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IComponentProxy;
import java.awt.Point;

public abstract class CollapsibleComponent<T extends IComponent>
implements IComponentProxy<T> {
    protected AnimatedToggleable toggle;

    public CollapsibleComponent(IToggleable toggle, Animation animation) {
        this.toggle = new AnimatedToggleable(toggle, animation);
    }

    public CollapsibleComponent(AnimatedToggleable toggle) {
        this.toggle = toggle;
    }

    @Override
    public void render(Context context) {
        this.doOperation(context, subContext -> {
            context.getInterface().window(context.getRect());
            this.getComponent().render((Context)subContext);
            context.getInterface().restore();
        });
    }

    @Override
    public boolean isVisible() {
        return this.getComponent().isVisible() && this.toggle.getValue() != 0.0;
    }

    @Override
    public Context getContext(Context context) {
        Context subContext = new Context(context, context.getSize().width, new Point(0, 0), true, true);
        this.getComponent().getHeight(subContext);
        int height = this.getHeight(subContext.getSize().height);
        int offset = height - subContext.getSize().height;
        context.setHeight(height);
        return new Context(context, context.getSize().width, new Point(0, offset), true, context.isHovered());
    }

    @Override
    public int getHeight(int height) {
        return (int)(this.toggle.getValue() * (double)height);
    }

    public AnimatedToggleable getToggle() {
        return this.toggle;
    }
}

