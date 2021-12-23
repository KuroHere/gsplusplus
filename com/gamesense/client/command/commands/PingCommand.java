/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import java.util.Objects;

@Command.Declaration(name="Ping", syntax="Ping [player]", alias={"ping", "ms", "latency", "lag"})
public class PingCommand
extends Command {
    @Override
    public void onCommand(String command, String[] message) {
        String pl = message[0];
        if (!pl.equals(PingCommand.mc.field_71439_g.func_70005_c_())) {
            try {
                MessageBus.sendClientPrefixMessage(Objects.requireNonNull(PingCommand.mc.field_71441_e.func_72924_a(pl)).func_70005_c_() + " Has " + mc.func_147114_u().func_175104_a(pl).func_178853_c() + "ms");
            }
            catch (NullPointerException ignored) {
                MessageBus.sendClientPrefixMessage("Invalid Player");
            }
        } else {
            try {
                MessageBus.sendClientPrefixMessage("You have no idea what your ms is trol");
            }
            catch (NullPointerException ignored) {
                MessageBus.sendClientPrefixMessage("Invalid Player");
            }
        }
    }
}

