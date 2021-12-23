/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.command.commands;

import com.gamesense.api.config.SaveConfig;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;

@Command.Declaration(name="SaveConfig", syntax="saveconfig", alias={"saveconfig", "saveconfiguration"})
public class SaveConfigCommand
extends Command {
    @Override
    public void onCommand(String command, String[] message) {
        SaveConfig.init();
        MessageBus.sendCommandMessage("Config saved!", true);
    }
}

