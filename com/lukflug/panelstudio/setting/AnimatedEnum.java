/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.setting;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.setting.IEnumSetting;

public final class AnimatedEnum {
    private final IEnumSetting setting;
    private final Animation animation;

    public AnimatedEnum(IEnumSetting setting, Animation animation) {
        this.setting = setting;
        this.animation = animation;
    }

    public double getValue() {
        int index = this.setting.getValueIndex();
        if (this.animation.getTarget() != (double)index) {
            this.animation.setValue(index);
        }
        return this.animation.getValue();
    }
}

