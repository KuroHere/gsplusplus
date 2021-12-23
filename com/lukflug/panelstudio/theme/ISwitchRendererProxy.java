/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.theme.IButtonRendererProxy;
import com.lukflug.panelstudio.theme.ISwitchRenderer;
import java.awt.Rectangle;

@FunctionalInterface
public interface ISwitchRendererProxy<T>
extends ISwitchRenderer<T>,
IButtonRendererProxy<T> {
    @Override
    default public Rectangle getOnField(Context context) {
        return this.getRenderer().getOnField(context);
    }

    @Override
    default public Rectangle getOffField(Context context) {
        return this.getRenderer().getOffField(context);
    }

    @Override
    public ISwitchRenderer<T> getRenderer();
}

