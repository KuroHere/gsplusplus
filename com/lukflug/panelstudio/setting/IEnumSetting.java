/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.setting;

import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.ISetting;
import java.util.Arrays;

public interface IEnumSetting
extends ISetting<String> {
    public void increment();

    public void decrement();

    public String getValueName();

    default public int getValueIndex() {
        ILabeled[] stuff = this.getAllowedValues();
        String compare = this.getValueName();
        for (int i = 0; i < stuff.length; ++i) {
            if (!stuff[i].getDisplayName().equals(compare)) continue;
            return i;
        }
        return -1;
    }

    public void setValueIndex(int var1);

    public ILabeled[] getAllowedValues();

    @Override
    default public String getSettingState() {
        return this.getValueName();
    }

    @Override
    default public Class<String> getSettingClass() {
        return String.class;
    }

    public static ILabeled[] getVisibleValues(IEnumSetting setting) {
        return (ILabeled[])Arrays.stream(setting.getAllowedValues()).filter(value -> value.isVisible().isOn()).toArray(ILabeled[]::new);
    }
}

