/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.theme.IContainerRenderer;

public interface IPanelRenderer<T>
extends IContainerRenderer {
    public void renderPanelOverlay(Context var1, boolean var2, T var3, boolean var4);

    public void renderTitleOverlay(Context var1, boolean var2, T var3, boolean var4);
}

