/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.render.shaders.impl.outline;

import com.gamesense.api.util.render.shaders.FramebufferShader;
import java.awt.Color;
import java.util.HashMap;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public final class SmokeOutlineShader
extends FramebufferShader {
    public static final SmokeOutlineShader INSTANCE = new SmokeOutlineShader();
    public float time = 0.0f;

    public SmokeOutlineShader() {
        super("smokeOutline.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("texture");
        this.setupUniform("texelSize");
        this.setupUniform("divider");
        this.setupUniform("radius");
        this.setupUniform("maxSample");
        this.setupUniform("alpha0");
        this.setupUniform("resolution");
        this.setupUniform("time");
        this.setupUniform("first");
        this.setupUniform("second");
        this.setupUniform("third");
        this.setupUniform("oct");
    }

    public void updateUniforms(Color first, float radius, float quality, boolean gradientAlpha, int alphaOutline, float duplicate, Color second, Color third, int oct) {
        GL20.glUniform1i((int)this.getUniform("texture"), (int)0);
        GL20.glUniform2f((int)this.getUniform("texelSize"), (float)(1.0f / (float)this.mc.field_71443_c * (radius * quality)), (float)(1.0f / (float)this.mc.field_71440_d * (radius * quality)));
        GL20.glUniform1f((int)this.getUniform("divider"), (float)140.0f);
        GL20.glUniform1f((int)this.getUniform("radius"), (float)radius);
        GL20.glUniform1f((int)this.getUniform("maxSample"), (float)10.0f);
        GL20.glUniform1f((int)this.getUniform("alpha0"), (float)(gradientAlpha ? -1.0f : (float)alphaOutline / 255.0f));
        GL20.glUniform2f((int)this.getUniform("resolution"), (float)((float)new ScaledResolution(this.mc).func_78326_a() / duplicate), (float)((float)new ScaledResolution(this.mc).func_78328_b() / duplicate));
        GL20.glUniform1f((int)this.getUniform("time"), (float)this.time);
        GL20.glUniform4f((int)this.getUniform("first"), (float)((float)first.getRed() / 255.0f * 5.0f), (float)((float)first.getGreen() / 255.0f * 5.0f), (float)((float)first.getBlue() / 255.0f * 5.0f), (float)((float)first.getAlpha() / 255.0f));
        GL20.glUniform3f((int)this.getUniform("second"), (float)((float)second.getRed() / 255.0f * 5.0f), (float)((float)second.getGreen() / 255.0f * 5.0f), (float)((float)second.getBlue() / 255.0f * 5.0f));
        GL20.glUniform3f((int)this.getUniform("third"), (float)((float)third.getRed() / 255.0f * 5.0f), (float)((float)third.getGreen() / 255.0f * 5.0f), (float)((float)third.getBlue() / 255.0f * 5.0f));
        GL20.glUniform1i((int)this.getUniform("oct"), (int)oct);
    }

    public void stopDraw(Color color, float radius, float quality, boolean gradientAlpha, int alphaOutline, float duplicate, Color second, Color third, int oct) {
        this.mc.field_71474_y.field_181151_V = this.entityShadows;
        this.framebuffer.func_147609_e();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        this.mc.func_147110_a().func_147610_a(true);
        this.mc.field_71460_t.func_175072_h();
        RenderHelper.func_74518_a();
        this.startShader(color, radius, quality, gradientAlpha, alphaOutline, duplicate, second, third, oct);
        this.mc.field_71460_t.func_78478_c();
        this.drawFramebuffer(this.framebuffer);
        this.stopShader();
        this.mc.field_71460_t.func_175072_h();
        GlStateManager.func_179121_F();
        GlStateManager.func_179099_b();
    }

    public void startShader(Color color, float radius, float quality, boolean gradientAlpha, int alphaOutline, float duplicate, Color second, Color third, int oct) {
        GL11.glPushMatrix();
        GL20.glUseProgram((int)this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap();
            this.setupUniforms();
        }
        this.updateUniforms(color, radius, quality, gradientAlpha, alphaOutline, duplicate, second, third, oct);
    }

    public void update(double speed) {
        this.time = (float)((double)this.time + speed);
    }
}

