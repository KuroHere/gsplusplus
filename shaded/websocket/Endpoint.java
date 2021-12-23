/*
 * Decompiled with CFR 0.152.
 */
package shaded.websocket;

import shaded.websocket.CloseReason;
import shaded.websocket.EndpointConfig;
import shaded.websocket.Session;

public abstract class Endpoint {
    public abstract void onOpen(Session var1, EndpointConfig var2);

    public void onClose(Session session, CloseReason closeReason) {
    }

    public void onError(Session session, Throwable thr) {
    }
}

