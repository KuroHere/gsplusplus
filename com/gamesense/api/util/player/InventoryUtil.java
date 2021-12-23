/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.player;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.OffHand;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InventoryUtil {
    private static final Minecraft mc = Minecraft.func_71410_x();

    public static void swap(int InvSlot, int newSlot) {
        InventoryUtil.mc.field_71442_b.func_187098_a(0, InvSlot, 0, ClickType.PICKUP, (EntityPlayer)InventoryUtil.mc.field_71439_g);
        InventoryUtil.mc.field_71442_b.func_187098_a(0, newSlot, 0, ClickType.PICKUP, (EntityPlayer)InventoryUtil.mc.field_71439_g);
        InventoryUtil.mc.field_71442_b.func_187098_a(0, InvSlot, 0, ClickType.PICKUP, (EntityPlayer)InventoryUtil.mc.field_71439_g);
        InventoryUtil.mc.field_71442_b.func_78765_e();
    }

    public static int findObsidianSlot(boolean offHandActived, boolean activeBefore) {
        int slot = -1;
        NonNullList mainInventory = InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        if (offHandActived && ModuleManager.isModuleEnabled(OffHand.class)) {
            if (!activeBefore) {
                OffHand.requestItems(0);
            }
            return 9;
        }
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = (ItemStack)mainInventory.get(i);
            if (stack == ItemStack.field_190927_a || !(stack.func_77973_b() instanceof ItemBlock) || !((block = ((ItemBlock)stack.func_77973_b()).func_179223_d()) instanceof BlockObsidian)) continue;
            slot = i;
            break;
        }
        return slot;
    }

    public static int findCrystalBlockSlot(boolean offHandActived, boolean activeBefore) {
        int slot = -1;
        NonNullList mainInventory = InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        if (offHandActived && ModuleManager.isModuleEnabled(OffHand.class)) {
            if (!activeBefore) {
                OffHand.requestItems(0);
            }
            return 9;
        }
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = (ItemStack)mainInventory.get(i);
            if (stack == ItemStack.field_190927_a || !(stack.func_77973_b() instanceof ItemBlock)) continue;
            Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
            if (!(block.func_176194_O().func_177622_c().field_149782_v > 6.0f)) continue;
            slot = i;
            break;
        }
        return slot;
    }

    public static int findSkullSlot(boolean offHandActived, boolean activeBefore) {
        int slot = -1;
        NonNullList mainInventory = InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        if (offHandActived) {
            if (!activeBefore) {
                OffHand.requestItems(1);
            }
            return 9;
        }
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = (ItemStack)mainInventory.get(i);
            if (stack == ItemStack.field_190927_a || !(stack.func_77973_b() instanceof ItemSkull)) continue;
            return i;
        }
        return slot;
    }

    public static int findTotemSlot(int lower, int upper) {
        int slot = -1;
        NonNullList mainInventory = InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        for (int i = lower; i <= upper; ++i) {
            ItemStack stack = (ItemStack)mainInventory.get(i);
            if (stack == ItemStack.field_190927_a || stack.func_77973_b() != Items.field_190929_cY) continue;
            slot = i;
            break;
        }
        return slot;
    }

    public static int findFirstShulker(int lower, int upper) {
        int slot = -1;
        NonNullList mainInventory = InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        for (int i = lower; i <= upper; ++i) {
            ItemStack stack = (ItemStack)mainInventory.get(i);
            if (stack == ItemStack.field_190927_a || !(stack.func_77973_b() instanceof ItemBlock) || !(((ItemBlock)stack.func_77973_b()).func_179223_d() instanceof BlockShulkerBox)) continue;
            slot = i;
            break;
        }
        return slot;
    }

    public static int findFirstItemSlot(Class<? extends Item> itemToFind, int lower, int upper) {
        int slot = -1;
        NonNullList mainInventory = InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        for (int i = lower; i <= upper; ++i) {
            ItemStack stack = (ItemStack)mainInventory.get(i);
            if (stack == ItemStack.field_190927_a || !itemToFind.isInstance(stack.func_77973_b()) || !itemToFind.isInstance(stack.func_77973_b())) continue;
            slot = i;
            break;
        }
        return slot;
    }

    public static int findFirstBlockSlot(Class<? extends Block> blockToFind, int lower, int upper) {
        int slot = -1;
        NonNullList mainInventory = InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        for (int i = lower; i <= upper; ++i) {
            ItemStack stack = (ItemStack)mainInventory.get(i);
            if (stack == ItemStack.field_190927_a || !(stack.func_77973_b() instanceof ItemBlock) || !blockToFind.isInstance(((ItemBlock)stack.func_77973_b()).func_179223_d())) continue;
            slot = i;
            break;
        }
        return slot;
    }

    public static int findAnyBlockSlot(int lower, int upper) {
        int slot = -1;
        NonNullList mainInventory = InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        for (int i = lower; i <= upper; ++i) {
            ItemStack stack = (ItemStack)mainInventory.get(i);
            if (stack == ItemStack.field_190927_a || !(stack.func_77973_b() instanceof ItemBlock)) continue;
            slot = i;
        }
        return slot;
    }

    public static List<Integer> findAllItemSlots(Class<? extends Item> itemToFind) {
        ArrayList<Integer> slots = new ArrayList<Integer>();
        NonNullList mainInventory = InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        for (int i = 0; i < 36; ++i) {
            ItemStack stack = (ItemStack)mainInventory.get(i);
            if (stack == ItemStack.field_190927_a || !itemToFind.isInstance(stack.func_77973_b())) continue;
            slots.add(i);
        }
        return slots;
    }

    public static List<Integer> findAllBlockSlots(Class<? extends Block> blockToFind) {
        ArrayList<Integer> slots = new ArrayList<Integer>();
        NonNullList mainInventory = InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        for (int i = 0; i < 36; ++i) {
            ItemStack stack = (ItemStack)mainInventory.get(i);
            if (stack == ItemStack.field_190927_a || !(stack.func_77973_b() instanceof ItemBlock) || !blockToFind.isInstance(((ItemBlock)stack.func_77973_b()).func_179223_d())) continue;
            slots.add(i);
        }
        return slots;
    }

    public static int findToolForBlockState(IBlockState iBlockState, int lower, int upper) {
        int slot = -1;
        NonNullList mainInventory = InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        double foundMaxSpeed = 0.0;
        for (int i = lower; i <= upper; ++i) {
            ItemStack itemStack = (ItemStack)mainInventory.get(i);
            if (itemStack == ItemStack.field_190927_a) continue;
            float breakSpeed = itemStack.func_150997_a(iBlockState);
            int efficiencySpeed = EnchantmentHelper.func_77506_a((Enchantment)Enchantments.field_185305_q, (ItemStack)itemStack);
            if (!(breakSpeed > 1.0f) || !((double)(breakSpeed = (float)((double)breakSpeed + (efficiencySpeed > 0 ? Math.pow(efficiencySpeed, 2.0) + 1.0 : 0.0))) > foundMaxSpeed)) continue;
            foundMaxSpeed = breakSpeed;
            slot = i;
        }
        return slot;
    }
}

