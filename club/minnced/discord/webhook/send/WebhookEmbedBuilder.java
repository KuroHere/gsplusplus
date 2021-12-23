/*
 * Decompiled with CFR 0.152.
 */
package club.minnced.discord.webhook.send;

import club.minnced.discord.webhook.send.WebhookEmbed;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.EmbedAuthorData;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedFooterData;
import discord4j.discordjson.json.EmbedImageData;
import discord4j.discordjson.json.EmbedThumbnailData;
import discord4j.discordjson.possible.Possible;
import java.awt.Color;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedImage;
import org.javacord.api.entity.message.embed.EmbedThumbnail;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WebhookEmbedBuilder {
    private final List<WebhookEmbed.EmbedField> fields = new ArrayList<WebhookEmbed.EmbedField>(10);
    private OffsetDateTime timestamp;
    private Integer color;
    private String description;
    private String thumbnailUrl;
    private String imageUrl;
    private WebhookEmbed.EmbedFooter footer;
    private WebhookEmbed.EmbedTitle title;
    private WebhookEmbed.EmbedAuthor author;

    public WebhookEmbedBuilder() {
    }

    public WebhookEmbedBuilder(@Nullable WebhookEmbed embed) {
        this();
        if (embed != null) {
            this.timestamp = embed.getTimestamp();
            this.color = embed.getColor();
            this.description = embed.getDescription();
            this.thumbnailUrl = embed.getThumbnailUrl();
            this.imageUrl = embed.getImageUrl();
            this.footer = embed.getFooter();
            this.title = embed.getTitle();
            this.author = embed.getAuthor();
            this.fields.addAll(embed.getFields());
        }
    }

    public void reset() {
        this.fields.clear();
        this.timestamp = null;
        this.color = null;
        this.description = null;
        this.thumbnailUrl = null;
        this.imageUrl = null;
        this.footer = null;
        this.title = null;
        this.author = null;
    }

    @NotNull
    public WebhookEmbedBuilder setTimestamp(@Nullable TemporalAccessor timestamp) {
        this.timestamp = timestamp instanceof Instant ? OffsetDateTime.ofInstant((Instant)timestamp, ZoneId.of("UTC")) : (timestamp == null ? null : OffsetDateTime.from(timestamp));
        return this;
    }

    @NotNull
    public WebhookEmbedBuilder setColor(@Nullable Integer color) {
        this.color = color;
        return this;
    }

    @NotNull
    public WebhookEmbedBuilder setDescription(@Nullable String description) {
        this.description = description;
        return this;
    }

    @NotNull
    public WebhookEmbedBuilder setThumbnailUrl(@Nullable String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
        return this;
    }

    @NotNull
    public WebhookEmbedBuilder setImageUrl(@Nullable String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    @NotNull
    public WebhookEmbedBuilder setFooter(@Nullable WebhookEmbed.EmbedFooter footer) {
        this.footer = footer;
        return this;
    }

    @NotNull
    public WebhookEmbedBuilder setTitle(@Nullable WebhookEmbed.EmbedTitle title) {
        this.title = title;
        return this;
    }

    @NotNull
    public WebhookEmbedBuilder setAuthor(@Nullable WebhookEmbed.EmbedAuthor author) {
        this.author = author;
        return this;
    }

    @NotNull
    public WebhookEmbedBuilder addField(@NotNull WebhookEmbed.EmbedField field) {
        if (this.fields.size() == 25) {
            throw new IllegalStateException("Cannot add more than 25 fields");
        }
        this.fields.add(Objects.requireNonNull(field));
        return this;
    }

    public boolean isEmpty() {
        return this.isEmpty(this.description) && this.isEmpty(this.imageUrl) && this.isEmpty(this.thumbnailUrl) && this.isFieldsEmpty() && this.isAuthorEmpty() && this.isTitleEmpty() && this.isFooterEmpty() && this.timestamp == null;
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean isTitleEmpty() {
        return this.title == null || this.isEmpty(this.title.getText());
    }

    private boolean isFooterEmpty() {
        return this.footer == null || this.isEmpty(this.footer.getText());
    }

    private boolean isAuthorEmpty() {
        return this.author == null || this.isEmpty(this.author.getName());
    }

    private boolean isFieldsEmpty() {
        if (this.fields.isEmpty()) {
            return true;
        }
        return this.fields.stream().allMatch(f -> this.isEmpty(f.getName()) && this.isEmpty(f.getValue()));
    }

    @NotNull
    public WebhookEmbed build() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Cannot build an empty embed");
        }
        return new WebhookEmbed(this.timestamp, this.color, this.description, this.thumbnailUrl, this.imageUrl, this.footer, this.title, this.author, new ArrayList<WebhookEmbed.EmbedField>(this.fields));
    }

    @NotNull
    public static WebhookEmbedBuilder fromJDA(@NotNull MessageEmbed embed) {
        WebhookEmbedBuilder builder = new WebhookEmbedBuilder();
        String url = embed.getUrl();
        String title = embed.getTitle();
        String description = embed.getDescription();
        MessageEmbed.Thumbnail thumbnail = embed.getThumbnail();
        MessageEmbed.AuthorInfo author = embed.getAuthor();
        MessageEmbed.Footer footer = embed.getFooter();
        MessageEmbed.ImageInfo image = embed.getImage();
        List fields = embed.getFields();
        int color = embed.getColorRaw();
        OffsetDateTime timestamp = embed.getTimestamp();
        if (title != null) {
            builder.setTitle(new WebhookEmbed.EmbedTitle(title, url));
        }
        if (description != null) {
            builder.setDescription(description);
        }
        if (thumbnail != null) {
            builder.setThumbnailUrl(thumbnail.getUrl());
        }
        if (author != null) {
            builder.setAuthor(new WebhookEmbed.EmbedAuthor(author.getName(), author.getIconUrl(), author.getUrl()));
        }
        if (footer != null) {
            builder.setFooter(new WebhookEmbed.EmbedFooter(footer.getText(), footer.getIconUrl()));
        }
        if (image != null) {
            builder.setImageUrl(image.getUrl());
        }
        if (!fields.isEmpty()) {
            fields.forEach(field -> builder.addField(new WebhookEmbed.EmbedField(field.isInline(), field.getName(), field.getValue())));
        }
        if (color != 0x1FFFFFFF) {
            builder.setColor(color);
        }
        if (timestamp != null) {
            builder.setTimestamp(timestamp);
        }
        return builder;
    }

    @NotNull
    public static WebhookEmbedBuilder fromJavacord(@NotNull Embed embed) {
        WebhookEmbedBuilder builder = new WebhookEmbedBuilder();
        embed.getTitle().ifPresent(title -> builder.setTitle(new WebhookEmbed.EmbedTitle((String)title, embed.getUrl().map(URL::toString).orElse(null))));
        embed.getDescription().ifPresent(builder::setDescription);
        embed.getTimestamp().ifPresent(builder::setTimestamp);
        embed.getColor().map(Color::getRGB).ifPresent(builder::setColor);
        embed.getFooter().map(footer -> new WebhookEmbed.EmbedFooter((String)footer.getText().orElseThrow(NullPointerException::new), footer.getIconUrl().map(URL::toString).orElse(null))).ifPresent(builder::setFooter);
        embed.getImage().map(EmbedImage::getUrl).map(URL::toString).ifPresent(builder::setImageUrl);
        embed.getThumbnail().map(EmbedThumbnail::getUrl).map(URL::toString).ifPresent(builder::setThumbnailUrl);
        embed.getFields().stream().map(field -> new WebhookEmbed.EmbedField(field.isInline(), field.getName(), field.getValue())).forEach(builder::addField);
        return builder;
    }

    @NotNull
    public static WebhookEmbedBuilder fromD4J(@NotNull Consumer<? super EmbedCreateSpec> callback) {
        EmbedCreateSpec spec = new EmbedCreateSpec();
        callback.accept((EmbedCreateSpec)spec);
        EmbedData data = spec.asRequest();
        return WebhookEmbedBuilder.fromD4J(data);
    }

    @NotNull
    public static WebhookEmbedBuilder fromD4J(@NotNull EmbedData data) {
        WebhookEmbedBuilder builder = new WebhookEmbedBuilder();
        Possible title = data.title();
        Possible description = data.description();
        Possible url = data.url();
        Possible timestamp = data.timestamp();
        Possible color = data.color();
        Possible footer = data.footer();
        Possible image = data.image();
        Possible thumbnail = data.thumbnail();
        Possible author = data.author();
        Possible fields = data.fields();
        if (!title.isAbsent()) {
            builder.setTitle(new WebhookEmbed.EmbedTitle((String)title.get(), url.toOptional().orElse(null)));
        }
        if (!description.isAbsent()) {
            builder.setDescription((String)description.get());
        }
        if (!timestamp.isAbsent()) {
            builder.setTimestamp(OffsetDateTime.parse((CharSequence)timestamp.get()));
        }
        if (!color.isAbsent()) {
            builder.setColor((Integer)color.get());
        }
        if (!footer.isAbsent()) {
            builder.setFooter(new WebhookEmbed.EmbedFooter(((EmbedFooterData)footer.get()).text(), ((EmbedFooterData)footer.get()).iconUrl().toOptional().orElse(null)));
        }
        if (!image.isAbsent()) {
            builder.setImageUrl((String)((EmbedImageData)image.get()).url().get());
        }
        if (!thumbnail.isAbsent()) {
            builder.setThumbnailUrl((String)((EmbedThumbnailData)thumbnail.get()).url().get());
        }
        if (!author.isAbsent()) {
            EmbedAuthorData authorData = (EmbedAuthorData)author.get();
            builder.setAuthor(new WebhookEmbed.EmbedAuthor((String)authorData.name().get(), authorData.iconUrl().toOptional().orElse(null), authorData.url().toOptional().orElse(null)));
        }
        if (!fields.isAbsent()) {
            ((List)fields.get()).stream().map(field -> new WebhookEmbed.EmbedField(field.inline().toOptional().orElse(false), field.name(), field.value())).forEach(builder::addField);
        }
        return builder;
    }
}

