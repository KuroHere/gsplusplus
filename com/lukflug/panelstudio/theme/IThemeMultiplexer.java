/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.theme.IButtonRenderer;
import com.lukflug.panelstudio.theme.IButtonRendererProxy;
import com.lukflug.panelstudio.theme.IColorPickerRenderer;
import com.lukflug.panelstudio.theme.IColorPickerRendererProxy;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import com.lukflug.panelstudio.theme.IContainerRendererProxy;
import com.lukflug.panelstudio.theme.IDescriptionRenderer;
import com.lukflug.panelstudio.theme.IDescriptionRendererProxy;
import com.lukflug.panelstudio.theme.IEmptySpaceRenderer;
import com.lukflug.panelstudio.theme.IEmptySpaceRendererProxy;
import com.lukflug.panelstudio.theme.IPanelRenderer;
import com.lukflug.panelstudio.theme.IPanelRendererProxy;
import com.lukflug.panelstudio.theme.IRadioRenderer;
import com.lukflug.panelstudio.theme.IRadioRendererProxy;
import com.lukflug.panelstudio.theme.IResizeBorderRenderer;
import com.lukflug.panelstudio.theme.IResizeBorderRendererProxy;
import com.lukflug.panelstudio.theme.IScrollBarRenderer;
import com.lukflug.panelstudio.theme.IScrollBarRendererProxy;
import com.lukflug.panelstudio.theme.ISliderRenderer;
import com.lukflug.panelstudio.theme.ISliderRendererProxy;
import com.lukflug.panelstudio.theme.ISwitchRenderer;
import com.lukflug.panelstudio.theme.ISwitchRendererProxy;
import com.lukflug.panelstudio.theme.ITextFieldRenderer;
import com.lukflug.panelstudio.theme.ITextFieldRendererProxy;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;

@FunctionalInterface
public interface IThemeMultiplexer
extends ITheme {
    @Override
    default public void loadAssets(IInterface inter) {
        this.getTheme().loadAssets(inter);
    }

    @Override
    default public IDescriptionRenderer getDescriptionRenderer() {
        IDescriptionRendererProxy proxy = () -> this.getTheme().getDescriptionRenderer();
        return proxy;
    }

    @Override
    default public IContainerRenderer getContainerRenderer(int logicalLevel, int graphicalLevel, boolean horizontal) {
        IContainerRendererProxy proxy = () -> this.getTheme().getContainerRenderer(logicalLevel, graphicalLevel, horizontal);
        return proxy;
    }

    @Override
    default public <T> IPanelRenderer<T> getPanelRenderer(Class<T> type, int logicalLevel, int graphicalLevel) {
        IPanelRendererProxy proxy = () -> this.getTheme().getPanelRenderer(type, logicalLevel, graphicalLevel);
        return proxy;
    }

    @Override
    default public <T> IScrollBarRenderer<T> getScrollBarRenderer(Class<T> type, int logicalLevel, int graphicalLevel) {
        IScrollBarRendererProxy proxy = () -> this.getTheme().getScrollBarRenderer(type, logicalLevel, graphicalLevel);
        return proxy;
    }

    @Override
    default public <T> IEmptySpaceRenderer<T> getEmptySpaceRenderer(Class<T> type, int logicalLevel, int graphicalLevel, boolean container) {
        IEmptySpaceRendererProxy proxy = () -> this.getTheme().getEmptySpaceRenderer(type, logicalLevel, graphicalLevel, container);
        return proxy;
    }

    @Override
    default public <T> IButtonRenderer<T> getButtonRenderer(Class<T> type, int logicalLevel, int graphicalLevel, boolean container) {
        IButtonRendererProxy proxy = () -> this.getTheme().getButtonRenderer(type, logicalLevel, graphicalLevel, container);
        return proxy;
    }

    @Override
    default public IButtonRenderer<Void> getSmallButtonRenderer(int symbol, int logicalLevel, int graphicalLevel, boolean container) {
        IButtonRendererProxy<Void> proxy = () -> this.getTheme().getSmallButtonRenderer(symbol, logicalLevel, graphicalLevel, container);
        return proxy;
    }

    @Override
    default public IButtonRenderer<String> getKeybindRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        IButtonRendererProxy<String> proxy = () -> this.getTheme().getKeybindRenderer(logicalLevel, graphicalLevel, container);
        return proxy;
    }

    @Override
    default public ISliderRenderer getSliderRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        ISliderRendererProxy proxy = () -> this.getTheme().getSliderRenderer(logicalLevel, graphicalLevel, container);
        return proxy;
    }

    @Override
    default public IRadioRenderer getRadioRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        IRadioRendererProxy proxy = () -> this.getTheme().getRadioRenderer(logicalLevel, graphicalLevel, container);
        return proxy;
    }

    @Override
    default public IResizeBorderRenderer getResizeRenderer() {
        IResizeBorderRendererProxy proxy = () -> this.getTheme().getResizeRenderer();
        return proxy;
    }

    @Override
    default public ITextFieldRenderer getTextRenderer(boolean embed, int logicalLevel, int graphicalLevel, boolean container) {
        ITextFieldRendererProxy proxy = () -> this.getTheme().getTextRenderer(embed, logicalLevel, graphicalLevel, container);
        return proxy;
    }

    @Override
    default public ISwitchRenderer<Boolean> getToggleSwitchRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        ISwitchRendererProxy<Boolean> proxy = () -> this.getTheme().getToggleSwitchRenderer(logicalLevel, graphicalLevel, container);
        return proxy;
    }

    @Override
    default public ISwitchRenderer<String> getCycleSwitchRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        ISwitchRendererProxy<String> proxy = () -> this.getTheme().getCycleSwitchRenderer(logicalLevel, graphicalLevel, container);
        return proxy;
    }

    @Override
    default public IColorPickerRenderer getColorPickerRenderer() {
        IColorPickerRendererProxy proxy = () -> this.getTheme().getColorPickerRenderer();
        return proxy;
    }

    @Override
    default public int getBaseHeight() {
        return this.getTheme().getBaseHeight();
    }

    @Override
    default public Color getMainColor(boolean focus, boolean active) {
        return this.getTheme().getMainColor(focus, active);
    }

    @Override
    default public Color getBackgroundColor(boolean focus) {
        return this.getTheme().getBackgroundColor(focus);
    }

    @Override
    default public Color getFontColor(boolean focus) {
        return this.getTheme().getFontColor(focus);
    }

    @Override
    default public void overrideMainColor(Color color) {
        this.getTheme().overrideMainColor(color);
    }

    @Override
    default public void restoreMainColor() {
        this.getTheme().restoreMainColor();
    }

    public ITheme getTheme();
}

