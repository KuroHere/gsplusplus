/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

@Command.Declaration(name="OpenFolder", syntax="openfolder", alias={"openfolder", "config", "open", "folder"})
public class OpenFolderCommand
extends Command {
    @Override
    public void onCommand(String command, String[] message) {
        try {
            Desktop.getDesktop().open(new File("gs++/".replace("/", "")));
            MessageBus.sendCommandMessage("Opened config folder!", true);
        }
        catch (IOException e) {
            MessageBus.sendCommandMessage("Could not open config folder!", true);
            e.printStackTrace();
        }
    }
}

