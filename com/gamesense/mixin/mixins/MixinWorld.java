/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.NoRender;
import com.gamesense.client.module.modules.render.noGlitchBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={World.class})
public class MixinWorld {
    @Inject(method={"checkLightFor"}, at={@At(value="HEAD")}, cancellable=true)
    private void updateLightmapHook(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && ((Boolean)noRender.noSkylight.getValue()).booleanValue()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(method={"setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"}, at={@At(value="HEAD")}, cancellable=true)
    private void setBlockState(BlockPos pos, IBlockState newState, int flags, CallbackInfoReturnable<Boolean> cir) {
        noGlitchBlock noGlitchBlock2 = ModuleManager.getModule(noGlitchBlock.class);
        if (noGlitchBlock2.isEnabled() && ((Boolean)noGlitchBlock2.placeBlock.getValue()).booleanValue() && flags != 3) {
            cir.cancel();
            cir.setReturnValue(false);
        }
    }

    @Inject(method={"getThunderStrength"}, at={@At(value="HEAD")}, cancellable=true)
    private void getThunderStrengthHead(float delta, CallbackInfoReturnable<Float> cir) {
        if (((Boolean)ModuleManager.getModule(NoRender.class).noWeather.getValue()).booleanValue() && !((String)ModuleManager.getModule(NoRender.class).weather.getValue()).equals("Thunder")) {
            cir.setReturnValue(Float.valueOf(0.0f));
        }
    }

    @Inject(method={"getRainStrength"}, at={@At(value="HEAD")}, cancellable=true)
    private void getRainStrengthHead(float delta, CallbackInfoReturnable<Float> cir) {
        if (((Boolean)ModuleManager.getModule(NoRender.class).noWeather.getValue()).booleanValue() && ((String)ModuleManager.getModule(NoRender.class).weather.getValue()).equals("Clear")) {
            cir.setReturnValue(Float.valueOf(0.0f));
        }
    }
}

