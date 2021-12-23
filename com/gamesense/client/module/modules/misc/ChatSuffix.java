/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.command.CommandManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketChatMessage;

@Module.Declaration(name="ChatSuffix", category=Category.Misc)
public class ChatSuffix
extends Module {
    ModeSetting Separator = this.registerMode("Separator", Arrays.asList(">>", "<<", "|"), "|");
    BooleanSetting noUnicode = this.registerBoolean("No Unicode", false);
    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<PacketEvent.Send>(event -> {
        if (event.getPacket() instanceof CPacketChatMessage) {
            if (((CPacketChatMessage)event.getPacket()).func_149439_c().startsWith("/") || ((CPacketChatMessage)event.getPacket()).func_149439_c().startsWith(CommandManager.getCommandPrefix())) {
                return;
            }
            String Separator2 = (Boolean)this.noUnicode.getValue() != false ? " " + (String)this.Separator.getValue() : (((String)this.Separator.getValue()).equalsIgnoreCase(">>") ? " \u300b" : (((String)this.Separator.getValue()).equalsIgnoreCase("<<") ? " \u300a" : " \u23d0 "));
            String old = ((CPacketChatMessage)event.getPacket()).func_149439_c();
            String suffix = Separator2 + ((Boolean)this.noUnicode.getValue() != false ? "gs++" : this.toUnicode("gs++"));
            String s = old + suffix;
            if (s.length() > 255) {
                return;
            }
            ((CPacketChatMessage)event.getPacket()).field_149440_a = s;
        }
    }, new Predicate[0]);

    private String toUnicode(String s) {
        return s.toLowerCase().replace("a", "\u1d00").replace("b", "\u0299").replace("c", "\u1d04").replace("d", "\u1d05").replace("e", "\u1d07").replace("f", "\ua730").replace("g", "\u0262").replace("h", "\u029c").replace("i", "\u026a").replace("j", "\u1d0a").replace("k", "\u1d0b").replace("l", "\u029f").replace("m", "\u1d0d").replace("n", "\u0274").replace("o", "\u1d0f").replace("p", "\u1d18").replace("q", "\u01eb").replace("r", "\u0280").replace("s", "\ua731").replace("t", "\u1d1b").replace("u", "\u1d1c").replace("v", "\u1d20").replace("w", "\u1d21").replace("x", "\u02e3").replace("y", "\u028f").replace("z", "\u1d22");
    }
}

