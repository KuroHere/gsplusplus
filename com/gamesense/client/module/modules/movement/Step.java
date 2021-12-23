/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.StepEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

@Module.Declaration(name="Step", category=Category.Movement)
public class Step
extends Module {
    ModeSetting mode = this.registerMode("Mode", Arrays.asList("NCP", "Vanilla", "Beta"), "NCP");
    ModeSetting height = this.registerMode("Height", Arrays.asList("1", "1.5", "2", "2.5"), "2.5", () -> ((String)this.mode.getValue()).equalsIgnoreCase("NCP"));
    ModeSetting vHeight = this.registerMode("Height", Arrays.asList("1", "1.5", "2", "2.5"), "2.5", () -> ((String)this.mode.getValue()).equalsIgnoreCase("Vanilla"));
    ModeSetting bHeight = this.registerMode("Height", Arrays.asList("1", "1.5", "2", "2.5"), "2", () -> ((String)this.mode.getValue()).equalsIgnoreCase("Beta"));
    BooleanSetting abnormal = this.registerBoolean("Abnormal", false, () -> !((String)this.mode.getValue()).equalsIgnoreCase("Vanilla"));
    BooleanSetting onGround = this.registerBoolean("On Ground", false);
    BooleanSetting timer = this.registerBoolean("Timer", false, () -> !((String)this.mode.getValue()).equalsIgnoreCase("VANILLA"));
    DoubleSetting multiplier = this.registerDouble("Multiplier", 1.0, 0.0, 3.0, () -> (Boolean)this.timer.getValue() != false && this.timer.isVisible());
    BooleanSetting debug = this.registerBoolean("Debug Height", false);
    double[] pointFiveToOne = new double[]{0.41999998688698};
    double[] one = new double[]{0.41999998688698, 0.7531999805212};
    double[] oneFive = new double[]{0.42, 0.753, 1.001, 1.084, 1.006};
    double[] oneSixTwoFive = new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372};
    double[] oneEightSevenFive = new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652};
    double[] two = new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869};
    double[] twoFive = new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
    double[] betaShared = new double[]{0.419999986887, 0.7531999805212, 1.0013359791121, 1.1661092609382, 1.249187078744682, 1.176759275064238};
    double[] betaTwo = new double[]{1.596759261951216, 1.929959255585439};
    double[] betaTwoFive = new double[]{1.596759261951216, 1.929959255585439, 2.178095254176385, 2.3428685360024515, 2.425946353808919};
    boolean prevTickTimer;
    @EventHandler
    private final Listener<StepEvent> stepEventListener = new Listener<StepEvent>(event -> {
        double step = event.getBB().field_72338_b - Step.mc.field_71439_g.field_70163_u;
        if (((Boolean)this.debug.getValue()).booleanValue()) {
            MessageBus.sendClientPrefixMessageWithID("Stepping " + step + " blocks", Module.getIdFromString("Stepping ... Blocks"));
        }
        if (((String)this.mode.getValue()).equalsIgnoreCase("Vanilla")) {
            return;
        }
        if (((String)this.mode.getValue()).equalsIgnoreCase("NCP")) {
            if (step == 0.625 && ((Boolean)this.abnormal.getValue()).booleanValue()) {
                this.sendOffsets(this.pointFiveToOne);
                if (((Boolean)this.timer.getValue()).booleanValue()) {
                    Step.mc.field_71428_T.field_194149_e = 50.0f * (float)(this.pointFiveToOne.length + 1) * (((Double)this.multiplier.getValue()).floatValue() == 0.0f ? 1.0f : ((Double)this.multiplier.getValue()).floatValue());
                    this.prevTickTimer = true;
                }
            } else if (step == 1.0 || (step == 0.875 || step == 1.0625 || step == 0.9375) && ((Boolean)this.abnormal.getValue()).booleanValue()) {
                this.sendOffsets(this.one);
                if (((Boolean)this.timer.getValue()).booleanValue()) {
                    Step.mc.field_71428_T.field_194149_e = 50.0f * (float)(this.one.length + 1) * (((Double)this.multiplier.getValue()).floatValue() == 0.0f ? 1.0f : ((Double)this.multiplier.getValue()).floatValue());
                    this.prevTickTimer = true;
                }
            } else if (step == 1.5) {
                this.sendOffsets(this.oneFive);
                if (((Boolean)this.timer.getValue()).booleanValue()) {
                    Step.mc.field_71428_T.field_194149_e = 50.0f * (float)(this.oneFive.length + 1) * (((Double)this.multiplier.getValue()).floatValue() == 0.0f ? 1.0f : ((Double)this.multiplier.getValue()).floatValue());
                    this.prevTickTimer = true;
                }
            } else if (step == 1.875 && ((Boolean)this.abnormal.getValue()).booleanValue()) {
                this.sendOffsets(this.oneEightSevenFive);
                if (((Boolean)this.timer.getValue()).booleanValue()) {
                    Step.mc.field_71428_T.field_194149_e = 50.0f * (float)(this.oneEightSevenFive.length + 1) * (((Double)this.multiplier.getValue()).floatValue() == 0.0f ? 1.0f : ((Double)this.multiplier.getValue()).floatValue());
                    this.prevTickTimer = true;
                }
            } else if (step == 1.625 && ((Boolean)this.abnormal.getValue()).booleanValue()) {
                this.sendOffsets(this.oneSixTwoFive);
                if (((Boolean)this.timer.getValue()).booleanValue()) {
                    Step.mc.field_71428_T.field_194149_e = 50.0f * (float)(this.oneSixTwoFive.length + 1) * (((Double)this.multiplier.getValue()).floatValue() == 0.0f ? 1.0f : ((Double)this.multiplier.getValue()).floatValue());
                    this.prevTickTimer = true;
                }
            } else if (step == 2.0) {
                this.sendOffsets(this.two);
                if (((Boolean)this.timer.getValue()).booleanValue()) {
                    Step.mc.field_71428_T.field_194149_e = 50.0f * (float)(this.two.length + 1) * (((Double)this.multiplier.getValue()).floatValue() == 0.0f ? 1.0f : ((Double)this.multiplier.getValue()).floatValue());
                    this.prevTickTimer = true;
                }
            } else if (step == 2.5) {
                this.sendOffsets(this.twoFive);
                if (((Boolean)this.timer.getValue()).booleanValue()) {
                    Step.mc.field_71428_T.field_194149_e = 50.0f * (float)(this.twoFive.length + 1) * (((Double)this.multiplier.getValue()).floatValue() == 0.0f ? 1.0f : ((Double)this.multiplier.getValue()).floatValue());
                    this.prevTickTimer = true;
                }
            } else {
                event.cancel();
            }
        } else if (((String)this.mode.getValue()).equalsIgnoreCase("Beta")) {
            if (step == 1.5) {
                this.sendOffsets(this.betaShared);
                if (((Boolean)this.timer.getValue()).booleanValue()) {
                    Step.mc.field_71428_T.field_194149_e = 50.0f * (float)(this.betaShared.length + 1) * (((Double)this.multiplier.getValue()).floatValue() == 0.0f ? 1.0f : ((Double)this.multiplier.getValue()).floatValue());
                    this.prevTickTimer = true;
                }
            } else if (step == 2.0) {
                this.sendOffsets(this.betaShared);
                this.sendOffsets(this.betaTwo);
                if (((Boolean)this.timer.getValue()).booleanValue()) {
                    Step.mc.field_71428_T.field_194149_e = 50.0f * (float)(this.betaShared.length + this.betaTwo.length + 1) * (((Double)this.multiplier.getValue()).floatValue() == 0.0f ? 1.0f : ((Double)this.multiplier.getValue()).floatValue());
                    this.prevTickTimer = true;
                }
            } else if (step == 2.5) {
                this.sendOffsets(this.betaShared);
                this.sendOffsets(this.betaTwoFive);
                if (((Boolean)this.timer.getValue()).booleanValue()) {
                    Step.mc.field_71428_T.field_194149_e = 50.0f * (float)(this.betaShared.length + this.betaTwoFive.length + 1) * (((Double)this.multiplier.getValue()).floatValue() == 0.0f ? 1.0f : ((Double)this.multiplier.getValue()).floatValue());
                    this.prevTickTimer = true;
                }
            } else {
                event.cancel();
            }
        }
    }, new Predicate[0]);

    @Override
    public void onUpdate() {
        float f = Step.mc.field_71439_g.field_70138_W = (Boolean)this.onGround.getValue() != false && !Step.mc.field_71439_g.field_70122_E ? 0.5f : this.getHeight((String)this.mode.getValue());
        if (this.prevTickTimer) {
            this.prevTickTimer = false;
            Step.mc.field_71428_T.field_194149_e = 50.0f;
        }
    }

    float getHeight(String mode) {
        return Float.parseFloat(mode.equals("Beta") ? (String)this.bHeight.getValue() : (mode.equals("Vanilla") ? (String)this.vHeight.getValue() : (String)this.height.getValue()));
    }

    @Override
    protected void onDisable() {
        Step.mc.field_71439_g.field_70138_W = 0.5f;
        Step.mc.field_71428_T.field_194149_e = 50.0f;
    }

    void sendOffsets(double[] offsets) {
        for (double i : offsets) {
            Step.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(Step.mc.field_71439_g.field_70165_t, Step.mc.field_71439_g.field_70163_u + i + 0.0, Step.mc.field_71439_g.field_70161_v, false));
        }
    }
}

