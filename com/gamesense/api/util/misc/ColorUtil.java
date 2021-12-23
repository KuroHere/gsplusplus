/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.misc;

import com.gamesense.api.setting.values.ModeSetting;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.TextFormatting;

public class ColorUtil {
    public static List<String> colors = Arrays.asList("Black", "Dark Green", "Dark Red", "Gold", "Dark Gray", "Green", "Red", "Yellow", "Dark Blue", "Dark Aqua", "Dark Purple", "Gray", "Blue", "Aqua", "Light Purple", "White");

    public static TextFormatting settingToTextFormatting(ModeSetting setting) {
        if (((String)setting.getValue()).equalsIgnoreCase("Black")) {
            return TextFormatting.BLACK;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Green")) {
            return TextFormatting.DARK_GREEN;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Red")) {
            return TextFormatting.DARK_RED;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Gold")) {
            return TextFormatting.GOLD;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Gray")) {
            return TextFormatting.DARK_GRAY;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Green")) {
            return TextFormatting.GREEN;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Red")) {
            return TextFormatting.RED;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Yellow")) {
            return TextFormatting.YELLOW;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Blue")) {
            return TextFormatting.DARK_BLUE;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Aqua")) {
            return TextFormatting.DARK_AQUA;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Purple")) {
            return TextFormatting.DARK_PURPLE;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Gray")) {
            return TextFormatting.GRAY;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Blue")) {
            return TextFormatting.BLUE;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Light Purple")) {
            return TextFormatting.LIGHT_PURPLE;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("White")) {
            return TextFormatting.WHITE;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Aqua")) {
            return TextFormatting.AQUA;
        }
        return null;
    }

    public static ChatFormatting textToChatFormatting(ModeSetting setting) {
        if (((String)setting.getValue()).equalsIgnoreCase("Black")) {
            return ChatFormatting.BLACK;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Green")) {
            return ChatFormatting.DARK_GREEN;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Red")) {
            return ChatFormatting.DARK_RED;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Gold")) {
            return ChatFormatting.GOLD;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Gray")) {
            return ChatFormatting.DARK_GRAY;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Green")) {
            return ChatFormatting.GREEN;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Red")) {
            return ChatFormatting.RED;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Yellow")) {
            return ChatFormatting.YELLOW;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Blue")) {
            return ChatFormatting.DARK_BLUE;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Aqua")) {
            return ChatFormatting.DARK_AQUA;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Purple")) {
            return ChatFormatting.DARK_PURPLE;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Gray")) {
            return ChatFormatting.GRAY;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Blue")) {
            return ChatFormatting.BLUE;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Light Purple")) {
            return ChatFormatting.LIGHT_PURPLE;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("White")) {
            return ChatFormatting.WHITE;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Aqua")) {
            return ChatFormatting.AQUA;
        }
        return null;
    }

    public static Color settingToColor(ModeSetting setting) {
        if (((String)setting.getValue()).equalsIgnoreCase("Black")) {
            return Color.BLACK;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Green")) {
            return Color.GREEN.darker();
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Red")) {
            return Color.RED.darker();
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Gold")) {
            return Color.yellow.darker();
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Gray")) {
            return Color.DARK_GRAY;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Green")) {
            return Color.green;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Red")) {
            return Color.red;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Yellow")) {
            return Color.yellow;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Blue")) {
            return Color.blue.darker();
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Aqua")) {
            return Color.CYAN.darker();
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Dark Purple")) {
            return Color.MAGENTA.darker();
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Gray")) {
            return Color.GRAY;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Blue")) {
            return Color.blue;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Light Purple")) {
            return Color.magenta;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("White")) {
            return Color.WHITE;
        }
        if (((String)setting.getValue()).equalsIgnoreCase("Aqua")) {
            return Color.cyan;
        }
        return Color.WHITE;
    }
}

