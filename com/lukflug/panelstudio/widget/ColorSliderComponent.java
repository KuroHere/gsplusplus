/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.setting.IBooleanSetting;
import com.lukflug.panelstudio.setting.IColorSetting;
import com.lukflug.panelstudio.setting.INumberSetting;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.ColorComponent;
import com.lukflug.panelstudio.widget.NumberSlider;
import com.lukflug.panelstudio.widget.ToggleButton;

public class ColorSliderComponent
extends ColorComponent {
    public ColorSliderComponent(IColorSetting setting, ThemeTuple theme) {
        super(setting, theme);
    }

    @Override
    public void populate(ThemeTuple theme) {
        this.addComponent(this.getRainbowComponent(theme, new ColorComponent.RainbowToggle()));
        this.addComponent(this.getColorComponent(theme, 0, new ColorComponent.ColorNumber(0, () -> this.setting.hasHSBModel())));
        this.addComponent(this.getColorComponent(theme, 1, new ColorComponent.ColorNumber(1, () -> this.setting.hasHSBModel())));
        this.addComponent(this.getColorComponent(theme, 2, new ColorComponent.ColorNumber(2, () -> this.setting.hasHSBModel())));
        this.addComponent(this.getColorComponent(theme, 3, new ColorComponent.ColorNumber(3, () -> this.setting.hasHSBModel())));
    }

    public IComponent getRainbowComponent(ThemeTuple theme, IBooleanSetting toggle) {
        return new ToggleButton(toggle, theme.getButtonRenderer(Boolean.class, false));
    }

    public IComponent getColorComponent(ThemeTuple theme, int value, INumberSetting number) {
        return new NumberSlider(number, theme.getSliderRenderer(false));
    }
}

