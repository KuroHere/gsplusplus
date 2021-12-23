/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.DamageBlockEvent;
import com.gamesense.api.event.events.DestroyBlockEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.HashMap;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(name="AutoTool", category=Category.Misc)
public class AutoTool
extends Module {
    BooleanSetting switchBack = this.registerBoolean("Switch Back", true);
    private final HashMap<BlockPos, Integer> blockPosIntegerHashMap = new HashMap();
    @EventHandler
    private final Listener<DamageBlockEvent> damageBlockEventListener = new Listener<DamageBlockEvent>(event -> this.runAutoTool(event.getBlockPos(), this.blockPosIntegerHashMap.getOrDefault(event.getBlockPos(), -1)), new Predicate[0]);
    @EventHandler
    private final Listener<DestroyBlockEvent> destroyBlockEventListener = new Listener<DestroyBlockEvent>(event -> {
        if (AutoTool.mc.field_71439_g == null || AutoTool.mc.field_71441_e == null) {
            return;
        }
        if (((Boolean)this.switchBack.getValue()).booleanValue() && this.blockPosIntegerHashMap.containsKey(event.getBlockPos()) && AutoTool.mc.field_71439_g.field_71071_by.field_70461_c != this.blockPosIntegerHashMap.get(event.getBlockPos())) {
            AutoTool.mc.field_71439_g.field_71071_by.field_70461_c = this.blockPosIntegerHashMap.get(event.getBlockPos());
        }
        if (!((Boolean)this.switchBack.getValue()).booleanValue() || this.blockPosIntegerHashMap.size() >= 10) {
            this.blockPosIntegerHashMap.clear();
        }
    }, new Predicate[0]);

    private void runAutoTool(BlockPos blockPos, int switchSlot) {
        int toolSlot = InventoryUtil.findToolForBlockState(AutoTool.mc.field_71441_e.func_180495_p(blockPos), 0, 9);
        if (toolSlot != -1) {
            this.blockPosIntegerHashMap.put(blockPos, switchSlot != -1 ? switchSlot : AutoTool.mc.field_71439_g.field_71071_by.field_70461_c);
            AutoTool.mc.field_71439_g.field_71071_by.field_70461_c = toolSlot;
        }
    }
}

