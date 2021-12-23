/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.player;

import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PredictUtil {
    static final Minecraft mc = Minecraft.func_71410_x();

    public static EntityPlayer predictPlayer(EntityPlayer entity, PredictSettings settings) {
        double[] posVec = new double[]{entity.field_70165_t, entity.field_70163_u, entity.field_70161_v};
        double[] newPosVec = (double[])posVec.clone();
        double motionX = entity.field_70165_t - entity.field_70169_q;
        double motionY = entity.field_70163_u - entity.field_70167_r;
        if (settings.debug) {
            PistonCrystal.printDebug("Motion Y:" + motionY, false);
        }
        double motionZ = entity.field_70161_v - entity.field_70166_s;
        boolean goingUp = false;
        boolean start = true;
        int up = 0;
        int down = 0;
        if (settings.debug) {
            PistonCrystal.printDebug(String.format("Values: %f %f %f", newPosVec[0], newPosVec[1], newPosVec[2]), false);
        }
        boolean isHole = false;
        if (settings.manualOutHole && motionY > 0.2) {
            if (HoleUtil.isHole(EntityUtil.getPosition((Entity)entity), false, true).getType() != HoleUtil.HoleType.NONE && BlockUtil.getBlock(EntityUtil.getPosition((Entity)entity).func_177982_a(0, 2, 0)) instanceof BlockAir) {
                isHole = true;
            } else if (settings.aboveHoleManual && HoleUtil.isHole(EntityUtil.getPosition((Entity)entity).func_177982_a(0, -1, 0), false, true).getType() != HoleUtil.HoleType.NONE) {
                isHole = true;
            }
            if (isHole) {
                posVec[1] = posVec[1] + 1.0;
            }
        }
        boolean allowPredictStair = false;
        int stairPredicted = 0;
        if (settings.stairPredict) {
            boolean bl = allowPredictStair = Math.abs(entity.field_70165_t - entity.field_70169_q) + Math.abs(entity.field_70161_v - entity.field_70166_s) > settings.speedActivationStairs;
            if (settings.debug) {
                PistonCrystal.printDebug(String.format("Speed: %.2f Activation speed Stairs: %.2f", Math.abs(entity.field_70165_t - entity.field_70169_q) + Math.abs(entity.field_70161_v - entity.field_70166_s), settings.speedActivationStairs), false);
            }
        }
        for (int i = 0; i < settings.tick; ++i) {
            RayTraceResult result;
            if (settings.splitXZ) {
                newPosVec = (double[])posVec.clone();
                newPosVec[0] = newPosVec[0] + motionX;
                result = PredictUtil.mc.field_71441_e.func_72933_a(new Vec3d(posVec[0], posVec[1], posVec[2]), new Vec3d(newPosVec[0], posVec[1], posVec[2]));
                boolean predictedStair = false;
                if (result == null || result.field_72313_a == RayTraceResult.Type.ENTITY) {
                    posVec = (double[])newPosVec.clone();
                } else if (settings.stairPredict && allowPredictStair && BlockUtil.getBlock(newPosVec[0], newPosVec[1] + 1.0, newPosVec[2]) instanceof BlockAir && stairPredicted++ < settings.nStairs) {
                    posVec[1] = posVec[1] + 1.0;
                    predictedStair = true;
                }
                newPosVec = (double[])posVec.clone();
                newPosVec[2] = newPosVec[2] + motionZ;
                result = PredictUtil.mc.field_71441_e.func_72933_a(new Vec3d(posVec[0], posVec[1], posVec[2]), new Vec3d(newPosVec[0], posVec[1], newPosVec[2]));
                if (result == null || result.field_72313_a == RayTraceResult.Type.ENTITY) {
                    posVec = (double[])newPosVec.clone();
                } else if (settings.stairPredict && allowPredictStair && !predictedStair && BlockUtil.getBlock(newPosVec[0], newPosVec[1] + 1.0, newPosVec[2]) instanceof BlockAir && stairPredicted++ < settings.nStairs) {
                    posVec[1] = posVec[1] + 1.0;
                }
            } else {
                newPosVec = (double[])posVec.clone();
                newPosVec[0] = newPosVec[0] + motionX;
                newPosVec[2] = newPosVec[2] + motionZ;
                result = PredictUtil.mc.field_71441_e.func_72933_a(new Vec3d(posVec[0], posVec[1], posVec[2]), new Vec3d(newPosVec[0], posVec[1], newPosVec[2]));
                if (result == null || result.field_72313_a == RayTraceResult.Type.ENTITY) {
                    posVec = (double[])newPosVec.clone();
                } else if (settings.stairPredict && allowPredictStair && BlockUtil.getBlock(newPosVec[0], newPosVec[1] + 1.0, newPosVec[2]) instanceof BlockAir && stairPredicted++ < settings.nStairs) {
                    posVec[1] = posVec[1] + 1.0;
                }
            }
            if (settings.calculateY && !isHole) {
                newPosVec = (double[])posVec.clone();
                if (!entity.field_70122_E && motionY != -0.0784000015258789 && motionY != 0.0) {
                    double decreasePow = (double)settings.startDecrease / Math.pow(10.0, settings.exponentStartDecrease);
                    if (start) {
                        if (motionY == 0.0) {
                            motionY = decreasePow;
                        }
                        goingUp = false;
                        start = false;
                        if (settings.debug) {
                            PistonCrystal.printDebug("Start motionY: " + motionY, false);
                        }
                    }
                    double increasePowY = (double)settings.increaseY / Math.pow(10.0, settings.exponentIncreaseY);
                    double decreasePowY = (double)settings.decreaseY / Math.pow(10.0, settings.exponentDecreaseY);
                    if (Math.abs(motionY += goingUp ? increasePowY : decreasePowY) > decreasePow) {
                        goingUp = false;
                        if (settings.debug) {
                            ++up;
                        }
                        motionY = decreasePowY;
                    }
                    newPosVec[1] = newPosVec[1] + (double)(goingUp ? 1 : -1) * motionY;
                    result = PredictUtil.mc.field_71441_e.func_72933_a(new Vec3d(posVec[0], posVec[1], posVec[2]), new Vec3d(newPosVec[0], newPosVec[1], newPosVec[2]));
                    if (result == null || result.field_72313_a == RayTraceResult.Type.ENTITY) {
                        posVec = (double[])newPosVec.clone();
                    } else if (!goingUp) {
                        goingUp = true;
                        newPosVec[1] = newPosVec[1] + increasePowY;
                        motionY = increasePowY;
                        newPosVec[1] = newPosVec[1] + motionY;
                        if (settings.debug) {
                            ++down;
                        }
                    }
                }
            }
            if (!settings.show) continue;
            PistonCrystal.printDebug(String.format("Values: %f %f %f", posVec[0], posVec[1], posVec[2]), false);
        }
        if (settings.debug) {
            PistonCrystal.printDebug(String.format("Player: %s Total ticks: %d Up: %d Down: %d", entity.func_146103_bH().getName(), settings.tick, up, down), false);
        }
        EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP((World)PredictUtil.mc.field_71441_e, new GameProfile(UUID.fromString("fdee323e-7f0c-4c15-8d1c-0f277442342a"), entity.func_70005_c_()));
        clonedPlayer.func_70107_b(posVec[0], posVec[1], posVec[2]);
        clonedPlayer.field_71071_by.func_70455_b(entity.field_71071_by);
        clonedPlayer.func_70606_j(entity.func_110143_aJ());
        clonedPlayer.field_70169_q = entity.field_70169_q;
        clonedPlayer.field_70167_r = entity.field_70167_r;
        clonedPlayer.field_70166_s = entity.field_70166_s;
        for (PotionEffect effect : entity.func_70651_bq()) {
            clonedPlayer.func_70690_d(effect);
        }
        return clonedPlayer;
    }

    public static class PredictSettings {
        final int tick;
        final boolean calculateY;
        final int startDecrease;
        final int exponentStartDecrease;
        final int decreaseY;
        final int exponentDecreaseY;
        final int increaseY;
        final int exponentIncreaseY;
        final boolean splitXZ;
        final int width;
        final boolean debug;
        final boolean show;
        final boolean manualOutHole;
        final boolean aboveHoleManual;
        final boolean stairPredict;
        final int nStairs;
        final double speedActivationStairs;

        public PredictSettings(int tick, boolean calculateY, int startDecrease, int exponentStartDecrease, int decreaseY, int exponentDecreaseY, int increaseY, int exponentIncreaseY, boolean splitXZ, int width, boolean debug, boolean show, boolean manualOutHole, boolean aboveHoleManual, boolean stairPredict, int nStairs, double speedActivationStairs) {
            this.tick = tick;
            this.calculateY = calculateY;
            this.startDecrease = startDecrease;
            this.exponentStartDecrease = exponentStartDecrease;
            this.decreaseY = decreaseY;
            this.exponentDecreaseY = exponentDecreaseY;
            this.increaseY = increaseY;
            this.exponentIncreaseY = exponentIncreaseY;
            this.splitXZ = splitXZ;
            this.width = width;
            this.debug = debug;
            this.show = show;
            this.manualOutHole = manualOutHole;
            this.aboveHoleManual = aboveHoleManual;
            this.stairPredict = stairPredict;
            this.nStairs = nStairs;
            this.speedActivationStairs = speedActivationStairs;
        }
    }
}

