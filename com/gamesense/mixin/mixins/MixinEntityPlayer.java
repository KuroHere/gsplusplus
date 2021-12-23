/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.PlayerJumpEvent;
import com.gamesense.api.event.events.WaterPushEvent;
import com.gamesense.client.GameSense;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EntityPlayer.class})
public abstract class MixinEntityPlayer {
    @Shadow
    public abstract String func_70005_c_();

    @Inject(method={"jump"}, at={@At(value="HEAD")}, cancellable=true)
    public void onJump(CallbackInfo callbackInfo) {
        if (Minecraft.func_71410_x().field_71439_g.func_70005_c_() == this.func_70005_c_()) {
            PlayerJumpEvent event = new PlayerJumpEvent();
            GameSense.EVENT_BUS.post(event);
            if (event.isCancelled()) {
                callbackInfo.cancel();
            }
        }
    }

    @Inject(method={"isPushedByWater"}, at={@At(value="HEAD")}, cancellable=true)
    private void onPushedByWater(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        WaterPushEvent event = new WaterPushEvent();
        GameSense.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }
}

