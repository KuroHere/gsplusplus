/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.setting;

import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.IModule;
import java.util.stream.Stream;

public interface ICategory
extends ILabeled {
    public Stream<IModule> getModules();
}

