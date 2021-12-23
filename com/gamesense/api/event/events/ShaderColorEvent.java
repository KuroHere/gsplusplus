/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;
import java.awt.Color;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class ShaderColorEvent
extends GameSenseEvent {
    private final Entity entity;
    private Color color;

    public ShaderColorEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color in) {
        this.color = in;
    }
}

