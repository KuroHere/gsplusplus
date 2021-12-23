/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.container;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.Description;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.component.ComponentBase;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.container.IContainer;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Container<T extends IComponent>
extends ComponentBase
implements IContainer<T> {
    protected List<ComponentState> components = new ArrayList<ComponentState>();
    protected IContainerRenderer renderer;
    private boolean visible;

    public Container(ILabeled label, IContainerRenderer renderer) {
        super(label);
        this.renderer = renderer;
    }

    @Override
    public boolean addComponent(T component) {
        if (this.getComponentState(component) == null) {
            this.components.add(new ComponentState(this, component, this.getDefaultVisibility()));
            return true;
        }
        return false;
    }

    @Override
    public boolean addComponent(T component, IBoolean visible) {
        if (this.getComponentState(component) == null) {
            this.components.add(new ComponentState(this, component, visible));
            return true;
        }
        return false;
    }

    @Override
    public boolean removeComponent(T component) {
        ComponentState state = this.getComponentState(component);
        if (state != null) {
            this.components.remove(state);
            if (state.lastVisible) {
                state.component.exit();
            }
            return true;
        }
        return false;
    }

    @Override
    public void render(Context context) {
        this.getHeight(context);
        if (this.renderer != null) {
            this.renderer.renderBackground(context, context.hasFocus());
        }
        this.doContextSensitiveLoop(context, (subContext, component) -> {
            component.render(subContext);
            if (subContext.isHovered() && subContext.getDescription() != null) {
                context.setDescription(new Description(subContext.getDescription(), subContext.getRect()));
            }
        });
        if (context.getDescription() == null && this.label.getDescription() != null) {
            context.setDescription(new Description(context.getRect(), this.label.getDescription()));
        }
    }

    @Override
    public void handleButton(Context context, int button) {
        this.doContextSensitiveLoop(context, (subContext, component) -> component.handleButton(subContext, button));
    }

    @Override
    public void handleKey(Context context, int scancode) {
        this.doContextSensitiveLoop(context, (subContext, component) -> component.handleKey(subContext, scancode));
    }

    @Override
    public void handleChar(Context context, char character) {
        this.doContextSensitiveLoop(context, (subContext, component) -> component.handleChar(subContext, character));
    }

    @Override
    public void handleScroll(Context context, int diff) {
        this.doContextSensitiveLoop(context, (subContext, component) -> component.handleScroll(subContext, diff));
    }

    @Override
    public void getHeight(Context context) {
        this.doContextSensitiveLoop(context, (subContext, component) -> component.getHeight(subContext));
    }

    @Override
    public void enter() {
        this.visible = true;
        this.doContextlessLoop(component -> {});
    }

    @Override
    public void exit() {
        this.visible = false;
        this.doContextlessLoop(component -> {});
    }

    @Override
    public void releaseFocus() {
        this.doContextlessLoop(IComponent::releaseFocus);
    }

    @Override
    protected int getHeight() {
        return 0;
    }

    protected ComponentState getComponentState(T component) {
        for (ComponentState state : this.components) {
            if (state.component != component) continue;
            return state;
        }
        return null;
    }

    protected void doContextlessLoop(Consumer<T> function) {
        ArrayList<ComponentState> components = new ArrayList<ComponentState>();
        for (ComponentState state : this.components) {
            components.add(state);
        }
        for (ComponentState state : components) {
            state.update();
        }
        for (ComponentState state : components) {
            if (!state.lastVisible()) continue;
            function.accept(state.component);
        }
    }

    protected abstract void doContextSensitiveLoop(Context var1, ContextSensitiveConsumer<T> var2);

    protected IBoolean getDefaultVisibility() {
        return () -> true;
    }

    @FunctionalInterface
    protected static interface ContextSensitiveConsumer<T extends IComponent> {
        public void accept(Context var1, T var2);
    }

    protected static final class ComponentState {
        public final T component;
        public final IBoolean externalVisibility;
        private boolean lastVisible = false;
        final /* synthetic */ Container this$0;

        public ComponentState(T component, IBoolean externalVisibility) {
            this.this$0 = this$0;
            this.component = component;
            this.externalVisibility = externalVisibility;
            this.update();
        }

        public void update() {
            if ((this.component.isVisible() && this.externalVisibility.isOn() && this.this$0.visible) != this.lastVisible) {
                if (this.lastVisible) {
                    this.lastVisible = false;
                    this.component.exit();
                } else {
                    this.lastVisible = true;
                    this.component.enter();
                }
            }
        }

        public boolean lastVisible() {
            return this.lastVisible;
        }
    }
}

