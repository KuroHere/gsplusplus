/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.gui;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.Announcer;

@Module.Declaration(name="HudEditor", category=Category.GUI, bind=25, drawn=false)
public class HUDEditor
extends Module {
    @Override
    public void onEnable() {
        GameSense.INSTANCE.gameSenseGUI.enterHUDEditor();
        Announcer announcer = ModuleManager.getModule(Announcer.class);
        if (((Boolean)announcer.clickGui.getValue()).booleanValue() && announcer.isEnabled() && HUDEditor.mc.field_71439_g != null) {
            if (((Boolean)announcer.clientSide.getValue()).booleanValue()) {
                MessageBus.sendClientPrefixMessage(Announcer.guiMessage);
            } else {
                MessageBus.sendServerMessage(Announcer.guiMessage);
            }
        }
        this.disable();
    }
}

