/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.util.world.MotionUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.LongJump;
import net.minecraft.entity.player.EntityPlayer;

@Module.Declaration(name="PassiveSpeed", category=Category.Movement)
public class PassiveSpeed
extends Module {
    DoubleSetting speed = this.registerDouble("Speed", 1.1, 1.0, 2.0);

    @Override
    public void onUpdate() {
        if (!PassiveSpeed.mc.field_71439_g.field_70122_E && MotionUtil.getMotion((EntityPlayer)PassiveSpeed.mc.field_71439_g) != 0.0 && !ModuleManager.isModuleEnabled(LongJump.class)) {
            PassiveSpeed.mc.field_71439_g.field_70747_aH = (float)(0.02 * (double)((Double)this.speed.getValue()).floatValue());
        }
    }
}

