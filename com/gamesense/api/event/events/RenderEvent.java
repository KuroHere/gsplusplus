/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;

public class RenderEvent
extends GameSenseEvent {
    private final float partialTicks;

    public RenderEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}

