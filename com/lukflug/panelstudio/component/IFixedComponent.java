/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.config.IPanelConfig;
import com.lukflug.panelstudio.popup.IPopup;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import java.awt.Point;
import java.awt.Rectangle;

public interface IFixedComponent
extends IComponent,
IPopup {
    public Point getPosition(IInterface var1);

    public void setPosition(IInterface var1, Point var2);

    @Override
    default public void setPosition(IInterface inter, Rectangle component, Rectangle panel, IPopupPositioner positioner) {
        this.setPosition(inter, positioner.getPosition(inter, null, component, panel));
    }

    public int getWidth(IInterface var1);

    public boolean savesState();

    public void saveConfig(IInterface var1, IPanelConfig var2);

    public void loadConfig(IInterface var1, IPanelConfig var2);

    public String getConfigName();
}

