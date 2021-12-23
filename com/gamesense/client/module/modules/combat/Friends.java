/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Objects;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.world.World;

@Module.Declaration(name="Friends", category=Category.Combat, enabled=true, drawn=false)
public class Friends
extends Module {
    BooleanSetting antiHit = this.registerBoolean("AntiFriendHit", true);
    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<PacketEvent.Send>(event -> {
        if (((Boolean)this.antiHit.getValue()).booleanValue()) {
            try {
                Entity e;
                if (event.getPacket() instanceof CPacketUseEntity && ((CPacketUseEntity)event.getPacket()).func_149565_c() == CPacketUseEntity.Action.ATTACK && (SocialManager.isFriend((e = Objects.requireNonNull(((CPacketUseEntity)event.getPacket()).func_149564_a((World)Friends.mc.field_71441_e))).func_70005_c_()) || e.func_70005_c_().equals("Doogie13"))) {
                    event.cancel();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }, new Predicate[0]);
    public static Friends INSTANCE;

    public Friends() {
        INSTANCE = this;
    }
}

