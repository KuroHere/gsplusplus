/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.AnimatedToggleable;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.component.ComponentProxy;
import com.lukflug.panelstudio.component.DraggableComponent;
import com.lukflug.panelstudio.component.FixedComponent;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.component.IFixedComponentProxy;
import com.lukflug.panelstudio.component.IResizable;
import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.config.IPanelConfig;
import com.lukflug.panelstudio.theme.IResizeBorderRenderer;
import com.lukflug.panelstudio.theme.RendererTuple;
import com.lukflug.panelstudio.widget.ClosableComponent;
import com.lukflug.panelstudio.widget.ScrollBarComponent;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.function.Supplier;

public class ResizableComponent<T extends IFixedComponent>
implements IFixedComponentProxy<T> {
    protected T component;
    protected IResizeBorderRenderer renderer;
    protected IResizable size;
    protected boolean[] resizing = new boolean[]{false, false, false, false};
    protected Point attachPoint = null;
    protected Rectangle attachRect = null;

    public ResizableComponent(T component, IResizeBorderRenderer renderer, IResizable size) {
        this.component = component;
        this.renderer = renderer;
        this.size = size;
    }

    @Override
    public void render(Context context) {
        IFixedComponentProxy.super.render(context);
        this.renderer.drawBorder(context, context.hasFocus());
    }

    @Override
    public void handleButton(Context context, int button) {
        IFixedComponentProxy.super.handleButton(context, button);
        if (button == 0 && context.isClicked(button)) {
            this.attachPoint = context.getInterface().getMouse();
            this.attachRect = new Rectangle(this.getComponent().getPosition(context.getInterface()), this.size.getSize());
            Rectangle r = context.getRect();
            if (new Rectangle(r.x, r.y, r.width, this.renderer.getBorder()).contains(this.attachPoint)) {
                this.resizing[0] = true;
            } else if (new Rectangle(r.x, r.y + r.height - this.renderer.getBorder(), r.width, this.renderer.getBorder()).contains(this.attachPoint)) {
                this.resizing[1] = true;
            }
            if (new Rectangle(r.x, r.y, this.renderer.getBorder(), r.height).contains(this.attachPoint)) {
                this.resizing[2] = true;
            } else if (new Rectangle(r.x + r.width - this.renderer.getBorder(), r.y, this.renderer.getBorder(), r.height).contains(this.attachPoint)) {
                this.resizing[3] = true;
            }
        } else if (!context.getInterface().getButton(0)) {
            this.resizing[0] = false;
            this.resizing[1] = false;
            this.resizing[2] = false;
            this.resizing[3] = false;
        }
    }

    @Override
    public int getHeight(int height) {
        return height + 2 * this.renderer.getBorder();
    }

    @Override
    public Context getContext(Context context) {
        if (this.resizing[0]) {
            this.getComponent().setPosition(context.getInterface(), new Point(this.getComponent().getPosition((IInterface)context.getInterface()).x, this.attachRect.y + context.getInterface().getMouse().y - this.attachPoint.y));
            this.size.setSize(new Dimension(this.size.getSize().width, this.attachRect.height - context.getInterface().getMouse().y + this.attachPoint.y));
        } else if (this.resizing[1]) {
            this.size.setSize(new Dimension(this.size.getSize().width, this.attachRect.height + context.getInterface().getMouse().y - this.attachPoint.y));
        }
        if (this.resizing[2]) {
            this.getComponent().setPosition(context.getInterface(), new Point(this.attachRect.x + context.getInterface().getMouse().x - this.attachPoint.x, this.getComponent().getPosition((IInterface)context.getInterface()).y));
            this.size.setSize(new Dimension(this.attachRect.width - context.getInterface().getMouse().x + this.attachPoint.x, this.size.getSize().height));
        } else if (this.resizing[3]) {
            this.size.setSize(new Dimension(this.attachRect.width + context.getInterface().getMouse().x - this.attachPoint.x, this.size.getSize().height));
        }
        return new Context(context, context.getSize().width - 2 * this.renderer.getBorder(), new Point(this.renderer.getBorder(), this.renderer.getBorder()), true, true);
    }

    @Override
    public Point getPosition(IInterface inter) {
        Point p = this.getComponent().getPosition(inter);
        p.translate(-this.renderer.getBorder(), -this.renderer.getBorder());
        return p;
    }

    @Override
    public void setPosition(IInterface inter, Point position) {
        position.translate(this.renderer.getBorder(), this.renderer.getBorder());
        this.getComponent().setPosition(inter, position);
    }

    @Override
    public int getWidth(IInterface inter) {
        return this.size.getSize().width + 2 * this.renderer.getBorder();
    }

    @Override
    public void saveConfig(IInterface inter, IPanelConfig config) {
        IFixedComponentProxy.super.saveConfig(inter, config);
        config.saveSize(this.size.getSize());
    }

    @Override
    public void loadConfig(IInterface inter, IPanelConfig config) {
        IFixedComponentProxy.super.loadConfig(inter, config);
        Dimension s = config.loadSize();
        if (s != null) {
            this.size.setSize(s);
        }
    }

    @Override
    public T getComponent() {
        return this.component;
    }

    public static <S extends IComponent, T extends IComponent, U> IFixedComponent createResizableComponent(S title, T content, Supplier<U> state, AnimatedToggleable open, RendererTuple<U> renderer, IResizeBorderRenderer resizeRenderer, IResizable size, IScrollSize scrollSize, Point position, int width, boolean savesState, String configName) {
        DraggableComponent<FixedComponent<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>>> draggable = ClosableComponent.createDraggableComponent(title, content, state, open, renderer, scrollSize, position, width, savesState, configName);
        if (size != null) {
            return new ResizableComponent<DraggableComponent<FixedComponent<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>>>>(draggable, resizeRenderer, size);
        }
        return draggable;
    }
}

