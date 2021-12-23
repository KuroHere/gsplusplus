/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraftforge.client.event.GuiOpenEvent;

@Module.Declaration(name="AutoRespawn", category=Category.Misc)
public class AutoRespawn
extends Module {
    BooleanSetting respawnMessage = this.registerBoolean("Respawn Message", false);
    IntegerSetting respawnMessageDelay = this.registerInteger("Msg Delay(ms)", 0, 0, 5000);
    private static String AutoRespawnMessage = "/kit";
    private boolean isDead;
    private boolean sentRespawnMessage = true;
    long timeSinceRespawn;
    @EventHandler
    private final Listener<GuiOpenEvent> livingDeathEventListener = new Listener<GuiOpenEvent>(event -> {
        if (event.getGui() instanceof GuiGameOver) {
            event.setCanceled(true);
            this.isDead = true;
            this.sentRespawnMessage = true;
            AutoRespawn.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
        }
    }, new Predicate[0]);

    @Override
    public void onUpdate() {
        if (AutoRespawn.mc.field_71439_g == null) {
            return;
        }
        if (this.isDead && AutoRespawn.mc.field_71439_g.func_70089_S()) {
            if (((Boolean)this.respawnMessage.getValue()).booleanValue()) {
                this.sentRespawnMessage = false;
                this.timeSinceRespawn = System.currentTimeMillis();
            }
            this.isDead = false;
        }
        if (!this.sentRespawnMessage && System.currentTimeMillis() - this.timeSinceRespawn > (long)((Integer)this.respawnMessageDelay.getValue()).intValue()) {
            AutoRespawn.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketChatMessage(AutoRespawnMessage));
            this.sentRespawnMessage = true;
        }
    }

    public static void setAutoRespawnMessage(String string) {
        AutoRespawnMessage = string;
    }

    public static String getAutoRespawnMessages() {
        return AutoRespawnMessage;
    }
}

