/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Arrays;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Module.Declaration(name="Foot Walker", category=Category.Combat, priority=101)
public class FootWalker
extends Module {
    boolean finishOnGround;
    boolean materials;
    boolean center;
    boolean havePhase;
    boolean beforeShiftJump;
    BooleanSetting allowEchest = this.registerBoolean("Allow Echest", true);
    BooleanSetting onlyEchest = this.registerBoolean("Only Echest", false, () -> (Boolean)this.allowEchest.getValue());
    BooleanSetting allowAnvil = this.registerBoolean("Allow Anvil", true);
    BooleanSetting onlyAnvil = this.registerBoolean("Only Anvil", false, () -> (Boolean)this.allowAnvil.getValue());
    BooleanSetting instaActive = this.registerBoolean("Insta Active", true);
    BooleanSetting disactiveAfter = this.registerBoolean("Insta disactive", true);
    BooleanSetting alwaysActive = this.registerBoolean("Always Active", false);
    BooleanSetting onShift = this.registerBoolean("On Shift", true);
    BooleanSetting preRotate = this.registerBoolean("Pre Rotate", false);
    ModeSetting jumpMode = this.registerMode("Jump Mode", Arrays.asList("Konas", "Future"), "Konas");
    BooleanSetting doubleRubberband = this.registerBoolean("Double Rubberband", false);
    DoubleSetting addDouble = this.registerDouble("Add Double", 1.1, 0.0, 2.0, () -> (Boolean)this.doubleRubberband.getValue());
    BooleanSetting beforeRubberband = this.registerBoolean("Before Rubberband", false);
    ModeSetting rubberbandMode = this.registerMode("Rubberband Mode", Arrays.asList("+Y", "-Y", "Add Y", "Free Y"), "+Y");
    IntegerSetting ym = this.registerInteger("Y-", -4, -64, 0, () -> ((String)this.rubberbandMode.getValue()).equals("-Y"));
    IntegerSetting yp = this.registerInteger("Y+", 128, 0, 200, () -> ((String)this.rubberbandMode.getValue()).equals("+Y"));
    IntegerSetting minY = this.registerInteger("Min Y", 9, 0, 30, () -> ((String)this.rubberbandMode.getValue()).equals("Free Y"));
    IntegerSetting yStart = this.registerInteger("Min Y Start", -9, 0, -20, () -> ((String)this.rubberbandMode.getValue()).equals("Free Y"));
    IntegerSetting maxStartY = this.registerInteger("Max Start Y", 8, 5, 20, () -> ((String)this.rubberbandMode.getValue()).equals("Free Y"));
    IntegerSetting maxFinishY = this.registerInteger("Max Finish Y", 15, 10, 40, () -> ((String)this.rubberbandMode.getValue()).equals("Free Y"));
    IntegerSetting addY = this.registerInteger("Rub Add Y", 10, -40, 40, () -> ((String)this.rubberbandMode.getValue()).equals("Add Y"));
    BooleanSetting onPlayer = this.registerBoolean("On Player", false);
    DoubleSetting rangePlayer = this.registerDouble("Range Player", 3.0, 0.0, 4.0, () -> (Boolean)this.onPlayer.getValue());
    BooleanSetting phase = this.registerBoolean("Phase", false);
    BooleanSetting predictPhase = this.registerBoolean("Predict Phase", false, () -> (Boolean)this.phase.getValue());
    ModeSetting phaseRubberband = this.registerMode("Phase Rubberband", Arrays.asList("Y+", "Y-", "Y0", "AddY", "X", "Z", "XZ"), "Y+", () -> (Boolean)this.phase.getValue());
    IntegerSetting phaseAddY = this.registerInteger("Phase Add Y", 40, -40, 40, () -> ((String)this.phaseRubberband.getValue()).equals("AddY") && (Boolean)this.phase.getValue() != false);
    BooleanSetting scaffold = this.registerBoolean("Scaffold", false);
    BooleanSetting shiftJump = this.registerBoolean("Shift Jump", false);
    BooleanSetting safeMode = this.registerBoolean("Safe Mode", false);
    BooleanSetting normalSwitch = this.registerBoolean("Normal Switch", false);
    BooleanSetting switchBack = this.registerBoolean("Switch Back", false, () -> (Boolean)this.normalSwitch.getValue());
    IntegerSetting tickSwitchBack = this.registerInteger("Tick SwitchBack", 4, 0, 10, () -> (Boolean)this.normalSwitch.getValue() != false && (Boolean)this.switchBack.getValue() != false);
    int tick;
    int oldSlotBack;

    @Override
    public void onEnable() {
        this.initValues();
        if (((Boolean)this.instaActive.getValue()).booleanValue()) {
            this.instaBurrow((Boolean)this.disactiveAfter.getValue());
        }
    }

    void initValues() {
        this.beforeShiftJump = false;
        this.havePhase = false;
        this.center = false;
        this.finishOnGround = false;
        this.materials = true;
        this.tick = -1;
        this.oldSlotBack = -1;
    }

    @Override
    public void onUpdate() {
        if (this.tick != -1 && this.tick++ >= (Integer)this.tickSwitchBack.getValue()) {
            FootWalker.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlotBack;
            this.tick = -1;
            this.oldSlotBack = -1;
        }
        if (this.havePhase) {
            this.doPhase();
            this.havePhase = false;
            if (((Boolean)this.disactiveAfter.getValue()).booleanValue()) {
                this.disable();
            }
        }
        if (((Boolean)this.onShift.getValue()).booleanValue() && FootWalker.mc.field_71439_g.func_70093_af() || ((Boolean)this.alwaysActive.getValue()).booleanValue() || this.finishOnGround || ((Boolean)this.onPlayer.getValue()).booleanValue() && PlayerUtil.findClosestTarget((Double)this.rangePlayer.getValue(), null) != null) {
            this.instaBurrow((Boolean)this.disactiveAfter.getValue());
        }
        if (((Boolean)this.shiftJump.getValue()).booleanValue() && FootWalker.mc.field_71474_y.field_74314_A.func_151470_d() && FootWalker.mc.field_71474_y.field_74311_E.func_151470_d() && !this.beforeShiftJump) {
            FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(FootWalker.mc.field_71439_g.field_70165_t, Math.floor(FootWalker.mc.field_71439_g.field_70163_u) + 1.0, FootWalker.mc.field_71439_g.field_70161_v, FootWalker.mc.field_71439_g.field_70177_z, FootWalker.mc.field_71439_g.field_70125_A, FootWalker.mc.field_71439_g.field_70122_E));
            FootWalker.mc.field_71439_g.func_70107_b(FootWalker.mc.field_71439_g.field_70165_t, Math.floor(FootWalker.mc.field_71439_g.field_70163_u) + 1.0, FootWalker.mc.field_71439_g.field_70161_v);
            KeyBinding.func_74510_a((int)FootWalker.mc.field_71474_y.field_74314_A.func_151463_i(), (boolean)false);
        }
        if (this.center) {
            PlayerUtil.centerPlayer(BlockUtil.getCenterOfBlock(FootWalker.mc.field_71439_g.field_70165_t, FootWalker.mc.field_71439_g.field_70163_u, FootWalker.mc.field_71439_g.field_70161_v));
            this.center = false;
        }
    }

    @Override
    public void onDisable() {
        if (this.materials) {
            this.setDisabledMessage("No materials found... FootConcrete disabled");
        }
    }

    void instaBurrow(boolean disactive) {
        if (FootWalker.mc.field_71439_g.field_70122_E) {
            int slotBlock;
            int n = (Boolean)this.onlyEchest.getValue() != false ? -1 : (slotBlock = (Boolean)this.onlyAnvil.getValue() != false ? -1 : InventoryUtil.findObsidianSlot(false, false));
            if (slotBlock == -1) {
                if (((Boolean)this.allowEchest.getValue()).booleanValue()) {
                    slotBlock = InventoryUtil.findFirstBlockSlot(Blocks.field_150477_bB.getClass(), 0, 8);
                }
                if (((Boolean)this.allowAnvil.getValue()).booleanValue()) {
                    slotBlock = InventoryUtil.findFirstBlockSlot(Blocks.field_150467_bQ.getClass(), 0, 8);
                }
            }
            if (slotBlock == -1) {
                this.materials = false;
                this.disable();
                return;
            }
            double posY = FootWalker.mc.field_71439_g.field_70163_u % 1.0 >= 0.5 ? (double)Math.round(FootWalker.mc.field_71439_g.field_70163_u) : FootWalker.mc.field_71439_g.field_70163_u;
            BlockPos pos = new BlockPos(FootWalker.mc.field_71439_g.field_70165_t, posY, FootWalker.mc.field_71439_g.field_70161_v);
            if (!FootWalker.mc.field_71441_e.func_180495_p(pos).func_185904_a().func_76222_j() || FootWalker.mc.field_71441_e.func_72839_b(null, new AxisAlignedBB(pos)).stream().anyMatch(entity -> entity instanceof EntityPlayer && entity != FootWalker.mc.field_71439_g)) {
                if (!((Boolean)this.alwaysActive.getValue()).booleanValue()) {
                    this.disable();
                }
                return;
            }
            boolean scaf = false;
            if (BlockUtil.getBlock(pos.func_177982_a(0, -1, 0)) instanceof BlockAir) {
                if (((Boolean)this.scaffold.getValue()).booleanValue()) {
                    scaf = true;
                } else {
                    if (!((Boolean)this.alwaysActive.getValue()).booleanValue()) {
                        this.disable();
                    }
                    return;
                }
            }
            if (!(BlockUtil.getBlock(pos.func_177982_a(0, 2, 0)) instanceof BlockAir)) {
                return;
            }
            if (((Boolean)this.preRotate.getValue()).booleanValue()) {
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(0.0f, 90.0f, true));
            }
            int y = Integer.MIN_VALUE;
            if (((String)this.rubberbandMode.getValue()).equals("Free Y")) {
                boolean air;
                int i;
                boolean bottom = BlockUtil.getBlock(FootWalker.mc.field_71439_g.field_70165_t, FootWalker.mc.field_71439_g.field_70163_u - (double)((Integer)this.minY.getValue()).intValue(), FootWalker.mc.field_71439_g.field_70161_v) instanceof BlockAir;
                for (i = (Integer)this.minY.getValue() - 1; i > -1; --i) {
                    air = BlockUtil.getBlock(FootWalker.mc.field_71439_g.field_70165_t, FootWalker.mc.field_71439_g.field_70163_u - (double)i, FootWalker.mc.field_71439_g.field_70161_v) instanceof BlockAir;
                    if (FootWalker.mc.field_71439_g.field_70163_u - (double)i < (double)((Integer)this.yStart.getValue()).intValue()) continue;
                    if (bottom && air) {
                        y = -i;
                        break;
                    }
                    bottom = air;
                }
                if (y == Integer.MIN_VALUE) {
                    bottom = BlockUtil.getBlock(FootWalker.mc.field_71439_g.field_70165_t, FootWalker.mc.field_71439_g.field_70163_u + (double)((Integer)this.maxStartY.getValue()).intValue(), FootWalker.mc.field_71439_g.field_70161_v) instanceof BlockAir;
                    for (i = (Integer)this.maxStartY.getValue() + 1; i < (Integer)this.maxFinishY.getValue(); ++i) {
                        air = BlockUtil.getBlock(FootWalker.mc.field_71439_g.field_70165_t, FootWalker.mc.field_71439_g.field_70163_u + (double)y, FootWalker.mc.field_71439_g.field_70161_v) instanceof BlockAir;
                        if (bottom && air) {
                            y = i;
                            break;
                        }
                        bottom = air;
                    }
                    if (y == Integer.MIN_VALUE) {
                        return;
                    }
                }
            }
            double posX = FootWalker.mc.field_71439_g.field_70165_t;
            double posZ = FootWalker.mc.field_71439_g.field_70161_v;
            Vec3d newPos = BlockUtil.getCenterOfBlock(posX, FootWalker.mc.field_71439_g.field_70163_u, posZ);
            int oldSlot = FootWalker.mc.field_71439_g.field_71071_by.field_70461_c;
            if (!FootWalker.mc.field_71441_e.func_184144_a((Entity)FootWalker.mc.field_71439_g, FootWalker.mc.field_71439_g.func_174813_aQ()).isEmpty() || ((Boolean)this.safeMode.getValue()).booleanValue()) {
                double d;
                double distance = FootWalker.mc.field_71439_g.func_70092_e(newPos.field_72450_a, FootWalker.mc.field_71439_g.field_70163_u, newPos.field_72449_c);
                if (d > 0.1) {
                    if (scaf) {
                        if (slotBlock != oldSlot) {
                            FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slotBlock));
                            if (((Boolean)this.normalSwitch.getValue()).booleanValue()) {
                                FootWalker.mc.field_71439_g.field_71071_by.field_70461_c = slotBlock;
                            }
                        }
                        this.placeBlockPacket(null, pos.func_177982_a(0, -1, 0));
                    }
                    this.finishOnGround = true;
                    FootWalker.mc.field_71439_g.field_70159_w = 0.0;
                    FootWalker.mc.field_71439_g.field_70179_y = 0.0;
                    double newX = posX + (newPos.field_72450_a - posX) / 2.0;
                    double newZ = posZ + (newPos.field_72449_c - posZ) / 2.0;
                    FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(newX, FootWalker.mc.field_71439_g.field_70163_u, newZ, true));
                    FootWalker.mc.field_71439_g.func_70107_b(newX, FootWalker.mc.field_71439_g.field_70163_u, newZ);
                    if (slotBlock != oldSlot) {
                        if (((Boolean)this.normalSwitch.getValue()).booleanValue()) {
                            if (((Boolean)this.switchBack.getValue()).booleanValue()) {
                                this.oldSlotBack = oldSlot;
                                this.tick = 0;
                            }
                        } else {
                            FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(FootWalker.mc.field_71439_g.field_71071_by.field_70461_c));
                        }
                    }
                    return;
                }
                if (((Boolean)this.safeMode.getValue()).booleanValue() && distance > 0.05) {
                    if (scaf) {
                        if (slotBlock != oldSlot) {
                            if (((Boolean)this.normalSwitch.getValue()).booleanValue()) {
                                FootWalker.mc.field_71439_g.field_71071_by.field_70461_c = slotBlock;
                            }
                            FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slotBlock));
                        }
                        this.placeBlockPacket(null, pos.func_177982_a(0, -1, 0));
                    }
                    this.center = true;
                    if (slotBlock != oldSlot) {
                        if (((Boolean)this.normalSwitch.getValue()).booleanValue()) {
                            if (((Boolean)this.switchBack.getValue()).booleanValue()) {
                                this.oldSlotBack = oldSlot;
                                this.tick = 0;
                            }
                        } else {
                            FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(FootWalker.mc.field_71439_g.field_71071_by.field_70461_c));
                        }
                    }
                    return;
                }
            }
            posX = newPos.field_72450_a;
            posZ = newPos.field_72449_c;
            boolean isSneaking = false;
            if (BlockUtil.canBeClicked(pos.func_177982_a(0, -1, 0)) && !FootWalker.mc.field_71439_g.func_70093_af()) {
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)FootWalker.mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
                isSneaking = true;
            }
            if (slotBlock != oldSlot) {
                if (((Boolean)this.normalSwitch.getValue()).booleanValue()) {
                    FootWalker.mc.field_71439_g.field_71071_by.field_70461_c = slotBlock;
                }
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slotBlock));
            }
            if (scaf) {
                this.placeBlockPacket(null, pos.func_177982_a(0, -1, 0));
            }
            this.jump((String)this.jumpMode.getValue(), posX, posZ);
            this.placeBlockPacket(EnumFacing.DOWN, pos);
            double newY = -4.0;
            switch ((String)this.rubberbandMode.getValue()) {
                case "+Y": {
                    newY = ((Integer)this.yp.getValue()).intValue();
                    break;
                }
                case "-Y": {
                    newY = ((Integer)this.ym.getValue()).intValue();
                    break;
                }
                case "Add Y": {
                    newY = posY + (double)((Integer)this.addY.getValue()).intValue();
                    break;
                }
                case "Free Y": {
                    newY = posY + (double)y;
                }
            }
            if (((Boolean)this.beforeRubberband.getValue()).booleanValue()) {
                this.rubberband(posX, newY, posZ);
            }
            if (slotBlock != oldSlot) {
                if (((Boolean)this.normalSwitch.getValue()).booleanValue()) {
                    if (((Boolean)this.switchBack.getValue()).booleanValue()) {
                        this.oldSlotBack = oldSlot;
                        this.tick = 0;
                    }
                } else {
                    FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(oldSlot));
                }
            }
            if (isSneaking) {
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)FootWalker.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
            }
            if (!((Boolean)this.beforeRubberband.getValue()).booleanValue()) {
                this.rubberband(posX, newY, posZ);
            }
            if (((Boolean)this.phase.getValue()).booleanValue()) {
                if (((Boolean)this.predictPhase.getValue()).booleanValue()) {
                    this.doPhase();
                }
                this.havePhase = true;
            }
        } else {
            this.finishOnGround = true;
        }
        if (disactive && !((Boolean)this.phase.getValue()).booleanValue()) {
            this.disable();
        }
    }

    void rubberband(double posX, double newY, double posZ) {
        FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(posX, newY, posZ, true));
        if (((Boolean)this.doubleRubberband.getValue()).booleanValue()) {
            FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(posX, newY + (Double)this.addDouble.getValue(), posZ, true));
        }
    }

    void jump(String mode, double posX, double posZ) {
        switch (mode) {
            case "Konas": {
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(posX, FootWalker.mc.field_71439_g.field_70163_u + 0.42, posZ, true));
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(posX, FootWalker.mc.field_71439_g.field_70163_u + 0.75, posZ, true));
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(posX, FootWalker.mc.field_71439_g.field_70163_u + 1.01, posZ, true));
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(posX, FootWalker.mc.field_71439_g.field_70163_u + 1.16, posZ, true));
                break;
            }
            case "Future": {
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(posX, FootWalker.mc.field_71439_g.field_70163_u + 0.42, posZ, true));
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(posX, FootWalker.mc.field_71439_g.field_70163_u + 0.75, posZ, true));
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(posX, FootWalker.mc.field_71439_g.field_70163_u + 0.9, posZ, true));
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(posX, FootWalker.mc.field_71439_g.field_70163_u + 1.17, posZ, true));
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(posX, FootWalker.mc.field_71439_g.field_70163_u + 1.17, posZ, true));
            }
        }
    }

    boolean placeBlockPacket(EnumFacing side, BlockPos pos) {
        if (side == null) {
            side = BlockUtil.getPlaceableSide(pos);
        }
        if (side == null) {
            return false;
        }
        BlockPos neighbour = pos.func_177972_a(side);
        EnumFacing opposite = side.func_176734_d();
        Vec3d vec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.5, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        float f = (float)(vec.field_72450_a - (double)pos.func_177958_n());
        float f1 = (float)(vec.field_72448_b - (double)pos.func_177956_o());
        float f2 = (float)(vec.field_72449_c - (double)pos.func_177952_p());
        FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(neighbour, opposite, EnumHand.MAIN_HAND, f, f1, f2));
        FootWalker.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
        return true;
    }

    void doPhase() {
        FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(FootWalker.mc.field_71439_g.field_70165_t, FootWalker.mc.field_71439_g.field_70163_u - 0.03125, FootWalker.mc.field_71439_g.field_70161_v, FootWalker.mc.field_71439_g.field_70177_z, FootWalker.mc.field_71439_g.field_70125_A, FootWalker.mc.field_71439_g.field_70122_E));
        switch ((String)this.phaseRubberband.getValue()) {
            case "Y+": {
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(FootWalker.mc.field_71439_g.field_70165_t, 1000.0, FootWalker.mc.field_71439_g.field_70161_v, FootWalker.mc.field_71439_g.field_70177_z, FootWalker.mc.field_71439_g.field_70125_A, FootWalker.mc.field_71439_g.field_70122_E));
                break;
            }
            case "Y-": {
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(FootWalker.mc.field_71439_g.field_70165_t, -1000.0, FootWalker.mc.field_71439_g.field_70161_v, FootWalker.mc.field_71439_g.field_70177_z, FootWalker.mc.field_71439_g.field_70125_A, FootWalker.mc.field_71439_g.field_70122_E));
                break;
            }
            case "Y0": {
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(FootWalker.mc.field_71439_g.field_70165_t, 0.0, FootWalker.mc.field_71439_g.field_70161_v, FootWalker.mc.field_71439_g.field_70177_z, FootWalker.mc.field_71439_g.field_70125_A, FootWalker.mc.field_71439_g.field_70122_E));
                break;
            }
            case "AddY": {
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(FootWalker.mc.field_71439_g.field_70165_t, FootWalker.mc.field_71439_g.field_70163_u + (double)((Integer)this.phaseAddY.getValue()).intValue(), FootWalker.mc.field_71439_g.field_70161_v, FootWalker.mc.field_71439_g.field_70177_z, FootWalker.mc.field_71439_g.field_70125_A, FootWalker.mc.field_71439_g.field_70122_E));
                break;
            }
            case "X": {
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(FootWalker.mc.field_71439_g.field_70165_t + 75.0, FootWalker.mc.field_71439_g.field_70163_u, FootWalker.mc.field_71439_g.field_70161_v, FootWalker.mc.field_71439_g.field_70177_z, FootWalker.mc.field_71439_g.field_70125_A, FootWalker.mc.field_71439_g.field_70122_E));
                break;
            }
            case "Z": {
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(FootWalker.mc.field_71439_g.field_70165_t, FootWalker.mc.field_71439_g.field_70163_u, FootWalker.mc.field_71439_g.field_70161_v + 75.0, FootWalker.mc.field_71439_g.field_70177_z, FootWalker.mc.field_71439_g.field_70125_A, FootWalker.mc.field_71439_g.field_70122_E));
                break;
            }
            case "XZ": {
                FootWalker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(FootWalker.mc.field_71439_g.field_70165_t + 75.0, FootWalker.mc.field_71439_g.field_70163_u, FootWalker.mc.field_71439_g.field_70161_v + 75.0, FootWalker.mc.field_71439_g.field_70177_z, FootWalker.mc.field_71439_g.field_70125_A, FootWalker.mc.field_71439_g.field_70122_E));
            }
        }
    }
}

