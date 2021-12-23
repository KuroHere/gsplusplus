/*
 * Decompiled with CFR 0.152.
 */
package club.minnced.discord.webhook.receive;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONPropertyName;
import org.json.JSONString;

public class ReadonlyAttachment
implements JSONString {
    private final String url;
    private final String proxyUrl;
    private final String fileName;
    private final int width;
    private final int height;
    private final int size;
    private final long id;

    public ReadonlyAttachment(@NotNull String url, @NotNull String proxyUrl, @NotNull String fileName, int width, int height, int size, long id) {
        this.url = url;
        this.proxyUrl = proxyUrl;
        this.fileName = fileName;
        this.width = width;
        this.height = height;
        this.size = size;
        this.id = id;
    }

    @NotNull
    public String getUrl() {
        return this.url;
    }

    @JSONPropertyName(value="proxy_url")
    @NotNull
    public String getProxyUrl() {
        return this.proxyUrl;
    }

    @JSONPropertyName(value="filename")
    @NotNull
    public String getFileName() {
        return this.fileName;
    }

    public int getSize() {
        return this.size;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public long getId() {
        return this.id;
    }

    public String toString() {
        return this.toJSONString();
    }

    public String toJSONString() {
        return new JSONObject((Object)this).toString();
    }
}

