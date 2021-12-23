/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.misc.Timer;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.AutoCrystalRewrite;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Module.Declaration(name="Surround", category=Category.Combat, priority=101)
public class Surround
extends Module {
    private final Timer delayTimer = new Timer();
    BooleanSetting allowNon1x1 = this.registerBoolean("Allow non 1x1", true);
    BooleanSetting centre = this.registerBoolean("Centre", false, () -> (Boolean)this.allowNon1x1.getValue() == false);
    IntegerSetting delayTicks = this.registerInteger("Tick Delay", 3, 0, 10);
    IntegerSetting blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 1, 20);
    BooleanSetting onlyOnStop = this.registerBoolean("OnStop", false);
    BooleanSetting disableOnJump = this.registerBoolean("Disable On Jump", false);
    BooleanSetting onlyOnSneak = this.registerBoolean("Only on Sneak", false);
    BooleanSetting rotate = this.registerBoolean("Rotate", false);
    IntegerSetting afterRotate = this.registerInteger("After Rotate", 3, 0, 5, () -> (Boolean)this.rotate.getValue());
    BooleanSetting destroyCrystal = this.registerBoolean("Destroy Stuck Crystal", false);
    BooleanSetting destroyAboveCrystal = this.registerBoolean("Destroy Above Crystal", false);
    BooleanSetting alertPlayerClip = this.registerBoolean("Alert Player Clip", true);
    BooleanSetting stopAC = this.registerBoolean("Stop AC", false);
    IntegerSetting tickStopAC = this.registerInteger("Tick Stop AC", 3, 0, 5, () -> (Boolean)this.stopAC.getValue());
    ArrayList<BlockPos> blockChanged = new ArrayList();
    int y;
    @EventHandler
    private final Listener<PacketEvent.Receive> listener2 = new Listener<PacketEvent.Receive>(event -> {}, new Predicate[0]);
    Timer alertDelay = new Timer();
    boolean hasPlaced;
    int lookDown = -1;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
        if (Surround.mc.field_71439_g == null || Surround.mc.field_71441_e == null || this.lookDown == -1) {
            return;
        }
        PlayerPacketManager.INSTANCE.addPacket(new PlayerPacket((Module)this, new Vec2f(0.0f, 90.0f)));
        --this.lookDown;
    }, new Predicate[0]);
    int tickAC = -1;

    int getSlot() {
        int slot = InventoryUtil.findFirstBlockSlot(Blocks.field_150343_Z.getClass(), 0, 8);
        if (slot == -1) {
            slot = InventoryUtil.findFirstBlockSlot(Blocks.field_150477_bB.getClass(), 0, 8);
        }
        return slot;
    }

    @Override
    protected void onEnable() {
        this.alertDelay.setTimer(0L);
        this.y = (int)Math.floor(Surround.mc.field_71439_g.field_70163_u);
    }

    @Override
    public void onUpdate() {
        if (Surround.mc.field_71439_g == null || Surround.mc.field_71441_e == null) {
            return;
        }
        if (((Boolean)this.onlyOnStop.getValue()).booleanValue() && (Surround.mc.field_71439_g.field_70159_w != 0.0 || Surround.mc.field_71439_g.field_70181_x != 0.0 || Surround.mc.field_71439_g.field_70179_y != 0.0)) {
            return;
        }
        if (((Boolean)this.onlyOnSneak.getValue()).booleanValue() && !Surround.mc.field_71474_y.field_74311_E.func_151468_f()) {
            return;
        }
        if (((Boolean)this.disableOnJump.getValue()).booleanValue() && Math.abs((double)Math.abs(this.y) - Math.abs(Surround.mc.field_71439_g.field_70163_u)) >= 0.3) {
            this.disable();
            return;
        }
        if (this.tickAC > -1 && --this.tickAC == 0) {
            AutoCrystalRewrite.stopAC = false;
        }
        if (this.delayTimer.getTimePassed() / 50L >= (long)((Integer)this.delayTicks.getValue()).intValue()) {
            this.delayTimer.reset();
            int blocksPlaced = 0;
            this.hasPlaced = false;
            List<BlockPos> offsetPattern = this.getOffsets();
            int maxSteps = offsetPattern.size();
            boolean hasSilentSwitched = false;
            int blockSlot = this.getSlot();
            if (blockSlot == -1) {
                return;
            }
            int offsetSteps = 0;
            if (((Boolean)this.centre.getValue()).booleanValue() && !((Boolean)this.allowNon1x1.getValue()).booleanValue()) {
                PlayerUtil.centerPlayer(Surround.mc.field_71439_g.func_174791_d());
            }
            while (blocksPlaced <= (Integer)this.blocksPerTick.getValue() && offsetSteps < maxSteps) {
                BlockPos targetPos;
                if (this.blockChanged.contains(targetPos = offsetPattern.get(offsetSteps++))) continue;
                Surround.mc.field_71441_e.func_175674_a(null, new AxisAlignedBB(targetPos), null);
                boolean foundSomeone = false;
                for (Entity entity : Surround.mc.field_71441_e.func_72839_b(null, new AxisAlignedBB(targetPos))) {
                    if (entity instanceof EntityPlayer) {
                        foundSomeone = true;
                        if (!((Boolean)this.alertPlayerClip.getValue()).booleanValue() || entity == Surround.mc.field_71439_g || !this.alertDelay.hasReached(1000L)) break;
                        PistonCrystal.printDebug("Player " + entity.func_70005_c_() + " is clipping in your surround", false);
                        this.alertDelay.reset();
                        break;
                    }
                    if (!(entity instanceof EntityEnderCrystal) || !((Boolean)this.destroyCrystal.getValue()).booleanValue()) continue;
                    if (((Boolean)this.rotate.getValue()).booleanValue()) {
                        BlockUtil.faceVectorPacketInstant(new Vec3d((Vec3i)entity.func_180425_c()).func_72441_c(0.5, 0.0, 0.5), true);
                    }
                    Surround.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketUseEntity(entity));
                    Surround.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                }
                if (((Boolean)this.destroyAboveCrystal.getValue()).booleanValue()) {
                    for (Entity entity : new ArrayList(Surround.mc.field_71441_e.field_72996_f)) {
                        if (!(entity instanceof EntityEnderCrystal) || !this.sameBlockPos(entity.func_180425_c(), targetPos)) continue;
                        if (((Boolean)this.rotate.getValue()).booleanValue()) {
                            BlockUtil.faceVectorPacketInstant(new Vec3d((Vec3i)entity.func_180425_c()).func_72441_c(0.5, 0.0, 0.5), true);
                        }
                        Surround.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketUseEntity(entity));
                        Surround.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                    }
                }
                if (foundSomeone || !Surround.mc.field_71441_e.func_180495_p(targetPos).func_185904_a().func_76222_j()) continue;
                if (!hasSilentSwitched && blockSlot != Surround.mc.field_71439_g.field_71071_by.field_70461_c) {
                    Surround.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(blockSlot));
                    hasSilentSwitched = true;
                }
                if (!PlacementUtil.place(targetPos, EnumHand.MAIN_HAND, (boolean)((Boolean)this.rotate.getValue()), false)) continue;
                if (((Boolean)this.centre.getValue()).booleanValue()) {
                    PlayerUtil.centerPlayer(Surround.mc.field_71439_g.func_174791_d());
                }
                this.y = (int)Math.floor(Surround.mc.field_71439_g.field_70163_u);
                if (((Boolean)this.stopAC.getValue()).booleanValue()) {
                    this.tickAC = (Integer)this.tickStopAC.getValue();
                    AutoCrystalRewrite.stopAC = true;
                }
                PistonCrystal.printDebug("Normal Place", false);
                ++blocksPlaced;
                if (!((Boolean)this.rotate.getValue()).booleanValue() || (Integer)this.afterRotate.getValue() == 0) continue;
                this.lookDown = (Integer)this.afterRotate.getValue();
            }
            if (hasSilentSwitched) {
                Surround.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(Surround.mc.field_71439_g.field_71071_by.field_70461_c));
                Surround.mc.field_71442_b.func_78765_e();
            }
        }
        PlacementUtil.stopSneaking();
        this.blockChanged.clear();
    }

    boolean sameBlockPos(BlockPos first, BlockPos second) {
        if (first == null || second == null) {
            return false;
        }
        return first.func_177958_n() == second.func_177958_n() && first.func_177956_o() == second.func_177956_o() + 2 && first.func_177952_p() == second.func_177952_p();
    }

    List<BlockPos> getOffsets() {
        BlockPos playerPos = this.getPlayerPos();
        ArrayList<BlockPos> offsets = new ArrayList<BlockPos>();
        if (((Boolean)this.allowNon1x1.getValue()).booleanValue()) {
            int z;
            int x;
            double decimalX = Math.abs(Surround.mc.field_71439_g.field_70165_t) - Math.floor(Math.abs(Surround.mc.field_71439_g.field_70165_t));
            double decimalZ = Math.abs(Surround.mc.field_71439_g.field_70161_v) - Math.floor(Math.abs(Surround.mc.field_71439_g.field_70161_v));
            int lengthXPos = this.calcLength(decimalX, false);
            int lengthXNeg = this.calcLength(decimalX, true);
            int lengthZPos = this.calcLength(decimalZ, false);
            int lengthZNeg = this.calcLength(decimalZ, true);
            ArrayList<BlockPos> tempOffsets = new ArrayList<BlockPos>();
            offsets.addAll(this.getOverlapPos());
            for (x = 1; x < lengthXPos + 1; ++x) {
                tempOffsets.add(this.addToPlayer(playerPos, x, 0.0, 1 + lengthZPos));
                tempOffsets.add(this.addToPlayer(playerPos, x, 0.0, -(1 + lengthZNeg)));
            }
            for (x = 0; x <= lengthXNeg; ++x) {
                tempOffsets.add(this.addToPlayer(playerPos, -x, 0.0, 1 + lengthZPos));
                tempOffsets.add(this.addToPlayer(playerPos, -x, 0.0, -(1 + lengthZNeg)));
            }
            for (z = 1; z < lengthZPos + 1; ++z) {
                tempOffsets.add(this.addToPlayer(playerPos, 1 + lengthXPos, 0.0, z));
                tempOffsets.add(this.addToPlayer(playerPos, -(1 + lengthXNeg), 0.0, z));
            }
            for (z = 0; z <= lengthZNeg; ++z) {
                tempOffsets.add(this.addToPlayer(playerPos, 1 + lengthXPos, 0.0, -z));
                tempOffsets.add(this.addToPlayer(playerPos, -(1 + lengthXNeg), 0.0, -z));
            }
            for (BlockPos pos : tempOffsets) {
                if (Surround.getDown(pos)) {
                    offsets.add(pos.func_177982_a(0, -1, 0));
                }
                offsets.add(pos);
            }
        } else {
            offsets.add(playerPos.func_177982_a(0, -1, 0));
            for (int[] surround : new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}}) {
                if (Surround.getDown(playerPos.func_177982_a(surround[0], 0, surround[1]))) {
                    offsets.add(playerPos.func_177982_a(surround[0], -1, surround[1]));
                }
                offsets.add(playerPos.func_177982_a(surround[0], 0, surround[1]));
            }
        }
        return offsets;
    }

    public static boolean getDown(BlockPos pos) {
        for (EnumFacing e : EnumFacing.values()) {
            if (Surround.mc.field_71441_e.func_175623_d(pos.func_177971_a(e.func_176730_m()))) continue;
            return false;
        }
        return true;
    }

    int calcOffset(double dec) {
        return dec >= 0.7 ? 1 : (dec <= 0.3 ? -1 : 0);
    }

    BlockPos addToPlayer(BlockPos playerPos, double x, double y, double z) {
        if (playerPos.func_177958_n() < 0) {
            x = -x;
        }
        if (playerPos.func_177956_o() < 0) {
            y = -y;
        }
        if (playerPos.func_177952_p() < 0) {
            z = -z;
        }
        return playerPos.func_177963_a(x, y, z);
    }

    List<BlockPos> getOverlapPos() {
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        double decimalX = Surround.mc.field_71439_g.field_70165_t - Math.floor(Surround.mc.field_71439_g.field_70165_t);
        double decimalZ = Surround.mc.field_71439_g.field_70161_v - Math.floor(Surround.mc.field_71439_g.field_70161_v);
        int offX = this.calcOffset(decimalX);
        int offZ = this.calcOffset(decimalZ);
        positions.add(this.getPlayerPos());
        for (int x = 0; x <= Math.abs(offX); ++x) {
            for (int z = 0; z <= Math.abs(offZ); ++z) {
                int properX = x * offX;
                int properZ = z * offZ;
                positions.add(this.getPlayerPos().func_177982_a(properX, -1, properZ));
            }
        }
        return positions;
    }

    int calcLength(double decimal, boolean negative) {
        if (negative) {
            return decimal <= 0.3 ? 1 : 0;
        }
        return decimal >= 0.7 ? 1 : 0;
    }

    BlockPos getPlayerPos() {
        double decimalPoint = Surround.mc.field_71439_g.field_70163_u - Math.floor(Surround.mc.field_71439_g.field_70163_u);
        return new BlockPos(Surround.mc.field_71439_g.field_70165_t, decimalPoint > 0.8 ? Math.floor(Surround.mc.field_71439_g.field_70163_u) + 1.0 : Math.floor(Surround.mc.field_71439_g.field_70163_u), Surround.mc.field_71439_g.field_70161_v);
    }
}

