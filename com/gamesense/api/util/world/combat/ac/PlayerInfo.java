/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.world.combat.ac;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;

public class PlayerInfo {
    private static final Potion RESISTANCE = Potion.func_188412_a((int)11);
    private static final DamageSource EXPLOSION_SOURCE = new DamageSource("explosion").func_76351_m().func_94540_d();
    public final EntityPlayer entity;
    public final float totalArmourValue;
    public final float armourToughness;
    public final float health;
    public final int enchantModifier;
    public final boolean hasResistance;
    public final boolean lowArmour;

    public PlayerInfo(EntityPlayer entity, float armorPercent) {
        int enchantModifier1;
        this.entity = entity;
        this.totalArmourValue = entity.func_70658_aO();
        this.armourToughness = (float)entity.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e();
        this.health = entity.func_110143_aJ() + entity.func_110139_bj();
        try {
            enchantModifier1 = EnchantmentHelper.func_77508_a((Iterable)entity.func_184193_aE(), (DamageSource)EXPLOSION_SOURCE);
        }
        catch (NullPointerException e) {
            enchantModifier1 = 0;
        }
        this.enchantModifier = enchantModifier1;
        this.hasResistance = entity.func_70644_a(RESISTANCE);
        boolean i = false;
        for (ItemStack stack : entity.func_184193_aE()) {
            if (!(1.0f - (float)stack.func_77952_i() / (float)stack.func_77958_k() < armorPercent)) continue;
            i = true;
            break;
        }
        this.lowArmour = i;
    }

    public PlayerInfo(EntityPlayer entity, float armorPercent, float totalArmourValue, float armourToughness) {
        this.entity = entity;
        this.totalArmourValue = entity.func_70658_aO();
        this.armourToughness = (float)entity.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e();
        this.health = entity.func_110143_aJ() + entity.func_110139_bj();
        this.enchantModifier = EnchantmentHelper.func_77508_a((Iterable)entity.func_184193_aE(), (DamageSource)EXPLOSION_SOURCE);
        this.hasResistance = entity.func_70644_a(RESISTANCE);
        boolean i = false;
        for (ItemStack stack : entity.func_184193_aE()) {
            if (!(1.0f - (float)stack.func_77952_i() / (float)stack.func_77958_k() < armorPercent)) continue;
            i = true;
            break;
        }
        this.lowArmour = i;
    }

    public PlayerInfo(EntityPlayer entity, boolean lowArmour, float totalArmourValue, float armourToughness) {
        int enchantModifier1;
        this.entity = entity;
        this.totalArmourValue = totalArmourValue;
        this.armourToughness = armourToughness;
        this.health = entity.func_110143_aJ() + entity.func_110139_bj();
        try {
            enchantModifier1 = EnchantmentHelper.func_77508_a((Iterable)entity.func_184193_aE(), (DamageSource)EXPLOSION_SOURCE);
        }
        catch (NullPointerException e) {
            enchantModifier1 = 0;
        }
        this.enchantModifier = enchantModifier1;
        this.hasResistance = entity.func_70644_a(RESISTANCE);
        this.lowArmour = lowArmour;
    }
}

