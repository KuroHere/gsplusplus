/*
 * Decompiled with CFR 0.152.
 */
package com.lukflug.panelstudio.layout;

import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.component.FocusableComponent;
import com.lukflug.panelstudio.component.HorizontalComponent;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.container.HorizontalContainer;
import com.lukflug.panelstudio.container.IContainer;
import com.lukflug.panelstudio.container.VerticalContainer;
import com.lukflug.panelstudio.layout.ChildUtil;
import com.lukflug.panelstudio.layout.IComponentAdder;
import com.lukflug.panelstudio.layout.IComponentGenerator;
import com.lukflug.panelstudio.layout.ILayout;
import com.lukflug.panelstudio.popup.PopupTuple;
import com.lukflug.panelstudio.setting.IBooleanSetting;
import com.lukflug.panelstudio.setting.IClient;
import com.lukflug.panelstudio.setting.IEnumSetting;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.IModule;
import com.lukflug.panelstudio.setting.ISetting;
import com.lukflug.panelstudio.theme.ITheme;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.Button;
import com.lukflug.panelstudio.widget.RadioButton;
import com.lukflug.panelstudio.widget.ScrollBarComponent;
import com.lukflug.panelstudio.widget.ToggleButton;
import java.awt.Point;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CSGOLayout
implements ILayout,
IScrollSize {
    protected ILabeled label;
    protected Point position;
    protected int width;
    protected Supplier<Animation> animation;
    protected String enabledButton;
    protected boolean horizontal;
    protected boolean moduleColumn;
    protected int weight;
    protected ChildUtil.ChildMode colorType;
    protected ChildUtil util;

    public CSGOLayout(ILabeled label, Point position, int width, int popupWidth, Supplier<Animation> animation, String enabledButton, boolean horizontal, boolean moduleColumn, int weight, ChildUtil.ChildMode colorType, PopupTuple popupType) {
        this.label = label;
        this.position = position;
        this.width = width;
        this.animation = animation;
        this.enabledButton = enabledButton;
        this.horizontal = horizontal;
        this.moduleColumn = moduleColumn;
        this.weight = weight;
        this.colorType = colorType;
        this.util = new ChildUtil(popupWidth, animation, popupType);
    }

    @Override
    public void populateGUI(IComponentAdder gui, IComponentGenerator components, IClient client, ITheme theme) {
        IEnumSetting catSelect;
        Button<Void> title = new Button<Void>(this.label, () -> null, theme.getButtonRenderer(Void.class, 0, 0, true));
        HorizontalContainer window = new HorizontalContainer(this.label, theme.getContainerRenderer(0, this.horizontal ? 1 : 0, true));
        if (this.horizontal) {
            VerticalContainer container = new VerticalContainer(this.label, theme.getContainerRenderer(0, 0, false));
            catSelect = this.addContainer(this.label, client.getCategories().map(cat -> cat), container, new ThemeTuple(theme, 0, 1), true, button -> button, () -> true);
            container.addComponent(window);
            gui.addComponent(title, container, new ThemeTuple(theme, 0, 0), this.position, this.width, this.animation);
        } else {
            catSelect = this.addContainer(this.label, client.getCategories().map(cat -> cat), window, new ThemeTuple(theme, 0, 1), false, button -> this.wrapColumn((IComponent)button, new ThemeTuple(theme, 0, 1), 1), () -> true);
            gui.addComponent(title, window, new ThemeTuple(theme, 0, 0), this.position, this.width, this.animation);
        }
        client.getCategories().forEach(category -> {
            if (this.moduleColumn) {
                IEnumSetting modSelect = this.addContainer((ILabeled)category, category.getModules().map(mod -> mod), window, new ThemeTuple(theme, 1, 1), false, button -> this.wrapColumn((IComponent)button, new ThemeTuple(theme, 0, 1), 1), () -> catSelect.getValueName() == category.getDisplayName());
                category.getModules().forEach(module -> {
                    VerticalContainer container = new VerticalContainer((ILabeled)module, theme.getContainerRenderer(1, 1, false));
                    window.addComponent(this.wrapColumn(container, new ThemeTuple(theme, 1, 1), this.weight), () -> catSelect.getValueName() == category.getDisplayName() && modSelect.getValueName() == module.getDisplayName());
                    if (module.isEnabled() != null) {
                        container.addComponent(components.getComponent(new IBooleanSetting((IModule)module){
                            final /* synthetic */ IModule val$module;
                            {
                                this.val$module = iModule;
                            }

                            @Override
                            public String getDisplayName() {
                                return CSGOLayout.this.enabledButton;
                            }

                            @Override
                            public void toggle() {
                                this.val$module.isEnabled().toggle();
                            }

                            @Override
                            public boolean isOn() {
                                return this.val$module.isEnabled().isOn();
                            }
                        }, this.animation, gui, new ThemeTuple(theme, 1, 2), 2, false));
                    }
                    module.getSettings().forEach(setting -> this.addSettingsComponent((ISetting)setting, container, gui, components, new ThemeTuple(theme, 2, 2)));
                });
            } else {
                VerticalContainer categoryContent = new VerticalContainer((ILabeled)category, theme.getContainerRenderer(0, 1, false));
                window.addComponent(this.wrapColumn(categoryContent, new ThemeTuple(theme, 0, 1), 1), () -> catSelect.getValueName() == category.getDisplayName());
                category.getModules().forEach(module -> {
                    int graphicalLevel = 1;
                    FocusableComponent moduleTitle = module.isEnabled() == null ? new Button<Void>((ILabeled)module, () -> null, theme.getButtonRenderer(Void.class, 1, 1, true)) : new ToggleButton((ILabeled)module, module.isEnabled(), theme.getButtonRenderer(Boolean.class, 1, 1, true));
                    VerticalContainer moduleContainer = new VerticalContainer((ILabeled)module, theme.getContainerRenderer(1, graphicalLevel, false));
                    if (module.isEnabled() == null) {
                        this.util.addContainer((ILabeled)module, moduleTitle, moduleContainer, () -> null, Void.class, categoryContent, gui, new ThemeTuple(theme, 1, graphicalLevel), ChildUtil.ChildMode.DOWN);
                    } else {
                        this.util.addContainer((ILabeled)module, moduleTitle, moduleContainer, () -> module.isEnabled(), IBoolean.class, categoryContent, gui, new ThemeTuple(theme, 1, graphicalLevel), ChildUtil.ChildMode.DOWN);
                    }
                    module.getSettings().forEach(setting -> this.addSettingsComponent((ISetting)setting, moduleContainer, gui, components, new ThemeTuple(theme, 2, graphicalLevel + 1)));
                });
            }
        });
    }

    protected <T> void addSettingsComponent(ISetting<T> setting, VerticalContainer container, IComponentAdder gui, IComponentGenerator components, ThemeTuple theme) {
        boolean isContainer;
        int colorLevel = this.colorType == ChildUtil.ChildMode.DOWN ? theme.graphicalLevel : 0;
        IComponent component = components.getComponent(setting, this.animation, gui, theme, colorLevel, isContainer = setting.getSubSettings() != null);
        if (component instanceof VerticalContainer) {
            VerticalContainer colorContainer = (VerticalContainer)component;
            Button<Object> button = new Button<Object>(setting, () -> setting.getSettingState(), theme.getButtonRenderer(setting.getSettingClass(), this.colorType == ChildUtil.ChildMode.DOWN));
            this.util.addContainer(setting, button, colorContainer, () -> setting.getSettingState(), setting.getSettingClass(), container, gui, new ThemeTuple(theme.theme, theme.logicalLevel, colorLevel), this.colorType);
            if (setting.getSubSettings() != null) {
                setting.getSubSettings().forEach(subSetting -> this.addSettingsComponent((ISetting)subSetting, colorContainer, gui, components, new ThemeTuple(theme.theme, theme.logicalLevel + 1, colorLevel + 1)));
            }
        } else if (setting.getSubSettings() != null) {
            VerticalContainer settingContainer = new VerticalContainer(setting, theme.getContainerRenderer(false));
            this.util.addContainer(setting, component, settingContainer, () -> setting.getSettingState(), setting.getSettingClass(), container, gui, theme, ChildUtil.ChildMode.DOWN);
            setting.getSubSettings().forEach(subSetting -> this.addSettingsComponent((ISetting)subSetting, settingContainer, gui, components, new ThemeTuple(theme, 1, 1)));
        } else {
            container.addComponent(component);
        }
    }

    protected <T extends IComponent> IEnumSetting addContainer(final ILabeled label, final Stream<ILabeled> labels, IContainer<T> window, ThemeTuple theme, boolean horizontal, Function<RadioButton, T> container, IBoolean visible) {
        IEnumSetting setting = new IEnumSetting(){
            private int state = 0;
            private ILabeled[] array = (ILabeled[])labels.toArray(ILabeled[]::new);

            @Override
            public String getDisplayName() {
                return label.getDisplayName();
            }

            @Override
            public String getDescription() {
                return label.getDescription();
            }

            @Override
            public IBoolean isVisible() {
                return label.isVisible();
            }

            @Override
            public void increment() {
                this.state = (this.state + 1) % this.array.length;
            }

            @Override
            public void decrement() {
                --this.state;
                if (this.state < 0) {
                    this.state = this.array.length - 1;
                }
            }

            @Override
            public String getValueName() {
                return this.array[this.state].getDisplayName();
            }

            @Override
            public void setValueIndex(int index) {
                this.state = index;
            }

            @Override
            public int getValueIndex() {
                return this.state;
            }

            @Override
            public ILabeled[] getAllowedValues() {
                return this.array;
            }
        };
        RadioButton button = new RadioButton(setting, theme.getRadioRenderer(true), this.animation.get(), horizontal){

            @Override
            protected boolean isUpKey(int key) {
                if (this.horizontal) {
                    return CSGOLayout.this.isLeftKey(key);
                }
                return CSGOLayout.this.isUpKey(key);
            }

            @Override
            protected boolean isDownKey(int key) {
                if (this.horizontal) {
                    return CSGOLayout.this.isRightKey(key);
                }
                return CSGOLayout.this.isDownKey(key);
            }
        };
        window.addComponent((IComponent)container.apply(button), visible);
        return setting;
    }

    protected HorizontalComponent<ScrollBarComponent<Void, IComponent>> wrapColumn(IComponent button, ThemeTuple theme, int weight) {
        return new HorizontalComponent<ScrollBarComponent<Void, IComponent>>(new ScrollBarComponent<Void, IComponent>(button, theme.getScrollBarRenderer(Void.class), theme.getEmptySpaceRenderer(Void.class, false), theme.getEmptySpaceRenderer(Void.class, true)){

            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return CSGOLayout.this.getScrollHeight(context, componentHeight);
            }

            @Override
            protected Void getState() {
                return null;
            }
        }, 0, weight);
    }

    protected boolean isUpKey(int key) {
        return false;
    }

    protected boolean isDownKey(int key) {
        return false;
    }

    protected boolean isLeftKey(int key) {
        return false;
    }

    protected boolean isRightKey(int key) {
        return false;
    }
}

