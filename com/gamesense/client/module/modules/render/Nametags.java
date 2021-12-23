/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.font.FontUtil;
import com.gamesense.api.util.misc.ColorUtil;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.client.manager.managers.TotemPopManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

@Module.Declaration(name="Nametags", category=Category.Render)
public class Nametags
extends Module {
    IntegerSetting range = this.registerInteger("Range", 100, 10, 260);
    BooleanSetting renderSelf = this.registerBoolean("Render Self", false);
    BooleanSetting showDurability = this.registerBoolean("Durability", true);
    BooleanSetting showItems = this.registerBoolean("Items", true);
    BooleanSetting showEnchantName = this.registerBoolean("Enchants", true);
    BooleanSetting showItemName = this.registerBoolean("Item Name", false);
    BooleanSetting showGameMode = this.registerBoolean("Gamemode", false);
    BooleanSetting showHealth = this.registerBoolean("Health", true);
    BooleanSetting showPing = this.registerBoolean("Ping", false);
    BooleanSetting showTotem = this.registerBoolean("Totem Pops", true);
    BooleanSetting showEntityID = this.registerBoolean("Entity Id", false);
    ModeSetting levelColor = this.registerMode("Level Color", ColorUtil.colors, "Green");
    public BooleanSetting customColor = this.registerBoolean("Custom Color", true);
    public ColorSetting borderColor = this.registerColor("Border Color", new GSColor(255, 0, 0, 255));

    @Override
    public void onWorldRender(RenderEvent event) {
        if (Nametags.mc.field_71439_g == null || Nametags.mc.field_71441_e == null) {
            return;
        }
        Nametags.mc.field_71441_e.field_73010_i.stream().filter(this::shouldRender).forEach(entityPlayer -> {
            Vec3d vec3d = this.findEntityVec3d((EntityPlayer)entityPlayer);
            this.renderNameTags((EntityPlayer)entityPlayer, vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72449_c);
        });
    }

    private boolean shouldRender(EntityPlayer entityPlayer) {
        if (entityPlayer == Nametags.mc.field_71439_g && !((Boolean)this.renderSelf.getValue()).booleanValue()) {
            return false;
        }
        if (entityPlayer.func_70005_c_().length() == 0) {
            return false;
        }
        if (entityPlayer.field_70128_L || entityPlayer.func_110143_aJ() <= 0.0f) {
            return false;
        }
        return !(entityPlayer.func_70032_d((Entity)Nametags.mc.field_71439_g) > (float)((Integer)this.range.getValue()).intValue());
    }

    private Vec3d findEntityVec3d(EntityPlayer entityPlayer) {
        double posX = this.balancePosition(entityPlayer.field_70165_t, entityPlayer.field_70142_S);
        double posY = this.balancePosition(entityPlayer.field_70163_u, entityPlayer.field_70137_T);
        double posZ = this.balancePosition(entityPlayer.field_70161_v, entityPlayer.field_70136_U);
        return new Vec3d(posX, posY, posZ);
    }

    private double balancePosition(double newPosition, double oldPosition) {
        return oldPosition + (newPosition - oldPosition) * (double)Nametags.mc.field_71428_T.field_194147_b;
    }

    private void renderNameTags(EntityPlayer entityPlayer, double posX, double posY, double posZ) {
        double adjustedY = posY + (entityPlayer.func_70093_af() ? 1.9 : 2.1);
        String[] name = new String[]{this.buildEntityNameString(entityPlayer)};
        RenderUtil.drawNametag(posX, adjustedY, posZ, name, this.findTextColor(entityPlayer), 2);
        this.renderItemsAndArmor(entityPlayer, 0, 0);
        GlStateManager.func_179121_F();
    }

    private String buildEntityNameString(EntityPlayer entityPlayer) {
        String name = entityPlayer.func_70005_c_();
        if (((Boolean)this.showEntityID.getValue()).booleanValue()) {
            name = name + " ID: " + entityPlayer.func_145782_y();
        }
        if (((Boolean)this.showGameMode.getValue()).booleanValue()) {
            name = entityPlayer.func_184812_l_() ? name + " [C]" : (entityPlayer.func_175149_v() ? name + " [I]" : name + " [S]");
        }
        if (((Boolean)this.showTotem.getValue()).booleanValue()) {
            name = name + " [" + TotemPopManager.INSTANCE.getPlayerPopCount(entityPlayer.func_70005_c_()) + "]";
        }
        if (((Boolean)this.showPing.getValue()).booleanValue()) {
            int value = 0;
            if (mc.func_147114_u() != null && mc.func_147114_u().func_175102_a(entityPlayer.func_110124_au()) != null) {
                value = mc.func_147114_u().func_175102_a(entityPlayer.func_110124_au()).func_178853_c();
            }
            name = name + " " + value + "ms";
        }
        if (((Boolean)this.showHealth.getValue()).booleanValue()) {
            int health = (int)(entityPlayer.func_110143_aJ() + entityPlayer.func_110139_bj());
            TextFormatting textFormatting = this.findHealthColor(health);
            name = name + " " + textFormatting + health;
        }
        return name;
    }

    private TextFormatting findHealthColor(int health) {
        if (health <= 0) {
            return TextFormatting.DARK_RED;
        }
        if (health <= 5) {
            return TextFormatting.RED;
        }
        if (health <= 10) {
            return TextFormatting.GOLD;
        }
        if (health <= 15) {
            return TextFormatting.YELLOW;
        }
        if (health <= 20) {
            return TextFormatting.DARK_GREEN;
        }
        return TextFormatting.GREEN;
    }

    private GSColor findTextColor(EntityPlayer entityPlayer) {
        ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        if (SocialManager.isFriend(entityPlayer.func_70005_c_())) {
            return colorMain.getFriendGSColor();
        }
        if (SocialManager.isEnemy(entityPlayer.func_70005_c_())) {
            return colorMain.getEnemyGSColor();
        }
        if (entityPlayer.func_82150_aj()) {
            return new GSColor(128, 128, 128);
        }
        if (mc.func_147114_u() != null && mc.func_147114_u().func_175102_a(entityPlayer.func_110124_au()) == null) {
            return new GSColor(239, 1, 71);
        }
        if (entityPlayer.func_70093_af()) {
            return new GSColor(255, 153, 0);
        }
        return new GSColor(255, 255, 255);
    }

    private void renderItemsAndArmor(EntityPlayer entityPlayer, int posX, int posY) {
        int enchantSize;
        ItemStack mainHandItem = entityPlayer.func_184614_ca();
        ItemStack offHandItem = entityPlayer.func_184592_cb();
        int armorCount = 3;
        for (int i = 0; i <= 3; ++i) {
            ItemStack itemStack = (ItemStack)entityPlayer.field_71071_by.field_70460_b.get(armorCount);
            if (!itemStack.func_190926_b()) {
                posX -= 8;
                int size = EnchantmentHelper.func_82781_a((ItemStack)itemStack).size();
                if (((Boolean)this.showItems.getValue()).booleanValue() && size > posY) {
                    posY = size;
                }
            }
            --armorCount;
        }
        if (!mainHandItem.func_190926_b() && (((Boolean)this.showItems.getValue()).booleanValue() || ((Boolean)this.showDurability.getValue()).booleanValue() && offHandItem.func_77984_f())) {
            posX -= 8;
            enchantSize = EnchantmentHelper.func_82781_a((ItemStack)offHandItem).size();
            if (((Boolean)this.showItems.getValue()).booleanValue() && enchantSize > posY) {
                posY = enchantSize;
            }
        }
        if (!mainHandItem.func_190926_b()) {
            enchantSize = EnchantmentHelper.func_82781_a((ItemStack)mainHandItem).size();
            if (((Boolean)this.showItems.getValue()).booleanValue() && enchantSize > posY) {
                posY = enchantSize;
            }
            int armorY = this.findArmorY(posY);
            if (((Boolean)this.showItems.getValue()).booleanValue() || ((Boolean)this.showDurability.getValue()).booleanValue() && mainHandItem.func_77984_f()) {
                posX -= 8;
            }
            if (((Boolean)this.showItems.getValue()).booleanValue()) {
                this.renderItem(mainHandItem, posX, armorY, posY);
                armorY -= 32;
            }
            if (((Boolean)this.showDurability.getValue()).booleanValue() && mainHandItem.func_77984_f()) {
                this.renderItemDurability(mainHandItem, posX, armorY);
            }
            ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
            armorY -= (Boolean)colorMain.customFont.getValue() != false ? FontUtil.getFontHeight((Boolean)colorMain.customFont.getValue()) : Nametags.mc.field_71466_p.field_78288_b;
            if (((Boolean)this.showItemName.getValue()).booleanValue()) {
                this.renderItemName(mainHandItem, armorY);
            }
            if (((Boolean)this.showItems.getValue()).booleanValue() || ((Boolean)this.showDurability.getValue()).booleanValue() && mainHandItem.func_77984_f()) {
                posX += 16;
            }
        }
        int armorCount2 = 3;
        for (int i = 0; i <= 3; ++i) {
            ItemStack itemStack = (ItemStack)entityPlayer.field_71071_by.field_70460_b.get(armorCount2);
            if (!itemStack.func_190926_b()) {
                int armorY = this.findArmorY(posY);
                if (((Boolean)this.showItems.getValue()).booleanValue()) {
                    this.renderItem(itemStack, posX, armorY, posY);
                    armorY -= 32;
                }
                if (((Boolean)this.showDurability.getValue()).booleanValue() && itemStack.func_77984_f()) {
                    this.renderItemDurability(itemStack, posX, armorY);
                }
                posX += 16;
            }
            --armorCount2;
        }
        if (!offHandItem.func_190926_b()) {
            int armorY = this.findArmorY(posY);
            if (((Boolean)this.showItems.getValue()).booleanValue()) {
                this.renderItem(offHandItem, posX, armorY, posY);
                armorY -= 32;
            }
            if (((Boolean)this.showDurability.getValue()).booleanValue() && offHandItem.func_77984_f()) {
                this.renderItemDurability(offHandItem, posX, armorY);
            }
        }
    }

    private int findArmorY(int posY) {
        int posY2;
        int n = posY2 = (Boolean)this.showItems.getValue() != false ? -26 : -27;
        if (posY > 4) {
            posY2 -= (posY - 4) * 8;
        }
        return posY2;
    }

    private void renderItemName(ItemStack itemStack, int posY) {
        GlStateManager.func_179098_w();
        GlStateManager.func_179094_E();
        GlStateManager.func_179139_a((double)0.5, (double)0.5, (double)0.5);
        ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        FontUtil.drawStringWithShadow((Boolean)colorMain.customFont.getValue(), itemStack.func_82833_r(), -FontUtil.getStringWidth((Boolean)colorMain.customFont.getValue(), itemStack.func_82833_r()) / 2, posY, new GSColor(255, 255, 255));
        GlStateManager.func_179121_F();
        GlStateManager.func_179090_x();
    }

    private void renderItemDurability(ItemStack itemStack, int posX, int posY) {
        float damagePercent = (float)(itemStack.func_77958_k() - itemStack.func_77952_i()) / (float)itemStack.func_77958_k();
        float green = damagePercent;
        if (green > 1.0f) {
            green = 1.0f;
        } else if (green < 0.0f) {
            green = 0.0f;
        }
        float red = 1.0f - green;
        GlStateManager.func_179098_w();
        GlStateManager.func_179094_E();
        GlStateManager.func_179139_a((double)0.5, (double)0.5, (double)0.5);
        ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        FontUtil.drawStringWithShadow((Boolean)colorMain.customFont.getValue(), (int)(damagePercent * 100.0f) + "%", posX * 2, posY, new GSColor((int)(red * 255.0f), (int)(green * 255.0f), 0));
        GlStateManager.func_179121_F();
        GlStateManager.func_179090_x();
    }

    private void renderItem(ItemStack itemStack, int posX, int posY, int posY2) {
        GlStateManager.func_179098_w();
        GlStateManager.func_179132_a((boolean)true);
        GlStateManager.func_179086_m((int)256);
        GlStateManager.func_179126_j();
        GlStateManager.func_179118_c();
        int posY3 = posY2 > 4 ? (posY2 - 4) * 8 / 2 : 0;
        Nametags.mc.func_175599_af().field_77023_b = -150.0f;
        RenderHelper.func_74519_b();
        mc.func_175599_af().func_180450_b(itemStack, posX, posY + posY3);
        mc.func_175599_af().func_175030_a(Nametags.mc.field_71466_p, itemStack, posX, posY + posY3);
        RenderHelper.func_74518_a();
        Nametags.mc.func_175599_af().field_77023_b = 0.0f;
        RenderUtil.prepare();
        GlStateManager.func_179094_E();
        GlStateManager.func_179139_a((double)0.5, (double)0.5, (double)0.5);
        this.renderEnchants(itemStack, posX, posY - 24);
        GlStateManager.func_179121_F();
    }

    private void renderEnchants(ItemStack itemStack, int posX, int posY) {
        GlStateManager.func_179098_w();
        for (Enchantment enchantment : EnchantmentHelper.func_82781_a((ItemStack)itemStack).keySet()) {
            if (enchantment == null) continue;
            if (((Boolean)this.showEnchantName.getValue()).booleanValue()) {
                int level = EnchantmentHelper.func_77506_a((Enchantment)enchantment, (ItemStack)itemStack);
                ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
                FontUtil.drawStringWithShadow((Boolean)colorMain.customFont.getValue(), this.findStringForEnchants(enchantment, level), posX * 2, posY, new GSColor(255, 255, 255));
            }
            posY += 8;
        }
        if (itemStack.func_77973_b().equals(Items.field_151153_ao) && itemStack.func_77962_s()) {
            ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
            FontUtil.drawStringWithShadow((Boolean)colorMain.customFont.getValue(), "God", posX * 2, posY, new GSColor(195, 77, 65));
        }
        GlStateManager.func_179090_x();
    }

    private String findStringForEnchants(Enchantment enchantment, int level) {
        int charCount;
        ResourceLocation resourceLocation = (ResourceLocation)Enchantment.field_185264_b.func_177774_c((Object)enchantment);
        String string = resourceLocation == null ? enchantment.func_77320_a() : resourceLocation.toString();
        int n = charCount = level > 1 ? 12 : 13;
        if (string.length() > charCount) {
            string = string.substring(10, charCount);
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1) + ColorUtil.settingToTextFormatting(this.levelColor) + (level > 1 ? Integer.valueOf(level) : "");
    }
}

