/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.NewRenderEntityEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.awt.Color;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@Module.Declaration(name="Chams2", category=Category.Render)
public class Chams2
extends Module {
    final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    BooleanSetting self = this.registerBoolean("Self", false);
    BooleanSetting crystals = this.registerBoolean("Crystal", false);
    BooleanSetting players = this.registerBoolean("Players", false);
    BooleanSetting customBlendFunc = this.registerBoolean("customBlendFunc", false);
    BooleanSetting playerImage = this.registerBoolean("playerImage", false);
    BooleanSetting playerCancel = this.registerBoolean("playerCancel", false);
    BooleanSetting crystalCancel = this.registerBoolean("crystalCancel", false);
    BooleanSetting playerTexture = this.registerBoolean("playerTexture", false);
    BooleanSetting crystalTexture = this.registerBoolean("crystalTexture", false);
    BooleanSetting playerSecondaryTexture = this.registerBoolean("playerSecondaryTexture", false);
    BooleanSetting crystalSecondaryTexture = this.registerBoolean("crystalSecondaryTexture", false);
    ColorSetting playerSecondaryTextureColor = this.registerColor("playerSecondaryTextureColor", new GSColor(255, 255, 255, 255), () -> true);
    ColorSetting crystalSecondaryTextureColor = this.registerColor("crystalSecondaryTextureColor", new GSColor(255, 255, 255, 255), () -> true);
    ColorSetting friendLine = this.registerColor("friendLine", new GSColor(255, 255, 255, 255), () -> true, true);
    ColorSetting playerLine = this.registerColor("playerLine", new GSColor(255, 255, 255, 255), () -> true, true);
    ColorSetting crystalLine1 = this.registerColor("crystalLine1", new GSColor(255, 255, 255, 255), () -> true, true);
    ColorSetting friendFill = this.registerColor("friendFill", new GSColor(255, 255, 255, 255), () -> true, true);
    ColorSetting playerFill = this.registerColor("playerFill", new GSColor(255, 255, 255, 255), () -> true, true);
    ColorSetting crystalFill1 = this.registerColor("crystalFill1", new GSColor(255, 255, 255, 255), () -> true, true);
    BooleanSetting playerGlint = this.registerBoolean("playerGlint", false);
    BooleanSetting crystalGlint = this.registerBoolean("crystalGlint", false);
    ColorSetting crystalGlint1 = this.registerColor("crystalGlint1", new GSColor(255, 255, 255, 255), () -> true, true);
    ColorSetting playerGlintColor = this.registerColor("playerGlintColor", new GSColor(255, 255, 255, 255), () -> true, true);
    ColorSetting friendGlintColor = this.registerColor("friendGlintColor", new GSColor(255, 255, 255, 255), () -> true, true);
    DoubleSetting crystalRotateSpeed = this.registerDouble("crystalRotateSpeed", 1.0, 0.0, 2.0);
    DoubleSetting crystalScale = this.registerDouble("crystalScale", 1.0, 0.0, 2.0);
    DoubleSetting lineWidth = this.registerDouble("lineWidth", 1.0, 0.0, 4.0);
    DoubleSetting lineWidthInterp = this.registerDouble("lineWidthInterp", 1.0, 0.1, 4.0);
    boolean cancel = false;
    Action currentAction = Action.NONE;
    @EventHandler
    private final Listener<NewRenderEntityEvent> renderEntityHeadEventListener = new Listener<NewRenderEntityEvent>(event -> {
        GSColor gSColor;
        GSColor line;
        if (Chams2.mc.field_71439_g == null || Chams2.mc.field_71441_e == null || event.entityIn == null) {
            return;
        }
        if (event.entityIn instanceof EntityPlayer) {
            if (event.entityIn == Chams2.mc.field_71439_g ? (Boolean)this.self.getValue() == false : (Boolean)this.players.getValue() == false) {
                return;
            }
        } else if (event.entityIn instanceof EntityEnderCrystal) {
            if (!((Boolean)this.crystals.getValue()).booleanValue()) {
                return;
            }
        } else {
            return;
        }
        this.prepare();
        GL11.glPushAttrib((int)1048575);
        if (((Boolean)this.customBlendFunc.getValue()).booleanValue()) {
            GL11.glBlendFunc((int)770, (int)32772);
        }
        GL11.glEnable((int)2881);
        GL11.glEnable((int)2848);
        boolean image = !(event.entityIn instanceof EntityEnderCrystal) && (Boolean)this.playerImage.getValue() != false;
        boolean cancelRender = event.entityIn instanceof EntityLivingBase ? ((Boolean)this.playerCancel.getValue()).booleanValue() : ((Boolean)this.crystalCancel.getValue()).booleanValue();
        boolean texture2d = event.entityIn instanceof EntityLivingBase ? ((Boolean)this.playerTexture.getValue()).booleanValue() : ((Boolean)this.crystalTexture.getValue()).booleanValue();
        boolean secondaryTexture = event.entityIn instanceof EntityLivingBase ? ((Boolean)this.playerSecondaryTexture.getValue()).booleanValue() : ((Boolean)this.crystalSecondaryTexture.getValue()).booleanValue();
        GSColor secondaryTextureColor = event.entityIn instanceof EntityLivingBase ? this.playerSecondaryTextureColor.getValue() : this.crystalSecondaryTextureColor.getValue();
        GSColor color = secondaryTextureColor;
        GSColor gSColor2 = event.entityIn instanceof EntityLivingBase ? (SocialManager.isFriend(event.entityIn.func_70005_c_()) ? this.friendLine.getValue() : this.playerLine.getValue()) : (line = (gSColor = this.crystalLine1.getValue()));
        GSColor fill = event.entityIn instanceof EntityLivingBase ? (SocialManager.isFriend(event.entityIn.func_70005_c_()) ? this.friendFill.getValue() : this.playerFill.getValue()) : this.crystalFill1.getValue();
        boolean texture = event.entityIn instanceof EntityLivingBase ? ((Boolean)this.playerGlint.getValue()).booleanValue() : ((Boolean)this.crystalGlint.getValue()).booleanValue();
        boolean bl = texture;
        GSColor textureColor = event.entityIn instanceof EntityLivingBase ? (SocialManager.isFriend(event.entityIn.func_70005_c_()) ? this.friendGlintColor.getValue() : this.playerGlintColor.getValue()) : this.crystalGlint1.getValue();
        float limbSwingAmt = event.entityIn instanceof EntityEnderCrystal ? event.limbSwingAmount * ((Double)this.crystalRotateSpeed.getValue()).floatValue() : event.limbSwingAmount;
        float scale = event.entityIn instanceof EntityEnderCrystal ? ((Double)this.crystalScale.getValue()).floatValue() : event.scale;
        GlStateManager.func_187441_d((float)this.getInterpolatedLinWid(Chams2.mc.field_71439_g.func_70032_d(event.entityIn) + 1.0f, ((Double)this.lineWidth.getValue()).floatValue(), ((Double)this.lineWidthInterp.getValue()).floatValue()));
        if (!image) {
            GlStateManager.func_179118_c();
            this.glColor(fill);
            if (texture2d) {
                GL11.glEnable((int)3553);
            } else {
                GL11.glDisable((int)3553);
            }
            this.currentAction = Action.FILL;
            event.modelBase.func_78088_a(event.entityIn, event.limbSwing, limbSwingAmt, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
            GL11.glDisable((int)3553);
            if (secondaryTexture) {
                this.currentAction = Action.NONE;
                this.glColor(secondaryTextureColor);
                event.modelBase.func_78088_a(event.entityIn, event.limbSwing, limbSwingAmt, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
            }
            GL11.glPolygonMode((int)1032, (int)6913);
            this.currentAction = Action.LINE;
            this.glColor(line);
            event.modelBase.func_78088_a(event.entityIn, event.limbSwing, limbSwingAmt, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
            this.currentAction = Action.GLINT;
            GL11.glPolygonMode((int)1032, (int)6914);
            if (texture) {
                mc.func_110434_K().func_110577_a(this.RES_ITEM_GLINT);
                GL11.glEnable((int)3553);
                GL11.glBlendFunc((int)768, (int)771);
                this.glColor(textureColor);
                event.modelBase.func_78088_a(event.entityIn, event.limbSwing, limbSwingAmt, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
                if (((Boolean)this.customBlendFunc.getValue()).booleanValue()) {
                    GL11.glBlendFunc((int)770, (int)32772);
                } else {
                    GL11.glBlendFunc((int)770, (int)771);
                }
            }
            if (event.entityIn instanceof EntityLivingBase) {
                // empty if block
            }
            event.limbSwingAmount = limbSwingAmt;
            this.currentAction = Action.NONE;
        }
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.func_179099_b();
        this.release();
        if (cancelRender) {
            event.cancel();
        }
    }, new Predicate[0]);

    void glColor(Color color) {
        GL11.glColor4f((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
    }

    void prepare() {
        GlStateManager.func_179094_E();
        GlStateManager.func_179097_i();
        GlStateManager.func_179140_f();
        GlStateManager.func_179132_a((boolean)false);
        GlStateManager.func_179118_c();
        GlStateManager.func_179129_p();
        GlStateManager.func_179147_l();
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glBlendFunc((int)770, (int)771);
    }

    void release() {
        GlStateManager.func_179132_a((boolean)true);
        GlStateManager.func_179145_e();
        GlStateManager.func_179126_j();
        GlStateManager.func_179141_d();
        GlStateManager.func_179121_F();
        GL11.glEnable((int)3553);
        GL11.glPolygonMode((int)1032, (int)6914);
    }

    float getInterpolatedLinWid(float distance, float line, float lineFactor) {
        return line * lineFactor / distance;
    }

    static enum Action {
        FILL,
        LINE,
        GLINT,
        NONE;

    }
}

