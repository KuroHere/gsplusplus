/*
 * Decompiled with CFR 0.152.
 */
package club.minnced.discord.webhook.send;

import club.minnced.discord.webhook.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;

public class MessageAttachment {
    private final String name;
    private final byte[] data;

    MessageAttachment(@NotNull String name, @NotNull byte[] data) {
        this.name = name;
        this.data = data;
    }

    MessageAttachment(@NotNull String name, @NotNull InputStream stream) throws IOException {
        this.name = name;
        try (InputStream data = stream;){
            this.data = IOUtil.readAllBytes(data);
        }
    }

    MessageAttachment(@NotNull String name, @NotNull File file) throws IOException {
        this(name, new FileInputStream(file));
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public byte[] getData() {
        return this.data;
    }
}

