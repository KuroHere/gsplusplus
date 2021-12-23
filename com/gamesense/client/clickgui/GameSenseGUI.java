/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.clickgui;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.setting.SettingsManager;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.StringSetting;
import com.gamesense.api.util.font.FontUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSense;
import com.gamesense.client.clickgui.TextFieldKeys;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.base.SettingsAnimation;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.component.IFixedComponentProxy;
import com.lukflug.panelstudio.component.IResizable;
import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.container.IContainer;
import com.lukflug.panelstudio.hud.HUDGUI;
import com.lukflug.panelstudio.layout.CSGOLayout;
import com.lukflug.panelstudio.layout.ChildUtil;
import com.lukflug.panelstudio.layout.ComponentGenerator;
import com.lukflug.panelstudio.layout.IComponentAdder;
import com.lukflug.panelstudio.layout.PanelAdder;
import com.lukflug.panelstudio.layout.PanelLayout;
import com.lukflug.panelstudio.mc12.MinecraftGUI;
import com.lukflug.panelstudio.mc12.MinecraftHUDGUI;
import com.lukflug.panelstudio.popup.CenteredPositioner;
import com.lukflug.panelstudio.popup.MousePositioner;
import com.lukflug.panelstudio.popup.PanelPositioner;
import com.lukflug.panelstudio.popup.PopupTuple;
import com.lukflug.panelstudio.setting.IBooleanSetting;
import com.lukflug.panelstudio.setting.ICategory;
import com.lukflug.panelstudio.setting.IClient;
import com.lukflug.panelstudio.setting.IColorSetting;
import com.lukflug.panelstudio.setting.IEnumSetting;
import com.lukflug.panelstudio.setting.IKeybindSetting;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.IModule;
import com.lukflug.panelstudio.setting.INumberSetting;
import com.lukflug.panelstudio.setting.ISetting;
import com.lukflug.panelstudio.setting.IStringSetting;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ClearTheme;
import com.lukflug.panelstudio.theme.GameSenseTheme;
import com.lukflug.panelstudio.theme.IColorScheme;
import com.lukflug.panelstudio.theme.ITheme;
import com.lukflug.panelstudio.theme.IThemeMultiplexer;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.ColorPickerComponent;
import com.lukflug.panelstudio.widget.TextField;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GameSenseGUI
extends MinecraftHUDGUI {
    public static final int WIDTH = 100;
    public static final int HEIGHT = 12;
    public static final int FONT_HEIGHT = 9;
    public static final int DISTANCE = 10;
    public static final int HUD_BORDER = 2;
    public static IClient client;
    public static MinecraftGUI.GUIInterface guiInterface;
    public static HUDGUI gui;
    private final ITheme theme;
    private final ITheme gameSenseTheme;
    private final ITheme clearTheme;

    public GameSenseGUI() {
        final ClickGuiModule clickGuiModule = ModuleManager.getModule(ClickGuiModule.class);
        final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        guiInterface = new MinecraftGUI.GUIInterface(true){

            @Override
            public void drawString(Point pos, int height, String s, Color c) {
                GlStateManager.func_179094_E();
                GlStateManager.func_179109_b((float)pos.x, (float)pos.y, (float)0.0f);
                double scale = (double)height / (double)(FontUtil.getFontHeight((Boolean)colorMain.customFont.getValue()) + ((Boolean)colorMain.customFont.getValue() != false ? 1 : 0));
                this.end(false);
                FontUtil.drawStringWithShadow((Boolean)colorMain.customFont.getValue(), s, 0, 0, new GSColor(c));
                this.begin(false);
                GlStateManager.func_179139_a((double)scale, (double)scale, (double)1.0);
                GlStateManager.func_179121_F();
            }

            @Override
            public int getFontWidth(int height, String s) {
                double scale = (double)height / (double)(FontUtil.getFontHeight((Boolean)colorMain.customFont.getValue()) + ((Boolean)colorMain.customFont.getValue() != false ? 1 : 0));
                return (int)Math.round((double)FontUtil.getStringWidth((Boolean)colorMain.customFont.getValue(), s) * scale);
            }

            @Override
            public double getScreenWidth() {
                return super.getScreenWidth();
            }

            @Override
            public double getScreenHeight() {
                return super.getScreenHeight();
            }

            @Override
            public String getResourcePrefix() {
                return "gamesense:gui/";
            }
        };
        final Supplier<Boolean> themePredicate = () -> ((String)clickGuiModule.theme.getValue()).equals("2.0") || ((String)clickGuiModule.theme.getValue()).equals("2.1.2");
        this.gameSenseTheme = new GameSenseTheme(new GSColorScheme("gamesense", () -> (Boolean)themePredicate.get() == false), 9, 3, 5, ": " + TextFormatting.GRAY);
        this.clearTheme = new ClearTheme(new GSColorScheme("clear", themePredicate), () -> ((String)clickGuiModule.theme.getValue()).equals("2.1.2"), 9, 3, 1, ": " + TextFormatting.GRAY);
        this.theme = new IThemeMultiplexer(){

            @Override
            public ITheme getTheme() {
                if (((Boolean)themePredicate.get()).booleanValue()) {
                    return GameSenseGUI.this.clearTheme;
                }
                return GameSenseGUI.this.gameSenseTheme;
            }
        };
        client = () -> Arrays.stream(Category.values()).sorted((a, b) -> a.toString().compareTo(b.toString())).map(category -> new ICategory((Category)((Object)((Object)category))){
            final /* synthetic */ Category val$category;
            {
                this.val$category = category;
            }

            @Override
            public String getDisplayName() {
                return this.val$category.toString();
            }

            @Override
            public Stream<IModule> getModules() {
                return ModuleManager.getModulesInCategory(this.val$category).stream().sorted((a, b) -> a.getName().compareTo(b.getName())).map(module -> new IModule((Module)module){
                    final /* synthetic */ Module val$module;
                    {
                        this.val$module = module;
                    }

                    @Override
                    public String getDisplayName() {
                        return this.val$module.getName();
                    }

                    @Override
                    public IToggleable isEnabled() {
                        return new IToggleable(){

                            @Override
                            public boolean isOn() {
                                return val$module.isEnabled();
                            }

                            @Override
                            public void toggle() {
                                val$module.toggle();
                            }
                        };
                    }

                    @Override
                    public Stream<ISetting<?>> getSettings() {
                        Stream<ISetting> temp = SettingsManager.getSettingsForModule(this.val$module).stream().map(setting -> GameSenseGUI.this.createSetting(setting));
                        return Stream.concat(temp, Stream.concat(Stream.of(new IBooleanSetting(){

                            @Override
                            public String getDisplayName() {
                                return "Toggle Msgs";
                            }

                            @Override
                            public void toggle() {
                                val$module.setToggleMsg(!val$module.isToggleMsg());
                            }

                            @Override
                            public boolean isOn() {
                                return val$module.isToggleMsg();
                            }
                        }), Stream.of(new IKeybindSetting(){

                            @Override
                            public String getDisplayName() {
                                return "Keybind";
                            }

                            @Override
                            public int getKey() {
                                return val$module.getBind();
                            }

                            @Override
                            public void setKey(int key) {
                                val$module.setBind(key);
                            }

                            @Override
                            public String getKeyName() {
                                return Keyboard.getKeyName((int)val$module.getBind());
                            }
                        })));
                    }
                });
            }
        });
        final SimpleToggleable guiToggle = new SimpleToggleable(false);
        SimpleToggleable hudToggle = new SimpleToggleable(false){

            @Override
            public boolean isOn() {
                if (guiToggle.isOn() && super.isOn()) {
                    return (Boolean)clickGuiModule.showHUD.getValue();
                }
                return super.isOn();
            }
        };
        gui = new HUDGUI(guiInterface, this.theme.getDescriptionRenderer(), new MousePositioner(new Point(10, 10)), guiToggle, hudToggle);
        final BiFunction<Context, Integer, Integer> scrollHeight = (context, componentHeight) -> {
            if (((String)clickGuiModule.scrolling.getValue()).equals("Screen")) {
                return componentHeight;
            }
            return Math.min(componentHeight, Math.max(48, this.field_146295_m - context.getPos().y - 12));
        };
        Supplier<Animation> animation = () -> new SettingsAnimation(() -> (Integer)clickGuiModule.animationSpeed.getValue(), () -> guiInterface.getTime());
        PopupTuple popupType = new PopupTuple(new PanelPositioner(new Point(0, 0)), false, new IScrollSize(){

            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return (Integer)scrollHeight.apply(context, componentHeight);
            }
        });
        for (final Module module : ModuleManager.getModules()) {
            if (!(module instanceof HUDModule)) continue;
            ((HUDModule)module).populate(this.theme);
            gui.addHUDComponent(((HUDModule)module).getComponent(), new IToggleable(){

                @Override
                public boolean isOn() {
                    return module.isEnabled();
                }

                @Override
                public void toggle() {
                    module.toggle();
                }
            }, animation.get(), this.theme, 2);
        }
        PanelAdder classicPanelAdder = new PanelAdder(new IContainer<IFixedComponent>(){

            @Override
            public boolean addComponent(final IFixedComponent component) {
                return gui.addComponent(new IFixedComponentProxy<IFixedComponent>(){

                    @Override
                    public void handleScroll(Context context, int diff) {
                        IFixedComponentProxy.super.handleScroll(context, diff);
                        if (((String)clickGuiModule.scrolling.getValue()).equals("Screen")) {
                            Point p = this.getPosition(guiInterface);
                            p.translate(0, -diff);
                            this.setPosition(guiInterface, p);
                        }
                    }

                    @Override
                    public IFixedComponent getComponent() {
                        return component;
                    }
                });
            }

            @Override
            public boolean addComponent(final IFixedComponent component, IBoolean visible) {
                return gui.addComponent(new IFixedComponentProxy<IFixedComponent>(){

                    @Override
                    public void handleScroll(Context context, int diff) {
                        IFixedComponentProxy.super.handleScroll(context, diff);
                        if (((String)clickGuiModule.scrolling.getValue()).equals("Screen")) {
                            Point p = this.getPosition(guiInterface);
                            p.translate(0, -diff);
                            this.setPosition(guiInterface, p);
                        }
                    }

                    @Override
                    public IFixedComponent getComponent() {
                        return component;
                    }
                }, visible);
            }

            @Override
            public boolean removeComponent(IFixedComponent component) {
                return gui.removeComponent(component);
            }
        }, false, () -> (Boolean)clickGuiModule.csgoLayout.getValue() == false, title -> title){

            @Override
            protected IScrollSize getScrollSize(IResizable size) {
                return new IScrollSize(){

                    @Override
                    public int getScrollHeight(Context context, int componentHeight) {
                        return (Integer)scrollHeight.apply(context, componentHeight);
                    }
                };
            }
        };
        ComponentGenerator generator = new ComponentGenerator(scancode -> scancode == 211, character -> character >= 32, new TextFieldKeys()){

            @Override
            public IComponent getColorComponent(IColorSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new ColorPickerComponent(setting, theme);
            }

            @Override
            public IComponent getStringComponent(IStringSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new TextField(setting, this.keys, 0, new SimpleToggleable(false), theme.getTextRenderer(false, isContainer)){

                    @Override
                    public boolean allowCharacter(char character) {
                        return charFilter.test(character) && character != '\u007f';
                    }
                };
            }
        };
        PanelLayout classicPanelLayout = new PanelLayout(100, new Point(10, 10), 55, 22, animation, level -> ChildUtil.ChildMode.DOWN, level -> ChildUtil.ChildMode.DOWN, popupType);
        classicPanelLayout.populateGUI(classicPanelAdder, generator, client, this.theme);
        PopupTuple colorPopup = new PopupTuple(new CenteredPositioner(() -> new Rectangle(new Point(0, 0), guiInterface.getWindowSize())), true, new IScrollSize(){});
        PanelAdder horizontalCSGOAdder = new PanelAdder(gui, true, () -> (Boolean)clickGuiModule.csgoLayout.getValue(), title -> title);
        CSGOLayout horizontalCSGOLayout = new CSGOLayout(new Labeled("gs++", null, () -> true), new Point(100, 100), 480, 100, animation, "Enabled", true, true, 2, ChildUtil.ChildMode.DOWN, colorPopup){

            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return 320;
            }

            @Override
            protected boolean isUpKey(int key) {
                return key == 200;
            }

            @Override
            protected boolean isDownKey(int key) {
                return key == 208;
            }

            @Override
            protected boolean isLeftKey(int key) {
                return key == 203;
            }

            @Override
            protected boolean isRightKey(int key) {
                return key == 205;
            }
        };
        horizontalCSGOLayout.populateGUI(horizontalCSGOAdder, generator, client, this.theme);
    }

    @Override
    protected HUDGUI getGUI() {
        return gui;
    }

    private ISetting<?> createSetting(final Setting<?> setting) {
        if (setting instanceof BooleanSetting) {
            return new IBooleanSetting(){

                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public IBoolean isVisible() {
                    return () -> setting.isVisible();
                }

                @Override
                public void toggle() {
                    ((BooleanSetting)setting).setValue((Boolean)((BooleanSetting)setting).getValue() == false);
                }

                @Override
                public boolean isOn() {
                    return (Boolean)((BooleanSetting)setting).getValue();
                }

                @Override
                public Stream<ISetting<?>> getSubSettings() {
                    if (setting.getSubSettings().count() == 0L) {
                        return null;
                    }
                    return setting.getSubSettings().map(subSetting -> GameSenseGUI.this.createSetting(subSetting));
                }
            };
        }
        if (setting instanceof IntegerSetting) {
            return new INumberSetting(){

                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public IBoolean isVisible() {
                    return () -> setting.isVisible();
                }

                @Override
                public double getNumber() {
                    return ((Integer)((IntegerSetting)setting).getValue()).intValue();
                }

                @Override
                public void setNumber(double value) {
                    ((IntegerSetting)setting).setValue((int)Math.round(value));
                }

                @Override
                public double getMaximumValue() {
                    return ((IntegerSetting)setting).getMax();
                }

                @Override
                public double getMinimumValue() {
                    return ((IntegerSetting)setting).getMin();
                }

                @Override
                public int getPrecision() {
                    return 0;
                }

                @Override
                public Stream<ISetting<?>> getSubSettings() {
                    if (setting.getSubSettings().count() == 0L) {
                        return null;
                    }
                    return setting.getSubSettings().map(subSetting -> GameSenseGUI.this.createSetting(subSetting));
                }
            };
        }
        if (setting instanceof DoubleSetting) {
            return new INumberSetting(){

                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public IBoolean isVisible() {
                    return () -> setting.isVisible();
                }

                @Override
                public double getNumber() {
                    return (Double)((DoubleSetting)setting).getValue();
                }

                @Override
                public void setNumber(double value) {
                    ((DoubleSetting)setting).setValue(value);
                }

                @Override
                public double getMaximumValue() {
                    return ((DoubleSetting)setting).getMax();
                }

                @Override
                public double getMinimumValue() {
                    return ((DoubleSetting)setting).getMin();
                }

                @Override
                public int getPrecision() {
                    return 2;
                }

                @Override
                public Stream<ISetting<?>> getSubSettings() {
                    if (setting.getSubSettings().count() == 0L) {
                        return null;
                    }
                    return setting.getSubSettings().map(subSetting -> GameSenseGUI.this.createSetting(subSetting));
                }
            };
        }
        if (setting instanceof ModeSetting) {
            return new IEnumSetting(){
                private final ILabeled[] states;
                {
                    this.states = (ILabeled[])((ModeSetting)setting).getModes().stream().map(mode -> new Labeled((String)mode, null, () -> true)).toArray(ILabeled[]::new);
                }

                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public IBoolean isVisible() {
                    return () -> setting.isVisible();
                }

                @Override
                public void increment() {
                    ((ModeSetting)setting).increment();
                }

                @Override
                public void decrement() {
                    ((ModeSetting)setting).decrement();
                }

                @Override
                public String getValueName() {
                    return (String)((ModeSetting)setting).getValue();
                }

                @Override
                public int getValueIndex() {
                    return ((ModeSetting)setting).getModes().indexOf(this.getValueName());
                }

                @Override
                public void setValueIndex(int index) {
                    ((ModeSetting)setting).setValue(((ModeSetting)setting).getModes().get(index));
                }

                @Override
                public ILabeled[] getAllowedValues() {
                    return this.states;
                }

                @Override
                public Stream<ISetting<?>> getSubSettings() {
                    if (setting.getSubSettings().count() == 0L) {
                        return null;
                    }
                    return setting.getSubSettings().map(subSetting -> GameSenseGUI.this.createSetting(subSetting));
                }
            };
        }
        if (setting instanceof ColorSetting) {
            return new IColorSetting(){

                @Override
                public String getDisplayName() {
                    return TextFormatting.BOLD + setting.getName();
                }

                @Override
                public IBoolean isVisible() {
                    return () -> setting.isVisible();
                }

                @Override
                public Color getValue() {
                    return ((ColorSetting)setting).getValue();
                }

                @Override
                public void setValue(Color value) {
                    ((ColorSetting)setting).setValue(new GSColor(value));
                }

                @Override
                public Color getColor() {
                    return ((ColorSetting)setting).getColor();
                }

                @Override
                public boolean getRainbow() {
                    return ((ColorSetting)setting).getRainbow();
                }

                @Override
                public void setRainbow(boolean rainbow) {
                    ((ColorSetting)setting).setRainbow(rainbow);
                }

                @Override
                public boolean hasAlpha() {
                    return ((ColorSetting)setting).alphaEnabled();
                }

                @Override
                public boolean allowsRainbow() {
                    return ((ColorSetting)setting).rainbowEnabled();
                }

                @Override
                public boolean hasHSBModel() {
                    return ((String)ModuleManager.getModule(ColorMain.class).colorModel.getValue()).equalsIgnoreCase("HSB");
                }

                @Override
                public Stream<ISetting<?>> getSubSettings() {
                    Stream<ISetting> temp = setting.getSubSettings().map(subSetting -> GameSenseGUI.this.createSetting(subSetting));
                    return Stream.concat(temp, Stream.of(new IBooleanSetting(){

                        @Override
                        public String getDisplayName() {
                            return "Sync Color";
                        }

                        @Override
                        public IBoolean isVisible() {
                            return () -> setting != ModuleManager.getModule(ColorMain.class).enabledColor;
                        }

                        @Override
                        public void toggle() {
                            ((ColorSetting)setting).setValue(ModuleManager.getModule(ColorMain.class).enabledColor.getColor());
                            ((ColorSetting)setting).setRainbow(ModuleManager.getModule(ColorMain.class).enabledColor.getRainbow());
                        }

                        @Override
                        public boolean isOn() {
                            return ModuleManager.getModule(ColorMain.class).enabledColor.getColor().equals(((ColorSetting)setting).getColor());
                        }
                    }));
                }
            };
        }
        if (setting instanceof StringSetting) {
            return new IStringSetting(){

                @Override
                public String getValue() {
                    return ((StringSetting)setting).getText();
                }

                @Override
                public void setValue(String string) {
                    ((StringSetting)setting).setText(string);
                }

                @Override
                public String getDisplayName() {
                    return setting.getName();
                }
            };
        }
        return new ISetting<Void>(){

            @Override
            public String getDisplayName() {
                return setting.getName();
            }

            @Override
            public IBoolean isVisible() {
                return () -> setting.isVisible();
            }

            @Override
            public Void getSettingState() {
                return null;
            }

            @Override
            public Class<Void> getSettingClass() {
                return Void.class;
            }

            @Override
            public Stream<ISetting<?>> getSubSettings() {
                if (setting.getSubSettings().count() == 0L) {
                    return null;
                }
                return setting.getSubSettings().map(subSetting -> GameSenseGUI.this.createSetting(subSetting));
            }
        };
    }

    public static void renderItem(ItemStack item, Point pos) {
        GameSense.INSTANCE.gameSenseGUI.getInterface().end(false);
        GlStateManager.func_179098_w();
        GlStateManager.func_179132_a((boolean)true);
        GL11.glPushAttrib((int)524288);
        GL11.glDisable((int)3089);
        GlStateManager.func_179086_m((int)256);
        GL11.glPopAttrib();
        GlStateManager.func_179126_j();
        GlStateManager.func_179118_c();
        GlStateManager.func_179094_E();
        Minecraft.func_71410_x().func_175599_af().field_77023_b = -150.0f;
        RenderHelper.func_74520_c();
        Minecraft.func_71410_x().func_175599_af().func_180450_b(item, pos.x, pos.y);
        Minecraft.func_71410_x().func_175599_af().func_175030_a(Minecraft.func_71410_x().field_71466_p, item, pos.x, pos.y);
        RenderHelper.func_74518_a();
        Minecraft.func_71410_x().func_175599_af().field_77023_b = 0.0f;
        GlStateManager.func_179121_F();
        GlStateManager.func_179097_i();
        GlStateManager.func_179132_a((boolean)false);
        GameSense.INSTANCE.gameSenseGUI.getInterface().begin(false);
    }

    public static void renderItemTest(ItemStack item, Point pos) {
        GlStateManager.func_179098_w();
        GlStateManager.func_179132_a((boolean)true);
        GL11.glPushAttrib((int)524288);
        GL11.glDisable((int)3089);
        GlStateManager.func_179086_m((int)256);
        GL11.glPopAttrib();
        GlStateManager.func_179126_j();
        GlStateManager.func_179118_c();
        GlStateManager.func_179094_E();
        Minecraft.func_71410_x().func_175599_af().field_77023_b = -150.0f;
        RenderHelper.func_74520_c();
        Minecraft.func_71410_x().func_175599_af().func_180450_b(item, pos.x, pos.y);
        Minecraft.func_71410_x().func_175599_af().func_175030_a(Minecraft.func_71410_x().field_71466_p, item, pos.x, pos.y);
        RenderHelper.func_74518_a();
        Minecraft.func_71410_x().func_175599_af().field_77023_b = 0.0f;
        GlStateManager.func_179121_F();
        GlStateManager.func_179097_i();
        GlStateManager.func_179132_a((boolean)false);
    }

    public static void renderEntity(EntityLivingBase entity, Point pos, int scale) {
        GameSense.INSTANCE.gameSenseGUI.getInterface().end(false);
        GlStateManager.func_179098_w();
        GlStateManager.func_179132_a((boolean)true);
        GL11.glPushAttrib((int)524288);
        GL11.glDisable((int)3089);
        GlStateManager.func_179086_m((int)256);
        GL11.glPopAttrib();
        GlStateManager.func_179126_j();
        GlStateManager.func_179118_c();
        GlStateManager.func_179094_E();
        GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GuiInventory.func_147046_a((int)pos.x, (int)pos.y, (int)scale, (float)28.0f, (float)60.0f, (EntityLivingBase)entity);
        GlStateManager.func_179121_F();
        GlStateManager.func_179097_i();
        GlStateManager.func_179132_a((boolean)false);
        GameSense.INSTANCE.gameSenseGUI.getInterface().begin(false);
    }

    @Override
    protected MinecraftGUI.GUIInterface getInterface() {
        return guiInterface;
    }

    @Override
    protected int getScrollSpeed() {
        return (Integer)ModuleManager.getModule(ClickGuiModule.class).scrollSpeed.getValue();
    }

    private final class GSColorScheme
    implements IColorScheme {
        private final String configName;
        private final Supplier<Boolean> isVisible;

        public GSColorScheme(String configName, Supplier<Boolean> isVisible) {
            this.configName = configName;
            this.isVisible = isVisible;
        }

        @Override
        public void createSetting(ITheme theme, String name, String description, boolean hasAlpha, boolean allowsRainbow, Color color, boolean rainbow) {
            ClickGuiModule clickGuiModule = ModuleManager.getModule(ClickGuiModule.class);
            clickGuiModule.theme.addSubSetting(new ColorSetting(name, this.configName + "_" + name.replace(" ", ""), clickGuiModule, this.isVisible, rainbow, allowsRainbow, hasAlpha, new GSColor(color)));
        }

        @Override
        public Color getColor(String name) {
            return ((ColorSetting)ModuleManager.getModule(ClickGuiModule.class).theme.getSubSettings().filter(setting -> setting.getConfigName().equals(this.configName + "_" + name.replace(" ", ""))).findFirst().orElse(null)).getValue();
        }
    }
}

