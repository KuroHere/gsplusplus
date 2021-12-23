/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BlockUtil {
    public static final List blackList;
    public static final List shulkerList;
    private static final Minecraft mc;

    public static IBlockState getState(BlockPos pos) {
        return BlockUtil.mc.field_71441_e.func_180495_p(pos);
    }

    public static boolean checkForNeighbours(BlockPos blockPos) {
        if (!BlockUtil.hasNeighbour(blockPos)) {
            for (EnumFacing side : EnumFacing.values()) {
                BlockPos neighbour = blockPos.func_177972_a(side);
                if (!BlockUtil.hasNeighbour(neighbour)) continue;
                return true;
            }
            return false;
        }
        return true;
    }

    private static boolean hasNeighbour(BlockPos blockPos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = blockPos.func_177972_a(side);
            if (BlockUtil.mc.field_71441_e.func_180495_p(neighbour).func_185904_a().func_76222_j()) continue;
            return true;
        }
        return false;
    }

    public static Block getBlock(BlockPos pos) {
        return BlockUtil.getState(pos).func_177230_c();
    }

    public static Block getBlock(double x, double y, double z) {
        return BlockUtil.mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
    }

    public static boolean canBeClicked(BlockPos pos) {
        return BlockUtil.getBlock(pos).func_176209_a(BlockUtil.getState(pos), false);
    }

    public static CPacketPlayer.Rotation faceVectorPacketInstant(Vec3d vec, Boolean roundAngles) {
        float[] rotations = BlockUtil.getNeededRotations2(vec);
        CPacketPlayer.Rotation e = new CPacketPlayer.Rotation(rotations[0], roundAngles != false ? (float)MathHelper.func_180184_b((int)((int)rotations[1]), (int)360) : rotations[1], BlockUtil.mc.field_71439_g.field_70122_E);
        BlockUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)e);
        return e;
    }

    public static CPacketPlayer.Rotation getFaceVectorPacket(Vec3d vec, Boolean roundAngles) {
        float[] rotations = BlockUtil.getNeededRotations2(vec);
        CPacketPlayer.Rotation e = new CPacketPlayer.Rotation(rotations[0], roundAngles != false ? (float)MathHelper.func_180184_b((int)((int)rotations[1]), (int)360) : rotations[1], BlockUtil.mc.field_71439_g.field_70122_E);
        BlockUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)e);
        return e;
    }

    public static float[] getNeededRotations2(Vec3d vec) {
        Vec3d eyesPos = BlockUtil.getEyesPos();
        double diffX = vec.field_72450_a - eyesPos.field_72450_a;
        double diffY = vec.field_72448_b - eyesPos.field_72448_b;
        double diffZ = vec.field_72449_c - eyesPos.field_72449_c;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{BlockUtil.mc.field_71439_g.field_70177_z + MathHelper.func_76142_g((float)(yaw - BlockUtil.mc.field_71439_g.field_70177_z)), BlockUtil.mc.field_71439_g.field_70125_A + MathHelper.func_76142_g((float)(pitch - BlockUtil.mc.field_71439_g.field_70125_A))};
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(BlockUtil.mc.field_71439_g.field_70165_t, BlockUtil.mc.field_71439_g.field_70163_u + (double)BlockUtil.mc.field_71439_g.func_70047_e(), BlockUtil.mc.field_71439_g.field_70161_v);
    }

    public static List<BlockPos> getCircle(BlockPos loc, int y, float r, boolean hollow) {
        ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        int cx = loc.func_177958_n();
        int cz = loc.func_177952_p();
        int x = cx - (int)r;
        while ((float)x <= (float)cx + r) {
            int z = cz - (int)r;
            while ((float)z <= (float)cz + r) {
                double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z);
                if (dist < (double)(r * r) && (!hollow || dist >= (double)((r - 1.0f) * (r - 1.0f)))) {
                    BlockPos l = new BlockPos(x, y, z);
                    circleblocks.add(l);
                }
                ++z;
            }
            ++x;
        }
        return circleblocks;
    }

    public static EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            IBlockState blockState;
            BlockPos neighbour = pos.func_177972_a(side);
            if (!BlockUtil.mc.field_71441_e.func_180495_p(neighbour).func_177230_c().func_176209_a(BlockUtil.mc.field_71441_e.func_180495_p(neighbour), false) || (blockState = BlockUtil.mc.field_71441_e.func_180495_p(neighbour)).func_185904_a().func_76222_j()) continue;
            return side;
        }
        return null;
    }

    public static EnumFacing getPlaceableSideExlude(BlockPos pos, ArrayList<EnumFacing> excluding) {
        for (EnumFacing side : EnumFacing.values()) {
            IBlockState blockState;
            BlockPos neighbour;
            if (excluding.contains(side) || !BlockUtil.mc.field_71441_e.func_180495_p(neighbour = pos.func_177972_a(side)).func_177230_c().func_176209_a(BlockUtil.mc.field_71441_e.func_180495_p(neighbour), false) || (blockState = BlockUtil.mc.field_71441_e.func_180495_p(neighbour)).func_185904_a().func_76222_j()) continue;
            return side;
        }
        return null;
    }

    public static Vec3d getCenterOfBlock(double playerX, double playerY, double playerZ) {
        double newX = Math.floor(playerX) + 0.5;
        double newY = Math.floor(playerY);
        double newZ = Math.floor(playerZ) + 0.5;
        return new Vec3d(newX, newY, newZ);
    }

    static {
        mc = Minecraft.func_71410_x();
        blackList = Arrays.asList(Blocks.field_150477_bB, Blocks.field_150486_ae, Blocks.field_150447_bR, Blocks.field_150462_ai, Blocks.field_150467_bQ, Blocks.field_150382_bo, Blocks.field_150438_bZ, Blocks.field_150409_cd, Blocks.field_150367_z);
        shulkerList = Arrays.asList(Blocks.field_190977_dl, Blocks.field_190978_dm, Blocks.field_190979_dn, Blocks.field_190980_do, Blocks.field_190981_dp, Blocks.field_190982_dq, Blocks.field_190983_dr, Blocks.field_190984_ds, Blocks.field_190985_dt, Blocks.field_190986_du, Blocks.field_190987_dv, Blocks.field_190988_dw, Blocks.field_190989_dx, Blocks.field_190990_dy, Blocks.field_190991_dz, Blocks.field_190975_dA);
    }
}

