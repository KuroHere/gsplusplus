/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.StringSetting;
import com.gamesense.api.util.misc.KeyBoardClass;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.MotionUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

@Module.Declaration(name="TickShift", category=Category.Movement)
public class TickShift
extends Module {
    IntegerSetting limit = this.registerInteger("Limit", 16, 1, 50);
    DoubleSetting minSpeed = this.registerDouble("Min Speed", 1.0, 0.0, 1.0);
    DoubleSetting timer = this.registerDouble("Timer", 2.0, 1.0, 5.0);
    BooleanSetting doDecay = this.registerBoolean("Decay", false);
    DoubleSetting min = this.registerDouble("Lowest", 1.4, 1.0, 5.0, () -> (Boolean)this.doDecay.getValue());
    StringSetting onClick = this.registerString("onClick", "");
    int ticks;

    @Override
    protected void onEnable() {
        TickShift.mc.field_71428_T.field_194149_e = 50.0f;
        this.ticks = 0;
    }

    @Override
    protected void onDisable() {
        TickShift.mc.field_71428_T.field_194149_e = 50.0f;
    }

    @Override
    public void onUpdate() {
        if (this.isMoving()) {
            if (MotionUtil.getMotion((EntityPlayer)TickShift.mc.field_71439_g) > (Double)this.minSpeed.getValue() * MotionUtil.getBaseMoveSpeed()) {
                if (this.ticks > 0 && !PlayerUtil.isPlayerClipped()) {
                    double ourTimer = 1.0;
                    double diff = (Double)this.timer.getValue() - (Double)this.min.getValue();
                    double steps = diff / (double)((Integer)this.limit.getValue()).intValue();
                    ourTimer = (Boolean)this.doDecay.getValue() != false ? (Double)this.min.getValue() + steps : (Double)this.timer.getValue();
                    String bind = this.onClick.getText();
                    if (this.ticks > 0 && (bind.length() == 0 || Keyboard.isKeyDown((int)KeyBoardClass.getKeyFromChar(bind.charAt(0))))) {
                        float f = TickShift.mc.field_71428_T.field_194149_e = (Boolean)this.doDecay.getValue() != false ? (float)Math.max(50.0 / ourTimer, 50.0) : 50.0f / ((Double)this.timer.getValue()).floatValue();
                    }
                }
                if (this.ticks > 0) {
                    --this.ticks;
                }
            }
        } else {
            if (!MotionUtil.isMoving((EntityLivingBase)TickShift.mc.field_71439_g)) {
                TickShift.mc.field_71439_g.field_70159_w = 0.0;
                TickShift.mc.field_71439_g.field_70179_y = 0.0;
            }
            TickShift.mc.field_71428_T.field_194149_e = 50.0f;
            if (this.ticks < (Integer)this.limit.getValue()) {
                ++this.ticks;
            }
        }
    }

    @Override
    public String getHudInfo() {
        return TextFormatting.WHITE + "[" + this.getColour(this.ticks) + this.ticks + TextFormatting.WHITE + "]";
    }

    public TextFormatting getColour(int ticks) {
        if (ticks == 0) {
            return TextFormatting.RED;
        }
        if (ticks <= (Integer)this.limit.getValue()) {
            return TextFormatting.GREEN;
        }
        return TextFormatting.GOLD;
    }

    boolean isMoving() {
        return MotionUtil.getMotion((EntityPlayer)TickShift.mc.field_71439_g) + Math.abs(TickShift.mc.field_71439_g.field_70163_u - TickShift.mc.field_71439_g.field_70167_r) != 0.0;
    }
}

