/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;
import net.minecraft.block.Block;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class BoundingBoxEvent
extends GameSenseEvent {
    Block block;
    AxisAlignedBB bb;
    Vec3d pos;
    public boolean changed;

    public BoundingBoxEvent(Block block, Vec3d pos) {
        this.block = block;
        this.pos = pos;
    }

    public void setbb(AxisAlignedBB BoundingBox) {
        this.bb = BoundingBox;
        this.changed = true;
    }

    public Block getBlock() {
        return this.block;
    }

    public Vec3d getPos() {
        return this.pos;
    }

    public AxisAlignedBB getbb() {
        return this.bb;
    }
}

