/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.setting;

import com.lukflug.panelstudio.base.IBoolean;

@FunctionalInterface
public interface ILabeled {
    public String getDisplayName();

    default public String getDescription() {
        return null;
    }

    default public IBoolean isVisible() {
        return () -> true;
    }
}

