/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.theme.IButtonRenderer;
import java.awt.Rectangle;

public interface ISwitchRenderer<T>
extends IButtonRenderer<T> {
    public Rectangle getOnField(Context var1);

    public Rectangle getOffField(Context var1);
}

