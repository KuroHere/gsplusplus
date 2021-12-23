/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Objects;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;

@Module.Declaration(name="LevitationControl", category=Category.Movement)
public class LevitationControl
extends Module {
    DoubleSetting upAmplifier = this.registerDouble("Amplifier Up", 1.0, 1.0, 3.0);
    DoubleSetting downAmplifier = this.registerDouble("Amplifier Down", 1.0, 1.0, 3.0);

    @Override
    public void onUpdate() {
        if (LevitationControl.mc.field_71439_g.func_70644_a(MobEffects.field_188424_y)) {
            int amplifier = Objects.requireNonNull(LevitationControl.mc.field_71439_g.func_70660_b(Objects.requireNonNull(Potion.func_188412_a((int)25)))).func_76458_c();
            LevitationControl.mc.field_71439_g.field_70181_x = LevitationControl.mc.field_71474_y.field_74314_A.func_151470_d() ? (0.05 * (double)(amplifier + 1) - LevitationControl.mc.field_71439_g.field_70181_x) * 0.2 * (Double)this.upAmplifier.getValue() : (LevitationControl.mc.field_71474_y.field_74311_E.func_151470_d() ? -((0.05 * (double)(amplifier + 1) - LevitationControl.mc.field_71439_g.field_70181_x) * 0.2 * (Double)this.downAmplifier.getValue()) : 0.0);
        }
    }
}

