/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.hud;

import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;
import net.minecraft.client.Minecraft;

@Module.Declaration(name="Frames", category=Category.HUD)
@HUDModule.Declaration(posX=0, posZ=0)
public class Frames
extends HUDModule {
    int frames;
    private final FrameList list = new FrameList();

    @Override
    public void populate(ITheme theme) {
        this.component = new ListComponent(new Labeled(this.getName(), null, () -> true), this.position, this.getName(), this.list, 9, 1);
    }

    private class FrameList
    implements HUDList {
        private FrameList() {
        }

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public String getItem(int index) {
            try {
                return "FPS " + Minecraft.func_175610_ah();
            }
            catch (Exception e) {
                return "FPS 0";
            }
        }

        @Override
        public Color getItemColor(int index) {
            return Color.WHITE;
        }

        @Override
        public boolean sortUp() {
            return true;
        }

        @Override
        public boolean sortRight() {
            return false;
        }
    }
}

