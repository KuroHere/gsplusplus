/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.theme.IColorPickerRenderer;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

public abstract class StandardColorPicker
implements IColorPickerRenderer {
    @Override
    public void renderPicker(Context context, boolean focus, Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        Color colorA = Color.getHSBColor(hsb[0], 0.0f, 1.0f);
        Color colorB = Color.getHSBColor(hsb[0], 1.0f, 1.0f);
        context.getInterface().fillRect(context.getRect(), colorA, colorB, colorB, colorA);
        Color colorC = new Color(0, 0, 0, 0);
        Color colorD = new Color(0, 0, 0);
        context.getInterface().fillRect(context.getRect(), colorC, colorC, colorD, colorD);
        Point p = new Point(Math.round((float)context.getPos().x + hsb[1] * (float)(context.getSize().width - 1)), Math.round((float)context.getPos().y + (1.0f - hsb[2]) * (float)(context.getSize().height - 1)));
        this.renderCursor(context, p, color);
    }

    @Override
    public Color transformPoint(Context context, Color color, Point point) {
        float hue = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null)[0];
        float saturation = (float)(point.x - context.getPos().x) / (float)(context.getSize().width - 1);
        float brightness = 1.0f + (float)(context.getPos().y - point.y) / (float)(context.getSize().height - 1);
        if (saturation > 1.0f) {
            saturation = 1.0f;
        } else if (saturation < 0.0f) {
            saturation = 0.0f;
        }
        if (brightness > 1.0f) {
            brightness = 1.0f;
        } else if (brightness < 0.0f) {
            brightness = 0.0f;
        }
        Color value = Color.getHSBColor(hue, saturation, brightness);
        return ITheme.combineColors(value, color);
    }

    @Override
    public int getDefaultHeight(int width) {
        return Math.min(width, 8 * this.getBaseHeight());
    }

    protected void renderCursor(Context context, Point p, Color color) {
        Color fontColor = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
        context.getInterface().fillRect(new Rectangle(p.x, p.y - this.getPadding(), 1, 2 * this.getPadding() + 1), fontColor, fontColor, fontColor, fontColor);
        context.getInterface().fillRect(new Rectangle(p.x - this.getPadding(), p.y, 2 * this.getPadding() + 1, 1), fontColor, fontColor, fontColor, fontColor);
    }

    public abstract int getPadding();

    public abstract int getBaseHeight();
}

