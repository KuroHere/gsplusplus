/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.BoundingBoxEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

@Module.Declaration(name="Avoid", category=Category.Movement)
public class Avoid
extends Module {
    public static Avoid INSTANCE;
    public BooleanSetting unloaded = this.registerBoolean("Unloaded", false);
    public BooleanSetting cactus = this.registerBoolean("Cactus", false);
    public BooleanSetting fire = this.registerBoolean("Fire", false);
    public BooleanSetting bigFire = this.registerBoolean("Extend Fire", false, () -> (Boolean)this.fire.getValue());
    @EventHandler
    private final Listener<BoundingBoxEvent> playerMoveEventListener = new Listener<BoundingBoxEvent>(event -> {
        if (event.getBlock().equals(Blocks.field_189881_dj) && (Boolean)this.unloaded.getValue() != false || event.getBlock().equals(Blocks.field_150434_aF) && (Boolean)this.cactus.getValue() != false || event.getBlock().equals(Blocks.field_150480_ab) && ((Boolean)this.fire.getValue()).booleanValue()) {
            if (((Boolean)this.bigFire.getValue()).booleanValue() && event.getBlock() == Blocks.field_150480_ab) {
                event.setbb(Block.field_185505_j.func_72321_a(0.1, 0.1, 0.1));
            } else {
                event.setbb(Block.field_185505_j);
            }
        }
    }, new Predicate[0]);

    public Avoid() {
        INSTANCE = this;
    }
}

