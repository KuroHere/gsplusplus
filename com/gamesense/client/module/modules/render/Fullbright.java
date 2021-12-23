/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Arrays;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@Module.Declaration(name="Fullbright", category=Category.Render)
public class Fullbright
extends Module {
    ModeSetting mode = this.registerMode("Mode", Arrays.asList("Gamma", "Potion"), "Gamma");
    float oldGamma;

    @Override
    public void onEnable() {
        this.oldGamma = Fullbright.mc.field_71474_y.field_74333_Y;
    }

    @Override
    public void onUpdate() {
        if (((String)this.mode.getValue()).equalsIgnoreCase("Gamma")) {
            Fullbright.mc.field_71474_y.field_74333_Y = 666.0f;
            Fullbright.mc.field_71439_g.func_184589_d(Potion.func_188412_a((int)16));
        } else if (((String)this.mode.getValue()).equalsIgnoreCase("Potion")) {
            PotionEffect potionEffect = new PotionEffect(Potion.func_188412_a((int)16), 123456789, 5);
            potionEffect.func_100012_b(true);
            Fullbright.mc.field_71439_g.func_70690_d(potionEffect);
        }
    }

    @Override
    public void onDisable() {
        Fullbright.mc.field_71474_y.field_74333_Y = this.oldGamma;
        Fullbright.mc.field_71439_g.func_184589_d(Potion.func_188412_a((int)16));
    }
}

