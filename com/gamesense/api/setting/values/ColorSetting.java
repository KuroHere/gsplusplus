/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.setting.values;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Module;
import java.util.function.Supplier;

public class ColorSetting
extends Setting<GSColor> {
    private boolean rainbow = false;
    private final boolean rainbowEnabled;
    private final boolean alphaEnabled;

    public ColorSetting(String name, Module module, boolean rainbow, GSColor value) {
        super(value, name, module);
        this.rainbow = rainbow;
        this.rainbowEnabled = true;
        this.alphaEnabled = false;
    }

    public ColorSetting(String name, Module module, boolean rainbow, GSColor value, boolean alphaEnabled) {
        super(value, name, module);
        this.rainbow = rainbow;
        this.rainbowEnabled = true;
        this.alphaEnabled = alphaEnabled;
    }

    public ColorSetting(String name, String configName, Module module, Supplier<Boolean> isVisible, boolean rainbow, boolean rainbowEnabled, boolean alphaEnabled, GSColor value) {
        super(value, name, configName, module, isVisible);
        this.rainbow = rainbow;
        this.rainbowEnabled = rainbowEnabled;
        this.alphaEnabled = alphaEnabled;
    }

    @Override
    public GSColor getValue() {
        if (this.rainbow) {
            return ColorSetting.getRainbowColor(0, 0, 0, false);
        }
        return (GSColor)super.getValue();
    }

    public static GSColor getRainbowColor(int incr, int multiply, int start, boolean stop) {
        return GSColor.fromHSB((float)(((stop ? (long)start : System.currentTimeMillis()) + (long)(incr * multiply)) % 11520L) / 11520.0f, 1.0f, 1.0f);
    }

    public static GSColor getRainbowColor(double incr) {
        return GSColor.fromHSB((float)(incr % 11520.0 / 11520.0), 1.0f, 1.0f);
    }

    public static GSColor getRainbowSin(int incr, int multiply, double height, int multiplyHeight, double millSin, int start, boolean stop) {
        return GSColor.fromHSB((float)(height * (double)multiplyHeight * Math.sin(((double)(stop ? (long)start : System.currentTimeMillis()) + (double)incr / millSin * (double)multiply) % 11520.0 / 11520.0)), 1.0f, 1.0f);
    }

    public static GSColor getRainbowTan(int incr, int multiply, double height, int multiplyHeight, double millSin, int start, boolean stop) {
        return GSColor.fromHSB((float)(height * (double)multiplyHeight * Math.tan(((double)(stop ? (long)start : System.currentTimeMillis()) + (double)incr / millSin * (double)multiply % 11520.0) / 11520.0)), 1.0f, 1.0f);
    }

    public static GSColor getRainbowSec(int incr, int multiply, double height, int multiplyHeight, double millSin, int start, boolean stop) {
        return GSColor.fromHSB((float)(height * (double)multiplyHeight * (1.0 / Math.sin(((double)(stop ? (long)start : System.currentTimeMillis()) + (double)incr / millSin * (double)multiply) % 11520.0 / 11520.0))), 1.0f, 1.0f);
    }

    public static GSColor getRainbowCosec(int incr, int multiply, double height, int multiplyHeight, double millSin, int start, boolean stop) {
        return GSColor.fromHSB((float)(height * (double)multiplyHeight * (1.0 / Math.cos(((double)(stop ? (long)start : System.currentTimeMillis()) + (double)incr / millSin * (double)multiply) % 11520.0 / 11520.0))), 1.0f, 1.0f);
    }

    public static GSColor getRainbowCoTan(int incr, int multiply, double height, int multiplyHeight, double millSin, int start, boolean stop) {
        return GSColor.fromHSB((float)(height * (double)multiplyHeight * Math.tan(((double)(stop ? (long)start : System.currentTimeMillis()) + (double)incr / millSin * (double)multiply) % 11520.0 / 11520.0)), 1.0f, 1.0f);
    }

    @Override
    public void setValue(GSColor value) {
        super.setValue(new GSColor(value));
    }

    public GSColor getColor() {
        return (GSColor)super.getValue();
    }

    public boolean getRainbow() {
        return this.rainbow;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public boolean rainbowEnabled() {
        return this.rainbowEnabled;
    }

    public boolean alphaEnabled() {
        return this.alphaEnabled;
    }

    public long toLong() {
        long temp = this.getColor().getRGB() & 0xFFFFFF;
        if (this.rainbowEnabled) {
            temp += (long)((this.rainbow ? 1 : 0) << 24);
        }
        if (this.alphaEnabled) {
            temp += (long)this.getColor().getAlpha() << 32;
        }
        return temp;
    }

    public void fromLong(long number) {
        this.rainbow = this.rainbowEnabled ? (number & 0x1000000L) != 0L : false;
        this.setValue(new GSColor((int)(number & 0xFFFFFFL)));
        if (this.alphaEnabled) {
            this.setValue(new GSColor(this.getColor(), (int)((number & 0xFF00000000L) >> 32)));
        }
    }
}

