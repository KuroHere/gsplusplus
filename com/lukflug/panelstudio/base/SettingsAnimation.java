/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.base;

import com.lukflug.panelstudio.base.Animation;
import java.util.function.Supplier;

public class SettingsAnimation
extends Animation {
    protected final Supplier<Integer> speed;

    public SettingsAnimation(Supplier<Integer> speed, Supplier<Long> time) {
        super(time);
        this.speed = speed;
    }

    @Override
    protected int getSpeed() {
        return this.speed.get();
    }
}

