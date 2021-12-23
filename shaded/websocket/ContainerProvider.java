/*
 * Decompiled with CFR 0.152.
 */
package shaded.websocket;

import java.util.ServiceLoader;
import shaded.websocket.WebSocketContainer;

public abstract class ContainerProvider {
    public static WebSocketContainer getWebSocketContainer() {
        WebSocketContainer wsc = null;
        for (ContainerProvider impl : ServiceLoader.load(ContainerProvider.class)) {
            wsc = impl.getContainer();
            if (wsc == null) continue;
            return wsc;
        }
        if (wsc == null) {
            throw new RuntimeException("Could not find an implementation class.");
        }
        throw new RuntimeException("Could not find an implementation class with a non-null WebSocketContainer.");
    }

    protected abstract WebSocketContainer getContainer();
}

