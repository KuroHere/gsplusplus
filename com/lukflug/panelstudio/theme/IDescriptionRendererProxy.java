/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.theme.IDescriptionRenderer;
import java.awt.Point;

@FunctionalInterface
public interface IDescriptionRendererProxy
extends IDescriptionRenderer {
    @Override
    default public void renderDescription(IInterface inter, Point pos, String text) {
        this.getRenderer().renderDescription(inter, pos, text);
    }

    public IDescriptionRenderer getRenderer();
}

