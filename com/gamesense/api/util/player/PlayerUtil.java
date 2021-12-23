/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.player;

import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.EntityUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PlayerUtil {
    private static final Minecraft mc = Minecraft.func_71410_x();

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(PlayerUtil.mc.field_71439_g.field_70165_t), Math.floor(PlayerUtil.mc.field_71439_g.field_70163_u), Math.floor(PlayerUtil.mc.field_71439_g.field_70161_v));
    }

    public static boolean nullCheck() {
        return PlayerUtil.mc.field_71441_e != null && PlayerUtil.mc.field_71439_g != null;
    }

    public static boolean hungry() {
        return PlayerUtil.mc.field_71439_g.field_71100_bB.func_75116_a() > 6;
    }

    public static EntityPlayer findClosestTarget(double rangeMax, EntityPlayer aimTarget) {
        return PlayerUtil.findClosestTarget(rangeMax, aimTarget, false);
    }

    public static EntityPlayer findClosestTarget(double rangeMax, EntityPlayer aimTarget, boolean moving) {
        rangeMax *= rangeMax;
        List playerList = PlayerUtil.mc.field_71441_e.field_73010_i;
        EntityPlayer closestTarget = null;
        for (EntityPlayer entityPlayer : playerList) {
            if (EntityUtil.basicChecksEntity((Entity)entityPlayer) || entityPlayer.field_70159_w + PlayerUtil.mc.field_71439_g.field_70179_y == 0.0 && moving) continue;
            if (aimTarget == null && PlayerUtil.mc.field_71439_g.func_70068_e((Entity)entityPlayer) <= rangeMax) {
                closestTarget = entityPlayer;
                continue;
            }
            if (aimTarget == null || !(PlayerUtil.mc.field_71439_g.func_70068_e((Entity)entityPlayer) <= rangeMax) || !(PlayerUtil.mc.field_71439_g.func_70068_e((Entity)entityPlayer) < PlayerUtil.mc.field_71439_g.func_70068_e((Entity)aimTarget))) continue;
            closestTarget = entityPlayer;
        }
        return closestTarget;
    }

    public static EntityPlayer findClosestTarget() {
        List playerList = PlayerUtil.mc.field_71441_e.field_73010_i;
        EntityPlayer closestTarget = null;
        for (EntityPlayer entityPlayer : playerList) {
            if (EntityUtil.basicChecksEntity((Entity)entityPlayer)) continue;
            if (closestTarget == null) {
                closestTarget = entityPlayer;
                continue;
            }
            if (!(PlayerUtil.mc.field_71439_g.func_70068_e((Entity)entityPlayer) < PlayerUtil.mc.field_71439_g.func_70068_e((Entity)closestTarget))) continue;
            closestTarget = entityPlayer;
        }
        return closestTarget;
    }

    public static boolean isPlayerClipped() {
        return !PlayerUtil.mc.field_71441_e.func_184144_a((Entity)PlayerUtil.mc.field_71439_g, PlayerUtil.mc.field_71439_g.func_174813_aQ()).isEmpty();
    }

    public static EntityPlayer findLookingPlayer(double rangeMax) {
        ArrayList<EntityPlayer> listPlayer = new ArrayList<EntityPlayer>();
        for (EntityPlayer playerSin : PlayerUtil.mc.field_71441_e.field_73010_i) {
            if (EntityUtil.basicChecksEntity((Entity)playerSin) || !((double)PlayerUtil.mc.field_71439_g.func_70032_d((Entity)playerSin) <= rangeMax)) continue;
            listPlayer.add(playerSin);
        }
        EntityPlayer target = null;
        Vec3d positionEyes = PlayerUtil.mc.field_71439_g.func_174824_e(mc.func_184121_ak());
        Vec3d rotationEyes = PlayerUtil.mc.field_71439_g.func_70676_i(mc.func_184121_ak());
        int precision = 2;
        for (int i = 0; i < (int)rangeMax; ++i) {
            for (int j = precision; j > 0; --j) {
                for (EntityPlayer targetTemp : listPlayer) {
                    AxisAlignedBB playerBox = targetTemp.func_174813_aQ();
                    double xArray = positionEyes.field_72450_a + rotationEyes.field_72450_a * (double)i + rotationEyes.field_72450_a / (double)j;
                    double yArray = positionEyes.field_72448_b + rotationEyes.field_72448_b * (double)i + rotationEyes.field_72448_b / (double)j;
                    double zArray = positionEyes.field_72449_c + rotationEyes.field_72449_c * (double)i + rotationEyes.field_72449_c / (double)j;
                    if (!(playerBox.field_72337_e >= yArray) || !(playerBox.field_72338_b <= yArray) || !(playerBox.field_72336_d >= xArray) || !(playerBox.field_72340_a <= xArray) || !(playerBox.field_72334_f >= zArray) || !(playerBox.field_72339_c <= zArray)) continue;
                    target = targetTemp;
                }
            }
        }
        return target;
    }

    public static void fakeJump() {
        PlayerUtil.fakeJump(5);
    }

    public static void fakeJump(int packets) {
        if (packets > 0 && packets != 5) {
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t, PlayerUtil.mc.field_71439_g.field_70163_u, PlayerUtil.mc.field_71439_g.field_70161_v, true));
        }
        if (packets > 1) {
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t, PlayerUtil.mc.field_71439_g.field_70163_u + 0.419999986887, PlayerUtil.mc.field_71439_g.field_70161_v, true));
        }
        if (packets > 2) {
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t, PlayerUtil.mc.field_71439_g.field_70163_u + 0.7531999805212, PlayerUtil.mc.field_71439_g.field_70161_v, true));
        }
        if (packets > 3) {
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t, PlayerUtil.mc.field_71439_g.field_70163_u + 1.0013359791121, PlayerUtil.mc.field_71439_g.field_70161_v, true));
        }
        if (packets > 4) {
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t, PlayerUtil.mc.field_71439_g.field_70163_u + 1.1661092609382, PlayerUtil.mc.field_71439_g.field_70161_v, true));
        }
    }

    public static void fall(int distance) {
        if (distance >= 1) {
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t + PlayerUtil.mc.field_71439_g.field_70159_w, PlayerUtil.mc.field_71439_g.field_70163_u - 0.3235291238557352, PlayerUtil.mc.field_71439_g.field_70161_v + PlayerUtil.mc.field_71439_g.field_70179_y, true));
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t + PlayerUtil.mc.field_71439_g.field_70159_w, PlayerUtil.mc.field_71439_g.field_70163_u - 0.8378585789730266, PlayerUtil.mc.field_71439_g.field_70161_v + PlayerUtil.mc.field_71439_g.field_70179_y, true));
        }
        if (distance >= 2) {
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t + PlayerUtil.mc.field_71439_g.field_70159_w, PlayerUtil.mc.field_71439_g.field_70163_u - 1.420301456323898, PlayerUtil.mc.field_71439_g.field_70161_v + PlayerUtil.mc.field_71439_g.field_70179_y, true));
        }
        if (distance >= 3) {
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t + PlayerUtil.mc.field_71439_g.field_70159_w, PlayerUtil.mc.field_71439_g.field_70163_u - 2.06949548876284, PlayerUtil.mc.field_71439_g.field_70161_v + PlayerUtil.mc.field_71439_g.field_70179_y, true));
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t + PlayerUtil.mc.field_71439_g.field_70159_w, PlayerUtil.mc.field_71439_g.field_70163_u - 2.7841056544612854, PlayerUtil.mc.field_71439_g.field_70161_v + PlayerUtil.mc.field_71439_g.field_70179_y, true));
        }
        if (distance >= 4) {
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t + PlayerUtil.mc.field_71439_g.field_70159_w, PlayerUtil.mc.field_71439_g.field_70163_u - 3.562823632001738, PlayerUtil.mc.field_71439_g.field_70161_v + PlayerUtil.mc.field_71439_g.field_70179_y, true));
        }
        if (distance >= 5) {
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t + PlayerUtil.mc.field_71439_g.field_70159_w, PlayerUtil.mc.field_71439_g.field_70163_u - 4.404367266370144, PlayerUtil.mc.field_71439_g.field_70161_v + PlayerUtil.mc.field_71439_g.field_70179_y, true));
        }
        if (distance >= 6) {
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t + PlayerUtil.mc.field_71439_g.field_70159_w, PlayerUtil.mc.field_71439_g.field_70163_u - 5.3074800456282105, PlayerUtil.mc.field_71439_g.field_70161_v + PlayerUtil.mc.field_71439_g.field_70179_y, true));
        }
        if (distance >= 7) {
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t + PlayerUtil.mc.field_71439_g.field_70159_w, PlayerUtil.mc.field_71439_g.field_70163_u - 6.270930588052522, PlayerUtil.mc.field_71439_g.field_70161_v + PlayerUtil.mc.field_71439_g.field_70179_y, true));
        }
        if (distance >= 8) {
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t + PlayerUtil.mc.field_71439_g.field_70159_w, PlayerUtil.mc.field_71439_g.field_70163_u - 7.293512139530577, PlayerUtil.mc.field_71439_g.field_70161_v + PlayerUtil.mc.field_71439_g.field_70179_y, true));
        }
        if (distance >= 9) {
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t + PlayerUtil.mc.field_71439_g.field_70159_w, PlayerUtil.mc.field_71439_g.field_70163_u - 8.37404208100915, PlayerUtil.mc.field_71439_g.field_70161_v + PlayerUtil.mc.field_71439_g.field_70179_y, true));
        }
        if (distance >= 10) {
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t + PlayerUtil.mc.field_71439_g.field_70159_w, PlayerUtil.mc.field_71439_g.field_70163_u - 9.511361445793511, PlayerUtil.mc.field_71439_g.field_70161_v + PlayerUtil.mc.field_71439_g.field_70179_y, true));
        }
        if (distance >= 11) {
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(PlayerUtil.mc.field_71439_g.field_70165_t + PlayerUtil.mc.field_71439_g.field_70159_w, PlayerUtil.mc.field_71439_g.field_70163_u - 10.704334446500695, PlayerUtil.mc.field_71439_g.field_70161_v + PlayerUtil.mc.field_71439_g.field_70179_y, true));
        }
        PlayerUtil.mc.field_71439_g.func_70107_b(PlayerUtil.mc.field_71439_g.field_70165_t + PlayerUtil.mc.field_71439_g.field_70159_w, PlayerUtil.mc.field_71439_g.field_70163_u - (double)distance, PlayerUtil.mc.field_71439_g.field_70161_v + PlayerUtil.mc.field_71439_g.field_70179_y);
    }

    public static float getHealth() {
        return PlayerUtil.mc.field_71439_g.func_110143_aJ() + PlayerUtil.mc.field_71439_g.func_110139_bj();
    }

    public static void centerPlayer(Vec3d centeredBlock) {
        double xDeviation = Math.abs(centeredBlock.field_72450_a - PlayerUtil.mc.field_71439_g.field_70165_t);
        double zDeviation = Math.abs(centeredBlock.field_72449_c - PlayerUtil.mc.field_71439_g.field_70161_v);
        if (xDeviation <= 0.1 && zDeviation <= 0.1) {
            int zRel;
            double newX = -2.0;
            double newZ = -2.0;
            int xRel = PlayerUtil.mc.field_71439_g.field_70165_t < 0.0 ? -1 : 1;
            int n = zRel = PlayerUtil.mc.field_71439_g.field_70161_v < 0.0 ? -1 : 1;
            if (BlockUtil.getBlock(PlayerUtil.mc.field_71439_g.field_70165_t, PlayerUtil.mc.field_71439_g.field_70163_u - 1.0, PlayerUtil.mc.field_71439_g.field_70161_v) instanceof BlockAir) {
                if (Math.abs(PlayerUtil.mc.field_71439_g.field_70165_t % 1.0) * 100.0 <= 30.0) {
                    newX = (double)Math.round(PlayerUtil.mc.field_71439_g.field_70165_t - 0.3 * (double)xRel) + 0.5 * (double)(-xRel);
                } else if (Math.abs(PlayerUtil.mc.field_71439_g.field_70165_t % 1.0) * 100.0 >= 70.0) {
                    newX = (double)Math.round(PlayerUtil.mc.field_71439_g.field_70165_t + 0.3 * (double)xRel) - 0.5 * (double)(-xRel);
                }
                if (Math.abs(PlayerUtil.mc.field_71439_g.field_70161_v % 1.0) * 100.0 <= 30.0) {
                    newZ = (double)Math.round(PlayerUtil.mc.field_71439_g.field_70161_v - 0.3 * (double)zRel) + 0.5 * (double)(-zRel);
                } else if (Math.abs(PlayerUtil.mc.field_71439_g.field_70161_v % 1.0) * 100.0 >= 70.0) {
                    newZ = (double)Math.round(PlayerUtil.mc.field_71439_g.field_70161_v + 0.3 * (double)zRel) - 0.5 * (double)(-zRel);
                }
            }
            if (newX == -2.0) {
                newX = PlayerUtil.mc.field_71439_g.field_70165_t > (double)Math.round(PlayerUtil.mc.field_71439_g.field_70165_t) ? (double)Math.round(PlayerUtil.mc.field_71439_g.field_70165_t) + 0.5 : (PlayerUtil.mc.field_71439_g.field_70165_t < (double)Math.round(PlayerUtil.mc.field_71439_g.field_70165_t) ? (double)Math.round(PlayerUtil.mc.field_71439_g.field_70165_t) - 0.5 : PlayerUtil.mc.field_71439_g.field_70165_t);
            }
            if (newZ == -2.0) {
                newZ = PlayerUtil.mc.field_71439_g.field_70161_v > (double)Math.round(PlayerUtil.mc.field_71439_g.field_70161_v) ? (double)Math.round(PlayerUtil.mc.field_71439_g.field_70161_v) + 0.5 : (PlayerUtil.mc.field_71439_g.field_70161_v < (double)Math.round(PlayerUtil.mc.field_71439_g.field_70161_v) ? (double)Math.round(PlayerUtil.mc.field_71439_g.field_70161_v) - 0.5 : PlayerUtil.mc.field_71439_g.field_70161_v);
            }
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(newX, PlayerUtil.mc.field_71439_g.field_70163_u, newZ, true));
            PlayerUtil.mc.field_71439_g.func_70107_b(newX, PlayerUtil.mc.field_71439_g.field_70163_u, newZ);
        }
    }

    public static void centerPlayer(Vec3d centeredBlock, double diffX, double diffZ) {
        double xDeviation = Math.abs(centeredBlock.field_72450_a - PlayerUtil.mc.field_71439_g.field_70165_t);
        double zDeviation = Math.abs(centeredBlock.field_72449_c - PlayerUtil.mc.field_71439_g.field_70161_v);
        if (xDeviation <= 0.1 && zDeviation <= 0.1) {
            int zRel;
            double newX = -2.0;
            double newZ = -2.0;
            int xRel = PlayerUtil.mc.field_71439_g.field_70165_t < 0.0 ? -1 : 1;
            int n = zRel = PlayerUtil.mc.field_71439_g.field_70161_v < 0.0 ? -1 : 1;
            if (BlockUtil.getBlock(PlayerUtil.mc.field_71439_g.field_70165_t, PlayerUtil.mc.field_71439_g.field_70163_u - 1.0, PlayerUtil.mc.field_71439_g.field_70161_v) instanceof BlockAir) {
                if (Math.abs(PlayerUtil.mc.field_71439_g.field_70165_t % 1.0) * 100.0 <= 30.0) {
                    newX = (double)Math.round(PlayerUtil.mc.field_71439_g.field_70165_t - 0.3 * (double)xRel) + 0.5 * (double)(-xRel);
                } else if (Math.abs(PlayerUtil.mc.field_71439_g.field_70165_t % 1.0) * 100.0 >= 70.0) {
                    newX = (double)Math.round(PlayerUtil.mc.field_71439_g.field_70165_t + 0.3 * (double)xRel) - 0.5 * (double)(-xRel);
                }
                if (Math.abs(PlayerUtil.mc.field_71439_g.field_70161_v % 1.0) * 100.0 <= 30.0) {
                    newZ = (double)Math.round(PlayerUtil.mc.field_71439_g.field_70161_v - 0.3 * (double)zRel) + 0.5 * (double)(-zRel);
                } else if (Math.abs(PlayerUtil.mc.field_71439_g.field_70161_v % 1.0) * 100.0 >= 70.0) {
                    newZ = (double)Math.round(PlayerUtil.mc.field_71439_g.field_70161_v + 0.3 * (double)zRel) - 0.5 * (double)(-zRel);
                }
            }
            if (newX == -2.0) {
                newX = PlayerUtil.mc.field_71439_g.field_70165_t > (double)Math.round(PlayerUtil.mc.field_71439_g.field_70165_t) ? (double)Math.round(PlayerUtil.mc.field_71439_g.field_70165_t) + 0.5 : (PlayerUtil.mc.field_71439_g.field_70165_t < (double)Math.round(PlayerUtil.mc.field_71439_g.field_70165_t) ? (double)Math.round(PlayerUtil.mc.field_71439_g.field_70165_t) - 0.5 : PlayerUtil.mc.field_71439_g.field_70165_t);
            }
            if (newZ == -2.0) {
                newZ = PlayerUtil.mc.field_71439_g.field_70161_v > (double)Math.round(PlayerUtil.mc.field_71439_g.field_70161_v) ? (double)Math.round(PlayerUtil.mc.field_71439_g.field_70161_v) + 0.5 : (PlayerUtil.mc.field_71439_g.field_70161_v < (double)Math.round(PlayerUtil.mc.field_71439_g.field_70161_v) ? (double)Math.round(PlayerUtil.mc.field_71439_g.field_70161_v) - 0.5 : PlayerUtil.mc.field_71439_g.field_70161_v);
            }
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(newX + diffX, PlayerUtil.mc.field_71439_g.field_70163_u, newZ + diffZ, true));
            PlayerUtil.mc.field_71439_g.func_70107_b(newX + diffX, PlayerUtil.mc.field_71439_g.field_70163_u, newZ + diffZ);
        }
    }
}

