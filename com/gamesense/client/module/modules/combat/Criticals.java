/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.PlayerTweaks;
import java.util.Objects;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.world.World;

@Module.Declaration(name="Criticals", category=Category.Combat)
public class Criticals
extends Module {
    BooleanSetting allowWater = this.registerBoolean("In Water", false);
    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<PacketEvent.Send>(event -> {
        if (PlayerUtil.nullCheck() && Criticals.mc.field_71439_g.field_70122_E && event.getPacket() instanceof CPacketUseEntity && (!Criticals.mc.field_71439_g.func_70090_H() || !((Boolean)this.allowWater.getValue()).booleanValue()) && ((CPacketUseEntity)event.getPacket()).func_149564_a((World)Criticals.mc.field_71441_e) != null && ((CPacketUseEntity)event.getPacket()).func_149565_c() == CPacketUseEntity.Action.ATTACK && !(Criticals.mc.field_71441_e.func_73045_a(Objects.requireNonNull(((CPacketUseEntity)event.getPacket()).func_149564_a((World)Criticals.mc.field_71441_e)).func_145782_y()) instanceof EntityEnderCrystal)) {
            ModuleManager.getModule(PlayerTweaks.class).pauseNoFallPacket = true;
            Criticals.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(Criticals.mc.field_71439_g.field_70165_t, Criticals.mc.field_71439_g.field_70163_u + 0.11, Criticals.mc.field_71439_g.field_70161_v, false));
            Criticals.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(Criticals.mc.field_71439_g.field_70165_t, Criticals.mc.field_71439_g.field_70163_u + 0.1100013579, Criticals.mc.field_71439_g.field_70161_v, false));
            Criticals.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(Criticals.mc.field_71439_g.field_70165_t, Criticals.mc.field_71439_g.field_70163_u + 1.3579E-6, Criticals.mc.field_71439_g.field_70161_v, false));
        }
    }, new Predicate[0]);
}

