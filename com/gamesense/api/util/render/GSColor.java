/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.render;

import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;

public class GSColor
extends Color {
    private static final long serialVersionUID = 1L;

    public GSColor(int rgb) {
        super(rgb);
    }

    public GSColor(int rgba, boolean hasalpha) {
        super(rgba, hasalpha);
    }

    public GSColor(int r, int g, int b) {
        super(r, g, b);
    }

    public GSColor(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public GSColor(Color color) {
        super(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public GSColor(GSColor color, int a) {
        super(color.getRed(), color.getGreen(), color.getBlue(), a);
    }

    public static GSColor fromHSB(float hue, float saturation, float brightness) {
        return new GSColor(Color.getHSBColor(hue, saturation, brightness));
    }

    public float getHue() {
        return GSColor.RGBtoHSB(this.getRed(), this.getGreen(), this.getBlue(), null)[0];
    }

    public float getSaturation() {
        return GSColor.RGBtoHSB(this.getRed(), this.getGreen(), this.getBlue(), null)[1];
    }

    public float getBrightness() {
        return GSColor.RGBtoHSB(this.getRed(), this.getGreen(), this.getBlue(), null)[2];
    }

    public void glColor() {
        GlStateManager.func_179131_c((float)((float)this.getRed() / 255.0f), (float)((float)this.getGreen() / 255.0f), (float)((float)this.getBlue() / 255.0f), (float)((float)this.getAlpha() / 255.0f));
    }
}

