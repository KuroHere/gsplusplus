/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.BoundingBoxEvent;
import com.gamesense.api.event.events.PlayerMoveEvent;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(name="Jesus", category=Category.Movement)
public class Jesus
extends Module {
    @EventHandler
    private final Listener<BoundingBoxEvent> boundingBoxEventListener = new Listener<BoundingBoxEvent>(event -> {
        if ((event.getBlock().equals(Blocks.field_150355_j) || event.getBlock().equals(Blocks.field_150353_l)) && !Jesus.mc.field_71474_y.field_74311_E.func_151470_d()) {
            event.setbb(Block.field_185505_j);
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
        if ((Jesus.mc.field_71441_e.func_180495_p(new BlockPos(Jesus.mc.field_71439_g.func_174791_d())).func_177230_c().equals(Blocks.field_150355_j) || Jesus.mc.field_71441_e.func_180495_p(new BlockPos(Jesus.mc.field_71439_g.func_174791_d())).func_177230_c().equals(Blocks.field_150353_l)) && !Jesus.mc.field_71474_y.field_74311_E.func_151470_d()) {
            Jesus.mc.field_71439_g.field_70181_x = 0.1;
        }
    }, new Predicate[0]);
}

