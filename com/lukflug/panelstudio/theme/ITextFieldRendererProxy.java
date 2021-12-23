/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.theme.ITextFieldRenderer;
import java.awt.Rectangle;

@FunctionalInterface
public interface ITextFieldRendererProxy
extends ITextFieldRenderer {
    @Override
    default public int renderTextField(Context context, String title, boolean focus, String content, int position, int select, int boxPosition, boolean insertMode) {
        return this.getRenderer().renderTextField(context, title, focus, content, position, select, boxPosition, insertMode);
    }

    @Override
    default public int getDefaultHeight() {
        return this.getRenderer().getDefaultHeight();
    }

    @Override
    default public Rectangle getTextArea(Context context, String title) {
        return this.getRenderer().getTextArea(context, title);
    }

    @Override
    default public int transformToCharPos(Context context, String title, String content, int boxPosition) {
        return this.getRenderer().transformToCharPos(context, title, content, boxPosition);
    }

    public ITextFieldRenderer getRenderer();
}

