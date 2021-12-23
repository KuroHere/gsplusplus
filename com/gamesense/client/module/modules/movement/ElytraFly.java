/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.PlayerMoveEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.Timer;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.util.world.MotionUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec2f;

@Module.Declaration(name="ElytraFly", category=Category.Movement)
public class ElytraFly
extends Module {
    public BooleanSetting sound = this.registerBoolean("Sounds", true);
    ModeSetting mode = this.registerMode("Mode", Arrays.asList("Control", "Packet", "Boost"), "Boost");
    BooleanSetting replace = this.registerBoolean("Replace", false);
    ModeSetting toMode = this.registerMode("Takeoff", Arrays.asList("PacketFly", "Timer", "Freeze", "Fast", "None"), "PacketFly");
    ModeSetting upMode = this.registerMode("Up Mode", Arrays.asList("Jump", "Aim"), "Jump", () -> !((String)this.mode.getValue()).equals("Boost"));
    DoubleSetting speed = this.registerDouble("Speed", 2.5, 0.0, 10.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Control"));
    DoubleSetting ySpeed = this.registerDouble("Y Speed", 0.0, 1.0, 10.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Control"));
    DoubleSetting glideSpeed = this.registerDouble("Glide Speed", 0.0, 0.0, 3.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Control"));
    BooleanSetting yawLock = this.registerBoolean("Yaw Lock", false, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Control"));
    BooleanSetting pursue = this.registerBoolean("Pursue", false, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Control") && ((String)this.upMode.getValue()).equalsIgnoreCase("Jump"));
    BooleanSetting build = this.registerBoolean("Build Height", false, () -> (Boolean)this.pursue.getValue() != false && this.pursue.isVisible());
    int ticks;
    boolean setAng;
    boolean shouldEflyPacket;
    @EventHandler
    private final Listener<PacketEvent.Send> packetSendListener = new Listener<PacketEvent.Send>(event -> {
        if (event.getPacket() instanceof CPacketPlayer && this.setAng && !((String)this.mode.getValue()).equalsIgnoreCase("Boost")) {
            ((CPacketPlayer)event.getPacket()).field_149473_f = 0.0f;
        }
    }, new Predicate[0]);
    Timer upTimer = new Timer();
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
        if (ElytraFly.mc.field_71439_g.func_184613_cA()) {
            ElytraFly.mc.field_71428_T.field_194149_e = 50.0f;
            if (((String)this.mode.getValue()).equals("Boost")) {
                if (ElytraFly.mc.field_71474_y.field_74314_A.func_151470_d() || ElytraFly.mc.field_71474_y.field_74351_w.func_151470_d()) {
                    float yaw = (float)Math.toRadians(ElytraFly.mc.field_71439_g.field_70177_z);
                    ElytraFly.mc.field_71439_g.field_70159_w -= Math.sin(yaw) * (double)0.05f;
                    ElytraFly.mc.field_71439_g.field_70179_y += Math.cos(yaw) * (double)0.05f;
                }
            } else if (((String)this.mode.getValue()).equals("Control")) {
                if (((String)this.upMode.getValue()).equalsIgnoreCase("Jump")) {
                    if (ElytraFly.mc.field_71474_y.field_74314_A.func_151470_d()) {
                        event.setY((Double)this.ySpeed.getValue());
                    } else if (ElytraFly.mc.field_71474_y.field_74311_E.func_151470_d()) {
                        event.setY(-((Double)this.ySpeed.getValue()).doubleValue());
                    } else {
                        event.setY(-1.0E-6 - (Double)this.glideSpeed.getValue());
                    }
                    if (ElytraFly.mc.field_71474_y.field_74351_w.func_151470_d() || ElytraFly.mc.field_71474_y.field_74368_y.func_151470_d() || ElytraFly.mc.field_71474_y.field_74370_x.func_151470_d() || ElytraFly.mc.field_71474_y.field_74366_z.func_151470_d()) {
                        double[] dir;
                        if (!((Boolean)this.yawLock.getValue()).booleanValue()) {
                            dir = MotionUtil.forward((Double)this.speed.getValue());
                        } else {
                            int angle = 45;
                            float yaw = ElytraFly.mc.field_71439_g.field_70177_z;
                            yaw = Math.round(yaw / 45.0f) * 45;
                            dir = MotionUtil.forward((Double)this.speed.getValue(), yaw);
                        }
                        ElytraFly.mc.field_71439_g.field_70159_w = dir[0];
                        ElytraFly.mc.field_71439_g.field_70179_y = dir[1];
                        event.setX(dir[0]);
                        event.setZ(dir[1]);
                    } else if (!((Boolean)this.pursue.getValue()).booleanValue()) {
                        event.setX(0.0);
                        event.setZ(0.0);
                    } else if (ElytraFly.mc.field_71439_g.field_70163_u > 256.0 || !((Boolean)this.build.getValue()).booleanValue()) {
                        EntityPlayer target = PlayerUtil.findClosestTarget(696969.0, null);
                        if (target != null) {
                            Vec2f rot = RotationUtil.getRotationTo(target.func_174791_d());
                            double[] dir = MotionUtil.forward(Math.min((Double)this.speed.getValue(), (double)ElytraFly.mc.field_71439_g.func_70032_d((Entity)target)), rot.field_189982_i);
                            ElytraFly.mc.field_71439_g.func_70016_h(dir[0], ElytraFly.mc.field_71439_g.field_70181_x, dir[1]);
                            if (ElytraFly.mc.field_71439_g.field_70163_u > target.field_70163_u) {
                                event.setY(-((Double)this.ySpeed.getValue()).doubleValue());
                            } else if (ElytraFly.mc.field_71439_g.field_70163_u < target.field_70163_u) {
                                event.setY(Math.min((Double)this.ySpeed.getValue(), target.field_70163_u));
                            } else {
                                event.setY(0.0);
                            }
                        } else {
                            event.setX(0.0);
                            event.setZ(0.0);
                        }
                    }
                } else if (((String)this.upMode.getValue()).equalsIgnoreCase("Aim")) {
                    if (ElytraFly.mc.field_71439_g.field_70125_A > 0.0f || this.upTimer.getTimePassed() >= 1500L) {
                        this.upTimer.reset();
                        if (ElytraFly.mc.field_71474_y.field_74311_E.func_151470_d()) {
                            event.setY(-((Double)this.ySpeed.getValue()).doubleValue());
                        } else {
                            event.setY(-((Double)this.glideSpeed.getValue()).doubleValue());
                        }
                        if (ElytraFly.mc.field_71474_y.field_74351_w.func_151470_d() || ElytraFly.mc.field_71474_y.field_74368_y.func_151470_d() || ElytraFly.mc.field_71474_y.field_74370_x.func_151470_d() || ElytraFly.mc.field_71474_y.field_74366_z.func_151470_d()) {
                            double[] dir;
                            if (!((Boolean)this.yawLock.getValue()).booleanValue()) {
                                dir = MotionUtil.forward((Double)this.speed.getValue());
                            } else {
                                int angle = 45;
                                float yaw = ElytraFly.mc.field_71439_g.field_70177_z;
                                yaw = Math.round(yaw / 45.0f) * 45;
                                dir = MotionUtil.forward((Double)this.speed.getValue(), yaw);
                            }
                            ElytraFly.mc.field_71439_g.field_70159_w = dir[0];
                            ElytraFly.mc.field_71439_g.field_70179_y = dir[1];
                            event.setX(dir[0]);
                            event.setZ(dir[1]);
                        } else {
                            event.setX(0.0);
                            event.setZ(0.0);
                        }
                        this.setAng = true;
                        ++this.ticks;
                    } else {
                        this.setAng = false;
                    }
                }
            } else if (((String)this.mode.getValue()).equalsIgnoreCase("Packet")) {
                boolean bl = this.shouldEflyPacket = !ElytraFly.mc.field_71439_g.field_70122_E;
                if (this.shouldEflyPacket) {
                    this.setAng = true;
                    event.setY(-1.0E-6 - (Double)this.glideSpeed.getValue());
                    ElytraFly.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)ElytraFly.mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
                    if (ElytraFly.mc.field_71474_y.field_74351_w.func_151470_d() || ElytraFly.mc.field_71474_y.field_74368_y.func_151470_d() || ElytraFly.mc.field_71474_y.field_74370_x.func_151470_d() || ElytraFly.mc.field_71474_y.field_74366_z.func_151470_d()) {
                        double[] dir;
                        if (!((Boolean)this.yawLock.getValue()).booleanValue()) {
                            dir = MotionUtil.forward((Double)this.speed.getValue());
                        } else {
                            int angle = 45;
                            float yaw = ElytraFly.mc.field_71439_g.field_70177_z;
                            yaw = Math.round(yaw / 45.0f) * 45;
                            dir = MotionUtil.forward((Double)this.speed.getValue(), yaw);
                        }
                        event.setX(dir[0]);
                        event.setZ(dir[1]);
                    } else {
                        event.setX(0.0);
                        event.setZ(0.0);
                    }
                }
            }
        } else if (ElytraFly.mc.field_71474_y.field_74314_A.func_151470_d() && ((ItemStack)ElytraFly.mc.field_71439_g.field_71071_by.field_70460_b.get(2)).func_77973_b().equals(Items.field_185160_cR) && !this.shouldEflyPacket) {
            switch ((String)this.toMode.getValue()) {
                case "PacketFly": {
                    if (ElytraFly.mc.field_71439_g.field_70122_E) {
                        ElytraFly.mc.field_71439_g.func_70664_aZ();
                        break;
                    }
                    if (!(ElytraFly.mc.field_71439_g.field_70181_x < 0.0)) break;
                    ElytraFly.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(ElytraFly.mc.field_71439_g.field_70165_t + ElytraFly.mc.field_71439_g.field_70159_w, ElytraFly.mc.field_71439_g.field_70163_u - 0.0025, ElytraFly.mc.field_71439_g.field_70161_v + ElytraFly.mc.field_71439_g.field_70179_y, ElytraFly.mc.field_71439_g.field_70177_z, ElytraFly.mc.field_71439_g.field_70125_A, false));
                    ElytraFly.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(ElytraFly.mc.field_71439_g.field_70165_t + 55.0, ElytraFly.mc.field_71439_g.field_70163_u, ElytraFly.mc.field_71439_g.field_70161_v + 55.0, ElytraFly.mc.field_71439_g.field_70177_z, ElytraFly.mc.field_71439_g.field_70125_A, false));
                    ElytraFly.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)ElytraFly.mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
                    break;
                }
                case "Timer": {
                    if (ElytraFly.mc.field_71439_g.field_70122_E) {
                        ElytraFly.mc.field_71439_g.func_70664_aZ();
                        break;
                    }
                    if (!(ElytraFly.mc.field_71439_g.field_70181_x < 0.0)) break;
                    ElytraFly.mc.field_71428_T.field_194149_e = 300.0f;
                    ElytraFly.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)ElytraFly.mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
                    break;
                }
                case "Fast": {
                    if (ElytraFly.mc.field_71439_g.field_70122_E) {
                        ElytraFly.mc.field_71439_g.func_70664_aZ();
                        break;
                    }
                    if (!(ElytraFly.mc.field_71439_g.field_70181_x < 0.0)) break;
                    ElytraFly.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)ElytraFly.mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
                    break;
                }
                case "Freeze": {
                    if (ElytraFly.mc.field_71439_g.field_70122_E) {
                        ElytraFly.mc.field_71439_g.func_70664_aZ();
                        break;
                    }
                    if (!(ElytraFly.mc.field_71439_g.field_70181_x < 0.0)) break;
                    event.setY(-1.0E-5);
                    ElytraFly.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)ElytraFly.mc.field_71439_g, CPacketEntityAction.Action.START_FALL_FLYING));
                }
            }
        }
    }, new Predicate[0]);

    @Override
    protected void onEnable() {
        if (((Boolean)this.replace.getValue()).booleanValue() && !ElytraFly.mc.field_71439_g.field_71071_by.func_70440_f(2).func_77973_b().equals(Items.field_185160_cR) && InventoryUtil.findFirstItemSlot(Items.field_185160_cR.getClass(), 0, 35) != -1) {
            InventoryUtil.swap(InventoryUtil.findFirstItemSlot(Items.field_185160_cR.getClass(), 0, 35), 6);
        }
    }

    @Override
    protected void onDisable() {
        ElytraFly.mc.field_71428_T.field_194149_e = 50.0f;
        if (((Boolean)this.replace.getValue()).booleanValue() && ElytraFly.mc.field_71439_g.field_71071_by.func_70440_f(2).func_77973_b().equals(Items.field_185160_cR) && InventoryUtil.findFirstItemSlot(Items.field_190931_a.getClass(), 0, 35) != -1) {
            InventoryUtil.swap(6, InventoryUtil.findFirstItemSlot(Items.field_190931_a.getClass(), 0, 35));
        }
    }
}

