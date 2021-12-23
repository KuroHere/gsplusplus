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

public class GameSenseTheme
extends ThemeBase {
    protected int height;
    protected int padding;
    protected int scroll;
    protected String separator;

    public GameSenseTheme(IColorScheme scheme, int height, int padding, int scroll, String separator) {
        super(scheme);
        this.height = height;
        this.padding = padding;
        this.scroll = scroll;
        this.separator = separator;
        scheme.createSetting(this, "Title Color", "The color for panel titles.", false, true, new Color(255, 0, 0), false);
        scheme.createSetting(this, "Outline Color", "The color for panel outlines.", false, true, new Color(255, 0, 0), false);
        scheme.createSetting(this, "Enabled Color", "The main color for enabled components.", true, true, new Color(255, 0, 0, 150), false);
        scheme.createSetting(this, "Disabled Color", "The main color for disabled modules.", false, true, new Color(0, 0, 0), false);
        scheme.createSetting(this, "Settings Color", "The background color for settings.", false, true, new Color(30, 30, 30), false);
        scheme.createSetting(this, "Font Color", "The main color for text.", false, true, new Color(255, 255, 255), false);
        scheme.createSetting(this, "Highlight Color", "The color for highlighted text.", false, true, new Color(0, 0, 255), false);
    }

    protected void fillBaseRect(Context context, boolean focus, boolean active, int logicalLevel, int graphicalLevel, Color colorState) {
        Color color = this.getMainColor(focus, active);
        if (logicalLevel > 1 && !active) {
            color = this.getBackgroundColor(focus);
        } else if (graphicalLevel <= 0 && active) {
            color = ITheme.combineColors(this.getColor(this.scheme.getColor("Title Color")), this.scheme.getColor("Enabled Color"));
        }
        if (colorState != null) {
            color = ITheme.combineColors(colorState, this.scheme.getColor("Enabled Color"));
        }
        context.getInterface().fillRect(context.getRect(), color, color, color, color);
    }

    protected void renderOverlay(Context context) {
        Color color = context.isHovered() ? new Color(255, 255, 255, 64) : new Color(0, 0, 0, 0);
        context.getInterface().fillRect(context.getRect(), color, color, color, color);
    }

    @Override
    public IDescriptionRenderer getDescriptionRenderer() {
        return new IDescriptionRenderer(){

            @Override
            public void renderDescription(IInterface inter, Point pos, String text) {
                Rectangle rect = new Rectangle(pos, new Dimension(inter.getFontWidth(GameSenseTheme.this.height, text) + 4, GameSenseTheme.this.height + 4));
                Color color = GameSenseTheme.this.getMainColor(true, false);
                inter.fillRect(rect, color, color, color, color);
                inter.drawString(new Point(pos.x + 2, pos.y + 2), GameSenseTheme.this.height, text, GameSenseTheme.this.getFontColor(true));
                ITheme.drawRect(inter, rect, GameSenseTheme.this.scheme.getColor("Outline Color"));
            }
        };
    }

    @Override
    public IContainerRenderer getContainerRenderer(int logicalLevel, final int graphicalLevel, boolean horizontal) {
        return new IContainerRenderer(){

            @Override
            public void renderBackground(Context context, boolean focus) {
                if (graphicalLevel > 0) {
                    Color color = GameSenseTheme.this.scheme.getColor("Outline Color");
                    context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y, context.getSize().width, 1), color, color, color, color);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y + context.getSize().height - 1, context.getSize().width, 1), color, color, color, color);
                }
            }

            @Override
            public int getTop() {
                return graphicalLevel <= 0 ? 0 : 1;
            }

            @Override
            public int getBottom() {
                return graphicalLevel <= 0 ? 0 : 1;
            }
        };
    }

    @Override
    public <T> IPanelRenderer<T> getPanelRenderer(Class<T> type, int logicalLevel, final int graphicalLevel) {
        return new IPanelRenderer<T>(){

            @Override
            public int getLeft() {
                return graphicalLevel == 0 ? 1 : 0;
            }

            @Override
            public int getRight() {
                return graphicalLevel == 0 ? 1 : 0;
            }

            @Override
            public void renderPanelOverlay(Context context, boolean focus, T state, boolean open) {
                if (graphicalLevel == 0) {
                    ITheme.drawRect(context.getInterface(), context.getRect(), GameSenseTheme.this.scheme.getColor("Outline Color"));
                }
            }

            @Override
            public void renderTitleOverlay(Context context, boolean focus, T state, boolean open) {
            }
        };
    }

    @Override
    public <T> IScrollBarRenderer<T> getScrollBarRenderer(Class<T> type, int logicalLevel, int graphicalLevel) {
        return new IScrollBarRenderer<T>(){

            @Override
            public int renderScrollBar(Context context, boolean focus, T state, boolean horizontal, int height, int position) {
                int a;
                Color activecolor = GameSenseTheme.this.getMainColor(focus, true);
                Color inactivecolor = GameSenseTheme.this.getMainColor(focus, false);
                if (horizontal) {
                    a = (int)((double)position / (double)height * (double)context.getSize().width);
                    int b = (int)((double)(position + context.getSize().width) / (double)height * (double)context.getSize().width);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y, a, context.getSize().height), inactivecolor, inactivecolor, inactivecolor, inactivecolor);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x + a, context.getPos().y, b - a, context.getSize().height), activecolor, activecolor, activecolor, activecolor);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x + b, context.getPos().y, context.getSize().width - b, context.getSize().height), inactivecolor, inactivecolor, inactivecolor, inactivecolor);
                } else {
                    a = (int)((double)position / (double)height * (double)context.getSize().height);
                    int b = (int)((double)(position + context.getSize().height) / (double)height * (double)context.getSize().height);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y, context.getSize().width, a), inactivecolor, inactivecolor, inactivecolor, inactivecolor);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y + a, context.getSize().width, b - a), activecolor, activecolor, activecolor, activecolor);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y + b, context.getSize().width, context.getSize().height - b), inactivecolor, inactivecolor, inactivecolor, inactivecolor);
                }
                Color bordercolor = GameSenseTheme.this.scheme.getColor("Outline Color");
                if (horizontal) {
                    context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y, context.getSize().width, 1), bordercolor, bordercolor, bordercolor, bordercolor);
                } else {
                    context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y, 1, context.getSize().height), bordercolor, bordercolor, bordercolor, bordercolor);
                }
                if (horizontal) {
                    return (int)((double)((context.getInterface().getMouse().x - context.getPos().x) * height) / (double)context.getSize().width - (double)context.getSize().width / 2.0);
                }
                return (int)((double)((context.getInterface().getMouse().y - context.getPos().y) * height) / (double)context.getSize().height - (double)context.getSize().height / 2.0);
            }

            @Override
            public int getThickness() {
                return GameSenseTheme.this.scroll;
            }
        };
    }

    @Override
    public <T> IEmptySpaceRenderer<T> getEmptySpaceRenderer(Class<T> type, int logicalLevel, int graphicalLevel, boolean container) {
        return (context, focus, state) -> {
            Color color = container ? (logicalLevel > 0 ? this.getBackgroundColor(focus) : this.getMainColor(focus, false)) : this.scheme.getColor("Outline Color");
            context.getInterface().fillRect(context.getRect(), color, color, color, color);
        };
    }

    @Override
    public <T> IButtonRenderer<T> getButtonRenderer(final Class<T> type, final int logicalLevel, final int graphicalLevel, final boolean container) {
        return new IButtonRenderer<T>(){

            @Override
            public void renderButton(Context context, String title, boolean focus, T state) {
                boolean effFocus;
                boolean bl = effFocus = container ? context.hasFocus() : focus;
                if (type == Boolean.class) {
                    GameSenseTheme.this.fillBaseRect(context, effFocus, (Boolean)state, logicalLevel, graphicalLevel, null);
                } else if (type == Color.class) {
                    GameSenseTheme.this.fillBaseRect(context, effFocus, graphicalLevel <= 0, logicalLevel, graphicalLevel, (Color)state);
                } else {
                    GameSenseTheme.this.fillBaseRect(context, effFocus, graphicalLevel <= 0, logicalLevel, graphicalLevel, null);
                }
                if (graphicalLevel <= 0 && container) {
                    Color color = GameSenseTheme.this.scheme.getColor("Outline Color");
                    context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y + context.getSize().height - 1, context.getSize().width, 1), color, color, color, color);
                }
                GameSenseTheme.this.renderOverlay(context);
                if (type == String.class) {
                    context.getInterface().drawString(new Point(context.getPos().x + GameSenseTheme.this.padding, context.getPos().y + GameSenseTheme.this.padding), GameSenseTheme.this.height, title + GameSenseTheme.this.separator + state, GameSenseTheme.this.getFontColor(focus));
                } else {
                    context.getInterface().drawString(new Point(context.getPos().x + GameSenseTheme.this.padding, context.getPos().y + GameSenseTheme.this.padding), GameSenseTheme.this.height, title, GameSenseTheme.this.getFontColor(focus));
                }
            }

            @Override
            public int getDefaultHeight() {
                return GameSenseTheme.this.getBaseHeight();
            }
        };
    }

    @Override
    public IButtonRenderer<Void> getSmallButtonRenderer(final int symbol, final int logicalLevel, final int graphicalLevel, final boolean container) {
        return new IButtonRenderer<Void>(){

            @Override
            public void renderButton(Context context, String title, boolean focus, Void state) {
                boolean effFocus = container ? context.hasFocus() : focus;
                GameSenseTheme.this.fillBaseRect(context, effFocus, true, logicalLevel, graphicalLevel, null);
                GameSenseTheme.this.renderOverlay(context);
                Point[] points = new Point[3];
                int padding = context.getSize().height <= 2 * GameSenseTheme.this.padding ? 2 : GameSenseTheme.this.padding;
                Rectangle rect = new Rectangle(context.getPos().x + padding / 2, context.getPos().y + padding / 2, context.getSize().height - 2 * (padding / 2), context.getSize().height - 2 * (padding / 2));
                if (title == null) {
                    rect.x += context.getSize().width / 2 - context.getSize().height / 2;
                }
                Color color = GameSenseTheme.this.getFontColor(effFocus);
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
                        points[1] = new Point(rect.x + rect.width, rect.y + rect.height);
                        points[0] = new Point(rect.x, rect.y + rect.height / 2);
                        break;
                    }
                    case 5: {
                        if (rect.height % 2 == 1) {
                            --rect.height;
                        }
                        points[0] = new Point(rect.x, rect.y);
                        points[1] = new Point(rect.x, rect.y + rect.height);
                        points[2] = new Point(rect.x + rect.width, rect.y + rect.height / 2);
                        break;
                    }
                    case 6: {
                        if (rect.width % 2 == 1) {
                            --rect.width;
                        }
                        points[0] = new Point(rect.x, rect.y + rect.height);
                        points[1] = new Point(rect.x + rect.width, rect.y + rect.height);
                        points[2] = new Point(rect.x + rect.width / 2, rect.y);
                        break;
                    }
                    case 7: {
                        if (rect.width % 2 == 1) {
                            --rect.width;
                        }
                        points[2] = new Point(rect.x, rect.y);
                        points[1] = new Point(rect.x + rect.width, rect.y);
                        points[0] = new Point(rect.x + rect.width / 2, rect.y + rect.height);
                    }
                }
                if (symbol >= 4 && symbol <= 7) {
                    context.getInterface().fillTriangle(points[0], points[1], points[2], color, color, color);
                }
                if (title != null) {
                    context.getInterface().drawString(new Point(context.getPos().x + (symbol == 0 ? padding : context.getSize().height), context.getPos().y + padding), GameSenseTheme.this.height, title, GameSenseTheme.this.getFontColor(focus));
                }
            }

            @Override
            public int getDefaultHeight() {
                return GameSenseTheme.this.getBaseHeight();
            }
        };
    }

    @Override
    public IButtonRenderer<String> getKeybindRenderer(final int logicalLevel, final int graphicalLevel, final boolean container) {
        return new IButtonRenderer<String>(){

            @Override
            public void renderButton(Context context, String title, boolean focus, String state) {
                boolean effFocus = container ? context.hasFocus() : focus;
                GameSenseTheme.this.fillBaseRect(context, effFocus, effFocus, logicalLevel, graphicalLevel, null);
                GameSenseTheme.this.renderOverlay(context);
                context.getInterface().drawString(new Point(context.getPos().x + GameSenseTheme.this.padding, context.getPos().y + GameSenseTheme.this.padding), GameSenseTheme.this.height, title + GameSenseTheme.this.separator + (focus ? "..." : state), GameSenseTheme.this.getFontColor(focus));
            }

            @Override
            public int getDefaultHeight() {
                return GameSenseTheme.this.getBaseHeight();
            }
        };
    }

    @Override
    public ISliderRenderer getSliderRenderer(int logicalLevel, int graphicalLevel, final boolean container) {
        return new ISliderRenderer(){

            @Override
            public void renderSlider(Context context, String title, String state, boolean focus, double value) {
                boolean effFocus = container ? context.hasFocus() : focus;
                Color colorA = GameSenseTheme.this.getMainColor(effFocus, true);
                Color colorB = GameSenseTheme.this.getBackgroundColor(effFocus);
                Rectangle rect = this.getSlideArea(context, title, state);
                int divider = (int)((double)rect.width * value);
                context.getInterface().fillRect(new Rectangle(rect.x, rect.y, divider, rect.height), colorA, colorA, colorA, colorA);
                context.getInterface().fillRect(new Rectangle(rect.x + divider, rect.y, rect.width - divider, rect.height), colorB, colorB, colorB, colorB);
                GameSenseTheme.this.renderOverlay(context);
                context.getInterface().drawString(new Point(context.getPos().x + GameSenseTheme.this.padding, context.getPos().y + GameSenseTheme.this.padding), GameSenseTheme.this.height, title + GameSenseTheme.this.separator + state, GameSenseTheme.this.getFontColor(focus));
            }

            @Override
            public int getDefaultHeight() {
                return GameSenseTheme.this.getBaseHeight();
            }
        };
    }

    @Override
    public IRadioRenderer getRadioRenderer(final int logicalLevel, final int graphicalLevel, boolean container) {
        return new IRadioRenderer(){

            @Override
            public void renderItem(Context context, ILabeled[] items, boolean focus, int target, double state, boolean horizontal) {
                for (int i = 0; i < items.length; ++i) {
                    Rectangle rect = this.getItemRect(context, items, i, horizontal);
                    Context subContext = new Context(context.getInterface(), rect.width, rect.getLocation(), context.hasFocus(), context.onTop());
                    subContext.setHeight(rect.height);
                    GameSenseTheme.this.fillBaseRect(subContext, focus, i == target, logicalLevel, graphicalLevel, null);
                    GameSenseTheme.this.renderOverlay(subContext);
                    context.getInterface().drawString(new Point(rect.x + GameSenseTheme.this.padding, rect.y + GameSenseTheme.this.padding), GameSenseTheme.this.height, items[i].getDisplayName(), GameSenseTheme.this.getFontColor(focus));
                }
            }

            @Override
            public int getDefaultHeight(ILabeled[] items, boolean horizontal) {
                return (horizontal ? 1 : items.length) * GameSenseTheme.this.getBaseHeight();
            }
        };
    }

    @Override
    public IResizeBorderRenderer getResizeRenderer() {
        return new IResizeBorderRenderer(){

            @Override
            public void drawBorder(Context context, boolean focus) {
                Color color = ITheme.combineColors(GameSenseTheme.this.scheme.getColor("Outline Color"), GameSenseTheme.this.scheme.getColor("Enabled Color"));
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
    public ITextFieldRenderer getTextRenderer(final boolean embed, final int logicalLevel, final int graphicalLevel, final boolean container) {
        return new ITextFieldRenderer(){

            @Override
            public int renderTextField(Context context, String title, boolean focus, String content, int position, int select, int boxPosition, boolean insertMode) {
                int maxPosition;
                boolean effFocus = container ? context.hasFocus() : focus;
                Color color = focus ? GameSenseTheme.this.scheme.getColor("Outline Color") : GameSenseTheme.this.scheme.getColor("Settings Color");
                Color textColor = GameSenseTheme.this.getFontColor(effFocus);
                Color highlightColor = GameSenseTheme.this.scheme.getColor("Highlight Color");
                Rectangle rect = this.getTextArea(context, title);
                int strlen = context.getInterface().getFontWidth(GameSenseTheme.this.height, content.substring(0, position));
                if (boxPosition < position) {
                    int minPosition;
                    for (minPosition = boxPosition; minPosition < position && context.getInterface().getFontWidth(GameSenseTheme.this.height, content.substring(0, minPosition)) + rect.width - GameSenseTheme.this.padding < strlen; ++minPosition) {
                    }
                    if (boxPosition < minPosition) {
                        boxPosition = minPosition;
                    }
                } else if (boxPosition > position) {
                    boxPosition = position - 1;
                }
                for (maxPosition = content.length(); maxPosition > 0; --maxPosition) {
                    if (context.getInterface().getFontWidth(GameSenseTheme.this.height, content.substring(maxPosition)) < rect.width - GameSenseTheme.this.padding) continue;
                    ++maxPosition;
                    break;
                }
                if (boxPosition > maxPosition) {
                    boxPosition = maxPosition;
                } else if (boxPosition < 0) {
                    boxPosition = 0;
                }
                int offset = context.getInterface().getFontWidth(GameSenseTheme.this.height, content.substring(0, boxPosition));
                int x1 = rect.x + GameSenseTheme.this.padding / 2 - offset + strlen;
                int x2 = rect.x + GameSenseTheme.this.padding / 2 - offset;
                x2 = position < content.length() ? (x2 += context.getInterface().getFontWidth(GameSenseTheme.this.height, content.substring(0, position + 1))) : (x2 += context.getInterface().getFontWidth(GameSenseTheme.this.height, content + "X"));
                GameSenseTheme.this.fillBaseRect(context, effFocus, false, logicalLevel, graphicalLevel, null);
                GameSenseTheme.this.renderOverlay(context);
                context.getInterface().drawString(new Point(context.getRect().x + GameSenseTheme.this.padding, context.getRect().y + GameSenseTheme.this.padding / (embed ? 2 : 1)), GameSenseTheme.this.height, title + (embed ? GameSenseTheme.this.separator : ""), textColor);
                context.getInterface().window(rect);
                if (select >= 0) {
                    int x3 = rect.x + GameSenseTheme.this.padding / 2 - offset + context.getInterface().getFontWidth(GameSenseTheme.this.height, content.substring(0, select));
                    context.getInterface().fillRect(new Rectangle(Math.min(x1, x3), rect.y + GameSenseTheme.this.padding / 2, Math.abs(x3 - x1), GameSenseTheme.this.height), highlightColor, highlightColor, highlightColor, highlightColor);
                }
                context.getInterface().drawString(new Point(rect.x + GameSenseTheme.this.padding / 2 - offset, rect.y + GameSenseTheme.this.padding / 2), GameSenseTheme.this.height, content, textColor);
                if (System.currentTimeMillis() / 500L % 2L == 0L && focus) {
                    if (insertMode) {
                        context.getInterface().fillRect(new Rectangle(x1, rect.y + GameSenseTheme.this.padding / 2 + GameSenseTheme.this.height, x2 - x1, 1), textColor, textColor, textColor, textColor);
                    } else {
                        context.getInterface().fillRect(new Rectangle(x1, rect.y + GameSenseTheme.this.padding / 2, 1, GameSenseTheme.this.height), textColor, textColor, textColor, textColor);
                    }
                }
                ITheme.drawRect(context.getInterface(), rect, color);
                context.getInterface().restore();
                return boxPosition;
            }

            @Override
            public int getDefaultHeight() {
                if (embed) {
                    int height = GameSenseTheme.this.getBaseHeight() - GameSenseTheme.this.padding;
                    if (height % 2 == 1) {
                        ++height;
                    }
                    return height;
                }
                return 2 * GameSenseTheme.this.getBaseHeight();
            }

            @Override
            public Rectangle getTextArea(Context context, String title) {
                Rectangle rect = context.getRect();
                if (embed) {
                    int length = GameSenseTheme.this.padding + context.getInterface().getFontWidth(GameSenseTheme.this.height, title + GameSenseTheme.this.separator);
                    return new Rectangle(rect.x + length, rect.y, rect.width - length, rect.height);
                }
                return new Rectangle(rect.x + GameSenseTheme.this.padding, rect.y + GameSenseTheme.this.getBaseHeight(), rect.width - 2 * GameSenseTheme.this.padding, rect.height - GameSenseTheme.this.getBaseHeight() - GameSenseTheme.this.padding);
            }

            @Override
            public int transformToCharPos(Context context, String title, String content, int boxPosition) {
                Rectangle rect = this.getTextArea(context, title);
                Point mouse = context.getInterface().getMouse();
                int offset = context.getInterface().getFontWidth(GameSenseTheme.this.height, content.substring(0, boxPosition));
                if (rect.contains(mouse)) {
                    for (int i = 1; i <= content.length(); ++i) {
                        if (rect.x + GameSenseTheme.this.padding / 2 - offset + context.getInterface().getFontWidth(GameSenseTheme.this.height, content.substring(0, i)) <= mouse.x) continue;
                        return i - 1;
                    }
                    return content.length();
                }
                return -1;
            }
        };
    }

    @Override
    public ISwitchRenderer<Boolean> getToggleSwitchRenderer(final int logicalLevel, final int graphicalLevel, final boolean container) {
        return new ISwitchRenderer<Boolean>(){

            @Override
            public void renderButton(Context context, String title, boolean focus, Boolean state) {
                boolean effFocus = container ? context.hasFocus() : focus;
                GameSenseTheme.this.fillBaseRect(context, effFocus, false, logicalLevel, graphicalLevel, null);
                Color color = GameSenseTheme.this.scheme.getColor("Outline Color");
                if (graphicalLevel <= 0 && container) {
                    context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y + context.getSize().height - 1, context.getSize().width, 1), color, color, color, color);
                }
                GameSenseTheme.this.renderOverlay(context);
                context.getInterface().drawString(new Point(context.getPos().x + GameSenseTheme.this.padding, context.getPos().y + GameSenseTheme.this.padding), GameSenseTheme.this.height, title + GameSenseTheme.this.separator + (state != false ? "On" : "Off"), GameSenseTheme.this.getFontColor(focus));
                Color fillColor = GameSenseTheme.this.getMainColor(effFocus, true);
                Rectangle rect = state != false ? this.getOnField(context) : this.getOffField(context);
                context.getInterface().fillRect(rect, fillColor, fillColor, fillColor, fillColor);
                rect = context.getRect();
                rect = new Rectangle(rect.x + rect.width - 2 * rect.height + 3 * GameSenseTheme.this.padding, rect.y + GameSenseTheme.this.padding, 2 * rect.height - 4 * GameSenseTheme.this.padding, rect.height - 2 * GameSenseTheme.this.padding);
                ITheme.drawRect(context.getInterface(), rect, color);
            }

            @Override
            public int getDefaultHeight() {
                return GameSenseTheme.this.getBaseHeight();
            }

            @Override
            public Rectangle getOnField(Context context) {
                Rectangle rect = context.getRect();
                return new Rectangle(rect.x + rect.width - rect.height + GameSenseTheme.this.padding, rect.y + GameSenseTheme.this.padding, rect.height - 2 * GameSenseTheme.this.padding, rect.height - 2 * GameSenseTheme.this.padding);
            }

            @Override
            public Rectangle getOffField(Context context) {
                Rectangle rect = context.getRect();
                return new Rectangle(rect.x + rect.width - 2 * rect.height + 3 * GameSenseTheme.this.padding, rect.y + GameSenseTheme.this.padding, rect.height - 2 * GameSenseTheme.this.padding, rect.height - 2 * GameSenseTheme.this.padding);
            }
        };
    }

    @Override
    public ISwitchRenderer<String> getCycleSwitchRenderer(final int logicalLevel, final int graphicalLevel, final boolean container) {
        return new ISwitchRenderer<String>(){

            @Override
            public void renderButton(Context context, String title, boolean focus, String state) {
                boolean effFocus = container ? context.hasFocus() : focus;
                GameSenseTheme.this.fillBaseRect(context, effFocus, false, logicalLevel, graphicalLevel, null);
                Color color = GameSenseTheme.this.scheme.getColor("Outline Color");
                if (graphicalLevel <= 0 && container) {
                    context.getInterface().fillRect(new Rectangle(context.getPos().x, context.getPos().y + context.getSize().height - 1, context.getSize().width, 1), color, color, color, color);
                }
                Context subContext = new Context(context, context.getSize().width - 2 * context.getSize().height, new Point(0, 0), true, true);
                subContext.setHeight(context.getSize().height);
                GameSenseTheme.this.renderOverlay(subContext);
                Color textColor = GameSenseTheme.this.getFontColor(effFocus);
                context.getInterface().drawString(new Point(context.getPos().x + GameSenseTheme.this.padding, context.getPos().y + GameSenseTheme.this.padding), GameSenseTheme.this.height, title + GameSenseTheme.this.separator + state, textColor);
                Rectangle rect = this.getOnField(context);
                subContext = new Context(context, rect.width, new Point(rect.x - context.getPos().x, 0), true, true);
                subContext.setHeight(rect.height);
                GameSenseTheme.this.getSmallButtonRenderer(5, logicalLevel, graphicalLevel, container).renderButton(subContext, null, effFocus, null);
                rect = this.getOffField(context);
                subContext = new Context(context, rect.width, new Point(rect.x - context.getPos().x, 0), true, true);
                subContext.setHeight(rect.height);
                GameSenseTheme.this.getSmallButtonRenderer(4, logicalLevel, graphicalLevel, container).renderButton(subContext, null, effFocus, null);
            }

            @Override
            public int getDefaultHeight() {
                return GameSenseTheme.this.getBaseHeight();
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
                return GameSenseTheme.this.padding;
            }

            @Override
            public int getBaseHeight() {
                return GameSenseTheme.this.getBaseHeight();
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
            return ITheme.combineColors(this.getColor(this.scheme.getColor("Enabled Color")), this.scheme.getColor("Enabled Color"));
        }
        return ITheme.combineColors(this.scheme.getColor("Disabled Color"), this.scheme.getColor("Enabled Color"));
    }

    @Override
    public Color getBackgroundColor(boolean focus) {
        return ITheme.combineColors(this.scheme.getColor("Settings Color"), this.scheme.getColor("Enabled Color"));
    }

    @Override
    public Color getFontColor(boolean focus) {
        return this.scheme.getColor("Font Color");
    }
}

