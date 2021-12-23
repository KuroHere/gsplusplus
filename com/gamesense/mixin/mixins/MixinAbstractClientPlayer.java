/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.api.util.render.CapeUtil;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.CapesModule;
import com.gamesense.client.module.modules.render.Shaders;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={AbstractClientPlayer.class})
public abstract class MixinAbstractClientPlayer {
    private String me = null;

    @Shadow
    @Nullable
    protected abstract NetworkPlayerInfo func_175155_b();

    @Inject(method={"getLocationCape"}, at={@At(value="HEAD")}, cancellable=true)
    public void getLocationCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        if (!ModuleManager.getModule(Shaders.class).renderCape) {
            callbackInfoReturnable.cancel();
            return;
        }
        UUID uuid = this.func_175155_b().func_178845_a().getId();
        CapesModule capesModule = ModuleManager.getModule(CapesModule.class);
        if (CapeUtil.hasCape(uuid)) {
            if (this.me == null) {
                this.me = CapesModule.getUsName();
            }
            if (this.func_175155_b().func_178845_a().getName().equals(this.me) && !capesModule.isEnabled()) {
                return;
            }
            if (((String)capesModule.capeMode.getValue()).equalsIgnoreCase("Old")) {
                callbackInfoReturnable.setReturnValue(CapeUtil.capes.get(0));
            } else if (((String)capesModule.capeMode.getValue()).equalsIgnoreCase("New")) {
                callbackInfoReturnable.setReturnValue(CapeUtil.capes.get(1));
            } else {
                callbackInfoReturnable.setReturnValue(CapeUtil.capes.get(2));
            }
        }
    }
}

