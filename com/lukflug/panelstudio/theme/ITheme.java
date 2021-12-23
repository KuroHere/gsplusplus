/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.theme.IButtonRenderer;
import com.lukflug.panelstudio.theme.IColorPickerRenderer;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import com.lukflug.panelstudio.theme.IDescriptionRenderer;
import com.lukflug.panelstudio.theme.IEmptySpaceRenderer;
import com.lukflug.panelstudio.theme.IPanelRenderer;
import com.lukflug.panelstudio.theme.IRadioRenderer;
import com.lukflug.panelstudio.theme.IResizeBorderRenderer;
import com.lukflug.panelstudio.theme.IScrollBarRenderer;
import com.lukflug.panelstudio.theme.ISliderRenderer;
import com.lukflug.panelstudio.theme.ISwitchRenderer;
import com.lukflug.panelstudio.theme.ITextFieldRenderer;
import java.awt.Color;
import java.awt.Rectangle;

public interface ITheme {
    public static final int NONE = 0;
    public static final int CLOSE = 1;
    public static final int MINIMIZE = 2;
    public static final int ADD = 3;
    public static final int LEFT = 4;
    public static final int RIGHT = 5;
    public static final int UP = 6;
    public static final int DOWN = 7;

    public void loadAssets(IInterface var1);

    public IDescriptionRenderer getDescriptionRenderer();

    public IContainerRenderer getContainerRenderer(int var1, int var2, boolean var3);

    public <T> IPanelRenderer<T> getPanelRenderer(Class<T> var1, int var2, int var3);

    public <T> IScrollBarRenderer<T> getScrollBarRenderer(Class<T> var1, int var2, int var3);

    public <T> IEmptySpaceRenderer<T> getEmptySpaceRenderer(Class<T> var1, int var2, int var3, boolean var4);

    public <T> IButtonRenderer<T> getButtonRenderer(Class<T> var1, int var2, int var3, boolean var4);

    public IButtonRenderer<Void> getSmallButtonRenderer(int var1, int var2, int var3, boolean var4);

    public IButtonRenderer<String> getKeybindRenderer(int var1, int var2, boolean var3);

    public ISliderRenderer getSliderRenderer(int var1, int var2, boolean var3);

    public IRadioRenderer getRadioRenderer(int var1, int var2, boolean var3);

    public IResizeBorderRenderer getResizeRenderer();

    public ITextFieldRenderer getTextRenderer(boolean var1, int var2, int var3, boolean var4);

    public ISwitchRenderer<Boolean> getToggleSwitchRenderer(int var1, int var2, boolean var3);

    public ISwitchRenderer<String> getCycleSwitchRenderer(int var1, int var2, boolean var3);

    public IColorPickerRenderer getColorPickerRenderer();

    public int getBaseHeight();

    public Color getMainColor(boolean var1, boolean var2);

    public Color getBackgroundColor(boolean var1);

    public Color getFontColor(boolean var1);

    public void overrideMainColor(Color var1);

    public void restoreMainColor();

    public static Color combineColors(Color main, Color opacity) {
        return new Color(main.getRed(), main.getGreen(), main.getBlue(), opacity.getAlpha());
    }

    public static void drawRect(IInterface inter, Rectangle rect, Color color) {
        inter.fillRect(new Rectangle(rect.x, rect.y, 1, rect.height), color, color, color, color);
        inter.fillRect(new Rectangle(rect.x + 1, rect.y, rect.width - 2, 1), color, color, color, color);
        inter.fillRect(new Rectangle(rect.x + rect.width - 1, rect.y, 1, rect.height), color, color, color, color);
        inter.fillRect(new Rectangle(rect.x + 1, rect.y + rect.height - 1, rect.width - 2, 1), color, color, color, color);
    }
}

