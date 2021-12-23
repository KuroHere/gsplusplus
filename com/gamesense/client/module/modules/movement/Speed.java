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
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.MotionUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.Anchor;
import com.gamesense.client.module.modules.movement.Timer;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

@Module.Declaration(name="Speed", category=Category.Movement)
public class Speed
extends Module {
    private final com.gamesense.api.util.misc.Timer timer = new com.gamesense.api.util.misc.Timer();
    public int yl;
    ModeSetting mode = this.registerMode("Mode", Arrays.asList("Strafe", "GroundStrafe", "OnGround", "Fake", "YPort"), "Strafe");
    DoubleSetting speed = this.registerDouble("Speed", 2.0, 0.0, 10.0, () -> ((String)this.mode.getValue()).equals("Strafe") || ((String)this.mode.getValue()).equalsIgnoreCase("Beta"));
    BooleanSetting jump = this.registerBoolean("Jump", true, () -> ((String)this.mode.getValue()).equals("Strafe") || ((String)this.mode.getValue()).equalsIgnoreCase("Beta"));
    BooleanSetting boost = this.registerBoolean("Boost", false, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Strafe") || ((String)this.mode.getValue()).equalsIgnoreCase("Beta"));
    DoubleSetting multiply = this.registerDouble("Multiply", 0.8, 0.1, 1.0, () -> (Boolean)this.boost.getValue() != false && this.boost.isVisible());
    DoubleSetting max = this.registerDouble("Maximum", 0.5, 0.0, 1.0, () -> (Boolean)this.boost.getValue() != false && this.boost.isVisible());
    DoubleSetting gSpeed = this.registerDouble("Ground Speed", 0.3, 0.0, 0.5, () -> ((String)this.mode.getValue()).equals("GroundStrafe"));
    DoubleSetting yPortSpeed = this.registerDouble("Speed YPort", 0.06, 0.01, 0.15, () -> ((String)this.mode.getValue()).equals("YPort"));
    DoubleSetting onGroundSpeed = this.registerDouble("Speed OnGround", 1.5, 0.01, 3.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("OnGround"));
    BooleanSetting strictOG = this.registerBoolean("Head Block Only", false, () -> ((String)this.mode.getValue()).equalsIgnoreCase("OnGround"));
    DoubleSetting jumpHeight = this.registerDouble("Jump Speed", 0.41, 0.0, 1.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Strafe") && (Boolean)this.jump.getValue() != false || ((String)this.mode.getValue()).equalsIgnoreCase("Beta") && (Boolean)this.jump.getValue() != false);
    IntegerSetting jumpDelay = this.registerInteger("Jump Delay", 300, 0, 1000, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Strafe") && (Boolean)this.jump.getValue() != false || ((String)this.mode.getValue()).equalsIgnoreCase("Beta") && (Boolean)this.jump.getValue() != false);
    BooleanSetting useTimer = this.registerBoolean("Timer", false);
    DoubleSetting timerVal = this.registerDouble("Timer Speed", 1.088, 0.8, 1.2);
    private boolean slowDown;
    private double playerSpeed;
    private double velocity;
    com.gamesense.api.util.misc.Timer kbTimer = new com.gamesense.api.util.misc.Timer();
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
        if (Speed.mc.field_71439_g.func_180799_ab() || Speed.mc.field_71439_g.func_70090_H() || Speed.mc.field_71439_g.func_70617_f_() || Speed.mc.field_71439_g.field_70134_J || Anchor.active) {
            return;
        }
        if (((String)this.mode.getValue()).equalsIgnoreCase("Strafe")) {
            double speedY = (Double)this.jumpHeight.getValue();
            if (Speed.mc.field_71439_g.field_70122_E && ((Boolean)this.jump.getValue()).booleanValue()) {
                Speed.mc.field_71439_g.func_70664_aZ();
            }
            if (Speed.mc.field_71439_g.field_70122_E && MotionUtil.isMoving((EntityLivingBase)Speed.mc.field_71439_g) && this.timer.hasReached(((Integer)this.jumpDelay.getValue()).intValue())) {
                if (Speed.mc.field_71439_g.func_70644_a(MobEffects.field_76430_j)) {
                    speedY += (double)((float)(Objects.requireNonNull(Speed.mc.field_71439_g.func_70660_b(MobEffects.field_76430_j)).func_76458_c() + 1) * 0.1f);
                }
                if (((Boolean)this.jump.getValue()).booleanValue()) {
                    Speed.mc.field_71439_g.field_70181_x = speedY;
                    event.setY(Speed.mc.field_71439_g.field_70181_x);
                }
                this.playerSpeed = MotionUtil.getBaseMoveSpeed() * (EntityUtil.isColliding(0.0, -0.5, 0.0) instanceof BlockLiquid && !EntityUtil.isInLiquid() ? 0.91 : (Double)this.speed.getValue());
                this.slowDown = true;
                this.timer.reset();
            } else if (this.slowDown || Speed.mc.field_71439_g.field_70123_F) {
                double d;
                if (EntityUtil.isColliding(0.0, -0.8, 0.0) instanceof BlockLiquid && !EntityUtil.isInLiquid()) {
                    d = 0.4;
                } else {
                    this.playerSpeed = MotionUtil.getBaseMoveSpeed();
                    d = 0.7 * this.playerSpeed;
                }
                this.playerSpeed -= d;
                this.slowDown = false;
            } else {
                this.playerSpeed -= this.playerSpeed / 159.0;
            }
            this.playerSpeed = Math.max(this.playerSpeed, MotionUtil.getBaseMoveSpeed());
            if (((Boolean)this.boost.getValue()).booleanValue() && !this.kbTimer.hasReached(50L)) {
                this.playerSpeed += Math.min(this.velocity * (Double)this.multiply.getValue(), (Double)this.max.getValue());
            }
            double[] dir = MotionUtil.forward(this.playerSpeed);
            event.setX(dir[0]);
            event.setZ(dir[1]);
        }
        if (((String)this.mode.getValue()).equalsIgnoreCase("GroundStrafe")) {
            this.playerSpeed = (Double)this.gSpeed.getValue();
            this.playerSpeed *= MotionUtil.getBaseMoveSpeed() / 0.2873;
            if (Speed.mc.field_71439_g.field_70122_E) {
                double[] dir = MotionUtil.forward(this.playerSpeed);
                event.setX(dir[0]);
                event.setZ(dir[1]);
            }
        } else if (((String)this.mode.getValue()).equalsIgnoreCase("OnGround")) {
            if (Speed.mc.field_71439_g.field_70123_F) {
                return;
            }
            double offset = 0.4;
            if (Speed.mc.field_71441_e.func_184143_b(Speed.mc.field_71439_g.field_70121_D.func_72317_d(0.0, 0.4, 0.0))) {
                offset = 2.0 - Speed.mc.field_71439_g.field_70121_D.field_72337_e;
            } else if (((Boolean)this.strictOG.getValue()).booleanValue()) {
                return;
            }
            Speed.mc.field_71439_g.field_70163_u -= offset;
            Speed.mc.field_71439_g.field_70181_x = -1000.0;
            Speed.mc.field_71439_g.field_70726_aT = 0.3f;
            Speed.mc.field_71439_g.field_70140_Q = 44.0f;
            if (Speed.mc.field_71439_g.field_70122_E) {
                Speed.mc.field_71439_g.field_70163_u += offset;
                Speed.mc.field_71439_g.field_70181_x = offset;
                Speed.mc.field_71439_g.field_82151_R = 44.0f;
                Speed.mc.field_71439_g.field_70159_w *= ((Double)this.onGroundSpeed.getValue()).doubleValue();
                Speed.mc.field_71439_g.field_70179_y *= ((Double)this.onGroundSpeed.getValue()).doubleValue();
                Speed.mc.field_71439_g.field_70726_aT = 0.0f;
            }
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<PacketEvent.Receive>(event -> {
        if (event.getPacket() instanceof SPacketExplosion) {
            this.velocity = Math.abs(((SPacketExplosion)event.getPacket()).field_149152_f) + Math.abs(((SPacketExplosion)event.getPacket()).field_149159_h);
            this.kbTimer.reset();
        }
        if (event.getPacket() instanceof SPacketEntityVelocity) {
            if (((SPacketEntityVelocity)event.getPacket()).func_149412_c() != Speed.mc.field_71439_g.func_145782_y()) {
                return;
            }
            if (this.velocity < (double)(Math.abs(((SPacketEntityVelocity)event.getPacket()).field_149415_b) + Math.abs(((SPacketEntityVelocity)event.getPacket()).field_149414_d))) {
                this.velocity = Math.abs(((SPacketEntityVelocity)event.getPacket()).field_149415_b) + Math.abs(((SPacketEntityVelocity)event.getPacket()).field_149414_d);
                this.kbTimer.reset();
            }
        }
    }, new Predicate[0]);

    @Override
    public void onEnable() {
        this.playerSpeed = MotionUtil.getBaseMoveSpeed();
        this.yl = (int)Speed.mc.field_71439_g.field_70163_u;
    }

    @Override
    public void onDisable() {
        this.timer.reset();
    }

    @Override
    public void onUpdate() {
        if (Speed.mc.field_71439_g == null || Speed.mc.field_71441_e == null) {
            this.disable();
            return;
        }
        if (Anchor.active) {
            return;
        }
        if (((String)this.mode.getValue()).equalsIgnoreCase("YPort")) {
            this.handleYPortSpeed();
        }
        if (!ModuleManager.isModuleEnabled(Timer.class) && ((Boolean)this.useTimer.getValue()).booleanValue()) {
            Speed.mc.field_71428_T.field_194149_e = 50.0f / ((Double)this.timerVal.getValue()).floatValue();
        }
    }

    private void handleYPortSpeed() {
        if (!MotionUtil.isMoving((EntityLivingBase)Speed.mc.field_71439_g) || Speed.mc.field_71439_g.func_70090_H() && Speed.mc.field_71439_g.func_180799_ab() || Speed.mc.field_71439_g.field_70123_F) {
            return;
        }
        if (Speed.mc.field_71439_g.field_70122_E) {
            Speed.mc.field_71439_g.func_70664_aZ();
            MotionUtil.setSpeed((EntityLivingBase)Speed.mc.field_71439_g, MotionUtil.getBaseMoveSpeed() + (Double)this.yPortSpeed.getValue());
        } else {
            Speed.mc.field_71439_g.field_70181_x = -1.0;
        }
    }

    @Override
    public String getHudInfo() {
        return "[" + ChatFormatting.WHITE + (String)this.mode.getValue() + ChatFormatting.GRAY + "]";
    }
}

