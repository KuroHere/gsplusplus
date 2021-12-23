/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.component.HorizontalComponent;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.component.ScrollableComponent;
import com.lukflug.panelstudio.container.HorizontalContainer;
import com.lukflug.panelstudio.container.VerticalContainer;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import com.lukflug.panelstudio.theme.IEmptySpaceRenderer;
import com.lukflug.panelstudio.theme.IScrollBarRenderer;
import com.lukflug.panelstudio.widget.EmptySpace;
import com.lukflug.panelstudio.widget.ScrollBar;
import java.awt.Rectangle;

public abstract class ScrollBarComponent<S, T extends IComponent>
extends HorizontalContainer
implements IScrollSize {
    protected final T component;

    public ScrollBarComponent(T component, final IScrollBarRenderer<S> renderer, IEmptySpaceRenderer<S> cornerRenderer, IEmptySpaceRenderer<S> emptyRenderer) {
        super(new Labeled(component.getTitle(), null, () -> component.isVisible()), new IContainerRenderer(){});
        this.component = component;
        final ScrollableComponent scrollComponent = new ScrollableComponent<T>((IComponent)component, emptyRenderer){
            final /* synthetic */ IComponent val$component;
            final /* synthetic */ IEmptySpaceRenderer val$emptyRenderer;
            {
                this.val$component = iComponent;
                this.val$emptyRenderer = iEmptySpaceRenderer;
            }

            @Override
            public T getComponent() {
                return this.val$component;
            }

            @Override
            public int getScrollHeight(Context context, int height) {
                return ScrollBarComponent.this.getScrollHeight(context, height);
            }

            @Override
            public int getComponentWidth(Context context) {
                return ScrollBarComponent.this.getComponentWidth(context);
            }

            @Override
            public void fillEmptySpace(Context context, Rectangle rect) {
                Context subContext = new Context(context.getInterface(), rect.width, rect.getLocation(), context.hasFocus(), context.onTop());
                subContext.setHeight(rect.height);
                this.val$emptyRenderer.renderSpace(subContext, context.hasFocus(), ScrollBarComponent.this.getState());
            }
        };
        ScrollBar verticalBar = new ScrollBar<S>(new Labeled(component.getTitle(), null, () -> scrollComponent.isScrollingY()), false, renderer){

            @Override
            protected int getLength() {
                return scrollComponent.getScrollSize().height;
            }

            @Override
            protected int getContentHeight() {
                return scrollComponent.getContentSize().height;
            }

            @Override
            protected int getScrollPosition() {
                return scrollComponent.getScrollPos().y;
            }

            @Override
            protected void setScrollPosition(int position) {
                scrollComponent.setScrollPosY(position);
            }

            @Override
            protected S getState() {
                return ScrollBarComponent.this.getState();
            }
        };
        ScrollBar horizontalBar = new ScrollBar<S>(new Labeled(component.getTitle(), null, () -> scrollComponent.isScrollingX()), true, renderer){

            @Override
            protected int getLength() {
                return scrollComponent.getScrollSize().width;
            }

            @Override
            protected int getContentHeight() {
                return scrollComponent.getContentSize().width;
            }

            @Override
            protected int getScrollPosition() {
                return scrollComponent.getScrollPos().x;
            }

            @Override
            protected void setScrollPosition(int position) {
                scrollComponent.setScrollPosX(position);
            }

            @Override
            protected S getState() {
                return ScrollBarComponent.this.getState();
            }
        };
        VerticalContainer leftContainer = new VerticalContainer(new Labeled(component.getTitle(), null, () -> true), new IContainerRenderer(){});
        leftContainer.addComponent(scrollComponent);
        leftContainer.addComponent(horizontalBar);
        VerticalContainer rightContainer = new VerticalContainer(new Labeled(component.getTitle(), null, () -> true), new IContainerRenderer(){});
        rightContainer.addComponent(verticalBar);
        rightContainer.addComponent(new EmptySpace<S>(new Labeled("Empty", null, () -> scrollComponent.isScrollingX() && scrollComponent.isScrollingY()), () -> renderer.getThickness(), cornerRenderer){

            @Override
            protected S getState() {
                return ScrollBarComponent.this.getState();
            }
        });
        this.addComponent(new HorizontalComponent<VerticalContainer>(leftContainer, 0, 1));
        this.addComponent(new HorizontalComponent<VerticalContainer>(rightContainer, 0, 0){

            @Override
            public int getWidth(IInterface inter) {
                return renderer.getThickness();
            }
        }, () -> scrollComponent.isScrollingY());
    }

    public T getContentComponent() {
        return this.component;
    }

    protected abstract S getState();
}

