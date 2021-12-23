/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.NewRenderEntityEvent;
import com.gamesense.api.event.events.RenderEntityEvent;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.NoRender;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderLivingBase.class})
public class MixinRenderLivingBase<T extends EntityLivingBase>
extends Render<T> {
    @Shadow
    protected ModelBase field_77045_g;
    protected final Minecraft mc = Minecraft.func_71410_x();
    private boolean isClustered;

    public MixinRenderLivingBase(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn);
        this.field_77045_g = modelBaseIn;
        this.field_76989_e = shadowSizeIn;
    }

    @Inject(method={"renderModel"}, at={@At(value="HEAD")}, cancellable=true)
    void doRender(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo ci) {
        NewRenderEntityEvent event = new NewRenderEntityEvent(this.field_77045_g, (Entity)entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        if (!this.func_180548_c((Entity)entityIn)) {
            return;
        }
        NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && ((Boolean)noRender.noCluster.getValue()).booleanValue() && this.mc.field_71439_g.func_70032_d(entityIn) < 1.0f && entityIn != this.mc.field_71439_g) {
            GlStateManager.func_187408_a((GlStateManager.Profile)GlStateManager.Profile.TRANSPARENT_MODEL);
            this.isClustered = true;
            if (!noRender.incrementNoClusterRender()) {
                ci.cancel();
            }
        } else {
            this.isClustered = false;
        }
        RenderEntityEvent.Head renderEntityHeadEvent = new RenderEntityEvent.Head((Entity)entityIn, RenderEntityEvent.Type.COLOR);
        GameSense.EVENT_BUS.post(renderEntityHeadEvent);
        GlStateManager.func_187408_a((GlStateManager.Profile)GlStateManager.Profile.TRANSPARENT_MODEL);
        GameSense.EVENT_BUS.post(event);
        GlStateManager.func_187440_b((GlStateManager.Profile)GlStateManager.Profile.TRANSPARENT_MODEL);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Nullable
    protected ResourceLocation getEntityTexture(@NotNull T entity) {
        return null;
    }

    @Inject(method={"renderModel"}, at={@At(value="RETURN")}, cancellable=true)
    protected void renderModelReturn(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo callbackInfo) {
        RenderEntityEvent.Return renderEntityReturnEvent = new RenderEntityEvent.Return((Entity)entitylivingbaseIn, RenderEntityEvent.Type.COLOR);
        GameSense.EVENT_BUS.post(renderEntityReturnEvent);
        if (!renderEntityReturnEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method={"renderLayers"}, at={@At(value="HEAD")}, cancellable=true)
    protected void renderLayers(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn, CallbackInfo callbackInfo) {
        if (this.isClustered && !ModuleManager.getModule(NoRender.class).getNoClusterRender()) {
            callbackInfo.cancel();
        }
    }

    @Redirect(method={"setBrightness"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V", ordinal=6))
    protected void glTexEnvi0(int target, int parameterName, int parameter) {
        if (!this.isClustered) {
            GlStateManager.func_187399_a((int)target, (int)parameterName, (int)parameter);
        }
    }

    @Redirect(method={"setBrightness"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V", ordinal=7))
    protected void glTexEnvi1(int target, int parameterName, int parameter) {
        if (!this.isClustered) {
            GlStateManager.func_187399_a((int)target, (int)parameterName, (int)parameter);
        }
    }

    @Redirect(method={"setBrightness"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V", ordinal=8))
    protected void glTexEnvi2(int target, int parameterName, int parameter) {
        if (!this.isClustered) {
            GlStateManager.func_187399_a((int)target, (int)parameterName, (int)parameter);
        }
    }
}

