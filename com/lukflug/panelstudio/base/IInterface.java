/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public interface IInterface {
    public static final int LBUTTON = 0;
    public static final int RBUTTON = 1;
    public static final int SHIFT = 0;
    public static final int CTRL = 1;
    public static final int ALT = 2;
    public static final int SUPER = 3;

    public long getTime();

    public Point getMouse();

    public boolean getButton(int var1);

    public boolean getModifier(int var1);

    public void drawString(Point var1, int var2, String var3, Color var4);

    public int getFontWidth(int var1, String var2);

    public void fillTriangle(Point var1, Point var2, Point var3, Color var4, Color var5, Color var6);

    public void drawLine(Point var1, Point var2, Color var3, Color var4);

    public void fillRect(Rectangle var1, Color var2, Color var3, Color var4, Color var5);

    public void drawRect(Rectangle var1, Color var2, Color var3, Color var4, Color var5);

    public int loadImage(String var1);

    default public void drawImage(Rectangle r, int rotation, boolean parity, int image) {
        this.drawImage(r, rotation, parity, image, new Color(255, 255, 255));
    }

    public void drawImage(Rectangle var1, int var2, boolean var3, int var4, Color var5);

    public Dimension getWindowSize();

    public void window(Rectangle var1);

    public void restore();
}

