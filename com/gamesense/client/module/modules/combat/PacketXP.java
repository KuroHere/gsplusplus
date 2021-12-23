/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;

@Module.Declaration(name="PacketXP", category=Category.Combat)
public class PacketXP
extends Module {
    public boolean pause;
    BooleanSetting sneakOnly = this.registerBoolean("Sneak Only", true);
    BooleanSetting noEntityCollision = this.registerBoolean("No Collision", true);
    BooleanSetting silentSwitch = this.registerBoolean("Silent Switch", true);
    IntegerSetting minDamage = this.registerInteger("Min Damage", 50, 1, 100);
    IntegerSetting maxHeal = this.registerInteger("Repair To", 90, 1, 100);
    BooleanSetting predict = this.registerBoolean("Predict", false);
    char toMend = '\u0000';

    @Override
    public void onUpdate() {
        if (PacketXP.mc.field_71439_g == null || PacketXP.mc.field_71441_e == null || PacketXP.mc.field_71439_g.field_70173_aa < 10) {
            return;
        }
        int sumOfDamage = 0;
        NonNullList armour = PacketXP.mc.field_71439_g.field_71071_by.field_70460_b;
        for (int i = 0; i < armour.size(); ++i) {
            ItemStack itemStack = (ItemStack)armour.get(i);
            if (itemStack.field_190928_g) continue;
            float damageOnArmor = itemStack.func_77958_k() - itemStack.func_77952_i();
            float damagePercent = 100.0f - 100.0f * (1.0f - damageOnArmor / (float)itemStack.func_77958_k());
            if (damagePercent <= (float)((Integer)this.maxHeal.getValue()).intValue()) {
                if (damagePercent <= (float)((Integer)this.minDamage.getValue()).intValue()) {
                    this.toMend = (char)(this.toMend | 1 << i);
                }
                if (!((Boolean)this.predict.getValue()).booleanValue()) continue;
                sumOfDamage = (int)((float)sumOfDamage + ((float)(itemStack.func_77958_k() * (Integer)this.maxHeal.getValue()) / 100.0f - (float)(itemStack.func_77958_k() - itemStack.func_77952_i())));
                continue;
            }
            this.toMend = (char)(this.toMend & ~(1 << i));
        }
        if (this.toMend > '\u0000') {
            this.pause = true;
            if (((Boolean)this.predict.getValue()).booleanValue()) {
                int totalXp = PacketXP.mc.field_71441_e.field_72996_f.stream().filter(entity -> entity instanceof EntityXPOrb).filter(entity -> entity.func_70068_e((Entity)PacketXP.mc.field_71439_g) <= 1.0).mapToInt(entity -> ((EntityXPOrb)entity).field_70530_e).sum();
                if (totalXp * 2 < sumOfDamage) {
                    this.mendArmor(PacketXP.mc.field_71439_g.field_71071_by.field_70461_c);
                }
            } else {
                this.mendArmor(PacketXP.mc.field_71439_g.field_71071_by.field_70461_c);
            }
        } else {
            this.disable();
        }
    }

    private void mendArmor(int oldSlot) {
        if (((Boolean)this.noEntityCollision.getValue()).booleanValue()) {
            for (EntityPlayer entityPlayer : PacketXP.mc.field_71441_e.field_73010_i) {
                if (!(entityPlayer.func_70032_d((Entity)PacketXP.mc.field_71439_g) < 1.0f) || entityPlayer == PacketXP.mc.field_71439_g) continue;
                return;
            }
        }
        if (((Boolean)this.sneakOnly.getValue()).booleanValue() && !PacketXP.mc.field_71439_g.func_70093_af()) {
            return;
        }
        int newSlot = this.findXPSlot();
        if (newSlot == -1) {
            return;
        }
        if (oldSlot != newSlot) {
            if (((Boolean)this.silentSwitch.getValue()).booleanValue()) {
                PacketXP.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(newSlot));
            } else {
                PacketXP.mc.field_71439_g.field_71071_by.field_70461_c = newSlot;
            }
            PacketXP.mc.field_71442_b.func_78750_j();
        }
        PacketXP.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(0.0f, 90.0f, true));
        PacketXP.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        if (((Boolean)this.silentSwitch.getValue()).booleanValue()) {
            PacketXP.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(oldSlot));
        } else {
            PacketXP.mc.field_71439_g.field_71071_by.field_70461_c = oldSlot;
        }
        PacketXP.mc.field_71442_b.func_78750_j();
    }

    private int findXPSlot() {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            if (PacketXP.mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() != Items.field_151062_by) continue;
            slot = i;
            break;
        }
        return slot;
    }

    void handleArmour(EntityPlayer e) {
        for (int i = 5; i <= 9; ++i) {
            if (!this.percDmg(e.field_71071_by.func_70301_a(i))) continue;
            this.rem(i);
        }
    }

    boolean percDmg(ItemStack it) {
        if (it.func_77952_i() != 0) {
            return it.func_77952_i() / it.func_77958_k() * 100 >= (Integer)this.maxHeal.getValue();
        }
        return true;
    }

    void rem(int i) {
        InventoryUtil.swap(i, i - 4);
    }
}

