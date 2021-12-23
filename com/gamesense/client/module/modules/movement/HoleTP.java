/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.Speed;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

@Module.Declaration(name="HoleTP", category=Category.Movement)
public class HoleTP
extends Module {
    private int packets;
    private boolean jumped;
    private final double[] oneblockPositions = new double[]{0.42, 0.75};

    @Override
    public void onUpdate() {
        if (HoleTP.mc.field_71441_e == null || HoleTP.mc.field_71439_g == null || ModuleManager.isModuleEnabled(Speed.class)) {
            return;
        }
        if (!HoleTP.mc.field_71439_g.field_70122_E) {
            if (HoleTP.mc.field_71474_y.field_74314_A.func_151470_d()) {
                this.jumped = true;
            }
        } else {
            this.jumped = false;
        }
        if (!this.jumped && (double)HoleTP.mc.field_71439_g.field_70143_R < 0.5 && this.isInHole() && HoleTP.mc.field_71439_g.field_70163_u - this.getNearestBlockBelow() <= 1.125 && HoleTP.mc.field_71439_g.field_70163_u - this.getNearestBlockBelow() <= 0.95 && !this.isOnLiquid() && !this.isInLiquid()) {
            if (!HoleTP.mc.field_71439_g.field_70122_E) {
                ++this.packets;
            }
            if (!(HoleTP.mc.field_71439_g.field_70122_E || HoleTP.mc.field_71439_g.func_70055_a(Material.field_151586_h) || HoleTP.mc.field_71439_g.func_70055_a(Material.field_151587_i) || HoleTP.mc.field_71474_y.field_74314_A.func_151470_d() || HoleTP.mc.field_71439_g.func_70617_f_() || this.packets <= 0)) {
                BlockPos blockPos = new BlockPos(HoleTP.mc.field_71439_g.field_70165_t, HoleTP.mc.field_71439_g.field_70163_u, HoleTP.mc.field_71439_g.field_70161_v);
                for (double position : this.oneblockPositions) {
                    HoleTP.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position((double)((float)blockPos.func_177958_n() + 0.5f), HoleTP.mc.field_71439_g.field_70163_u - position, (double)((float)blockPos.func_177952_p() + 0.5f), true));
                }
                HoleTP.mc.field_71439_g.func_70107_b((double)((float)blockPos.func_177958_n() + 0.5f), this.getNearestBlockBelow() + 0.1, (double)((float)blockPos.func_177952_p() + 0.5f));
                this.packets = 0;
            }
        }
    }

    private boolean isInHole() {
        BlockPos blockPos = new BlockPos(HoleTP.mc.field_71439_g.field_70165_t, HoleTP.mc.field_71439_g.field_70163_u, HoleTP.mc.field_71439_g.field_70161_v);
        IBlockState blockState = HoleTP.mc.field_71441_e.func_180495_p(blockPos);
        return this.isBlockValid(blockState, blockPos);
    }

    private double getNearestBlockBelow() {
        int y = (int)Math.floor(HoleTP.mc.field_71439_g.field_70163_u);
        while ((double)y > 0.0) {
            if (!(HoleTP.mc.field_71441_e.func_180495_p(new BlockPos(HoleTP.mc.field_71439_g.field_70165_t, (double)y, HoleTP.mc.field_71439_g.field_70161_v)).func_177230_c() instanceof BlockSlab) && HoleTP.mc.field_71441_e.func_180495_p(new BlockPos(HoleTP.mc.field_71439_g.field_70165_t, (double)y, HoleTP.mc.field_71439_g.field_70161_v)).func_177230_c().func_176223_P().func_185890_d((IBlockAccess)HoleTP.mc.field_71441_e, new BlockPos(0, 0, 0)) != null) {
                return y + 1;
            }
            --y;
        }
        return -1.0;
    }

    private boolean isBlockValid(IBlockState blockState, BlockPos blockPos) {
        return blockState.func_177230_c() == Blocks.field_150350_a && HoleTP.mc.field_71439_g.func_174818_b(blockPos) >= 1.0 && HoleTP.mc.field_71441_e.func_180495_p(blockPos.func_177984_a()).func_177230_c() == Blocks.field_150350_a && HoleTP.mc.field_71441_e.func_180495_p(blockPos.func_177981_b(2)).func_177230_c() == Blocks.field_150350_a && this.isSafeHole(blockPos);
    }

    private boolean isSafeHole(BlockPos blockPos) {
        return HoleUtil.isHole(blockPos, true, false).getType() != HoleUtil.HoleType.NONE;
    }

    private boolean isOnLiquid() {
        double y = HoleTP.mc.field_71439_g.field_70163_u - 0.03;
        for (int x = MathHelper.func_76128_c((double)HoleTP.mc.field_71439_g.field_70165_t); x < MathHelper.func_76143_f((double)HoleTP.mc.field_71439_g.field_70165_t); ++x) {
            for (int z = MathHelper.func_76128_c((double)HoleTP.mc.field_71439_g.field_70161_v); z < MathHelper.func_76143_f((double)HoleTP.mc.field_71439_g.field_70161_v); ++z) {
                BlockPos pos = new BlockPos(x, MathHelper.func_76128_c((double)y), z);
                if (!(HoleTP.mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockLiquid)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean isInLiquid() {
        double y = HoleTP.mc.field_71439_g.field_70163_u + 0.01;
        for (int x = MathHelper.func_76128_c((double)HoleTP.mc.field_71439_g.field_70165_t); x < MathHelper.func_76143_f((double)HoleTP.mc.field_71439_g.field_70165_t); ++x) {
            for (int z = MathHelper.func_76128_c((double)HoleTP.mc.field_71439_g.field_70161_v); z < MathHelper.func_76143_f((double)HoleTP.mc.field_71439_g.field_70161_v); ++z) {
                BlockPos pos = new BlockPos(x, (int)y, z);
                if (!(HoleTP.mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockLiquid)) continue;
                return true;
            }
        }
        return false;
    }
}

