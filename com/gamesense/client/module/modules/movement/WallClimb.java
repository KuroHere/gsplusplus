/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.PlayerMoveEvent;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

@Module.Declaration(name="WallClimb", category=Category.Movement)
public class WallClimb
extends Module {
    DoubleSetting speed = this.registerDouble("Speed", 0.42, 0.0, 1.0);
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
        if (WallClimb.mc.field_71439_g.field_70123_F && (WallClimb.mc.field_71439_g.field_71158_b.field_192832_b != 0.0f || WallClimb.mc.field_71439_g.field_71158_b.field_78902_a != 0.0f)) {
            event.setY((Double)this.speed.getValue());
            WallClimb.mc.field_71439_g.field_70143_R = 0.0f;
        }
    }, new Predicate[0]);
}

