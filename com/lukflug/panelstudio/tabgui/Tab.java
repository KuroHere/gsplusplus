/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.tabgui;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.setting.ICategory;
import com.lukflug.panelstudio.tabgui.ITabGUIRenderer;
import com.lukflug.panelstudio.tabgui.TabItem;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

public class Tab
extends TabItem<IToggleable, Boolean> {
    public Tab(ICategory category, ITabGUIRenderer<Boolean> renderer, Animation animation, IntPredicate up, IntPredicate down, IntPredicate enter) {
        super(category, renderer, animation, up, down, enter, key -> false);
        this.contents = category.getModules().map(module -> new TabItem.ContentItem(module.getDisplayName(), module.isEnabled())).collect(Collectors.toList());
    }

    @Override
    protected void handleSelect(Context context) {
        ((IToggleable)((TabItem.ContentItem)this.contents.get((int)((int)this.tabState.getTarget()))).content).toggle();
    }

    @Override
    protected void handleExit(Context context) {
    }
}

