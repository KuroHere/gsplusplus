/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.event;

import com.gamesense.api.event.GameSenseEvent;
import com.gamesense.api.event.Phase;

public interface MultiPhase<T extends GameSenseEvent> {
    public Phase getPhase();

    public T nextPhase();
}

