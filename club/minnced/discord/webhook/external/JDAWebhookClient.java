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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Webhook;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

public class JDAWebhookClient
extends WebhookClient {
    public JDAWebhookClient(long id, String token, boolean parseMessage, OkHttpClient client, ScheduledExecutorService pool, AllowedMentions mentions) {
        super(id, token, parseMessage, client, pool, mentions);
    }

    @NotNull
    public static WebhookClient fromJDA(@NotNull Webhook webhook) {
        return WebhookClientBuilder.fromJDA(webhook).build();
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull Message message) {
        return this.send(WebhookMessageBuilder.fromJDA(message).build());
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull MessageEmbed embed) {
        return this.send(WebhookEmbedBuilder.fromJDA(embed).build(), new WebhookEmbed[0]);
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> edit(long messageId, @NotNull Message message) {
        return this.edit(messageId, WebhookMessageBuilder.fromJDA(message).build());
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> edit(long messageId, @NotNull MessageEmbed embed) {
        return this.edit(messageId, WebhookEmbedBuilder.fromJDA(embed).build(), new WebhookEmbed[0]);
    }
}

