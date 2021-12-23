/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.events.BlockChangeEvent;
import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.combat.CrystalUtil;
import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

@Module.Declaration(name="AutoCity", category=Category.Combat)
public class AutoCity
extends Module {
    DoubleSetting range = this.registerDouble("Range", 6.0, 0.0, 8.0);
    DoubleSetting minDamage = this.registerDouble("Min Damage", 5.0, 0.0, 10.0);
    DoubleSetting maxDamage = this.registerDouble("Max Self Damage", 7.0, 0.0, 20.0);
    ModeSetting target = this.registerMode("Target", Arrays.asList("Nearest", "Looking"), "Nearest");
    BooleanSetting onlyObby = this.registerBoolean("Only Obby", false);
    BooleanSetting switchPick = this.registerBoolean("Switch Pick", true);
    ModeSetting mineMode = this.registerMode("Mine Mode", Arrays.asList("Packet", "Vanilla"), "Packet");
    ModeSetting renderMode = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both", "None"), "Both");
    IntegerSetting width = this.registerInteger("Width", 1, 1, 10, () -> !((String)this.renderMode.getValue()).equals("None"));
    ColorSetting color = this.registerColor("Color", new GSColor(102, 51, 153), () -> !((String)this.renderMode.getValue()).equals("None"));
    BooleanSetting newPlace = this.registerBoolean("New Place", false);
    BooleanSetting disableAfter = this.registerBoolean("Disable After", true);
    private BlockPos blockMine;
    private BlockPos blockCrystal;
    private int oldSlot;
    private EntityPlayer aimTarget;
    private boolean isMining;
    private boolean packet;
    private boolean blockInside;
    private boolean finalY;
    private boolean noHole;
    private boolean noPossible;
    private boolean done;
    @EventHandler
    private final Listener<BlockChangeEvent> totemPopEventListener = new Listener<BlockChangeEvent>(event -> {
        if (AutoCity.mc.field_71439_g == null || AutoCity.mc.field_71441_e == null) {
            return;
        }
        if (event.getBlock() == null || event.getPosition() == null || this.blockMine == null) {
            return;
        }
        if (event.getPosition() == this.blockMine && event.getBlock() instanceof BlockAir) {
            if (!this.packet && this.oldSlot != -1) {
                AutoCity.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
            }
            this.done = true;
        }
    }, new Predicate[0]);

    @Override
    public void onEnable() {
        this.resetValues();
    }

    void resetValues() {
        this.aimTarget = null;
        this.blockCrystal = null;
        this.blockMine = null;
        this.done = false;
        this.noPossible = false;
        this.noHole = false;
        this.finalY = false;
        this.blockInside = false;
        this.packet = false;
        this.isMining = false;
    }

    @Override
    public void onDisable() {
        if (AutoCity.mc.field_71439_g == null) {
            return;
        }
        if (this.blockInside) {
            this.setDisabledMessage("Detected block inside... AutoCity turned OFF!");
        } else if (this.noHole) {
            this.setDisabledMessage("Enemy is not in a hole... AutoCity turned OFF!");
        } else if (this.finalY) {
            this.setDisabledMessage("Not correct y... AutoCity turned OFF!");
        } else if (this.noPossible) {
            this.setDisabledMessage("Enemy moved away from the hole... AutoCity turned OFF!");
        } else {
            this.setDisabledMessage("AutoCity turned OFF!");
        }
    }

    @Override
    public void onUpdate() {
        if (AutoCity.mc.field_71439_g == null || AutoCity.mc.field_71441_e == null) {
            return;
        }
        if (this.isMining) {
            if (BlockUtil.getBlock(this.blockMine) instanceof BlockAir) {
                this.resetValues();
                if (((Boolean)this.disableAfter.getValue()).booleanValue()) {
                    this.disable();
                }
                if (!this.packet && this.oldSlot != -1) {
                    AutoCity.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
                }
            } else if (this.done) {
                if (((Boolean)this.disableAfter.getValue()).booleanValue()) {
                    this.disable();
                }
            } else if (!this.packet) {
                this.breakBlock();
            }
            return;
        }
        if (((String)this.target.getValue()).equals("Nearest")) {
            this.aimTarget = PlayerUtil.findClosestTarget((Double)this.range.getValue(), this.aimTarget);
        } else if (((String)this.target.getValue()).equals("Looking")) {
            this.aimTarget = PlayerUtil.findLookingPlayer((Double)this.range.getValue());
        }
        if (this.aimTarget == null) {
            return;
        }
        boolean found = false;
        for (int[] positions : new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}}) {
            BlockPos blockPos = new BlockPos(this.aimTarget.field_70165_t + (double)positions[0], this.aimTarget.field_70163_u + (double)positions[1] + (this.aimTarget.field_70163_u % 1.0 > 0.2 ? 0.5 : 0.0), this.aimTarget.field_70161_v + (double)positions[2]);
            Block toCheck = BlockUtil.getBlock(blockPos);
            if (toCheck instanceof BlockAir || ((Boolean)this.onlyObby.getValue() != false ? !(toCheck instanceof BlockObsidian) : toCheck.field_149781_w > 6001.0f)) continue;
            AutoCity.mc.field_71441_e.func_175698_g(blockPos);
            for (Vec3i placement : new Vec3i[]{new Vec3i(1, -1, 0), new Vec3i(-1, -1, 0), new Vec3i(0, -1, 1), new Vec3i(0, -1, -1)}) {
                float damagePlayer;
                BlockPos temp = blockPos.func_177971_a(placement);
                if (!CrystalUtil.canPlaceCrystal(temp, (Boolean)this.newPlace.getValue()) || (double)DamageUtil.calculateDamage((double)temp.func_177958_n() + 0.5, (double)temp.func_177956_o() + 1.0, (double)temp.func_177952_p() + 0.5, (Entity)AutoCity.mc.field_71439_g, false) >= (Double)this.maxDamage.getValue() || (double)(damagePlayer = DamageUtil.calculateDamage((double)temp.func_177958_n() + 0.5, (double)temp.func_177956_o() + 1.0, (double)temp.func_177952_p() + 0.5, (Entity)this.aimTarget, false)) < (Double)this.minDamage.getValue()) continue;
                found = true;
                this.blockMine = blockPos;
                break;
            }
            AutoCity.mc.field_71441_e.func_175656_a(blockPos, toCheck.func_176223_P());
            if (found) break;
        }
        if (!found) {
            this.noPossible = true;
            if (((Boolean)this.disableAfter.getValue()).booleanValue()) {
                this.disable();
            }
            return;
        }
        if (AutoCity.mc.field_71439_g.func_184614_ca().func_77973_b() != Items.field_151046_w && ((Boolean)this.switchPick.getValue()).booleanValue()) {
            this.oldSlot = AutoCity.mc.field_71439_g.field_71071_by.field_70461_c;
            int slot = InventoryUtil.findFirstItemSlot(ItemPickaxe.class, 0, 9);
            if (slot != 1) {
                AutoCity.mc.field_71439_g.field_71071_by.field_70461_c = slot;
            }
        }
        switch ((String)this.mineMode.getValue()) {
            case "Packet": {
                AutoCity.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                AutoCity.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.blockMine, EnumFacing.UP));
                AutoCity.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.blockMine, EnumFacing.UP));
                this.isMining = true;
                this.packet = true;
                AutoCity.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
            }
            case "Vanilla": {
                this.breakBlock();
                this.isMining = true;
            }
        }
        this.breakBlock();
        this.isMining = true;
    }

    private void breakBlock() {
        AutoCity.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
        AutoCity.mc.field_71442_b.func_180512_c(this.blockMine, EnumFacing.UP);
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (this.blockMine == null) {
            return;
        }
        this.renderBox(this.blockMine);
    }

    private void renderBox(BlockPos blockPos) {
        GSColor gsColor1 = new GSColor(this.color.getValue(), 255);
        GSColor gsColor2 = new GSColor(this.color.getValue(), 50);
        switch ((String)this.renderMode.getValue()) {
            case "Both": {
                RenderUtil.drawBox(blockPos, 1.0, gsColor2, 63);
                RenderUtil.drawBoundingBox(blockPos, 1.0, ((Integer)this.width.getValue()).intValue(), gsColor1);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(blockPos, 1.0, ((Integer)this.width.getValue()).intValue(), gsColor1);
                break;
            }
            case "Fill": {
                RenderUtil.drawBox(blockPos, 1.0, gsColor2, 63);
                break;
            }
        }
    }
}

