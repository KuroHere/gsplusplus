/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Module.Declaration(name="KillEffect", category=Category.Misc, enabled=true)
public class KillEffect
extends Module {
    BooleanSetting thunder = this.registerBoolean("Thunder", true);
    IntegerSetting numbersThunder = this.registerInteger("Number Thunder", 1, 1, 10);
    BooleanSetting sound = this.registerBoolean("Sound", true);
    IntegerSetting numberSound = this.registerInteger("Number Sound", 1, 1, 10);
    BooleanSetting circle = this.registerBoolean("Circle", false);
    DoubleSetting startRay = this.registerDouble("Start Ray", 0.0, 0.0, 5.0);
    DoubleSetting increaseRay = this.registerDouble("Increase Ray", 0.1, 0.0, 2.0);
    IntegerSetting life = this.registerInteger("Life", 1000, 0, 5000);
    BooleanSetting multipleCircle = this.registerBoolean("Multiple Circle", false);
    IntegerSetting nCircles = this.registerInteger("N^ Circles", 2, 1, 5);
    IntegerSetting firstDelay = this.registerInteger("First Delay", 800, 0, 2000);
    IntegerSetting othersDelay = this.registerInteger("Others Delay", 200, 0, 1000);
    ColorSetting color = this.registerColor("Circle Color", new GSColor(255, 255, 255, 255), () -> true, true);
    BooleanSetting rainbowCircle = this.registerBoolean("Rainbow Circle", false);
    IntegerSetting stepRainbowCircle = this.registerInteger("Step Rainbow Circle", 1, 1, 100);
    ArrayList<EntityPlayer> playersDead = new ArrayList();
    ArrayList<circleRender> circleList = new ArrayList();
    final Object sync = new Object();

    @Override
    protected void onEnable() {
        this.playersDead.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onUpdate() {
        if (KillEffect.mc.field_71441_e == null) {
            this.playersDead.clear();
            return;
        }
        for (int i = 0; i < this.circleList.size(); ++i) {
            if (this.circleList.get(i).update()) continue;
            Object object = this.sync;
            synchronized (object) {
                this.circleList.remove(i);
            }
            --i;
        }
        KillEffect.mc.field_71441_e.field_73010_i.forEach(entity -> {
            if (this.playersDead.contains(entity)) {
                if (entity.func_110143_aJ() > 0.0f) {
                    this.playersDead.remove(entity);
                }
            } else if (entity.func_110143_aJ() == 0.0f) {
                int i;
                if (((Boolean)this.thunder.getValue()).booleanValue()) {
                    for (i = 0; i < (Integer)this.numbersThunder.getValue(); ++i) {
                        KillEffect.mc.field_71441_e.func_72838_d((Entity)new EntityLightningBolt((World)KillEffect.mc.field_71441_e, entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, true));
                    }
                }
                if (((Boolean)this.sound.getValue()).booleanValue()) {
                    for (i = 0; i < (Integer)this.numberSound.getValue(); ++i) {
                        KillEffect.mc.field_71439_g.func_184185_a(SoundEvents.field_187754_de, 0.5f, 1.0f);
                    }
                }
                this.playersDead.add((EntityPlayer)entity);
                if (((Boolean)this.circle.getValue()).booleanValue()) {
                    Object i2 = this.sync;
                    synchronized (i2) {
                        this.circleList.add(new circleRender(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, (Double)this.startRay.getValue(), (Double)this.increaseRay.getValue(), (Integer)this.life.getValue(), this.color.getValue(), (Boolean)this.rainbowCircle.getValue(), (Integer)this.stepRainbowCircle.getValue()));
                    }
                    if (((Boolean)this.multipleCircle.getValue()).booleanValue()) {
                        for (int i3 = 0; i3 < (Integer)this.nCircles.getValue(); ++i3) {
                            int delay = (Integer)this.firstDelay.getValue() + i3 * (Integer)this.othersDelay.getValue();
                            Timer t = new Timer();
                            t.schedule(new TimerTask((EntityPlayer)entity){
                                final /* synthetic */ EntityPlayer val$entity;
                                {
                                    this.val$entity = entityPlayer;
                                }

                                /*
                                 * WARNING - Removed try catching itself - possible behaviour change.
                                 */
                                @Override
                                public void run() {
                                    Object object = KillEffect.this.sync;
                                    synchronized (object) {
                                        KillEffect.this.circleList.add(new circleRender(this.val$entity.field_70165_t, this.val$entity.field_70163_u, this.val$entity.field_70161_v, (Double)KillEffect.this.startRay.getValue(), (Double)KillEffect.this.increaseRay.getValue(), (Integer)KillEffect.this.life.getValue(), KillEffect.this.color.getValue(), (Boolean)KillEffect.this.rainbowCircle.getValue(), (Integer)KillEffect.this.stepRainbowCircle.getValue()));
                                    }
                                }
                            }, delay);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        try {
            this.circleList.forEach(circleRender::render);
        }
        catch (ConcurrentModificationException concurrentModificationException) {
            // empty catch block
        }
    }

    static class circleRender {
        final Vec3d center;
        double ray;
        final double increaseRay;
        final long startLife;
        final int life;
        final GSColor color;
        final boolean rainbowColor;
        final int rainbowStep;

        public circleRender(double posX, double posY, double posZ, double ray, double increaseRay, int life, GSColor color, boolean rainbowColor, int rainbowStep) {
            this.center = new Vec3d(posX, posY, posZ);
            this.ray = ray;
            this.startLife = System.currentTimeMillis();
            this.increaseRay = increaseRay;
            this.life = life;
            this.color = color;
            this.rainbowColor = rainbowColor;
            this.rainbowStep = rainbowStep;
        }

        public boolean update() {
            this.ray += this.increaseRay;
            return System.currentTimeMillis() - this.startLife <= (long)this.life;
        }

        void render() {
            if (this.rainbowColor) {
                RenderUtil.drawCircle((float)this.center.field_72450_a, (float)this.center.field_72448_b, (float)this.center.field_72449_c, this.ray, this.rainbowStep, this.color.getAlpha());
            } else {
                RenderUtil.drawCircle((float)this.center.field_72450_a, (float)this.center.field_72448_b, (float)this.center.field_72449_c, this.ray, this.color);
            }
        }
    }
}

