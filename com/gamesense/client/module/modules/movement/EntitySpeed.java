/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.util.world.MotionUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Module.Declaration(name="EntitySpeed", category=Category.Movement)
public class EntitySpeed
extends Module {
    DoubleSetting speed = this.registerDouble("Speed", 1.0, 0.0, 3.8);

    @Override
    public void onUpdate() {
        if (EntitySpeed.mc.field_71439_g.field_184239_as != null) {
            double[] dir = MotionUtil.forward((Double)this.speed.getValue());
            EntitySpeed.mc.field_71439_g.field_184239_as.field_70159_w = dir[0];
            EntitySpeed.mc.field_71439_g.field_184239_as.field_70179_y = dir[1];
        }
    }
}

