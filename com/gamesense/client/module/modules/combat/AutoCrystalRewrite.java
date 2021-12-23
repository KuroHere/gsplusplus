/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.Phase;
import com.gamesense.api.event.events.DamageBlockEvent;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.StringSetting;
import com.gamesense.api.util.misc.KeyBoardClass;
import com.gamesense.api.util.misc.Timer;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.player.PredictUtil;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.combat.CrystalUtil;
import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.api.util.world.combat.ac.CrystalInfo;
import com.gamesense.api.util.world.combat.ac.PlayerInfo;
import com.gamesense.api.util.world.combat.ac.PositionInfo;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AutoWeb;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import com.gamesense.mixin.mixins.accessor.AccessorCPacketAttack;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@Module.Declaration(name="AutoCrystalRewrite", category=Category.Combat, priority=100)
public class AutoCrystalRewrite
extends Module {
    BooleanSetting logicTarget = this.registerBoolean("Logic Section", true);
    ModeSetting logic = this.registerMode("Logic", Arrays.asList("Place->Break", "Break->Place", "Place", "Break"), "Place->Break", () -> (Boolean)this.logicTarget.getValue());
    BooleanSetting oneStop = this.registerBoolean("One Stop", false, () -> (Boolean)this.logicTarget.getValue() != false && (((String)this.logic.getValue()).equals("Place->Break") || ((String)this.logic.getValue()).equals("Break->Place")));
    ModeSetting targetPlacing = this.registerMode("Target Placing", Arrays.asList("Nearest", "Lowest", "Damage"), "Nearest", () -> (Boolean)this.logicTarget.getValue());
    ModeSetting targetBreaking = this.registerMode("Target Breaking", Arrays.asList("Nearest", "Lowest", "Damage"), "Nearest", () -> (Boolean)this.logicTarget.getValue());
    BooleanSetting stopGapple = this.registerBoolean("Stop Gapple", false, () -> (Boolean)this.logicTarget.getValue());
    IntegerSetting tickWaitEat = this.registerInteger("Tick Wait Eat", 4, 0, 10, () -> (Boolean)this.logicTarget.getValue() != false && (Boolean)this.stopGapple.getValue() != false);
    public BooleanSetting newPlace = this.registerBoolean("1.13 mode", false, () -> (Boolean)this.logicTarget.getValue());
    BooleanSetting ignoreTerrain = this.registerBoolean("Ignore Terrain", false, () -> (Boolean)this.logicTarget.getValue());
    BooleanSetting bindIgnoreTerrain = this.registerBoolean("Bind IgnoreTerrain", false, () -> (Boolean)this.logicTarget.getValue() != false && (Boolean)this.ignoreTerrain.getValue() != false);
    BooleanSetting entityPredict = this.registerBoolean("Entity Predict", false, () -> (Boolean)this.logicTarget.getValue());
    IntegerSetting offset = this.registerInteger("OffSet Predict", 0, 0, 10, () -> (Boolean)this.logicTarget.getValue() != false && (Boolean)this.entityPredict.getValue() != false);
    IntegerSetting tryAttack = this.registerInteger("Try Attack", 1, 1, 10, () -> (Boolean)this.logicTarget.getValue() != false && (Boolean)this.entityPredict.getValue() != false);
    IntegerSetting delayAttacks = this.registerInteger("Delay Attacks", 50, 0, 500, () -> (Boolean)this.logicTarget.getValue() != false && (Boolean)this.entityPredict.getValue() != false);
    IntegerSetting midDelayAttacks = this.registerInteger("Mid Delay Attack", 5, 0, 100, () -> (Boolean)this.logicTarget.getValue() != false && (Boolean)this.entityPredict.getValue() != false);
    BooleanSetting ranges = this.registerBoolean("Range Section", false);
    DoubleSetting rangeEnemyPlace = this.registerDouble("Range Enemy Place", 7.0, 0.0, 12.0, () -> (Boolean)this.ranges.getValue());
    DoubleSetting rangeEnemyBreaking = this.registerDouble("Range Enemy Breaking", 7.0, 0.0, 12.0, () -> (Boolean)this.ranges.getValue());
    public DoubleSetting placeRange = this.registerDouble("Place Range", 6.0, 0.0, 8.0, () -> (Boolean)this.ranges.getValue());
    public DoubleSetting breakRange = this.registerDouble("Break Range", 6.0, 0.0, 8.0, () -> (Boolean)this.ranges.getValue());
    DoubleSetting crystalWallPlace = this.registerDouble("Wall Range Place", 3.5, 0.0, 8.0, () -> (Boolean)this.ranges.getValue());
    DoubleSetting wallrangeBreak = this.registerDouble("Wall Range Break", 3.5, 0.0, 8.0, () -> (Boolean)this.ranges.getValue());
    IntegerSetting maxYTarget = this.registerInteger("Max Y", 3, 0, 5, () -> (Boolean)this.ranges.getValue());
    IntegerSetting minYTarget = this.registerInteger("Min Y", 3, 0, 5, () -> (Boolean)this.ranges.getValue());
    BooleanSetting place = this.registerBoolean("Place Section", false);
    ModeSetting placeDelay = this.registerMode("Place Delay", Arrays.asList("Tick", "Time", "Vanilla"), "Tick", () -> (Boolean)this.place.getValue());
    IntegerSetting tickDelayPlace = this.registerInteger("Tick Delay Place", 0, 0, 20, () -> (Boolean)this.place.getValue() != false && ((String)this.placeDelay.getValue()).equals("Tick"));
    IntegerSetting timeDelayPlace = this.registerInteger("TIme Delay Place", 0, 0, 2000, () -> (Boolean)this.place.getValue() != false && ((String)this.placeDelay.getValue()).equals("Time"));
    IntegerSetting vanillaSpeedPlace = this.registerInteger("Vanilla Speed pl", 19, 0, 20, () -> (Boolean)this.place.getValue() != false && ((String)this.placeDelay.getValue()).equals("Vanilla"));
    BooleanSetting placeOnCrystal = this.registerBoolean("Place On Crystal", false, () -> (Boolean)this.place.getValue());
    DoubleSetting minDamagePlace = this.registerDouble("Min Damage Place", 5.0, 0.0, 30.0, () -> (Boolean)this.place.getValue());
    DoubleSetting maxSelfDamagePlace = this.registerDouble("Max Self Damage Place", 12.0, 0.0, 30.0, () -> (Boolean)this.place.getValue());
    BooleanSetting relativeDamagePlace = this.registerBoolean("Relative Damage Pl", false, () -> (Boolean)this.place.getValue());
    DoubleSetting relativeDamageValuePlace = this.registerDouble("Damage Relative Damage Pl", 0.8, 0.0, 1.0, () -> (Boolean)this.place.getValue() != false && (Boolean)this.relativeDamagePlace.getValue() != false);
    IntegerSetting armourFacePlace = this.registerInteger("Armour Health%", 20, 0, 100, () -> (Boolean)this.place.getValue());
    IntegerSetting facePlaceValue = this.registerInteger("FacePlace HP", 8, 0, 36, () -> (Boolean)this.place.getValue());
    DoubleSetting minFacePlaceDmg = this.registerDouble("FacePlace Dmg", 2.0, 0.0, 10.0, () -> (Boolean)this.place.getValue());
    BooleanSetting antiSuicidepl = this.registerBoolean("AntiSuicide pl", true, () -> (Boolean)this.place.getValue());
    BooleanSetting includeCrystalMapping = this.registerBoolean("Include Crystal Mapping", true, () -> (Boolean)this.place.getValue());
    ModeSetting limitPacketPlace = this.registerMode("Limit Packet Place", Arrays.asList("None", "Tick", "Time"), "None", () -> (Boolean)this.place.getValue());
    IntegerSetting limitTickPlace = this.registerInteger("Limit Tick Place", 0, 0, 20, () -> (Boolean)this.place.getValue() != false && ((String)this.limitPacketPlace.getValue()).equals("Tick"));
    IntegerSetting limitTickTime = this.registerInteger("Limit Time Place", 0, 0, 2000, () -> (Boolean)this.place.getValue() != false && ((String)this.limitPacketPlace.getValue()).equals("Time"));
    ModeSetting swingModepl = this.registerMode("Swing Mode pl", Arrays.asList("Client", "Server", "None"), "Server", () -> (Boolean)this.place.getValue());
    BooleanSetting hideClientpl = this.registerBoolean("Hide Client pl", false, () -> (Boolean)this.place.getValue() != false && ((String)this.swingModepl.getValue()).equals("Server"));
    BooleanSetting autoWeb = this.registerBoolean("Auto Web", false, () -> (Boolean)this.place.getValue());
    BooleanSetting stopCrystal = this.registerBoolean("Stop Crystal", true, () -> (Boolean)this.place.getValue() != false && (Boolean)this.autoWeb.getValue() != false);
    BooleanSetting preRotateWeb = this.registerBoolean("Pre Rotate Web", false, () -> (Boolean)this.place.getValue() != false && (Boolean)this.autoWeb.getValue() != false);
    BooleanSetting focusWebRotate = this.registerBoolean("Focus Ber Rotate", false, () -> (Boolean)this.place.getValue() != false && (Boolean)this.autoWeb.getValue() != false);
    BooleanSetting onlyAutoWebActive = this.registerBoolean("On AutoWeb active", true, () -> (Boolean)this.place.getValue() != false && (Boolean)this.autoWeb.getValue() != false);
    BooleanSetting switchWeb = this.registerBoolean("Switch Web", false, () -> (Boolean)this.place.getValue() != false && (Boolean)this.autoWeb.getValue() != false);
    BooleanSetting silentSwitchWeb = this.registerBoolean("Silent Switch Web", false, () -> (Boolean)this.place.getValue() != false && (Boolean)this.autoWeb.getValue() != false);
    BooleanSetting switchBackWeb = this.registerBoolean("Switch Back Web", false, () -> (Boolean)this.place.getValue() != false && (Boolean)this.autoWeb.getValue() != false && (Boolean)this.switchWeb.getValue() != false && (Boolean)this.silentSwitchWeb.getValue() == false);
    BooleanSetting switchBackEnd = this.registerBoolean("Switch Back Web End", false, () -> (Boolean)this.place.getValue() != false && (Boolean)this.autoWeb.getValue() != false && (Boolean)this.switchWeb.getValue() != false && (Boolean)this.silentSwitchWeb.getValue() == false && (Boolean)this.switchBackWeb.getValue() != false);
    BooleanSetting breakNearCrystal = this.registerBoolean("Break Near Crystal", false, () -> (Boolean)this.place.getValue());
    BooleanSetting breakSection = this.registerBoolean("Break Section", false);
    ModeSetting breakDelay = this.registerMode("Break Delay", Arrays.asList("Tick", "Time", "Vanilla"), "Tick", () -> (Boolean)this.breakSection.getValue());
    IntegerSetting tickDelayBreak = this.registerInteger("Tick Delay Place", 0, 0, 20, () -> (Boolean)this.breakSection.getValue() != false && ((String)this.breakDelay.getValue()).equals("Tick"));
    IntegerSetting timeDelayBreak = this.registerInteger("TIme Delay Place", 0, 0, 2000, () -> (Boolean)this.breakSection.getValue() != false && ((String)this.breakDelay.getValue()).equals("Time"));
    IntegerSetting vanillaSpeedBreak = this.registerInteger("Vanilla Speed br", 19, 0, 20, () -> (Boolean)this.breakSection.getValue() != false && ((String)this.breakDelay.getValue()).equals("Vanilla"));
    ModeSetting chooseCrystal = this.registerMode("Choose Type", Arrays.asList("Own", "All", "Smart"), "Smart", () -> (Boolean)this.breakSection.getValue());
    DoubleSetting minDamageBreak = this.registerDouble("Min Damage Break", 5.0, 0.0, 30.0, () -> (Boolean)this.breakSection.getValue());
    DoubleSetting maxSelfDamageBreak = this.registerDouble("Max Self Damage Break", 12.0, 0.0, 30.0, () -> (Boolean)this.breakSection.getValue() != false && ((String)this.chooseCrystal.getValue()).equals("Smart"));
    BooleanSetting relativeDamageBreak = this.registerBoolean("Relative Damage Br", false, () -> (Boolean)this.breakSection.getValue());
    DoubleSetting relativeDamageValueBreak = this.registerDouble("Damage Relative Damage Br", 0.8, 0.0, 1.0, () -> (Boolean)this.breakSection.getValue() != false && (Boolean)this.relativeDamagePlace.getValue() != false);
    ModeSetting swingModebr = this.registerMode("Swing Mode br", Arrays.asList("Client", "Server", "None"), "Server", () -> (Boolean)this.breakSection.getValue());
    BooleanSetting hideClientbr = this.registerBoolean("Hide Client br", false, () -> (Boolean)this.breakSection.getValue() != false && ((String)this.swingModebr.getValue()).equals("Server"));
    ModeSetting breakTypeCrystal = this.registerMode("Break Type", Arrays.asList("Packet", "Vanilla"), "Packet", () -> (Boolean)this.breakSection.getValue());
    ModeSetting limitBreakPacket = this.registerMode("Limit Break Packet", Arrays.asList("Tick", "Time", "None"), "None", () -> (Boolean)this.breakSection.getValue());
    IntegerSetting lomitBreakPacketTick = this.registerInteger("Limit Break Tick", 4, 0, 20, () -> (Boolean)this.breakSection.getValue() != false && ((String)this.limitBreakPacket.getValue()).equals("Tick"));
    IntegerSetting limitBreakPacketTime = this.registerInteger("Limit Break Time", 500, 0, 2000, () -> (Boolean)this.breakSection.getValue() != false && ((String)this.limitBreakPacket.getValue()).equals("Time"));
    ModeSetting firstHit = this.registerMode("First Hit", Arrays.asList("Tick", "Time", "None"), "None", () -> (Boolean)this.breakSection.getValue());
    IntegerSetting firstHitTick = this.registerInteger("Tick First Hit", 0, 0, 20, () -> (Boolean)this.breakSection.getValue() != false && ((String)this.firstHit.getValue()).equals("Tick"));
    IntegerSetting fitstHitTime = this.registerInteger("TIme First Hit", 0, 0, 2000, () -> (Boolean)this.breakSection.getValue() != false && ((String)this.firstHit.getValue()).equals("Time"));
    BooleanSetting cancelCrystal = this.registerBoolean("Cancel Crystal", false, () -> (Boolean)this.breakSection.getValue());
    BooleanSetting setDead = this.registerBoolean("Set Dead", true, () -> (Boolean)this.breakSection.getValue());
    BooleanSetting placeAfterBreak = this.registerBoolean("Place After", false, () -> (Boolean)this.breakSection.getValue());
    BooleanSetting instaPlace = this.registerBoolean("Insta Place", false, () -> (Boolean)this.breakSection.getValue() != false && (Boolean)this.placeAfterBreak.getValue() != false);
    BooleanSetting checkinstaPlace = this.registerBoolean("Check Insta Place", false, () -> (Boolean)this.breakSection.getValue() != false && (Boolean)this.placeAfterBreak.getValue() != false && (Boolean)this.instaPlace.getValue() != false);
    BooleanSetting forcePlace = this.registerBoolean("Force Place", false, () -> (Boolean)this.breakSection.getValue() != false && (Boolean)this.placeAfterBreak.getValue() != false && (Boolean)this.instaPlace.getValue() != false);
    BooleanSetting antiWeakness = this.registerBoolean("Anti Weakness", false, () -> (Boolean)this.breakSection.getValue());
    ModeSetting slowBreak = this.registerMode("Slow Break", Arrays.asList("None", "Tick", "Time"), "None", () -> (Boolean)this.breakSection.getValue());
    DoubleSetting speedActivation = this.registerDouble("Speed Activation", 0.5, 0.0, 1.0, () -> (Boolean)this.breakSection.getValue() != false && !((String)this.slowBreak.getValue()).equals("None"));
    IntegerSetting tickSlowBreak = this.registerInteger("Tick Slow Break", 3, 0, 20, () -> (Boolean)this.breakSection.getValue() != false && ((String)this.slowBreak.getValue()).equals("Tick"));
    IntegerSetting timeSlowBreak = this.registerInteger("Time Slow Break", 3, 0, 10, () -> (Boolean)this.breakSection.getValue() != false && ((String)this.slowBreak.getValue()).equals("Time"));
    BooleanSetting predictHit = this.registerBoolean("Predict Hit", false, () -> (Boolean)this.breakSection.getValue());
    IntegerSetting predictHitDelay = this.registerInteger("Predict Hit Delay", 0, 0, 500, () -> (Boolean)this.breakSection.getValue() != false && (Boolean)this.predictHit.getValue() != false);
    BooleanSetting antiSuicidebr = this.registerBoolean("AntiSuicide br", true, () -> (Boolean)this.breakSection.getValue());
    BooleanSetting antiCity = this.registerBoolean("Anti City", false, () -> (Boolean)this.breakSection.getValue());
    BooleanSetting destroyCrystal = this.registerBoolean("Destroy Stuck Crystal", false, () -> (Boolean)this.breakSection.getValue() != false && (Boolean)this.antiCity.getValue() != false);
    BooleanSetting destroyAboveCrystal = this.registerBoolean("Destroy Above Crystal", false, () -> (Boolean)this.breakSection.getValue() != false && (Boolean)this.antiCity.getValue() != false);
    BooleanSetting allowNon1x1 = this.registerBoolean("Allow non 1x1", false, () -> (Boolean)this.breakSection.getValue() != false && (Boolean)this.antiCity.getValue() != false);
    BooleanSetting misc = this.registerBoolean("Misc Section", false);
    BooleanSetting switchHotbar = this.registerBoolean("Switch Crystal", false, () -> (Boolean)this.misc.getValue());
    BooleanSetting switchBack = this.registerBoolean("Switch Back", false, () -> (Boolean)this.misc.getValue() != false && (Boolean)this.switchHotbar.getValue() != false);
    IntegerSetting tickSwitchBack = this.registerInteger("Tick Switch Back", 5, 0, 50, () -> (Boolean)this.misc.getValue() != false && (Boolean)this.switchHotbar.getValue() != false && (Boolean)this.switchBack.getValue() != false);
    BooleanSetting waitGappleSwitch = this.registerBoolean("Wait Gapple Switch", false, () -> (Boolean)this.misc.getValue() != false && (Boolean)this.switchHotbar.getValue() != false && (Boolean)this.stopGapple.getValue() != false);
    BooleanSetting silentSwitch = this.registerBoolean("Silent Switch", false, () -> (Boolean)this.misc.getValue() != false && (Boolean)this.switchHotbar.getValue() != false);
    BooleanSetting renders = this.registerBoolean("Renders", false);
    ModeSetting typePlace = this.registerMode("Render Place", Arrays.asList("None", "Outline", "Fill", "Both"), "Both", () -> (Boolean)this.renders.getValue());
    ModeSetting placeDimension = this.registerMode("Place Dimension", Arrays.asList("Box", "Flat", "Slab", "Circle"), "Box", () -> (Boolean)this.renders.getValue() != false && !((String)this.typePlace.getValue()).equals("None"));
    DoubleSetting rangeCirclePl = this.registerDouble("Range Circle Pl", 0.5, 0.1, 1.5, () -> (Boolean)this.renders.getValue() != false && ((String)this.placeDimension.getValue()).equals("Circle"));
    DoubleSetting slabHeightPlace = this.registerDouble("Slab height Place", 0.2, 0.0, 1.0, () -> (Boolean)this.renders.getValue() != false && ((String)this.placeDimension.getValue()).equals("Slab"));
    BooleanSetting OutLineSection = this.registerBoolean("OutLine Section Custom pl", false, () -> (((String)this.typePlace.getValue()).equals("Outline") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.renders.getValue() != false);
    IntegerSetting outlineWidthpl = this.registerInteger("Outline Width", 5, 1, 5, () -> (((String)this.typePlace.getValue()).equals("Outline") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.renders.getValue() != false && (Boolean)this.OutLineSection.getValue() != false);
    ModeSetting NVerticesOutlineBot = this.registerMode("N^ Vertices Outline Bot pl", Arrays.asList("1", "2", "4"), "1", () -> (((String)this.typePlace.getValue()).equals("Outline") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && (Boolean)this.renders.getValue() != false);
    ModeSetting direction2OutLineBot = this.registerMode("Direction Outline Bot pl", Arrays.asList("X", "Z"), "X", () -> (((String)this.typePlace.getValue()).equals("Outline") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && ((String)this.NVerticesOutlineBot.getValue()).equals("2"));
    ColorSetting firstVerticeOutlineBot = this.registerColor("1 Vert Out Bot pl", new GSColor(255, 16, 19, 50), () -> (((String)this.typePlace.getValue()).equals("Outline") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && (Boolean)this.renders.getValue() != false, true);
    ColorSetting secondVerticeOutlineBot = this.registerColor("2 Vert Out Bot pl", new GSColor(0, 0, 255, 50), () -> !(!((String)this.typePlace.getValue()).equals("Outline") && !((String)this.typePlace.getValue()).equals("Both") || (Boolean)this.OutLineSection.getValue() == false || (Boolean)this.renders.getValue() == false || !((String)this.NVerticesOutlineBot.getValue()).equals("2") && !((String)this.NVerticesOutlineBot.getValue()).equals("4")), true);
    ColorSetting thirdVerticeOutlineBot = this.registerColor("3 Vert Out Bot pl", new GSColor(0, 255, 128, 50), () -> (((String)this.typePlace.getValue()).equals("Outline") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesOutlineBot.getValue()).equals("4"), true);
    ColorSetting fourVerticeOutlineBot = this.registerColor("4 Vert Out Bot pl", new GSColor(255, 255, 2, 50), () -> (((String)this.typePlace.getValue()).equals("Outline") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesOutlineBot.getValue()).equals("4"), true);
    ModeSetting NVerticesOutlineTop = this.registerMode("N^ Vertices Outline Top pl", Arrays.asList("1", "2", "4"), "1", () -> (((String)this.typePlace.getValue()).equals("Outline") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && (Boolean)this.renders.getValue() != false);
    ModeSetting direction2OutLineTop = this.registerMode("Direction Outline Top pl", Arrays.asList("X", "Z"), "X", () -> (((String)this.typePlace.getValue()).equals("Outline") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesOutlineTop.getValue()).equals("2"));
    ColorSetting firstVerticeOutlineTop = this.registerColor("1 Vert Out Top pl", new GSColor(255, 16, 19, 50), () -> (((String)this.typePlace.getValue()).equals("Outline") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && (Boolean)this.renders.getValue() != false, true);
    ColorSetting secondVerticeOutlineTop = this.registerColor("2 Vert Out Top pl", new GSColor(0, 0, 255, 50), () -> !(!((String)this.typePlace.getValue()).equals("Outline") && !((String)this.typePlace.getValue()).equals("Both") || (Boolean)this.OutLineSection.getValue() == false || (Boolean)this.renders.getValue() == false || !((String)this.NVerticesOutlineTop.getValue()).equals("2") && !((String)this.NVerticesOutlineTop.getValue()).equals("4")), true);
    ColorSetting thirdVerticeOutlineTop = this.registerColor("3 Vert Out Top pl", new GSColor(0, 255, 128, 50), () -> (((String)this.typePlace.getValue()).equals("Outline") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesOutlineTop.getValue()).equals("4"), true);
    ColorSetting fourVerticeOutlineTop = this.registerColor("4 Vert Out Top pl", new GSColor(255, 255, 2, 50), () -> (((String)this.typePlace.getValue()).equals("Outline") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesOutlineTop.getValue()).equals("4"), true);
    BooleanSetting FillSection = this.registerBoolean("Fill Section Custom pl", false, () -> (((String)this.typePlace.getValue()).equals("Fill") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.renders.getValue() != false);
    ModeSetting NVerticesFillBot = this.registerMode("N^ Vertices Fill Bot pl", Arrays.asList("1", "2", "4"), "1", () -> (((String)this.typePlace.getValue()).equals("Fill") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && (Boolean)this.renders.getValue() != false);
    ModeSetting direction2FillBot = this.registerMode("Direction Fill Bot pl", Arrays.asList("X", "Z"), "X", () -> (((String)this.typePlace.getValue()).equals("Fill") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && ((String)this.NVerticesFillBot.getValue()).equals("2") && (Boolean)this.renders.getValue() != false);
    ColorSetting firstVerticeFillBot = this.registerColor("1 Vert Fill Bot pl", new GSColor(17, 89, 100, 50), () -> (((String)this.typePlace.getValue()).equals("Fill") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && (Boolean)this.renders.getValue() != false, true);
    ColorSetting secondVerticeFillBot = this.registerColor("2 Vert Fill Bot pl", new GSColor(0, 0, 255, 50), () -> !(!((String)this.typePlace.getValue()).equals("Fill") && !((String)this.typePlace.getValue()).equals("Both") || (Boolean)this.FillSection.getValue() == false || (Boolean)this.renders.getValue() == false || !((String)this.NVerticesFillBot.getValue()).equals("2") && !((String)this.NVerticesFillBot.getValue()).equals("4")), true);
    ColorSetting thirdVerticeFillBot = this.registerColor("3 Vert Fill Bot pl", new GSColor(0, 255, 128, 50), () -> (((String)this.typePlace.getValue()).equals("Fill") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesFillBot.getValue()).equals("4"), true);
    ColorSetting fourVerticeFillBot = this.registerColor("4 Vert Fill Bot pl", new GSColor(255, 255, 2, 50), () -> (((String)this.typePlace.getValue()).equals("Fill") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesFillBot.getValue()).equals("4"), true);
    ModeSetting NVerticesFillTop = this.registerMode("N^ Vertices Fill Top pl", Arrays.asList("1", "2", "4"), "1", () -> (((String)this.typePlace.getValue()).equals("Fill") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && (Boolean)this.renders.getValue() != false);
    ModeSetting direction2FillTop = this.registerMode("Direction Fill Top pl", Arrays.asList("X", "Z"), "X", () -> (((String)this.typePlace.getValue()).equals("Fill") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && ((String)this.NVerticesFillTop.getValue()).equals("2") && (Boolean)this.renders.getValue() != false);
    ColorSetting firstVerticeFillTop = this.registerColor("1 Vert Fill Top pl", new GSColor(255, 16, 19, 50), () -> (((String)this.typePlace.getValue()).equals("Fill") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && (Boolean)this.renders.getValue() != false, true);
    ColorSetting secondVerticeFillTop = this.registerColor("2 Vert Fill Top pl", new GSColor(0, 0, 255, 50), () -> !(!((String)this.typePlace.getValue()).equals("Fill") && !((String)this.typePlace.getValue()).equals("Both") || (Boolean)this.FillSection.getValue() == false || (Boolean)this.renders.getValue() == false || !((String)this.NVerticesFillTop.getValue()).equals("2") && !((String)this.NVerticesFillTop.getValue()).equals("4")), true);
    ColorSetting thirdVerticeFillTop = this.registerColor("3 Vert Fill Top pl", new GSColor(0, 255, 128, 50), () -> (((String)this.typePlace.getValue()).equals("Fill") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesFillTop.getValue()).equals("4"), true);
    ColorSetting fourVerticeFillTop = this.registerColor("4 Vert Fill Top pl", new GSColor(255, 255, 2, 50), () -> (((String)this.typePlace.getValue()).equals("Fill") || ((String)this.typePlace.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesFillTop.getValue()).equals("4"), true);
    ModeSetting typeBreak = this.registerMode("Render Break", Arrays.asList("None", "Outline", "Fill", "Both"), "Both", () -> (Boolean)this.renders.getValue());
    ModeSetting breakDimension = this.registerMode("Break Dimension", Arrays.asList("Box", "Flat", "Slab", "Circle"), "Box", () -> (Boolean)this.renders.getValue() & !((String)this.typeBreak.getValue()).equals("None"));
    DoubleSetting rangeCircleBr = this.registerDouble("Range Circle Br", 0.5, 0.1, 1.5, () -> (Boolean)this.renders.getValue() != false && ((String)this.breakDimension.getValue()).equals("Circle"));
    DoubleSetting slabHeightBreak = this.registerDouble("Slab height Break", 0.2, 0.0, 1.0, () -> (Boolean)this.renders.getValue() != false && ((String)this.breakDimension.getValue()).equals("Slab"));
    BooleanSetting OutLineSectionbr = this.registerBoolean("OutLine Section Custom br", false, () -> (((String)this.typeBreak.getValue()).equals("Outline") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.renders.getValue() != false);
    ModeSetting NVerticesOutlineBotbr = this.registerMode("N^ Vertices Outline Bot br", Arrays.asList("1", "2", "4"), "1", () -> (((String)this.typeBreak.getValue()).equals("Outline") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.OutLineSectionbr.getValue() != false && (Boolean)this.renders.getValue() != false);
    ModeSetting direction2OutLineBotbr = this.registerMode("Direction Outline Bot br", Arrays.asList("X", "Z"), "X", () -> (((String)this.typeBreak.getValue()).equals("Outline") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.OutLineSectionbr.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesOutlineBotbr.getValue()).equals("2"));
    ColorSetting firstVerticeOutlineBotbr = this.registerColor("1 Vert Out Bot br", new GSColor(16, 50, 100, 255), () -> (((String)this.typeBreak.getValue()).equals("Outline") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.OutLineSectionbr.getValue() != false && (Boolean)this.renders.getValue() != false, true);
    ColorSetting secondVerticeOutlineBotbr = this.registerColor("2 Vert Out Bot br", new GSColor(0, 0, 255, 50), () -> !(!((String)this.typeBreak.getValue()).equals("Outline") && !((String)this.typeBreak.getValue()).equals("Both") || (Boolean)this.OutLineSectionbr.getValue() == false || (Boolean)this.renders.getValue() == false || !((String)this.NVerticesOutlineBotbr.getValue()).equals("2") && !((String)this.NVerticesOutlineBotbr.getValue()).equals("4")), true);
    ColorSetting thirdVerticeOutlineBotbr = this.registerColor("3 Vert Out Bot br", new GSColor(0, 255, 128, 50), () -> (((String)this.typeBreak.getValue()).equals("Outline") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.OutLineSectionbr.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesOutlineBotbr.getValue()).equals("4"), true);
    ColorSetting fourVerticeOutlineBotbr = this.registerColor("4 Vert Out Bot br", new GSColor(255, 255, 2, 50), () -> (((String)this.typeBreak.getValue()).equals("Outline") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.OutLineSectionbr.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesOutlineBotbr.getValue()).equals("4"), true);
    ModeSetting NVerticesOutlineTopbr = this.registerMode("N^ Vertices Outline Top br", Arrays.asList("1", "2", "4"), "1", () -> (((String)this.typeBreak.getValue()).equals("Outline") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.OutLineSectionbr.getValue() != false && (Boolean)this.renders.getValue() != false);
    ModeSetting direction2OutLineTopbr = this.registerMode("Direction Outline Top br", Arrays.asList("X", "Z"), "X", () -> (((String)this.typeBreak.getValue()).equals("Outline") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.OutLineSectionbr.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesOutlineTopbr.getValue()).equals("2"));
    ColorSetting firstVerticeOutlineTopbr = this.registerColor("1 Vert Out Top br", new GSColor(255, 16, 19, 255), () -> (((String)this.typeBreak.getValue()).equals("Outline") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.OutLineSectionbr.getValue() != false && (Boolean)this.renders.getValue() != false, true);
    ColorSetting secondVerticeOutlineTopbr = this.registerColor("2 Vert Out Top br", new GSColor(0, 0, 255, 50), () -> !(!((String)this.typeBreak.getValue()).equals("Outline") && !((String)this.typeBreak.getValue()).equals("Both") || (Boolean)this.OutLineSectionbr.getValue() == false || (Boolean)this.renders.getValue() == false || !((String)this.NVerticesOutlineTopbr.getValue()).equals("2") && !((String)this.NVerticesOutlineTopbr.getValue()).equals("4")), true);
    ColorSetting thirdVerticeOutlineTopbr = this.registerColor("3 Vert Out Top br", new GSColor(0, 255, 128, 50), () -> (((String)this.typeBreak.getValue()).equals("Outline") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.OutLineSectionbr.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesOutlineTopbr.getValue()).equals("4"), true);
    ColorSetting fourVerticeOutlineTopbr = this.registerColor("4 Vert Out Top br", new GSColor(255, 255, 2, 50), () -> (((String)this.typeBreak.getValue()).equals("Outline") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.OutLineSectionbr.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesOutlineTopbr.getValue()).equals("4"), true);
    BooleanSetting FillSectionbr = this.registerBoolean("Fill Section Custom br", false, () -> (((String)this.typeBreak.getValue()).equals("Fill") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.renders.getValue() != false);
    ModeSetting NVerticesFillBotbr = this.registerMode("N^ Vertices Fill Bot br", Arrays.asList("1", "2", "4"), "1", () -> (((String)this.typeBreak.getValue()).equals("Fill") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.FillSectionbr.getValue() != false);
    ModeSetting direction2FillBotbr = this.registerMode("Direction Fill Bot br", Arrays.asList("X", "Z"), "X", () -> (((String)this.typeBreak.getValue()).equals("Fill") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.FillSectionbr.getValue() != false && ((String)this.NVerticesFillBotbr.getValue()).equals("2"));
    ColorSetting firstVerticeFillBotbr = this.registerColor("1 Vert Fill Bot br", new GSColor(17, 89, 100, 50), () -> (((String)this.typeBreak.getValue()).equals("Fill") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.FillSectionbr.getValue() != false && (Boolean)this.renders.getValue() != false, true);
    ColorSetting secondVerticeFillBotbr = this.registerColor("2 Vert Fill Bot br", new GSColor(0, 0, 255, 50), () -> !(!((String)this.typeBreak.getValue()).equals("Fill") && !((String)this.typeBreak.getValue()).equals("Both") || (Boolean)this.FillSectionbr.getValue() == false || (Boolean)this.renders.getValue() == false || !((String)this.NVerticesFillBotbr.getValue()).equals("2") && !((String)this.NVerticesFillBotbr.getValue()).equals("4")), true);
    ColorSetting thirdVerticeFillBotbr = this.registerColor("3 Vert Fill Bot br", new GSColor(0, 255, 128, 50), () -> (((String)this.typeBreak.getValue()).equals("Fill") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.FillSectionbr.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesFillBotbr.getValue()).equals("4"), true);
    ColorSetting fourVerticeFillBotbr = this.registerColor("4 Vert Fill Bot br", new GSColor(255, 255, 2, 50), () -> (((String)this.typeBreak.getValue()).equals("Fill") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.FillSectionbr.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesFillBot.getValue()).equals("4"), true);
    ModeSetting NVerticesFillTopbr = this.registerMode("N^ Vertices Fill Top br", Arrays.asList("1", "2", "4"), "1", () -> (((String)this.typeBreak.getValue()).equals("Fill") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.FillSectionbr.getValue() != false && (Boolean)this.renders.getValue() != false);
    ModeSetting direction2FillTopbr = this.registerMode("Direction Fill Top br", Arrays.asList("X", "Z"), "X", () -> (((String)this.typeBreak.getValue()).equals("Fill") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.FillSectionbr.getValue() != false && ((String)this.NVerticesFillTopbr.getValue()).equals("2") && (Boolean)this.renders.getValue() != false);
    ColorSetting firstVerticeFillTopbr = this.registerColor("1 Vert Fill Top br", new GSColor(255, 16, 19, 50), () -> (((String)this.typeBreak.getValue()).equals("Fill") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.FillSectionbr.getValue() != false, true);
    ColorSetting secondVerticeFillTopbr = this.registerColor("2 Vert Fill Top br", new GSColor(0, 0, 255, 50), () -> !(!((String)this.typeBreak.getValue()).equals("Fill") && !((String)this.typeBreak.getValue()).equals("Both") || (Boolean)this.FillSectionbr.getValue() == false || (Boolean)this.renders.getValue() == false || !((String)this.NVerticesFillTopbr.getValue()).equals("2") && !((String)this.NVerticesFillTopbr.getValue()).equals("4")), true);
    ColorSetting thirdVerticeFillTopbr = this.registerColor("3 Vert Fill Top br", new GSColor(0, 255, 128, 50), () -> (((String)this.typeBreak.getValue()).equals("Fill") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.FillSectionbr.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesFillTopbr.getValue()).equals("4"), true);
    ColorSetting fourVerticeFillTopbr = this.registerColor("4 Vert Fill Top br", new GSColor(255, 255, 2, 50), () -> (((String)this.typeBreak.getValue()).equals("Fill") || ((String)this.typeBreak.getValue()).equals("Both")) && (Boolean)this.FillSectionbr.getValue() != false && (Boolean)this.renders.getValue() != false && ((String)this.NVerticesFillTopbr.getValue()).equals("4"), true);
    BooleanSetting showTextpl = this.registerBoolean("Show text Place", true, () -> (Boolean)this.renders.getValue());
    ColorSetting colorPlaceText = this.registerColor("Color Place Text", new GSColor(0, 255, 255), () -> (Boolean)this.renders.getValue() != false && (Boolean)this.showTextpl.getValue() != false, true);
    DoubleSetting textYPlace = this.registerDouble("Text Y Place", 0.5, -1.0, 1.0, () -> (Boolean)this.renders.getValue() != false && (Boolean)this.showTextpl.getValue() != false);
    BooleanSetting showTextbr = this.registerBoolean("Show text Brea", true, () -> (Boolean)this.renders.getValue());
    ColorSetting colorBreakText = this.registerColor("Color Break Text", new GSColor(0, 255, 255), () -> (Boolean)this.renders.getValue() != false && (Boolean)this.showTextbr.getValue() != false, true);
    DoubleSetting textYBreak = this.registerDouble("Text Y Break", 0.5, -1.0, 1.0, () -> (Boolean)this.renders.getValue() != false && (Boolean)this.showTextbr.getValue() != false);
    BooleanSetting movingPlace = this.registerBoolean("Moving Place", false, () -> (Boolean)this.renders.getValue());
    DoubleSetting movingPlaceSpeed = this.registerDouble("Moving Place Speed", 0.1, 0.01, 0.5, () -> (Boolean)this.renders.getValue() != false && (Boolean)this.movingPlace.getValue() != false);
    BooleanSetting movingBreak = this.registerBoolean("Moving Break", false, () -> (Boolean)this.renders.getValue());
    DoubleSetting movingBreakSpeed = this.registerDouble("Moving Break Speed", 0.1, 0.01, 0.5, () -> (Boolean)this.renders.getValue() != false && (Boolean)this.movingPlace.getValue() != false);
    IntegerSetting extendedPlace = this.registerInteger("Extended place", 5, 0, 20, () -> (Boolean)this.renders.getValue());
    IntegerSetting extendedBreak = this.registerInteger("Extended break", 5, 0, 20, () -> (Boolean)this.renders.getValue());
    BooleanSetting fadeCapl = this.registerBoolean("Fade Ca pl", true, () -> (Boolean)this.renders.getValue());
    IntegerSetting endFadePlace = this.registerInteger("End Fade Place pl", 0, 0, 255, () -> (Boolean)this.renders.getValue() != false && (Boolean)this.fadeCapl.getValue() != false);
    BooleanSetting fadeCabr = this.registerBoolean("Fade Ca br", true, () -> (Boolean)this.renders.getValue());
    IntegerSetting endFadeBreak = this.registerInteger("End Fade Break pl", 0, 0, 255, () -> (Boolean)this.renders.getValue() != false && (Boolean)this.fadeCabr.getValue() != false);
    IntegerSetting lifeTime = this.registerInteger("Life Time", 3000, 0, 5000, () -> (Boolean)this.renders.getValue() != false && ((Boolean)this.fadeCapl.getValue() != false || (Boolean)this.fadeCabr.getValue() != false));
    BooleanSetting placeDominant = this.registerBoolean("Place Dominant", false, () -> (Boolean)this.renders.getValue() != false && (!((String)this.typePlace.getValue()).equals("None") || !((String)this.typeBreak.getValue()).equals("None")));
    BooleanSetting predictSection = this.registerBoolean("Predict Section", false);
    BooleanSetting predictSurround = this.registerBoolean("Predict Surround", false, () -> (Boolean)this.predictSection.getValue());
    BooleanSetting predictPacketSurround = this.registerBoolean("Predict Packet Surround", false, () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.predictSurround.getValue() != false);
    IntegerSetting percentSurround = this.registerInteger("Percent Surround", 80, 0, 100, () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.predictSurround.getValue() != false && (Boolean)this.predictPacketSurround.getValue() == false);
    IntegerSetting tickPacketBreak = this.registerInteger("Tick Packet Break", 40, 0, 100, () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.predictSurround.getValue() != false && (Boolean)this.predictPacketSurround.getValue() != false);
    IntegerSetting tickMaxPacketBreak = this.registerInteger("Tick Max Packet Break", 40, 0, 150, () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.predictSurround.getValue() != false && (Boolean)this.predictPacketSurround.getValue() != false);
    DoubleSetting maxSelfDamageSur = this.registerDouble("Max Self Dam Sur", 7.0, 0.0, 20.0, () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.predictSurround.getValue() != false);
    BooleanSetting predictSelfPlace = this.registerBoolean("Predict Self Place", false, () -> (Boolean)this.predictSection.getValue());
    BooleanSetting showSelfPredictPlace = this.registerBoolean("Show Self Predict Place", false, () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.predictSelfPlace.getValue() != false);
    ColorSetting colorSelfPlace = this.registerColor("Color Self Place", new GSColor(0, 255, 255), () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.predictSelfPlace.getValue() != false && (Boolean)this.showSelfPredictPlace.getValue() != false);
    BooleanSetting predictPlaceEnemy = this.registerBoolean("Predict Place Enemy", false, () -> (Boolean)this.predictSection.getValue());
    ColorSetting showColorPredictEnemyPlace = this.registerColor("Color Place Predict Enemy", new GSColor(255, 160, 0), () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.predictPlaceEnemy.getValue() != false);
    BooleanSetting predictSelfDBreaking = this.registerBoolean("Predict Self Break", false, () -> (Boolean)this.predictSection.getValue());
    BooleanSetting showSelfPredictBreaking = this.registerBoolean("Show Self Predict Break", false, () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.predictSelfPlace.getValue() != false);
    ColorSetting colorSelfBreaking = this.registerColor("Color Self Break", new GSColor(0, 255, 255), () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.predictSelfPlace.getValue() != false && (Boolean)this.showSelfPredictPlace.getValue() != false);
    BooleanSetting predictBreakingEnemy = this.registerBoolean("Predict Break Enemy", false, () -> (Boolean)this.predictSection.getValue());
    ColorSetting showColorPredictEnemyBreaking = this.registerColor("Color Break Predict Enemy", new GSColor(255, 160, 0), () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.predictPlaceEnemy.getValue() != false);
    IntegerSetting tickPredict = this.registerInteger("Tick Predict", 8, 0, 30, () -> (Boolean)this.predictSection.getValue());
    BooleanSetting calculateYPredict = this.registerBoolean("Calculate Y Predict", true, () -> (Boolean)this.predictSection.getValue());
    IntegerSetting startDecrease = this.registerInteger("Start Decrease", 39, 0, 200, () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.calculateYPredict.getValue() != false);
    IntegerSetting exponentStartDecrease = this.registerInteger("Exponent Start", 2, 1, 5, () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.calculateYPredict.getValue() != false);
    IntegerSetting decreaseY = this.registerInteger("Decrease Y", 2, 1, 5, () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.calculateYPredict.getValue() != false);
    IntegerSetting exponentDecreaseY = this.registerInteger("Exponent Decrease Y", 1, 1, 3, () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.calculateYPredict.getValue() != false);
    IntegerSetting increaseY = this.registerInteger("Increase Y", 3, 1, 5, () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.calculateYPredict.getValue() != false);
    IntegerSetting exponentIncreaseY = this.registerInteger("Exponent Increase Y", 2, 1, 3, () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.calculateYPredict.getValue() != false);
    BooleanSetting splitXZ = this.registerBoolean("Split XZ", true, () -> (Boolean)this.predictSection.getValue());
    IntegerSetting widthPredict = this.registerInteger("Line Width", 2, 1, 5, () -> (Boolean)this.predictSection.getValue());
    BooleanSetting manualOutHole = this.registerBoolean("Manual Out Hole", false, () -> (Boolean)this.predictSection.getValue());
    BooleanSetting aboveHoleManual = this.registerBoolean("Above Hole Manual", false, () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.manualOutHole.getValue() != false);
    BooleanSetting stairPredict = this.registerBoolean("Stair Predict", false, () -> (Boolean)this.predictSection.getValue());
    IntegerSetting nStair = this.registerInteger("N Stair", 2, 1, 4, () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.stairPredict.getValue() != false);
    DoubleSetting speedActivationStair = this.registerDouble("Speed Activation Stair", 0.11, 0.0, 1.0, () -> (Boolean)this.predictSection.getValue() != false && (Boolean)this.stairPredict.getValue() != false);
    BooleanSetting threading = this.registerBoolean("Threading Section", false);
    IntegerSetting nThread = this.registerInteger("N Thread", 4, 1, 20, () -> (Boolean)this.threading.getValue());
    IntegerSetting maxTarget = this.registerInteger("Max Target", 5, 1, 30, () -> (Boolean)this.threading.getValue());
    IntegerSetting placeTimeout = this.registerInteger("Place Timeout", 100, 0, 1000, () -> (Boolean)this.threading.getValue());
    IntegerSetting predictPlaceTimeout = this.registerInteger("Predict Place Timeout", 100, 0, 1000, () -> (Boolean)this.threading.getValue());
    IntegerSetting breakTimeout = this.registerInteger("Break Timeout", 100, 0, 1000, () -> (Boolean)this.threading.getValue());
    IntegerSetting predictBreakTimeout = this.registerInteger("Predict Break Timeout", 100, 0, 1000, () -> (Boolean)this.threading.getValue());
    BooleanSetting strict = this.registerBoolean("Strict Section", false);
    BooleanSetting raytrace = this.registerBoolean("Raytrace", false, () -> (Boolean)this.strict.getValue());
    BooleanSetting rotate = this.registerBoolean("Rotate", false, () -> (Boolean)this.strict.getValue());
    BooleanSetting preRotate = this.registerBoolean("Pre Rotate", false, () -> (Boolean)this.strict.getValue() != false && (Boolean)this.rotate.getValue() != false);
    IntegerSetting tickAfterRotation = this.registerInteger("Tick After Rotation", 0, 0, 10, () -> (Boolean)this.strict.getValue() != false && (Boolean)this.rotate.getValue() != false);
    ModeSetting focusPlaceType = this.registerMode("Focus Place Type", Arrays.asList("Disabled", "Tick", "Time"), "Disabled", () -> (Boolean)this.strict.getValue());
    BooleanSetting recalculateDamage = this.registerBoolean("Recalculate Damage", true, () -> (Boolean)this.strict.getValue() != false && !((String)this.focusPlaceType.getValue()).equals("Disabled"));
    IntegerSetting tickWaitFocusPlace = this.registerInteger("Tick Wait Focus Pl", 4, 0, 20, () -> (Boolean)this.strict.getValue() != false && ((String)this.focusPlaceType.getValue()).equals("Tick"));
    IntegerSetting timeWaitFocusPlace = this.registerInteger("Time Wait Focus Pl", 100, 0, 2000, () -> (Boolean)this.strict.getValue() != false && ((String)this.focusPlaceType.getValue()).equals("Time"));
    BooleanSetting yawCheck = this.registerBoolean("Yaw Check", false, () -> (Boolean)this.strict.getValue());
    IntegerSetting yawStep = this.registerInteger("Yaw Step", 40, 0, 180, () -> (Boolean)this.strict.getValue() != false && (Boolean)this.yawCheck.getValue() != false);
    BooleanSetting pitchCheck = this.registerBoolean("Pitch Check", false, () -> (Boolean)this.strict.getValue());
    IntegerSetting pitchStep = this.registerInteger("Pitch Step", 40, 0, 180, () -> (Boolean)this.strict.getValue() != false && (Boolean)this.pitchCheck.getValue() != false);
    BooleanSetting placeStrictDirection = this.registerBoolean("Place Strict Predict", false, () -> (Boolean)this.strict.getValue() != false && ((Boolean)this.pitchCheck.getValue() != false || (Boolean)this.yawCheck.getValue() != false));
    BooleanSetting predictBreakRotation = this.registerBoolean("Predict Break Rotation", false, () -> (Boolean)this.strict.getValue() != false && ((Boolean)this.pitchCheck.getValue() != false || (Boolean)this.yawCheck.getValue() != false));
    BooleanSetting blockRotation = this.registerBoolean("Block Rotation", true, () -> (Boolean)this.strict.getValue() != false && ((Boolean)this.pitchCheck.getValue() != false || (Boolean)this.yawCheck.getValue() != false));
    BooleanSetting debugMenu = this.registerBoolean("Debug Section", false);
    BooleanSetting timeCalcPlacement = this.registerBoolean("Calc Placement Time", false, () -> (Boolean)this.debugMenu.getValue());
    BooleanSetting timeCalcBreaking = this.registerBoolean("Calc Breaking Time", false, () -> (Boolean)this.debugMenu.getValue());
    IntegerSetting nCalc = this.registerInteger("N Calc", 100, 1, 1000, () -> (Boolean)this.debugMenu.getValue() != false && ((Boolean)this.timeCalcPlacement.getValue() != false || (Boolean)this.timeCalcBreaking.getValue() != false));
    BooleanSetting debugPredict = this.registerBoolean("Debug Predict", false, () -> (Boolean)this.debugMenu.getValue());
    BooleanSetting showPredictions = this.registerBoolean("Show Predictions", false, () -> (Boolean)this.debugMenu.getValue() != false && (Boolean)this.debugPredict.getValue() != false);
    BooleanSetting hudDisplayShow = this.registerBoolean("Hud Display Section", false);
    BooleanSetting showPlaceName = this.registerBoolean("Show Place Name", false, () -> (Boolean)this.hudDisplayShow.getValue());
    BooleanSetting showPlaceDamage = this.registerBoolean("Show Place Damage", false, () -> (Boolean)this.hudDisplayShow.getValue());
    BooleanSetting showPlaceCrystalsSecond = this.registerBoolean("Show c/s place", false, () -> (Boolean)this.hudDisplayShow.getValue());
    BooleanSetting cleanPlace = this.registerBoolean("Clean Place", true, () -> (Boolean)this.hudDisplayShow.getValue());
    BooleanSetting showBreakName = this.registerBoolean("Show break Name", false, () -> (Boolean)this.hudDisplayShow.getValue());
    BooleanSetting showBreakDamage = this.registerBoolean("Show break Damage", false, () -> (Boolean)this.hudDisplayShow.getValue());
    BooleanSetting showBreakCrystalsSecond = this.registerBoolean("Show c/s break", false, () -> (Boolean)this.hudDisplayShow.getValue());
    BooleanSetting cleanBreak = this.registerBoolean("Clean break", true, () -> (Boolean)this.hudDisplayShow.getValue());
    StringSetting letterIgnoreTerrain = this.registerString("Ignore Terrain", "");
    StringSetting forceFacePlace = this.registerString("Force FacePlace", "");
    StringSetting anvilCity = this.registerString("Anvil City", "");
    IntegerSetting placeAnvil = this.registerInteger("Place Anvil", 10, 0, 100);
    public static boolean stopAC = false;
    boolean checkTimePlace;
    boolean checkTimeBreak;
    boolean placedCrystal;
    boolean brokenCrystal;
    boolean isRotating;
    int oldSlot;
    int tick = 0;
    int tickBeforePlace = 0;
    int tickBeforeBreak;
    int slotChange;
    int tickSwitch;
    int oldSlotBackWeb;
    int oldSlotObby;
    int slotWebBack;
    int highestId = -100000;
    int placeRender;
    int breakRender;
    double xPlayerRotation;
    double yPlayerRotation;
    Timer timerPlace = new Timer();
    Timer timerBreak = new Timer();
    long timePlace = 0L;
    long timeBreak = 0L;
    Vec3d lastHitVec;
    crystalPlaceWait listCrystalsPlaced = new crystalPlaceWait();
    crystalPlaceWait listCrystalsSecondWait = new crystalPlaceWait();
    crystalPlaceWait crystalSecondPlace = new crystalPlaceWait();
    crystalPlaceWait breakPacketLimit = new crystalPlaceWait();
    crystalPlaceWait existsCrystal = new crystalPlaceWait();
    crystalPlaceWait crystalSecondBreak = new crystalPlaceWait();
    crystalPlaceWait attempedCrystalBreak = new crystalPlaceWait();
    managerClassRenderBlocks managerRenderBlocks = new managerClassRenderBlocks();
    crystalPlaced endCrystalPlaced = new crystalPlaced();
    crystalTime crystalPlace = null;
    EntityEnderCrystal forceBreak = null;
    BlockPos forceBreakPlace = null;
    ArrayList<display> toDisplay = new ArrayList();
    ArrayList<Long> durationsPlace = new ArrayList();
    ArrayList<Long> durationsBreaking = new ArrayList();
    ArrayList<packetBlock> packetsBlocks = new ArrayList();
    ArrayList<slowBreakPlayers> listPlayersBreak = new ArrayList();
    ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newCachedThreadPool();
    CrystalInfo.PlaceInfo bestPlace = new CrystalInfo.PlaceInfo(-100.0f, null, null, 100.0);
    CrystalInfo.NewBreakInfo bestBreak = new CrystalInfo.NewBreakInfo(-100.0f, null, null, 100.0);
    float forcePlaceDamage;
    PlayerInfo forcePlaceTarget;
    BlockPos forcePlaceCrystal = null;
    int tickEat = 0;
    boolean isAnvilling = false;
    BlockPos crystalAnvil = null;
    BlockPos blockCity = null;
    Vec3d movingPlaceNow = new Vec3d(-1.0, -1.0, -1.0);
    Vec3d movingBreakNow = new Vec3d(-1.0, -1.0, -1.0);
    BlockPos lastBestPlace = null;
    BlockPos lastBestBreak = null;
    @EventHandler
    private final Listener<DamageBlockEvent> listener = new Listener<DamageBlockEvent>(event -> {
        block12: {
            try {
                if (AutoCrystalRewrite.mc.field_71441_e == null || AutoCrystalRewrite.mc.field_71439_g == null || !((Boolean)this.predictPacketSurround.getValue()).booleanValue()) {
                    return;
                }
                if (!this.canBreak(event.getBlockPos()) || event.getBlockPos() == null) {
                    return;
                }
                BlockPos blockPos = event.getBlockPos();
                if (AutoCrystalRewrite.mc.field_71441_e.func_180495_p(blockPos).func_177230_c() == Blocks.field_150350_a) {
                    return;
                }
                if (this.packetsBlocks.stream().anyMatch(e -> this.sameBlockPos(e.block, blockPos))) {
                    return;
                }
                if (!(blockPos.func_185332_f((int)AutoCrystalRewrite.mc.field_71439_g.field_70165_t, (int)AutoCrystalRewrite.mc.field_71439_g.field_70163_u, (int)AutoCrystalRewrite.mc.field_71439_g.field_70161_v) <= (Double)this.placeRange.getValue())) break block12;
                float armourPercent = (float)((Integer)this.armourFacePlace.getValue()).intValue() / 100.0f;
                for (Vec3i surround : new Vec3i[]{new Vec3i(1, 0, 0), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(0, 0, -1)}) {
                    ArrayList players = new ArrayList(AutoCrystalRewrite.mc.field_71441_e.func_72839_b(null, new AxisAlignedBB(blockPos.func_177971_a(surround))));
                    PlayerInfo info = null;
                    for (Entity pl : players) {
                        if (!(pl instanceof EntityPlayer) || pl == AutoCrystalRewrite.mc.field_71439_g || !(pl.field_70163_u + 0.5 >= (double)blockPos.field_177960_b)) continue;
                        EntityPlayer temp = (EntityPlayer)pl;
                        info = new PlayerInfo(temp, armourPercent, (float)temp.func_70658_aO(), (float)temp.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
                        break;
                    }
                    boolean quit = false;
                    if (info == null) continue;
                    BlockPos coords = null;
                    double damage = Double.MIN_VALUE;
                    Block toReplace = BlockUtil.getBlock(blockPos);
                    AutoCrystalRewrite.mc.field_71441_e.func_175698_g(blockPos);
                    for (Vec3i placement : new Vec3i[]{new Vec3i(1, -1, 0), new Vec3i(-1, -1, 0), new Vec3i(0, -1, 1), new Vec3i(0, -1, -1)}) {
                        BlockPos temp = blockPos.func_177971_a(placement);
                        if (!CrystalUtil.canPlaceCrystal(temp, (Boolean)this.newPlace.getValue()) || (double)DamageUtil.calculateDamage((double)temp.func_177958_n() + 0.5, (double)temp.func_177956_o() + 1.0, (double)temp.func_177952_p() + 0.5, (Entity)AutoCrystalRewrite.mc.field_71439_g, (Boolean)this.ignoreTerrain.getValue()) >= (Double)this.maxSelfDamageSur.getValue()) continue;
                        if (!((Boolean)this.placeOnCrystal.getValue()).booleanValue() && !this.isCrystalHere(temp)) {
                            quit = true;
                            break;
                        }
                        float damagePlayer = DamageUtil.calculateDamageThreaded((double)temp.func_177958_n() + 0.5, (double)temp.func_177956_o() + 1.0, (double)temp.func_177952_p() + 0.5, info, (Boolean)this.ignoreTerrain.getValue());
                        if (!((double)damagePlayer > damage)) continue;
                        damage = damagePlayer;
                        coords = temp;
                        quit = true;
                    }
                    AutoCrystalRewrite.mc.field_71441_e.func_175656_a(blockPos, toReplace.func_176223_P());
                    if (coords != null) {
                        this.packetsBlocks.add(new packetBlock(coords, (Integer)this.tickPacketBreak.getValue(), (Integer)this.tickMaxPacketBreak.getValue()));
                    }
                    if (!quit) {
                        continue;
                    }
                    break;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
        if (event.getPhase() != Phase.PRE || !((Boolean)this.rotate.getValue()).booleanValue() || this.lastHitVec == null || AutoCrystalRewrite.mc.field_71441_e == null || AutoCrystalRewrite.mc.field_71439_g == null) {
            return;
        }
        if (this.tick++ > (Integer)this.tickAfterRotation.getValue()) {
            this.lastHitVec = null;
            this.tick = 0;
            this.isRotating = false;
            this.xPlayerRotation = Double.MAX_VALUE;
            this.yPlayerRotation = Double.MAX_VALUE;
        } else {
            Vec2f nowRotation;
            Vec2f rotationWanted = RotationUtil.getRotationTo(this.lastHitVec);
            if (((Boolean)this.yawCheck.getValue()).booleanValue() || ((Boolean)this.pitchCheck.getValue()).booleanValue()) {
                int direction;
                double distanceDo;
                if (this.yPlayerRotation == Double.MIN_VALUE) {
                    this.yPlayerRotation = rotationWanted.field_189983_j;
                } else {
                    distanceDo = (double)rotationWanted.field_189983_j - this.yPlayerRotation;
                    int n = direction = distanceDo > 0.0 ? 1 : -1;
                    if (Math.abs(distanceDo) > (double)((Integer)this.pitchStep.getValue()).intValue()) {
                        this.yPlayerRotation = RotationUtil.normalizeAngle(this.yPlayerRotation + (double)((Integer)this.pitchStep.getValue() * direction));
                        this.tick = 0;
                    } else {
                        this.yPlayerRotation = rotationWanted.field_189983_j;
                    }
                }
                if (this.xPlayerRotation == Double.MIN_VALUE) {
                    this.xPlayerRotation = rotationWanted.field_189982_i;
                } else {
                    distanceDo = (double)rotationWanted.field_189982_i - this.xPlayerRotation;
                    if (Math.abs(distanceDo) > 180.0) {
                        distanceDo = RotationUtil.normalizeAngle(distanceDo);
                    }
                    int n = direction = distanceDo > 0.0 ? 1 : -1;
                    if (Math.abs(distanceDo) > (double)((Integer)this.yawStep.getValue()).intValue()) {
                        this.xPlayerRotation = RotationUtil.normalizeAngle(this.xPlayerRotation + (double)((Integer)this.yawStep.getValue() * direction));
                        this.tick = 0;
                    } else {
                        this.xPlayerRotation = rotationWanted.field_189982_i;
                    }
                }
                nowRotation = new Vec2f((float)this.xPlayerRotation, (float)this.yPlayerRotation);
            } else {
                nowRotation = rotationWanted;
            }
            PlayerPacket packet = new PlayerPacket((Module)this, nowRotation);
            PlayerPacketManager.INSTANCE.addPacket(packet);
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PacketEvent.Send> packetSendListener = new Listener<PacketEvent.Send>(event -> {
        if (AutoCrystalRewrite.mc.field_71441_e == null || AutoCrystalRewrite.mc.field_71439_g == null) {
            return;
        }
        if (((Boolean)this.entityPredict.getValue()).booleanValue() && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            final CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
            if (this.bestPlace.crystal != null && this.sameBlockPos(packet.func_187023_a(), this.bestPlace.crystal)) {
                int idx = 0;
                int i = 1 - (Integer)this.offset.getValue();
                while (i <= (Integer)this.tryAttack.getValue()) {
                    this.updateHighestID();
                    final java.util.Timer t = new java.util.Timer();
                    final int finalI = i++;
                    t.schedule(new TimerTask(){

                        @Override
                        public void run() {
                            AutoCrystalRewrite.this.attackID(packet.func_187023_a(), AutoCrystalRewrite.this.highestId + finalI);
                            t.cancel();
                        }
                    }, (Integer)this.delayAttacks.getValue() + ++idx * (Integer)this.midDelayAttacks.getValue());
                }
            }
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PacketEvent.Receive> packetReceiveListener = new Listener<PacketEvent.Receive>(event -> {
        try {
            if (AutoCrystalRewrite.mc.field_71441_e == null || AutoCrystalRewrite.mc.field_71439_g == null) {
                return;
            }
            if (event.getPacket() instanceof SPacketSpawnObject) {
                final SPacketSpawnObject SpawnObject = (SPacketSpawnObject)event.getPacket();
                if (((Boolean)this.entityPredict.getValue()).booleanValue()) {
                    this.checkID(SpawnObject.func_149001_c());
                }
                if (SpawnObject.func_148993_l() != 51) return;
                double[] positions = new double[]{SpawnObject.func_186880_c() - 0.5, SpawnObject.func_186882_d() - 0.5, SpawnObject.func_186881_e() - 0.5};
                if (!((String)this.limitPacketPlace.getValue()).equals("None")) {
                    this.listCrystalsPlaced.removeCrystal(positions[0], positions[1], positions[2]);
                }
                if (this.crystalPlace != null && this.sameBlockPos(new BlockPos(positions[0], positions[1], positions[2]), this.crystalPlace.posCrystal)) {
                    this.crystalPlace = null;
                }
                if (((Boolean)this.showPlaceCrystalsSecond.getValue()).booleanValue() && this.listCrystalsSecondWait.removeCrystal(positions[0], positions[1], positions[2])) {
                    this.crystalSecondPlace.addCrystal(null, 1000);
                }
                switch ((String)this.firstHit.getValue()) {
                    case "Tick": {
                        this.existsCrystal.addCrystal(new BlockPos(positions[0], positions[1], positions[2]), 0, (Integer)this.firstHitTick.getValue());
                        break;
                    }
                    case "Time": {
                        this.existsCrystal.addCrystal(new BlockPos(positions[0], positions[1], positions[2]), (Integer)this.fitstHitTime.getValue());
                    }
                }
                if (!((Boolean)this.predictHit.getValue()).booleanValue()) return;
                boolean hit = false;
                switch ((String)this.chooseCrystal.getValue()) {
                    case "All": {
                        hit = true;
                        break;
                    }
                    case "Own": {
                        if (!this.endCrystalPlaced.hasCrystal(new BlockPos(positions[0], positions[1], positions[2]))) break;
                        hit = true;
                        break;
                    }
                    case "Smart": {
                        if (!this.sameBlockPos(this.getTargetPlacing((String)((String)this.targetPlacing.getValue())).crystal, new BlockPos(positions[0], positions[1], positions[2]))) break;
                        hit = true;
                    }
                }
                if (!hit) return;
                final java.util.Timer t = new java.util.Timer();
                t.schedule(new TimerTask(){

                    @Override
                    public void run() {
                        CPacketUseEntity attack = new CPacketUseEntity();
                        ((AccessorCPacketAttack)attack).setId(SpawnObject.func_149001_c());
                        ((AccessorCPacketAttack)attack).setAction(CPacketUseEntity.Action.ATTACK);
                        mc.field_71439_g.field_71174_a.func_147297_a((Packet)attack);
                        mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                        t.cancel();
                    }
                }, ((Integer)this.predictHitDelay.getValue()).intValue());
                return;
            }
            if (event.getPacket() instanceof SPacketSoundEffect) {
                SPacketSoundEffect packetSoundEffect = (SPacketSoundEffect)event.getPacket();
                if (packetSoundEffect.func_186977_b() != SoundCategory.BLOCKS || packetSoundEffect.func_186978_a() != SoundEvents.field_187539_bB) return;
                for (Entity entity : new ArrayList(AutoCrystalRewrite.mc.field_71441_e.field_72996_f)) {
                    int slot;
                    if (!(entity instanceof EntityEnderCrystal)) continue;
                    if (((Boolean)this.setDead.getValue()).booleanValue() && entity.func_70092_e(packetSoundEffect.func_149207_d(), packetSoundEffect.func_149211_e(), packetSoundEffect.func_149210_f()) <= 36.0) {
                        try {
                            entity.func_70106_y();
                        }
                        catch (Exception t) {
                            // empty catch block
                        }
                    }
                    if (this.attempedCrystalBreak.removeCrystal(packetSoundEffect.func_149207_d(), packetSoundEffect.func_149211_e(), packetSoundEffect.func_149210_f())) {
                        this.crystalSecondBreak.addCrystal(null, 1000);
                    }
                    if (this.crystalAnvil == null || !this.sameBlockPos(this.crystalAnvil, new BlockPos(packetSoundEffect.func_149207_d(), packetSoundEffect.func_149211_e(), packetSoundEffect.func_149210_f())) || (slot = InventoryUtil.findFirstBlockSlot(Blocks.field_150467_bQ.getClass(), 0, 8)) == -1 || !(BlockUtil.getBlock(this.blockCity) instanceof BlockAir)) continue;
                    int oldSlot = AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c;
                    AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slot));
                    PlacementUtil.place(this.blockCity, EnumHand.MAIN_HAND, (boolean)((Boolean)this.rotate.getValue()), false);
                    AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(oldSlot));
                }
                this.breakPacketLimit.removeCrystal(packetSoundEffect.func_149207_d(), packetSoundEffect.func_149211_e(), packetSoundEffect.func_149210_f());
                return;
            }
            if (event.getPacket() instanceof SPacketSpawnExperienceOrb) {
                this.checkID(((SPacketSpawnExperienceOrb)event.getPacket()).func_148985_c());
                return;
            } else if (event.getPacket() instanceof SPacketSpawnPlayer) {
                this.checkID(((SPacketSpawnPlayer)event.getPacket()).func_148943_d());
                return;
            } else if (event.getPacket() instanceof SPacketSpawnGlobalEntity) {
                this.checkID(((SPacketSpawnGlobalEntity)event.getPacket()).func_149052_c());
                return;
            } else if (event.getPacket() instanceof SPacketSpawnPainting) {
                this.checkID(((SPacketSpawnPainting)event.getPacket()).func_148965_c());
                return;
            } else {
                if (!(event.getPacket() instanceof SPacketSpawnMob)) return;
                this.checkID(((SPacketSpawnMob)event.getPacket()).func_149024_d());
            }
            return;
        }
        catch (ConcurrentModificationException e) {
            PistonCrystal.printDebug("Prevented a crash from the ca. If this repet, spam me in dm", true);
            Logger LOGGER = LogManager.getLogger((String)"GameSense");
            LOGGER.error("[AutoCrystalRewrite] error during the creation of the structure.");
            if (e.getMessage() != null) {
                LOGGER.error("[AutoCrystalRewrite] error message: " + e.getClass().getName() + " " + e.getMessage());
            } else {
                LOGGER.error("[AutoCrystalRewrite] cannot find the cause");
            }
            boolean i5 = false;
            if (e.getStackTrace().length == 0) return;
            LOGGER.error("[AutoCrystalRewrite] StackTrace Start");
            for (StackTraceElement errorMess : e.getStackTrace()) {
                LOGGER.error("[AutoCrystalRewrite] " + errorMess.toString());
            }
            LOGGER.error("[AutoCrystalRewrite] StackTrace End");
        }
    }, new Predicate[0]);

    @Override
    public void onEnable() {
        this.tick = 0;
        this.tickBeforeBreak = 0;
        this.tickBeforePlace = 0;
        this.timeBreak = 0L;
        this.timePlace = 0L;
        this.breakRender = 0;
        this.placeRender = 0;
        this.oldSlotObby = -1;
        this.slotWebBack = -1;
        this.tickSwitch = -1;
        this.oldSlotBackWeb = -1;
        this.placedCrystal = this.brokenCrystal = this.checkTimeBreak;
        this.checkTimePlace = this.brokenCrystal;
        this.xPlayerRotation = Double.MAX_VALUE;
        this.yPlayerRotation = Double.MAX_VALUE;
        this.forceBreak = null;
        this.forceBreakPlace = null;
        this.lastHitVec = null;
        this.bestPlace = new CrystalInfo.PlaceInfo(-100.0f, null, null, 100.0);
        this.bestBreak = new CrystalInfo.NewBreakInfo(-100.0f, null, null, 100.0);
        this.isAnvilling = false;
        this.isRotating = false;
        stopAC = false;
        this.crystalAnvil = null;
        this.highestId = 0;
        String rickroll = "Never gonna give you up\n            Never gonna let you down\n            Never gonna run around and desert you\n            Never gonna make you cry\n            Never gonna say goodbye\n            Never gonna tell a lie and hurt you";
    }

    @Override
    public void onDisable() {
        this.bestPlace = new CrystalInfo.PlaceInfo(-100.0f, null, null, 100.0);
        this.bestBreak = new CrystalInfo.NewBreakInfo(-100.0f, null, null, 100.0);
        this.movingPlaceNow = new Vec3d(-1.0, -1.0, -1.0);
        this.movingBreakNow = new Vec3d(-1.0, -1.0, -1.0);
    }

    boolean stopGapple(boolean decrease) {
        if (((Boolean)this.stopGapple.getValue()).booleanValue()) {
            Item item;
            if (AutoCrystalRewrite.mc.field_71439_g.func_184587_cr() && ((item = AutoCrystalRewrite.mc.field_71439_g.func_184614_ca().func_77973_b()) == Items.field_151153_ao || item == Items.field_185161_cS || (item = AutoCrystalRewrite.mc.field_71439_g.func_184592_cb().func_77973_b()) == Items.field_151153_ao || item == Items.field_185161_cS)) {
                if (decrease) {
                    this.tickEat = (Integer)this.tickWaitEat.getValue();
                }
                return true;
            }
            if (this.tickEat > 0) {
                if (decrease) {
                    --this.tickEat;
                }
                return true;
            }
        }
        return false;
    }

    void updateCounters() {
        this.listCrystalsPlaced.updateCrystals();
        this.listCrystalsSecondWait.updateCrystals();
        this.crystalSecondPlace.updateCrystals();
        this.endCrystalPlaced.updateCrystals();
        this.existsCrystal.updateCrystals();
        for (int i = 0; i < this.packetsBlocks.size(); ++i) {
            if (this.packetsBlocks.get(i).update()) continue;
            this.packetsBlocks.remove(i);
            --i;
        }
        this.breakPacketLimit.updateCrystals();
        this.listPlayersBreak.removeIf(slowBreakPlayers::update);
        this.crystalSecondBreak.updateCrystals();
        this.attempedCrystalBreak.updateCrystals();
        this.managerRenderBlocks.update((Integer)this.lifeTime.getValue());
    }

    @Override
    public void onUpdate() {
        block20: {
            if (AutoCrystalRewrite.mc.field_71441_e == null || AutoCrystalRewrite.mc.field_71439_g == null || AutoCrystalRewrite.mc.field_71439_g.field_70128_L || stopAC) {
                return;
            }
            this.toDisplay.clear();
            this.updateCounters();
            if (((Boolean)this.entityPredict.getValue()).booleanValue()) {
                this.updateHighestID();
            }
            if (this.stopGapple(true)) {
                return;
            }
            try {
                switch ((String)this.logic.getValue()) {
                    case "Place->Break": {
                        if (this.placeCrystals() && ((Boolean)this.oneStop.getValue()).booleanValue()) break;
                        this.breakCrystals();
                        break;
                    }
                    case "Break->Place": {
                        if (this.breakCrystals() && ((Boolean)this.oneStop.getValue()).booleanValue()) break;
                        this.placeCrystals();
                        break;
                    }
                    case "Place": {
                        this.placeCrystals();
                        if (this.bestBreak.crystal == null) break;
                        this.bestBreak = new CrystalInfo.NewBreakInfo(0.0f, null, null, 0.0);
                        break;
                    }
                    case "Break": {
                        this.breakCrystals();
                        if (this.bestPlace.crystal == null) break;
                        this.bestPlace = new CrystalInfo.PlaceInfo(0.0f, null, null, 0.0);
                    }
                }
            }
            catch (Exception e) {
                PistonCrystal.printDebug("Prevented a crash from the ca. If this repet, spam me in dm", true);
                Logger LOGGER = LogManager.getLogger((String)"GameSense");
                LOGGER.error("[AutoCrystalRewrite] error during the creation of the structure.");
                if (e.getMessage() != null) {
                    LOGGER.error("[AutoCrystalRewrite] error message: " + e.getClass().getName() + " " + e.getMessage());
                } else {
                    LOGGER.error("[AutoCrystalRewrite] cannot find the cause");
                }
                boolean i5 = false;
                if (e.getStackTrace().length == 0) break block20;
                LOGGER.error("[AutoCrystalRewrite] StackTrace Start");
                for (StackTraceElement errorMess : e.getStackTrace()) {
                    LOGGER.error("[AutoCrystalRewrite] " + errorMess.toString());
                }
                LOGGER.error("[AutoCrystalRewrite] StackTrace End");
            }
        }
        this.oldSlot = AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c;
        PlacementUtil.stopSneaking();
    }

    @Override
    public String getHudInfo() {
        int temp;
        StringBuilder t = new StringBuilder();
        boolean place = false;
        if (this.bestPlace.target != null) {
            if (((Boolean)this.showPlaceName.getValue()).booleanValue()) {
                t.append(ChatFormatting.GRAY + "[").append(ChatFormatting.WHITE + ((Boolean)this.cleanPlace.getValue() == false ? "Place Name: " : "")).append(this.bestPlace.target.entity.func_70005_c_());
                place = true;
            }
            if (((Boolean)this.showPlaceDamage.getValue()).booleanValue()) {
                if (!place) {
                    t.append(ChatFormatting.GRAY + "[").append(ChatFormatting.WHITE + ((Boolean)this.cleanPlace.getValue() == false ? "Place damage: " : "")).append((int)this.bestPlace.damage);
                    place = true;
                } else {
                    t.append((Boolean)this.cleanPlace.getValue() == false ? " Damage: " : " ").append((int)this.bestPlace.damage);
                }
            }
        }
        if (((Boolean)this.showPlaceCrystalsSecond.getValue()).booleanValue() && (temp = this.crystalSecondPlace.countCrystals()) > 0) {
            if (!place) {
                t.append(ChatFormatting.GRAY + "[").append(ChatFormatting.WHITE + ((Boolean)this.cleanPlace.getValue() != false ? "Place c/s: " : "")).append(temp);
                place = true;
            } else {
                t.append((Boolean)this.cleanPlace.getValue() != false ? " c/s: " : " ").append(temp);
            }
        }
        if (this.bestBreak.target != null) {
            if (((Boolean)this.showBreakName.getValue()).booleanValue()) {
                if (!place) {
                    t.append(ChatFormatting.GRAY + "[").append(ChatFormatting.WHITE + ((Boolean)this.cleanBreak.getValue() == false ? "Break Name: " : "")).append(this.bestBreak.target.entity.func_70005_c_());
                    place = true;
                } else {
                    t.append((Boolean)this.cleanPlace.getValue() == false ? " Name: " : " ").append(this.bestBreak.target.entity.func_70005_c_());
                }
            }
            if (((Boolean)this.showBreakDamage.getValue()).booleanValue()) {
                if (!place) {
                    t.append(ChatFormatting.GRAY + "[").append(ChatFormatting.WHITE + ((Boolean)this.cleanBreak.getValue() == false ? "Break damage: " : "")).append((int)this.bestBreak.damage);
                    place = true;
                } else {
                    t.append((Boolean)this.cleanPlace.getValue() == false ? " Damage: " : " ").append((int)this.bestBreak.damage);
                }
            }
        }
        if (((Boolean)this.showBreakCrystalsSecond.getValue()).booleanValue() && (temp = this.crystalSecondBreak.countCrystals()) > 0) {
            if (!place) {
                t.append(ChatFormatting.GRAY + "[").append(ChatFormatting.WHITE + ((Boolean)this.cleanBreak.getValue() != false ? "Break b/s: " : "")).append(temp);
                place = true;
            } else {
                t.append((Boolean)this.cleanPlace.getValue() != false ? " b/s: " : " ").append(temp);
            }
        }
        if (place) {
            t.append(ChatFormatting.GRAY + "]");
        }
        return t.toString();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    CrystalInfo.PlaceInfo getTargetPlacing(String mode) {
        if (this.anvilCity.getText().length() > 0) {
            if (Keyboard.isKeyDown((int)KeyBoardClass.getKeyFromChar(this.anvilCity.getText().charAt(0))) && this.bestBreak.damage > 5.0f) {
                int slot;
                if (this.crystalAnvil != null && this.blockCity != null && BlockUtil.getBlock(this.blockCity) instanceof BlockAir && (slot = InventoryUtil.findFirstBlockSlot(Blocks.field_150467_bQ.getClass(), 0, 8)) != -1) {
                    int oldSlot = AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c;
                    AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slot));
                    PlacementUtil.place(this.blockCity, EnumHand.MAIN_HAND, (boolean)((Boolean)this.rotate.getValue()), false);
                    AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(oldSlot));
                }
                if (this.crystalAnvil != null) {
                    if (!AutoCrystalRewrite.mc.field_71441_e.func_72910_y().stream().filter(e -> e instanceof EntityEnderCrystal && e.func_180425_c() == this.crystalAnvil).findAny().isPresent()) return new CrystalInfo.PlaceInfo(8.0f, null, this.crystalAnvil, 6.0);
                    this.crystalAnvil = null;
                }
            } else {
                this.isAnvilling = false;
                this.crystalAnvil = null;
            }
        } else {
            this.isAnvilling = false;
            this.crystalAnvil = null;
        }
        PredictUtil.PredictSettings settings = new PredictUtil.PredictSettings((Integer)this.tickPredict.getValue(), (Boolean)this.calculateYPredict.getValue(), (Integer)this.startDecrease.getValue(), (Integer)this.exponentStartDecrease.getValue(), (Integer)this.decreaseY.getValue(), (Integer)this.exponentDecreaseY.getValue(), (Integer)this.increaseY.getValue(), (Integer)this.exponentIncreaseY.getValue(), (Boolean)this.splitXZ.getValue(), (Integer)this.widthPredict.getValue(), (Boolean)this.debugPredict.getValue(), (Boolean)this.showPredictions.getValue(), (Boolean)this.manualOutHole.getValue(), (Boolean)this.aboveHoleManual.getValue(), (Boolean)this.stairPredict.getValue(), (Integer)this.nStair.getValue(), (Double)this.speedActivationStair.getValue());
        int nThread = (Integer)this.nThread.getValue();
        float armourPercent = (float)((Integer)this.armourFacePlace.getValue()).intValue() / 100.0f;
        double minDamage = (Double)this.minDamagePlace.getValue();
        double minFacePlaceDamage = (Double)this.minFacePlaceDmg.getValue();
        double minFacePlaceHp = ((Integer)this.facePlaceValue.getValue()).intValue();
        if (this.forceFacePlace.getText().length() > 0 && Keyboard.isKeyDown((int)KeyBoardClass.getKeyFromChar(this.forceFacePlace.getText().charAt(0)))) {
            minFacePlaceHp = 36.0;
        }
        double enemyRangeSQ = (Double)this.rangeEnemyPlace.getValue() * (Double)this.rangeEnemyPlace.getValue();
        double maxSelfDamage = (Double)this.maxSelfDamagePlace.getValue();
        double wallRangePlaceSQ = (Double)this.crystalWallPlace.getValue() * (Double)this.crystalWallPlace.getValue();
        boolean raytraceValue = (Boolean)this.raytrace.getValue();
        int maxYTarget = (Integer)this.maxYTarget.getValue();
        int minYTarget = (Integer)this.minYTarget.getValue();
        int placeTimeout = (Integer)this.placeTimeout.getValue();
        boolean ignoreTerrainValue = false;
        if (((Boolean)this.ignoreTerrain.getValue()).booleanValue()) {
            if (((Boolean)this.bindIgnoreTerrain.getValue()).booleanValue()) {
                if (this.letterIgnoreTerrain.getText().length() > 0 && Keyboard.isKeyDown((int)KeyBoardClass.getKeyFromChar(this.letterIgnoreTerrain.getText().charAt(0)))) {
                    ignoreTerrainValue = true;
                }
            } else {
                ignoreTerrainValue = true;
            }
        }
        boolean relativeDamage = (Boolean)this.relativeDamagePlace.getValue();
        double valueRelativeDamage = (Double)this.relativeDamageValuePlace.getValue();
        CrystalInfo.PlaceInfo bestPlace = new CrystalInfo.PlaceInfo(-100.0f, null, null, 100.0);
        ArrayList<BlockPos> webRemoved = new ArrayList<BlockPos>();
        block5 : switch (mode) {
            case "Lowest": 
            case "Nearest": {
                List<List<PositionInfo>> possibleCrystals;
                EntityPlayer targetEP;
                EntityPlayer entityPlayer = targetEP = mode.equals("Lowest") ? (EntityPlayer)this.getBasicPlayers(enemyRangeSQ).min((x, y) -> (int)x.func_110143_aJ()).orElse(null) : (EntityPlayer)this.getBasicPlayers(enemyRangeSQ).min(Comparator.comparingDouble(x -> x.func_70068_e((Entity)AutoCrystalRewrite.mc.field_71439_g))).orElse(null);
                if (targetEP == null) break;
                if (BlockUtil.getBlock(targetEP.field_70165_t, targetEP.field_70163_u, targetEP.field_70161_v) instanceof BlockWeb) {
                    AutoCrystalRewrite.mc.field_71441_e.func_175698_g(new BlockPos(targetEP.field_70165_t, targetEP.field_70163_u, targetEP.field_70161_v));
                    webRemoved.add(new BlockPos(targetEP.field_70165_t, targetEP.field_70163_u, targetEP.field_70161_v));
                }
                PlayerInfo player = new PlayerInfo((EntityPlayer)((Boolean)this.predictSelfPlace.getValue() != false ? PredictUtil.predictPlayer((EntityPlayer)AutoCrystalRewrite.mc.field_71439_g, settings) : AutoCrystalRewrite.mc.field_71439_g), false, (float)AutoCrystalRewrite.mc.field_71439_g.func_70658_aO(), (float)AutoCrystalRewrite.mc.field_71439_g.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
                if (((Boolean)this.predictSelfPlace.getValue()).booleanValue() && ((Boolean)this.showSelfPredictPlace.getValue()).booleanValue()) {
                    this.toDisplay.add(new display(player.entity.func_174813_aQ(), this.colorSelfPlace.getColor(), (Integer)this.widthPredict.getValue()));
                }
                if ((possibleCrystals = this.getPossibleCrystalsPlacing(player, maxSelfDamage, raytraceValue, wallRangePlaceSQ, ignoreTerrainValue)) == null) break;
                PlayerInfo target = new PlayerInfo(targetEP, armourPercent, (float)targetEP.func_70658_aO(), (float)targetEP.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
                bestPlace = this.calcualteBestPlace(nThread, possibleCrystals, player.entity.field_70165_t, player.entity.field_70163_u, player.entity.field_70161_v, target, minDamage, minFacePlaceHp, minFacePlaceDamage, maxSelfDamage, maxYTarget, minYTarget, placeTimeout, new CrystalInfo.PlaceInfo(0.0f, null, null, 0.0), ignoreTerrainValue, relativeDamage, valueRelativeDamage);
                break;
            }
            case "Damage": {
                PlayerInfo target;
                List<List<PositionInfo>> possibleCrystals;
                List<EntityPlayer> players = this.getBasicPlayers(enemyRangeSQ).sorted(new Sortbyroll()).collect(Collectors.toList());
                if (players.size() == 0) break;
                for (EntityPlayer et : players) {
                    if (!(BlockUtil.getBlock(et.field_70165_t, et.field_70163_u, et.field_70161_v) instanceof BlockWeb)) continue;
                    AutoCrystalRewrite.mc.field_71441_e.func_175698_g(new BlockPos(et.field_70165_t, et.field_70163_u, et.field_70161_v));
                    webRemoved.add(new BlockPos(et.field_70165_t, et.field_70163_u, et.field_70161_v));
                }
                if (((Boolean)this.predictPlaceEnemy.getValue()).booleanValue()) {
                    players = this.getPlayersThreaded(nThread, players, settings, (Integer)this.predictPlaceTimeout.getValue());
                }
                PlayerInfo player = new PlayerInfo((EntityPlayer)((Boolean)this.predictSelfPlace.getValue() != false ? PredictUtil.predictPlayer((EntityPlayer)AutoCrystalRewrite.mc.field_71439_g, settings) : AutoCrystalRewrite.mc.field_71439_g), false, (float)AutoCrystalRewrite.mc.field_71439_g.func_70658_aO(), (float)AutoCrystalRewrite.mc.field_71439_g.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
                if (((Boolean)this.predictSelfPlace.getValue()).booleanValue() && ((Boolean)this.showSelfPredictPlace.getValue()).booleanValue()) {
                    this.toDisplay.add(new display(player.entity.func_174813_aQ(), this.colorSelfPlace.getColor(), (Integer)this.widthPredict.getValue()));
                }
                if ((possibleCrystals = this.getPossibleCrystalsPlacing(player, maxSelfDamage, raytraceValue, wallRangePlaceSQ, ignoreTerrainValue)) == null) break;
                int count = 0;
                for (EntityPlayer playerTemp : players) {
                    if (count++ >= (Integer)this.maxTarget.getValue()) break block5;
                    target = new PlayerInfo(playerTemp, armourPercent, (float)playerTemp.func_70658_aO(), (float)playerTemp.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
                    bestPlace = this.calcualteBestPlace(nThread, possibleCrystals, player.entity.field_70165_t, player.entity.field_70163_u, player.entity.field_70161_v, target, minDamage, minFacePlaceHp, minFacePlaceDamage, maxSelfDamage, maxYTarget, minYTarget, placeTimeout, bestPlace, ignoreTerrainValue, relativeDamage, valueRelativeDamage);
                }
                break;
            }
        }
        for (BlockPos web : webRemoved) {
            AutoCrystalRewrite.mc.field_71441_e.func_175656_a(web, Blocks.field_150321_G.func_176223_P());
        }
        if (bestPlace.target == null) return bestPlace;
        this.placeRender = 0;
        return bestPlace;
    }

    CrystalInfo.PlaceInfo calcualteBestPlace(int nThread, List<List<PositionInfo>> possibleCrystals, double posX, double posY, double posZ, PlayerInfo target, double minDamage, double minFacePlaceHp, double minFacePlaceDamage, double maxSelfDamage, int maxYTarget, int minYTarget, int placeTimeout, CrystalInfo.PlaceInfo old, boolean ignoreTerrain, boolean relativeDamage, double valueRelativeDamage) {
        LinkedList<Future<CrystalInfo.PlaceInfo>> futures = new LinkedList<Future<CrystalInfo.PlaceInfo>>();
        int i = 0;
        while (i < nThread) {
            int finalI = i++;
            futures.add(this.executor.submit(() -> this.calculateBestPlaceTarget((List)possibleCrystals.get(finalI), posX, posY, posZ, target, minDamage, minFacePlaceHp, minFacePlaceDamage, maxSelfDamage, maxYTarget, minYTarget, ignoreTerrain, relativeDamage, valueRelativeDamage)));
        }
        Stack<CrystalInfo.PlaceInfo> results = new Stack<CrystalInfo.PlaceInfo>();
        for (Future future : futures) {
            try {
                CrystalInfo.PlaceInfo temp = (CrystalInfo.PlaceInfo)future.get(placeTimeout, TimeUnit.MILLISECONDS);
                if (temp == null) continue;
                results.add(temp);
            }
            catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }
        results.add(old);
        return this.getResultPlace(results);
    }

    CrystalInfo.PlaceInfo getResultPlace(Stack<CrystalInfo.PlaceInfo> result) {
        CrystalInfo.PlaceInfo returnValue = new CrystalInfo.PlaceInfo(0.0f, null, null, 100.0);
        while (!result.isEmpty()) {
            CrystalInfo.PlaceInfo now = result.pop();
            if (now.damage == returnValue.damage) {
                if (!(now.distance < returnValue.distance)) continue;
                returnValue = now;
                continue;
            }
            if (!(now.damage > returnValue.damage)) continue;
            returnValue = now;
        }
        return returnValue;
    }

    List<List<PositionInfo>> getPossibleCrystalsPlacing(PlayerInfo self, double maxSelfDamage, boolean raytrace, double wallRangeSQ, boolean ignoreTerrain) {
        ArrayList<PositionInfo> damagePos = new ArrayList<PositionInfo>();
        ((Boolean)this.includeCrystalMapping.getValue() != false ? CrystalUtil.findCrystalBlocksExcludingCrystals(((Double)this.placeRange.getValue()).floatValue(), (Boolean)this.newPlace.getValue()) : CrystalUtil.findCrystalBlocks(((Double)this.placeRange.getValue()).floatValue(), (Boolean)this.newPlace.getValue())).forEach(crystal -> {
            RayTraceResult result;
            float damage = DamageUtil.calculateDamageThreaded((double)crystal.func_177958_n() + 0.5, (double)crystal.func_177956_o() + 1.0, (double)crystal.func_177952_p() + 0.5, self, ignoreTerrain);
            if ((double)damage < maxSelfDamage && (!((Boolean)this.antiSuicidepl.getValue()).booleanValue() || damage < self.health) && ((result = AutoCrystalRewrite.mc.field_71441_e.func_72933_a(new Vec3d(AutoCrystalRewrite.mc.field_71439_g.field_70165_t, AutoCrystalRewrite.mc.field_71439_g.field_70163_u + (double)AutoCrystalRewrite.mc.field_71439_g.func_70047_e(), AutoCrystalRewrite.mc.field_71439_g.field_70161_v), new Vec3d((double)crystal.func_177958_n() + 0.5, (double)crystal.func_177956_o() + 1.0, (double)crystal.func_177952_p() + 0.5))) == null || this.sameBlockPos(result.func_178782_a(), (BlockPos)crystal) || !raytrace && AutoCrystalRewrite.mc.field_71439_g.func_174818_b(crystal) <= wallRangeSQ)) {
                damagePos.add(new PositionInfo((BlockPos)crystal, (double)damage));
            }
        });
        return this.splitList(damagePos, (Integer)this.nThread.getValue());
    }

    List<List<PositionInfo>> splitList(final List<PositionInfo> start, int nThreads) {
        int i;
        if (nThreads == 1) {
            return new ArrayList<List<PositionInfo>>(){
                {
                    this.add(start);
                }
            };
        }
        int count = start.size();
        if (count == 0) {
            return null;
        }
        ArrayList<List<PositionInfo>> output = new ArrayList<List<PositionInfo>>(nThreads);
        for (i = 0; i < nThreads; ++i) {
            output.add(new ArrayList());
        }
        for (i = 0; i < count; ++i) {
            ((List)output.get(i % nThreads)).add(start.get(i));
        }
        return output;
    }

    CrystalInfo.PlaceInfo calculateBestPlaceTarget(List<PositionInfo> possibleLocations, double x, double y, double z, PlayerInfo target, double minDamage, double minFacePlaceHealth, double minFacePlaceDamage, double maxSelfDamage, int maxYTarget, int minYTarget, boolean ignoreTerrain, boolean relativeDamage, double valueRelativeDamage) {
        PositionInfo best = new PositionInfo();
        for (PositionInfo crystal : possibleLocations) {
            double d;
            double temp = target.entity.field_70163_u - (double)crystal.pos.func_177956_o() - 1.0;
            if (!(d > 0.0) ? temp < (double)(-maxYTarget) : temp > (double)minYTarget) continue;
            float currentDamage = DamageUtil.calculateDamageThreaded((double)crystal.pos.func_177958_n() + 0.5, (double)crystal.pos.func_177956_o() + 1.0, (double)crystal.pos.func_177952_p() + 0.5, target, ignoreTerrain);
            if ((double)currentDamage == best.damage) {
                if (best.pos != null && (temp = crystal.pos.func_177954_c(x, y, z)) != best.distance && !((double)currentDamage / maxSelfDamage > best.rapp) && !(temp < best.distance)) continue;
                best = crystal;
                best.setEnemyDamage(currentDamage);
                best.distance = target.entity.func_70092_e((double)crystal.pos.func_177958_n() + 0.5, (double)crystal.pos.func_177956_o() + 1.0, (double)crystal.pos.func_177952_p() + 0.5);
                best.distancePlayer = AutoCrystalRewrite.mc.field_71439_g.func_70092_e((double)crystal.pos.func_177958_n() + 0.5, (double)crystal.pos.func_177956_o() + 1.0, (double)crystal.pos.func_177952_p() + 0.5);
                continue;
            }
            if (!((double)currentDamage > best.damage) || relativeDamage && crystal.getSelfDamage() / (double)currentDamage > valueRelativeDamage) continue;
            best = crystal;
            best.setEnemyDamage(currentDamage);
            best.distance = target.entity.func_70092_e((double)crystal.pos.func_177958_n() + 0.5, (double)crystal.pos.func_177956_o() + 1.0, (double)crystal.pos.func_177952_p() + 0.5);
            best.distancePlayer = AutoCrystalRewrite.mc.field_71439_g.func_70092_e((double)crystal.pos.func_177958_n() + 0.5, (double)crystal.pos.func_177956_o() + 1.0, (double)crystal.pos.func_177952_p() + 0.5);
        }
        if (best.pos != null && (best.damage >= minDamage || ((double)target.health <= minFacePlaceHealth || target.lowArmour) && best.damage >= minFacePlaceDamage)) {
            return new CrystalInfo.PlaceInfo((float)best.damage, target, best.pos, best.distancePlayer);
        }
        return null;
    }

    boolean canStartPlacing() {
        switch ((String)this.placeDelay.getValue()) {
            case "Tick": {
                if (this.tickBeforePlace == 0) {
                    return true;
                }
                --this.tickBeforePlace;
                break;
            }
            case "Time": {
                if (!this.checkTimePlace) {
                    return true;
                }
                if (System.currentTimeMillis() - this.timePlace < (long)((Integer)this.timeDelayPlace.getValue()).intValue()) break;
                this.checkTimePlace = false;
                return true;
            }
            case "Vanilla": {
                if (this.timerPlace.getTimePassed() / 50L < (long)(20 - (Integer)this.vanillaSpeedPlace.getValue())) break;
                this.timerPlace.reset();
                return true;
            }
        }
        return false;
    }

    boolean placeCrystals() {
        if (this.placedCrystal) {
            this.placedCrystal = false;
            return false;
        }
        if (!this.canStartPlacing()) {
            return false;
        }
        EnumHand hand = this.getHandCrystal();
        if (hand == null) {
            return false;
        }
        if (this.crystalPlace != null) {
            RayTraceResult result;
            if (((Boolean)this.raytrace.getValue()).booleanValue() && ((result = AutoCrystalRewrite.mc.field_71441_e.func_72933_a(new Vec3d(AutoCrystalRewrite.mc.field_71439_g.field_70165_t, AutoCrystalRewrite.mc.field_71439_g.field_70163_u + (double)AutoCrystalRewrite.mc.field_71439_g.func_70047_e(), AutoCrystalRewrite.mc.field_71439_g.field_70161_v), new Vec3d((double)this.crystalPlace.posCrystal.func_177958_n() + 0.5, (double)this.crystalPlace.posCrystal.func_177956_o() + 0.5, (double)this.crystalPlace.posCrystal.func_177952_p() + 0.5))) == null || result.field_178784_b == null)) {
                this.crystalPlace = null;
            }
            if (this.crystalPlace != null && ((Boolean)this.recalculateDamage.getValue()).booleanValue() && this.isCrystalGood(this.crystalPlace.posCrystal) == null) {
                this.crystalPlace = null;
            }
            if (this.crystalPlace != null && this.crystalPlace.posCrystal != null) {
                if (this.crystalPlace.isReady()) {
                    this.crystalPlace = null;
                } else {
                    if (this.isPlacingWeb()) {
                        return true;
                    }
                    if (this.crystalPlace != null) {
                        return this.placeCrystal(this.crystalPlace.posCrystal, hand, false);
                    }
                }
            }
        }
        long inizio = 0L;
        if (((Boolean)this.timeCalcPlacement.getValue()).booleanValue()) {
            inizio = System.currentTimeMillis();
        }
        boolean instaPlaceBol = false;
        if (this.forcePlaceCrystal != null && ((Boolean)this.forcePlace.getValue()).booleanValue()) {
            this.bestPlace = new CrystalInfo.PlaceInfo(this.forcePlaceDamage, this.forcePlaceTarget, this.forcePlaceCrystal, -10.0);
            this.placeRender = 0;
            instaPlaceBol = true;
        } else {
            this.bestPlace = this.getTargetPlacing((String)this.targetPlacing.getValue());
            if (this.forcePlaceCrystal != null && this.bestPlace.crystal != null && this.sameBlockPos(this.forcePlaceCrystal, this.bestPlace.crystal)) {
                instaPlaceBol = true;
            }
            this.placeRender = 0;
        }
        if (((Boolean)this.timeCalcPlacement.getValue()).booleanValue()) {
            long fine = System.currentTimeMillis();
            this.durationsPlace.add(fine - inizio);
            if (this.durationsPlace.size() > (Integer)this.nCalc.getValue()) {
                double sum = this.durationsPlace.stream().mapToDouble(a -> a.longValue()).sum();
                this.durationsPlace.clear();
                PistonCrystal.printDebug(String.format("N: %d Value: %f", this.nCalc.getValue(), sum /= (double)((Integer)this.nCalc.getValue()).intValue()), false);
            }
        }
        if (((Boolean)this.instaPlace.getValue()).booleanValue() && this.bestPlace.target == null && this.forcePlaceCrystal != null) {
            this.bestPlace = new CrystalInfo.PlaceInfo(this.forcePlaceDamage, this.forcePlaceTarget, this.forcePlaceCrystal, -10.0);
            instaPlaceBol = true;
            this.placeRender = 0;
        }
        this.forcePlaceCrystal = null;
        if (this.bestPlace.crystal != null) {
            if (((Boolean)this.showTextpl.getValue()).booleanValue()) {
                this.toDisplay.add(new display(String.valueOf((int)this.bestPlace.damage), this.bestPlace.crystal, this.colorPlaceText.getValue(), (Double)this.textYPlace.getValue()));
            }
            if (((Boolean)this.predictPlaceEnemy.getValue()).booleanValue()) {
                this.toDisplay.add(new display(this.bestPlace.getTarget().func_174813_aQ(), this.showColorPredictEnemyPlace.getColor(), (Integer)this.outlineWidthpl.getValue()));
            }
            if (this.isPlacingWeb()) {
                return true;
            }
            return this.placeCrystal(this.bestPlace.crystal, hand, instaPlaceBol);
        }
        if (((Boolean)this.switchBack.getValue()).booleanValue() && this.oldSlotObby != -1) {
            if (this.tickSwitch > 0) {
                --this.tickSwitch;
            } else if (this.tickSwitch == 0) {
                AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlotObby;
                this.tickSwitch = -1;
            }
        }
        return false;
    }

    boolean isPlacingWeb() {
        if (((Boolean)this.autoWeb.getValue()).booleanValue() && this.bestPlace != null && this.bestPlace.target != null && (!((Boolean)this.onlyAutoWebActive.getValue()).booleanValue() || ModuleManager.isModuleEnabled(AutoWeb.class))) {
            if (BlockUtil.getBlock(this.bestPlace.getTarget().field_70165_t, this.bestPlace.getTarget().field_70163_u, this.bestPlace.getTarget().field_70161_v) instanceof BlockAir && this.placeWeb(new BlockPos(this.bestPlace.getTarget().field_70165_t, this.bestPlace.getTarget().field_70163_u, this.bestPlace.getTarget().field_70161_v)) && ((Boolean)this.stopCrystal.getValue()).booleanValue()) {
                return true;
            }
        } else if (this.oldSlotBackWeb != -1) {
            AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlotBackWeb;
            this.oldSlotBackWeb = -1;
        }
        return false;
    }

    boolean placeWeb(BlockPos target) {
        EnumFacing side = BlockUtil.getPlaceableSide(target);
        if (side == null) {
            return false;
        }
        BlockPos neighbour = target.func_177972_a(side);
        EnumFacing opposite = side.func_176734_d();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }
        int oldSlot = -1;
        if (!(AutoCrystalRewrite.mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() instanceof ItemBlock) || ((ItemBlock)AutoCrystalRewrite.mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b()).func_179223_d() != Blocks.field_150321_G) {
            int slot = InventoryUtil.findFirstBlockSlot(Blocks.field_150321_G.getClass(), 0, 8);
            oldSlot = AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c;
            if (slot == -1) {
                return false;
            }
            if (((Boolean)this.silentSwitchWeb.getValue()).booleanValue()) {
                AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slot));
                AutoCrystalRewrite.mc.field_71442_b.func_78765_e();
            } else if (((Boolean)this.switchBackEnd.getValue()).booleanValue()) {
                this.oldSlotBackWeb = oldSlot;
                AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c = slot;
                oldSlot = -1;
            } else if (((Boolean)this.switchBackWeb.getValue()).booleanValue()) {
                AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c = slot;
            } else if (((Boolean)this.switchWeb.getValue()).booleanValue()) {
                AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c = slot;
                oldSlot = -1;
            } else {
                return false;
            }
        }
        Vec3d hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.5, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        Block neighbourBlock = AutoCrystalRewrite.mc.field_71441_e.func_180495_p(neighbour).func_177230_c();
        if (((Boolean)this.preRotate.getValue()).booleanValue()) {
            BlockUtil.faceVectorPacketInstant(hitVec, true);
        }
        if (((Boolean)this.focusWebRotate.getValue()).booleanValue()) {
            this.lastHitVec = hitVec;
            this.tick = 0;
        }
        boolean isSneaking = false;
        if (BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)AutoCrystalRewrite.mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }
        AutoCrystalRewrite.mc.field_71442_b.func_187099_a(AutoCrystalRewrite.mc.field_71439_g, AutoCrystalRewrite.mc.field_71441_e, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        AutoCrystalRewrite.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
        if (isSneaking) {
            AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)AutoCrystalRewrite.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        if (oldSlot != -1) {
            if (((Boolean)this.silentSwitchWeb.getValue()).booleanValue()) {
                AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(oldSlot));
            } else {
                AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c = oldSlot;
            }
        }
        AutoCrystalRewrite.mc.field_71442_b.func_78765_e();
        return true;
    }

    EntityPlayer isCrystalGood(BlockPos crystal) {
        float f;
        float damage = DamageUtil.calculateDamage((double)crystal.func_177958_n() + 0.5, (double)crystal.func_177956_o() + 1.0, (double)crystal.func_177952_p() + 0.5, (Entity)AutoCrystalRewrite.mc.field_71439_g, (Boolean)this.ignoreTerrain.getValue());
        if ((double)f >= (Double)this.maxSelfDamagePlace.getValue() && (!((Boolean)this.antiSuicidepl.getValue()).booleanValue() || damage < PlayerUtil.getHealth())) {
            return null;
        }
        double rangeSQ = (Double)this.rangeEnemyPlace.getValue() * (Double)this.rangeEnemyPlace.getValue();
        Optional<EntityPlayer> a = AutoCrystalRewrite.mc.field_71441_e.field_73010_i.stream().filter(entity -> !EntityUtil.basicChecksEntity((Entity)entity)).filter(entity -> entity.func_110143_aJ() > 0.0f).filter(entity -> AutoCrystalRewrite.mc.field_71439_g.func_70068_e((Entity)entity) <= rangeSQ).filter(entity -> (double)DamageUtil.calculateDamage((double)crystal.func_177958_n() + 0.5, (double)crystal.func_177956_o() + 1.0, (double)crystal.func_177952_p() + 0.5, (Entity)entity, (Boolean)this.ignoreTerrain.getValue()) >= (Double)this.minDamagePlace.getValue()).findAny();
        return a.orElse(null);
    }

    EnumHand getHandCrystal() {
        int slot;
        this.slotChange = -1;
        if (AutoCrystalRewrite.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemEndCrystal) {
            return EnumHand.OFF_HAND;
        }
        if (AutoCrystalRewrite.mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemEndCrystal) {
            return EnumHand.MAIN_HAND;
        }
        if (((Boolean)this.switchHotbar.getValue()).booleanValue() && (slot = InventoryUtil.findFirstItemSlot(ItemEndCrystal.class, 0, 8)) != -1) {
            if (((Boolean)this.waitGappleSwitch.getValue()).booleanValue() && AutoCrystalRewrite.mc.field_71439_g.field_71071_by.func_70301_a(AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c).func_77973_b() == Items.field_151153_ao && this.oldSlot == slot) {
                return null;
            }
            this.slotChange = slot;
            return EnumHand.MAIN_HAND;
        }
        return null;
    }

    boolean placeCrystal(BlockPos pos, EnumHand handSwing, boolean instaPlace) {
        if (!(((Boolean)this.placeOnCrystal.getValue()).booleanValue() || this.isCrystalHere(pos) || instaPlace)) {
            return false;
        }
        BlockPos posUp = pos.func_177984_a();
        AxisAlignedBB box = new AxisAlignedBB((double)posUp.func_177958_n(), (double)posUp.func_177956_o(), (double)posUp.func_177952_p(), (double)posUp.func_177958_n() + 1.0, (double)posUp.func_177956_o() + 2.0, (double)posUp.func_177952_p() + 1.0);
        List a = AutoCrystalRewrite.mc.field_71441_e.func_175647_a(Entity.class, box, entity -> entity instanceof EntityEnderCrystal && !this.sameBlockPos(entity.func_180425_c().func_177982_a(0, -1, 0), pos));
        if (a.size() > 0) {
            if (((Boolean)this.breakNearCrystal.getValue()).booleanValue()) {
                this.forceBreak = (EntityEnderCrystal)a.get(0);
                this.forceBreakPlace = pos;
            }
            return false;
        }
        if (this.listCrystalsPlaced.CrystalExists(pos) != -1) {
            return false;
        }
        if (((Boolean)this.rotate.getValue()).booleanValue()) {
            this.lastHitVec = new Vec3d((Vec3i)pos).func_72441_c(0.5, 1.0, 0.5);
            this.tick = 0;
            if (((Boolean)this.yawCheck.getValue()).booleanValue() || ((Boolean)this.pitchCheck.getValue()).booleanValue()) {
                Vec2f rotationWanted = RotationUtil.getRotationTo(this.lastHitVec);
                if (!((Boolean)this.blockRotation.getValue()).booleanValue() || !this.isRotating) {
                    double d = ((Boolean)this.pitchCheck.getValue()).booleanValue() ? (this.yPlayerRotation == Double.MAX_VALUE ? (double)AutoCrystalRewrite.mc.field_71439_g.func_189653_aC().field_189982_i : this.yPlayerRotation) : (this.yPlayerRotation = Double.MIN_VALUE);
                    this.xPlayerRotation = ((Boolean)this.yawCheck.getValue()).booleanValue() ? (this.xPlayerRotation == Double.MAX_VALUE ? (double)RotationUtil.normalizeAngle(AutoCrystalRewrite.mc.field_71439_g.func_189653_aC().field_189983_j) : this.xPlayerRotation) : Double.MIN_VALUE;
                    this.isRotating = true;
                }
                if (((Boolean)this.rotate.getValue()).booleanValue() && ((Boolean)this.placeStrictDirection.getValue()).booleanValue()) {
                    double distanceDo;
                    if (((Boolean)this.yawCheck.getValue()).booleanValue()) {
                        distanceDo = (double)rotationWanted.field_189982_i - this.xPlayerRotation;
                        if (Math.abs(distanceDo) > 180.0) {
                            distanceDo = RotationUtil.normalizeAngle(distanceDo);
                        }
                        if (Math.abs(distanceDo) > (double)((Integer)this.yawStep.getValue()).intValue()) {
                            return true;
                        }
                    }
                    if (((Boolean)this.pitchCheck.getValue()).booleanValue() && Math.abs(distanceDo = (double)rotationWanted.field_189983_j - this.yPlayerRotation) > (double)((Integer)this.pitchStep.getValue()).intValue()) {
                        return true;
                    }
                } else if (this.xPlayerRotation != (double)rotationWanted.field_189982_i || this.yPlayerRotation != (double)rotationWanted.field_189983_j) {
                    return true;
                }
            }
        }
        if (handSwing == EnumHand.MAIN_HAND) {
            if (this.slotChange != -1) {
                if (((Boolean)this.silentSwitch.getValue()).booleanValue()) {
                    AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(this.slotChange));
                } else if (this.slotChange != AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c) {
                    if (((Boolean)this.switchBack.getValue()).booleanValue()) {
                        this.tickSwitch = (Integer)this.tickSwitchBack.getValue();
                        this.oldSlotObby = AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c;
                    }
                    AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c = this.slotChange;
                    AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c));
                    AutoCrystalRewrite.mc.field_71442_b.func_78765_e();
                }
            } else if (this.oldSlot != AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c) {
                AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c));
                AutoCrystalRewrite.mc.field_71442_b.func_78765_e();
            }
        }
        if (((Boolean)this.raytrace.getValue()).booleanValue()) {
            RayTraceResult result = AutoCrystalRewrite.mc.field_71441_e.func_72933_a(new Vec3d(AutoCrystalRewrite.mc.field_71439_g.field_70165_t, AutoCrystalRewrite.mc.field_71439_g.field_70163_u + (double)AutoCrystalRewrite.mc.field_71439_g.func_70047_e(), AutoCrystalRewrite.mc.field_71439_g.field_70161_v), new Vec3d((double)pos.func_177958_n() + 0.5, (double)pos.func_177956_o() + 0.5, (double)pos.func_177952_p() + 0.5));
            if (result == null || result.field_178784_b == null) {
                return false;
            }
            EnumFacing enumFacing = result.field_178784_b;
            if (((Boolean)this.rotate.getValue()).booleanValue() && ((Boolean)this.preRotate.getValue()).booleanValue()) {
                Vec2f rot = RotationUtil.getRotationTo(this.lastHitVec);
                AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(rot.field_189982_i, rot.field_189983_j, AutoCrystalRewrite.mc.field_71439_g.field_70122_E));
            }
            AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(pos, enumFacing, handSwing, 0.0f, 0.0f, 0.0f));
        } else if (((Boolean)this.placeStrictDirection.getValue()).booleanValue()) {
            EnumFacing result;
            if (AutoCrystalRewrite.mc.field_71439_g.field_70163_u + 0.63 > (double)pos.func_177956_o()) {
                result = EnumFacing.UP;
            } else {
                double xDiff = (double)pos.func_177958_n() - AutoCrystalRewrite.mc.field_71439_g.field_70165_t + 0.5;
                double zDiff = (double)pos.func_177952_p() - AutoCrystalRewrite.mc.field_71439_g.field_70161_v + 0.5;
                EnumFacing enumFacing = Math.abs(xDiff) > Math.abs(zDiff) ? (xDiff > 0.0 ? EnumFacing.WEST : EnumFacing.EAST) : (result = zDiff > 0.0 ? EnumFacing.NORTH : EnumFacing.SOUTH);
            }
            if (((Boolean)this.rotate.getValue()).booleanValue() && ((Boolean)this.preRotate.getValue()).booleanValue()) {
                Vec2f rot = RotationUtil.getRotationTo(this.lastHitVec);
                AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(rot.field_189982_i, rot.field_189983_j, AutoCrystalRewrite.mc.field_71439_g.field_70122_E));
            }
            AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(pos, result, handSwing, 0.0f, 0.0f, 0.0f));
        } else {
            if (((Boolean)this.rotate.getValue()).booleanValue() && ((Boolean)this.preRotate.getValue()).booleanValue()) {
                Vec2f rot = RotationUtil.getRotationTo(this.lastHitVec);
                AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(rot.field_189982_i, rot.field_189983_j, AutoCrystalRewrite.mc.field_71439_g.field_70122_E));
            }
            if (pos.func_177956_o() == 255) {
                AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.DOWN, handSwing, 0.0f, 0.0f, 0.0f));
            } else {
                AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, handSwing, 0.0f, 0.0f, 0.0f));
            }
        }
        if (!((String)this.swingModepl.getValue()).equals("None")) {
            this.swingArm((String)this.swingModepl.getValue(), (Boolean)this.hideClientpl.getValue(), handSwing);
        }
        if (this.slotChange != -1 && ((Boolean)this.silentSwitch.getValue()).booleanValue()) {
            AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c));
            AutoCrystalRewrite.mc.field_71442_b.func_78765_e();
        }
        this.tickBeforePlace = (Integer)this.tickDelayPlace.getValue();
        this.checkTimePlace = true;
        this.timePlace = System.currentTimeMillis();
        switch ((String)this.limitPacketPlace.getValue()) {
            case "Tick": {
                this.listCrystalsPlaced.addCrystal(pos, 0, (Integer)this.limitTickPlace.getValue());
                break;
            }
            case "Time": {
                this.listCrystalsPlaced.addCrystal(pos, (Integer)this.limitTickTime.getValue());
            }
        }
        this.endCrystalPlaced.addCrystal(pos);
        if (((Boolean)this.showPlaceCrystalsSecond.getValue()).booleanValue()) {
            this.listCrystalsSecondWait.addCrystal(pos, 2000);
        }
        if (this.crystalPlace == null) {
            switch ((String)this.focusPlaceType.getValue()) {
                case "Tick": {
                    this.crystalPlace = new crystalTime(pos, 0, (Integer)this.tickWaitFocusPlace.getValue());
                    break;
                }
                case "Time": {
                    this.crystalPlace = new crystalTime(pos, (Integer)this.timeWaitFocusPlace.getValue());
                }
            }
        }
        return true;
    }

    boolean isCrystalHere(BlockPos pos) {
        BlockPos posUp = pos.func_177984_a();
        AxisAlignedBB box = new AxisAlignedBB((double)posUp.func_177958_n(), (double)posUp.func_177956_o(), (double)posUp.func_177952_p(), (double)posUp.func_177958_n() + 1.0, (double)posUp.func_177956_o() + 2.0, (double)posUp.func_177952_p() + 1.0);
        return AutoCrystalRewrite.mc.field_71441_e.func_175647_a(Entity.class, box, entity -> entity instanceof EntityEnderCrystal && this.sameBlockPos(entity.func_180425_c(), pos)).isEmpty();
    }

    CrystalInfo.NewBreakInfo getTargetBreaking(String mode) {
        PredictUtil.PredictSettings settings = new PredictUtil.PredictSettings((Integer)this.tickPredict.getValue(), (Boolean)this.calculateYPredict.getValue(), (Integer)this.startDecrease.getValue(), (Integer)this.exponentStartDecrease.getValue(), (Integer)this.decreaseY.getValue(), (Integer)this.exponentDecreaseY.getValue(), (Integer)this.increaseY.getValue(), (Integer)this.exponentIncreaseY.getValue(), (Boolean)this.splitXZ.getValue(), (Integer)this.widthPredict.getValue(), (Boolean)this.debugPredict.getValue(), (Boolean)this.showPredictions.getValue(), (Boolean)this.manualOutHole.getValue(), (Boolean)this.aboveHoleManual.getValue(), (Boolean)this.stairPredict.getValue(), (Integer)this.nStair.getValue(), (Double)this.speedActivationStair.getValue());
        int nThread = (Integer)this.nThread.getValue();
        double enemyRangeSQ = (Double)this.rangeEnemyBreaking.getValue() * (Double)this.rangeEnemyBreaking.getValue();
        double maxSelfDamage = (Double)this.maxSelfDamageBreak.getValue();
        boolean rayTrace = (Boolean)this.raytrace.getValue();
        double wallRangeSQ = (Double)this.wallrangeBreak.getValue() * (Double)this.wallrangeBreak.getValue();
        float armourPercent = (float)((Integer)this.armourFacePlace.getValue()).intValue() / 100.0f;
        int maxYTarget = (Integer)this.maxYTarget.getValue();
        int minYTarget = (Integer)this.minYTarget.getValue();
        double minFacePlaceHp = ((Integer)this.facePlaceValue.getValue()).intValue();
        if (this.forceFacePlace.getText().length() > 0 && Keyboard.isKeyDown((int)KeyBoardClass.getKeyFromChar(this.forceFacePlace.getText().charAt(0)))) {
            minFacePlaceHp = 36.0;
        }
        double minFacePlaceDamage = (Double)this.minFacePlaceDmg.getValue();
        double minDamage = (Double)this.minDamageBreak.getValue();
        double rangeSQ = (Double)this.breakRange.getValue() * (Double)this.breakRange.getValue();
        int breakTimeout = (Integer)this.breakTimeout.getValue();
        boolean relativeDamage = (Boolean)this.relativeDamageBreak.getValue();
        double valueRelativeDamage = (Double)this.relativeDamageValueBreak.getValue();
        boolean ignoreTerrainValue = false;
        boolean antiSuicide = (Boolean)this.antiSuicidebr.getValue();
        if (((Boolean)this.ignoreTerrain.getValue()).booleanValue()) {
            if (((Boolean)this.bindIgnoreTerrain.getValue()).booleanValue()) {
                if (this.letterIgnoreTerrain.getText().length() > 0 && Keyboard.isKeyDown((int)KeyBoardClass.getKeyFromChar(this.letterIgnoreTerrain.getText().charAt(0)))) {
                    ignoreTerrainValue = true;
                }
            } else {
                ignoreTerrainValue = true;
            }
        }
        CrystalInfo.NewBreakInfo bestBreak = new CrystalInfo.NewBreakInfo(-100.0f, null, null, 100.0);
        ArrayList<BlockPos> webRemoved = new ArrayList<BlockPos>();
        block5 : switch (mode) {
            case "Nearest": 
            case "Lowest": {
                List<List<PositionInfo>> possibleCrystals;
                EntityPlayer targetEP;
                EntityPlayer entityPlayer = targetEP = mode.equals("Lowest") ? (EntityPlayer)this.getBasicPlayers(enemyRangeSQ).min((x, y) -> (int)x.func_110143_aJ()).orElse(null) : (EntityPlayer)this.getBasicPlayers(enemyRangeSQ).min(Comparator.comparingDouble(x -> x.func_70068_e((Entity)AutoCrystalRewrite.mc.field_71439_g))).orElse(null);
                if (targetEP == null) break;
                if (BlockUtil.getBlock(targetEP.field_70165_t, targetEP.field_70163_u, targetEP.field_70161_v) instanceof BlockWeb) {
                    AutoCrystalRewrite.mc.field_71441_e.func_175698_g(new BlockPos(targetEP.field_70165_t, targetEP.field_70163_u, targetEP.field_70161_v));
                    webRemoved.add(new BlockPos(targetEP.field_70165_t, targetEP.field_70163_u, targetEP.field_70161_v));
                }
                PlayerInfo player = new PlayerInfo((EntityPlayer)((Boolean)this.predictSelfDBreaking.getValue() != false ? PredictUtil.predictPlayer((EntityPlayer)AutoCrystalRewrite.mc.field_71439_g, settings) : AutoCrystalRewrite.mc.field_71439_g), false, (float)AutoCrystalRewrite.mc.field_71439_g.func_70658_aO(), (float)AutoCrystalRewrite.mc.field_71439_g.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
                if (((Boolean)this.predictSelfDBreaking.getValue()).booleanValue() && ((Boolean)this.showSelfPredictBreaking.getValue()).booleanValue()) {
                    this.toDisplay.add(new display(player.entity.func_174813_aQ(), this.colorSelfBreaking.getColor(), (Integer)this.widthPredict.getValue()));
                }
                if ((possibleCrystals = this.getPossibleCrystalsBreaking(player, maxSelfDamage, rayTrace, wallRangeSQ, rangeSQ, antiSuicide, ignoreTerrainValue)) == null) break;
                PlayerInfo target = new PlayerInfo(targetEP, armourPercent, (float)targetEP.func_70658_aO(), (float)targetEP.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
                bestBreak = this.calcualteBestBreak(nThread, possibleCrystals, player.entity.field_70165_t, player.entity.field_70163_u, player.entity.field_70161_v, target, minDamage, minFacePlaceHp, minFacePlaceDamage, maxSelfDamage, maxYTarget, minYTarget, breakTimeout, new CrystalInfo.NewBreakInfo(0.0f, null, null, 0.0), ignoreTerrainValue, relativeDamage, valueRelativeDamage);
                break;
            }
            case "Damage": {
                PlayerInfo target;
                List<List<PositionInfo>> possibleCrystals;
                List<EntityPlayer> players = this.getBasicPlayers(enemyRangeSQ).sorted(new Sortbyroll()).collect(Collectors.toList());
                if (players.size() == 0) break;
                for (EntityPlayer et : players) {
                    if (!(BlockUtil.getBlock(et.field_70165_t, et.field_70163_u, et.field_70161_v) instanceof BlockWeb)) continue;
                    AutoCrystalRewrite.mc.field_71441_e.func_175698_g(new BlockPos(et.field_70165_t, et.field_70163_u, et.field_70161_v));
                    webRemoved.add(new BlockPos(et.field_70165_t, et.field_70163_u, et.field_70161_v));
                }
                if (((Boolean)this.predictPlaceEnemy.getValue()).booleanValue()) {
                    players = this.getPlayersThreaded(nThread, players, settings, (Integer)this.predictBreakTimeout.getValue());
                }
                PlayerInfo player = new PlayerInfo((EntityPlayer)((Boolean)this.predictSelfDBreaking.getValue() != false ? PredictUtil.predictPlayer((EntityPlayer)AutoCrystalRewrite.mc.field_71439_g, settings) : AutoCrystalRewrite.mc.field_71439_g), false, (float)AutoCrystalRewrite.mc.field_71439_g.func_70658_aO(), (float)AutoCrystalRewrite.mc.field_71439_g.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
                if (((Boolean)this.predictSelfDBreaking.getValue()).booleanValue() && ((Boolean)this.showSelfPredictBreaking.getValue()).booleanValue()) {
                    this.toDisplay.add(new display(player.entity.func_174813_aQ(), this.colorSelfBreaking.getColor(), (Integer)this.widthPredict.getValue()));
                }
                if ((possibleCrystals = this.getPossibleCrystalsBreaking(player, maxSelfDamage, rayTrace, wallRangeSQ, rangeSQ, antiSuicide, ignoreTerrainValue)) == null) break;
                int count = 0;
                for (EntityPlayer playerTemp : players) {
                    if (count++ >= (Integer)this.maxTarget.getValue()) break block5;
                    target = new PlayerInfo(playerTemp, armourPercent, (float)playerTemp.func_70658_aO(), (float)playerTemp.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
                    bestBreak = this.calcualteBestBreak(nThread, possibleCrystals, player.entity.field_70165_t, player.entity.field_70163_u, player.entity.field_70161_v, target, minDamage, minFacePlaceHp, minFacePlaceDamage, maxSelfDamage, maxYTarget, minYTarget, breakTimeout, bestBreak, ignoreTerrainValue, relativeDamage, valueRelativeDamage);
                }
                break;
            }
        }
        for (BlockPos web : webRemoved) {
            AutoCrystalRewrite.mc.field_71441_e.func_175656_a(web, Blocks.field_150321_G.func_176223_P());
        }
        if (bestBreak.target != null) {
            this.breakRender = 0;
        }
        return bestBreak;
    }

    List<List<PositionInfo>> getPossibleCrystalsBreaking(PlayerInfo self, double maxSelfDamage, boolean raytrace, double wallRangeSQ, double rangeSQ, boolean antiSuicide, boolean ignoreTerrain) {
        ArrayList<PositionInfo> damagePos = new ArrayList<PositionInfo>();
        AutoCrystalRewrite.mc.field_71441_e.field_72996_f.stream().filter(entity -> entity instanceof EntityEnderCrystal && !this.breakPacketLimit.crystalIdExists(entity.field_145783_c) && AutoCrystalRewrite.mc.field_71439_g.func_70068_e(entity) <= rangeSQ && this.existsCrystal.CrystalExists(entity.func_180425_c().func_177982_a(0, -1, 0)) == -1).map(entity -> (EntityEnderCrystal)entity).collect(Collectors.toList()).forEach(crystal -> {
            RayTraceResult result;
            float damage = Float.MIN_VALUE;
            boolean continueFor = true;
            if (antiSuicide && (damage = DamageUtil.calculateDamageThreaded(crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v, self, ignoreTerrain)) >= self.health) {
                continueFor = false;
            }
            if (continueFor && ((result = AutoCrystalRewrite.mc.field_71441_e.func_72933_a(new Vec3d(AutoCrystalRewrite.mc.field_71439_g.field_70165_t, AutoCrystalRewrite.mc.field_71439_g.field_70163_u + (double)AutoCrystalRewrite.mc.field_71439_g.func_70047_e(), AutoCrystalRewrite.mc.field_71439_g.field_70161_v), new Vec3d(crystal.field_70165_t, crystal.field_70163_u + 1.0, crystal.field_70161_v))) == null || !raytrace && AutoCrystalRewrite.mc.field_71439_g.func_70068_e((Entity)crystal) <= wallRangeSQ)) {
                if (damage == Float.MIN_VALUE) {
                    damage = DamageUtil.calculateDamageThreaded(crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v, self, ignoreTerrain);
                }
                switch ((String)this.chooseCrystal.getValue()) {
                    case "All": {
                        damagePos.add(new PositionInfo((EntityEnderCrystal)crystal, (double)damage));
                        break;
                    }
                    case "Own": {
                        if (!this.endCrystalPlaced.hasCrystal((EntityEnderCrystal)crystal)) break;
                        damagePos.add(new PositionInfo((EntityEnderCrystal)crystal, (double)damage));
                        break;
                    }
                    case "Smart": {
                        if (!((double)damage < maxSelfDamage)) break;
                        damagePos.add(new PositionInfo((EntityEnderCrystal)crystal, (double)damage));
                    }
                }
            }
        });
        return this.splitList(damagePos, (Integer)this.nThread.getValue());
    }

    CrystalInfo.NewBreakInfo calcualteBestBreak(int nThread, List<List<PositionInfo>> possibleCrystals, double posX, double posY, double posZ, PlayerInfo target, double minDamage, double minFacePlaceHp, double minFacePlaceDamage, double maxSelfDamage, int maxYTarget, int minYTarget, int placeTimeout, CrystalInfo.NewBreakInfo oldBreak, boolean ignoreTerrain, boolean relativeDamage, double valueRelativeDamage) {
        LinkedList<Future<CrystalInfo.NewBreakInfo>> futures = new LinkedList<Future<CrystalInfo.NewBreakInfo>>();
        int i = 0;
        while (i < nThread) {
            int finalI = i++;
            futures.add(this.executor.submit(() -> this.calculateBestBreakTarget((List)possibleCrystals.get(finalI), posX, posY, posZ, target, minDamage, minFacePlaceHp, minFacePlaceDamage, maxSelfDamage, maxYTarget, minYTarget, ignoreTerrain, relativeDamage, valueRelativeDamage)));
        }
        Stack<CrystalInfo.NewBreakInfo> results = new Stack<CrystalInfo.NewBreakInfo>();
        for (Future future : futures) {
            try {
                CrystalInfo.NewBreakInfo temp = (CrystalInfo.NewBreakInfo)future.get(placeTimeout, TimeUnit.MILLISECONDS);
                if (temp == null) continue;
                results.add(temp);
            }
            catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }
        results.add(oldBreak);
        return this.getResultBreak(results);
    }

    CrystalInfo.NewBreakInfo calculateBestBreakTarget(List<PositionInfo> possibleLocations, double x, double y, double z, PlayerInfo target, double minDamage, double minFacePlaceHealth, double minFacePlaceDamage, double maxSelfDamage, int maxYTarget, int minYTarget, boolean ignoreTerrain, boolean relativeDamage, double valueRelativeDamage) {
        PositionInfo best = new PositionInfo();
        for (PositionInfo crystal : possibleLocations) {
            double d;
            double temp = target.entity.field_70163_u - crystal.crystal.field_70163_u - 1.0;
            if (!(d > 0.0) ? temp < (double)(-maxYTarget) : temp > (double)minYTarget) continue;
            float currentDamage = DamageUtil.calculateDamageThreaded(crystal.crystal.field_70165_t, crystal.crystal.field_70163_u, crystal.crystal.field_70161_v, target, ignoreTerrain);
            if ((double)currentDamage == best.damage) {
                if (best.crystal != null && (temp = crystal.crystal.func_70092_e(x, y, z)) != best.distance && !((double)currentDamage / maxSelfDamage > best.rapp) && !(temp < best.distance)) continue;
                best = crystal;
                best.setEnemyDamage(currentDamage);
                best.distance = target.entity.func_70092_e(crystal.crystal.field_70165_t, crystal.crystal.field_70163_u, crystal.crystal.field_70161_v);
                best.distancePlayer = AutoCrystalRewrite.mc.field_71439_g.func_70092_e(crystal.crystal.field_70165_t, crystal.crystal.field_70163_u, crystal.crystal.field_70161_v);
                continue;
            }
            if (!((double)currentDamage > best.damage) || relativeDamage && crystal.getSelfDamage() / (double)currentDamage > valueRelativeDamage) continue;
            best = crystal;
            best.setEnemyDamage(currentDamage);
            best.distance = target.entity.func_70092_e(crystal.crystal.field_70165_t, crystal.crystal.field_70163_u, crystal.crystal.field_70161_v);
            best.distancePlayer = AutoCrystalRewrite.mc.field_71439_g.func_70092_e(crystal.crystal.field_70165_t, crystal.crystal.field_70163_u, crystal.crystal.field_70161_v);
        }
        if (best.crystal != null && (best.damage >= minDamage || ((double)target.health <= minFacePlaceHealth || target.lowArmour) && best.damage >= minFacePlaceDamage)) {
            return new CrystalInfo.NewBreakInfo((float)best.damage, target, best.crystal, best.distancePlayer);
        }
        return null;
    }

    CrystalInfo.NewBreakInfo getResultBreak(Stack<CrystalInfo.NewBreakInfo> result) {
        CrystalInfo.NewBreakInfo returnValue = new CrystalInfo.NewBreakInfo(0.0f, null, null, 100.0);
        while (!result.isEmpty()) {
            CrystalInfo.NewBreakInfo now = result.pop();
            if (now.damage == returnValue.damage) {
                if (!(now.distance < returnValue.distance)) continue;
                returnValue = now;
                continue;
            }
            if (!(now.damage > returnValue.damage)) continue;
            returnValue = now;
        }
        return returnValue;
    }

    boolean canStartBreaking() {
        switch ((String)this.breakDelay.getValue()) {
            case "Tick": {
                if (this.tickBeforeBreak == 0) {
                    return true;
                }
                --this.tickBeforeBreak;
                break;
            }
            case "Time": {
                if (!this.checkTimeBreak) {
                    return true;
                }
                if (System.currentTimeMillis() - this.timeBreak < (long)((Integer)this.timeDelayBreak.getValue()).intValue()) break;
                this.checkTimeBreak = false;
                return true;
            }
            case "Vanilla": {
                if (this.timerBreak.getTimePassed() / 50L < (long)(20 - (Integer)this.vanillaSpeedBreak.getValue())) break;
                this.timerBreak.reset();
                return true;
            }
        }
        return false;
    }

    boolean breakCrystals() {
        if (this.brokenCrystal) {
            this.brokenCrystal = false;
            return false;
        }
        if (!this.canStartBreaking()) {
            return false;
        }
        if (((Boolean)this.antiCity.getValue()).booleanValue() && this.forceBreak == null) {
            this.forceBreak = this.possibleCrystal();
        }
        long inizio = 0L;
        if (((Boolean)this.timeCalcBreaking.getValue()).booleanValue()) {
            inizio = System.currentTimeMillis();
        }
        if (this.forceBreak == null) {
            this.bestBreak = this.getTargetBreaking((String)this.targetBreaking.getValue());
        }
        if (((Boolean)this.timeCalcBreaking.getValue()).booleanValue()) {
            long fine = System.currentTimeMillis();
            this.durationsBreaking.add(fine - inizio);
            if (this.durationsPlace.size() > (Integer)this.nCalc.getValue()) {
                double sum = this.durationsBreaking.stream().mapToDouble(a -> a.longValue()).sum();
                this.durationsBreaking.clear();
                PistonCrystal.printDebug(String.format("N: %d Value: %f", this.nCalc.getValue(), sum /= (double)((Integer)this.nCalc.getValue()).intValue()), false);
            }
        }
        if (this.forceBreak != null) {
            return this.breakCrystal(this.forceBreak);
        }
        if (this.bestBreak.crystal != null) {
            if (((Boolean)this.showTextbr.getValue()).booleanValue()) {
                this.toDisplay.add(new display(String.valueOf((int)this.bestBreak.damage), this.bestBreak.crystal.func_180425_c().func_177982_a(0, -1, 0), this.colorBreakText.getValue(), (Double)this.textYBreak.getValue()));
            }
            if (((Boolean)this.predictBreakingEnemy.getValue()).booleanValue()) {
                this.toDisplay.add(new display(this.bestBreak.target.entity.func_174813_aQ(), this.showColorPredictEnemyBreaking.getColor(), (Integer)this.outlineWidthpl.getValue()));
            }
            if (this.listPlayersBreak.stream().noneMatch(e -> this.bestBreak.target.entity.func_70005_c_().equals(e.name)) || this.isMoving(this.bestBreak.target.entity.func_70005_c_())) {
                return this.breakCrystal(this.bestBreak.crystal);
            }
        }
        return false;
    }

    boolean isMoving(String name) {
        for (EntityPlayer e : AutoCrystalRewrite.mc.field_71441_e.field_73010_i) {
            if (!e.func_70005_c_().equals(name)) continue;
            if (Math.abs(e.field_70165_t - e.field_70169_q) + Math.abs(e.field_70161_v - e.field_70166_s) > (Double)this.speedActivation.getValue()) {
                this.listPlayersBreak.removeIf(f -> f.name.equals(name));
                return true;
            }
            return false;
        }
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    boolean breakCrystal(EntityEnderCrystal cr) {
        BlockPos pos = cr.func_180425_c();
        if (((Boolean)this.rotate.getValue()).booleanValue()) {
            this.lastHitVec = new Vec3d((Vec3i)pos).func_72441_c(0.5, 0.0, 0.5);
            this.tick = 0;
            if (((Boolean)this.yawCheck.getValue()).booleanValue() || ((Boolean)this.pitchCheck.getValue()).booleanValue()) {
                Vec2f rotationWanted = RotationUtil.getRotationTo(this.lastHitVec);
                if (!((Boolean)this.blockRotation.getValue()).booleanValue() || !this.isRotating) {
                    double d = ((Boolean)this.pitchCheck.getValue()).booleanValue() ? (this.yPlayerRotation == Double.MAX_VALUE ? (double)AutoCrystalRewrite.mc.field_71439_g.func_189653_aC().field_189982_i : this.yPlayerRotation) : (this.yPlayerRotation = Double.MIN_VALUE);
                    this.xPlayerRotation = ((Boolean)this.yawCheck.getValue()).booleanValue() ? (this.xPlayerRotation == Double.MAX_VALUE ? (double)RotationUtil.normalizeAngle(AutoCrystalRewrite.mc.field_71439_g.func_189653_aC().field_189983_j) : this.xPlayerRotation) : Double.MIN_VALUE;
                    this.isRotating = true;
                }
                if (((Boolean)this.placeStrictDirection.getValue()).booleanValue()) {
                    double distanceDo;
                    boolean back = false;
                    if (((Boolean)this.yawCheck.getValue()).booleanValue()) {
                        distanceDo = (double)rotationWanted.field_189982_i - this.xPlayerRotation;
                        if (Math.abs(distanceDo) > 180.0) {
                            distanceDo = RotationUtil.normalizeAngle(distanceDo);
                        }
                        if (Math.abs(distanceDo) > (double)((Integer)this.yawStep.getValue()).intValue()) {
                            back = true;
                        }
                    }
                    if (((Boolean)this.pitchCheck.getValue()).booleanValue() && Math.abs(distanceDo = (double)rotationWanted.field_189983_j - this.yPlayerRotation) > (double)((Integer)this.pitchStep.getValue()).intValue()) {
                        back = true;
                    }
                    if (back) {
                        if (!((Boolean)this.predictBreakRotation.getValue()).booleanValue()) return false;
                        if (this.lookingCrystal(cr)) {
                            return false;
                        }
                    }
                } else if (this.xPlayerRotation != (double)rotationWanted.field_189982_i || this.yPlayerRotation != (double)rotationWanted.field_189983_j) {
                    return false;
                }
            }
        }
        int switchBack = -1;
        if (((Boolean)this.antiWeakness.getValue()).booleanValue() && AutoCrystalRewrite.mc.field_71439_g.func_70644_a(MobEffects.field_76437_t) && AutoCrystalRewrite.mc.field_71439_g.func_70651_bq().stream().noneMatch(e -> e.func_76453_d().contains("damageBoost") && e.func_76458_c() > 0)) {
            int slotSword = InventoryUtil.findFirstItemSlot(ItemSword.class, 0, 8);
            if (slotSword == -1) {
                return false;
            }
            if (slotSword != AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c) {
                AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slotSword));
                AutoCrystalRewrite.mc.field_71439_g.field_71071_by.field_70461_c = slotSword;
                return false;
            }
        }
        if (((Boolean)this.rotate.getValue()).booleanValue() && ((Boolean)this.preRotate.getValue()).booleanValue()) {
            Vec2f rot = RotationUtil.getRotationTo(this.lastHitVec);
            AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(rot.field_189982_i, rot.field_189983_j, AutoCrystalRewrite.mc.field_71439_g.field_70122_E));
        }
        if (!((String)this.swingModebr.getValue()).equals("None")) {
            this.swingArm((String)this.swingModebr.getValue(), (Boolean)this.hideClientbr.getValue(), null);
        }
        if (((String)this.breakTypeCrystal.getValue()).equalsIgnoreCase("Swing")) {
            AutoCrystalRewrite.mc.field_71442_b.func_78764_a((EntityPlayer)AutoCrystalRewrite.mc.field_71439_g, (Entity)cr);
        } else {
            AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketUseEntity((Entity)cr));
        }
        if (((Boolean)this.cancelCrystal.getValue()).booleanValue()) {
            cr.func_70106_y();
            AutoCrystalRewrite.mc.field_71441_e.func_73022_a();
            AutoCrystalRewrite.mc.field_71441_e.func_72910_y();
        }
        switch ((String)this.limitBreakPacket.getValue()) {
            case "Tick": {
                this.breakPacketLimit.addCrystalId(cr.func_180425_c(), cr.field_145783_c, 0, (Integer)this.lomitBreakPacketTick.getValue());
                break;
            }
            case "Time": {
                this.breakPacketLimit.addCrystalId(cr.func_180425_c(), cr.field_145783_c, (Integer)this.limitBreakPacketTime.getValue());
            }
        }
        this.tickBeforeBreak = (Integer)this.tickDelayBreak.getValue();
        this.checkTimeBreak = true;
        this.timeBreak = System.currentTimeMillis();
        if (((Boolean)this.placeAfterBreak.getValue()).booleanValue()) {
            Object position;
            Object object = position = this.forceBreak == null ? cr.func_180425_c().func_177982_a(0, -1, 0) : this.forceBreakPlace;
            if (((Boolean)this.instaPlace.getValue()).booleanValue()) {
                EnumHand hand;
                BlockPos crystal = null;
                if (((Boolean)this.checkinstaPlace.getValue()).booleanValue()) {
                    crystal = this.getTargetPlacing((String)((String)this.targetPlacing.getValue())).crystal;
                }
                if (((Boolean)this.checkinstaPlace.getValue()).booleanValue() && crystal != null && !this.sameBlockPos((BlockPos)position, crystal)) {
                    crystal = position;
                }
                if ((hand = this.getHandCrystal()) != null) {
                    this.placeCrystal((BlockPos)position, hand, true);
                }
            } else {
                this.forcePlaceCrystal = position;
                if (this.forceBreak == null) {
                    this.forcePlaceDamage = this.bestBreak.damage;
                    this.forcePlaceTarget = this.bestBreak.target;
                } else {
                    this.forcePlaceDamage = 10.0f;
                    this.forcePlaceTarget = new PlayerInfo((EntityPlayer)AutoCrystalRewrite.mc.field_71439_g, 0.0f);
                }
            }
        }
        this.forceBreak = null;
        this.forceBreakPlace = null;
        if (this.bestBreak.target != null && Math.abs(this.bestBreak.target.entity.field_70165_t - this.bestBreak.target.entity.field_70169_q) + Math.abs(this.bestBreak.target.entity.field_70161_v - this.bestBreak.target.entity.field_70166_s) < (Double)this.speedActivation.getValue()) {
            switch ((String)this.slowBreak.getValue()) {
                case "Tick": {
                    this.listPlayersBreak.add(new slowBreakPlayers(this.bestBreak.target.entity.func_70005_c_(), (Integer)this.tickSlowBreak.getValue(), false));
                    break;
                }
                case "Time": {
                    this.listPlayersBreak.add(new slowBreakPlayers(this.bestBreak.target.entity.func_70005_c_(), (Integer)this.timeSlowBreak.getValue()));
                }
            }
        }
        if (((Boolean)this.showBreakCrystalsSecond.getValue()).booleanValue()) {
            this.attempedCrystalBreak.addCrystalId(cr.func_180425_c(), cr.field_145783_c, 500);
        }
        if (this.anvilCity.getText().length() > 0) {
            if (Keyboard.isKeyDown((int)KeyBoardClass.getKeyFromChar(this.anvilCity.getText().charAt(0))) && this.bestBreak.damage > 5.0f) {
                int slot;
                boolean isCity = false;
                BlockPos anvilPosition = BlockPos.field_177992_a;
                int[] endCrystalPositions = new int[]{(int)cr.field_70165_t, (int)cr.field_70163_u, (int)cr.field_70161_v};
                block16: for (Vec3i surround : new Vec3i[]{new Vec3i(1, 0, 0), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(0, 0, -1)}) {
                    int[] surroundPosition = new int[]{endCrystalPositions[0] + surround.field_177962_a, endCrystalPositions[1], endCrystalPositions[2] + surround.field_177961_c};
                    for (EntityPlayer t : this.getBasicPlayers(40.0).collect(Collectors.toList())) {
                        int[] playerPosition = new int[]{(int)t.field_70165_t, (int)t.field_70163_u, (int)t.field_70161_v};
                        if (playerPosition[1] != surroundPosition[1]) continue;
                        if (playerPosition[0] == surroundPosition[0]) {
                            if (Math.abs(playerPosition[2] - surroundPosition[2]) != 1 || !(BlockUtil.getBlock(cr.func_180425_c().func_177971_a(surround)) instanceof BlockAir)) continue;
                            isCity = true;
                            anvilPosition = cr.func_180425_c().func_177971_a(surround);
                            continue block16;
                        }
                        if (playerPosition[2] != surroundPosition[2] || Math.abs(playerPosition[0] - surroundPosition[0]) != 1 || !(BlockUtil.getBlock(cr.func_180425_c().func_177971_a(surround)) instanceof BlockAir)) continue;
                        isCity = true;
                        anvilPosition = cr.func_180425_c().func_177971_a(surround);
                        continue block16;
                    }
                }
                if (!isCity || (slot = InventoryUtil.findFirstBlockSlot(Blocks.field_150467_bQ.getClass(), 0, 8)) == -1) return true;
                this.isAnvilling = true;
                final java.util.Timer t = new java.util.Timer();
                final BlockPos finalCity = anvilPosition;
                this.blockCity = anvilPosition;
                this.crystalAnvil = cr.func_180425_c().func_177982_a(0, -1, 0);
                t.schedule(new TimerTask(){

                    @Override
                    public void run() {
                        int oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
                        mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slot));
                        PlacementUtil.place(finalCity, EnumHand.MAIN_HAND, (boolean)((Boolean)AutoCrystalRewrite.this.rotate.getValue()), false);
                        mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(oldSlot));
                        t.cancel();
                    }
                }, ((Integer)this.placeAnvil.getValue()).intValue());
                return true;
            } else {
                this.isAnvilling = false;
            }
            return true;
        } else {
            this.isAnvilling = false;
        }
        return true;
    }

    private void swingArm(String swingMode, boolean hideClient, EnumHand handSwingDef) {
        EnumHand[] handSwing;
        if (handSwingDef == null) {
            switch (swingMode) {
                case "Both": {
                    handSwing = new EnumHand[]{EnumHand.MAIN_HAND, EnumHand.OFF_HAND};
                    break;
                }
                case "Offhand": {
                    handSwing = new EnumHand[]{EnumHand.OFF_HAND};
                    break;
                }
                default: {
                    handSwing = new EnumHand[]{EnumHand.MAIN_HAND};
                    break;
                }
            }
        } else {
            handSwing = new EnumHand[]{handSwingDef};
        }
        for (EnumHand hand : handSwing) {
            if (hideClient) {
                if (((Boolean)this.hideClientbr.getValue()).booleanValue()) {
                    AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketAnimation(hand));
                    continue;
                }
                AutoCrystalRewrite.mc.field_71439_g.func_184609_a(hand);
                continue;
            }
            ItemStack stack = AutoCrystalRewrite.mc.field_71439_g.func_184586_b(hand);
            if (!stack.func_190926_b() && stack.func_77973_b().onEntitySwing((EntityLivingBase)AutoCrystalRewrite.mc.field_71439_g, stack)) {
                return;
            }
            AutoCrystalRewrite.mc.field_71439_g.field_110158_av = -1;
            AutoCrystalRewrite.mc.field_71439_g.field_82175_bq = true;
            AutoCrystalRewrite.mc.field_71439_g.field_184622_au = hand;
        }
    }

    EntityEnderCrystal possibleCrystal() {
        List<BlockPos> offsetPattern = this.getOffsets();
        for (BlockPos pos : offsetPattern) {
            for (Entity entity : AutoCrystalRewrite.mc.field_71441_e.func_72839_b(null, new AxisAlignedBB(pos))) {
                if (!(entity instanceof EntityEnderCrystal) || !((Boolean)this.destroyCrystal.getValue()).booleanValue()) continue;
                return (EntityEnderCrystal)entity;
            }
            if (!((Boolean)this.destroyAboveCrystal.getValue()).booleanValue()) continue;
            for (Entity entity : new ArrayList(AutoCrystalRewrite.mc.field_71441_e.field_72996_f)) {
                if (!(entity instanceof EntityEnderCrystal) || !this.sameBlockPos(entity.func_180425_c(), pos)) continue;
                return (EntityEnderCrystal)entity;
            }
        }
        return null;
    }

    List<BlockPos> getOffsets() {
        BlockPos playerPos = this.getPlayerPos();
        ArrayList<BlockPos> offsets = new ArrayList<BlockPos>();
        if (((Boolean)this.allowNon1x1.getValue()).booleanValue()) {
            int z;
            int x;
            double decimalX = Math.abs(AutoCrystalRewrite.mc.field_71439_g.field_70165_t) - Math.floor(Math.abs(AutoCrystalRewrite.mc.field_71439_g.field_70165_t));
            double decimalZ = Math.abs(AutoCrystalRewrite.mc.field_71439_g.field_70161_v) - Math.floor(Math.abs(AutoCrystalRewrite.mc.field_71439_g.field_70161_v));
            int lengthXPos = this.calcLength(decimalX, false);
            int lengthXNeg = this.calcLength(decimalX, true);
            int lengthZPos = this.calcLength(decimalZ, false);
            int lengthZNeg = this.calcLength(decimalZ, true);
            ArrayList<BlockPos> tempOffsets = new ArrayList<BlockPos>();
            offsets.addAll(this.getOverlapPos());
            for (x = 1; x < lengthXPos + 1; ++x) {
                tempOffsets.add(this.addToPlayer(playerPos, x, 0.0, 1 + lengthZPos));
                tempOffsets.add(this.addToPlayer(playerPos, x, 0.0, -(1 + lengthZNeg)));
            }
            for (x = 0; x <= lengthXNeg; ++x) {
                tempOffsets.add(this.addToPlayer(playerPos, -x, 0.0, 1 + lengthZPos));
                tempOffsets.add(this.addToPlayer(playerPos, -x, 0.0, -(1 + lengthZNeg)));
            }
            for (z = 1; z < lengthZPos + 1; ++z) {
                tempOffsets.add(this.addToPlayer(playerPos, 1 + lengthXPos, 0.0, z));
                tempOffsets.add(this.addToPlayer(playerPos, -(1 + lengthXNeg), 0.0, z));
            }
            for (z = 0; z <= lengthZNeg; ++z) {
                tempOffsets.add(this.addToPlayer(playerPos, 1 + lengthXPos, 0.0, -z));
                tempOffsets.add(this.addToPlayer(playerPos, -(1 + lengthXNeg), 0.0, -z));
            }
            for (BlockPos pos : tempOffsets) {
                if (AutoCrystalRewrite.getDown(pos)) {
                    offsets.add(pos.func_177982_a(0, -1, 0));
                }
                offsets.add(pos);
            }
        } else {
            offsets.add(playerPos.func_177982_a(0, -1, 0));
            for (int[] surround : new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}}) {
                if (AutoCrystalRewrite.getDown(playerPos.func_177982_a(surround[0], 0, surround[1]))) {
                    offsets.add(playerPos.func_177982_a(surround[0], -1, surround[1]));
                }
                offsets.add(playerPos.func_177982_a(surround[0], 0, surround[1]));
            }
        }
        return offsets;
    }

    public static boolean getDown(BlockPos pos) {
        for (EnumFacing e : EnumFacing.values()) {
            if (AutoCrystalRewrite.mc.field_71441_e.func_175623_d(pos.func_177971_a(e.func_176730_m()))) continue;
            return false;
        }
        return true;
    }

    BlockPos addToPlayer(BlockPos playerPos, double x, double y, double z) {
        if (playerPos.func_177958_n() < 0) {
            x = -x;
        }
        if (playerPos.func_177956_o() < 0) {
            y = -y;
        }
        if (playerPos.func_177952_p() < 0) {
            z = -z;
        }
        return playerPos.func_177963_a(x, y, z);
    }

    List<BlockPos> getOverlapPos() {
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        double decimalX = AutoCrystalRewrite.mc.field_71439_g.field_70165_t - Math.floor(AutoCrystalRewrite.mc.field_71439_g.field_70165_t);
        double decimalZ = AutoCrystalRewrite.mc.field_71439_g.field_70161_v - Math.floor(AutoCrystalRewrite.mc.field_71439_g.field_70161_v);
        int offX = this.calcOffset(decimalX);
        int offZ = this.calcOffset(decimalZ);
        positions.add(this.getPlayerPos());
        for (int x = 0; x <= Math.abs(offX); ++x) {
            for (int z = 0; z <= Math.abs(offZ); ++z) {
                int properX = x * offX;
                int properZ = z * offZ;
                positions.add(this.getPlayerPos().func_177982_a(properX, -1, properZ));
            }
        }
        return positions;
    }

    int calcOffset(double dec) {
        return dec >= 0.7 ? 1 : (dec <= 0.3 ? -1 : 0);
    }

    int calcLength(double decimal, boolean negative) {
        if (negative) {
            return decimal <= 0.3 ? 1 : 0;
        }
        return decimal >= 0.7 ? 1 : 0;
    }

    BlockPos getPlayerPos() {
        double decimalPoint = AutoCrystalRewrite.mc.field_71439_g.field_70163_u - Math.floor(AutoCrystalRewrite.mc.field_71439_g.field_70163_u);
        return new BlockPos(AutoCrystalRewrite.mc.field_71439_g.field_70165_t, decimalPoint > 0.8 ? Math.floor(AutoCrystalRewrite.mc.field_71439_g.field_70163_u) + 1.0 : Math.floor(AutoCrystalRewrite.mc.field_71439_g.field_70163_u), AutoCrystalRewrite.mc.field_71439_g.field_70161_v);
    }

    List<EntityPlayer> getPredicts(List<EntityPlayer> players, PredictUtil.PredictSettings settings) {
        players.replaceAll(entity -> PredictUtil.predictPlayer(entity, settings));
        return players;
    }

    List<List<EntityPlayer>> splitListEntity(final List<EntityPlayer> start, int nThreads) {
        int i;
        if (nThreads == 1) {
            return new ArrayList<List<EntityPlayer>>(){
                {
                    this.add(start);
                }
            };
        }
        int count = start.size();
        if (count == 0) {
            return null;
        }
        ArrayList<List<EntityPlayer>> output = new ArrayList<List<EntityPlayer>>(nThreads);
        for (i = 0; i < nThreads; ++i) {
            output.add(new ArrayList());
        }
        for (i = 0; i < count; ++i) {
            ((List)output.get(i % nThreads)).add(start.get(i));
        }
        return output;
    }

    Stream<EntityPlayer> getBasicPlayers(double rangeEnemySQ) {
        try {
            return AutoCrystalRewrite.mc.field_71441_e.field_73010_i.stream().filter(entity -> entity.func_70068_e((Entity)AutoCrystalRewrite.mc.field_71439_g) <= rangeEnemySQ).filter(entity -> !EntityUtil.basicChecksEntity((Entity)entity)).filter(entity -> entity.func_110143_aJ() > 0.0f);
        }
        catch (Exception e) {
            return new ArrayList().stream();
        }
    }

    boolean lookingCrystal(EntityEnderCrystal cr) {
        Vec3d positionEyes = AutoCrystalRewrite.mc.field_71439_g.func_174824_e(mc.func_184121_ak());
        Vec3d rotationEyes = new Vec3d(Math.cos(this.xPlayerRotation) * Math.cos(this.yPlayerRotation), Math.sin(this.xPlayerRotation) * Math.cos(this.yPlayerRotation), Math.sin(this.yPlayerRotation));
        int precision = 2;
        for (int i = 0; i < ((Double)this.breakRange.getValue()).intValue() + 1; ++i) {
            for (int j = precision; j > 0; --j) {
                AxisAlignedBB playerBox = cr.func_174813_aQ();
                double xArray = positionEyes.field_72450_a + rotationEyes.field_72450_a * (double)i + rotationEyes.field_72450_a / (double)j;
                double yArray = positionEyes.field_72448_b + rotationEyes.field_72448_b * (double)i + rotationEyes.field_72448_b / (double)j;
                double zArray = positionEyes.field_72449_c + rotationEyes.field_72449_c * (double)i + rotationEyes.field_72449_c / (double)j;
                if (!(playerBox.field_72337_e >= yArray) || !(playerBox.field_72338_b <= yArray) || !(playerBox.field_72336_d >= xArray) || !(playerBox.field_72340_a <= xArray) || !(playerBox.field_72334_f >= zArray) || !(playerBox.field_72339_c <= zArray)) continue;
                return true;
            }
        }
        return false;
    }

    boolean sameBlockPos(BlockPos first, BlockPos second) {
        if (first == null || second == null) {
            return false;
        }
        return first.func_177958_n() == second.func_177958_n() && first.func_177956_o() == second.func_177956_o() && first.func_177952_p() == second.func_177952_p();
    }

    AxisAlignedBB getBox(BlockPos centreBlock) {
        double minX = centreBlock.func_177958_n();
        double maxX = centreBlock.func_177958_n() + 1;
        double minZ = centreBlock.func_177952_p();
        double maxZ = centreBlock.func_177952_p() + 1;
        return new AxisAlignedBB(minX, (double)centreBlock.func_177956_o(), minZ, maxX, (double)(centreBlock.func_177956_o() + 1), maxZ);
    }

    AxisAlignedBB getBox(double x, double y, double z) {
        double minX = x;
        double maxX = x + 1.0;
        double minZ = z;
        double maxZ = z + 1.0;
        return new AxisAlignedBB(minX, y, minZ, maxX, y + 1.0, maxZ);
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (!this.isEnabled()) {
            this.bestPlace = null;
            this.bestBreak = null;
            this.managerRenderBlocks.blocks.clear();
            this.movingPlaceNow = new Vec3d(0.0, 0.0, 0.0);
            this.movingBreakNow = new Vec3d(0.0, 0.0, 0.0);
        }
        this.managerRenderBlocks.render();
        if (this.bestPlace != null && this.bestPlace.crystal != null) {
            if (!((Boolean)this.movingPlace.getValue()).booleanValue()) {
                this.drawBoxMain((String)this.typePlace.getValue(), this.bestPlace.crystal, (String)this.placeDimension.getValue(), (Double)this.slabHeightPlace.getValue(), true, -1);
            } else {
                this.lastBestPlace = this.bestPlace.crystal;
            }
            if (((Boolean)this.fadeCapl.getValue()).booleanValue()) {
                this.managerRenderBlocks.addRender(true, this.bestPlace.crystal);
            }
        }
        if (((Boolean)this.movingPlace.getValue()).booleanValue() && this.lastBestPlace != null) {
            if (this.movingPlaceNow.field_72448_b == -1.0 && this.movingBreakNow.field_72450_a == -1.0 && this.movingPlaceNow.field_72449_c == -1.0) {
                this.movingPlaceNow = new Vec3d((double)this.lastBestPlace.func_177958_n(), (double)this.lastBestPlace.func_177956_o(), (double)this.lastBestPlace.func_177952_p());
            }
            this.movingPlaceNow = new Vec3d(this.movingPlaceNow.field_72450_a + ((double)this.lastBestPlace.func_177958_n() - this.movingPlaceNow.field_72450_a) * (double)((Double)this.movingPlaceSpeed.getValue()).floatValue(), this.movingPlaceNow.field_72448_b + ((double)this.lastBestPlace.func_177956_o() - this.movingPlaceNow.field_72448_b) * (double)((Double)this.movingPlaceSpeed.getValue()).floatValue(), this.movingPlaceNow.field_72449_c + ((double)this.lastBestPlace.func_177952_p() - this.movingPlaceNow.field_72449_c) * (double)((Double)this.movingPlaceSpeed.getValue()).floatValue());
            this.drawBoxMain((String)this.typePlace.getValue(), this.movingPlaceNow.field_72450_a, this.movingPlaceNow.field_72448_b, this.movingPlaceNow.field_72449_c, (String)this.placeDimension.getValue(), (Double)this.slabHeightPlace.getValue(), true, -1);
            if (Math.abs(this.movingPlaceNow.field_72450_a - (double)this.lastBestPlace.func_177958_n()) <= 0.125 && Math.abs(this.movingPlaceNow.field_72448_b - (double)this.lastBestPlace.func_177956_o()) <= 0.125 && Math.abs(this.movingPlaceNow.field_72449_c - (double)this.lastBestPlace.func_177952_p()) <= 0.125) {
                this.lastBestPlace = null;
            }
        }
        if (this.bestBreak != null && this.bestBreak.crystal != null && (!((Boolean)this.placeDominant.getValue()).booleanValue() || this.bestPlace != null && this.bestPlace.crystal != null && !this.sameBlockPos(this.bestPlace.crystal, this.bestBreak.crystal.func_180425_c().func_177982_a(0, -1, 0)))) {
            if (!((Boolean)this.movingBreak.getValue()).booleanValue()) {
                this.drawBoxMain((String)this.typeBreak.getValue(), this.bestBreak.crystal.func_180425_c().func_177982_a(0, -1, 0), (String)this.breakDimension.getValue(), (Double)this.slabHeightBreak.getValue(), false, -1);
            } else if (((Boolean)this.movingBreak.getValue()).booleanValue()) {
                this.lastBestBreak = this.bestBreak.crystal.func_180425_c().func_177982_a(0, -1, 0);
            }
            if (((Boolean)this.fadeCabr.getValue()).booleanValue()) {
                this.managerRenderBlocks.addRender(false, this.bestBreak.crystal.func_180425_c().func_177982_a(0, -1, 0));
            }
        }
        if (((Boolean)this.movingBreak.getValue()).booleanValue() && this.lastBestBreak != null) {
            if (this.movingBreakNow.field_72448_b == -1.0 && this.movingBreakNow.field_72450_a == -1.0 && this.movingBreakNow.field_72449_c == -1.0) {
                BlockPos pos = this.bestBreak.crystal.func_180425_c().func_177982_a(0, -1, 0);
                this.movingBreakNow = new Vec3d((double)pos.func_177958_n(), (double)pos.func_177956_o(), (double)pos.func_177952_p());
            }
            this.movingBreakNow = new Vec3d(this.movingBreakNow.field_72450_a + ((double)this.lastBestBreak.func_177958_n() - this.movingBreakNow.field_72450_a) * (double)((Double)this.movingBreakSpeed.getValue()).floatValue(), this.movingBreakNow.field_72448_b + ((double)this.lastBestBreak.func_177956_o() - this.movingBreakNow.field_72448_b) * (double)((Double)this.movingBreakSpeed.getValue()).floatValue(), this.movingBreakNow.field_72449_c + ((double)this.lastBestBreak.func_177952_p() - this.movingBreakNow.field_72449_c) * (double)((Double)this.movingBreakSpeed.getValue()).floatValue());
            this.drawBoxMain((String)this.typeBreak.getValue(), this.movingBreakNow.field_72450_a, this.movingBreakNow.field_72448_b, this.movingBreakNow.field_72449_c, (String)this.breakDimension.getValue(), (Double)this.slabHeightBreak.getValue(), false, -1);
            if (Math.abs(this.movingBreakNow.field_72450_a - (double)this.lastBestBreak.func_177958_n()) <= 0.125 && Math.abs(this.movingBreakNow.field_72448_b - (double)this.lastBestBreak.func_177956_o()) <= 0.125 && Math.abs(this.movingBreakNow.field_72449_c - (double)this.lastBestBreak.func_177952_p()) <= 0.125) {
                this.lastBestBreak = null;
            }
        }
        this.toDisplay.forEach(display::draw);
        if (((Boolean)this.predictSurround.getValue()).booleanValue() && !((Boolean)this.predictPacketSurround.getValue()).booleanValue()) {
            AutoCrystalRewrite.mc.field_71438_f.field_72738_E.forEach((integer, destroyBlockProgress) -> {
                if (this.stopGapple(false)) {
                    return;
                }
                EnumHand hand = this.getHandCrystal();
                if (hand == null) {
                    return;
                }
                if (destroyBlockProgress != null) {
                    BlockPos blockPos = destroyBlockProgress.func_180246_b();
                    if (AutoCrystalRewrite.mc.field_71441_e.func_180495_p(blockPos).func_177230_c() == Blocks.field_150350_a) {
                        return;
                    }
                    if (blockPos.func_185332_f((int)AutoCrystalRewrite.mc.field_71439_g.field_70165_t, (int)AutoCrystalRewrite.mc.field_71439_g.field_70163_u, (int)AutoCrystalRewrite.mc.field_71439_g.field_70161_v) <= (Double)this.placeRange.getValue() && destroyBlockProgress.func_73106_e() / 2 * 25 >= (Integer)this.percentSurround.getValue()) {
                        this.placeSurroundBlock(blockPos, hand);
                    }
                }
            });
        }
        if (this.placeRender++ > (Integer)this.extendedPlace.getValue()) {
            this.bestPlace = new CrystalInfo.PlaceInfo(-100.0f, null, null, 100.0);
        }
        if (this.breakRender++ > (Integer)this.extendedBreak.getValue()) {
            this.bestBreak = new CrystalInfo.NewBreakInfo(-100.0f, null, null, 100.0);
        }
    }

    void drawBoxMain(String type, BlockPos position, String dimension, double heightSlab, boolean place, int alpha) {
        if (dimension.equals("Circle")) {
            int alphaValue = alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha;
            RenderUtil.drawCircle((float)position.field_177962_a + 0.5f, position.func_177956_o() + 1, (float)position.field_177961_c + 0.5f, place ? (Double)this.rangeCirclePl.getValue() : (Double)this.rangeCircleBr.getValue(), place ? new GSColor(this.firstVerticeOutlineBot.getColor(), alphaValue) : new GSColor(this.firstVerticeOutlineBotbr.getColor(), alphaValue));
        } else {
            AxisAlignedBB box = this.getBox(position);
            int mask = 63;
            if (dimension.equals("Flat")) {
                mask = 2;
                box = new AxisAlignedBB(box.field_72340_a, box.field_72337_e, box.field_72339_c, box.field_72336_d, box.field_72337_e, box.field_72334_f);
            } else if (dimension.equals("Slab")) {
                box = new AxisAlignedBB(box.field_72340_a, box.field_72337_e - heightSlab, box.field_72339_c, box.field_72336_d, box.field_72337_e, box.field_72334_f);
            }
            switch (type) {
                case "Outline": {
                    this.displayOutline(box, place, alpha);
                    break;
                }
                case "Fill": {
                    this.displayFill(box, mask, place, alpha);
                    break;
                }
                case "Both": {
                    this.displayFill(box, mask, place, alpha);
                    this.displayOutline(box, place, alpha);
                }
            }
        }
    }

    void drawBoxMain(String type, double x, double y, double z, String dimension, double heightSlab, boolean place, int alpha) {
        if (dimension.equals("Circle")) {
            int alphaValue = alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha;
            RenderUtil.drawCircle((float)(x + 0.5), (float)(y + 1.0), (float)(z + 0.5), place ? (Double)this.rangeCirclePl.getValue() : (Double)this.rangeCircleBr.getValue(), place ? new GSColor(this.firstVerticeOutlineBot.getColor(), alphaValue) : new GSColor(this.firstVerticeOutlineBotbr.getColor(), alphaValue));
        } else {
            AxisAlignedBB box = this.getBox(x, y, z);
            int mask = 63;
            if (dimension.equals("Flat")) {
                mask = 2;
                box = new AxisAlignedBB(box.field_72340_a, box.field_72337_e, box.field_72339_c, box.field_72336_d, box.field_72337_e, box.field_72334_f);
            } else if (dimension.equals("Slab")) {
                box = new AxisAlignedBB(box.field_72340_a, box.field_72337_e - heightSlab, box.field_72339_c, box.field_72336_d, box.field_72337_e, box.field_72334_f);
            }
            switch (type) {
                case "Outline": {
                    this.displayOutline(box, place, alpha);
                    break;
                }
                case "Fill": {
                    this.displayFill(box, mask, place, alpha);
                    break;
                }
                case "Both": {
                    this.displayFill(box, mask, place, alpha);
                    this.displayOutline(box, place, alpha);
                }
            }
        }
    }

    void displayOutline(AxisAlignedBB box, boolean place, int alpha) {
        this.renderCustomOutline(box, place, alpha);
    }

    void displayFill(AxisAlignedBB box, int mask, boolean place, int alpha) {
        this.renderFillCustom(box, mask, place, alpha);
    }

    private boolean canBreak(BlockPos pos) {
        IBlockState blockState = AutoCrystalRewrite.mc.field_71441_e.func_180495_p(pos);
        Block block = blockState.func_177230_c();
        return block.func_176195_g(blockState, (World)AutoCrystalRewrite.mc.field_71441_e, pos) != -1.0f;
    }

    void placeSurroundBlock(BlockPos blockPos, EnumHand hand) {
        float armourPercent = (float)((Integer)this.armourFacePlace.getValue()).intValue() / 100.0f;
        for (Vec3i surround : new Vec3i[]{new Vec3i(1, 0, 0), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(0, 0, -1)}) {
            ArrayList players = new ArrayList(AutoCrystalRewrite.mc.field_71441_e.func_72839_b(null, new AxisAlignedBB(blockPos.func_177971_a(surround))));
            PlayerInfo info = null;
            for (Entity pl : players) {
                if (!(pl instanceof EntityPlayer) || pl == AutoCrystalRewrite.mc.field_71439_g || !(pl.field_70163_u + 0.5 >= (double)blockPos.field_177960_b)) continue;
                EntityPlayer temp = (EntityPlayer)pl;
                info = new PlayerInfo(temp, armourPercent, (float)temp.func_70658_aO(), (float)temp.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
                break;
            }
            boolean quit = false;
            if (info == null) continue;
            BlockPos coords = null;
            double damage = Double.MIN_VALUE;
            Block toReplace = BlockUtil.getBlock(blockPos);
            AutoCrystalRewrite.mc.field_71441_e.func_175698_g(blockPos);
            for (Vec3i placement : new Vec3i[]{new Vec3i(1, -1, 0), new Vec3i(-1, -1, 0), new Vec3i(0, -1, 1), new Vec3i(0, -1, -1)}) {
                BlockPos temp = blockPos.func_177971_a(placement);
                if (!CrystalUtil.canPlaceCrystal(temp, (Boolean)this.newPlace.getValue()) || (double)DamageUtil.calculateDamage((double)temp.func_177958_n() + 0.5, (double)temp.func_177956_o() + 1.0, (double)temp.func_177952_p() + 0.5, (Entity)AutoCrystalRewrite.mc.field_71439_g, (Boolean)this.ignoreTerrain.getValue()) >= (Double)this.maxSelfDamageSur.getValue()) continue;
                if (!((Boolean)this.placeOnCrystal.getValue()).booleanValue() && !this.isCrystalHere(temp)) {
                    quit = true;
                    break;
                }
                float damagePlayer = DamageUtil.calculateDamageThreaded((double)temp.func_177958_n() + 0.5, (double)temp.func_177956_o() + 1.0, (double)temp.func_177952_p() + 0.5, info, (Boolean)this.ignoreTerrain.getValue());
                if (!((double)damagePlayer > damage)) continue;
                damage = damagePlayer;
                coords = temp;
                quit = true;
            }
            AutoCrystalRewrite.mc.field_71441_e.func_175656_a(blockPos, toReplace.func_176223_P());
            if (coords != null) {
                this.placeCrystal(coords, hand, false);
                this.placedCrystal = true;
            }
            if (quit) break;
        }
    }

    private void renderCustomOutline(AxisAlignedBB hole, boolean place, int alpha) {
        ArrayList<GSColor> colors = new ArrayList<GSColor>();
        if (place) {
            switch ((String)this.NVerticesOutlineBot.getValue()) {
                case "1": {
                    colors.add(new GSColor(this.firstVerticeOutlineBot.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeOutlineBot.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeOutlineBot.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeOutlineBot.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                    break;
                }
                case "2": {
                    if (((String)this.direction2OutLineBot.getValue()).equals("X")) {
                        colors.add(new GSColor(this.firstVerticeOutlineBot.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.secondVerticeOutlineBot.getValue(), alpha == -1 ? this.secondVerticeOutlineBot.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.firstVerticeOutlineBot.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.secondVerticeOutlineBot.getValue(), alpha == -1 ? this.secondVerticeOutlineBot.getColor().getAlpha() : alpha));
                        break;
                    }
                    colors.add(new GSColor(this.firstVerticeOutlineBot.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeOutlineBot.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeOutlineBot.getValue(), alpha == -1 ? this.secondVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeOutlineBot.getValue(), alpha == -1 ? this.secondVerticeOutlineBot.getColor().getAlpha() : alpha));
                    break;
                }
                case "4": {
                    colors.add(new GSColor(this.firstVerticeOutlineBot.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeOutlineBot.getValue(), alpha == -1 ? this.secondVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.thirdVerticeOutlineBot.getValue(), alpha == -1 ? this.thirdVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.fourVerticeOutlineBot.getValue(), alpha == -1 ? this.fourVerticeOutlineBot.getColor().getAlpha() : alpha));
                }
            }
            switch ((String)this.NVerticesOutlineTop.getValue()) {
                case "1": {
                    colors.add(new GSColor(this.firstVerticeOutlineTop.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeOutlineTop.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeOutlineTop.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeOutlineTop.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                    break;
                }
                case "2": {
                    if (((String)this.direction2OutLineTop.getValue()).equals("X")) {
                        colors.add(new GSColor(this.firstVerticeOutlineTop.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.secondVerticeOutlineTop.getValue(), alpha == -1 ? this.secondVerticeOutlineBot.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.firstVerticeOutlineTop.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.secondVerticeOutlineTop.getValue(), alpha == -1 ? this.secondVerticeOutlineBot.getColor().getAlpha() : alpha));
                        break;
                    }
                    colors.add(new GSColor(this.firstVerticeOutlineTop.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeOutlineTop.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeOutlineTop.getValue(), alpha == -1 ? this.secondVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeOutlineTop.getValue(), alpha == -1 ? this.secondVerticeOutlineBot.getColor().getAlpha() : alpha));
                    break;
                }
                case "4": {
                    colors.add(new GSColor(this.firstVerticeOutlineTop.getValue(), alpha == -1 ? this.firstVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeOutlineTop.getValue(), alpha == -1 ? this.secondVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.thirdVerticeOutlineTop.getValue(), alpha == -1 ? this.thirdVerticeOutlineBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.fourVerticeOutlineTop.getValue(), alpha == -1 ? this.fourVerticeOutlineBot.getColor().getAlpha() : alpha));
                }
            }
        } else {
            switch ((String)this.NVerticesOutlineBotbr.getValue()) {
                case "1": {
                    colors.add(new GSColor(this.firstVerticeOutlineBotbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeOutlineBotbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeOutlineBotbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeOutlineBotbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    break;
                }
                case "2": {
                    if (((String)this.direction2OutLineBotbr.getValue()).equals("X")) {
                        colors.add(new GSColor(this.firstVerticeOutlineBotbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.secondVerticeOutlineBotbr.getValue(), alpha == -1 ? this.secondVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.firstVerticeOutlineBotbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.secondVerticeOutlineBotbr.getValue(), alpha == -1 ? this.secondVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                        break;
                    }
                    colors.add(new GSColor(this.firstVerticeOutlineBotbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeOutlineBotbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeOutlineBotbr.getValue(), alpha == -1 ? this.secondVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeOutlineBotbr.getValue(), alpha == -1 ? this.secondVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    break;
                }
                case "4": {
                    colors.add(new GSColor(this.firstVerticeOutlineBotbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeOutlineBotbr.getValue(), alpha == -1 ? this.secondVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.thirdVerticeOutlineBotbr.getValue(), alpha == -1 ? this.thirdVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.fourVerticeOutlineBotbr.getValue(), alpha == -1 ? this.fourVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                }
            }
            switch ((String)this.NVerticesOutlineTopbr.getValue()) {
                case "1": {
                    colors.add(new GSColor(this.firstVerticeOutlineTopbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeOutlineTopbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeOutlineTopbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeOutlineTopbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    break;
                }
                case "2": {
                    if (((String)this.direction2OutLineTopbr.getValue()).equals("X")) {
                        colors.add(new GSColor(this.firstVerticeOutlineTopbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.secondVerticeOutlineTopbr.getValue(), alpha == -1 ? this.secondVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.firstVerticeOutlineTopbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.secondVerticeOutlineTopbr.getValue(), alpha == -1 ? this.secondVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                        break;
                    }
                    colors.add(new GSColor(this.firstVerticeOutlineTopbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeOutlineTopbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeOutlineTopbr.getValue(), alpha == -1 ? this.secondVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeOutlineTopbr.getValue(), alpha == -1 ? this.secondVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    break;
                }
                case "4": {
                    colors.add(new GSColor(this.firstVerticeOutlineTopbr.getValue(), alpha == -1 ? this.firstVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeOutlineTopbr.getValue(), alpha == -1 ? this.secondVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.thirdVerticeOutlineTopbr.getValue(), alpha == -1 ? this.thirdVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.fourVerticeOutlineTopbr.getValue(), alpha == -1 ? this.fourVerticeOutlineBotbr.getColor().getAlpha() : alpha));
                }
            }
        }
        RenderUtil.drawBoundingBox(hole, (double)((Integer)this.outlineWidthpl.getValue()).intValue(), colors.toArray(new GSColor[7]));
    }

    void renderFillCustom(AxisAlignedBB hole, int mask, boolean place, int alpha) {
        ArrayList<GSColor> colors = new ArrayList<GSColor>();
        if (place) {
            switch ((String)this.NVerticesFillBot.getValue()) {
                case "1": {
                    colors.add(new GSColor(this.firstVerticeFillBot.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeFillBot.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeFillBot.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeFillBot.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                    break;
                }
                case "2": {
                    if (((String)this.direction2FillBot.getValue()).equals("X")) {
                        colors.add(new GSColor(this.firstVerticeFillBot.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.secondVerticeFillBot.getValue(), alpha == -1 ? this.secondVerticeFillBot.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.firstVerticeFillBot.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.secondVerticeFillBot.getValue(), alpha == -1 ? this.secondVerticeFillBot.getColor().getAlpha() : alpha));
                        break;
                    }
                    colors.add(new GSColor(this.firstVerticeFillBot.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeFillBot.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeFillBot.getValue(), alpha == -1 ? this.secondVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeFillBot.getValue(), alpha == -1 ? this.secondVerticeFillBot.getColor().getAlpha() : alpha));
                    break;
                }
                case "4": {
                    colors.add(new GSColor(this.firstVerticeFillBot.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeFillBot.getValue(), alpha == -1 ? this.secondVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.thirdVerticeFillBot.getValue(), alpha == -1 ? this.thirdVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.fourVerticeFillBot.getValue(), alpha == -1 ? this.fourVerticeFillBot.getColor().getAlpha() : alpha));
                }
            }
            switch ((String)this.NVerticesFillTop.getValue()) {
                case "1": {
                    colors.add(new GSColor(this.firstVerticeFillTop.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeFillTop.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeFillTop.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeFillTop.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                    break;
                }
                case "2": {
                    if (((String)this.direction2FillTop.getValue()).equals("X")) {
                        colors.add(new GSColor(this.firstVerticeFillTop.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.secondVerticeFillTop.getValue(), alpha == -1 ? this.secondVerticeFillBot.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.firstVerticeFillTop.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.secondVerticeFillTop.getValue(), alpha == -1 ? this.secondVerticeFillBot.getColor().getAlpha() : alpha));
                        break;
                    }
                    colors.add(new GSColor(this.firstVerticeFillTop.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeFillTop.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeFillTop.getValue(), alpha == -1 ? this.secondVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeFillTop.getValue(), alpha == -1 ? this.secondVerticeFillBot.getColor().getAlpha() : alpha));
                    break;
                }
                case "4": {
                    colors.add(new GSColor(this.firstVerticeFillTop.getValue(), alpha == -1 ? this.firstVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeFillTop.getValue(), alpha == -1 ? this.secondVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.thirdVerticeFillTop.getValue(), alpha == -1 ? this.thirdVerticeFillBot.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.fourVerticeFillTop.getValue(), alpha == -1 ? this.fourVerticeFillBot.getColor().getAlpha() : alpha));
                }
            }
        } else {
            switch ((String)this.NVerticesFillBotbr.getValue()) {
                case "1": {
                    colors.add(new GSColor(this.firstVerticeFillBotbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeFillBotbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeFillBotbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeFillBotbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    break;
                }
                case "2": {
                    if (((String)this.direction2FillBotbr.getValue()).equals("X")) {
                        colors.add(new GSColor(this.firstVerticeFillBotbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.secondVerticeFillBotbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.firstVerticeFillBotbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.secondVerticeFillBotbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                        break;
                    }
                    colors.add(new GSColor(this.firstVerticeFillBotbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeFillBotbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeFillBotbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeFillBotbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    break;
                }
                case "4": {
                    colors.add(new GSColor(this.firstVerticeFillBotbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeFillBotbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.thirdVerticeFillBotbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.fourVerticeFillBotbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                }
            }
            switch ((String)this.NVerticesFillTopbr.getValue()) {
                case "1": {
                    colors.add(new GSColor(this.firstVerticeFillTopbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeFillTopbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeFillTopbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeFillTopbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    break;
                }
                case "2": {
                    if (((String)this.direction2FillTopbr.getValue()).equals("X")) {
                        colors.add(new GSColor(this.firstVerticeFillTopbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.secondVerticeFillTopbr.getValue(), alpha == -1 ? this.secondVerticeFillBotbr.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.firstVerticeFillTopbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                        colors.add(new GSColor(this.secondVerticeFillTopbr.getValue(), alpha == -1 ? this.secondVerticeFillBotbr.getColor().getAlpha() : alpha));
                        break;
                    }
                    colors.add(new GSColor(this.firstVerticeFillTopbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.firstVerticeFillTopbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeFillTopbr.getValue(), alpha == -1 ? this.secondVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeFillTopbr.getValue(), alpha == -1 ? this.secondVerticeFillBotbr.getColor().getAlpha() : alpha));
                    break;
                }
                case "4": {
                    colors.add(new GSColor(this.firstVerticeFillTopbr.getValue(), alpha == -1 ? this.firstVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.secondVerticeFillTopbr.getValue(), alpha == -1 ? this.secondVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.thirdVerticeFillTopbr.getValue(), alpha == -1 ? this.thirdVerticeFillBotbr.getColor().getAlpha() : alpha));
                    colors.add(new GSColor(this.fourVerticeFillTopbr.getValue(), alpha == -1 ? this.fourVerticeFillBotbr.getColor().getAlpha() : alpha));
                }
            }
        }
        RenderUtil.drawBoxProva2(hole, true, 1.0, colors.toArray(new GSColor[7]), mask, true);
    }

    List<EntityPlayer> getPlayersThreaded(int nThread, List<EntityPlayer> players, PredictUtil.PredictSettings settings, int timeOut) {
        List<List<EntityPlayer>> list = this.splitListEntity(players, nThread);
        ArrayList<EntityPlayer> output = new ArrayList<EntityPlayer>();
        LinkedList<Future<List>> futures = new LinkedList<Future<List>>();
        int i = 0;
        while (i < nThread) {
            int n = i++;
            futures.add(this.executor.submit(() -> this.getPredicts((List)list.get(finalI), settings)));
        }
        for (Future future : futures) {
            try {
                List temp = (List)future.get(timeOut, TimeUnit.MILLISECONDS);
                if (temp == null) continue;
                output.addAll(temp);
            }
            catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }
        return output;
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        this.highestId = -10000;
    }

    void updateHighestID() {
        for (Entity entity : AutoCrystalRewrite.mc.field_71441_e.field_72996_f) {
            if (entity.func_145782_y() <= this.highestId) continue;
            this.highestId = entity.func_145782_y();
        }
    }

    void checkID(int id) {
        if (id > this.highestId) {
            this.highestId = id;
        }
    }

    void attackID(BlockPos pos, int id) {
        block6: {
            try {
                Entity entity = AutoCrystalRewrite.mc.field_71441_e.func_73045_a(id);
                if (entity == null || entity instanceof EntityEnderCrystal) {
                    CPacketUseEntity attack = new CPacketUseEntity();
                    ((AccessorCPacketAttack)attack).setId(id);
                    ((AccessorCPacketAttack)attack).setAction(CPacketUseEntity.Action.ATTACK);
                    AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)attack);
                    AutoCrystalRewrite.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                }
            }
            catch (Exception e) {
                PistonCrystal.printDebug("Prevented a crash from the ca. If this repet, spam me in dm", true);
                Logger LOGGER = LogManager.getLogger((String)"GameSense");
                LOGGER.error("[AutoCrystalRewrite] error during the creation of the structure.");
                if (e.getMessage() != null) {
                    LOGGER.error("[AutoCrystalRewrite] error message: " + e.getClass().getName() + " " + e.getMessage());
                } else {
                    LOGGER.error("[AutoCrystalRewrite] cannot find the cause");
                }
                boolean i5 = false;
                if (e.getStackTrace().length == 0) break block6;
                LOGGER.error("[AutoCrystalRewrite] StackTrace Start");
                for (StackTraceElement errorMess : e.getStackTrace()) {
                    LOGGER.error("[AutoCrystalRewrite] " + errorMess.toString());
                }
                LOGGER.error("[AutoCrystalRewrite] StackTrace End");
            }
        }
    }

    class managerClassRenderBlocks {
        ArrayList<renderBlock> blocks = new ArrayList();

        managerClassRenderBlocks() {
        }

        void update(int time) {
            this.blocks.removeIf(e -> System.currentTimeMillis() - ((renderBlock)e).start > (long)time);
        }

        void render() {
            this.blocks.forEach(e -> {
                if (AutoCrystalRewrite.this.bestBreak.crystal != null && AutoCrystalRewrite.this.sameBlockPos(((renderBlock)e).pos, AutoCrystalRewrite.this.bestBreak.crystal.func_180425_c().func_177982_a(0, -1, 0)) || AutoCrystalRewrite.this.bestPlace.crystal != null && AutoCrystalRewrite.this.sameBlockPos(((renderBlock)e).pos, AutoCrystalRewrite.this.bestPlace.crystal)) {
                    e.resetTime();
                } else {
                    e.render();
                }
            });
        }

        void addRender(boolean place, BlockPos pos) {
            boolean render = true;
            for (renderBlock block : this.blocks) {
                if (!AutoCrystalRewrite.this.sameBlockPos(block.pos, pos) || block.place != place) continue;
                render = false;
                block.resetTime();
                break;
            }
            if (render) {
                this.blocks.add(new renderBlock(place, pos));
            }
        }
    }

    class renderBlock {
        private final BlockPos pos;
        private long start;
        private final boolean place;

        public renderBlock(boolean place, BlockPos pos) {
            this.place = place;
            this.start = System.currentTimeMillis();
            this.pos = pos;
        }

        void resetTime() {
            this.start = System.currentTimeMillis();
        }

        void render() {
            if (this.place) {
                AutoCrystalRewrite.this.drawBoxMain((String)AutoCrystalRewrite.this.typePlace.getValue(), this.pos, (String)AutoCrystalRewrite.this.placeDimension.getValue(), (Double)AutoCrystalRewrite.this.slabHeightPlace.getValue(), true, this.returnGradient());
            } else {
                AutoCrystalRewrite.this.drawBoxMain((String)AutoCrystalRewrite.this.typeBreak.getValue(), this.pos, (String)AutoCrystalRewrite.this.breakDimension.getValue(), (Double)AutoCrystalRewrite.this.slabHeightBreak.getValue(), false, this.returnGradient());
            }
        }

        public int returnGradient() {
            int endFade;
            int startFade;
            long end = this.start + (long)((Integer)AutoCrystalRewrite.this.lifeTime.getValue()).intValue();
            int result = (int)((float)(end - System.currentTimeMillis()) / (float)(end - this.start) * 100.0f);
            if (result < 0) {
                result = 0;
            }
            if (this.place) {
                startFade = AutoCrystalRewrite.this.firstVerticeFillBot.getValue().getAlpha();
                endFade = (Integer)AutoCrystalRewrite.this.endFadePlace.getValue();
            } else {
                startFade = AutoCrystalRewrite.this.firstVerticeFillBotbr.getValue().getAlpha();
                endFade = (Integer)AutoCrystalRewrite.this.endFadeBreak.getValue();
            }
            return (int)(((double)startFade - (double)endFade) * ((double)result / 100.0));
        }
    }

    class crystalPlaced {
        ArrayList<crystalTime> endCrystalPlaced = new ArrayList();

        crystalPlaced() {
        }

        void addCrystal(BlockPos pos) {
            this.endCrystalPlaced.removeIf(check -> AutoCrystalRewrite.this.sameBlockPos(check.posCrystal, pos));
            this.endCrystalPlaced.add(new crystalTime(pos, 5000));
        }

        boolean hasCrystal(EntityEnderCrystal crystal) {
            BlockPos now = crystal.func_180425_c().func_177982_a(0, -1, 0);
            return this.endCrystalPlaced.stream().anyMatch(check -> AutoCrystalRewrite.this.sameBlockPos(check.posCrystal, now));
        }

        boolean hasCrystal(BlockPos crystal) {
            return this.endCrystalPlaced.stream().anyMatch(check -> AutoCrystalRewrite.this.sameBlockPos(check.posCrystal, crystal));
        }

        void updateCrystals() {
            for (int i = 0; i < this.endCrystalPlaced.size(); ++i) {
                if (!this.endCrystalPlaced.get(i).isReady()) continue;
                this.endCrystalPlaced.remove(i);
                --i;
            }
        }
    }

    class packetBlock {
        public final BlockPos block;
        public int tick;
        public final int startTick;
        public final int finishTish;

        public packetBlock(BlockPos block, int startTick, int finishTick) {
            this.block = block;
            this.tick = 0;
            this.startTick = startTick;
            this.finishTish = finishTick;
        }

        boolean update() {
            ++this.tick;
            if (this.tick > this.startTick) {
                if (this.tick > this.finishTish) {
                    return false;
                }
                if (AutoCrystalRewrite.this.stopGapple(false)) {
                    return true;
                }
                EnumHand hand = AutoCrystalRewrite.this.getHandCrystal();
                if (hand == null) {
                    return true;
                }
                if (!CrystalUtil.canPlaceCrystal(this.block, (Boolean)AutoCrystalRewrite.this.newPlace.getValue())) {
                    return true;
                }
                AutoCrystalRewrite.this.placeCrystal(this.block, hand, false);
                AutoCrystalRewrite.this.placedCrystal = true;
            }
            return true;
        }
    }

    static class slowBreakPlayers {
        final String name;
        int tick = Integer.MAX_VALUE;
        int finalTick;
        long start = Long.MAX_VALUE;
        int finish;

        public slowBreakPlayers(String name, int finalTick, boolean ignored) {
            this.name = name;
            this.finalTick = finalTick;
            this.tick = 0;
        }

        public slowBreakPlayers(String name, int finish) {
            this.name = name;
            this.finish = finish;
            this.start = System.currentTimeMillis();
        }

        boolean update() {
            if (this.tick == Integer.MAX_VALUE) {
                return System.currentTimeMillis() - this.start >= (long)this.finish;
            }
            return ++this.tick >= this.finalTick;
        }
    }

    class crystalPlaceWait {
        ArrayList<crystalTime> listWait = new ArrayList();

        crystalPlaceWait() {
        }

        void addCrystal(BlockPos cryst, int finish) {
            this.listWait.add(new crystalTime(cryst, finish));
        }

        void addCrystal(BlockPos cryst, int tick, int tickFinish) {
            this.removeCrystal(Double.valueOf(cryst.func_177958_n()), Double.valueOf(cryst.func_177956_o()), Double.valueOf(cryst.func_177952_p()));
            this.listWait.add(new crystalTime(cryst, tick, tickFinish));
        }

        void addCrystalId(BlockPos cryst, int id, int finish) {
            this.listWait.add(new crystalTime(cryst, id, finish, false));
        }

        void addCrystalId(BlockPos cryst, int id, int tick, int tickFinish) {
            this.removeCrystal(Double.valueOf(cryst.func_177958_n()), Double.valueOf(cryst.func_177956_o()), Double.valueOf(cryst.func_177952_p()));
            this.listWait.add(new crystalTime(cryst, id, tick, tickFinish));
        }

        boolean removeCrystal(Double x, Double y, Double z) {
            int i = this.CrystalExists(new BlockPos(x.doubleValue(), y.doubleValue(), z.doubleValue()));
            if (i != -1) {
                this.listWait.remove(i);
                return true;
            }
            return false;
        }

        int CrystalExists(BlockPos pos) {
            for (int i = 0; i < this.listWait.size(); ++i) {
                if (!AutoCrystalRewrite.this.sameBlockPos(pos, this.listWait.get((int)i).posCrystal)) continue;
                return i;
            }
            return -1;
        }

        boolean crystalIdExists(int id) {
            try {
                return this.listWait.stream().anyMatch(e -> e.idCrystal == id);
            }
            catch (NullPointerException | ConcurrentModificationException ignored) {
                return false;
            }
        }

        void updateCrystals() {
            for (int i = 0; i < this.listWait.size(); ++i) {
                try {
                    if (!this.listWait.get(i).isReady()) continue;
                    this.listWait.remove(i);
                    --i;
                    continue;
                }
                catch (NullPointerException e) {
                    this.listWait.remove(i);
                    --i;
                }
            }
        }

        int countCrystals() {
            return this.listWait.size();
        }
    }

    static class crystalTime {
        BlockPos posCrystal;
        int idCrystal = -100;
        final int type;
        int tick;
        int finishTick;
        long start;
        int finish;

        public crystalTime(BlockPos posCrystal, int tick, int finishTick) {
            this.posCrystal = posCrystal;
            this.tick = tick;
            this.type = 0;
            this.finishTick = finishTick;
        }

        public crystalTime(BlockPos posCrystal, int finish) {
            this.posCrystal = posCrystal;
            this.start = System.currentTimeMillis();
            this.finish = finish;
            this.type = 1;
        }

        public crystalTime(BlockPos pos, int id, int finish, boolean lol) {
            this.posCrystal = pos;
            this.idCrystal = id;
            this.start = System.currentTimeMillis();
            this.finish = finish;
            this.type = 1;
        }

        public crystalTime(BlockPos pos, int id, int tick, int finishTick) {
            this.posCrystal = pos;
            this.idCrystal = id;
            this.tick = tick;
            this.type = 0;
            this.finishTick = finishTick;
        }

        boolean isReady() {
            switch (this.type) {
                case 0: {
                    return ++this.tick >= this.finishTick;
                }
                case 1: {
                    return System.currentTimeMillis() - this.start >= (long)this.finish;
                }
            }
            return true;
        }
    }

    static class display {
        AxisAlignedBB box;
        BlockPos block;
        final GSColor color;
        int width;
        int type;
        String[] text;
        double yDiff;

        public display(AxisAlignedBB box, GSColor color, int width) {
            this.box = box;
            this.color = color;
            this.width = width;
            this.type = 0;
        }

        public display(String text, BlockPos block, GSColor color, double yDiff) {
            this.text = new String[]{text};
            this.block = block;
            this.color = color;
            this.type = 1;
            this.yDiff = yDiff;
        }

        void draw() {
            switch (this.type) {
                case 0: {
                    RenderUtil.drawBoundingBox(this.box, (double)this.width, this.color);
                    break;
                }
                case 1: {
                    RenderUtil.drawNametag((double)this.block.func_177958_n() + 0.5, (double)this.block.func_177956_o() + this.yDiff, (double)this.block.func_177952_p() + 0.5, this.text, this.color, 1);
                }
            }
        }
    }

    static class Sortbyroll
    implements Comparator<EntityPlayer> {
        Sortbyroll() {
        }

        @Override
        public int compare(EntityPlayer o1, EntityPlayer o2) {
            return (int)(o1.func_70068_e((Entity)mc.field_71439_g) - o2.func_70068_e((Entity)mc.field_71439_g));
        }
    }
}

