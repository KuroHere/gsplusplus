/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;

@Module.Declaration(name="AntiHunger", category=Category.Movement)
public class AntiHunger
extends Module {
    BooleanSetting spoofMovement = this.registerBoolean("Spoof Movement", true);
    @EventHandler
    private final Listener<PacketEvent.Send> packetSendListener = new Listener<PacketEvent.Send>(event -> {
        Packet packet = event.getPacket();
        EntityPlayerSP player = AntiHunger.mc.field_71439_g;
        if (packet instanceof CPacketPlayer) {
            boolean bl = ((CPacketPlayer)packet).field_149474_g = (player.field_70143_R <= 0.0f || AntiHunger.mc.field_71442_b.field_78778_j) && player.func_184613_cA();
        }
        if (packet instanceof CPacketEntityAction && ((Boolean)this.spoofMovement.getValue()).booleanValue() && (((CPacketEntityAction)packet).func_180764_b() == CPacketEntityAction.Action.START_SPRINTING || ((CPacketEntityAction)packet).func_180764_b() == CPacketEntityAction.Action.STOP_SPRINTING)) {
            event.cancel();
        }
    }, new Predicate[0]);
}

