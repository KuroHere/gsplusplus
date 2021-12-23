/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.SpoofRotationUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.combat.CrystalUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AntiCrystal;
import com.gamesense.client.module.modules.combat.OffHand;
import com.gamesense.client.module.modules.gui.ColorMain;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(name="Blocker", category=Category.Combat)
public class Blocker
extends Module {
    BooleanSetting rotate = this.registerBoolean("Rotate", true);
    BooleanSetting anvilBlocker = this.registerBoolean("Anvil", true);
    BooleanSetting offHandObby = this.registerBoolean("Off Hand Obby", true);
    BooleanSetting pistonBlocker = this.registerBoolean("Piston", true);
    BooleanSetting antiFacePlace = this.registerBoolean("Shift AntiFacePlace", true);
    IntegerSetting BlocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 10);
    ModeSetting blockPlaced = this.registerMode("Block Place", Arrays.asList("Pressure", "String"), "String");
    IntegerSetting tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
    private int delayTimeTicks = 0;
    private boolean noObby;
    private boolean noActive;
    private boolean activedBefore;

    @Override
    public void onEnable() {
        SpoofRotationUtil.ROTATION_UTIL.onEnable();
        PlacementUtil.onEnable();
        if (Blocker.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        if (!(((Boolean)this.anvilBlocker.getValue()).booleanValue() || ((Boolean)this.pistonBlocker.getValue()).booleanValue() || ((Boolean)this.antiFacePlace.getValue()).booleanValue())) {
            this.noActive = true;
            this.disable();
        }
        this.noObby = false;
    }

    @Override
    public void onDisable() {
        SpoofRotationUtil.ROTATION_UTIL.onDisable();
        PlacementUtil.onDisable();
        if (Blocker.mc.field_71439_g == null) {
            return;
        }
        if (this.noActive) {
            this.setDisabledMessage("Nothing is active... Blocker turned OFF!");
        } else if (this.noObby) {
            this.setDisabledMessage("Obsidian not found... Blocker turned OFF!");
        }
    }

    @Override
    public void onUpdate() {
        if (Blocker.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        if (this.noObby) {
            this.disable();
            return;
        }
        if (this.delayTimeTicks < (Integer)this.tickDelay.getValue()) {
            ++this.delayTimeTicks;
        } else {
            SpoofRotationUtil.ROTATION_UTIL.shouldSpoofAngles(true);
            this.delayTimeTicks = 0;
            if (((Boolean)this.anvilBlocker.getValue()).booleanValue()) {
                this.blockAnvil();
            }
            if (((Boolean)this.pistonBlocker.getValue()).booleanValue()) {
                this.blockPiston();
            }
            if (((Boolean)this.antiFacePlace.getValue()).booleanValue() && Blocker.mc.field_71474_y.field_74311_E.func_151468_f()) {
                this.antiFacePlace();
            }
        }
    }

    private void antiFacePlace() {
        int blocksPlaced = 0;
        for (Vec3d surround : new Vec3d[]{new Vec3d(1.0, 1.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(0.0, 1.0, -1.0)}) {
            BlockPos pos = new BlockPos(Blocker.mc.field_71439_g.field_70165_t + surround.field_72450_a, Blocker.mc.field_71439_g.field_70163_u, Blocker.mc.field_71439_g.field_70161_v + surround.field_72449_c);
            Block temp = BlockUtil.getBlock(pos);
            if (!(temp instanceof BlockObsidian) && temp != Blocks.field_150357_h) continue;
            if (blocksPlaced++ == 0) {
                AntiCrystal.getHotBarPressure((String)this.blockPlaced.getValue());
            }
            PlacementUtil.placeItem(new BlockPos((double)pos.func_177958_n(), (double)pos.func_177956_o() + surround.field_72448_b, (double)pos.func_177952_p()), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), Items.field_151007_F.getClass());
            if (blocksPlaced != (Integer)this.BlocksPerTick.getValue()) continue;
            return;
        }
    }

    private void blockAnvil() {
        boolean found = false;
        for (Entity t : Blocker.mc.field_71441_e.field_72996_f) {
            Block ex;
            if (!(t instanceof EntityFallingBlock) || !((ex = ((EntityFallingBlock)t).field_175132_d.func_177230_c()) instanceof BlockAnvil) || (int)t.field_70165_t != (int)Blocker.mc.field_71439_g.field_70165_t || (int)t.field_70161_v != (int)Blocker.mc.field_71439_g.field_70161_v || !(BlockUtil.getBlock(Blocker.mc.field_71439_g.field_70165_t, Blocker.mc.field_71439_g.field_70163_u + 2.0, Blocker.mc.field_71439_g.field_70161_v) instanceof BlockAir)) continue;
            this.placeBlock(new BlockPos(Blocker.mc.field_71439_g.field_70165_t, Blocker.mc.field_71439_g.field_70163_u + 2.0, Blocker.mc.field_71439_g.field_70161_v));
            MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getEnabledColor() + "AutoAnvil detected... Anvil Blocked!");
            found = true;
        }
        if (!found && this.activedBefore) {
            this.activedBefore = false;
            OffHand.removeItem(0);
        }
    }

    private void blockPiston() {
        for (Entity t : Blocker.mc.field_71441_e.field_72996_f) {
            if (!(t instanceof EntityEnderCrystal) || !(t.field_70165_t >= Blocker.mc.field_71439_g.field_70165_t - 1.5) || !(t.field_70165_t <= Blocker.mc.field_71439_g.field_70165_t + 1.5) || !(t.field_70161_v >= Blocker.mc.field_71439_g.field_70161_v - 1.5) || !(t.field_70161_v <= Blocker.mc.field_71439_g.field_70161_v + 1.5)) continue;
            for (int i = -2; i < 3; ++i) {
                for (int j = -2; j < 3; ++j) {
                    if (i != 0 && j != 0 || !(BlockUtil.getBlock(t.field_70165_t + (double)i, t.field_70163_u, t.field_70161_v + (double)j) instanceof BlockPistonBase)) continue;
                    this.breakCrystalPiston(t);
                    MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getEnabledColor() + "PistonCrystal detected... Destroyed crystal!");
                }
            }
        }
    }

    private void placeBlock(BlockPos pos) {
        EnumHand handSwing = EnumHand.MAIN_HAND;
        int obsidianSlot = InventoryUtil.findObsidianSlot((Boolean)this.offHandObby.getValue(), this.activedBefore);
        if (obsidianSlot == -1) {
            this.noObby = true;
            return;
        }
        if (obsidianSlot == 9) {
            this.activedBefore = true;
            if (Blocker.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock && ((ItemBlock)Blocker.mc.field_71439_g.func_184592_cb().func_77973_b()).func_179223_d() instanceof BlockObsidian) {
                handSwing = EnumHand.OFF_HAND;
            } else {
                return;
            }
        }
        if (Blocker.mc.field_71439_g.field_71071_by.field_70461_c != obsidianSlot && obsidianSlot != 9) {
            Blocker.mc.field_71439_g.field_71071_by.field_70461_c = obsidianSlot;
        }
        PlacementUtil.place(pos, handSwing, (boolean)((Boolean)this.rotate.getValue()), true);
    }

    private void breakCrystalPiston(Entity crystal) {
        if (((Boolean)this.rotate.getValue()).booleanValue()) {
            SpoofRotationUtil.ROTATION_UTIL.lookAtPacket(crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v, (EntityPlayer)Blocker.mc.field_71439_g);
        }
        CrystalUtil.breakCrystal(crystal);
        if (((Boolean)this.rotate.getValue()).booleanValue()) {
            SpoofRotationUtil.ROTATION_UTIL.resetRotation();
        }
    }
}

