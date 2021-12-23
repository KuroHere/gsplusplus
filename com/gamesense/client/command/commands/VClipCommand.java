/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;

@Command.Declaration(name="VerticalClip", syntax="VClip [Distance]", alias={"vclip", "vc", "yclip", "yc"})
public class VClipCommand
extends Command {
    double amount;

    @Override
    public void onCommand(String command, String[] message) {
        if (VClipCommand.mc.field_71439_g != null) {
            String main = message[0];
            try {
                this.amount = Double.parseDouble(main);
                if (this.amount >= 0.0) {
                    MessageBus.sendCommandMessage(ModuleManager.getModule(ColorMain.class).getEnabledColor() + "Clipped the player " + this.amount + " blocks up", true);
                } else {
                    MessageBus.sendCommandMessage(ModuleManager.getModule(ColorMain.class).getEnabledColor() + "Clipped the player " + -this.amount + " blocks down", true);
                }
            }
            catch (NumberFormatException e) {
                MessageBus.sendCommandMessage(ModuleManager.getModule(ColorMain.class).getDisabledColor() + "You moron, you absolute buffoon, how do you mess up entering a number into a command, you philistine!", true);
                return;
            }
            VClipCommand.mc.field_71439_g.func_70634_a(VClipCommand.mc.field_71439_g.field_70165_t, VClipCommand.mc.field_71439_g.field_70163_u + this.amount, VClipCommand.mc.field_71439_g.field_70161_v);
        }
    }
}

