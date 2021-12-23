/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.popup;

import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.popup.IPopup;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import java.awt.Rectangle;

public interface IPopupDisplayer {
    public void displayPopup(IPopup var1, Rectangle var2, IToggleable var3, IPopupPositioner var4);
}

