/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.setting;

import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.ISetting;
import java.util.stream.Stream;

public interface IModule
extends ILabeled {
    public IToggleable isEnabled();

    public Stream<ISetting<?>> getSettings();
}

