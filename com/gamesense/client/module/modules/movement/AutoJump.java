/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Module.Declaration(name="AutoJump", category=Category.Movement)
public class AutoJump
extends Module {
    @Override
    public void onUpdate() {
        if (AutoJump.mc.field_71439_g.field_70122_E) {
            AutoJump.mc.field_71439_g.func_70664_aZ();
        }
    }
}

