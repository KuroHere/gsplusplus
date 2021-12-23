/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.RainbowEnchant;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={LayerArmorBase.class})
public abstract class MixinLayerArmorBase {
    @Redirect(method={"renderEnchantedGlint"}, at=@At(value="INVOKE", target="net/minecraft/client/renderer/GlStateManager.color(FFFF)V"))
    private static void renderEnchantedGlint(float a2, float a3, float a4, float v1) {
        RainbowEnchant rainbowEnchant = ModuleManager.getModule(RainbowEnchant.class);
        if (rainbowEnchant.isEnabled()) {
            a2 = (float)rainbowEnchant.color.getValue().getRed() / 255.0f;
            a4 = (float)rainbowEnchant.color.getValue().getGreen() / 255.0f;
            a3 = (float)rainbowEnchant.color.getValue().getBlue() / 255.0f;
            v1 = (float)rainbowEnchant.color.getValue().getAlpha() / 255.0f;
        }
        GlStateManager.func_179131_c((float)a2, (float)a4, (float)a3, (float)v1);
    }
}

