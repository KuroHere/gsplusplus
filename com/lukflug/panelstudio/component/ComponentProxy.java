/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IComponentProxy;

public class ComponentProxy<T extends IComponent>
implements IComponentProxy<T> {
    protected final T component;

    public ComponentProxy(T component) {
        this.component = component;
    }

    @Override
    public T getComponent() {
        return this.component;
    }
}

