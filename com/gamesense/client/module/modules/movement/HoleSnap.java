/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.api.util.world.MotionUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Module.Declaration(name="HoleSnap", category=Category.Movement)
public class HoleSnap
extends Module {
    DoubleSetting speedA = this.registerDouble("Speed", 0.0, 0.0, 2.0);
    DoubleSetting range = this.registerDouble("Range", 4.0, 0.0, 10.0);
    BlockPos hole;
    double yawRad;
    double speed;
    double lastDist;
    BlockPos distPos;

    @Override
    protected void onEnable() {
        this.hole = null;
        this.hole = this.findHoles();
        if (this.hole == null) {
            this.disable();
        }
    }

    @Override
    protected void onDisable() {
        this.hole = null;
    }

    @Override
    public void onUpdate() {
        if (HoleSnap.mc.field_71474_y.field_74311_E.func_151470_d() || HoleUtil.isInHole((Entity)HoleSnap.mc.field_71439_g, true, false)) {
            PlayerUtil.centerPlayer(HoleSnap.mc.field_71439_g.func_174791_d());
            this.disable();
            return;
        }
        this.yawRad = (double)RotationUtil.getRotationTo((Vec3d)HoleSnap.mc.field_71439_g.func_174791_d().func_72441_c((double)-0.5, (double)0.0, (double)-0.5), (Vec3d)new Vec3d((Vec3i)this.hole)).field_189982_i * Math.PI / 180.0;
        double dist = HoleSnap.mc.field_71439_g.func_174791_d().func_72438_d(new Vec3d((double)this.hole.func_177958_n(), (double)this.hole.func_177956_o(), (double)this.hole.func_177952_p()));
        this.speed = HoleSnap.mc.field_71439_g.field_70122_E ? Math.min(MotionUtil.getBaseMoveSpeed(), Math.abs(dist) / 2.0) : Math.min(Math.abs(HoleSnap.mc.field_71439_g.field_70159_w) + Math.abs(HoleSnap.mc.field_71439_g.field_70179_y), Math.abs(dist) / 2.0);
        this.speed *= ((Double)this.speedA.getValue()).doubleValue();
        HoleSnap.mc.field_71439_g.field_70159_w = -Math.sin(this.yawRad) * this.speed;
        HoleSnap.mc.field_71439_g.field_70179_y = Math.cos(this.yawRad) * this.speed;
    }

    private BlockPos findHoles() {
        NonNullList holes = NonNullList.func_191196_a();
        List<BlockPos> blockPosList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), ((Double)this.range.getValue()).floatValue(), ((Double)this.range.getValue()).intValue(), false, true, 0);
        blockPosList.forEach(pos -> {
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, true, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            if (holeType != HoleUtil.HoleType.NONE) {
                AxisAlignedBB centreBlocks = holeInfo.getCentre();
                if (centreBlocks == null) {
                    return;
                }
                if (holeType == HoleUtil.HoleType.SINGLE && HoleSnap.mc.field_71441_e.func_175623_d(pos) && HoleSnap.mc.field_71441_e.func_175623_d(pos.func_177982_a(0, 1, 0)) && HoleSnap.mc.field_71441_e.func_175623_d(pos.func_177982_a(0, 2, 0)) && (double)pos.func_177956_o() <= HoleSnap.mc.field_71439_g.field_70163_u) {
                    holes.add(pos);
                }
            }
        });
        this.distPos = new BlockPos(Double.POSITIVE_INFINITY, 69.0, 429.0);
        this.lastDist = 2.147483647E9;
        for (BlockPos blockPos : holes) {
            if (!(HoleSnap.mc.field_71439_g.func_174818_b(blockPos) < this.lastDist)) continue;
            this.distPos = blockPos;
            this.lastDist = HoleSnap.mc.field_71439_g.func_174818_b(blockPos);
        }
        if (!this.distPos.equals((Object)new BlockPos(Double.POSITIVE_INFINITY, 69.0, 429.0))) {
            return this.distPos;
        }
        return null;
    }
}

