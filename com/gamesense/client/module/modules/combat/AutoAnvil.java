/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AutoCrystalRewrite;
import com.gamesense.client.module.modules.combat.OffHand;
import com.gamesense.client.module.modules.misc.AutoGG;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Module.Declaration(name="AutoAnvil", category=Category.Combat)
public class AutoAnvil
extends Module {
    ModeSetting anvilMode = this.registerMode("Mode", Arrays.asList("Pick", "Feet", "None"), "Pick");
    ModeSetting target = this.registerMode("Target", Arrays.asList("Nearest", "Looking"), "Nearest");
    ModeSetting anvilPlace = this.registerMode("Anvil Place", Arrays.asList("Single", "Double", "Full"), "Single");
    BooleanSetting antiCrystal = this.registerBoolean("Anti Crystal", false);
    BooleanSetting fastAnvil = this.registerBoolean("Fast Anvil", true);
    BooleanSetting offHandObby = this.registerBoolean("Off Hand Obby", false);
    BooleanSetting rotate = this.registerBoolean("Rotate", true);
    DoubleSetting enemyRange = this.registerDouble("Range", 5.9, 0.0, 6.0);
    DoubleSetting decrease = this.registerDouble("Decrease", 2.0, 0.0, 6.0);
    IntegerSetting tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
    IntegerSetting blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 8);
    IntegerSetting hDistance = this.registerInteger("H Distance", 7, 1, 10);
    IntegerSetting minH = this.registerInteger("Min H", 3, 1, 10);
    IntegerSetting failStop = this.registerInteger("Fail Stop", 2, 1, 10);
    private boolean isSneaking = false;
    private boolean firstRun = false;
    private boolean noMaterials = false;
    private boolean hasMoved = false;
    private boolean isHole = true;
    private boolean enoughSpace = true;
    private boolean blockUp = false;
    private int oldSlot = -1;
    private int noKick;
    private int anvilBlock;
    private ArrayList<Integer> anvilsPositions = new ArrayList();
    private int[] slot_mat = new int[]{-1, -1, -1, -1};
    private double[] enemyCoords;
    Double[][] sur_block;
    int[][] model = new int[][]{{1, 1, 0}, {-1, 1, 0}, {0, 1, 1}, {0, 1, -1}};
    private int blocksPlaced = 0;
    private int delayTimeTicks = 0;
    private int offsetSteps = 0;
    private boolean pick_d = false;
    private EntityPlayer aimTarget;
    private static ArrayList<Vec3d> to_place = new ArrayList();

    @Override
    public void onEnable() {
        this.pick_d = ((String)this.anvilMode.getValue()).equalsIgnoreCase("Pick");
        this.blocksPlaced = 0;
        this.isHole = true;
        this.blockUp = false;
        this.hasMoved = false;
        this.firstRun = true;
        this.slot_mat = new int[]{-1, -1, -1, -1};
        to_place = new ArrayList();
        if (AutoAnvil.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        this.oldSlot = AutoAnvil.mc.field_71439_g.field_71071_by.field_70461_c;
    }

    @Override
    public void onDisable() {
        if (AutoAnvil.mc.field_71439_g == null) {
            return;
        }
        if (this.noMaterials) {
            this.setDisabledMessage("No Materials Detected... AutoAnvil turned OFF!");
        } else if (!this.isHole) {
            this.setDisabledMessage("Enemy is not in a hole... AutoAnvil turned OFF!");
        } else if (!this.enoughSpace) {
            this.setDisabledMessage("Not enough space... AutoAnvil turned OFF!");
        } else if (this.hasMoved) {
            this.setDisabledMessage("Enemy moved away from the hole... AutoAnvil turned OFF!");
        } else if (this.blockUp) {
            this.setDisabledMessage("Enemy head blocked.. AutoAnvil turned OFF!");
        }
        if (this.isSneaking) {
            AutoAnvil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)AutoAnvil.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        if (this.oldSlot != AutoAnvil.mc.field_71439_g.field_71071_by.field_70461_c && this.oldSlot != -1) {
            AutoAnvil.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
            this.oldSlot = -1;
        }
        this.noMaterials = false;
        this.firstRun = true;
        AutoCrystalRewrite.stopAC = false;
        if (this.slot_mat[0] == -2) {
            OffHand.removeItem(0);
        }
    }

    @Override
    public void onUpdate() {
        if (AutoAnvil.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        if (this.firstRun) {
            if (((String)this.target.getValue()).equals("Nearest")) {
                this.aimTarget = PlayerUtil.findClosestTarget((Double)this.enemyRange.getValue(), this.aimTarget);
            } else if (((String)this.target.getValue()).equals("Looking")) {
                this.aimTarget = PlayerUtil.findLookingPlayer((Double)this.enemyRange.getValue());
            }
            if (this.aimTarget == null) {
                return;
            }
            if (ModuleManager.isModuleEnabled(AutoGG.class)) {
                AutoGG.INSTANCE.addTargetedPlayer(this.aimTarget.func_70005_c_());
            }
            this.firstRun = false;
            if (this.getMaterialsSlot()) {
                if (this.is_in_hole()) {
                    this.enemyCoords = new double[]{this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v};
                    this.enoughSpace = this.createStructure();
                } else {
                    this.isHole = false;
                }
            } else {
                this.noMaterials = true;
            }
        } else {
            if (this.delayTimeTicks < (Integer)this.tickDelay.getValue()) {
                ++this.delayTimeTicks;
                return;
            }
            this.delayTimeTicks = 0;
            if ((int)this.enemyCoords[0] != (int)this.aimTarget.field_70165_t || (int)this.enemyCoords[2] != (int)this.aimTarget.field_70161_v) {
                this.hasMoved = true;
            }
            if (!(BlockUtil.getBlock(this.enemyCoords[0], this.enemyCoords[1] + 2.0, this.enemyCoords[2]) instanceof BlockAir) && !(BlockUtil.getBlock(this.enemyCoords[0], this.enemyCoords[1] + 2.0, this.enemyCoords[2]) instanceof BlockAnvil)) {
                this.blockUp = true;
            }
        }
        this.blocksPlaced = 0;
        if (this.noMaterials || !this.isHole || !this.enoughSpace || this.hasMoved || this.blockUp) {
            this.disable();
            return;
        }
        this.anvilsPositions = new ArrayList();
        for (Entity everyEntity : AutoAnvil.mc.field_71441_e.field_72996_f) {
            if (!(everyEntity instanceof EntityFallingBlock)) continue;
            this.anvilsPositions.add((int)everyEntity.field_70163_u);
            this.anvilsPositions.add((int)everyEntity.field_70163_u + 1);
        }
        this.noKick = 0;
        while (this.blocksPlaced <= (Integer)this.blocksPerTick.getValue()) {
            int maxSteps = to_place.size();
            if (this.offsetSteps >= maxSteps) {
                this.offsetSteps = 0;
                break;
            }
            BlockPos offsetPos = new BlockPos(to_place.get(this.offsetSteps));
            BlockPos targetPos = new BlockPos(this.enemyCoords[0], this.enemyCoords[1], this.enemyCoords[2]).func_177982_a(offsetPos.func_177958_n(), offsetPos.func_177956_o(), offsetPos.func_177952_p());
            boolean tryPlacing = true;
            if (this.offsetSteps > 0 && this.offsetSteps < to_place.size() - 1) {
                for (Entity entity : AutoAnvil.mc.field_71441_e.func_72839_b(null, new AxisAlignedBB(targetPos))) {
                    if (!(entity instanceof EntityPlayer)) continue;
                    tryPlacing = false;
                    break;
                }
            }
            if (tryPlacing && this.placeBlock(targetPos, this.offsetSteps)) {
                ++this.blocksPlaced;
            }
            ++this.offsetSteps;
            if (this.isSneaking) {
                AutoAnvil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)AutoAnvil.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
                this.isSneaking = false;
            }
            if (this.noKick != (Integer)this.failStop.getValue()) continue;
            break;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean placeBlock(BlockPos pos, int step) {
        EnumFacing prova;
        int utilSlot;
        Block block = AutoAnvil.mc.field_71441_e.func_180495_p(pos).func_177230_c();
        EnumFacing side = BlockUtil.getPlaceableSide(pos);
        if (step == to_place.size() - 1 && block instanceof BlockAnvil && side != null) {
            AutoAnvil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side));
            ++this.noKick;
        }
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
        Vec3d hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.5, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        Block neighbourBlock = AutoAnvil.mc.field_71441_e.func_180495_p(neighbour).func_177230_c();
        EnumHand handSwing = EnumHand.MAIN_HAND;
        int n = step == 0 && ((String)this.anvilMode.getValue()).equalsIgnoreCase("feet") ? 2 : (utilSlot = step >= to_place.size() - this.anvilBlock ? 1 : 0);
        if (step == 1 && this.anvilsPositions.contains(pos.field_177960_b)) {
            return false;
        }
        if (((Boolean)this.offHandObby.getValue()).booleanValue() && ModuleManager.isModuleEnabled(OffHand.class) && this.slot_mat[utilSlot] == -2) {
            if (!(AutoAnvil.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock) || !(((ItemBlock)AutoAnvil.mc.field_71439_g.func_184592_cb().func_77973_b()).func_179223_d() instanceof BlockObsidian)) return false;
            handSwing = EnumHand.OFF_HAND;
        } else {
            if (AutoAnvil.mc.field_71439_g.field_71071_by.func_70301_a(this.slot_mat[utilSlot]) == ItemStack.field_190927_a) return false;
            if (AutoAnvil.mc.field_71439_g.field_71071_by.field_70461_c != this.slot_mat[utilSlot]) {
                AutoAnvil.mc.field_71439_g.field_71071_by.field_70461_c = this.slot_mat[utilSlot];
            }
        }
        if (!this.isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            AutoAnvil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)AutoAnvil.mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        boolean stoppedAC = false;
        if (ModuleManager.isModuleEnabled(AutoCrystalRewrite.class)) {
            AutoCrystalRewrite.stopAC = true;
            stoppedAC = true;
        }
        if (((Boolean)this.rotate.getValue()).booleanValue()) {
            BlockUtil.faceVectorPacketInstant(hitVec, true);
        }
        int bef = AutoAnvil.mc.field_71467_ac;
        if (step == to_place.size() - 1) {
            EntityPlayer found = this.getPlayerFromName(this.aimTarget.field_146106_i.getName());
            if (found == null || (int)found.field_70165_t != (int)this.enemyCoords[0] || (int)found.field_70161_v != (int)this.enemyCoords[2]) {
                this.hasMoved = true;
                return false;
            }
            if (((Boolean)this.fastAnvil.getValue()).booleanValue()) {
                AutoAnvil.mc.field_71467_ac = 0;
            }
        }
        AutoAnvil.mc.field_71442_b.func_187099_a(AutoAnvil.mc.field_71439_g, AutoAnvil.mc.field_71441_e, neighbour, opposite, hitVec, handSwing);
        AutoAnvil.mc.field_71439_g.func_184609_a(handSwing);
        if (((Boolean)this.fastAnvil.getValue()).booleanValue() && step == to_place.size() - 1) {
            AutoAnvil.mc.field_71467_ac = bef;
        }
        if (stoppedAC) {
            AutoCrystalRewrite.stopAC = false;
            stoppedAC = false;
        }
        if (!this.pick_d || step != to_place.size() - 1 || (prova = BlockUtil.getPlaceableSide(new BlockPos(this.enemyCoords[0], this.enemyCoords[1], this.enemyCoords[2]))) == null) return true;
        AutoAnvil.mc.field_71439_g.field_71071_by.field_70461_c = this.slot_mat[3];
        AutoAnvil.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
        AutoAnvil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, new BlockPos(this.enemyCoords[0], this.enemyCoords[1], this.enemyCoords[2]), prova));
        AutoAnvil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(this.enemyCoords[0], this.enemyCoords[1], this.enemyCoords[2]), prova));
        return true;
    }

    private EntityPlayer getPlayerFromName(String name) {
        List playerList = AutoAnvil.mc.field_71441_e.field_73010_i;
        for (EntityPlayer entityPlayer : playerList) {
            if (!entityPlayer.field_146106_i.getName().equals(name)) continue;
            return entityPlayer;
        }
        return null;
    }

    private boolean getMaterialsSlot() {
        boolean feet = false;
        boolean pick = false;
        if (((String)this.anvilMode.getValue()).equalsIgnoreCase("Feet")) {
            feet = true;
        }
        if (((String)this.anvilMode.getValue()).equalsIgnoreCase("Pick")) {
            pick = true;
        }
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = AutoAnvil.mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack == ItemStack.field_190927_a) continue;
            if (pick && stack.func_77973_b() instanceof ItemPickaxe) {
                this.slot_mat[3] = i;
            }
            if (!(stack.func_77973_b() instanceof ItemBlock)) continue;
            Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
            if (block instanceof BlockObsidian) {
                this.slot_mat[0] = i;
                continue;
            }
            if (block instanceof BlockAnvil) {
                this.slot_mat[1] = i;
                continue;
            }
            if (!feet || !(block instanceof BlockPressurePlate) && !(block instanceof BlockButton)) continue;
            this.slot_mat[2] = i;
        }
        if (((Boolean)this.offHandObby.getValue()).booleanValue() && ModuleManager.isModuleEnabled(OffHand.class)) {
            this.slot_mat[0] = -2;
            OffHand.requestItems(0);
        }
        int count = 0;
        for (int val : this.slot_mat) {
            if (val == -1) continue;
            ++count;
        }
        return count - (feet || pick ? 1 : 0) == 2;
    }

    private boolean is_in_hole() {
        this.sur_block = new Double[][]{{this.aimTarget.field_70165_t + 1.0, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v}, {this.aimTarget.field_70165_t - 1.0, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v}, {this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v + 1.0}, {this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v - 1.0}};
        this.enemyCoords = new double[]{this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v};
        return HoleUtil.isHole(EntityUtil.getPosition((Entity)this.aimTarget), true, true).getType() != HoleUtil.HoleType.NONE;
    }

    private boolean createStructure() {
        int incr;
        if (((String)this.anvilMode.getValue()).equalsIgnoreCase("feet")) {
            to_place.add(new Vec3d(0.0, 0.0, 0.0));
        }
        to_place.add(new Vec3d(1.0, 1.0, 0.0));
        to_place.add(new Vec3d(-1.0, 1.0, 0.0));
        to_place.add(new Vec3d(0.0, 1.0, 1.0));
        to_place.add(new Vec3d(0.0, 1.0, -1.0));
        to_place.add(new Vec3d(1.0, 2.0, 0.0));
        to_place.add(new Vec3d(-1.0, 2.0, 0.0));
        to_place.add(new Vec3d(0.0, 2.0, 1.0));
        to_place.add(new Vec3d(0.0, 2.0, -1.0));
        int hDistanceMod = (Integer)this.hDistance.getValue();
        for (double distEnemy = (double)AutoAnvil.mc.field_71439_g.func_70032_d((Entity)this.aimTarget); distEnemy > (Double)this.decrease.getValue(); distEnemy -= ((Double)this.decrease.getValue()).doubleValue()) {
            --hDistanceMod;
        }
        int add = (int)(AutoAnvil.mc.field_71439_g.field_70163_u - this.aimTarget.field_70163_u);
        if (add > 1) {
            add = 2;
        }
        hDistanceMod = (int)((double)hDistanceMod + (AutoAnvil.mc.field_71439_g.field_70163_u - this.aimTarget.field_70163_u));
        double min_found = Double.MAX_VALUE;
        double[] coords_blocks_min = new double[]{-1.0, -1.0, -1.0};
        int cor = -1;
        int i = 0;
        for (Double[] cord_b : this.sur_block) {
            double d;
            double[] coords_blocks_temp = new double[]{cord_b[0], cord_b[1], cord_b[2]};
            double distance_now = AutoAnvil.mc.field_71439_g.func_174818_b(new BlockPos(cord_b[0].doubleValue(), cord_b[1].doubleValue(), cord_b[2].doubleValue()));
            if (d < min_found) {
                min_found = distance_now;
                cor = i;
            }
            ++i;
        }
        boolean possible = false;
        for (incr = 1; BlockUtil.getBlock(this.enemyCoords[0], this.enemyCoords[1] + (double)incr, this.enemyCoords[2]) instanceof BlockAir && incr < hDistanceMod; ++incr) {
            if (!((Boolean)this.antiCrystal.getValue()).booleanValue()) {
                to_place.add(new Vec3d((double)this.model[cor][0], (double)(this.model[cor][1] + incr), (double)this.model[cor][2]));
                continue;
            }
            for (int ij = 0; ij < 4; ++ij) {
                to_place.add(new Vec3d((double)this.model[ij][0], (double)(this.model[ij][1] + incr), (double)this.model[ij][2]));
            }
        }
        if (!(BlockUtil.getBlock(this.enemyCoords[0], this.enemyCoords[1] + (double)incr, this.enemyCoords[2]) instanceof BlockAir)) {
            --incr;
        }
        if (incr >= (Integer)this.minH.getValue()) {
            possible = true;
        }
        double yRef = AutoAnvil.to_place.get((int)(AutoAnvil.to_place.size() - 1)).field_72448_b;
        this.anvilBlock = 0;
        switch ((String)this.anvilPlace.getValue()) {
            case "Full": {
                to_place.add(new Vec3d(0.0, 3.0, 0.0));
                ++this.anvilBlock;
            }
            case "Double": {
                to_place.add(new Vec3d(0.0, 2.0, 0.0));
                ++this.anvilBlock;
            }
            case "Single": {
                to_place.add(new Vec3d(0.0, yRef, 0.0));
                ++this.anvilBlock;
            }
        }
        return possible;
    }
}

