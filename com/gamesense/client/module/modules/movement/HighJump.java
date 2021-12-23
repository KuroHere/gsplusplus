/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Module.Declaration(name="HighJump", category=Category.Movement)
public class HighJump
extends Module {
    public DoubleSetting height = this.registerDouble("Height", 1.0, 0.0, 25.0);
}

