/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.base;

import com.lukflug.panelstudio.base.ConstantToggleable;

public class SimpleToggleable
extends ConstantToggleable {
    public SimpleToggleable(boolean value) {
        super(value);
    }

    @Override
    public void toggle() {
        this.value = !this.value;
    }
}

