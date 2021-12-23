/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Arrays;

@Module.Declaration(name="Capes", category=Category.Render, drawn=false)
public class CapesModule
extends Module {
    public ModeSetting capeMode = this.registerMode("Type", Arrays.asList("Old", "New", "Amber"), "New");

    public static String getUsName() {
        return CapesModule.mc.field_71439_g.func_70005_c_();
    }
}

