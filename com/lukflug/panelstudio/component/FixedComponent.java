/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.component.ComponentProxy;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.config.IPanelConfig;
import java.awt.Point;

public class FixedComponent<T extends IComponent>
extends ComponentProxy<T>
implements IFixedComponent {
    protected Point position;
    protected int width;
    protected IToggleable state;
    protected boolean savesState;
    protected String configName;

    public FixedComponent(T component, Point position, int width, IToggleable state, boolean savesState, String configName) {
        super(component);
        this.position = position;
        this.width = width;
        this.state = state;
        this.savesState = savesState;
        this.configName = configName;
    }

    @Override
    public Point getPosition(IInterface inter) {
        return new Point(this.position);
    }

    @Override
    public void setPosition(IInterface inter, Point position) {
        this.position = new Point(position);
    }

    @Override
    public int getWidth(IInterface inter) {
        return this.width;
    }

    @Override
    public boolean savesState() {
        return this.savesState;
    }

    @Override
    public void saveConfig(IInterface inter, IPanelConfig config) {
        config.savePositon(this.position);
        if (this.state != null) {
            config.saveState(this.state.isOn());
        }
    }

    @Override
    public void loadConfig(IInterface inter, IPanelConfig config) {
        this.position = config.loadPosition();
        if (this.state != null && this.state.isOn() != config.loadState()) {
            this.state.toggle();
        }
    }

    @Override
    public String getConfigName() {
        return this.configName;
    }
}

