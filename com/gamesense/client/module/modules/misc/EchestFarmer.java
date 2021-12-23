/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.Phase;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.util.player.SpoofRotationUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.OffHand;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Module.Declaration(name="EchestFarmer", category=Category.Misc)
public class EchestFarmer
extends Module {
    ModeSetting breakBlock = this.registerMode("Break Block", Arrays.asList("Normal", "Packet"), "Packet");
    ModeSetting HowplaceBlock = this.registerMode("Place Block", Arrays.asList("Near", "Looking"), "Looking");
    IntegerSetting stackCount = this.registerInteger("N^Stack", 0, 0, 64);
    IntegerSetting tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
    BooleanSetting offHandEchest = this.registerBoolean("OffHand echest", false);
    BooleanSetting rotate = this.registerBoolean("Rotate", false);
    BooleanSetting forceRotation = this.registerBoolean("ForceRotation", false);
    private int delayTimeTicks;
    private int echestToMine;
    private int slotObby;
    private int slotPick;
    BlockPos blockAim;
    private boolean looking;
    private boolean noSpace;
    private boolean materialsNeeded;
    private boolean prevBreak;
    private ArrayList<EnumFacing> sides = new ArrayList();
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
    }

    private void initValues() {
        this.looking = false;
        this.noSpace = false;
        this.prevBreak = false;
        this.delayTimeTicks = 0;
        this.materialsNeeded = true;
        int obbyCount = EchestFarmer.mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> itemStack.func_77973_b() instanceof ItemBlock && ((ItemBlock)itemStack.func_77973_b()).func_179223_d() == Blocks.field_150343_Z).mapToInt(ItemStack::func_190916_E).sum();
        int stackWanted = (Integer)this.stackCount.getValue() == 0 ? -1 : (Integer)this.stackCount.getValue() * 64;
        this.echestToMine = (stackWanted - obbyCount) / 8;
        if (((String)this.HowplaceBlock.getValue()).equals("Looking")) {
            try {
                this.blockAim = EchestFarmer.mc.field_71476_x.func_178782_a();
                ++this.blockAim.field_177960_b;
            }
            catch (NullPointerException e) {
                this.disable();
                return;
            }
            if (BlockUtil.getPlaceableSide(this.blockAim) == null) {
                this.looking = false;
                return;
            }
            this.sides.clear();
            this.sides.add(EnumFacing.func_190914_a((BlockPos)this.blockAim, (EntityLivingBase)EchestFarmer.mc.field_71439_g));
        } else {
            for (int[] sur : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}}) {
                for (int h : new int[]{1, 0}) {
                    if (!(BlockUtil.getBlock(EchestFarmer.mc.field_71439_g.field_70165_t + (double)sur[0], EchestFarmer.mc.field_71439_g.field_70163_u + (double)h, EchestFarmer.mc.field_71439_g.field_70161_v + (double)sur[1]) instanceof BlockAir) || BlockUtil.getPlaceableSide(new BlockPos(EchestFarmer.mc.field_71439_g.field_70165_t + (double)sur[0], EchestFarmer.mc.field_71439_g.field_70163_u + (double)h, EchestFarmer.mc.field_71439_g.field_70161_v + (double)sur[1])) == null || PistonCrystal.someoneInCoords(EchestFarmer.mc.field_71439_g.field_70165_t + (double)sur[0], EchestFarmer.mc.field_71439_g.field_70163_u + (double)h, EchestFarmer.mc.field_71439_g.field_70161_v + (double)sur[1])) continue;
                    this.blockAim = new BlockPos(EchestFarmer.mc.field_71439_g.field_70165_t + (double)sur[0], EchestFarmer.mc.field_71439_g.field_70163_u + (double)h, EchestFarmer.mc.field_71439_g.field_70161_v + (double)sur[1]);
                    break;
                }
                if (this.blockAim != null) break;
            }
            if (this.blockAim == null) {
                this.noSpace = false;
                return;
            }
        }
        if (this.isToggleMsg()) {
            if ((Integer)this.stackCount.getValue() == 0) {
                PistonCrystal.printDebug("Starting farming obby", false);
            } else {
                PistonCrystal.printDebug(String.format("N^obby: %d, N^stack: %d, echest needed: %d", obbyCount, stackWanted, this.echestToMine), false);
            }
        }
        this.slotPick = InventoryUtil.findFirstItemSlot(Items.field_151046_w.getClass(), 0, 9);
        if (((Boolean)this.offHandEchest.getValue()).booleanValue()) {
            this.slotObby = 11;
            OffHand.requestItems(2);
            EchestFarmer.mc.field_71439_g.field_71071_by.field_70461_c = this.slotPick;
            EchestFarmer.mc.field_71442_b.func_78765_e();
        } else {
            this.slotObby = InventoryUtil.findFirstBlockSlot(Blocks.field_150477_bB.getClass(), 0, 9);
        }
        if (this.slotObby == -1 || this.slotPick == -1) {
            this.materialsNeeded = false;
        }
    }

    @Override
    public void onDisable() {
        String output = "";
        if (!this.materialsNeeded) {
            output = "No materials detected... " + (this.slotObby == -1 ? "No Echest detected " : "") + (this.slotPick == -1 ? "No Pick detected" : "");
        } else if (this.noSpace) {
            output = "Not enough space";
        } else if (this.looking) {
            output = "Impossible to place";
        }
        if (!output.equals("")) {
            PistonCrystal.printDebug(output, true);
        } else if (this.echestToMine == 0) {
            PistonCrystal.printDebug("Mined every echest", false);
        }
        if (((Boolean)this.offHandEchest.getValue()).booleanValue()) {
            OffHand.removeItem(2);
        }
    }

    @Override
    public void onUpdate() {
        if (EchestFarmer.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        if (this.delayTimeTicks < (Integer)this.tickDelay.getValue()) {
            ++this.delayTimeTicks;
            return;
        }
        this.delayTimeTicks = 0;
        if (this.blockAim == null || !this.materialsNeeded || this.slotPick == -1 || this.looking || this.noSpace) {
            this.disable();
            return;
        }
        if (BlockUtil.getBlock(this.blockAim) instanceof BlockAir) {
            if (this.prevBreak && --this.echestToMine == 0) {
                this.disable();
                return;
            }
            this.placeBlock(this.blockAim);
            this.prevBreak = false;
        } else {
            EnumFacing sideBreak;
            if (EchestFarmer.mc.field_71439_g.field_71071_by.field_70461_c != this.slotPick) {
                EchestFarmer.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(this.slotPick));
                EchestFarmer.mc.field_71439_g.field_71071_by.field_70461_c = this.slotPick;
                EchestFarmer.mc.field_71442_b.func_78765_e();
            }
            if ((sideBreak = BlockUtil.getPlaceableSide(this.blockAim)) != null) {
                switch ((String)this.breakBlock.getValue()) {
                    case "Packet": {
                        if (this.prevBreak) break;
                        EchestFarmer.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                        EchestFarmer.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.blockAim, sideBreak));
                        EchestFarmer.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.blockAim, sideBreak));
                        this.prevBreak = true;
                        break;
                    }
                    case "Normal": {
                        EchestFarmer.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                        EchestFarmer.mc.field_71442_b.func_180512_c(this.blockAim, sideBreak);
                        this.prevBreak = true;
                    }
                }
            }
        }
    }

    private void placeBlock(BlockPos pos) {
        EnumHand handSwing;
        if (this.slotObby == 11) {
            handSwing = EnumHand.OFF_HAND;
        } else {
            handSwing = EnumHand.MAIN_HAND;
            if (EchestFarmer.mc.field_71439_g.field_71071_by.field_70461_c != this.slotObby) {
                EchestFarmer.mc.field_71439_g.field_71071_by.field_70461_c = this.slotObby;
                EchestFarmer.mc.field_71442_b.func_78765_e();
            }
        }
        if (EchestFarmer.mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBlock && ((ItemBlock)EchestFarmer.mc.field_71439_g.func_184614_ca().func_77973_b()).func_179223_d() != Blocks.field_150477_bB || EchestFarmer.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock && ((ItemBlock)EchestFarmer.mc.field_71439_g.func_184592_cb().func_77973_b()).func_179223_d() != Blocks.field_150477_bB) {
            return;
        }
        if (((Boolean)this.forceRotation.getValue()).booleanValue()) {
            EnumFacing side = BlockUtil.getPlaceableSide(this.blockAim);
            if (side == null) {
                return;
            }
            BlockPos neighbour = this.blockAim.func_177972_a(side);
            EnumFacing opposite = side.func_176734_d();
            if (!BlockUtil.canBeClicked(neighbour)) {
                return;
            }
            this.lastHitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.5, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        }
        PlacementUtil.place(pos, handSwing, (boolean)((Boolean)this.rotate.getValue()), true);
    }
}

