/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.render.shaders.impl.fill;

import com.gamesense.api.util.render.shaders.FramebufferShader;
import java.awt.Color;
import java.util.HashMap;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class GradientShader
extends FramebufferShader {
    public static final GradientShader INSTANCE = new GradientShader();
    public float time;

    public GradientShader() {
        super("gradient.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
        this.setupUniform("moreGradient");
        this.setupUniform("Creepy");
        this.setupUniform("alpha");
        this.setupUniform("NUM_OCTAVES");
    }

    public void updateUniforms(float duplicate, float moreGradient, float creepy, float alpha, int numOctaves) {
        GL20.glUniform2f((int)this.getUniform("resolution"), (float)((float)new ScaledResolution(this.mc).func_78326_a() / duplicate), (float)((float)new ScaledResolution(this.mc).func_78328_b() / duplicate));
        GL20.glUniform1f((int)this.getUniform("time"), (float)this.time);
        GL20.glUniform1f((int)this.getUniform("moreGradient"), (float)moreGradient);
        GL20.glUniform1f((int)this.getUniform("Creepy"), (float)creepy);
        GL20.glUniform1f((int)this.getUniform("alpha"), (float)alpha);
        GL20.glUniform1i((int)this.getUniform("NUM_OCTAVES"), (int)numOctaves);
    }

    public void stopDraw(Color color, float radius, float quality, float duplicate, float moreGradient, float creepy, float alpha, int numOctaves) {
        this.mc.field_71474_y.field_181151_V = this.entityShadows;
        this.framebuffer.func_147609_e();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        this.mc.func_147110_a().func_147610_a(true);
        this.red = (float)color.getRed() / 255.0f;
        this.green = (float)color.getGreen() / 255.0f;
        this.blue = (float)color.getBlue() / 255.0f;
        this.radius = radius;
        this.quality = quality;
        this.mc.field_71460_t.func_175072_h();
        RenderHelper.func_74518_a();
        this.startShader(duplicate, moreGradient, creepy, alpha, numOctaves);
        this.mc.field_71460_t.func_78478_c();
        this.drawFramebuffer(this.framebuffer);
        this.stopShader();
        this.mc.field_71460_t.func_175072_h();
        GlStateManager.func_179121_F();
        GlStateManager.func_179099_b();
    }

    public void startShader(float duplicate, float moreGradient, float creepy, float alpha, int numOctaves) {
        GL11.glPushMatrix();
        GL20.glUseProgram((int)this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap();
            this.setupUniforms();
        }
        this.updateUniforms(duplicate, moreGradient, creepy, alpha, numOctaves);
    }

    public void update(double speed) {
        this.time = (float)((double)this.time + speed);
    }
}

