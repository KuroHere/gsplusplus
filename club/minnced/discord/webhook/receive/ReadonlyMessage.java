/*
 * Decompiled with CFR 0.152.
 */
package club.minnced.discord.webhook.receive;

import club.minnced.discord.webhook.receive.ReadonlyAttachment;
import club.minnced.discord.webhook.receive.ReadonlyEmbed;
import club.minnced.discord.webhook.receive.ReadonlyUser;
import club.minnced.discord.webhook.send.WebhookMessage;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONString;

public class ReadonlyMessage
implements JSONString {
    private final long id;
    private final long channelId;
    private final boolean mentionsEveryone;
    private final boolean tts;
    private final ReadonlyUser author;
    private final String content;
    private final List<ReadonlyEmbed> embeds;
    private final List<ReadonlyAttachment> attachments;
    private final List<ReadonlyUser> mentionedUsers;
    private final List<Long> mentionedRoles;

    public ReadonlyMessage(long id, long channelId, boolean mentionsEveryone, boolean tts, @NotNull ReadonlyUser author, @NotNull String content, @NotNull List<ReadonlyEmbed> embeds, @NotNull List<ReadonlyAttachment> attachments, @NotNull List<ReadonlyUser> mentionedUsers, @NotNull List<Long> mentionedRoles) {
        this.id = id;
        this.channelId = channelId;
        this.mentionsEveryone = mentionsEveryone;
        this.tts = tts;
        this.author = author;
        this.content = content;
        this.embeds = embeds;
        this.attachments = attachments;
        this.mentionedUsers = mentionedUsers;
        this.mentionedRoles = mentionedRoles;
    }

    public long getId() {
        return this.id;
    }

    public long getChannelId() {
        return this.channelId;
    }

    public boolean isMentionsEveryone() {
        return this.mentionsEveryone;
    }

    public boolean isTTS() {
        return this.tts;
    }

    @NotNull
    public ReadonlyUser getAuthor() {
        return this.author;
    }

    @NotNull
    public String getContent() {
        return this.content;
    }

    @NotNull
    public List<ReadonlyEmbed> getEmbeds() {
        return this.embeds;
    }

    @NotNull
    public List<ReadonlyAttachment> getAttachments() {
        return this.attachments;
    }

    @NotNull
    public List<ReadonlyUser> getMentionedUsers() {
        return this.mentionedUsers;
    }

    @NotNull
    public List<Long> getMentionedRoles() {
        return this.mentionedRoles;
    }

    @NotNull
    public WebhookMessage toWebhookMessage() {
        return WebhookMessage.from(this);
    }

    public String toString() {
        return this.toJSONString();
    }

    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("content", (Object)this.content).put("embeds", this.embeds).put("mentions", this.mentionedUsers).put("mention_roles", this.mentionedRoles).put("attachments", this.attachments).put("author", (Object)this.author).put("tts", this.tts).put("id", (Object)Long.toUnsignedString(this.id)).put("channel_id", (Object)Long.toUnsignedString(this.channelId)).put("mention_everyone", this.mentionsEveryone);
        return json.toString();
    }
}

