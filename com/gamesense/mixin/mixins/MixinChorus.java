/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.ChorusEvent;
import com.gamesense.client.GameSense;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemChorusFruit;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ItemChorusFruit.class})
public class MixinChorus
extends ItemFood {
    public MixinChorus(int amount, float saturation) {
        super(amount, saturation, false);
    }

    @Inject(method={"onItemUseFinish"}, at={@At(value="HEAD")})
    public void attemptTeleportHook(ItemStack stack, World worldIn, EntityLivingBase entityLiving, CallbackInfoReturnable<ItemStack> cir) {
        ChorusEvent event = new ChorusEvent();
        GameSense.EVENT_BUS.post(event);
    }
}

