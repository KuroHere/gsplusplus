/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;

public interface IContainerRenderer {
    default public void renderBackground(Context context, boolean focus) {
    }

    default public int getBorder() {
        return 0;
    }

    default public int getLeft() {
        return 0;
    }

    default public int getRight() {
        return 0;
    }

    default public int getTop() {
        return 0;
    }

    default public int getBottom() {
        return 0;
    }
}

