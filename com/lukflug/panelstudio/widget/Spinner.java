/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.HorizontalComponent;
import com.lukflug.panelstudio.container.HorizontalContainer;
import com.lukflug.panelstudio.container.VerticalContainer;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.INumberSetting;
import com.lukflug.panelstudio.setting.IStringSetting;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.Button;
import com.lukflug.panelstudio.widget.ITextFieldKeys;
import com.lukflug.panelstudio.widget.TextField;

public class Spinner
extends HorizontalContainer {
    public Spinner(final INumberSetting setting, ThemeTuple theme, boolean container, final boolean allowInput, ITextFieldKeys keys) {
        super(setting, new IContainerRenderer(){});
        final TextField textField = new TextField(new IStringSetting(){
            private String value = null;
            private long lastTime;

            @Override
            public String getDisplayName() {
                return setting.getDisplayName();
            }

            @Override
            public String getValue() {
                if (this.value != null && System.currentTimeMillis() - this.lastTime > 500L) {
                    double number;
                    if (this.value.isEmpty()) {
                        this.value = "0";
                    }
                    if (this.value.endsWith(".")) {
                        this.value = this.value + '0';
                    }
                    if ((number = Double.parseDouble(this.value)) > setting.getMaximumValue()) {
                        number = setting.getMaximumValue();
                    } else if (number < setting.getMinimumValue()) {
                        number = setting.getMinimumValue();
                    }
                    setting.setNumber(number);
                    this.value = null;
                }
                if (this.value == null) {
                    return setting.getSettingState();
                }
                return this.value;
            }

            @Override
            public void setValue(String string) {
                if (this.value == null) {
                    this.lastTime = System.currentTimeMillis();
                }
                this.value = new String(string);
            }
        }, keys, 0, new SimpleToggleable(false), theme.getTextRenderer(true, container)){

            @Override
            public boolean allowCharacter(char character) {
                if (!allowInput) {
                    return false;
                }
                return character >= '0' && character <= '9' || character == '.' && !this.setting.getSettingState().contains(".");
            }
        };
        this.addComponent(new HorizontalComponent<3>(textField, 0, 1));
        VerticalContainer buttons = new VerticalContainer(setting, new IContainerRenderer(){});
        buttons.addComponent(new Button<Void>((ILabeled)new Labeled(null, null, () -> true), () -> null, theme.getSmallButtonRenderer(6, container)){

            @Override
            public void handleButton(Context context, int button) {
                super.handleButton(context, button);
                if (button == 0 && context.isClicked(button)) {
                    double number = setting.getNumber();
                    if ((number += Math.pow(10.0, -setting.getPrecision())) <= setting.getMaximumValue()) {
                        setting.setNumber(number);
                    }
                }
            }

            @Override
            public int getHeight() {
                return textField.getHeight() / 2;
            }
        });
        buttons.addComponent(new Button<Void>((ILabeled)new Labeled(null, null, () -> true), () -> null, theme.getSmallButtonRenderer(7, container)){

            @Override
            public void handleButton(Context context, int button) {
                super.handleButton(context, button);
                if (button == 0 && context.isClicked(button)) {
                    double number = setting.getNumber();
                    if ((number -= Math.pow(10.0, -setting.getPrecision())) >= setting.getMinimumValue()) {
                        setting.setNumber(number);
                    }
                }
            }

            @Override
            public int getHeight() {
                return textField.getHeight() / 2;
            }
        });
        this.addComponent(new HorizontalComponent<VerticalContainer>(buttons, textField.getHeight(), 0));
    }
}

