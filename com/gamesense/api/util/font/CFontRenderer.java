/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.font;

import com.gamesense.api.util.font.CFont;
import com.gamesense.api.util.render.GSColor;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class CFontRenderer
extends CFont {
    protected CFont.CharData[] boldChars = new CFont.CharData[256];
    protected CFont.CharData[] italicChars = new CFont.CharData[256];
    protected CFont.CharData[] boldItalicChars = new CFont.CharData[256];
    private final int[] colorCode = new int[32];
    private final String colorcodeIdentifiers = "0123456789abcdefklmnor";
    String fontName = "Arial";
    int fontSize = 18;
    protected DynamicTexture texBold;
    protected DynamicTexture texItalic;
    protected DynamicTexture texItalicBold;

    public CFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        super(font, antiAlias, fractionalMetrics);
        this.setupMinecraftColorcodes();
        this.setupBoldItalicIDs();
    }

    public String getFontName() {
        return this.fontName;
    }

    public int getFontSize() {
        return this.fontSize;
    }

    public void setFontName(String newName) {
        this.fontName = newName;
    }

    public void setFontSize(int newSize) {
        this.fontSize = newSize;
    }

    public float drawStringWithShadow(String text, double x, double y, GSColor color) {
        float shadowWidth = this.drawString(text, x + 1.0, y + 1.0, color, true);
        return Math.max(shadowWidth, this.drawString(text, x, y, color, false));
    }

    public float drawString(String text, float x, float y, GSColor color) {
        return this.drawString(text, x, y, color, false);
    }

    public float drawCenteredStringWithShadow(String text, float x, float y, GSColor color) {
        return this.drawStringWithShadow(text, x - (float)(this.getStringWidth(text) / 2), y, color);
    }

    public float drawCenteredString(String text, float x, float y, GSColor color) {
        return this.drawString(text, x - (float)(this.getStringWidth(text) / 2), y, color);
    }

    public float drawString(String text, double x, double y, GSColor gsColor, boolean shadow) {
        x -= 1.0;
        y -= 2.0;
        GSColor color = new GSColor(gsColor);
        if (text == null) {
            return 0.0f;
        }
        if (color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 255 && color.getAlpha() == 32) {
            color = new GSColor(255, 255, 255);
        }
        if (color.getAlpha() < 4) {
            color = new GSColor(color, 255);
        }
        if (shadow) {
            color = new GSColor(color.getRed() / 4, color.getGreen() / 4, color.getBlue() / 4, color.getAlpha());
        }
        CFont.CharData[] currentData = this.charData;
        boolean randomCase = false;
        boolean bold = false;
        boolean italic = false;
        boolean strikethrough = false;
        boolean underline = false;
        boolean render = true;
        x *= 2.0;
        y *= 2.0;
        if (render) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179139_a((double)0.5, (double)0.5, (double)0.5);
            GlStateManager.func_179147_l();
            GlStateManager.func_179112_b((int)770, (int)771);
            GlStateManager.func_179131_c((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
            int size = text.length();
            GlStateManager.func_179098_w();
            GlStateManager.func_179144_i((int)this.tex.func_110552_b());
            for (int i = 0; i < size; ++i) {
                char character = text.charAt(i);
                if (character == '\u00a7' && i < size) {
                    int colorIndex = 21;
                    try {
                        colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    if (colorIndex < 16) {
                        bold = false;
                        italic = false;
                        randomCase = false;
                        underline = false;
                        strikethrough = false;
                        GlStateManager.func_179144_i((int)this.tex.func_110552_b());
                        currentData = this.charData;
                        if (colorIndex < 0 || colorIndex > 15) {
                            colorIndex = 15;
                        }
                        if (shadow) {
                            colorIndex += 16;
                        }
                        int colorcode = this.colorCode[colorIndex];
                        GlStateManager.func_179131_c((float)((float)(colorcode >> 16 & 0xFF) / 255.0f), (float)((float)(colorcode >> 8 & 0xFF) / 255.0f), (float)((float)(colorcode & 0xFF) / 255.0f), (float)color.getAlpha());
                    } else if (colorIndex == 16) {
                        randomCase = true;
                    } else if (colorIndex == 17) {
                        bold = true;
                        if (italic) {
                            GlStateManager.func_179144_i((int)this.texItalicBold.func_110552_b());
                            currentData = this.boldItalicChars;
                        } else {
                            GlStateManager.func_179144_i((int)this.texBold.func_110552_b());
                            currentData = this.boldChars;
                        }
                    } else if (colorIndex == 18) {
                        strikethrough = true;
                    } else if (colorIndex == 19) {
                        underline = true;
                    } else if (colorIndex == 20) {
                        italic = true;
                        if (bold) {
                            GlStateManager.func_179144_i((int)this.texItalicBold.func_110552_b());
                            currentData = this.boldItalicChars;
                        } else {
                            GlStateManager.func_179144_i((int)this.texItalic.func_110552_b());
                            currentData = this.italicChars;
                        }
                    } else if (colorIndex == 21) {
                        bold = false;
                        italic = false;
                        randomCase = false;
                        underline = false;
                        strikethrough = false;
                        GlStateManager.func_179131_c((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
                        GlStateManager.func_179144_i((int)this.tex.func_110552_b());
                        currentData = this.charData;
                    }
                    ++i;
                    continue;
                }
                if (character >= currentData.length || character < '\u0000') continue;
                GlStateManager.func_187447_r((int)4);
                this.drawChar(currentData, character, (float)x, (float)y);
                GlStateManager.func_187437_J();
                if (strikethrough) {
                    this.drawLine(x, y + (double)(currentData[character].height / 2), x + (double)currentData[character].width - 8.0, y + (double)(currentData[character].height / 2), 1.0f);
                }
                if (underline) {
                    this.drawLine(x, y + (double)currentData[character].height - 2.0, x + (double)currentData[character].width - 8.0, y + (double)currentData[character].height - 2.0, 1.0f);
                }
                x += (double)(currentData[character].width - 8 + this.charOffset);
            }
            GL11.glHint((int)3155, (int)4352);
            GlStateManager.func_179121_F();
        }
        return (float)x / 2.0f;
    }

    @Override
    public int getStringWidth(String text) {
        if (text == null) {
            return 0;
        }
        int width = 0;
        CFont.CharData[] currentData = this.charData;
        boolean bold = false;
        boolean italic = false;
        int size = text.length();
        for (int i = 0; i < size; ++i) {
            char character = text.charAt(i);
            if (character == '\u00a7' && i < size) {
                int colorIndex = "0123456789abcdefklmnor".indexOf(character);
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                } else if (colorIndex == 17) {
                    bold = true;
                    currentData = italic ? this.boldItalicChars : this.boldChars;
                } else if (colorIndex == 20) {
                    italic = true;
                    currentData = bold ? this.boldItalicChars : this.italicChars;
                } else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    currentData = this.charData;
                }
                ++i;
                continue;
            }
            if (character >= currentData.length || character < '\u0000') continue;
            width += currentData[character].width - 8 + this.charOffset;
        }
        return width / 2;
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        this.setupBoldItalicIDs();
    }

    @Override
    public void setAntiAlias(boolean antiAlias) {
        super.setAntiAlias(antiAlias);
        this.setupBoldItalicIDs();
    }

    @Override
    public void setFractionalMetrics(boolean fractionalMetrics) {
        super.setFractionalMetrics(fractionalMetrics);
        this.setupBoldItalicIDs();
    }

    private void setupBoldItalicIDs() {
        this.texBold = this.setupTexture(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics, this.boldChars);
        this.texItalic = this.setupTexture(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics, this.italicChars);
        this.texItalicBold = this.setupTexture(this.font.deriveFont(3), this.antiAlias, this.fractionalMetrics, this.boldItalicChars);
    }

    private void drawLine(double x, double y, double x1, double y1, float width) {
        GlStateManager.func_179090_x();
        GlStateManager.func_187441_d((float)width);
        GlStateManager.func_187447_r((int)1);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)x1, (double)y1);
        GlStateManager.func_187437_J();
        GlStateManager.func_179098_w();
    }

    public List<String> wrapWords(String text, double width) {
        ArrayList<String> finalWords = new ArrayList<String>();
        if ((double)this.getStringWidth(text) > width) {
            String[] words = text.split(" ");
            String currentWord = "";
            char lastColorCode = '\uffff';
            for (String word : words) {
                for (int i = 0; i < word.toCharArray().length; ++i) {
                    char c = word.toCharArray()[i];
                    if (c != '\u00a7' || i >= word.toCharArray().length - 1) continue;
                    lastColorCode = word.toCharArray()[i + 1];
                }
                StringBuilder stringBuilder = new StringBuilder();
                if ((double)this.getStringWidth(stringBuilder.append(currentWord).append(word).append(" ").toString()) < width) {
                    currentWord = currentWord + word + " ";
                    continue;
                }
                finalWords.add(currentWord);
                currentWord = "\u00a7" + lastColorCode + word + " ";
            }
            if (currentWord.length() > 0) {
                if ((double)this.getStringWidth(currentWord) < width) {
                    finalWords.add("\u00a7" + lastColorCode + currentWord + " ");
                    currentWord = "";
                } else {
                    for (String s : this.formatString(currentWord, width)) {
                        finalWords.add(s);
                    }
                }
            }
        } else {
            finalWords.add(text);
        }
        return finalWords;
    }

    public List<String> formatString(String string, double width) {
        ArrayList<String> finalWords = new ArrayList<String>();
        String currentWord = "";
        char lastColorCode = '\uffff';
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if (c == '\u00a7' && i < chars.length - 1) {
                lastColorCode = chars[i + 1];
            }
            StringBuilder stringBuilder = new StringBuilder();
            if ((double)this.getStringWidth(stringBuilder.append(currentWord).append(c).toString()) < width) {
                currentWord = currentWord + c;
                continue;
            }
            finalWords.add(currentWord);
            currentWord = "\u00a7" + lastColorCode + c;
        }
        if (currentWord.length() > 0) {
            finalWords.add(currentWord);
        }
        return finalWords;
    }

    private void setupMinecraftColorcodes() {
        for (int index = 0; index < 32; ++index) {
            int noClue = (index >> 3 & 1) * 85;
            int red = (index >> 2 & 1) * 170 + noClue;
            int green = (index >> 1 & 1) * 170 + noClue;
            int blue = (index >> 0 & 1) * 170 + noClue;
            if (index == 6) {
                red += 85;
            }
            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }
            this.colorCode[index] = (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
        }
    }
}

