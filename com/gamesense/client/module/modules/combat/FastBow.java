/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import net.minecraft.item.ItemBow;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(name="FastBow", category=Category.Combat)
public class FastBow
extends Module {
    IntegerSetting drawLength = this.registerInteger("Draw Length", 3, 0, 21);

    @Override
    public void onUpdate() {
        if (FastBow.mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBow && FastBow.mc.field_71439_g.func_184587_cr() && FastBow.mc.field_71439_g.func_184612_cw() >= (Integer)this.drawLength.getValue() && !ModuleManager.isModuleEnabled("Quiver")) {
            FastBow.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.field_177992_a, FastBow.mc.field_71439_g.func_174811_aO()));
            FastBow.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItem(FastBow.mc.field_71439_g.func_184600_cs()));
            FastBow.mc.field_71439_g.func_184597_cx();
        }
    }
}

