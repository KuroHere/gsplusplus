/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

@Module.Declaration(name="BlockHighlight", category=Category.Render)
public class BlockHighlight
extends Module {
    ModeSetting renderLook = this.registerMode("RenderBlock", Arrays.asList("Block", "Side"), "Block");
    ModeSetting type = this.registerMode("RenderType", Arrays.asList("Outline", "Fill", "Both"), "Both");
    IntegerSetting lineWidth = this.registerInteger("Width", 1, 1, 5);
    BooleanSetting OutLineSection = this.registerBoolean("OutLineSectionCustom", false, () -> ((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both"));
    ModeSetting NVerticesOutlineBot = this.registerMode("N^Vertices OutlineBot", Arrays.asList("1", "2", "4"), "4", () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false);
    ModeSetting direction2OutLineBot = this.registerMode("DirectionOutlineBot", Arrays.asList("X", "Z"), "X", () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && ((String)this.NVerticesOutlineBot.getValue()).equals("2"));
    ColorSetting firstVerticeOutlineBot = this.registerColor("1VertOutBot", new GSColor(255, 16, 19, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false, true);
    ColorSetting secondVerticeOutlineBot = this.registerColor("2VertOutBot", new GSColor(0, 0, 255, 255), () -> !(!((String)this.type.getValue()).equals("Outline") && !((String)this.type.getValue()).equals("Both") || (Boolean)this.OutLineSection.getValue() == false || !((String)this.NVerticesOutlineBot.getValue()).equals("2") && !((String)this.NVerticesOutlineBot.getValue()).equals("4")), true);
    ColorSetting thirdVerticeOutlineBot = this.registerColor("3VertOutBot", new GSColor(0, 255, 128, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && ((String)this.NVerticesOutlineBot.getValue()).equals("4"), true);
    ColorSetting fourVerticeOutlineBot = this.registerColor("4VertOutBot", new GSColor(255, 255, 2, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && ((String)this.NVerticesOutlineBot.getValue()).equals("4"), true);
    ModeSetting NVerticesOutlineTop = this.registerMode("N^VerticesOutlineTop", Arrays.asList("1", "2", "4"), "4", () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false);
    ModeSetting direction2OutLineTop = this.registerMode("DirectionOutlineTop", Arrays.asList("X", "Z"), "X", () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && ((String)this.NVerticesOutlineTop.getValue()).equals("2"));
    ColorSetting firstVerticeOutlineTop = this.registerColor("1VertOutTop", new GSColor(255, 16, 19, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false, true);
    ColorSetting secondVerticeOutlineTop = this.registerColor("2VertOutTop", new GSColor(0, 0, 255, 255), () -> !(!((String)this.type.getValue()).equals("Outline") && !((String)this.type.getValue()).equals("Both") || (Boolean)this.OutLineSection.getValue() == false || !((String)this.NVerticesOutlineTop.getValue()).equals("2") && !((String)this.NVerticesOutlineTop.getValue()).equals("4")), true);
    ColorSetting thirdVerticeOutlineTop = this.registerColor("3VertOutTop", new GSColor(0, 255, 128, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && ((String)this.NVerticesOutlineTop.getValue()).equals("4"), true);
    ColorSetting fourVerticeOutlineTop = this.registerColor("4VertOutTop", new GSColor(255, 255, 2, 255), () -> (((String)this.type.getValue()).equals("Outline") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.OutLineSection.getValue() != false && ((String)this.NVerticesOutlineTop.getValue()).equals("4"), true);
    BooleanSetting FillSection = this.registerBoolean("FillSectionCustom", false, () -> ((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both"));
    ModeSetting NVerticesFillBot = this.registerMode("N^VerticesFillBot", Arrays.asList("1", "2", "4"), "4", () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false);
    ModeSetting direction2FillBot = this.registerMode("DirectionFillBot", Arrays.asList("X", "Z"), "X", () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && ((String)this.NVerticesFillBot.getValue()).equals("2"));
    ColorSetting firstVerticeFillBot = this.registerColor("1VertFillBot", new GSColor(255, 16, 19, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false, true);
    ColorSetting secondVerticeFillBot = this.registerColor("2VertFillBot", new GSColor(0, 0, 255, 255), () -> !(!((String)this.type.getValue()).equals("Fill") && !((String)this.type.getValue()).equals("Both") || (Boolean)this.FillSection.getValue() == false || !((String)this.NVerticesFillBot.getValue()).equals("2") && !((String)this.NVerticesFillBot.getValue()).equals("4")), true);
    ColorSetting thirdVerticeFillBot = this.registerColor("3VertFillBot", new GSColor(0, 255, 128, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && ((String)this.NVerticesFillBot.getValue()).equals("4"), true);
    ColorSetting fourVerticeFillBot = this.registerColor("4VertFillBot", new GSColor(255, 255, 2, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && ((String)this.NVerticesFillBot.getValue()).equals("4"), true);
    ModeSetting NVerticesFillTop = this.registerMode("N^VerticesFillTop", Arrays.asList("1", "2", "4"), "4", () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false);
    ModeSetting direction2FillTop = this.registerMode("DirectionFillTop", Arrays.asList("X", "Z"), "X", () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && ((String)this.NVerticesFillTop.getValue()).equals("2"));
    ColorSetting firstVerticeFillTop = this.registerColor("1VertFillTop", new GSColor(255, 16, 19, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false, true);
    ColorSetting secondVerticeFillTop = this.registerColor("2VertFill op", new GSColor(0, 0, 255, 255), () -> !(!((String)this.type.getValue()).equals("Fill") && !((String)this.type.getValue()).equals("Both") || (Boolean)this.FillSection.getValue() == false || !((String)this.NVerticesFillTop.getValue()).equals("2") && !((String)this.NVerticesFillTop.getValue()).equals("4")), true);
    ColorSetting thirdVerticeFillTop = this.registerColor("3VertFillTop", new GSColor(0, 255, 128, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && ((String)this.NVerticesFillTop.getValue()).equals("4"), true);
    ColorSetting fourVerticeFillTop = this.registerColor("4VertFillTop", new GSColor(255, 255, 2, 255), () -> (((String)this.type.getValue()).equals("Fill") || ((String)this.type.getValue()).equals("Both")) && (Boolean)this.FillSection.getValue() != false && ((String)this.NVerticesFillTop.getValue()).equals("4"), true);

    @Override
    public void onWorldRender(RenderEvent event) {
        RayTraceResult rayTraceResult = BlockHighlight.mc.field_71476_x;
        if (rayTraceResult == null) {
            return;
        }
        EnumFacing enumFacing = BlockHighlight.mc.field_71476_x.field_178784_b;
        if (enumFacing == null) {
            return;
        }
        if (rayTraceResult.field_72313_a == RayTraceResult.Type.BLOCK) {
            int lookInt;
            BlockPos blockPos = rayTraceResult.func_178782_a();
            AxisAlignedBB axisAlignedBB = BlockHighlight.mc.field_71441_e.func_180495_p(blockPos).func_185918_c((World)BlockHighlight.mc.field_71441_e, blockPos);
            int n = lookInt = ((String)this.renderLook.getValue()).equalsIgnoreCase("Side") ? this.findRenderingSide(enumFacing) : 63;
            if (BlockHighlight.mc.field_71441_e.func_180495_p(blockPos).func_185904_a() != Material.field_151579_a) {
                switch ((String)this.type.getValue()) {
                    case "Outline": {
                        if (((String)this.NVerticesOutlineBot.getValue()).equals("1") && ((String)this.NVerticesOutlineTop.getValue()).equals("1")) {
                            this.renderOutline(axisAlignedBB, (Integer)this.lineWidth.getValue(), new GSColor(this.firstVerticeOutlineBot.getValue(), this.firstVerticeOutlineBot.getColor().getAlpha()), lookInt);
                            break;
                        }
                        this.renderCustomOutline(axisAlignedBB, lookInt);
                        break;
                    }
                    case "Fill": {
                        if (((String)this.NVerticesFillBot.getValue()).equals("1") && ((String)this.NVerticesFillTop.getValue()).equals("1")) {
                            RenderUtil.drawBox(axisAlignedBB, true, 1.0, new GSColor(this.firstVerticeFillBot.getValue(), this.firstVerticeFillBot.getColor().getAlpha()), lookInt);
                            break;
                        }
                        this.renderCustomFill(axisAlignedBB, lookInt);
                        break;
                    }
                    default: {
                        if (((String)this.NVerticesOutlineBot.getValue()).equals("1") && ((String)this.NVerticesOutlineTop.getValue()).equals("1")) {
                            this.renderOutline(axisAlignedBB, (Integer)this.lineWidth.getValue(), new GSColor(this.firstVerticeOutlineBot.getValue(), this.firstVerticeOutlineBot.getColor().getAlpha()), lookInt);
                        } else {
                            this.renderCustomOutline(axisAlignedBB, lookInt);
                        }
                        if (((String)this.NVerticesFillBot.getValue()).equals("1") && ((String)this.NVerticesFillTop.getValue()).equals("1")) {
                            RenderUtil.drawBox(axisAlignedBB, true, 1.0, new GSColor(this.firstVerticeFillBot.getValue(), this.firstVerticeFillBot.getColor().getAlpha()), lookInt);
                            break;
                        }
                        this.renderCustomFill(axisAlignedBB, lookInt);
                    }
                }
            }
        }
    }

    void renderCustomOutline(AxisAlignedBB box, int face) {
        ArrayList<GSColor> colors = new ArrayList<GSColor>();
        switch ((String)this.NVerticesOutlineBot.getValue()) {
            case "1": {
                colors.add(this.firstVerticeOutlineBot.getValue());
                colors.add(this.firstVerticeOutlineBot.getValue());
                colors.add(this.firstVerticeOutlineBot.getValue());
                colors.add(this.firstVerticeOutlineBot.getValue());
                break;
            }
            case "2": {
                if (((String)this.direction2OutLineBot.getValue()).equals("X")) {
                    colors.add(this.firstVerticeOutlineBot.getValue());
                    colors.add(this.secondVerticeOutlineBot.getValue());
                    colors.add(this.firstVerticeOutlineBot.getValue());
                    colors.add(this.secondVerticeOutlineBot.getValue());
                    break;
                }
                colors.add(this.firstVerticeOutlineBot.getValue());
                colors.add(this.firstVerticeOutlineBot.getValue());
                colors.add(this.secondVerticeOutlineBot.getValue());
                colors.add(this.secondVerticeOutlineBot.getValue());
                break;
            }
            case "4": {
                colors.add(this.firstVerticeOutlineBot.getValue());
                colors.add(this.secondVerticeOutlineBot.getValue());
                colors.add(this.thirdVerticeOutlineBot.getValue());
                colors.add(this.fourVerticeOutlineBot.getValue());
            }
        }
        switch ((String)this.NVerticesOutlineTop.getValue()) {
            case "1": {
                colors.add(this.firstVerticeOutlineTop.getValue());
                colors.add(this.firstVerticeOutlineTop.getValue());
                colors.add(this.firstVerticeOutlineTop.getValue());
                colors.add(this.firstVerticeOutlineTop.getValue());
                break;
            }
            case "2": {
                if (((String)this.direction2OutLineTop.getValue()).equals("X")) {
                    colors.add(this.firstVerticeOutlineTop.getValue());
                    colors.add(this.secondVerticeOutlineTop.getValue());
                    colors.add(this.firstVerticeOutlineTop.getValue());
                    colors.add(this.secondVerticeOutlineTop.getValue());
                    break;
                }
                colors.add(this.firstVerticeOutlineTop.getValue());
                colors.add(this.firstVerticeOutlineTop.getValue());
                colors.add(this.secondVerticeOutlineTop.getValue());
                colors.add(this.secondVerticeOutlineTop.getValue());
                break;
            }
            case "4": {
                colors.add(this.firstVerticeOutlineTop.getValue());
                colors.add(this.secondVerticeOutlineTop.getValue());
                colors.add(this.thirdVerticeOutlineTop.getValue());
                colors.add(this.fourVerticeOutlineTop.getValue());
            }
        }
        if (face == 63) {
            RenderUtil.drawBoundingBox(box, (double)((Integer)this.lineWidth.getValue()).intValue(), colors.toArray(new GSColor[7]));
        } else {
            RenderUtil.drawBoundingBox(box, ((Integer)this.lineWidth.getValue()).intValue(), colors.toArray(new GSColor[7]), true, face);
        }
    }

    void renderCustomFill(AxisAlignedBB box, int face) {
        ArrayList<GSColor> colors = new ArrayList<GSColor>();
        switch ((String)this.NVerticesFillBot.getValue()) {
            case "1": {
                colors.add(this.firstVerticeFillBot.getValue());
                colors.add(this.firstVerticeFillBot.getValue());
                colors.add(this.firstVerticeFillBot.getValue());
                colors.add(this.firstVerticeFillBot.getValue());
                break;
            }
            case "2": {
                if (((String)this.direction2FillBot.getValue()).equals("X")) {
                    colors.add(this.firstVerticeFillBot.getValue());
                    colors.add(this.secondVerticeFillBot.getValue());
                    colors.add(this.firstVerticeFillBot.getValue());
                    colors.add(this.secondVerticeFillBot.getValue());
                    break;
                }
                colors.add(this.firstVerticeFillBot.getValue());
                colors.add(this.firstVerticeFillBot.getValue());
                colors.add(this.secondVerticeFillBot.getValue());
                colors.add(this.secondVerticeFillBot.getValue());
                break;
            }
            case "4": {
                colors.add(this.firstVerticeFillBot.getValue());
                colors.add(this.secondVerticeFillBot.getValue());
                colors.add(this.thirdVerticeFillBot.getValue());
                colors.add(this.fourVerticeFillBot.getValue());
            }
        }
        switch ((String)this.NVerticesFillTop.getValue()) {
            case "1": {
                colors.add(this.firstVerticeFillTop.getValue());
                colors.add(this.firstVerticeFillTop.getValue());
                colors.add(this.firstVerticeFillTop.getValue());
                colors.add(this.firstVerticeFillTop.getValue());
                break;
            }
            case "2": {
                if (((String)this.direction2FillTop.getValue()).equals("X")) {
                    colors.add(this.firstVerticeFillTop.getValue());
                    colors.add(this.secondVerticeFillTop.getValue());
                    colors.add(this.firstVerticeFillTop.getValue());
                    colors.add(this.secondVerticeFillTop.getValue());
                    break;
                }
                colors.add(this.firstVerticeFillTop.getValue());
                colors.add(this.firstVerticeFillTop.getValue());
                colors.add(this.secondVerticeFillTop.getValue());
                colors.add(this.secondVerticeFillTop.getValue());
                break;
            }
            case "4": {
                colors.add(this.firstVerticeFillTop.getValue());
                colors.add(this.secondVerticeFillTop.getValue());
                colors.add(this.thirdVerticeFillTop.getValue());
                colors.add(this.fourVerticeFillTop.getValue());
            }
        }
        RenderUtil.drawBoxProva2(box, true, 1.0, colors.toArray(new GSColor[7]), face, true);
    }

    private void renderOutline(AxisAlignedBB axisAlignedBB, int lineWidth, GSColor color, int lookInt) {
        if (lookInt == 63) {
            RenderUtil.drawBoundingBox(axisAlignedBB, (double)lineWidth, color);
        } else {
            RenderUtil.drawBoundingBoxWithSides(axisAlignedBB, lineWidth, color, lookInt);
        }
    }

    private int findRenderingSide(EnumFacing enumFacing) {
        switch (enumFacing) {
            case EAST: {
                return 32;
            }
            case WEST: {
                return 16;
            }
            case NORTH: {
                return 4;
            }
            case SOUTH: {
                return 8;
            }
            case UP: {
                return 2;
            }
        }
        return 1;
    }
}

