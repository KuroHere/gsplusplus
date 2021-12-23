/*
 * Decompiled with CFR 0.152.
 */
package club.minnced.discord.webhook;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.external.D4JWebhookClient;
import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.external.JavacordWebhookClient;
import club.minnced.discord.webhook.send.AllowedMentions;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.OkHttpClient;
import org.javacord.api.entity.webhook.Webhook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WebhookClientBuilder {
    public static final Pattern WEBHOOK_PATTERN = Pattern.compile("(?:https?://)?(?:\\w+\\.)?discord(?:app)?\\.com/api(?:/v\\d+)?/webhooks/(\\d+)/([\\w-]+)(?:/(?:\\w+)?)?");
    protected final long id;
    protected final String token;
    protected ScheduledExecutorService pool;
    protected OkHttpClient client;
    protected ThreadFactory threadFactory;
    protected AllowedMentions allowedMentions = AllowedMentions.all();
    protected boolean isDaemon;
    protected boolean parseMessage = true;

    public WebhookClientBuilder(long id, @NotNull String token) {
        Objects.requireNonNull(token, "Token");
        this.id = id;
        this.token = token;
    }

    public WebhookClientBuilder(@NotNull String url) {
        Objects.requireNonNull(url, "Url");
        Matcher matcher = WEBHOOK_PATTERN.matcher(url);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Failed to parse webhook URL");
        }
        this.id = Long.parseUnsignedLong(matcher.group(1));
        this.token = matcher.group(2);
    }

    @NotNull
    public static WebhookClientBuilder fromJDA(@NotNull net.dv8tion.jda.api.entities.Webhook webhook) {
        Objects.requireNonNull(webhook, "Webhook");
        return new WebhookClientBuilder(webhook.getIdLong(), Objects.requireNonNull(webhook.getToken(), "Webhook Token"));
    }

    @NotNull
    public static WebhookClientBuilder fromD4J(@NotNull discord4j.core.object.entity.Webhook webhook) {
        Objects.requireNonNull(webhook, "Webhook");
        String token = webhook.getToken();
        Objects.requireNonNull(token, "Webhook Token");
        if (token.isEmpty()) {
            throw new NullPointerException("Webhook Token");
        }
        return new WebhookClientBuilder(webhook.getId().asLong(), token);
    }

    @NotNull
    public static WebhookClientBuilder fromJavacord(@NotNull Webhook webhook) {
        Objects.requireNonNull(webhook, "Webhook");
        return new WebhookClientBuilder(webhook.getId(), (String)webhook.getToken().orElseThrow(NullPointerException::new));
    }

    @NotNull
    public WebhookClientBuilder setExecutorService(@Nullable ScheduledExecutorService executorService) {
        this.pool = executorService;
        return this;
    }

    @NotNull
    public WebhookClientBuilder setHttpClient(@Nullable OkHttpClient client) {
        this.client = client;
        return this;
    }

    @NotNull
    public WebhookClientBuilder setThreadFactory(@Nullable ThreadFactory factory) {
        this.threadFactory = factory;
        return this;
    }

    @NotNull
    public WebhookClientBuilder setAllowedMentions(@Nullable AllowedMentions mentions) {
        this.allowedMentions = mentions == null ? AllowedMentions.all() : mentions;
        return this;
    }

    @NotNull
    public WebhookClientBuilder setDaemon(boolean isDaemon) {
        this.isDaemon = isDaemon;
        return this;
    }

    @NotNull
    public WebhookClientBuilder setWait(boolean waitForMessage) {
        this.parseMessage = waitForMessage;
        return this;
    }

    @NotNull
    public WebhookClient build() {
        OkHttpClient client = this.client == null ? new OkHttpClient() : this.client;
        ScheduledExecutorService pool = this.pool != null ? this.pool : WebhookClientBuilder.getDefaultPool(this.id, this.threadFactory, this.isDaemon);
        return new WebhookClient(this.id, this.token, this.parseMessage, client, pool, this.allowedMentions);
    }

    @NotNull
    public JDAWebhookClient buildJDA() {
        OkHttpClient client = this.client == null ? new OkHttpClient() : this.client;
        ScheduledExecutorService pool = this.pool != null ? this.pool : WebhookClientBuilder.getDefaultPool(this.id, this.threadFactory, this.isDaemon);
        return new JDAWebhookClient(this.id, this.token, this.parseMessage, client, pool, this.allowedMentions);
    }

    @NotNull
    public D4JWebhookClient buildD4J() {
        OkHttpClient client = this.client == null ? new OkHttpClient() : this.client;
        ScheduledExecutorService pool = this.pool != null ? this.pool : WebhookClientBuilder.getDefaultPool(this.id, this.threadFactory, this.isDaemon);
        return new D4JWebhookClient(this.id, this.token, this.parseMessage, client, pool, this.allowedMentions);
    }

    @NotNull
    public JavacordWebhookClient buildJavacord() {
        OkHttpClient client = this.client == null ? new OkHttpClient() : this.client;
        ScheduledExecutorService pool = this.pool != null ? this.pool : WebhookClientBuilder.getDefaultPool(this.id, this.threadFactory, this.isDaemon);
        return new JavacordWebhookClient(this.id, this.token, this.parseMessage, client, pool, this.allowedMentions);
    }

    protected static ScheduledExecutorService getDefaultPool(long id, ThreadFactory factory, boolean isDaemon) {
        return Executors.newSingleThreadScheduledExecutor(factory == null ? new DefaultWebhookThreadFactory(id, isDaemon) : factory);
    }

    private static final class DefaultWebhookThreadFactory
    implements ThreadFactory {
        private final long id;
        private final boolean isDaemon;

        public DefaultWebhookThreadFactory(long id, boolean isDaemon) {
            this.id = id;
            this.isDaemon = isDaemon;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "Webhook-RateLimit Thread WebhookID: " + this.id);
            thread.setDaemon(this.isDaemon);
            return thread;
        }
    }
}

