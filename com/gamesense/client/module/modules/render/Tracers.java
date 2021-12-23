/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import java.util.Arrays;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(name="Tracers", category=Category.Render)
public class Tracers
extends Module {
    IntegerSetting renderDistance = this.registerInteger("Distance", 100, 10, 260);
    ModeSetting pointsTo = this.registerMode("Draw To", Arrays.asList("Head", "Feet"), "Feet");
    BooleanSetting colorType = this.registerBoolean("Color Sync", true);
    ColorSetting nearColor = this.registerColor("Near Color", new GSColor(255, 0, 0, 255));
    ColorSetting midColor = this.registerColor("Middle Color", new GSColor(255, 255, 0, 255));
    ColorSetting farColor = this.registerColor("Far Color", new GSColor(0, 255, 0, 255));
    GSColor tracerColor;

    @Override
    public void onWorldRender(RenderEvent event) {
        ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        Tracers.mc.field_71441_e.field_72996_f.stream().filter(e -> e instanceof EntityPlayer).filter(e -> e != Tracers.mc.field_71439_g).forEach(e -> {
            if (Tracers.mc.field_71439_g.func_70032_d(e) > (float)((Integer)this.renderDistance.getValue()).intValue()) {
                return;
            }
            if (SocialManager.isFriend(e.func_70005_c_())) {
                this.tracerColor = colorMain.getFriendGSColor();
            } else if (SocialManager.isEnemy(e.func_70005_c_())) {
                this.tracerColor = colorMain.getEnemyGSColor();
            } else {
                if (Tracers.mc.field_71439_g.func_70032_d(e) < 20.0f) {
                    this.tracerColor = this.nearColor.getValue();
                }
                if (Tracers.mc.field_71439_g.func_70032_d(e) >= 20.0f && Tracers.mc.field_71439_g.func_70032_d(e) < 50.0f) {
                    this.tracerColor = this.midColor.getValue();
                }
                if (Tracers.mc.field_71439_g.func_70032_d(e) >= 50.0f) {
                    this.tracerColor = this.farColor.getValue();
                }
                if (((Boolean)this.colorType.getValue()).booleanValue()) {
                    this.tracerColor = this.getDistanceColor((int)Tracers.mc.field_71439_g.func_70032_d(e));
                }
            }
            this.drawLineToEntityPlayer((Entity)e, this.tracerColor);
        });
    }

    public void drawLineToEntityPlayer(Entity e, GSColor color) {
        double[] xyz = Tracers.interpolate(e);
        this.drawLine1(xyz[0], xyz[1], xyz[2], e.field_70131_O, color);
    }

    public static double[] interpolate(Entity entity) {
        double posX = Tracers.interpolate(entity.field_70165_t, entity.field_70142_S);
        double posY = Tracers.interpolate(entity.field_70163_u, entity.field_70137_T);
        double posZ = Tracers.interpolate(entity.field_70161_v, entity.field_70136_U);
        return new double[]{posX, posY, posZ};
    }

    public static double interpolate(double now, double then) {
        return then + (now - then) * (double)mc.func_184121_ak();
    }

    public void drawLine1(double posx, double posy, double posz, double up, GSColor color) {
        Vec3d eyes = ActiveRenderInfo.getCameraPosition().func_72441_c(Tracers.mc.func_175598_ae().field_78730_l, Tracers.mc.func_175598_ae().field_78731_m, Tracers.mc.func_175598_ae().field_78728_n);
        RenderUtil.prepare();
        if (((String)this.pointsTo.getValue()).equalsIgnoreCase("Head")) {
            RenderUtil.drawLine(eyes.field_72450_a, eyes.field_72448_b, eyes.field_72449_c, posx, posy + up, posz, color);
        } else {
            RenderUtil.drawLine(eyes.field_72450_a, eyes.field_72448_b, eyes.field_72449_c, posx, posy, posz, color);
        }
        RenderUtil.release();
    }

    private GSColor getDistanceColor(int distance) {
        if (distance > 50) {
            distance = 50;
        }
        int red = (int)(255.0 - (double)distance * 5.1);
        int green = 255 - red;
        return new GSColor(red, green, 0, 255);
    }
}

