/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.config;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.setting.SettingsManager;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.StringSetting;
import com.gamesense.api.util.font.CFontRenderer;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.client.GameSense;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.clickgui.GuiConfig;
import com.gamesense.client.command.CommandManager;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.gamesense.client.module.modules.misc.AutoReply;
import com.gamesense.client.module.modules.misc.AutoRespawn;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Paths;

public class LoadConfig {
    private static final String fileName = "gs++/";
    private static final String moduleName = "Modules/";
    private static final String mainName = "Main/";
    private static final String miscName = "Misc/";

    public static void init() {
        try {
            LoadConfig.loadModules();
            LoadConfig.loadEnabledModules();
            LoadConfig.loadModuleKeybinds();
            LoadConfig.loadDrawnModules();
            LoadConfig.loadToggleMessageModules();
            LoadConfig.loadCommandPrefix();
            LoadConfig.loadCustomFont();
            LoadConfig.loadFriendsList();
            LoadConfig.loadEnemiesList();
            LoadConfig.loadSpecialNames();
            LoadConfig.loadClickGUIPositions();
            LoadConfig.loadAutoGG();
            LoadConfig.loadAutoReply();
            LoadConfig.loadAutoRespawn();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadModules() throws IOException {
        String moduleLocation = "gs++/Modules/";
        for (Module module : ModuleManager.getModules()) {
            try {
                LoadConfig.loadModuleDirect(moduleLocation, module);
            }
            catch (IOException e) {
                System.out.println(module.getName());
                e.printStackTrace();
            }
        }
    }

    private static void loadModuleDirect(String moduleLocation, Module module) throws IOException {
        JsonObject moduleObject;
        if (!Files.exists(Paths.get(moduleLocation + module.getName() + ".json", new String[0]), new LinkOption[0])) {
            return;
        }
        InputStream inputStream = Files.newInputStream(Paths.get(moduleLocation + module.getName() + ".json", new String[0]), new OpenOption[0]);
        try {
            moduleObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        }
        catch (IllegalStateException e) {
            return;
        }
        if (moduleObject.get("Module") == null) {
            return;
        }
        JsonObject settingObject = moduleObject.get("Settings").getAsJsonObject();
        for (Setting setting : SettingsManager.getSettingsForModule(module)) {
            JsonElement dataObject = settingObject.get(setting.getConfigName());
            try {
                if (dataObject == null || !dataObject.isJsonPrimitive()) continue;
                if (setting instanceof BooleanSetting) {
                    setting.setValue(dataObject.getAsBoolean());
                    continue;
                }
                if (setting instanceof IntegerSetting) {
                    setting.setValue(dataObject.getAsInt());
                    continue;
                }
                if (setting instanceof DoubleSetting) {
                    setting.setValue(dataObject.getAsDouble());
                    continue;
                }
                if (setting instanceof ColorSetting) {
                    ((ColorSetting)setting).fromLong(dataObject.getAsLong());
                    continue;
                }
                if (setting instanceof ModeSetting) {
                    setting.setValue(dataObject.getAsString());
                    continue;
                }
                if (!(setting instanceof StringSetting)) continue;
                setting.setValue(dataObject.getAsString());
                ((StringSetting)setting).setText(dataObject.getAsString());
            }
            catch (NumberFormatException e) {
                System.out.println(setting.getConfigName() + " " + module.getName());
                System.out.println(dataObject);
            }
        }
        inputStream.close();
    }

    private static void loadEnabledModules() throws IOException {
        String enabledLocation = "gs++/Main/";
        if (!Files.exists(Paths.get(enabledLocation + "Toggle.json", new String[0]), new LinkOption[0])) {
            return;
        }
        InputStream inputStream = Files.newInputStream(Paths.get(enabledLocation + "Toggle.json", new String[0]), new OpenOption[0]);
        JsonObject moduleObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (moduleObject.get("Modules") == null) {
            return;
        }
        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (Module module : ModuleManager.getModules()) {
            JsonElement dataObject = settingObject.get(module.getName());
            if (dataObject == null || !dataObject.isJsonPrimitive() || !dataObject.getAsBoolean()) continue;
            try {
                module.enable();
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        inputStream.close();
    }

    private static void loadModuleKeybinds() throws IOException {
        String bindLocation = "gs++/Main/";
        if (!Files.exists(Paths.get(bindLocation + "Bind.json", new String[0]), new LinkOption[0])) {
            return;
        }
        InputStream inputStream = Files.newInputStream(Paths.get(bindLocation + "Bind.json", new String[0]), new OpenOption[0]);
        JsonObject moduleObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (moduleObject.get("Modules") == null) {
            return;
        }
        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (Module module : ModuleManager.getModules()) {
            JsonElement dataObject = settingObject.get(module.getName());
            if (dataObject == null || !dataObject.isJsonPrimitive()) continue;
            module.setBind(dataObject.getAsInt());
        }
        inputStream.close();
    }

    private static void loadDrawnModules() throws IOException {
        String drawnLocation = "gs++/Main/";
        if (!Files.exists(Paths.get(drawnLocation + "Drawn.json", new String[0]), new LinkOption[0])) {
            return;
        }
        InputStream inputStream = Files.newInputStream(Paths.get(drawnLocation + "Drawn.json", new String[0]), new OpenOption[0]);
        JsonObject moduleObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (moduleObject.get("Modules") == null) {
            return;
        }
        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (Module module : ModuleManager.getModules()) {
            JsonElement dataObject = settingObject.get(module.getName());
            if (dataObject == null || !dataObject.isJsonPrimitive()) continue;
            module.setDrawn(dataObject.getAsBoolean());
        }
        inputStream.close();
    }

    private static void loadToggleMessageModules() throws IOException {
        String toggleMessageLocation = "gs++/Main/";
        if (!Files.exists(Paths.get(toggleMessageLocation + "ToggleMessages.json", new String[0]), new LinkOption[0])) {
            return;
        }
        InputStream inputStream = Files.newInputStream(Paths.get(toggleMessageLocation + "ToggleMessages.json", new String[0]), new OpenOption[0]);
        JsonObject moduleObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (moduleObject.get("Modules") == null) {
            return;
        }
        JsonObject toggleObject = moduleObject.get("Modules").getAsJsonObject();
        for (Module module : ModuleManager.getModules()) {
            JsonElement dataObject = toggleObject.get(module.getName());
            if (dataObject == null || !dataObject.isJsonPrimitive()) continue;
            module.setToggleMsg(dataObject.getAsBoolean());
        }
        inputStream.close();
    }

    private static void loadCommandPrefix() throws IOException {
        String prefixLocation = "gs++/Main/";
        if (!Files.exists(Paths.get(prefixLocation + "CommandPrefix.json", new String[0]), new LinkOption[0])) {
            return;
        }
        InputStream inputStream = Files.newInputStream(Paths.get(prefixLocation + "CommandPrefix.json", new String[0]), new OpenOption[0]);
        JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Prefix") == null) {
            return;
        }
        JsonElement prefixObject = mainObject.get("Prefix");
        if (prefixObject != null && prefixObject.isJsonPrimitive()) {
            CommandManager.setCommandPrefix(prefixObject.getAsString());
        }
        inputStream.close();
    }

    private static void loadCustomFont() throws IOException {
        String fontLocation = "gs++/Misc/";
        if (!Files.exists(Paths.get(fontLocation + "CustomFont.json", new String[0]), new LinkOption[0])) {
            return;
        }
        InputStream inputStream = Files.newInputStream(Paths.get(fontLocation + "CustomFont.json", new String[0]), new OpenOption[0]);
        JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Font Name") == null || mainObject.get("Font Size") == null) {
            return;
        }
        JsonElement fontNameObject = mainObject.get("Font Name");
        String name = null;
        if (fontNameObject != null && fontNameObject.isJsonPrimitive()) {
            name = fontNameObject.getAsString();
        }
        JsonElement fontSizeObject = mainObject.get("Font Size");
        int size = -1;
        if (fontSizeObject != null && fontSizeObject.isJsonPrimitive()) {
            size = fontSizeObject.getAsInt();
        }
        if (name != null && size != -1) {
            GameSense.INSTANCE.cFontRenderer = new CFontRenderer(new Font(name, 0, size), true, true);
            GameSense.INSTANCE.cFontRenderer.setFont(new Font(name, 0, size));
            GameSense.INSTANCE.cFontRenderer.setAntiAlias(true);
            GameSense.INSTANCE.cFontRenderer.setFractionalMetrics(true);
            GameSense.INSTANCE.cFontRenderer.setFontName(name);
            GameSense.INSTANCE.cFontRenderer.setFontSize(size);
        }
        inputStream.close();
    }

    private static void loadFriendsList() throws IOException {
        String friendLocation = "gs++/Misc/";
        if (!Files.exists(Paths.get(friendLocation + "Friends.json", new String[0]), new LinkOption[0])) {
            return;
        }
        InputStream inputStream = Files.newInputStream(Paths.get(friendLocation + "Friends.json", new String[0]), new OpenOption[0]);
        JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Friends") == null) {
            return;
        }
        JsonArray friendObject = mainObject.get("Friends").getAsJsonArray();
        friendObject.forEach(object -> SocialManager.addFriend(object.getAsString()));
        inputStream.close();
    }

    private static void loadSpecialNames() throws IOException {
        String friendLocation = "gs++/Misc/";
        if (!Files.exists(Paths.get(friendLocation + "SpecialNames.json", new String[0]), new LinkOption[0])) {
            for (String defaultValue : new String[]{"nocatsnolife", "gamesense", "gs", "sable", "phantom826", "doogie13", "soulbond", "vqk", "anonymousplayer", "lambdaclient", "\u2063", "\u0262\ua731", "0b00101010", "a2h", "hoosier", "aven", "eighttwosix"}) {
                SocialManager.addSpecialName(defaultValue);
            }
            return;
        }
        InputStream inputStream = Files.newInputStream(Paths.get(friendLocation + "SpecialNames.json", new String[0]), new OpenOption[0]);
        JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("SpecialNames") == null) {
            return;
        }
        JsonArray friendObject = mainObject.get("SpecialNames").getAsJsonArray();
        friendObject.forEach(object -> SocialManager.addSpecialName(object.getAsString()));
        inputStream.close();
    }

    private static void loadEnemiesList() throws IOException {
        String enemyLocation = "gs++/Misc/";
        if (!Files.exists(Paths.get(enemyLocation + "Enemies.json", new String[0]), new LinkOption[0])) {
            return;
        }
        InputStream inputStream = Files.newInputStream(Paths.get(enemyLocation + "Enemies.json", new String[0]), new OpenOption[0]);
        JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Enemies") == null) {
            return;
        }
        JsonArray enemyObject = mainObject.get("Enemies").getAsJsonArray();
        enemyObject.forEach(object -> SocialManager.addEnemy(object.getAsString()));
        inputStream.close();
    }

    private static void loadClickGUIPositions() throws IOException {
        GameSenseGUI.gui.loadConfig(new GuiConfig("gs++/Main/"));
    }

    private static void loadAutoGG() throws IOException {
        String fileLocation = "gs++/Misc/";
        if (!Files.exists(Paths.get(fileLocation + "AutoGG.json", new String[0]), new LinkOption[0])) {
            return;
        }
        InputStream inputStream = Files.newInputStream(Paths.get(fileLocation + "AutoGG.json", new String[0]), new OpenOption[0]);
        JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Messages") == null) {
            return;
        }
        JsonArray messageObject = mainObject.get("Messages").getAsJsonArray();
        messageObject.forEach(object -> AutoGG.addAutoGgMessage(object.getAsString()));
        inputStream.close();
    }

    private static void loadAutoReply() throws IOException {
        String fileLocation = "gs++/Misc/";
        if (!Files.exists(Paths.get(fileLocation + "AutoReply.json", new String[0]), new LinkOption[0])) {
            return;
        }
        InputStream inputStream = Files.newInputStream(Paths.get(fileLocation + "AutoReply.json", new String[0]), new OpenOption[0]);
        JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("AutoReply") == null) {
            return;
        }
        JsonObject arObject = mainObject.get("AutoReply").getAsJsonObject();
        JsonElement dataObject = arObject.get("Message");
        if (dataObject != null && dataObject.isJsonPrimitive()) {
            AutoReply.setReply(dataObject.getAsString());
        }
        inputStream.close();
    }

    private static void loadAutoRespawn() throws IOException {
        String fileLocation = "gs++/Misc/";
        if (!Files.exists(Paths.get(fileLocation + "AutoRespawn.json", new String[0]), new LinkOption[0])) {
            return;
        }
        InputStream inputStream = Files.newInputStream(Paths.get(fileLocation + "AutoRespawn.json", new String[0]), new OpenOption[0]);
        JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Message") == null) {
            return;
        }
        JsonElement dataObject = mainObject.get("Message");
        if (dataObject != null && dataObject.isJsonPrimitive()) {
            AutoRespawn.setAutoRespawnMessage(dataObject.getAsString());
        }
        inputStream.close();
    }
}

