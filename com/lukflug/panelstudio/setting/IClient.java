/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.setting;

import com.lukflug.panelstudio.setting.ICategory;
import java.util.stream.Stream;

@FunctionalInterface
public interface IClient {
    public Stream<ICategory> getCategories();
}

