/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module;

import com.gamesense.api.util.misc.ClassUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;

public class ModuleManager {
    private static final String modulePath = "com.gamesense.client.module.modules";
    private static final LinkedHashMap<Class<? extends Module>, Module> modulesClassMap = new LinkedHashMap();
    private static final LinkedHashMap<String, Module> modulesNameMap = new LinkedHashMap();

    public static void init() {
        for (Category category : Category.values()) {
            for (Class<?> clazz : ClassUtil.findClassesInPath("com.gamesense.client.module.modules." + category.toString().toLowerCase())) {
                if (clazz == null || !Module.class.isAssignableFrom(clazz)) continue;
                try {
                    Module module = (Module)clazz.newInstance();
                    ModuleManager.addMod(module);
                }
                catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void addMod(Module module) {
        modulesClassMap.put(module.getClass(), module);
        modulesNameMap.put(module.getName().toLowerCase(Locale.ROOT), module);
    }

    public static Collection<Module> getModules() {
        return modulesClassMap.values();
    }

    public static ArrayList<Module> getModulesInCategory(Category category) {
        ArrayList<Module> list = new ArrayList<Module>();
        for (Module module : modulesClassMap.values()) {
            if (!module.getCategory().equals((Object)category)) continue;
            list.add(module);
        }
        return list;
    }

    public static <T extends Module> T getModule(Class<T> clazz) {
        return (T)modulesClassMap.get(clazz);
    }

    public static Module getModule(String name) {
        if (name == null) {
            return null;
        }
        return modulesNameMap.get(name.toLowerCase(Locale.ROOT));
    }

    public static boolean isModuleEnabled(Class<? extends Module> clazz) {
        Module module = ModuleManager.getModule(clazz);
        return module != null && module.isEnabled();
    }

    public static boolean isModuleEnabled(String name) {
        Module module = ModuleManager.getModule(name);
        return module != null && module.isEnabled();
    }
}

