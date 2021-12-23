/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(name="HoleESP", category=Category.Render)
public class HoleESP
extends Module {
    public IntegerSetting range = this.registerInteger("Range", 5, 1, 20);
    ModeSetting customHoles = this.registerMode("Show", Arrays.asList("Single", "Double", "Custom"), "Single");
    ModeSetting type = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
    BooleanSetting bOutLineSection = this.registerBoolean("OutLine Section Bedrock", false, () -> ((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both"));
    ModeSetting bNVerticesOutlineBot = this.registerMode("bN^ Vertices Outline Bot", Arrays.asList("1", "2", "4"), "4", () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bOutLineSection.getValue() != false);
    ModeSetting bdirection2OutLineBot = this.registerMode("bDirection Outline Bot", Arrays.asList("X", "Z"), "X", () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bOutLineSection.getValue() != false && ((String)this.bNVerticesOutlineBot.getValue()).equals("2"));
    ColorSetting bfirstVerticeOutlineBot = this.registerColor("b1 Vert Out Bot", new GSColor(255, 16, 19, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bOutLineSection.getValue() != false, true);
    ColorSetting bsecondVerticeOutlineBot = this.registerColor("b2 Vert Out Bot", new GSColor(0, 0, 255, 255), () -> !(!((String)this.type.getValue()).equals("Outline") && !((String)this.type.getValue()).equals("Both") || (Boolean)this.bOutLineSection.getValue() == false || !((String)this.bNVerticesOutlineBot.getValue()).equals("2") && !((String)this.bNVerticesOutlineBot.getValue()).equals("4")), true);
    ColorSetting bthirdVerticeOutlineBot = this.registerColor("b3 Vert Out Bot", new GSColor(0, 255, 128, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bOutLineSection.getValue() != false && ((String)this.bNVerticesOutlineBot.getValue()).equals("4"), true);
    ColorSetting bfourVerticeOutlineBot = this.registerColor("b4 Vert Out Bot", new GSColor(255, 255, 2, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bOutLineSection.getValue() != false && ((String)this.bNVerticesOutlineBot.getValue()).equals("4"), true);
    ModeSetting bNVerticesOutlineTop = this.registerMode("bN^ Vertices Outline Top", Arrays.asList("1", "2", "4"), "4", () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bOutLineSection.getValue() != false);
    ModeSetting bdirection2OutLineTop = this.registerMode("bDirection Outline Top", Arrays.asList("X", "Z"), "X", () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bOutLineSection.getValue() != false && ((String)this.bNVerticesOutlineTop.getValue()).equals("2"));
    ColorSetting bfirstVerticeOutlineTop = this.registerColor("b1 Vert Out Top", new GSColor(255, 16, 19, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bOutLineSection.getValue() != false, true);
    ColorSetting bsecondVerticeOutlineTop = this.registerColor("b2 Vert Out Top", new GSColor(0, 0, 255, 255), () -> !(!((String)this.type.getValue()).equals("Outline") && !((String)this.type.getValue()).equals("Both") || (Boolean)this.bOutLineSection.getValue() == false || !((String)this.bNVerticesOutlineTop.getValue()).equals("2") && !((String)this.bNVerticesOutlineTop.getValue()).equals("4")), true);
    ColorSetting bthirdVerticeOutlineTop = this.registerColor("b3 Vert Out Top", new GSColor(0, 255, 128, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bOutLineSection.getValue() != false && ((String)this.bNVerticesOutlineTop.getValue()).equals("4"), true);
    ColorSetting bfourVerticeOutlineTop = this.registerColor("b4 Vert Out Top", new GSColor(255, 255, 2, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bOutLineSection.getValue() != false && ((String)this.bNVerticesOutlineTop.getValue()).equals("4"), true);
    BooleanSetting bFillSection = this.registerBoolean("Fill Section Bedrock", false, () -> ((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both"));
    ModeSetting bNVerticesFillBot = this.registerMode("bN^ Vertices Fill Bot", Arrays.asList("1", "2", "4"), "4", () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bFillSection.getValue() != false);
    ModeSetting bdirection2FillBot = this.registerMode("bDirection Fill Bot", Arrays.asList("X", "Z"), "X", () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bFillSection.getValue() != false && ((String)this.bNVerticesFillBot.getValue()).equals("2"));
    ColorSetting bfirstVerticeFillBot = this.registerColor("b1 Vert Fill Bot", new GSColor(255, 16, 19, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bFillSection.getValue() != false, true);
    ColorSetting bsecondVerticeFillBot = this.registerColor("b2 Vert Fill Bot", new GSColor(0, 0, 255, 255), () -> !(!((String)this.type.getValue()).equals("Fill") && !((String)this.type.getValue()).equals("Both") || (Boolean)this.bFillSection.getValue() == false || !((String)this.bNVerticesFillBot.getValue()).equals("2") && !((String)this.bNVerticesFillBot.getValue()).equals("4")), true);
    ColorSetting bthirdVerticeFillBot = this.registerColor("b3 Vert Fill Bot", new GSColor(0, 255, 128, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bFillSection.getValue() != false && ((String)this.bNVerticesFillBot.getValue()).equals("4"), true);
    ColorSetting bfourVerticeFillBot = this.registerColor("b4 Vert Fill Bot", new GSColor(255, 255, 2, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bFillSection.getValue() != false && ((String)this.bNVerticesFillBot.getValue()).equals("4"), true);
    ModeSetting bNVerticesFillTop = this.registerMode("N^ Vertices Fill Top", Arrays.asList("1", "2", "4"), "4", () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bFillSection.getValue() != false);
    ModeSetting bdirection2FillTop = this.registerMode("bDirection Fill Top", Arrays.asList("X", "Z"), "X", () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bFillSection.getValue() != false && ((String)this.bNVerticesFillTop.getValue()).equals("2"));
    ColorSetting bfirstVerticeFillTop = this.registerColor("b1 Vert Fill Top", new GSColor(255, 16, 19, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bFillSection.getValue() != false, true);
    ColorSetting bsecondVerticeFillTop = this.registerColor("b2 Vert Fill Top", new GSColor(0, 0, 255, 255), () -> !(!((String)this.type.getValue()).equals("Fill") && !((String)this.type.getValue()).equals("Both") || (Boolean)this.bFillSection.getValue() == false || !((String)this.bNVerticesFillTop.getValue()).equals("2") && !((String)this.bNVerticesFillTop.getValue()).equals("4")), true);
    ColorSetting bthirdVerticeFillTop = this.registerColor("b3 Vert Fill Top", new GSColor(0, 255, 128, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bFillSection.getValue() != false && ((String)this.bNVerticesFillTop.getValue()).equals("4"), true);
    ColorSetting bfourVerticeFillTop = this.registerColor("b4 Vert Fill Top", new GSColor(255, 255, 2, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.bFillSection.getValue() != false && ((String)this.bNVerticesFillTop.getValue()).equals("4"), true);
    BooleanSetting oOutLineSection = this.registerBoolean("OutLine Section Obsidian", false, () -> ((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both"));
    ModeSetting oNVerticesOutlineBot = this.registerMode("oN^ Vertices Outline Bot", Arrays.asList("1", "2", "4"), "4", () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oOutLineSection.getValue() != false);
    ModeSetting odirection2OutLineBot = this.registerMode("oDirection Outline Bot", Arrays.asList("X", "Z"), "X", () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oOutLineSection.getValue() != false && ((String)this.oNVerticesOutlineBot.getValue()).equals("2"));
    ColorSetting ofirstVerticeOutlineBot = this.registerColor("o1 Vert Out Bot", new GSColor(255, 16, 19, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oOutLineSection.getValue() != false, true);
    ColorSetting osecondVerticeOutlineBot = this.registerColor("o2 Vert Out Bot", new GSColor(0, 0, 255, 255), () -> !(!((String)this.type.getValue()).equals("Outline") && !((String)this.type.getValue()).equals("Both") || (Boolean)this.oOutLineSection.getValue() == false || !((String)this.oNVerticesOutlineBot.getValue()).equals("2") && !((String)this.oNVerticesOutlineBot.getValue()).equals("4")), true);
    ColorSetting othirdVerticeOutlineBot = this.registerColor("o3 Vert Out Bot", new GSColor(0, 255, 128, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oOutLineSection.getValue() != false && ((String)this.oNVerticesOutlineBot.getValue()).equals("4"), true);
    ColorSetting ofourVerticeOutlineBot = this.registerColor("o4 Vert Out Bot", new GSColor(255, 255, 2, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oOutLineSection.getValue() != false && ((String)this.oNVerticesOutlineBot.getValue()).equals("4"), true);
    ModeSetting oNVerticesOutlineTop = this.registerMode("oN^ Vertices Outline Top", Arrays.asList("1", "2", "4"), "4", () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oOutLineSection.getValue() != false);
    ModeSetting odirection2OutLineTop = this.registerMode("oDirection Outline Top", Arrays.asList("X", "Z"), "X", () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oOutLineSection.getValue() != false && ((String)this.oNVerticesOutlineTop.getValue()).equals("2"));
    ColorSetting ofirstVerticeOutlineTop = this.registerColor("o1 Vert Out Top", new GSColor(255, 16, 19, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oOutLineSection.getValue() != false, true);
    ColorSetting osecondVerticeOutlineTop = this.registerColor("o2 Vert Out Top", new GSColor(0, 0, 255, 255), () -> !(!((String)this.type.getValue()).equals("Outline") && !((String)this.type.getValue()).equals("Both") || (Boolean)this.oOutLineSection.getValue() == false || !((String)this.oNVerticesOutlineTop.getValue()).equals("2") && !((String)this.oNVerticesOutlineTop.getValue()).equals("4")), true);
    ColorSetting othirdVerticeOutlineTop = this.registerColor("o3 Vert Out Top", new GSColor(0, 255, 128, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oOutLineSection.getValue() != false && ((String)this.oNVerticesOutlineTop.getValue()).equals("4"), true);
    ColorSetting ofourVerticeOutlineTop = this.registerColor("o4 Vert Out Top", new GSColor(255, 255, 2, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oOutLineSection.getValue() != false && ((String)this.oNVerticesOutlineTop.getValue()).equals("4"), true);
    BooleanSetting oFillSection = this.registerBoolean("Fill Section Obsidian", false, () -> ((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both"));
    ModeSetting oNVerticesFillBot = this.registerMode("oN^ Vertices Fill Bot", Arrays.asList("1", "2", "4"), "4", () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oFillSection.getValue() != false);
    ModeSetting odirection2FillBot = this.registerMode("oDirection Fill Bot", Arrays.asList("X", "Z"), "X", () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oFillSection.getValue() != false && ((String)this.oNVerticesFillBot.getValue()).equals("2"));
    ColorSetting ofirstVerticeFillBot = this.registerColor("o1 Vert Fill Bot", new GSColor(255, 16, 19, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oFillSection.getValue() != false, true);
    ColorSetting osecondVerticeFillBot = this.registerColor("o2 Vert Fill Bot", new GSColor(0, 0, 255, 255), () -> !(!((String)this.type.getValue()).equals("Fill") && !((String)this.type.getValue()).equals("Both") || (Boolean)this.oFillSection.getValue() == false || !((String)this.oNVerticesFillBot.getValue()).equals("2") && !((String)this.oNVerticesFillBot.getValue()).equals("4")), true);
    ColorSetting othirdVerticeFillBot = this.registerColor("o3 Vert Fill Bot", new GSColor(0, 255, 128, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oFillSection.getValue() != false && ((String)this.oNVerticesFillBot.getValue()).equals("4"), true);
    ColorSetting ofourVerticeFillBot = this.registerColor("o4 Vert Fill Bot", new GSColor(255, 255, 2, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oFillSection.getValue() != false && ((String)this.oNVerticesFillBot.getValue()).equals("4"), true);
    ModeSetting oNVerticesFillTop = this.registerMode("oN^ Vertices Fill Top", Arrays.asList("1", "2", "4"), "4", () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oFillSection.getValue() != false);
    ModeSetting odirection2FillTop = this.registerMode("oDirection Fill Top", Arrays.asList("X", "Z"), "X", () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oFillSection.getValue() != false && ((String)this.oNVerticesFillTop.getValue()).equals("2"));
    ColorSetting ofirstVerticeFillTop = this.registerColor("o1 Vert Fill Top", new GSColor(255, 16, 19, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oFillSection.getValue() != false, true);
    ColorSetting osecondVerticeFillTop = this.registerColor("o2 Vert Fill Top", new GSColor(0, 0, 255, 255), () -> !(!((String)this.type.getValue()).equals("Fill") && !((String)this.type.getValue()).equals("Both") || (Boolean)this.oFillSection.getValue() == false || !((String)this.oNVerticesFillTop.getValue()).equals("2") && !((String)this.oNVerticesFillTop.getValue()).equals("4")), true);
    ColorSetting othirdVerticeFillTop = this.registerColor("o3 Vert Fill Top", new GSColor(0, 255, 128, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oFillSection.getValue() != false && ((String)this.oNVerticesFillTop.getValue()).equals("4"), true);
    ColorSetting ofourVerticeFillTop = this.registerColor("o4 Vert Fill Top", new GSColor(255, 255, 2, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.oFillSection.getValue() != false && ((String)this.oNVerticesFillTop.getValue()).equals("4"), true);
    BooleanSetting OutLineSection = this.registerBoolean("OutLine Section Custom", false, () -> ((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both"));
    ModeSetting NVerticesOutlineBot = this.registerMode("N^ Vertices Outline Bot", Arrays.asList("1", "2", "4"), "4", () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false);
    ModeSetting direction2OutLineBot = this.registerMode("Direction Outline Bot", Arrays.asList("X", "Z"), "X", () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && ((String)this.NVerticesOutlineBot.getValue()).equals("2"));
    ColorSetting firstVerticeOutlineBot = this.registerColor("1 Vert Out Bot", new GSColor(255, 16, 19, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false, true);
    ColorSetting secondVerticeOutlineBot = this.registerColor("2 Vert Out Bot", new GSColor(0, 0, 255, 255), () -> !(!((String)this.type.getValue()).equals("Outline") && !((String)this.type.getValue()).equals("Both") || (Boolean)this.OutLineSection.getValue() == false || !((String)this.NVerticesOutlineBot.getValue()).equals("2") && !((String)this.NVerticesOutlineBot.getValue()).equals("4")), true);
    ColorSetting thirdVerticeOutlineBot = this.registerColor("3 Vert Out Bot", new GSColor(0, 255, 128, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && ((String)this.NVerticesOutlineBot.getValue()).equals("4"), true);
    ColorSetting fourVerticeOutlineBot = this.registerColor("4 Vert Out Bot", new GSColor(255, 255, 2, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && ((String)this.NVerticesOutlineBot.getValue()).equals("4"), true);
    ModeSetting NVerticesOutlineTop = this.registerMode("N^ Vertices Outline Top", Arrays.asList("1", "2", "4"), "4", () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false);
    ModeSetting direction2OutLineTop = this.registerMode("Direction Outline Top", Arrays.asList("X", "Z"), "X", () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && ((String)this.NVerticesOutlineTop.getValue()).equals("2"));
    ColorSetting firstVerticeOutlineTop = this.registerColor("1 Vert Out Top", new GSColor(255, 16, 19, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false, true);
    ColorSetting secondVerticeOutlineTop = this.registerColor("2 Vert Out Top", new GSColor(0, 0, 255, 255), () -> !(!((String)this.type.getValue()).equals("Outline") && !((String)this.type.getValue()).equals("Both") || (Boolean)this.OutLineSection.getValue() == false || !((String)this.NVerticesOutlineTop.getValue()).equals("2") && !((String)this.NVerticesOutlineTop.getValue()).equals("4")), true);
    ColorSetting thirdVerticeOutlineTop = this.registerColor("3 Vert Out Top", new GSColor(0, 255, 128, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && ((String)this.NVerticesOutlineTop.getValue()).equals("4"), true);
    ColorSetting fourVerticeOutlineTop = this.registerColor("4 Vert Out Top", new GSColor(255, 255, 2, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && ((String)this.NVerticesOutlineTop.getValue()).equals("4"), true);
    BooleanSetting FillSection = this.registerBoolean("Fill Section Custom", false, () -> ((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both"));
    ModeSetting NVerticesFillBot = this.registerMode("N^ Vertices Fill Bot", Arrays.asList("1", "2", "4"), "4", () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false);
    ModeSetting direction2FillBot = this.registerMode("Direction Fill Bot", Arrays.asList("X", "Z"), "X", () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && ((String)this.NVerticesFillBot.getValue()).equals("2"));
    ColorSetting firstVerticeFillBot = this.registerColor("1 Vert Fill Bot", new GSColor(255, 16, 19, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false, true);
    ColorSetting secondVerticeFillBot = this.registerColor("2 Vert Fill Bot", new GSColor(0, 0, 255, 255), () -> !(!((String)this.type.getValue()).equals("Fill") && !((String)this.type.getValue()).equals("Both") || (Boolean)this.FillSection.getValue() == false || !((String)this.NVerticesFillBot.getValue()).equals("2") && !((String)this.NVerticesFillBot.getValue()).equals("4")), true);
    ColorSetting thirdVerticeFillBot = this.registerColor("3 Vert Fill Bot", new GSColor(0, 255, 128, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && ((String)this.NVerticesFillBot.getValue()).equals("4"), true);
    ColorSetting fourVerticeFillBot = this.registerColor("4 Vert Fill Bot", new GSColor(255, 255, 2, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && ((String)this.NVerticesFillBot.getValue()).equals("4"), true);
    ModeSetting NVerticesFillTop = this.registerMode("N^ Vertices Fill Top", Arrays.asList("1", "2", "4"), "4", () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false);
    ModeSetting direction2FillTop = this.registerMode("Direction Fill Top", Arrays.asList("X", "Z"), "X", () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && ((String)this.NVerticesFillTop.getValue()).equals("2"));
    ColorSetting firstVerticeFillTop = this.registerColor("1 Vert Fill Top", new GSColor(255, 16, 19, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false, true);
    ColorSetting secondVerticeFillTop = this.registerColor("2 Vert Fill Top", new GSColor(0, 0, 255, 255), () -> !(!((String)this.type.getValue()).equals("Fill") && !((String)this.type.getValue()).equals("Both") || (Boolean)this.FillSection.getValue() == false || !((String)this.NVerticesFillTop.getValue()).equals("2") && !((String)this.NVerticesFillTop.getValue()).equals("4")), true);
    ColorSetting thirdVerticeFillTop = this.registerColor("3 Vert Fill Top", new GSColor(0, 255, 128, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && ((String)this.NVerticesFillTop.getValue()).equals("4"), true);
    ColorSetting fourVerticeFillTop = this.registerColor("4 Vert Fill Top", new GSColor(255, 255, 2, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && ((String)this.NVerticesFillTop.getValue()).equals("4"), true);
    BooleanSetting fillRaytrace = this.registerBoolean("Fill raytrace", false);
    ModeSetting mode = this.registerMode("Mode", Arrays.asList("Air", "Ground", "Flat", "Slab", "Double"), "Air");
    BooleanSetting hideOwn = this.registerBoolean("Hide Own", false);
    BooleanSetting flatOwn = this.registerBoolean("Flat Own", false);
    DoubleSetting slabHeightOutline = this.registerDouble("Slab Height Outline", 0.5, 0.1, 1.5);
    DoubleSetting slabHeightFill = this.registerDouble("Slab Height Fill", 0.5, 0.1, 1.5);
    BooleanSetting animatedHeight = this.registerBoolean("Animated Heith", false, () -> ((String)this.mode.getValue()).equals("Slab"));
    BooleanSetting animatedAlpha = this.registerBoolean("Animated Alpha", false);
    BooleanSetting desyncColor = this.registerBoolean("Desync Color", false);
    IntegerSetting desyncColorValue = this.registerInteger("Desync Color Value", 100, 0, 3000);
    IntegerSetting desyncSpeed = this.registerInteger("Desync Speed", 10, 1, 500);
    IntegerSetting width = this.registerInteger("Width", 1, 1, 10);
    IntegerSetting ufoAlpha = this.registerInteger("UFOAlpha", 255, 0, 255);
    private ConcurrentHashMap<AxisAlignedBB, Integer> holes;
    long count = 0L;

    @Override
    public void onUpdate() {
        this.count += (long)((Integer)this.desyncSpeed.getValue()).intValue();
        if (HoleESP.mc.field_71439_g == null || HoleESP.mc.field_71441_e == null) {
            return;
        }
        if (this.holes == null) {
            this.holes = new ConcurrentHashMap();
        } else {
            this.holes.clear();
        }
        int range = (int)Math.ceil(((Integer)this.range.getValue()).intValue());
        HashSet possibleHoles = Sets.newHashSet();
        List<BlockPos> blockPosList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), range, range, false, true, 0);
        for (BlockPos pos2 : blockPosList) {
            if (!HoleESP.mc.field_71441_e.func_180495_p(pos2).func_177230_c().equals(Blocks.field_150350_a) || HoleESP.mc.field_71441_e.func_180495_p(pos2.func_177982_a(0, -1, 0)).func_177230_c().equals(Blocks.field_150350_a) || !HoleESP.mc.field_71441_e.func_180495_p(pos2.func_177982_a(0, 1, 0)).func_177230_c().equals(Blocks.field_150350_a) || !HoleESP.mc.field_71441_e.func_180495_p(pos2.func_177982_a(0, 2, 0)).func_177230_c().equals(Blocks.field_150350_a)) continue;
            possibleHoles.add(pos2);
        }
        possibleHoles.forEach(pos -> {
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            if (holeType != HoleUtil.HoleType.NONE) {
                String mode;
                HoleUtil.BlockSafety holeSafety = holeInfo.getSafety();
                AxisAlignedBB centreBlocks = holeInfo.getCentre();
                if (centreBlocks == null) {
                    return;
                }
                int typeHole = holeSafety == HoleUtil.BlockSafety.UNBREAKABLE ? 0 : 1;
                if (holeType == HoleUtil.HoleType.CUSTOM) {
                    typeHole = 2;
                }
                if ((mode = (String)this.customHoles.getValue()).equalsIgnoreCase("Custom") && (holeType == HoleUtil.HoleType.CUSTOM || holeType == HoleUtil.HoleType.DOUBLE)) {
                    this.holes.put(centreBlocks, typeHole);
                } else if (mode.equalsIgnoreCase("Double") && holeType == HoleUtil.HoleType.DOUBLE) {
                    this.holes.put(centreBlocks, typeHole);
                } else if (holeType == HoleUtil.HoleType.SINGLE) {
                    this.holes.put(centreBlocks, typeHole);
                }
            }
        });
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (HoleESP.mc.field_71439_g == null || HoleESP.mc.field_71441_e == null || this.holes == null || this.holes.isEmpty()) {
            return;
        }
        this.holes.forEach(this::renderHoles);
    }

    boolean isOne(Integer typeHole, boolean outline) {
        return outline ? (typeHole == 0 ? ((String)this.bNVerticesOutlineBot.getValue()).equals("1") && ((String)this.bNVerticesOutlineTop.getValue()).equals("1") : (typeHole == 1 ? ((String)this.oNVerticesOutlineBot.getValue()).equals("1") && ((String)this.oNVerticesOutlineTop.getValue()).equals("1") : ((String)this.NVerticesOutlineBot.getValue()).equals("1") && ((String)this.NVerticesOutlineTop.getValue()).equals("1"))) : (typeHole == 0 ? ((String)this.bNVerticesFillBot.getValue()).equals("1") && ((String)this.bNVerticesFillTop.getValue()).equals("1") : (typeHole == 1 ? ((String)this.oNVerticesFillBot.getValue()).equals("1") && ((String)this.oNVerticesFillTop.getValue()).equals("1") : ((String)this.NVerticesFillBot.getValue()).equals("1") && ((String)this.NVerticesFillTop.getValue()).equals("1")));
    }

    private void renderHoles(AxisAlignedBB hole, Integer typeHole) {
        switch ((String)this.type.getValue()) {
            case "Outline": {
                if (this.isOne(typeHole, true)) {
                    this.renderOutline(hole, typeHole == 0 ? this.bfirstVerticeOutlineBot.getColor() : (typeHole == 1 ? this.ofirstVerticeOutlineBot.getColor() : this.firstVerticeOutlineBot.getColor()));
                    break;
                }
                this.renderCustomOutline(hole, typeHole);
                break;
            }
            case "Fill": {
                if (this.isOne(typeHole, false)) {
                    this.renderFill(hole, typeHole == 0 ? this.bfirstVerticeFillBot.getColor() : (typeHole == 1 ? this.ofirstVerticeFillBot.getColor() : this.firstVerticeFillBot.getColor()));
                    break;
                }
                this.renderFillCustom(hole, typeHole);
                break;
            }
            case "Both": {
                if (!((Boolean)this.fillRaytrace.getValue()).booleanValue() || HoleESP.mc.field_71441_e.func_72933_a(hole.func_189972_c(), new Vec3d(HoleESP.mc.field_71439_g.field_70165_t, HoleESP.mc.field_71439_g.field_70163_u + (double)HoleESP.mc.field_71439_g.func_70047_e() + 1.0, HoleESP.mc.field_71439_g.field_70161_v)) == null) {
                    if (this.isOne(typeHole, false)) {
                        this.renderFill(hole, typeHole == 0 ? this.bfirstVerticeFillBot.getColor() : (typeHole == 1 ? this.ofirstVerticeFillBot.getColor() : this.firstVerticeFillBot.getColor()));
                    } else {
                        this.renderFillCustom(hole, typeHole);
                    }
                }
                if (this.isOne(typeHole, true)) {
                    this.renderOutline(hole, typeHole == 0 ? this.bfirstVerticeOutlineBot.getColor() : (typeHole == 1 ? this.ofirstVerticeOutlineBot.getColor() : this.firstVerticeOutlineBot.getColor()));
                    break;
                }
                this.renderCustomOutline(hole, typeHole);
            }
        }
    }

    private void renderFill(AxisAlignedBB hole, GSColor color) {
        GSColor fillColor = new GSColor(color, 50);
        int ufoAlpha = (Integer)this.ufoAlpha.getValue() * 50 / 255;
        if (((Boolean)this.hideOwn.getValue()).booleanValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
            return;
        }
        switch ((String)this.mode.getValue()) {
            case "Air": {
                if (((Boolean)this.flatOwn.getValue()).booleanValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
                    RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 1);
                    break;
                }
                RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 63);
                break;
            }
            case "Ground": {
                RenderUtil.drawBox(hole.func_72317_d(0.0, -1.0, 0.0), true, 1.0, new GSColor(fillColor, ufoAlpha), fillColor.getAlpha(), 63);
                break;
            }
            case "Flat": {
                RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 1);
                break;
            }
            case "Slab": {
                if (((Boolean)this.flatOwn.getValue()).booleanValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
                    RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 1);
                    break;
                }
                RenderUtil.drawBox(hole, false, (Double)this.slabHeightFill.getValue() * ((Boolean)this.animatedHeight.getValue() != false ? 1.0 - HoleESP.mc.field_71439_g.func_70092_e(hole.field_72340_a + 0.5, hole.field_72338_b + 0.5, hole.field_72339_c + 0.5) / Math.pow(((Integer)this.range.getValue()).intValue(), 2.0) : 1.0), fillColor, ufoAlpha, 63);
                break;
            }
            case "Double": {
                if (((Boolean)this.flatOwn.getValue()).booleanValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
                    RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 1);
                    break;
                }
                RenderUtil.drawBox(hole.func_186666_e(hole.field_72337_e + 1.0), true, 2.0, fillColor, ufoAlpha, 63);
            }
        }
    }

    private void renderOutline(AxisAlignedBB hole, GSColor color) {
        GSColor outlineColor = new GSColor(color, 255);
        if (((Boolean)this.hideOwn.getValue()).booleanValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
            return;
        }
        switch ((String)this.mode.getValue()) {
            case "Air": {
                if (((Boolean)this.flatOwn.getValue()).booleanValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, (int)((Integer)this.width.getValue()), outlineColor, (int)((Integer)this.ufoAlpha.getValue()), 1);
                    break;
                }
                RenderUtil.drawBoundingBox(hole, (double)((Integer)this.width.getValue()).intValue(), outlineColor, (Integer)this.ufoAlpha.getValue());
                break;
            }
            case "Ground": {
                RenderUtil.drawBoundingBox(hole.func_72317_d(0.0, -1.0, 0.0), (double)((Integer)this.width.getValue()).intValue(), new GSColor(outlineColor, (Integer)this.ufoAlpha.getValue()), outlineColor.getAlpha());
                break;
            }
            case "Flat": {
                RenderUtil.drawBoundingBoxWithSides(hole, (int)((Integer)this.width.getValue()), outlineColor, (int)((Integer)this.ufoAlpha.getValue()), 1);
                break;
            }
            case "Slab": {
                if (((Boolean)this.flatOwn.getValue()).booleanValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, (int)((Integer)this.width.getValue()), outlineColor, (int)((Integer)this.ufoAlpha.getValue()), 1);
                    break;
                }
                RenderUtil.drawBoundingBox(hole.func_186666_e(hole.field_72338_b + (Double)this.slabHeightOutline.getValue() * ((Boolean)this.animatedHeight.getValue() != false ? 1.0 - HoleESP.mc.field_71439_g.func_70092_e(hole.field_72340_a + 0.5, hole.field_72338_b + 0.5, hole.field_72339_c + 0.5) / Math.pow(((Integer)this.range.getValue()).intValue(), 2.0) : 1.0)), (double)((Integer)this.width.getValue()).intValue(), outlineColor, (Integer)this.ufoAlpha.getValue());
                break;
            }
            case "Double": {
                if (((Boolean)this.flatOwn.getValue()).booleanValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, (int)((Integer)this.width.getValue()), outlineColor, (int)((Integer)this.ufoAlpha.getValue()), 1);
                    break;
                }
                RenderUtil.drawBoundingBox(hole.func_186666_e(hole.field_72337_e + 1.0), (double)((Integer)this.width.getValue()).intValue(), outlineColor, (Integer)this.ufoAlpha.getValue());
            }
        }
    }

    private void renderCustomOutline(AxisAlignedBB hole, int typeHole) {
        if (((Boolean)this.hideOwn.getValue()).booleanValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
            return;
        }
        ArrayList<GSColor> colors = new ArrayList<GSColor>();
        block0 : switch (typeHole) {
            case 0: {
                switch ((String)this.bNVerticesOutlineBot.getValue()) {
                    case "1": {
                        colors.add(this.bfirstVerticeOutlineBot.getValue());
                        colors.add(this.bfirstVerticeOutlineBot.getValue());
                        colors.add(this.bfirstVerticeOutlineBot.getValue());
                        colors.add(this.bfirstVerticeOutlineBot.getValue());
                        break;
                    }
                    case "2": {
                        if (((String)this.bdirection2OutLineBot.getValue()).equals("X")) {
                            if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.bfirstVerticeOutlineBot.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.bfirstVerticeOutlineBot.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.bfirstVerticeOutlineBot.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.bfirstVerticeOutlineBot.getColor().getAlpha()));
                                break;
                            }
                            colors.add(this.bfirstVerticeOutlineBot.getValue());
                            colors.add(this.bsecondVerticeOutlineBot.getValue());
                            colors.add(this.bfirstVerticeOutlineBot.getValue());
                            colors.add(this.bsecondVerticeOutlineBot.getValue());
                            break;
                        }
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.bfirstVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.bfirstVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.bfirstVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.bfirstVerticeOutlineBot.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.bfirstVerticeOutlineBot.getValue());
                        colors.add(this.bfirstVerticeOutlineBot.getValue());
                        colors.add(this.bsecondVerticeOutlineBot.getValue());
                        colors.add(this.bsecondVerticeOutlineBot.getValue());
                        break;
                    }
                    case "4": {
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.bfirstVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.bfirstVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 2)), this.bfirstVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 3)), this.bfirstVerticeOutlineBot.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.bfirstVerticeOutlineBot.getValue());
                        colors.add(this.bsecondVerticeOutlineBot.getValue());
                        colors.add(this.bthirdVerticeOutlineBot.getValue());
                        colors.add(this.bfourVerticeOutlineBot.getValue());
                    }
                }
                switch ((String)this.bNVerticesOutlineTop.getValue()) {
                    case "1": {
                        colors.add(this.bfirstVerticeOutlineTop.getValue());
                        colors.add(this.bfirstVerticeOutlineTop.getValue());
                        colors.add(this.bfirstVerticeOutlineTop.getValue());
                        colors.add(this.bfirstVerticeOutlineTop.getValue());
                        break;
                    }
                    case "2": {
                        if (((String)this.bdirection2OutLineTop.getValue()).equals("X")) {
                            if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.bfirstVerticeOutlineTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.bfirstVerticeOutlineTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.bfirstVerticeOutlineTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.bfirstVerticeOutlineTop.getColor().getAlpha()));
                                break;
                            }
                            colors.add(this.bfirstVerticeOutlineTop.getValue());
                            colors.add(this.bsecondVerticeOutlineTop.getValue());
                            colors.add(this.bfirstVerticeOutlineTop.getValue());
                            colors.add(this.bsecondVerticeOutlineTop.getValue());
                            break;
                        }
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.bfirstVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.bfirstVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.bfirstVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.bfirstVerticeOutlineTop.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.bfirstVerticeOutlineTop.getValue());
                        colors.add(this.bfirstVerticeOutlineTop.getValue());
                        colors.add(this.bsecondVerticeOutlineTop.getValue());
                        colors.add(this.bsecondVerticeOutlineTop.getValue());
                        break;
                    }
                    case "4": {
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.bfirstVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.bfirstVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 2)), this.bfirstVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 3)), this.bfirstVerticeOutlineTop.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.bfirstVerticeOutlineTop.getValue());
                        colors.add(this.bsecondVerticeOutlineTop.getValue());
                        colors.add(this.bthirdVerticeOutlineTop.getValue());
                        colors.add(this.bfourVerticeOutlineTop.getValue());
                    }
                }
                break;
            }
            case 1: {
                switch ((String)this.oNVerticesOutlineBot.getValue()) {
                    case "1": {
                        colors.add(this.ofirstVerticeOutlineBot.getValue());
                        colors.add(this.ofirstVerticeOutlineBot.getValue());
                        colors.add(this.ofirstVerticeOutlineBot.getValue());
                        colors.add(this.ofirstVerticeOutlineBot.getValue());
                        break;
                    }
                    case "2": {
                        if (((String)this.odirection2OutLineBot.getValue()).equals("X")) {
                            if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeOutlineBot.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeOutlineBot.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeOutlineBot.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeOutlineBot.getColor().getAlpha()));
                                break;
                            }
                            colors.add(this.ofirstVerticeOutlineBot.getValue());
                            colors.add(this.osecondVerticeOutlineBot.getValue());
                            colors.add(this.ofirstVerticeOutlineBot.getValue());
                            colors.add(this.osecondVerticeOutlineBot.getValue());
                            break;
                        }
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeOutlineBot.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.ofirstVerticeOutlineBot.getValue());
                        colors.add(this.ofirstVerticeOutlineBot.getValue());
                        colors.add(this.osecondVerticeOutlineBot.getValue());
                        colors.add(this.osecondVerticeOutlineBot.getValue());
                        break;
                    }
                    case "4": {
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 2)), this.othirdVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 3)), this.ofourVerticeOutlineBot.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.ofirstVerticeOutlineBot.getValue());
                        colors.add(this.osecondVerticeOutlineBot.getValue());
                        colors.add(this.othirdVerticeOutlineBot.getValue());
                        colors.add(this.ofourVerticeOutlineBot.getValue());
                    }
                }
                switch ((String)this.oNVerticesOutlineTop.getValue()) {
                    case "1": {
                        colors.add(this.ofirstVerticeOutlineTop.getValue());
                        colors.add(this.ofirstVerticeOutlineTop.getValue());
                        colors.add(this.ofirstVerticeOutlineTop.getValue());
                        colors.add(this.ofirstVerticeOutlineTop.getValue());
                        break;
                    }
                    case "2": {
                        if (((String)this.odirection2OutLineTop.getValue()).equals("X")) {
                            if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeOutlineTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeOutlineTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.osecondVerticeOutlineTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeOutlineTop.getColor().getAlpha()));
                                break;
                            }
                            colors.add(this.ofirstVerticeOutlineTop.getValue());
                            colors.add(this.osecondVerticeOutlineTop.getValue());
                            colors.add(this.ofirstVerticeOutlineTop.getValue());
                            colors.add(this.osecondVerticeOutlineTop.getValue());
                            break;
                        }
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeOutlineTop.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.ofirstVerticeOutlineTop.getValue());
                        colors.add(this.ofirstVerticeOutlineTop.getValue());
                        colors.add(this.osecondVerticeOutlineTop.getValue());
                        colors.add(this.osecondVerticeOutlineTop.getValue());
                        break;
                    }
                    case "4": {
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 2)), this.othirdVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 3)), this.ofourVerticeOutlineTop.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.ofirstVerticeOutlineTop.getValue());
                        colors.add(this.osecondVerticeOutlineTop.getValue());
                        colors.add(this.othirdVerticeOutlineTop.getValue());
                        colors.add(this.ofourVerticeOutlineTop.getValue());
                    }
                }
                break;
            }
            case 2: {
                switch ((String)this.NVerticesOutlineBot.getValue()) {
                    case "1": {
                        colors.add(this.firstVerticeOutlineBot.getValue());
                        colors.add(this.firstVerticeOutlineBot.getValue());
                        colors.add(this.firstVerticeOutlineBot.getValue());
                        colors.add(this.firstVerticeOutlineBot.getValue());
                        break;
                    }
                    case "2": {
                        if (((String)this.direction2OutLineBot.getValue()).equals("X")) {
                            if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeOutlineBot.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeOutlineBot.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeOutlineBot.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeOutlineBot.getColor().getAlpha()));
                                break;
                            }
                            colors.add(this.firstVerticeOutlineBot.getValue());
                            colors.add(this.secondVerticeOutlineBot.getValue());
                            colors.add(this.firstVerticeOutlineBot.getValue());
                            colors.add(this.secondVerticeOutlineBot.getValue());
                            break;
                        }
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeOutlineBot.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.firstVerticeOutlineBot.getValue());
                        colors.add(this.firstVerticeOutlineBot.getValue());
                        colors.add(this.secondVerticeOutlineBot.getValue());
                        colors.add(this.secondVerticeOutlineBot.getValue());
                        break;
                    }
                    case "4": {
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 2)), this.thirdVerticeOutlineBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 3)), this.fourVerticeOutlineBot.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.firstVerticeOutlineBot.getValue());
                        colors.add(this.secondVerticeOutlineBot.getValue());
                        colors.add(this.thirdVerticeOutlineBot.getValue());
                        colors.add(this.fourVerticeOutlineBot.getValue());
                    }
                }
                switch ((String)this.NVerticesOutlineTop.getValue()) {
                    case "1": {
                        colors.add(this.firstVerticeOutlineTop.getValue());
                        colors.add(this.firstVerticeOutlineTop.getValue());
                        colors.add(this.firstVerticeOutlineTop.getValue());
                        colors.add(this.firstVerticeOutlineTop.getValue());
                        break block0;
                    }
                    case "2": {
                        if (((String)this.direction2OutLineTop.getValue()).equals("X")) {
                            if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeOutlineTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeOutlineTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeOutlineTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeOutlineTop.getColor().getAlpha()));
                                break block0;
                            }
                            colors.add(this.firstVerticeOutlineTop.getValue());
                            colors.add(this.secondVerticeOutlineTop.getValue());
                            colors.add(this.firstVerticeOutlineTop.getValue());
                            colors.add(this.secondVerticeOutlineTop.getValue());
                            break block0;
                        }
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeOutlineTop.getColor().getAlpha()));
                            break block0;
                        }
                        colors.add(this.firstVerticeOutlineTop.getValue());
                        colors.add(this.firstVerticeOutlineTop.getValue());
                        colors.add(this.secondVerticeOutlineTop.getValue());
                        colors.add(this.secondVerticeOutlineTop.getValue());
                        break block0;
                    }
                    case "4": {
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 2)), this.thirdVerticeOutlineTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 3)), this.fourVerticeOutlineTop.getColor().getAlpha()));
                            break block0;
                        }
                        colors.add(this.firstVerticeOutlineTop.getValue());
                        colors.add(this.secondVerticeOutlineTop.getValue());
                        colors.add(this.thirdVerticeOutlineTop.getValue());
                        colors.add(this.fourVerticeOutlineTop.getValue());
                    }
                }
            }
        }
        if (((Boolean)this.animatedAlpha.getValue()).booleanValue()) {
            ArrayList<GSColor> newColors = new ArrayList<GSColor>();
            for (GSColor col : colors) {
                int alpha = (int)((double)col.getAlpha() * (1.0 - HoleESP.mc.field_71439_g.func_70092_e(hole.field_72340_a + 0.5, hole.field_72338_b + 0.5, hole.field_72339_c + 0.5) / (double)((Integer)this.range.getValue() * (Integer)this.range.getValue())));
                if (alpha < 0) {
                    alpha = 0;
                } else if (alpha > 255) {
                    alpha = 255;
                }
                newColors.add(new GSColor(col, alpha));
            }
            colors = newColors;
        }
        switch ((String)this.mode.getValue()) {
            case "Air": {
                if (!((Boolean)this.flatOwn.getValue()).booleanValue() || !hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) break;
                hole = hole.func_186666_e(hole.field_72337_e - 1.0);
                break;
            }
            case "Ground": {
                hole = hole.func_72317_d(0.0, -1.0, 0.0);
                break;
            }
            case "Double": {
                if (((Boolean)this.flatOwn.getValue()).booleanValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
                    hole = hole.func_186666_e(hole.field_72337_e - 1.0);
                    break;
                }
                hole = hole.func_186666_e(hole.field_72337_e + 1.0);
                break;
            }
            case "Slab": {
                if (((Boolean)this.flatOwn.getValue()).booleanValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
                    hole = hole.func_186666_e(hole.field_72337_e - 1.0);
                    break;
                }
                hole = hole.func_186666_e(hole.field_72338_b + (Double)this.slabHeightOutline.getValue() * ((Boolean)this.animatedHeight.getValue() != false ? 1.0 - HoleESP.mc.field_71439_g.func_70092_e(hole.field_72340_a + 0.5, hole.field_72338_b + 0.5, hole.field_72339_c + 0.5) / Math.pow(((Integer)this.range.getValue()).intValue(), 2.0) : 1.0));
                break;
            }
            case "Flat": {
                hole = hole.func_186666_e(hole.field_72337_e - 1.0);
            }
        }
        RenderUtil.drawBoundingBox(hole, (double)((Integer)this.width.getValue()).intValue(), colors.toArray(new GSColor[7]));
    }

    void renderFillCustom(AxisAlignedBB hole, int typeHole) {
        int mask = 63;
        if (((Boolean)this.hideOwn.getValue()).booleanValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
            return;
        }
        ArrayList<GSColor> colors = new ArrayList<GSColor>();
        block0 : switch (typeHole) {
            case 0: {
                switch ((String)this.bNVerticesFillBot.getValue()) {
                    case "1": {
                        colors.add(this.bfirstVerticeFillBot.getValue());
                        colors.add(this.bfirstVerticeFillBot.getValue());
                        colors.add(this.bfirstVerticeFillBot.getValue());
                        colors.add(this.bfirstVerticeFillBot.getValue());
                        break;
                    }
                    case "2": {
                        if (((String)this.bdirection2FillBot.getValue()).equals("X")) {
                            colors.add(this.bfirstVerticeFillBot.getValue());
                            colors.add(this.bsecondVerticeFillBot.getValue());
                            colors.add(this.bfirstVerticeFillBot.getValue());
                            colors.add(this.bsecondVerticeFillBot.getValue());
                            break;
                        }
                        colors.add(this.bfirstVerticeFillBot.getValue());
                        colors.add(this.bfirstVerticeFillBot.getValue());
                        colors.add(this.bsecondVerticeFillBot.getValue());
                        colors.add(this.bsecondVerticeFillBot.getValue());
                        break;
                    }
                    case "4": {
                        colors.add(this.bfirstVerticeFillBot.getValue());
                        colors.add(this.bsecondVerticeFillBot.getValue());
                        colors.add(this.bthirdVerticeFillBot.getValue());
                        colors.add(this.bfourVerticeFillBot.getValue());
                    }
                }
                switch ((String)this.bNVerticesFillTop.getValue()) {
                    case "1": {
                        colors.add(this.bfirstVerticeFillTop.getValue());
                        colors.add(this.bfirstVerticeFillTop.getValue());
                        colors.add(this.bfirstVerticeFillTop.getValue());
                        colors.add(this.bfirstVerticeFillTop.getValue());
                        break;
                    }
                    case "2": {
                        if (((String)this.bdirection2FillTop.getValue()).equals("X")) {
                            if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.bfirstVerticeFillTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.bsecondVerticeFillTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.bfirstVerticeFillTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.bsecondVerticeFillTop.getColor().getAlpha()));
                                break;
                            }
                            colors.add(this.bfirstVerticeFillTop.getValue());
                            colors.add(this.bsecondVerticeFillTop.getValue());
                            colors.add(this.bfirstVerticeFillTop.getValue());
                            colors.add(this.bsecondVerticeFillTop.getValue());
                            break;
                        }
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.bfirstVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.bfirstVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.bsecondVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.bsecondVerticeFillTop.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.bfirstVerticeFillTop.getValue());
                        colors.add(this.bfirstVerticeFillTop.getValue());
                        colors.add(this.bsecondVerticeFillTop.getValue());
                        colors.add(this.bsecondVerticeFillTop.getValue());
                        break;
                    }
                    case "4": {
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.bfirstVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.bsecondVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 2)), this.bthirdVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 3)), this.bfourVerticeFillTop.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.bfirstVerticeFillTop.getValue());
                        colors.add(this.bsecondVerticeFillTop.getValue());
                        colors.add(this.bthirdVerticeFillTop.getValue());
                        colors.add(this.bfourVerticeFillTop.getValue());
                    }
                }
                break;
            }
            case 1: {
                switch ((String)this.oNVerticesFillBot.getValue()) {
                    case "1": {
                        colors.add(this.ofirstVerticeFillBot.getValue());
                        colors.add(this.ofirstVerticeFillBot.getValue());
                        colors.add(this.ofirstVerticeFillBot.getValue());
                        colors.add(this.ofirstVerticeFillBot.getValue());
                        break;
                    }
                    case "2": {
                        if (((String)this.odirection2FillBot.getValue()).equals("X")) {
                            if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeFillBot.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeFillBot.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeFillBot.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeFillBot.getColor().getAlpha()));
                                break;
                            }
                            colors.add(this.ofirstVerticeFillBot.getValue());
                            colors.add(this.osecondVerticeFillBot.getValue());
                            colors.add(this.ofirstVerticeFillBot.getValue());
                            colors.add(this.osecondVerticeFillBot.getValue());
                            break;
                        }
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeFillBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeFillBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeFillBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeFillBot.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.ofirstVerticeFillBot.getValue());
                        colors.add(this.ofirstVerticeFillBot.getValue());
                        colors.add(this.osecondVerticeFillBot.getValue());
                        colors.add(this.osecondVerticeFillBot.getValue());
                        break;
                    }
                    case "4": {
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeFillBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeFillBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 2)), this.othirdVerticeFillBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 3)), this.ofourVerticeFillBot.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.ofirstVerticeFillBot.getValue());
                        colors.add(this.osecondVerticeFillBot.getValue());
                        colors.add(this.othirdVerticeFillBot.getValue());
                        colors.add(this.ofourVerticeFillBot.getValue());
                    }
                }
                switch ((String)this.oNVerticesFillTop.getValue()) {
                    case "1": {
                        colors.add(this.ofirstVerticeFillTop.getValue());
                        colors.add(this.ofirstVerticeFillTop.getValue());
                        colors.add(this.ofirstVerticeFillTop.getValue());
                        colors.add(this.ofirstVerticeFillTop.getValue());
                        break;
                    }
                    case "2": {
                        if (((String)this.odirection2FillTop.getValue()).equals("X")) {
                            if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeFillTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeFillTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeFillTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeFillTop.getColor().getAlpha()));
                                break;
                            }
                            colors.add(this.ofirstVerticeFillTop.getValue());
                            colors.add(this.osecondVerticeFillTop.getValue());
                            colors.add(this.ofirstVerticeFillTop.getValue());
                            colors.add(this.osecondVerticeFillTop.getValue());
                            break;
                        }
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeFillTop.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.ofirstVerticeFillTop.getValue());
                        colors.add(this.ofirstVerticeFillTop.getValue());
                        colors.add(this.osecondVerticeFillTop.getValue());
                        colors.add(this.osecondVerticeFillTop.getValue());
                        break;
                    }
                    case "4": {
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.ofirstVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.osecondVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 2)), this.othirdVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 3)), this.ofourVerticeFillTop.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.ofirstVerticeFillTop.getValue());
                        colors.add(this.osecondVerticeFillTop.getValue());
                        colors.add(this.othirdVerticeFillTop.getValue());
                        colors.add(this.ofourVerticeFillTop.getValue());
                    }
                }
                break;
            }
            case 2: {
                switch ((String)this.NVerticesFillBot.getValue()) {
                    case "1": {
                        colors.add(this.firstVerticeFillBot.getValue());
                        colors.add(this.firstVerticeFillBot.getValue());
                        colors.add(this.firstVerticeFillBot.getValue());
                        colors.add(this.firstVerticeFillBot.getValue());
                        break;
                    }
                    case "2": {
                        if (((String)this.direction2FillBot.getValue()).equals("X")) {
                            if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeFillBot.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeFillBot.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeFillBot.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeFillBot.getColor().getAlpha()));
                                break;
                            }
                            colors.add(this.firstVerticeFillBot.getValue());
                            colors.add(this.secondVerticeFillBot.getValue());
                            colors.add(this.firstVerticeFillBot.getValue());
                            colors.add(this.secondVerticeFillBot.getValue());
                            break;
                        }
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeFillBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeFillBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeFillBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeFillBot.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.firstVerticeFillBot.getValue());
                        colors.add(this.firstVerticeFillBot.getValue());
                        colors.add(this.secondVerticeFillBot.getValue());
                        colors.add(this.secondVerticeFillBot.getValue());
                        break;
                    }
                    case "4": {
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeFillBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeFillBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 2)), this.thirdVerticeFillBot.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 3)), this.fourVerticeFillBot.getColor().getAlpha()));
                            break;
                        }
                        colors.add(this.firstVerticeFillBot.getValue());
                        colors.add(this.secondVerticeFillBot.getValue());
                        colors.add(this.thirdVerticeFillBot.getValue());
                        colors.add(this.fourVerticeFillBot.getValue());
                    }
                }
                switch ((String)this.NVerticesFillTop.getValue()) {
                    case "1": {
                        colors.add(this.firstVerticeFillTop.getValue());
                        colors.add(this.firstVerticeFillTop.getValue());
                        colors.add(this.firstVerticeFillTop.getValue());
                        colors.add(this.firstVerticeFillTop.getValue());
                        break block0;
                    }
                    case "2": {
                        if (((String)this.direction2FillTop.getValue()).equals("X")) {
                            if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeFillTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeFillTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeFillTop.getColor().getAlpha()));
                                colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeFillTop.getColor().getAlpha()));
                                break block0;
                            }
                            colors.add(this.firstVerticeFillTop.getValue());
                            colors.add(this.secondVerticeFillTop.getValue());
                            colors.add(this.firstVerticeFillTop.getValue());
                            colors.add(this.secondVerticeFillTop.getValue());
                            break block0;
                        }
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeFillTop.getColor().getAlpha()));
                            break block0;
                        }
                        colors.add(this.firstVerticeFillTop.getValue());
                        colors.add(this.firstVerticeFillTop.getValue());
                        colors.add(this.secondVerticeFillTop.getValue());
                        colors.add(this.secondVerticeFillTop.getValue());
                        break block0;
                    }
                    case "4": {
                        if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 0)), this.firstVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 1)), this.secondVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 2)), this.thirdVerticeFillTop.getColor().getAlpha()));
                            colors.add(new GSColor(ColorSetting.getRainbowColor(this.count + (long)((Integer)this.desyncColorValue.getValue() * 3)), this.fourVerticeFillTop.getColor().getAlpha()));
                            break block0;
                        }
                        colors.add(this.firstVerticeFillTop.getValue());
                        colors.add(this.secondVerticeFillTop.getValue());
                        colors.add(this.thirdVerticeFillTop.getValue());
                        colors.add(this.fourVerticeFillTop.getValue());
                    }
                }
            }
        }
        switch ((String)this.mode.getValue()) {
            case "Air": {
                if (!((Boolean)this.flatOwn.getValue()).booleanValue() || !hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) break;
                mask = 1;
                break;
            }
            case "Ground": {
                hole = hole.func_72317_d(0.0, -1.0, 0.0);
                break;
            }
            case "Double": {
                if (((Boolean)this.flatOwn.getValue()).booleanValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
                    mask = 1;
                    break;
                }
                hole = hole.func_186666_e(hole.field_72337_e + 1.0);
                break;
            }
            case "Slab": {
                if (((Boolean)this.flatOwn.getValue()).booleanValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
                    mask = 1;
                    break;
                }
                hole = hole.func_186666_e(hole.field_72338_b + (Double)this.slabHeightFill.getValue() * ((Boolean)this.animatedHeight.getValue() != false ? 1.0 - HoleESP.mc.field_71439_g.func_70092_e(hole.field_72340_a + 0.5, hole.field_72338_b + 0.5, hole.field_72339_c + 0.5) / Math.pow(((Integer)this.range.getValue()).intValue(), 2.0) : 1.0));
                break;
            }
            case "Flat": {
                mask = 1;
            }
        }
        if (((Boolean)this.animatedAlpha.getValue()).booleanValue()) {
            ArrayList<GSColor> newColors = new ArrayList<GSColor>();
            for (GSColor col : colors) {
                int alpha = (int)((double)col.getAlpha() * (1.0 - HoleESP.mc.field_71439_g.func_70092_e(hole.field_72340_a + 0.5, hole.field_72338_b + 0.5, hole.field_72339_c + 0.5) / (double)((Integer)this.range.getValue() * (Integer)this.range.getValue())));
                if (alpha < 0) {
                    alpha = 0;
                } else if (alpha > 255) {
                    alpha = 255;
                }
                newColors.add(new GSColor(col, alpha));
            }
            colors = newColors;
        }
        RenderUtil.drawBoxProva2(hole, true, 1.0, colors.toArray(new GSColor[7]), mask, true);
    }
}

