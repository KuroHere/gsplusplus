/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.SettingsManager;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.StringSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.function.Supplier;
import me.zero.alpine.listener.Listenable;
import net.minecraft.client.Minecraft;

public abstract class Module
implements Listenable {
    protected static final Minecraft mc = Minecraft.func_71410_x();
    private final String name = this.getDeclaration().name();
    private final Category category = this.getDeclaration().category();
    private final int priority = this.getDeclaration().priority();
    private int bind = this.getDeclaration().bind();
    private boolean enabled = this.getDeclaration().enabled();
    private boolean drawn = this.getDeclaration().drawn();
    private boolean toggleMsg = this.getDeclaration().toggleMsg();
    private String disabledMessage = this.name + " turned OFF!";

    private Declaration getDeclaration() {
        return this.getClass().getAnnotation(Declaration.class);
    }

    protected void onEnable() {
    }

    public void onDisabledUpdate() {
    }

    protected void onDisable() {
    }

    public void onUpdate() {
    }

    public void onRender() {
    }

    public void onWorldRender(RenderEvent event) {
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setDisabledMessage(String message) {
        this.disabledMessage = message;
    }

    public void enable() {
        this.setEnabled(true);
        GameSense.EVENT_BUS.subscribe(this);
        try {
            this.onEnable();
        }
        catch (Exception e) {
            String vowel = "[aeiouAEIOU]";
            MessageBus.sendClientPrefixMessage("Disabled " + this.getName() + " due to " + e);
            for (StackTraceElement stack : e.getStackTrace()) {
                System.out.println(stack.toString());
            }
        }
        if (this.toggleMsg && Module.mc.field_71439_g != null) {
            MessageBus.sendClientPrefixMessageWithID(ModuleManager.getModule(ColorMain.class).getEnabledColor() + this.name + " turned ON!", Module.getIdFromString(this.name));
        }
    }

    public void disable() {
        this.setEnabled(false);
        GameSense.EVENT_BUS.unsubscribe(this);
        try {
            this.onDisable();
        }
        catch (Exception e) {
            String vowel = "[aeiouAEIOU]";
            MessageBus.sendClientPrefixMessage("Failed to onDisable " + this.getName() + "properly due to " + e);
            for (StackTraceElement stack : e.getStackTrace()) {
                System.out.println(stack.toString());
            }
        }
        if (this.toggleMsg && Module.mc.field_71439_g != null) {
            MessageBus.sendClientPrefixMessageWithID(ModuleManager.getModule(ColorMain.class).getDisabledColor() + this.disabledMessage, Module.getIdFromString(this.name));
        }
        this.setDisabledMessage(this.name + " turned OFF!");
    }

    public static int getIdFromString(String name) {
        StringBuilder s = new StringBuilder();
        name = name.replace("\u00a7", "e");
        String blacklist = "[^a-z]";
        for (int i = 0; i < name.length(); ++i) {
            s.append(Integer.parseInt(String.valueOf(name.charAt(i)).replaceAll(blacklist, "e"), 36));
        }
        try {
            s = new StringBuilder(s.substring(0, 8));
        }
        catch (StringIndexOutOfBoundsException ignored) {
            s = new StringBuilder(Integer.MAX_VALUE);
        }
        return Integer.MAX_VALUE - Integer.parseInt(s.toString().toLowerCase());
    }

    public void toggle() {
        if (this.isEnabled()) {
            this.disable();
        } else if (!this.isEnabled()) {
            this.enable();
        }
    }

    public String getName() {
        return this.name;
    }

    public Category getCategory() {
        return this.category;
    }

    public int getPriority() {
        return this.priority;
    }

    public int getBind() {
        return this.bind;
    }

    public void setBind(int bind) {
        if (bind >= 0 && bind <= 255) {
            this.bind = bind;
        }
    }

    public String getHudInfo() {
        return "";
    }

    public boolean isDrawn() {
        return this.drawn;
    }

    public void setDrawn(boolean drawn) {
        this.drawn = drawn;
    }

    public boolean isToggleMsg() {
        return this.toggleMsg;
    }

    public void setToggleMsg(boolean toggleMsg) {
        this.toggleMsg = toggleMsg;
    }

    protected IntegerSetting registerInteger(String name, int value, int min, int max) {
        IntegerSetting integerSetting = new IntegerSetting(name, this, value, min, max);
        SettingsManager.addSetting(integerSetting);
        return integerSetting;
    }

    protected IntegerSetting registerInteger(String name, int value, int min, int max, Supplier<Boolean> dipendent) {
        IntegerSetting integerSetting = new IntegerSetting(name, this, value, min, max);
        integerSetting.setVisible(dipendent);
        SettingsManager.addSetting(integerSetting);
        return integerSetting;
    }

    protected StringSetting registerString(String name, String value) {
        StringSetting stringSetting = new StringSetting(name, this, value);
        SettingsManager.addSetting(stringSetting);
        return stringSetting;
    }

    protected StringSetting registerString(String name, String value, Supplier<Boolean> dipendent) {
        StringSetting stringSetting = new StringSetting(name, this, value);
        stringSetting.setVisible(dipendent);
        SettingsManager.addSetting(stringSetting);
        return stringSetting;
    }

    protected DoubleSetting registerDouble(String name, double value, double min, double max) {
        DoubleSetting doubleSetting = new DoubleSetting(name, this, value, min, max);
        SettingsManager.addSetting(doubleSetting);
        return doubleSetting;
    }

    protected DoubleSetting registerDouble(String name, double value, double min, double max, Supplier<Boolean> dipendent) {
        DoubleSetting doubleSetting = new DoubleSetting(name, this, value, min, max);
        doubleSetting.setVisible(dipendent);
        SettingsManager.addSetting(doubleSetting);
        return doubleSetting;
    }

    protected BooleanSetting registerBoolean(String name, boolean value) {
        BooleanSetting booleanSetting = new BooleanSetting(name, this, value);
        SettingsManager.addSetting(booleanSetting);
        return booleanSetting;
    }

    protected BooleanSetting registerBoolean(String name, boolean value, Supplier<Boolean> dipendent) {
        BooleanSetting booleanSetting = new BooleanSetting(name, this, value);
        booleanSetting.setVisible(dipendent);
        SettingsManager.addSetting(booleanSetting);
        return booleanSetting;
    }

    protected ModeSetting registerMode(String name, List<String> modes, String value) {
        ModeSetting modeSetting = new ModeSetting(name, this, value, modes);
        SettingsManager.addSetting(modeSetting);
        return modeSetting;
    }

    protected ModeSetting registerMode(String name, List<String> modes, String value, Supplier<Boolean> dipendent) {
        ModeSetting modeSetting = new ModeSetting(name, this, value, modes);
        modeSetting.setVisible(dipendent);
        SettingsManager.addSetting(modeSetting);
        return modeSetting;
    }

    protected ColorSetting registerColor(String name, GSColor color) {
        ColorSetting colorSetting = new ColorSetting(name, this, false, color);
        SettingsManager.addSetting(colorSetting);
        return colorSetting;
    }

    protected ColorSetting registerColor(String name, GSColor color, Supplier<Boolean> dipendent) {
        ColorSetting colorSetting = new ColorSetting(name, this, false, color);
        colorSetting.setVisible(dipendent);
        colorSetting.alphaEnabled();
        SettingsManager.addSetting(colorSetting);
        return colorSetting;
    }

    protected ColorSetting registerColor(String name, GSColor color, Supplier<Boolean> dipendent, Boolean alphaEnabled) {
        ColorSetting colorSetting = new ColorSetting(name, this, false, color, alphaEnabled);
        colorSetting.setVisible(dipendent);
        colorSetting.alphaEnabled();
        SettingsManager.addSetting(colorSetting);
        return colorSetting;
    }

    protected ColorSetting registerColor(String name) {
        return this.registerColor(name, new GSColor(90, 145, 240));
    }

    protected ColorSetting registerColor(String name, Supplier<Boolean> dipendent) {
        ColorSetting color = this.registerColor(name, new GSColor(90, 145, 240));
        color.setVisible(dipendent);
        return color;
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.TYPE})
    public static @interface Declaration {
        public String name();

        public Category category();

        public int priority() default 0;

        public int bind() default 0;

        public boolean enabled() default false;

        public boolean drawn() default true;

        public boolean toggleMsg() default false;
    }
}

