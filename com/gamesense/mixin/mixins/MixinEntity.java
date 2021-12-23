/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.EntityCollisionEvent;
import com.gamesense.api.event.events.StepEvent;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.SafeWalk;
import com.gamesense.client.module.modules.movement.Scaffold;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Entity.class})
public abstract class MixinEntity {
    @Shadow
    public double field_70163_u;
    @Shadow
    public double field_70161_v;

    @Inject(method={"applyEntityCollision"}, at={@At(value="HEAD")}, cancellable=true)
    public void velocity(Entity entityIn, CallbackInfo ci) {
        EntityCollisionEvent event = new EntityCollisionEvent();
        GameSense.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Redirect(method={"move"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;isSneaking()Z"))
    public boolean isSneaking(Entity entity) {
        return ModuleManager.isModuleEnabled(Scaffold.class) && !Minecraft.func_71410_x().field_71474_y.field_151444_V.func_151470_d() || ModuleManager.isModuleEnabled(SafeWalk.class) || entity.func_70093_af();
    }

    @Inject(method={"move"}, at={@At(value="HEAD")})
    public void move(MoverType type, double tx, double ty, double tz, CallbackInfo ci) {
        Minecraft mc = Minecraft.func_71410_x();
        if (mc.func_147104_D() == null) {
            return;
        }
        double x = tx;
        double y = ty;
        double z = tz;
        if (ci.isCancelled()) {
            return;
        }
        AxisAlignedBB bb = mc.field_71439_g.func_174813_aQ();
        if (!mc.field_71439_g.field_70145_X) {
            boolean flag;
            if (type.equals((Object)MoverType.PISTON)) {
                return;
            }
            mc.field_71441_e.field_72984_F.func_76320_a("move");
            if (mc.field_71439_g.field_70134_J) {
                return;
            }
            double d2 = x;
            double d3 = y;
            double d4 = z;
            if ((type == MoverType.SELF || type == MoverType.PLAYER) && mc.field_71439_g.field_70122_E && mc.field_71439_g.func_70093_af()) {
                double d5 = 0.05;
                while (x != 0.0 && mc.field_71441_e.func_184144_a((Entity)mc.field_71439_g, bb.func_72317_d(x, (double)(-mc.field_71439_g.field_70138_W), 0.0)).isEmpty()) {
                    x = x < 0.05 && x >= -0.05 ? 0.0 : (x > 0.0 ? (x -= 0.05) : (x += 0.05));
                    d2 = x;
                }
                while (z != 0.0 && mc.field_71441_e.func_184144_a((Entity)mc.field_71439_g, bb.func_72317_d(0.0, (double)(-mc.field_71439_g.field_70138_W), z)).isEmpty()) {
                    z = z < 0.05 && z >= -0.05 ? 0.0 : (z > 0.0 ? (z -= 0.05) : (z += 0.05));
                    d4 = z;
                }
                while (x != 0.0 && z != 0.0 && mc.field_71441_e.func_184144_a((Entity)mc.field_71439_g, bb.func_72317_d(x, (double)(-mc.field_71439_g.field_70138_W), z)).isEmpty()) {
                    x = x < 0.05 && x >= -0.05 ? 0.0 : (x > 0.0 ? (x -= 0.05) : (x += 0.05));
                    d2 = x;
                    z = z < 0.05 && z >= -0.05 ? 0.0 : (z > 0.0 ? (z -= 0.05) : (z += 0.05));
                    d4 = z;
                }
            }
            List list1 = mc.field_71441_e.func_184144_a((Entity)mc.field_71439_g, bb.func_72321_a(x, y, z));
            if (y != 0.0) {
                int l = list1.size();
                for (int k = 0; k < l; ++k) {
                    y = ((AxisAlignedBB)list1.get(k)).func_72323_b(bb, y);
                }
                bb = bb.func_72317_d(0.0, y, 0.0);
            }
            if (x != 0.0) {
                int l5 = list1.size();
                for (int j5 = 0; j5 < l5; ++j5) {
                    x = ((AxisAlignedBB)list1.get(j5)).func_72316_a(bb, x);
                }
                if (x != 0.0) {
                    bb = bb.func_72317_d(x, 0.0, 0.0);
                }
            }
            if (z != 0.0) {
                int i6 = list1.size();
                for (int k5 = 0; k5 < i6; ++k5) {
                    z = ((AxisAlignedBB)list1.get(k5)).func_72322_c(bb, z);
                }
                if (z != 0.0) {
                    bb = bb.func_72317_d(0.0, 0.0, z);
                }
            }
            boolean bl = flag = mc.field_71439_g.field_70122_E || d3 != y && d3 < 0.0;
            if (mc.field_71439_g.field_70138_W > 0.0f && flag && (d2 != x || d4 != z)) {
                double d14 = x;
                double d6 = y;
                double d7 = z;
                y = mc.field_71439_g.field_70138_W;
                List list = mc.field_71441_e.func_184144_a((Entity)mc.field_71439_g, bb.func_72321_a(d2, y, d4));
                AxisAlignedBB axisalignedbb2 = bb;
                AxisAlignedBB axisalignedbb3 = axisalignedbb2.func_72321_a(d2, 0.0, d4);
                double d8 = y;
                int k1 = list.size();
                for (int j1 = 0; j1 < k1; ++j1) {
                    d8 = ((AxisAlignedBB)list.get(j1)).func_72323_b(axisalignedbb3, d8);
                }
                axisalignedbb2 = axisalignedbb2.func_72317_d(0.0, d8, 0.0);
                double d18 = d2;
                int i2 = list.size();
                for (int l1 = 0; l1 < i2; ++l1) {
                    d18 = ((AxisAlignedBB)list.get(l1)).func_72316_a(axisalignedbb2, d18);
                }
                axisalignedbb2 = axisalignedbb2.func_72317_d(d18, 0.0, 0.0);
                double d19 = d4;
                int k2 = list.size();
                for (int j2 = 0; j2 < k2; ++j2) {
                    d19 = ((AxisAlignedBB)list.get(j2)).func_72322_c(axisalignedbb2, d19);
                }
                axisalignedbb2 = axisalignedbb2.func_72317_d(0.0, 0.0, d19);
                AxisAlignedBB axisalignedbb4 = bb;
                double d20 = y;
                int i3 = list.size();
                for (int l2 = 0; l2 < i3; ++l2) {
                    d20 = ((AxisAlignedBB)list.get(l2)).func_72323_b(axisalignedbb4, d20);
                }
                axisalignedbb4 = axisalignedbb4.func_72317_d(0.0, d20, 0.0);
                double d21 = d2;
                int k3 = list.size();
                for (int j3 = 0; j3 < k3; ++j3) {
                    d21 = ((AxisAlignedBB)list.get(j3)).func_72316_a(axisalignedbb4, d21);
                }
                axisalignedbb4 = axisalignedbb4.func_72317_d(d21, 0.0, 0.0);
                double d22 = d4;
                int i4 = list.size();
                for (int l3 = 0; l3 < i4; ++l3) {
                    d22 = ((AxisAlignedBB)list.get(l3)).func_72322_c(axisalignedbb4, d22);
                }
                axisalignedbb4 = axisalignedbb4.func_72317_d(0.0, 0.0, d22);
                double d23 = d18 * d18 + d19 * d19;
                double d9 = d21 * d21 + d22 * d22;
                if (d23 > d9) {
                    x = d18;
                    z = d19;
                    y = -d8;
                    bb = axisalignedbb2;
                } else {
                    x = d21;
                    z = d22;
                    y = -d20;
                    bb = axisalignedbb4;
                }
                int k4 = list.size();
                for (int j4 = 0; j4 < k4; ++j4) {
                    y = ((AxisAlignedBB)list.get(j4)).func_72323_b(bb, y);
                }
                bb = bb.func_72317_d(0.0, y, 0.0);
                if (!(d14 * d14 + d7 * d7 >= x * x + z * z)) {
                    StepEvent event = new StepEvent(bb);
                    GameSense.EVENT_BUS.post(event);
                    if (event.isCancelled()) {
                        mc.field_71439_g.field_70138_W = 0.5f;
                    }
                }
            }
        }
    }
}

