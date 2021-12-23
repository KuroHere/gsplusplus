/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.component.FocusableComponent;
import com.lukflug.panelstudio.setting.IKeybindSetting;
import com.lukflug.panelstudio.theme.IButtonRenderer;

public class KeybindComponent
extends FocusableComponent {
    protected IKeybindSetting keybind;
    protected IButtonRenderer<String> renderer;

    public KeybindComponent(IKeybindSetting keybind, IButtonRenderer<String> renderer) {
        super(keybind);
        this.keybind = keybind;
        this.renderer = renderer;
    }

    @Override
    public void render(Context context) {
        super.render(context);
        this.renderer.renderButton(context, this.getTitle(), this.hasFocus(context), this.keybind.getKeyName());
    }

    @Override
    public void handleKey(Context context, int scancode) {
        super.handleKey(context, scancode);
        if (this.hasFocus(context)) {
            this.keybind.setKey(this.transformKey(scancode));
            this.releaseFocus();
        }
    }

    @Override
    public void exit() {
        super.exit();
        this.releaseFocus();
    }

    @Override
    protected int getHeight() {
        return this.renderer.getDefaultHeight();
    }

    protected int transformKey(int scancode) {
        return scancode;
    }
}

