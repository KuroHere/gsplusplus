/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.theme.IButtonRenderer;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import com.lukflug.panelstudio.theme.IEmptySpaceRenderer;
import com.lukflug.panelstudio.theme.IPanelRenderer;
import com.lukflug.panelstudio.theme.IRadioRenderer;
import com.lukflug.panelstudio.theme.IScrollBarRenderer;
import com.lukflug.panelstudio.theme.ISliderRenderer;
import com.lukflug.panelstudio.theme.ISwitchRenderer;
import com.lukflug.panelstudio.theme.ITextFieldRenderer;
import com.lukflug.panelstudio.theme.ITheme;

public final class ThemeTuple {
    public final ITheme theme;
    public final int logicalLevel;
    public final int graphicalLevel;

    public ThemeTuple(ITheme theme, int logicalLevel, int graphicalLevel) {
        this.theme = theme;
        this.logicalLevel = logicalLevel;
        this.graphicalLevel = graphicalLevel;
    }

    public ThemeTuple(ThemeTuple previous, int logicalDiff, int graphicalDiff) {
        this.theme = previous.theme;
        this.logicalLevel = previous.logicalLevel + logicalDiff;
        this.graphicalLevel = previous.graphicalLevel + graphicalDiff;
    }

    public IContainerRenderer getContainerRenderer(boolean horizontal) {
        return this.theme.getContainerRenderer(this.logicalLevel, this.graphicalLevel, horizontal);
    }

    public <T> IPanelRenderer<T> getPanelRenderer(Class<T> type) {
        return this.theme.getPanelRenderer(type, this.logicalLevel, this.graphicalLevel);
    }

    public <T> IScrollBarRenderer<T> getScrollBarRenderer(Class<T> type) {
        return this.theme.getScrollBarRenderer(type, this.logicalLevel, this.graphicalLevel);
    }

    public <T> IEmptySpaceRenderer<T> getEmptySpaceRenderer(Class<T> type, boolean container) {
        return this.theme.getEmptySpaceRenderer(type, this.logicalLevel, this.graphicalLevel, container);
    }

    public <T> IButtonRenderer<T> getButtonRenderer(Class<T> type, boolean container) {
        return this.theme.getButtonRenderer(type, this.logicalLevel, this.graphicalLevel, container);
    }

    public IButtonRenderer<Void> getSmallButtonRenderer(int symbol, boolean container) {
        return this.theme.getSmallButtonRenderer(symbol, this.logicalLevel, this.graphicalLevel, container);
    }

    public IButtonRenderer<String> getKeybindRenderer(boolean container) {
        return this.theme.getKeybindRenderer(this.logicalLevel, this.graphicalLevel, container);
    }

    public ISliderRenderer getSliderRenderer(boolean container) {
        return this.theme.getSliderRenderer(this.logicalLevel, this.graphicalLevel, container);
    }

    public IRadioRenderer getRadioRenderer(boolean container) {
        return this.theme.getRadioRenderer(this.logicalLevel, this.graphicalLevel, container);
    }

    public ITextFieldRenderer getTextRenderer(boolean embed, boolean container) {
        return this.theme.getTextRenderer(embed, this.logicalLevel, this.graphicalLevel, container);
    }

    public ISwitchRenderer<Boolean> getToggleSwitchRenderer(boolean container) {
        return this.theme.getToggleSwitchRenderer(this.logicalLevel, this.graphicalLevel, container);
    }

    public ISwitchRenderer<String> getCycleSwitchRenderer(boolean container) {
        return this.theme.getCycleSwitchRenderer(this.logicalLevel, this.graphicalLevel, container);
    }
}

