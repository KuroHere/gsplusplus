/*
 * Decompiled with CFR 0.152.
 */
package shaded.websocket.server;

import shaded.websocket.DeploymentException;
import shaded.websocket.WebSocketContainer;
import shaded.websocket.server.ServerEndpointConfig;

public interface ServerContainer
extends WebSocketContainer {
    public void addEndpoint(Class<?> var1) throws DeploymentException;

    public void addEndpoint(ServerEndpointConfig var1) throws DeploymentException;
}

