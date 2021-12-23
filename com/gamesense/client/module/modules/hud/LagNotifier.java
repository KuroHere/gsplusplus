/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.hud;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.render.GSColor;
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
import net.minecraft.network.play.server.SPacketPlayerPosLook;

@Module.Declaration(name="LagNotifier", category=Category.HUD)
@HUDModule.Declaration(posX=50, posZ=50)
public class LagNotifier
extends HUDModule {
    public boolean lag;
    int tmr;
    boolean lagB;
    IntegerSetting delay = this.registerInteger("Hide Delay Ticks", 20, 0, 60);
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<PacketEvent.Receive>(event -> {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.lagB = true;
            this.tmr = 0;
        }
    }, new Predicate[0]);

    @Override
    public void populate(ITheme theme) {
        this.component = new ListComponent(new Labeled(this.getName(), null, () -> true), this.position, this.getName(), new LagNotifierList(), 9, 1);
    }

    @Override
    public void onUpdate() {
        ++this.tmr;
        this.lag = this.tmr < (Integer)this.delay.getValue();
    }

    private class LagNotifierList
    implements HUDList {
        private LagNotifierList() {
        }

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public String getItem(int index) {
            if (LagNotifier.this.lag) {
                return "Rubberband Detected [" + ((Integer)LagNotifier.this.delay.getValue() - LagNotifier.this.tmr) + "]";
            }
            return "";
        }

        @Override
        public Color getItemColor(int index) {
            return GSColor.red;
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

