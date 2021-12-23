/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.GameSense;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.command.Command;

@Command.Declaration(name="FixGUI", syntax="fixgui", alias={"fixgui", "gui", "resetgui"})
public class FixGUICommand
extends Command {
    @Override
    public void onCommand(String command, String[] message) {
        GameSense.INSTANCE.gameSenseGUI = new GameSenseGUI();
        MessageBus.sendCommandMessage("ClickGUI positions reset!", true);
    }
}

