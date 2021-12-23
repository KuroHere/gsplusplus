/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.Phase;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.util.player.SpoofRotationUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Module.Declaration(name="FastShulker", category=Category.Misc)
public class FastShulker
extends Module {
    ModeSetting HowplaceBlock = this.registerMode("Place Block", Arrays.asList("Near", "Looking"), "Looking");
    IntegerSetting tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
    BooleanSetting rotate = this.registerBoolean("Rotate", false);
    BooleanSetting forceRotation = this.registerBoolean("ForceRotation", false);
    private int delayTimeTicks;
    BlockPos blockAim;
    private boolean looking;
    private boolean noSpace;
    private boolean materialsNeeded;
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
    int slot;
    boolean swapped = false;

    @Override
    public void onEnable() {
        SpoofRotationUtil.ROTATION_UTIL.onEnable();
        this.initValues();
    }

    private int getShulkerSlot() {
        for (int i = 0; i < FastShulker.mc.field_71439_g.field_71071_by.field_70462_a.size(); ++i) {
            if (!(FastShulker.mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() instanceof ItemBlock) || !(((ItemBlock)FastShulker.mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b()).func_179223_d() instanceof BlockShulkerBox)) continue;
            return i;
        }
        return -1;
    }

    private void initValues() {
        this.slot = this.getShulkerSlot();
        if (this.slot == -1) {
            this.materialsNeeded = false;
            return;
        }
        this.materialsNeeded = true;
        if (((String)this.HowplaceBlock.getValue()).equals("Looking")) {
            this.blockAim = FastShulker.mc.field_71476_x.func_178782_a();
            ++this.blockAim.field_177960_b;
            if (BlockUtil.getPlaceableSide(this.blockAim) == null) {
                this.looking = false;
                return;
            }
        } else {
            for (int[] sur : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}}) {
                for (int h : new int[]{1, 0}) {
                    if (!(BlockUtil.getBlock(FastShulker.mc.field_71439_g.field_70165_t + (double)sur[0], FastShulker.mc.field_71439_g.field_70163_u + (double)h, FastShulker.mc.field_71439_g.field_70161_v + (double)sur[1]) instanceof BlockAir) || BlockUtil.getPlaceableSide(new BlockPos(FastShulker.mc.field_71439_g.field_70165_t + (double)sur[0], FastShulker.mc.field_71439_g.field_70163_u + (double)h, FastShulker.mc.field_71439_g.field_70161_v + (double)sur[1])) == null || !(BlockUtil.getBlock(FastShulker.mc.field_71439_g.field_70165_t + (double)sur[0], FastShulker.mc.field_71439_g.field_70163_u + (double)h + 1.0, FastShulker.mc.field_71439_g.field_70161_v + (double)sur[1]) instanceof BlockAir) || PistonCrystal.someoneInCoords(FastShulker.mc.field_71439_g.field_70165_t + (double)sur[0], FastShulker.mc.field_71439_g.field_70163_u + (double)h, FastShulker.mc.field_71439_g.field_70161_v + (double)sur[1])) continue;
                    this.blockAim = new BlockPos(FastShulker.mc.field_71439_g.field_70165_t + (double)sur[0], FastShulker.mc.field_71439_g.field_70163_u + (double)h, FastShulker.mc.field_71439_g.field_70161_v + (double)sur[1]);
                    break;
                }
                if (this.blockAim != null) break;
            }
            if (this.blockAim == null) {
                this.noSpace = false;
                return;
            }
        }
        EnumFacing side = EnumFacing.func_190914_a((BlockPos)this.blockAim, (EntityLivingBase)FastShulker.mc.field_71439_g);
        BlockPos neighbour = this.blockAim.func_177972_a(side);
        EnumFacing opposite = side.func_176734_d();
        this.lastHitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.5, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
    }

    @Override
    public void onDisable() {
        String output = "";
        if (!this.materialsNeeded) {
            output = "No materials detected... Shulker not found";
        } else if (this.noSpace) {
            output = "Not enough space";
        } else if (this.looking) {
            output = "Impossible to place";
        }
        if (!output.equals("")) {
            PistonCrystal.printDebug(output, true);
        } else {
            PistonCrystal.printDebug("Shulker placed and opened", false);
        }
    }

    @Override
    public void onUpdate() {
        if (FastShulker.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        if (this.delayTimeTicks < (Integer)this.tickDelay.getValue()) {
            ++this.delayTimeTicks;
            return;
        }
        this.delayTimeTicks = 0;
        if (this.blockAim == null || !this.materialsNeeded || this.looking || this.noSpace) {
            this.disable();
            return;
        }
        if (this.slot > 9 && !this.swapped) {
            FastShulker.mc.field_71442_b.func_187098_a(0, 9, 0, ClickType.PICKUP, (EntityPlayer)FastShulker.mc.field_71439_g);
            FastShulker.mc.field_71442_b.func_187098_a(0, this.slot, 0, ClickType.PICKUP, (EntityPlayer)FastShulker.mc.field_71439_g);
            FastShulker.mc.field_71442_b.func_187098_a(0, 9, 0, ClickType.PICKUP, (EntityPlayer)FastShulker.mc.field_71439_g);
            this.swapped = true;
            if ((Integer)this.tickDelay.getValue() != 0) {
                return;
            }
        }
        if (BlockUtil.getBlock(this.blockAim) instanceof BlockAir) {
            if (this.slot > 9) {
                if (FastShulker.mc.field_71439_g.field_71071_by.field_70461_c != 9) {
                    FastShulker.mc.field_71439_g.field_71071_by.field_70461_c = 9;
                    FastShulker.mc.field_71442_b.func_78765_e();
                }
            } else if (FastShulker.mc.field_71439_g.field_71071_by.field_70461_c != this.slot) {
                FastShulker.mc.field_71439_g.field_71071_by.field_70461_c = this.slot;
            }
            PlacementUtil.place(this.blockAim, EnumHand.MAIN_HAND, (boolean)((Boolean)this.rotate.getValue()), true);
            if ((Integer)this.tickDelay.getValue() == 0) {
                this.openBlock();
            }
        } else {
            this.openBlock();
        }
    }

    private void openBlock() {
        EnumFacing side = EnumFacing.func_190914_a((BlockPos)this.blockAim, (EntityLivingBase)FastShulker.mc.field_71439_g);
        BlockPos neighbour = this.blockAim.func_177972_a(side);
        EnumFacing opposite = side.func_176734_d();
        Vec3d hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.5, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        FastShulker.mc.field_71442_b.func_187099_a(FastShulker.mc.field_71439_g, FastShulker.mc.field_71441_e, this.blockAim, opposite, hitVec, EnumHand.MAIN_HAND);
        this.disable();
    }
}

