/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.layout;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.layout.IComponentAdder;
import com.lukflug.panelstudio.setting.IBooleanSetting;
import com.lukflug.panelstudio.setting.IColorSetting;
import com.lukflug.panelstudio.setting.IEnumSetting;
import com.lukflug.panelstudio.setting.IKeybindSetting;
import com.lukflug.panelstudio.setting.INumberSetting;
import com.lukflug.panelstudio.setting.ISetting;
import com.lukflug.panelstudio.setting.IStringSetting;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.Button;
import com.lukflug.panelstudio.widget.ColorSliderComponent;
import com.lukflug.panelstudio.widget.CycleButton;
import com.lukflug.panelstudio.widget.ITextFieldKeys;
import com.lukflug.panelstudio.widget.KeybindComponent;
import com.lukflug.panelstudio.widget.NumberSlider;
import com.lukflug.panelstudio.widget.TextField;
import com.lukflug.panelstudio.widget.ToggleButton;
import java.util.function.Supplier;

public interface IComponentGenerator {
    default public IComponent getComponent(ISetting<?> setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
        if (setting instanceof IBooleanSetting) {
            return this.getBooleanComponent((IBooleanSetting)setting, animation, adder, theme, colorLevel, isContainer);
        }
        if (setting instanceof INumberSetting) {
            return this.getNumberComponent((INumberSetting)setting, animation, adder, theme, colorLevel, isContainer);
        }
        if (setting instanceof IEnumSetting) {
            return this.getEnumComponent((IEnumSetting)setting, animation, adder, theme, colorLevel, isContainer);
        }
        if (setting instanceof IColorSetting) {
            return this.getColorComponent((IColorSetting)setting, animation, adder, theme, colorLevel, isContainer);
        }
        if (setting instanceof IKeybindSetting) {
            return this.getKeybindComponent((IKeybindSetting)setting, animation, adder, theme, colorLevel, isContainer);
        }
        if (setting instanceof IStringSetting) {
            return this.getStringComponent((IStringSetting)setting, animation, adder, theme, colorLevel, isContainer);
        }
        return new Button<Void>(setting, () -> null, theme.getButtonRenderer(Void.class, isContainer));
    }

    default public IComponent getBooleanComponent(IBooleanSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
        return new ToggleButton(setting, theme.getButtonRenderer(Boolean.class, isContainer));
    }

    default public IComponent getNumberComponent(INumberSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
        return new NumberSlider(setting, theme.getSliderRenderer(isContainer));
    }

    default public IComponent getEnumComponent(IEnumSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
        return new CycleButton(setting, theme.getButtonRenderer(String.class, isContainer));
    }

    default public IComponent getColorComponent(IColorSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
        return new ColorSliderComponent(setting, new ThemeTuple(theme.theme, theme.logicalLevel, colorLevel));
    }

    default public IComponent getKeybindComponent(IKeybindSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
        return new KeybindComponent(setting, theme.getKeybindRenderer(isContainer));
    }

    default public IComponent getStringComponent(IStringSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
        return new TextField(setting, new ITextFieldKeys(){

            @Override
            public boolean isBackspaceKey(int scancode) {
                return false;
            }

            @Override
            public boolean isDeleteKey(int scancode) {
                return false;
            }

            @Override
            public boolean isInsertKey(int scancode) {
                return false;
            }

            @Override
            public boolean isLeftKey(int scancode) {
                return false;
            }

            @Override
            public boolean isRightKey(int scancode) {
                return false;
            }

            @Override
            public boolean isHomeKey(int scancode) {
                return false;
            }

            @Override
            public boolean isEndKey(int scancode) {
                return false;
            }

            @Override
            public boolean isCopyKey(int scancode) {
                return false;
            }

            @Override
            public boolean isPasteKey(int scancode) {
                return false;
            }

            @Override
            public boolean isCutKey(int scancode) {
                return false;
            }

            @Override
            public boolean isAllKey(int scancode) {
                return false;
            }
        }, 0, new SimpleToggleable(false), theme.getTextRenderer(false, isContainer)){

            @Override
            public boolean allowCharacter(char character) {
                return false;
            }
        };
    }
}

