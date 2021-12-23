/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.ShaderColorEvent;
import com.gamesense.client.GameSense;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Render.class})
public class MixinRender<T extends Entity> {
    @Inject(method={"getTeamColor"}, at={@At(value="HEAD")}, cancellable=true)
    public void getTeamColor(T entity, CallbackInfoReturnable<Integer> info) {
        ShaderColorEvent shaderColorEvent = new ShaderColorEvent((Entity)entity);
        GameSense.EVENT_BUS.post(shaderColorEvent);
        if (shaderColorEvent.isCancelled()) {
            info.cancel();
            info.setReturnValue(shaderColorEvent.getColor().getRGB());
        }
    }
}

