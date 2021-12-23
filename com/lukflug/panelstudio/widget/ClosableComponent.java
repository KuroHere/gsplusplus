/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.AnimatedToggleable;
import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.ConstantToggleable;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.component.CollapsibleComponent;
import com.lukflug.panelstudio.component.ComponentProxy;
import com.lukflug.panelstudio.component.DraggableComponent;
import com.lukflug.panelstudio.component.FixedComponent;
import com.lukflug.panelstudio.component.FocusableComponentProxy;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.component.PopupComponent;
import com.lukflug.panelstudio.container.VerticalContainer;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.IPanelRenderer;
import com.lukflug.panelstudio.theme.RendererTuple;
import com.lukflug.panelstudio.widget.ScrollBarComponent;
import java.awt.Point;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class ClosableComponent<S extends IComponent, T extends IComponent>
extends FocusableComponentProxy<VerticalContainer> {
    protected final S title;
    protected final CollapsibleComponent<T> collapsible;
    protected final VerticalContainer container;

    public <U> ClosableComponent(S title, T content, final Supplier<U> state, final AnimatedToggleable open, final IPanelRenderer<U> panelRenderer, boolean focus) {
        super(focus);
        this.title = title;
        this.container = new VerticalContainer(new Labeled(content.getTitle(), null, () -> content.isVisible()), panelRenderer){

            @Override
            public void render(Context context) {
                super.render(context);
                panelRenderer.renderPanelOverlay(context, this.hasFocus(context), state.get(), open.isOn());
            }

            @Override
            protected boolean hasFocus(Context context) {
                return ClosableComponent.this.hasFocus(context);
            }
        };
        this.collapsible = new CollapsibleComponent<T>(open, (IComponent)content){
            final /* synthetic */ IComponent val$content;
            {
                this.val$content = iComponent;
                super(x0);
            }

            @Override
            public T getComponent() {
                return this.val$content;
            }
        };
        this.container.addComponent(new ComponentProxy<IComponent>(title){

            @Override
            public void render(Context context) {
                super.render(context);
                panelRenderer.renderTitleOverlay(context, ClosableComponent.this.hasFocus(context), state.get(), open.isOn());
            }

            @Override
            public void handleButton(Context context, int button) {
                super.handleButton(context, button);
                if (button == 1 && context.isClicked(button)) {
                    ClosableComponent.this.collapsible.getToggle().toggle();
                }
            }
        });
        this.container.addComponent(this.collapsible);
    }

    @Override
    public final VerticalContainer getComponent() {
        return this.container;
    }

    public IComponent getTitleBar() {
        return this.title;
    }

    public CollapsibleComponent<T> getCollapsible() {
        return this.collapsible;
    }

    public static <S extends IComponent, T extends IComponent, U> DraggableComponent<FixedComponent<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>>> createStaticPopup(S title, T content, Supplier<U> state, Animation animation, RendererTuple<U> renderer, IScrollSize popupSize, final IToggleable shown, final IntSupplier widthSupplier, final boolean savesState, final String configName, final boolean closeOnClick) {
        final AtomicReference<Object> panel = new AtomicReference<Object>(null);
        DraggableComponent draggable = new DraggableComponent<FixedComponent<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>>>(){
            FixedComponent<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>> fixedComponent = null;

            @Override
            public void handleButton(Context context, int button) {
                super.handleButton(context, button);
                if (context.getInterface().getButton(button) && (!context.isHovered() || closeOnClick) && shown.isOn()) {
                    shown.toggle();
                }
            }

            @Override
            public boolean isVisible() {
                return super.isVisible() && shown.isOn();
            }

            @Override
            public FixedComponent<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>> getComponent() {
                if (this.fixedComponent == null) {
                    this.fixedComponent = new FixedComponent<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>>((ClosableComponent)panel.get(), new Point(0, 0), widthSupplier.getAsInt(), ((ClosableComponent)panel.get()).getCollapsible().getToggle(), savesState, configName){

                        @Override
                        public int getWidth(IInterface inter) {
                            return widthSupplier.getAsInt();
                        }
                    };
                }
                return this.fixedComponent;
            }
        };
        panel.set(ClosableComponent.createScrollableComponent(draggable.getWrappedDragComponent(title), content, state, new AnimatedToggleable(new ConstantToggleable(true), animation), renderer, popupSize, true));
        return draggable;
    }

    public static <S extends IComponent, T extends IComponent, U> PopupComponent<ClosableComponent<S, ScrollBarComponent<U, T>>> createDynamicPopup(S title, T content, Supplier<U> state, Animation animation, RendererTuple<U> renderer, IScrollSize popupSize, final IToggleable shown, int width) {
        ClosableComponent<S, ScrollBarComponent<U, T>> panel = ClosableComponent.createScrollableComponent(title, content, state, new AnimatedToggleable(new ConstantToggleable(true), animation), renderer, popupSize, true);
        return new PopupComponent<ClosableComponent<S, ScrollBarComponent<U, T>>>(panel, width){

            @Override
            public void handleButton(Context context, int button) {
                this.doOperation(context, subContext -> ((ClosableComponent)this.getComponent()).handleButton((Context)subContext, button));
                if (context.getInterface().getButton(button) && !context.isHovered() && shown.isOn()) {
                    shown.toggle();
                }
            }

            @Override
            public boolean isVisible() {
                return ((ClosableComponent)this.getComponent()).isVisible() && shown.isOn();
            }
        };
    }

    public static <S extends IComponent, T extends IComponent, U> DraggableComponent<FixedComponent<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>>> createDraggableComponent(S title, T content, Supplier<U> state, AnimatedToggleable open, RendererTuple<U> renderer, IScrollSize scrollSize, Point position, int width, boolean savesState, String configName) {
        AtomicReference<Object> panel = new AtomicReference<Object>(null);
        DraggableComponent<FixedComponent<ClosableComponent<ComponentProxy<S>, ScrollBarComponent<U, T>>>> draggable = ClosableComponent.createDraggableComponent(() -> (ClosableComponent)panel.get(), position, width, savesState, configName);
        panel.set(ClosableComponent.createScrollableComponent(draggable.getWrappedDragComponent(title), content, state, open, renderer, scrollSize, false));
        return draggable;
    }

    public static <S extends IComponent, T extends IComponent, U> DraggableComponent<FixedComponent<ClosableComponent<S, T>>> createDraggableComponent(final Supplier<ClosableComponent<S, T>> panel, final Point position, final int width, final boolean savesState, final String configName) {
        return new DraggableComponent<FixedComponent<ClosableComponent<S, T>>>(){
            FixedComponent<ClosableComponent<S, T>> fixedComponent = null;

            @Override
            public FixedComponent<ClosableComponent<S, T>> getComponent() {
                if (this.fixedComponent == null) {
                    this.fixedComponent = new FixedComponent<IComponent>((IComponent)panel.get(), position, width, ((ClosableComponent)panel.get()).getCollapsible().getToggle(), savesState, configName);
                }
                return this.fixedComponent;
            }
        };
    }

    public static <S extends IComponent, T extends IComponent, U> ClosableComponent<S, ScrollBarComponent<U, T>> createScrollableComponent(S title, T content, final Supplier<U> state, AnimatedToggleable open, RendererTuple<U> renderer, final IScrollSize scrollSize, boolean focus) {
        return new ClosableComponent<S, 7>(title, new ScrollBarComponent<U, T>(content, renderer.scrollRenderer, renderer.cornerRenderer, renderer.emptyRenderer){

            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return scrollSize.getScrollHeight(context, componentHeight);
            }

            @Override
            public int getComponentWidth(Context context) {
                return scrollSize.getComponentWidth(context);
            }

            @Override
            protected U getState() {
                return state.get();
            }
        }, state, open, renderer.panelRenderer, focus);
    }
}

