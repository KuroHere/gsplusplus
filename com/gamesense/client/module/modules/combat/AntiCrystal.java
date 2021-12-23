/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AutoCrystalRewrite;
import com.gamesense.client.module.modules.combat.OffHand;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Module.Declaration(name="AntiCrystal", category=Category.Combat)
public class AntiCrystal
extends Module {
    BooleanSetting targetSection = this.registerBoolean("Target Section", true);
    DoubleSetting rangePlace = this.registerDouble("Range Place", 5.9, 0.0, 6.0, () -> (Boolean)this.targetSection.getValue());
    DoubleSetting enemyRange = this.registerDouble("Enemy Range", 12.0, 0.0, 20.0, () -> (Boolean)this.targetSection.getValue());
    BooleanSetting onlyIfEnemy = this.registerBoolean("Only If Enemy", true, () -> (Boolean)this.targetSection.getValue());
    BooleanSetting damageSection = this.registerBoolean("Damage Section", true);
    DoubleSetting damageMin = this.registerDouble("Damage Min", 4.0, 0.0, 15.0, () -> (Boolean)this.damageSection.getValue());
    DoubleSetting biasDamage = this.registerDouble("Bias Damage", 1.0, 0.0, 3.0, () -> (Boolean)this.damageSection.getValue());
    BooleanSetting checkDamage = this.registerBoolean("Damage Check", true, () -> (Boolean)this.damageSection.getValue());
    BooleanSetting notOurCrystals = this.registerBoolean("Ignore AutoCrystal", true, () -> (Boolean)this.damageSection.getValue());
    BooleanSetting delaySection = this.registerBoolean("Delay Section", true);
    ModeSetting blockPlaced = this.registerMode("Block Place", Arrays.asList("Pressure", "String"), "String", () -> (Boolean)this.delaySection.getValue());
    IntegerSetting tickDelay = this.registerInteger("Tick Delay", 5, 0, 10, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 8, () -> (Boolean)this.delaySection.getValue());
    BooleanSetting miscSection = this.registerBoolean("Misc Section", true);
    BooleanSetting offHandMode = this.registerBoolean("OffHand Mode", true, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting rotate = this.registerBoolean("Rotate", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting nonAbusive = this.registerBoolean("Non Abusive", true, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting switchBack = this.registerBoolean("Switch Back", true, () -> (Boolean)this.miscSection.getValue());
    private int delayTimeTicks;
    private boolean isSneaking = false;

    @Override
    public void onEnable() {
        this.delayTimeTicks = 0;
    }

    @Override
    public void onDisable() {
        if (this.isSneaking) {
            AntiCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)AntiCrystal.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
    }

    @Override
    public void onUpdate() {
        if (this.delayTimeTicks < (Integer)this.tickDelay.getValue()) {
            ++this.delayTimeTicks;
            return;
        }
        this.delayTimeTicks = 0;
        if (((Boolean)this.onlyIfEnemy.getValue()).booleanValue()) {
            if (AntiCrystal.mc.field_71441_e.field_73010_i.size() > 1) {
                boolean found = false;
                for (EntityPlayer check : AntiCrystal.mc.field_71441_e.field_73010_i) {
                    if (check == AntiCrystal.mc.field_71439_g || !((double)AntiCrystal.mc.field_71439_g.func_70032_d((Entity)check) <= (Double)this.enemyRange.getValue())) continue;
                    found = true;
                    break;
                }
                if (!found) {
                    return;
                }
            } else {
                return;
            }
        }
        int blocksPlaced = 0;
        boolean pressureSwitch = true;
        int slotPressure = -1;
        for (Entity t : AntiCrystal.mc.field_71441_e.field_72996_f) {
            float damage;
            if (!(t instanceof EntityEnderCrystal) || !((double)AntiCrystal.mc.field_71439_g.func_70032_d(t) <= (Double)this.rangePlace.getValue())) continue;
            if (pressureSwitch) {
                if (((Boolean)this.offHandMode.getValue()).booleanValue() && AntiCrystal.isOffHandPressure((String)this.blockPlaced.getValue())) {
                    slotPressure = 9;
                } else {
                    slotPressure = AntiCrystal.getHotBarPressure((String)this.blockPlaced.getValue());
                    if (slotPressure == -1) {
                        return;
                    }
                }
                pressureSwitch = false;
            }
            if (!((Boolean)this.notOurCrystals.getValue()).booleanValue() && this.usCrystal(t)) {
                return;
            }
            if (((Boolean)this.checkDamage.getValue()).booleanValue() && (double)(damage = (float)((double)DamageUtil.calculateDamage(t.field_70165_t, t.field_70163_u, t.field_70161_v, (Entity)AntiCrystal.mc.field_71439_g, false) * (Double)this.biasDamage.getValue())) < (Double)this.damageMin.getValue() && damage < AntiCrystal.mc.field_71439_g.func_110143_aJ()) {
                return;
            }
            if (BlockUtil.getBlock(t.field_70165_t, t.field_70163_u, t.field_70161_v) instanceof BlockAir) {
                this.placeBlock(new BlockPos(t.field_70165_t, t.field_70163_u, t.field_70161_v), slotPressure);
                if (++blocksPlaced == (Integer)this.blocksPerTick.getValue()) {
                    return;
                }
            }
            if (!this.isSneaking) continue;
            AntiCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)AntiCrystal.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
    }

    public boolean usCrystal(Entity crystal) {
        AutoCrystalRewrite autoCrystal = ModuleManager.getModule(AutoCrystalRewrite.class);
        return false;
    }

    public static boolean isOffHandPressure(String itemMode) {
        OffHand offHand = ModuleManager.getModule(OffHand.class);
        return ((String)offHand.nonDefaultItem.getValue()).equals(itemMode) || ((String)offHand.defaultItem.getValue()).equals(itemMode);
    }

    private void placeBlock(BlockPos pos, int slotPressure) {
        int oldSlot = -1;
        EnumFacing side = EnumFacing.DOWN;
        BlockPos neighbour = pos.func_177972_a(side);
        EnumFacing opposite = side.func_176734_d();
        Vec3d hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.5, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        Block neighbourBlock = AntiCrystal.mc.field_71441_e.func_180495_p(neighbour).func_177230_c();
        if (slotPressure != 9 && AntiCrystal.mc.field_71439_g.field_71071_by.field_70461_c != slotPressure) {
            if (!((Boolean)this.nonAbusive.getValue()).booleanValue()) {
                if (((Boolean)this.switchBack.getValue()).booleanValue()) {
                    oldSlot = AntiCrystal.mc.field_71439_g.field_71071_by.field_70461_c;
                }
                AntiCrystal.mc.field_71439_g.field_71071_by.field_70461_c = slotPressure;
            } else {
                return;
            }
        }
        if (!this.isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            AntiCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)AntiCrystal.mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
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
        EnumHand swingHand = EnumHand.MAIN_HAND;
        if (slotPressure == 9) {
            swingHand = EnumHand.OFF_HAND;
            if (!AntiCrystal.isPressure(AntiCrystal.mc.field_71439_g.func_184592_cb())) {
                return;
            }
        } else if (((String)this.blockPlaced.getValue()).equals("Pressure") ? !AntiCrystal.isPressure(AntiCrystal.mc.field_71439_g.func_184614_ca()) : !AntiCrystal.isString(AntiCrystal.mc.field_71439_g.func_184614_ca())) {
            return;
        }
        AntiCrystal.mc.field_71442_b.func_187099_a(AntiCrystal.mc.field_71439_g, AntiCrystal.mc.field_71441_e, neighbour, opposite, hitVec, swingHand);
        AntiCrystal.mc.field_71439_g.func_184609_a(swingHand);
        if (((Boolean)this.switchBack.getValue()).booleanValue() && oldSlot != -1) {
            AntiCrystal.mc.field_71439_g.field_71071_by.field_70461_c = oldSlot;
        }
        if (stoppedAC) {
            AutoCrystalRewrite.stopAC = false;
            stoppedAC = false;
        }
    }

    public static boolean isPressure(ItemStack stack) {
        if (stack == ItemStack.field_190927_a || !(stack.func_77973_b() instanceof ItemBlock)) {
            return false;
        }
        return ((ItemBlock)stack.func_77973_b()).func_179223_d() instanceof BlockPressurePlate;
    }

    public static boolean isString(ItemStack stack) {
        if (stack == ItemStack.field_190927_a || stack.func_77973_b() instanceof ItemBlock) {
            return false;
        }
        return stack.func_77973_b() == Items.field_151007_F;
    }

    public static int getHotBarPressure(String mode) {
        for (int i = 0; i < 9; ++i) {
            if (!(mode.equals("Pressure") ? AntiCrystal.isPressure(AntiCrystal.mc.field_71439_g.field_71071_by.func_70301_a(i)) : AntiCrystal.isString(AntiCrystal.mc.field_71439_g.field_71071_by.func_70301_a(i)))) continue;
            return i;
        }
        return -1;
    }
}

