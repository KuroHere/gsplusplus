/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.misc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClassUtil {
    private static final boolean debug = false;

    public static ArrayList<Class<?>> findClassesInPath(String classPath) {
        ArrayList foundClasses = new ArrayList();
        String resource = ClassUtil.class.getClassLoader().getResource(classPath.replace(".", "/")).getPath();
        if (resource.contains("!")) {
            try {
                ZipEntry entry;
                ZipInputStream file = new ZipInputStream(new URL(resource.substring(0, resource.lastIndexOf(33))).openStream());
                while ((entry = file.getNextEntry()) != null) {
                    String name = entry.getName();
                    if (!name.startsWith(classPath.replace(".", "/") + "/") || !name.endsWith(".class")) continue;
                    try {
                        Class<?> clazz = Class.forName(name.substring(0, name.length() - 6).replace("/", "."));
                        foundClasses.add(clazz);
                    }
                    catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                String[] classNamesFound;
                File file;
                URL classPathURL = ClassUtil.class.getClassLoader().getResource(classPath.replace(".", "/"));
                if (classPathURL != null && (file = new File(classPathURL.getFile())).exists() && (classNamesFound = file.list()) != null) {
                    for (String className : classNamesFound) {
                        if (!className.endsWith(".class")) continue;
                        foundClasses.add(Class.forName(classPath + "." + className.substring(0, className.length() - 6)));
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        foundClasses.sort(Comparator.comparing(Class::getName));
        return foundClasses;
    }
}

