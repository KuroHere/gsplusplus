/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.OffHand;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(name="HoleFill", category=Category.Combat)
public class HoleFill
extends Module {
    ModeSetting mode = this.registerMode("Type", Arrays.asList("Obby", "Echest", "Both", "Web", "Plate", "Skull"), "Obby");
    IntegerSetting placeDelay = this.registerInteger("Delay", 2, 0, 10);
    IntegerSetting retryDelay = this.registerInteger("Retry Delay", 10, 0, 50);
    IntegerSetting bpc = this.registerInteger("Block pre Cycle", 2, 1, 5);
    DoubleSetting range = this.registerDouble("Range", 4.0, 0.0, 10.0);
    DoubleSetting playerRange = this.registerDouble("Player Range", 3.0, 1.0, 6.0);
    BooleanSetting onlyPlayer = this.registerBoolean("Only Player", false);
    BooleanSetting rotate = this.registerBoolean("Rotate", true);
    BooleanSetting autoSwitch = this.registerBoolean("Switch", true);
    BooleanSetting offHandObby = this.registerBoolean("Off Hand Obby", false);
    BooleanSetting disableOnFinish = this.registerBoolean("Disable on Finish", true);
    BooleanSetting SilentSwitch = this.registerBoolean("Silent Switch", false);
    BooleanSetting doubleHole = this.registerBoolean("Double Hole", false);
    private int delayTicks = 0;
    private int oldHandEnable = -1;
    private boolean activedOff;
    private int obbySlot;
    boolean hasPlaced;
    int oldslot = 1;
    private final HashMap<BlockPos, Integer> recentPlacements = new HashMap();
    private final ArrayList<EnumFacing> exd = new ArrayList<EnumFacing>(){
        {
            this.add(EnumFacing.DOWN);
            this.add(EnumFacing.UP);
        }
    };

    @Override
    public void onEnable() {
        this.activedOff = false;
        PlacementUtil.onEnable();
        if (((Boolean)this.autoSwitch.getValue()).booleanValue() && HoleFill.mc.field_71439_g != null) {
            this.oldHandEnable = HoleFill.mc.field_71439_g.field_71071_by.field_70461_c;
        }
        this.obbySlot = InventoryUtil.findObsidianSlot((Boolean)this.offHandObby.getValue(), this.activedOff);
        if (this.obbySlot == 9) {
            this.activedOff = true;
        }
    }

    @Override
    public void onDisable() {
        PlacementUtil.onDisable();
        if (((Boolean)this.autoSwitch.getValue()).booleanValue() && HoleFill.mc.field_71439_g != null && this.oldHandEnable != -1) {
            HoleFill.mc.field_71439_g.field_71071_by.field_70461_c = this.oldHandEnable;
        }
        this.recentPlacements.clear();
        if (((Boolean)this.offHandObby.getValue()).booleanValue() && ModuleManager.isModuleEnabled(OffHand.class)) {
            OffHand.removeItem(0);
            this.activedOff = false;
        }
    }

    @Override
    public void onUpdate() {
        if (HoleFill.mc.field_71439_g == null || HoleFill.mc.field_71441_e == null) {
            this.disable();
            return;
        }
        this.recentPlacements.replaceAll((blockPos, integer) -> integer + 1);
        this.recentPlacements.values().removeIf(integer -> integer > (Integer)this.retryDelay.getValue() * 2);
        if (this.delayTicks <= (Integer)this.placeDelay.getValue() * 2) {
            ++this.delayTicks;
            return;
        }
        if (!(this.obbySlot != 9 || HoleFill.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock && ((ItemBlock)HoleFill.mc.field_71439_g.func_184592_cb().func_77973_b()).func_179223_d() instanceof BlockObsidian)) {
            return;
        }
        List<Object> holePos = new ArrayList<BlockPos>(this.findHoles());
        holePos.removeAll(this.recentPlacements.keySet());
        AtomicInteger placements = new AtomicInteger();
        holePos = holePos.stream().sorted(Comparator.comparing(blockPos -> blockPos.func_177954_c((double)((int)HoleFill.mc.field_71439_g.field_70165_t), (double)((int)HoleFill.mc.field_71439_g.field_70163_u), (double)((int)HoleFill.mc.field_71439_g.field_70161_v)))).collect(Collectors.toList());
        ArrayList<EntityPlayer> listPlayer = new ArrayList<EntityPlayer>(HoleFill.mc.field_71441_e.field_73010_i);
        listPlayer.removeIf(player -> EntityUtil.basicChecksEntity((Entity)player) || (Boolean)this.onlyPlayer.getValue() == false || (double)HoleFill.mc.field_71439_g.func_70032_d((Entity)player) > 6.0 + (Double)this.playerRange.getValue());
        this.hasPlaced = false;
        holePos.removeIf(placePos -> {
            if (placements.get() >= (Integer)this.bpc.getValue()) {
                return false;
            }
            if (HoleFill.mc.field_71441_e.func_72839_b(null, new AxisAlignedBB(placePos)).stream().anyMatch(entity -> entity instanceof EntityPlayer)) {
                return true;
            }
            boolean output = false;
            if (this.isHoldingRightBlock(HoleFill.mc.field_71439_g.field_71071_by.field_70461_c, HoleFill.mc.field_71439_g.func_184586_b(EnumHand.MAIN_HAND).func_77973_b()).booleanValue() || ((Boolean)this.offHandObby.getValue()).booleanValue() || ((Boolean)this.SilentSwitch.getValue()).booleanValue()) {
                boolean found = false;
                if (((Boolean)this.onlyPlayer.getValue()).booleanValue()) {
                    for (EntityPlayer player : listPlayer) {
                        if (!(player.func_174831_c(placePos) < (Double)this.playerRange.getValue() * 2.0)) continue;
                        found = true;
                        break;
                    }
                    if (!found) {
                        return false;
                    }
                }
                if (this.placeBlock((BlockPos)placePos)) {
                    placements.getAndIncrement();
                    output = true;
                    this.delayTicks = 0;
                }
                this.recentPlacements.put((BlockPos)placePos, 0);
            }
            return output;
        });
        if (this.hasPlaced) {
            HoleFill.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(this.oldslot));
        }
        if (((Boolean)this.disableOnFinish.getValue()).booleanValue() && holePos.size() == 0) {
            this.disable();
        }
    }

    private boolean placeBlock(BlockPos pos) {
        EnumHand handSwing = EnumHand.MAIN_HAND;
        if (((Boolean)this.offHandObby.getValue()).booleanValue()) {
            int obsidianSlot = InventoryUtil.findObsidianSlot((Boolean)this.offHandObby.getValue(), this.activedOff);
            if (obsidianSlot == -1) {
                return false;
            }
            if (obsidianSlot == 9) {
                this.activedOff = true;
                if (HoleFill.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock && ((ItemBlock)HoleFill.mc.field_71439_g.func_184592_cb().func_77973_b()).func_179223_d() instanceof BlockObsidian) {
                    handSwing = EnumHand.OFF_HAND;
                } else {
                    return false;
                }
            }
        }
        if (((Boolean)this.autoSwitch.getValue()).booleanValue() || ((Boolean)this.SilentSwitch.getValue()).booleanValue()) {
            int newHand = this.findRightBlock();
            if (newHand != -1) {
                if (HoleFill.mc.field_71439_g.field_71071_by.field_70461_c != newHand) {
                    if (((Boolean)this.SilentSwitch.getValue()).booleanValue()) {
                        if (!this.hasPlaced) {
                            HoleFill.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(newHand));
                            this.hasPlaced = true;
                            this.oldslot = HoleFill.mc.field_71439_g.field_71071_by.field_70461_c;
                        }
                    } else {
                        HoleFill.mc.field_71439_g.field_71071_by.field_70461_c = newHand;
                        HoleFill.mc.field_71442_b.func_78750_j();
                    }
                }
            } else {
                return false;
            }
        }
        return ((String)this.mode.getValue()).equalsIgnoreCase("skull") ? PlacementUtil.place(pos, handSwing, (boolean)((Boolean)this.rotate.getValue()), this.exd) : PlacementUtil.place(pos, handSwing, (boolean)((Boolean)this.rotate.getValue()), (Boolean)this.SilentSwitch.getValue() == false);
    }

    private List<BlockPos> findHoles() {
        NonNullList holes = NonNullList.func_191196_a();
        List<BlockPos> blockPosList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), ((Double)this.range.getValue()).floatValue(), ((Double)this.range.getValue()).intValue(), false, true, 0);
        blockPosList.forEach(pos -> {
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            if (holeType != HoleUtil.HoleType.NONE) {
                AxisAlignedBB centreBlocks = holeInfo.getCentre();
                if (centreBlocks == null) {
                    return;
                }
                if (((Boolean)this.doubleHole.getValue()).booleanValue() && holeType == HoleUtil.HoleType.DOUBLE) {
                    holes.add(pos);
                } else if (holeType == HoleUtil.HoleType.SINGLE) {
                    holes.add(pos);
                }
            }
        });
        if (holes.isEmpty() && ((Boolean)this.disableOnFinish.getValue()).booleanValue()) {
            this.disable();
        }
        return holes;
    }

    private int findRightBlock() {
        switch ((String)this.mode.getValue()) {
            case "Both": {
                int newHand = InventoryUtil.findFirstBlockSlot(BlockObsidian.class, 0, 8);
                if (newHand == -1) {
                    return InventoryUtil.findFirstBlockSlot(BlockEnderChest.class, 0, 8);
                }
                return newHand;
            }
            case "Obby": {
                return InventoryUtil.findFirstBlockSlot(BlockObsidian.class, 0, 8);
            }
            case "Echest": {
                return InventoryUtil.findFirstBlockSlot(BlockEnderChest.class, 0, 8);
            }
            case "Web": {
                return InventoryUtil.findFirstBlockSlot(BlockWeb.class, 0, 8);
            }
            case "Plate": {
                return InventoryUtil.findFirstBlockSlot(BlockPressurePlate.class, 0, 8);
            }
            case "Skull": {
                return InventoryUtil.findSkullSlot(false, false);
            }
        }
        return -1;
    }

    private Boolean isHoldingRightBlock(int hand, Item item) {
        if (hand == -1) {
            return false;
        }
        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock)item).func_179223_d();
            switch ((String)this.mode.getValue()) {
                case "Both": {
                    return block instanceof BlockObsidian || block instanceof BlockEnderChest;
                }
                case "Obby": {
                    return block instanceof BlockObsidian;
                }
                case "Echest": {
                    return block instanceof BlockEnderChest;
                }
                case "Web": {
                    return block instanceof BlockWeb;
                }
                case "Plate": {
                    return block instanceof BlockPressurePlate;
                }
            }
            return false;
        }
        return false;
    }
}

