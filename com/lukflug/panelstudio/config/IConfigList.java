/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.config;

import com.lukflug.panelstudio.config.IPanelConfig;

public interface IConfigList {
    public void begin(boolean var1);

    public void end(boolean var1);

    public IPanelConfig addPanel(String var1);

    public IPanelConfig getPanel(String var1);
}

