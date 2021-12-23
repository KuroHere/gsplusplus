/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.api.config.SaveConfig;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.MultiTask;
import com.gamesense.mixin.mixins.accessor.AccessorEntityPlayerSP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Minecraft.class})
public class MixinMinecraft {
    @Shadow
    public EntityPlayerSP field_71439_g;
    @Shadow
    public PlayerControllerMP field_71442_b;
    private boolean handActive = false;
    private boolean isHittingBlock = false;

    @Inject(method={"rightClickMouse"}, at={@At(value="HEAD")})
    public void rightClickMousePre(CallbackInfo ci) {
        if (ModuleManager.isModuleEnabled(MultiTask.class)) {
            this.isHittingBlock = this.field_71442_b.func_181040_m();
            this.field_71442_b.field_78778_j = false;
        }
    }

    @Inject(method={"rightClickMouse"}, at={@At(value="RETURN")})
    public void rightClickMousePost(CallbackInfo ci) {
        if (ModuleManager.isModuleEnabled(MultiTask.class) && !this.field_71442_b.func_181040_m()) {
            this.field_71442_b.field_78778_j = this.isHittingBlock;
        }
    }

    @Inject(method={"sendClickBlockToController"}, at={@At(value="HEAD")})
    public void sendClickBlockToControllerPre(boolean leftClick, CallbackInfo ci) {
        if (ModuleManager.isModuleEnabled(MultiTask.class)) {
            this.handActive = this.field_71439_g.func_184587_cr();
            ((AccessorEntityPlayerSP)this.field_71439_g).gsSetHandActive(false);
        }
    }

    @Inject(method={"sendClickBlockToController"}, at={@At(value="RETURN")})
    public void sendClickBlockToControllerPost(boolean leftClick, CallbackInfo ci) {
        if (ModuleManager.isModuleEnabled(MultiTask.class) && !this.field_71439_g.func_184587_cr()) {
            ((AccessorEntityPlayerSP)this.field_71439_g).gsSetHandActive(this.handActive);
        }
    }

    @Inject(method={"crashed"}, at={@At(value="HEAD")})
    public void crashed(CrashReport crash, CallbackInfo callbackInfo) {
        SaveConfig.init();
    }

    @Inject(method={"shutdown"}, at={@At(value="HEAD")})
    public void shutdown(CallbackInfo callbackInfo) {
        SaveConfig.init();
    }
}

