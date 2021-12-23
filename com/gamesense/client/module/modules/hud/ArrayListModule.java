/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSense;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Module.Declaration(name="ArrayList", category=Category.HUD)
@HUDModule.Declaration(posX=0, posZ=200)
public class ArrayListModule
extends HUDModule {
    BooleanSetting sortUp = this.registerBoolean("Sort Up", true);
    BooleanSetting sortRight = this.registerBoolean("Sort Right", false);
    ColorSetting color = this.registerColor("Color", new GSColor(255, 0, 0, 255));
    private final ModuleList list = new ModuleList();

    @Override
    public void populate(ITheme theme) {
        this.component = new ListComponent(new Labeled(this.getName(), null, () -> true), this.position, this.getName(), this.list, 9, 1);
    }

    @Override
    public void onRender() {
        this.list.activeModules.clear();
        for (Module module2 : ModuleManager.getModules()) {
            if (!module2.isEnabled() || !module2.isDrawn()) continue;
            this.list.activeModules.add(module2);
        }
        this.list.activeModules.sort(Comparator.comparing(module -> {
            GameSenseGUI cfr_ignored_0 = GameSense.INSTANCE.gameSenseGUI;
            return -GameSenseGUI.guiInterface.getFontWidth(9, module.getName() + ChatFormatting.GRAY + " " + module.getHudInfo());
        }));
    }

    private class ModuleList
    implements HUDList {
        public List<Module> activeModules = new ArrayList<Module>();

        private ModuleList() {
        }

        @Override
        public int getSize() {
            return this.activeModules.size();
        }

        @Override
        public String getItem(int index) {
            Module module = this.activeModules.get(index);
            return !module.getHudInfo().equals("") ? module.getName() + ChatFormatting.GRAY + " " + module.getHudInfo() : module.getName();
        }

        @Override
        public Color getItemColor(int index) {
            GSColor c = ArrayListModule.this.color.getValue();
            return Color.getHSBColor(c.getHue() + (ArrayListModule.this.color.getRainbow() ? 0.02f * (float)index : 0.0f), c.getSaturation(), c.getBrightness());
        }

        @Override
        public boolean sortUp() {
            return (Boolean)ArrayListModule.this.sortUp.getValue();
        }

        @Override
        public boolean sortRight() {
            return (Boolean)ArrayListModule.this.sortRight.getValue();
        }
    }
}

