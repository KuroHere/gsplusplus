/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;

@Module.Declaration(name="NoEntityTrace", category=Category.Misc)
public class NoEntityTrace
extends Module {
    BooleanSetting pickaxe = this.registerBoolean("Pickaxe", true);
    BooleanSetting gap = this.registerBoolean("Food Stuffs", false);
    BooleanSetting obsidian = this.registerBoolean("Obsidian", false);
    BooleanSetting eChest = this.registerBoolean("EnderChest", false);
    BooleanSetting block = this.registerBoolean("Blocks", false);
    BooleanSetting all = this.registerBoolean("All", false);
    boolean isHoldingPickaxe = false;
    boolean isHoldingObsidian = false;
    boolean isHoldingEChest = false;
    boolean isHoldingBlock = false;
    boolean isHoldingGap = false;

    @Override
    public void onUpdate() {
        Item item = NoEntityTrace.mc.field_71439_g.func_184614_ca().func_77973_b();
        this.isHoldingPickaxe = item instanceof ItemPickaxe;
        this.isHoldingBlock = item instanceof ItemBlock;
        boolean bl = this.isHoldingGap = item instanceof ItemFood || item instanceof ItemPotion;
        if (this.isHoldingBlock) {
            this.isHoldingObsidian = ((ItemBlock)item).func_179223_d() instanceof BlockObsidian;
            this.isHoldingEChest = ((ItemBlock)item).func_179223_d() instanceof BlockEnderChest;
        } else {
            this.isHoldingObsidian = false;
            this.isHoldingEChest = false;
        }
    }

    public boolean noTrace() {
        if (((Boolean)this.pickaxe.getValue()).booleanValue() && this.isHoldingPickaxe) {
            return this.isEnabled();
        }
        if (((Boolean)this.obsidian.getValue()).booleanValue() && this.isHoldingObsidian) {
            return this.isEnabled();
        }
        if (((Boolean)this.eChest.getValue()).booleanValue() && this.isHoldingEChest) {
            return this.isEnabled();
        }
        if (((Boolean)this.block.getValue()).booleanValue() && this.isHoldingBlock) {
            return this.isEnabled();
        }
        if (((Boolean)this.gap.getValue()).booleanValue() && this.isHoldingGap) {
            return this.isEnabled();
        }
        if (((Boolean)this.all.getValue()).booleanValue()) {
            return this.isEnabled();
        }
        return false;
    }
}

