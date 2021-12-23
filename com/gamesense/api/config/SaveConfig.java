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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;

public class SaveConfig {
    public static final String fileName = "gs++/";
    private static final String moduleName = "Modules/";
    private static final String mainName = "Main/";
    private static final String miscName = "Misc/";

    public static void init() {
        try {
            SaveConfig.saveConfig();
            SaveConfig.saveModules();
            SaveConfig.saveEnabledModules();
            SaveConfig.saveModuleKeybinds();
            SaveConfig.saveDrawnModules();
            SaveConfig.saveToggleMessagesModules();
            SaveConfig.saveCommandPrefix();
            SaveConfig.saveCustomFont();
            SaveConfig.saveFriendsList();
            SaveConfig.saveEnemiesList();
            SaveConfig.saveSpecialNames();
            SaveConfig.saveClickGUIPositions();
            SaveConfig.saveAutoGG();
            SaveConfig.saveAutoReply();
            SaveConfig.saveAutoRespawn();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        GameSense.LOGGER.info("Saved Config!");
    }

    private static void saveConfig() throws IOException {
        if (!Files.exists(Paths.get(fileName, new String[0]), new LinkOption[0])) {
            Files.createDirectories(Paths.get(fileName, new String[0]), new FileAttribute[0]);
        }
        if (!Files.exists(Paths.get("gs++/Modules/", new String[0]), new LinkOption[0])) {
            Files.createDirectories(Paths.get("gs++/Modules/", new String[0]), new FileAttribute[0]);
        }
        if (!Files.exists(Paths.get("gs++/Main/", new String[0]), new LinkOption[0])) {
            Files.createDirectories(Paths.get("gs++/Main/", new String[0]), new FileAttribute[0]);
        }
        if (!Files.exists(Paths.get("gs++/Misc/", new String[0]), new LinkOption[0])) {
            Files.createDirectories(Paths.get("gs++/Misc/", new String[0]), new FileAttribute[0]);
        }
    }

    private static void registerFiles(String location, String name) throws IOException {
        if (Files.exists(Paths.get(fileName + location + name + ".json", new String[0]), new LinkOption[0])) {
            File file = new File(fileName + location + name + ".json");
            file.delete();
        }
        Files.createFile(Paths.get(fileName + location + name + ".json", new String[0]), new FileAttribute[0]);
    }

    private static void saveModules() throws IOException {
        for (Module module : ModuleManager.getModules()) {
            try {
                SaveConfig.saveModuleDirect(module);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveModuleDirect(Module module) throws IOException {
        SaveConfig.registerFiles(moduleName, module.getName());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream("gs++/Modules/" + module.getName() + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject settingObject = new JsonObject();
        moduleObject.add("Module", (JsonElement)new JsonPrimitive(module.getName()));
        for (Setting setting : SettingsManager.getSettingsForModule(module)) {
            if (setting instanceof BooleanSetting) {
                settingObject.add(setting.getConfigName(), (JsonElement)new JsonPrimitive((Boolean)((BooleanSetting)setting).getValue()));
                continue;
            }
            if (setting instanceof IntegerSetting) {
                settingObject.add(setting.getConfigName(), (JsonElement)new JsonPrimitive((Number)((IntegerSetting)setting).getValue()));
                continue;
            }
            if (setting instanceof DoubleSetting) {
                settingObject.add(setting.getConfigName(), (JsonElement)new JsonPrimitive((Number)((DoubleSetting)setting).getValue()));
                continue;
            }
            if (setting instanceof ColorSetting) {
                settingObject.add(setting.getConfigName(), (JsonElement)new JsonPrimitive((Number)((ColorSetting)setting).toLong()));
                continue;
            }
            if (setting instanceof ModeSetting) {
                settingObject.add(setting.getConfigName(), (JsonElement)new JsonPrimitive((String)((ModeSetting)setting).getValue()));
                continue;
            }
            if (!(setting instanceof StringSetting)) continue;
            settingObject.add(setting.getConfigName(), (JsonElement)new JsonPrimitive(((StringSetting)setting).getText()));
        }
        moduleObject.add("Settings", (JsonElement)settingObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveEnabledModules() throws IOException {
        SaveConfig.registerFiles(mainName, "Toggle");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream("gs++/Main/Toggle.json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject enabledObject = new JsonObject();
        for (Module module : ModuleManager.getModules()) {
            enabledObject.add(module.getName(), (JsonElement)new JsonPrimitive(Boolean.valueOf(module.isEnabled())));
        }
        moduleObject.add("Modules", (JsonElement)enabledObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveModuleKeybinds() throws IOException {
        SaveConfig.registerFiles(mainName, "Bind");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream("gs++/Main/Bind.json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject bindObject = new JsonObject();
        for (Module module : ModuleManager.getModules()) {
            bindObject.add(module.getName(), (JsonElement)new JsonPrimitive((Number)module.getBind()));
        }
        moduleObject.add("Modules", (JsonElement)bindObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveDrawnModules() throws IOException {
        SaveConfig.registerFiles(mainName, "Drawn");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream("gs++/Main/Drawn.json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject drawnObject = new JsonObject();
        for (Module module : ModuleManager.getModules()) {
            drawnObject.add(module.getName(), (JsonElement)new JsonPrimitive(Boolean.valueOf(module.isDrawn())));
        }
        moduleObject.add("Modules", (JsonElement)drawnObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveToggleMessagesModules() throws IOException {
        SaveConfig.registerFiles(mainName, "ToggleMessages");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream("gs++/Main/ToggleMessages.json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject toggleMessagesObject = new JsonObject();
        for (Module module : ModuleManager.getModules()) {
            toggleMessagesObject.add(module.getName(), (JsonElement)new JsonPrimitive(Boolean.valueOf(module.isToggleMsg())));
        }
        moduleObject.add("Modules", (JsonElement)toggleMessagesObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveCommandPrefix() throws IOException {
        SaveConfig.registerFiles(mainName, "CommandPrefix");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream("gs++/Main/CommandPrefix.json"), StandardCharsets.UTF_8);
        JsonObject prefixObject = new JsonObject();
        prefixObject.add("Prefix", (JsonElement)new JsonPrimitive(CommandManager.getCommandPrefix()));
        String jsonString = gson.toJson(new JsonParser().parse(prefixObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveCustomFont() throws IOException {
        SaveConfig.registerFiles(miscName, "CustomFont");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream("gs++/Misc/CustomFont.json"), StandardCharsets.UTF_8);
        JsonObject fontObject = new JsonObject();
        fontObject.add("Font Name", (JsonElement)new JsonPrimitive(GameSense.INSTANCE.cFontRenderer.getFontName()));
        fontObject.add("Font Size", (JsonElement)new JsonPrimitive((Number)GameSense.INSTANCE.cFontRenderer.getFontSize()));
        String jsonString = gson.toJson(new JsonParser().parse(fontObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveSpecialNames() throws IOException {
        SaveConfig.registerFiles(miscName, "SpecialNames");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream("gs++/Misc/SpecialNames.json"), StandardCharsets.UTF_8);
        JsonObject mainObject = new JsonObject();
        JsonArray friendArray = new JsonArray();
        SocialManager.getSpecialNames().forEach(specialNames -> friendArray.add(specialNames.getName()));
        mainObject.add("SpecialNames", (JsonElement)friendArray);
        String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveFriendsList() throws IOException {
        SaveConfig.registerFiles(miscName, "Friends");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream("gs++/Misc/Friends.json"), StandardCharsets.UTF_8);
        JsonObject mainObject = new JsonObject();
        JsonArray friendArray = new JsonArray();
        SocialManager.getFriends().forEach(friend -> friendArray.add(friend.getName()));
        mainObject.add("Friends", (JsonElement)friendArray);
        String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveEnemiesList() throws IOException {
        SaveConfig.registerFiles(miscName, "Enemies");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream("gs++/Misc/Enemies.json"), StandardCharsets.UTF_8);
        JsonObject mainObject = new JsonObject();
        JsonArray enemyArray = new JsonArray();
        SocialManager.getEnemies().forEach(enemy -> enemyArray.add(enemy.getName()));
        mainObject.add("Enemies", (JsonElement)enemyArray);
        String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveClickGUIPositions() throws IOException {
        SaveConfig.registerFiles(mainName, "ClickGUI");
        GameSenseGUI.gui.saveConfig(new GuiConfig("gs++/Main/"));
    }

    private static void saveAutoGG() throws IOException {
        SaveConfig.registerFiles(miscName, "AutoGG");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream("gs++/Misc/AutoGG.json"), StandardCharsets.UTF_8);
        JsonObject mainObject = new JsonObject();
        JsonArray messageArray = new JsonArray();
        AutoGG.getAutoGgMessages().forEach(arg_0 -> ((JsonArray)messageArray).add(arg_0));
        mainObject.add("Messages", (JsonElement)messageArray);
        String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveAutoReply() throws IOException {
        SaveConfig.registerFiles(miscName, "AutoReply");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream("gs++/Misc/AutoReply.json"), StandardCharsets.UTF_8);
        JsonObject mainObject = new JsonObject();
        JsonObject messageObject = new JsonObject();
        messageObject.add("Message", (JsonElement)new JsonPrimitive(AutoReply.getReply()));
        mainObject.add("AutoReply", (JsonElement)messageObject);
        String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveAutoRespawn() throws IOException {
        SaveConfig.registerFiles(miscName, "AutoRespawn");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream("gs++/Misc/AutoRespawn.json"), StandardCharsets.UTF_8);
        JsonObject mainObject = new JsonObject();
        mainObject.add("Message", (JsonElement)new JsonPrimitive(AutoRespawn.getAutoRespawnMessages()));
        String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
}

