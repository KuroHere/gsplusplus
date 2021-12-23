/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.shaders.impl.fill.AquaShader;
import com.gamesense.api.util.render.shaders.impl.fill.CircleShader;
import com.gamesense.api.util.render.shaders.impl.fill.FillShader;
import com.gamesense.api.util.render.shaders.impl.fill.FlowShader;
import com.gamesense.api.util.render.shaders.impl.fill.GradientShader;
import com.gamesense.api.util.render.shaders.impl.fill.PhobosShader;
import com.gamesense.api.util.render.shaders.impl.fill.RainbowCubeShader;
import com.gamesense.api.util.render.shaders.impl.fill.SmokeShader;
import com.gamesense.api.util.render.shaders.impl.outline.AquaOutlineShader;
import com.gamesense.api.util.render.shaders.impl.outline.AstralOutlineShader;
import com.gamesense.api.util.render.shaders.impl.outline.CircleOutlineShader;
import com.gamesense.api.util.render.shaders.impl.outline.GlowShader;
import com.gamesense.api.util.render.shaders.impl.outline.GradientOutlineShader;
import com.gamesense.api.util.render.shaders.impl.outline.RainbowCubeOutlineShader;
import com.gamesense.api.util.render.shaders.impl.outline.SmokeOutlineShader;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import java.awt.Color;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

@Module.Declaration(name="Shaders", category=Category.Render)
public class Shaders
extends Module {
    ModeSetting glowESP = this.registerMode("Glow ESP", Arrays.asList("None", "Color", "Astral", "RainbowCube", "Gradient", "Aqua", "Circle", "Smoke"), "None");
    ColorSetting colorESP = this.registerColor("Color ESP", new GSColor(255, 255, 255, 255));
    DoubleSetting radius = this.registerDouble("Radius ESP", 1.0, 0.0, 5.0);
    DoubleSetting quality = this.registerDouble("Quality ESP", 1.0, 0.0, 20.0);
    BooleanSetting GradientAlpha = this.registerBoolean("Gradient Alpha", false);
    IntegerSetting alphaValue = this.registerInteger("Alpha Outline", 255, 0, 255, () -> (Boolean)this.GradientAlpha.getValue() == false);
    DoubleSetting PIOutline = this.registerDouble("PI Outline", 3.141592653, 0.0, 10.0, () -> ((String)this.glowESP.getValue()).equals("Circle"));
    DoubleSetting radOutline = this.registerDouble("RAD Outline", 0.75, 0.0, 5.0, () -> ((String)this.glowESP.getValue()).equals("Circle"));
    DoubleSetting moreGradientOutline = this.registerDouble("More Gradient", 1.0, 0.0, 10.0, () -> ((String)this.glowESP.getValue()).equals("Gradient"));
    DoubleSetting creepyOutline = this.registerDouble("Creepy", 1.0, 0.0, 20.0, () -> ((String)this.glowESP.getValue()).equals("Gradient"));
    IntegerSetting WaveLenghtOutline = this.registerInteger("Wave Lenght", 555, 0, 2000, () -> ((String)this.glowESP.getValue()).equals("RainbowCube"));
    IntegerSetting RSTARTOutline = this.registerInteger("RSTART", 0, 0, 1000, () -> ((String)this.glowESP.getValue()).equals("RainbowCube"));
    IntegerSetting GSTARTOutline = this.registerInteger("GSTART", 0, 0, 1000, () -> ((String)this.glowESP.getValue()).equals("RainbowCube"));
    IntegerSetting BSTARTOutline = this.registerInteger("BSTART", 0, 0, 1000, () -> ((String)this.glowESP.getValue()).equals("RainbowCube"));
    ColorSetting colorImgOutline = this.registerColor("Color Img", new GSColor(0, 0, 0, 255), () -> ((String)this.glowESP.getValue()).equals("Aqua") || ((String)this.glowESP.getValue()).equals("Smoke") || ((String)this.glowESP.getValue()).equals("RainbowCube"), true);
    ColorSetting secondColorImgOutline = this.registerColor("Second Color Img", new GSColor(255, 255, 255, 255), () -> ((String)this.glowESP.getValue()).equals("Smoke"));
    ColorSetting thirdColorImgOutline = this.registerColor("Third Color Img", new GSColor(255, 255, 255, 255), () -> ((String)this.glowESP.getValue()).equals("Smoke"));
    IntegerSetting NUM_OCTAVESOutline = this.registerInteger("NUM_OCTAVES", 5, 1, 30, () -> ((String)this.glowESP.getValue()).equals("Smoke"));
    IntegerSetting MaxIterOutline = this.registerInteger("Max Iter", 5, 0, 30, () -> ((String)this.glowESP.getValue()).equals("Aqua"));
    DoubleSetting tauOutline = this.registerDouble("TAU", 6.28318530718, 0.0, 20.0, () -> ((String)this.glowESP.getValue()).equals("Aqua"));
    IntegerSetting redOutline = this.registerInteger("Red", 0, 0, 100, () -> ((String)this.glowESP.getValue()).equals("Astral"));
    DoubleSetting greenOutline = this.registerDouble("Green", 0.0, 0.0, 5.0, () -> ((String)this.glowESP.getValue()).equals("Astral"));
    DoubleSetting blueOutline = this.registerDouble("Blue", 0.0, 0.0, 5.0, () -> ((String)this.glowESP.getValue()).equals("Astral"));
    DoubleSetting alphaOutline = this.registerDouble("Alpha", 1.0, 0.0, 1.0, () -> ((String)this.glowESP.getValue()).equals("Astral") || ((String)this.glowESP.getValue()).equals("Gradient"));
    IntegerSetting iterationsOutline = this.registerInteger("Iteration", 4, 3, 20, () -> ((String)this.glowESP.getValue()).equals("Astral"));
    DoubleSetting formuparam2Outline = this.registerDouble("formuparam2", 0.89, 0.0, 1.5, () -> ((String)this.glowESP.getValue()).equals("Astral"));
    DoubleSetting zoomOutline = this.registerDouble("Zoom", 3.9, 0.0, 20.0, () -> ((String)this.glowESP.getValue()).equals("Astral"));
    IntegerSetting volumStepsOutline = this.registerInteger("Volum Steps", 10, 0, 10, () -> ((String)this.glowESP.getValue()).equals("Astral"));
    DoubleSetting stepSizeOutline = this.registerDouble("Step Size", 0.19, 0.0, 0.7, () -> ((String)this.glowESP.getValue()).equals("Astral"));
    DoubleSetting titleOutline = this.registerDouble("Tile", 0.45, 0.0, 1.3, () -> ((String)this.glowESP.getValue()).equals("Astral"));
    DoubleSetting distfadingOutline = this.registerDouble("distfading", 0.56, 0.0, 1.0, () -> ((String)this.glowESP.getValue()).equals("Astral"));
    DoubleSetting saturationOutline = this.registerDouble("saturation", 0.4, 0.0, 3.0, () -> ((String)this.glowESP.getValue()).equals("Astral"));
    BooleanSetting fadeOutline = this.registerBoolean("Fade Fill", false, () -> ((String)this.glowESP.getValue()).equals("Astral"));
    ModeSetting fillShader = this.registerMode("Fill Shader", Arrays.asList("Astral", "Aqua", "Smoke", "RainbowCube", "Gradient", "Fill", "Circle", "Phobos", "None"), "None");
    DoubleSetting moreGradientFill = this.registerDouble("More Gradient", 1.0, 0.0, 10.0, () -> ((String)this.fillShader.getValue()).equals("Gradient"));
    DoubleSetting creepyFill = this.registerDouble("Creepy", 1.0, 0.0, 20.0, () -> ((String)this.fillShader.getValue()).equals("Gradient"));
    IntegerSetting WaveLenghtFIll = this.registerInteger("Wave Lenght", 555, 0, 2000, () -> ((String)this.fillShader.getValue()).equals("RainbowCube"));
    IntegerSetting RSTARTFill = this.registerInteger("RSTART", 0, 0, 1000, () -> ((String)this.fillShader.getValue()).equals("RainbowCube"));
    IntegerSetting GSTARTFill = this.registerInteger("GSTART", 0, 0, 1000, () -> ((String)this.fillShader.getValue()).equals("RainbowCube"));
    IntegerSetting BSTARTFIll = this.registerInteger("BSTART", 0, 0, 1000, () -> ((String)this.fillShader.getValue()).equals("RainbowCube"));
    ColorSetting colorImgFill = this.registerColor("Color Img", new GSColor(0, 0, 0, 255), () -> ((String)this.fillShader.getValue()).equals("Aqua") || ((String)this.fillShader.getValue()).equals("Smoke") || ((String)this.fillShader.getValue()).equals("RainbowCube") || ((String)this.fillShader.getValue()).equals("Fill") || ((String)this.fillShader.getValue()).equals("Circle") || ((String)this.fillShader.getValue()).equals("Future"), true);
    ColorSetting secondColorImgFill = this.registerColor("Second Color Img", new GSColor(255, 255, 255, 255), () -> ((String)this.fillShader.getValue()).equals("Smoke"));
    ColorSetting thirdColorImgFIll = this.registerColor("Third Color Img", new GSColor(255, 255, 255, 255), () -> ((String)this.fillShader.getValue()).equals("Smoke"));
    IntegerSetting NUM_OCTAVESFill = this.registerInteger("NUM_OCTAVES", 5, 1, 30, () -> ((String)this.fillShader.getValue()).equals("Smoke"));
    IntegerSetting MaxIterFill = this.registerInteger("Max Iter", 5, 0, 30, () -> ((String)this.fillShader.getValue()).equals("Aqua"));
    DoubleSetting tauFill = this.registerDouble("TAU", 6.28318530718, 0.0, 20.0, () -> ((String)this.fillShader.getValue()).equals("Aqua"));
    IntegerSetting redFill = this.registerInteger("Red", 0, 0, 100, () -> ((String)this.fillShader.getValue()).equals("Astral"));
    DoubleSetting greenFill = this.registerDouble("Green", 0.0, 0.0, 5.0, () -> ((String)this.fillShader.getValue()).equals("Astral"));
    DoubleSetting blueFill = this.registerDouble("Blue", 0.0, 0.0, 5.0, () -> ((String)this.fillShader.getValue()).equals("Astral"));
    DoubleSetting alphaFill = this.registerDouble("Alpha", 1.0, 0.0, 1.0, () -> ((String)this.fillShader.getValue()).equals("Astral") || ((String)this.fillShader.getValue()).equals("Gradient"));
    IntegerSetting iterationsFill = this.registerInteger("Iteration", 4, 3, 20, () -> ((String)this.fillShader.getValue()).equals("Astral"));
    DoubleSetting formuparam2Fill = this.registerDouble("formuparam2", 0.89, 0.0, 1.5, () -> ((String)this.fillShader.getValue()).equals("Astral"));
    DoubleSetting zoomFill = this.registerDouble("Zoom", 3.9, 0.0, 20.0, () -> ((String)this.fillShader.getValue()).equals("Astral"));
    IntegerSetting volumStepsFill = this.registerInteger("Volum Steps", 10, 0, 10, () -> ((String)this.fillShader.getValue()).equals("Astral"));
    DoubleSetting stepSizeFill = this.registerDouble("Step Size", 0.19, 0.0, 0.7, () -> ((String)this.fillShader.getValue()).equals("Astral"));
    DoubleSetting titleFill = this.registerDouble("Tile", 0.45, 0.0, 1.3, () -> ((String)this.fillShader.getValue()).equals("Astral"));
    DoubleSetting distfadingFill = this.registerDouble("distfading", 0.56, 0.0, 1.0, () -> ((String)this.fillShader.getValue()).equals("Astral"));
    DoubleSetting saturationFill = this.registerDouble("saturation", 0.4, 0.0, 3.0, () -> ((String)this.fillShader.getValue()).equals("Astral"));
    BooleanSetting fadeFill = this.registerBoolean("Fade Fill", false, () -> ((String)this.fillShader.getValue()).equals("Astral"));
    DoubleSetting PI = this.registerDouble("PI Fill", 3.141592653, 0.0, 10.0, () -> ((String)this.fillShader.getValue()).equals("Circle"));
    DoubleSetting rad = this.registerDouble("RAD Fill", 0.75, 0.0, 5.0, () -> ((String)this.fillShader.getValue()).equals("Circle"));
    BooleanSetting itemsFill = this.registerBoolean("Items Fill", false);
    BooleanSetting mobsFill = this.registerBoolean("Mobs Fill", false);
    BooleanSetting playersFill = this.registerBoolean("Players Fill", false);
    BooleanSetting crystalsFill = this.registerBoolean("Crystals Fill", false);
    BooleanSetting xpFill = this.registerBoolean("XP Fill", false);
    BooleanSetting bottleFill = this.registerBoolean("Bottle Fill", false);
    BooleanSetting boatFill = this.registerBoolean("Boat Fill", false);
    BooleanSetting minecartFill = this.registerBoolean("MinecartTnt Fill", false);
    BooleanSetting enderPerleFill = this.registerBoolean("EnderPerle Fill", false);
    BooleanSetting arrowFill = this.registerBoolean("Arrow Fill", false);
    BooleanSetting itemsOutline = this.registerBoolean("Items Outline", false);
    BooleanSetting mobsOutline = this.registerBoolean("Mobs Outline", false);
    BooleanSetting playersOutline = this.registerBoolean("Players Outline", false);
    BooleanSetting crystalsOutline = this.registerBoolean("Crystals Outline", false);
    BooleanSetting xpOutline = this.registerBoolean("XP Outline", false);
    BooleanSetting bottleOutline = this.registerBoolean("Bottle Outline", false);
    BooleanSetting boatOutline = this.registerBoolean("Boat Outline", false);
    BooleanSetting minecartTntOutline = this.registerBoolean("MinecartTnt Outline", false);
    BooleanSetting enderPerleOutline = this.registerBoolean("EnderPerle Outline", false);
    BooleanSetting arrowOutline = this.registerBoolean("Arrow Outline", false);
    BooleanSetting rangeCheck = this.registerBoolean("Range Check", true);
    DoubleSetting minRange = this.registerDouble("Min range", 1.0, 0.0, 5.0, () -> (Boolean)this.rangeCheck.getValue());
    DoubleSetting maxRange = this.registerDouble("Max Range", 20.0, 10.0, 100.0, () -> (Boolean)this.rangeCheck.getValue());
    IntegerSetting maxEntities = this.registerInteger("Max Entities", 100, 10, 500);
    DoubleSetting speedFill = this.registerDouble("Speed Fill", 0.1, 0.001, 0.1);
    DoubleSetting speedOutline = this.registerDouble("Speed Outline", 0.1, 0.001, 0.1);
    DoubleSetting duplicateFill = this.registerDouble("Duplicate Fill", 1.0, 0.0, 5.0);
    DoubleSetting duplicateOutline = this.registerDouble("Duplicate Outline", 1.0, 0.0, 20.0);
    public boolean renderTags = true;
    public boolean renderCape = true;
    @EventHandler
    private final Listener<RenderGameOverlayEvent.Pre> renderGameOverlayEventListener = new Listener<RenderGameOverlayEvent.Pre>(event -> {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            if (Shaders.mc.field_71441_e == null || Shaders.mc.field_71439_g == null) {
                return;
            }
            GlStateManager.func_179094_E();
            this.renderTags = false;
            this.renderCape = false;
            switch ((String)this.fillShader.getValue()) {
                case "Astral": {
                    FlowShader.INSTANCE.startDraw(event.getPartialTicks());
                    this.renderPlayersFill(event.getPartialTicks());
                    FlowShader.INSTANCE.stopDraw(Color.WHITE, 1.0f, 1.0f, ((Double)this.duplicateFill.getValue()).floatValue(), ((Integer)this.redFill.getValue()).floatValue(), ((Double)this.greenFill.getValue()).floatValue(), ((Double)this.blueFill.getValue()).floatValue(), ((Double)this.alphaFill.getValue()).floatValue(), (Integer)this.iterationsFill.getValue(), ((Double)this.formuparam2Fill.getValue()).floatValue(), ((Double)this.zoomFill.getValue()).floatValue(), ((Integer)this.volumStepsFill.getValue()).intValue(), ((Double)this.stepSizeFill.getValue()).floatValue(), ((Double)this.titleFill.getValue()).floatValue(), ((Double)this.distfadingFill.getValue()).floatValue(), ((Double)this.saturationFill.getValue()).floatValue(), 0.0f, (Boolean)this.fadeFill.getValue() != false ? 1 : 0);
                    FlowShader.INSTANCE.update((Double)this.speedFill.getValue());
                    break;
                }
                case "Aqua": {
                    AquaShader.INSTANCE.startDraw(event.getPartialTicks());
                    this.renderPlayersFill(event.getPartialTicks());
                    AquaShader.INSTANCE.stopDraw(this.colorImgFill.getColor(), 1.0f, 1.0f, ((Double)this.duplicateFill.getValue()).floatValue(), (Integer)this.MaxIterFill.getValue(), (Double)this.tauFill.getValue());
                    AquaShader.INSTANCE.update((Double)this.speedFill.getValue());
                    break;
                }
                case "Smoke": {
                    SmokeShader.INSTANCE.startDraw(event.getPartialTicks());
                    this.renderPlayersFill(event.getPartialTicks());
                    SmokeShader.INSTANCE.stopDraw(Color.WHITE, 1.0f, 1.0f, ((Double)this.duplicateFill.getValue()).floatValue(), this.colorImgFill.getColor(), this.secondColorImgFill.getColor(), this.thirdColorImgFIll.getColor(), (Integer)this.NUM_OCTAVESFill.getValue());
                    SmokeShader.INSTANCE.update((Double)this.speedFill.getValue());
                    break;
                }
                case "RainbowCube": {
                    RainbowCubeShader.INSTANCE.startDraw(event.getPartialTicks());
                    this.renderPlayersFill(event.getPartialTicks());
                    RainbowCubeShader.INSTANCE.stopDraw(Color.WHITE, 1.0f, 1.0f, ((Double)this.duplicateFill.getValue()).floatValue(), this.colorImgFill.getColor(), (Integer)this.WaveLenghtFIll.getValue(), (Integer)this.RSTARTFill.getValue(), (Integer)this.GSTARTFill.getValue(), (Integer)this.BSTARTFIll.getValue());
                    RainbowCubeShader.INSTANCE.update((Double)this.speedFill.getValue());
                    break;
                }
                case "Gradient": {
                    GradientShader.INSTANCE.startDraw(event.getPartialTicks());
                    this.renderPlayersFill(event.getPartialTicks());
                    GradientShader.INSTANCE.stopDraw(this.colorESP.getValue(), 1.0f, 1.0f, ((Double)this.duplicateFill.getValue()).floatValue(), ((Double)this.moreGradientFill.getValue()).floatValue(), ((Double)this.creepyFill.getValue()).floatValue(), ((Double)this.alphaFill.getValue()).floatValue(), (Integer)this.NUM_OCTAVESFill.getValue());
                    GradientShader.INSTANCE.update((Double)this.speedFill.getValue());
                    break;
                }
                case "Fill": {
                    FillShader.INSTANCE.startDraw(event.getPartialTicks());
                    this.renderPlayersFill(event.getPartialTicks());
                    FillShader.INSTANCE.stopDraw(new GSColor(this.colorImgFill.getValue(), this.colorImgFill.getColor().getAlpha()));
                    FillShader.INSTANCE.update((Double)this.speedFill.getValue());
                    break;
                }
                case "Circle": {
                    CircleShader.INSTANCE.startDraw(event.getPartialTicks());
                    this.renderPlayersFill(event.getPartialTicks());
                    CircleShader.INSTANCE.stopDraw(((Double)this.duplicateFill.getValue()).floatValue(), this.colorImgFill.getValue(), (Double)this.PI.getValue(), (Double)this.rad.getValue());
                    CircleShader.INSTANCE.update((Double)this.speedFill.getValue());
                    break;
                }
                case "Phobos": {
                    PhobosShader.INSTANCE.startDraw(event.getPartialTicks());
                    this.renderPlayersFill(event.getPartialTicks());
                    PhobosShader.INSTANCE.stopDraw(this.colorImgFill.getColor(), 1.0f, 1.0f, ((Double)this.duplicateFill.getValue()).floatValue(), (Integer)this.MaxIterFill.getValue(), (Double)this.tauFill.getValue());
                    PhobosShader.INSTANCE.update((Double)this.speedFill.getValue());
                }
            }
            switch ((String)this.glowESP.getValue()) {
                case "Color": {
                    GlowShader.INSTANCE.startDraw(event.getPartialTicks());
                    this.renderPlayersOutline(event.getPartialTicks());
                    GlowShader.INSTANCE.stopDraw(this.colorESP.getValue(), ((Double)this.radius.getValue()).floatValue(), ((Double)this.quality.getValue()).floatValue(), (Boolean)this.GradientAlpha.getValue(), (Integer)this.alphaValue.getValue());
                    break;
                }
                case "RainbowCube": {
                    RainbowCubeOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                    this.renderPlayersOutline(event.getPartialTicks());
                    RainbowCubeOutlineShader.INSTANCE.stopDraw(this.colorESP.getValue(), ((Double)this.radius.getValue()).floatValue(), ((Double)this.quality.getValue()).floatValue(), (Boolean)this.GradientAlpha.getValue(), (Integer)this.alphaValue.getValue(), ((Double)this.duplicateOutline.getValue()).floatValue(), this.colorImgOutline.getColor(), (Integer)this.WaveLenghtOutline.getValue(), (Integer)this.RSTARTOutline.getValue(), (Integer)this.GSTARTOutline.getValue(), (Integer)this.BSTARTOutline.getValue());
                    RainbowCubeOutlineShader.INSTANCE.update((Double)this.speedOutline.getValue());
                    break;
                }
                case "Gradient": {
                    GradientOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                    this.renderPlayersOutline(event.getPartialTicks());
                    GradientOutlineShader.INSTANCE.stopDraw(this.colorESP.getValue(), ((Double)this.radius.getValue()).floatValue(), ((Double)this.quality.getValue()).floatValue(), (Boolean)this.GradientAlpha.getValue(), (Integer)this.alphaValue.getValue(), ((Double)this.duplicateOutline.getValue()).floatValue(), ((Double)this.moreGradientOutline.getValue()).floatValue(), ((Double)this.creepyOutline.getValue()).floatValue(), ((Double)this.alphaOutline.getValue()).floatValue(), (Integer)this.NUM_OCTAVESOutline.getValue());
                    GradientOutlineShader.INSTANCE.update((Double)this.speedOutline.getValue());
                    break;
                }
                case "Astral": {
                    AstralOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                    this.renderPlayersOutline(event.getPartialTicks());
                    AstralOutlineShader.INSTANCE.stopDraw(this.colorESP.getValue(), ((Double)this.radius.getValue()).floatValue(), ((Double)this.quality.getValue()).floatValue(), (Boolean)this.GradientAlpha.getValue(), (Integer)this.alphaValue.getValue(), ((Double)this.duplicateOutline.getValue()).floatValue(), ((Integer)this.redOutline.getValue()).floatValue(), ((Double)this.greenOutline.getValue()).floatValue(), ((Double)this.blueOutline.getValue()).floatValue(), ((Double)this.alphaOutline.getValue()).floatValue(), (Integer)this.iterationsOutline.getValue(), ((Double)this.formuparam2Outline.getValue()).floatValue(), ((Double)this.zoomOutline.getValue()).floatValue(), ((Integer)this.volumStepsOutline.getValue()).intValue(), ((Double)this.stepSizeOutline.getValue()).floatValue(), ((Double)this.titleOutline.getValue()).floatValue(), ((Double)this.distfadingOutline.getValue()).floatValue(), ((Double)this.saturationOutline.getValue()).floatValue(), 0.0f, (Boolean)this.fadeOutline.getValue() != false ? 1 : 0);
                    AstralOutlineShader.INSTANCE.update((Double)this.speedOutline.getValue());
                    break;
                }
                case "Aqua": {
                    AquaOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                    this.renderPlayersOutline(event.getPartialTicks());
                    AquaOutlineShader.INSTANCE.stopDraw(this.colorESP.getValue(), ((Double)this.radius.getValue()).floatValue(), ((Double)this.quality.getValue()).floatValue(), (Boolean)this.GradientAlpha.getValue(), (Integer)this.alphaValue.getValue(), ((Double)this.duplicateOutline.getValue()).floatValue(), (Integer)this.MaxIterOutline.getValue(), (Double)this.tauOutline.getValue());
                    AquaOutlineShader.INSTANCE.update((Double)this.speedOutline.getValue());
                    break;
                }
                case "Circle": {
                    CircleOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                    this.renderPlayersOutline(event.getPartialTicks());
                    CircleOutlineShader.INSTANCE.stopDraw(this.colorESP.getValue(), ((Double)this.radius.getValue()).floatValue(), ((Double)this.quality.getValue()).floatValue(), (Boolean)this.GradientAlpha.getValue(), (Integer)this.alphaValue.getValue(), ((Double)this.duplicateOutline.getValue()).floatValue(), (Double)this.PIOutline.getValue(), (Double)this.radOutline.getValue());
                    CircleOutlineShader.INSTANCE.update((Double)this.speedOutline.getValue());
                    break;
                }
                case "Smoke": {
                    SmokeOutlineShader.INSTANCE.startDraw(event.getPartialTicks());
                    this.renderPlayersOutline(event.getPartialTicks());
                    SmokeOutlineShader.INSTANCE.stopDraw(this.colorESP.getValue(), ((Double)this.radius.getValue()).floatValue(), ((Double)this.quality.getValue()).floatValue(), (Boolean)this.GradientAlpha.getValue(), (Integer)this.alphaValue.getValue(), ((Double)this.duplicateOutline.getValue()).floatValue(), this.secondColorImgOutline.getValue(), this.thirdColorImgOutline.getValue(), (Integer)this.NUM_OCTAVESOutline.getValue());
                    SmokeOutlineShader.INSTANCE.update((Double)this.speedOutline.getValue());
                }
            }
            this.renderTags = true;
            this.renderCape = true;
            GlStateManager.func_179121_F();
        }
    }, new Predicate[0]);

    void renderPlayersFill(float tick) {
        boolean rangeCheck = (Boolean)this.rangeCheck.getValue();
        double minRange = (Double)this.minRange.getValue() * (Double)this.minRange.getValue();
        double maxRange = (Double)this.maxRange.getValue() * (Double)this.maxRange.getValue();
        AtomicInteger nEntities = new AtomicInteger();
        int maxEntities = (Integer)this.maxEntities.getValue();
        try {
            Shaders.mc.field_71441_e.field_72996_f.stream().filter(e -> {
                if (nEntities.getAndIncrement() > maxEntities) {
                    return false;
                }
                return e instanceof EntityPlayer ? (Boolean)this.playersFill.getValue() != false && (e != Shaders.mc.field_71439_g || Shaders.mc.field_71474_y.field_74320_O != 0) : (e instanceof EntityItem ? (Boolean)this.itemsFill.getValue() != false : (e instanceof EntityCreature ? (Boolean)this.mobsFill.getValue() != false : (e instanceof EntityEnderCrystal ? (Boolean)this.crystalsFill.getValue() != false : (e instanceof EntityXPOrb ? (Boolean)this.xpFill.getValue() != false : (e instanceof EntityExpBottle ? (Boolean)this.bottleFill.getValue() != false : (e instanceof EntityBoat ? (Boolean)this.boatFill.getValue() != false : (e instanceof EntityMinecart ? (Boolean)this.minecartFill.getValue() != false : (e instanceof EntityEnderPearl ? (Boolean)this.enderPerleFill.getValue() != false : e instanceof EntityArrow && (Boolean)this.arrowFill.getValue() != false))))))));
            }).filter(e -> {
                if (!rangeCheck) {
                    return true;
                }
                double distancePl = Shaders.mc.field_71439_g.func_70068_e(e);
                return distancePl > minRange && distancePl < maxRange;
            }).forEach(e -> mc.func_175598_ae().func_188388_a(e, tick, true));
        }
        catch (NullPointerException e2) {
            PistonCrystal.printDebug("e", false);
        }
    }

    void renderPlayersOutline(float tick) {
        boolean rangeCheck = (Boolean)this.rangeCheck.getValue();
        double minRange = (Double)this.minRange.getValue() * (Double)this.minRange.getValue();
        double maxRange = (Double)this.maxRange.getValue() * (Double)this.maxRange.getValue();
        AtomicInteger nEntities = new AtomicInteger();
        int maxEntities = (Integer)this.maxEntities.getValue();
        Shaders.mc.field_71441_e.func_73027_a(-1000, (Entity)new EntityXPOrb((World)Shaders.mc.field_71441_e, Shaders.mc.field_71439_g.field_70165_t, Shaders.mc.field_71439_g.field_70163_u + 1000000.0, Shaders.mc.field_71439_g.field_70161_v, 1));
        Shaders.mc.field_71441_e.field_72996_f.stream().filter(e -> {
            if (nEntities.getAndIncrement() > maxEntities) {
                return false;
            }
            return e instanceof EntityPlayer ? (Boolean)this.playersOutline.getValue() != false && (e != Shaders.mc.field_71439_g || Shaders.mc.field_71474_y.field_74320_O != 0) : (e instanceof EntityItem ? (Boolean)this.itemsOutline.getValue() != false : (e instanceof EntityCreature ? (Boolean)this.mobsOutline.getValue() != false : (e instanceof EntityEnderCrystal ? (Boolean)this.crystalsOutline.getValue() != false : (e instanceof EntityXPOrb ? (Boolean)this.xpOutline.getValue() != false || e.func_145782_y() == -1000 : (e instanceof EntityExpBottle ? (Boolean)this.bottleOutline.getValue() != false : (e instanceof EntityBoat ? (Boolean)this.boatOutline.getValue() != false : (e instanceof EntityMinecart ? (Boolean)this.minecartTntOutline.getValue() != false : (e instanceof EntityEnderPearl ? (Boolean)this.enderPerleOutline.getValue() != false : e instanceof EntityArrow && (Boolean)this.arrowOutline.getValue() != false))))))));
        }).filter(e -> {
            if (!rangeCheck) {
                return true;
            }
            double distancePl = Shaders.mc.field_71439_g.func_70068_e(e);
            return distancePl > minRange && distancePl < maxRange || e.func_145782_y() == -1000;
        }).forEach(e -> mc.func_175598_ae().func_188388_a(e, tick, true));
        Shaders.mc.field_71441_e.func_73028_b(-1000);
    }
}

