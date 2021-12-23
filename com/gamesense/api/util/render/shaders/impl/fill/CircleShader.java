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

public class CircleShader
extends FramebufferShader {
    public static final CircleShader INSTANCE = new CircleShader();
    public float time;

    public CircleShader() {
        super("circle.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
        this.setupUniform("colors");
        this.setupUniform("PI");
        this.setupUniform("rad");
    }

    public void updateUniforms(float duplicate, Color color, Double PI, Double rad) {
        GL20.glUniform2f((int)this.getUniform("resolution"), (float)((float)new ScaledResolution(this.mc).func_78326_a() / duplicate), (float)((float)new ScaledResolution(this.mc).func_78328_b() / duplicate));
        GL20.glUniform1f((int)this.getUniform("time"), (float)this.time);
        GL20.glUniform1f((int)this.getUniform("PI"), (float)PI.floatValue());
        GL20.glUniform1f((int)this.getUniform("rad"), (float)rad.floatValue());
        GL20.glUniform4f((int)this.getUniform("colors"), (float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
    }

    public void stopDraw(float duplicate, Color color, Double PI, Double rad) {
        this.mc.field_71474_y.field_181151_V = this.entityShadows;
        this.framebuffer.func_147609_e();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        this.mc.func_147110_a().func_147610_a(true);
        this.mc.field_71460_t.func_175072_h();
        RenderHelper.func_74518_a();
        this.startShader(duplicate, color, PI, rad);
        this.mc.field_71460_t.func_78478_c();
        this.drawFramebuffer(this.framebuffer);
        this.stopShader();
        this.mc.field_71460_t.func_175072_h();
        GlStateManager.func_179121_F();
        GlStateManager.func_179099_b();
    }

    public void startShader(float duplicate, Color color, Double PI, Double rad) {
        GL11.glPushMatrix();
        GL20.glUseProgram((int)this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap();
            this.setupUniforms();
        }
        this.updateUniforms(duplicate, color, PI, rad);
    }

    public void update(double speed) {
        this.time = (float)((double)this.time + speed);
    }
}

