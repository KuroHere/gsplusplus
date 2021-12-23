/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

@Module.Declaration(name="AutoGG", category=Category.Misc)
public class AutoGG
extends Module {
    public static AutoGG INSTANCE;
    static List<String> AutoGgMessages;
    private ConcurrentHashMap targetedPlayers = null;
    int index = -1;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener = new Listener<PacketEvent.Send>(event -> {
        if (AutoGG.mc.field_71439_g != null) {
            Entity targetEntity;
            CPacketUseEntity cPacketUseEntity;
            if (this.targetedPlayers == null) {
                this.targetedPlayers = new ConcurrentHashMap();
            }
            if (event.getPacket() instanceof CPacketUseEntity && (cPacketUseEntity = (CPacketUseEntity)event.getPacket()).func_149565_c().equals((Object)CPacketUseEntity.Action.ATTACK) && (targetEntity = cPacketUseEntity.func_149564_a((World)AutoGG.mc.field_71441_e)) instanceof EntityPlayer) {
                this.addTargetedPlayer(targetEntity.func_70005_c_());
            }
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<LivingDeathEvent> livingDeathEventListener = new Listener<LivingDeathEvent>(event -> {
        if (AutoGG.mc.field_71439_g != null) {
            String name;
            EntityPlayer player;
            EntityLivingBase entity;
            if (this.targetedPlayers == null) {
                this.targetedPlayers = new ConcurrentHashMap();
            }
            if ((entity = event.getEntityLiving()) != null && entity instanceof EntityPlayer && (player = (EntityPlayer)entity).func_110143_aJ() <= 0.0f && this.shouldAnnounce(name = player.func_70005_c_())) {
                this.doAnnounce(name);
            }
        }
    }, new Predicate[0]);

    public AutoGG() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        this.targetedPlayers = new ConcurrentHashMap();
    }

    @Override
    public void onDisable() {
        this.targetedPlayers = null;
    }

    @Override
    public void onUpdate() {
        if (this.targetedPlayers == null) {
            this.targetedPlayers = new ConcurrentHashMap();
        }
        for (Entity entity : AutoGG.mc.field_71441_e.func_72910_y()) {
            String name;
            EntityPlayer player;
            if (!(entity instanceof EntityPlayer) || !((player = (EntityPlayer)entity).func_110143_aJ() <= 0.0f) || !this.shouldAnnounce(name = player.func_70005_c_())) continue;
            this.doAnnounce(name);
            break;
        }
        this.targetedPlayers.forEach((namex, timeout) -> {
            if ((Integer)timeout <= 0) {
                this.targetedPlayers.remove(namex);
            } else {
                this.targetedPlayers.put(namex, (Integer)timeout - 1);
            }
        });
    }

    private boolean shouldAnnounce(String name) {
        return this.targetedPlayers.containsKey(name);
    }

    private void doAnnounce(String name) {
        this.targetedPlayers.remove(name);
        if (this.index >= AutoGgMessages.size() - 1) {
            this.index = -1;
        }
        ++this.index;
        String message = AutoGgMessages.size() > 0 ? AutoGgMessages.get(this.index) : "GG! GameSense v2.3.4 is on top!";
        String messageSanitized = message.replaceAll("\u0e22\u0e07", "").replace("{name}", name);
        if (messageSanitized.length() > 255) {
            messageSanitized = messageSanitized.substring(0, 255);
        }
        MessageBus.sendServerMessage(messageSanitized);
    }

    public void addTargetedPlayer(String name) {
        if (!Objects.equals(name, AutoGG.mc.field_71439_g.func_70005_c_())) {
            if (this.targetedPlayers == null) {
                this.targetedPlayers = new ConcurrentHashMap();
            }
            this.targetedPlayers.put(name, 20);
        }
    }

    public static void addAutoGgMessage(String s) {
        AutoGgMessages.add(s);
    }

    public static List<String> getAutoGgMessages() {
        return AutoGgMessages;
    }

    static {
        AutoGgMessages = new ArrayList<String>();
    }
}

