/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

@Module.Declaration(name="AutoReply", category=Category.Misc)
public class AutoReply
extends Module {
    private static String reply = "I don't speak to newfags!";
    @EventHandler
    private final Listener<ClientChatReceivedEvent> listener = new Listener<ClientChatReceivedEvent>(event -> {
        if (event.getMessage().func_150260_c().contains("whispers: ") && !event.getMessage().func_150260_c().startsWith(AutoReply.mc.field_71439_g.func_70005_c_())) {
            if (event.getMessage().func_150260_c().contains("I don't speak to newfags!")) {
                return;
            }
            MessageBus.sendServerMessage("/r " + reply);
        }
    }, new Predicate[0]);

    public static String getReply() {
        return reply;
    }

    public static void setReply(String r) {
        reply = r;
    }
}

