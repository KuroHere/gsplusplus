/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.misc;

import java.util.Locale;
import java.util.Random;

public final class cryptUtils {
    String keyString;

    public cryptUtils(String key) {
        this.keyString = key;
    }

    Random getRandom(String text, int lenght) {
        int value = 0;
        for (char character : text.toCharArray()) {
            value += character;
        }
        return new Random(value += lenght);
    }

    String encrypt(String text) {
        text = text.toLowerCase(Locale.ROOT);
        StringBuilder st = new StringBuilder();
        int lenght = text.length();
        int i = 0;
        for (char character : text.toCharArray()) {
            st.append(this.encryptChar(this.getRandom(this.keyString, lenght + i++), character));
        }
        return st.toString();
    }

    char encryptChar(Random rd, char character) {
        int newChar = character + rd.nextInt(25);
        return (char)(newChar > 122 ? newChar - 25 : newChar);
    }

    String decrypt(String text) {
        StringBuilder st = new StringBuilder();
        int lenght = text.length();
        int i = 0;
        for (char character : text.toCharArray()) {
            st.append(this.decryptChar(this.getRandom(this.keyString, lenght + i++), character));
        }
        return st.toString();
    }

    char decryptChar(Random rd, char charecter) {
        int newChar = charecter - rd.nextInt(25);
        return (char)(newChar < 97 ? newChar + 25 : newChar);
    }
}

