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

public class FlowShader
extends FramebufferShader {
    public static final FlowShader INSTANCE = new FlowShader();
    public float time;

    public FlowShader() {
        super("flow.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
        this.setupUniform("color");
        this.setupUniform("iterations");
        this.setupUniform("formuparam2");
        this.setupUniform("stepsize");
        this.setupUniform("volsteps");
        this.setupUniform("zoom");
        this.setupUniform("tile");
        this.setupUniform("distfading");
        this.setupUniform("saturation");
        this.setupUniform("fadeBol");
    }

    public void updateUniforms(float duplicate, float red, float green, float blue, float alpha, int iteractions, float formuparam2, float zoom, float volumSteps, float stepSize, float title, float distfading, float saturation, float cloud, int fade) {
        GL20.glUniform2f((int)this.getUniform("resolution"), (float)((float)new ScaledResolution(this.mc).func_78326_a() / duplicate), (float)((float)new ScaledResolution(this.mc).func_78328_b() / duplicate));
        GL20.glUniform1f((int)this.getUniform("time"), (float)this.time);
        GL20.glUniform4f((int)this.getUniform("color"), (float)red, (float)green, (float)blue, (float)alpha);
        GL20.glUniform1i((int)this.getUniform("iterations"), (int)iteractions);
        GL20.glUniform1f((int)this.getUniform("formuparam2"), (float)formuparam2);
        GL20.glUniform1i((int)this.getUniform("volsteps"), (int)((int)volumSteps));
        GL20.glUniform1f((int)this.getUniform("stepsize"), (float)stepSize);
        GL20.glUniform1f((int)this.getUniform("zoom"), (float)zoom);
        GL20.glUniform1f((int)this.getUniform("tile"), (float)title);
        GL20.glUniform1f((int)this.getUniform("distfading"), (float)distfading);
        GL20.glUniform1f((int)this.getUniform("saturation"), (float)saturation);
        GL20.glUniform1i((int)this.getUniform("fadeBol"), (int)fade);
    }

    public void stopDraw(Color color, float radius, float quality, float duplicate, float red, float green, float blue, float alpha, int iteractions, float formuparam2, float zoom, float volumSteps, float stepSize, float title, float distfading, float saturation, float cloud, int fade) {
        this.mc.field_71474_y.field_181151_V = this.entityShadows;
        this.framebuffer.func_147609_e();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        this.mc.func_147110_a().func_147610_a(true);
        this.radius = radius;
        this.quality = quality;
        this.mc.field_71460_t.func_175072_h();
        RenderHelper.func_74518_a();
        this.startShader(duplicate, red, green, blue, alpha, iteractions, formuparam2, zoom, volumSteps, stepSize, title, distfading, saturation, cloud, fade);
        this.mc.field_71460_t.func_78478_c();
        this.drawFramebuffer(this.framebuffer);
        this.stopShader();
        this.mc.field_71460_t.func_175072_h();
        GlStateManager.func_179121_F();
        GlStateManager.func_179099_b();
    }

    public void startShader(float duplicate, float red, float green, float blue, float alpha, int iteractions, float formuparam2, float zoom, float volumSteps, float stepSize, float title, float distfading, float saturation, float cloud, int fade) {
        GL11.glPushMatrix();
        GL20.glUseProgram((int)this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap();
            this.setupUniforms();
        }
        this.updateUniforms(duplicate, red, green, blue, alpha, iteractions, formuparam2, zoom, volumSteps, stepSize, title, distfading, saturation, cloud, fade);
    }

    public void update(double speed) {
        this.time = (float)((double)this.time + speed);
    }
}

