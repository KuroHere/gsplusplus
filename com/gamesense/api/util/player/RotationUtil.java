/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class RotationUtil {
    private static final Minecraft mc = Minecraft.func_71410_x();

    public static Vec2f getRotationTo(AxisAlignedBB box) {
        EntityPlayerSP player = RotationUtil.mc.field_71439_g;
        if (player == null) {
            return Vec2f.field_189974_a;
        }
        Vec3d eyePos = player.func_174824_e(1.0f);
        if (player.func_174813_aQ().func_72326_a(box)) {
            return RotationUtil.getRotationTo(eyePos, box.func_189972_c());
        }
        double x = MathHelper.func_151237_a((double)eyePos.field_72450_a, (double)box.field_72340_a, (double)box.field_72336_d);
        double y = MathHelper.func_151237_a((double)eyePos.field_72448_b, (double)box.field_72338_b, (double)box.field_72337_e);
        double z = MathHelper.func_151237_a((double)eyePos.field_72449_c, (double)box.field_72339_c, (double)box.field_72334_f);
        return RotationUtil.getRotationTo(eyePos, new Vec3d(x, y, z));
    }

    public static Vec2f getRotationTo(Vec3d posTo) {
        EntityPlayerSP player = RotationUtil.mc.field_71439_g;
        return player != null ? RotationUtil.getRotationTo(player.func_174824_e(1.0f), posTo) : Vec2f.field_189974_a;
    }

    public static Vec2f getRotationTo(Vec3d posFrom, Vec3d posTo) {
        return RotationUtil.getRotationFromVec(posTo.func_178788_d(posFrom));
    }

    public static Vec2f getRotationFromVec(Vec3d vec) {
        double lengthXZ = Math.hypot(vec.field_72450_a, vec.field_72449_c);
        double yaw = RotationUtil.normalizeAngle(Math.toDegrees(Math.atan2(vec.field_72449_c, vec.field_72450_a)) - 90.0);
        double pitch = RotationUtil.normalizeAngle(Math.toDegrees(-Math.atan2(vec.field_72448_b, lengthXZ)));
        return new Vec2f((float)yaw, (float)pitch);
    }

    public static double normalizeAngle(double angle) {
        if ((angle %= 360.0) >= 180.0) {
            angle -= 360.0;
        }
        if (angle < -180.0) {
            angle += 360.0;
        }
        return angle;
    }

    public static float normalizeAngle(float angle) {
        if ((angle %= 360.0f) >= 180.0f) {
            angle -= 360.0f;
        }
        if (angle < -180.0f) {
            angle += 360.0f;
        }
        return angle;
    }
}

