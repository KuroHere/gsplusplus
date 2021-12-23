/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.theme.IButtonRenderer;
import com.lukflug.panelstudio.theme.IColorPickerRenderer;
import com.lukflug.panelstudio.theme.IColorScheme;
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
import com.lukflug.panelstudio.theme.ITheme;
import com.lukflug.panelstudio.theme.StandardColorPicker;
import com.lukflug.panelstudio.theme.ThemeBase;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class ImpactTheme
extends ThemeBase {
    int height;
    int padding;

    public ImpactTheme(IColorScheme scheme, int height, int padding) {
        super(scheme);
        this.height = height;
        this.padding = padding;
        scheme.createSetting(this, "Title Color", "The color for panel titles.", true, true, new Color(16, 16, 16, 198), false);
        scheme.createSetting(this, "Background Color", "The panel background color.", true, true, new Color(30, 30, 30, 192), false);
        scheme.createSetting(this, "Panel Outline Color", "The main color for panel outlines.", false, true, new Color(20, 20, 20), false);
        scheme.createSetting(this, "Component Outline Color", "The main color for component outlines.", true, true, new Color(0, 0, 0, 92), false);
        scheme.createSetting(this, "Active Font Color", "The color for active text.", false, true, new Color(255, 255, 255), false);
        scheme.createSetting(this, "Hovered Font Color", "The color for hovered text.", false, true, new Color(192, 192, 192), false);
        scheme.createSetting(this, "Inactive Font Color", "The color for inactive text.", false, true, new Color(128, 128, 128), false);
        scheme.createSetting(this, "Enabled Color", "The color for enabled modules.", false, true, new Color(91, 201, 79), false);
        scheme.createSetting(this, "Disabled Color", "The  color for disabled modules.", false, true, new Color(194, 48, 48), false);
        scheme.createSetting(this, "Highlight Color", "The color for highlighted text.", false, true, new Color(0, 0, 255), false);
        scheme.createSetting(this, "Tooltip Color", "The color for description tooltips.", false, true, new Color(0, 0, 0, 128), false);
    }

    protected void renderBackground(Context context, boolean focus) {
        Color color = this.getBackgroundColor(focus);
        context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y, context.getSize().width, context.getSize().height), color, color, color, color);
    }

    protected void renderOverlay(Context context) {
        if (context.isHovered()) {
            Color color = new Color(0, 0, 0, 24);
            context.getInterface().fillRect(context.getRect(), color, color, color, color);
        }
    }

    protected void renderSmallButton(Context context, String title, int symbol, boolean focus) {
        Point[] points = new Point[3];
        int padding = context.getSize().height <= 12 ? (context.getSize().height <= 8 ? 2 : 4) : 6;
        Rectangle rect = new Rectangle(context.getPos().x + padding / 2, context.getPos().y + padding / 2, context.getSize().height - 2 * (padding / 2), context.getSize().height - 2 * (padding / 2));
        if (title == null) {
            rect.x += context.getSize().width / 2 - context.getSize().height / 2;
        }
        Color color = this.getFontColor(focus);
        if (context.isHovered()) {
            color = this.scheme.getColor("Active Font Color");
        }
        switch (symbol) {
            case 1: {
                context.getInterface().drawLine(new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), color, color);
                context.getInterface().drawLine(new Point(rect.x, rect.y + rect.height), new Point(rect.x + rect.width, rect.y), color, color);
                break;
            }
            case 2: {
                context.getInterface().fillRect(new Rectangle(rect.x, rect.y + rect.height - 2, rect.width, 2), color, color, color, color);
                break;
            }
            case 3: {
                if (rect.width % 2 == 1) {
                    --rect.width;
                }
                if (rect.height % 2 == 1) {
                    --rect.height;
                }
                context.getInterface().fillRect(new Rectangle(rect.x + rect.width / 2 - 1, rect.y, 2, rect.height), color, color, color, color);
                context.getInterface().fillRect(new Rectangle(rect.x, rect.y + rect.height / 2 - 1, rect.width, 2), color, color, color, color);
                break;
            }
            case 4: {
                if (rect.height % 2 == 1) {
                    --rect.height;
                }
                points[2] = new Point(rect.x + rect.width, rect.y);
                points[0] = new Point(rect.x + rect.width, rect.y + rect.height);
                points[1] = new Point(rect.x, rect.y + rect.height / 2);
                break;
            }
            case 5: {
                if (rect.height % 2 == 1) {
                    --rect.height;
                }
                points[0] = new Point(rect.x, rect.y);
                points[2] = new Point(rect.x, rect.y + rect.height);
                points[1] = new Point(rect.x + rect.width, rect.y + rect.height / 2);
                break;
            }
            case 6: {
                if (rect.width % 2 == 1) {
                    --rect.width;
                }
                points[0] = new Point(rect.x, rect.y + rect.height);
                points[2] = new Point(rect.x + rect.width, rect.y + rect.height);
                points[1] = new Point(rect.x + rect.width / 2, rect.y);
                break;
            }
            case 7: {
                if (rect.width % 2 == 1) {
                    --rect.width;
                }
                points[2] = new Point(rect.x, rect.y);
                points[0] = new Point(rect.x + rect.width, rect.y);
                points[1] = new Point(rect.x + rect.width / 2, rect.y + rect.height);
            }
        }
        if (symbol >= 4 && symbol <= 7) {
            context.getInterface().drawLine(points[0], points[1], color, color);
            context.getInterface().drawLine(points[1], points[2], color, color);
        }
        if (title != null) {
            context.getInterface().drawString(new Point(context.getPos().x + (symbol == 0 ? padding : context.getSize().height), context.getPos().y + padding), this.height, title, this.getFontColor(focus));
        }
    }

    @Override
    public IDescriptionRenderer getDescriptionRenderer() {
        return new IDescriptionRenderer(){

            @Override
            public void renderDescription(IInterface inter, Point pos, String text) {
                Rectangle rect = new Rectangle(pos, new Dimension(inter.getFontWidth(ImpactTheme.this.height, text) + 2 * ImpactTheme.this.padding - 2, ImpactTheme.this.height + 2 * ImpactTheme.this.padding - 2));
                Color color = ImpactTheme.this.scheme.getColor("Tooltip Color");
                inter.fillRect(rect, color, color, color, color);
                inter.drawString(new Point(pos.x + ImpactTheme.this.padding - 1, pos.y + ImpactTheme.this.padding - 1), ImpactTheme.this.height, text, ImpactTheme.this.getFontColor(true));
            }
        };
    }

    @Override
    public IContainerRenderer getContainerRenderer(int logicalLevel, final int graphicalLevel, boolean horizontal) {
        return new IContainerRenderer(){

            @Override
            public void renderBackground(Context context, boolean focus) {
                if (graphicalLevel == 0) {
                    ImpactTheme.this.renderBackground(context, focus);
                }
            }

            @Override
            public int getBorder() {
                return 2;
            }

            @Override
            public int getLeft() {
                return 2;
            }

            @Override
            public int getRight() {
                return 2;
            }

            @Override
            public int getTop() {
                return 2;
            }

            @Override
            public int getBottom() {
                return 2;
            }
        };
    }

    @Override
    public <T> IPanelRenderer<T> getPanelRenderer(Class<T> type, int logicalLevel, final int graphicalLevel) {
        return new IPanelRenderer<T>(){

            @Override
            public int getBorder() {
                return graphicalLevel <= 0 ? 1 : 0;
            }

            @Override
            public int getLeft() {
                return 1;
            }

            @Override
            public int getRight() {
                return 1;
            }

            @Override
            public int getTop() {
                return 1;
            }

            @Override
            public int getBottom() {
                return 1;
            }

            @Override
            public void renderPanelOverlay(Context context, boolean focus, T state, boolean open) {
                Color color = graphicalLevel <= 0 ? ImpactTheme.this.scheme.getColor("Panel Outline Color") : ImpactTheme.this.scheme.getColor("Component Outline Color");
                ITheme.drawRect(context.getInterface(), context.getRect(), color);
            }

            @Override
            public void renderTitleOverlay(Context context, boolean focus, T state, boolean open) {
                if (graphicalLevel <= 0) {
                    Color colorA = ImpactTheme.this.scheme.getColor("Panel Outline Color");
                    context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y + context.getSize().height, context.getSize().width, 1), colorA, colorA, colorA, colorA);
                } else {
                    ImpactTheme.this.renderOverlay(context);
                    Context subContext = new Context(context, ImpactTheme.this.height, new Point(ImpactTheme.this.padding / 2, ImpactTheme.this.padding / 2), true, true);
                    subContext.setHeight(context.getSize().height - ImpactTheme.this.padding);
                    ImpactTheme.this.renderSmallButton(subContext, null, open ? 7 : 5, focus);
                }
            }
        };
    }

    @Override
    public <T> IScrollBarRenderer<T> getScrollBarRenderer(Class<T> type, int logicalLevel, int graphicalLevel) {
        return new IScrollBarRenderer<T>(){};
    }

    @Override
    public <T> IEmptySpaceRenderer<T> getEmptySpaceRenderer(Class<T> type, int logicalLevel, final int graphicalLevel, boolean container) {
        return new IEmptySpaceRenderer<T>(){

            @Override
            public void renderSpace(Context context, boolean focus, T state) {
                if (graphicalLevel == 0) {
                    ImpactTheme.this.renderBackground(context, focus);
                }
            }
        };
    }

    @Override
    public <T> IButtonRenderer<T> getButtonRenderer(final Class<T> type, int logicalLevel, final int graphicalLevel, final boolean container) {
        return new IButtonRenderer<T>(){

            @Override
            public void renderButton(Context context, String title, boolean focus, T state) {
                Color color;
                boolean effFocus;
                boolean bl = effFocus = container ? context.hasFocus() : focus;
                if (graphicalLevel <= 0) {
                    if (container) {
                        color = ImpactTheme.this.scheme.getColor("Title Color");
                        context.getInterface().fillRect(context.getRect(), color, color, color, color);
                    } else {
                        ImpactTheme.this.renderBackground(context, effFocus);
                    }
                }
                if (!container) {
                    color = graphicalLevel <= 0 ? ImpactTheme.this.scheme.getColor("Panel Outline Color") : ImpactTheme.this.scheme.getColor("Component Outline Color");
                    ITheme.drawRect(context.getInterface(), context.getRect(), color);
                    ImpactTheme.this.renderOverlay(context);
                }
                int colorLevel = 1;
                if (type == Boolean.class) {
                    colorLevel = (Boolean)state != false ? 2 : 0;
                } else if (type == String.class) {
                    colorLevel = 2;
                }
                if (container && graphicalLevel <= 0) {
                    colorLevel = 2;
                }
                Color valueColor = ImpactTheme.this.getFontColor(effFocus);
                if (context.isHovered() && context.getInterface().getMouse().x > context.getPos().x + context.getSize().height - ImpactTheme.this.padding) {
                    if (colorLevel < 2) {
                        ++colorLevel;
                    }
                    valueColor = ImpactTheme.this.scheme.getColor("Active Font Color");
                }
                Color fontColor = ImpactTheme.this.getFontColor(effFocus);
                if (colorLevel == 2) {
                    fontColor = ImpactTheme.this.scheme.getColor("Active Font Color");
                } else if (colorLevel == 0) {
                    fontColor = ImpactTheme.this.scheme.getColor("Inactive Font Color");
                }
                int xpos = context.getPos().x + context.getSize().height - ImpactTheme.this.padding;
                if (container && graphicalLevel <= 0) {
                    xpos = context.getPos().x + context.getSize().width / 2 - context.getInterface().getFontWidth(ImpactTheme.this.height, title) / 2;
                }
                context.getInterface().drawString(new Point(xpos, context.getPos().y + ImpactTheme.this.padding - (container ? 1 : 0)), ImpactTheme.this.height, title, fontColor);
                if (type == String.class) {
                    context.getInterface().drawString(new Point(context.getPos().x + context.getSize().width - ImpactTheme.this.padding - context.getInterface().getFontWidth(ImpactTheme.this.height, (String)state), context.getPos().y + ImpactTheme.this.padding - (container ? 1 : 0)), ImpactTheme.this.height, (String)state, valueColor);
                } else if (type == Boolean.class) {
                    if (context.isHovered() && container) {
                        int width = context.getInterface().getFontWidth(ImpactTheme.this.height, "OFF") + 2 * ImpactTheme.this.padding;
                        Rectangle rect = new Rectangle(context.getPos().x + context.getSize().width - width, context.getPos().y + ImpactTheme.this.padding / 2, width, context.getSize().height - 2 * (ImpactTheme.this.padding / 2));
                        String text = (Boolean)state != false ? "ON" : "OFF";
                        Color color2 = ImpactTheme.this.getMainColor(effFocus, (Boolean)state);
                        context.getInterface().fillRect(rect, color2, color2, color2, color2);
                        context.getInterface().drawString(new Point(rect.x + (rect.width - context.getInterface().getFontWidth(ImpactTheme.this.height, text)) / 2, context.getPos().y + ImpactTheme.this.padding / 2), ImpactTheme.this.height, text, ImpactTheme.this.scheme.getColor("Active Font Color"));
                    } else if (!container && ((Boolean)state).booleanValue()) {
                        Point a = new Point(context.getPos().x + context.getSize().width - context.getSize().height + ImpactTheme.this.padding, context.getPos().y + context.getSize().height / 2);
                        Point b = new Point(context.getPos().x + context.getSize().width - context.getSize().height / 2, context.getPos().y + context.getSize().height - ImpactTheme.this.padding);
                        Point c = new Point(context.getPos().x + context.getSize().width - ImpactTheme.this.padding, context.getPos().y + ImpactTheme.this.padding);
                        Color checkColor = ImpactTheme.this.scheme.getColor("Active Font Color");
                        context.getInterface().drawLine(a, b, checkColor, checkColor);
                        context.getInterface().drawLine(b, c, checkColor, checkColor);
                    }
                }
            }

            @Override
            public int getDefaultHeight() {
                return container ? ImpactTheme.this.getBaseHeight() - 2 : ImpactTheme.this.getBaseHeight();
            }
        };
    }

    @Override
    public IButtonRenderer<Void> getSmallButtonRenderer(final int symbol, final int logicalLevel, final int graphicalLevel, final boolean container) {
        return new IButtonRenderer<Void>(){

            @Override
            public void renderButton(Context context, String title, boolean focus, Void state) {
                Color color;
                if (graphicalLevel <= 0) {
                    if (container) {
                        color = ImpactTheme.this.scheme.getColor("Title Color");
                        context.getInterface().fillRect(context.getRect(), color, color, color, color);
                    } else {
                        ImpactTheme.this.renderBackground(context, focus);
                    }
                }
                if (!container) {
                    color = graphicalLevel <= 0 ? ImpactTheme.this.scheme.getColor("Panel Outline Color") : ImpactTheme.this.scheme.getColor("Component Outline Color");
                    ITheme.drawRect(context.getInterface(), context.getRect(), color);
                    ImpactTheme.this.renderOverlay(context);
                }
                ImpactTheme.this.renderOverlay(context);
                if (!container || logicalLevel <= 0) {
                    ImpactTheme.this.renderSmallButton(context, title, symbol, focus);
                }
            }

            @Override
            public int getDefaultHeight() {
                return ImpactTheme.this.getBaseHeight();
            }
        };
    }

    @Override
    public IButtonRenderer<String> getKeybindRenderer(int logicalLevel, final int graphicalLevel, final boolean container) {
        return new IButtonRenderer<String>(){

            @Override
            public void renderButton(Context context, String title, boolean focus, String state) {
                Color color;
                boolean effFocus;
                boolean bl = effFocus = container ? context.hasFocus() : focus;
                if (graphicalLevel <= 0) {
                    if (container) {
                        color = ImpactTheme.this.scheme.getColor("Title Color");
                        context.getInterface().fillRect(context.getRect(), color, color, color, color);
                    } else {
                        ImpactTheme.this.renderBackground(context, effFocus);
                    }
                }
                if (!container) {
                    color = graphicalLevel <= 0 ? ImpactTheme.this.scheme.getColor("Panel Outline Color") : ImpactTheme.this.scheme.getColor("Component Outline Color");
                    ITheme.drawRect(context.getInterface(), context.getRect(), color);
                    ImpactTheme.this.renderOverlay(context);
                }
                Color valueColor = ImpactTheme.this.scheme.getColor("Active Font Color");
                Color fontColor = ImpactTheme.this.getFontColor(effFocus);
                if (context.isHovered() && context.getInterface().getMouse().x > context.getPos().x + context.getSize().height - ImpactTheme.this.padding) {
                    fontColor = ImpactTheme.this.scheme.getColor("Active Font Color");
                }
                int xpos = context.getPos().x + context.getSize().height - ImpactTheme.this.padding;
                if (container && graphicalLevel <= 0) {
                    xpos = context.getPos().x + context.getSize().width / 2 - context.getInterface().getFontWidth(ImpactTheme.this.height, title) / 2;
                }
                context.getInterface().drawString(new Point(xpos, context.getPos().y + ImpactTheme.this.padding - (container ? 1 : 0)), ImpactTheme.this.height, title, fontColor);
                context.getInterface().drawString(new Point(context.getPos().x + context.getSize().width - ImpactTheme.this.padding - context.getInterface().getFontWidth(ImpactTheme.this.height, effFocus ? "..." : state), context.getPos().y + ImpactTheme.this.padding - (container ? 1 : 0)), ImpactTheme.this.height, effFocus ? "..." : state, valueColor);
            }

            @Override
            public int getDefaultHeight() {
                return container ? ImpactTheme.this.getBaseHeight() - 2 : ImpactTheme.this.getBaseHeight();
            }
        };
    }

    @Override
    public ISliderRenderer getSliderRenderer(int logicalLevel, final int graphicalLevel, final boolean container) {
        return new ISliderRenderer(){

            @Override
            public void renderSlider(Context context, String title, String state, boolean focus, double value) {
                Color color;
                boolean effFocus;
                boolean bl = effFocus = container ? context.hasFocus() : focus;
                if (graphicalLevel <= 0) {
                    if (container) {
                        color = ImpactTheme.this.scheme.getColor("Title Color");
                        context.getInterface().fillRect(context.getRect(), color, color, color, color);
                    } else {
                        ImpactTheme.this.renderBackground(context, effFocus);
                    }
                }
                if (!container) {
                    color = graphicalLevel <= 0 ? ImpactTheme.this.scheme.getColor("Panel Outline Color") : ImpactTheme.this.scheme.getColor("Component Outline Color");
                    ITheme.drawRect(context.getInterface(), context.getRect(), color);
                    ImpactTheme.this.renderOverlay(context);
                }
                Rectangle rect = context.getRect();
                if (!container) {
                    rect = new Rectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
                }
                if (ImpactTheme.this.getColor(null) != null && (title.equals("Red") || title.equals("Green") || title.equals("Blue") || title.equals("Hue") || title.equals("Saturation") || title.equals("Brightness"))) {
                    Color main = ImpactTheme.this.getColor(null);
                    Color colorA = null;
                    Color colorB = null;
                    float[] hsb = Color.RGBtoHSB(main.getRed(), main.getGreen(), main.getBlue(), null);
                    if (title.equals("Red")) {
                        colorA = new Color(0, main.getGreen(), main.getBlue());
                        colorB = new Color(255, main.getGreen(), main.getBlue());
                    } else if (title.equals("Green")) {
                        colorA = new Color(main.getRed(), 0, main.getBlue());
                        colorB = new Color(main.getRed(), 255, main.getBlue());
                    } else if (title.equals("Blue")) {
                        colorA = new Color(main.getRed(), main.getGreen(), 0);
                        colorB = new Color(main.getRed(), main.getGreen(), 255);
                    } else if (title.equals("Saturation")) {
                        colorA = Color.getHSBColor(hsb[0], 0.0f, hsb[2]);
                        colorB = Color.getHSBColor(hsb[0], 1.0f, hsb[2]);
                    } else if (title.equals("Brightness")) {
                        colorA = Color.getHSBColor(hsb[0], hsb[1], 0.0f);
                        colorB = Color.getHSBColor(hsb[0], hsb[1], 1.0f);
                    }
                    if (colorA != null && colorB != null) {
                        context.getInterface().fillRect(new Rectangle(context.getPos().x + 1, context.getPos().y + 1, context.getSize().width - 2, context.getSize().height - 2), colorA, colorB, colorB, colorA);
                    } else {
                        int a = rect.x;
                        int b = rect.width / 6;
                        int c = rect.width * 2 / 6;
                        int d = rect.width * 3 / 6;
                        int e = rect.width * 4 / 6;
                        int f = rect.width * 5 / 6;
                        int g = rect.width;
                        b += a;
                        c += a;
                        d += a;
                        e += a;
                        f += a;
                        g += a;
                        Color c0 = Color.getHSBColor(0.0f, hsb[1], hsb[2]);
                        Color c1 = Color.getHSBColor(0.16666667f, hsb[1], hsb[2]);
                        Color c2 = Color.getHSBColor(0.33333334f, hsb[1], hsb[2]);
                        Color c3 = Color.getHSBColor(0.5f, hsb[1], hsb[2]);
                        Color c4 = Color.getHSBColor(0.6666667f, hsb[1], hsb[2]);
                        Color c5 = Color.getHSBColor(0.8333333f, hsb[1], hsb[2]);
                        context.getInterface().fillRect(new Rectangle(a, rect.y, b - a, rect.height), c0, c1, c1, c0);
                        context.getInterface().fillRect(new Rectangle(b, rect.y, c - b, rect.height), c1, c2, c2, c1);
                        context.getInterface().fillRect(new Rectangle(c, rect.y, d - c, rect.height), c2, c3, c3, c2);
                        context.getInterface().fillRect(new Rectangle(d, rect.y, e - d, rect.height), c3, c4, c4, c3);
                        context.getInterface().fillRect(new Rectangle(e, rect.y, f - e, rect.height), c4, c5, c5, c4);
                        context.getInterface().fillRect(new Rectangle(f, rect.y, g - f, rect.height), c5, c0, c0, c5);
                    }
                    ImpactTheme.this.renderOverlay(context);
                    Color lineColor = ImpactTheme.this.scheme.getColor("Active Font Color");
                    int separator = (int)Math.round((double)(rect.width - 1) * value);
                    context.getInterface().fillRect(new Rectangle(rect.x + separator, rect.y, 1, rect.height), lineColor, lineColor, lineColor, lineColor);
                } else {
                    Color valueColor = ImpactTheme.this.scheme.getColor("Active Font Color");
                    Color fontColor = ImpactTheme.this.getFontColor(effFocus);
                    if (context.isHovered() && context.getInterface().getMouse().x > context.getPos().x + context.getSize().height - ImpactTheme.this.padding) {
                        fontColor = ImpactTheme.this.scheme.getColor("Active Font Color");
                    }
                    int xpos = context.getPos().x + context.getSize().height - ImpactTheme.this.padding;
                    if (container && graphicalLevel <= 0) {
                        xpos = context.getPos().x + context.getSize().width / 2 - context.getInterface().getFontWidth(ImpactTheme.this.height, title) / 2;
                    }
                    context.getInterface().drawString(new Point(xpos, context.getPos().y + ImpactTheme.this.padding - (container ? 1 : 0)), ImpactTheme.this.height, title, fontColor);
                    if (context.isHovered()) {
                        context.getInterface().drawString(new Point(context.getPos().x + context.getSize().width - ImpactTheme.this.padding - context.getInterface().getFontWidth(ImpactTheme.this.height, state), context.getPos().y + ImpactTheme.this.padding - (container ? 1 : 0)), ImpactTheme.this.height, state, valueColor);
                    }
                    Color lineColor = ImpactTheme.this.scheme.getColor("Active Font Color");
                    int separator = (int)Math.round((double)(context.getSize().width - context.getSize().height + ImpactTheme.this.padding - (container ? 0 : 1)) * value);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x + context.getSize().height - ImpactTheme.this.padding, context.getPos().y + context.getSize().height - (container ? 1 : 2), separator, 1), lineColor, lineColor, lineColor, lineColor);
                }
            }

            @Override
            public int getDefaultHeight() {
                return container ? ImpactTheme.this.getBaseHeight() - 2 : ImpactTheme.this.getBaseHeight();
            }

            @Override
            public Rectangle getSlideArea(Context context, String title, String state) {
                if (ImpactTheme.this.getColor(null) != null && (title.equals("Red") || title.equals("Green") || title.equals("Blue") || title.equals("Hue") || title.equals("Saturation") || title.equals("Brightness"))) {
                    Rectangle rect = context.getRect();
                    if (!container) {
                        rect = new Rectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
                    }
                    return rect;
                }
                return new Rectangle(context.getPos().x + context.getSize().height - ImpactTheme.this.padding, context.getPos().y, context.getSize().width - context.getSize().height + ImpactTheme.this.padding - (container ? 0 : 1), context.getSize().height);
            }
        };
    }

    @Override
    public IRadioRenderer getRadioRenderer(int logicalLevel, final int graphicalLevel, boolean container) {
        return new IRadioRenderer(){

            @Override
            public void renderItem(Context context, ILabeled[] items, boolean focus, int target, double state, boolean horizontal) {
                if (graphicalLevel <= 0) {
                    ImpactTheme.this.renderBackground(context, focus);
                }
                for (int i = 0; i < items.length; ++i) {
                    Rectangle rect = this.getItemRect(context, items, i, horizontal);
                    Context subContext = new Context(context.getInterface(), rect.width, rect.getLocation(), context.hasFocus(), context.onTop());
                    subContext.setHeight(rect.height);
                    ImpactTheme.this.renderOverlay(subContext);
                    Color color = ImpactTheme.this.getFontColor(focus);
                    if (i == target) {
                        color = ImpactTheme.this.scheme.getColor("Active Font Color");
                    } else if (!subContext.isHovered()) {
                        color = ImpactTheme.this.scheme.getColor("Inactive Font Color");
                    }
                    context.getInterface().drawString(new Point(rect.x + ImpactTheme.this.padding, rect.y + ImpactTheme.this.padding), ImpactTheme.this.height, items[i].getDisplayName(), color);
                }
            }

            @Override
            public int getDefaultHeight(ILabeled[] items, boolean horizontal) {
                return (horizontal ? 1 : items.length) * ImpactTheme.this.getBaseHeight();
            }
        };
    }

    @Override
    public IResizeBorderRenderer getResizeRenderer() {
        return new IResizeBorderRenderer(){

            @Override
            public void drawBorder(Context context, boolean focus) {
                Color color = ImpactTheme.this.getBackgroundColor(focus);
                Rectangle rect = context.getRect();
                context.getInterface().fillRect(new Rectangle(rect.x, rect.y, rect.width, this.getBorder()), color, color, color, color);
                context.getInterface().fillRect(new Rectangle(rect.x, rect.y + rect.height - this.getBorder(), rect.width, this.getBorder()), color, color, color, color);
                context.getInterface().fillRect(new Rectangle(rect.x, rect.y + this.getBorder(), this.getBorder(), rect.height - 2 * this.getBorder()), color, color, color, color);
                context.getInterface().fillRect(new Rectangle(rect.x + rect.width - this.getBorder(), rect.y + this.getBorder(), this.getBorder(), rect.height - 2 * this.getBorder()), color, color, color, color);
            }

            @Override
            public int getBorder() {
                return 2;
            }
        };
    }

    @Override
    public ITextFieldRenderer getTextRenderer(boolean embed, int logicalLevel, final int graphicalLevel, final boolean container) {
        return new ITextFieldRenderer(){

            @Override
            public int renderTextField(Context context, String title, boolean focus, String content, int position, int select, int boxPosition, boolean insertMode) {
                int maxPosition;
                boolean effFocus;
                boolean bl = effFocus = container ? context.hasFocus() : focus;
                if (graphicalLevel <= 0) {
                    ImpactTheme.this.renderBackground(context, effFocus);
                }
                if (!container) {
                    Color color = graphicalLevel <= 0 ? ImpactTheme.this.scheme.getColor("Panel Outline Color") : ImpactTheme.this.scheme.getColor("Component Outline Color");
                    ITheme.drawRect(context.getInterface(), context.getRect(), color);
                    ImpactTheme.this.renderOverlay(context);
                }
                Color textColor = ImpactTheme.this.getFontColor(effFocus);
                if (context.isHovered() && context.getInterface().getMouse().x > context.getPos().x + context.getSize().height - ImpactTheme.this.padding) {
                    textColor = ImpactTheme.this.scheme.getColor("Active Font Color");
                }
                Color highlightColor = ImpactTheme.this.scheme.getColor("Highlight Color");
                Rectangle rect = this.getTextArea(context, title);
                int strlen = context.getInterface().getFontWidth(ImpactTheme.this.height, content.substring(0, position));
                context.getInterface().fillRect(rect, new Color(0, 0, 0, 64), new Color(0, 0, 0, 64), new Color(0, 0, 0, 64), new Color(0, 0, 0, 64));
                if (boxPosition < position) {
                    int minPosition;
                    for (minPosition = boxPosition; minPosition < position && context.getInterface().getFontWidth(ImpactTheme.this.height, content.substring(0, minPosition)) + rect.width - ImpactTheme.this.padding < strlen; ++minPosition) {
                    }
                    if (boxPosition < minPosition) {
                        boxPosition = minPosition;
                    }
                } else if (boxPosition > position) {
                    boxPosition = position - 1;
                }
                for (maxPosition = content.length(); maxPosition > 0; --maxPosition) {
                    if (context.getInterface().getFontWidth(ImpactTheme.this.height, content.substring(maxPosition)) < rect.width - ImpactTheme.this.padding) continue;
                    ++maxPosition;
                    break;
                }
                if (boxPosition > maxPosition) {
                    boxPosition = maxPosition;
                } else if (boxPosition < 0) {
                    boxPosition = 0;
                }
                int offset = context.getInterface().getFontWidth(ImpactTheme.this.height, content.substring(0, boxPosition));
                int x1 = rect.x + ImpactTheme.this.padding / 2 - offset + strlen;
                int x2 = rect.x + ImpactTheme.this.padding / 2 - offset;
                x2 = position < content.length() ? (x2 += context.getInterface().getFontWidth(ImpactTheme.this.height, content.substring(0, position + 1))) : (x2 += context.getInterface().getFontWidth(ImpactTheme.this.height, content + "X"));
                ImpactTheme.this.renderOverlay(context);
                context.getInterface().drawString(new Point(context.getPos().x + context.getSize().height - ImpactTheme.this.padding, context.getPos().y + ImpactTheme.this.padding - (container ? 1 : 0)), ImpactTheme.this.height, title, textColor);
                context.getInterface().window(rect);
                if (select >= 0) {
                    int x3 = rect.x + ImpactTheme.this.padding / 2 - offset + context.getInterface().getFontWidth(ImpactTheme.this.height, content.substring(0, select));
                    context.getInterface().fillRect(new Rectangle(Math.min(x1, x3), context.getPos().y + ImpactTheme.this.padding - (container ? 1 : 0), Math.abs(x3 - x1), ImpactTheme.this.height), highlightColor, highlightColor, highlightColor, highlightColor);
                }
                context.getInterface().drawString(new Point(rect.x + ImpactTheme.this.padding / 2 - offset, context.getPos().y + ImpactTheme.this.padding - (container ? 1 : 0)), ImpactTheme.this.height, content, textColor);
                if (System.currentTimeMillis() / 500L % 2L == 0L && focus) {
                    if (insertMode) {
                        context.getInterface().fillRect(new Rectangle(x1, context.getPos().y + ImpactTheme.this.padding - (container ? 1 : 0) + ImpactTheme.this.height, x2 - x1, 1), textColor, textColor, textColor, textColor);
                    } else {
                        context.getInterface().fillRect(new Rectangle(x1, context.getPos().y + ImpactTheme.this.padding - (container ? 1 : 0), 1, ImpactTheme.this.height), textColor, textColor, textColor, textColor);
                    }
                }
                context.getInterface().restore();
                return boxPosition;
            }

            @Override
            public int getDefaultHeight() {
                return container ? ImpactTheme.this.getBaseHeight() - 2 : ImpactTheme.this.getBaseHeight();
            }

            @Override
            public Rectangle getTextArea(Context context, String title) {
                Rectangle rect = context.getRect();
                int length = rect.height - ImpactTheme.this.padding + context.getInterface().getFontWidth(ImpactTheme.this.height, title + "X");
                return new Rectangle(rect.x + length, rect.y + (container ? 0 : 1), rect.width - length, rect.height - (container ? 0 : 2));
            }

            @Override
            public int transformToCharPos(Context context, String title, String content, int boxPosition) {
                Rectangle rect = this.getTextArea(context, title);
                Point mouse = context.getInterface().getMouse();
                int offset = context.getInterface().getFontWidth(ImpactTheme.this.height, content.substring(0, boxPosition));
                if (rect.contains(mouse)) {
                    for (int i = 1; i <= content.length(); ++i) {
                        if (rect.x + ImpactTheme.this.padding / 2 - offset + context.getInterface().getFontWidth(ImpactTheme.this.height, content.substring(0, i)) <= mouse.x) continue;
                        return i - 1;
                    }
                    return content.length();
                }
                return -1;
            }
        };
    }

    @Override
    public ISwitchRenderer<Boolean> getToggleSwitchRenderer(int logicalLevel, final int graphicalLevel, final boolean container) {
        return new ISwitchRenderer<Boolean>(){

            @Override
            public void renderButton(Context context, String title, boolean focus, Boolean state) {
                Color color;
                boolean effFocus;
                boolean bl = effFocus = container ? context.hasFocus() : focus;
                if (graphicalLevel <= 0) {
                    if (container) {
                        color = ImpactTheme.this.scheme.getColor("Title Color");
                        context.getInterface().fillRect(context.getRect(), color, color, color, color);
                    } else {
                        ImpactTheme.this.renderBackground(context, effFocus);
                    }
                }
                if (!container) {
                    color = graphicalLevel <= 0 ? ImpactTheme.this.scheme.getColor("Panel Outline Color") : ImpactTheme.this.scheme.getColor("Component Outline Color");
                    ITheme.drawRect(context.getInterface(), context.getRect(), color);
                    ImpactTheme.this.renderOverlay(context);
                }
                ImpactTheme.this.renderOverlay(context);
                context.getInterface().drawString(new Point(context.getPos().x + ImpactTheme.this.padding, context.getPos().y + ImpactTheme.this.padding), ImpactTheme.this.height, title, ImpactTheme.this.getFontColor(focus));
                Color fillColor = ImpactTheme.this.getMainColor(focus, state);
                Rectangle rect = state != false ? this.getOnField(context) : this.getOffField(context);
                context.getInterface().fillRect(rect, fillColor, fillColor, fillColor, fillColor);
                rect = context.getRect();
                rect = new Rectangle(rect.x + rect.width - 2 * rect.height + 3 * ImpactTheme.this.padding, rect.y + ImpactTheme.this.padding, 2 * rect.height - 4 * ImpactTheme.this.padding, rect.height - 2 * ImpactTheme.this.padding);
                ITheme.drawRect(context.getInterface(), rect, ImpactTheme.this.scheme.getColor("Component Outline Color"));
            }

            @Override
            public int getDefaultHeight() {
                return ImpactTheme.this.getBaseHeight();
            }

            @Override
            public Rectangle getOnField(Context context) {
                Rectangle rect = context.getRect();
                return new Rectangle(rect.x + rect.width - rect.height + ImpactTheme.this.padding, rect.y + ImpactTheme.this.padding, rect.height - 2 * ImpactTheme.this.padding, rect.height - 2 * ImpactTheme.this.padding);
            }

            @Override
            public Rectangle getOffField(Context context) {
                Rectangle rect = context.getRect();
                return new Rectangle(rect.x + rect.width - 2 * rect.height + 3 * ImpactTheme.this.padding, rect.y + ImpactTheme.this.padding, rect.height - 2 * ImpactTheme.this.padding, rect.height - 2 * ImpactTheme.this.padding);
            }
        };
    }

    @Override
    public ISwitchRenderer<String> getCycleSwitchRenderer(final int logicalLevel, final int graphicalLevel, final boolean container) {
        return new ISwitchRenderer<String>(){

            @Override
            public void renderButton(Context context, String title, boolean focus, String state) {
                Color color;
                boolean effFocus = container ? context.hasFocus() : focus;
                Context subContext = new Context(context, context.getSize().width - 2 * context.getSize().height, new Point(0, 0), true, true);
                subContext.setHeight(context.getSize().height);
                if (graphicalLevel <= 0) {
                    if (container) {
                        color = ImpactTheme.this.scheme.getColor("Title Color");
                        context.getInterface().fillRect(subContext.getRect(), color, color, color, color);
                    } else {
                        ImpactTheme.this.renderBackground(subContext, effFocus);
                    }
                }
                if (!container) {
                    color = graphicalLevel <= 0 ? ImpactTheme.this.scheme.getColor("Panel Outline Color") : ImpactTheme.this.scheme.getColor("Component Outline Color");
                    ITheme.drawRect(context.getInterface(), subContext.getRect(), color);
                    ImpactTheme.this.renderOverlay(subContext);
                }
                Color valueColor = ImpactTheme.this.getFontColor(effFocus);
                if (context.isHovered() && context.getInterface().getMouse().x > subContext.getPos().x + subContext.getSize().height - ImpactTheme.this.padding) {
                    valueColor = ImpactTheme.this.scheme.getColor("Active Font Color");
                }
                Color fontColor = ImpactTheme.this.scheme.getColor("Active Font Color");
                int xpos = context.getPos().x + context.getSize().height - ImpactTheme.this.padding;
                if (container && graphicalLevel <= 0) {
                    xpos = subContext.getPos().x + subContext.getSize().width / 2 - context.getInterface().getFontWidth(ImpactTheme.this.height, title) / 2;
                }
                context.getInterface().drawString(new Point(xpos, subContext.getPos().y + ImpactTheme.this.padding - (container ? 1 : 0)), ImpactTheme.this.height, title, fontColor);
                context.getInterface().drawString(new Point(subContext.getPos().x + subContext.getSize().width - ImpactTheme.this.padding - context.getInterface().getFontWidth(ImpactTheme.this.height, state), subContext.getPos().y + ImpactTheme.this.padding - (container ? 1 : 0)), ImpactTheme.this.height, state, valueColor);
                Rectangle rect = this.getOnField(context);
                subContext = new Context(context, rect.width, new Point(rect.x - context.getPos().x, 0), true, true);
                subContext.setHeight(rect.height);
                ImpactTheme.this.getSmallButtonRenderer(5, logicalLevel, graphicalLevel, false).renderButton(subContext, null, effFocus, null);
                rect = this.getOffField(context);
                subContext = new Context(context, rect.width, new Point(rect.x - context.getPos().x, 0), true, true);
                subContext.setHeight(rect.height);
                ImpactTheme.this.getSmallButtonRenderer(4, logicalLevel, graphicalLevel, false).renderButton(subContext, null, effFocus, null);
            }

            @Override
            public int getDefaultHeight() {
                return ImpactTheme.this.getBaseHeight();
            }

            @Override
            public Rectangle getOnField(Context context) {
                Rectangle rect = context.getRect();
                return new Rectangle(rect.x + rect.width - rect.height, rect.y, rect.height, rect.height);
            }

            @Override
            public Rectangle getOffField(Context context) {
                Rectangle rect = context.getRect();
                return new Rectangle(rect.x + rect.width - 2 * rect.height, rect.y, rect.height, rect.height);
            }
        };
    }

    @Override
    public IColorPickerRenderer getColorPickerRenderer() {
        return new StandardColorPicker(){

            @Override
            public int getPadding() {
                return ImpactTheme.this.padding;
            }

            @Override
            public int getBaseHeight() {
                return ImpactTheme.this.getBaseHeight();
            }

            @Override
            public void renderCursor(Context context, Point p, Color color) {
                Color fontColor = ImpactTheme.this.scheme.getColor("Active Font Color");
                context.getInterface().fillRect(new Rectangle(p.x - 1, p.y - 1, 2, 2), fontColor, fontColor, fontColor, fontColor);
            }
        };
    }

    @Override
    public int getBaseHeight() {
        return this.height + 2 * this.padding;
    }

    @Override
    public Color getMainColor(boolean focus, boolean active) {
        if (active) {
            return this.scheme.getColor("Enabled Color");
        }
        return this.scheme.getColor("Disabled Color");
    }

    @Override
    public Color getBackgroundColor(boolean focus) {
        return this.scheme.getColor("Background Color");
    }

    @Override
    public Color getFontColor(boolean focus) {
        return this.scheme.getColor("Hovered Font Color");
    }
}

