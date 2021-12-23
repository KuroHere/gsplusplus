/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.world;

import com.gamesense.api.util.player.social.SocialManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityUtil {
    private static final Minecraft mc = Minecraft.func_71410_x();

    public static Block isColliding(double posX, double posY, double posZ) {
        Block block = null;
        if (EntityUtil.mc.field_71439_g != null) {
            AxisAlignedBB bb = EntityUtil.mc.field_71439_g.func_184187_bx() != null ? EntityUtil.mc.field_71439_g.func_184187_bx().func_174813_aQ().func_191195_a(0.0, 0.0, 0.0).func_72317_d(posX, posY, posZ) : EntityUtil.mc.field_71439_g.func_174813_aQ().func_191195_a(0.0, 0.0, 0.0).func_72317_d(posX, posY, posZ);
            int y = (int)bb.field_72338_b;
            for (int x = MathHelper.func_76128_c((double)bb.field_72340_a); x < MathHelper.func_76128_c((double)bb.field_72336_d) + 1; ++x) {
                for (int z = MathHelper.func_76128_c((double)bb.field_72339_c); z < MathHelper.func_76128_c((double)bb.field_72334_f) + 1; ++z) {
                    block = EntityUtil.mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
                }
            }
        }
        return block;
    }

    public static boolean isInLiquid() {
        if (EntityUtil.mc.field_71439_g != null) {
            if (EntityUtil.mc.field_71439_g.field_70143_R >= 3.0f) {
                return false;
            }
            boolean inLiquid = false;
            AxisAlignedBB bb = EntityUtil.mc.field_71439_g.func_184187_bx() != null ? EntityUtil.mc.field_71439_g.func_184187_bx().func_174813_aQ() : EntityUtil.mc.field_71439_g.func_174813_aQ();
            int y = (int)bb.field_72338_b;
            for (int x = MathHelper.func_76128_c((double)bb.field_72340_a); x < MathHelper.func_76128_c((double)bb.field_72336_d) + 1; ++x) {
                for (int z = MathHelper.func_76128_c((double)bb.field_72339_c); z < MathHelper.func_76128_c((double)bb.field_72334_f) + 1; ++z) {
                    Block block = EntityUtil.mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
                    if (block instanceof BlockAir) continue;
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    inLiquid = true;
                }
            }
            return inLiquid;
        }
        return false;
    }

    public static void setTimer(float speed) {
        Minecraft.func_71410_x().field_71428_T.field_194149_e = 50.0f / speed;
    }

    public static void resetTimer() {
        Minecraft.func_71410_x().field_71428_T.field_194149_e = 50.0f;
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return EntityUtil.getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
        return new Vec3d(entity.field_70142_S, entity.field_70137_T, entity.field_70136_U).func_178787_e(EntityUtil.getInterpolatedAmount(entity, ticks));
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.field_70165_t - entity.field_70142_S) * x, (entity.field_70163_u - entity.field_70137_T) * y, (entity.field_70161_v - entity.field_70136_U) * z);
    }

    public static float clamp(float val, float min, float max) {
        if (val <= min) {
            val = min;
        }
        if (val >= max) {
            val = max;
        }
        return val;
    }

    public static List<BlockPos> getSphere(BlockPos loc, float radius, int height, boolean hollow, boolean sphere, int plus_y) {
        ArrayList<BlockPos> circleBlocks = new ArrayList<BlockPos>();
        int locX = loc.func_177958_n();
        int locY = loc.func_177956_o();
        int locZ = loc.func_177952_p();
        int x = locX - (int)radius;
        while ((float)x <= (float)locX + radius) {
            int z = locZ - (int)radius;
            while ((float)z <= (float)locZ + radius) {
                int y = sphere ? locY - (int)radius : locY;
                while (true) {
                    float f = y;
                    float f2 = sphere ? (float)locY + radius : (float)(locY + height);
                    if (!(f < f2)) break;
                    double dist = (locX - x) * (locX - x) + (locZ - z) * (locZ - z) + (sphere ? (locY - y) * (locY - y) : 0);
                    if (!(!(dist < (double)(radius * radius)) || hollow && dist < (double)((radius - 1.0f) * (radius - 1.0f)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleBlocks.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return circleBlocks;
    }

    public static List<BlockPos> getHollowSphere(BlockPos loc, float radius, int height, boolean sphere, int plus_y, float hollowRad, float hollowHeight) {
        ArrayList<BlockPos> circleBlocks = new ArrayList<BlockPos>();
        int locX = loc.func_177958_n();
        int locY = loc.func_177956_o();
        int locZ = loc.func_177952_p();
        int x = locX - (int)radius;
        while ((float)x <= (float)locX + radius) {
            int z = locZ - (int)radius;
            while ((float)z <= (float)locZ + radius) {
                int y = sphere ? locY - (int)radius : locY;
                while (true) {
                    float f = y;
                    float f2 = sphere ? (float)locY + radius : (float)(locY + height);
                    if (!(f < f2)) break;
                    double dist = (locX - x) * (locX - x) + (locZ - z) * (locZ - z) + (sphere ? (locY - y) * (locY - y) : 0);
                    if (dist < (double)(radius * radius)) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleBlocks.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        ArrayList<BlockPos> remove = new ArrayList<BlockPos>();
        int x2 = locX - (int)hollowRad;
        while ((float)x2 <= (float)locX + hollowRad) {
            int z = locZ - (int)hollowRad;
            while ((float)z <= (float)locZ + hollowRad) {
                int y = sphere ? locY - (int)hollowRad : locY;
                while (true) {
                    float f = y;
                    float f3 = sphere ? (float)locY + hollowRad : (float)locY + hollowHeight;
                    if (!(f < f3)) break;
                    double dist = (locX - x2) * (locX - x2) + (locZ - z) * (locZ - z) + (sphere ? (locY - y) * (locY - y) : 0);
                    if (dist < (double)(hollowRad * hollowRad)) {
                        BlockPos l = new BlockPos(x2, y + plus_y, z);
                        remove.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x2;
        }
        circleBlocks.removeAll(remove);
        return circleBlocks;
    }

    public static List<BlockPos> getSquare(BlockPos pos1, BlockPos pos2) {
        ArrayList<BlockPos> squareBlocks = new ArrayList<BlockPos>();
        int x1 = pos1.func_177958_n();
        int y1 = pos1.func_177956_o();
        int z1 = pos1.func_177952_p();
        int x2 = pos2.func_177958_n();
        int y2 = pos2.func_177956_o();
        int z2 = pos2.func_177952_p();
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); ++x) {
            for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); ++z) {
                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); ++y) {
                    squareBlocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return squareBlocks;
    }

    public static double[] calculateLookAt(double px, double py, double pz, Entity me) {
        double dirx = me.field_70165_t - px;
        double diry = me.field_70163_u - py;
        double dirz = me.field_70161_v - pz;
        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        double pitch = Math.asin(diry /= len);
        double yaw = Math.atan2(dirz /= len, dirx /= len);
        pitch = pitch * 180.0 / Math.PI;
        yaw = yaw * 180.0 / Math.PI;
        return new double[]{yaw += 90.0, pitch};
    }

    public static boolean basicChecksEntity(Entity pl) {
        return pl.func_70005_c_().equals(EntityUtil.mc.field_71439_g.func_70005_c_()) || SocialManager.isFriend(pl.func_70005_c_()) || pl.field_70128_L || pl.func_70005_c_().length() == 0;
    }

    public static BlockPos getPosition(Entity pl) {
        return new BlockPos(Math.floor(pl.field_70165_t), Math.floor(pl.field_70163_u), Math.floor(pl.field_70161_v));
    }

    public static List<BlockPos> getBlocksIn(Entity pl) {
        ArrayList<BlockPos> blocks = new ArrayList<BlockPos>();
        AxisAlignedBB bb = pl.func_174813_aQ();
        for (double x = Math.floor(bb.field_72340_a); x < Math.ceil(bb.field_72336_d); x += 1.0) {
            for (double y = Math.floor(bb.field_72338_b); y < Math.ceil(bb.field_72337_e); y += 1.0) {
                for (double z = Math.floor(bb.field_72339_c); z < Math.ceil(bb.field_72334_f); z += 1.0) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return blocks;
    }
}

