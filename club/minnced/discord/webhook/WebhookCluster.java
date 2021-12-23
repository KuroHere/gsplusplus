/*
 * Decompiled with CFR 0.152.
 */
package club.minnced.discord.webhook;

import club.minnced.discord.webhook.IOUtil;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WebhookCluster
implements AutoCloseable {
    protected final List<WebhookClient> webhooks;
    protected OkHttpClient defaultHttpClient;
    protected ScheduledExecutorService defaultPool;
    protected ThreadFactory threadFactory;
    protected AllowedMentions allowedMentions = AllowedMentions.all();
    protected boolean isDaemon;

    public WebhookCluster(@NotNull Collection<? extends WebhookClient> initialClients) {
        Objects.requireNonNull(initialClients, "List");
        this.webhooks = new ArrayList<WebhookClient>(initialClients.size());
        for (WebhookClient webhookClient : initialClients) {
            this.addWebhooks(webhookClient);
        }
    }

    public WebhookCluster(int initialCapacity) {
        this.webhooks = new ArrayList<WebhookClient>(initialCapacity);
    }

    public WebhookCluster() {
        this.webhooks = new ArrayList<WebhookClient>();
    }

    @NotNull
    public WebhookCluster setDefaultHttpClient(@Nullable OkHttpClient defaultHttpClient) {
        this.defaultHttpClient = defaultHttpClient;
        return this;
    }

    @NotNull
    public WebhookCluster setDefaultExecutorService(@Nullable ScheduledExecutorService executorService) {
        this.defaultPool = executorService;
        return this;
    }

    @NotNull
    public WebhookCluster setDefaultThreadFactory(@Nullable ThreadFactory factory) {
        this.threadFactory = factory;
        return this;
    }

    @NotNull
    public WebhookCluster setAllowedMentions(@Nullable AllowedMentions allowedMentions) {
        this.allowedMentions = allowedMentions == null ? AllowedMentions.all() : allowedMentions;
        return this;
    }

    @NotNull
    public WebhookCluster setDefaultDaemon(boolean isDaemon) {
        this.isDaemon = isDaemon;
        return this;
    }

    @NotNull
    public WebhookCluster buildWebhook(long id, @NotNull String token) {
        this.webhooks.add(this.newBuilder(id, token).build());
        return this;
    }

    @NotNull
    public WebhookClientBuilder newBuilder(long id, @NotNull String token) {
        WebhookClientBuilder builder = new WebhookClientBuilder(id, token);
        builder.setExecutorService(this.defaultPool).setHttpClient(this.defaultHttpClient).setThreadFactory(this.threadFactory).setAllowedMentions(this.allowedMentions).setDaemon(this.isDaemon);
        return builder;
    }

    @NotNull
    public WebhookCluster addWebhooks(WebhookClient ... clients) {
        Objects.requireNonNull(clients, "Clients");
        for (WebhookClient client : clients) {
            Objects.requireNonNull(client, "Client");
            if (client.isShutdown) {
                throw new IllegalArgumentException("One of the provided WebhookClients has been closed already!");
            }
            this.webhooks.add(client);
        }
        return this;
    }

    @NotNull
    public WebhookCluster addWebhooks(@NotNull Collection<WebhookClient> clients) {
        Objects.requireNonNull(clients, "Clients");
        for (WebhookClient client : clients) {
            Objects.requireNonNull(client, "Client");
            if (client.isShutdown) {
                throw new IllegalArgumentException("One of the provided WebhookClients has been closed already!");
            }
            this.webhooks.add(client);
        }
        return this;
    }

    @NotNull
    public WebhookCluster removeWebhooks(WebhookClient ... clients) {
        Objects.requireNonNull(clients, "Clients");
        this.webhooks.removeAll(Arrays.asList(clients));
        return this;
    }

    @NotNull
    public WebhookCluster removeWebhooks(@NotNull Collection<WebhookClient> clients) {
        Objects.requireNonNull(clients, "Clients");
        this.webhooks.removeAll(clients);
        return this;
    }

    @NotNull
    public List<WebhookClient> removeIf(@NotNull Predicate<WebhookClient> predicate) {
        Objects.requireNonNull(predicate, "Predicate");
        ArrayList<WebhookClient> clients = new ArrayList<WebhookClient>();
        for (WebhookClient client : this.webhooks) {
            if (!predicate.test(client)) continue;
            clients.add(client);
        }
        this.removeWebhooks(clients);
        return clients;
    }

    @NotNull
    public List<WebhookClient> closeIf(@NotNull Predicate<WebhookClient> predicate) {
        Objects.requireNonNull(predicate, "Filter");
        ArrayList<WebhookClient> clients = new ArrayList<WebhookClient>();
        for (WebhookClient client : this.webhooks) {
            if (!predicate.test(client)) continue;
            clients.add(client);
        }
        this.removeWebhooks(clients);
        clients.forEach(WebhookClient::close);
        return clients;
    }

    @NotNull
    public List<WebhookClient> getWebhooks() {
        return Collections.unmodifiableList(new ArrayList<WebhookClient>(this.webhooks));
    }

    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> multicast(@NotNull Predicate<WebhookClient> filter, @NotNull WebhookMessage message) {
        Objects.requireNonNull(filter, "Filter");
        Objects.requireNonNull(message, "Message");
        RequestBody body = message.getBody();
        ArrayList<CompletableFuture<ReadonlyMessage>> callbacks = new ArrayList<CompletableFuture<ReadonlyMessage>>();
        for (WebhookClient client : this.webhooks) {
            if (!filter.test(client)) continue;
            callbacks.add(client.execute(body));
        }
        return callbacks;
    }

    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> broadcast(@NotNull WebhookMessage message) {
        Objects.requireNonNull(message, "Message");
        RequestBody body = message.getBody();
        ArrayList<CompletableFuture<ReadonlyMessage>> callbacks = new ArrayList<CompletableFuture<ReadonlyMessage>>(this.webhooks.size());
        for (WebhookClient webhook : this.webhooks) {
            callbacks.add(webhook.execute(body));
            if (!message.isFile()) continue;
            body = message.getBody();
        }
        return callbacks;
    }

    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> broadcast(@NotNull WebhookEmbed first, WebhookEmbed ... embeds) {
        ArrayList<WebhookEmbed> list = new ArrayList<WebhookEmbed>(embeds.length + 2);
        list.add(first);
        Collections.addAll(list, embeds);
        return this.broadcast(list);
    }

    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> broadcast(@NotNull Collection<WebhookEmbed> embeds) {
        return this.webhooks.stream().map(w -> w.send(embeds)).collect(Collectors.toList());
    }

    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> broadcast(@NotNull String content) {
        Objects.requireNonNull(content, "Content");
        if (content.length() > 2000) {
            throw new IllegalArgumentException("Content may not exceed 2000 characters!");
        }
        return this.webhooks.stream().map(w -> w.send(content)).collect(Collectors.toList());
    }

    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> broadcast(@NotNull File file) {
        Objects.requireNonNull(file, "File");
        return this.broadcast(file.getName(), file);
    }

    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> broadcast(@NotNull String fileName, @NotNull File file) {
        Objects.requireNonNull(file, "File");
        if (file.length() > 10L) {
            throw new IllegalArgumentException("Provided File exceeds the maximum size of 8MB!");
        }
        try {
            return this.broadcast(fileName, new FileInputStream(file));
        }
        catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> broadcast(@NotNull String fileName, @NotNull InputStream data) {
        try {
            return this.broadcast(fileName, IOUtil.readAllBytes(data));
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> broadcast(@NotNull String fileName, @NotNull byte[] data) {
        Objects.requireNonNull(data, "Data");
        if (data.length > 10) {
            throw new IllegalArgumentException("Provided data exceeds the maximum size of 8MB!");
        }
        return this.webhooks.stream().map(w -> w.send(data, fileName)).collect(Collectors.toList());
    }

    @Override
    public void close() {
        this.webhooks.forEach(WebhookClient::close);
        this.webhooks.clear();
    }
}

