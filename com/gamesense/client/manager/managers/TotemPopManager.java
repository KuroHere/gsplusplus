/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.manager.managers;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.TotemPopEvent;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.GameSense;
import com.gamesense.client.manager.Manager;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.PvPInfo;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.HashMap;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public enum TotemPopManager implements Manager
{
    INSTANCE;

    public boolean sendMsgs = false;
    public boolean sendCountPops = false;
    public boolean sendCountKills = false;
    public boolean popCount = false;
    public ChatFormatting chatFormatting = ChatFormatting.WHITE;
    private final HashMap<String, Integer> playerPopCount = new HashMap();
    private int countPops = 0;
    private int countKills = 0;
    PvPInfo pvp = ModuleManager.getModule(PvPInfo.class);
    @EventHandler
    private final Listener<TickEvent.ClientTickEvent> clientTickEventListener = new Listener<TickEvent.ClientTickEvent>(event -> {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        if (this.getPlayer() == null || this.getWorld() == null) {
            this.playerPopCount.clear();
            return;
        }
        for (EntityPlayer entityPlayer : this.getWorld().field_73010_i) {
            if (!(entityPlayer.func_110143_aJ() <= 0.0f) || !this.playerPopCount.containsKey(entityPlayer.func_70005_c_()) || entityPlayer == Minecraft.func_71410_x().field_71439_g) continue;
            if (this.sendMsgs && this.pvp.isEnabled()) {
                MessageBus.sendClientPrefixMessage(this.chatFormatting + entityPlayer.func_70005_c_() + " died after popping " + ChatFormatting.GREEN + this.getPlayerPopCount(entityPlayer.func_70005_c_()) + this.chatFormatting + " totems!");
            }
            ++this.countKills;
            if (this.sendCountKills && this.pvp.isEnabled()) {
                MessageBus.sendClientPrefixMessage(this.chatFormatting + "You have seen " + ChatFormatting.GREEN + this.countKills + this.chatFormatting + " people killed!");
            }
            this.playerPopCount.remove(entityPlayer.func_70005_c_());
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PacketEvent.Receive> packetEventListener = new Listener<PacketEvent.Receive>(event -> {
        if (this.getPlayer() == null || this.getWorld() == null) {
            return;
        }
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus)event.getPacket();
            Entity entity = packet.func_149161_a((World)this.getWorld());
            if (packet.func_149160_c() == 35) {
                GameSense.EVENT_BUS.post(new TotemPopEvent(entity));
            }
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<TotemPopEvent> totemPopEventListener = new Listener<TotemPopEvent>(event -> {
        if (this.getPlayer() == null || this.getWorld() == null) {
            return;
        }
        if (event.getEntity() == null) {
            return;
        }
        String entityName = event.getEntity().func_70005_c_();
        if (this.getMinecraft().field_71439_g.field_146106_i.getName().equals(entityName)) {
            return;
        }
        ++this.countPops;
        if (this.sendMsgs && this.popCount && this.pvp.isEnabled()) {
            MessageBus.sendClientPrefixMessage(this.chatFormatting + "You have seen " + ChatFormatting.GREEN + this.countPops + this.chatFormatting + " people popped!");
        }
        if (this.playerPopCount.get(entityName) == null) {
            this.playerPopCount.put(entityName, 1);
            if (this.sendMsgs && this.sendCountPops && this.pvp.isEnabled()) {
                MessageBus.sendClientPrefixMessage(this.chatFormatting + entityName + " popped " + ChatFormatting.RED + 1 + this.chatFormatting + " totem!");
            }
        } else {
            int popCounter = this.playerPopCount.get(entityName) + 1;
            this.playerPopCount.put(entityName, popCounter);
            if (this.sendMsgs && this.sendCountPops && this.pvp.isEnabled()) {
                MessageBus.sendClientPrefixMessage(this.chatFormatting + entityName + " popped " + ChatFormatting.RED + popCounter + this.chatFormatting + " totems!");
            }
        }
    }, new Predicate[0]);

    public int getPops() {
        return this.countPops;
    }

    public int getKills() {
        return this.countKills;
    }

    public int getPlayerPopCount(String name) {
        if (this.playerPopCount.containsKey(name)) {
            return this.playerPopCount.get(name);
        }
        return 0;
    }
}

