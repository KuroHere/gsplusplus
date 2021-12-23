/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraftforge.client.event.EntityViewRenderEvent;

@Module.Declaration(name="SkyColor", category=Category.Render)
public class SkyColor
extends Module {
    BooleanSetting fog = this.registerBoolean("Fog", true);
    ColorSetting color = this.registerColor("Color", new GSColor(0, 255, 0, 255));
    @EventHandler
    private final Listener<EntityViewRenderEvent.FogColors> fogColorsListener = new Listener<EntityViewRenderEvent.FogColors>(event -> {
        event.setRed((float)this.color.getValue().getRed() / 255.0f);
        event.setGreen((float)this.color.getValue().getGreen() / 255.0f);
        event.setBlue((float)this.color.getValue().getBlue() / 255.0f);
    }, new Predicate[0]);
    @EventHandler
    private final Listener<EntityViewRenderEvent.FogDensity> fogDensityListener = new Listener<EntityViewRenderEvent.FogDensity>(event -> {
        if (!((Boolean)this.fog.getValue()).booleanValue()) {
            event.setDensity(0.0f);
            event.setCanceled(true);
        }
    }, new Predicate[0]);
}

