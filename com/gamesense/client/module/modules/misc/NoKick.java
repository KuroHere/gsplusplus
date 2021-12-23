/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.network.play.server.SPacketSoundEffect;
import org.lwjgl.input.Keyboard;

@Module.Declaration(name="NoKick", category=Category.Misc)
public class NoKick
extends Module {
    public BooleanSetting noPacketKick = this.registerBoolean("Packet", true);
    BooleanSetting noSlimeCrash = this.registerBoolean("Slime", false);
    BooleanSetting noOffhandCrash = this.registerBoolean("Offhand", false);
    BooleanSetting noSignCrash = this.registerBoolean("Cancel Sign Edit", false);
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<PacketEvent.Receive>(event -> {
        if (((Boolean)this.noOffhandCrash.getValue()).booleanValue() && !Keyboard.isKeyDown((int)42) && event.getPacket() instanceof SPacketSoundEffect && ((SPacketSoundEffect)event.getPacket()).func_186978_a() == SoundEvents.field_187719_p) {
            event.cancel();
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener = new Listener<PacketEvent.Send>(event -> {
        if (event.getPacket() instanceof CPacketUpdateSign && ((Boolean)this.noSignCrash.getValue()).booleanValue() && !Keyboard.isKeyDown((int)NoKick.mc.field_71474_y.field_74311_E.func_151463_i())) {
            event.cancel();
        }
    }, new Predicate[0]);

    @Override
    public void onUpdate() {
        if (NoKick.mc.field_71441_e != null && ((Boolean)this.noSlimeCrash.getValue()).booleanValue()) {
            NoKick.mc.field_71441_e.field_72996_f.forEach(entity -> {
                EntitySlime slime;
                if (entity instanceof EntitySlime && (slime = (EntitySlime)entity).func_70809_q() > 4) {
                    NoKick.mc.field_71441_e.func_72900_e(entity);
                }
            });
        }
    }
}

