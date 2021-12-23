/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client;

import com.gamesense.api.config.LoadConfig;
import com.gamesense.api.util.fix.Fixer;
import com.gamesense.api.util.font.CFontRenderer;
import com.gamesense.api.util.render.CapeUtil;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.command.CommandManager;
import com.gamesense.client.manager.ManagerLoader;
import com.gamesense.client.module.ModuleManager;
import java.awt.Font;
import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(modid="gs++", name="gs++", version="v2.3.4")
public class GameSense {
    public static final String MODNAME = "gs++";
    public static final String MODID = "gs++";
    public static final String MODVER = "v2.3.4";
    public static final Logger LOGGER = LogManager.getLogger((String)"gs++");
    public static final EventBus EVENT_BUS = new EventManager();
    @Mod.Instance
    public static GameSense INSTANCE;
    public CFontRenderer cFontRenderer;
    public GameSenseGUI gameSenseGUI;

    public GameSense() {
        INSTANCE = this;
    }

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) {
        try {
            Fixer.disableJndiManager();
        }
        catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Fixer.doRuntimeTest(event.getModLog());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle((String)"gs++ v2.3.4");
        LOGGER.info("Starting up gs++ v2.3.4!");
        this.startClient();
        LOGGER.info("Finished initialization for gs++ v2.3.4!");
    }

    private void startClient() {
        this.cFontRenderer = new CFontRenderer(new Font("Verdana", 0, 18), true, true);
        LOGGER.info("Custom font initialized!");
        ModuleManager.init();
        LOGGER.info("Modules initialized!");
        CommandManager.init();
        LOGGER.info("Commands initialized!");
        ManagerLoader.init();
        LOGGER.info("Managers initialized!");
        this.gameSenseGUI = new GameSenseGUI();
        LOGGER.info("GUI initialized!");
        CapeUtil.init();
        LOGGER.info("Capes initialized!");
        LoadConfig.init();
        LOGGER.info("Config initialized!");
    }
}

