/*
 * Decompiled with CFR 0.152.
 */
package shaded.websocket;

import java.util.List;
import java.util.Map;
import shaded.websocket.Decoder;
import shaded.websocket.Encoder;

public interface EndpointConfig {
    public List<Class<? extends Encoder>> getEncoders();

    public List<Class<? extends Decoder>> getDecoders();

    public Map<String, Object> getUserProperties();
}

