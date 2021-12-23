/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.tabgui;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.FixedComponent;
import com.lukflug.panelstudio.container.IContainer;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import com.lukflug.panelstudio.setting.ICategory;
import com.lukflug.panelstudio.setting.IClient;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.tabgui.ITabGUIRenderer;
import com.lukflug.panelstudio.tabgui.ITabGUITheme;
import com.lukflug.panelstudio.tabgui.Tab;
import com.lukflug.panelstudio.tabgui.TabItem;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TabGUI
extends TabItem<ChildTab, Void> {
    private final FixedComponent<TabGUI> fixedComponent;
    protected int width;
    protected IContainer<? super FixedComponent<Tab>> container;
    protected IPopupPositioner positioner;
    protected ITabGUIRenderer<Boolean> childRenderer;

    public TabGUI(ILabeled label, IClient client, ITabGUITheme theme, IContainer<? super FixedComponent<Tab>> container, Supplier<Animation> animation, IntPredicate up, IntPredicate down, IntPredicate enter, IntPredicate exit, Point position, String configName) {
        super(label, theme.getParentRenderer(), animation.get(), up, down, enter, exit);
        this.width = theme.getTabWidth();
        this.container = container;
        this.positioner = theme.getPositioner();
        this.childRenderer = theme.getChildRenderer();
        AtomicInteger i = new AtomicInteger(0);
        this.contents = client.getCategories().map(category -> new TabItem.ContentItem(category.getDisplayName(), new ChildTab((ICategory)category, (Animation)animation.get(), i.getAndIncrement()))).collect(Collectors.toList());
        this.fixedComponent = new FixedComponent<TabGUI>(this, position, this.width, null, true, configName);
    }

    public FixedComponent<TabGUI> getWrappedComponent() {
        return this.fixedComponent;
    }

    @Override
    protected boolean hasChildren() {
        for (TabItem.ContentItem tab : this.contents) {
            if (!((ChildTab)tab.content).visible.isOn()) continue;
            return true;
        }
        return false;
    }

    @Override
    protected void handleSelect(Context context) {
        ChildTab tab = (ChildTab)((TabItem.ContentItem)this.contents.get((int)((int)this.tabState.getTarget()))).content;
        if (!tab.visible.isOn()) {
            tab.visible.toggle();
        }
    }

    @Override
    protected void handleExit(Context context) {
        ChildTab tab = (ChildTab)((TabItem.ContentItem)this.contents.get((int)((int)this.tabState.getTarget()))).content;
        if (tab.visible.isOn()) {
            tab.visible.toggle();
        }
    }

    protected class ChildTab
    implements Supplier<Void> {
        public final FixedComponent<Tab> tab;
        public final IToggleable visible;

        public ChildTab(ICategory category, Animation animation, final int index) {
            this.tab = new FixedComponent<Tab>(new Tab(category, TabGUI.this.childRenderer, animation, TabGUI.this.up, TabGUI.this.down, TabGUI.this.enter), new Point(0, 0), TabGUI.this.width, null, false, category.getDisplayName()){

                @Override
                public Point getPosition(IInterface inter) {
                    Rectangle rect = new Rectangle(TabGUI.this.fixedComponent.getPosition(inter), new Dimension(this.width, TabGUI.this.getHeight()));
                    Dimension dim = new Dimension(this.width, ((Tab)this.component).getHeight());
                    return TabGUI.this.positioner.getPosition(inter, dim, TabGUI.this.renderer.getItemRect(inter, rect, TabGUI.this.contents.size(), index), rect);
                }
            };
            this.visible = new SimpleToggleable(false);
            TabGUI.this.container.addComponent(this.tab, this.visible);
        }

        @Override
        public Void get() {
            return null;
        }
    }
}

