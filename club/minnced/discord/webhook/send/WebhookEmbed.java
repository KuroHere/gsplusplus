/*
 * Decompiled with CFR 0.152.
 */
package club.minnced.discord.webhook.send;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONPropertyIgnore;
import org.json.JSONPropertyName;
import org.json.JSONString;

public class WebhookEmbed
implements JSONString {
    public static final int MAX_FIELDS = 25;
    private final OffsetDateTime timestamp;
    private final Integer color;
    private final String description;
    private final String thumbnailUrl;
    private final String imageUrl;
    private final EmbedFooter footer;
    private final EmbedTitle title;
    private final EmbedAuthor author;
    private final List<EmbedField> fields;

    public WebhookEmbed(@Nullable OffsetDateTime timestamp, @Nullable Integer color, @Nullable String description, @Nullable String thumbnailUrl, @Nullable String imageUrl, @Nullable EmbedFooter footer, @Nullable EmbedTitle title, @Nullable EmbedAuthor author, @NotNull List<EmbedField> fields) {
        this.timestamp = timestamp;
        this.color = color;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.imageUrl = imageUrl;
        this.footer = footer;
        this.title = title;
        this.author = author;
        this.fields = Collections.unmodifiableList(fields);
    }

    @JSONPropertyIgnore
    @Nullable
    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    @JSONPropertyIgnore
    @Nullable
    public String getImageUrl() {
        return this.imageUrl;
    }

    @Nullable
    public OffsetDateTime getTimestamp() {
        return this.timestamp;
    }

    @JSONPropertyIgnore
    @Nullable
    public EmbedTitle getTitle() {
        return this.title;
    }

    @Nullable
    public Integer getColor() {
        return this.color;
    }

    @Nullable
    public String getDescription() {
        return this.description;
    }

    @Nullable
    public EmbedFooter getFooter() {
        return this.footer;
    }

    @Nullable
    public EmbedAuthor getAuthor() {
        return this.author;
    }

    @NotNull
    public List<EmbedField> getFields() {
        return this.fields;
    }

    @NotNull
    public WebhookEmbed reduced() {
        return this;
    }

    public String toString() {
        return this.toJSONString();
    }

    public String toJSONString() {
        JSONObject json = new JSONObject();
        if (this.description != null) {
            json.put("description", (Object)this.description);
        }
        if (this.timestamp != null) {
            json.put("timestamp", (Object)this.timestamp);
        }
        if (this.color != null) {
            json.put("color", this.color & 0xFFFFFF);
        }
        if (this.author != null) {
            json.put("author", (Object)this.author);
        }
        if (this.footer != null) {
            json.put("footer", (Object)this.footer);
        }
        if (this.thumbnailUrl != null) {
            json.put("thumbnail", (Object)new JSONObject().put("url", (Object)this.thumbnailUrl));
        }
        if (this.imageUrl != null) {
            json.put("image", (Object)new JSONObject().put("url", (Object)this.imageUrl));
        }
        if (!this.fields.isEmpty()) {
            json.put("fields", this.fields);
        }
        if (this.title != null) {
            if (this.title.getUrl() != null) {
                json.put("url", (Object)this.title.url);
            }
            json.put("title", (Object)this.title.text);
        }
        return json.toString();
    }

    public static class EmbedTitle {
        private final String text;
        private final String url;

        public EmbedTitle(@NotNull String text, @Nullable String url) {
            this.text = Objects.requireNonNull(text);
            this.url = url;
        }

        @NotNull
        public String getText() {
            return this.text;
        }

        @Nullable
        public String getUrl() {
            return this.url;
        }

        public String toString() {
            return new JSONObject((Object)this).toString();
        }
    }

    public static class EmbedFooter
    implements JSONString {
        private final String text;
        private final String icon;

        public EmbedFooter(@NotNull String text, @Nullable String icon) {
            this.text = Objects.requireNonNull(text);
            this.icon = icon;
        }

        @NotNull
        public String getText() {
            return this.text;
        }

        @JSONPropertyName(value="icon_url")
        @Nullable
        public String getIconUrl() {
            return this.icon;
        }

        public String toString() {
            return this.toJSONString();
        }

        public String toJSONString() {
            return new JSONObject((Object)this).toString();
        }
    }

    public static class EmbedAuthor
    implements JSONString {
        private final String name;
        private final String iconUrl;
        private final String url;

        public EmbedAuthor(@NotNull String name, @Nullable String iconUrl, @Nullable String url) {
            this.name = Objects.requireNonNull(name);
            this.iconUrl = iconUrl;
            this.url = url;
        }

        @NotNull
        public String getName() {
            return this.name;
        }

        @JSONPropertyName(value="icon_url")
        @Nullable
        public String getIconUrl() {
            return this.iconUrl;
        }

        @Nullable
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

    public static class EmbedField
    implements JSONString {
        private final boolean inline;
        private final String name;
        private final String value;

        public EmbedField(boolean inline, @NotNull String name, @NotNull String value) {
            this.inline = inline;
            this.name = Objects.requireNonNull(name);
            this.value = Objects.requireNonNull(value);
        }

        public boolean isInline() {
            return this.inline;
        }

        @NotNull
        public String getName() {
            return this.name;
        }

        @NotNull
        public String getValue() {
            return this.value;
        }

        public String toString() {
            return this.toJSONString();
        }

        public String toJSONString() {
            return new JSONObject((Object)this).toString();
        }
    }
}

