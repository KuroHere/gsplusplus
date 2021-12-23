/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Module.Declaration(name="Coordinates", category=Category.HUD)
@HUDModule.Declaration(posX=0, posZ=0)
public class Coordinates
extends HUDModule {
    BooleanSetting showNetherOverworld = this.registerBoolean("Show Nether", true);
    BooleanSetting thousandsSeparator = this.registerBoolean("Thousands Separator", true);
    IntegerSetting decimalPlaces = this.registerInteger("Decimal Places", 1, 0, 5);
    private final String[] coordinateString = new String[]{"", ""};
    @EventHandler
    private final Listener<TickEvent.ClientTickEvent> listener = new Listener<TickEvent.ClientTickEvent>(event -> {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Entity viewEntity = mc.func_175606_aa();
        EntityPlayerSP player = Coordinates.mc.field_71439_g;
        if (viewEntity == null) {
            if (player != null) {
                viewEntity = player;
            } else {
                return;
            }
        }
        int dimension = viewEntity.field_71093_bK;
        this.coordinateString[0] = "XYZ " + this.getFormattedCoords(viewEntity.field_70165_t, viewEntity.field_70163_u, viewEntity.field_70161_v);
        switch (dimension) {
            case -1: {
                this.coordinateString[1] = "Overworld " + this.getFormattedCoords(viewEntity.field_70165_t * 8.0, viewEntity.field_70163_u, viewEntity.field_70161_v * 8.0);
                break;
            }
            case 0: {
                this.coordinateString[1] = "Nether " + this.getFormattedCoords(viewEntity.field_70165_t / 8.0, viewEntity.field_70163_u, viewEntity.field_70161_v / 8.0);
                break;
            }
        }
    }, new Predicate[0]);

    private String getFormattedCoords(double x, double y, double z) {
        return this.roundOrInt(x) + ", " + this.roundOrInt(y) + ", " + this.roundOrInt(z);
    }

    private String roundOrInt(double input) {
        String separatorFormat = (Boolean)this.thousandsSeparator.getValue() != false ? "," : "";
        return String.format('%' + separatorFormat + '.' + this.decimalPlaces.getValue() + 'f', input);
    }

    @Override
    public void populate(ITheme theme) {
        this.component = new ListComponent(new Labeled(this.getName(), null, () -> true), this.position, this.getName(), new CoordinateLabel(), 9, 1);
    }

    private class CoordinateLabel
    implements HUDList {
        private CoordinateLabel() {
        }

        @Override
        public int getSize() {
            int dimension;
            EntityPlayerSP player = mc.field_71439_g;
            int n = dimension = player != null ? player.field_71093_bK : 1;
            if (((Boolean)Coordinates.this.showNetherOverworld.getValue()).booleanValue() && (dimension == -1 || dimension == 0)) {
                return 2;
            }
            return 1;
        }

        @Override
        public String getItem(int index) {
            return Coordinates.this.coordinateString[index];
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

