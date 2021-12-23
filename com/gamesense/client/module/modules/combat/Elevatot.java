/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.Phase;
import com.gamesense.api.event.events.BlockChangeEvent;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.util.player.SpoofRotationUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.AutoCrystalRewrite;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.LogManager;

@Module.Declaration(name="Elevatot", category=Category.Combat)
public class Elevatot
extends Module {
    BooleanSetting targetSection = this.registerBoolean("Target Section", true);
    ModeSetting target = this.registerMode("Target", Arrays.asList("Nearest", "Looking"), "Nearest", () -> (Boolean)this.targetSection.getValue());
    DoubleSetting enemyRange = this.registerDouble("Range", 4.9, 0.0, 6.0, () -> (Boolean)this.targetSection.getValue());
    BooleanSetting delaySection = this.registerBoolean("Delay Section", true);
    IntegerSetting supportDelay = this.registerInteger("Support Delay", 0, 0, 8, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting pistonDelay = this.registerInteger("Piston Delay", 0, 0, 8, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting redstoneDelay = this.registerInteger("Redstone Delay", 0, 0, 8, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting blocksPerTick = this.registerInteger("Blocks per Tick", 4, 1, 8, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting tickBreakRedstone = this.registerInteger("Tick Break Redstone", 2, 0, 10, () -> (Boolean)this.delaySection.getValue());
    BooleanSetting clientInstaBreak = this.registerBoolean("Client Insta Break", false, () -> (Boolean)this.delaySection.getValue());
    BooleanSetting clientInstaPlace = this.registerBoolean("Client Insta Place", false, () -> (Boolean)this.delaySection.getValue());
    BooleanSetting pauseAfterSupport = this.registerBoolean("Pause After Support", false, () -> (Boolean)this.delaySection.getValue());
    BooleanSetting miscSection = this.registerBoolean("Misc Section", true);
    BooleanSetting debugMode = this.registerBoolean("Debug Mode", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting trapMode = this.registerBoolean("Trap Before", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting doubleTrap = this.registerBoolean("Double Trap", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting stopCa = this.registerBoolean("StopCa", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting rotate = this.registerBoolean("Rotate", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting forceBurrow = this.registerBoolean("Force Burrow", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting noGlitchPiston = this.registerBoolean("No Glitch Piston", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting fillHole = this.registerBoolean("Fill hole", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting addRoof = this.registerBoolean("Add Roof", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting silentSwitch = this.registerBoolean("Silent Switch", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting checksSection = this.registerBoolean("Checks Section", true);
    BooleanSetting checkPush = this.registerBoolean("Check Push", false, () -> (Boolean)this.checksSection.getValue());
    BooleanSetting checkAbove = this.registerBoolean("Check Above", false, () -> (Boolean)this.checksSection.getValue());
    BooleanSetting checkSurround = this.registerBoolean("Check Surround", true, () -> (Boolean)this.checksSection.getValue());
    BooleanSetting checkBurrow = this.registerBoolean("Check Burrow", false, () -> (Boolean)this.checksSection.getValue());
    BooleanSetting stopOut = this.registerBoolean("Stop Out", true, () -> (Boolean)this.checksSection.getValue());
    IntegerSetting tickOutHole = this.registerInteger("Tick Out Hole", 0, 0, 10, () -> (Boolean)this.checksSection.getValue());
    BooleanSetting waitRotate = this.registerBoolean("Wait Rotate", false);
    EntityPlayer aimTarget;
    double[][] sur_block;
    double[] enemyCoordsDouble;
    int[][] disp_surblock = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}};
    int[] slot_mat;
    int[] enemyCoordsInt;
    int[] meCoordsInt;
    int lastStage;
    int blockPlaced;
    int delayTimeTicks;
    int tickOut;
    boolean redstoneBlockMode;
    boolean enoughSpace;
    boolean isHole;
    boolean noMaterials;
    boolean redstoneAbovePiston;
    boolean isSneaking;
    boolean redstonePlaced;
    String uuid_enemy;
    structureTemp toPlace;
    @EventHandler
    private final Listener<BlockChangeEvent> blockChangeEventListener = new Listener<BlockChangeEvent>(event -> {
        if (Elevatot.mc.field_71439_g == null || Elevatot.mc.field_71441_e == null || this.aimTarget == null) {
            return;
        }
        if (event.getBlock() == null || event.getPosition() == null) {
            return;
        }
        BlockPos temp = this.compactBlockPos(2);
        if (event.getPosition().func_177958_n() == temp.func_177958_n() && event.getPosition().func_177956_o() == temp.func_177956_o() && event.getPosition().func_177952_p() == temp.func_177952_p() && !(BlockUtil.getBlock(temp = this.compactBlockPos(1)) instanceof BlockAir)) {
            if (event.getBlock() instanceof BlockRedstoneTorch) {
                if ((Integer)this.tickBreakRedstone.getValue() == 0) {
                    this.breakBlock(this.compactBlockPos(2));
                    this.lastStage = 2;
                } else {
                    this.lastStage = 3;
                }
            } else if (event.getBlock() instanceof BlockAir && (Integer)this.redstoneDelay.getValue() == 0) {
                this.placeBlock(temp, 0.0, 0.0, 0.0, true, false, this.slot_mat[2], -1);
                if (((Boolean)this.clientInstaPlace.getValue()).booleanValue()) {
                    Elevatot.mc.field_71441_e.func_175656_a(this.compactBlockPos(2), Blocks.field_150429_aA.func_176223_P());
                }
            }
        }
    }, new Predicate[0]);
    Vec3d lastHitVec = null;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
        if (event.getPhase() != Phase.PRE || !((Boolean)this.rotate.getValue()).booleanValue() || this.lastHitVec == null) {
            return;
        }
        Vec2f rotation = RotationUtil.getRotationTo(this.lastHitVec);
        PlayerPacket packet = new PlayerPacket((Module)this, rotation);
        PlayerPacketManager.INSTANCE.addPacket(packet);
    }, new Predicate[0]);
    final ArrayList<EnumFacing> exd = new ArrayList<EnumFacing>(){
        {
            this.add(EnumFacing.DOWN);
        }
    };

    private void breakBlock(BlockPos pos) {
        EnumFacing side = BlockUtil.getPlaceableSide(pos);
        if (side != null) {
            if (((Boolean)this.rotate.getValue()).booleanValue()) {
                BlockPos neighbour = pos.func_177972_a(side);
                EnumFacing opposite = side.func_176734_d();
                Vec3d hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.0, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
                BlockUtil.faceVectorPacketInstant(hitVec, true);
                this.lastHitVec = hitVec;
            }
            Elevatot.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
            Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side));
            Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side));
            if (((Boolean)this.clientInstaBreak.getValue()).booleanValue()) {
                Elevatot.mc.field_71441_e.func_175698_g(pos);
            }
        }
    }

    @Override
    public void onEnable() {
        if (Elevatot.mc.field_71439_g == null || Elevatot.mc.field_71441_e == null) {
            this.disable();
            return;
        }
        SpoofRotationUtil.ROTATION_UTIL.onEnable();
        this.resetValues();
        if (!this.getAimTarget()) {
            return;
        }
        this.playerChecks();
    }

    @Override
    public void onDisable() {
        if (Elevatot.mc.field_71439_g == null || Elevatot.mc.field_71441_e == null) {
            return;
        }
        if (this.isSneaking) {
            Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)Elevatot.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        String output = "";
        String materialsNeeded = "";
        if (this.aimTarget == null) {
            output = "No target found...";
        } else if (!this.isHole) {
            output = "The enemy is not in a hole...";
        } else if (!this.enoughSpace) {
            output = "Not enough space...";
        } else if (this.noMaterials) {
            output = "No materials detected...";
            materialsNeeded = this.getMissingMaterials();
        }
        this.setDisabledMessage(output + "Elevatot turned OFF!");
        if (!materialsNeeded.equals("")) {
            this.setDisabledMessage("Materials missing:" + materialsNeeded);
        }
        if (((Boolean)this.stopCa.getValue()).booleanValue()) {
            AutoCrystalRewrite.stopAC = false;
        }
    }

    String getMissingMaterials() {
        StringBuilder output = new StringBuilder();
        if (this.slot_mat[0] == -1) {
            output.append(" Obsidian");
        }
        if (this.slot_mat[1] == -1) {
            output.append(" Piston");
        }
        if (this.slot_mat[2] == -1) {
            output.append(" Redstone");
        }
        if (this.slot_mat[3] == -1 && this.redstoneBlockMode) {
            output.append(" Pick");
        }
        if (this.slot_mat[4] == -1 && ((Boolean)this.forceBurrow.getValue()).booleanValue()) {
            output.append(" Skull");
        }
        return output.toString();
    }

    @Override
    public void onUpdate() {
        int toWait;
        if (Elevatot.mc.field_71439_g == null || Elevatot.mc.field_71441_e == null) {
            this.disable();
            return;
        }
        if (this.aimTarget == null) {
            if (!this.getAimTarget()) {
                return;
            }
            this.playerChecks();
        }
        SpoofRotationUtil.ROTATION_UTIL.shouldSpoofAngles(true);
        switch (this.lastStage) {
            case 0: {
                toWait = (Integer)this.supportDelay.getValue();
                break;
            }
            case 1: {
                toWait = (Integer)this.pistonDelay.getValue();
                break;
            }
            case 2: {
                toWait = (Integer)this.redstoneDelay.getValue();
                break;
            }
            case 3: {
                toWait = (Integer)this.tickBreakRedstone.getValue();
                break;
            }
            default: {
                toWait = 0;
            }
        }
        if (this.delayTimeTicks < toWait) {
            ++this.delayTimeTicks;
            return;
        }
        if (this.enemyCoordsDouble == null) {
            this.disable();
            return;
        }
        boolean back = false;
        BlockPos pos = new BlockPos(0, -100, 0);
        for (int i = 0; i < Elevatot.mc.field_71441_e.field_73010_i.size(); ++i) {
            if (!((EntityPlayer)Elevatot.mc.field_71441_e.field_73010_i.get(i)).func_146103_bH().getId().toString().equals(this.uuid_enemy)) continue;
            pos = ((EntityPlayer)Elevatot.mc.field_71441_e.field_73010_i.get(i)).func_180425_c();
            break;
        }
        if (pos.func_177956_o() == -100) {
            this.disable();
            return;
        }
        if (this.checkVariable()) {
            return;
        }
        if (((Boolean)this.stopOut.getValue()).booleanValue() && pos.func_177956_o() != this.enemyCoordsInt[1] && (pos.func_177958_n() != this.enemyCoordsInt[0] || pos.func_177952_p() != this.enemyCoordsInt[2])) {
            if (this.tickOut++ >= (Integer)this.tickOutHole.getValue()) {
                PistonCrystal.printDebug("Enemy pushed out of the hole.", false);
                if (((Boolean)this.trapMode.getValue()).booleanValue()) {
                    PistonCrystal.printDebug("Finished trapping him", false);
                    this.placeBlock(new BlockPos(this.enemyCoordsDouble[0], this.enemyCoordsDouble[1] + 2.0, this.enemyCoordsDouble[2]), 0.0, 0.0, 0.0, false, false, this.slot_mat[0], -1);
                }
                if (((Boolean)this.fillHole.getValue()).booleanValue()) {
                    PistonCrystal.printDebug("Filling the hole", false);
                    this.placeBlock(new BlockPos(this.enemyCoordsDouble[0], this.enemyCoordsDouble[1], this.enemyCoordsDouble[2]), 0.0, 0.0, 0.0, false, false, this.slot_mat[0], -1);
                }
                this.breakBlock(this.compactBlockPos(2));
                this.disable();
                return;
            }
        } else if (this.tickOut != 0) {
            this.tickOut = 0;
        }
        this.blockPlaced = 0;
        if (this.placeSupport()) {
            BlockPos temp = this.compactBlockPos(1);
            if (BlockUtil.getBlock(temp) instanceof BlockAir) {
                this.placeBlock(temp, this.toPlace.offsetX, this.toPlace.offsetY, this.toPlace.offsetZ, false, true, this.slot_mat[1], this.toPlace.position);
                if (((Boolean)this.noGlitchPiston.getValue()).booleanValue()) {
                    Elevatot.mc.field_71441_e.func_175698_g(temp);
                }
                if (this.continueBlock()) {
                    this.lastStage = 1;
                }
            }
            if (BlockUtil.getBlock(temp = this.compactBlockPos(2)) instanceof BlockAir) {
                this.placeBlock(temp, 0.0, 0.0, 0.0, true, false, this.slot_mat[2], -1);
                this.lastStage = 3;
                return;
            }
            this.breakBlock(this.compactBlockPos(2));
            this.lastStage = 2;
            return;
        }
    }

    boolean continueBlock() {
        return ++this.blockPlaced == (Integer)this.blocksPerTick.getValue();
    }

    boolean placeSupport() {
        if (this.toPlace.supportBlock > 0) {
            for (int i = 0; i < this.toPlace.supportBlock; ++i) {
                boolean placed;
                BlockPos targetPos = this.getTargetPos(i);
                if (!(BlockUtil.getBlock(targetPos) instanceof BlockAir)) continue;
                if (((Boolean)this.forceBurrow.getValue()).booleanValue() && i == 0) {
                    boolean temp = this.redstoneAbovePiston;
                    this.redstoneAbovePiston = true;
                    placed = this.placeBlock(targetPos, 0.0, 0.0, 0.0, true, false, this.slot_mat[4], -1);
                    this.redstoneAbovePiston = temp;
                } else {
                    placed = this.placeBlock(targetPos, 0.0, 0.0, 0.0, false, false, this.slot_mat[0], -1);
                }
                if (!placed || !this.continueBlock()) continue;
                this.lastStage = 0;
                return false;
            }
        }
        return this.blockPlaced <= 0 || (Boolean)this.pauseAfterSupport.getValue() == false;
    }

    boolean placeBlock(BlockPos pos, double offsetX, double offsetY, double offsetZ, boolean redstone, boolean piston, int slot, int position) {
        Block block = Elevatot.mc.field_71441_e.func_180495_p(pos).func_177230_c();
        EnumFacing side = redstone && this.redstoneAbovePiston ? BlockUtil.getPlaceableSideExlude(pos, this.exd) : BlockUtil.getPlaceableSide(pos);
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
        Vec3d hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5 + offsetX, 0.5, 0.5 + offsetZ).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        if (((Boolean)this.rotate.getValue()).booleanValue()) {
            BlockUtil.faceVectorPacketInstant(hitVec, true);
            boolean stop = this.lastHitVec != hitVec && this.lastHitVec != null && Math.abs(this.lastHitVec.field_72450_a - hitVec.field_72450_a) > 0.7 && Math.abs(this.lastHitVec.field_72449_c - hitVec.field_72449_c) > 0.7;
            this.lastHitVec = hitVec;
            if (((Boolean)this.waitRotate.getValue()).booleanValue() && stop) {
                return false;
            }
        }
        Block neighbourBlock = Elevatot.mc.field_71441_e.func_180495_p(neighbour).func_177230_c();
        int oldSlot = Elevatot.mc.field_71439_g.field_71071_by.field_70461_c;
        try {
            if (Elevatot.mc.field_71439_g.field_71071_by.func_70301_a(slot) != ItemStack.field_190927_a && Elevatot.mc.field_71439_g.field_71071_by.field_70461_c != slot) {
                if (slot == -1) {
                    this.noMaterials = true;
                    return false;
                }
                if (((Boolean)this.silentSwitch.getValue()).booleanValue()) {
                    Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slot));
                } else {
                    Elevatot.mc.field_71439_g.field_71071_by.field_70461_c = slot;
                }
            }
        }
        catch (Exception e) {
            PistonCrystal.printDebug("Fatal Error during the creation of the structure. Please, report this bug in the discor's server", true);
            Logger LOGGER = (Logger)LogManager.getLogger((String)"GameSense");
            LOGGER.info("[Elevatot] error during the creation of the structure.");
            if (e.getMessage() != null) {
                LOGGER.info("[Elevatot] error message: " + e.getClass().getName() + " " + e.getMessage());
            } else {
                LOGGER.info("[Elevatot] cannot find the cause");
            }
            boolean i5 = false;
            if (e.getStackTrace().length != 0) {
                LOGGER.info("[Elevatot] StackTrace Start");
                for (StackTraceElement errorMess : e.getStackTrace()) {
                    LOGGER.info("[Elevatot] " + errorMess.toString());
                }
                LOGGER.info("[Elevatot] StackTrace End");
            }
            this.disable();
        }
        if (!this.isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)Elevatot.mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        } else if (piston) {
            switch (position) {
                case 0: {
                    Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(0.0f, 0.0f, Elevatot.mc.field_71439_g.field_70122_E));
                    break;
                }
                case 1: {
                    Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(180.0f, 0.0f, Elevatot.mc.field_71439_g.field_70122_E));
                    break;
                }
                case 2: {
                    Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(-90.0f, 0.0f, Elevatot.mc.field_71439_g.field_70122_E));
                    break;
                }
                default: {
                    Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(90.0f, 0.0f, Elevatot.mc.field_71439_g.field_70122_E));
                }
            }
        }
        Elevatot.mc.field_71442_b.func_187099_a(Elevatot.mc.field_71439_g, Elevatot.mc.field_71441_e, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        if (((Boolean)this.silentSwitch.getValue()).booleanValue()) {
            Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
            Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(oldSlot));
        } else {
            Elevatot.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
        }
        return true;
    }

    BlockPos getTargetPos(int idx) {
        BlockPos offsetPos = new BlockPos(this.toPlace.to_place.get(idx));
        return new BlockPos(this.enemyCoordsDouble[0] + (double)offsetPos.func_177958_n(), this.enemyCoordsDouble[1] + (double)offsetPos.func_177956_o(), this.enemyCoordsDouble[2] + (double)offsetPos.func_177952_p());
    }

    public BlockPos compactBlockPos(int step) {
        try {
            BlockPos offsetPos = new BlockPos(this.toPlace.to_place.get(this.toPlace.supportBlock + step - 1));
            return new BlockPos(this.enemyCoordsDouble[0] + (double)offsetPos.func_177958_n(), this.enemyCoordsDouble[1] + (double)offsetPos.func_177956_o(), this.enemyCoordsDouble[2] + (double)offsetPos.func_177952_p());
        }
        catch (NullPointerException e) {
            PistonCrystal.printDebug("Crash!", false);
            this.disable();
            return new BlockPos(this.enemyCoordsDouble[0], this.enemyCoordsDouble[1], this.enemyCoordsDouble[2]);
        }
    }

    boolean checkVariable() {
        if (this.noMaterials || !this.isHole || !this.enoughSpace) {
            this.disable();
            return true;
        }
        return false;
    }

    void resetValues() {
        this.lastHitVec = null;
        this.sur_block = new double[4][3];
        this.slot_mat = new int[]{-1, -1, -1, -1, -1};
        this.enemyCoordsDouble = new double[3];
        this.toPlace = new structureTemp(0.0, 0, null, -1);
        this.redstonePlaced = false;
        this.noMaterials = false;
        this.redstoneBlockMode = false;
        this.isHole = true;
        this.aimTarget = null;
        this.lastStage = -1;
        this.tickOut = 0;
        this.delayTimeTicks = 0;
        if (((Boolean)this.stopCa.getValue()).booleanValue()) {
            AutoCrystalRewrite.stopAC = true;
        }
    }

    boolean getMaterialsSlot() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = Elevatot.mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack == ItemStack.field_190927_a) continue;
            if (((Boolean)this.forceBurrow.getValue()).booleanValue() && stack.func_77973_b() instanceof ItemSkull) {
                this.slot_mat[4] = i;
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
            if (!(block instanceof BlockRedstoneTorch)) continue;
            this.slot_mat[2] = i;
            this.redstoneBlockMode = false;
        }
        int count = 0;
        for (int val : this.slot_mat) {
            if (val == -1) continue;
            ++count;
        }
        if (((Boolean)this.debugMode.getValue()).booleanValue()) {
            PistonCrystal.printDebug(String.format("%d %d %d %d", this.slot_mat[0], this.slot_mat[1], this.slot_mat[2], this.slot_mat[3]), false);
        }
        return count >= 3 + ((Boolean)this.forceBurrow.getValue() != false ? 1 : 0);
    }

    boolean getAimTarget() {
        this.aimTarget = ((String)this.target.getValue()).equals("Nearest") ? PlayerUtil.findClosestTarget((Double)this.enemyRange.getValue(), this.aimTarget) : PlayerUtil.findLookingPlayer((Double)this.enemyRange.getValue());
        if (this.aimTarget == null) {
            if (!((String)this.target.getValue()).equals("Looking")) {
                this.disable();
            }
        } else {
            this.uuid_enemy = this.aimTarget.func_146103_bH().getId().toString();
        }
        return this.aimTarget != null;
    }

    void playerChecks() {
        if (this.getMaterialsSlot()) {
            if (this.is_in_hole()) {
                this.enemyCoordsDouble = new double[]{this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v};
                this.enemyCoordsInt = new int[]{(int)this.aimTarget.field_70165_t, this.aimTarget.func_180425_c().func_177956_o(), (int)this.aimTarget.field_70161_v};
                this.meCoordsInt = new int[]{(int)Elevatot.mc.field_71439_g.field_70165_t, (int)Elevatot.mc.field_71439_g.field_70163_u, (int)Elevatot.mc.field_71439_g.field_70161_v};
                this.enoughSpace = this.createStructure();
            } else {
                this.isHole = false;
            }
        } else {
            this.noMaterials = true;
        }
    }

    boolean is_in_hole() {
        this.sur_block = new double[][]{{this.aimTarget.field_70165_t + 1.0, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v}, {this.aimTarget.field_70165_t - 1.0, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v}, {this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v + 1.0}, {this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v - 1.0}};
        return !((Boolean)this.checkSurround.getValue() != false && HoleUtil.isHole(EntityUtil.getPosition((Entity)this.aimTarget), true, true).getType() == HoleUtil.HoleType.NONE || (Boolean)this.checkBurrow.getValue() != false && BlockUtil.getBlock(EntityUtil.getPosition((Entity)this.aimTarget)) instanceof BlockAir);
    }

    boolean createStructure() {
        structureTemp addedStructure = new structureTemp(Double.MAX_VALUE, 0, null, -1);
        try {
            if (((Boolean)this.checkAbove.getValue()).booleanValue() && !(BlockUtil.getBlock(this.enemyCoordsDouble[0], this.enemyCoordsDouble[1] + 1.0, this.enemyCoordsDouble[2]) instanceof BlockAir)) {
                return false;
            }
            for (int i = 0; i < 4; ++i) {
                float offsetZ;
                float offsetX;
                double[] pistonCoordsAbs = new double[]{this.sur_block[i][0], this.sur_block[i][1] + 1.0, this.sur_block[i][2]};
                int[] pistonCoordsRel = new int[]{this.disp_surblock[i][0], this.disp_surblock[i][1] + 1, this.disp_surblock[i][2]};
                double distanceNowCrystal = Elevatot.mc.field_71439_g.func_70011_f(pistonCoordsAbs[0], pistonCoordsAbs[1], pistonCoordsAbs[2]);
                if (!(distanceNowCrystal < addedStructure.distance) || !(BlockUtil.getBlock(pistonCoordsAbs[0], pistonCoordsAbs[1], pistonCoordsAbs[2]) instanceof BlockAir) && !(BlockUtil.getBlock(pistonCoordsAbs[0], pistonCoordsAbs[1], pistonCoordsAbs[2]) instanceof BlockPistonBase) || PistonCrystal.someoneInCoords(pistonCoordsAbs[0], pistonCoordsAbs[1], pistonCoordsAbs[2])) continue;
                BlockPos blockPos = new BlockPos(pistonCoordsAbs[0] - (double)(this.disp_surblock[i][0] * 2), pistonCoordsAbs[1], pistonCoordsAbs[2] - (double)(this.disp_surblock[i][2] * 2));
                if (((Boolean)this.checkPush.getValue()).booleanValue() && (!(BlockUtil.getBlock(blockPos) instanceof BlockAir) || !(BlockUtil.getBlock(blockPos.func_177958_n(), blockPos.func_177956_o() + 1, blockPos.func_177952_p()) instanceof BlockAir))) continue;
                if (((Boolean)this.rotate.getValue()).booleanValue()) {
                    int[] pistonCordInt = new int[]{(int)pistonCoordsAbs[0], (int)pistonCoordsAbs[1], (int)pistonCoordsAbs[2]};
                    boolean behindBol = false;
                    if (Math.abs(this.meCoordsInt[0] - this.enemyCoordsInt[0]) != 1 || Math.abs(this.meCoordsInt[2] - this.enemyCoordsInt[2]) != 1) {
                        behindBol = true;
                        if (this.meCoordsInt[0] == this.enemyCoordsInt[0] && this.enemyCoordsInt[0] == pistonCordInt[0]) {
                            if (this.meCoordsInt[2] > this.enemyCoordsInt[2] == this.enemyCoordsInt[2] > pistonCordInt[2]) {
                                behindBol = false;
                            }
                        } else if (this.meCoordsInt[2] == this.enemyCoordsInt[2] && this.enemyCoordsInt[2] == pistonCordInt[2] && this.meCoordsInt[0] > this.enemyCoordsInt[0] == this.enemyCoordsInt[0] > pistonCordInt[0]) {
                            behindBol = false;
                        }
                    }
                    if (behindBol) continue;
                }
                double[] redstoneCoordsAbs = new double[3];
                int[] redstoneCoordsRel = new int[3];
                double minFound = 1000.0;
                boolean foundOne = false;
                for (Object[] pos : this.disp_surblock) {
                    double d;
                    double[] torchCoords = new double[]{pistonCoordsAbs[0] + (double)pos[0], pistonCoordsAbs[1], pistonCoordsAbs[2] + (double)pos[2]};
                    double minNow = Elevatot.mc.field_71439_g.func_70011_f(torchCoords[0], torchCoords[1], torchCoords[2]);
                    if (d > minFound || PistonCrystal.someoneInCoords(torchCoords[0], torchCoords[1], torchCoords[2]) || !(BlockUtil.getBlock(torchCoords[0], torchCoords[1], torchCoords[2]) instanceof BlockRedstoneTorch) && !(BlockUtil.getBlock(torchCoords[0], torchCoords[1], torchCoords[2]) instanceof BlockAir)) continue;
                    redstoneCoordsAbs = new double[]{torchCoords[0], torchCoords[1], torchCoords[2]};
                    redstoneCoordsRel = new int[]{pistonCoordsRel[0] + pos[0], pistonCoordsRel[1], pistonCoordsRel[2] + pos[2]};
                    foundOne = true;
                    minFound = minNow;
                }
                this.redstoneAbovePiston = false;
                if (!foundOne) {
                    if (!this.redstoneBlockMode && BlockUtil.getBlock(pistonCoordsAbs[0], pistonCoordsAbs[1] + 1.0, pistonCoordsAbs[2]) instanceof BlockAir) {
                        redstoneCoordsAbs = new double[]{pistonCoordsAbs[0], pistonCoordsAbs[1] + 1.0, pistonCoordsAbs[2]};
                        redstoneCoordsRel = new int[]{pistonCoordsRel[0], pistonCoordsRel[1] + 1, pistonCoordsRel[2]};
                        this.redstoneAbovePiston = true;
                    }
                    if (!this.redstoneAbovePiston) continue;
                }
                ArrayList<Vec3d> toPlaceTemp = new ArrayList<Vec3d>();
                int supportBlock = 0;
                if (((Boolean)this.forceBurrow.getValue()).booleanValue()) {
                    toPlaceTemp.add(new Vec3d(0.0, 0.0, 0.0));
                    ++supportBlock;
                }
                if (!this.redstoneBlockMode) {
                    if (this.redstoneAbovePiston) {
                        for (int hight = -1; hight < 2; ++hight) {
                            if (PistonCrystal.someoneInCoords(pistonCoordsAbs[0] + (double)pistonCoordsRel[0], pistonCoordsAbs[1], pistonCoordsAbs[2] + (double)pistonCoordsRel[0]) || !(BlockUtil.getBlock(pistonCoordsAbs[0] + (double)pistonCoordsRel[0], pistonCoordsAbs[1] + (double)hight, pistonCoordsRel[2] + pistonCoordsRel[2]) instanceof BlockAir)) continue;
                            toPlaceTemp.add(new Vec3d((double)(pistonCoordsRel[0] * 2), (double)(pistonCoordsRel[1] + hight), (double)(pistonCoordsRel[2] * 2)));
                            ++supportBlock;
                        }
                    } else if (BlockUtil.getBlock(redstoneCoordsAbs[0], redstoneCoordsAbs[1] - 1.0, redstoneCoordsAbs[2]) instanceof BlockAir) {
                        toPlaceTemp.add(new Vec3d((double)redstoneCoordsRel[0], (double)(redstoneCoordsRel[1] - 1), (double)redstoneCoordsRel[2]));
                        ++supportBlock;
                    }
                }
                if (((Boolean)this.trapMode.getValue()).booleanValue() && !((Boolean)this.doubleTrap.getValue()).booleanValue()) {
                    for (Vec3d var : new Vec3d[]{new Vec3d(0.0, 0.0, 1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(1.0, 1.0, 1.0), new Vec3d(1.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(1.0, 2.0, 1.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, -1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, -1.0)}) {
                        if ((int)var.field_72450_a == this.disp_surblock[i][0] && (int)var.field_72449_c == this.disp_surblock[i][2]) continue;
                        toPlaceTemp.add(new Vec3d((double)((int)var.field_72450_a - this.disp_surblock[i][0]), var.field_72448_b, (double)((int)var.field_72449_c - this.disp_surblock[i][2])));
                        ++supportBlock;
                    }
                    if (((Boolean)this.addRoof.getValue()).booleanValue()) {
                        for (Vec3d var2 : new Vec3d[]{new Vec3d(0.0, 3.0, -1.0), new Vec3d(1.0, 3.0, -1.0)}) {
                            toPlaceTemp.add(new Vec3d((double)((int)var2.field_72450_a - this.disp_surblock[i][0]), var2.field_72448_b, (double)((int)var2.field_72449_c - this.disp_surblock[i][2])));
                            ++supportBlock;
                        }
                    }
                } else if (((Boolean)this.doubleTrap.getValue()).booleanValue()) {
                    int var;
                    int var2;
                    Object[] pos;
                    pos = new Vec3d[]{new Vec3d(0.0, 0.0, 1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(1.0, 1.0, 1.0), new Vec3d(1.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(1.0, 2.0, 1.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, -1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, -1.0)};
                    int torchCoords = pos.length;
                    for (var2 = 0; var2 < torchCoords; ++var2) {
                        var = pos[var2];
                        if ((int)var.field_72450_a != this.disp_surblock[i][0] || (int)var.field_72449_c != this.disp_surblock[i][2]) {
                            toPlaceTemp.add(new Vec3d((double)((int)var.field_72450_a - this.disp_surblock[i][0]), var.field_72448_b, (double)((int)var.field_72449_c - this.disp_surblock[i][2])));
                            ++supportBlock;
                            continue;
                        }
                        int hight = var;
                    }
                    toPlaceTemp.add(new Vec3d((double)this.disp_surblock[i][0], 2.0, (double)this.disp_surblock[i][2]));
                    ++supportBlock;
                    if (((Boolean)this.addRoof.getValue()).booleanValue()) {
                        pos = new Vec3d[]{new Vec3d(0.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 0.0)};
                        torchCoords = pos.length;
                        for (var2 = 0; var2 < torchCoords; ++var2) {
                            var = pos[var2];
                            toPlaceTemp.add(new Vec3d((double)((int)var.field_72450_a - this.disp_surblock[i][0]), var.field_72448_b, (double)((int)var.field_72449_c - this.disp_surblock[i][2])));
                            ++supportBlock;
                        }
                        toPlaceTemp.add(new Vec3d(0.0, 3.0, 0.0));
                        ++supportBlock;
                    }
                }
                toPlaceTemp.add(new Vec3d((double)pistonCoordsRel[0], (double)pistonCoordsRel[1], (double)pistonCoordsRel[2]));
                toPlaceTemp.add(new Vec3d((double)redstoneCoordsRel[0], (double)redstoneCoordsRel[1], (double)redstoneCoordsRel[2]));
                int position = this.disp_surblock[i][0] == 0 ? (this.disp_surblock[i][2] == 1 ? 0 : 1) : (this.disp_surblock[i][0] == 1 ? 2 : 3);
                if (this.disp_surblock[i][0] != 0) {
                    offsetX = (float)this.disp_surblock[i][0] / 2.0f;
                    offsetZ = Elevatot.mc.field_71439_g.func_70092_e(pistonCoordsAbs[0], pistonCoordsAbs[1], pistonCoordsAbs[2] + 0.5) > Elevatot.mc.field_71439_g.func_70092_e(pistonCoordsAbs[0], pistonCoordsAbs[1], pistonCoordsAbs[2] - 0.5) ? -0.5f : 0.5f;
                } else {
                    offsetZ = (float)this.disp_surblock[i][2] / 2.0f;
                    offsetX = Elevatot.mc.field_71439_g.func_70092_e(pistonCoordsAbs[0] + 0.5, pistonCoordsAbs[1], pistonCoordsAbs[2]) > Elevatot.mc.field_71439_g.func_70092_e(pistonCoordsAbs[0] - 0.5, pistonCoordsAbs[1], pistonCoordsAbs[2]) ? -0.5f : 0.5f;
                }
                float offsetY = this.meCoordsInt[1] - this.enemyCoordsInt[1] == -1 ? 0.0f : 1.0f;
                addedStructure.replaceValues(distanceNowCrystal, supportBlock, toPlaceTemp, offsetX, offsetZ, offsetY, position, blockPos);
                this.toPlace = addedStructure;
            }
        }
        catch (Exception e) {
            PistonCrystal.printDebug("Fatal Error during the creation of the structure. Please, report this bug in the discord's server", true);
            Logger LOGGER = (Logger)LogManager.getLogger((String)"GameSense");
            LOGGER.info("[Elevator] error during the creation of the structure.");
            if (e.getMessage() != null) {
                LOGGER.info("[Elevator] error message: " + e.getClass().getName() + " " + e.getMessage());
            } else {
                LOGGER.info("[Elevator] cannot find the cause");
            }
            int i5 = 0;
            if (e.getStackTrace().length != 0) {
                LOGGER.info("[Elevator] StackTrace Start");
                for (StackTraceElement errorMess : e.getStackTrace()) {
                    LOGGER.info("[Elevator] " + errorMess.toString());
                }
                LOGGER.info("[Elevator] StackTrace End");
            }
            if (this.aimTarget != null) {
                LOGGER.info("[Elevator] closest target is not null");
            } else {
                LOGGER.info("[Elevator] closest target is null somehow");
            }
            for (double[] cord_b : this.sur_block) {
                if (cord_b != null) {
                    LOGGER.info("[Elevator] " + i5 + " is not null");
                } else {
                    LOGGER.info("[Elevator] " + i5 + " is null");
                }
                ++i5;
            }
        }
        if (((Boolean)this.debugMode.getValue()).booleanValue() && addedStructure.to_place != null) {
            PistonCrystal.printDebug("Skeleton structure:", false);
            for (Vec3d parte : addedStructure.to_place) {
                PistonCrystal.printDebug(String.format("%f %f %f", parte.field_72450_a, parte.field_72448_b, parte.field_72449_c), false);
            }
            PistonCrystal.printDebug(String.format("X: %f Y: %f Z: %f", Float.valueOf(this.toPlace.offsetX), Float.valueOf(this.toPlace.offsetY), Float.valueOf(this.toPlace.offsetZ)), false);
        }
        return addedStructure.to_place != null;
    }

    @Override
    public String getHudInfo() {
        String temp;
        if (this.aimTarget != null && !(temp = this.aimTarget.func_146103_bH().getName()).equalsIgnoreCase("")) {
            return "[" + ChatFormatting.WHITE + temp + ChatFormatting.GRAY + "]";
        }
        return "";
    }

    static class structureTemp {
        public double distance;
        public int supportBlock;
        public List<Vec3d> to_place;
        public float offsetX;
        public float offsetY;
        public float offsetZ;
        public int position;
        BlockPos target;

        public structureTemp(double distance, int supportBlock, List<Vec3d> to_place, int position) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.position = position;
        }

        public void replaceValues(double distance, int supportBlock, List<Vec3d> to_place, float offsetX, float offsetZ, float offsetY, int position, BlockPos target) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.offsetX = offsetX;
            this.offsetZ = offsetZ;
            this.offsetY = offsetY;
            this.position = position;
            this.target = target;
        }
    }
}

