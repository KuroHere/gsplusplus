/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import java.awt.Color;
import java.awt.Point;

public interface IColorPickerRenderer {
    public void renderPicker(Context var1, boolean var2, Color var3);

    public Color transformPoint(Context var1, Color var2, Point var3);

    public int getDefaultHeight(int var1);
}

