/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.container.VerticalContainer;
import com.lukflug.panelstudio.setting.IEnumSetting;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.IStringSetting;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.ITextFieldKeys;
import com.lukflug.panelstudio.widget.RadioButton;
import com.lukflug.panelstudio.widget.TextField;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public abstract class SearchableRadioButton
extends VerticalContainer {
    protected boolean transferFocus = false;

    public SearchableRadioButton(final IEnumSetting setting, ThemeTuple theme, boolean container, ITextFieldKeys keys) {
        super(setting, new IContainerRenderer(){});
        final AtomicReference<String> searchTerm = new AtomicReference<String>("");
        TextField textField = new TextField(new IStringSetting(){

            @Override
            public String getDisplayName() {
                return setting.getDisplayName();
            }

            @Override
            public String getValue() {
                return (String)searchTerm.get();
            }

            @Override
            public void setValue(String string) {
                searchTerm.set(string);
            }
        }, keys, 0, new SimpleToggleable(false), theme.getTextRenderer(true, container)){

            @Override
            public void handleButton(Context context, int button) {
                super.handleButton(context, button);
                if (this.hasFocus(context)) {
                    SearchableRadioButton.this.transferFocus = true;
                }
            }

            @Override
            public boolean allowCharacter(char character) {
                return SearchableRadioButton.this.allowCharacter(character);
            }
        };
        this.addComponent(textField);
        RadioButton content = new RadioButton(new IEnumSetting(){
            ILabeled[] values;
            {
                this.values = (ILabeled[])Arrays.stream(setting.getAllowedValues()).map(value -> new Labeled(value.getDisplayName(), value.getDescription(), () -> {
                    if (!value.isVisible().isOn()) {
                        return false;
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
        }, theme.getRadioRenderer(container), this.getAnimation(), false){

            @Override
            protected boolean isUpKey(int key) {
                return SearchableRadioButton.this.isUpKey(key);
            }

            @Override
            protected boolean isDownKey(int key) {
                return SearchableRadioButton.this.isDownKey(key);
            }
        };
        this.addComponent(content);
    }

    protected abstract Animation getAnimation();

    public abstract boolean allowCharacter(char var1);

    protected abstract boolean isUpKey(int var1);

    protected abstract boolean isDownKey(int var1);
}

