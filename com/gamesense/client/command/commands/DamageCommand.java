/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.client.module.modules.movement.PlayerTweaks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

@Command.Declaration(name="Damage", syntax="damage", alias={"damage", "dmg", "hurt", "legbreak"})
public class DamageCommand
extends Command {
    @Override
    public void onCommand(String command, String[] message) {
        if (DamageCommand.mc.field_71439_g != null) {
            String dmg = message[0];
            int damage = 0;
            ModuleManager.getModule(PlayerTweaks.class).pauseNoFallPacket = true;
            try {
                damage = Integer.parseInt(dmg);
                MessageBus.sendCommandMessage(ModuleManager.getModule(ColorMain.class).getEnabledColor() + "Attempted to deal " + dmg + " damage to the player", true);
                DamageCommand.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(DamageCommand.mc.field_71439_g.field_70165_t, DamageCommand.mc.field_71439_g.field_70163_u + (double)damage + 2.1, DamageCommand.mc.field_71439_g.field_70161_v, false));
                DamageCommand.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(DamageCommand.mc.field_71439_g.field_70165_t, DamageCommand.mc.field_71439_g.field_70163_u + 0.05, DamageCommand.mc.field_71439_g.field_70161_v, false));
                DamageCommand.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(DamageCommand.mc.field_71439_g.field_70165_t, DamageCommand.mc.field_71439_g.field_70163_u - (double)damage - 2.1, DamageCommand.mc.field_71439_g.field_70161_v, false));
                DamageCommand.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(DamageCommand.mc.field_71439_g.field_70165_t, DamageCommand.mc.field_71439_g.field_70163_u + 0.05, DamageCommand.mc.field_71439_g.field_70161_v, false));
            }
            catch (NumberFormatException ignored) {
                for (int i = 0; i < 64; ++i) {
                    DamageCommand.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(DamageCommand.mc.field_71439_g.field_70165_t, DamageCommand.mc.field_71439_g.field_70163_u + 0.049, DamageCommand.mc.field_71439_g.field_70161_v, false));
                    DamageCommand.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(DamageCommand.mc.field_71439_g.field_70165_t, DamageCommand.mc.field_71439_g.field_70163_u, DamageCommand.mc.field_71439_g.field_70161_v, false));
                }
            }
            DamageCommand.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(DamageCommand.mc.field_71439_g.field_70165_t, DamageCommand.mc.field_71439_g.field_70163_u, DamageCommand.mc.field_71439_g.field_70161_v, true));
        }
    }
}

