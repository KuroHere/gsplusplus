/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.setting;

import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.setting.ISetting;

public interface IBooleanSetting
extends ISetting<Boolean>,
IToggleable {
    @Override
    default public Boolean getSettingState() {
        return this.isOn();
    }

    @Override
    default public Class<Boolean> getSettingClass() {
        return Boolean.class;
    }
}

