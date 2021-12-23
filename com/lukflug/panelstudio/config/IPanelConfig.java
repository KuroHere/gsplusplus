/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.config;

import java.awt.Dimension;
import java.awt.Point;

public interface IPanelConfig {
    public void savePositon(Point var1);

    public void saveSize(Dimension var1);

    public Point loadPosition();

    public Dimension loadSize();

    public void saveState(boolean var1);

    public boolean loadState();
}

