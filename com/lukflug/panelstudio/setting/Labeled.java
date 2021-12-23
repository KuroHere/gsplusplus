/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.setting;

import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.setting.ILabeled;

public class Labeled
implements ILabeled {
    protected String title;
    protected String description;
    protected IBoolean visible;

    public Labeled(String title, String description, IBoolean visible) {
        this.title = title;
        this.description = description;
        this.visible = visible;
    }

    @Override
    public String getDisplayName() {
        return this.title;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public IBoolean isVisible() {
        return this.visible;
    }
}

