/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.PlayerMoveEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.Timer;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.MotionUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.movement.Anchor;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

@Module.Declaration(name="LongJump", category=Category.Movement)
public class LongJump
extends Module {
    ModeSetting mode = this.registerMode("mode", Arrays.asList("BHop", "Peak", "Ground", "Velocity", "Continuous"), "Peak");
    DoubleSetting speed = this.registerDouble("BHop speed", 2.15, 0.0, 10.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("BHop"));
    DoubleSetting farSpeed = this.registerDouble("Peak Speed", 1.0, 0.0, 10.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Peak"));
    IntegerSetting farAccel = this.registerInteger("Peak Acceleration", 0, 1, 5, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Peak"));
    DoubleSetting initialFar = this.registerDouble("Peak Hop Speed", 1.0, 0.0, 10.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Peak"));
    BooleanSetting jump = this.registerBoolean("Jump", true, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Velocity"));
    DoubleSetting jumpHeightVelo = this.registerDouble("Jump Height Velocity", 1.0, 0.0, 10.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Velocity"));
    BooleanSetting allowY = this.registerBoolean("Velocity Multiply", true, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Velocity"));
    DoubleSetting xzvelocity = this.registerDouble("XZ Velocity Multiplier", 0.1, 0.0, 5.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Velocity"));
    DoubleSetting yvelocity = this.registerDouble("Y Velocity Multiplier", 0.1, 0.0, 2.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Velocity"));
    DoubleSetting factorMax = this.registerDouble("Factor", 0.0, 0.0, 50.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Continuous"));
    DoubleSetting normalSpeed = this.registerDouble("Ground Speed", 3.0, 0.0, 10.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Ground"));
    BooleanSetting lagback = this.registerBoolean("Disable On LagBack", false);
    DoubleSetting jumpHeight = this.registerDouble("jumpHeight", 0.41, 0.0, 1.0);
    Double playerSpeed;
    boolean slowDown;
    boolean hasaccel;
    public boolean velo;
    float mf;
    int i;
    private final Timer timer = new Timer();
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<PacketEvent.Receive>(event -> {
        if (event.getPacket() instanceof SPacketPlayerPosLook && ((Boolean)this.lagback.getValue()).booleanValue()) {
            this.disable();
        }
        if ((event.getPacket() instanceof SPacketExplosion || event.getPacket() instanceof SPacketEntityVelocity) && LongJump.mc.field_71439_g != null) {
            if (((String)this.mode.getValue()).equals("Velocity")) {
                double[] dir = MotionUtil.forward(1.0);
                if (((Boolean)this.jump.getValue()).booleanValue() && LongJump.mc.field_71439_g.field_70122_E) {
                    LongJump.mc.field_71439_g.field_70747_aH = ((Double)this.jumpHeightVelo.getValue()).floatValue();
                    LongJump.mc.field_71439_g.field_70181_x = (Double)this.jumpHeight.getValue();
                }
                this.velo = false;
                if (event.getPacket() instanceof SPacketEntityVelocity && (((Boolean)this.allowY.getValue()).booleanValue() || ((SPacketEntityVelocity)event.getPacket()).field_149416_c <= 0)) {
                    ((SPacketEntityVelocity)event.getPacket()).field_149416_c = (int)((double)((SPacketEntityVelocity)event.getPacket()).field_149416_c * (Double)this.yvelocity.getValue());
                    ((SPacketEntityVelocity)event.getPacket()).field_149415_b = (int)((double)((SPacketEntityVelocity)event.getPacket()).field_149415_b * (Double)this.xzvelocity.getValue() * dir[0]);
                    ((SPacketEntityVelocity)event.getPacket()).field_149414_d = (int)((double)((SPacketEntityVelocity)event.getPacket()).field_149414_d * (Double)this.xzvelocity.getValue() * dir[1]);
                }
                if (event.getPacket() instanceof SPacketExplosion && (((Boolean)this.allowY.getValue()).booleanValue() || !(((SPacketExplosion)event.getPacket()).field_149153_g > 0.0f))) {
                    ((SPacketExplosion)event.getPacket()).field_149153_g = (int)((double)((SPacketExplosion)event.getPacket()).field_149153_g * (Double)this.yvelocity.getValue());
                    ((SPacketExplosion)event.getPacket()).field_149152_f = (int)((double)((SPacketExplosion)event.getPacket()).field_149152_f * (Double)this.xzvelocity.getValue() * dir[0]);
                    ((SPacketExplosion)event.getPacket()).field_149159_h = (int)((double)((SPacketExplosion)event.getPacket()).field_149159_h * (Double)this.xzvelocity.getValue() * dir[1]);
                }
            } else {
                this.velo = true;
            }
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
        if (((String)this.mode.getValue()).equals("BHop")) {
            if (LongJump.mc.field_71439_g.func_180799_ab() || LongJump.mc.field_71439_g.func_70090_H() || LongJump.mc.field_71439_g.func_70617_f_() || LongJump.mc.field_71439_g.field_70134_J || Anchor.active) {
                return;
            }
            double speedY = (Double)this.jumpHeight.getValue();
            if (LongJump.mc.field_71439_g.field_70122_E && MotionUtil.isMoving((EntityLivingBase)LongJump.mc.field_71439_g) && this.timer.hasReached(300L)) {
                if (LongJump.mc.field_71439_g.func_70644_a(MobEffects.field_76430_j)) {
                    speedY += (double)((float)(LongJump.mc.field_71439_g.func_70660_b(MobEffects.field_76430_j).func_76458_c() + 1) * 0.1f);
                }
                LongJump.mc.field_71439_g.field_70181_x = speedY;
                event.setY(LongJump.mc.field_71439_g.field_70181_x);
                this.playerSpeed = MotionUtil.getBaseMoveSpeed() * (EntityUtil.isColliding(0.0, -0.5, 0.0) instanceof BlockLiquid && !EntityUtil.isInLiquid() ? 0.9 : (Double)this.speed.getValue());
                this.slowDown = true;
                this.timer.reset();
            } else if (this.slowDown || LongJump.mc.field_71439_g.field_70123_F) {
                double d;
                double d2 = this.playerSpeed;
                if (EntityUtil.isColliding(0.0, -0.8, 0.0) instanceof BlockLiquid && !EntityUtil.isInLiquid()) {
                    d = 0.4;
                } else {
                    this.playerSpeed = MotionUtil.getBaseMoveSpeed();
                    d = 0.7 * this.playerSpeed;
                }
                this.playerSpeed = d2 - d;
                this.slowDown = false;
            } else {
                this.playerSpeed = this.playerSpeed - this.playerSpeed / 159.0;
            }
            this.playerSpeed = Math.max(this.playerSpeed, MotionUtil.getBaseMoveSpeed());
            double[] dir = MotionUtil.forward(this.playerSpeed);
            event.setX(dir[0]);
            event.setZ(dir[1]);
        }
    }, new Predicate[0]);

    @Override
    public void onEnable() {
        this.playerSpeed = MotionUtil.getBaseMoveSpeed();
        this.mf = LongJump.mc.field_71439_g.field_70747_aH;
    }

    @Override
    public void onDisable() {
        this.timer.reset();
        LongJump.mc.field_71439_g.field_70747_aH = this.mf;
    }

    @Override
    public void onUpdate() {
        double[] dir = MotionUtil.forward(this.playerSpeed);
        if (((String)this.mode.getValue()).equalsIgnoreCase("Peak")) {
            if (LongJump.mc.field_71439_g.field_70122_E) {
                this.hasaccel = false;
            }
            if (LongJump.mc.field_71439_g.field_70122_E && LongJump.mc.field_71474_y.field_74351_w.func_151470_d()) {
                LongJump.mc.field_71439_g.field_70159_w = dir[0] * (double)((Double)this.initialFar.getValue()).floatValue();
                LongJump.mc.field_71439_g.field_70179_y = dir[1] * (double)((Double)this.initialFar.getValue()).floatValue();
                LongJump.mc.field_71439_g.field_70181_x = (Double)this.jumpHeight.getValue();
                this.i = 0;
            }
            if (LongJump.mc.field_71439_g.field_70181_x <= 0.0 && !this.hasaccel) {
                boolean bl = this.hasaccel = !LongJump.mc.field_71439_g.field_70122_E;
                if (((Integer)this.farAccel.getValue()).equals(0)) {
                    LongJump.mc.field_71439_g.field_70747_aH = ((Double)this.farSpeed.getValue()).floatValue();
                } else {
                    ++this.i;
                    LongJump.mc.field_71439_g.field_70747_aH = (float)this.i * (((Double)this.farSpeed.getValue()).floatValue() / (float)((Integer)this.farAccel.getValue()).intValue());
                }
            }
        } else if (((String)this.mode.getValue()).equalsIgnoreCase("Continuous")) {
            if (LongJump.mc.field_71439_g.field_70122_E) {
                if (MotionUtil.isMoving((EntityLivingBase)LongJump.mc.field_71439_g)) {
                    LongJump.mc.field_71439_g.func_70664_aZ();
                }
                LongJump.mc.field_71439_g.field_70747_aH = this.mf;
                LongJump.mc.field_71439_g.field_70181_x = (Double)this.jumpHeight.getValue();
                dir = MotionUtil.forward(MotionUtil.getBaseMoveSpeed());
                LongJump.mc.field_71439_g.field_70159_w = dir[0];
                LongJump.mc.field_71439_g.field_70179_y = dir[1];
            } else {
                LongJump.mc.field_71439_g.field_70747_aH = 0.02f * ((Double)this.factorMax.getValue()).floatValue();
            }
        } else if (((String)this.mode.getValue()).equalsIgnoreCase("Ground") && LongJump.mc.field_71439_g.field_70122_E && MotionUtil.isMoving((EntityLivingBase)LongJump.mc.field_71439_g)) {
            LongJump.mc.field_71439_g.field_70181_x = (Double)this.jumpHeight.getValue();
            LongJump.mc.field_71439_g.field_70159_w = dir[0] * (Double)this.normalSpeed.getValue();
            LongJump.mc.field_71439_g.field_70179_y = dir[1] * (Double)this.normalSpeed.getValue();
        }
    }

    @Override
    public String getHudInfo() {
        return "[" + ChatFormatting.WHITE + (String)this.mode.getValue() + ChatFormatting.GRAY + "]";
    }
}

