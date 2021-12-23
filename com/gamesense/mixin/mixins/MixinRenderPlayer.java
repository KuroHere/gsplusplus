/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.TargetHUD;
import com.gamesense.client.module.modules.hud.TargetInfo;
import com.gamesense.client.module.modules.render.Nametags;
import com.gamesense.client.module.modules.render.Shaders;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderPlayer.class})
public abstract class MixinRenderPlayer {
    @Inject(method={"renderEntityName"}, at={@At(value="HEAD")}, cancellable=true)
    private void renderLivingLabel(AbstractClientPlayer entity, double x, double y, double z, String name, double distanceSq, CallbackInfo callbackInfo) {
        if (entity.func_70005_c_().length() == 0) {
            callbackInfo.cancel();
        }
        if (ModuleManager.isModuleEnabled(Nametags.class)) {
            callbackInfo.cancel();
        }
        if (ModuleManager.isModuleEnabled(TargetHUD.class) && TargetHUD.isRenderingEntity((EntityPlayer)entity)) {
            callbackInfo.cancel();
        }
        if (ModuleManager.isModuleEnabled(TargetInfo.class) && TargetInfo.isRenderingEntity((EntityPlayer)entity)) {
            callbackInfo.cancel();
        }
        if (!ModuleManager.getModule(Shaders.class).renderTags) {
            callbackInfo.cancel();
        }
    }
}

