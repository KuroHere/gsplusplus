/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;

@Command.Declaration(name="DisableAll", syntax="disableall", alias={"disableall", "stop"})
public class DisableAllCommand
extends Command {
    @Override
    public void onCommand(String command, String[] message) {
        int count = 0;
        for (Module module : ModuleManager.getModules()) {
            if (!module.isEnabled()) continue;
            module.disable();
            ++count;
        }
        MessageBus.sendCommandMessage("Disabled " + count + " modules!", true);
    }
}

