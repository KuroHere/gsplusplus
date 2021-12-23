/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.setting;

import com.lukflug.panelstudio.setting.ISetting;

public interface INumberSetting
extends ISetting<String> {
    public double getNumber();

    public void setNumber(double var1);

    public double getMaximumValue();

    public double getMinimumValue();

    public int getPrecision();

    @Override
    default public String getSettingState() {
        if (this.getPrecision() == 0) {
            return "" + (int)this.getNumber();
        }
        return String.format("%." + this.getPrecision() + "f", this.getNumber());
    }

    @Override
    default public Class<String> getSettingClass() {
        return String.class;
    }
}

