/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.world.EntityUtil;
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
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;

@Module.Declaration(name="TargetHUD", category=Category.HUD)
@HUDModule.Declaration(posX=0, posZ=70)
public class TargetHUD
extends HUDModule {
    IntegerSetting range = this.registerInteger("Range", 100, 10, 260);
    ColorSetting outline = this.registerColor("Outline", new GSColor(255, 0, 0, 255));
    ColorSetting background = this.registerColor("Background", new GSColor(0, 0, 0, 255));
    private static EntityPlayer targetPlayer;

    @Override
    public void populate(ITheme theme) {
        this.component = new TargetHUDComponent(theme);
    }

    private static Color getNameColor(String playerName) {
        if (SocialManager.isFriend(playerName)) {
            return new GSColor(ModuleManager.getModule(ColorMain.class).getFriendGSColor(), 255);
        }
        if (SocialManager.isEnemy(playerName)) {
            return new GSColor(ModuleManager.getModule(ColorMain.class).getEnemyGSColor(), 255);
        }
        return new GSColor(255, 255, 255, 255);
    }

    private static GSColor getHealthColor(int health) {
        if (health > 36) {
            health = 36;
        }
        if (health < 0) {
            health = 0;
        }
        int red = (int)(255.0 - (double)health * 7.0833);
        int green = 255 - red;
        return new GSColor(red, green, 0, 255);
    }

    private static boolean isValidEntity(Entity e) {
        if (!(e instanceof EntityPlayer) || e.func_70005_c_().length() == 0) {
            return false;
        }
        return e != TargetHUD.mc.field_71439_g;
    }

    private static float getPing(EntityPlayer player) {
        float ping = 0.0f;
        try {
            ping = EntityUtil.clamp(Objects.requireNonNull(mc.func_147114_u()).func_175102_a(player.func_110124_au()).func_178853_c(), 1.0f, 300.0f);
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
        return ping;
    }

    public static boolean isRenderingEntity(EntityPlayer entityPlayer) {
        return targetPlayer == entityPlayer;
    }

    private class TargetHUDComponent
    extends HUDComponent {
        public TargetHUDComponent(ITheme theme) {
            super(new Labeled(TargetHUD.this.getName(), null, () -> true), TargetHUD.this.position, TargetHUD.this.getName());
        }

        @Override
        public void render(Context context) {
            EntityPlayer entityPlayer;
            super.render(context);
            if (mc.field_71441_e != null && mc.field_71439_g.field_70173_aa >= 10 && (entityPlayer = (EntityPlayer)mc.field_71441_e.field_72996_f.stream().filter(x$0 -> TargetHUD.isValidEntity(x$0)).map(entity -> (EntityLivingBase)entity).min(Comparator.comparing(c -> Float.valueOf(mc.field_71439_g.func_70032_d((Entity)c)))).orElse(null)) != null && entityPlayer.func_70032_d((Entity)mc.field_71439_g) <= (float)((Integer)TargetHUD.this.range.getValue()).intValue()) {
                GSColor bgcolor = new GSColor(TargetHUD.this.background.getValue(), 100);
                context.getInterface().fillRect(context.getRect(), bgcolor, bgcolor, bgcolor, bgcolor);
                GSColor color = TargetHUD.this.outline.getValue();
                context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(context.getSize().width, 1)), color, color, color, color);
                context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(1, context.getSize().height)), color, color, color, color);
                context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x + context.getSize().width - 1, context.getPos().y), new Dimension(1, context.getSize().height)), color, color, color, color);
                context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x, context.getPos().y + context.getSize().height - 1), new Dimension(context.getSize().width, 1)), color, color, color, color);
                targetPlayer = entityPlayer;
                GameSenseGUI.renderEntity((EntityLivingBase)entityPlayer, new Point(context.getPos().x + 35, context.getPos().y + 87 - (entityPlayer.func_70093_af() ? 10 : 0)), 43);
                targetPlayer = null;
                String playerName = entityPlayer.func_70005_c_();
                Color nameColor = TargetHUD.getNameColor(playerName);
                context.getInterface().drawString(new Point(context.getPos().x + 71, context.getPos().y + 11), 9, TextFormatting.BOLD + playerName, nameColor);
                int playerHealth = (int)(entityPlayer.func_110143_aJ() + entityPlayer.func_110139_bj());
                GSColor healthColor = TargetHUD.getHealthColor(playerHealth);
                context.getInterface().drawString(new Point(context.getPos().x + 71, context.getPos().y + 23), 9, TextFormatting.WHITE + "Health: " + TextFormatting.RESET + playerHealth, healthColor);
                context.getInterface().drawString(new Point(context.getPos().x + 71, context.getPos().y + 33), 9, "Distance: " + (int)entityPlayer.func_70032_d((Entity)mc.field_71439_g), new Color(255, 255, 255));
                String info = entityPlayer.field_71071_by.func_70440_f(2).func_77973_b().equals(Items.field_185160_cR) ? TextFormatting.LIGHT_PURPLE + "Wasp" : (entityPlayer.field_71071_by.func_70440_f(2).func_77973_b().equals(Items.field_151163_ad) ? TextFormatting.RED + "Threat" : (entityPlayer.field_71071_by.func_70440_f(3).func_77973_b().equals(Items.field_190931_a) ? TextFormatting.GREEN + "NewFag" : TextFormatting.WHITE + "None"));
                context.getInterface().drawString(new Point(context.getPos().x + 71, context.getPos().y + 43), 9, info + TextFormatting.WHITE + " | " + TargetHUD.getPing(entityPlayer) + " ms", new Color(255, 255, 255));
                String status = null;
                Color statusColor = null;
                for (PotionEffect effect : entityPlayer.func_70651_bq()) {
                    if (effect.func_188419_a() == MobEffects.field_76437_t) {
                        status = "Weakness!";
                        statusColor = new Color(135, 0, 25);
                        continue;
                    }
                    if (effect.func_188419_a() == MobEffects.field_76441_p) {
                        status = "Invisible!";
                        statusColor = new Color(90, 90, 90);
                        continue;
                    }
                    if (effect.func_188419_a() != MobEffects.field_76420_g) continue;
                    status = "Strength!";
                    statusColor = new Color(185, 65, 185);
                }
                if (status != null) {
                    context.getInterface().drawString(new Point(context.getPos().x + 71, context.getPos().y + 55), 9, TextFormatting.WHITE + "Status: " + TextFormatting.RESET + status, statusColor);
                }
                int xPos = context.getPos().x + 150;
                for (ItemStack itemStack : entityPlayer.func_184193_aE()) {
                    GameSenseGUI.renderItem(itemStack, new Point(xPos -= 20, context.getPos().y + 73));
                }
            }
        }

        @Override
        public Dimension getSize(IInterface inter) {
            return new Dimension(162, 94);
        }
    }
}

