/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;

@Module.Declaration(name="Welcomer", category=Category.HUD)
@HUDModule.Declaration(posX=450, posZ=0)
public class Welcomer
extends HUDModule {
    ColorSetting color = this.registerColor("Color", new GSColor(255, 0, 0, 255));

    @Override
    public void populate(ITheme theme) {
        this.component = new ListComponent(new Labeled(this.getName(), null, () -> true), this.position, this.getName(), new WelcomerList(), 9, 1);
    }

    private class WelcomerList
    implements HUDList {
        private WelcomerList() {
        }

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public String getItem(int index) {
            return "Hello " + mc.field_71439_g.func_70005_c_() + " :^)";
        }

        @Override
        public Color getItemColor(int index) {
            return Welcomer.this.color.getValue();
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

