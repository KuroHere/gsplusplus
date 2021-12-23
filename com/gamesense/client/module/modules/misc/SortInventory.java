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
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

@Module.Declaration(name="SortInventory", category=Category.Misc)
public class SortInventory
extends Module {
    IntegerSetting tickDelay = this.registerInteger("Tick Delay", 0, 0, 20);
    IntegerSetting switchForTick = this.registerInteger("Switch Per Tick", 1, 1, 100);
    BooleanSetting confirmSort = this.registerBoolean("Confirm Sort", true);
    BooleanSetting instaSort = this.registerBoolean("Insta Sort", false);
    BooleanSetting closeAfter = this.registerBoolean("Close After", false);
    BooleanSetting infoMsgs = this.registerBoolean("Info Msgs", true);
    BooleanSetting finishCheck = this.registerBoolean("Finish Check", true);
    BooleanSetting debugMode = this.registerBoolean("Debug Mode", false);
    private HashMap<Integer, String> planInventory = new HashMap();
    private HashMap<String, Integer> nItems = new HashMap();
    private ArrayList<Integer> sortItems = new ArrayList();
    private int delayTimeTicks;
    private int stepNow;
    private boolean openedBefore;
    private boolean finishSort;
    private boolean doneBefore;
    private boolean lastCheck = false;
    private int lastItem = -1;

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
        this.nItems = new HashMap();
        for (int i = 0; i < inventoryDivided.length; ++i) {
            if (inventoryDivided[i].contains("air")) continue;
            this.planInventory.put(i, inventoryDivided[i]);
            if (this.nItems.containsKey(inventoryDivided[i])) {
                this.nItems.put(inventoryDivided[i], this.nItems.get(inventoryDivided[i]) + 1);
                continue;
            }
            this.nItems.put(inventoryDivided[i], 1);
        }
        this.delayTimeTicks = 0;
        this.doneBefore = false;
        this.openedBefore = false;
        if (((Boolean)this.instaSort.getValue()).booleanValue()) {
            mc.func_147108_a((GuiScreen)new GuiInventory((EntityPlayer)SortInventory.mc.field_71439_g));
        }
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
        if (((Boolean)this.finishCheck.getValue()).booleanValue() && this.lastCheck) {
            if (this.lastItem != -1 && SortInventory.mc.field_71439_g.field_71071_by.func_70301_a(this.lastItem).func_190926_b()) {
                SortInventory.mc.field_71442_b.func_187098_a(0, this.lastItem < 9 ? this.lastItem + 36 : this.lastItem, 0, ClickType.PICKUP, (EntityPlayer)SortInventory.mc.field_71439_g);
            }
            this.lastCheck = false;
        }
        if (this.planInventory.size() == 0) {
            this.disable();
        }
        if (SortInventory.mc.field_71462_r instanceof GuiInventory) {
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
            this.sortItems = this.getInventorySort();
            if (this.sortItems.size() == 0 && !this.doneBefore) {
                this.finishSort = false;
                if (((Boolean)this.infoMsgs.getValue()).booleanValue()) {
                    PistonCrystal.printDebug("Inventory arleady sorted...", true);
                }
                if (((Boolean)this.instaSort.getValue()).booleanValue() || ((Boolean)this.closeAfter.getValue()).booleanValue()) {
                    SortInventory.mc.field_71439_g.func_71053_j();
                    if (((Boolean)this.instaSort.getValue()).booleanValue()) {
                        this.disable();
                    }
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
                    SortInventory.mc.field_71442_b.func_187098_a(0, slotChange < 9 ? slotChange + 36 : slotChange, 0, ClickType.PICKUP, (EntityPlayer)SortInventory.mc.field_71439_g);
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
                if (((Boolean)this.instaSort.getValue()).booleanValue() || ((Boolean)this.closeAfter.getValue()).booleanValue()) {
                    SortInventory.mc.field_71439_g.func_71053_j();
                    if (((Boolean)this.instaSort.getValue()).booleanValue()) {
                        this.disable();
                    }
                }
                return;
            }
        }
    }

    private void checkLastItem() {
        if (this.sortItems.size() != 0) {
            int slotChange = this.sortItems.get(this.sortItems.size() - 1);
            if (SortInventory.mc.field_71439_g.field_71071_by.func_70301_a(slotChange).func_190926_b()) {
                SortInventory.mc.field_71442_b.func_187098_a(0, slotChange < 9 ? slotChange + 36 : slotChange, 0, ClickType.PICKUP, (EntityPlayer)SortInventory.mc.field_71439_g);
            }
            this.lastItem = slotChange;
            this.lastCheck = true;
        }
    }

    private ArrayList<Integer> getInventorySort() {
        ArrayList<Integer> planMove = new ArrayList<Integer>();
        ArrayList<String> copyInventory = this.getInventoryCopy();
        HashMap planInventoryCopy = (HashMap)this.planInventory.clone();
        HashMap nItemsCopy = (HashMap)this.nItems.clone();
        ArrayList<Integer> ignoreValues = new ArrayList<Integer>();
        for (int i = 0; i < this.planInventory.size(); ++i) {
            int value = (Integer)this.planInventory.keySet().toArray()[i];
            if (!copyInventory.get(value).equals(planInventoryCopy.get(value))) continue;
            ignoreValues.add(value);
            nItemsCopy.put(planInventoryCopy.get(value), (Integer)nItemsCopy.get(planInventoryCopy.get(value)) - 1);
            if ((Integer)nItemsCopy.get(planInventoryCopy.get(value)) == 0) {
                nItemsCopy.remove(planInventoryCopy.get(value));
            }
            planInventoryCopy.remove(value);
        }
        String pickedItem = null;
        for (int i = 0; i < copyInventory.size(); ++i) {
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
                copyInventory.set(i, copyInventory.get(aimKey));
                copyInventory.set(aimKey, itemCheck);
                if (!copyInventory.get(aimKey).equals("minecraft:air0")) {
                    if (i >= copyInventory.size()) continue;
                    pickedItem = copyInventory.get(i);
                    --i;
                } else {
                    pickedItem = null;
                }
                planInventoryCopy.remove(aimKey);
                continue;
            }
            if (pickedItem == null) continue;
            if (planMove.get(planMove.size() - 1) != i) {
                planMove.add(i);
                copyInventory.set(i, pickedItem);
            }
            pickedItem = null;
        }
        if (planMove.size() != 0 && ((Integer)planMove.get(planMove.size() - 1)).equals(planMove.get(planMove.size() - 2))) {
            planMove.remove(planMove.size() - 1);
        }
        if (((Boolean)this.debugMode.getValue()).booleanValue()) {
            Iterator iterator = planMove.iterator();
            while (iterator.hasNext()) {
                int valuePath = (Integer)iterator.next();
                PistonCrystal.printDebug(Integer.toString(valuePath), false);
            }
        }
        return planMove;
    }

    private ArrayList<String> getInventoryCopy() {
        ArrayList<String> output = new ArrayList<String>();
        for (ItemStack i : SortInventory.mc.field_71439_g.field_71071_by.field_70462_a) {
            output.add(Objects.requireNonNull(i.func_77973_b().getRegistryName()).toString() + i.func_77960_j());
        }
        return output;
    }
}

