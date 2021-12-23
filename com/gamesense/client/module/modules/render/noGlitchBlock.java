/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Module.Declaration(name="NoGlitchBlock", category=Category.Render)
public class noGlitchBlock
extends Module {
    public BooleanSetting breakBlock = this.registerBoolean("Break", true);
    public BooleanSetting placeBlock = this.registerBoolean("Place", true);
}

