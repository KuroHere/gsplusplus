/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.AspectEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

@Module.Declaration(name="Aspect", category=Category.Render)
public class Aspect
extends Module {
    DoubleSetting aspect = this.registerDouble("Aspect", 1.0, 0.0, 10.0);
    BooleanSetting credits = this.registerBoolean("Credits", false);
    @EventHandler
    private final Listener<AspectEvent> aspectListener = new Listener<AspectEvent>(event -> event.setAspect(((Double)this.aspect.getValue()).floatValue()), new Predicate[0]);

    @Override
    protected void onEnable() {
        if (((Boolean)this.credits.getValue()).booleanValue()) {
            PistonCrystal.printDebug("Aspect module imported from quantum-0.4.6", false);
        }
    }
}

