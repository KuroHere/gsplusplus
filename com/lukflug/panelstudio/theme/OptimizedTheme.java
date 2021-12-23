/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.theme.IButtonRenderer;
import com.lukflug.panelstudio.theme.IColorPickerRenderer;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import com.lukflug.panelstudio.theme.IDescriptionRenderer;
import com.lukflug.panelstudio.theme.IEmptySpaceRenderer;
import com.lukflug.panelstudio.theme.IPanelRenderer;
import com.lukflug.panelstudio.theme.IRadioRenderer;
import com.lukflug.panelstudio.theme.IResizeBorderRenderer;
import com.lukflug.panelstudio.theme.IScrollBarRenderer;
import com.lukflug.panelstudio.theme.ISliderRenderer;
import com.lukflug.panelstudio.theme.ISwitchRenderer;
import com.lukflug.panelstudio.theme.ITextFieldRenderer;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class OptimizedTheme
implements ITheme {
    private final ITheme theme;
    private IDescriptionRenderer descriptionRenderer = null;
    private final Map<ParameterTuple<Void, Boolean>, IContainerRenderer> containerRenderer = new HashMap<ParameterTuple<Void, Boolean>, IContainerRenderer>();
    private final Map<ParameterTuple<Class<?>, Void>, IPanelRenderer<?>> panelRenderer = new HashMap();
    private final Map<ParameterTuple<Class<?>, Void>, IScrollBarRenderer<?>> scrollBarRenderer = new HashMap();
    private final Map<ParameterTuple<Class<?>, Boolean>, IEmptySpaceRenderer<?>> emptySpaceRenderer = new HashMap();
    private final Map<ParameterTuple<Class<?>, Boolean>, IButtonRenderer<?>> buttonRenderer = new HashMap();
    private final Map<ParameterTuple<Integer, Boolean>, IButtonRenderer<Void>> smallButtonRenderer = new HashMap<ParameterTuple<Integer, Boolean>, IButtonRenderer<Void>>();
    private final Map<ParameterTuple<Void, Boolean>, IButtonRenderer<String>> keybindRenderer = new HashMap<ParameterTuple<Void, Boolean>, IButtonRenderer<String>>();
    private final Map<ParameterTuple<Void, Boolean>, ISliderRenderer> sliderRenderer = new HashMap<ParameterTuple<Void, Boolean>, ISliderRenderer>();
    private final Map<ParameterTuple<Void, Boolean>, IRadioRenderer> radioRenderer = new HashMap<ParameterTuple<Void, Boolean>, IRadioRenderer>();
    private IResizeBorderRenderer resizeRenderer = null;
    private final Map<ParameterTuple<Boolean, Boolean>, ITextFieldRenderer> textRenderer = new HashMap<ParameterTuple<Boolean, Boolean>, ITextFieldRenderer>();
    private final Map<ParameterTuple<Void, Boolean>, ISwitchRenderer<Boolean>> toggleSwitchRenderer = new HashMap<ParameterTuple<Void, Boolean>, ISwitchRenderer<Boolean>>();
    private final Map<ParameterTuple<Void, Boolean>, ISwitchRenderer<String>> cycleSwitchRenderer = new HashMap<ParameterTuple<Void, Boolean>, ISwitchRenderer<String>>();
    private IColorPickerRenderer colorPickerRenderer = null;

    public OptimizedTheme(ITheme theme) {
        this.theme = theme;
    }

    @Override
    public void loadAssets(IInterface inter) {
        this.theme.loadAssets(inter);
    }

    @Override
    public IDescriptionRenderer getDescriptionRenderer() {
        if (this.descriptionRenderer == null) {
            this.descriptionRenderer = this.theme.getDescriptionRenderer();
        }
        return this.descriptionRenderer;
    }

    @Override
    public IContainerRenderer getContainerRenderer(int logicalLevel, int graphicalLevel, boolean horizontal) {
        return OptimizedTheme.getRenderer(this.containerRenderer, () -> this.theme.getContainerRenderer(logicalLevel, graphicalLevel, horizontal), null, logicalLevel, graphicalLevel, horizontal);
    }

    @Override
    public <T> IPanelRenderer<T> getPanelRenderer(Class<T> type, int logicalLevel, int graphicalLevel) {
        return OptimizedTheme.getRenderer(this.panelRenderer, () -> this.theme.getPanelRenderer(type, logicalLevel, graphicalLevel), type, logicalLevel, graphicalLevel, null);
    }

    @Override
    public <T> IScrollBarRenderer<T> getScrollBarRenderer(Class<T> type, int logicalLevel, int graphicalLevel) {
        return OptimizedTheme.getRenderer(this.scrollBarRenderer, () -> this.theme.getScrollBarRenderer(type, logicalLevel, graphicalLevel), type, logicalLevel, graphicalLevel, null);
    }

    @Override
    public <T> IEmptySpaceRenderer<T> getEmptySpaceRenderer(Class<T> type, int logicalLevel, int graphicalLevel, boolean container) {
        return OptimizedTheme.getRenderer(this.emptySpaceRenderer, () -> this.theme.getEmptySpaceRenderer(type, logicalLevel, graphicalLevel, container), type, logicalLevel, graphicalLevel, container);
    }

    @Override
    public <T> IButtonRenderer<T> getButtonRenderer(Class<T> type, int logicalLevel, int graphicalLevel, boolean container) {
        return OptimizedTheme.getRenderer(this.buttonRenderer, () -> this.theme.getButtonRenderer(type, logicalLevel, graphicalLevel, container), type, logicalLevel, graphicalLevel, container);
    }

    @Override
    public IButtonRenderer<Void> getSmallButtonRenderer(int symbol, int logicalLevel, int graphicalLevel, boolean container) {
        return OptimizedTheme.getRenderer(this.smallButtonRenderer, () -> this.theme.getSmallButtonRenderer(symbol, logicalLevel, graphicalLevel, container), symbol, logicalLevel, graphicalLevel, container);
    }

    @Override
    public IButtonRenderer<String> getKeybindRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        return OptimizedTheme.getRenderer(this.keybindRenderer, () -> this.theme.getKeybindRenderer(logicalLevel, graphicalLevel, container), null, logicalLevel, graphicalLevel, container);
    }

    @Override
    public ISliderRenderer getSliderRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        return OptimizedTheme.getRenderer(this.sliderRenderer, () -> this.theme.getSliderRenderer(logicalLevel, graphicalLevel, container), null, logicalLevel, graphicalLevel, container);
    }

    @Override
    public IRadioRenderer getRadioRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        return OptimizedTheme.getRenderer(this.radioRenderer, () -> this.theme.getRadioRenderer(logicalLevel, graphicalLevel, container), null, logicalLevel, graphicalLevel, container);
    }

    @Override
    public IResizeBorderRenderer getResizeRenderer() {
        if (this.resizeRenderer == null) {
            this.resizeRenderer = this.theme.getResizeRenderer();
        }
        return this.resizeRenderer;
    }

    @Override
    public ITextFieldRenderer getTextRenderer(boolean embed, int logicalLevel, int graphicalLevel, boolean container) {
        return OptimizedTheme.getRenderer(this.textRenderer, () -> this.theme.getTextRenderer(embed, logicalLevel, graphicalLevel, container), embed, logicalLevel, graphicalLevel, container);
    }

    @Override
    public ISwitchRenderer<Boolean> getToggleSwitchRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        return OptimizedTheme.getRenderer(this.toggleSwitchRenderer, () -> this.theme.getToggleSwitchRenderer(logicalLevel, graphicalLevel, container), null, logicalLevel, graphicalLevel, container);
    }

    @Override
    public ISwitchRenderer<String> getCycleSwitchRenderer(int logicalLevel, int graphicalLevel, boolean container) {
        return OptimizedTheme.getRenderer(this.cycleSwitchRenderer, () -> this.theme.getCycleSwitchRenderer(logicalLevel, graphicalLevel, container), null, logicalLevel, graphicalLevel, container);
    }

    @Override
    public IColorPickerRenderer getColorPickerRenderer() {
        if (this.colorPickerRenderer == null) {
            this.colorPickerRenderer = this.theme.getColorPickerRenderer();
        }
        return this.colorPickerRenderer;
    }

    @Override
    public int getBaseHeight() {
        return this.theme.getBaseHeight();
    }

    @Override
    public Color getMainColor(boolean focus, boolean active) {
        return this.theme.getMainColor(focus, active);
    }

    @Override
    public Color getBackgroundColor(boolean focus) {
        return this.theme.getBackgroundColor(focus);
    }

    @Override
    public Color getFontColor(boolean focus) {
        return this.theme.getFontColor(focus);
    }

    @Override
    public void overrideMainColor(Color color) {
        this.theme.overrideMainColor(color);
    }

    @Override
    public void restoreMainColor() {
        this.theme.restoreMainColor();
    }

    private static <S, T, U> U getRenderer(Map<ParameterTuple<S, T>, U> table, Supplier<U> init, S type, int logicalLevel, int graphicalLevel, T container) {
        ParameterTuple<S, T> key = new ParameterTuple<S, T>(type, logicalLevel, graphicalLevel, container);
        U value = table.getOrDefault(key, null);
        if (value == null) {
            U u = init.get();
            value = u;
            table.put(key, u);
        }
        return value;
    }

    private static class ParameterTuple<S, T> {
        private final S type;
        private final int logicalLevel;
        private final int graphicalLevel;
        private final T container;

        public ParameterTuple(S type, int logicalLevel, int graphicalLevel, T container) {
            this.type = type;
            this.logicalLevel = logicalLevel;
            this.graphicalLevel = graphicalLevel;
            this.container = container;
        }

        public int hashCode() {
            return this.toString().hashCode();
        }

        public boolean equals(Object o) {
            if (o instanceof ParameterTuple) {
                return this.toString().equals(o.toString());
            }
            return false;
        }

        public String toString() {
            return "(" + this.type + "," + this.logicalLevel + "," + this.graphicalLevel + "," + this.container + ")";
        }
    }
}

