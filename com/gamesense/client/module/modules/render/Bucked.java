/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import java.util.Arrays;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(name="Bucked", category=Category.Render)
public class Bucked
extends Module {
    IntegerSetting range = this.registerInteger("Range", 100, 10, 260);
    BooleanSetting self = this.registerBoolean("Self", false);
    BooleanSetting friend = this.registerBoolean("Friend", true);
    BooleanSetting enemiesOnly = this.registerBoolean("Only Enemies", false);
    ModeSetting heightMode = this.registerMode("Height", Arrays.asList("Single", "Double"), "Single");
    ModeSetting renderMode = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
    IntegerSetting width = this.registerInteger("Line Width", 2, 1, 5);
    ColorSetting color = this.registerColor("Color", new GSColor(0, 255, 0, 255));

    @Override
    public void onWorldRender(RenderEvent event) {
        Bucked.mc.field_71441_e.field_73010_i.stream().filter(this::isValidTarget).forEach(entityPlayer -> {
            BlockPos blockPos = new BlockPos(this.roundValueToCenter(entityPlayer.field_70165_t), this.roundValueToCenter(entityPlayer.field_70163_u), this.roundValueToCenter(entityPlayer.field_70161_v));
            if (!this.isSurrounded(blockPos)) {
                this.renderESP(blockPos, this.findGSColor((EntityPlayer)entityPlayer));
            }
        });
    }

    private boolean isValidTarget(EntityPlayer entityPlayer) {
        if (entityPlayer == null || entityPlayer.field_70128_L || entityPlayer.func_110143_aJ() <= 0.0f) {
            return false;
        }
        if (((Boolean)this.enemiesOnly.getValue()).booleanValue() && !SocialManager.isEnemy(entityPlayer.func_70005_c_())) {
            return false;
        }
        if (entityPlayer.func_70032_d((Entity)Bucked.mc.field_71439_g) > (float)((Integer)this.range.getValue()).intValue()) {
            return false;
        }
        if (!((Boolean)this.self.getValue()).booleanValue() && entityPlayer == Bucked.mc.field_71439_g) {
            return false;
        }
        return (Boolean)this.friend.getValue() != false || !SocialManager.isFriend(entityPlayer.func_70005_c_());
    }

    private boolean isSurrounded(BlockPos blockPos) {
        return HoleUtil.isHole(blockPos, true, false).getType() != HoleUtil.HoleType.NONE;
    }

    private void renderESP(BlockPos blockPos, GSColor color) {
        int upValue = ((String)this.heightMode.getValue()).equalsIgnoreCase("Double") ? 2 : 1;
        GSColor gsColor1 = new GSColor(color, 255);
        GSColor gsColor2 = new GSColor(color, 50);
        switch ((String)this.renderMode.getValue()) {
            case "Both": {
                RenderUtil.drawBox(blockPos, upValue, gsColor2, 63);
                RenderUtil.drawBoundingBox(blockPos, (double)upValue, ((Integer)this.width.getValue()).intValue(), gsColor1);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(blockPos, (double)upValue, ((Integer)this.width.getValue()).intValue(), gsColor1);
                break;
            }
            default: {
                RenderUtil.drawBox(blockPos, upValue, gsColor2, 63);
            }
        }
    }

    private GSColor findGSColor(EntityPlayer entityPlayer) {
        if (SocialManager.isFriend(entityPlayer.func_70005_c_())) {
            return ModuleManager.getModule(ColorMain.class).getFriendGSColor();
        }
        if (SocialManager.isEnemy(entityPlayer.func_70005_c_())) {
            return ModuleManager.getModule(ColorMain.class).getEnemyGSColor();
        }
        return this.color.getValue();
    }

    private double roundValueToCenter(double inputVal) {
        double roundVal = Math.round(inputVal);
        if (roundVal > inputVal) {
            roundVal -= 0.5;
        } else if (roundVal <= inputVal) {
            roundVal += 0.5;
        }
        return roundVal;
    }
}

