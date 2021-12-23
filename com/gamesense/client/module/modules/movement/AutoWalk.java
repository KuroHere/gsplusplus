/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.client.settings.KeyBinding;

@Module.Declaration(name="AutoWalk", category=Category.Movement)
public class AutoWalk
extends Module {
    @Override
    public void onUpdate() {
        KeyBinding.func_74510_a((int)AutoWalk.mc.field_71474_y.field_74351_w.func_151463_i(), (boolean)true);
    }

    @Override
    protected void onDisable() {
        KeyBinding.func_74510_a((int)AutoWalk.mc.field_71474_y.field_74351_w.func_151463_i(), (boolean)false);
    }
}

