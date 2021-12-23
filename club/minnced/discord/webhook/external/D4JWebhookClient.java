/*
 * Decompiled with CFR 0.152.
 */
package club.minnced.discord.webhook.external;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import discord4j.core.object.entity.Webhook;
import discord4j.core.spec.MessageCreateSpec;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import javax.annotation.CheckReturnValue;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

public class D4JWebhookClient
extends WebhookClient {
    public D4JWebhookClient(long id, String token, boolean parseMessage, OkHttpClient client, ScheduledExecutorService pool, AllowedMentions mentions) {
        super(id, token, parseMessage, client, pool, mentions);
    }

    @NotNull
    public static WebhookClient from(@NotNull Webhook webhook) {
        return WebhookClientBuilder.fromD4J(webhook).build();
    }

    @CheckReturnValue
    @NotNull
    public Mono<ReadonlyMessage> send(@NotNull Consumer<? super MessageCreateSpec> callback) {
        WebhookMessage message = WebhookMessageBuilder.fromD4J(callback).build();
        return Mono.fromFuture(() -> this.send(message));
    }

    @CheckReturnValue
    @NotNull
    public Mono<ReadonlyMessage> edit(long messageId, @NotNull Consumer<? super MessageCreateSpec> callback) {
        WebhookMessage message = WebhookMessageBuilder.fromD4J(callback).build();
        return Mono.fromFuture(() -> this.edit(messageId, message));
    }
}

