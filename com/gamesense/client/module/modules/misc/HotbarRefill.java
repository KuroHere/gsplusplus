/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.misc.Pair;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

@Module.Declaration(name="HotbarRefill", category=Category.Misc)
public class HotbarRefill
extends Module {
    BooleanSetting stop = this.registerBoolean("Strict", false);
    IntegerSetting threshold = this.registerInteger("Threshold", 32, 1, 63);
    IntegerSetting tickDelay = this.registerInteger("Tick Delay", 2, 1, 10);
    private int delayStep = 0;

    @Override
    public void onUpdate() {
        if (HotbarRefill.mc.field_71439_g == null) {
            return;
        }
        if (HotbarRefill.mc.field_71462_r instanceof GuiContainer) {
            return;
        }
        if (this.delayStep < (Integer)this.tickDelay.getValue()) {
            ++this.delayStep;
            return;
        }
        this.delayStep = 0;
        Pair<Integer, Integer> slots = this.findReplenishableHotbarSlot();
        if (slots == null) {
            return;
        }
        int inventorySlot = slots.getKey();
        int hotbarSlot = slots.getValue();
        if (!HotbarRefill.mc.field_71439_g.func_184587_cr() && HotbarRefill.mc.field_71439_g.field_191988_bg == 0.0f || !((Boolean)this.stop.getValue()).booleanValue()) {
            HotbarRefill.mc.field_71442_b.func_187098_a(0, inventorySlot, 0, ClickType.PICKUP, (EntityPlayer)HotbarRefill.mc.field_71439_g);
            HotbarRefill.mc.field_71442_b.func_187098_a(0, hotbarSlot + 36, 0, ClickType.PICKUP, (EntityPlayer)HotbarRefill.mc.field_71439_g);
            HotbarRefill.mc.field_71442_b.func_187098_a(0, inventorySlot, 0, ClickType.PICKUP, (EntityPlayer)HotbarRefill.mc.field_71439_g);
        }
    }

    private Pair<Integer, Integer> findReplenishableHotbarSlot() {
        NonNullList inventory = HotbarRefill.mc.field_71439_g.field_71071_by.field_70462_a;
        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            int inventorySlot;
            ItemStack stack = (ItemStack)inventory.get(hotbarSlot);
            if (!stack.func_77985_e() || stack.field_190928_g || stack.func_77973_b() == Items.field_190931_a || stack.field_77994_a >= stack.func_77976_d() || stack.field_77994_a > (Integer)this.threshold.getValue() || (inventorySlot = this.findCompatibleInventorySlot(stack)) == -1) continue;
            return new Pair<Integer, Integer>(inventorySlot, hotbarSlot);
        }
        return null;
    }

    private int findCompatibleInventorySlot(ItemStack hotbarStack) {
        Item item = hotbarStack.func_77973_b();
        List<Integer> potentialSlots = item instanceof ItemBlock ? InventoryUtil.findAllBlockSlots(((ItemBlock)item).func_179223_d().getClass()) : InventoryUtil.findAllItemSlots(item.getClass());
        potentialSlots = potentialSlots.stream().filter(integer -> integer > 8 && integer < 36).sorted(Comparator.comparingInt(interger -> -interger.intValue())).collect(Collectors.toList());
        for (int slot : potentialSlots) {
            if (!this.isCompatibleStacks(hotbarStack, HotbarRefill.mc.field_71439_g.field_71071_by.func_70301_a(slot))) continue;
            return slot;
        }
        return -1;
    }

    private boolean isCompatibleStacks(ItemStack stack1, ItemStack stack2) {
        if (!stack1.func_77973_b().equals(stack2.func_77973_b())) {
            return false;
        }
        if (stack1.func_77973_b() instanceof ItemBlock && stack2.func_77973_b() instanceof ItemBlock) {
            Block block1 = ((ItemBlock)stack1.func_77973_b()).func_179223_d();
            Block block2 = ((ItemBlock)stack2.func_77973_b()).func_179223_d();
            if (!block1.field_149764_J.equals(block2.field_149764_J)) {
                return false;
            }
        }
        if (!stack1.func_82833_r().equals(stack2.func_82833_r())) {
            return false;
        }
        return stack1.func_77952_i() == stack2.func_77952_i();
    }
}

