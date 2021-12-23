/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.base;

import java.util.function.Supplier;

public abstract class Animation {
    protected final Supplier<Long> time;
    protected double value;
    protected double lastValue;
    protected long lastTime;

    public Animation(Supplier<Long> time) {
        this.time = time;
        this.lastTime = time.get();
    }

    public void initValue(double value) {
        this.value = value;
        this.lastValue = value;
    }

    public double getValue() {
        if (this.getSpeed() == 0) {
            return this.value;
        }
        double weight = (double)(this.time.get() - this.lastTime) / (double)this.getSpeed();
        if (weight >= 1.0) {
            return this.value;
        }
        if (weight <= 0.0) {
            return this.lastValue;
        }
        weight = this.interpolate(weight);
        return this.value * weight + this.lastValue * (1.0 - weight);
    }

    public double getTarget() {
        return this.value;
    }

    public void setValue(double value) {
        this.lastValue = this.getValue();
        this.value = value;
        this.lastTime = this.time.get();
    }

    protected double interpolate(double weight) {
        return (weight - 1.0) * (weight - 1.0) * (weight - 1.0) + 1.0;
    }

    protected abstract int getSpeed();
}

