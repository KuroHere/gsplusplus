/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.ControlEvent;
import com.gamesense.client.GameSense;
import net.minecraft.entity.passive.EntityPig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EntityPig.class})
public class MixinEntityPig {
    @Inject(method={"canBeSteered"}, at={@At(value="HEAD")}, cancellable=true)
    public void canBeSteered(CallbackInfoReturnable<Boolean> cir) {
        ControlEvent event = new ControlEvent();
        GameSense.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            cir.cancel();
            cir.setReturnValue(true);
        }
    }

    @Inject(method={"getSaddled"}, at={@At(value="HEAD")}, cancellable=true)
    public void getSaddled(CallbackInfoReturnable<Boolean> cir) {
        ControlEvent event = new ControlEvent();
        GameSense.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            cir.cancel();
            cir.setReturnValue(true);
        }
    }
}

