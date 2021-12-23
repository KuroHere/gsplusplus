/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.setting;

import com.lukflug.panelstudio.setting.ISetting;
import java.awt.Color;

public interface IColorSetting
extends ISetting<Color> {
    public Color getValue();

    public void setValue(Color var1);

    public Color getColor();

    public boolean getRainbow();

    public void setRainbow(boolean var1);

    default public boolean hasAlpha() {
        return false;
    }

    default public boolean allowsRainbow() {
        return true;
    }

    default public boolean hasHSBModel() {
        return false;
    }

    @Override
    default public Color getSettingState() {
        return this.getValue();
    }

    @Override
    default public Class<Color> getSettingClass() {
        return Color.class;
    }
}

