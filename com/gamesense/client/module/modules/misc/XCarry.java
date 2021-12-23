/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketCloseWindow;

@Module.Declaration(name="XCarry", category=Category.Misc)
public class XCarry
extends Module {
    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<PacketEvent.Send>(event -> {
        if (event.getPacket() instanceof CPacketCloseWindow && ((CPacketCloseWindow)event.getPacket()).field_149556_a == XCarry.mc.field_71439_g.field_71069_bz.field_75152_c) {
            event.cancel();
        }
    }, new Predicate[0]);
}

