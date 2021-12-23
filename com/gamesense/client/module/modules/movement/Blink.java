/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.world.World;

@Module.Declaration(name="Blink", category=Category.Movement)
public class Blink
extends Module {
    BooleanSetting ghostPlayer = this.registerBoolean("Ghost Player", true);
    BooleanSetting keepRotations = this.registerBoolean("Keep Rotations", false);
    ModeSetting scatterTiming = this.registerMode("Scatter", Arrays.asList("Numbers", "Timer"), "Milliseconds");
    IntegerSetting millisecondPackets = this.registerInteger("Milliseconds", 5000, 1000, 10000, () -> ((String)this.scatterTiming.getValue()).equals("Timer"));
    IntegerSetting nPacketsLimit = this.registerInteger("Number Packets", 150, 0, 2000, () -> ((String)this.scatterTiming.getValue()).equals("Numbers"));
    IntegerSetting outputScatter = this.registerInteger("Output Scatter", 10, 0, 50);
    BooleanSetting debug = this.registerBoolean("Debug", false);
    BooleanSetting shiftScatter = this.registerBoolean("Shift Scatter", false);
    private EntityOtherPlayerMP entity;
    public final ArrayList<Packet<?>> packets = new ArrayList();
    private boolean startScatter;
    private long startScatterTimer;
    private int nPackets;
    boolean isRemoving = false;
    @EventHandler
    private final Listener<PacketEvent.Send> packetSendListener = new Listener<PacketEvent.Send>(event -> {
        if (this.isRemoving) {
            return;
        }
        if (Blink.mc.field_71439_g == null || Blink.mc.field_71441_e == null) {
            this.disable();
        }
        if (!((Boolean)this.keepRotations.getValue()).booleanValue()) {
            Packet packet = event.getPacket();
            if (Blink.mc.field_71439_g != null && Blink.mc.field_71439_g.func_70089_S() && packet instanceof CPacketPlayer) {
                this.packets.add(packet);
                ++this.nPackets;
                event.cancel();
            }
        } else {
            Packet packet = event.getPacket();
            if (Blink.mc.field_71439_g != null && Blink.mc.field_71439_g.func_70089_S() && (packet instanceof CPacketPlayer.Position || packet instanceof CPacketPlayer.PositionRotation)) {
                this.packets.add(packet);
                ++this.nPackets;
                event.cancel();
            }
        }
    }, new Predicate[0]);

    @Override
    public void onEnable() {
        EntityPlayerSP player = Blink.mc.field_71439_g;
        WorldClient world = Blink.mc.field_71441_e;
        this.isRemoving = false;
        this.startScatter = false;
        this.nPackets = 0;
        this.startScatterTimer = System.currentTimeMillis();
        if (player == null || world == null) {
            this.disable();
        } else if (((Boolean)this.ghostPlayer.getValue()).booleanValue()) {
            this.entity = new EntityOtherPlayerMP((World)world, mc.func_110432_I().func_148256_e());
            this.entity.func_82149_j((Entity)player);
            this.entity.field_71071_by.func_70455_b(player.field_71071_by);
            this.entity.field_70177_z = player.field_70177_z;
            this.entity.field_70759_as = player.field_70759_as;
            world.func_73027_a(667, (Entity)this.entity);
        }
        this.packets.clear();
    }

    @Override
    public void onUpdate() {
        if (Blink.mc.field_71439_g == null || Blink.mc.field_71441_e == null) {
            this.disable();
        }
        EntityOtherPlayerMP entity = this.entity;
        WorldClient world = Blink.mc.field_71441_e;
        if (!((Boolean)this.ghostPlayer.getValue()).booleanValue() && entity != null && world != null) {
            world.func_72900_e((Entity)entity);
        }
        if (((Boolean)this.shiftScatter.getValue()).booleanValue() && Blink.mc.field_71474_y.field_74311_E.func_151468_f()) {
            this.startScatter = true;
        }
        if (!this.startScatter) {
            switch ((String)this.scatterTiming.getValue()) {
                case "Timer": {
                    if (System.currentTimeMillis() - this.startScatterTimer < (long)((Integer)this.millisecondPackets.getValue()).intValue()) break;
                    this.startScatter = true;
                    if (!((Boolean)this.debug.getValue()).booleanValue()) break;
                    PistonCrystal.printDebug("N^Packets: " + this.nPackets, false);
                    break;
                }
                case "Numbers": {
                    if (this.nPackets < (Integer)this.nPacketsLimit.getValue()) break;
                    this.startScatter = true;
                    if (!((Boolean)this.debug.getValue()).booleanValue()) break;
                    PistonCrystal.printDebug("N^Packets: " + this.nPackets, false);
                }
            }
        }
        if (this.startScatter) {
            this.isRemoving = true;
            for (int i = 0; i < (Integer)this.outputScatter.getValue(); ++i) {
                if (this.packets.size() == 0) {
                    this.disable();
                    return;
                }
                if (((Boolean)this.ghostPlayer.getValue()).booleanValue()) {
                    CPacketPlayer packet = (CPacketPlayer)this.packets.get(0);
                    ((Entity)Objects.requireNonNull(entity)).func_70107_b(packet.field_149479_a, packet.field_149477_b, packet.field_149478_c);
                }
                Blink.mc.field_71439_g.field_71174_a.func_147297_a(this.packets.get(0));
                this.packets.remove(0);
                --this.nPackets;
            }
            this.isRemoving = false;
        }
    }

    @Override
    public void onDisable() {
        EntityOtherPlayerMP entity = this.entity;
        WorldClient world = Blink.mc.field_71441_e;
        if (entity != null && world != null) {
            world.func_72900_e((Entity)entity);
        }
        EntityPlayerSP player = Blink.mc.field_71439_g;
        if (this.packets.size() > 0 && player != null) {
            for (Packet<?> packet : this.packets) {
                player.field_71174_a.func_147297_a(packet);
            }
            this.packets.clear();
        }
    }

    @Override
    public String getHudInfo() {
        return "[" + ChatFormatting.WHITE + this.nPackets + ChatFormatting.GRAY + "]";
    }
}

