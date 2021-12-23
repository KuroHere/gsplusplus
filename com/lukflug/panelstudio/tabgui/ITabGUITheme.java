/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.tabgui;

import com.lukflug.panelstudio.popup.IPopupPositioner;
import com.lukflug.panelstudio.tabgui.ITabGUIRenderer;

public interface ITabGUITheme {
    public int getTabWidth();

    public IPopupPositioner getPositioner();

    public ITabGUIRenderer<Void> getParentRenderer();

    public ITabGUIRenderer<Boolean> getChildRenderer();
}

