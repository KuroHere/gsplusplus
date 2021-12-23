/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.ComponentProxy;
import com.lukflug.panelstudio.component.DraggableComponent;
import com.lukflug.panelstudio.component.FixedComponent;
import com.lukflug.panelstudio.component.HorizontalComponent;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.container.HorizontalContainer;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import com.lukflug.panelstudio.setting.IEnumSetting;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.IStringSetting;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import com.lukflug.panelstudio.theme.RendererTuple;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.Button;
import com.lukflug.panelstudio.widget.ClosableComponent;
import com.lukflug.panelstudio.widget.ITextFieldKeys;
import com.lukflug.panelstudio.widget.RadioButton;
import com.lukflug.panelstudio.widget.ScrollBarComponent;
import com.lukflug.panelstudio.widget.TextField;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public abstract class DropDownList
extends HorizontalContainer {
    private Rectangle rect = new Rectangle();
    private boolean transferFocus = false;
    protected IToggleable toggle = new SimpleToggleable(false);

    public DropDownList(final IEnumSetting setting, ThemeTuple theme, boolean container, final boolean allowSearch, ITextFieldKeys keys, IScrollSize popupSize, Consumer<IFixedComponent> popupAdder) {
        super(setting, new IContainerRenderer(){});
        final AtomicReference<Object> searchTerm = new AtomicReference<Object>(null);
        final TextField textField = new TextField(new IStringSetting(){

            @Override
            public String getDisplayName() {
                return setting.getDisplayName();
            }

            @Override
            public String getValue() {
                String returnValue = allowSearch && DropDownList.this.toggle.isOn() ? (String)searchTerm.get() : setting.getValueName();
                searchTerm.set(returnValue);
                return returnValue;
            }

            @Override
            public void setValue(String string) {
                searchTerm.set(string);
            }
        }, keys, 0, new SimpleToggleable(false), theme.getTextRenderer(true, container)){

            @Override
            public void handleButton(Context context, int button) {
                super.handleButton(context, button);
                DropDownList.this.rect = this.renderer.getTextArea(context, this.getTitle());
                if (button == 0 && context.isClicked(button)) {
                    DropDownList.this.transferFocus = true;
                }
            }

            @Override
            public boolean hasFocus(Context context) {
                return super.hasFocus(context) || DropDownList.this.toggle.isOn();
            }

            @Override
            public boolean allowCharacter(char character) {
                return DropDownList.this.allowCharacter(character);
            }
        };
        this.addComponent(new HorizontalComponent<3>(textField, 0, 1));
        ThemeTuple popupTheme = new ThemeTuple(theme.theme, theme.logicalLevel, 0);
        Button<Void> title = new Button<Void>(new Labeled("", null, () -> false), () -> null, popupTheme.getButtonRenderer(Void.class, false));
        RadioButton content = new RadioButton(new IEnumSetting(){
            ILabeled[] values;
            {
                this.values = (ILabeled[])Arrays.stream(setting.getAllowedValues()).map(value -> new Labeled(value.getDisplayName(), value.getDescription(), () -> {
                    if (!value.isVisible().isOn()) {
                        return false;
                    }
                    if (!allowSearch) {
                        return true;
                    }
                    return value.getDisplayName().toUpperCase().contains(((String)searchTerm.get()).toUpperCase());
                })).toArray(ILabeled[]::new);
            }

            @Override
            public String getDisplayName() {
                return setting.getDisplayName();
            }

            @Override
            public String getDescription() {
                return setting.getDescription();
            }

            @Override
            public IBoolean isVisible() {
                return setting.isVisible();
            }

            @Override
            public void increment() {
                setting.increment();
            }

            @Override
            public void decrement() {
                setting.decrement();
            }

            @Override
            public String getValueName() {
                return setting.getValueName();
            }

            @Override
            public void setValueIndex(int index) {
                setting.setValueIndex(index);
            }

            @Override
            public ILabeled[] getAllowedValues() {
                return this.values;
            }
        }, popupTheme.getRadioRenderer(false), this.getAnimation(), false){

            @Override
            protected boolean isUpKey(int key) {
                return DropDownList.this.isUpKey(key);
            }

            @Override
            protected boolean isDownKey(int key) {
                return DropDownList.this.isDownKey(key);
            }
        };
        final DraggableComponent<FixedComponent<ClosableComponent<ComponentProxy<Button<Void>>, ScrollBarComponent<Void, 5>>>> popup = ClosableComponent.createStaticPopup(title, content, () -> null, this.getAnimation(), new RendererTuple<Void>(Void.class, popupTheme), popupSize, this.toggle, () -> this.rect.width, false, "", true);
        popupAdder.accept(popup);
        final IPopupPositioner positioner = new IPopupPositioner(){

            @Override
            public Point getPosition(IInterface inter, Dimension popup, Rectangle component, Rectangle panel) {
                return new Point(component.x, component.y + component.height);
            }
        };
        Button<Void> button = new Button<Void>((ILabeled)new Labeled(null, null, () -> true), () -> null, theme.getSmallButtonRenderer(7, container)){

            @Override
            public void handleButton(Context context, int button) {
                super.handleButton(context, button);
                DropDownList.this.rect = new Rectangle(((DropDownList)DropDownList.this).rect.x, context.getPos().y, context.getPos().x + context.getSize().width - ((DropDownList)DropDownList.this).rect.x, context.getSize().height);
                if (button == 0 && context.isClicked(button) || DropDownList.this.transferFocus) {
                    context.getPopupDisplayer().displayPopup(popup, DropDownList.this.rect, DropDownList.this.toggle, positioner);
                    DropDownList.this.transferFocus = false;
                }
            }

            @Override
            public int getHeight() {
                return textField.getHeight();
            }
        };
        this.addComponent(new HorizontalComponent<7>(button, textField.getHeight(), 0));
    }

    @Override
    public void handleKey(Context context, int scancode) {
        super.handleKey(context, scancode);
        if (this.toggle.isOn() && this.isEnterKey(scancode)) {
            this.toggle.toggle();
        }
    }

    protected abstract Animation getAnimation();

    public abstract boolean allowCharacter(char var1);

    protected abstract boolean isUpKey(int var1);

    protected abstract boolean isDownKey(int var1);

    protected abstract boolean isEnterKey(int var1);
}

