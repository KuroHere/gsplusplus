/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@Module.Declaration(name="PotionEffects", category=Category.HUD)
@HUDModule.Declaration(posX=0, posZ=300)
public class PotionEffects
extends HUDModule {
    BooleanSetting sortUp = this.registerBoolean("Sort Up", false);
    BooleanSetting sortRight = this.registerBoolean("Sort Right", false);
    private final PotionList list = new PotionList();

    @Override
    public void populate(ITheme theme) {
        this.component = new ListComponent(new Labeled(this.getName(), null, () -> true), this.position, this.getName(), this.list, 9, 1);
    }

    Color getColour(PotionEffect potion) {
        int colour = potion.func_188419_a().func_76401_j();
        float r = (float)(colour >> 16 & 0xFF) / 255.0f;
        float g = (float)(colour >> 8 & 0xFF) / 255.0f;
        float b = (float)(colour & 0xFF) / 255.0f;
        return new Color(r, g, b);
    }

    private class PotionList
    implements HUDList {
        private PotionList() {
        }

        @Override
        public int getSize() {
            return mc.field_71439_g.func_70651_bq().size();
        }

        @Override
        public String getItem(int index) {
            PotionEffect effect = (PotionEffect)mc.field_71439_g.func_70651_bq().toArray()[index];
            String name = I18n.func_135052_a((String)effect.func_188419_a().func_76393_a(), (Object[])new Object[0]);
            int amplifier = effect.func_76458_c() + 1;
            return name + " " + amplifier + ChatFormatting.GRAY + " " + Potion.func_188410_a((PotionEffect)effect, (float)1.0f);
        }

        @Override
        public Color getItemColor(int i) {
            if (mc.field_71439_g.func_70651_bq().toArray().length != 0) {
                return PotionEffects.this.getColour((PotionEffect)mc.field_71439_g.func_70651_bq().toArray()[i]);
            }
            return null;
        }

        @Override
        public boolean sortUp() {
            return (Boolean)PotionEffects.this.sortUp.getValue();
        }

        @Override
        public boolean sortRight() {
            return (Boolean)PotionEffects.this.sortRight.getValue();
        }
    }
}

