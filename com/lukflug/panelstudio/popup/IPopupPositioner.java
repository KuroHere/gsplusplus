/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.popup;

import com.lukflug.panelstudio.base.IInterface;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

@FunctionalInterface
public interface IPopupPositioner {
    public Point getPosition(IInterface var1, Dimension var2, Rectangle var3, Rectangle var4);
}

