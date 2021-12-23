/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.render;

import com.gamesense.api.util.render.GSColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

public class ChamsUtil {
    private static final Minecraft mc = Minecraft.func_71410_x();

    public static void createChamsPre() {
        mc.func_175598_ae().func_178633_a(false);
        mc.func_175598_ae().func_178632_c(false);
        GlStateManager.func_179094_E();
        GlStateManager.func_179132_a((boolean)true);
        OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)240.0f, (float)240.0f);
        GL11.glEnable((int)32823);
        GL11.glDepthRange((double)0.0, (double)0.01);
        GlStateManager.func_179121_F();
    }

    public static void createChamsPost() {
        boolean shadow = mc.func_175598_ae().func_178627_a();
        mc.func_175598_ae().func_178633_a(shadow);
        GlStateManager.func_179094_E();
        GlStateManager.func_179132_a((boolean)false);
        GL11.glDisable((int)32823);
        GL11.glDepthRange((double)0.0, (double)1.0);
        GlStateManager.func_179121_F();
    }

    public static void createColorPre(GSColor color, boolean isPlayer) {
        mc.func_175598_ae().func_178633_a(false);
        mc.func_175598_ae().func_178632_c(false);
        GlStateManager.func_179094_E();
        GlStateManager.func_179132_a((boolean)true);
        OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)240.0f, (float)240.0f);
        GL11.glEnable((int)32823);
        GL11.glDepthRange((double)0.0, (double)0.01);
        GL11.glDisable((int)3553);
        if (!isPlayer) {
            GlStateManager.func_187408_a((GlStateManager.Profile)GlStateManager.Profile.TRANSPARENT_MODEL);
        }
        color.glColor();
        GlStateManager.func_179121_F();
    }

    public static void createColorPost(boolean isPlayer) {
        boolean shadow = mc.func_175598_ae().func_178627_a();
        mc.func_175598_ae().func_178633_a(shadow);
        GlStateManager.func_179094_E();
        GlStateManager.func_179132_a((boolean)false);
        if (!isPlayer) {
            GlStateManager.func_187440_b((GlStateManager.Profile)GlStateManager.Profile.TRANSPARENT_MODEL);
        }
        GL11.glDisable((int)32823);
        GL11.glDepthRange((double)0.0, (double)1.0);
        GL11.glEnable((int)3553);
        GlStateManager.func_179121_F();
    }

    public static void createWirePre(GSColor color, int lineWidth, boolean isPlayer) {
        mc.func_175598_ae().func_178633_a(false);
        mc.func_175598_ae().func_178632_c(false);
        GlStateManager.func_179094_E();
        GlStateManager.func_179132_a((boolean)true);
        OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)240.0f, (float)240.0f);
        GL11.glPolygonMode((int)1032, (int)6913);
        GL11.glEnable((int)10754);
        GL11.glDepthRange((double)0.0, (double)0.01);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2896);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        if (!isPlayer) {
            GlStateManager.func_187408_a((GlStateManager.Profile)GlStateManager.Profile.TRANSPARENT_MODEL);
        }
        GL11.glLineWidth((float)lineWidth);
        color.glColor();
        GlStateManager.func_179121_F();
    }

    public static void createWirePost(boolean isPlayer) {
        boolean shadow = mc.func_175598_ae().func_178627_a();
        mc.func_175598_ae().func_178633_a(shadow);
        GlStateManager.func_179094_E();
        GlStateManager.func_179132_a((boolean)false);
        if (!isPlayer) {
            GlStateManager.func_187440_b((GlStateManager.Profile)GlStateManager.Profile.TRANSPARENT_MODEL);
        }
        GL11.glPolygonMode((int)1032, (int)6914);
        GL11.glDisable((int)10754);
        GL11.glDepthRange((double)0.0, (double)1.0);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2896);
        GL11.glDisable((int)2848);
        GlStateManager.func_179121_F();
    }
}

