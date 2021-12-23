/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.misc.Timer;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.exploits.RubberBand;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.client.module.modules.movement.Blink;
import java.util.Arrays;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(name="FootConcrete", category=Category.Combat, priority=101)
public class FootConcrete
extends Module {
    final Timer concreteTimer = new Timer();
    ModeSetting jumpMode = this.registerMode("Jump Mode", Arrays.asList("Real", "Instant"), "Instant");
    BooleanSetting general = this.registerBoolean("General Settings", false);
    ModeSetting rubberBandMode = this.registerMode("Rubberband Mode", Arrays.asList("flat", "clip", "basic"), "jump");
    IntegerSetting strength = this.registerInteger("Strength", 1, 0, 25, () -> (Boolean)this.general.getValue() != false && !((String)this.rubberBandMode.getValue()).equalsIgnoreCase("clip"));
    BooleanSetting useBlink = this.registerBoolean("Use Blink", true, () -> ((String)this.jumpMode.getValue()).equals("real") && (Boolean)this.general.getValue() != false);
    BooleanSetting rotate = this.registerBoolean("rotate", true, () -> (Boolean)this.general.getValue());
    BooleanSetting positive = this.registerBoolean("Positive Pos", false, () -> ((String)this.rubberBandMode.getValue()).equalsIgnoreCase("clip"));
    BooleanSetting debugpos = this.registerBoolean("Debug Position", false, () -> ((String)this.rubberBandMode.getValue()).equalsIgnoreCase("clip") && (Boolean)this.general.getValue() != false);
    BooleanSetting blocks = this.registerBoolean("Blocks Menu", false);
    BooleanSetting obby = this.registerBoolean("Obsidian", true, () -> (Boolean)this.blocks.getValue());
    BooleanSetting echest = this.registerBoolean("Ender Chest", true, () -> (Boolean)this.blocks.getValue());
    BooleanSetting rod = this.registerBoolean("End Rod", false, () -> (Boolean)this.blocks.getValue());
    BooleanSetting anvil = this.registerBoolean("Anvil", false, () -> (Boolean)this.blocks.getValue());
    BooleanSetting any = this.registerBoolean("Any", false, () -> (Boolean)this.blocks.getValue());
    boolean invalidHotbar;
    int oldSlot;
    int targetBlockSlot;
    BlockPos burrowBlockPos;
    int oldslot;
    int pos;

    @Override
    public void onEnable() {
        this.invalidHotbar = false;
        if (!FootConcrete.mc.field_71441_e.func_175623_d(new BlockPos(FootConcrete.mc.field_71439_g.func_174791_d()))) {
            MessageBus.sendClientPrefixMessage("You are already clipped, disabling!");
            this.disable();
        }
        this.targetBlockSlot = this.getBlocks();
        if (this.targetBlockSlot == -1) {
            MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getDisabledColor() + "No burrow blocks in hotbar, disabling");
            this.invalidHotbar = true;
            this.disable();
            this.disable();
        }
        if (!this.invalidHotbar) {
            if (FootConcrete.mc.field_71439_g.field_70122_E) {
                this.burrowBlockPos = new BlockPos(FootConcrete.mc.field_71439_g.func_174791_d());
                if (FootConcrete.mc.field_71441_e.func_189509_E(this.burrowBlockPos)) {
                    this.disable();
                    MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getDisabledColor() + "You are trying to burrow above build limit, disabling.");
                }
                if (((String)this.jumpMode.getValue()).equals("real")) {
                    if (((Boolean)this.useBlink.getValue()).booleanValue()) {
                        ModuleManager.getModule(Blink.class).enable();
                    }
                    FootConcrete.mc.field_71439_g.func_70664_aZ();
                    this.pos = (int)FootConcrete.mc.field_71439_g.func_174791_d().field_72448_b;
                }
                this.concreteTimer.reset();
            } else {
                this.disable();
            }
        }
    }

    @Override
    public void onUpdate() {
        if (((String)this.jumpMode.getValue()).equalsIgnoreCase("Real")) {
            if (FootConcrete.mc.field_71439_g.field_70163_u > (double)this.pos + 1.02) {
                this.targetBlockSlot = this.getBlocks();
                this.oldSlot = FootConcrete.mc.field_71439_g.field_71071_by.field_70461_c;
                if (this.targetBlockSlot == -1) {
                    this.disable();
                }
                if (((Boolean)this.useBlink.getValue()).booleanValue()) {
                    ModuleManager.getModule(Blink.class).disable();
                }
                this.place(this.burrowBlockPos, this.targetBlockSlot);
                RubberBand.getPacket((Boolean)this.debugpos.getValue(), (Boolean)this.positive.getValue());
                this.disable();
            }
        } else {
            this.targetBlockSlot = this.getBlocks();
            this.oldSlot = FootConcrete.mc.field_71439_g.field_71071_by.field_70461_c;
            if (this.targetBlockSlot == -1) {
                this.disable();
            }
            PlayerUtil.fakeJump();
            this.place(this.burrowBlockPos, this.targetBlockSlot);
            RubberBand.getPacket((Boolean)this.debugpos.getValue(), (Boolean)this.positive.getValue());
            this.disable();
        }
    }

    void place(BlockPos pos, int targetBlockSlot) {
        FootConcrete.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(targetBlockSlot));
        PlacementUtil.place(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), false, false);
        FootConcrete.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(this.oldslot));
    }

    @Override
    protected void onDisable() {
        if (((Boolean)this.useBlink.getValue()).booleanValue() && ((String)this.jumpMode.getValue()).equalsIgnoreCase("Real") && ModuleManager.isModuleEnabled(Blink.class)) {
            ModuleManager.getModule(Blink.class).disable();
        }
    }

    int getBlocks() {
        int current = -1;
        if (((Boolean)this.any.getValue()).booleanValue() && InventoryUtil.findAnyBlockSlot(0, 8) != -1) {
            current = InventoryUtil.findAnyBlockSlot(0, 8);
        }
        if (((Boolean)this.anvil.getValue()).booleanValue() && InventoryUtil.findFirstBlockSlot(Blocks.field_150467_bQ.getClass(), 0, 8) != -1) {
            current = InventoryUtil.findFirstBlockSlot(Blocks.field_150467_bQ.getClass(), 0, 8);
        }
        if (((Boolean)this.rod.getValue()).booleanValue() && InventoryUtil.findFirstBlockSlot(Blocks.field_185764_cQ.getClass(), 0, 8) != -1) {
            current = InventoryUtil.findFirstBlockSlot(Blocks.field_185764_cQ.getClass(), 0, 8);
        }
        if (((Boolean)this.echest.getValue()).booleanValue() && InventoryUtil.findFirstBlockSlot(Blocks.field_150477_bB.getClass(), 0, 8) != -1) {
            current = InventoryUtil.findFirstBlockSlot(Blocks.field_150477_bB.getClass(), 0, 8);
        }
        if (((Boolean)this.obby.getValue()).booleanValue() && InventoryUtil.findFirstBlockSlot(Blocks.field_150343_Z.getClass(), 0, 8) != -1) {
            current = InventoryUtil.findFirstBlockSlot(Blocks.field_150343_Z.getClass(), 0, 8);
        }
        return current;
    }
}

