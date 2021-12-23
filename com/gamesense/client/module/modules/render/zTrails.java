/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.ArrayList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

@Module.Declaration(name="Trails", category=Category.Render)
public class zTrails
extends Module {
    BooleanSetting self = this.registerBoolean("Self", true);
    BooleanSetting others = this.registerBoolean("Others", false);
    ColorSetting color = this.registerColor("Color", new GSColor(255, 255, 255, 255), () -> true, true);
    IntegerSetting life = this.registerInteger("Life", 300, 0, 1000);
    IntegerSetting lineWidth = this.registerInteger("Line Width", 1, 1, 5);
    DoubleSetting distance = this.registerDouble("Distance", 10.0, 0.0, 20.0);
    BooleanSetting desyncColor = this.registerBoolean("Desync Color", false);
    IntegerSetting speedDesyncColor = this.registerInteger("Speed Desync Color", 1, 0, 1000);
    long count = 0L;
    ArrayList<EntityPlayer> players = new ArrayList();
    ArrayList<ArrayList<Vec3d>> points = new ArrayList();
    ArrayList<ArrayList<GSColor>> colors = new ArrayList();
    ArrayList<ArrayList<Long>> lifeSpan = new ArrayList();

    @Override
    public void onUpdate() {
        this.count += (long)((Integer)this.speedDesyncColor.getValue()).intValue();
        if (zTrails.mc.field_71441_e == null || zTrails.mc.field_71439_g == null) {
            return;
        }
        for (int i = 0; i < this.points.size(); ++i) {
            if (this.points.get(i).size() > 0) {
                while (this.lifeSpan.get(i).size() > 0 && System.currentTimeMillis() - this.lifeSpan.get(i).get(0) > (long)((Integer)this.life.getValue()).intValue()) {
                    this.lifeSpan.get(i).remove(0);
                    this.colors.get(i).remove(0);
                    this.points.get(i).remove(0);
                }
                continue;
            }
            this.points.remove(i);
            this.colors.remove(i);
            this.players.remove(i);
            this.lifeSpan.remove(i);
            --i;
        }
        zTrails.mc.field_71441_e.field_73010_i.forEach(e -> {
            boolean add = false;
            if (e == zTrails.mc.field_71439_g) {
                if (((Boolean)this.self.getValue()).booleanValue()) {
                    add = true;
                }
            } else if (((Boolean)this.others.getValue()).booleanValue()) {
                add = true;
            }
            if (add && zTrails.mc.field_71439_g.func_70068_e((Entity)e) < (Double)this.distance.getValue() * (Double)this.distance.getValue()) {
                int found = -1;
                for (int i = 0; i < this.players.size(); ++i) {
                    if (this.players.get(i) != e) continue;
                    found = i;
                    break;
                }
                if (found == -1) {
                    this.players.add((EntityPlayer)e);
                    this.points.add(new ArrayList());
                    this.colors.add(new ArrayList());
                    this.lifeSpan.add(new ArrayList());
                    found = this.points.size() - 1;
                }
                this.points.get(found).add(new Vec3d(e.field_70165_t, e.field_70163_u, e.field_70161_v));
                if (((Boolean)this.desyncColor.getValue()).booleanValue()) {
                    this.colors.get(found).add(new GSColor(ColorSetting.getRainbowColor(this.count), this.color.getValue().getAlpha()));
                } else {
                    this.colors.get(found).add(new GSColor(this.color.getValue(), this.color.getValue().getAlpha()));
                }
                this.lifeSpan.get(found).add(System.currentTimeMillis());
            }
        });
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (zTrails.mc.field_71441_e == null || zTrails.mc.field_71439_g == null) {
            return;
        }
        GlStateManager.func_179094_E();
        GL11.glLineWidth((float)1.0f);
        GL11.glEnable((int)2848);
        GL11.glEnable((int)34383);
        GL11.glHint((int)3154, (int)4354);
        GlStateManager.func_179118_c();
        GlStateManager.func_179103_j((int)7425);
        GlStateManager.func_179129_p();
        GlStateManager.func_179147_l();
        GlStateManager.func_179132_a((boolean)false);
        GlStateManager.func_179090_x();
        GlStateManager.func_179140_f();
        for (int i = 0; i < this.points.size(); ++i) {
            ArrayList<Vec3d> externalVec3 = this.points.get(i);
            ArrayList<GSColor> externalColor = this.colors.get(i);
            GlStateManager.func_187441_d((float)((Integer)this.lineWidth.getValue()).intValue());
            GlStateManager.func_187447_r((int)3);
            for (int j = 0; j < this.points.get(i).size(); ++j) {
                Vec3d pos = externalVec3.get(j);
                externalColor.get(j).glColor();
                GL11.glVertex3d((double)(pos.field_72450_a - zTrails.mc.func_175598_ae().field_78730_l), (double)(pos.field_72448_b - zTrails.mc.func_175598_ae().field_78731_m), (double)(pos.field_72449_c - zTrails.mc.func_175598_ae().field_78728_n));
            }
            GlStateManager.func_187437_J();
        }
        GlStateManager.func_179098_w();
        GlStateManager.func_179126_j();
        GlStateManager.func_179084_k();
        GlStateManager.func_179089_o();
        GlStateManager.func_179103_j((int)7424);
        GlStateManager.func_179141_d();
        GlStateManager.func_179132_a((boolean)true);
        GL11.glDisable((int)34383);
        GL11.glDisable((int)2848);
        GlStateManager.func_179124_c((float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glLineWidth((float)1.0f);
        GlStateManager.func_179121_F();
    }
}

