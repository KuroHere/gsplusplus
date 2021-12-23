/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.Phase;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.util.player.SpoofRotationUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.api.util.world.combat.CrystalUtil;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AutoCrystalRewrite;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.client.module.modules.misc.AutoGG;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Module.Declaration(name="PistonCrystal", category=Category.Combat, priority=999)
public class PistonCrystal
extends Module {
    BooleanSetting targetSection = this.registerBoolean("Target Section", true);
    ModeSetting target = this.registerMode("Target", Arrays.asList("Nearest", "Looking"), "Nearest", () -> (Boolean)this.targetSection.getValue());
    IntegerSetting minHealth = this.registerInteger("Min Health", 8, 0, 20, () -> (Boolean)this.targetSection.getValue());
    DoubleSetting enemyRange = this.registerDouble("Range", 4.9, 0.0, 6.0, () -> (Boolean)this.targetSection.getValue());
    BooleanSetting placeBreakSection = this.registerBoolean("Place Break Section", true);
    ModeSetting breakType = this.registerMode("Type", Arrays.asList("Swing", "Packet"), "Swing", () -> (Boolean)this.placeBreakSection.getValue());
    ModeSetting placeMode = this.registerMode("Place", Arrays.asList("Torch", "Block", "Both"), "Torch", () -> (Boolean)this.placeBreakSection.getValue());
    DoubleSetting crystalDeltaBreak = this.registerDouble("Center Break", 0.1, 0.0, 0.5, () -> (Boolean)this.placeBreakSection.getValue());
    IntegerSetting crystalPlaceTry = this.registerInteger("Crystal Place Try", 15, 2, 30, () -> (Boolean)this.placeBreakSection.getValue());
    IntegerSetting blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 20, () -> (Boolean)this.placeBreakSection.getValue());
    IntegerSetting maxYincr = this.registerInteger("Max Y", 3, 0, 5, () -> (Boolean)this.placeBreakSection.getValue());
    BooleanSetting blockPlayer = this.registerBoolean("Trap Player", true, () -> (Boolean)this.placeBreakSection.getValue());
    BooleanSetting confirmBreak = this.registerBoolean("No Glitch Break", true, () -> (Boolean)this.placeBreakSection.getValue());
    BooleanSetting confirmPlace = this.registerBoolean("No Glitch Place", true, () -> (Boolean)this.placeBreakSection.getValue());
    BooleanSetting delaySection = this.registerBoolean("Delay Section", true);
    IntegerSetting supBlocksDelay = this.registerInteger("Surround Delay", 4, 0, 20, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting startDelay = this.registerInteger("Start Delay", 4, 0, 20, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting pistonDelay = this.registerInteger("Piston Delay", 2, 0, 20, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting crystalDelay = this.registerInteger("Crystal Delay", 2, 0, 20, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting redstoneDelay = this.registerInteger("Redstone Delay", 0, 0, 20, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting midHitDelay = this.registerInteger("Mid Hit Delay", 5, 0, 20, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting hitDelay = this.registerInteger("Hit Delay", 2, 0, 20, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting stuckDetector = this.registerInteger("Stuck Check", 35, 0, 200, () -> (Boolean)this.delaySection.getValue());
    BooleanSetting rotationSection = this.registerBoolean("Rotation Section", true);
    BooleanSetting packetReducer = this.registerBoolean("Packet Reducer", false, () -> (Boolean)this.rotationSection.getValue());
    BooleanSetting rotate = this.registerBoolean("Rotate", false, () -> (Boolean)this.rotationSection.getValue());
    BooleanSetting preRotation = this.registerBoolean("Pre Rotation", false, () -> (Boolean)this.rotationSection.getValue());
    BooleanSetting forceRotation = this.registerBoolean("Force Rotation", false, () -> (Boolean)this.rotationSection.getValue());
    IntegerSetting preRotationDelay = this.registerInteger("Pre Rotation Delay", 0, 0, 20, () -> (Boolean)this.rotationSection.getValue());
    IntegerSetting afterRotationDelay = this.registerInteger("After Rotation Delay", 0, 0, 20, () -> (Boolean)this.rotationSection.getValue());
    BooleanSetting miscSection = this.registerBoolean("Misc Section", true);
    BooleanSetting allowCheapMode = this.registerBoolean("Cheap Mode", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting betterPlacement = this.registerBoolean("Better Place", true, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting bypassObsidian = this.registerBoolean("Bypass Obsidian", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting antiWeakness = this.registerBoolean("Anti Weakness", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting debugMode = this.registerBoolean("Debug Mode", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting speedMeter = this.registerBoolean("Speed Meter", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting chatMsg = this.registerBoolean("Chat Msgs", true, () -> (Boolean)this.miscSection.getValue());
    private boolean noMaterials = false;
    private boolean hasMoved = false;
    private boolean isSneaking = false;
    private boolean yUnder = false;
    private boolean isHole = true;
    private boolean enoughSpace = true;
    private boolean redstoneBlockMode = false;
    private boolean fastModeActive = false;
    private boolean broken;
    private boolean brokenCrystalBug;
    private boolean brokenRedstoneTorch;
    private boolean stoppedCa;
    private boolean deadPl;
    private boolean rotationPlayerMoved;
    private boolean preRotationBol = false;
    private boolean minHp;
    private boolean itemCrystal;
    private int oldSlot = -1;
    private int stage;
    private int delayTimeTicks;
    private int stuck = 0;
    private int hitTryTick;
    private int round = 0;
    private int nCrystal;
    private int redstoneTickDelay;
    private int preRotationTick;
    private int afterRotationTick;
    private int placeTry;
    private long startTime;
    private long endTime;
    private int[] slot_mat;
    private int[] delayTable;
    private int[] meCoordsInt;
    private int[] enemyCoordsInt;
    private double[] enemyCoordsDouble;
    private structureTemp toPlace;
    int[][] disp_surblock = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}};
    Double[][] sur_block = new Double[4][3];
    private EntityPlayer aimTarget;
    @EventHandler
    private final Listener<PacketEvent.Receive> packetReceiveListener = new Listener<PacketEvent.Receive>(event -> {
        SPacketSoundEffect packet;
        if (event.getPacket() instanceof SPacketSoundEffect && (packet = (SPacketSoundEffect)event.getPacket()).func_186977_b() == SoundCategory.BLOCKS && packet.func_186978_a() == SoundEvents.field_187539_bB && (int)packet.func_149207_d() == this.enemyCoordsInt[0] && (int)packet.func_149210_f() == this.enemyCoordsInt[2]) {
            this.stage = 1;
        }
    }, new Predicate[0]);
    int lenTable;
    Vec3d lastHitVec;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
        if (event.getPhase() != Phase.PRE || !((Boolean)this.rotate.getValue()).booleanValue() || this.lastHitVec == null || !((Boolean)this.forceRotation.getValue()).booleanValue()) {
            return;
        }
        Vec2f rotation = RotationUtil.getRotationTo(this.lastHitVec);
        PlayerPacket packet = new PlayerPacket((Module)this, rotation);
        PlayerPacketManager.INSTANCE.addPacket(packet);
    }, new Predicate[0]);
    private final ArrayList<EnumFacing> exd = new ArrayList<EnumFacing>(){
        {
            this.add(EnumFacing.DOWN);
        }
    };
    boolean redstoneAbovePiston;

    @Override
    public void onEnable() {
        SpoofRotationUtil.ROTATION_UTIL.onEnable();
        this.initValues();
        if (this.getAimTarget()) {
            return;
        }
        this.playerChecks();
    }

    private boolean getAimTarget() {
        this.aimTarget = ((String)this.target.getValue()).equals("Nearest") ? PlayerUtil.findClosestTarget((Double)this.enemyRange.getValue(), this.aimTarget) : PlayerUtil.findLookingPlayer((Double)this.enemyRange.getValue());
        if (this.aimTarget == null || !((String)this.target.getValue()).equals("Looking")) {
            if (!((String)this.target.getValue()).equals("Looking") && this.aimTarget == null) {
                this.disable();
            }
            return this.aimTarget == null;
        }
        return false;
    }

    private void playerChecks() {
        if (this.getMaterialsSlot()) {
            if (this.is_in_hole()) {
                this.enemyCoordsDouble = new double[]{this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v};
                this.enemyCoordsInt = new int[]{(int)this.enemyCoordsDouble[0], (int)this.enemyCoordsDouble[1], (int)this.enemyCoordsDouble[2]};
                this.meCoordsInt = new int[]{(int)PistonCrystal.mc.field_71439_g.field_70165_t, (int)PistonCrystal.mc.field_71439_g.field_70163_u, (int)PistonCrystal.mc.field_71439_g.field_70161_v};
                this.antiAutoDestruction();
                this.enoughSpace = this.createStructure();
            } else {
                this.isHole = false;
            }
        } else {
            this.noMaterials = true;
        }
    }

    private void antiAutoDestruction() {
        if (this.redstoneBlockMode || ((Boolean)this.rotate.getValue()).booleanValue()) {
            this.betterPlacement.setValue(false);
        }
    }

    private void initValues() {
        this.preRotationBol = false;
        this.afterRotationTick = 0;
        this.preRotationTick = 0;
        this.lastHitVec = null;
        this.aimTarget = null;
        this.delayTable = new int[]{(Integer)this.startDelay.getValue(), (Integer)this.supBlocksDelay.getValue(), (Integer)this.pistonDelay.getValue(), (Integer)this.crystalDelay.getValue(), (Integer)this.hitDelay.getValue()};
        this.lenTable = this.delayTable.length;
        this.toPlace = new structureTemp(0.0, 0, null);
        this.minHp = true;
        this.isHole = true;
        this.fastModeActive = false;
        this.redstoneBlockMode = false;
        this.yUnder = false;
        this.brokenRedstoneTorch = false;
        this.brokenCrystalBug = false;
        this.broken = false;
        this.deadPl = false;
        this.rotationPlayerMoved = false;
        this.itemCrystal = false;
        this.hasMoved = false;
        this.slot_mat = new int[]{-1, -1, -1, -1, -1, -1};
        this.stuck = 0;
        this.delayTimeTicks = 0;
        this.stage = 0;
        if (PistonCrystal.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        if (((Boolean)this.chatMsg.getValue()).booleanValue()) {
            PistonCrystal.printDebug("PistonCrystal turned ON!", false);
        }
        this.oldSlot = PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c;
        this.stoppedCa = false;
        if (ModuleManager.isModuleEnabled(AutoCrystalRewrite.class)) {
            AutoCrystalRewrite.stopAC = true;
            this.stoppedCa = true;
        }
        if (((Boolean)this.debugMode.getValue()).booleanValue() || ((Boolean)this.speedMeter.getValue()).booleanValue()) {
            PistonCrystal.printDebug("Started pistonCrystal n^" + ++this.round, false);
            this.startTime = System.currentTimeMillis();
            this.nCrystal = 0;
        }
    }

    @Override
    public void onDisable() {
        SpoofRotationUtil.ROTATION_UTIL.onDisable();
        if (PistonCrystal.mc.field_71439_g == null) {
            return;
        }
        String output = "";
        String materialsNeeded = "";
        if (this.aimTarget == null) {
            output = "No target found...";
        } else if (this.yUnder) {
            output = String.format("Sorry but you cannot be 2+ blocks under the enemy or %d above...", this.maxYincr.getValue());
        } else if (this.noMaterials) {
            output = "No Materials Detected...";
            materialsNeeded = this.getMissingMaterials();
        } else if (!this.isHole) {
            output = "The enemy is not in a hole...";
        } else if (!this.enoughSpace) {
            output = "Not enough space...";
        } else if (this.hasMoved) {
            output = "Out of range...";
        } else if (this.deadPl) {
            output = "Enemy is dead, gg! ";
        } else if (this.rotationPlayerMoved) {
            output = "You cannot move from your hole if you have rotation on. ";
        } else if (!this.minHp) {
            output = "Your hp is low";
        } else if (this.itemCrystal) {
            output = "An item is where the crystal should be placed";
        }
        this.setDisabledMessage(output + "PistonCrystal turned OFF!");
        if (!materialsNeeded.equals("")) {
            this.setDisabledMessage("Materials missing:" + materialsNeeded);
        }
        if (this.stoppedCa) {
            AutoCrystalRewrite.stopAC = false;
            this.stoppedCa = false;
        }
        if (this.isSneaking) {
            PistonCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)PistonCrystal.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        if (this.oldSlot != PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c && this.oldSlot != -1) {
            PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
            this.oldSlot = -1;
        }
        this.noMaterials = false;
        AutoCrystalRewrite.stopAC = false;
        if (((Boolean)this.debugMode.getValue()).booleanValue() || ((Boolean)this.speedMeter.getValue()).booleanValue()) {
            PistonCrystal.printDebug("Ended pistonCrystal n^" + this.round, false);
        }
    }

    private String getMissingMaterials() {
        StringBuilder output = new StringBuilder();
        if (this.slot_mat[0] == -1) {
            output.append(" Obsidian");
        }
        if (this.slot_mat[1] == -1) {
            output.append(" Piston");
        }
        if (this.slot_mat[2] == -1) {
            output.append(" Crystals");
        }
        if (this.slot_mat[3] == -1) {
            output.append(" Redstone");
        }
        if (((Boolean)this.antiWeakness.getValue()).booleanValue() && this.slot_mat[4] == -1) {
            output.append(" Sword");
        }
        if (this.redstoneBlockMode && this.slot_mat[5] == -1) {
            output.append(" Pick");
        }
        return output.toString();
    }

    @Override
    public void onUpdate() {
        if (PistonCrystal.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        if (this.stage >= this.lenTable) {
            this.stage = 0;
        }
        if (this.delayTimeTicks < this.delayTable[this.stage]) {
            ++this.delayTimeTicks;
            return;
        }
        this.delayTimeTicks = 0;
        SpoofRotationUtil.ROTATION_UTIL.shouldSpoofAngles(true);
        if (this.enemyCoordsDouble == null || this.aimTarget == null) {
            if (this.aimTarget == null) {
                this.aimTarget = PlayerUtil.findLookingPlayer((Double)this.enemyRange.getValue());
                if (this.aimTarget != null) {
                    if (ModuleManager.isModuleEnabled(AutoGG.class)) {
                        AutoGG.INSTANCE.addTargetedPlayer(this.aimTarget.func_70005_c_());
                    }
                    this.playerChecks();
                }
            } else {
                this.checkVariable();
            }
            return;
        }
        if (this.aimTarget.field_70128_L) {
            this.deadPl = true;
        }
        if (PlayerUtil.getHealth() <= (float)((Integer)this.minHealth.getValue()).intValue()) {
            this.minHp = false;
        }
        if (((Boolean)this.rotate.getValue()).booleanValue() && (int)PistonCrystal.mc.field_71439_g.field_70165_t != this.meCoordsInt[0] && (int)PistonCrystal.mc.field_71439_g.field_70161_v != this.meCoordsInt[2]) {
            this.rotationPlayerMoved = true;
        }
        if ((int)this.aimTarget.field_70165_t != (int)this.enemyCoordsDouble[0] || (int)this.aimTarget.field_70161_v != (int)this.enemyCoordsDouble[2]) {
            this.hasMoved = true;
        }
        if (this.checkVariable()) {
            return;
        }
        if (this.placeSupport()) {
            switch (this.stage) {
                case 1: {
                    this.placeTry = 0;
                    if (((Boolean)this.confirmBreak.getValue()).booleanValue() && (this.checkCrystalPlaceExt(false) || this.checkCrystalPlaceIns() != null)) {
                        this.stage = 4;
                        break;
                    }
                    if (this.checkPistonPlace(false)) {
                        ++this.stage;
                        return;
                    }
                    if (((Boolean)this.preRotation.getValue()).booleanValue() && !this.preRotationBol) {
                        this.placeBlockThings(this.stage, false, true, false);
                        if (this.preRotationTick == (Integer)this.preRotationDelay.getValue()) {
                            this.preRotationBol = true;
                            this.preRotationTick = 0;
                        } else {
                            ++this.preRotationTick;
                            break;
                        }
                    }
                    if (this.afterRotationTick != (Integer)this.afterRotationDelay.getValue()) {
                        ++this.afterRotationTick;
                        break;
                    }
                    if (((Boolean)this.debugMode.getValue()).booleanValue()) {
                        PistonCrystal.printDebug("step 1", false);
                    }
                    if (this.fastModeActive || this.breakRedstone()) {
                        if (!this.fastModeActive || this.checkCrystalPlaceExt(true)) {
                            this.placeBlockThings(this.stage, false, false, false);
                        } else {
                            this.stage = 2;
                            this.afterRotationTick = 0;
                        }
                    }
                    this.preRotationBol = false;
                    break;
                }
                case 2: {
                    if (this.placeTry++ >= (Integer)this.crystalPlaceTry.getValue()) {
                        this.itemCrystal = true;
                        return;
                    }
                    if (this.afterRotationTick != (Integer)this.afterRotationDelay.getValue()) {
                        ++this.afterRotationTick;
                        break;
                    }
                    if (((Boolean)this.preRotation.getValue()).booleanValue() && !this.preRotationBol) {
                        this.placeBlockThings(this.stage, false, true, false);
                        if (this.preRotationTick == (Integer)this.preRotationDelay.getValue()) {
                            this.preRotationBol = true;
                            this.preRotationTick = 0;
                            break;
                        }
                        ++this.preRotationTick;
                        break;
                    }
                    if (((Boolean)this.debugMode.getValue()).booleanValue()) {
                        PistonCrystal.printDebug("step 2", false);
                    }
                    if (this.fastModeActive || !((Boolean)this.confirmPlace.getValue()).booleanValue() || this.checkPistonPlace(true)) {
                        this.placeBlockThings(this.stage, false, false, false);
                    }
                    this.redstoneTickDelay = 0;
                    this.preRotationBol = false;
                    break;
                }
                case 3: {
                    if (this.afterRotationTick != (Integer)this.afterRotationDelay.getValue()) {
                        ++this.afterRotationTick;
                        break;
                    }
                    if (((Boolean)this.preRotation.getValue()).booleanValue() && !this.preRotationBol) {
                        this.placeBlockThings(this.stage, false, true, false);
                        if (this.preRotationTick == (Integer)this.preRotationDelay.getValue()) {
                            this.preRotationBol = true;
                            this.preRotationTick = 0;
                            break;
                        }
                        ++this.preRotationTick;
                        break;
                    }
                    if (this.redstoneTickDelay++ != (Integer)this.redstoneDelay.getValue()) {
                        this.delayTimeTicks = 99;
                        break;
                    }
                    this.redstoneTickDelay = 0;
                    if (((Boolean)this.debugMode.getValue()).booleanValue()) {
                        PistonCrystal.printDebug("step 3", false);
                    }
                    if (this.fastModeActive || !((Boolean)this.confirmPlace.getValue()).booleanValue() || this.checkCrystalPlaceExt(true)) {
                        this.placeBlockThings(this.stage, true, false, false);
                        this.hitTryTick = 0;
                        if (this.fastModeActive && !this.checkPistonPlace(true)) {
                            this.stage = 1;
                        }
                    }
                    this.preRotationBol = false;
                    break;
                }
                case 4: {
                    if (this.afterRotationTick != (Integer)this.afterRotationDelay.getValue()) {
                        ++this.afterRotationTick;
                        break;
                    }
                    if (((Boolean)this.debugMode.getValue()).booleanValue()) {
                        PistonCrystal.printDebug("step 4", false);
                    }
                    this.destroyCrystalAlgo();
                    this.preRotationBol = false;
                    if (!((Boolean)this.confirmPlace.getValue()).booleanValue() || !this.checkRedstonePlace()) break;
                    this.stage = 3;
                }
            }
        }
    }

    private boolean checkRedstonePlace() {
        BlockPos targetPosPist = this.compactBlockPos(3);
        return !BlockUtil.getBlock(targetPosPist.func_177958_n(), targetPosPist.func_177956_o(), targetPosPist.func_177952_p()).getRegistryName().toString().contains("redstone");
    }

    public void destroyCrystalAlgo() {
        Entity crystal = this.checkCrystalPlaceIns();
        if (((Boolean)this.confirmBreak.getValue()).booleanValue() && this.broken && crystal == null) {
            this.stuck = 0;
            this.stage = 0;
            this.broken = false;
            if ((((Boolean)this.debugMode.getValue()).booleanValue() || ((Boolean)this.speedMeter.getValue()).booleanValue()) && ++this.nCrystal == 3) {
                this.printTimeCrystals();
            }
        }
        if (crystal != null) {
            this.breakCrystalPiston(crystal);
            if (((Boolean)this.confirmBreak.getValue()).booleanValue()) {
                this.broken = true;
            } else {
                this.stuck = 0;
                this.stage = 0;
                if ((((Boolean)this.debugMode.getValue()).booleanValue() || ((Boolean)this.speedMeter.getValue()).booleanValue()) && ++this.nCrystal == 3) {
                    this.printTimeCrystals();
                }
            }
        } else if (++this.stuck >= (Integer)this.stuckDetector.getValue()) {
            if (!this.checkPistonPlace(true)) {
                BlockPos crystPos = this.getTargetPos(this.toPlace.supportBlock + 1);
                PistonCrystal.printDebug(String.format("aim: %d %d", crystPos.func_177958_n(), crystPos.func_177952_p()), false);
                Entity crystalF = null;
                for (Entity t : PistonCrystal.mc.field_71441_e.field_72996_f) {
                    if (!(t instanceof EntityEnderCrystal) || (int)(t.field_70165_t - 0.5) != crystPos.func_177958_n() || (int)(t.field_70161_v - 0.5) != crystPos.func_177952_p()) continue;
                    crystalF = t;
                }
                if (((Boolean)this.confirmBreak.getValue()).booleanValue() && this.brokenCrystalBug && crystalF == null) {
                    this.stuck = 0;
                    this.stage = 0;
                }
                if (crystalF != null) {
                    this.breakCrystalPiston(crystalF);
                    if (((Boolean)this.confirmBreak.getValue()).booleanValue()) {
                        this.brokenCrystalBug = true;
                    } else {
                        this.stuck = 0;
                        this.stage = 0;
                    }
                }
                PistonCrystal.printDebug("Stuck detected: piston not placed", true);
                return;
            }
            boolean found = false;
            for (Entity t : PistonCrystal.mc.field_71441_e.field_72996_f) {
                if (!(t instanceof EntityEnderCrystal) || (int)t.field_70165_t != (int)this.toPlace.to_place.get((int)(this.toPlace.supportBlock + 1)).field_72450_a || (int)t.field_70161_v != (int)this.toPlace.to_place.get((int)(this.toPlace.supportBlock + 1)).field_72449_c) continue;
                found = true;
                break;
            }
            if (!found) {
                BlockPos offsetPosPist = new BlockPos(this.toPlace.to_place.get(this.toPlace.supportBlock + 2));
                BlockPos pos = new BlockPos(this.aimTarget.func_174791_d()).func_177982_a(offsetPosPist.func_177958_n(), offsetPosPist.func_177956_o(), offsetPosPist.func_177952_p());
                if (((Boolean)this.confirmBreak.getValue()).booleanValue() && this.brokenRedstoneTorch && BlockUtil.getBlock(pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p()) instanceof BlockAir) {
                    this.stage = 1;
                    this.brokenRedstoneTorch = false;
                } else {
                    EnumFacing side = BlockUtil.getPlaceableSide(pos);
                    if (side != null) {
                        this.breakRedstone();
                        if (((Boolean)this.confirmBreak.getValue()).booleanValue()) {
                            this.brokenRedstoneTorch = true;
                        } else {
                            this.stage = 1;
                            if ((((Boolean)this.debugMode.getValue()).booleanValue() || ((Boolean)this.speedMeter.getValue()).booleanValue()) && ++this.nCrystal == 3) {
                                this.printTimeCrystals();
                            }
                        }
                        PistonCrystal.printDebug("Stuck detected: crystal not placed", true);
                    }
                }
            } else {
                boolean ext = false;
                for (Entity t : PistonCrystal.mc.field_71441_e.field_72996_f) {
                    if (!(t instanceof EntityEnderCrystal) || (int)t.field_70165_t != (int)this.toPlace.to_place.get((int)(this.toPlace.supportBlock + 1)).field_72450_a || (int)t.field_70161_v != (int)this.toPlace.to_place.get((int)(this.toPlace.supportBlock + 1)).field_72449_c) continue;
                    ext = true;
                    break;
                }
                if (((Boolean)this.confirmBreak.getValue()).booleanValue() && this.brokenCrystalBug && !ext) {
                    this.stuck = 0;
                    this.stage = 0;
                    this.brokenCrystalBug = false;
                }
                if (ext) {
                    this.breakCrystalPiston(crystal);
                    if (((Boolean)this.confirmBreak.getValue()).booleanValue()) {
                        this.brokenCrystalBug = true;
                    } else {
                        this.stuck = 0;
                        this.stage = 0;
                    }
                    PistonCrystal.printDebug("Stuck detected: crystal is stuck in the moving piston", true);
                }
            }
        }
    }

    private void printTimeCrystals() {
        this.endTime = System.currentTimeMillis();
        PistonCrystal.printDebug("3 crystal, time took: " + (this.endTime - this.startTime), false);
        this.nCrystal = 0;
        this.startTime = System.currentTimeMillis();
    }

    private void breakCrystalPiston(Entity crystal) {
        if (this.hitTryTick++ < (Integer)this.midHitDelay.getValue()) {
            return;
        }
        this.hitTryTick = 0;
        if (((Boolean)this.antiWeakness.getValue()).booleanValue()) {
            PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c = this.slot_mat[4];
        }
        if (((Boolean)this.rotate.getValue()).booleanValue()) {
            SpoofRotationUtil.ROTATION_UTIL.lookAtPacket(crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v, (EntityPlayer)PistonCrystal.mc.field_71439_g);
        }
        if (((Boolean)this.forceRotation.getValue()).booleanValue()) {
            this.lastHitVec = new Vec3d(crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v);
        }
        if (((String)this.breakType.getValue()).equals("Swing")) {
            CrystalUtil.breakCrystal(crystal);
        } else if (((String)this.breakType.getValue()).equals("Packet")) {
            try {
                PistonCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketUseEntity(crystal));
                PistonCrystal.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
            }
            catch (NullPointerException nullPointerException) {
                // empty catch block
            }
        }
        if (((Boolean)this.rotate.getValue()).booleanValue()) {
            SpoofRotationUtil.ROTATION_UTIL.resetRotation();
        }
    }

    private boolean breakRedstone() {
        BlockPos offsetPosPist = new BlockPos(this.toPlace.to_place.get(this.toPlace.supportBlock + 2));
        BlockPos pos = new BlockPos(this.aimTarget.func_174791_d()).func_177982_a(offsetPosPist.func_177958_n(), offsetPosPist.func_177956_o(), offsetPosPist.func_177952_p());
        if (!(BlockUtil.getBlock(pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p()) instanceof BlockAir)) {
            this.breakBlock(pos);
            return false;
        }
        return true;
    }

    private void breakBlock(BlockPos pos) {
        EnumFacing side;
        if (this.redstoneBlockMode) {
            PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c = this.slot_mat[5];
        }
        if ((side = BlockUtil.getPlaceableSide(pos)) != null) {
            if (((Boolean)this.rotate.getValue()).booleanValue()) {
                BlockPos neighbour = pos.func_177972_a(side);
                EnumFacing opposite = side.func_176734_d();
                Vec3d hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.0, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
                BlockUtil.faceVectorPacketInstant(hitVec, true);
                if (((Boolean)this.forceRotation.getValue()).booleanValue()) {
                    this.lastHitVec = hitVec;
                }
            }
            PistonCrystal.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
            PistonCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side));
            PistonCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side));
        }
    }

    private boolean checkPistonPlace(boolean decr) {
        BlockPos targetPosPist = this.compactBlockPos(1);
        if (!(BlockUtil.getBlock(targetPosPist.func_177958_n(), targetPosPist.func_177956_o(), targetPosPist.func_177952_p()) instanceof BlockPistonBase)) {
            if (this.stage != 4 && decr) {
                --this.stage;
            }
            return false;
        }
        return true;
    }

    private boolean checkCrystalPlaceExt(boolean decr) {
        for (Entity t : PistonCrystal.mc.field_71441_e.field_72996_f) {
            if (!(t instanceof EntityEnderCrystal) || (int)t.field_70165_t != (int)(this.aimTarget.field_70165_t + this.toPlace.to_place.get((int)(this.toPlace.supportBlock + 1)).field_72450_a) || (int)t.field_70161_v != (int)(this.aimTarget.field_70161_v + this.toPlace.to_place.get((int)(this.toPlace.supportBlock + 1)).field_72449_c)) continue;
            return true;
        }
        if (decr) {
            --this.stage;
        }
        return false;
    }

    private Entity checkCrystalPlaceIns() {
        for (Entity t : PistonCrystal.mc.field_71441_e.field_72996_f) {
            if (!(t instanceof EntityEnderCrystal) || ((int)t.field_70165_t != this.enemyCoordsInt[0] || (int)(t.field_70161_v - (Double)this.crystalDeltaBreak.getValue()) != this.enemyCoordsInt[2] && (int)(t.field_70161_v + (Double)this.crystalDeltaBreak.getValue()) != this.enemyCoordsInt[2]) && ((int)t.field_70161_v != this.enemyCoordsInt[2] || (int)(t.field_70165_t - (Double)this.crystalDeltaBreak.getValue()) != this.enemyCoordsInt[0] && (int)(t.field_70165_t + (Double)this.crystalDeltaBreak.getValue()) != this.enemyCoordsInt[0])) continue;
            return t;
        }
        return null;
    }

    private boolean placeSupport() {
        int checksDone = 0;
        int blockDone = 0;
        if (this.toPlace.supportBlock > 0) {
            do {
                BlockPos targetPos;
                if (!(BlockUtil.getBlock(targetPos = this.getTargetPos(checksDone)) instanceof BlockAir)) continue;
                if (((Boolean)this.preRotation.getValue()).booleanValue() && !this.preRotationBol) {
                    if (this.preRotationTick == 0) {
                        this.placeBlockConfirm(targetPos, 0, 0.0, 0.0, 1.0, false, true, false);
                    }
                    if (this.preRotationTick == (Integer)this.preRotationDelay.getValue()) {
                        this.preRotationBol = true;
                        this.preRotationTick = 0;
                    } else {
                        ++this.preRotationTick;
                        return false;
                    }
                }
                if (!((Boolean)this.packetReducer.getValue() != false ? this.placeBlockConfirm(targetPos, 0, 0.0, 0.0, 1.0, false, false, false) : this.placeBlock(targetPos, 0, 0.0, 0.0, 1.0, false))) continue;
                this.preRotationBol = false;
                if (++blockDone != (Integer)this.blocksPerTick.getValue()) continue;
                return false;
            } while (++checksDone != this.toPlace.supportBlock);
        }
        this.stage = this.stage == 0 ? 1 : this.stage;
        return true;
    }

    private boolean placeBlock(BlockPos pos, int step, double offsetX, double offsetZ, double offsetY, boolean redstone) {
        Block neighbourBlock;
        Vec3d hitVec;
        EnumFacing opposite;
        BlockPos neighbour;
        block16: {
            Block block = PistonCrystal.mc.field_71441_e.func_180495_p(pos).func_177230_c();
            EnumFacing side = redstone && this.redstoneAbovePiston ? BlockUtil.getPlaceableSideExlude(pos, this.exd) : BlockUtil.getPlaceableSide(pos);
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
                return false;
            }
            if (side == null) {
                return false;
            }
            neighbour = pos.func_177972_a(side);
            opposite = side.func_176734_d();
            if (!BlockUtil.canBeClicked(neighbour)) {
                return false;
            }
            hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5 + offsetX, offsetY, 0.5 + offsetZ).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
            neighbourBlock = PistonCrystal.mc.field_71441_e.func_180495_p(neighbour).func_177230_c();
            try {
                if (this.slot_mat[step] == 11 || PistonCrystal.mc.field_71439_g.field_71071_by.func_70301_a(this.slot_mat[step]) != ItemStack.field_190927_a) {
                    if (PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c != this.slot_mat[step]) {
                        if (this.slot_mat[step] == -1) {
                            this.noMaterials = true;
                            return false;
                        }
                        PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c = this.slot_mat[step] == 11 ? PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c : this.slot_mat[step];
                    }
                    break block16;
                }
                this.noMaterials = true;
                return false;
            }
            catch (Exception e) {
                PistonCrystal.printDebug("Fatal Error during the creation of the structure. Please, report this bug in the discor's server", true);
                Logger LOGGER = LogManager.getLogger((String)"GameSense");
                LOGGER.error("[PistonCrystal] error during the creation of the structure.");
                if (e.getMessage() != null) {
                    LOGGER.error("[PistonCrystal] error message: " + e.getClass().getName() + " " + e.getMessage());
                } else {
                    LOGGER.error("[PistonCrystal] cannot find the cause");
                }
                boolean i5 = false;
                if (e.getStackTrace().length != 0) {
                    LOGGER.error("[PistonCrystal] StackTrace Start");
                    for (StackTraceElement errorMess : e.getStackTrace()) {
                        LOGGER.error("[PistonCrystal] " + errorMess.toString());
                    }
                    LOGGER.error("[PistonCrystal] StackTrace End");
                }
                PistonCrystal.printDebug(Integer.toString(step), true);
                this.disable();
            }
        }
        if (!this.isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            PistonCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)PistonCrystal.mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        if (((Boolean)this.rotate.getValue()).booleanValue() || step == 1) {
            Vec3d positionHit = hitVec;
            if (!((Boolean)this.rotate.getValue()).booleanValue() && step == 1) {
                positionHit = new Vec3d(PistonCrystal.mc.field_71439_g.field_70165_t + offsetX, PistonCrystal.mc.field_71439_g.field_70163_u + (offsetY == -1.0 ? offsetY : 0.0), PistonCrystal.mc.field_71439_g.field_70161_v + offsetZ);
            }
            BlockUtil.faceVectorPacketInstant(positionHit, true);
        }
        EnumHand handSwing = EnumHand.MAIN_HAND;
        if (this.slot_mat[step] == 11) {
            handSwing = EnumHand.OFF_HAND;
        }
        PistonCrystal.mc.field_71442_b.func_187099_a(PistonCrystal.mc.field_71439_g, PistonCrystal.mc.field_71441_e, neighbour, opposite, hitVec, handSwing);
        PistonCrystal.mc.field_71439_g.func_184609_a(handSwing);
        return true;
    }

    private boolean placeBlockConfirm(BlockPos pos, int step, double offsetX, double offsetZ, double offsetY, boolean redstone, boolean onlyRotation, boolean support) {
        Vec3d hitVec;
        EnumFacing side;
        block16: {
            Block block = PistonCrystal.mc.field_71441_e.func_180495_p(pos).func_177230_c();
            side = redstone && this.redstoneAbovePiston ? BlockUtil.getPlaceableSideExlude(pos, this.exd) : BlockUtil.getPlaceableSide(pos);
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
                return false;
            }
            if (side == null) {
                return false;
            }
            BlockPos neighbour = pos.func_177972_a(side);
            EnumFacing opposite = side.func_176734_d();
            if (!BlockUtil.canBeClicked(neighbour)) {
                return false;
            }
            hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5 + offsetX, 0.5, 0.5 + offsetZ).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
            if (((Boolean)this.forceRotation.getValue()).booleanValue()) {
                this.lastHitVec = hitVec;
            }
            try {
                if (this.slot_mat[step] == 11 || PistonCrystal.mc.field_71439_g.field_71071_by.func_70301_a(this.slot_mat[step]) != ItemStack.field_190927_a) {
                    if (PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c != this.slot_mat[step]) {
                        if (this.slot_mat[step] == -1) {
                            this.noMaterials = true;
                            return false;
                        }
                        PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c = this.slot_mat[step] == 11 ? PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c : this.slot_mat[step];
                    }
                    break block16;
                }
                this.noMaterials = true;
                return false;
            }
            catch (Exception e) {
                PistonCrystal.printDebug("Fatal Error during the creation of the structure. Please, report this bug in the discor's server", true);
                Logger LOGGER = LogManager.getLogger((String)"GameSense");
                LOGGER.error("[PistonCrystal] error during the creation of the structure.");
                if (e.getMessage() != null) {
                    LOGGER.error("[PistonCrystal] error message: " + e.getClass().getName() + " " + e.getMessage());
                } else {
                    LOGGER.error("[PistonCrystal] cannot find the cause");
                }
                boolean i5 = false;
                if (e.getStackTrace().length != 0) {
                    LOGGER.error("[PistonCrystal] StackTrace Start");
                    for (StackTraceElement errorMess : e.getStackTrace()) {
                        LOGGER.error("[PistonCrystal] " + errorMess.toString());
                    }
                    LOGGER.error("[PistonCrystal] StackTrace End");
                }
                PistonCrystal.printDebug(Integer.toString(step), true);
                this.disable();
            }
        }
        Vec3d positionHit = null;
        if (((Boolean)this.rotate.getValue()).booleanValue() || step == 1) {
            positionHit = hitVec;
            if (!((Boolean)this.rotate.getValue()).booleanValue() && step == 1) {
                positionHit = new Vec3d(PistonCrystal.mc.field_71439_g.field_70165_t + offsetX, PistonCrystal.mc.field_71439_g.field_70163_u + (offsetY == -1.0 ? offsetY : 0.0), PistonCrystal.mc.field_71439_g.field_70161_v + offsetZ);
            }
        }
        EnumHand handSwing = EnumHand.MAIN_HAND;
        if (this.slot_mat[step] == 11) {
            handSwing = EnumHand.OFF_HAND;
        }
        PlacementUtil.placePrecise(pos, handSwing, step == 1 || (Boolean)this.rotate.getValue() != false && (Boolean)this.forceRotation.getValue() == false, positionHit, side, onlyRotation, !support || (Boolean)this.forceRotation.getValue() == false);
        return true;
    }

    public void placeBlockThings(int step, boolean redstone, boolean preRotation, boolean support) {
        BlockPos targetPos = this.compactBlockPos(step);
        if (((Boolean)this.packetReducer.getValue() != false ? this.placeBlockConfirm(targetPos, step, this.toPlace.offsetX, this.toPlace.offsetZ, this.toPlace.offsetY, redstone, preRotation, support) : this.placeBlock(targetPos, step, this.toPlace.offsetX, this.toPlace.offsetZ, this.toPlace.offsetY, redstone)) && !preRotation) {
            ++this.stage;
            this.afterRotationTick = 0;
        }
    }

    public BlockPos compactBlockPos(int step) {
        BlockPos offsetPos = new BlockPos(this.toPlace.to_place.get(this.toPlace.supportBlock + step - 1));
        return new BlockPos(this.enemyCoordsDouble[0] + (double)offsetPos.func_177958_n(), this.enemyCoordsDouble[1] + (double)offsetPos.func_177956_o(), this.enemyCoordsDouble[2] + (double)offsetPos.func_177952_p());
    }

    private BlockPos getTargetPos(int idx) {
        BlockPos offsetPos = new BlockPos(this.toPlace.to_place.get(idx));
        return new BlockPos(this.enemyCoordsDouble[0] + (double)offsetPos.func_177958_n(), this.enemyCoordsDouble[1] + (double)offsetPos.func_177956_o(), this.enemyCoordsDouble[2] + (double)offsetPos.func_177952_p());
    }

    private boolean checkVariable() {
        if (this.noMaterials || !this.isHole || !this.enoughSpace || this.hasMoved || this.deadPl || this.rotationPlayerMoved || !this.minHp || this.itemCrystal) {
            this.disable();
            return true;
        }
        return false;
    }

    /*
     * WARNING - void declaration
     */
    private boolean createStructure() {
        structureTemp addedStructure;
        block43: {
            addedStructure = new structureTemp(Double.MAX_VALUE, 0, null);
            try {
                block44: {
                    if (this.meCoordsInt[1] - this.enemyCoordsInt[1] <= -1 || this.meCoordsInt[1] - this.enemyCoordsInt[1] > (Integer)this.maxYincr.getValue()) break block44;
                    for (int startH = 1; startH >= 0; --startH) {
                        void var8_18;
                        if (addedStructure.to_place != null) continue;
                        int incr = 0;
                        ArrayList<Vec3d> highSup = new ArrayList<Vec3d>();
                        while (this.meCoordsInt[1] > this.enemyCoordsInt[1] + incr) {
                            ++incr;
                            for (int[] nArray : this.disp_surblock) {
                                highSup.add(new Vec3d((double)nArray[0], (double)incr, (double)nArray[2]));
                            }
                        }
                        incr += startH;
                        int i = -1;
                        Double[][] doubleArray = this.sur_block;
                        int n = doubleArray.length;
                        boolean bl = false;
                        while (var8_18 < n) {
                            block45: {
                                float offsetZ;
                                float offsetX;
                                int[] redstoneCoordsRel;
                                double[] redstoneCoordsAbs;
                                int[] pistonCordRel;
                                double[] pistonCordAbs;
                                double distanceNowCrystal;
                                int[] crystalCordsRel;
                                double[] crystalCordsAbs;
                                block49: {
                                    Object disp22;
                                    block48: {
                                        block47: {
                                            block46: {
                                                Double[] cord_b = doubleArray[var8_18];
                                                crystalCordsAbs = new double[]{cord_b[0], cord_b[1] + (double)incr, cord_b[2]};
                                                crystalCordsRel = new int[]{this.disp_surblock[++i][0], this.disp_surblock[i][1] + incr, this.disp_surblock[i][2]};
                                                distanceNowCrystal = PistonCrystal.mc.field_71439_g.func_70011_f(crystalCordsAbs[0], crystalCordsAbs[1], crystalCordsAbs[2]);
                                                if (!(distanceNowCrystal < addedStructure.distance) || !(BlockUtil.getBlock(crystalCordsAbs[0], crystalCordsAbs[1], crystalCordsAbs[2]) instanceof BlockAir) || !(BlockUtil.getBlock(crystalCordsAbs[0], crystalCordsAbs[1] + 1.0, crystalCordsAbs[2]) instanceof BlockAir) || PistonCrystal.someoneInCoords(crystalCordsAbs[0], crystalCordsAbs[1], crystalCordsAbs[2]) || !(BlockUtil.getBlock(crystalCordsAbs[0], crystalCordsAbs[1] - 1.0, crystalCordsAbs[2]) instanceof BlockObsidian) && !BlockUtil.getBlock(crystalCordsAbs[0], crystalCordsAbs[1] - 1.0, crystalCordsAbs[2]).getRegistryName().func_110623_a().equals("bedrock")) break block45;
                                                pistonCordAbs = new double[3];
                                                pistonCordRel = new int[3];
                                                if (!((Boolean)this.rotate.getValue()).booleanValue() && ((Boolean)this.betterPlacement.getValue()).booleanValue()) break block46;
                                                pistonCordAbs = new double[]{crystalCordsAbs[0] + (double)this.disp_surblock[i][0], crystalCordsAbs[1], crystalCordsAbs[2] + (double)this.disp_surblock[i][2]};
                                                Block tempBlock = BlockUtil.getBlock(pistonCordAbs[0], pistonCordAbs[1], pistonCordAbs[2]);
                                                if (tempBlock instanceof BlockPistonBase == tempBlock instanceof BlockAir || PistonCrystal.someoneInCoords(pistonCordAbs[0], pistonCordAbs[1], pistonCordAbs[2])) break block45;
                                                pistonCordRel = new int[]{crystalCordsRel[0] * 2, crystalCordsRel[1], crystalCordsRel[2] * 2};
                                                break block47;
                                            }
                                            double distancePist = Double.MAX_VALUE;
                                            for (Object disp22 : this.disp_surblock) {
                                                double d;
                                                BlockPos blockPiston = new BlockPos(crystalCordsAbs[0] + (double)disp22[0], crystalCordsAbs[1], crystalCordsAbs[2] + (double)disp22[2]);
                                                double distanceNowPiston = PistonCrystal.mc.field_71439_g.func_174831_c(blockPiston);
                                                if (d > distancePist || !(BlockUtil.getBlock(blockPiston.func_177958_n(), blockPiston.func_177956_o(), blockPiston.func_177952_p()) instanceof BlockPistonBase) && !(BlockUtil.getBlock(blockPiston.func_177958_n(), blockPiston.func_177956_o(), blockPiston.func_177952_p()) instanceof BlockAir) || PistonCrystal.someoneInCoords(crystalCordsAbs[0] + (double)disp22[0], crystalCordsAbs[1], crystalCordsAbs[2] + (double)disp22[2]) || !(BlockUtil.getBlock(blockPiston.func_177958_n() - crystalCordsRel[0], blockPiston.func_177956_o(), blockPiston.func_177952_p() - crystalCordsRel[2]) instanceof BlockAir)) continue;
                                                distancePist = distanceNowPiston;
                                                pistonCordAbs = new double[]{crystalCordsAbs[0] + (double)disp22[0], crystalCordsAbs[1], crystalCordsAbs[2] + (double)disp22[2]};
                                                pistonCordRel = new int[]{crystalCordsRel[0] + disp22[0], crystalCordsRel[1], crystalCordsRel[2] + disp22[2]};
                                            }
                                            if (distancePist == Double.MAX_VALUE) break block45;
                                        }
                                        if (!((Boolean)this.rotate.getValue()).booleanValue()) break block48;
                                        int[] pistonCordInt = new int[]{(int)pistonCordAbs[0], (int)pistonCordAbs[1], (int)pistonCordAbs[2]};
                                        boolean behindBol = false;
                                        for (int checkBehind : new int[]{0, 2}) {
                                            int idx;
                                            if (this.meCoordsInt[checkBehind] != pistonCordInt[checkBehind] || pistonCordInt[idx = checkBehind == 2 ? 0 : 2] >= this.enemyCoordsInt[idx] != this.meCoordsInt[idx] >= this.enemyCoordsInt[idx]) continue;
                                            behindBol = true;
                                        }
                                        if (!behindBol && Math.abs(this.meCoordsInt[0] - this.enemyCoordsInt[0]) == 2 && Math.abs(this.meCoordsInt[2] - this.enemyCoordsInt[2]) == 2 && (this.meCoordsInt[0] == pistonCordInt[0] && Math.abs(this.meCoordsInt[2] - pistonCordInt[2]) >= 2 || this.meCoordsInt[2] == pistonCordInt[2] && Math.abs(this.meCoordsInt[0] - pistonCordInt[0]) >= 2)) {
                                            behindBol = true;
                                        }
                                        if (!behindBol && Math.abs(this.meCoordsInt[0] - this.enemyCoordsInt[0]) > 2 && this.meCoordsInt[2] != this.enemyCoordsInt[2] || Math.abs(this.meCoordsInt[2] - this.enemyCoordsInt[2]) > 2 && this.meCoordsInt[0] != this.enemyCoordsInt[0]) {
                                            behindBol = true;
                                        }
                                        if (behindBol) break block45;
                                    }
                                    redstoneCoordsAbs = new double[3];
                                    redstoneCoordsRel = new int[3];
                                    double minFound = Double.MAX_VALUE;
                                    double minNow = -1.0;
                                    boolean foundOne = true;
                                    disp22 = this.disp_surblock;
                                    int blockPiston = ((int[])disp22).length;
                                    for (int j = 0; j < blockPiston; ++j) {
                                        double d;
                                        int pos = disp22[j];
                                        double[] torchCoords = new double[]{pistonCordAbs[0] + (double)pos[0], pistonCordAbs[1], pistonCordAbs[2] + (double)pos[2]};
                                        minNow = PistonCrystal.mc.field_71439_g.func_70011_f(torchCoords[0], torchCoords[1], torchCoords[2]);
                                        if (d >= minFound || this.redstoneBlockMode && pos[0] != crystalCordsRel[0] || PistonCrystal.someoneInCoords(torchCoords[0], torchCoords[1], torchCoords[2]) || !(BlockUtil.getBlock(torchCoords[0], torchCoords[1], torchCoords[2]) instanceof BlockRedstoneTorch) && !(BlockUtil.getBlock(torchCoords[0], torchCoords[1], torchCoords[2]) instanceof BlockAir) || (int)torchCoords[0] == (int)crystalCordsAbs[0] && (int)torchCoords[2] == (int)crystalCordsAbs[2]) continue;
                                        boolean torchFront = false;
                                        for (int part : new int[]{0, 2}) {
                                            int contPart;
                                            int n2 = contPart = part == 0 ? 2 : 0;
                                            if ((int)torchCoords[contPart] != (int)pistonCordAbs[contPart] || (int)torchCoords[part] != this.enemyCoordsInt[part]) continue;
                                            torchFront = true;
                                        }
                                        if (torchFront) continue;
                                        redstoneCoordsAbs = new double[]{torchCoords[0], torchCoords[1], torchCoords[2]};
                                        redstoneCoordsRel = new int[]{pistonCordRel[0] + pos[0], pistonCordRel[1], pistonCordRel[2] + pos[2]};
                                        foundOne = false;
                                        minFound = minNow;
                                    }
                                    this.redstoneAbovePiston = false;
                                    if (!foundOne) break block49;
                                    if (this.redstoneBlockMode || !(BlockUtil.getBlock(pistonCordAbs[0], pistonCordAbs[1] + 1.0, pistonCordAbs[2]) instanceof BlockAir)) break block45;
                                    redstoneCoordsAbs = new double[]{pistonCordAbs[0], pistonCordAbs[1] + 1.0, pistonCordAbs[2]};
                                    redstoneCoordsRel = new int[]{pistonCordRel[0], pistonCordRel[1] + 1, pistonCordRel[2]};
                                    this.redstoneAbovePiston = true;
                                }
                                if (this.redstoneBlockMode && ((Boolean)this.allowCheapMode.getValue()).booleanValue() && (BlockUtil.getBlock(redstoneCoordsAbs[0], redstoneCoordsAbs[1] - 1.0, redstoneCoordsAbs[2]) instanceof BlockAir || BlockUtil.getBlock((double)redstoneCoordsAbs[0], (double)(redstoneCoordsAbs[1] - 1.0), (double)redstoneCoordsAbs[2]).field_149770_b.equals("blockRedstone"))) {
                                    pistonCordAbs = new double[]{redstoneCoordsAbs[0], redstoneCoordsAbs[1], redstoneCoordsAbs[2]};
                                    pistonCordRel = new int[]{redstoneCoordsRel[0], redstoneCoordsRel[1], redstoneCoordsRel[2]};
                                    redstoneCoordsAbs = new double[]{redstoneCoordsAbs[0], redstoneCoordsAbs[1] - 1.0, redstoneCoordsRel[2]};
                                    redstoneCoordsRel = new int[]{redstoneCoordsRel[0], redstoneCoordsRel[1] - 1, redstoneCoordsRel[2]};
                                    this.fastModeActive = true;
                                }
                                ArrayList<Vec3d> toPlaceTemp = new ArrayList<Vec3d>();
                                int supportBlock = 0;
                                if (BlockUtil.getBlock(crystalCordsAbs[0], crystalCordsAbs[1] - 1.0, crystalCordsAbs[2]) instanceof BlockAir) {
                                    toPlaceTemp.add(new Vec3d((double)crystalCordsRel[0], (double)(crystalCordsRel[1] - 1), (double)crystalCordsRel[2]));
                                    ++supportBlock;
                                }
                                if (!this.fastModeActive && BlockUtil.getBlock(pistonCordAbs[0], pistonCordAbs[1] - 1.0, pistonCordAbs[2]) instanceof BlockAir) {
                                    toPlaceTemp.add(new Vec3d((double)pistonCordRel[0], (double)(pistonCordRel[1] - 1), (double)pistonCordRel[2]));
                                    ++supportBlock;
                                }
                                if (!this.fastModeActive) {
                                    if (this.redstoneAbovePiston) {
                                        int[] toAdd = this.enemyCoordsInt[0] == (int)pistonCordAbs[0] && this.enemyCoordsInt[2] == (int)pistonCordAbs[2] ? new int[]{crystalCordsRel[0], 0, 0} : new int[]{crystalCordsRel[0], 0, crystalCordsRel[2]};
                                        for (int hight = 0; hight < 2; ++hight) {
                                            if (!(BlockUtil.getBlock(pistonCordAbs[0] + (double)toAdd[0], pistonCordAbs[1] + (double)hight, pistonCordAbs[2] + (double)toAdd[2]) instanceof BlockAir)) continue;
                                            toPlaceTemp.add(new Vec3d((double)(pistonCordRel[0] + toAdd[0]), (double)(pistonCordRel[1] + hight), (double)(pistonCordRel[2] + toAdd[2])));
                                            ++supportBlock;
                                        }
                                    } else if (!this.redstoneBlockMode && BlockUtil.getBlock(redstoneCoordsAbs[0], redstoneCoordsAbs[1] - 1.0, redstoneCoordsAbs[2]) instanceof BlockAir) {
                                        toPlaceTemp.add(new Vec3d((double)redstoneCoordsRel[0], (double)(redstoneCoordsRel[1] - 1), (double)redstoneCoordsRel[2]));
                                        ++supportBlock;
                                    }
                                } else if (BlockUtil.getBlock(redstoneCoordsAbs[0] - (double)crystalCordsRel[0], redstoneCoordsAbs[1] - 1.0, redstoneCoordsAbs[2] - (double)crystalCordsRel[2]) instanceof BlockAir) {
                                    toPlaceTemp.add(new Vec3d((double)(redstoneCoordsRel[0] - crystalCordsRel[0]), (double)redstoneCoordsRel[1], (double)(redstoneCoordsRel[2] - crystalCordsRel[2])));
                                    ++supportBlock;
                                }
                                toPlaceTemp.add(new Vec3d((double)pistonCordRel[0], (double)pistonCordRel[1], (double)pistonCordRel[2]));
                                toPlaceTemp.add(new Vec3d((double)crystalCordsRel[0], (double)crystalCordsRel[1], (double)crystalCordsRel[2]));
                                toPlaceTemp.add(new Vec3d((double)redstoneCoordsRel[0], (double)redstoneCoordsRel[1], (double)redstoneCoordsRel[2]));
                                if (incr > 1) {
                                    for (int i2 = 0; i2 < highSup.size(); ++i2) {
                                        toPlaceTemp.add(0, (Vec3d)highSup.get(i2));
                                        ++supportBlock;
                                    }
                                }
                                if (this.disp_surblock[i][0] != 0) {
                                    float f = offsetX = (Boolean)this.rotate.getValue() != false ? (float)this.disp_surblock[i][0] / 2.0f : (float)this.disp_surblock[i][0];
                                    offsetZ = ((Boolean)this.rotate.getValue()).booleanValue() ? (PistonCrystal.mc.field_71439_g.func_70092_e(pistonCordAbs[0], pistonCordAbs[1], pistonCordAbs[2] + 0.5) > PistonCrystal.mc.field_71439_g.func_70092_e(pistonCordAbs[0], pistonCordAbs[1], pistonCordAbs[2] - 0.5) ? -0.5f : 0.5f) : (float)this.disp_surblock[i][2];
                                } else {
                                    float f = offsetZ = (Boolean)this.rotate.getValue() != false ? (float)this.disp_surblock[i][2] / 2.0f : (float)this.disp_surblock[i][2];
                                    offsetX = ((Boolean)this.rotate.getValue()).booleanValue() ? (PistonCrystal.mc.field_71439_g.func_70092_e(pistonCordAbs[0] + 0.5, pistonCordAbs[1], pistonCordAbs[2]) > PistonCrystal.mc.field_71439_g.func_70092_e(pistonCordAbs[0] - 0.5, pistonCordAbs[1], pistonCordAbs[2]) ? -0.5f : 0.5f) : (float)this.disp_surblock[i][0];
                                }
                                float offsetY = this.meCoordsInt[1] - this.enemyCoordsInt[1] == -1 ? 0.0f : 1.0f;
                                addedStructure.replaceValues(distanceNowCrystal, supportBlock, toPlaceTemp, -1, offsetX, offsetZ, offsetY);
                                if (((Boolean)this.blockPlayer.getValue()).booleanValue()) {
                                    Vec3d valuesStart = addedStructure.to_place.get(addedStructure.supportBlock + 1);
                                    int[] valueBegin = new int[]{(int)(-valuesStart.field_72450_a), (int)valuesStart.field_72448_b, (int)(-valuesStart.field_72449_c)};
                                    if (!((Boolean)this.bypassObsidian.getValue()).booleanValue() || (int)PistonCrystal.mc.field_71439_g.field_70163_u == this.enemyCoordsInt[1]) {
                                        addedStructure.to_place.add(0, new Vec3d(0.0, (double)(incr + 1), 0.0));
                                        addedStructure.to_place.add(0, new Vec3d((double)valueBegin[0], (double)(incr + 1), (double)valueBegin[2]));
                                        addedStructure.to_place.add(0, new Vec3d((double)valueBegin[0], (double)incr, (double)valueBegin[2]));
                                        addedStructure.supportBlock += 3;
                                    } else {
                                        addedStructure.to_place.add(0, new Vec3d(0.0, (double)incr, 0.0));
                                        addedStructure.to_place.add(0, new Vec3d((double)valueBegin[0], (double)incr, (double)valueBegin[2]));
                                        addedStructure.supportBlock += 2;
                                    }
                                }
                                this.toPlace = addedStructure;
                            }
                            ++var8_18;
                        }
                    }
                    break block43;
                }
                this.yUnder = true;
            }
            catch (Exception e) {
                PistonCrystal.printDebug("Fatal Error during the creation of the structure. Please, report this bug in the discor's server", true);
                Logger LOGGER = LogManager.getLogger((String)"GameSense");
                LOGGER.error("[PistonCrystal] error during the creation of the structure.");
                if (e.getMessage() != null) {
                    LOGGER.error("[PistonCrystal] error message: " + e.getClass().getName() + " " + e.getMessage());
                } else {
                    LOGGER.error("[PistonCrystal] cannot find the cause");
                }
                int i5 = 0;
                if (e.getStackTrace().length != 0) {
                    LOGGER.error("[PistonCrystal] StackTrace Start");
                    for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                        LOGGER.error("[PistonCrystal] " + stackTraceElement.toString());
                    }
                    LOGGER.error("[PistonCrystal] StackTrace End");
                }
                if (this.aimTarget != null) {
                    LOGGER.error("[PistonCrystal] closest target is not null");
                } else {
                    LOGGER.error("[PistonCrystal] closest target is null somehow");
                }
                for (StackTraceElement stackTraceElement : this.sur_block) {
                    if (stackTraceElement != null) {
                        LOGGER.error("[PistonCrystal] " + i5 + " is not null");
                    } else {
                        LOGGER.error("[PistonCrystal] " + i5 + " is null");
                    }
                    ++i5;
                }
            }
        }
        if (((Boolean)this.debugMode.getValue()).booleanValue() && addedStructure.to_place != null) {
            PistonCrystal.printDebug("Skeleton structure:", false);
            for (Vec3d parte : addedStructure.to_place) {
                PistonCrystal.printDebug(String.format("%f %f %f", parte.field_72450_a, parte.field_72448_b, parte.field_72449_c), false);
            }
        }
        return addedStructure.to_place != null;
    }

    public static boolean someoneInCoords(double x, double y, double z) {
        return PistonCrystal.mc.field_71441_e.func_72839_b(null, new AxisAlignedBB(new BlockPos(x, y, z))).stream().anyMatch(entity -> entity instanceof EntityPlayer);
    }

    private boolean getMaterialsSlot() {
        if (PistonCrystal.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemEndCrystal) {
            this.slot_mat[2] = 11;
        }
        if (((String)this.placeMode.getValue()).equals("Block")) {
            this.redstoneBlockMode = true;
        }
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = PistonCrystal.mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack == ItemStack.field_190927_a) continue;
            if (this.slot_mat[2] == -1 && stack.func_77973_b() instanceof ItemEndCrystal) {
                this.slot_mat[2] = i;
            } else if (((Boolean)this.antiWeakness.getValue()).booleanValue() && stack.func_77973_b() instanceof ItemSword) {
                this.slot_mat[4] = i;
            } else if (stack.func_77973_b() instanceof ItemPickaxe) {
                this.slot_mat[5] = i;
            }
            if (!(stack.func_77973_b() instanceof ItemBlock)) continue;
            Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
            if (block instanceof BlockObsidian) {
                this.slot_mat[0] = i;
                continue;
            }
            if (block instanceof BlockPistonBase) {
                this.slot_mat[1] = i;
                continue;
            }
            if (!((String)this.placeMode.getValue()).equals("Block") && block instanceof BlockRedstoneTorch) {
                this.slot_mat[3] = i;
                this.redstoneBlockMode = false;
                continue;
            }
            if (((String)this.placeMode.getValue()).equals("Torch") || !block.field_149770_b.equals("blockRedstone")) continue;
            this.slot_mat[3] = i;
            this.redstoneBlockMode = true;
        }
        if (!this.redstoneBlockMode) {
            this.slot_mat[5] = -1;
        }
        int count = 0;
        for (int val : this.slot_mat) {
            if (val == -1) continue;
            ++count;
        }
        if (((Boolean)this.debugMode.getValue()).booleanValue()) {
            PistonCrystal.printDebug(String.format("%d %d %d %d %d %d", this.slot_mat[0], this.slot_mat[1], this.slot_mat[2], this.slot_mat[3], this.slot_mat[4], this.slot_mat[5]), false);
        }
        return count >= 4 + ((Boolean)this.antiWeakness.getValue() != false ? 1 : 0) + (this.redstoneBlockMode ? 1 : 0);
    }

    private boolean is_in_hole() {
        this.sur_block = new Double[][]{{this.aimTarget.field_70165_t + 1.0, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v}, {this.aimTarget.field_70165_t - 1.0, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v}, {this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v + 1.0}, {this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v - 1.0}};
        return HoleUtil.isHole(EntityUtil.getPosition((Entity)this.aimTarget), true, true).getType() != HoleUtil.HoleType.NONE;
    }

    public static void printDebug(String text, Boolean error) {
        ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        MessageBus.sendClientPrefixMessage((error != false ? colorMain.getDisabledColor() : colorMain.getEnabledColor()) + text);
    }

    private static class structureTemp {
        public double distance;
        public int supportBlock;
        public List<Vec3d> to_place;
        public int direction;
        public float offsetX;
        public float offsetY;
        public float offsetZ;

        public structureTemp(double distance, int supportBlock, List<Vec3d> to_place) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.direction = -1;
        }

        public void replaceValues(double distance, int supportBlock, List<Vec3d> to_place, int direction, float offsetX, float offsetZ, float offsetY) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.direction = direction;
            this.offsetX = offsetX;
            this.offsetZ = offsetZ;
            this.offsetY = offsetY;
        }
    }
}

