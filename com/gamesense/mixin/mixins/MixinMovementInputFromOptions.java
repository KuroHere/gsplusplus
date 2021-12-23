/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.PlayerTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={MovementInputFromOptions.class}, priority=10000)
public abstract class MixinMovementInputFromOptions
extends MovementInput {
    @Redirect(method={"updatePlayerMoveState"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z"))
    public boolean isKeyPressed(KeyBinding keyBinding) {
        PlayerTweaks playerTweaks;
        int keyCode = keyBinding.func_151463_i();
        if (keyCode > 0 && keyCode < 256 && (playerTweaks = ModuleManager.getModule(PlayerTweaks.class)).isEnabled() && ((Boolean)playerTweaks.guiMove.getValue()).booleanValue() && Minecraft.func_71410_x().field_71462_r != null && !(Minecraft.func_71410_x().field_71462_r instanceof GuiChat) && keyCode != Minecraft.func_71410_x().field_71474_y.field_74311_E.func_151463_i()) {
            return Keyboard.isKeyDown((int)keyCode);
        }
        return keyBinding.func_151470_d();
    }
}

