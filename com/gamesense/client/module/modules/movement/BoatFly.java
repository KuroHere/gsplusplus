/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.util.world.MotionUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

@Module.Declaration(name="BoatFly", category=Category.Movement)
public class BoatFly
extends Module {
    DoubleSetting speed = this.registerDouble("Speed", 2.0, 0.0, 10.0);
    DoubleSetting ySpeed = this.registerDouble("Y Speed", 1.0, 0.0, 10.0);
    DoubleSetting glideSpeed = this.registerDouble("Glide Speed", 0.0, -10.0, 10.0);
    BooleanSetting hover = this.registerBoolean("Hover", false);
    BooleanSetting bypass = this.registerBoolean("Bypass", false);

    @Override
    public void onUpdate() {
        Entity e = BoatFly.mc.field_71439_g.field_184239_as;
        if (e == null) {
            return;
        }
        if (BoatFly.mc.field_71474_y.field_74314_A.func_151470_d()) {
            e.field_70181_x = (Double)this.ySpeed.getValue();
        } else if (BoatFly.mc.field_71474_y.field_74311_E.func_151470_d()) {
            e.field_70181_x = -((Double)this.ySpeed.getValue()).doubleValue();
        } else {
            double d = e.field_70181_x = (Boolean)this.hover.getValue() != false && BoatFly.mc.field_71439_g.field_70173_aa % 2 == 0 ? (Double)this.glideSpeed.getValue() : -((Double)this.glideSpeed.getValue()).doubleValue();
        }
        if (MotionUtil.isMoving((EntityLivingBase)BoatFly.mc.field_71439_g)) {
            double[] dir = MotionUtil.forward((Double)this.speed.getValue());
            e.field_70159_w = dir[0];
            e.field_70179_y = dir[1];
        } else {
            e.field_70159_w = 0.0;
            e.field_70179_y = 0.0;
        }
        if (((Boolean)this.bypass.getValue()).booleanValue() && BoatFly.mc.field_71439_g.field_70173_aa % 4 == 0 && BoatFly.mc.field_71439_g.field_184239_as instanceof EntityBoat) {
            BoatFly.mc.field_71442_b.func_187097_a((EntityPlayer)BoatFly.mc.field_71439_g, BoatFly.mc.field_71439_g.field_184239_as, EnumHand.MAIN_HAND);
        }
    }
}

