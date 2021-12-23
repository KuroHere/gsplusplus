/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.Context;

public interface IComponent {
    public String getTitle();

    public void render(Context var1);

    public void handleButton(Context var1, int var2);

    public void handleKey(Context var1, int var2);

    public void handleChar(Context var1, char var2);

    public void handleScroll(Context var1, int var2);

    public void getHeight(Context var1);

    public void enter();

    public void exit();

    public void releaseFocus();

    public boolean isVisible();
}

