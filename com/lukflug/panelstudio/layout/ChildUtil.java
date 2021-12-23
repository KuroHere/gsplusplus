/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.layout;

import com.lukflug.panelstudio.base.AnimatedToggleable;
import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.ComponentProxy;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.container.VerticalContainer;
import com.lukflug.panelstudio.layout.IComponentAdder;
import com.lukflug.panelstudio.popup.PopupTuple;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.RendererTuple;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.Button;
import com.lukflug.panelstudio.widget.ClosableComponent;
import java.util.function.Supplier;

public class ChildUtil {
    protected int width;
    protected Supplier<Animation> animation;
    protected PopupTuple popupType;

    public ChildUtil(int width, Supplier<Animation> animation, PopupTuple popupType) {
        this.width = width;
        this.animation = animation;
        this.popupType = popupType;
    }

    protected <T> void addContainer(ILabeled label, IComponent title, IComponent container, Supplier<T> state, Class<T> stateClass, VerticalContainer parent, IComponentAdder gui, ThemeTuple theme, ChildMode mode) {
        boolean drawTitle = mode == ChildMode.DRAG_POPUP;
        switch (mode) {
            case DOWN: {
                parent.addComponent(new ClosableComponent<IComponent, IComponent>(title, container, state, new AnimatedToggleable(new SimpleToggleable(false), this.animation.get()), theme.getPanelRenderer(stateClass), false));
                break;
            }
            case POPUP: 
            case DRAG_POPUP: {
                final SimpleToggleable toggle = new SimpleToggleable(false);
                Button<T> button = new Button<T>(new Labeled(label.getDisplayName(), label.getDescription(), () -> drawTitle && label.isVisible().isOn()), state, theme.getButtonRenderer(stateClass, true));
                final IFixedComponent popup = this.popupType.dynamicPopup ? ClosableComponent.createDynamicPopup(button, container, state, this.animation.get(), new RendererTuple<T>(stateClass, theme), this.popupType.popupSize, toggle, this.width) : ClosableComponent.createStaticPopup(button, container, state, this.animation.get(), new RendererTuple<T>(stateClass, theme), this.popupType.popupSize, toggle, () -> this.width, false, "", false);
                parent.addComponent(new ComponentProxy<IComponent>(title){

                    @Override
                    public void handleButton(Context context, int button) {
                        super.handleButton(context, button);
                        if (button == 1 && context.isClicked(button)) {
                            context.getPopupDisplayer().displayPopup(popup, context.getRect(), toggle, ChildUtil.this.popupType.popupPos);
                            context.releaseFocus();
                        }
                    }
                });
                gui.addPopup(popup);
            }
        }
    }

    public static enum ChildMode {
        DOWN,
        POPUP,
        DRAG_POPUP;

    }
}

