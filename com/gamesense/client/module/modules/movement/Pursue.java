/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.util.world.MotionUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Arrays;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(name="Pursue", category=Category.Movement)
public class Pursue
extends Module {
    ModeSetting mode = this.registerMode("Mode", Arrays.asList("Closest", "Looking"), "Closest");

    @Override
    public void onUpdate() {
        EntityPlayer target = null;
        target = ((String)this.mode.getValue()).equalsIgnoreCase("Closest") ? PlayerUtil.findClosestTarget(6969.0, null, true) : PlayerUtil.findLookingPlayer(6969.0);
        if (target != null) {
            if (Pursue.mc.field_71439_g.field_70123_F && Pursue.mc.field_71439_g.field_70122_E) {
                Pursue.mc.field_71439_g.func_70664_aZ();
            }
            float rot = RotationUtil.getRotationTo((Vec3d)target.func_174791_d()).field_189982_i;
            double[] dir = MotionUtil.forward(Math.min(MotionUtil.getBaseMoveSpeed(), (double)Pursue.mc.field_71439_g.func_70032_d((Entity)target)), rot);
            Pursue.mc.field_71439_g.func_70016_h(dir[0], Pursue.mc.field_71439_g.field_70181_x, dir[1]);
        }
    }
}

