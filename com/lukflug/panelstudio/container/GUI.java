/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.container;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.config.IConfigList;
import com.lukflug.panelstudio.container.FixedContainer;
import com.lukflug.panelstudio.container.IContainer;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import com.lukflug.panelstudio.theme.IDescriptionRenderer;
import java.awt.Point;

public class GUI
implements IContainer<IFixedComponent> {
    protected FixedContainer container;
    protected IInterface inter;
    protected IDescriptionRenderer descriptionRenderer;
    protected IPopupPositioner descriptionPosition;

    public GUI(IInterface inter, IDescriptionRenderer descriptionRenderer, IPopupPositioner descriptionPosition) {
        this.inter = inter;
        this.descriptionRenderer = descriptionRenderer;
        this.descriptionPosition = descriptionPosition;
        this.container = new FixedContainer(() -> "GUI", null, false);
    }

    @Override
    public boolean addComponent(IFixedComponent component) {
        return this.container.addComponent(component);
    }

    @Override
    public boolean addComponent(IFixedComponent component, IBoolean visible) {
        return this.container.addComponent(component, visible);
    }

    @Override
    public boolean removeComponent(IFixedComponent component) {
        return this.container.removeComponent(component);
    }

    public void render() {
        Context context = this.getContext();
        this.container.render(context);
        if (context.getDescription() != null) {
            Point pos = this.descriptionPosition.getPosition(this.inter, null, context.getDescription().getComponentPos(), context.getDescription().getPanelPos());
            this.descriptionRenderer.renderDescription(this.inter, pos, context.getDescription().getContent());
        }
    }

    public void handleButton(int button) {
        this.container.handleButton(this.getContext(), button);
    }

    public void handleKey(int scancode) {
        this.container.handleKey(this.getContext(), scancode);
    }

    public void handleChar(char character) {
        this.container.handleChar(this.getContext(), character);
    }

    public void handleScroll(int diff) {
        this.container.handleScroll(this.getContext(), diff);
    }

    public void enter() {
        this.container.enter();
    }

    public void exit() {
        this.container.exit();
    }

    public void saveConfig(IConfigList config) {
        this.container.saveConfig(this.inter, config);
    }

    public void loadConfig(IConfigList config) {
        this.container.loadConfig(this.inter, config);
    }

    protected Context getContext() {
        return new Context(this.inter, 0, new Point(0, 0), true, true);
    }
}

