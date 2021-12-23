/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;
import net.minecraft.util.math.AxisAlignedBB;

public class StepEvent
extends GameSenseEvent {
    AxisAlignedBB BB;

    public StepEvent(AxisAlignedBB bb) {
        this.BB = bb;
    }

    public AxisAlignedBB getBB() {
        return this.BB;
    }
}

