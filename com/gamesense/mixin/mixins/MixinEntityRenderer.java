/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.AspectEvent;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.NoEntityTrace;
import com.gamesense.client.module.modules.render.Ambience;
import com.gamesense.client.module.modules.render.NoRender;
import com.gamesense.client.module.modules.render.RenderTweaks;
import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderer.class})
public class MixinEntityRenderer {
    @Shadow
    @Final
    private int[] field_78504_Q;

    @Redirect(method={"orientCamera"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;"))
    public RayTraceResult rayTraceBlocks(WorldClient world, Vec3d start, Vec3d end) {
        RenderTweaks renderTweaks = ModuleManager.getModule(RenderTweaks.class);
        if (renderTweaks.isEnabled() && ((Boolean)renderTweaks.viewClip.getValue()).booleanValue()) {
            return null;
        }
        return world.func_72933_a(start, end);
    }

    @Redirect(method={"setupCameraTransform"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onSetupCameraTransform(float fovy, float aspect, float zNear, float zFar) {
        AspectEvent aspectEvent = new AspectEvent((float)Minecraft.func_71410_x().field_71443_c / (float)Minecraft.func_71410_x().field_71440_d);
        GameSense.EVENT_BUS.post(aspectEvent);
        Project.gluPerspective((float)fovy, (float)aspectEvent.getAspect(), (float)zNear, (float)zFar);
    }

    @Redirect(method={"renderWorldPass"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderWorldPass(float fovy, float aspect, float zNear, float zFar) {
        AspectEvent aspectEvent = new AspectEvent((float)Minecraft.func_71410_x().field_71443_c / (float)Minecraft.func_71410_x().field_71440_d);
        GameSense.EVENT_BUS.post(aspectEvent);
        Project.gluPerspective((float)fovy, (float)aspectEvent.getAspect(), (float)zNear, (float)zFar);
    }

    @Redirect(method={"renderCloudsCheck"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderCloudsCheck(float fovy, float aspect, float zNear, float zFar) {
        AspectEvent aspectEvent = new AspectEvent((float)Minecraft.func_71410_x().field_71443_c / (float)Minecraft.func_71410_x().field_71440_d);
        GameSense.EVENT_BUS.post(aspectEvent);
        Project.gluPerspective((float)fovy, (float)aspectEvent.getAspect(), (float)zNear, (float)zFar);
    }

    @Redirect(method={"getMouseOver"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate<? super Entity> predicate) {
        if (ModuleManager.getModule(NoEntityTrace.class).noTrace()) {
            return new ArrayList<Entity>();
        }
        return worldClient.func_175674_a(entityIn, boundingBox, predicate);
    }

    @Inject(method={"hurtCameraEffect"}, at={@At(value="HEAD")}, cancellable=true)
    public void hurtCameraEffect(float ticks, CallbackInfo callbackInfo) {
        NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && ((Boolean)noRender.hurtCam.getValue()).booleanValue()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method={"updateLightmap"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/texture/DynamicTexture;updateDynamicTexture()V", shift=At.Shift.BEFORE)})
    private void updateTextureHook(float partialTicks, CallbackInfo ci) {
        Ambience ambience = ModuleManager.getModule(Ambience.class);
        if (ambience.isEnabled()) {
            for (int i = 0; i < this.field_78504_Q.length; ++i) {
                GSColor ambientColor = ambience.colorLight.getValue();
                int alpha = ambientColor.getAlpha();
                float modifier = (float)alpha / 255.0f;
                int color = this.field_78504_Q[i];
                int[] bgr = this.toRGBAArray(color);
                Vector3f values = new Vector3f((float)bgr[2] / 255.0f, (float)bgr[1] / 255.0f, (float)bgr[0] / 255.0f);
                Vector3f newValues = new Vector3f((float)ambientColor.getRed() / 255.0f, (float)ambientColor.getGreen() / 255.0f, (float)ambientColor.getBlue() / 255.0f);
                Vector3f finalValues = this.mix(values, newValues, modifier);
                int red = (int)(finalValues.x * 255.0f);
                int green = (int)(finalValues.y * 255.0f);
                int blue = (int)(finalValues.z * 255.0f);
                this.field_78504_Q[i] = 0xFF000000 | red << 16 | green << 8 | blue;
            }
        }
    }

    private int[] toRGBAArray(int colorBuffer) {
        return new int[]{colorBuffer >> 16 & 0xFF, colorBuffer >> 8 & 0xFF, colorBuffer & 0xFF};
    }

    private Vector3f mix(Vector3f first, Vector3f second, float factor) {
        return new Vector3f(first.x * (1.0f - factor) + second.x * factor, first.y * (1.0f - factor) + second.y * factor, first.z * (1.0f - factor) + first.z * factor);
    }
}

