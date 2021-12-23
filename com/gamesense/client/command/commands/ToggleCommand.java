/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;

@Command.Declaration(name="Toggle", syntax="toggle [module]", alias={"toggle", "t", "enable", "disable"})
public class ToggleCommand
extends Command {
    @Override
    public void onCommand(String command, String[] message) {
        String main = message[0];
        Module module = ModuleManager.getModule(main);
        if (module == null) {
            MessageBus.sendCommandMessage(this.getSyntax(), true);
            return;
        }
        module.toggle();
        if (module.isEnabled()) {
            MessageBus.sendCommandMessage("Module " + module.getName() + " set to: ENABLED!", true);
        } else {
            MessageBus.sendCommandMessage("Module " + module.getName() + " set to: DISABLED!", true);
        }
    }
}

