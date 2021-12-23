/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.Timer;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.AutoGG;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBed;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Module.Declaration(name="BedAura", category=Category.Combat)
public class BedAura
extends Module {
    ModeSetting attackMode = this.registerMode("Mode", Arrays.asList("Normal", "Own"), "Own");
    DoubleSetting attackRange = this.registerDouble("Attack Range", 4.0, 0.0, 10.0);
    IntegerSetting breakDelay = this.registerInteger("Break Delay", 1, 0, 20);
    IntegerSetting placeDelay = this.registerInteger("Place Delay", 1, 0, 20);
    DoubleSetting targetRange = this.registerDouble("Target Range", 7.0, 0.0, 16.0);
    BooleanSetting rotate = this.registerBoolean("Rotate", true);
    BooleanSetting disableNone = this.registerBoolean("Disable No Bed", false);
    BooleanSetting autoSwitch = this.registerBoolean("Switch", true);
    BooleanSetting silent = this.registerBoolean("Silent Switch", false, () -> (Boolean)this.autoSwitch.getValue());
    BooleanSetting antiSuicide = this.registerBoolean("Anti Suicide", false);
    IntegerSetting antiSuicideHealth = this.registerInteger("Suicide Health", 14, 1, 36);
    IntegerSetting minDamage = this.registerInteger("Min Damage", 5, 1, 36);
    private boolean hasNone = false;
    private int oldSlot = -1;
    private final ArrayList<BlockPos> placedPos = new ArrayList();
    private final Timer breakTimer = new Timer();
    private final Timer placeTimer = new Timer();

    @Override
    public void onEnable() {
        this.hasNone = false;
        this.placedPos.clear();
        if (BedAura.mc.field_71439_g == null || BedAura.mc.field_71441_e == null) {
            this.disable();
            return;
        }
        int bedSlot = InventoryUtil.findFirstItemSlot(ItemBed.class, 0, 8);
        if (BedAura.mc.field_71439_g.field_71071_by.field_70461_c != bedSlot && bedSlot != -1 && ((Boolean)this.autoSwitch.getValue()).booleanValue()) {
            this.oldSlot = BedAura.mc.field_71439_g.field_71071_by.field_70461_c;
            if (!((Boolean)this.silent.getValue()).booleanValue()) {
                BedAura.mc.field_71439_g.field_71071_by.field_70461_c = bedSlot;
            } else {
                BedAura.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(bedSlot));
            }
        } else if (bedSlot == -1) {
            this.hasNone = true;
        }
    }

    @Override
    public void onDisable() {
        this.placedPos.clear();
        if (BedAura.mc.field_71439_g == null || BedAura.mc.field_71441_e == null) {
            return;
        }
        if (((Boolean)this.autoSwitch.getValue()).booleanValue() && BedAura.mc.field_71439_g.field_71071_by.field_70461_c != this.oldSlot && this.oldSlot != -1) {
            if (!((Boolean)this.silent.getValue()).booleanValue()) {
                BedAura.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
            } else {
                BedAura.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(this.oldSlot));
            }
        }
        if (this.hasNone && ((Boolean)this.disableNone.getValue()).booleanValue()) {
            this.setDisabledMessage("No beds detected... BedAura turned OFF!");
        }
        this.hasNone = false;
        this.oldSlot = -1;
    }

    @Override
    public void onUpdate() {
        if (BedAura.mc.field_71439_g == null || BedAura.mc.field_71441_e == null || BedAura.mc.field_71439_g.field_71093_bK == 0) {
            this.disable();
            return;
        }
        int bedSlot = InventoryUtil.findFirstItemSlot(ItemBed.class, 0, 8);
        if (BedAura.mc.field_71439_g.field_71071_by.field_70461_c != bedSlot && bedSlot != -1 && ((Boolean)this.autoSwitch.getValue()).booleanValue()) {
            this.oldSlot = BedAura.mc.field_71439_g.field_71071_by.field_70461_c;
            if (!((Boolean)this.silent.getValue()).booleanValue()) {
                BedAura.mc.field_71439_g.field_71071_by.field_70461_c = bedSlot;
            } else {
                BedAura.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(bedSlot));
            }
        } else if (bedSlot == -1) {
            this.hasNone = true;
        }
        if (((Boolean)this.antiSuicide.getValue()).booleanValue() && BedAura.mc.field_71439_g.func_110143_aJ() + BedAura.mc.field_71439_g.func_110139_bj() < (float)((Integer)this.antiSuicideHealth.getValue()).intValue()) {
            return;
        }
        if (this.breakTimer.getTimePassed() / 50L >= (long)((Integer)this.breakDelay.getValue()).intValue()) {
            this.breakTimer.reset();
            this.breakBed();
        }
        if (this.hasNone) {
            if (((Boolean)this.disableNone.getValue()).booleanValue()) {
                this.disable();
                return;
            }
            return;
        }
        if (BedAura.mc.field_71439_g.field_71071_by.func_70301_a(BedAura.mc.field_71439_g.field_71071_by.field_70461_c).func_77973_b() != Items.field_151104_aV) {
            return;
        }
        if (this.placeTimer.getTimePassed() / 50L >= (long)((Integer)this.placeDelay.getValue()).intValue()) {
            this.placeTimer.reset();
            this.placeBed();
        }
    }

    private void breakBed() {
        for (TileEntity tileEntity : this.findBedEntities((EntityPlayer)BedAura.mc.field_71439_g)) {
            if (!(tileEntity instanceof TileEntityBed)) continue;
            if (((Boolean)this.rotate.getValue()).booleanValue()) {
                BlockUtil.faceVectorPacketInstant(new Vec3d((double)tileEntity.func_174877_v().func_177958_n(), (double)tileEntity.func_174877_v().func_177956_o(), (double)tileEntity.func_174877_v().func_177952_p()), true);
            }
            BedAura.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(tileEntity.func_174877_v(), EnumFacing.UP, EnumHand.OFF_HAND, 0.0f, 0.0f, 0.0f));
            return;
        }
    }

    private void placeBed() {
        for (EntityPlayer entityPlayer : this.findTargetEntities((EntityPlayer)BedAura.mc.field_71439_g)) {
            NonNullList<BlockPos> targetPos;
            if (entityPlayer.field_70128_L || (targetPos = this.findTargetPlacePos(entityPlayer)).size() < 1) continue;
            for (BlockPos blockPos : targetPos) {
                BlockPos targetPos1 = blockPos.func_177984_a();
                if (targetPos1.func_185332_f((int)BedAura.mc.field_71439_g.field_70165_t, (int)BedAura.mc.field_71439_g.field_70163_u, (int)BedAura.mc.field_71439_g.field_70161_v) > (Double)this.attackRange.getValue() || BedAura.mc.field_71441_e.func_180495_p(targetPos1).func_177230_c() != Blocks.field_150350_a || DamageUtil.calculateDamage(targetPos1.func_177958_n(), targetPos1.func_177956_o(), targetPos1.func_177952_p(), (Entity)entityPlayer, false) < (float)((Integer)this.minDamage.getValue()).intValue()) continue;
                if (ModuleManager.isModuleEnabled(AutoGG.class)) {
                    AutoGG.INSTANCE.addTargetedPlayer(entityPlayer.func_70005_c_());
                }
                if (BedAura.mc.field_71441_e.func_180495_p(targetPos1.func_177974_f()).func_177230_c() == Blocks.field_150350_a) {
                    this.placeBedFinal(targetPos1, 90, EnumFacing.DOWN);
                    return;
                }
                if (BedAura.mc.field_71441_e.func_180495_p(targetPos1.func_177976_e()).func_177230_c() == Blocks.field_150350_a) {
                    this.placeBedFinal(targetPos1, -90, EnumFacing.DOWN);
                    return;
                }
                if (BedAura.mc.field_71441_e.func_180495_p(targetPos1.func_177978_c()).func_177230_c() == Blocks.field_150350_a) {
                    this.placeBedFinal(targetPos1, 0, EnumFacing.DOWN);
                    return;
                }
                if (BedAura.mc.field_71441_e.func_180495_p(targetPos1.func_177968_d()).func_177230_c() != Blocks.field_150350_a) continue;
                this.placeBedFinal(targetPos1, 180, EnumFacing.SOUTH);
                return;
            }
        }
    }

    private NonNullList<TileEntity> findBedEntities(EntityPlayer entityPlayer) {
        NonNullList bedEntities = NonNullList.func_191196_a();
        BedAura.mc.field_71441_e.field_147482_g.stream().filter(tileEntity -> tileEntity instanceof TileEntityBed).filter(tileEntity -> tileEntity.func_145835_a(entityPlayer.field_70165_t, entityPlayer.field_70163_u, entityPlayer.field_70161_v) <= (Double)this.attackRange.getValue() * (Double)this.attackRange.getValue()).filter(this::isOwn).forEach(arg_0 -> bedEntities.add(arg_0));
        bedEntities.sort(Comparator.comparing(tileEntity -> tileEntity.func_145835_a(entityPlayer.field_70165_t, entityPlayer.field_70163_u, entityPlayer.field_70161_v)));
        return bedEntities;
    }

    private boolean isOwn(TileEntity tileEntity) {
        if (((String)this.attackMode.getValue()).equalsIgnoreCase("Normal")) {
            return true;
        }
        if (((String)this.attackMode.getValue()).equalsIgnoreCase("Own")) {
            for (BlockPos blockPos : this.placedPos) {
                if (!(blockPos.func_185332_f(tileEntity.func_174877_v().func_177958_n(), tileEntity.func_174877_v().func_177956_o(), tileEntity.func_174877_v().func_177952_p()) <= 3.0)) continue;
                return true;
            }
        }
        return false;
    }

    private NonNullList<EntityPlayer> findTargetEntities(EntityPlayer entityPlayer) {
        NonNullList targetEntities = NonNullList.func_191196_a();
        BedAura.mc.field_71441_e.field_73010_i.stream().filter(entityPlayer1 -> !EntityUtil.basicChecksEntity((Entity)entityPlayer1)).filter(entityPlayer1 -> (double)entityPlayer1.func_70032_d((Entity)entityPlayer) <= (Double)this.targetRange.getValue()).sorted(Comparator.comparing(entityPlayer1 -> Float.valueOf(entityPlayer1.func_70032_d((Entity)entityPlayer)))).forEach(arg_0 -> targetEntities.add(arg_0));
        return targetEntities;
    }

    private NonNullList<BlockPos> findTargetPlacePos(EntityPlayer entityPlayer) {
        NonNullList targetPlacePos = NonNullList.func_191196_a();
        targetPlacePos.addAll((Collection)EntityUtil.getSphere(BedAura.mc.field_71439_g.func_180425_c(), ((Double)this.attackRange.getValue()).floatValue(), ((Double)this.attackRange.getValue()).intValue(), false, true, 0).stream().filter(this::canPlaceBed).sorted(Comparator.comparing(blockPos -> Float.valueOf(1.0f - DamageUtil.calculateDamage(blockPos.func_177984_a().func_177958_n(), blockPos.func_177984_a().func_177956_o(), blockPos.func_177984_a().func_177952_p(), (Entity)entityPlayer, false)))).collect(Collectors.toList()));
        return targetPlacePos;
    }

    private boolean canPlaceBed(BlockPos blockPos) {
        if (BedAura.mc.field_71441_e.func_180495_p(blockPos.func_177984_a()).func_177230_c() != Blocks.field_150350_a) {
            return false;
        }
        if (BedAura.mc.field_71441_e.func_180495_p(blockPos).func_177230_c() == Blocks.field_150350_a) {
            return false;
        }
        return BedAura.mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(blockPos)).isEmpty();
    }

    private void placeBedFinal(BlockPos blockPos, int direction, EnumFacing enumFacing) {
        BedAura.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation((float)direction, 0.0f, BedAura.mc.field_71439_g.field_70122_E));
        if (BedAura.mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150350_a) {
            return;
        }
        BlockPos neighbourPos = blockPos.func_177972_a(enumFacing);
        EnumFacing oppositeFacing = enumFacing.func_176734_d();
        Vec3d vec3d = new Vec3d((Vec3i)neighbourPos).func_72441_c(0.5, 0.5, 0.5).func_178787_e(new Vec3d(oppositeFacing.func_176730_m()).func_186678_a(0.5));
        if (((Boolean)this.rotate.getValue()).booleanValue()) {
            BlockUtil.faceVectorPacketInstant(vec3d, true);
        }
        BedAura.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)BedAura.mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
        BedAura.mc.field_71442_b.func_187099_a(BedAura.mc.field_71439_g, BedAura.mc.field_71441_e, neighbourPos, oppositeFacing, vec3d, EnumHand.MAIN_HAND);
        BedAura.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
        BedAura.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)BedAura.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
        this.placedPos.add(blockPos);
    }
}

