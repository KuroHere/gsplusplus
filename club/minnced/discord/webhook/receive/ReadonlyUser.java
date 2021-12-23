/*
 * Decompiled with CFR 0.152.
 */
package club.minnced.discord.webhook.receive;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONPropertyName;
import org.json.JSONString;

public class ReadonlyUser
implements JSONString {
    private final long id;
    private final short discriminator;
    private final boolean bot;
    private final String name;
    private final String avatar;

    public ReadonlyUser(long id, short discriminator, boolean bot, @NotNull String name, @Nullable String avatar) {
        this.id = id;
        this.discriminator = discriminator;
        this.bot = bot;
        this.name = name;
        this.avatar = avatar;
    }

    public long getId() {
        return this.id;
    }

    public String getDiscriminator() {
        return String.format("%04d", this.discriminator);
    }

    public boolean isBot() {
        return this.bot;
    }

    @JSONPropertyName(value="username")
    @NotNull
    public String getName() {
        return this.name;
    }

    @JSONPropertyName(value="avatar_id")
    @Nullable
    public String getAvatarId() {
        return this.avatar;
    }

    public String toString() {
        return this.toJSONString();
    }

    public String toJSONString() {
        return new JSONObject((Object)this).toString();
    }
}

