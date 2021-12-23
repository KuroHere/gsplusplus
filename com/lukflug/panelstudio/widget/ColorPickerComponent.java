/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.setting.IColorSetting;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.ColorComponent;
import com.lukflug.panelstudio.widget.ColorPicker;
import com.lukflug.panelstudio.widget.NumberSlider;
import com.lukflug.panelstudio.widget.ToggleButton;

public class ColorPickerComponent
extends ColorComponent {
    public ColorPickerComponent(IColorSetting setting, ThemeTuple theme) {
        super(setting, theme);
    }

    @Override
    public void populate(ThemeTuple theme) {
        this.addComponent(new ToggleButton(new ColorComponent.RainbowToggle(), theme.getButtonRenderer(Boolean.class, false)));
        this.addComponent(new ColorPicker(this.setting, theme.theme.getColorPickerRenderer()));
        this.addComponent(new NumberSlider(new ColorComponent.ColorNumber(0, () -> true), theme.getSliderRenderer(false)));
        this.addComponent(new NumberSlider(new ColorComponent.ColorNumber(3, () -> true), theme.getSliderRenderer(false)));
    }
}

