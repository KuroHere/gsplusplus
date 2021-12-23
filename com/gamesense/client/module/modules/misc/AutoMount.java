/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec2f;

@Module.Declaration(name="AutoMount", category=Category.Misc)
public class AutoMount
extends Module {
    @Override
    public void onUpdate() {
        if (AutoMount.mc.field_71439_g.field_184239_as != null) {
            return;
        }
        for (Entity e : AutoMount.mc.field_71441_e.field_72996_f) {
            if (!this.valid(e)) continue;
            Vec2f rot = RotationUtil.getRotationTo(e.func_174791_d());
            AutoMount.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(rot.field_189982_i, rot.field_189983_j, AutoMount.mc.field_71439_g.field_70122_E));
            AutoMount.mc.field_71442_b.func_187097_a((EntityPlayer)AutoMount.mc.field_71439_g, e, EnumHand.MAIN_HAND);
        }
    }

    boolean valid(Entity entity) {
        return entity instanceof EntityBoat || entity instanceof EntityAnimal && ((EntityAnimal)entity).func_70874_b() == 1 && (entity instanceof EntityHorse || entity instanceof EntitySkeletonHorse || entity instanceof EntityDonkey || entity instanceof EntityMule || entity instanceof EntityPig && ((EntityPig)entity).func_70901_n() || entity instanceof EntityLlama);
    }
}

