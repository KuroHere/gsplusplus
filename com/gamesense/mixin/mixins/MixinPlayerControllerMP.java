/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.BlockResetEvent;
import com.gamesense.api.event.events.DamageBlockEvent;
import com.gamesense.api.event.events.DestroyBlockEvent;
import com.gamesense.api.event.events.ReachDistanceEvent;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AutoArmor;
import com.gamesense.client.module.modules.combat.OffHand;
import com.gamesense.client.module.modules.exploits.PacketUtils;
import com.gamesense.client.module.modules.render.noGlitchBlock;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={PlayerControllerMP.class})
public abstract class MixinPlayerControllerMP {
    @Shadow
    public abstract void func_78750_j();

    @Inject(method={"resetBlockRemoving"}, at={@At(value="HEAD")}, cancellable=true)
    private void resetBlockWrapper(CallbackInfo callbackInfo) {
        BlockResetEvent uwu = new BlockResetEvent();
        GameSense.EVENT_BUS.post(uwu);
        if (uwu.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method={"onPlayerDestroyBlock"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/World;playEvent(ILnet/minecraft/util/math/BlockPos;I)V")}, cancellable=true)
    private void onPlayerDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        noGlitchBlock noGlitchBlock2 = ModuleManager.getModule(noGlitchBlock.class);
        if (noGlitchBlock2.isEnabled() && ((Boolean)noGlitchBlock2.breakBlock.getValue()).booleanValue()) {
            callbackInfoReturnable.cancel();
            callbackInfoReturnable.setReturnValue(false);
        }
        GameSense.EVENT_BUS.post(new DestroyBlockEvent(pos));
    }

    @Inject(method={"onPlayerDamageBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z"}, at={@At(value="HEAD")}, cancellable=true)
    private void onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        DamageBlockEvent event = new DamageBlockEvent(posBlock, directionFacing);
        GameSense.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(method={"getBlockReachDistance"}, at={@At(value="RETURN")}, cancellable=true)
    private void getReachDistanceHook(CallbackInfoReturnable<Float> distance) {
        ReachDistanceEvent reachDistanceEvent = new ReachDistanceEvent(distance.getReturnValue().floatValue());
        GameSense.EVENT_BUS.post(reachDistanceEvent);
        distance.setReturnValue(Float.valueOf(reachDistanceEvent.getDistance()));
    }

    @Inject(method={"onStoppedUsingItem"}, at={@At(value="HEAD")}, cancellable=true)
    public void onStoppedUsingItem(EntityPlayer playerIn, CallbackInfo ci) {
        PacketUtils packetUtils = ModuleManager.getModule(PacketUtils.class);
        OffHand offHand = ModuleManager.getModule(OffHand.class);
        AutoArmor armor = ModuleManager.getModule(AutoArmor.class);
        if (packetUtils.isEnabled() && ((!offHand.dontMove || !armor.dontMove) && ((Boolean)packetUtils.food.getValue()).booleanValue() && playerIn.func_184586_b(playerIn.func_184600_cs()).func_77973_b() instanceof ItemFood || ((Boolean)packetUtils.potion.getValue()).booleanValue() && playerIn.func_184586_b(playerIn.func_184600_cs()).func_77973_b() instanceof ItemPotion || ((Boolean)packetUtils.all.getValue()).booleanValue())) {
            this.func_78750_j();
            playerIn.func_184597_cx();
            ci.cancel();
        }
    }
}

