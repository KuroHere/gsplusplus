/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;
import net.minecraft.util.math.BlockPos;

public class BlockUpdateEvent
extends GameSenseEvent {
    BlockPos pos;

    public BlockUpdateEvent(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}

