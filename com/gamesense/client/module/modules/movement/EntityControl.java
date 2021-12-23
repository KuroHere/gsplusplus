/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.ControlEvent;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.event.type.Cancellable;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

@Module.Declaration(name="EntityControl", category=Category.Movement)
public class EntityControl
extends Module {
    @EventHandler
    private final Listener<ControlEvent> packetSendListener = new Listener<ControlEvent>(Cancellable::cancel, new Predicate[0]);

    @Override
    public void onUpdate() {
        if (EntityControl.mc.field_71439_g.field_184239_as != null) {
            EntityControl.mc.field_71439_g.field_184239_as.field_70177_z = EntityControl.mc.field_71439_g.field_70177_z;
        }
    }
}

