/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.container;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.Description;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.config.IConfigList;
import com.lukflug.panelstudio.config.IPanelConfig;
import com.lukflug.panelstudio.container.Container;
import com.lukflug.panelstudio.popup.IPopup;
import com.lukflug.panelstudio.popup.IPopupDisplayer;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class FixedContainer
extends Container<IFixedComponent>
implements IPopupDisplayer {
    protected boolean clip;
    protected List<PopupPair> popups = new ArrayList<PopupPair>();

    public FixedContainer(ILabeled label, IContainerRenderer renderer, boolean clip) {
        super(label, renderer);
        this.clip = clip;
    }

    @Override
    public void displayPopup(IPopup popup, Rectangle rect, IToggleable visible, IPopupPositioner positioner) {
        this.popups.add(new PopupPair(popup, rect, visible, positioner));
    }

    @Override
    public void render(Context context) {
        context.setHeight(this.getHeight());
        if (this.clip) {
            context.getInterface().window(context.getRect());
        }
        if (this.renderer != null) {
            this.renderer.renderBackground(context, context.hasFocus());
        }
        AtomicReference<Object> highest = new AtomicReference<Object>(null);
        AtomicReference<Object> first = new AtomicReference<Object>(null);
        this.doContextlessLoop((IFixedComponent component) -> {
            if (first.get() == null) {
                first.set(component);
            }
            Context subContext = this.getSubContext(context, (IFixedComponent)component, true, true);
            component.getHeight(subContext);
            if (subContext.isHovered() && highest.get() == null) {
                highest.set(component);
            }
        });
        AtomicBoolean highestReached = new AtomicBoolean(false);
        if (highest.get() == null) {
            highestReached.set(true);
        }
        AtomicReference<Object> focusComponent = new AtomicReference<Object>(null);
        super.doContextlessLoop((T component) -> {
            if (component == highest.get()) {
                highestReached.set(true);
            }
            Context subContext = this.getSubContext(context, (IFixedComponent)component, component == first.get(), highestReached.get());
            component.render(subContext);
            if (subContext.focusReleased()) {
                context.releaseFocus();
            } else if (subContext.foucsRequested()) {
                focusComponent.set(component);
                context.requestFocus();
            }
            if (subContext.isHovered() && subContext.getDescription() != null) {
                context.setDescription(new Description(subContext.getDescription(), subContext.getRect()));
            }
            for (PopupPair popup : this.popups) {
                popup.popup.setPosition(context.getInterface(), popup.rect, subContext.getRect(), popup.positioner);
                if (!popup.visible.isOn()) {
                    popup.visible.toggle();
                }
                if (!(popup.popup instanceof IFixedComponent)) continue;
                focusComponent.set(((IFixedComponent)popup.popup));
            }
            this.popups.clear();
        });
        if (focusComponent.get() != null && this.removeComponent(focusComponent.get())) {
            this.addComponent(focusComponent.get());
        }
        if (context.getDescription() == null && this.label.getDescription() != null) {
            context.setDescription(new Description(context.getRect(), this.label.getDescription()));
        }
        if (this.clip) {
            context.getInterface().restore();
        }
    }

    @Override
    protected void doContextlessLoop(Consumer<IFixedComponent> function) {
        ArrayList<Container.ComponentState> components = new ArrayList<Container.ComponentState>();
        for (Container.ComponentState state : this.components) {
            components.add(state);
        }
        for (Container.ComponentState state : components) {
            state.update();
        }
        for (int i = components.size() - 1; i >= 0; --i) {
            Container.ComponentState state;
            state = (Container.ComponentState)components.get(i);
            if (!state.lastVisible()) continue;
            function.accept((IFixedComponent)state.component);
        }
    }

    @Override
    protected void doContextSensitiveLoop(Context context, Container.ContextSensitiveConsumer<IFixedComponent> function) {
        Container.ComponentState focusState;
        context.setHeight(this.getHeight());
        AtomicBoolean highest = new AtomicBoolean(true);
        AtomicBoolean first = new AtomicBoolean(true);
        AtomicReference<Object> focusComponent = new AtomicReference<Object>(null);
        this.doContextlessLoop((IFixedComponent component) -> {
            Context subContext = this.getSubContext(context, (IFixedComponent)component, first.get(), highest.get());
            first.set(false);
            function.accept(subContext, (IFixedComponent)component);
            if (subContext.focusReleased()) {
                context.releaseFocus();
            } else if (subContext.foucsRequested()) {
                focusComponent.set(component);
                context.requestFocus();
            }
            if (subContext.isHovered()) {
                highest.set(false);
            }
            for (PopupPair popup : this.popups) {
                popup.popup.setPosition(context.getInterface(), popup.rect, subContext.getRect(), popup.positioner);
                if (!popup.visible.isOn()) {
                    popup.visible.toggle();
                }
                if (!(popup.popup instanceof IFixedComponent)) continue;
                focusComponent.set(((IFixedComponent)popup.popup));
            }
            this.popups.clear();
        });
        if (focusComponent.get() != null && (focusState = (Container.ComponentState)this.components.stream().filter(state -> state.component == focusComponent.get()).findFirst().orElse(null)) != null) {
            this.components.remove(focusState);
            this.components.add(focusState);
        }
    }

    protected Context getSubContext(Context context, IFixedComponent component, boolean focus, boolean highest) {
        Context subContext = new Context(context, component.getWidth(context.getInterface()), component.getPosition(context.getInterface()), context.hasFocus() && focus, highest);
        subContext.setPopupDisplayer(this);
        return subContext;
    }

    public void saveConfig(IInterface inter, IConfigList config) {
        config.begin(false);
        for (Container.ComponentState state : this.components) {
            IPanelConfig cf;
            if (!((IFixedComponent)state.component).savesState() || (cf = config.addPanel(((IFixedComponent)state.component).getConfigName())) == null) continue;
            ((IFixedComponent)state.component).saveConfig(inter, cf);
        }
        config.end(false);
    }

    public void loadConfig(IInterface inter, IConfigList config) {
        config.begin(true);
        for (Container.ComponentState state : this.components) {
            IPanelConfig cf;
            if (!((IFixedComponent)state.component).savesState() || (cf = config.getPanel(((IFixedComponent)state.component).getConfigName())) == null) continue;
            ((IFixedComponent)state.component).loadConfig(inter, cf);
        }
        config.end(true);
    }

    protected final class PopupPair {
        public final IPopup popup;
        public final Rectangle rect;
        public final IToggleable visible;
        public final IPopupPositioner positioner;

        public PopupPair(IPopup popup, Rectangle rect, IToggleable visible, IPopupPositioner positioner) {
            this.popup = popup;
            this.rect = rect;
            this.visible = visible;
            this.positioner = positioner;
        }
    }
}

