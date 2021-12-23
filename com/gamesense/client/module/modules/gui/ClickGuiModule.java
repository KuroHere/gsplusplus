/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.gui;

import com.gamesense.api.setting.SettingsManager;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.Announcer;
import java.util.Arrays;
import java.util.function.Supplier;

@Module.Declaration(name="ClickGUI", category=Category.GUI, bind=24, drawn=false)
public class ClickGuiModule
extends Module {
    public IntegerSetting scrollSpeed = this.registerInteger("Scroll Speed", 10, 1, 20);
    public IntegerSetting animationSpeed = this.registerInteger("Animation Speed", 200, 0, 1000);
    public ModeSetting scrolling = this.registerMode("Scrolling", Arrays.asList("Screen", "Container"), "Screen");
    public BooleanSetting showHUD = this.registerBoolean("Show HUD Panels", false);
    public BooleanSetting csgoLayout = this.registerBoolean("CSGO Layout", false);
    public ModeSetting theme = this.registerMode("Skin", Arrays.asList("2.2", "2.1.2", "2.0"), "2.2");

    @Override
    public void onEnable() {
        GameSense.INSTANCE.gameSenseGUI.enterGUI();
        Announcer announcer = ModuleManager.getModule(Announcer.class);
        if (((Boolean)announcer.clickGui.getValue()).booleanValue() && announcer.isEnabled() && ClickGuiModule.mc.field_71439_g != null) {
            if (((Boolean)announcer.clientSide.getValue()).booleanValue()) {
                MessageBus.sendClientPrefixMessage(Announcer.guiMessage);
            } else {
                MessageBus.sendServerMessage(Announcer.guiMessage);
            }
        }
        this.disable();
    }

    public ColorSetting registerColor(String name, String configName, Supplier<Boolean> isVisible, boolean rainbow, boolean rainbowEnabled, boolean alphaEnabled, GSColor value) {
        ColorSetting setting = new ColorSetting(name, configName, this, isVisible, rainbow, rainbowEnabled, alphaEnabled, value);
        SettingsManager.addSetting(setting);
        return setting;
    }
}

