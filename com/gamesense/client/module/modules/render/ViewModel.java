/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.TransformSideFirstPersonEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.lwjgl.opengl.GL11;

@Module.Declaration(name="ViewModel", category=Category.Render)
public class ViewModel
extends Module {
    public BooleanSetting cancelEating = this.registerBoolean("No Eat", false);
    public BooleanSetting EatSection = this.registerBoolean("Eat Section", false);
    public DoubleSetting xEat = this.registerDouble("Eat X", 0.0, -2.0, 2.0, () -> (Boolean)this.EatSection.getValue());
    public DoubleSetting yEat = this.registerDouble("Eat Y", 0.2, -2.0, 2.0, () -> (Boolean)this.EatSection.getValue());
    public DoubleSetting zEat = this.registerDouble("Eat Z", -1.2, -2.0, 2.0, () -> (Boolean)this.EatSection.getValue());
    public DoubleSetting xScaleEat = this.registerDouble("Eat X Scale", 1.0, 0.0, 3.0, () -> (Boolean)this.EatSection.getValue());
    public DoubleSetting yScaleEat = this.registerDouble("Eat Y Scale", 1.0, 0.0, 3.0, () -> (Boolean)this.EatSection.getValue());
    public DoubleSetting zScaleEat = this.registerDouble("Eat Z Scale", 1.0, 0.0, 3.0, () -> (Boolean)this.EatSection.getValue());
    public IntegerSetting xEatRotate = this.registerInteger("Eat X Rotate", 0, 0, 180, () -> (Boolean)this.EatSection.getValue());
    public IntegerSetting yEatRotate = this.registerInteger("Eat Y Rotate", 0, 0, 180, () -> (Boolean)this.EatSection.getValue());
    public IntegerSetting zEatRotate = this.registerInteger("Eat Z Rotate", 0, 0, 180, () -> (Boolean)this.EatSection.getValue());
    public BooleanSetting cancelStandardBow = this.registerBoolean("Cancel Standard Bow", true);
    public BooleanSetting hand = this.registerBoolean("Hand Section", false);
    public DoubleSetting offX = this.registerDouble("OffhandX", 0.0, -1.0, 1.0, () -> (Boolean)this.hand.getValue());
    public DoubleSetting offY = this.registerDouble("OffhandY", 0.0, -1.0, 1.0, () -> (Boolean)this.hand.getValue());
    public DoubleSetting mainX = this.registerDouble("MainhandX", 0.0, -1.0, 1.0, () -> (Boolean)this.hand.getValue());
    public DoubleSetting mainY = this.registerDouble("MainhandY", 0.0, -1.0, 1.0, () -> (Boolean)this.hand.getValue());
    BooleanSetting leftSection = this.registerBoolean("Left Section", false);
    DoubleSetting xLeft = this.registerDouble("Left X", 0.0, -2.0, 2.0, () -> (Boolean)this.leftSection.getValue());
    DoubleSetting yLeft = this.registerDouble("Left Y", 0.2, -2.0, 2.0, () -> (Boolean)this.leftSection.getValue());
    DoubleSetting zLeft = this.registerDouble("Left Z", -1.2, -2.0, 2.0, () -> (Boolean)this.leftSection.getValue());
    DoubleSetting xScaleLeft = this.registerDouble("Left X Scale", 1.0, 0.0, 3.0, () -> (Boolean)this.leftSection.getValue());
    DoubleSetting yScaleLeft = this.registerDouble("Left Y Scale", 1.0, 0.0, 3.0, () -> (Boolean)this.leftSection.getValue());
    DoubleSetting zScaleLeft = this.registerDouble("Left Z Scale", 1.0, 0.0, 3.0, () -> (Boolean)this.leftSection.getValue());
    IntegerSetting xLeftRotate = this.registerInteger("Left X Rotate", 0, 0, 180, () -> (Boolean)this.leftSection.getValue());
    IntegerSetting yLeftRotate = this.registerInteger("Left Y Rotate", 0, 0, 180, () -> (Boolean)this.leftSection.getValue());
    IntegerSetting zLeftRotate = this.registerInteger("Left Z Rotate", 0, 0, 180, () -> (Boolean)this.leftSection.getValue());
    BooleanSetting rightSection = this.registerBoolean("Right Section", false);
    DoubleSetting xRight = this.registerDouble("Right X", 0.0, -5.0, 2.0, () -> (Boolean)this.rightSection.getValue());
    DoubleSetting yRight = this.registerDouble("Right Y", 0.2, -2.0, 5.0, () -> (Boolean)this.rightSection.getValue());
    DoubleSetting zRight = this.registerDouble("Right Z", -1.2, -5.0, 2.0, () -> (Boolean)this.rightSection.getValue());
    IntegerSetting xRightRotate = this.registerInteger("Right X Rotate", 0, 0, 360, () -> (Boolean)this.rightSection.getValue());
    IntegerSetting yRightRotate = this.registerInteger("Right Y Rotate", 0, 0, 360, () -> (Boolean)this.rightSection.getValue());
    IntegerSetting zRightRotate = this.registerInteger("Right Z Rotate", 0, 0, 360, () -> (Boolean)this.rightSection.getValue());
    DoubleSetting xScaleRight = this.registerDouble("Right X Scale", 1.0, 0.0, 3.0, () -> (Boolean)this.rightSection.getValue());
    DoubleSetting yScaleRight = this.registerDouble("Right Y Scale", 1.0, 0.0, 3.0, () -> (Boolean)this.rightSection.getValue());
    DoubleSetting zScaleRight = this.registerDouble("Right Z Scale", 1.0, 0.0, 3.0, () -> (Boolean)this.rightSection.getValue());
    BooleanSetting fovEnabled = this.registerBoolean("Enable Fov", false);
    DoubleSetting fov = this.registerDouble("Item FOV", 130.0, 70.0, 200.0, () -> (Boolean)this.fovEnabled.getValue());
    BooleanSetting animations = this.registerBoolean("Animations", false);
    BooleanSetting xLeftAnimation = this.registerBoolean("X Left Animation", false, () -> (Boolean)this.animations.getValue());
    BooleanSetting yLeftAnimation = this.registerBoolean("Y Left Animation", false, () -> (Boolean)this.animations.getValue());
    BooleanSetting zLeftAnimation = this.registerBoolean("Z Left Animation", false, () -> (Boolean)this.animations.getValue());
    BooleanSetting xRightAnimation = this.registerBoolean("X Right Animation", false, () -> (Boolean)this.animations.getValue());
    BooleanSetting yRightAnimation = this.registerBoolean("Y Right Animation", false, () -> (Boolean)this.animations.getValue());
    BooleanSetting zRightAnimation = this.registerBoolean("Z Right Animation", false, () -> (Boolean)this.animations.getValue());
    public BooleanSetting leftDipendentRight = this.registerBoolean("Left Dipendent Right", false);
    int xLeftAnimationCount = 0;
    int yLeftAnimationCount = 0;
    int zLeftAnimationCount = 0;
    int xRightAnimationCount = 0;
    int yRightAnimationCount = 0;
    int zRightAnimationCount = 0;
    @EventHandler
    private final Listener<TransformSideFirstPersonEvent> eventListener = new Listener<TransformSideFirstPersonEvent>(event -> {
        GlStateManager.func_179121_F();
        if (((Boolean)this.leftDipendentRight.getValue()).booleanValue() || event.getEnumHandSide() == EnumHandSide.LEFT && ViewModel.mc.field_71439_g.func_184614_ca().func_190926_b()) {
            GlStateManager.func_179094_E();
        }
        if (event.getEnumHandSide() == EnumHandSide.RIGHT) {
            GlStateManager.func_179137_b((double)((Double)this.xRight.getValue()), (double)((Double)this.yRight.getValue()), (double)((Double)this.zRight.getValue()));
            if (((Boolean)this.xRightAnimation.getValue()).booleanValue()) {
                GL11.glRotatef((float)(++this.xRightAnimationCount), (float)1.0f, (float)0.0f, (float)0.0f);
            } else {
                GL11.glRotatef((float)((Integer)this.xRightRotate.getValue()).intValue(), (float)1.0f, (float)0.0f, (float)0.0f);
            }
            if (((Boolean)this.yRightAnimation.getValue()).booleanValue()) {
                GL11.glRotatef((float)(++this.yRightAnimationCount), (float)0.0f, (float)1.0f, (float)0.0f);
            } else {
                GL11.glRotatef((float)((Integer)this.yRightRotate.getValue()).intValue(), (float)0.0f, (float)1.0f, (float)0.0f);
            }
            if (((Boolean)this.zRightAnimation.getValue()).booleanValue()) {
                GL11.glRotatef((float)(++this.zRightAnimationCount), (float)0.0f, (float)0.0f, (float)1.0f);
            } else {
                GL11.glRotatef((float)((Integer)this.zRightRotate.getValue()).intValue(), (float)0.0f, (float)0.0f, (float)1.0f);
            }
            GlStateManager.func_179139_a((double)((Double)this.xScaleRight.getValue()), (double)((Double)this.yScaleRight.getValue()), (double)((Double)this.zScaleRight.getValue()));
        } else if (event.getEnumHandSide() == EnumHandSide.LEFT) {
            GlStateManager.func_179137_b((double)((Double)this.xLeft.getValue()), (double)((Double)this.yLeft.getValue()), (double)((Double)this.zLeft.getValue()));
            if (((Boolean)this.xLeftAnimation.getValue()).booleanValue()) {
                GL11.glRotatef((float)(++this.xLeftAnimationCount), (float)1.0f, (float)0.0f, (float)0.0f);
            } else {
                GL11.glRotatef((float)((Integer)this.xLeftRotate.getValue()).intValue(), (float)1.0f, (float)0.0f, (float)0.0f);
            }
            if (((Boolean)this.yLeftAnimation.getValue()).booleanValue()) {
                GL11.glRotatef((float)(++this.yLeftAnimationCount), (float)0.0f, (float)1.0f, (float)0.0f);
            } else {
                GL11.glRotatef((float)((Integer)this.yLeftRotate.getValue()).intValue(), (float)0.0f, (float)1.0f, (float)0.0f);
            }
            if (((Boolean)this.zLeftAnimation.getValue()).booleanValue()) {
                GL11.glRotatef((float)(++this.zLeftAnimationCount), (float)0.0f, (float)0.0f, (float)1.0f);
            } else {
                GL11.glRotatef((float)((Integer)this.zLeftRotate.getValue()).intValue(), (float)0.0f, (float)0.0f, (float)1.0f);
            }
            GlStateManager.func_179139_a((double)((Double)this.xScaleLeft.getValue()), (double)((Double)this.yScaleLeft.getValue()), (double)((Double)this.zScaleLeft.getValue()));
        }
        this.xLeftAnimationCount %= 360;
        this.yLeftAnimationCount %= 360;
        this.zLeftAnimationCount %= 360;
        this.xRightAnimationCount %= 360;
        this.yRightAnimationCount %= 360;
        this.zRightAnimationCount %= 360;
    }, new Predicate[0]);
    @EventHandler
    private final Listener<EntityViewRenderEvent.FOVModifier> fovModifierListener = new Listener<EntityViewRenderEvent.FOVModifier>(event -> {
        if (((Boolean)this.fovEnabled.getValue()).booleanValue()) {
            event.setFOV(((Double)this.fov.getValue()).floatValue());
        }
    }, new Predicate[0]);
}

