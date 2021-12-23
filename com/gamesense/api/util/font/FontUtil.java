/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.font;

import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSense;
import net.minecraft.client.Minecraft;

public class FontUtil {
    private static final Minecraft mc = Minecraft.func_71410_x();

    public static float drawStringWithShadow(boolean customFont, String text, int x, int y, GSColor color) {
        if (customFont) {
            return GameSense.INSTANCE.cFontRenderer.drawStringWithShadow(text, x, y, color);
        }
        return FontUtil.mc.field_71466_p.func_175063_a(text, (float)x, (float)y, color.getRGB());
    }

    public static int getStringWidth(boolean customFont, String string) {
        if (customFont) {
            return GameSense.INSTANCE.cFontRenderer.getStringWidth(string);
        }
        return FontUtil.mc.field_71466_p.func_78256_a(string);
    }

    public static int getFontHeight(boolean customFont) {
        if (customFont) {
            return GameSense.INSTANCE.cFontRenderer.getHeight();
        }
        return FontUtil.mc.field_71466_p.field_78288_b;
    }
}

