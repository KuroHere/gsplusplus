/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.mixin;

import com.gamesense.client.GameSense;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

@IFMLLoadingPlugin.Name(value="gs++")
@IFMLLoadingPlugin.MCVersion(value="1.12.2")
public class GameSenseMixinLoader
implements IFMLLoadingPlugin {
    private static boolean isObfuscatedEnvironment = false;

    public GameSenseMixinLoader() {
        GameSense.LOGGER.info("Mixins initialized");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.gamesense.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        GameSense.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    @Nullable
    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
        isObfuscatedEnvironment = (Boolean)data.get("runtimeDeobfuscationEnabled");
    }

    public String getAccessTransformerClass() {
        return null;
    }
}

