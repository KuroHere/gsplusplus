/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.Phase;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.util.player.SpoofRotationUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.OffHand;
import java.util.ArrayList;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockSkull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSkull;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Module.Declaration(name="AutoSkull", category=Category.Combat)
public class AutoSkull
extends Module {
    BooleanSetting placementSection = this.registerBoolean("Placement Section", true);
    BooleanSetting offHandSkull = this.registerBoolean("OffHand Skull", false, () -> (Boolean)this.placementSection.getValue());
    DoubleSetting playerDistance = this.registerDouble("Player Distance", 0.0, 0.0, 6.0, () -> (Boolean)this.placementSection.getValue());
    IntegerSetting BlocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 10, () -> (Boolean)this.placementSection.getValue());
    BooleanSetting autoTrap = this.registerBoolean("AutoTrap", false, () -> (Boolean)this.placementSection.getValue());
    BooleanSetting noUp = this.registerBoolean("No Up", false, () -> (Boolean)this.placementSection.getValue());
    BooleanSetting onlyHoles = this.registerBoolean("Only Holes", false, () -> (Boolean)this.placementSection.getValue());
    BooleanSetting centerPlayer = this.registerBoolean("Center Player", false, () -> (Boolean)this.placementSection.getValue());
    BooleanSetting delaySection = this.registerBoolean("Delay Section", true);
    BooleanSetting onShift = this.registerBoolean("On Shift", false, () -> (Boolean)this.delaySection.getValue());
    BooleanSetting instaActive = this.registerBoolean("Insta Active", true, () -> (Boolean)this.delaySection.getValue());
    BooleanSetting disableAfter = this.registerBoolean("Disable After", true, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting tickDelay = this.registerInteger("Tick Delay", 5, 0, 10, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting preSwitch = this.registerInteger("Pre Switch", 0, 0, 20, () -> (Boolean)this.delaySection.getValue());
    IntegerSetting afterSwitch = this.registerInteger("After Switch", 0, 0, 20, () -> (Boolean)this.delaySection.getValue());
    BooleanSetting rotationSection = this.registerBoolean("Rotation Section", true);
    BooleanSetting forceRotation = this.registerBoolean("Force Rotation", false, () -> (Boolean)this.rotationSection.getValue());
    BooleanSetting rotate = this.registerBoolean("Rotate", true, () -> (Boolean)this.rotationSection.getValue());
    BooleanSetting phaseSection = this.registerBoolean("Phase Section", true);
    BooleanSetting phase = this.registerBoolean("Phase", true, () -> (Boolean)this.phaseSection.getValue());
    BooleanSetting ServerRespond = this.registerBoolean("Server Respond", true, () -> (Boolean)this.phaseSection.getValue());
    BooleanSetting predictPhase = this.registerBoolean("Predict Phase", true, () -> (Boolean)this.phaseSection.getValue());
    IntegerSetting maxTickTries = this.registerInteger("Max Tick Try", 100, 1, 200, () -> (Boolean)this.phaseSection.getValue());
    private static final Vec3d[] AIR = new Vec3d[]{new Vec3d(-1.0, -1.0, -1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(-1.0, 1.0, -1.0), new Vec3d(-1.0, 2.0, -1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(1.0, 2.0, 1.0), new Vec3d(0.0, 2.0, 1.0)};
    private int delayTimeTicks = 0;
    private boolean noObby;
    private boolean activedBefore;
    private int oldSlot;
    private Vec3d lastHitVec = new Vec3d(-1.0, -1.0, -1.0);
    private int preRotationTick;
    private int afterRotationTick;
    private int stage;
    private boolean toPhase;
    private boolean alrPlaced;
    private int tickTry;
    private Vec3d centeredBlock = Vec3d.field_186680_a;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
        if (event.getPhase() != Phase.PRE || !((Boolean)this.rotate.getValue()).booleanValue() || this.lastHitVec == null || !((Boolean)this.forceRotation.getValue()).booleanValue()) {
            return;
        }
        Vec2f rotation = RotationUtil.getRotationTo(this.lastHitVec);
        PlayerPacket packet = new PlayerPacket((Module)this, rotation);
        PlayerPacketManager.INSTANCE.addPacket(packet);
    }, new Predicate[0]);
    private boolean firstShift;
    private int resetPhase;
    private final ArrayList<EnumFacing> exd = new ArrayList<EnumFacing>(){
        {
            this.add(EnumFacing.DOWN);
            this.add(EnumFacing.UP);
        }
    };

    @Override
    public void onEnable() {
        SpoofRotationUtil.ROTATION_UTIL.onEnable();
        PlacementUtil.onEnable();
        if (AutoSkull.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        this.toPhase = false;
        this.activedBefore = false;
        this.alrPlaced = false;
        this.firstShift = false;
        this.noObby = false;
        this.lastHitVec = null;
        this.tickTry = 0;
        this.resetPhase = 0;
        this.stage = 0;
        this.afterRotationTick = 0;
        this.preRotationTick = 0;
        if (((Boolean)this.centerPlayer.getValue()).booleanValue() && AutoSkull.mc.field_71439_g.field_70122_E) {
            AutoSkull.mc.field_71439_g.field_70159_w = 0.0;
            AutoSkull.mc.field_71439_g.field_70179_y = 0.0;
        }
        this.centeredBlock = BlockUtil.getCenterOfBlock(AutoSkull.mc.field_71439_g.field_70165_t, AutoSkull.mc.field_71439_g.field_70163_u, AutoSkull.mc.field_71439_g.field_70163_u);
    }

    @Override
    public void onDisable() {
        SpoofRotationUtil.ROTATION_UTIL.onDisable();
        PlacementUtil.onDisable();
        if (AutoSkull.mc.field_71439_g == null) {
            return;
        }
        if (this.noObby) {
            this.setDisabledMessage("Skull not found... AutoSkull turned OFF!");
        }
        if (((Boolean)this.offHandSkull.getValue()).booleanValue()) {
            OffHand.removeItem(1);
        }
    }

    @Override
    public void onUpdate() {
        if (AutoSkull.mc.field_71439_g == null) {
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
            EntityPlayer closest;
            this.delayTimeTicks = 0;
            if (((Boolean)this.centerPlayer.getValue()).booleanValue() && this.centeredBlock != Vec3d.field_186680_a && AutoSkull.mc.field_71439_g.field_70122_E) {
                PlayerUtil.centerPlayer(this.centeredBlock);
            }
            if (this.toPhase) {
                if (++this.tickTry == (Integer)this.maxTickTries.getValue()) {
                    AutoSkull.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(AutoSkull.mc.field_71439_g.field_70165_t, (double)((int)AutoSkull.mc.field_71439_g.field_70163_u + 1), AutoSkull.mc.field_71439_g.field_70161_v, AutoSkull.mc.field_71439_g.field_70177_z, AutoSkull.mc.field_71439_g.field_70125_A, AutoSkull.mc.field_71439_g.field_70122_E));
                    AutoSkull.mc.field_71439_g.field_70163_u = (int)AutoSkull.mc.field_71439_g.field_70163_u + 1;
                    this.disable();
                }
                if (BlockUtil.getBlock(AutoSkull.mc.field_71439_g.field_70165_t, AutoSkull.mc.field_71439_g.field_70163_u, AutoSkull.mc.field_71439_g.field_70161_v) instanceof BlockSkull) {
                    if (!AutoSkull.mc.field_71439_g.field_70122_E) {
                        AutoSkull.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(AutoSkull.mc.field_71439_g.field_70165_t, AutoSkull.mc.field_71439_g.field_70163_u - (AutoSkull.mc.field_71439_g.field_70163_u - (double)((int)AutoSkull.mc.field_71439_g.field_70163_u)), AutoSkull.mc.field_71439_g.field_70161_v, AutoSkull.mc.field_71439_g.field_70177_z, AutoSkull.mc.field_71439_g.field_70125_A, AutoSkull.mc.field_71439_g.field_70122_E));
                        return;
                    }
                    switch (this.stage) {
                        case 0: {
                            AutoSkull.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(AutoSkull.mc.field_71439_g.field_70165_t, AutoSkull.mc.field_71439_g.field_70163_u - 0.001, AutoSkull.mc.field_71439_g.field_70161_v, AutoSkull.mc.field_71439_g.field_70177_z, AutoSkull.mc.field_71439_g.field_70125_A, AutoSkull.mc.field_71439_g.field_70122_E));
                            AutoSkull.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(AutoSkull.mc.field_71439_g.field_70165_t, AutoSkull.mc.field_71439_g.field_70163_u + 1000.0, AutoSkull.mc.field_71439_g.field_70161_v, AutoSkull.mc.field_71439_g.field_70177_z, AutoSkull.mc.field_71439_g.field_70125_A, AutoSkull.mc.field_71439_g.field_70122_E));
                        }
                    }
                } else {
                    if (BlockUtil.getBlock((double)AutoSkull.mc.field_71439_g.field_70165_t, (double)AutoSkull.mc.field_71439_g.field_70163_u, (double)AutoSkull.mc.field_71439_g.field_70161_v).field_149787_q && AutoSkull.mc.field_71439_g.field_70163_u - (double)((int)AutoSkull.mc.field_71439_g.field_70163_u) <= 0.5) {
                        this.disable();
                    }
                    if (AutoSkull.mc.field_71439_g.field_70163_u - (double)((int)AutoSkull.mc.field_71439_g.field_70163_u) > 0.5) {
                        this.placeBlock(false);
                    } else if (++this.resetPhase == 50) {
                        this.disable();
                    }
                }
                return;
            }
            if (((Boolean)this.onlyHoles.getValue()).booleanValue() && HoleUtil.isHole(EntityUtil.getPosition((Entity)AutoSkull.mc.field_71439_g), true, true).getType() == HoleUtil.HoleType.NONE) {
                return;
            }
            SpoofRotationUtil.ROTATION_UTIL.shouldSpoofAngles(true);
            if (((Boolean)this.autoTrap.getValue()).booleanValue() && BlockUtil.getBlock(new BlockPos((Vec3i)AutoSkull.mc.field_71439_g.func_180425_c().func_177963_a(0.0, 0.4, 0.0))) instanceof BlockSkull && (closest = PlayerUtil.findClosestTarget(2.0, null)) != null && (int)closest.field_70165_t == (int)AutoSkull.mc.field_71439_g.field_70165_t && (int)closest.field_70161_v == (int)AutoSkull.mc.field_71439_g.field_70161_v && closest.field_70163_u > AutoSkull.mc.field_71439_g.field_70163_u && closest.field_70163_u < AutoSkull.mc.field_71439_g.field_70163_u + 2.0) {
                int blocksPlaced = 0;
                for (int offsetSteps = 0; blocksPlaced <= (Integer)this.BlocksPerTick.getValue() && offsetSteps < 10; ++offsetSteps) {
                    BlockPos offsetPos = new BlockPos(AIR[offsetSteps]);
                    BlockPos targetPos = AutoSkull.mc.field_71439_g.func_180425_c().func_177982_a(offsetPos.func_177958_n(), offsetPos.func_177956_o(), offsetPos.func_177952_p());
                    if (!this.placeBlock(targetPos)) continue;
                    ++blocksPlaced;
                }
            }
            if (((Boolean)this.instaActive.getValue()).booleanValue()) {
                this.placeBlock(true);
                return;
            }
            if (((Boolean)this.onShift.getValue()).booleanValue() && AutoSkull.mc.field_71474_y.field_74311_E.func_151470_d()) {
                if (!this.firstShift) {
                    this.placeBlock(true);
                }
                return;
            }
            if (this.firstShift && !AutoSkull.mc.field_71474_y.field_74311_E.func_151470_d()) {
                this.firstShift = false;
            }
            if ((Double)this.playerDistance.getValue() != 0.0 && PlayerUtil.findClosestTarget((Double)this.playerDistance.getValue(), null) != null) {
                this.placeBlock(true);
                return;
            }
        }
    }

    private boolean placeBlock(BlockPos pos) {
        EnumHand handSwing = EnumHand.MAIN_HAND;
        int obsidianSlot = InventoryUtil.findObsidianSlot(false, false);
        if (AutoSkull.mc.field_71439_g.field_71071_by.field_70461_c != obsidianSlot && obsidianSlot != 9) {
            AutoSkull.mc.field_71439_g.field_71071_by.field_70461_c = obsidianSlot;
        }
        return PlacementUtil.place(pos, handSwing, (boolean)((Boolean)this.rotate.getValue()), true);
    }

    private void placeBlock(boolean changeStatus) {
        BlockPos pos = new BlockPos(AutoSkull.mc.field_71439_g.field_70165_t, AutoSkull.mc.field_71439_g.field_70163_u + 0.4, AutoSkull.mc.field_71439_g.field_70161_v);
        if (AutoSkull.mc.field_71441_e.func_180495_p(pos).func_185904_a().func_76222_j()) {
            EnumHand handSwing = EnumHand.MAIN_HAND;
            int skullSlot = InventoryUtil.findSkullSlot((Boolean)this.offHandSkull.getValue(), this.activedBefore);
            if (skullSlot == -1) {
                this.noObby = true;
                return;
            }
            if (skullSlot == 9) {
                if (AutoSkull.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemSkull) {
                    handSwing = EnumHand.OFF_HAND;
                } else {
                    return;
                }
            }
            if (AutoSkull.mc.field_71439_g.field_71071_by.field_70461_c != skullSlot && skullSlot != 9) {
                this.oldSlot = AutoSkull.mc.field_71439_g.field_71071_by.field_70461_c;
                AutoSkull.mc.field_71439_g.field_71071_by.field_70461_c = skullSlot;
            }
            if ((Integer)this.preSwitch.getValue() > 0 && this.preRotationTick++ == (Integer)this.preSwitch.getValue()) {
                this.lastHitVec = new Vec3d((double)pos.field_177962_a, (double)pos.field_177960_b, (double)pos.field_177961_c);
                return;
            }
            if (this.alrPlaced && changeStatus || ((Boolean)this.noUp.getValue() != false ? PlacementUtil.place(pos, handSwing, (boolean)((Boolean)this.rotate.getValue()), this.exd) || PlacementUtil.place(pos, handSwing, (Boolean)this.rotate.getValue()) : PlacementUtil.place(pos, handSwing, (Boolean)this.rotate.getValue()))) {
                this.alrPlaced = true;
                if ((Integer)this.afterSwitch.getValue() > 0 && this.afterRotationTick++ == (Integer)this.afterSwitch.getValue()) {
                    this.lastHitVec = new Vec3d((double)pos.field_177962_a, (double)pos.field_177960_b, (double)pos.field_177961_c);
                    return;
                }
                if (this.oldSlot != -1) {
                    AutoSkull.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
                    this.oldSlot = -1;
                }
                if (changeStatus) {
                    this.firstShift = true;
                    this.alrPlaced = true;
                    this.activedBefore = true;
                    if (((Boolean)this.offHandSkull.getValue()).booleanValue()) {
                        OffHand.removeItem(1);
                    }
                    if (((Boolean)this.disableAfter.getValue()).booleanValue() && !((Boolean)this.phase.getValue()).booleanValue()) {
                        this.disable();
                    }
                    if (((Boolean)this.phase.getValue()).booleanValue() && AutoSkull.mc.field_71439_g.field_70163_u > 1.0) {
                        this.toPhase = true;
                        this.stage = 0;
                        if (((Boolean)this.ServerRespond.getValue()).booleanValue()) {
                            AutoSkull.mc.field_71441_e.func_175698_g(new BlockPos(AutoSkull.mc.field_71439_g.field_70165_t, AutoSkull.mc.field_71439_g.field_70163_u, AutoSkull.mc.field_71439_g.field_70161_v));
                        }
                        if (((Boolean)this.predictPhase.getValue()).booleanValue()) {
                            AutoSkull.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(AutoSkull.mc.field_71439_g.field_70165_t, AutoSkull.mc.field_71439_g.field_70163_u - 1.0, AutoSkull.mc.field_71439_g.field_70161_v, AutoSkull.mc.field_71439_g.field_70177_z, AutoSkull.mc.field_71439_g.field_70125_A, AutoSkull.mc.field_71439_g.field_70122_E));
                        }
                    }
                    this.afterRotationTick = 0;
                    this.preRotationTick = 0;
                    this.lastHitVec = null;
                    this.centeredBlock = Vec3d.field_186680_a;
                }
            } else {
                this.lastHitVec = null;
            }
        }
    }
}

