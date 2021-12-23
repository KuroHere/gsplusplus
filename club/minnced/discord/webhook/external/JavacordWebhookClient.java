/*
 * Decompiled with CFR 0.152.
 */
package club.minnced.discord.webhook.external;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import okhttp3.OkHttpClient;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.webhook.Webhook;
import org.jetbrains.annotations.NotNull;

public class JavacordWebhookClient
extends WebhookClient {
    public JavacordWebhookClient(long id, String token, boolean parseMessage, OkHttpClient client, ScheduledExecutorService pool, AllowedMentions mentions) {
        super(id, token, parseMessage, client, pool, mentions);
    }

    @NotNull
    public static WebhookClient from(@NotNull Webhook webhook) {
        return WebhookClientBuilder.fromJavacord(webhook).build();
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull Message message) {
        return this.send(WebhookMessageBuilder.fromJavacord(message).build());
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull Embed embed) {
        return this.send(WebhookEmbedBuilder.fromJavacord(embed).build(), new WebhookEmbed[0]);
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> edit(long messageId, @NotNull Message message) {
        return this.edit(messageId, WebhookMessageBuilder.fromJavacord(message).build());
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> edit(long messageId, @NotNull Embed embed) {
        return this.edit(messageId, WebhookEmbedBuilder.fromJavacord(embed).build(), new WebhookEmbed[0]);
    }
}

