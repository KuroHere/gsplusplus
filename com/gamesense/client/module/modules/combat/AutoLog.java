/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@Module.Declaration(name="AutoLog", category=Category.Combat)
public class AutoLog
extends Module {
    IntegerSetting tots = this.registerInteger("Totems", 1, 0, 36);
    IntegerSetting hp = this.registerInteger("Health", 12, 0, 36);

    @Override
    public void onUpdate() {
        if (AutoLog.mc.field_71439_g.func_110143_aJ() + AutoLog.mc.field_71439_g.func_110139_bj() > (float)((Integer)this.hp.getValue()).intValue() && AutoLog.mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> itemStack.func_77973_b() == Items.field_190929_cY).mapToInt(ItemStack::func_190916_E).sum() < (Integer)this.tots.getValue()) {
            AutoLog.mc.field_71439_g.field_71174_a.func_147298_b().func_179293_l();
        }
    }
}

