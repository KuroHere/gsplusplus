/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.BossbarEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.material.Material;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

@Module.Declaration(name="NoRender", category=Category.Render)
public class NoRender
extends Module {
    public BooleanSetting armor = this.registerBoolean("Armor", false);
    BooleanSetting fire = this.registerBoolean("Fire", false);
    BooleanSetting blind = this.registerBoolean("Blind", false);
    BooleanSetting nausea = this.registerBoolean("Nausea", false);
    public BooleanSetting hurtCam = this.registerBoolean("HurtCam", false);
    public BooleanSetting noSkylight = this.registerBoolean("Skylight", false);
    public BooleanSetting noOverlay = this.registerBoolean("No Overlay", false);
    BooleanSetting noBossBar = this.registerBoolean("No Boss Bar", false);
    public BooleanSetting noWeather = this.registerBoolean("No Weather", false);
    public ModeSetting weather = this.registerMode("Allowed Weather", Arrays.asList("Clear", "Rain", "Thunder"), "Clear");
    public BooleanSetting noCluster = this.registerBoolean("No Cluster", false);
    IntegerSetting maxNoClusterRender = this.registerInteger("No Cluster Max", 5, 1, 25);
    public int currentClusterAmount = 0;
    @EventHandler
    public Listener<RenderBlockOverlayEvent> blockOverlayEventListener = new Listener<RenderBlockOverlayEvent>(event -> {
        if (((Boolean)this.fire.getValue()).booleanValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE) {
            event.setCanceled(true);
        }
        if (((Boolean)this.noOverlay.getValue()).booleanValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER) {
            event.setCanceled(true);
        }
        if (((Boolean)this.noOverlay.getValue()).booleanValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.BLOCK) {
            event.setCanceled(true);
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<EntityViewRenderEvent.FogDensity> fogDensityListener = new Listener<EntityViewRenderEvent.FogDensity>(event -> {
        if (((Boolean)this.noOverlay.getValue()).booleanValue() && (event.getState().func_185904_a().equals(Material.field_151586_h) || event.getState().func_185904_a().equals(Material.field_151587_i))) {
            event.setDensity(0.0f);
            event.setCanceled(true);
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<RenderBlockOverlayEvent> renderBlockOverlayEventListener = new Listener<RenderBlockOverlayEvent>(event -> {
        if (((Boolean)this.noOverlay.getValue()).booleanValue()) {
            event.setCanceled(true);
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<RenderGameOverlayEvent> renderGameOverlayEventListener = new Listener<RenderGameOverlayEvent>(event -> {
        if (((Boolean)this.noOverlay.getValue()).booleanValue()) {
            if (event.getType().equals((Object)RenderGameOverlayEvent.ElementType.HELMET)) {
                event.setCanceled(true);
            }
            if (event.getType().equals((Object)RenderGameOverlayEvent.ElementType.PORTAL)) {
                event.setCanceled(true);
            }
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<BossbarEvent> bossbarEventListener = new Listener<BossbarEvent>(event -> {
        if (((Boolean)this.noBossBar.getValue()).booleanValue()) {
            event.cancel();
        }
    }, new Predicate[0]);

    @Override
    public void onUpdate() {
        if (((Boolean)this.blind.getValue()).booleanValue() && NoRender.mc.field_71439_g.func_70644_a(MobEffects.field_76440_q)) {
            NoRender.mc.field_71439_g.func_184589_d(MobEffects.field_76440_q);
        }
        if (((Boolean)this.nausea.getValue()).booleanValue() && NoRender.mc.field_71439_g.func_70644_a(MobEffects.field_76431_k)) {
            NoRender.mc.field_71439_g.func_184589_d(MobEffects.field_76431_k);
        }
    }

    @Override
    public void onRender() {
        this.currentClusterAmount = 0;
    }

    public boolean incrementNoClusterRender() {
        ++this.currentClusterAmount;
        return this.currentClusterAmount <= (Integer)this.maxNoClusterRender.getValue();
    }

    public boolean getNoClusterRender() {
        return this.currentClusterAmount <= (Integer)this.maxNoClusterRender.getValue();
    }
}

