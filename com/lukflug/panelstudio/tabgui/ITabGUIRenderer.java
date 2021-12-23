/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.tabgui;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import java.awt.Rectangle;

public interface ITabGUIRenderer<T> {
    public void renderTab(Context var1, int var2, double var3);

    public void renderItem(Context var1, int var2, double var3, int var5, String var6, T var7);

    public int getTabHeight(int var1);

    public Rectangle getItemRect(IInterface var1, Rectangle var2, int var3, double var4);
}

