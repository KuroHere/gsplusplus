/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import java.awt.Rectangle;

public interface ITextFieldRenderer {
    public int renderTextField(Context var1, String var2, boolean var3, String var4, int var5, int var6, int var7, boolean var8);

    public int getDefaultHeight();

    public Rectangle getTextArea(Context var1, String var2);

    public int transformToCharPos(Context var1, String var2, String var3, int var4);
}

