/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.player;

import com.google.common.collect.Maps;
import java.net.URL;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class NameUtil {
    private static final Map<String, String> uuidNameCache = Maps.newConcurrentMap();

    public static String resolveName(String uuid) {
        if (uuidNameCache.containsKey(uuid = uuid.replace("-", ""))) {
            return uuidNameCache.get(uuid);
        }
        String url = "https://api.mojang.com/user/profiles/" + uuid + "/names";
        try {
            JSONObject latestName;
            JSONArray jsonArray;
            String nameJson = IOUtils.toString((URL)new URL(url));
            if (nameJson != null && nameJson.length() > 0 && (jsonArray = (JSONArray)JSONValue.parseWithException(nameJson)) != null && (latestName = (JSONObject)jsonArray.get(jsonArray.size() - 1)) != null) {
                return latestName.get("name").toString();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

