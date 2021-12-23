/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.component.IComponentProxy;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.config.IPanelConfig;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import java.awt.Point;
import java.awt.Rectangle;

@FunctionalInterface
public interface IFixedComponentProxy<T extends IFixedComponent>
extends IComponentProxy<T>,
IFixedComponent {
    @Override
    default public Point getPosition(IInterface inter) {
        return ((IFixedComponent)this.getComponent()).getPosition(inter);
    }

    @Override
    default public void setPosition(IInterface inter, Point position) {
        ((IFixedComponent)this.getComponent()).setPosition(inter, position);
    }

    @Override
    default public void setPosition(IInterface inter, Rectangle component, Rectangle panel, IPopupPositioner positioner) {
        ((IFixedComponent)this.getComponent()).setPosition(inter, component, panel, positioner);
    }

    @Override
    default public int getWidth(IInterface inter) {
        return ((IFixedComponent)this.getComponent()).getWidth(inter);
    }

    @Override
    default public boolean savesState() {
        return ((IFixedComponent)this.getComponent()).savesState();
    }

    @Override
    default public void saveConfig(IInterface inter, IPanelConfig config) {
        ((IFixedComponent)this.getComponent()).saveConfig(inter, config);
    }

    @Override
    default public void loadConfig(IInterface inter, IPanelConfig config) {
        ((IFixedComponent)this.getComponent()).loadConfig(inter, config);
    }

    @Override
    default public String getConfigName() {
        return ((IFixedComponent)this.getComponent()).getConfigName();
    }
}

