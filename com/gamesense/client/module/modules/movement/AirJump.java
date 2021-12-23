/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Arrays;

@Module.Declaration(name="AirJump", category=Category.Movement)
public class AirJump
extends Module {
    ModeSetting mode = this.registerMode("Mode", Arrays.asList("Single", "Repeat"), "Single");
    IntegerSetting repeat = this.registerInteger("Repeat", 19, 1, 20, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Repeat"));
    int timer;

    @Override
    public void onEnable() {
        this.timer = 0;
    }

    @Override
    public void onUpdate() {
        this.timer = AirJump.mc.field_71439_g.field_70122_E ? 0 : ++this.timer;
        if (((String)this.mode.getValue()).equalsIgnoreCase("Single")) {
            if (AirJump.mc.field_71474_y.field_74314_A.func_151468_f()) {
                AirJump.mc.field_71439_g.func_70664_aZ();
            }
        } else if (((String)this.mode.getValue()).equalsIgnoreCase("Repeat") && this.timer == (Integer)this.repeat.getValue() && AirJump.mc.field_71474_y.field_74314_A.func_151470_d()) {
            AirJump.mc.field_71439_g.func_70664_aZ();
            this.timer = 0;
        }
    }
}

