/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.combat.CrystalUtil;
import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

@Module.Declaration(name="CityESP", category=Category.Render)
public class CityESP
extends Module {
    IntegerSetting range = this.registerInteger("Range", 20, 1, 30);
    BooleanSetting onlyObby = this.registerBoolean("Only Obby", false);
    ModeSetting renderMode = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
    BooleanSetting self = this.registerBoolean("Self", false);
    IntegerSetting width = this.registerInteger("Width", 1, 1, 10);
    ColorSetting color = this.registerColor("Color", new GSColor(102, 51, 153));
    BooleanSetting newPlace = this.registerBoolean("New Place", false);
    DoubleSetting maxSelfDamage = this.registerDouble("Max Self Damage", 6.0, 0.0, 20.0);
    DoubleSetting minDamage = this.registerDouble("Min Damage", 6.0, 0.0, 20.0);

    @Override
    public void onWorldRender(RenderEvent event) {
        if (CityESP.mc.field_71439_g != null && CityESP.mc.field_71441_e != null) {
            CityESP.mc.field_71441_e.field_73010_i.stream().filter(entityPlayer -> entityPlayer.func_70032_d((Entity)CityESP.mc.field_71439_g) <= (float)((Integer)this.range.getValue()).intValue()).filter(entityPlayer -> !SocialManager.isFriend(entityPlayer.func_70005_c_())).filter(entityPlayer -> (Boolean)this.self.getValue() != false || entityPlayer != CityESP.mc.field_71439_g).forEach(entityPlayer -> {
                for (int[] positions : new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}}) {
                    BlockPos blockPos = new BlockPos(entityPlayer.field_70165_t + (double)positions[0], entityPlayer.field_70163_u + (double)positions[1] + (entityPlayer.field_70163_u % 1.0 > 0.2 ? 0.5 : 0.0), entityPlayer.field_70161_v + (double)positions[2]);
                    Block toCheck = BlockUtil.getBlock(blockPos);
                    if (toCheck instanceof BlockAir || ((Boolean)this.onlyObby.getValue() != false ? !(toCheck instanceof BlockObsidian) : toCheck.field_149781_w > 6001.0f)) continue;
                    CityESP.mc.field_71441_e.func_175698_g(blockPos);
                    for (Vec3i placement : new Vec3i[]{new Vec3i(1, -1, 0), new Vec3i(-1, -1, 0), new Vec3i(0, -1, 1), new Vec3i(0, -1, -1)}) {
                        float damagePlayer;
                        BlockPos temp = blockPos.func_177971_a(placement);
                        if (!CrystalUtil.canPlaceCrystal(temp, (Boolean)this.newPlace.getValue()) || (double)DamageUtil.calculateDamage((double)temp.func_177958_n() + 0.5, (double)temp.func_177956_o() + 1.0, (double)temp.func_177952_p() + 0.5, (Entity)CityESP.mc.field_71439_g, false) >= (Double)this.maxSelfDamage.getValue() || (double)(damagePlayer = DamageUtil.calculateDamage((double)temp.func_177958_n() + 0.5, (double)temp.func_177956_o() + 1.0, (double)temp.func_177952_p() + 0.5, (Entity)entityPlayer, false)) < (Double)this.minDamage.getValue()) continue;
                        this.renderBox2(blockPos);
                        break;
                    }
                    CityESP.mc.field_71441_e.func_175656_a(blockPos, toCheck.func_176223_P());
                }
            });
        }
    }

    private List<BlockPos> getBlocksToRender(EntityPlayer entityPlayer) {
        NonNullList blockPosList = NonNullList.func_191196_a();
        BlockPos blockPos = new BlockPos(entityPlayer.field_70165_t, entityPlayer.field_70163_u, entityPlayer.field_70161_v);
        if (CityESP.mc.field_71441_e.func_180495_p(blockPos.func_177974_f()).func_177230_c() != Blocks.field_150357_h) {
            blockPosList.add((Object)blockPos.func_177974_f());
        }
        if (CityESP.mc.field_71441_e.func_180495_p(blockPos.func_177976_e()).func_177230_c() != Blocks.field_150357_h) {
            blockPosList.add((Object)blockPos.func_177976_e());
        }
        if (CityESP.mc.field_71441_e.func_180495_p(blockPos.func_177978_c()).func_177230_c() != Blocks.field_150357_h) {
            blockPosList.add((Object)blockPos.func_177978_c());
        }
        if (CityESP.mc.field_71441_e.func_180495_p(blockPos.func_177968_d()).func_177230_c() != Blocks.field_150357_h) {
            blockPosList.add((Object)blockPos.func_177968_d());
        }
        return blockPosList;
    }

    private void renderBox2(BlockPos blockPos) {
        GSColor gsColor1 = new GSColor(this.color.getValue(), 255);
        GSColor gsColor2 = new GSColor(this.color.getValue(), 50);
        switch ((String)this.renderMode.getValue()) {
            case "Both": {
                RenderUtil.drawBox(blockPos, 1.0, gsColor2, 63);
                RenderUtil.drawBoundingBox(blockPos, 1.0, ((Integer)this.width.getValue()).intValue(), gsColor1);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(blockPos, 1.0, ((Integer)this.width.getValue()).intValue(), gsColor1);
                break;
            }
            case "Fill": {
                RenderUtil.drawBox(blockPos, 1.0, gsColor2, 63);
            }
        }
    }
}

