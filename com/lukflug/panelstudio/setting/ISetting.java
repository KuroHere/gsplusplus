/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.setting;

import com.lukflug.panelstudio.setting.ILabeled;
import java.util.stream.Stream;

public interface ISetting<T>
extends ILabeled {
    public T getSettingState();

    public Class<T> getSettingClass();

    default public Stream<ISetting<?>> getSubSettings() {
        return null;
    }
}

