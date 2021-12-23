/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.Context;

public interface IScrollSize {
    default public int getScrollHeight(Context context, int componentHeight) {
        return componentHeight;
    }

    default public int getComponentWidth(Context context) {
        return context.getSize().width;
    }
}

