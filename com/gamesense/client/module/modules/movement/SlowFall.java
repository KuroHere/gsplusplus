/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Arrays;

@Module.Declaration(name="SlowFall", category=Category.Movement)
public class SlowFall
extends Module {
    ModeSetting mode = this.registerMode("Mode", Arrays.asList("Timer", "Motion"), "Motion");
    DoubleSetting timer = this.registerDouble("Timer", 0.1, 0.1, 1.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Timer"));
    DoubleSetting motion = this.registerDouble("Motion", 1.0, 0.0, 100.0);

    @Override
    public void onUpdate() {
        if (SlowFall.mc.field_71474_y.field_74314_A.func_151470_d()) {
            if (((String)this.mode.getValue()).equalsIgnoreCase("Timer")) {
                SlowFall.mc.field_71428_T.field_194149_e = 50.0f / ((Double)this.timer.getValue()).floatValue();
            } else {
                SlowFall.mc.field_71439_g.field_70181_x = (Double)this.motion.getValue() / 100.0;
            }
        }
    }
}

