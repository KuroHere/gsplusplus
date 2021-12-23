/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@Module.Declaration(name="HitSpheres", category=Category.Render)
public class HitSpheres
extends Module {
    IntegerSetting range = this.registerInteger("Range", 100, 10, 260);
    DoubleSetting lineWidth = this.registerDouble("Line Width", 2.0, 1.0, 5.0);
    IntegerSetting slices = this.registerInteger("Slices", 20, 10, 30);
    IntegerSetting stacks = this.registerInteger("Stacks", 15, 10, 20);

    @Override
    public void onWorldRender(RenderEvent event) {
        HitSpheres.mc.field_71441_e.field_73010_i.stream().filter(this::isValidPlayer).forEach(entityPlayer -> {
            double posX = entityPlayer.field_70142_S + (entityPlayer.field_70165_t - entityPlayer.field_70142_S) * (double)HitSpheres.mc.field_71428_T.field_194147_b;
            double posY = entityPlayer.field_70137_T + (entityPlayer.field_70163_u - entityPlayer.field_70137_T) * (double)HitSpheres.mc.field_71428_T.field_194147_b;
            double posZ = entityPlayer.field_70136_U + (entityPlayer.field_70161_v - entityPlayer.field_70136_U) * (double)HitSpheres.mc.field_71428_T.field_194147_b;
            GSColor color = this.findRenderColor((EntityPlayer)entityPlayer);
            RenderUtil.drawSphere(posX, posY, posZ, 6.0f, (Integer)this.slices.getValue(), (Integer)this.stacks.getValue(), ((Double)this.lineWidth.getValue()).floatValue(), color);
        });
    }

    private boolean isValidPlayer(EntityPlayer entityPlayer) {
        if (entityPlayer == HitSpheres.mc.field_71439_g) {
            return false;
        }
        return entityPlayer.func_70032_d((Entity)HitSpheres.mc.field_71439_g) <= (float)((Integer)this.range.getValue()).intValue();
    }

    private GSColor findRenderColor(EntityPlayer entityPlayer) {
        String name = entityPlayer.func_70005_c_();
        double distance = HitSpheres.mc.field_71439_g.func_70032_d((Entity)entityPlayer);
        ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        if (SocialManager.isFriend(name)) {
            return colorMain.getFriendGSColor();
        }
        if (distance >= 8.0) {
            return new GSColor(0, 255, 0, 255);
        }
        if (distance < 8.0) {
            return new GSColor(255, (int)(HitSpheres.mc.field_71439_g.func_70032_d((Entity)entityPlayer) * 255.0f / 150.0f), 0, 255);
        }
        if (SocialManager.isEnemy(name)) {
            return colorMain.getEnemyGSColor();
        }
        return new GSColor(1, 1, 1, 255);
    }
}

