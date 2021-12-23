/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.render;

import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.util.font.FontUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.client.module.modules.render.Nametags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

public class RenderUtil {
    private static final Minecraft mc = Minecraft.func_71410_x();

    public static void drawLine(double posx, double posy, double posz, double posx2, double posy2, double posz2, GSColor color) {
        RenderUtil.drawLine(posx, posy, posz, posx2, posy2, posz2, color, 1.0f);
    }

    public static void drawLine(double posx, double posy, double posz, double posx2, double posy2, double posz2, GSColor color, float width) {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        GlStateManager.func_187441_d((float)width);
        color.glColor();
        bufferbuilder.func_181668_a(1, DefaultVertexFormats.field_181705_e);
        RenderUtil.vertex(posx, posy, posz, bufferbuilder);
        RenderUtil.vertex(posx2, posy2, posz2, bufferbuilder);
        tessellator.func_78381_a();
    }

    public static void draw2DRect(int posX, int posY, int width, int height, int zHeight, GSColor color) {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        GlStateManager.func_179147_l();
        GlStateManager.func_179090_x();
        GlStateManager.func_187428_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        color.glColor();
        bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        bufferbuilder.func_181662_b((double)posX, (double)(posY + height), (double)zHeight).func_181675_d();
        bufferbuilder.func_181662_b((double)(posX + width), (double)(posY + height), (double)zHeight).func_181675_d();
        bufferbuilder.func_181662_b((double)(posX + width), (double)posY, (double)zHeight).func_181675_d();
        bufferbuilder.func_181662_b((double)posX, (double)posY, (double)zHeight).func_181675_d();
        tessellator.func_78381_a();
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
    }

    private static void drawBorderedRect(double x, double y, double x1, double y1, float lineWidth, GSColor inside, GSColor border) {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        inside.glColor();
        bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        bufferbuilder.func_181662_b(x, y1, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(x1, y1, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(x1, y, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(x, y, 0.0).func_181675_d();
        tessellator.func_78381_a();
        border.glColor();
        GlStateManager.func_187441_d((float)lineWidth);
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181705_e);
        bufferbuilder.func_181662_b(x, y, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(x, y1, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(x1, y1, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(x1, y, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(x, y, 0.0).func_181675_d();
        tessellator.func_78381_a();
    }

    public static void drawBox(BlockPos blockPos, double height, GSColor color, int sides) {
        RenderUtil.drawBox(blockPos.func_177958_n(), blockPos.func_177956_o(), blockPos.func_177952_p(), 1.0, height, 1.0, color, color.getAlpha(), sides);
    }

    public static void drawBox(AxisAlignedBB bb, boolean check, double height, GSColor color, int sides) {
        RenderUtil.drawBox(bb, check, height, color, color.getAlpha(), sides);
    }

    public static void drawBox(AxisAlignedBB bb, boolean check, double height, GSColor color, int alpha, int sides) {
        if (check) {
            RenderUtil.drawBox(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, bb.field_72336_d - bb.field_72340_a, bb.field_72337_e - bb.field_72338_b, bb.field_72334_f - bb.field_72339_c, color, alpha, sides);
        } else {
            RenderUtil.drawBox(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, bb.field_72336_d - bb.field_72340_a, height, bb.field_72334_f - bb.field_72339_c, color, alpha, sides);
        }
    }

    public static void drawBoxProva2(AxisAlignedBB bb, boolean check, double height, GSColor[] color, int sides, boolean five) {
        RenderUtil.drawBoxProva(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, bb.field_72336_d - bb.field_72340_a, bb.field_72337_e - bb.field_72338_b, bb.field_72334_f - bb.field_72339_c, color, sides, five);
    }

    public static void drawBox(double x, double y, double z, double w, double h, double d, GSColor color, int alpha, int sides) {
        GlStateManager.func_179118_c();
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        color.glColor();
        bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        RenderUtil.doVerticies(new AxisAlignedBB(x, y, z, x + w, y + h, z + d), color, alpha, bufferbuilder, sides, false);
        tessellator.func_78381_a();
        GlStateManager.func_179141_d();
    }

    public static void drawBoxProva(double x, double y, double z, double w, double h, double d, GSColor[] color, int sides, boolean five) {
        GlStateManager.func_179118_c();
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        RenderUtil.doVerticiesProva(new AxisAlignedBB(x, y, z, x + w, y + h, z + d), color, bufferbuilder, sides, false);
        tessellator.func_78381_a();
        GlStateManager.func_179141_d();
    }

    private static void doVerticiesProva(AxisAlignedBB axisAlignedBB, GSColor[] color, BufferBuilder bufferbuilder, int sides, boolean five) {
        if ((sides & 0x20) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[2], color[2].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[7], color[7].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[6], color[6].getAlpha(), bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[2], color[2].getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 0x10) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[0], color[0].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[1], color[1].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[5], color[5].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[4], color[4].getAlpha(), bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[0], color[0].getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 4) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[0], color[0].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[4], color[4].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[7], color[7].getAlpha(), bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 8) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[1], color[1].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[2], color[2].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[6], color[6].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[5], color[5].getAlpha(), bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[1], color[1].getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 2) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[7], color[7].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[6], color[6].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[5], color[5].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[4], color[4].getAlpha(), bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[7], color[7].getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 1) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[2], color[2].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[1], color[1].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[0], color[0].getAlpha(), bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
            }
        }
    }

    public static void drawBoundingBox(BlockPos bp, double height, float width, GSColor color) {
        RenderUtil.drawBoundingBox(RenderUtil.getBoundingBox(bp, 1.0, height, 1.0), (double)width, color, color.getAlpha());
    }

    public static void drawBoundingBox(AxisAlignedBB bb, double width, GSColor color) {
        RenderUtil.drawBoundingBox(bb, width, color, color.getAlpha());
    }

    public static void drawBoundingBox(AxisAlignedBB bb, double width, GSColor color, int alpha) {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        GlStateManager.func_187441_d((float)((float)width));
        color.glColor();
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        RenderUtil.colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
        RenderUtil.colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
        RenderUtil.colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
        RenderUtil.colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
        RenderUtil.colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
        RenderUtil.colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
        RenderUtil.colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
        RenderUtil.colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
        tessellator.func_78381_a();
    }

    public static void drawBoundingBox(AxisAlignedBB bb, double width, GSColor[] otherPos) {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        GlStateManager.func_187441_d((float)((float)width));
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        RenderUtil.colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, otherPos[0], otherPos[0].getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, otherPos[1], otherPos[1].getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, otherPos[2], otherPos[2].getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, otherPos[3], otherPos[3].getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, otherPos[0], otherPos[0].getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, otherPos[4], otherPos[4].getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, otherPos[5], otherPos[5].getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, otherPos[1], otherPos[1].getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, otherPos[2], otherPos[2].getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, otherPos[6], otherPos[6].getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, otherPos[5], otherPos[5].getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, otherPos[6], otherPos[6].getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, otherPos[7], otherPos[7].getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, otherPos[3], otherPos[3].getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, otherPos[7], otherPos[7].getAlpha(), bufferbuilder);
        RenderUtil.colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, otherPos[4], otherPos[4].getAlpha(), bufferbuilder);
        tessellator.func_78381_a();
    }

    public static void drawBoundingBox(AxisAlignedBB axisAlignedBB, double width, GSColor[] color, boolean five, int sides) {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        GlStateManager.func_187441_d((float)((float)width));
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        if ((sides & 0x20) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[2], color[2].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[7], color[7].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[6], color[6].getAlpha(), bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[2], color[2].getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 0x10) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[0], color[0].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[1], color[1].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[5], color[5].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[4], color[4].getAlpha(), bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[0], color[0].getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 4) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[0], color[0].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[4], color[4].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[7], color[7].getAlpha(), bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 8) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[1], color[1].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[2], color[2].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[6], color[6].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[5], color[5].getAlpha(), bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[1], color[1].getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 2) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[7], color[7].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[6], color[6].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[5], color[5].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color[4], color[4].getAlpha(), bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color[7], color[7].getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 1) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[2], color[2].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color[1], color[1].getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[0], color[0].getAlpha(), bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color[3], color[3].getAlpha(), bufferbuilder);
            }
        }
        tessellator.func_78381_a();
    }

    public static void drawBoundingBoxWithSides(BlockPos blockPos, int width, GSColor color, int sides) {
        RenderUtil.drawBoundingBoxWithSides(RenderUtil.getBoundingBox(blockPos, 1.0, 1.0, 1.0), width, color, color.getAlpha(), sides);
    }

    public static void drawBoundingBoxWithSides(BlockPos blockPos, int width, GSColor color, int alpha, int sides) {
        RenderUtil.drawBoundingBoxWithSides(RenderUtil.getBoundingBox(blockPos, 1.0, 1.0, 1.0), width, color, alpha, sides);
    }

    public static void drawBoundingBoxWithSides(AxisAlignedBB axisAlignedBB, int width, GSColor color, int sides) {
        RenderUtil.drawBoundingBoxWithSides(axisAlignedBB, width, color, color.getAlpha(), sides);
    }

    public static void drawBoundingBoxWithSides(AxisAlignedBB axisAlignedBB, int width, GSColor color, int alpha, int sides) {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        GlStateManager.func_187441_d((float)width);
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        RenderUtil.doVerticies(axisAlignedBB, color, alpha, bufferbuilder, sides, true);
        tessellator.func_78381_a();
    }

    public static void drawBoxWithDirection(AxisAlignedBB bb, GSColor color, float rotation, float width, int mode) {
        double xCenter = bb.field_72340_a + (bb.field_72336_d - bb.field_72340_a) / 2.0;
        double zCenter = bb.field_72339_c + (bb.field_72334_f - bb.field_72339_c) / 2.0;
        Points square = new Points(bb.field_72338_b, bb.field_72337_e, xCenter, zCenter, rotation);
        if (mode == 0) {
            square.addPoints(bb.field_72340_a, bb.field_72339_c);
            square.addPoints(bb.field_72340_a, bb.field_72334_f);
            square.addPoints(bb.field_72336_d, bb.field_72334_f);
            square.addPoints(bb.field_72336_d, bb.field_72339_c);
        }
        switch (mode) {
            case 0: {
                RenderUtil.drawDirection(square, color, width);
            }
        }
    }

    public static void drawDirection(Points square, GSColor color, float width) {
        int i;
        for (i = 0; i < 4; ++i) {
            RenderUtil.drawLine(square.getPoint(i)[0], square.yMin, square.getPoint(i)[1], square.getPoint((i + 1) % 4)[0], square.yMin, square.getPoint((i + 1) % 4)[1], color, width);
        }
        for (i = 0; i < 4; ++i) {
            RenderUtil.drawLine(square.getPoint(i)[0], square.yMax, square.getPoint(i)[1], square.getPoint((i + 1) % 4)[0], square.yMax, square.getPoint((i + 1) % 4)[1], color, width);
        }
        for (i = 0; i < 4; ++i) {
            RenderUtil.drawLine(square.getPoint(i)[0], square.yMin, square.getPoint(i)[1], square.getPoint(i)[0], square.yMax, square.getPoint(i)[1], color, width);
        }
    }

    public static void drawSphere(double x, double y, double z, float size, int slices, int stacks, float lineWidth, GSColor color) {
        Sphere sphere = new Sphere();
        GlStateManager.func_187441_d((float)lineWidth);
        color.glColor();
        sphere.setDrawStyle(100013);
        GlStateManager.func_179094_E();
        GlStateManager.func_179137_b((double)(x - RenderUtil.mc.func_175598_ae().field_78730_l), (double)(y - RenderUtil.mc.func_175598_ae().field_78731_m), (double)(z - RenderUtil.mc.func_175598_ae().field_78728_n));
        sphere.draw(size, slices, stacks);
        GlStateManager.func_179121_F();
    }

    public static void drawCircle(float x, float y, float z, Double radius, GSColor colour) {
        GlStateManager.func_179129_p();
        GlStateManager.func_179118_c();
        GlStateManager.func_179103_j((int)7425);
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        int alpha = 255 - colour.getAlpha();
        if (alpha == 0) {
            alpha = 1;
        }
        for (int i = 0; i < 361; ++i) {
            bufferbuilder.func_181662_b((double)x + Math.sin(Math.toRadians(i)) * radius - RenderUtil.mc.func_175598_ae().field_78730_l, (double)y - RenderUtil.mc.func_175598_ae().field_78731_m, (double)z + Math.cos(Math.toRadians(i)) * radius - RenderUtil.mc.func_175598_ae().field_78728_n).func_181666_a((float)colour.getRed() / 255.0f, (float)colour.getGreen() / 255.0f, (float)colour.getBlue() / 255.0f, (float)alpha).func_181675_d();
        }
        tessellator.func_78381_a();
        GlStateManager.func_179089_o();
        GlStateManager.func_179141_d();
        GlStateManager.func_179103_j((int)7424);
    }

    public static void drawCircle(float x, float y, float z, Double radius, int stepCircle, int alphaVal) {
        GlStateManager.func_179129_p();
        GlStateManager.func_179118_c();
        GlStateManager.func_179103_j((int)7425);
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        int alpha = 255 - alphaVal;
        if (alpha == 0) {
            alpha = 1;
        }
        for (int i = 0; i < 361; ++i) {
            GSColor colour = ColorSetting.getRainbowColor(i % 180 * stepCircle);
            bufferbuilder.func_181662_b((double)x + Math.sin(Math.toRadians(i)) * radius - RenderUtil.mc.func_175598_ae().field_78730_l, (double)y - RenderUtil.mc.func_175598_ae().field_78731_m, (double)z + Math.cos(Math.toRadians(i)) * radius - RenderUtil.mc.func_175598_ae().field_78728_n).func_181666_a((float)colour.getRed() / 255.0f, (float)colour.getGreen() / 255.0f, (float)colour.getBlue() / 255.0f, (float)alpha).func_181675_d();
        }
        tessellator.func_78381_a();
        GlStateManager.func_179089_o();
        GlStateManager.func_179141_d();
        GlStateManager.func_179103_j((int)7424);
    }

    public static void drawNametag(Entity entity, String[] text, GSColor color, int type) {
        Vec3d pos = EntityUtil.getInterpolatedPos(entity, mc.func_184121_ak());
        RenderUtil.drawNametag(pos.field_72450_a, pos.field_72448_b + (double)entity.field_70131_O, pos.field_72449_c, text, color, type);
    }

    public static void drawNametag(double x, double y, double z, String[] text, GSColor color, int type) {
        ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        double dist = RenderUtil.mc.field_71439_g.func_70011_f(x, y, z);
        double scale = 1.0;
        double offset = 0.0;
        int start = 0;
        switch (type) {
            case 0: {
                scale = dist / 20.0 * Math.pow(1.2589254, 0.1 / (dist < 25.0 ? 0.5 : 2.0));
                scale = Math.min(Math.max(scale, 0.5), 5.0);
                offset = scale > 2.0 ? scale / 2.0 : scale;
                scale /= 40.0;
                start = 10;
                break;
            }
            case 1: {
                scale = (double)(-((int)dist)) / 6.0;
                if (scale < 1.0) {
                    scale = 1.0;
                }
                scale *= 0.02666666666666667;
                break;
            }
            case 2: {
                scale = 0.0018 + 0.003 * dist;
                if (dist <= 8.0) {
                    scale = 0.0245;
                }
                start = -8;
            }
        }
        GlStateManager.func_179094_E();
        GlStateManager.func_179137_b((double)(x - RenderUtil.mc.func_175598_ae().field_78730_l), (double)(y + offset - RenderUtil.mc.func_175598_ae().field_78731_m), (double)(z - RenderUtil.mc.func_175598_ae().field_78728_n));
        GlStateManager.func_179114_b((float)(-RenderUtil.mc.func_175598_ae().field_78735_i), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.func_179114_b((float)RenderUtil.mc.func_175598_ae().field_78732_j, (float)(RenderUtil.mc.field_71474_y.field_74320_O == 2 ? -1.0f : 1.0f), (float)0.0f, (float)0.0f);
        GlStateManager.func_179139_a((double)(-scale), (double)(-scale), (double)scale);
        if (type == 2) {
            double width = 0.0;
            GSColor bcolor = new GSColor(0, 0, 0, 51);
            Nametags nametags = ModuleManager.getModule(Nametags.class);
            if (((Boolean)nametags.customColor.getValue()).booleanValue()) {
                bcolor = nametags.borderColor.getValue();
            }
            for (int i = 0; i < text.length; ++i) {
                double w = FontUtil.getStringWidth((Boolean)colorMain.customFont.getValue(), text[i]) / 2;
                if (!(w > width)) continue;
                width = w;
            }
            RenderUtil.drawBorderedRect(-width - 1.0, -RenderUtil.mc.field_71466_p.field_78288_b, width + 2.0, 1.0, 1.8f, new GSColor(0, 4, 0, 85), bcolor);
        }
        GlStateManager.func_179098_w();
        for (int i = 0; i < text.length; ++i) {
            FontUtil.drawStringWithShadow((Boolean)colorMain.customFont.getValue(), text[i], -FontUtil.getStringWidth((Boolean)colorMain.customFont.getValue(), text[i]) / 2, i * (RenderUtil.mc.field_71466_p.field_78288_b + 1) + start, color);
        }
        GlStateManager.func_179090_x();
        if (type != 2) {
            GlStateManager.func_179121_F();
        }
    }

    private static void vertex(double x, double y, double z, BufferBuilder bufferbuilder) {
        bufferbuilder.func_181662_b(x - RenderUtil.mc.func_175598_ae().field_78730_l, y - RenderUtil.mc.func_175598_ae().field_78731_m, z - RenderUtil.mc.func_175598_ae().field_78728_n).func_181675_d();
    }

    private static void colorVertex(double x, double y, double z, GSColor color, int alpha, BufferBuilder bufferbuilder) {
        bufferbuilder.func_181662_b(x - RenderUtil.mc.func_175598_ae().field_78730_l, y - RenderUtil.mc.func_175598_ae().field_78731_m, z - RenderUtil.mc.func_175598_ae().field_78728_n).func_181669_b(color.getRed(), color.getGreen(), color.getBlue(), alpha).func_181675_d();
    }

    public static void PublicColorVertex(double x, double y, double z, GSColor color, int alpha, BufferBuilder bufferbuilder) {
        bufferbuilder.func_181662_b(x - RenderUtil.mc.func_175598_ae().field_78730_l, y - RenderUtil.mc.func_175598_ae().field_78731_m, z - RenderUtil.mc.func_175598_ae().field_78728_n).func_181669_b(color.getRed(), color.getGreen(), color.getBlue(), alpha).func_181675_d();
    }

    private static AxisAlignedBB getBoundingBox(BlockPos bp, double width, double height, double depth) {
        double x = bp.func_177958_n();
        double y = bp.func_177956_o();
        double z = bp.func_177952_p();
        return new AxisAlignedBB(x, y, z, x + width, y + height, z + depth);
    }

    private static void doVerticies(AxisAlignedBB axisAlignedBB, GSColor color, int alpha, BufferBuilder bufferbuilder, int sides, boolean five) {
        if ((sides & 0x20) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 0x10) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 4) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 8) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 2) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
            }
        }
        if ((sides & 1) != 0) {
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
            RenderUtil.colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            if (five) {
                RenderUtil.colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            }
        }
    }

    public static void prepare() {
        GL11.glHint((int)3154, (int)4354);
        GlStateManager.func_179120_a((int)770, (int)771, (int)0, (int)1);
        GlStateManager.func_179103_j((int)7425);
        GlStateManager.func_179132_a((boolean)false);
        GlStateManager.func_179147_l();
        GlStateManager.func_179097_i();
        GlStateManager.func_179090_x();
        GlStateManager.func_179140_f();
        GlStateManager.func_179129_p();
        GlStateManager.func_179141_d();
        GL11.glEnable((int)2848);
        GL11.glEnable((int)34383);
    }

    public static void release() {
        GL11.glDisable((int)34383);
        GL11.glDisable((int)2848);
        GlStateManager.func_179141_d();
        GlStateManager.func_179089_o();
        GlStateManager.func_179098_w();
        GlStateManager.func_179126_j();
        GlStateManager.func_179084_k();
        GlStateManager.func_179132_a((boolean)true);
        GlStateManager.func_187441_d((float)1.0f);
        GlStateManager.func_179103_j((int)7424);
        GL11.glHint((int)3154, (int)4352);
    }

    private static class Points {
        double[][] point = new double[10][2];
        private int count = 0;
        private final double xCenter;
        private final double zCenter;
        public final double yMin;
        public final double yMax;
        private final float rotation;

        public Points(double yMin, double yMax, double xCenter, double zCenter, float rotation) {
            this.yMin = yMin;
            this.yMax = yMax;
            this.xCenter = xCenter;
            this.zCenter = zCenter;
            this.rotation = rotation;
        }

        public void addPoints(double x, double z) {
            double rotateX = (x -= this.xCenter) * Math.cos(this.rotation) - (z -= this.zCenter) * Math.sin(this.rotation);
            double rotateZ = x * Math.sin(this.rotation) + z * Math.cos(this.rotation);
            this.point[this.count++] = new double[]{rotateX += this.xCenter, rotateZ += this.zCenter};
        }

        public double[] getPoint(int index) {
            return this.point[index];
        }
    }
}

