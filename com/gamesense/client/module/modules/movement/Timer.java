/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.util.world.MotionUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.PlayerTweaks;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

@Module.Declaration(name="Timer", category=Category.Movement)
public class Timer
extends Module {
    String arraylistSpeed;
    DoubleSetting speed = this.registerDouble("speed", 1.08, 0.1, 50.0);
    BooleanSetting onMove = this.registerBoolean("onMove", false);
    float speedDouble;

    @Override
    public void onDisable() {
        Minecraft.func_71410_x().field_71428_T.field_194149_e = 50.0f;
    }

    @Override
    public void onUpdate() {
        if (!((Boolean)this.onMove.getValue()).booleanValue() || MotionUtil.isMoving((EntityLivingBase)Timer.mc.field_71439_g) && ((Boolean)this.onMove.getValue()).booleanValue()) {
            this.doTimer();
        } else {
            Timer.mc.field_71428_T.field_194149_e = !Timer.mc.field_71439_g.field_70122_E && (Boolean)ModuleManager.getModule(PlayerTweaks.class).webT.getValue() != false && Timer.mc.field_71439_g.field_70134_J ? 1.0f : 50.0f;
        }
    }

    public void doTimer() {
        this.speedDouble = ((Double)this.speed.getValue()).floatValue();
        Minecraft.func_71410_x().field_71428_T.field_194149_e = 50.0f / this.speedDouble;
    }

    @Override
    public String getHudInfo() {
        this.arraylistSpeed = "";
        this.arraylistSpeed = "[" + ChatFormatting.WHITE + (double)Math.round((double)(50.0f / Timer.mc.field_71428_T.field_194149_e) * 100.0) / 100.0 + ChatFormatting.GRAY + "]";
        return this.arraylistSpeed;
    }
}

