/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.setting.INumberSetting;
import com.lukflug.panelstudio.theme.ISliderRenderer;
import com.lukflug.panelstudio.widget.Slider;

public class NumberSlider
extends Slider {
    protected INumberSetting setting;

    public NumberSlider(INumberSetting setting, ISliderRenderer renderer) {
        super(setting, renderer);
        this.setting = setting;
    }

    @Override
    protected double getValue() {
        return (this.setting.getNumber() - this.setting.getMinimumValue()) / (this.setting.getMaximumValue() - this.setting.getMinimumValue());
    }

    @Override
    protected void setValue(double value) {
        this.setting.setNumber(value * (this.setting.getMaximumValue() - this.setting.getMinimumValue()) + this.setting.getMinimumValue());
    }

    @Override
    protected String getDisplayState() {
        return this.setting.getSettingState();
    }
}

