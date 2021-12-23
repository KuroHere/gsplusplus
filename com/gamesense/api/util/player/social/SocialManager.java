/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.player.social;

import com.gamesense.api.util.player.social.Enemy;
import com.gamesense.api.util.player.social.Friend;
import com.gamesense.api.util.player.social.SpecialNames;
import com.gamesense.client.module.modules.combat.Friends;
import java.util.ArrayList;

public class SocialManager {
    private static final ArrayList<Friend> friends = new ArrayList();
    private static final ArrayList<Enemy> enemies = new ArrayList();
    private static final ArrayList<SpecialNames> SpecialNames = new ArrayList();

    public static ArrayList<Friend> getFriends() {
        return friends;
    }

    public static ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public static ArrayList<SpecialNames> getSpecialNames() {
        return SpecialNames;
    }

    public static ArrayList<String> getFriendsByName() {
        ArrayList<String> friendNames = new ArrayList<String>();
        SocialManager.getFriends().forEach(friend -> friendNames.add(friend.getName()));
        return friendNames;
    }

    public static ArrayList<String> getEnemiesByName() {
        ArrayList<String> enemyNames = new ArrayList<String>();
        SocialManager.getEnemies().forEach(enemy -> enemyNames.add(enemy.getName()));
        return enemyNames;
    }

    public static boolean isFriend(String name) {
        for (Friend friend : SocialManager.getFriends()) {
            if (!friend.getName().equalsIgnoreCase(name) || !Friends.INSTANCE.isEnabled()) continue;
            return true;
        }
        return false;
    }

    public static boolean isFriendForce(String name) {
        for (Friend friend : SocialManager.getFriends()) {
            if (!friend.getName().equalsIgnoreCase(name)) continue;
            return true;
        }
        return false;
    }

    public static boolean isEnemy(String name) {
        for (Enemy enemy : SocialManager.getEnemies()) {
            if (!enemy.getName().equalsIgnoreCase(name)) continue;
            return true;
        }
        return false;
    }

    public static boolean isSpecial(String name) {
        for (SpecialNames enemy : SocialManager.getSpecialNames()) {
            if (!enemy.getName().equalsIgnoreCase(name)) continue;
            return true;
        }
        return false;
    }

    public static Friend getFriend(String name) {
        for (Friend friend : SocialManager.getFriends()) {
            if (!friend.getName().equalsIgnoreCase(name)) continue;
            return friend;
        }
        return null;
    }

    public static Enemy getEnemy(String name) {
        for (Enemy enemy : SocialManager.getEnemies()) {
            if (!enemy.getName().equalsIgnoreCase(name)) continue;
            return enemy;
        }
        return null;
    }

    public static SpecialNames getSpecialNames(String name) {
        for (SpecialNames specialNames : SocialManager.getSpecialNames()) {
            if (!specialNames.getName().equalsIgnoreCase(name)) continue;
            return specialNames;
        }
        return null;
    }

    public static ArrayList<String> getSpecialNamesString() {
        ArrayList<String> out = new ArrayList<String>();
        try {
            SocialManager.getSpecialNames().forEach(name -> out.add(name.getName()));
        }
        catch (OutOfMemoryError outOfMemoryError) {
            // empty catch block
        }
        return out;
    }

    public static void addFriend(String name) {
        SocialManager.getFriends().add(new Friend(name));
    }

    public static void delFriend(String name) {
        SocialManager.getFriends().remove(SocialManager.getFriend(name));
    }

    public static void addEnemy(String name) {
        SocialManager.getEnemies().add(new Enemy(name));
    }

    public static void delEnemy(String name) {
        SocialManager.getEnemies().remove(SocialManager.getEnemy(name));
    }

    public static void delSpecial(String name) {
        SocialManager.getSpecialNames().remove(SocialManager.getSpecialNames(name));
    }

    public static void addSpecialName(String name) {
        SocialManager.getSpecialNames().add(new SpecialNames(name));
    }

    public static void removeSpecialName(String name) {
        SocialManager.getSpecialNames().remove(SocialManager.getSpecialNames(name));
    }
}

