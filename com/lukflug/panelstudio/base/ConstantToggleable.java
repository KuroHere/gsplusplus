/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.base;

import com.lukflug.panelstudio.base.IToggleable;

public class ConstantToggleable
implements IToggleable {
    protected boolean value;

    public ConstantToggleable(boolean value) {
        this.value = value;
    }

    @Override
    public boolean isOn() {
        return this.value;
    }

    @Override
    public void toggle() {
    }
}

