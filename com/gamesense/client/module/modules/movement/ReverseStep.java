/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.PlayerMoveEvent;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;

@Module.Declaration(name="ReverseStep", category=Category.Movement)
public class ReverseStep
extends Module {
    ModeSetting reverse = this.registerMode("Reverse Mode", Arrays.asList("Strict", "Vanilla"), "Vanilla");
    DoubleSetting height = this.registerDouble("Height", 2.5, 0.5, 2.5);
    boolean doIt;
    @EventHandler
    final Listener<PlayerMoveEvent> eventListener = new Listener<PlayerMoveEvent>(event -> {
        if (ReverseStep.mc.field_71439_g == null || !ReverseStep.mc.field_71439_g.field_70122_E || ReverseStep.mc.field_71439_g.func_70090_H() || ReverseStep.mc.field_71439_g.func_70617_f_()) {
            return;
        }
        float dist = 6.9696968E7f;
        if (((String)this.reverse.getValue()).equalsIgnoreCase("Vanilla")) {
            for (double y = 0.0; y < (Double)this.height.getValue() + 0.5; y += 0.01) {
                if (ReverseStep.mc.field_71441_e.func_184144_a((Entity)ReverseStep.mc.field_71439_g, ReverseStep.mc.field_71439_g.func_174813_aQ().func_72317_d(0.0, -y, 0.0)).isEmpty()) continue;
                ReverseStep.mc.field_71439_g.field_70181_x = -10.0;
            }
        } else {
            for (double y = 0.0; y < (Double)this.height.getValue() + 1.0; y += 0.01) {
                if (ReverseStep.mc.field_71441_e.func_184144_a((Entity)ReverseStep.mc.field_71439_g, ReverseStep.mc.field_71439_g.func_174813_aQ().func_72317_d(0.0, -y, 0.0)).isEmpty()) {
                    dist = (float)y;
                }
                this.doIt = !ReverseStep.mc.field_71441_e.func_184144_a((Entity)ReverseStep.mc.field_71439_g, ReverseStep.mc.field_71439_g.func_174813_aQ().func_72317_d(0.0, (double)(-dist) - 0.1, 0.0)).isEmpty();
            }
            if ((double)dist <= (Double)this.height.getValue() && this.doIt) {
                PlayerUtil.fall((int)((double)dist + 0.1));
                MessageBus.sendClientRawMessage(dist + "");
                event.setVelocity(0.0, 0.0, 0.0);
                ReverseStep.mc.field_71439_g.func_70016_h(ReverseStep.mc.field_71439_g.field_70159_w, ReverseStep.mc.field_71439_g.field_70181_x, ReverseStep.mc.field_71439_g.field_70179_y);
            }
        }
    }, new Predicate[0]);
}

