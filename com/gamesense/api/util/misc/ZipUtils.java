/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ZipUtils {
    public static void zip(File source, File dest) {
        ArrayList<String> list = new ArrayList<String>();
        ZipUtils.createFileList(source, source, list);
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dest));
            for (String file : list) {
                int len;
                ZipEntry ze = new ZipEntry(file);
                FileInputStream in = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                zos.putNextEntry(ze);
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                in.close();
                zos.closeEntry();
            }
            zos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createFileList(File file, File source, List<String> list) {
        if (file.isFile()) {
            list.add(file.getPath());
        } else if (file.isDirectory()) {
            for (String subfile : file.list()) {
                ZipUtils.createFileList(new File(file, subfile), source, list);
            }
        }
    }
}

