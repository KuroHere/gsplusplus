/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import io.netty.util.internal.ConcurrentSet;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(name="VoidESP", category=Category.Render)
public class VoidESP
extends Module {
    IntegerSetting renderDistance = this.registerInteger("Distance", 10, 1, 256);
    IntegerSetting activeYValue = this.registerInteger("Activate Y", 20, 0, 256);
    ModeSetting renderType = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
    ModeSetting renderMode = this.registerMode("Mode", Arrays.asList("Box", "Flat"), "Flat");
    IntegerSetting width = this.registerInteger("Width", 1, 1, 10);
    ColorSetting color = this.registerColor("Color", new GSColor(255, 255, 0));
    IntegerSetting aboveCheck = this.registerInteger("Above Check", 0, 0, 10);
    private final ConcurrentSet<BlockPos> voidHoles = new ConcurrentSet();

    @Override
    public void onUpdate() {
        if (VoidESP.mc.field_71439_g.field_71093_bK == 1) {
            return;
        }
        if (VoidESP.mc.field_71439_g.func_180425_c().func_177956_o() > (Integer)this.activeYValue.getValue()) {
            return;
        }
        List<BlockPos> blockPosList = BlockUtil.getCircle(VoidESP.mc.field_71439_g.func_180425_c(), 0, ((Integer)this.renderDistance.getValue()).intValue(), false);
        this.voidHoles.clear();
        for (BlockPos blockPos : blockPosList) {
            if (VoidESP.mc.field_71441_e.func_180495_p(blockPos).func_177230_c().equals(Blocks.field_150357_h)) continue;
            this.voidHoles.add((Object)blockPos);
            for (int i = 0; i < (Integer)this.aboveCheck.getValue() && !(VoidESP.mc.field_71441_e.func_180495_p(blockPos.func_177982_a(0, i + 1, 0)).func_177230_c() instanceof BlockAir); ++i) {
                this.voidHoles.add((Object)blockPos.func_177982_a(0, i + 1, 0));
            }
        }
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (VoidESP.mc.field_71439_g.func_180425_c().func_177956_o() > (Integer)this.activeYValue.getValue()) {
            return;
        }
        if (this.voidHoles.isEmpty()) {
            return;
        }
        this.voidHoles.forEach(this::renderESP);
    }

    private void renderESP(BlockPos blockPos) {
        GSColor fillColor = new GSColor(this.color.getValue(), 50);
        GSColor outlineColor = new GSColor(this.color.getValue(), 255);
        if (blockPos.func_185332_f((int)VoidESP.mc.field_71439_g.field_70165_t, (int)VoidESP.mc.field_71439_g.field_70163_u, (int)VoidESP.mc.field_71439_g.field_70161_v) > (double)((Integer)this.renderDistance.getValue()).intValue()) {
            return;
        }
        int sides = ((String)this.renderMode.getValue()).equalsIgnoreCase("Flat") ? 1 : 63;
        switch ((String)this.renderType.getValue()) {
            case "Outline": {
                this.renderOutline(blockPos, (Integer)this.width.getValue(), outlineColor, sides);
                break;
            }
            case "Fill": {
                RenderUtil.drawBox(blockPos, 1.0, fillColor, sides);
                break;
            }
            default: {
                RenderUtil.drawBox(blockPos, 1.0, fillColor, sides);
                this.renderOutline(blockPos, (Integer)this.width.getValue(), outlineColor, sides);
            }
        }
    }

    private void renderOutline(BlockPos blockPos, int lineWidth, GSColor color, int sides) {
        if (sides == 63) {
            RenderUtil.drawBoundingBox(blockPos, 1.0, lineWidth, color);
        } else {
            RenderUtil.drawBoundingBoxWithSides(blockPos, lineWidth, color, sides);
        }
    }
}

