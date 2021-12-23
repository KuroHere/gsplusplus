/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.setting;

import com.lukflug.panelstudio.setting.ISetting;

public interface IKeybindSetting
extends ISetting<String> {
    public int getKey();

    public void setKey(int var1);

    public String getKeyName();

    @Override
    default public String getSettingState() {
        return this.getKeyName();
    }

    @Override
    default public Class<String> getSettingClass() {
        return String.class;
    }
}

