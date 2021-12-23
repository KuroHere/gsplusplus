/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.component.ComponentProxy;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IHorizontalComponent;

public class HorizontalComponent<T extends IComponent>
extends ComponentProxy<T>
implements IHorizontalComponent {
    protected int width;
    protected int weight;

    public HorizontalComponent(T component, int width, int weight) {
        super(component);
        this.width = width;
        this.weight = weight;
    }

    @Override
    public int getWidth(IInterface inter) {
        return this.width;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }
}

