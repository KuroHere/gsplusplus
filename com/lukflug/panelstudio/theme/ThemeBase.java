/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.theme.IColorScheme;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;

public abstract class ThemeBase
implements ITheme {
    protected final IColorScheme scheme;
    private Color overrideColor = null;

    public ThemeBase(IColorScheme scheme) {
        this.scheme = scheme;
    }

    @Override
    public void loadAssets(IInterface inter) {
    }

    @Override
    public void overrideMainColor(Color color) {
        this.overrideColor = color;
    }

    @Override
    public void restoreMainColor() {
        this.overrideColor = null;
    }

    protected Color getColor(Color color) {
        if (this.overrideColor == null) {
            return color;
        }
        return this.overrideColor;
    }
}

