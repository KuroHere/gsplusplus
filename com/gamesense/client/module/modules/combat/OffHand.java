/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.events.PlayerMoveEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.StringSetting;
import com.gamesense.api.util.misc.KeyBoardClass;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

@Module.Declaration(name="Offhand", category=Category.Combat)
public class OffHand
extends Module {
    BooleanSetting itemSection = this.registerBoolean("Item Section", true);
    ModeSetting defaultItem = this.registerMode("Default", Arrays.asList("Totem", "Crystal", "Gapple", "Plates", "Obby", "Pot", "Exp"), "Totem", () -> (Boolean)this.itemSection.getValue());
    ModeSetting nonDefaultItem = this.registerMode("Non Default", Arrays.asList("Totem", "Crystal", "Gapple", "Obby", "Pot", "Exp", "Plates", "String", "Skull"), "Crystal", () -> (Boolean)this.itemSection.getValue());
    ModeSetting noPlayerItem = this.registerMode("No Player", Arrays.asList("Totem", "Crystal", "Gapple", "Plates", "Obby", "Pot", "Exp"), "Gapple", () -> (Boolean)this.itemSection.getValue());
    ModeSetting potionChoose = this.registerMode("Potion", Arrays.asList("first", "strength", "swiftness"), "first", () -> (Boolean)this.itemSection.getValue());
    StringSetting bind = this.registerString("Bind", "");
    ModeSetting bindItem = this.registerMode("Bind Item", Arrays.asList("Totem", "Crystal", "Gapple", "Obby", "Pot", "Exp", "Plates", "String", "Skull"), "Crystal", () -> this.bind.getText().length() > 0);
    BooleanSetting switchSection = this.registerBoolean("Switch Section", true);
    IntegerSetting healthSwitch = this.registerInteger("Health Switch", 14, 0, 36, () -> (Boolean)this.switchSection.getValue());
    IntegerSetting tickDelay = this.registerInteger("Tick Delay", 0, 0, 20, () -> (Boolean)this.switchSection.getValue());
    BooleanSetting fallDistanceBol = this.registerBoolean("Fall Distance", true, () -> (Boolean)this.switchSection.getValue());
    IntegerSetting fallDistance = this.registerInteger("Fall Distance", 12, 0, 30, () -> (Boolean)this.switchSection.getValue() != false && (Boolean)this.fallDistanceBol.getValue() != false);
    IntegerSetting maxSwitchPerSecond = this.registerInteger("Max Switch", 6, 2, 10, () -> (Boolean)this.switchSection.getValue());
    DoubleSetting playerDistance = this.registerDouble("Player Distance", 0.0, 0.0, 30.0, () -> (Boolean)this.switchSection.getValue());
    public BooleanSetting strict = this.registerBoolean("Strict", true, () -> (Boolean)this.switchSection.getValue());
    BooleanSetting miscSection = this.registerBoolean("Misc Section", true);
    BooleanSetting pickObby = this.registerBoolean("Pick Obby", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting pickObbyShift = this.registerBoolean("Pick Obby On Shift", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting crystObby = this.registerBoolean("Cryst Shift Obby", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting rightGap = this.registerBoolean("Right Click Gap", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting shiftPot = this.registerBoolean("Shift Pot", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting swordCheck = this.registerBoolean("Only Sword", true, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting swordCrystal = this.registerBoolean("Sword Crystal", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting pickCrystal = this.registerBoolean("Pick Crystal", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting crystalCheck = this.registerBoolean("Crystal Check", false, () -> (Boolean)this.switchSection.getValue());
    BooleanSetting onlyHotBar = this.registerBoolean("Only HotBar", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting antiWeakness = this.registerBoolean("AntiWeakness", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting hotBarTotem = this.registerBoolean("HotBar Totem", false, () -> (Boolean)this.miscSection.getValue());
    IntegerSetting startingBiasDamage = this.registerInteger("Bias Health", 22, 0, 36, () -> (Boolean)this.switchSection.getValue() != false && (Boolean)this.crystalCheck.getValue() != false);
    DoubleSetting biasDamage = this.registerDouble("Bias Damage", 1.0, 0.0, 3.0, () -> (Boolean)this.switchSection.getValue() != false && (Boolean)this.crystalCheck.getValue() != false);
    BooleanSetting noHotBar = this.registerBoolean("No HotBar", false, () -> (Boolean)this.miscSection.getValue());
    BooleanSetting autoRefill = this.registerBoolean("Auto Refill", false, () -> (Boolean)this.miscSection.getValue() != false && (Boolean)this.noHotBar.getValue() != false);
    int prevSlot;
    int tickWaited;
    int totems;
    boolean returnBack;
    boolean firstChange;
    public boolean stepChanging;
    public boolean dontMove;
    private static String forceItem;
    private final ArrayList<Long> switchDone = new ArrayList();
    private final ArrayList<Item> ignoreNoSword = new ArrayList<Item>(){
        {
            this.add(Items.field_151153_ao);
            this.add(Items.field_151062_by);
            this.add(Items.field_151031_f);
            this.add(Items.field_151068_bn);
        }
    };
    Map<String, Item> allowedItemsItem = new HashMap<String, Item>(){
        {
            this.put("Totem", Items.field_190929_cY);
            this.put("Crystal", Items.field_185158_cP);
            this.put("Gapple", Items.field_151153_ao);
            this.put("Pot", Items.field_151068_bn);
            this.put("Exp", Items.field_151062_by);
            this.put("String", Items.field_151007_F);
        }
    };
    Map<String, Block> allowedItemsBlock = new HashMap<String, Block>(){
        {
            this.put("Plates", Blocks.field_150452_aw);
            this.put("Skull", Blocks.field_150465_bP);
            this.put("Obby", Blocks.field_150343_Z);
            this.put("Anvil", Blocks.field_150467_bQ);
            this.put("EChest", Blocks.field_150477_bB);
        }
    };
    boolean pressedBind = false;
    boolean isPressing = false;
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
        if (this.dontMove) {
            event.setX(0.0);
            event.setZ(0.0);
            this.dontMove = false;
        }
    }, new Predicate[0]);

    public static void requestItems(int want) {
        switch (want) {
            case 0: {
                forceItem = "Obby";
                break;
            }
            case 1: {
                forceItem = "Skull";
                break;
            }
            case 2: {
                forceItem = "EChest";
            }
        }
    }

    public static void removeItem(int want) {
        String check = "";
        switch (want) {
            case 0: {
                check = "Obby";
                break;
            }
            case 1: {
                check = "Skull";
                break;
            }
            case 2: {
                check = "EChest";
            }
        }
        if (forceItem.equals(check)) {
            forceItem = "";
        }
    }

    @Override
    public void onEnable() {
        this.firstChange = true;
        forceItem = "";
        this.pressedBind = false;
        this.isPressing = false;
        this.returnBack = false;
    }

    @Override
    public void onDisable() {
        forceItem = "";
    }

    @Override
    public void onUpdate() {
        String itemCheck;
        if (OffHand.mc.field_71462_r instanceof GuiContainer && !(OffHand.mc.field_71462_r instanceof GuiInventory)) {
            return;
        }
        if (this.stepChanging) {
            if (this.tickWaited++ >= (Integer)this.tickDelay.getValue()) {
                if (((Boolean)this.strict.getValue()).booleanValue()) {
                    try {
                        this.dontMove = true;
                        OffHand.mc.field_71442_b.func_78766_c((EntityPlayer)OffHand.mc.field_71439_g);
                    }
                    catch (Exception ignored) {
                        this.dontMove = true;
                    }
                }
                this.tickWaited = 0;
                this.stepChanging = false;
                OffHand.mc.field_71442_b.func_187098_a(0, 45, 0, ClickType.PICKUP, (EntityPlayer)OffHand.mc.field_71439_g);
                this.switchDone.add(System.currentTimeMillis());
            } else {
                return;
            }
        }
        this.totems = OffHand.mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> itemStack.func_77973_b() == Items.field_190929_cY).mapToInt(ItemStack::func_190916_E).sum();
        if (this.returnBack) {
            if (this.tickWaited++ >= (Integer)this.tickDelay.getValue()) {
                this.changeBack();
            } else {
                return;
            }
        }
        if (this.offHandSame(itemCheck = this.getItem())) {
            boolean done = false;
            if (((Boolean)this.hotBarTotem.getValue()).booleanValue() && itemCheck.equals("Totem")) {
                done = this.switchItemTotemHot();
            }
            if (!done) {
                this.switchItemNormal(itemCheck);
            }
        }
    }

    private void changeBack() {
        if (this.prevSlot == -1 || !OffHand.mc.field_71439_g.field_71071_by.func_70301_a(this.prevSlot).func_190926_b()) {
            this.prevSlot = this.findEmptySlot();
        }
        if (this.prevSlot != -1) {
            OffHand.mc.field_71442_b.func_187098_a(0, this.prevSlot < 9 ? this.prevSlot + 36 : this.prevSlot, 0, ClickType.PICKUP, (EntityPlayer)OffHand.mc.field_71439_g);
        } else {
            PistonCrystal.printDebug("Your inventory is full. the item that was on your offhand is going to be dropped. Open your inventory and choose where to put it", true);
        }
        this.returnBack = false;
        this.tickWaited = 0;
    }

    private boolean switchItemTotemHot() {
        int slot = InventoryUtil.findTotemSlot(0, 8);
        if (slot != -1) {
            if (OffHand.mc.field_71439_g.field_71071_by.field_70461_c != slot) {
                OffHand.mc.field_71439_g.field_71071_by.field_70461_c = slot;
            }
            return true;
        }
        return false;
    }

    private void switchItemNormal(String itemCheck) {
        int t = this.getInventorySlot(itemCheck);
        if (t == -1) {
            return;
        }
        if (!itemCheck.equals("Totem") && this.canSwitch()) {
            return;
        }
        this.toOffHand(t);
    }

    private String getItem() {
        String itemCheck = "";
        boolean normalOffHand = true;
        if ((Boolean)this.fallDistanceBol.getValue() != false && OffHand.mc.field_71439_g.field_70143_R >= (float)((Integer)this.fallDistance.getValue()).intValue() && OffHand.mc.field_71439_g.field_70167_r != OffHand.mc.field_71439_g.field_70163_u && !OffHand.mc.field_71439_g.func_184613_cA() || ((Boolean)this.crystalCheck.getValue()).booleanValue() && this.crystalDamage()) {
            normalOffHand = false;
            itemCheck = "Totem";
        }
        if (!forceItem.equals("")) {
            itemCheck = forceItem;
            normalOffHand = false;
        }
        if (this.bind.getText().length() > 0) {
            if (Keyboard.isKeyDown((int)KeyBoardClass.getKeyFromChar(this.bind.getText().charAt(0)))) {
                if (!this.isPressing) {
                    this.isPressing = true;
                    this.pressedBind = !this.pressedBind;
                }
            } else if (this.isPressing) {
                this.isPressing = false;
            }
        }
        if (this.pressedBind) {
            itemCheck = (String)this.bindItem.getValue();
        }
        Item mainHandItem = OffHand.mc.field_71439_g.func_184614_ca().func_77973_b();
        if (normalOffHand && (((Boolean)this.crystObby.getValue()).booleanValue() && OffHand.mc.field_71474_y.field_74311_E.func_151470_d() && mainHandItem == Items.field_185158_cP || ((Boolean)this.pickObby.getValue()).booleanValue() && mainHandItem == Items.field_151046_w && (!((Boolean)this.pickObbyShift.getValue()).booleanValue() || OffHand.mc.field_71474_y.field_74311_E.func_151470_d()))) {
            itemCheck = "Obby";
            normalOffHand = false;
        }
        if (((Boolean)this.swordCrystal.getValue()).booleanValue() && mainHandItem == Items.field_151048_u) {
            itemCheck = "Crystal";
            normalOffHand = false;
        }
        if (((Boolean)this.pickCrystal.getValue()).booleanValue() && mainHandItem == Items.field_151046_w) {
            itemCheck = "Crystal";
            normalOffHand = false;
        }
        if (normalOffHand && OffHand.mc.field_71474_y.field_74313_G.func_151470_d() && (!((Boolean)this.swordCheck.getValue()).booleanValue() || mainHandItem == Items.field_151048_u)) {
            if (OffHand.mc.field_71474_y.field_74311_E.func_151470_d()) {
                if (((Boolean)this.shiftPot.getValue()).booleanValue()) {
                    itemCheck = "Pot";
                    normalOffHand = false;
                }
            } else if (((Boolean)this.rightGap.getValue()).booleanValue() && !this.ignoreNoSword.contains(mainHandItem)) {
                itemCheck = "Gapple";
                normalOffHand = false;
            }
        }
        if (normalOffHand && ((Boolean)this.antiWeakness.getValue()).booleanValue() && OffHand.mc.field_71439_g.func_70644_a(MobEffects.field_76437_t)) {
            normalOffHand = false;
            itemCheck = "Crystal";
        }
        if (normalOffHand && !this.nearPlayer()) {
            normalOffHand = false;
            itemCheck = (String)this.noPlayerItem.getValue();
        }
        itemCheck = this.getItemToCheck(itemCheck);
        return itemCheck;
    }

    private boolean canSwitch() {
        long now = System.currentTimeMillis();
        for (int i = 0; i < this.switchDone.size() && now - this.switchDone.get(i) > 1000L; ++i) {
            this.switchDone.remove(i);
        }
        if (this.switchDone.size() / 2 >= (Integer)this.maxSwitchPerSecond.getValue()) {
            return true;
        }
        this.switchDone.add(now);
        return false;
    }

    private boolean nearPlayer() {
        if (((Double)this.playerDistance.getValue()).intValue() == 0) {
            return true;
        }
        for (EntityPlayer pl : OffHand.mc.field_71441_e.field_73010_i) {
            if (pl == OffHand.mc.field_71439_g || !((double)OffHand.mc.field_71439_g.func_70032_d((Entity)pl) < (Double)this.playerDistance.getValue())) continue;
            return true;
        }
        return false;
    }

    private boolean crystalDamage() {
        if (PlayerUtil.getHealth() <= (float)((Integer)this.startingBiasDamage.getValue()).intValue()) {
            for (Entity t : OffHand.mc.field_71441_e.field_72996_f) {
                if (!(t instanceof EntityEnderCrystal) || !(OffHand.mc.field_71439_g.func_70032_d(t) <= 12.0f) || !((double)DamageUtil.calculateDamage(t.field_70165_t, t.field_70163_u, t.field_70161_v, (Entity)OffHand.mc.field_71439_g, false) * (Double)this.biasDamage.getValue() >= (double)PlayerUtil.getHealth())) continue;
                return true;
            }
        }
        return false;
    }

    private int findEmptySlot() {
        for (int i = 35; i > -1; --i) {
            if (!OffHand.mc.field_71439_g.field_71071_by.func_70301_a(i).func_190926_b()) continue;
            return i;
        }
        return -1;
    }

    private boolean offHandSame(String itemCheck) {
        Item offHandItem = OffHand.mc.field_71439_g.func_184592_cb().func_77973_b();
        if (this.allowedItemsBlock.containsKey(itemCheck)) {
            Block item = this.allowedItemsBlock.get(itemCheck);
            if (offHandItem instanceof ItemBlock) {
                return ((ItemBlock)offHandItem).func_179223_d() != item;
            }
            if (offHandItem instanceof ItemSkull && item == Blocks.field_150465_bP) {
                return true;
            }
        } else {
            Item item = this.allowedItemsItem.get(itemCheck);
            return item != offHandItem;
        }
        return true;
    }

    private String getItemToCheck(String str) {
        return PlayerUtil.getHealth() > (float)((Integer)this.healthSwitch.getValue()).intValue() ? (str.equals("") ? (String)this.nonDefaultItem.getValue() : str) : (String)this.defaultItem.getValue();
    }

    private int getInventorySlot(String itemName) {
        int res;
        Item item;
        boolean blockBool = false;
        if (this.allowedItemsItem.containsKey(itemName)) {
            item = this.allowedItemsItem.get(itemName);
        } else {
            item = this.allowedItemsBlock.get(itemName);
            blockBool = true;
        }
        if (!this.firstChange && this.prevSlot != -1 && (res = this.isCorrect(this.prevSlot, blockBool, item, itemName)) != -1) {
            return res;
        }
        for (int i = (Boolean)this.onlyHotBar.getValue() != false ? 8 : 35; i > ((Boolean)this.noHotBar.getValue() != false ? 9 : -1); --i) {
            res = this.isCorrect(i, blockBool, item, itemName);
            if (res == -1) continue;
            return res;
        }
        return -1;
    }

    private int isCorrect(int i, boolean blockBool, Object item, String itemName) {
        Item temp = OffHand.mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
        if (blockBool) {
            if (temp instanceof ItemBlock ? ((ItemBlock)temp).func_179223_d() == item : temp instanceof ItemSkull && item == Blocks.field_150465_bP) {
                return i;
            }
        } else if (item == temp) {
            if (itemName.equals("Pot") && !((String)this.potionChoose.getValue()).equalsIgnoreCase("first") && !OffHand.mc.field_71439_g.field_71071_by.func_70301_a((int)i).field_77990_d.toString().split(":")[2].contains((CharSequence)this.potionChoose.getValue())) {
                return -1;
            }
            return i;
        }
        return -1;
    }

    private void toOffHand(int t) {
        if (!OffHand.mc.field_71439_g.func_184592_cb().func_190926_b()) {
            if (this.firstChange) {
                this.prevSlot = t;
            }
            this.returnBack = true;
            this.firstChange = !this.firstChange;
        } else {
            this.prevSlot = -1;
        }
        OffHand.mc.field_71442_b.func_187098_a(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, (EntityPlayer)OffHand.mc.field_71439_g);
        this.stepChanging = true;
        this.tickWaited = 0;
    }

    @Override
    public String getHudInfo() {
        return "[" + ChatFormatting.WHITE + this.totems + ChatFormatting.GRAY + "]";
    }
}

