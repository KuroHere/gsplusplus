/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.setting;

import com.lukflug.panelstudio.setting.ISetting;

public interface IStringSetting
extends ISetting<String> {
    public String getValue();

    public void setValue(String var1);

    @Override
    default public String getSettingState() {
        return this.getValue();
    }

    @Override
    default public Class<String> getSettingClass() {
        return String.class;
    }
}

