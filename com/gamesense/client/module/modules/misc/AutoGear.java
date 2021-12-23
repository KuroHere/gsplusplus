/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.command.commands.AutoGearCommand;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.item.ItemStack;

@Module.Declaration(name="AutoGear", category=Category.Misc)
public class AutoGear
extends Module {
    IntegerSetting tickDelay = this.registerInteger("Tick Delay", 0, 0, 20);
    IntegerSetting switchForTick = this.registerInteger("Switch Per Tick", 1, 1, 100);
    BooleanSetting enderChest = this.registerBoolean("EnderChest", false);
    BooleanSetting confirmSort = this.registerBoolean("Confirm Sort", true);
    BooleanSetting invasive = this.registerBoolean("Invasive", false);
    BooleanSetting closeAfter = this.registerBoolean("Close After", false);
    BooleanSetting infoMsgs = this.registerBoolean("Info Msgs", true);
    BooleanSetting debugMode = this.registerBoolean("Debug Mode", false);
    private HashMap<Integer, String> planInventory = new HashMap();
    private final HashMap<Integer, String> containerInv = new HashMap();
    private ArrayList<Integer> sortItems = new ArrayList();
    private int delayTimeTicks;
    private int stepNow;
    private boolean openedBefore;
    private boolean finishSort;
    private boolean doneBefore;

    @Override
    public void onEnable() {
        String inventoryConfig;
        String curConfigName = AutoGearCommand.getCurrentSet();
        if (curConfigName.equals("")) {
            this.disable();
            return;
        }
        if (((Boolean)this.infoMsgs.getValue()).booleanValue()) {
            PistonCrystal.printDebug("Config " + curConfigName + " actived", false);
        }
        if ((inventoryConfig = AutoGearCommand.getInventoryKit(curConfigName)).equals("")) {
            this.disable();
            return;
        }
        String[] inventoryDivided = inventoryConfig.split(" ");
        this.planInventory = new HashMap();
        HashMap<String, Integer> nItems = new HashMap<String, Integer>();
        for (int i = 0; i < inventoryDivided.length; ++i) {
            if (inventoryDivided[i].contains("air")) continue;
            this.planInventory.put(i, inventoryDivided[i]);
            if (nItems.containsKey(inventoryDivided[i])) {
                nItems.put(inventoryDivided[i], (Integer)nItems.get(inventoryDivided[i]) + 1);
                continue;
            }
            nItems.put(inventoryDivided[i], 1);
        }
        this.delayTimeTicks = 0;
        this.doneBefore = false;
        this.openedBefore = false;
    }

    @Override
    public void onDisable() {
        if (((Boolean)this.infoMsgs.getValue()).booleanValue() && this.planInventory.size() > 0) {
            PistonCrystal.printDebug("AutoSort Turned Off!", true);
        }
    }

    @Override
    public void onUpdate() {
        if (this.delayTimeTicks < (Integer)this.tickDelay.getValue()) {
            ++this.delayTimeTicks;
            return;
        }
        this.delayTimeTicks = 0;
        if (this.planInventory.size() == 0) {
            this.disable();
        }
        if (AutoGear.mc.field_71439_g.field_71070_bA instanceof ContainerChest && (((Boolean)this.enderChest.getValue()).booleanValue() || !((ContainerChest)AutoGear.mc.field_71439_g.field_71070_bA).func_85151_d().func_145748_c_().func_150260_c().equals("Ender Chest")) || AutoGear.mc.field_71439_g.field_71070_bA instanceof ContainerShulkerBox) {
            this.sortInventoryAlgo();
        } else {
            this.openedBefore = false;
        }
    }

    private void sortInventoryAlgo() {
        if (!this.openedBefore) {
            if (((Boolean)this.infoMsgs.getValue()).booleanValue() && !this.doneBefore) {
                PistonCrystal.printDebug("Start sorting inventory...", false);
            }
            int maxValue = AutoGear.mc.field_71439_g.field_71070_bA instanceof ContainerChest ? ((ContainerChest)AutoGear.mc.field_71439_g.field_71070_bA).func_85151_d().func_70302_i_() : 27;
            for (int i = 0; i < maxValue; ++i) {
                ItemStack item = (ItemStack)AutoGear.mc.field_71439_g.field_71070_bA.func_75138_a().get(i);
                this.containerInv.put(i, Objects.requireNonNull(item.func_77973_b().getRegistryName()).toString() + item.func_77960_j());
            }
            this.openedBefore = true;
            HashMap<Integer, String> inventoryCopy = this.getInventoryCopy(maxValue);
            HashMap<Integer, String> aimInventory = this.getInventoryCopy(maxValue, this.planInventory);
            this.sortItems = this.getInventorySort(inventoryCopy, aimInventory, maxValue);
            if (this.sortItems.size() == 0 && !this.doneBefore) {
                this.finishSort = false;
                if (((Boolean)this.infoMsgs.getValue()).booleanValue()) {
                    PistonCrystal.printDebug("Inventory already sorted...", true);
                }
                if (((Boolean)this.closeAfter.getValue()).booleanValue()) {
                    AutoGear.mc.field_71439_g.func_71053_j();
                }
            } else {
                this.finishSort = true;
                this.stepNow = 0;
            }
            this.openedBefore = true;
        } else if (this.finishSort) {
            for (int i = 0; i < (Integer)this.switchForTick.getValue(); ++i) {
                if (this.sortItems.size() != 0) {
                    int slotChange = this.sortItems.get(this.stepNow++);
                    AutoGear.mc.field_71442_b.func_187098_a(AutoGear.mc.field_71439_g.field_71070_bA.field_75152_c, slotChange, 0, ClickType.PICKUP, (EntityPlayer)AutoGear.mc.field_71439_g);
                }
                if (this.stepNow != this.sortItems.size()) continue;
                if (((Boolean)this.confirmSort.getValue()).booleanValue() && !this.doneBefore) {
                    this.openedBefore = false;
                    this.finishSort = false;
                    this.doneBefore = true;
                    this.checkLastItem();
                    return;
                }
                this.finishSort = false;
                if (((Boolean)this.infoMsgs.getValue()).booleanValue()) {
                    PistonCrystal.printDebug("Inventory sorted", false);
                }
                this.checkLastItem();
                this.doneBefore = false;
                if (((Boolean)this.closeAfter.getValue()).booleanValue()) {
                    AutoGear.mc.field_71439_g.func_71053_j();
                }
                return;
            }
        }
    }

    private void checkLastItem() {
        if (this.sortItems.size() != 0) {
            int slotChange = this.sortItems.get(this.sortItems.size() - 1);
            if (((ItemStack)AutoGear.mc.field_71439_g.field_71070_bA.func_75138_a().get(slotChange)).func_190926_b()) {
                AutoGear.mc.field_71442_b.func_187098_a(0, slotChange, 0, ClickType.PICKUP, (EntityPlayer)AutoGear.mc.field_71439_g);
            }
        }
    }

    private ArrayList<Integer> getInventorySort(HashMap<Integer, String> copyInventory, HashMap<Integer, String> planInventoryCopy, int startValues) {
        ArrayList<Integer> planMove = new ArrayList<Integer>();
        HashMap<String, Integer> nItemsCopy = new HashMap<String, Integer>();
        for (String value : planInventoryCopy.values()) {
            if (nItemsCopy.containsKey(value)) {
                nItemsCopy.put(value, (Integer)nItemsCopy.get(value) + 1);
                continue;
            }
            nItemsCopy.put(value, 1);
        }
        ArrayList<Integer> ignoreValues = new ArrayList<Integer>();
        int[] listValue = new int[planInventoryCopy.size()];
        int id = 0;
        Iterator<Integer> iterator = planInventoryCopy.keySet().iterator();
        while (iterator.hasNext()) {
            int idx = iterator.next();
            listValue[id++] = idx;
        }
        for (Object item : (Iterator<Integer>)listValue) {
            if (!copyInventory.get((int)item).equals(planInventoryCopy.get((int)item))) continue;
            ignoreValues.add((int)item);
            nItemsCopy.put(planInventoryCopy.get((int)item), (Integer)nItemsCopy.get(planInventoryCopy.get((int)item)) - 1);
            if ((Integer)nItemsCopy.get(planInventoryCopy.get((int)item)) == 0) {
                nItemsCopy.remove(planInventoryCopy.get((int)item));
            }
            planInventoryCopy.remove((int)item);
        }
        String pickedItem = null;
        for (int i = startValues; i < startValues + copyInventory.size(); ++i) {
            if (ignoreValues.contains(i)) continue;
            String itemCheck = copyInventory.get(i);
            Optional<Map.Entry> momentAim = planInventoryCopy.entrySet().stream().filter(x -> ((String)x.getValue()).equals(itemCheck)).findFirst();
            if (momentAim.isPresent()) {
                if (pickedItem == null) {
                    planMove.add(i);
                }
                int aimKey = (Integer)momentAim.get().getKey();
                planMove.add(aimKey);
                if (pickedItem == null || !pickedItem.equals(itemCheck)) {
                    ignoreValues.add(aimKey);
                }
                nItemsCopy.put(itemCheck, (Integer)nItemsCopy.get(itemCheck) - 1);
                if ((Integer)nItemsCopy.get(itemCheck) == 0) {
                    nItemsCopy.remove(itemCheck);
                }
                copyInventory.put(i, copyInventory.get(aimKey));
                copyInventory.put(aimKey, itemCheck);
                if (!copyInventory.get(aimKey).equals("minecraft:air0")) {
                    if (i >= startValues + copyInventory.size()) continue;
                    pickedItem = copyInventory.get(i);
                    --i;
                } else {
                    pickedItem = null;
                }
                planInventoryCopy.remove(aimKey);
                continue;
            }
            if (pickedItem == null) continue;
            planMove.add(i);
            copyInventory.put(i, pickedItem);
            pickedItem = null;
        }
        if (planMove.size() != 0 && ((Integer)planMove.get(planMove.size() - 1)).equals(planMove.get(planMove.size() - 2))) {
            planMove.remove(planMove.size() - 1);
        }
        Object[] keyList = this.containerInv.keySet().toArray();
        for (int values = 0; values < keyList.length; ++values) {
            int itemC = (Integer)keyList[values];
            if (!nItemsCopy.containsKey(this.containerInv.get(itemC))) continue;
            int start = (Integer)planInventoryCopy.entrySet().stream().filter(x -> ((String)x.getValue()).equals(this.containerInv.get(itemC))).findFirst().get().getKey();
            if (!((Boolean)this.invasive.getValue()).booleanValue() && !((ItemStack)AutoGear.mc.field_71439_g.field_71070_bA.func_75138_a().get(start)).func_190926_b()) continue;
            planMove.add(start);
            planMove.add(itemC);
            planMove.add(start);
            nItemsCopy.put(planInventoryCopy.get(start), (Integer)nItemsCopy.get(planInventoryCopy.get(start)) - 1);
            if ((Integer)nItemsCopy.get(planInventoryCopy.get(start)) == 0) {
                nItemsCopy.remove(planInventoryCopy.get(start));
            }
            planInventoryCopy.remove(start);
        }
        if (((Boolean)this.debugMode.getValue()).booleanValue()) {
            Iterator iterator2 = planMove.iterator();
            while (iterator2.hasNext()) {
                int valuePath = (Integer)iterator2.next();
                PistonCrystal.printDebug(Integer.toString(valuePath), false);
            }
        }
        return planMove;
    }

    private HashMap<Integer, String> getInventoryCopy(int startPoint) {
        HashMap<Integer, String> output = new HashMap<Integer, String>();
        int sizeInventory = AutoGear.mc.field_71439_g.field_71071_by.field_70462_a.size();
        for (int i = 0; i < sizeInventory; ++i) {
            int value = i + startPoint + (i < 9 ? sizeInventory - 9 : -9);
            ItemStack item = (ItemStack)AutoGear.mc.field_71439_g.field_71070_bA.func_75138_a().get(value);
            output.put(value, Objects.requireNonNull(item.func_77973_b().getRegistryName()).toString() + item.func_77960_j());
        }
        return output;
    }

    private HashMap<Integer, String> getInventoryCopy(int startPoint, HashMap<Integer, String> inventory) {
        HashMap<Integer, String> output = new HashMap<Integer, String>();
        int sizeInventory = AutoGear.mc.field_71439_g.field_71071_by.field_70462_a.size();
        Iterator<Integer> iterator = inventory.keySet().iterator();
        while (iterator.hasNext()) {
            int val;
            output.put(val + startPoint + ((val = iterator.next().intValue()) < 9 ? sizeInventory - 9 : -9), inventory.get(val));
        }
        return output;
    }
}

