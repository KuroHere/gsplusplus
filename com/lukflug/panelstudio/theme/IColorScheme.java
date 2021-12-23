/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;

public interface IColorScheme {
    public void createSetting(ITheme var1, String var2, String var3, boolean var4, boolean var5, Color var6, boolean var7);

    public Color getColor(String var1);
}

