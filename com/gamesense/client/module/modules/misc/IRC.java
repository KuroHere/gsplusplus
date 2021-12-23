/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.gamesense.api.event.events.SendMessageEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.StringSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.misc.WebsocketClientEndpoint;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

@Module.Declaration(name="IRC", category=Category.Misc)
public class IRC
extends Module {
    ColorSetting sideColor = this.registerColor("SideColor", new GSColor(255, 0, 0));
    BooleanSetting alwaysIRC = this.registerBoolean("Always IRC", false);
    StringSetting bindIRC = this.registerString("Bind IRC", "#");
    BooleanSetting addgs = this.registerBoolean("Add gs", true);
    static boolean finish;
    static final Object syn;
    WebsocketClientEndpoint clientEndPoint;
    int tries = 0;
    String realNameMsg;
    Map<String, String> map = Stream.of({"@doogie", "<@467346196873347082>"}, {"@techale", "<@185754779258060802>"}, {"@aven", "<@584363189890711562>"}, {"@mwa", "<@679115407751381014>"}, {"@phantom", "<@345816471949017089>"}, {"@soggy", "<@254741747832324096>"}, {"@sable", "<@901783136462065665>"}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    String msgSend;
    @EventHandler
    private final Listener<SendMessageEvent> chatReceivedEventListener = new Listener<SendMessageEvent>(event -> {
        String msg = event.getMessage();
        if (this.clientEndPoint == null) {
            return;
        }
        if (((Boolean)this.alwaysIRC.getValue()).booleanValue() || msg.startsWith(this.bindIRC.getText().length() > 0 ? String.valueOf(this.bindIRC.getText().charAt(0)) : "")) {
            if (!((Boolean)this.alwaysIRC.getValue()).booleanValue()) {
                msg = msg.substring(1);
            }
            for (String key : this.map.keySet()) {
                msg = msg.replace(key, this.map.get(key));
            }
            if (msg.equals("img")) {
                this.sendPicture(IRC.mc.field_71439_g.func_70005_c_());
            } else {
                Object object = syn;
                synchronized (object) {
                    this.msgSend = msg;
                    Thread t = new Thread(){

                        @Override
                        public void run() {
                            IRC.this.clientEndPoint.sendMessage(IRC.this.getSendMessageRequest(mc.field_71439_g.func_70005_c_(), IRC.this.msgSend));
                            String imageURL = "https://crafatar.com/avatars/" + IRC.this.getUUID(mc.field_71439_g.func_70005_c_()).replaceAll("-", "") + "?size=64&default=MHF_Steve&overlay";
                            String url = "https://discord.com/api/webhooks/906976095901986846/6mtRpFDzCEWuwTuuvWPfRywdcPPWtBWTjOlWxtusaC2gABELZ7N4Zr3_nQX8XwuFPYVz";
                            WebhookClient client = WebhookClient.withUrl(url);
                            WebhookMessage realMSG = new WebhookMessageBuilder().setAvatarUrl(imageURL).setUsername(mc.field_71439_g.func_70005_c_()).addEmbeds(new WebhookEmbed(null, IRC.this.sideColor.getColor().getRGB(), IRC.this.msgSend, null, null, null, null, null, new ArrayList<WebhookEmbed.EmbedField>())).build();
                            client.send(realMSG);
                        }
                    };
                    t.start();
                }
            }
            event.cancel();
        }
    }, new Predicate[0]);

    @Override
    public void onUpdate() {
        if (this.clientEndPoint == null) {
            finish = true;
            try {
                this.clientEndPoint = new WebsocketClientEndpoint(new URI("wss://hack.chat/chat-ws"));
                this.clientEndPoint.sendMessage(this.getCreateChatroomRequest("MC_" + IRC.mc.field_71439_g.func_70005_c_()));
                this.clientEndPoint.addMessageHandler(message -> {
                    JsonObject convertedObject = (JsonObject)new Gson().fromJson(message, JsonObject.class);
                    switch (convertedObject.get("cmd").getAsString()) {
                        case "onlineSet": {
                            JsonArray online = convertedObject.get("nicks").getAsJsonArray();
                            StringBuilder on = new StringBuilder();
                            for (int i = 0; i < online.size(); ++i) {
                                if (online.get(i).equals("server")) continue;
                                on.append(online.get(i)).append(" ");
                            }
                            String textMSG = ChatFormatting.BOLD + "" + ChatFormatting.AQUA + "IRC " + ChatFormatting.RESET + " players online: " + on.toString();
                            if (((Boolean)this.addgs.getValue()).booleanValue()) {
                                MessageBus.sendClientPrefixMessage(textMSG);
                                break;
                            }
                            MessageBus.sendClientRawMessage(textMSG);
                            break;
                        }
                        case "onlineRemove": {
                            String name = convertedObject.get("nick").toString();
                            if (name.contains("server")) {
                                return;
                            }
                            String textMSG = ChatFormatting.BOLD + "" + ChatFormatting.AQUA + "IRC " + ChatFormatting.RESET + name + " left the irc";
                            if (((Boolean)this.addgs.getValue()).booleanValue()) {
                                MessageBus.sendClientPrefixMessage(textMSG);
                                break;
                            }
                            MessageBus.sendClientRawMessage(textMSG);
                            break;
                        }
                        case "onlineAdd": {
                            String name = convertedObject.get("nick").toString();
                            if (name.equals("server")) {
                                return;
                            }
                            String textMSG = ChatFormatting.BOLD + "" + ChatFormatting.AQUA + "IRC " + ChatFormatting.RESET + name + " joined the irc";
                            if (((Boolean)this.addgs.getValue()).booleanValue()) {
                                MessageBus.sendClientPrefixMessage(textMSG);
                                break;
                            }
                            MessageBus.sendClientRawMessage(textMSG);
                            break;
                        }
                        case "chat": {
                            String realMSG;
                            String realAuthor;
                            String text = convertedObject.get("text").getAsString();
                            String[] values = text.split(":");
                            if (values.length == 1) {
                                realAuthor = convertedObject.get("nick").getAsString();
                                realMSG = text;
                            } else {
                                realAuthor = values[0];
                                realMSG = text.substring(text.indexOf(58));
                            }
                            if (realAuthor.equals("server")) {
                                return;
                            }
                            String textMSG = ChatFormatting.BOLD + "" + ChatFormatting.AQUA + "IRC " + ChatFormatting.RESET + realAuthor + realMSG;
                            if (((Boolean)this.addgs.getValue()).booleanValue()) {
                                MessageBus.sendClientPrefixMessage(textMSG);
                                break;
                            }
                            MessageBus.sendClientRawMessage(textMSG);
                            break;
                        }
                        case "warn": {
                            if (!convertedObject.get("text").getAsString().equals("Nickname taken")) break;
                            ++this.tries;
                            this.clientEndPoint.sendMessage(this.getCreateChatroomRequest("MC_" + IRC.mc.field_71439_g.func_70005_c_()));
                        }
                    }
                });
            }
            catch (URISyntaxException uRISyntaxException) {}
        } else if (this.clientEndPoint.getUserSession() == 0) {
            this.clientEndPoint = null;
        }
    }

    @Override
    protected void onDisable() {
        finish = false;
        this.clientEndPoint.close();
        this.clientEndPoint = null;
    }

    String getCreateChatroomRequest(String name) {
        return "{\"cmd\":\"join\",\"channel\":\"gs\",\"nick\":\"" + name + (this.tries == 0 ? "" : String.valueOf(this.tries)) + "\"}";
    }

    String getSendMessageRequest(String name, String message) {
        return "{\"cmd\":\"chat\",\"text\":\"" + name + ":" + message + "\"}";
    }

    String getUUID(String name) {
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));){
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }
            String str = result.toString();
            if (str.equals("")) {
                return "";
            }
            JsonObject convertedObject = (JsonObject)new Gson().fromJson(str, JsonObject.class);
            JsonElement value = convertedObject.get("id");
            if (value.isJsonNull()) {
                return "";
            }
            return value.getAsString();
        }
        catch (IOException ignored) {
            return "error";
        }
    }

    String getUUID() {
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL("https://heroku-temp-chat-server.herokuapp.com/uuid");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));){
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }
            String str = result.toString();
            if (str.equals("")) {
                return "";
            }
            JsonObject convertedObject = (JsonObject)new Gson().fromJson(str, JsonObject.class);
            JsonElement value = convertedObject.get("uuid");
            if (value.isJsonNull()) {
                return "";
            }
            return value.getAsString();
        }
        catch (IOException e) {
            return "";
        }
    }

    void sendPicture(String realName) {
        this.realNameMsg = realName;
        Thread t = new Thread(() -> {
            String imageURL = "https://crafatar.com/avatars/" + this.getUUID(this.realNameMsg).replaceAll("-", "") + "?size=64&default=MHF_Steve&overlay";
            String url = "https://discord.com/api/webhooks/906976095901986846/6mtRpFDzCEWuwTuuvWPfRywdcPPWtBWTjOlWxtusaC2gABELZ7N4Zr3_nQX8XwuFPYVz";
            WebhookClient client = WebhookClient.withUrl(url);
            if (Files.exists(Paths.get("screenshots", new String[0]), new LinkOption[0])) {
                File uploadFile1 = IRC.getLastModified(Paths.get("screenshots", new String[0]).toAbsolutePath().toString());
                WebhookMessage msg = new WebhookMessageBuilder().addFile(uploadFile1).setAvatarUrl(imageURL).setUsername(this.realNameMsg).build();
                client.send(msg);
            }
        });
        t.start();
    }

    public static File getLastModified(String directoryFilePath) {
        File directory = new File(directoryFilePath);
        File[] files = directory.listFiles(File::isFile);
        long lastModifiedTime = Long.MIN_VALUE;
        File chosenFile = null;
        if (files != null) {
            for (File file : files) {
                if (file.lastModified() <= lastModifiedTime) continue;
                chosenFile = file;
                lastModifiedTime = file.lastModified();
            }
        }
        return chosenFile;
    }

    static {
        syn = new Object();
    }
}

