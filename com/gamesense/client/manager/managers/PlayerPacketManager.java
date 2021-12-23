/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.manager.managers;

import com.gamesense.api.event.Phase;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.RenderEntityEvent;
import com.gamesense.api.util.misc.CollectionUtil;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.client.manager.Manager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public enum PlayerPacketManager implements Manager
{
    INSTANCE;

    private final List<PlayerPacket> packets = new ArrayList<PlayerPacket>();
    private Vec3d prevServerSidePosition = Vec3d.field_186680_a;
    private Vec3d serverSidePosition = Vec3d.field_186680_a;
    private Vec2f prevServerSideRotation = Vec2f.field_189974_a;
    private Vec2f serverSideRotation = Vec2f.field_189974_a;
    private Vec2f clientSidePitch = Vec2f.field_189974_a;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
        if (event.getPhase() != Phase.BY || this.packets.isEmpty()) {
            return;
        }
        PlayerPacket packet = CollectionUtil.maxOrNull(this.packets, PlayerPacket::getPriority);
        if (packet != null) {
            event.cancel();
            event.apply(packet);
        }
        this.packets.clear();
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PacketEvent.PostSend> postSendListener = new Listener<PacketEvent.PostSend>(event -> {
        if (event.isCancelled()) {
            return;
        }
        Packet rawPacket = event.getPacket();
        EntityPlayerSP player = this.getPlayer();
        if (player != null && rawPacket instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer)rawPacket;
            if (packet.field_149480_h) {
                this.serverSidePosition = new Vec3d(packet.field_149479_a, packet.field_149477_b, packet.field_149478_c);
            }
            if (packet.field_149481_i) {
                this.serverSideRotation = new Vec2f(packet.field_149476_e, packet.field_149473_f);
                player.field_70759_as = packet.field_149476_e;
            }
        }
    }, -200, new Predicate[0]);
    @EventHandler
    private final Listener<TickEvent.ClientTickEvent> tickEventListener = new Listener<TickEvent.ClientTickEvent>(event -> {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        this.prevServerSidePosition = this.serverSidePosition;
        this.prevServerSideRotation = this.serverSideRotation;
    }, new Predicate[0]);
    @EventHandler
    private final Listener<RenderEntityEvent.Head> renderEntityEventHeadListener = new Listener<RenderEntityEvent.Head>(event -> {
        EntityPlayerSP player = this.getPlayer();
        if (player == null || player.func_184218_aH() || event.getType() != RenderEntityEvent.Type.TEXTURE || event.getEntity() != player) {
            return;
        }
        this.clientSidePitch = new Vec2f(player.field_70127_C, player.field_70125_A);
        player.field_70127_C = this.prevServerSideRotation.field_189983_j;
        player.field_70125_A = this.serverSideRotation.field_189983_j;
    }, new Predicate[0]);
    @EventHandler
    private final Listener<RenderEntityEvent.Return> renderEntityEventReturnListener = new Listener<RenderEntityEvent.Return>(event -> {
        EntityPlayerSP player = this.getPlayer();
        if (player == null || player.func_184218_aH() || event.getType() != RenderEntityEvent.Type.TEXTURE || event.getEntity() != player) {
            return;
        }
        player.field_70127_C = this.clientSidePitch.field_189982_i;
        player.field_70125_A = this.clientSidePitch.field_189983_j;
    }, new Predicate[0]);

    public void addPacket(PlayerPacket packet) {
        this.packets.add(packet);
    }

    public Vec3d getPrevServerSidePosition() {
        return this.prevServerSidePosition;
    }

    public Vec3d getServerSidePosition() {
        return this.serverSidePosition;
    }

    public Vec2f getPrevServerSideRotation() {
        return this.prevServerSideRotation;
    }

    public Vec2f getServerSideRotation() {
        return this.serverSideRotation;
    }
}

