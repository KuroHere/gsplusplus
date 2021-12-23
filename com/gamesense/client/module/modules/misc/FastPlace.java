/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.init.Items;

@Module.Declaration(name="FastPlace", category=Category.Misc)
public class FastPlace
extends Module {
    BooleanSetting exp = this.registerBoolean("Exp", false);
    BooleanSetting crystals = this.registerBoolean("Crystals", false);
    BooleanSetting offhandCrystal = this.registerBoolean("Offhand Crystal", false);
    BooleanSetting everything = this.registerBoolean("Everything", false);

    @Override
    public void onUpdate() {
        if (((Boolean)this.exp.getValue()).booleanValue() && FastPlace.mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_151062_by || FastPlace.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151062_by) {
            FastPlace.mc.field_71467_ac = 0;
        }
        if (((Boolean)this.crystals.getValue()).booleanValue() && FastPlace.mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP) {
            FastPlace.mc.field_71467_ac = 0;
        }
        if (((Boolean)this.offhandCrystal.getValue()).booleanValue() && FastPlace.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP) {
            FastPlace.mc.field_71467_ac = 0;
        }
        if (((Boolean)this.everything.getValue()).booleanValue()) {
            FastPlace.mc.field_71467_ac = 0;
        }
        FastPlace.mc.field_71442_b.field_78781_i = 0;
    }
}

