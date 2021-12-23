/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import net.minecraft.item.ItemStack;

@Command.Declaration(name="AutoGear", syntax="gear set/save/del/list [name]", alias={"gear", "gr", "kit"})
public class AutoGearCommand
extends Command {
    private static final String pathSave = "gs++/Misc/AutoGear.json";
    private static final HashMap<String, String> errorMessage = new HashMap<String, String>(){
        {
            this.put("NoPar", "Not enough parameters");
            this.put("Exist", "This kit arleady exist");
            this.put("Saving", "Error saving the file");
            this.put("NoEx", "Kit not found");
        }
    };

    @Override
    public void onCommand(String command, String[] message) {
        switch (message[0].toLowerCase()) {
            case "list": {
                if (message.length == 1) {
                    this.listMessage();
                    break;
                }
                AutoGearCommand.errorMessage("NoPar");
                break;
            }
            case "set": {
                if (message.length == 2) {
                    this.set(message[1]);
                    break;
                }
                AutoGearCommand.errorMessage("NoPar");
                break;
            }
            case "save": 
            case "add": 
            case "create": {
                if (message.length == 2) {
                    this.save(message[1]);
                    break;
                }
                AutoGearCommand.errorMessage("NoPar");
                break;
            }
            case "del": {
                if (message.length == 2) {
                    this.delete(message[1]);
                    break;
                }
                AutoGearCommand.errorMessage("NoPar");
                break;
            }
            default: {
                MessageBus.sendCommandMessage("AutoGear message is: gear set/save/del/list [name]", true);
            }
        }
    }

    private void listMessage() {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader(pathSave)).getAsJsonObject();
            int lenghtJson = completeJson.entrySet().size();
            for (int i = 0; i < lenghtJson; ++i) {
                String item = new JsonParser().parse((Reader)new FileReader(pathSave)).getAsJsonObject().entrySet().toArray()[i].toString().split("=")[0];
                if (item.equals("pointer")) continue;
                PistonCrystal.printDebug("Kit avaible: " + item, false);
            }
        }
        catch (IOException e) {
            AutoGearCommand.errorMessage("NoEx");
        }
    }

    private void delete(String name) {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader(pathSave)).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                completeJson.remove(name);
                if (completeJson.get("pointer").getAsString().equals(name)) {
                    completeJson.addProperty("pointer", "none");
                }
                this.saveFile(completeJson, name, "deleted");
            } else {
                AutoGearCommand.errorMessage("NoEx");
            }
        }
        catch (IOException e) {
            AutoGearCommand.errorMessage("NoEx");
        }
    }

    private void set(String name) {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader(pathSave)).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                completeJson.addProperty("pointer", name);
                this.saveFile(completeJson, name, "selected");
            } else {
                AutoGearCommand.errorMessage("NoEx");
            }
        }
        catch (IOException e) {
            AutoGearCommand.errorMessage("NoEx");
        }
    }

    private void save(String name) {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader(pathSave)).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                AutoGearCommand.errorMessage("Exist");
                return;
            }
        }
        catch (IOException e) {
            completeJson.addProperty("pointer", "none");
        }
        StringBuilder jsonInventory = new StringBuilder();
        for (ItemStack item : AutoGearCommand.mc.field_71439_g.field_71071_by.field_70462_a) {
            jsonInventory.append(item.func_77973_b().getRegistryName().toString() + item.func_77960_j()).append(" ");
        }
        completeJson.addProperty(name, jsonInventory.toString());
        this.saveFile(completeJson, name, "saved");
    }

    private void saveFile(JsonObject completeJson, String name, String operation) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(pathSave));
            bw.write(completeJson.toString());
            bw.close();
            PistonCrystal.printDebug("Kit " + name + " " + operation, false);
        }
        catch (IOException e) {
            AutoGearCommand.errorMessage("Saving");
        }
    }

    private static void errorMessage(String e) {
        PistonCrystal.printDebug("Error: " + errorMessage.get(e), true);
    }

    public static String getCurrentSet() {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader(pathSave)).getAsJsonObject();
            if (!completeJson.get("pointer").getAsString().equals("none")) {
                return completeJson.get("pointer").getAsString();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        AutoGearCommand.errorMessage("NoEx");
        return "";
    }

    public static String getInventoryKit(String kit) {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader(pathSave)).getAsJsonObject();
            return completeJson.get(kit).getAsString();
        }
        catch (IOException iOException) {
            AutoGearCommand.errorMessage("NoEx");
            return "";
        }
    }
}

