/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.RainbowEnchant;
import java.awt.Color;
import net.minecraft.client.renderer.RenderItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value={RenderItem.class})
public class MixinRenderItem {
    int a2;
    int a4;
    int a3;

    @ModifyArg(method={"renderEffect"}, at=@At(value="INVOKE", target="net/minecraft/client/renderer/RenderItem.renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;I)V"), index=1)
    private int renderEffect(int oldValue) {
        RainbowEnchant rainbowEnchant = ModuleManager.getModule(RainbowEnchant.class);
        if (rainbowEnchant.isEnabled()) {
            this.a2 = rainbowEnchant.color.getValue().getRed();
            this.a4 = rainbowEnchant.color.getValue().getGreen();
            this.a3 = rainbowEnchant.color.getValue().getBlue();
            return new Color(this.a2, this.a4, this.a3).getRGB();
        }
        return oldValue;
    }
}

