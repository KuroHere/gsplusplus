/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.gui;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.ColorUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Arrays;
import net.minecraft.util.text.TextFormatting;

@Module.Declaration(name="Colors", category=Category.GUI, drawn=false)
public class ColorMain
extends Module {
    public ColorSetting enabledColor = this.registerColor("Main Color", new GSColor(255, 0, 0, 255));
    public BooleanSetting customFont = this.registerBoolean("Custom Font", true);
    public BooleanSetting textFont = this.registerBoolean("Custom Text", false);
    public ModeSetting friendColor = this.registerMode("Friend Color", ColorUtil.colors, "Blue");
    public ModeSetting enemyColor = this.registerMode("Enemy Color", ColorUtil.colors, "Red");
    public ModeSetting chatEnableColor = this.registerMode("Msg Enbl", ColorUtil.colors, "Green");
    public ModeSetting chatDisableColor = this.registerMode("Msg Dsbl", ColorUtil.colors, "Red");
    public ModeSetting colorModel = this.registerMode("Color Model", Arrays.asList("RGB", "HSB"), "HSB");

    @Override
    public void onEnable() {
        this.disable();
    }

    public TextFormatting getFriendColor() {
        return ColorUtil.settingToTextFormatting(this.friendColor);
    }

    public TextFormatting getEnemyColor() {
        return ColorUtil.settingToTextFormatting(this.enemyColor);
    }

    public TextFormatting getEnabledColor() {
        return ColorUtil.settingToTextFormatting(this.chatEnableColor);
    }

    public TextFormatting getDisabledColor() {
        return ColorUtil.settingToTextFormatting(this.chatDisableColor);
    }

    public GSColor getFriendGSColor() {
        return new GSColor(ColorUtil.settingToColor(this.friendColor));
    }

    public GSColor getEnemyGSColor() {
        return new GSColor(ColorUtil.settingToColor(this.enemyColor));
    }
}

