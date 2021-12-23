/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.hud.HUDComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Comparator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

@Module.Declaration(name="TargetInfo", category=Category.HUD)
@HUDModule.Declaration(posX=0, posZ=150)
public class TargetInfo
extends HUDModule {
    IntegerSetting range = this.registerInteger("Range", 100, 10, 260);
    ColorSetting backgroundColor = this.registerColor("Background", new GSColor(0, 0, 0, 255));
    ColorSetting outlineColor = this.registerColor("Outline", new GSColor(255, 0, 0, 255));
    public static EntityPlayer targetPlayer;

    @Override
    public void populate(ITheme theme) {
        this.component = new TargetInfoComponent(theme);
    }

    private Color getNameColor(EntityPlayer entityPlayer) {
        if (SocialManager.isFriend(entityPlayer.func_70005_c_())) {
            return new GSColor(ModuleManager.getModule(ColorMain.class).getFriendGSColor(), 255);
        }
        if (SocialManager.isEnemy(entityPlayer.func_70005_c_())) {
            return new GSColor(ModuleManager.getModule(ColorMain.class).getEnemyGSColor(), 255);
        }
        return new GSColor(255, 255, 255, 255);
    }

    private Color getHealthColor(EntityPlayer entityPlayer) {
        int health = (int)(entityPlayer.func_110143_aJ() + entityPlayer.func_110139_bj());
        if (health > 36) {
            health = 36;
        }
        if (health < 0) {
            health = 0;
        }
        int red = (int)(255.0 - (double)health * 7.0833);
        int green = 255 - red;
        return new Color(red, green, 0, 100);
    }

    private static Color getDistanceColor(EntityPlayer entityPlayer) {
        int distance = (int)entityPlayer.func_70032_d((Entity)TargetInfo.mc.field_71439_g);
        if (distance > 50) {
            distance = 50;
        }
        int red = (int)(255.0 - (double)distance * 5.1);
        int green = 255 - red;
        return new Color(red, green, 0, 100);
    }

    public static boolean isRenderingEntity(EntityPlayer entityPlayer) {
        return targetPlayer == entityPlayer;
    }

    private class TargetInfoComponent
    extends HUDComponent {
        public TargetInfoComponent(ITheme theme) {
            super(new Labeled(TargetInfo.this.getName(), null, () -> true), TargetInfo.this.position, TargetInfo.this.getName());
        }

        @Override
        public void render(Context context) {
            EntityPlayer entityPlayer;
            super.render(context);
            if (mc.field_71439_g != null && mc.field_71439_g.field_70173_aa >= 10 && (entityPlayer = (EntityPlayer)mc.field_71441_e.field_72996_f.stream().filter(entity -> entity instanceof EntityPlayer).filter(entity -> entity != mc.field_71439_g).map(entity -> (EntityLivingBase)entity).min(Comparator.comparing(c -> Float.valueOf(mc.field_71439_g.func_70032_d((Entity)c)))).orElse(null)) != null && entityPlayer.func_70032_d((Entity)mc.field_71439_g) <= (float)((Integer)TargetInfo.this.range.getValue()).intValue()) {
                GSColor background = new GSColor(TargetInfo.this.backgroundColor.getValue(), 100);
                context.getInterface().fillRect(context.getRect(), background, background, background, background);
                GSColor outline = new GSColor(TargetInfo.this.outlineColor.getValue(), 255);
                context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(context.getSize().width, 1)), outline, outline, outline, outline);
                context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(1, context.getSize().height)), outline, outline, outline, outline);
                context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x + context.getSize().width - 1, context.getPos().y), new Dimension(1, context.getSize().height)), outline, outline, outline, outline);
                context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x, context.getPos().y + context.getSize().height - 1), new Dimension(context.getSize().width, 1)), outline, outline, outline, outline);
                String name = entityPlayer.func_70005_c_();
                Color nameColor = TargetInfo.this.getNameColor(entityPlayer);
                context.getInterface().drawString(new Point(context.getPos().x + 2, context.getPos().y + 2), 9, name, nameColor);
                int healthVal = (int)(entityPlayer.func_110143_aJ() + entityPlayer.func_110139_bj());
                Color healthBox = TargetInfo.this.getHealthColor(entityPlayer);
                context.getInterface().fillRect(new Rectangle(context.getPos().x + 32, context.getPos().y + 12, (int)((double)healthVal * 1.9444), 15), healthBox, healthBox, healthBox, healthBox);
                int distanceVal = (int)entityPlayer.func_70032_d((Entity)mc.field_71439_g);
                int width = (int)((double)distanceVal * 1.38);
                if (width > 69) {
                    width = 69;
                }
                Color distanceBox = TargetInfo.getDistanceColor(entityPlayer);
                context.getInterface().fillRect(new Rectangle(context.getPos().x + 32, context.getPos().y + 27, width, 15), distanceBox, distanceBox, distanceBox, distanceBox);
                targetPlayer = entityPlayer;
                GameSenseGUI.renderEntity((EntityLivingBase)entityPlayer, new Point(context.getPos().x + 17, context.getPos().y + 40), 15);
                String health = "Health: " + healthVal;
                Color healthColor = new Color(255, 255, 255, 255);
                context.getInterface().drawString(new Point(context.getPos().x + 33, context.getPos().y + 14), 9, health, healthColor);
                String distance = "Distance: " + distanceVal;
                Color distanceColor = new Color(255, 255, 255, 255);
                context.getInterface().drawString(new Point(context.getPos().x + 33, context.getPos().y + 29), 9, distance, distanceColor);
            }
        }

        @Override
        public Dimension getSize(IInterface inter) {
            return new Dimension(102, 43);
        }
    }
}

