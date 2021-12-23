/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class BlockChangeEvent
extends GameSenseEvent {
    private final BlockPos position;
    private final Block block;

    public BlockChangeEvent(BlockPos position, Block block) {
        this.position = position;
        this.block = block;
    }

    public Block getBlock() {
        return this.block;
    }

    public BlockPos getPosition() {
        return this.position;
    }
}

