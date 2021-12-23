/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.manager;

import com.gamesense.client.GameSense;
import com.gamesense.client.manager.Manager;
import com.gamesense.client.manager.managers.ClientEventManager;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.manager.managers.TotemPopManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.MinecraftForge;

public class ManagerLoader {
    private static final List<Manager> managers = new ArrayList<Manager>();

    public static void init() {
        ManagerLoader.register(ClientEventManager.INSTANCE);
        ManagerLoader.register(PlayerPacketManager.INSTANCE);
        ManagerLoader.register(TotemPopManager.INSTANCE);
    }

    private static void register(Manager manager) {
        managers.add(manager);
        GameSense.EVENT_BUS.subscribe(manager);
        MinecraftForge.EVENT_BUS.register((Object)manager);
    }
}

