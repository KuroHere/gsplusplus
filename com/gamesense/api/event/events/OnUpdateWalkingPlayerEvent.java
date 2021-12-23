/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;
import com.gamesense.api.event.MultiPhase;
import com.gamesense.api.event.Phase;
import com.gamesense.api.util.misc.EnumUtils;
import com.gamesense.api.util.player.PlayerPacket;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class OnUpdateWalkingPlayerEvent
extends GameSenseEvent
implements MultiPhase<OnUpdateWalkingPlayerEvent> {
    private final Phase phase;
    private boolean moving = false;
    private boolean rotating = false;
    private Vec3d position;
    private Vec2f rotation;

    public OnUpdateWalkingPlayerEvent(Vec3d position, Vec2f rotation) {
        this(position, rotation, Phase.PRE);
    }

    private OnUpdateWalkingPlayerEvent(Vec3d position, Vec2f rotation, Phase phase) {
        this.position = position;
        this.rotation = rotation;
        this.phase = phase;
    }

    @Override
    public OnUpdateWalkingPlayerEvent nextPhase() {
        return new OnUpdateWalkingPlayerEvent(this.position, this.rotation, EnumUtils.next(this.phase));
    }

    public void apply(PlayerPacket packet) {
        Vec3d position = packet.getPosition();
        Vec2f rotation = packet.getRotation();
        if (position != null) {
            this.moving = true;
            this.position = position;
        }
        if (rotation != null) {
            this.rotating = true;
            this.rotation = rotation;
        }
    }

    public boolean isMoving() {
        return this.moving;
    }

    public boolean isRotating() {
        return this.rotating;
    }

    public Vec3d getPosition() {
        return this.position;
    }

    public Vec2f getRotation() {
        return this.rotation;
    }

    @Override
    public Phase getPhase() {
        return this.phase;
    }
}

