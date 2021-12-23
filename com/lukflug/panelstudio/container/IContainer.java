/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.container;

import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.component.IComponent;

public interface IContainer<T extends IComponent> {
    public boolean addComponent(T var1);

    public boolean addComponent(T var1, IBoolean var2);

    public boolean removeComponent(T var1);
}

