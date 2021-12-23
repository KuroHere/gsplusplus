/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.util.math.Vec3d;

@Command.Declaration(name="HorizontalClip", syntax="HClip [Distance]", alias={"hclip", "hc", "forward", "fwd", "chineseComunistParty"})
public class HClipCommand
extends Command {
    double amount;

    @Override
    public void onCommand(String command, String[] message) {
        if (HClipCommand.mc.field_71439_g != null) {
            String main = message[0];
            try {
                this.amount = Double.parseDouble(main);
                if (this.amount >= 0.0) {
                    MessageBus.sendCommandMessage(ModuleManager.getModule(ColorMain.class).getEnabledColor() + "Clipped the player " + this.amount + " blocks forward.", true);
                } else {
                    MessageBus.sendCommandMessage(ModuleManager.getModule(ColorMain.class).getEnabledColor() + "Clipped the player " + -this.amount + " blocks backward.", true);
                }
                Vec3d dir = new Vec3d(Math.cos((double)(HClipCommand.mc.field_71439_g.field_70177_z + 90.0f) * Math.PI / 180.0), 0.0, Math.sin((double)(HClipCommand.mc.field_71439_g.field_70177_z + 90.0f) * Math.PI / 180.0));
                HClipCommand.mc.field_71439_g.func_70107_b(HClipCommand.mc.field_71439_g.field_70165_t + dir.field_72450_a * this.amount, HClipCommand.mc.field_71439_g.field_70163_u, HClipCommand.mc.field_71439_g.field_70161_v + dir.field_72449_c * this.amount);
            }
            catch (NumberFormatException e) {
                MessageBus.sendCommandMessage(ModuleManager.getModule(ColorMain.class).getDisabledColor() + "You moron, you absolute buffoon, how do you mess up entering a number into a command, you philistine!", true);
            }
        }
    }
}

