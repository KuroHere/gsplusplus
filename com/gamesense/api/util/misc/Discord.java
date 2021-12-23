/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.misc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

public class Discord {
    private static final String discordID = "840996509880680479";
    public static final DiscordRichPresence discordRichPresence = new DiscordRichPresence();
    private static final DiscordRPC discordRPC = DiscordRPC.INSTANCE;
    private static final String clientVersion = "v2.3.4";

    public static void startRPC() {
        DiscordEventHandlers eventHandlers = new DiscordEventHandlers();
        eventHandlers.disconnected = (var1, var2) -> System.out.println("Discord RPC disconnected, var1: " + var1 + ", var2: " + var2);
        discordRPC.Discord_Initialize(discordID, eventHandlers, true, null);
        Discord.discordRichPresence.startTimestamp = System.currentTimeMillis() / 1000L;
        Discord.discordRichPresence.details = clientVersion;
        Discord.discordRichPresence.largeImageKey = "gs";
        Discord.discordRichPresence.largeImageText = "gs++";
        Discord.discordRichPresence.state = null;
        discordRPC.Discord_UpdatePresence(discordRichPresence);
    }

    public static void stopRPC() {
        discordRPC.Discord_Shutdown();
        discordRPC.Discord_ClearPresence();
    }
}

