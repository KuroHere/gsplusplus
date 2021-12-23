/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.component.IComponentProxy;
import com.lukflug.panelstudio.component.IHorizontalComponent;

@FunctionalInterface
public interface IHorizontalComponentProxy<T extends IHorizontalComponent>
extends IComponentProxy<T>,
IHorizontalComponent {
    @Override
    default public int getWidth(IInterface inter) {
        return ((IHorizontalComponent)this.getComponent()).getWidth(inter);
    }

    @Override
    default public int getWeight() {
        return ((IHorizontalComponent)this.getComponent()).getWeight();
    }
}

