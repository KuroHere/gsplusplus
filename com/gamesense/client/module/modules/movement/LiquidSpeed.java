/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

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
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.TickShift;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

@Module.Declaration(name="Liquid Speed", category=Category.Movement)
public class LiquidSpeed
extends Module {
    DoubleSetting timerVal = this.registerDouble("Timer Speed", 1.0, 1.0, 2.0);
    DoubleSetting XZWater = this.registerDouble("XZ Water", 1.0, 1.0, 5.0);
    DoubleSetting YPWater = this.registerDouble("Y+ Water", 1.0, 1.0, 5.0);
    DoubleSetting YMWater = this.registerDouble("Y- Water", 1.0, 0.0, 10.0);
    ModeSetting YWaterMotion = this.registerMode("Y Water motion", Arrays.asList("None", "Zero", "Bounding", "Min"), "None");
    IntegerSetting magnitudeMinWater = this.registerInteger("Magnitude Min Water", 0, 0, 6);
    DoubleSetting XZLava = this.registerDouble("XZ Lava", 1.0, 1.0, 5.0);
    DoubleSetting YPLava = this.registerDouble("Y+ Lava", 1.0, 1.0, 5.0);
    DoubleSetting YMLava = this.registerDouble("Y- Lava", 1.0, 0.0, 10.0);
    ModeSetting YLavaMotion = this.registerMode("Y Lava motion", Arrays.asList("None", "Zero", "Bounding", "Min"), "None");
    IntegerSetting magnitudeMinLava = this.registerInteger("Magnitude Min Lava", 0, 0, 6);
    BooleanSetting groundIgnore = this.registerBoolean("Ground Ignore", true);
    private boolean slowDown;
    private double playerSpeed;
    private final Timer timer = new Timer();
    boolean beforeUp = true;
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
        Double velY;
        if (LiquidSpeed.mc.field_71439_g == null || LiquidSpeed.mc.field_71441_e == null) {
            return;
        }
        Boolean isMovingUp = LiquidSpeed.mc.field_71474_y.field_74314_A.func_151470_d();
        Boolean isMovingDown = LiquidSpeed.mc.field_71474_y.field_74311_E.func_151470_d();
        Double velX = event.getX();
        Double memY = velY = Double.valueOf(event.getY());
        Double velZ = event.getZ();
        if (((Boolean)this.groundIgnore.getValue()).booleanValue() || !LiquidSpeed.mc.field_71439_g.field_70122_E) {
            if (LiquidSpeed.mc.field_71439_g.func_70090_H()) {
                if (!ModuleManager.isModuleEnabled(TickShift.class) && (Double)this.timerVal.getValue() != 1.0) {
                    EntityUtil.setTimer(((Double)this.timerVal.getValue()).floatValue());
                }
                velX = velX * (Double)this.XZWater.getValue();
                velY = velY * (isMovingUp != false ? (Double)this.YPWater.getValue() : (Double)this.YMWater.getValue());
                velZ = velZ * (Double)this.XZWater.getValue();
                if (!isMovingUp.booleanValue() && !isMovingDown.booleanValue()) {
                    switch ((String)this.YWaterMotion.getValue()) {
                        case "Zero": {
                            velY = 0.0;
                            break;
                        }
                        case "Bounding": {
                            velY = memY;
                            if (this.beforeUp) {
                                velY = velY * -1.0;
                            }
                            this.beforeUp = !this.beforeUp;
                            break;
                        }
                        case "Min": {
                            velY = this.getMagnitude((Integer)this.magnitudeMinWater.getValue());
                            if (this.beforeUp) {
                                velY = velY * -1.0;
                            }
                            boolean bl = this.beforeUp = !this.beforeUp;
                        }
                    }
                }
            }
            if (LiquidSpeed.mc.field_71439_g.func_180799_ab()) {
                if (!ModuleManager.isModuleEnabled(TickShift.class) && (Double)this.timerVal.getValue() != 1.0) {
                    EntityUtil.setTimer(((Double)this.timerVal.getValue()).floatValue());
                }
                velX = velX * (Double)this.XZLava.getValue();
                velY = velY * (isMovingUp != false ? (Double)this.YPLava.getValue() : (Double)this.YMLava.getValue());
                velZ = velZ * (Double)this.XZLava.getValue();
                if (!isMovingUp.booleanValue() && !isMovingDown.booleanValue()) {
                    switch ((String)this.YLavaMotion.getValue()) {
                        case "Zero": {
                            velY = 0.0;
                            break;
                        }
                        case "Bounding": {
                            velY = memY;
                            if (this.beforeUp) {
                                velY = velY * -1.0;
                            }
                            this.beforeUp = !this.beforeUp;
                            break;
                        }
                        case "Min": {
                            velY = this.getMagnitude((Integer)this.magnitudeMinLava.getValue());
                            if (this.beforeUp) {
                                velY = velY * -1.0;
                            }
                            this.beforeUp = !this.beforeUp;
                        }
                    }
                }
            }
        }
        event.setX(velX);
        event.setY(velY);
        event.setZ(velZ);
    }, new Predicate[0]);

    @Override
    public void onEnable() {
        this.playerSpeed = MotionUtil.getBaseMoveSpeed();
    }

    @Override
    public void onDisable() {
        this.timer.reset();
        EntityUtil.resetTimer();
    }

    double getMagnitude(int level) {
        return 1.0 / Math.pow(10.0, level / 2);
    }
}

