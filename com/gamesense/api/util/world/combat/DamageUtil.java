/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.world.combat;

import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.combat.ac.PlayerInfo;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class DamageUtil {
    private static final Minecraft mc = Minecraft.func_71410_x();

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity, boolean ignoreTerrain) {
        float finalDamage = 1.0f;
        try {
            float doubleExplosionSize = 12.0f;
            double distancedSize = entity.func_70011_f(posX, posY, posZ) / (double)doubleExplosionSize;
            double blockDensity = ignoreTerrain ? (double)DamageUtil.ignoreTerrainDecntiy(new Vec3d(posX, posY, posZ), entity.func_174813_aQ(), (World)DamageUtil.mc.field_71441_e) : (double)entity.field_70170_p.func_72842_a(new Vec3d(posX, posY, posZ), entity.func_174813_aQ());
            double v = (1.0 - distancedSize) * blockDensity;
            float damage = (int)((v * v + v) / 2.0 * 7.0 * (double)doubleExplosionSize + 1.0);
            if (entity instanceof EntityLivingBase) {
                finalDamage = DamageUtil.getBlastReduction((EntityLivingBase)entity, DamageUtil.getDamageMultiplied(damage), new Explosion((World)DamageUtil.mc.field_71441_e, null, posX, posY, posZ, 6.0f, false, true));
            }
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
        return finalDamage;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            int k;
            EntityPlayer ep = (EntityPlayer)entity;
            DamageSource ds = DamageSource.func_94539_a((Explosion)explosion);
            damage = CombatRules.func_189427_a((float)damage, (float)ep.func_70658_aO(), (float)((float)ep.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e()));
            try {
                k = EnchantmentHelper.func_77508_a((Iterable)ep.func_184193_aE(), (DamageSource)ds);
            }
            catch (NullPointerException e) {
                k = 0;
            }
            float f = MathHelper.func_76131_a((float)k, (float)0.0f, (float)20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.func_70644_a(Potion.func_188412_a((int)11))) {
                damage -= damage / 4.0f;
            }
            damage = Math.max(damage, 0.0f);
            return damage;
        }
        damage = CombatRules.func_189427_a((float)damage, (float)entity.func_70658_aO(), (float)((float)entity.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e()));
        return damage;
    }

    public static float calculateDamageThreaded(double posX, double posY, double posZ, PlayerInfo playerInfo2, boolean ignoreTerrain) {
        float finalDamage = 1.0f;
        try {
            float doubleExplosionSize = 12.0f;
            double distancedSize = playerInfo2.entity.func_70011_f(posX, posY, posZ) / (double)doubleExplosionSize;
            double blockDensity = ignoreTerrain ? (double)DamageUtil.ignoreTerrainDecntiy(new Vec3d(posX, posY, posZ), playerInfo2.entity.func_174813_aQ(), playerInfo2.entity.field_70170_p) : (double)playerInfo2.entity.field_70170_p.func_72842_a(new Vec3d(posX, posY, posZ), playerInfo2.entity.func_174813_aQ());
            double v = (1.0 - distancedSize) * blockDensity;
            float damage = (int)((v * v + v) / 2.0 * 7.0 * (double)doubleExplosionSize + 1.0);
            finalDamage = DamageUtil.getBlastReductionThreaded(playerInfo2, DamageUtil.getDamageMultiplied(damage));
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
        return finalDamage;
    }

    public static float ignoreTerrainDecntiy(Vec3d vec, AxisAlignedBB bb, World world) {
        double d0 = 1.0 / ((bb.field_72336_d - bb.field_72340_a) * 2.0 + 1.0);
        double d1 = 1.0 / ((bb.field_72337_e - bb.field_72338_b) * 2.0 + 1.0);
        double d2 = 1.0 / ((bb.field_72334_f - bb.field_72339_c) * 2.0 + 1.0);
        double d3 = (1.0 - Math.floor(1.0 / d0) * d0) / 2.0;
        double d4 = (1.0 - Math.floor(1.0 / d2) * d2) / 2.0;
        if (d0 >= 0.0 && d1 >= 0.0 && d2 >= 0.0) {
            int j2 = 0;
            int k2 = 0;
            float f = 0.0f;
            while (f <= 1.0f) {
                float f1 = 0.0f;
                while (f1 <= 1.0f) {
                    float f2 = 0.0f;
                    while (f2 <= 1.0f) {
                        double d5 = bb.field_72340_a + (bb.field_72336_d - bb.field_72340_a) * (double)f;
                        double d6 = bb.field_72338_b + (bb.field_72337_e - bb.field_72338_b) * (double)f1;
                        double d7 = bb.field_72339_c + (bb.field_72334_f - bb.field_72339_c) * (double)f2;
                        RayTraceResult result = world.func_72933_a(new Vec3d(d5 + d3, d6, d7 + d4), vec);
                        if (result == null) {
                            ++j2;
                        } else {
                            Block blockHit = BlockUtil.getBlock(result.func_178782_a());
                            if (blockHit.field_149781_w < 600.0f) {
                                ++j2;
                            }
                        }
                        ++k2;
                        f2 = (float)((double)f2 + d2);
                    }
                    f1 = (float)((double)f1 + d1);
                }
                f = (float)((double)f + d0);
            }
            return (float)j2 / (float)k2;
        }
        return 0.0f;
    }

    public static float getBlastReductionThreaded(PlayerInfo playerInfo2, float damage) {
        damage = CombatRules.func_189427_a((float)damage, (float)playerInfo2.totalArmourValue, (float)playerInfo2.armourToughness);
        float f = MathHelper.func_76131_a((float)playerInfo2.enchantModifier, (float)0.0f, (float)20.0f);
        damage *= 1.0f - f / 25.0f;
        if (playerInfo2.hasResistance) {
            damage -= damage / 4.0f;
        }
        damage = Math.max(damage, 0.0f);
        return damage;
    }

    private static float getDamageMultiplied(float damage) {
        int diff = DamageUtil.mc.field_71441_e.func_175659_aa().func_151525_a();
        return damage * (diff == 0 ? 0.0f : (diff == 2 ? 1.0f : (diff == 1 ? 0.5f : 1.5f)));
    }
}

