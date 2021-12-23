/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.PlayerMoveEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.HashMap;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(name="Anchor", category=Category.Movement, priority=1000)
public class Anchor
extends Module {
    BooleanSetting guarantee = this.registerBoolean("Guarantee Hole", true);
    IntegerSetting activateHeight = this.registerInteger("Activate Height", 2, 1, 5);
    IntegerSetting activationPitch = this.registerInteger("Activation Pitch", 75, 0, 90);
    BooleanSetting stopSpeed = this.registerBoolean("Stop Speed", true);
    BooleanSetting fastFall = this.registerBoolean("Fast Fall", false);
    public static boolean active = false;
    BlockPos playerPos;
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
        if (Anchor.mc.field_71439_g == null || Anchor.mc.field_71441_e == null) {
            return;
        }
        active = false;
        if (Anchor.mc.field_71439_g.field_70125_A < (float)((Integer)this.activationPitch.getValue()).intValue()) {
            return;
        }
        if (Anchor.mc.field_71439_g.field_70163_u < 0.0) {
            return;
        }
        double blockX = Math.floor(Anchor.mc.field_71439_g.field_70165_t);
        double blockZ = Math.floor(Anchor.mc.field_71439_g.field_70161_v);
        double offsetX = Math.abs(Anchor.mc.field_71439_g.field_70165_t - blockX);
        double offsetZ = Math.abs(Anchor.mc.field_71439_g.field_70161_v - blockZ);
        if (((Boolean)this.guarantee.getValue()).booleanValue() && (offsetX < (double)0.3f || offsetX > (double)0.7f || offsetZ < (double)0.3f || offsetZ > (double)0.7f)) {
            return;
        }
        this.playerPos = new BlockPos(blockX, Anchor.mc.field_71439_g.field_70163_u, blockZ);
        if (Anchor.mc.field_71441_e.func_180495_p(this.playerPos).func_177230_c() != Blocks.field_150350_a) {
            return;
        }
        BlockPos currentBlock = this.playerPos.func_177977_b();
        for (int i = 0; i < (Integer)this.activateHeight.getValue(); ++i) {
            if (Anchor.mc.field_71441_e.func_180495_p(currentBlock = currentBlock.func_177977_b()).func_177230_c() == Blocks.field_150350_a) continue;
            HashMap<HoleUtil.BlockOffset, HoleUtil.BlockSafety> sides = HoleUtil.getUnsafeSides(currentBlock.func_177984_a());
            sides.entrySet().removeIf(entry -> entry.getValue() == HoleUtil.BlockSafety.RESISTANT);
            if (sides.size() != 0) continue;
            if (((Boolean)this.stopSpeed.getValue()).booleanValue()) {
                active = true;
            }
            Anchor.mc.field_71439_g.field_70159_w = 0.0;
            Anchor.mc.field_71439_g.field_70179_y = 0.0;
            if (((Boolean)this.fastFall.getValue()).booleanValue()) {
                Anchor.mc.field_71439_g.field_70181_x = -10.0;
            }
            event.cancel();
        }
    }, new Predicate[0]);

    @Override
    protected void onDisable() {
        active = false;
    }
}

