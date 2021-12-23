/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.text.TextComponentString;

@Module.Declaration(name="Notifications", category=Category.HUD)
@HUDModule.Declaration(posX=0, posZ=50)
public class Notifications
extends HUDModule {
    public BooleanSetting sortUp = this.registerBoolean("Sort Up", false);
    public BooleanSetting sortRight = this.registerBoolean("Sort Right", false);
    public BooleanSetting disableChat = this.registerBoolean("No Chat Msg", true);
    private static final NotificationsList list = new NotificationsList();
    private static int waitCounter;

    @Override
    public void populate(ITheme theme) {
        this.component = new ListComponent(new Labeled(this.getName(), null, () -> true), this.position, this.getName(), list, 9, 1);
    }

    @Override
    public void onUpdate() {
        if (waitCounter < 500) {
            ++waitCounter;
            return;
        }
        waitCounter = 0;
        if (Notifications.list.list.size() > 0) {
            Notifications.list.list.remove(0);
        }
    }

    public void addMessage(TextComponentString m) {
        if (Notifications.list.list.size() < 3) {
            Notifications.list.list.remove(m);
            Notifications.list.list.add(m);
        } else {
            Notifications.list.list.remove(0);
            Notifications.list.list.remove(m);
            Notifications.list.list.add(m);
        }
    }

    private static class NotificationsList
    implements HUDList {
        public List<TextComponentString> list = new ArrayList<TextComponentString>();

        private NotificationsList() {
        }

        @Override
        public int getSize() {
            return this.list.size();
        }

        @Override
        public String getItem(int index) {
            return this.list.get(index).func_150265_g();
        }

        @Override
        public Color getItemColor(int index) {
            return new Color(255, 255, 255);
        }

        @Override
        public boolean sortUp() {
            return (Boolean)ModuleManager.getModule(Notifications.class).sortUp.getValue();
        }

        @Override
        public boolean sortRight() {
            return (Boolean)ModuleManager.getModule(Notifications.class).sortRight.getValue();
        }
    }
}

