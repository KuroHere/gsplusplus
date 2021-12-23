/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.popup;

import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.popup.IPopupPositioner;

public class PopupTuple {
    public final IPopupPositioner popupPos;
    public final boolean dynamicPopup;
    public final IScrollSize popupSize;

    public PopupTuple(IPopupPositioner popupPos, boolean dynamicPopup, IScrollSize popupSize) {
        this.popupPos = popupPos;
        this.dynamicPopup = dynamicPopup;
        this.popupSize = popupSize;
    }
}

