/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.Timer;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.Offsets;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AutoCrystalRewrite;
import com.gamesense.client.module.modules.combat.OffHand;
import java.util.Arrays;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(name="AutoTrap", category=Category.Combat)
public class AutoTrap
extends Module {
    ModeSetting offsetMode = this.registerMode("Pattern", Arrays.asList("Normal", "No Step", "Simple", "Crystal"), "Normal");
    ModeSetting targetMode = this.registerMode("Target", Arrays.asList("Nearest", "Looking"), "Nearest");
    IntegerSetting enemyRange = this.registerInteger("Range", 4, 0, 6);
    IntegerSetting delayTicks = this.registerInteger("Tick Delay", 3, 0, 10);
    IntegerSetting blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 1, 8);
    BooleanSetting rotate = this.registerBoolean("Rotate", true);
    BooleanSetting sneakOnly = this.registerBoolean("Sneak Only", false);
    BooleanSetting disableNoBlock = this.registerBoolean("Disable No Obby", true);
    BooleanSetting offhandObby = this.registerBoolean("Offhand Obby", false);
    BooleanSetting silentSwitch = this.registerBoolean("Silent Switch", false);
    private final Timer delayTimer = new Timer();
    private EntityPlayer targetPlayer = null;
    private int oldSlot = -1;
    private int offsetSteps = 0;
    private boolean outOfTargetBlock = false;
    private boolean activedOff = false;
    int secureSilentSwitch = -1;
    boolean hasPlaced;

    @Override
    public void onEnable() {
        PlacementUtil.onEnable();
        if (AutoTrap.mc.field_71439_g == null || AutoTrap.mc.field_71441_e == null) {
            this.disable();
            return;
        }
        this.oldSlot = AutoTrap.mc.field_71439_g.field_71071_by.field_70461_c;
        this.secureSilentSwitch = -1;
    }

    @Override
    public void onDisable() {
        PlacementUtil.onDisable();
        if (AutoTrap.mc.field_71439_g == null | AutoTrap.mc.field_71441_e == null) {
            return;
        }
        if (this.outOfTargetBlock) {
            this.setDisabledMessage("No obsidian detected... AutoTrap turned OFF!");
        }
        if (this.oldSlot != AutoTrap.mc.field_71439_g.field_71071_by.field_70461_c && this.oldSlot != -1 && this.oldSlot != 9) {
            AutoTrap.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
            this.oldSlot = -1;
        }
        AutoCrystalRewrite.stopAC = false;
        if (((Boolean)this.offhandObby.getValue()).booleanValue() && ModuleManager.isModuleEnabled(OffHand.class)) {
            OffHand.removeItem(0);
            this.activedOff = false;
        }
        this.outOfTargetBlock = false;
        this.targetPlayer = null;
    }

    @Override
    public void onUpdate() {
        if (AutoTrap.mc.field_71439_g == null || AutoTrap.mc.field_71441_e == null) {
            this.disable();
            return;
        }
        if (((Boolean)this.sneakOnly.getValue()).booleanValue() && !AutoTrap.mc.field_71439_g.func_70093_af()) {
            return;
        }
        int targetBlockSlot = InventoryUtil.findCrystalBlockSlot((Boolean)this.offhandObby.getValue(), this.activedOff);
        if ((this.outOfTargetBlock || targetBlockSlot == -1) && ((Boolean)this.disableNoBlock.getValue()).booleanValue()) {
            this.outOfTargetBlock = true;
            this.disable();
            return;
        }
        this.activedOff = true;
        switch ((String)this.targetMode.getValue()) {
            case "Nearest": {
                this.targetPlayer = PlayerUtil.findClosestTarget(((Integer)this.enemyRange.getValue()).intValue(), this.targetPlayer);
                break;
            }
            case "Looking": {
                this.targetPlayer = PlayerUtil.findLookingPlayer(((Integer)this.enemyRange.getValue()).intValue());
                break;
            }
            default: {
                this.targetPlayer = null;
            }
        }
        if (this.targetPlayer == null) {
            return;
        }
        Vec3d targetVec3d = this.targetPlayer.func_174791_d();
        if (this.delayTimer.getTimePassed() / 50L >= (long)((Integer)this.delayTicks.getValue()).intValue()) {
            this.delayTimer.reset();
            int blocksPlaced = 0;
            this.hasPlaced = false;
            while (blocksPlaced <= (Integer)this.blocksPerTick.getValue()) {
                int maxSteps;
                Vec3d[] offsetPattern;
                switch ((String)this.offsetMode.getValue()) {
                    case "No Step": {
                        offsetPattern = Offsets.TRAP_STEP;
                        maxSteps = Offsets.TRAP_STEP.length;
                        break;
                    }
                    case "Simple": {
                        offsetPattern = Offsets.TRAP_SIMPLE;
                        maxSteps = Offsets.TRAP_SIMPLE.length;
                        break;
                    }
                    case "Crystal": {
                        offsetPattern = Offsets.TRAP_CRYSTAL;
                        maxSteps = Offsets.TRAP_CRYSTAL.length;
                        break;
                    }
                    default: {
                        offsetPattern = Offsets.TRAP_FULL;
                        maxSteps = Offsets.TRAP_FULL.length;
                    }
                }
                if (this.offsetSteps >= maxSteps) {
                    this.offsetSteps = 0;
                    break;
                }
                BlockPos offsetPos = new BlockPos(offsetPattern[this.offsetSteps]);
                BlockPos targetPos = new BlockPos(targetVec3d).func_177982_a(offsetPos.func_177958_n(), offsetPos.func_177956_o(), offsetPos.func_177952_p());
                boolean tryPlacing = true;
                if (this.targetPlayer.field_70163_u % 1.0 > 0.2) {
                    targetPos = new BlockPos(targetPos.func_177958_n(), targetPos.func_177956_o() + 1, targetPos.func_177952_p());
                }
                if (!AutoTrap.mc.field_71441_e.func_180495_p(targetPos).func_185904_a().func_76222_j()) {
                    tryPlacing = false;
                }
                for (Entity entity : AutoTrap.mc.field_71441_e.func_72839_b(null, new AxisAlignedBB(targetPos))) {
                    if (!(entity instanceof EntityPlayer)) continue;
                    tryPlacing = false;
                    break;
                }
                if (tryPlacing && this.placeBlock(targetPos)) {
                    ++blocksPlaced;
                }
                ++this.offsetSteps;
            }
            if (this.hasPlaced) {
                AutoTrap.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(this.oldSlot));
            }
        }
    }

    private boolean placeBlock(BlockPos pos) {
        EnumHand handSwing = EnumHand.MAIN_HAND;
        int targetBlockSlot = InventoryUtil.findCrystalBlockSlot((Boolean)this.offhandObby.getValue(), this.activedOff);
        if (targetBlockSlot == -1) {
            this.outOfTargetBlock = true;
            return false;
        }
        if (targetBlockSlot == 9) {
            this.activedOff = true;
            if (AutoTrap.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock && ((ItemBlock)AutoTrap.mc.field_71439_g.func_184592_cb().func_77973_b()).func_179223_d() instanceof BlockObsidian) {
                handSwing = EnumHand.OFF_HAND;
            } else {
                return false;
            }
        }
        if (AutoTrap.mc.field_71439_g.field_71071_by.field_70461_c != targetBlockSlot && targetBlockSlot != 9) {
            if (((Boolean)this.silentSwitch.getValue()).booleanValue()) {
                if (!this.hasPlaced) {
                    this.secureSilentSwitch = AutoTrap.mc.field_71439_g.field_71071_by.field_70461_c;
                    AutoTrap.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(targetBlockSlot));
                    this.hasPlaced = true;
                }
            } else {
                AutoTrap.mc.field_71439_g.field_71071_by.field_70461_c = targetBlockSlot;
            }
        }
        return PlacementUtil.place(pos, handSwing, (boolean)((Boolean)this.rotate.getValue()), (Boolean)this.silentSwitch.getValue() == false);
    }
}

