/*
 * Decompiled with CFR 0.152.
 */
package club.minnced.discord.webhook.receive;

import club.minnced.discord.webhook.receive.ReadonlyAttachment;
import club.minnced.discord.webhook.receive.ReadonlyEmbed;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.receive.ReadonlyUser;
import club.minnced.discord.webhook.send.WebhookEmbed;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

public class EntityFactory {
    @NotNull
    public static ReadonlyUser makeUser(@NotNull JSONObject json) {
        long id = Long.parseUnsignedLong(json.getString("id"));
        String name = json.getString("username");
        String avatar = json.optString("avatar", null);
        short discriminator = Short.parseShort(json.getString("discriminator"));
        boolean bot = !json.isNull("bot") && json.getBoolean("bot");
        return new ReadonlyUser(id, discriminator, bot, name, avatar);
    }

    @NotNull
    public static ReadonlyAttachment makeAttachment(@NotNull JSONObject json) {
        String url = json.getString("url");
        String proxy = json.getString("proxy_url");
        String name = json.getString("filename");
        int size = json.getInt("size");
        int width = json.optInt("width", -1);
        int height = json.optInt("height", -1);
        long id = Long.parseUnsignedLong(json.getString("id"));
        return new ReadonlyAttachment(url, proxy, name, width, height, size, id);
    }

    @Nullable
    public static WebhookEmbed.EmbedField makeEmbedField(@Nullable JSONObject json) {
        if (json == null) {
            return null;
        }
        String name = json.getString("name");
        String value = json.getString("value");
        boolean inline = !json.isNull("inline") && json.getBoolean("inline");
        return new WebhookEmbed.EmbedField(inline, name, value);
    }

    @Nullable
    public static WebhookEmbed.EmbedAuthor makeEmbedAuthor(@Nullable JSONObject json) {
        if (json == null) {
            return null;
        }
        String name = json.getString("name");
        String url = json.optString("url", null);
        String icon = json.optString("icon_url", null);
        return new WebhookEmbed.EmbedAuthor(name, icon, url);
    }

    @Nullable
    public static WebhookEmbed.EmbedFooter makeEmbedFooter(@Nullable JSONObject json) {
        if (json == null) {
            return null;
        }
        String text = json.getString("text");
        String icon = json.optString("icon_url", null);
        return new WebhookEmbed.EmbedFooter(text, icon);
    }

    @Nullable
    public static WebhookEmbed.EmbedTitle makeEmbedTitle(@NotNull JSONObject json) {
        String text = json.optString("title", null);
        if (text == null) {
            return null;
        }
        String url = json.optString("url", null);
        return new WebhookEmbed.EmbedTitle(text, url);
    }

    @Nullable
    public static ReadonlyEmbed.EmbedImage makeEmbedImage(@Nullable JSONObject json) {
        if (json == null) {
            return null;
        }
        String url = json.getString("url");
        String proxyUrl = json.getString("proxy_url");
        int width = json.getInt("width");
        int height = json.getInt("height");
        return new ReadonlyEmbed.EmbedImage(url, proxyUrl, width, height);
    }

    @Nullable
    public static ReadonlyEmbed.EmbedProvider makeEmbedProvider(@Nullable JSONObject json) {
        if (json == null) {
            return null;
        }
        String url = json.getString("url");
        String name = json.getString("name");
        return new ReadonlyEmbed.EmbedProvider(name, url);
    }

    @Nullable
    public static ReadonlyEmbed.EmbedVideo makeEmbedVideo(@Nullable JSONObject json) {
        if (json == null) {
            return null;
        }
        String url = json.getString("url");
        int height = json.getInt("height");
        int width = json.getInt("width");
        return new ReadonlyEmbed.EmbedVideo(url, width, height);
    }

    @NotNull
    public static ReadonlyEmbed makeEmbed(@NotNull JSONObject json) {
        String description = json.optString("description", null);
        Integer color = json.isNull("color") ? null : Integer.valueOf(json.getInt("color"));
        ReadonlyEmbed.EmbedImage image = EntityFactory.makeEmbedImage(json.optJSONObject("image"));
        ReadonlyEmbed.EmbedImage thumbnail = EntityFactory.makeEmbedImage(json.optJSONObject("thumbnail"));
        ReadonlyEmbed.EmbedProvider provider = EntityFactory.makeEmbedProvider(json.optJSONObject("provider"));
        ReadonlyEmbed.EmbedVideo video = EntityFactory.makeEmbedVideo(json.optJSONObject("video"));
        WebhookEmbed.EmbedFooter footer = EntityFactory.makeEmbedFooter(json.optJSONObject("footer"));
        WebhookEmbed.EmbedAuthor author = EntityFactory.makeEmbedAuthor(json.optJSONObject("author"));
        WebhookEmbed.EmbedTitle title = EntityFactory.makeEmbedTitle(json);
        OffsetDateTime timestamp = json.isNull("timestamp") ? null : OffsetDateTime.parse(json.getString("timestamp"));
        JSONArray fieldArray = json.optJSONArray("fields");
        ArrayList<WebhookEmbed.EmbedField> fields = new ArrayList<WebhookEmbed.EmbedField>();
        if (fieldArray != null) {
            for (int i = 0; i < fieldArray.length(); ++i) {
                JSONObject obj = fieldArray.getJSONObject(i);
                WebhookEmbed.EmbedField field = EntityFactory.makeEmbedField(obj);
                if (field == null) continue;
                fields.add(field);
            }
        }
        return new ReadonlyEmbed(timestamp, color, description, thumbnail, image, footer, title, author, fields, provider, video);
    }

    @NotNull
    public static ReadonlyMessage makeMessage(@NotNull JSONObject json) {
        long id = Long.parseUnsignedLong(json.getString("id"));
        long channelId = Long.parseUnsignedLong(json.getString("channel_id"));
        ReadonlyUser author = EntityFactory.makeUser(json.getJSONObject("author"));
        String content = json.getString("content");
        boolean tts = json.getBoolean("tts");
        boolean mentionEveryone = json.getBoolean("mention_everyone");
        JSONArray usersArray = json.getJSONArray("mentions");
        JSONArray rolesArray = json.getJSONArray("mention_roles");
        JSONArray embedArray = json.getJSONArray("embeds");
        JSONArray attachmentArray = json.getJSONArray("attachments");
        List<ReadonlyUser> mentionedUsers = EntityFactory.convertToList(usersArray, EntityFactory::makeUser);
        List<ReadonlyEmbed> embeds = EntityFactory.convertToList(embedArray, EntityFactory::makeEmbed);
        List<ReadonlyAttachment> attachments = EntityFactory.convertToList(attachmentArray, EntityFactory::makeAttachment);
        ArrayList<Long> mentionedRoles = new ArrayList<Long>();
        for (int i = 0; i < rolesArray.length(); ++i) {
            mentionedRoles.add(Long.parseUnsignedLong(rolesArray.getString(i)));
        }
        return new ReadonlyMessage(id, channelId, mentionEveryone, tts, author, content, embeds, attachments, mentionedUsers, mentionedRoles);
    }

    private static <T> List<T> convertToList(JSONArray arr, Function<JSONObject, T> converter) {
        if (arr == null) {
            return Collections.emptyList();
        }
        ArrayList<T> list = new ArrayList<T>();
        for (int i = 0; i < arr.length(); ++i) {
            JSONObject json = arr.getJSONObject(i);
            T out = converter.apply(json);
            if (out == null) continue;
            list.add(out);
        }
        return Collections.unmodifiableList(list);
    }
}

