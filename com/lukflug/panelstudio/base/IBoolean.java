/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.base;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

@FunctionalInterface
public interface IBoolean
extends BooleanSupplier,
Supplier<Boolean>,
Predicate<Void> {
    public boolean isOn();

    @Override
    default public boolean getAsBoolean() {
        return this.isOn();
    }

    @Override
    default public Boolean get() {
        return this.isOn();
    }

    @Override
    default public boolean test(Void t) {
        return this.isOn();
    }
}

