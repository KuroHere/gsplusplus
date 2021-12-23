/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.item.ItemSword;

@Module.Declaration(name="RenderTweaks", category=Category.Render)
public class RenderTweaks
extends Module {
    public BooleanSetting viewClip = this.registerBoolean("View Clip", false);
    BooleanSetting nekoAnimation = this.registerBoolean("Neko Animation", false);
    BooleanSetting lowOffhand = this.registerBoolean("Low Offhand", false);
    DoubleSetting lowOffhandSlider = this.registerDouble("Offhand Height", 1.0, 0.1, 1.0);
    BooleanSetting fovChanger = this.registerBoolean("FOV", false);
    IntegerSetting fovChangerSlider = this.registerInteger("FOV Slider", 90, 30, 200);
    private float oldFOV;

    @Override
    public void onUpdate() {
        if (((Boolean)this.nekoAnimation.getValue()).booleanValue() && RenderTweaks.mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemSword && (double)RenderTweaks.mc.field_71460_t.field_78516_c.field_187470_g >= 0.9) {
            RenderTweaks.mc.field_71460_t.field_78516_c.field_187469_f = 1.0f;
            RenderTweaks.mc.field_71460_t.field_78516_c.field_187467_d = RenderTweaks.mc.field_71439_g.func_184614_ca();
        }
        if (((Boolean)this.lowOffhand.getValue()).booleanValue()) {
            RenderTweaks.mc.field_71460_t.field_78516_c.field_187471_h = ((Double)this.lowOffhandSlider.getValue()).floatValue();
        }
        if (((Boolean)this.fovChanger.getValue()).booleanValue()) {
            RenderTweaks.mc.field_71474_y.field_74334_X = ((Integer)this.fovChangerSlider.getValue()).intValue();
        }
        if (!((Boolean)this.fovChanger.getValue()).booleanValue()) {
            RenderTweaks.mc.field_71474_y.field_74334_X = this.oldFOV;
        }
    }

    @Override
    public void onEnable() {
        this.oldFOV = RenderTweaks.mc.field_71474_y.field_74334_X;
    }

    @Override
    public void onDisable() {
        RenderTweaks.mc.field_71474_y.field_74334_X = this.oldFOV;
    }
}

