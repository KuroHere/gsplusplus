/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.awt.Color;
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.passive.AbstractHorse;

@Module.Declaration(name="MobOwner", category=Category.Render)
public class MobOwner
extends Module {
    @Override
    public void onUpdate() {
        for (Entity e : MobOwner.mc.field_71441_e.field_72996_f) {
            if (!(e instanceof IEntityOwnable)) continue;
            if (!(e instanceof AbstractHorse)) {
                try {
                    RenderUtil.drawNametag(e, new String[]{Objects.requireNonNull(((IEntityOwnable)e).func_70902_q()).func_70005_c_() + ""}, new GSColor(Color.WHITE), 0);
                }
                catch (NullPointerException nullPointerException) {}
                continue;
            }
            String string = "Name: " + e.func_95999_t() + ", Owner: " + Objects.requireNonNull(((IEntityOwnable)e).func_70902_q()).func_70005_c_() + ", Speed: " + ((AbstractHorse)e).func_70689_ay();
            RenderUtil.drawNametag(e, new String[]{string}, new GSColor(Color.WHITE), 1);
        }
    }
}

