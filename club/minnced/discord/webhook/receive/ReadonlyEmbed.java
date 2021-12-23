/*
 * Decompiled with CFR 0.152.
 */
package club.minnced.discord.webhook.receive;

import club.minnced.discord.webhook.send.WebhookEmbed;
import java.time.OffsetDateTime;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONPropertyName;
import org.json.JSONString;

public class ReadonlyEmbed
extends WebhookEmbed {
    private final EmbedProvider provider;
    private final EmbedImage thumbnail;
    private final EmbedImage image;
    private final EmbedVideo video;

    public ReadonlyEmbed(@Nullable OffsetDateTime timestamp, @Nullable Integer color, @Nullable String description, @Nullable EmbedImage thumbnail, @Nullable EmbedImage image, @Nullable WebhookEmbed.EmbedFooter footer, @Nullable WebhookEmbed.EmbedTitle title, @Nullable WebhookEmbed.EmbedAuthor author, @NotNull List<WebhookEmbed.EmbedField> fields, @Nullable EmbedProvider provider, @Nullable EmbedVideo video) {
        super(timestamp, color, description, thumbnail == null ? null : thumbnail.getUrl(), image == null ? null : image.getUrl(), footer, title, author, fields);
        this.thumbnail = thumbnail;
        this.image = image;
        this.provider = provider;
        this.video = video;
    }

    @Nullable
    public EmbedProvider getProvider() {
        return this.provider;
    }

    @Nullable
    public EmbedImage getThumbnail() {
        return this.thumbnail;
    }

    @Nullable
    public EmbedImage getImage() {
        return this.image;
    }

    @Nullable
    public EmbedVideo getVideo() {
        return this.video;
    }

    @Override
    @NotNull
    public WebhookEmbed reduced() {
        return new WebhookEmbed(this.getTimestamp(), this.getColor(), this.getDescription(), this.thumbnail == null ? null : this.thumbnail.getUrl(), this.image == null ? null : this.image.getUrl(), this.getFooter(), this.getTitle(), this.getAuthor(), this.getFields());
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }

    @Override
    public String toJSONString() {
        JSONObject base = new JSONObject(super.toJSONString());
        base.put("provider", (Object)this.provider).put("thumbnail", (Object)this.thumbnail).put("video", (Object)this.video).put("image", (Object)this.image);
        if (this.getTitle() != null) {
            base.put("title", (Object)this.getTitle().getText());
            base.put("url", (Object)this.getTitle().getUrl());
        }
        return base.toString();
    }

    public static class EmbedImage
    implements JSONString {
        private final String url;
        private final String proxyUrl;
        private final int width;
        private final int height;

        public EmbedImage(@NotNull String url, @NotNull String proxyUrl, int width, int height) {
            this.url = url;
            this.proxyUrl = proxyUrl;
            this.width = width;
            this.height = height;
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

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public String toString() {
            return this.toJSONString();
        }

        public String toJSONString() {
            return new JSONObject((Object)this).toString();
        }
    }

    public static class EmbedVideo
    implements JSONString {
        private final String url;
        private final int width;
        private final int height;

        public EmbedVideo(@NotNull String url, int width, int height) {
            this.url = url;
            this.width = width;
            this.height = height;
        }

        @NotNull
        public String getUrl() {
            return this.url;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public String toString() {
            return this.toJSONString();
        }

        public String toJSONString() {
            return new JSONObject((Object)this).toString();
        }
    }

    public static class EmbedProvider
    implements JSONString {
        private final String name;
        private final String url;

        public EmbedProvider(@NotNull String name, @NotNull String url) {
            this.name = name;
            this.url = url;
        }

        @NotNull
        public String getName() {
            return this.name;
        }

        @NotNull
        public String getUrl() {
            return this.url;
        }

        public String toString() {
            return this.toJSONString();
        }

        public String toJSONString() {
            return new JSONObject((Object)this).toString();
        }
    }
}

