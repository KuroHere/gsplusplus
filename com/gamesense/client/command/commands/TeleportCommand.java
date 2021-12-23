/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.command.commands;

import com.gamesense.client.command.Command;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

@Command.Declaration(name="Teleport", syntax="tp [x] [y] [z]", alias={"tp", "teleport", "clipto"})
public class TeleportCommand
extends Command {
    @Override
    public void onCommand(String command, String[] message) {
        try {
            String x = message[0];
            String y = message[1];
            String z = message[2];
            int xp = Integer.parseInt(x);
            int yp = Integer.parseInt(y);
            int zp = Integer.parseInt(z);
            if (TeleportCommand.mc.field_71439_g.field_184239_as == null) {
                TeleportCommand.mc.field_71439_g.func_70634_a((double)xp, (double)yp, (double)zp);
            } else {
                TeleportCommand.mc.field_71439_g.field_184239_as.func_70107_b((double)xp, (double)yp, (double)zp);
            }
            TeleportCommand.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position((double)xp, (double)yp, (double)zp, false));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

