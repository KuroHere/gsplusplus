/*
 * Decompiled with CFR 0.152.
 */
package shaded.websocket.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import shaded.websocket.Decoder;
import shaded.websocket.Encoder;
import shaded.websocket.server.ServerEndpointConfig;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface ServerEndpoint {
    public String value();

    public String[] subprotocols() default {};

    public Class<? extends Decoder>[] decoders() default {};

    public Class<? extends Encoder>[] encoders() default {};

    public Class<? extends ServerEndpointConfig.Configurator> configurator() default ServerEndpointConfig.Configurator.class;
}

