/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.misc;

import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.Notifications;
import com.gamesense.client.module.modules.misc.ChatModifier;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ConcurrentModificationException;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class MessageBus {
    public static String watermark = ChatFormatting.GRAY + "[" + ChatFormatting.WHITE + "g" + ChatFormatting.GREEN + "s++" + ChatFormatting.GRAY + "] " + ChatFormatting.RESET;
    public static ChatFormatting messageFormatting = ChatFormatting.GRAY;
    protected static final Minecraft mc = Minecraft.func_71410_x();

    public static void sendClientPrefixMessage(String message) {
        MessageBus.sendClientPrefixMessageWithID(message, 0);
    }

    public static void sendClientPrefixMessageWithID(String message, boolean generateID) {
        if (!generateID) {
            MessageBus.sendClientPrefixMessageWithID(message, 0);
        } else {
            MessageBus.sendClientPrefixMessageWithID(message, Module.getIdFromString(message));
        }
    }

    public static void sendClientPrefixMessageWithID(String message, int id) {
        ChatModifier chat = ModuleManager.getModule(ChatModifier.class);
        TextComponentString string1 = new TextComponentString((chat.isEnabled() && (Boolean)chat.watermarkSpecial.getValue() != false ? "\u2063[gs++]" : watermark) + messageFormatting + message);
        TextComponentString string2 = new TextComponentString(messageFormatting + message);
        Notifications notifications = ModuleManager.getModule(Notifications.class);
        notifications.addMessage(string2);
        if (notifications.isEnabled() && ((Boolean)notifications.disableChat.getValue()).booleanValue()) {
            return;
        }
        try {
            if (MessageBus.mc.field_71439_g != null && MessageBus.mc.field_71441_e != null) {
                MessageBus.mc.field_71456_v.func_146158_b().func_146234_a((ITextComponent)string1, id);
            }
        }
        catch (ConcurrentModificationException concurrentModificationException) {
            // empty catch block
        }
    }

    public static void sendCommandMessage(String message, boolean prefix) {
        String watermark1 = prefix ? watermark : "";
        TextComponentString string = new TextComponentString(watermark1 + messageFormatting + message);
        MessageBus.mc.field_71456_v.func_146158_b().func_146234_a((ITextComponent)string, Module.getIdFromString(message));
    }

    public static void sendClientRawMessage(String message) {
        TextComponentString string = new TextComponentString(messageFormatting + message);
        Notifications notifications = ModuleManager.getModule(Notifications.class);
        notifications.addMessage(string);
        if (ModuleManager.isModuleEnabled(Notifications.class) && ((Boolean)notifications.disableChat.getValue()).booleanValue()) {
            return;
        }
        MessageBus.mc.field_71439_g.func_145747_a((ITextComponent)string);
    }

    public static void sendServerMessage(String message) {
        MessageBus.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketChatMessage(message));
    }
}

