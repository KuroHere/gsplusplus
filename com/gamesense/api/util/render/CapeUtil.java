/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.render;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

public class CapeUtil {
    private static final List<UUID> uuids = new ArrayList<UUID>();
    public static final List<ResourceLocation> capes = new ArrayList<ResourceLocation>();
    private static final Minecraft mc = Minecraft.func_71410_x();

    public static void init() {
        try {
            String inputLine;
            capes.add(mc.func_110434_K().func_110578_a("black", new DynamicTexture(ImageIO.read(new URL("https://i.toxicaven.dev/Bm11ZriMSjHn/direct.png")))));
            capes.add(mc.func_110434_K().func_110578_a("white", new DynamicTexture(ImageIO.read(new URL("https://i.toxicaven.dev/hiHyRHocHDQD/direct.png")))));
            capes.add(mc.func_110434_K().func_110578_a("amber", new DynamicTexture(ImageIO.read(new URL("https://i.toxicaven.dev/2XtPEM75HImX/direct.png")))));
            URL capesList = new URL("https://raw.githubusercontent.com/TechAle/gsplusplus-assets/main/capeslist.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            while ((inputLine = in.readLine()) != null) {
                uuids.add(UUID.fromString(inputLine));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasCape(UUID id) {
        return uuids.contains(id);
    }
}

