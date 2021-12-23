/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.base;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.base.SimpleToggleable;

public final class AnimatedToggleable
implements IToggleable {
    private final IToggleable toggle;
    private final Animation animation;

    public AnimatedToggleable(IToggleable toggle, Animation animation) {
        this.toggle = toggle != null ? toggle : new SimpleToggleable(false);
        this.animation = animation != null ? animation : new Animation(System::currentTimeMillis){

            @Override
            protected int getSpeed() {
                return 0;
            }
        };
        if (this.toggle.isOn()) {
            this.animation.initValue(1.0);
        } else {
            this.animation.initValue(0.0);
        }
    }

    @Override
    public void toggle() {
        this.toggle.toggle();
        if (this.toggle.isOn()) {
            this.animation.setValue(1.0);
        } else {
            this.animation.setValue(0.0);
        }
    }

    @Override
    public boolean isOn() {
        return this.toggle.isOn();
    }

    public double getValue() {
        if (this.animation.getTarget() != (double)(this.toggle.isOn() ? 1 : 0)) {
            if (this.toggle.isOn()) {
                this.animation.setValue(1.0);
            } else {
                this.animation.setValue(0.0);
            }
        }
        return this.animation.getValue();
    }
}

