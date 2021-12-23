/*
 * Decompiled with CFR 0.152.
 */
package shaded.websocket;

import shaded.websocket.Session;

public class SessionException
extends Exception {
    private final Session session;
    private static final long serialVersionUID = 12L;

    public SessionException(String message, Throwable cause, Session session) {
        super(message, cause);
        this.session = session;
    }

    public Session getSession() {
        return this.session;
    }
}

