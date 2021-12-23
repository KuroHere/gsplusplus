/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.SettingsManager;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.SettingsAnimation;
import com.lukflug.panelstudio.component.FixedComponent;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.container.IContainer;
import com.lukflug.panelstudio.tabgui.ITabGUITheme;
import com.lukflug.panelstudio.tabgui.StandardTheme;
import com.lukflug.panelstudio.tabgui.Tab;
import com.lukflug.panelstudio.tabgui.TabGUI;
import com.lukflug.panelstudio.theme.IColorScheme;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;

@Module.Declaration(name="TabGUI", category=Category.HUD)
@HUDModule.Declaration(posX=10, posZ=10)
public class TabGUIModule
extends HUDModule {
    private ITabGUITheme theme = new StandardTheme(new IColorScheme(){

        @Override
        public void createSetting(ITheme theme, String name, String description, boolean hasAlpha, boolean allowsRainbow, Color color, boolean rainbow) {
            ColorSetting setting = new ColorSetting(name, name.replace(" ", ""), TabGUIModule.this, () -> true, rainbow, allowsRainbow, hasAlpha, new GSColor(color));
            SettingsManager.addSetting(setting);
        }

        @Override
        public Color getColor(String name) {
            return ((ColorSetting)SettingsManager.getSettingsForModule(TabGUIModule.this).stream().filter(setting -> setting.getName() == name).findFirst().orElse(null)).getValue();
        }
    }, 75, 9, 2, 5);

    @Override
    public void populate(ITheme theme) {
        ClickGuiModule clickGuiModule = ModuleManager.getModule(ClickGuiModule.class);
        TabGUI tabgui = new TabGUI(() -> "TabGUI", GameSenseGUI.client, this.theme, (IContainer<? super FixedComponent<Tab>>)new IContainer<IFixedComponent>(){

            @Override
            public boolean addComponent(IFixedComponent component) {
                return GameSenseGUI.gui.addHUDComponent(component, () -> true);
            }

            @Override
            public boolean addComponent(IFixedComponent component, IBoolean visible) {
                return GameSenseGUI.gui.addHUDComponent(component, visible);
            }

            @Override
            public boolean removeComponent(IFixedComponent component) {
                return GameSenseGUI.gui.removeComponent(component);
            }
        }, () -> new SettingsAnimation(() -> (Integer)clickGuiModule.animationSpeed.getValue(), () -> GameSenseGUI.guiInterface.getTime()), key -> key == 200, key -> key == 208, key -> key == 28 || key == 205, key -> key == 203, this.position, this.getName());
        this.component = tabgui.getWrappedComponent();
    }
}

