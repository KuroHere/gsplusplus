/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.util.misc.Timer;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Objects;
import org.lwjgl.input.Keyboard;

@Module.Declaration(name="ViewLock", category=Category.Movement)
public class ViewLock
extends Module {
    boolean dontChange;
    Timer timer = new Timer();

    @Override
    public void onUpdate() {
        int angle = 45;
        float yaw = ViewLock.mc.field_71439_g.field_70177_z;
        if (Keyboard.isKeyDown((int)205) && !this.dontChange) {
            this.timer.reset();
            this.dontChange = true;
            yaw += 45.0f;
        } else if (Keyboard.isKeyDown((int)203) && !this.dontChange) {
            this.timer.reset();
            this.dontChange = true;
            yaw -= 45.0f;
        }
        if (this.dontChange && this.timer.hasReached(250L)) {
            this.dontChange = false;
        }
        ViewLock.mc.field_71439_g.field_70177_z = yaw = (float)(Math.round(yaw / 45.0f) * 45);
        if (ViewLock.mc.field_71439_g.func_184218_aH()) {
            Objects.requireNonNull(ViewLock.mc.field_71439_g.func_184187_bx()).field_70177_z = yaw;
        }
    }
}

