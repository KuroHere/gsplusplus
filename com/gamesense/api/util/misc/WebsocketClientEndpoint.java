/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.misc;

import java.io.IOException;
import java.net.URI;
import shaded.websocket.ClientEndpoint;
import shaded.websocket.CloseReason;
import shaded.websocket.ContainerProvider;
import shaded.websocket.OnClose;
import shaded.websocket.OnMessage;
import shaded.websocket.OnOpen;
import shaded.websocket.Session;
import shaded.websocket.WebSocketContainer;

@ClientEndpoint
public class WebsocketClientEndpoint {
    Session userSession = null;
    private MessageHandler messageHandler;

    public int getUserSession() {
        return this.userSession == null ? 0 : 1;
    }

    public void close() {
        try {
            if (this.userSession != null) {
                this.userSession.close();
            }
        }
        catch (IOException | NullPointerException exception) {
            // empty catch block
        }
    }

    public WebsocketClientEndpoint(URI endpointURI) {
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("opening websocket");
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("closing websocket");
        this.userSession = null;
    }

    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    public static interface MessageHandler {
        public void handleMessage(String var1);
    }
}

