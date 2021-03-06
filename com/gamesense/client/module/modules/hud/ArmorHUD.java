/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.hud;

import com.gamesense.api.util.font.FontUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

@Module.Declaration(name="ArmorHUD", category=Category.HUD)
public class ArmorHUD
extends Module {
    @Override
    public void onRender() {
        GlStateManager.func_179094_E();
        GlStateManager.func_179098_w();
        ScaledResolution resolution = new ScaledResolution(mc);
        int i = resolution.func_78326_a() / 2;
        int iteration = 0;
        int y = resolution.func_78328_b() - 55 - (ArmorHUD.mc.field_71439_g.func_70090_H() ? 10 : 0);
        for (ItemStack is : ArmorHUD.mc.field_71439_g.field_71071_by.field_70460_b) {
            ++iteration;
            if (is.func_190926_b()) continue;
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.func_179126_j();
            ArmorHUD.mc.func_175599_af().field_77023_b = 200.0f;
            mc.func_175599_af().func_180450_b(is, x, y);
            mc.func_175599_af().func_180453_a(ArmorHUD.mc.field_71466_p, is, x, y, "");
            ArmorHUD.mc.func_175599_af().field_77023_b = 0.0f;
            GlStateManager.func_179098_w();
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            String s = is.func_190916_E() > 1 ? is.func_190916_E() + "" : "";
            ArmorHUD.mc.field_71466_p.func_175063_a(s, (float)(x + 19 - 2 - ArmorHUD.mc.field_71466_p.func_78256_a(s)), (float)(y + 9), new GSColor(255, 255, 255).getRGB());
            float green = ((float)is.func_77958_k() - (float)is.func_77952_i()) / (float)is.func_77958_k();
            float red = 1.0f - green;
            int dmg = 100 - (int)(red * 100.0f);
            if (green > 1.0f) {
                green = 1.0f;
            } else if (green < 0.0f) {
                green = 0.0f;
            }
            if (red > 1.0f) {
                red = 1.0f;
            }
            if (dmg < 0) {
                dmg = 0;
            }
            FontUtil.drawStringWithShadow((Boolean)ModuleManager.getModule(ColorMain.class).customFont.getValue(), dmg + "", x + 8 - ArmorHUD.mc.field_71466_p.func_78256_a(dmg + "") / 2, y - 11, new GSColor((int)(red * 255.0f), (int)(green * 255.0f), 0));
        }
        GlStateManager.func_179126_j();
        GlStateManager.func_179121_F();
    }
}

