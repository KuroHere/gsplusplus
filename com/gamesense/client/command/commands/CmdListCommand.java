/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.command.CommandManager;

@Command.Declaration(name="Commands", syntax="commands", alias={"commands", "cmd", "command", "commandlist", "help"})
public class CmdListCommand
extends Command {
    @Override
    public void onCommand(String command, String[] message) {
        for (Command command1 : CommandManager.getCommands()) {
            MessageBus.sendCommandMessage(command1.getName() + ": \"" + command1.getSyntax() + "\"!", true);
        }
    }
}

