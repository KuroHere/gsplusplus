/*
 * Decompiled with CFR 0.152.
 */
package shaded.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import shaded.websocket.ClientEndpointConfig;
import shaded.websocket.DeploymentException;
import shaded.websocket.Endpoint;
import shaded.websocket.Extension;
import shaded.websocket.Session;

public interface WebSocketContainer {
    public long getDefaultAsyncSendTimeout();

    public void setAsyncSendTimeout(long var1);

    public Session connectToServer(Object var1, URI var2) throws DeploymentException, IOException;

    public Session connectToServer(Class<?> var1, URI var2) throws DeploymentException, IOException;

    public Session connectToServer(Endpoint var1, ClientEndpointConfig var2, URI var3) throws DeploymentException, IOException;

    public Session connectToServer(Class<? extends Endpoint> var1, ClientEndpointConfig var2, URI var3) throws DeploymentException, IOException;

    public long getDefaultMaxSessionIdleTimeout();

    public void setDefaultMaxSessionIdleTimeout(long var1);

    public int getDefaultMaxBinaryMessageBufferSize();

    public void setDefaultMaxBinaryMessageBufferSize(int var1);

    public int getDefaultMaxTextMessageBufferSize();

    public void setDefaultMaxTextMessageBufferSize(int var1);

    public Set<Extension> getInstalledExtensions();
}

