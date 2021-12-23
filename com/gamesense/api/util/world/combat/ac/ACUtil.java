/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.world.combat.ac;

import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.api.util.world.combat.ac.ACSettings;
import com.gamesense.api.util.world.combat.ac.CrystalInfo;
import com.gamesense.api.util.world.combat.ac.PlayerInfo;
import java.util.List;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.BlockPos;

public class ACUtil {
    public static CrystalInfo.PlaceInfo calculateBestPlacement(ACSettings settings, PlayerInfo target, List<BlockPos> possibleLocations) {
        double x = settings.playerPos.field_72450_a;
        double y = settings.playerPos.field_72448_b;
        double z = settings.playerPos.field_72449_c;
        BlockPos best = null;
        float bestDamage = 0.0f;
        for (BlockPos crystal : possibleLocations) {
            if (!(target.entity.func_70092_e((double)crystal.func_177958_n() + 0.5, (double)crystal.func_177956_o() + 1.0, (double)crystal.func_177952_p() + 0.5) <= settings.enemyRangeSq)) continue;
            float currentDamage = DamageUtil.calculateDamageThreaded((double)crystal.func_177958_n() + 0.5, (double)crystal.func_177956_o() + 1.0, (double)crystal.func_177952_p() + 0.5, target, false);
            if (currentDamage == bestDamage) {
                if (best != null && !(crystal.func_177954_c(x, y, z) < best.func_177954_c(x, y, z))) continue;
                bestDamage = currentDamage;
                best = crystal;
                continue;
            }
            if (!(currentDamage > bestDamage)) continue;
            bestDamage = currentDamage;
            best = crystal;
        }
        if (best != null && (bestDamage >= settings.minDamage || (target.health <= settings.facePlaceHealth || target.lowArmour) && bestDamage >= settings.minFacePlaceDamage)) {
            return new CrystalInfo.PlaceInfo(bestDamage, target, best, 0.0);
        }
        return null;
    }

    public static CrystalInfo.BreakInfo calculateBestBreakable(ACSettings settings, PlayerInfo target, List<EntityEnderCrystal> crystals) {
        double x = settings.playerPos.field_72450_a;
        double y = settings.playerPos.field_72448_b;
        double z = settings.playerPos.field_72449_c;
        boolean smart = settings.breakMode.equalsIgnoreCase("Smart");
        EntityEnderCrystal best = null;
        float bestDamage = 0.0f;
        for (EntityEnderCrystal crystal : crystals) {
            float currentDamage = DamageUtil.calculateDamageThreaded(crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v, target, false);
            if (currentDamage == bestDamage) {
                if (best != null && !(crystal.func_70092_e(x, y, z) < best.func_70092_e(x, y, z))) continue;
                bestDamage = currentDamage;
                best = crystal;
                continue;
            }
            if (!(currentDamage > bestDamage)) continue;
            bestDamage = currentDamage;
            best = crystal;
        }
        if (best != null) {
            boolean shouldAdd = false;
            if (smart) {
                if ((double)bestDamage >= (double)settings.minBreakDamage || (target.health <= settings.facePlaceHealth || target.lowArmour) && bestDamage > settings.minFacePlaceDamage) {
                    shouldAdd = true;
                }
            } else {
                shouldAdd = true;
            }
            if (shouldAdd) {
                return new CrystalInfo.BreakInfo(bestDamage, target, best);
            }
        }
        return null;
    }
}

