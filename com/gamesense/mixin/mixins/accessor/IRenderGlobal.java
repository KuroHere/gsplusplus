/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins.accessor;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.shader.ShaderGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={RenderGlobal.class})
public interface IRenderGlobal {
    @Accessor(value="entityOutlineShader")
    public ShaderGroup getEntityOutlineShader();
}

