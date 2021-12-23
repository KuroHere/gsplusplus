/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.hud;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.config.IPanelConfig;
import com.lukflug.panelstudio.hud.HUDComponent;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.setting.ILabeled;
import java.awt.Dimension;
import java.awt.Point;

public class ListComponent
extends HUDComponent {
    protected HUDList list;
    protected boolean lastUp = false;
    protected boolean lastRight = false;
    protected int height;
    protected int border;

    public ListComponent(ILabeled label, Point position, String configName, HUDList list, int height, int border) {
        super(label, position, configName);
        this.list = list;
        this.height = height;
        this.border = border;
    }

    @Override
    public void render(Context context) {
        super.render(context);
        for (int i = 0; i < this.list.getSize(); ++i) {
            String s = this.list.getItem(i);
            Point p = context.getPos();
            if (this.list.sortUp()) {
                p.translate(0, (this.height + this.border) * (this.list.getSize() - 1 - i));
            } else {
                p.translate(0, i * (this.height + this.border));
            }
            if (this.list.sortRight()) {
                p.translate(this.getWidth(context.getInterface()) - context.getInterface().getFontWidth(this.height, s), 0);
            }
            context.getInterface().drawString(p, this.height, s, this.list.getItemColor(i));
        }
    }

    @Override
    public Point getPosition(IInterface inter) {
        Dimension size = this.getSize(inter);
        if (this.lastUp != this.list.sortUp()) {
            if (this.list.sortUp()) {
                this.position.translate(0, size.height);
            } else {
                this.position.translate(0, -size.height);
            }
            this.lastUp = this.list.sortUp();
        }
        if (this.lastRight != this.list.sortRight()) {
            if (this.list.sortRight()) {
                this.position.translate(size.width, 0);
            } else {
                this.position.translate(-size.width, 0);
            }
            this.lastRight = this.list.sortRight();
        }
        if (this.list.sortUp()) {
            if (this.list.sortRight()) {
                return new Point(this.position.x - size.width, this.position.y - size.height);
            }
            return new Point(this.position.x, this.position.y - size.height);
        }
        if (this.list.sortRight()) {
            return new Point(new Point(this.position.x - size.width, this.position.y));
        }
        return new Point(this.position);
    }

    @Override
    public void setPosition(IInterface inter, Point position) {
        Dimension size = this.getSize(inter);
        this.position = this.list.sortUp() ? (this.list.sortRight() ? new Point(position.x + size.width, position.y + size.height) : new Point(position.x, position.y + size.height)) : (this.list.sortRight() ? new Point(position.x + size.width, position.y) : new Point(position));
    }

    @Override
    public void loadConfig(IInterface inter, IPanelConfig config) {
        super.loadConfig(inter, config);
        this.lastUp = this.list.sortUp();
        this.lastRight = this.list.sortRight();
    }

    @Override
    public Dimension getSize(IInterface inter) {
        int width = inter.getFontWidth(this.height, this.getTitle());
        for (int i = 0; i < this.list.getSize(); ++i) {
            String s = this.list.getItem(i);
            width = Math.max(width, inter.getFontWidth(this.height, s));
        }
        int height = (this.height + this.border) * this.list.getSize() - this.border;
        if (height < 0) {
            height = 0;
        }
        return new Dimension(width + 2 * this.border, height);
    }
}

