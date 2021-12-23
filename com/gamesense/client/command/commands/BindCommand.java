/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import org.lwjgl.input.Keyboard;

@Command.Declaration(name="Bind", syntax="bind [module] key", alias={"bind", "b", "setbind", "key"})
public class BindCommand
extends Command {
    @Override
    public void onCommand(String command, String[] message) {
        String main = message[0];
        String value = message[1].toUpperCase();
        for (Module module : ModuleManager.getModules()) {
            if (!module.getName().equalsIgnoreCase(main)) continue;
            if (value.equalsIgnoreCase("none")) {
                module.setBind(0);
                MessageBus.sendCommandMessage("Module " + module.getName() + " bind set to: " + value + "!", true);
                continue;
            }
            if (value.length() == 1) {
                int key = Keyboard.getKeyIndex((String)value);
                module.setBind(key);
                MessageBus.sendCommandMessage("Module " + module.getName() + " bind set to: " + value + "!", true);
                continue;
            }
            if (value.length() <= 1) continue;
            MessageBus.sendCommandMessage(this.getSyntax(), true);
        }
    }
}

