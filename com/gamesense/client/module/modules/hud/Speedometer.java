/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Module.Declaration(name="Speedometer", category=Category.HUD)
@HUDModule.Declaration(posX=0, posZ=70)
public class Speedometer
extends HUDModule {
    private static final String MPS = "m/s";
    private static final String KMH = "km/h";
    private static final String MPH = "mph";
    ModeSetting speedUnit = this.registerMode("Unit", Arrays.asList("m/s", "km/h", "mph"), "km/h");
    BooleanSetting averageSpeed = this.registerBoolean("Average Speed", true);
    IntegerSetting averageSpeedTicks = this.registerInteger("Average Time", 20, 5, 100);
    private final ArrayDeque<Double> speedDeque = new ArrayDeque();
    private String speedString = "";
    @EventHandler
    private final Listener<TickEvent.ClientTickEvent> listener = new Listener<TickEvent.ClientTickEvent>(event -> {
        double speed;
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        EntityPlayerSP player = Speedometer.mc.field_71439_g;
        if (player == null) {
            return;
        }
        String unit = (String)this.speedUnit.getValue();
        double displaySpeed = speed = this.calcSpeed(player, unit);
        if (((Boolean)this.averageSpeed.getValue()).booleanValue()) {
            if (speed > 0.0 || player.field_70173_aa % 4 == 0) {
                this.speedDeque.add(speed);
            } else {
                this.speedDeque.pollFirst();
            }
            while (!this.speedDeque.isEmpty() && this.speedDeque.size() > (Integer)this.averageSpeedTicks.getValue()) {
                this.speedDeque.poll();
            }
            displaySpeed = this.average(this.speedDeque);
        }
        this.speedString = String.format("%.2f", displaySpeed) + ' ' + unit;
    }, new Predicate[0]);

    @Override
    protected void onDisable() {
        this.speedDeque.clear();
        this.speedString = "";
    }

    private double calcSpeed(EntityPlayerSP player, String unit) {
        double tps = 1000.0 / (double)Speedometer.mc.field_71428_T.field_194149_e;
        double xDiff = player.field_70165_t - player.field_70169_q;
        double zDiff = player.field_70161_v - player.field_70166_s;
        double speed = Math.hypot(xDiff, zDiff) * tps;
        switch (unit) {
            case "km/h": {
                speed *= 3.6;
                break;
            }
            case "mph": {
                speed *= 2.237;
                break;
            }
        }
        return speed;
    }

    private double average(Collection<Double> collection) {
        if (collection.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        int size = 0;
        for (double element : collection) {
            sum += element;
            ++size;
        }
        return sum / (double)size;
    }

    @Override
    public void populate(ITheme theme) {
        this.component = new ListComponent(new Labeled(this.getName(), null, () -> true), this.position, this.getName(), new SpeedLabel(), 9, 1);
    }

    private class SpeedLabel
    implements HUDList {
        private SpeedLabel() {
        }

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public String getItem(int index) {
            return Speedometer.this.speedString;
        }

        @Override
        public Color getItemColor(int index) {
            return new Color(255, 255, 255);
        }

        @Override
        public boolean sortUp() {
            return false;
        }

        @Override
        public boolean sortRight() {
            return false;
        }
    }
}

