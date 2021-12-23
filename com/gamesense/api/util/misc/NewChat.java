/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.misc;

import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.client.module.modules.misc.ChatModifier;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(value=Side.CLIENT)
public class NewChat
extends GuiNewChat {
    final ChatModifier chatModifier = ModuleManager.getModule(ChatModifier.class);
    private static final Logger LOGGER = LogManager.getLogger();
    private final Minecraft mc;
    private final List<String> sentMessages = Lists.newArrayList();
    private final List<ChatLine> chatLines = Lists.newArrayList();
    private final List<ChatLine> drawnChatLines = Lists.newArrayList();
    private int scrollPos;
    public static float percentComplete = 0.0f;
    public static int newLines;
    public static long prevMillis;
    public boolean configuring;
    double count = 0.0;
    String[] specialWords = null;

    public NewChat(Minecraft mcIn) {
        super(mcIn);
        this.mc = mcIn;
    }

    private void updatePercentage(long diff) {
        if (percentComplete < 1.0f) {
            percentComplete += 0.004f * (float)diff;
        }
        percentComplete = this.chatModifier.clamp(percentComplete, 0.0f, 1.0f);
    }

    public void func_146230_a(int updateCounter) {
        if (!((Boolean)this.chatModifier.stopDesyncSpecial.getValue()).booleanValue()) {
            this.count += (Double)this.chatModifier.customAdd.getValue() * (Double)this.chatModifier.customMultiply.getValue();
        }
        boolean customText = (Boolean)ModuleManager.getModule(ColorMain.class).textFont.getValue();
        if (this.configuring) {
            return;
        }
        if (prevMillis == -1L) {
            prevMillis = System.currentTimeMillis();
            return;
        }
        long current = System.currentTimeMillis();
        long diff = current - prevMillis;
        prevMillis = current;
        this.updatePercentage(diff);
        float t = percentComplete;
        float percent = 1.0f - (t -= 1.0f) * t * t * t;
        percent = this.chatModifier.clamp(percent, 0.0f, 1.0f);
        if (this.mc.field_71474_y.field_74343_n != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int i = this.func_146232_i();
            int j = this.drawnChatLines.size();
            float f = this.mc.field_71474_y.field_74357_r * 0.9f + 0.1f;
            if (j > 0) {
                boolean flag = false;
                if (this.func_146241_e()) {
                    flag = true;
                }
                float f1 = this.func_146244_h();
                int k = MathHelper.func_76123_f((float)((float)this.func_146228_f() / f1));
                GlStateManager.func_179094_E();
                float x = 2.0f + (float)((Integer)this.chatModifier.leftPosition.getValue()).intValue();
                float y = 8.0f - (float)((Integer)this.chatModifier.upPosition.getValue()).intValue();
                GlStateManager.func_179139_a((double)((Double)this.chatModifier.xScale.getValue()), (double)((Double)this.chatModifier.yScale.getValue()), (double)1.0);
                GlStateManager.func_179109_b((float)((Integer)this.chatModifier.leftPosition.getValue() != -1 ? (float)((Integer)this.chatModifier.leftPosition.getValue()).intValue() : x), (float)((Integer)this.chatModifier.upPosition.getValue() != -100 ? (float)(-((Integer)this.chatModifier.upPosition.getValue()).intValue()) : y), (float)0.0f);
                int l = 0;
                for (int i1 = 0; i1 + this.scrollPos < this.drawnChatLines.size() && i1 < i; ++i1) {
                    int j1;
                    ChatLine chatline = this.drawnChatLines.get(i1 + this.scrollPos);
                    if (chatline == null || (j1 = updateCounter - chatline.func_74540_b()) >= 200 && !flag) continue;
                    double d0 = (double)j1 / 200.0;
                    d0 = 1.0 - d0;
                    d0 *= 10.0;
                    d0 = MathHelper.func_151237_a((double)d0, (double)0.0, (double)1.0);
                    d0 *= d0;
                    int l1 = (int)(255.0 * d0);
                    if (flag) {
                        l1 = 255;
                    }
                    l1 = (int)((float)l1 * f);
                    ++l;
                    if (l1 <= 3) continue;
                    int i2 = 0;
                    int j2 = -i1 * 9;
                    NewChat.func_73734_a((int)-2, (int)(j2 - 9), (int)(i2 + k + 4), (int)j2, (int)this.getColorAlpha(this.chatModifier.backColor, this.chatModifier.alphaColor));
                    String s = chatline.func_151461_a().func_150254_d();
                    GlStateManager.func_179147_l();
                    switch ((String)this.chatModifier.animationtext.getValue()) {
                        case "Down Up": {
                            x = 0.0f;
                            y = (float)(j2 - 8) + (18.0f - 18.0f * percent) * f1;
                            break;
                        }
                        case "Left Right": {
                            float xPercent = updateCounter - chatline.func_74540_b();
                            x = xPercent > 8.0f ? 0.0f : 8.0f - (9.0f - 9.0f * (xPercent / 8.0f * 100.0f - 100.0f) / 40.0f) * f1;
                            y = j2 - 8;
                            break;
                        }
                        default: {
                            x = 0.0f;
                            y = j2 - 8;
                        }
                    }
                    try {
                        this.displayText(s, x, y, customText);
                    }
                    catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                        // empty catch block
                    }
                    GlStateManager.func_179118_c();
                    GlStateManager.func_179084_k();
                }
                if (flag) {
                    int k2 = this.mc.field_71466_p.field_78288_b;
                    GlStateManager.func_179109_b((float)-3.0f, (float)0.0f, (float)0.0f);
                    int l2 = j * k2 + j;
                    int i3 = l * k2 + l;
                    int j3 = this.scrollPos * i3 / j;
                    int k1 = i3 * i3 / l2;
                    if (l2 != i3 && !((Boolean)this.chatModifier.hideSlider.getValue()).booleanValue()) {
                        NewChat.func_73734_a((int)(-((Integer)this.chatModifier.sliderSpace.getValue()).intValue() + (Integer)this.chatModifier.sliderWidth.getValue()), (int)(-j3), (int)(-((Integer)this.chatModifier.sliderSpace.getValue()).intValue()), (int)(-j3 - k1), (int)this.getColorAlpha(this.chatModifier.firstColor, this.chatModifier.firstAlpha));
                        NewChat.func_73734_a((int)(-((Integer)this.chatModifier.sliderSpace.getValue()).intValue()), (int)(-j3), (int)(-((Integer)this.chatModifier.sliderSpace.getValue()).intValue() + -((Integer)this.chatModifier.sliderWidth.getValue()).intValue()), (int)(-j3 - k1), (int)this.getColorAlpha(this.chatModifier.secondColor, this.chatModifier.secondAlpha));
                        NewChat.func_73734_a((int)(-((Integer)this.chatModifier.sliderSpace.getValue()).intValue() + -((Integer)this.chatModifier.sliderWidth.getValue()).intValue()), (int)(-j3), (int)(-((Integer)this.chatModifier.sliderSpace.getValue()).intValue() + -((Integer)this.chatModifier.sliderWidth.getValue()).intValue() * 2), (int)(-j3 - k1), (int)this.getColorAlpha(this.chatModifier.thirdColor, this.chatModifier.thirdAlpha));
                    }
                }
                GlStateManager.func_179121_F();
            }
        }
    }

    public void func_146231_a(boolean p_146231_1_) {
        this.drawnChatLines.clear();
        this.chatLines.clear();
        if (p_146231_1_) {
            this.sentMessages.clear();
        }
    }

    public void func_146227_a(ITextComponent chatComponent) {
        this.func_146234_a(chatComponent, 0);
    }

    public void func_146234_a(ITextComponent chatComponent, int chatLineId) {
        percentComplete = 0.0f;
        this.setChatLine(chatComponent, chatLineId, this.mc.field_71456_v.func_73834_c(), false);
        LOGGER.info("[CHAT] {}", (Object)chatComponent.func_150260_c().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    private void setChatLine(ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
        if (chatLineId != 0) {
            this.func_146242_c(chatLineId);
        }
        int i = MathHelper.func_76141_d((float)((float)this.func_146228_f() / this.func_146244_h()));
        List list = GuiUtilRenderComponents.func_178908_a((ITextComponent)chatComponent, (int)i, (FontRenderer)this.mc.field_71466_p, (boolean)false, (boolean)false);
        boolean flag = this.func_146241_e();
        newLines = list.size() - 1;
        for (ITextComponent itextcomponent : list) {
            if (flag && this.scrollPos > 0) {
                this.func_146229_b(1);
            }
            this.drawnChatLines.add(0, new ChatLine(updateCounter, itextcomponent, chatLineId));
        }
        while (this.drawnChatLines.size() > 100) {
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
        }
        if (!displayOnly) {
            this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));
            while (this.chatLines.size() > 100) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    public void func_146245_b() {
        this.drawnChatLines.clear();
        this.func_146240_d();
        for (int i = this.chatLines.size() - 1; i >= 0; --i) {
            ChatLine chatline = this.chatLines.get(i);
            this.setChatLine(chatline.func_151461_a(), chatline.func_74539_c(), chatline.func_74540_b(), true);
        }
    }

    public List<String> func_146238_c() {
        return this.sentMessages;
    }

    public void func_146239_a(String message) {
        if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(message)) {
            this.sentMessages.add(message);
        }
    }

    public void func_146240_d() {
        this.scrollPos = 0;
    }

    public void func_146229_b(int amount) {
        this.scrollPos += amount;
        int i = this.drawnChatLines.size();
        if (this.scrollPos > i - this.func_146232_i()) {
            this.scrollPos = i - this.func_146232_i();
        }
        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
        }
    }

    @Nullable
    public ITextComponent func_146236_a(int mouseX, int mouseY) {
        if (!this.func_146241_e()) {
            return null;
        }
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int i = scaledresolution.func_78325_e();
        float f = this.func_146244_h();
        int j = mouseX / i - 2 - (Integer)this.chatModifier.leftPosition.getValue();
        int k = mouseY / i - 40 - (Integer)this.chatModifier.upPosition.getValue();
        j = MathHelper.func_76141_d((float)((float)j / f));
        k = MathHelper.func_76141_d((float)((float)k / f));
        if (j >= 0 && k >= 0) {
            int l = Math.min(this.func_146232_i(), this.drawnChatLines.size());
            if (j <= MathHelper.func_76141_d((float)((float)this.func_146228_f() / this.func_146244_h())) && k < this.mc.field_71466_p.field_78288_b * l + l) {
                int i1 = k / this.mc.field_71466_p.field_78288_b + this.scrollPos;
                if (i1 >= 0 && i1 < this.drawnChatLines.size()) {
                    ChatLine chatline = this.drawnChatLines.get(i1);
                    int j1 = 0;
                    for (ITextComponent itextcomponent : chatline.func_151461_a()) {
                        if (!(itextcomponent instanceof TextComponentString) || (j1 += this.mc.field_71466_p.func_78256_a(GuiUtilRenderComponents.func_178909_a((String)((TextComponentString)itextcomponent).func_150265_g(), (boolean)false))) <= j) continue;
                        return itextcomponent;
                    }
                }
                return null;
            }
            return null;
        }
        return null;
    }

    public boolean func_146241_e() {
        return this.mc.field_71462_r instanceof GuiChat;
    }

    public void func_146242_c(int id) {
        Iterator<ChatLine> iterator = this.drawnChatLines.iterator();
        while (iterator.hasNext()) {
            ChatLine chatline = iterator.next();
            if (chatline.func_74539_c() != id) continue;
            iterator.remove();
        }
        iterator = this.chatLines.iterator();
        while (iterator.hasNext()) {
            ChatLine chatline1 = iterator.next();
            if (chatline1.func_74539_c() != id) continue;
            iterator.remove();
            break;
        }
    }

    public int func_146228_f() {
        return (Integer)this.chatModifier.maxW.getValue() != -1 ? (Integer)this.chatModifier.maxW.getValue() : NewChat.calculateChatboxWidth(this.mc.field_71474_y.field_96692_F);
    }

    public int func_146246_g() {
        return (Integer)this.chatModifier.maxH.getValue() != -1 ? (Integer)this.chatModifier.maxH.getValue() : NewChat.calculateChatboxHeight(this.func_146241_e() ? this.mc.field_71474_y.field_96694_H : this.mc.field_71474_y.field_96693_G);
    }

    private void displayText(String word, float x, float y, boolean isCustom) {
        StringBuilder outputstring = new StringBuilder();
        ArrayList<ArrayList<String>> toWrite = new ArrayList<ArrayList<String>>();
        String nowColor = "";
        boolean before$ = false;
        if (this.specialWords == null) {
            this.specialWords = SocialManager.getSpecialNamesString().toArray(new String[0]);
        }
        for (char c : word.toCharArray()) {
            if (c == '\u00a7') {
                before$ = true;
                if (outputstring.length() == 0) continue;
                String lowercase = outputstring.toString();
                boolean found = false;
                for (int i = 0; i < this.specialWords.length; ++i) {
                    if (!lowercase.toLowerCase().contains(this.specialWords[i])) continue;
                    found = true;
                    StringBuilder newOutput = new StringBuilder();
                    for (String part : lowercase.split(" ")) {
                        boolean foundSpecial = false;
                        for (int j = i; j < this.specialWords.length; ++j) {
                            if (!part.toLowerCase().contains(this.specialWords[j])) continue;
                            foundSpecial = true;
                            break;
                        }
                        if (foundSpecial) {
                            if (newOutput.length() != 0) {
                                toWrite.add(new ArrayList<String>(Arrays.asList(nowColor, newOutput.toString())));
                            }
                            toWrite.add(new ArrayList<String>(Arrays.asList("\u200especial", part + " ")));
                            newOutput.setLength(0);
                            continue;
                        }
                        newOutput.append(part).append(" ");
                    }
                    if (newOutput.length() == 0) break;
                    toWrite.add(new ArrayList<String>(Arrays.asList(nowColor, newOutput.toString())));
                    break;
                }
                if (!found) {
                    toWrite.add(new ArrayList<String>(Arrays.asList(nowColor, outputstring.toString())));
                }
                outputstring.setLength(0);
                nowColor = "";
                continue;
            }
            if (c == '\u2064') {
                nowColor = Integer.toString(this.getColorAlpha(this.chatModifier.friendColor));
                continue;
            }
            if (c == '\u2065') {
                nowColor = Integer.toString(this.getColorAlpha(this.chatModifier.enemyColor));
                continue;
            }
            if (c == '\u2066') {
                nowColor = Integer.toString(this.getColorAlpha(this.chatModifier.playerColor));
                continue;
            }
            if (c == '\u2067') {
                nowColor = Integer.toString(this.getColorAlpha(this.chatModifier.timeColor));
                continue;
            }
            if (before$) {
                if (c == 'k' || c == 'l' || c == 'm' || c == 'n' || c == 'o') {
                    outputstring.append("\u00a7").append(c);
                } else {
                    nowColor = c == 'r' ? "" : Integer.toString(this.getIntFromat(c));
                }
                before$ = false;
                continue;
            }
            outputstring.append(c);
        }
        int width = 0;
        int rainbowColor = 0;
        int rainbowDesyncSmooth = (Integer)this.chatModifier.rainbowDesyncSmooth.getValue();
        double heightSin = (Double)this.chatModifier.heightSin.getValue();
        int multiplyHeight = (Integer)this.chatModifier.multiplyHeight.getValue();
        double millSin = (Double)this.chatModifier.millSin.getValue();
        block12: for (List list : toWrite) {
            switch ((String)list.get(0)) {
                case "": {
                    if (((Boolean)this.chatModifier.desyncRainbowNormal.getValue()).booleanValue()) {
                        int[] temp = this.writeDesync((String)list.get(1), width, x, y, rainbowColor, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin, isCustom, this.getColorAlpha(this.chatModifier.normalColor), (Boolean)this.chatModifier.stopDesyncNormal.getValue());
                        width = temp[0];
                        rainbowColor = temp[1];
                        continue block12;
                    }
                    width += this.writeCustom((String)list.get(1), width, x, y, this.getColorAlpha(this.chatModifier.normalColor), isCustom);
                    continue block12;
                }
                case "\u200especial": {
                    if (((Boolean)this.chatModifier.desyncRainbowSpecial.getValue()).booleanValue()) {
                        int[] temp = this.writeDesync(((String)list.get(1)).replace("\u2063", ""), width, x, y, rainbowColor, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin, isCustom, this.getColorAlpha(this.chatModifier.specialColor), (Boolean)this.chatModifier.stopDesyncSpecial.getValue());
                        width = temp[0];
                        rainbowColor = temp[1];
                        continue block12;
                    }
                    width += this.writeCustom((String)list.get(1), width, x, y, this.getColorAlpha(this.chatModifier.specialColor), isCustom);
                    continue block12;
                }
            }
            width += this.writeCustom((String)list.get(1), width, x, y, Integer.parseInt((String)list.get(0)), isCustom);
        }
    }

    int getColorAlpha(ColorSetting startColor) {
        return -new GSColor(startColor.getValue(), 255).getRGB();
    }

    int getColorAlpha(ColorSetting startColor, IntegerSetting alpha) {
        return new GSColor(startColor.getValue(), (Integer)alpha.getValue()).getRGB();
    }

    private int getIntFromat(char value) {
        switch (value) {
            case 'b': {
                return this.getColorAlpha(this.chatModifier.aqua);
            }
            case 'd': {
                return this.getColorAlpha(this.chatModifier.purple);
            }
            case '5': {
                return this.getColorAlpha(this.chatModifier.dark_purple);
            }
            case '9': {
                return this.getColorAlpha(this.chatModifier.blue);
            }
            case '7': {
                return this.getColorAlpha(this.chatModifier.gray);
            }
            case '3': {
                return this.getColorAlpha(this.chatModifier.dark_aqua);
            }
            case '1': {
                return this.getColorAlpha(this.chatModifier.dark_blue);
            }
            case 'e': {
                return this.getColorAlpha(this.chatModifier.yellow);
            }
            case 'c': {
                return this.getColorAlpha(this.chatModifier.red);
            }
            case 'a': {
                return this.getColorAlpha(this.chatModifier.green);
            }
            case '8': {
                return this.getColorAlpha(this.chatModifier.dark_gray);
            }
            case '6': {
                return this.getColorAlpha(this.chatModifier.gold);
            }
            case '4': {
                return this.getColorAlpha(this.chatModifier.dark_red);
            }
            case '2': {
                return this.getColorAlpha(this.chatModifier.dark_green);
            }
            case '0': {
                return this.getColorAlpha(this.chatModifier.black);
            }
        }
        return this.getColorAlpha(this.chatModifier.white);
    }

    private int writeCustom(String text, int width, float x, float y, int color, boolean isCustom) {
        Minecraft.func_71410_x().field_71466_p.func_175063_a(text, x + (float)width, y, -color);
        return isCustom ? GameSense.INSTANCE.cFontRenderer.getStringWidth(text) : this.mc.field_71466_p.func_78256_a(text);
    }

    private int[] writeDesync(String text, int width, float x, float y, int rainbowColor, int rainbowDesyncSmooth, double heightSin, int multiplyHeight, double millSin, boolean isCustom, int startColor, boolean stop) {
        boolean skip = false;
        for (String character : text.split("")) {
            GSColor colorOut;
            if (skip) {
                skip = false;
                continue;
            }
            if (character.equals("\u00a7")) {
                skip = true;
                continue;
            }
            switch (((String)this.chatModifier.rainbowType.getValue()).toLowerCase()) {
                case "sin": {
                    colorOut = this.getSinRainbow(rainbowColor, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin, startColor, stop);
                    break;
                }
                case "tan": {
                    colorOut = this.getTanRainbow(rainbowColor, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin, startColor, stop);
                    break;
                }
                case "secant": {
                    colorOut = this.getSecantRainbow(rainbowColor, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin, startColor, stop);
                    break;
                }
                case "cosecant": {
                    colorOut = this.getCosecRainbow(rainbowColor, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin, startColor, stop);
                    break;
                }
                case "cotangent": {
                    colorOut = this.getCoTanRainbow(rainbowColor, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin, startColor, stop);
                    break;
                }
                case "custom": {
                    colorOut = this.getRainbowCustom(rainbowColor);
                    break;
                }
                default: {
                    colorOut = this.getRainbow(rainbowColor, startColor, stop);
                }
            }
            Minecraft.func_71410_x().field_71466_p.func_175063_a(character, x + (float)width, y, new GSColor(colorOut.getRGB()).getRGB());
            width += isCustom ? GameSense.INSTANCE.cFontRenderer.getStringWidth(character) : this.mc.field_71466_p.func_78256_a(character);
            if (((String)this.chatModifier.rainbowType.getValue()).equalsIgnoreCase("custom")) {
                rainbowColor += (Integer)this.chatModifier.rainbowDesyncSmooth.getValue() * (Integer)this.chatModifier.cutomDesync.getValue();
                continue;
            }
            ++rainbowColor;
        }
        return new int[]{width, rainbowColor};
    }

    private GSColor getRainbowCustom(int incr) {
        GSColor color = ColorSetting.getRainbowColor(this.count + (double)incr);
        return new GSColor(color.getRed(), color.getBlue(), color.getGreen(), 255);
    }

    private GSColor getRainbow(int incr, int start, boolean stop) {
        GSColor color = ColorSetting.getRainbowColor(incr, (Integer)this.chatModifier.rainbowDesyncSmooth.getValue(), start, stop);
        return new GSColor(color.getRed(), color.getBlue(), color.getGreen(), 255);
    }

    private GSColor getSinRainbow(int incr, int rainbowDesyncSmooth, double heightSin, int multiplyHeight, double millSin, int start, boolean stop) {
        GSColor color = ColorSetting.getRainbowSin(incr, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin, start, stop);
        return new GSColor(color.getRed(), color.getBlue(), color.getGreen(), 255);
    }

    private GSColor getTanRainbow(int incr, int rainbowDesyncSmooth, double heightSin, int multiplyHeight, double millSin, int start, boolean stop) {
        GSColor color = ColorSetting.getRainbowTan(incr, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin, start, stop);
        return new GSColor(color.getRed(), color.getBlue(), color.getGreen(), 255);
    }

    private GSColor getCosecRainbow(int incr, int rainbowDesyncSmooth, double heightSin, int multiplyHeight, double millSin, int start, boolean stop) {
        GSColor color = ColorSetting.getRainbowCosec(incr, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin, start, stop);
        return new GSColor(color.getRed(), color.getBlue(), color.getGreen(), 255);
    }

    private GSColor getSecantRainbow(int incr, int rainbowDesyncSmooth, double heightSin, int multiplyHeight, double millSin, int start, boolean stop) {
        GSColor color = ColorSetting.getRainbowSec(incr, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin, start, stop);
        return new GSColor(color.getRed(), color.getBlue(), color.getGreen(), 255);
    }

    private GSColor getCoTanRainbow(int incr, int rainbowDesyncSmooth, double heightSin, int multiplyHeight, double millSin, int start, boolean stop) {
        GSColor color = ColorSetting.getRainbowCoTan(incr, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin, start, stop);
        return new GSColor(color.getRed(), color.getBlue(), color.getGreen(), 255);
    }

    public float func_146244_h() {
        return this.mc.field_71474_y.field_96691_E;
    }

    public static int calculateChatboxWidth(float scale) {
        return MathHelper.func_76141_d((float)(scale * 280.0f + 40.0f));
    }

    public static int calculateChatboxHeight(float scale) {
        return MathHelper.func_76141_d((float)(scale * 160.0f + 20.0f));
    }

    public int func_146232_i() {
        return this.func_146246_g() / 9;
    }

    static {
        prevMillis = -1L;
    }
}

