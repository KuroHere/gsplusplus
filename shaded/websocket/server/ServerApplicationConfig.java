/*
 * Decompiled with CFR 0.152.
 */
package shaded.websocket.server;

import java.util.Set;
import shaded.websocket.Endpoint;
import shaded.websocket.server.ServerEndpointConfig;

public interface ServerApplicationConfig {
    public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> var1);

    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> var1);
}

