/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

@Module.Declaration(name="TextRadar", category=Category.HUD)
@HUDModule.Declaration(posX=0, posZ=50)
public class TextRadar
extends HUDModule {
    ModeSetting display = this.registerMode("Display", Arrays.asList("All", "Friend", "Enemy"), "All");
    BooleanSetting sortUp = this.registerBoolean("Sort Up", false);
    BooleanSetting sortRight = this.registerBoolean("Sort Right", false);
    IntegerSetting range = this.registerInteger("Range", 100, 1, 260);
    private final PlayerList list = new PlayerList();

    @Override
    public void populate(ITheme theme) {
        this.component = new ListComponent(new Labeled(this.getName(), null, () -> true), this.position, this.getName(), this.list, 9, 1);
    }

    @Override
    public void onRender() {
        this.list.players.clear();
        TextRadar.mc.field_71441_e.field_72996_f.stream().filter(e -> e instanceof EntityPlayer).filter(e -> e != TextRadar.mc.field_71439_g).forEach(e -> {
            if (TextRadar.mc.field_71439_g.func_70032_d(e) > (float)((Integer)this.range.getValue()).intValue()) {
                return;
            }
            if (((String)this.display.getValue()).equalsIgnoreCase("Friend") && !SocialManager.isFriend(e.func_70005_c_())) {
                return;
            }
            if (((String)this.display.getValue()).equalsIgnoreCase("Enemy") && !SocialManager.isEnemy(e.func_70005_c_())) {
                return;
            }
            this.list.players.add((EntityPlayer)e);
        });
    }

    private class PlayerList
    implements HUDList {
        public List<EntityPlayer> players = new ArrayList<EntityPlayer>();

        private PlayerList() {
        }

        @Override
        public int getSize() {
            return this.players.size();
        }

        @Override
        public String getItem(int index) {
            EntityPlayer e = this.players.get(index);
            TextFormatting friendcolor = SocialManager.isFriend(e.func_70005_c_()) ? ModuleManager.getModule(ColorMain.class).getFriendColor() : (SocialManager.isEnemy(e.func_70005_c_()) ? ModuleManager.getModule(ColorMain.class).getEnemyColor() : TextFormatting.GRAY);
            float health = e.func_110143_aJ() + e.func_110139_bj();
            TextFormatting healthcolor = health <= 5.0f ? TextFormatting.RED : (health > 5.0f && health < 15.0f ? TextFormatting.YELLOW : TextFormatting.GREEN);
            float distance = mc.field_71439_g.func_70032_d((Entity)e);
            TextFormatting distancecolor = distance < 20.0f ? TextFormatting.RED : (distance >= 20.0f && distance < 50.0f ? TextFormatting.YELLOW : TextFormatting.GREEN);
            return TextFormatting.GRAY + "[" + healthcolor + (int)health + TextFormatting.GRAY + "] " + friendcolor + e.func_70005_c_() + TextFormatting.GRAY + " [" + distancecolor + (int)distance + TextFormatting.GRAY + "]";
        }

        @Override
        public Color getItemColor(int index) {
            return new Color(255, 255, 255);
        }

        @Override
        public boolean sortUp() {
            return (Boolean)TextRadar.this.sortUp.getValue();
        }

        @Override
        public boolean sortRight() {
            return (Boolean)TextRadar.this.sortRight.getValue();
        }
    }
}

