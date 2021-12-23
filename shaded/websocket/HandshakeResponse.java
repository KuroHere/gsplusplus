/*
 * Decompiled with CFR 0.152.
 */
package shaded.websocket;

import java.util.List;
import java.util.Map;

public interface HandshakeResponse {
    public static final String SEC_WEBSOCKET_ACCEPT = "Sec-WebSocket-Accept";

    public Map<String, List<String>> getHeaders();
}

