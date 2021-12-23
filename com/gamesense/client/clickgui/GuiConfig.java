/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.clickgui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.lukflug.panelstudio.config.IConfigList;
import com.lukflug.panelstudio.config.IPanelConfig;
import java.awt.Dimension;
import java.awt.Point;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Paths;

public class GuiConfig
implements IConfigList {
    private final String fileLocation;
    private JsonObject panelObject = null;

    public GuiConfig(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    @Override
    public void begin(boolean loading) {
        if (loading) {
            if (!Files.exists(Paths.get(this.fileLocation + "ClickGUI.json", new String[0]), new LinkOption[0])) {
                return;
            }
            try {
                InputStream inputStream = Files.newInputStream(Paths.get(this.fileLocation + "ClickGUI.json", new String[0]), new OpenOption[0]);
                JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
                if (mainObject.get("Panels") == null) {
                    return;
                }
                this.panelObject = mainObject.get("Panels").getAsJsonObject();
                inputStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.panelObject = new JsonObject();
        }
    }

    @Override
    public void end(boolean loading) {
        if (this.panelObject == null) {
            return;
        }
        if (!loading) {
            try {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream(this.fileLocation + "ClickGUI.json"), StandardCharsets.UTF_8);
                JsonObject mainObject = new JsonObject();
                mainObject.add("Panels", (JsonElement)this.panelObject);
                String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
                fileOutputStreamWriter.write(jsonString);
                fileOutputStreamWriter.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.panelObject = null;
    }

    @Override
    public IPanelConfig addPanel(String title) {
        if (this.panelObject == null) {
            return null;
        }
        JsonObject valueObject = new JsonObject();
        this.panelObject.add(title, (JsonElement)valueObject);
        return new GSPanelConfig(valueObject);
    }

    @Override
    public IPanelConfig getPanel(String title) {
        if (this.panelObject == null) {
            return null;
        }
        JsonElement configObject = this.panelObject.get(title);
        if (configObject != null && configObject.isJsonObject()) {
            return new GSPanelConfig(configObject.getAsJsonObject());
        }
        return null;
    }

    private static class GSPanelConfig
    implements IPanelConfig {
        private final JsonObject configObject;

        public GSPanelConfig(JsonObject configObject) {
            this.configObject = configObject;
        }

        @Override
        public void savePositon(Point position) {
            this.configObject.add("PosX", (JsonElement)new JsonPrimitive((Number)position.x));
            this.configObject.add("PosY", (JsonElement)new JsonPrimitive((Number)position.y));
        }

        @Override
        public void saveSize(Dimension size) {
        }

        @Override
        public Point loadPosition() {
            Point point = new Point();
            JsonElement panelPosXObject = this.configObject.get("PosX");
            if (panelPosXObject == null || !panelPosXObject.isJsonPrimitive()) {
                return null;
            }
            point.x = panelPosXObject.getAsInt();
            JsonElement panelPosYObject = this.configObject.get("PosY");
            if (panelPosYObject == null || !panelPosYObject.isJsonPrimitive()) {
                return null;
            }
            point.y = panelPosYObject.getAsInt();
            return point;
        }

        @Override
        public Dimension loadSize() {
            return null;
        }

        @Override
        public void saveState(boolean state) {
            this.configObject.add("State", (JsonElement)new JsonPrimitive(Boolean.valueOf(state)));
        }

        @Override
        public boolean loadState() {
            JsonElement panelOpenObject = this.configObject.get("State");
            if (panelOpenObject != null && panelOpenObject.isJsonPrimitive()) {
                return panelOpenObject.getAsBoolean();
            }
            return false;
        }
    }
}

