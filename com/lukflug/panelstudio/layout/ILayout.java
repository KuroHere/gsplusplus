/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.layout;

import com.lukflug.panelstudio.layout.IComponentAdder;
import com.lukflug.panelstudio.layout.IComponentGenerator;
import com.lukflug.panelstudio.setting.IClient;
import com.lukflug.panelstudio.theme.ITheme;

@FunctionalInterface
public interface ILayout {
    public void populateGUI(IComponentAdder var1, IComponentGenerator var2, IClient var3, ITheme var4);
}

