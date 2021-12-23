/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.NewRenderEntityEvent;
import com.gamesense.client.GameSense;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={RenderEnderCrystal.class})
public class MixinRenderCrystal {
    @Redirect(method={"doRender"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderModelBaseHook(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        NewRenderEntityEvent event = new NewRenderEntityEvent(modelBase, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        GameSense.EVENT_BUS.post(event);
        if (!event.isCancelled()) {
            modelBase.func_78088_a(entityIn, limbSwing, event.limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }
}

