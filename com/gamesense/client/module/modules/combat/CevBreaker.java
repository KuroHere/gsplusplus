/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.Phase;
import com.gamesense.api.event.events.DestroyBlockEvent;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.util.player.SpoofRotationUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.api.util.world.combat.CrystalUtil;
import com.gamesense.client.GameSense;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AutoCrystalRewrite;
import com.gamesense.client.module.modules.misc.AutoGG;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Module.Declaration(name="CevBreaker", category=Category.Combat, priority=999)
public class CevBreaker
extends Module {
    BooleanSetting targetSection = this.registerBoolean("Target Section", true);
    ModeSetting target = this.registerMode("Target", Arrays.asList("Nearest", "Looking"), "Nearest", () -> (Boolean)this.targetSection.getValue());
    DoubleSetting enemyRange = this.registerDouble("Range", 4.9, 0.0, 6.0, () -> (Boolean)this.targetSection.getValue());
    BooleanSetting breakPlaceSection = this.registerBoolean("Break Place Section", true);
    ModeSetting breakCrystal = this.registerMode("Break Crystal", Arrays.asList("Vanilla", "Packet", "None"), "Packet", () -> (Boolean)this.breakPlaceSection.getValue());
    ModeSetting breakBlock = this.registerMode("Break Block", Arrays.asList("Normal", "Packet"), "Packet", () -> (Boolean)this.breakPlaceSection.getValue());
    BooleanSetting fastPlace = this.registerBoolean("Fast Place", false, () -> (Boolean)this.breakPlaceSection.getValue());
    BooleanSetting fastBreak = this.registerBoolean("Fast Break", true, () -> (Boolean)this.breakPlaceSection.getValue());
    BooleanSetting trapPlayer = this.registerBoolean("Trap Player", false, () -> (Boolean)this.breakPlaceSection.getValue());
    BooleanSetting antiStep = this.registerBoolean("Anti Step", false, () -> (Boolean)this.breakPlaceSection.getValue());
    BooleanSetting placeCrystal = this.registerBoolean("Place Crystal", true, () -> (Boolean)this.breakPlaceSection.getValue());
    BooleanSetting confirmBreak = this.registerBoolean("No Glitch Break", true, () -> (Boolean)this.breakPlaceSection.getValue());
    BooleanSetting confirmPlace = this.registerBoolean("No Glitch Place", true, () -> (Boolean)this.breakPlaceSection.getValue());
    BooleanSetting rotationSection = this.registerBoolean("Rotation Section", true);
    IntegerSetting preRotationDelay = this.registerInteger("Pre Rotation Delay", 0, 0, 20, () -> (Boolean)this.rotationSection.getValue());
    IntegerSetting afterRotationDelay = this.registerInteger("After Rotation Delay", 0, 0, 20, () -> (Boolean)this.rotationSection.getValue());
    BooleanSetting forceRotation = this.registerBoolean("Force Rotation", false, () -> (Boolean)this.rotationSection.getValue());
    BooleanSetting forceBreaker = this.registerBoolean("Force Breaker", false, () -> (Boolean)this.rotationSection.getValue());
    BooleanSetting rotate = this.registerBoolean("Rotate", false, () -> (Boolean)this.rotationSection.getValue());
    BooleanSetting delaySection = this.registerBoolean("Delay Section", true);
    IntegerSetting supDelay = this.registerInteger("Support Delay", 1, 0, 4, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting crystalDelay = this.registerInteger("Crystal Delay", 2, 0, 20, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 2, 6, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting hitDelay = this.registerInteger("Hit Delay", 2, 0, 20, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting midHitDelay = this.registerInteger("Mid Hit Delay", 1, 0, 20, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting endDelay = this.registerInteger("End Delay", 1, 0, 20, () -> (Boolean)this.delaySection.getValue());
    BooleanSetting miscSection = this.registerBoolean("Misc Section", true);
    IntegerSetting pickSwitchTick = this.registerInteger("Pick Switch Tick", 100, 0, 500, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting antiWeakness = this.registerBoolean("Anti Weakness", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting switchSword = this.registerBoolean("Switch Sword", false, () -> (Boolean)this.miscSection.getValue());
    public static int cur_item = -1;
    public static boolean isActive = false;
    public static boolean forceBrk = false;
    private boolean noMaterials = false;
    private boolean hasMoved = false;
    private boolean isSneaking = false;
    private boolean isHole = true;
    private boolean enoughSpace = true;
    private boolean broken;
    private boolean stoppedCa;
    private boolean deadPl;
    private boolean rotationPlayerMoved;
    private boolean prevBreak;
    private boolean preRotationBol;
    private int oldSlot = -1;
    private int stage;
    private int delayTimeTicks;
    private int hitTryTick;
    private int tickPick;
    private int afterRotationTick;
    private int preRotationTick;
    private final int[][] model = new int[][]{{1, 1, 0}, {-1, 1, 0}, {0, 1, 1}, {0, 1, -1}};
    public static boolean isPossible = false;
    private int[] slot_mat;
    private int[] delayTable;
    private int[] enemyCoordsInt;
    private double[] enemyCoordsDouble;
    private structureTemp toPlace;
    Double[][] sur_block = new Double[4][3];
    private EntityPlayer aimTarget;
    @EventHandler
    private final Listener<DestroyBlockEvent> listener2 = new Listener<DestroyBlockEvent>(event -> {
        if (this.enemyCoordsInt != null && event.getBlockPos().field_177962_a + (event.getBlockPos().field_177962_a < 0 ? 1 : 0) == this.enemyCoordsInt[0] && event.getBlockPos().field_177961_c + (event.getBlockPos().field_177961_c < 0 ? 1 : 0) == this.enemyCoordsInt[2]) {
            this.destroyCrystalAlgo();
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PacketEvent.Receive> packetReceiveListener = new Listener<PacketEvent.Receive>(event -> {
        SPacketSoundEffect packet;
        if (event.getPacket() instanceof SPacketSoundEffect && (packet = (SPacketSoundEffect)event.getPacket()).func_186977_b() == SoundCategory.BLOCKS && packet.func_186978_a() == SoundEvents.field_187539_bB && (int)packet.func_149207_d() == this.enemyCoordsInt[0] && (int)packet.func_149210_f() == this.enemyCoordsInt[2]) {
            this.stage = 1;
        }
    }, new Predicate[0]);
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
                this.enoughSpace = this.createStructure();
            } else {
                this.isHole = false;
            }
        } else {
            this.noMaterials = true;
        }
    }

    private void initValues() {
        this.preRotationBol = false;
        this.preRotationTick = 0;
        this.afterRotationTick = 0;
        isPossible = false;
        this.aimTarget = null;
        this.lastHitVec = null;
        this.delayTable = new int[]{(Integer)this.supDelay.getValue(), (Integer)this.crystalDelay.getValue(), (Integer)this.hitDelay.getValue(), (Integer)this.endDelay.getValue()};
        this.toPlace = new structureTemp(0.0, 0, new ArrayList<Vec3d>());
        isActive = true;
        this.isHole = true;
        this.broken = false;
        this.deadPl = false;
        this.rotationPlayerMoved = false;
        this.hasMoved = false;
        this.slot_mat = new int[]{-1, -1, -1, -1};
        this.delayTimeTicks = 0;
        this.stage = 0;
        if (CevBreaker.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        this.oldSlot = CevBreaker.mc.field_71439_g.field_71071_by.field_70461_c;
        this.stoppedCa = false;
        cur_item = -1;
        if (ModuleManager.isModuleEnabled(AutoCrystalRewrite.class)) {
            AutoCrystalRewrite.stopAC = true;
            this.stoppedCa = true;
        }
        forceBrk = (Boolean)this.forceBreaker.getValue();
    }

    @Override
    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe(this);
        SpoofRotationUtil.ROTATION_UTIL.onDisable();
        if (CevBreaker.mc.field_71439_g == null) {
            return;
        }
        String output = "";
        String materialsNeeded = "";
        if (this.aimTarget == null) {
            output = "No target found...";
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
        }
        this.setDisabledMessage(output + "CevBreaker turned OFF!");
        if (!materialsNeeded.equals("")) {
            this.setDisabledMessage("Materials missing:" + materialsNeeded);
        }
        if (this.stoppedCa) {
            AutoCrystalRewrite.stopAC = false;
            this.stoppedCa = false;
        }
        if (this.isSneaking) {
            CevBreaker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)CevBreaker.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        if (this.oldSlot != CevBreaker.mc.field_71439_g.field_71071_by.field_70461_c && this.oldSlot != -1) {
            CevBreaker.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
            this.oldSlot = -1;
        }
        forceBrk = false;
        isActive = false;
        AutoCrystalRewrite.stopAC = false;
        isPossible = false;
        this.noMaterials = false;
    }

    private String getMissingMaterials() {
        StringBuilder output = new StringBuilder();
        if (this.slot_mat[0] == -1) {
            output.append(" Obsidian");
        }
        if (this.slot_mat[1] == -1) {
            output.append(" Crystal");
        }
        if ((((Boolean)this.antiWeakness.getValue()).booleanValue() || ((Boolean)this.switchSword.getValue()).booleanValue()) && this.slot_mat[3] == -1) {
            output.append(" Sword");
        }
        if (this.slot_mat[2] == -1) {
            output.append(" Pick");
        }
        return output.toString();
    }

    @Override
    public void onUpdate() {
        if (CevBreaker.mc.field_71439_g == null || CevBreaker.mc.field_71439_g.field_70128_L) {
            this.disable();
            return;
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
                    this.playerChecks();
                    if (ModuleManager.isModuleEnabled(AutoGG.class)) {
                        AutoGG.INSTANCE.addTargetedPlayer(this.aimTarget.func_70005_c_());
                    }
                }
            } else {
                this.checkVariable();
            }
            return;
        }
        if (this.aimTarget.field_70128_L) {
            this.deadPl = true;
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
                    if (this.getCrystal() != null) {
                        this.stage = 3;
                        return;
                    }
                    if ((Integer)this.afterRotationDelay.getValue() != 0 && this.afterRotationTick != (Integer)this.afterRotationDelay.getValue()) {
                        ++this.afterRotationTick;
                        return;
                    }
                    if ((Integer)this.preRotationDelay.getValue() != 0 && !this.preRotationBol) {
                        this.placeBlockThings(this.stage, true, false);
                        if (this.preRotationTick == (Integer)this.preRotationDelay.getValue()) {
                            this.preRotationBol = true;
                            this.preRotationTick = 0;
                        } else {
                            ++this.preRotationTick;
                            break;
                        }
                    }
                    this.placeBlockThings(this.stage, false, false);
                    if (((Boolean)this.fastPlace.getValue()).booleanValue()) {
                        this.placeCrystal(false);
                    }
                    this.prevBreak = false;
                    this.tickPick = 0;
                    break;
                }
                case 2: {
                    if ((Integer)this.afterRotationDelay.getValue() != 0 && this.afterRotationTick != (Integer)this.afterRotationDelay.getValue()) {
                        ++this.afterRotationTick;
                        return;
                    }
                    if ((Integer)this.preRotationDelay.getValue() != 0 && !this.preRotationBol) {
                        this.placeCrystal(true);
                        if (this.preRotationTick == (Integer)this.preRotationDelay.getValue()) {
                            this.preRotationBol = true;
                            this.preRotationTick = 0;
                        } else {
                            ++this.preRotationTick;
                            break;
                        }
                    }
                    if (((Boolean)this.confirmPlace.getValue()).booleanValue() && !(BlockUtil.getBlock(this.compactBlockPos(0)) instanceof BlockObsidian)) {
                        --this.stage;
                        return;
                    }
                    this.placeCrystal(false);
                    break;
                }
                case 3: {
                    if (((Boolean)this.confirmPlace.getValue()).booleanValue() && this.getCrystal() == null) {
                        this.stage = 1;
                        return;
                    }
                    int switchValue = 3;
                    if (!((Boolean)this.switchSword.getValue()).booleanValue() || this.tickPick == (Integer)this.pickSwitchTick.getValue() || this.tickPick++ == 0) {
                        switchValue = 2;
                    }
                    this.switchPick(switchValue);
                    BlockPos obbyBreak = new BlockPos(this.enemyCoordsDouble[0], (double)(this.enemyCoordsInt[1] + 2), this.enemyCoordsDouble[2]);
                    if (BlockUtil.getBlock(obbyBreak) instanceof BlockObsidian) {
                        EnumFacing sideBreak = BlockUtil.getPlaceableSide(obbyBreak);
                        if (sideBreak != null) {
                            switch ((String)this.breakBlock.getValue()) {
                                case "Packet": {
                                    if (this.prevBreak) break;
                                    CevBreaker.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                                    CevBreaker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, obbyBreak, sideBreak));
                                    CevBreaker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, obbyBreak, sideBreak));
                                    this.prevBreak = true;
                                    break;
                                }
                                case "Normal": {
                                    CevBreaker.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                                    CevBreaker.mc.field_71442_b.func_180512_c(obbyBreak, sideBreak);
                                }
                            }
                        }
                        break;
                    }
                    this.destroyCrystalAlgo();
                }
            }
        }
    }

    private void switchPick(int switchValue) {
        if (cur_item != this.slot_mat[switchValue]) {
            if (this.slot_mat[switchValue] == -1) {
                this.noMaterials = true;
                return;
            }
            cur_item = this.slot_mat[switchValue];
            CevBreaker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(cur_item));
            CevBreaker.mc.field_71439_g.field_71071_by.field_70461_c = cur_item;
        }
    }

    private void placeCrystal(boolean onlyRotate) {
        this.placeBlockThings(this.stage, onlyRotate, true);
        if (((Boolean)this.fastBreak.getValue()).booleanValue() && !onlyRotate) {
            this.fastBreakFun();
        }
    }

    private void fastBreakFun() {
        this.switchPick(2);
        CevBreaker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(this.enemyCoordsInt[0], this.enemyCoordsInt[1] + 2, this.enemyCoordsInt[2]), EnumFacing.UP));
        isPossible = true;
    }

    private Entity getCrystal() {
        for (Entity t : CevBreaker.mc.field_71441_e.field_72996_f) {
            if (!(t instanceof EntityEnderCrystal) || (int)t.field_70165_t != this.enemyCoordsInt[0] || (int)t.field_70161_v != this.enemyCoordsInt[2] || t.field_70163_u - (double)this.enemyCoordsInt[1] != 3.0) continue;
            return t;
        }
        return null;
    }

    public void destroyCrystalAlgo() {
        isPossible = false;
        Entity crystal = this.getCrystal();
        if (((Boolean)this.confirmBreak.getValue()).booleanValue() && this.broken && crystal == null) {
            this.stage = 1;
            this.broken = false;
        }
        if (crystal != null) {
            this.breakCrystalPiston(crystal);
            if (((Boolean)this.confirmBreak.getValue()).booleanValue()) {
                this.broken = true;
            } else {
                this.stage = 1;
            }
        } else {
            this.stage = 1;
        }
    }

    private void breakCrystalPiston(Entity crystal) {
        if (this.hitTryTick++ < (Integer)this.midHitDelay.getValue()) {
            return;
        }
        this.hitTryTick = 0;
        if (((Boolean)this.antiWeakness.getValue()).booleanValue() && CevBreaker.mc.field_71439_g.func_70644_a(MobEffects.field_76437_t) && CevBreaker.mc.field_71439_g.func_70651_bq().stream().noneMatch(e -> e.func_76453_d().contains("damageBoost") && e.func_76458_c() > 0)) {
            CevBreaker.mc.field_71439_g.field_71071_by.field_70461_c = this.slot_mat[3];
        }
        Vec3d vecCrystal = crystal.func_174791_d().func_72441_c(0.5, 0.5, 0.5);
        if (!((String)this.breakCrystal.getValue()).equalsIgnoreCase("None") && ((Boolean)this.rotate.getValue()).booleanValue()) {
            SpoofRotationUtil.ROTATION_UTIL.lookAtPacket(vecCrystal.field_72450_a, vecCrystal.field_72448_b, vecCrystal.field_72449_c, (EntityPlayer)CevBreaker.mc.field_71439_g);
            if (((Boolean)this.forceRotation.getValue()).booleanValue()) {
                this.lastHitVec = vecCrystal;
            }
        }
        try {
            switch ((String)this.breakCrystal.getValue()) {
                case "Vanilla": {
                    CrystalUtil.breakCrystal(crystal);
                    break;
                }
                case "Packet": {
                    CrystalUtil.breakCrystalPacket(crystal);
                    break;
                }
            }
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
        if (((Boolean)this.rotate.getValue()).booleanValue()) {
            SpoofRotationUtil.ROTATION_UTIL.resetRotation();
        }
    }

    private boolean placeSupport() {
        int checksDone = 0;
        int blockDone = 0;
        if (this.toPlace.supportBlock > 0) {
            do {
                BlockPos targetPos;
                if (!(BlockUtil.getBlock(targetPos = this.getTargetPos(checksDone)) instanceof BlockAir)) continue;
                if ((Integer)this.preRotationDelay.getValue() != 0 && !this.preRotationBol) {
                    if (this.preRotationTick == 0) {
                        this.placeBlock(targetPos, 0, true);
                    }
                    if (this.preRotationTick == (Integer)this.preRotationDelay.getValue()) {
                        this.preRotationBol = true;
                        this.preRotationTick = 0;
                    } else {
                        ++this.preRotationTick;
                        return false;
                    }
                }
                if (!this.placeBlock(targetPos, 0, false)) continue;
                this.preRotationBol = false;
                if (++blockDone != (Integer)this.blocksPerTick.getValue()) continue;
                return false;
            } while (++checksDone != this.toPlace.supportBlock);
        }
        this.stage = this.stage == 0 ? 1 : this.stage;
        return true;
    }

    private boolean changeItem(int step) {
        if (this.slot_mat[step] == 11 || CevBreaker.mc.field_71439_g.field_71071_by.func_70301_a(this.slot_mat[step]) != ItemStack.field_190927_a) {
            if (cur_item != this.slot_mat[step]) {
                if (this.slot_mat[step] == -1) {
                    this.noMaterials = true;
                    return true;
                }
                if (this.slot_mat[step] != 11) {
                    cur_item = this.slot_mat[step];
                    CevBreaker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(cur_item));
                    CevBreaker.mc.field_71439_g.field_71071_by.field_70461_c = cur_item;
                }
            }
        } else {
            this.noMaterials = true;
            return true;
        }
        return false;
    }

    private boolean placeBlock(BlockPos pos, int step, boolean onlyRotate) {
        if (this.changeItem(step)) {
            return false;
        }
        if (onlyRotate) {
            EnumFacing side = BlockUtil.getPlaceableSide(pos);
            if (side == null) {
                return false;
            }
            BlockPos neighbour = pos.func_177972_a(side);
            EnumFacing opposite = side.func_176734_d();
            if (!BlockUtil.canBeClicked(neighbour)) {
                return false;
            }
            double add = step == 1 && (int)CevBreaker.mc.field_71439_g.field_70163_u == this.enemyCoordsInt[1] ? -0.5 : 0.0;
            this.lastHitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.5 + add, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
            return false;
        }
        EnumHand handSwing = EnumHand.MAIN_HAND;
        if (this.slot_mat[step] == 11) {
            handSwing = EnumHand.OFF_HAND;
        }
        PlacementUtil.place(pos, handSwing, (Boolean)this.rotate.getValue() != false && (Boolean)this.forceRotation.getValue() == false, false);
        return true;
    }

    public void placeBlockThings(int step, boolean onlyRotate, boolean isCrystal) {
        if (step != 1 || ((Boolean)this.placeCrystal.getValue()).booleanValue()) {
            BlockPos targetPos = this.compactBlockPos(--step);
            if (!isCrystal) {
                this.placeBlock(targetPos, step, onlyRotate);
            } else {
                if (this.changeItem(step)) {
                    return;
                }
                EnumHand handSwing = EnumHand.MAIN_HAND;
                if (this.slot_mat[step] == 11) {
                    handSwing = EnumHand.OFF_HAND;
                }
                CevBreaker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(targetPos.func_177963_a(0.5, 0.5, 0.5), EnumFacing.func_190914_a((BlockPos)targetPos, (EntityLivingBase)CevBreaker.mc.field_71439_g), handSwing, 0.0f, 0.0f, 0.0f));
                CevBreaker.mc.field_71439_g.func_184609_a(handSwing);
            }
        }
        if (!onlyRotate) {
            ++this.stage;
            this.afterRotationTick = 0;
            this.preRotationBol = false;
        }
    }

    public BlockPos compactBlockPos(int step) {
        BlockPos offsetPos = new BlockPos(this.toPlace.to_place.get(this.toPlace.supportBlock + step));
        return new BlockPos(this.enemyCoordsDouble[0] + (double)offsetPos.func_177958_n(), this.enemyCoordsDouble[1] + (double)offsetPos.func_177956_o(), this.enemyCoordsDouble[2] + (double)offsetPos.func_177952_p());
    }

    private BlockPos getTargetPos(int idx) {
        BlockPos offsetPos = new BlockPos(this.toPlace.to_place.get(idx));
        return new BlockPos(this.enemyCoordsDouble[0] + (double)offsetPos.func_177958_n(), this.enemyCoordsDouble[1] + (double)offsetPos.func_177956_o(), this.enemyCoordsDouble[2] + (double)offsetPos.func_177952_p());
    }

    private boolean checkVariable() {
        if (this.noMaterials || !this.isHole || !this.enoughSpace || this.hasMoved || this.deadPl || this.rotationPlayerMoved) {
            this.disable();
            return true;
        }
        return false;
    }

    private boolean createStructure() {
        if (Objects.requireNonNull(BlockUtil.getBlock(this.enemyCoordsDouble[0], this.enemyCoordsDouble[1] + 2.0, this.enemyCoordsDouble[2]).getRegistryName()).toString().toLowerCase().contains("bedrock") || !(BlockUtil.getBlock(this.enemyCoordsDouble[0], this.enemyCoordsDouble[1] + 3.0, this.enemyCoordsDouble[2]) instanceof BlockAir) || !(BlockUtil.getBlock(this.enemyCoordsDouble[0], this.enemyCoordsDouble[1] + 4.0, this.enemyCoordsDouble[2]) instanceof BlockAir)) {
            return false;
        }
        double max_found = Double.MIN_VALUE;
        int cor = 0;
        int i = 0;
        for (Double[] cord_b : this.sur_block) {
            double d;
            double distance_now = CevBreaker.mc.field_71439_g.func_174818_b(new BlockPos(cord_b[0].doubleValue(), cord_b[1].doubleValue(), cord_b[2].doubleValue()));
            if (d > max_found) {
                max_found = distance_now;
                cor = i;
            }
            ++i;
        }
        this.toPlace.to_place.add(new Vec3d((double)this.model[cor][0], 1.0, (double)this.model[cor][2]));
        this.toPlace.to_place.add(new Vec3d((double)this.model[cor][0], 2.0, (double)this.model[cor][2]));
        this.toPlace.supportBlock = 2;
        if (((Boolean)this.trapPlayer.getValue()).booleanValue() || ((Boolean)this.antiStep.getValue()).booleanValue()) {
            for (int high = 1; high < 3; ++high) {
                if (high == 2 && !((Boolean)this.antiStep.getValue()).booleanValue()) continue;
                for (int[] modelBas : this.model) {
                    Vec3d toAdd = new Vec3d((double)modelBas[0], (double)high, (double)modelBas[2]);
                    if (this.toPlace.to_place.contains(toAdd)) continue;
                    this.toPlace.to_place.add(toAdd);
                    ++this.toPlace.supportBlock;
                }
            }
        }
        this.toPlace.to_place.add(new Vec3d(0.0, 2.0, 0.0));
        this.toPlace.to_place.add(new Vec3d(0.0, 2.0, 0.0));
        return true;
    }

    private boolean getMaterialsSlot() {
        if (CevBreaker.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemEndCrystal) {
            this.slot_mat[1] = 11;
        }
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = CevBreaker.mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack == ItemStack.field_190927_a) continue;
            if (this.slot_mat[1] == -1 && stack.func_77973_b() instanceof ItemEndCrystal) {
                this.slot_mat[1] = i;
            } else if ((((Boolean)this.antiWeakness.getValue()).booleanValue() || ((Boolean)this.switchSword.getValue()).booleanValue()) && stack.func_77973_b() instanceof ItemSword) {
                this.slot_mat[3] = i;
            } else if (stack.func_77973_b() instanceof ItemPickaxe) {
                this.slot_mat[2] = i;
            }
            if (!(stack.func_77973_b() instanceof ItemBlock) || !((block = ((ItemBlock)stack.func_77973_b()).func_179223_d()) instanceof BlockObsidian)) continue;
            this.slot_mat[0] = i;
        }
        int count = 0;
        for (int val : this.slot_mat) {
            if (val == -1) continue;
            ++count;
        }
        return count >= 3 + ((Boolean)this.antiWeakness.getValue() != false || (Boolean)this.switchSword.getValue() != false ? 1 : 0);
    }

    private boolean is_in_hole() {
        this.sur_block = new Double[][]{{this.aimTarget.field_70165_t + 1.0, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v}, {this.aimTarget.field_70165_t - 1.0, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v}, {this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v + 1.0}, {this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v - 1.0}};
        return HoleUtil.isHole(EntityUtil.getPosition((Entity)this.aimTarget), true, true).getType() != HoleUtil.HoleType.NONE;
    }

    private static class structureTemp {
        public double distance;
        public int supportBlock;
        public ArrayList<Vec3d> to_place;
        public int direction;

        public structureTemp(double distance, int supportBlock, ArrayList<Vec3d> to_place) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.direction = -1;
        }
    }
}

