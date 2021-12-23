/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.PhaseUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.Flight;

@Module.Declaration(name="BoundsMove", category=Category.Movement)
public class BoundsMove
extends Module {
    ModeSetting bound = this.registerMode("Bounds", PhaseUtil.bound, PhaseUtil.normal);

    @Override
    public void onUpdate() {
        if (!(BoundsMove.mc.field_71439_g.field_191988_bg == 0.0f && BoundsMove.mc.field_71439_g.field_70702_br == 0.0f || ModuleManager.getModule(Flight.class).isEnabled() && ((String)ModuleManager.getModule(Flight.class).mode.getValue()).equalsIgnoreCase("Packet"))) {
            PhaseUtil.doBounds((String)this.bound.getValue(), true);
        }
    }
}

