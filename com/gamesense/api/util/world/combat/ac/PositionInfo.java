/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.world.combat.ac;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.BlockPos;

public class PositionInfo {
    public BlockPos pos;
    public EntityEnderCrystal crystal;
    private final double selfDamage;
    public double rapp;
    public double damage;
    public double distance;
    public double distancePlayer;

    public double getSelfDamage() {
        return this.selfDamage;
    }

    public PositionInfo(BlockPos pos, double selfDamage) {
        this.pos = pos;
        this.selfDamage = selfDamage;
    }

    public PositionInfo(EntityEnderCrystal pos, double selfDamage) {
        this.crystal = pos;
        this.selfDamage = selfDamage;
    }

    public PositionInfo() {
        this.pos = null;
        this.selfDamage = 100.0;
        this.damage = 0.0;
        this.rapp = 100.0;
        this.distancePlayer = 100.0;
    }

    public void setEnemyDamage(double damage) {
        this.damage = damage;
        this.rapp = this.damage / this.selfDamage;
    }
}

