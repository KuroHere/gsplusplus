/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.player;

import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AutoCrystalRewrite;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class PlacementUtil {
    private static final Minecraft mc = Minecraft.func_71410_x();
    private static int placementConnections = 0;
    private static boolean isSneaking = false;

    public static void onEnable() {
        ++placementConnections;
    }

    public static void stopSneaking() {
        if (isSneaking) {
            isSneaking = false;
            PlacementUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)PlacementUtil.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }

    public static void onDisable() {
        if (--placementConnections == 0 && isSneaking) {
            PlacementUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)PlacementUtil.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }
    }

    public static boolean placeBlock(BlockPos blockPos, EnumHand hand, boolean rotate, Class<? extends Block> blockToPlace) {
        int oldSlot = PlacementUtil.mc.field_71439_g.field_71071_by.field_70461_c;
        int newSlot = InventoryUtil.findFirstBlockSlot(blockToPlace, 0, 8);
        if (newSlot == -1) {
            return false;
        }
        PlacementUtil.mc.field_71439_g.field_71071_by.field_70461_c = newSlot;
        boolean output = PlacementUtil.place(blockPos, hand, rotate);
        PlacementUtil.mc.field_71439_g.field_71071_by.field_70461_c = oldSlot;
        return output;
    }

    public static boolean placeBlockSilent(BlockPos blockPos, EnumHand hand, boolean rotate, Class<? extends Block> blockToPlace) {
        int oldSlot = PlacementUtil.mc.field_71439_g.field_71071_by.field_70461_c;
        int newSlot = InventoryUtil.findFirstBlockSlot(blockToPlace, 0, 8);
        if (newSlot == -1) {
            return false;
        }
        PlacementUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(newSlot));
        boolean output = PlacementUtil.place(blockPos, hand, rotate);
        PlacementUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(oldSlot));
        return output;
    }

    public static boolean placeItem(BlockPos blockPos, EnumHand hand, boolean rotate, Class<? extends Item> itemToPlace) {
        int oldSlot = PlacementUtil.mc.field_71439_g.field_71071_by.field_70461_c;
        int newSlot = InventoryUtil.findFirstItemSlot(itemToPlace, 0, 8);
        if (newSlot == -1) {
            return false;
        }
        PlacementUtil.mc.field_71439_g.field_71071_by.field_70461_c = newSlot;
        boolean output = PlacementUtil.place(blockPos, hand, rotate);
        PlacementUtil.mc.field_71439_g.field_71071_by.field_70461_c = oldSlot;
        return output;
    }

    public static boolean place(BlockPos blockPos, EnumHand hand, boolean rotate) {
        return PlacementUtil.placeBlock(blockPos, hand, rotate, true, null, true);
    }

    public static boolean place(BlockPos blockPos, EnumHand hand, boolean rotate, ArrayList<EnumFacing> forceSide) {
        return PlacementUtil.placeBlock(blockPos, hand, rotate, true, forceSide, true);
    }

    public static boolean place(BlockPos blockPos, EnumHand hand, boolean rotate, boolean checkAction) {
        return PlacementUtil.placeBlock(blockPos, hand, rotate, checkAction, null, true);
    }

    public static boolean place(BlockPos blockPos, EnumHand hand, boolean rotate, boolean checkAction, boolean swingArm) {
        return PlacementUtil.placeBlock(blockPos, hand, rotate, checkAction, null, swingArm);
    }

    public static boolean placeBlock(BlockPos blockPos, EnumHand hand, boolean rotate, boolean checkAction, ArrayList<EnumFacing> forceSide, boolean swingArm) {
        EnumFacing side;
        EntityPlayerSP player = PlacementUtil.mc.field_71439_g;
        WorldClient world = PlacementUtil.mc.field_71441_e;
        PlayerControllerMP playerController = PlacementUtil.mc.field_71442_b;
        if (player == null || world == null || playerController == null) {
            return false;
        }
        if (!world.func_180495_p(blockPos).func_185904_a().func_76222_j()) {
            return false;
        }
        EnumFacing enumFacing = side = forceSide != null ? BlockUtil.getPlaceableSideExlude(blockPos, forceSide) : BlockUtil.getPlaceableSide(blockPos);
        if (side == null) {
            return false;
        }
        BlockPos neighbour = blockPos.func_177972_a(side);
        EnumFacing opposite = side.func_176734_d();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }
        Vec3d hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.5, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        Block neighbourBlock = world.func_180495_p(neighbour).func_177230_c();
        if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            player.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }
        boolean stoppedAC = false;
        if (ModuleManager.isModuleEnabled(AutoCrystalRewrite.class)) {
            AutoCrystalRewrite.stopAC = true;
            stoppedAC = true;
        }
        if (rotate) {
            BlockUtil.faceVectorPacketInstant(hitVec, true);
        }
        EnumActionResult action = playerController.func_187099_a(player, world, neighbour, opposite, hitVec, hand);
        if (!checkAction || action == EnumActionResult.SUCCESS) {
            if (swingArm) {
                player.func_184609_a(hand);
                PlacementUtil.mc.field_71467_ac = 4;
            } else {
                player.field_71174_a.func_147297_a((Packet)new CPacketAnimation(hand));
            }
        }
        if (stoppedAC) {
            AutoCrystalRewrite.stopAC = false;
        }
        return action == EnumActionResult.SUCCESS;
    }

    public static CPacketPlayer.Rotation placeBlockGetRotate(BlockPos blockPos, EnumHand hand, boolean checkAction, ArrayList<EnumFacing> forceSide, boolean swingArm) {
        EnumFacing side;
        EntityPlayerSP player = PlacementUtil.mc.field_71439_g;
        WorldClient world = PlacementUtil.mc.field_71441_e;
        PlayerControllerMP playerController = PlacementUtil.mc.field_71442_b;
        if (player == null || world == null || playerController == null) {
            return null;
        }
        if (!world.func_180495_p(blockPos).func_185904_a().func_76222_j()) {
            return null;
        }
        EnumFacing enumFacing = side = forceSide != null ? BlockUtil.getPlaceableSideExlude(blockPos, forceSide) : BlockUtil.getPlaceableSide(blockPos);
        if (side == null) {
            return null;
        }
        BlockPos neighbour = blockPos.func_177972_a(side);
        EnumFacing opposite = side.func_176734_d();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return null;
        }
        Vec3d hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.5, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        Block neighbourBlock = world.func_180495_p(neighbour).func_177230_c();
        if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            player.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }
        boolean stoppedAC = false;
        if (ModuleManager.isModuleEnabled(AutoCrystalRewrite.class)) {
            AutoCrystalRewrite.stopAC = true;
            stoppedAC = true;
        }
        EnumActionResult action = playerController.func_187099_a(player, world, neighbour, opposite, hitVec, hand);
        if (!checkAction || action == EnumActionResult.SUCCESS) {
            if (swingArm) {
                player.func_184609_a(hand);
                PlacementUtil.mc.field_71467_ac = 4;
            } else {
                player.field_71174_a.func_147297_a((Packet)new CPacketAnimation(hand));
            }
        }
        if (stoppedAC) {
            AutoCrystalRewrite.stopAC = false;
        }
        return BlockUtil.getFaceVectorPacket(hitVec, true);
    }

    public static boolean placePrecise(BlockPos blockPos, EnumHand hand, boolean rotate, Vec3d precise, EnumFacing forceSide, boolean onlyRotation, boolean support) {
        EnumFacing side;
        EntityPlayerSP player = PlacementUtil.mc.field_71439_g;
        WorldClient world = PlacementUtil.mc.field_71441_e;
        PlayerControllerMP playerController = PlacementUtil.mc.field_71442_b;
        if (player == null || world == null || playerController == null) {
            return false;
        }
        if (!world.func_180495_p(blockPos).func_185904_a().func_76222_j()) {
            return false;
        }
        EnumFacing enumFacing = side = forceSide == null ? BlockUtil.getPlaceableSide(blockPos) : forceSide;
        if (side == null) {
            return false;
        }
        BlockPos neighbour = blockPos.func_177972_a(side);
        EnumFacing opposite = side.func_176734_d();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }
        Vec3d hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.5, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        Block neighbourBlock = world.func_180495_p(neighbour).func_177230_c();
        if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            player.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }
        boolean stoppedAC = false;
        if (ModuleManager.isModuleEnabled(AutoCrystalRewrite.class)) {
            AutoCrystalRewrite.stopAC = true;
            stoppedAC = true;
        }
        if (rotate && !support) {
            BlockUtil.faceVectorPacketInstant(precise == null ? hitVec : precise, true);
        }
        if (!onlyRotation) {
            EnumActionResult action = playerController.func_187099_a(player, world, neighbour, opposite, precise == null ? hitVec : precise, hand);
            if (action == EnumActionResult.SUCCESS) {
                player.func_184609_a(hand);
                PlacementUtil.mc.field_71467_ac = 4;
            }
            if (stoppedAC) {
                AutoCrystalRewrite.stopAC = false;
            }
            return action == EnumActionResult.SUCCESS;
        }
        return true;
    }
}

