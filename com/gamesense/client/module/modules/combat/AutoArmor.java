/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.events.PlayerMoveEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.ElytraFly;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

@Module.Declaration(name="AutoArmor", category=Category.Combat)
public class AutoArmor
extends Module {
    ModeSetting allowElytra = this.registerMode("Elytra", Arrays.asList("Allow", "Only on ElytraFly", "Disallow"), "Only on ElytraFly");
    BooleanSetting noThorns = this.registerBoolean("No Thorns", false);
    BooleanSetting lastResortThorns = this.registerBoolean("No Other Thorns", false);
    BooleanSetting oneAtTime = this.registerBoolean("One At Time", false);
    BooleanSetting strict = this.registerBoolean("Strict", false);
    public boolean dontMove;
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
        if (this.dontMove) {
            event.setX(0.0);
            event.setZ(0.0);
            this.dontMove = false;
        }
    }, new Predicate[0]);

    @Override
    public void onUpdate() {
        if (AutoArmor.mc.field_71439_g.field_70173_aa % 2 == 0) {
            return;
        }
        if (AutoArmor.mc.field_71462_r instanceof GuiContainer && !(AutoArmor.mc.field_71462_r instanceof InventoryEffectRenderer)) {
            return;
        }
        NonNullList armorInventory = AutoArmor.mc.field_71439_g.field_71071_by.field_70460_b;
        NonNullList inventory = AutoArmor.mc.field_71439_g.field_71071_by.field_70462_a;
        int[] bestArmorSlots = new int[]{-1, -1, -1, -1};
        int[] bestArmorValues = new int[]{-1, -1, -1, -1};
        block2: for (int i = 0; i < 4; ++i) {
            ItemStack oldArmour = (ItemStack)armorInventory.get(i);
            if (oldArmour.func_77973_b() instanceof ItemArmor) {
                bestArmorValues[i] = ((ItemArmor)oldArmour.func_77973_b()).field_77879_b;
            }
            List<Integer> slots = InventoryUtil.findAllItemSlots(ItemArmor.class);
            HashMap<Integer, ItemStack> armour = new HashMap<Integer, ItemStack>();
            HashMap<Integer, ItemStack> thorns = new HashMap<Integer, ItemStack>();
            for (Integer slot : slots) {
                ItemStack item = (ItemStack)inventory.get(slot);
                if (((Boolean)this.noThorns.getValue()).booleanValue() && EnchantmentHelper.func_82781_a((ItemStack)item).containsKey(Enchantment.func_185262_c((int)7))) {
                    thorns.put(slot, item);
                    continue;
                }
                armour.put(slot, item);
            }
            armour.forEach((integer, itemStack) -> {
                ItemArmor itemArmor = (ItemArmor)itemStack.func_77973_b();
                int armorType = itemArmor.field_77881_a.ordinal() - 2;
                if (armorType == 2 && AutoArmor.mc.field_71439_g.field_71071_by.func_70440_f(armorType).func_77973_b().equals(Items.field_185160_cR)) {
                    return;
                }
                int armorValue = itemArmor.field_77879_b;
                if (armorValue > bestArmorValues[armorType]) {
                    bestArmorSlots[armorType] = integer;
                    bestArmorValues[armorType] = armorValue;
                }
            });
            if (((Boolean)this.noThorns.getValue()).booleanValue() && ((Boolean)this.lastResortThorns.getValue()).booleanValue()) {
                thorns.forEach((arg_0, arg_1) -> this.lambda$onUpdate$2((List)armorInventory, bestArmorSlots, bestArmorValues, arg_0, arg_1));
            }
            for (int j = 0; j < 4; ++j) {
                int slot = bestArmorSlots[j];
                if (slot == -1) continue;
                if (slot < 9) {
                    slot += 36;
                }
                if (((Boolean)this.strict.getValue()).booleanValue()) {
                    try {
                        this.dontMove = true;
                        AutoArmor.mc.field_71442_b.func_78766_c((EntityPlayer)AutoArmor.mc.field_71439_g);
                    }
                    catch (Exception ignored) {
                        this.dontMove = true;
                    }
                }
                AutoArmor.mc.field_71442_b.func_187098_a(0, slot, 0, ClickType.PICKUP, (EntityPlayer)AutoArmor.mc.field_71439_g);
                AutoArmor.mc.field_71442_b.func_187098_a(0, 8 - j, 0, ClickType.PICKUP, (EntityPlayer)AutoArmor.mc.field_71439_g);
                AutoArmor.mc.field_71442_b.func_187098_a(0, slot, 0, ClickType.PICKUP, (EntityPlayer)AutoArmor.mc.field_71439_g);
                if (((Boolean)this.oneAtTime.getValue()).booleanValue()) continue block2;
            }
        }
    }

    private /* synthetic */ void lambda$onUpdate$2(List armorInventory, int[] bestArmorSlots, int[] bestArmorValues, Integer integer, ItemStack itemStack) {
        ItemArmor itemArmor = (ItemArmor)itemStack.func_77973_b();
        int armorType = itemArmor.field_77881_a.ordinal() - 2;
        if (armorInventory.get(armorType) != ItemStack.field_190927_a || bestArmorSlots[armorType] != -1) {
            return;
        }
        if (armorType == 2 && AutoArmor.mc.field_71439_g.field_71071_by.func_70440_f(armorType).func_77973_b().equals(Items.field_185160_cR) && (((String)this.allowElytra.getValue()).equalsIgnoreCase("Allow") || ((String)this.allowElytra.getValue()).equalsIgnoreCase("Only on ElytraFly") && ModuleManager.isModuleEnabled(ElytraFly.class))) {
            return;
        }
        int armorValue = itemArmor.field_77879_b;
        if (armorValue > bestArmorValues[armorType]) {
            bestArmorSlots[armorType] = integer;
            bestArmorValues[armorType] = armorValue;
        }
    }
}

