/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.component.FocusableComponent;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.ISliderRenderer;
import java.awt.Rectangle;

public abstract class Slider
extends FocusableComponent {
    protected boolean attached = false;
    protected ISliderRenderer renderer;

    public Slider(ILabeled label, ISliderRenderer renderer) {
        super(label);
        this.renderer = renderer;
    }

    @Override
    public void render(Context context) {
        super.render(context);
        if (this.attached) {
            Rectangle rect = this.renderer.getSlideArea(context, this.getTitle(), this.getDisplayState());
            double value = (double)(context.getInterface().getMouse().x - rect.x) / (double)(rect.width - 1);
            if (value < 0.0) {
                value = 0.0;
            } else if (value > 1.0) {
                value = 1.0;
            }
            this.setValue(value);
        }
        if (!context.getInterface().getButton(0)) {
            this.attached = false;
        }
        this.renderer.renderSlider(context, this.getTitle(), this.getDisplayState(), this.hasFocus(context), this.getValue());
    }

    @Override
    public void handleButton(Context context, int button) {
        super.handleButton(context, button);
        if (button == 0 && context.isClicked(button) && this.renderer.getSlideArea(context, this.getTitle(), this.getDisplayState()).contains(context.getInterface().getMouse())) {
            this.attached = true;
        }
    }

    @Override
    public void exit() {
        super.exit();
        this.attached = false;
    }

    @Override
    protected int getHeight() {
        return this.renderer.getDefaultHeight();
    }

    protected abstract double getValue();

    protected abstract void setValue(double var1);

    protected abstract String getDisplayState();
}

