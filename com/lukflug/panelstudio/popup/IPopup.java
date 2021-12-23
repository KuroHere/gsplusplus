/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.popup;

import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import java.awt.Rectangle;

@FunctionalInterface
public interface IPopup {
    public void setPosition(IInterface var1, Rectangle var2, Rectangle var3, IPopupPositioner var4);
}

