/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.SwingEvent;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraft.world.WorldServer;

@Module.Declaration(name="Swing", category=Category.Render)
public class Swing
extends Module {
    ModeSetting mode = this.registerMode("Mode", Arrays.asList("Swap", "Silent"), "Swap");
    @EventHandler
    private final Listener<SwingEvent> swingEventListener = new Listener<SwingEvent>(event -> {
        switch ((String)this.mode.getValue()) {
            case "Swap": {
                EnumHand hand = event.getHand().equals((Object)EnumHand.OFF_HAND) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
                ItemStack stack = Swing.mc.field_71439_g.func_184586_b(hand);
                if (!stack.func_190926_b() && stack.func_77973_b().onEntitySwing((EntityLivingBase)Swing.mc.field_71439_g, stack)) {
                    return;
                }
                if (!Swing.mc.field_71439_g.field_82175_bq || Swing.mc.field_71439_g.field_110158_av >= this.getArmSwingAnimationEnd() / 2 || Swing.mc.field_71439_g.field_110158_av < 0) {
                    Swing.mc.field_71439_g.field_110158_av = -1;
                    Swing.mc.field_71439_g.field_82175_bq = true;
                    Swing.mc.field_71439_g.field_184622_au = hand;
                    if (Swing.mc.field_71439_g.field_70170_p instanceof WorldServer) {
                        ((WorldServer)Swing.mc.field_71439_g.field_70170_p).func_73039_n().func_151247_a((Entity)Swing.mc.field_71439_g, (Packet)new SPacketAnimation((Entity)Swing.mc.field_71439_g, hand == EnumHand.MAIN_HAND ? 0 : 3));
                    }
                }
                Swing.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketAnimation(hand));
                event.cancel();
                break;
            }
            case "Silent": {
                event.cancel();
                Swing.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketAnimation(event.getHand()));
            }
        }
    }, new Predicate[0]);

    private int getArmSwingAnimationEnd() {
        if (Swing.mc.field_71439_g.func_70644_a(MobEffects.field_76422_e)) {
            return 6 - (1 + Swing.mc.field_71439_g.func_70660_b(MobEffects.field_76422_e).func_76458_c());
        }
        return Swing.mc.field_71439_g.func_70644_a(MobEffects.field_76419_f) ? 6 + (1 + Swing.mc.field_71439_g.func_70660_b(MobEffects.field_76419_f).func_76458_c()) * 2 : 6;
    }
}

