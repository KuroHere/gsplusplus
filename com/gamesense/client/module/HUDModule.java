/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module;

import com.gamesense.client.GameSense;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Point;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public abstract class HUDModule
extends Module {
    public static final int LIST_BORDER = 1;
    protected IFixedComponent component;
    protected Point position = new Point(this.getDeclaration().posX(), this.getDeclaration().posZ());

    private Declaration getDeclaration() {
        return this.getClass().getAnnotation(Declaration.class);
    }

    public abstract void populate(ITheme var1);

    public IFixedComponent getComponent() {
        return this.component;
    }

    public void resetPosition() {
        GameSenseGUI cfr_ignored_0 = GameSense.INSTANCE.gameSenseGUI;
        this.component.setPosition(GameSenseGUI.guiInterface, this.position);
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.TYPE})
    public static @interface Declaration {
        public int posX();

        public int posZ();
    }
}

