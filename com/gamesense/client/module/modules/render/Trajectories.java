/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemLingeringPotion;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemSplashPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

@Module.Declaration(name="Trajectories", category=Category.Render)
public class Trajectories
extends Module {
    ColorSetting color = this.registerColor("Color", new GSColor(255, 255, 255, 255), () -> true, true);
    BooleanSetting landed = this.registerBoolean("Landed", true);
    BooleanSetting line = this.registerBoolean("Line", true);
    BooleanSetting rainbowLine = this.registerBoolean("Rainbow Line", false);
    IntegerSetting rainbowSpeed = this.registerInteger("Rainbow Speed", 1, 1, 100);
    IntegerSetting rainbowDesync = this.registerInteger("Rainbow Desync", 1, 1, 500);
    long count = 0L;

    @Override
    public void onWorldRender(RenderEvent event) {
        this.count += (long)((Integer)this.rainbowSpeed.getValue()).intValue();
        long start = this.count;
        if (Trajectories.mc.field_71439_g == null || Trajectories.mc.field_71441_e == null || Trajectories.mc.field_71474_y.field_74320_O != 0) {
            return;
        }
        if (!(Trajectories.mc.field_71439_g.func_184614_ca() != ItemStack.field_190927_a && Trajectories.mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBow || Trajectories.mc.field_71439_g.func_184614_ca() != ItemStack.field_190927_a && this.isThrowable(Trajectories.mc.field_71439_g.func_184614_ca().func_77973_b()) || Trajectories.mc.field_71439_g.func_184592_cb() != ItemStack.field_190927_a && this.isThrowable(Trajectories.mc.field_71439_g.func_184592_cb().func_77973_b()))) {
            return;
        }
        double renderPosX = Trajectories.mc.field_71439_g.field_70142_S + (Trajectories.mc.field_71439_g.field_70165_t - Trajectories.mc.field_71439_g.field_70142_S) * (double)event.getPartialTicks();
        double renderPosY = Trajectories.mc.field_71439_g.field_70137_T + (Trajectories.mc.field_71439_g.field_70163_u - Trajectories.mc.field_71439_g.field_70137_T) * (double)event.getPartialTicks();
        double renderPosZ = Trajectories.mc.field_71439_g.field_70136_U + (Trajectories.mc.field_71439_g.field_70161_v - Trajectories.mc.field_71439_g.field_70136_U) * (double)event.getPartialTicks();
        Trajectories.mc.field_71439_g.func_184586_b(EnumHand.MAIN_HAND);
        Item item = null;
        if (Trajectories.mc.field_71439_g.func_184614_ca() != ItemStack.field_190927_a && (Trajectories.mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBow || this.isThrowable(Trajectories.mc.field_71439_g.func_184614_ca().func_77973_b()))) {
            item = Trajectories.mc.field_71439_g.func_184614_ca().func_77973_b();
        } else if (Trajectories.mc.field_71439_g.func_184592_cb() != ItemStack.field_190927_a && this.isThrowable(Trajectories.mc.field_71439_g.func_184592_cb().func_77973_b())) {
            item = Trajectories.mc.field_71439_g.func_184592_cb().func_77973_b();
        }
        if (item == null) {
            return;
        }
        double posX = renderPosX - Math.cos(Trajectories.mc.field_71439_g.field_70177_z / 180.0f * (float)Math.PI) * (double)0.16f;
        double posY = renderPosY + (double)Trajectories.mc.field_71439_g.func_70047_e() - 0.1000000014901161;
        double posZ = renderPosZ - Math.sin(Trajectories.mc.field_71439_g.field_70177_z / 180.0f * (float)Math.PI) * (double)0.16f;
        float maxDist = this.getDistance(item);
        double motionX = -Math.sin(Trajectories.mc.field_71439_g.field_70177_z / 180.0f * (float)Math.PI) * Math.cos(Trajectories.mc.field_71439_g.field_70125_A / 180.0f * (float)Math.PI) * (double)maxDist;
        double motionY = -Math.sin((Trajectories.mc.field_71439_g.field_70125_A - (float)this.getThrowPitch(item)) / 180.0f * 3.141593f) * (double)maxDist;
        double motionZ = Math.cos(Trajectories.mc.field_71439_g.field_70177_z / 180.0f * (float)Math.PI) * Math.cos(Trajectories.mc.field_71439_g.field_70125_A / 180.0f * (float)Math.PI) * (double)maxDist;
        int var6 = 72000 - Trajectories.mc.field_71439_g.func_184605_cv();
        float power = (float)var6 / 20.0f;
        if ((power = (power * power + power * 2.0f) / 3.0f) > 1.0f) {
            power = 1.0f;
        }
        float distance = (float)Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX /= (double)distance;
        motionY /= (double)distance;
        motionZ /= (double)distance;
        float pow = (item instanceof ItemBow ? power * 2.0f : 1.0f) * this.getThrowVelocity(item);
        motionX *= (double)pow;
        motionY *= (double)pow;
        motionZ *= (double)pow;
        if (!Trajectories.mc.field_71439_g.field_70122_E) {
            motionY += Trajectories.mc.field_71439_g.field_70181_x;
        }
        GlStateManager.func_179094_E();
        GlStateManager.func_179131_c((float)((float)this.color.getValue().getRed() / 255.0f), (float)((float)this.color.getValue().getGreen() / 255.0f), (float)((float)this.color.getValue().getBlue() / 255.0f), (float)((float)this.color.getValue().getAlpha() / 255.0f));
        GL11.glEnable((int)2848);
        float size = (float)(item instanceof ItemBow ? 0.3 : 0.25);
        boolean hasLanded = false;
        Entity landingOnEntity = null;
        RayTraceResult landingPosition = null;
        GL11.glBegin((int)3);
        while (!hasLanded && posY > 0.0) {
            Vec3d present = new Vec3d(posX, posY, posZ);
            Vec3d future = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            RayTraceResult possibleLandingStrip = Trajectories.mc.field_71441_e.func_147447_a(present, future, false, true, false);
            if (possibleLandingStrip != null && possibleLandingStrip.field_72313_a != RayTraceResult.Type.MISS) {
                landingPosition = possibleLandingStrip;
                hasLanded = true;
            }
            AxisAlignedBB arrowBox = new AxisAlignedBB(posX - (double)size, posY - (double)size, posZ - (double)size, posX + (double)size, posY + (double)size, posZ + (double)size);
            List<Entity> entities = this.getEntitiesWithinAABB(arrowBox.func_72317_d(motionX, motionY, motionZ).func_72321_a(1.0, 1.0, 1.0));
            for (Entity entity : entities) {
                Entity boundingBox = entity;
                if (!boundingBox.func_70067_L() || boundingBox == Trajectories.mc.field_71439_g) continue;
                float var7 = 0.3f;
                AxisAlignedBB var8 = boundingBox.func_174813_aQ().func_72321_a((double)var7, (double)var7, (double)var7);
                RayTraceResult possibleEntityLanding = var8.func_72327_a(present, future);
                if (possibleEntityLanding == null) continue;
                hasLanded = true;
                landingOnEntity = boundingBox;
                landingPosition = possibleEntityLanding;
            }
            if (landingOnEntity != null) {
                GlStateManager.func_179131_c((float)1.0f, (float)0.0f, (float)0.0f, (float)((float)this.color.getValue().getAlpha() / 255.0f));
            }
            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            float motionAdjustment = 0.99f;
            motionX *= (double)0.99f;
            motionY *= (double)0.99f;
            motionZ *= (double)0.99f;
            motionY -= (double)this.getGravity(item);
            if (((Boolean)this.rainbowLine.getValue()).booleanValue()) {
                start += (long)((Integer)this.rainbowDesync.getValue()).intValue();
            }
            if (!((Boolean)this.line.getValue()).booleanValue()) continue;
            this.drawLine3D(posX - renderPosX, posY - renderPosY, posZ - renderPosZ, start);
        }
        GL11.glEnd();
        if (((Boolean)this.landed.getValue()).booleanValue() && landingPosition != null && landingPosition.field_72313_a == RayTraceResult.Type.BLOCK) {
            GlStateManager.func_179137_b((double)(posX - renderPosX), (double)(posY - renderPosY), (double)(posZ - renderPosZ));
            int side = landingPosition.field_178784_b.func_176745_a();
            if (side == 2) {
                GlStateManager.func_179114_b((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            } else if (side == 3) {
                GlStateManager.func_179114_b((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            } else if (side == 4) {
                GlStateManager.func_179114_b((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            } else if (side == 5) {
                GlStateManager.func_179114_b((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            }
            Cylinder c = new Cylinder();
            GlStateManager.func_179114_b((float)-90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            c.setDrawStyle(100013);
            if (landingOnEntity != null) {
                GlStateManager.func_179131_c((float)0.0f, (float)0.0f, (float)0.0f, (float)((float)this.color.getValue().getAlpha() / 255.0f));
                GL11.glLineWidth((float)2.5f);
                c.draw(0.5f, 0.15f, 0.0f, 8, 1);
                GL11.glLineWidth((float)0.1f);
                GlStateManager.func_179131_c((float)1.0f, (float)0.0f, (float)0.0f, (float)((float)this.color.getValue().getAlpha() / 255.0f));
            }
            c.draw(0.5f, 0.15f, 0.0f, 8, 1);
        }
        GlStateManager.func_179121_F();
    }

    protected boolean isThrowable(Item item) {
        return item instanceof ItemEnderPearl || item instanceof ItemExpBottle || item instanceof ItemSnowball || item instanceof ItemEgg || item instanceof ItemSplashPotion || item instanceof ItemLingeringPotion;
    }

    protected float getDistance(Item item) {
        return item instanceof ItemBow ? 1.0f : 0.4f;
    }

    protected float getThrowVelocity(Item item) {
        if (item instanceof ItemSplashPotion || item instanceof ItemLingeringPotion) {
            return 0.5f;
        }
        if (item instanceof ItemExpBottle) {
            return 0.59f;
        }
        return 1.5f;
    }

    protected int getThrowPitch(Item item) {
        if (item instanceof ItemSplashPotion || item instanceof ItemLingeringPotion || item instanceof ItemExpBottle) {
            return 20;
        }
        return 0;
    }

    protected float getGravity(Item item) {
        if (item instanceof ItemBow || item instanceof ItemSplashPotion || item instanceof ItemLingeringPotion || item instanceof ItemExpBottle) {
            return 0.05f;
        }
        return 0.03f;
    }

    protected List<Entity> getEntitiesWithinAABB(AxisAlignedBB bb) {
        ArrayList<Entity> list = new ArrayList<Entity>();
        int chunkMinX = (int)Math.floor((bb.field_72340_a - 2.0) / 16.0);
        int chunkMaxX = (int)Math.floor((bb.field_72336_d + 2.0) / 16.0);
        int chunkMinZ = (int)Math.floor((bb.field_72339_c - 2.0) / 16.0);
        int chunkMaxZ = (int)Math.floor((bb.field_72334_f + 2.0) / 16.0);
        for (int x = chunkMinX; x <= chunkMaxX; ++x) {
            for (int z = chunkMinZ; z <= chunkMaxZ; ++z) {
                if (Trajectories.mc.field_71441_e.func_72863_F().func_186026_b(x, z) == null) continue;
                Trajectories.mc.field_71441_e.func_72964_e(x, z).func_177414_a((Entity)Trajectories.mc.field_71439_g, bb, list, EntitySelectors.field_180132_d);
            }
        }
        return list;
    }

    public void drawLine3D(double var1, double var2, double var3, long start) {
        if (((Boolean)this.rainbowLine.getValue()).booleanValue()) {
            new GSColor(ColorSetting.getRainbowColor(start), this.color.getValue().getAlpha()).glColor();
        }
        GL11.glVertex3d((double)var1, (double)var2, (double)var3);
    }
}

