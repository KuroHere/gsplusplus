/*
 * Decompiled with CFR 0.152.
 */
package shaded.websocket;

public interface MessageHandler {

    public static interface Partial<T>
    extends MessageHandler {
        public void onMessage(T var1, boolean var2);
    }

    public static interface Whole<T>
    extends MessageHandler {
        public void onMessage(T var1);
    }
}

