/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.PlayerMoveEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemChorusFruit;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(name="AntiVoid", category=Category.Movement)
public class AntiVoid
extends Module {
    ModeSetting mode = this.registerMode("Mode", Arrays.asList("Freeze", "Glitch", "Catch"), "Freeze");
    DoubleSetting height = this.registerDouble("Height", 2.0, 0.0, 5.0);
    BooleanSetting chorus = this.registerBoolean("Chorus", false, () -> ((String)this.mode.getValue()).equals("Freeze"));
    BooleanSetting packetfly = this.registerBoolean("PacketFly", false, () -> ((String)this.mode.getValue()).equals("Catch"));
    boolean chor;
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
        try {
            if (AntiVoid.mc.field_71439_g.field_70163_u < (Double)this.height.getValue() + 0.1 && ((String)this.mode.getValue()).equalsIgnoreCase("Freeze") && AntiVoid.mc.field_71441_e.func_180495_p(new BlockPos(AntiVoid.mc.field_71439_g.field_70165_t, 0.0, AntiVoid.mc.field_71439_g.field_70161_v)).func_185904_a().func_76222_j()) {
                switch ((String)this.mode.getValue()) {
                    case "Freeze": {
                        AntiVoid.mc.field_71439_g.field_70163_u = (Double)this.height.getValue();
                        event.setY(0.0);
                        if (AntiVoid.mc.field_71439_g.func_184187_bx() != null) {
                            AntiVoid.mc.field_71439_g.field_184239_as.func_70016_h(0.0, 0.0, 0.0);
                        }
                        if (!((Boolean)this.chorus.getValue()).booleanValue()) break;
                        int newSlot = -1;
                        for (int i = 0; i < 9; ++i) {
                            ItemStack stack = AntiVoid.mc.field_71439_g.field_71071_by.func_70301_a(i);
                            if (stack == ItemStack.field_190927_a || !(stack.func_77973_b() instanceof ItemChorusFruit)) continue;
                            newSlot = i;
                            break;
                        }
                        if (newSlot == -1) {
                            newSlot = 1;
                            MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getDisabledColor() + "Out of chorus!");
                            this.chor = false;
                        } else {
                            this.chor = true;
                        }
                        if (!this.chor) break;
                        AntiVoid.mc.field_71439_g.field_71071_by.field_70461_c = newSlot;
                        if (!AntiVoid.mc.field_71439_g.func_71043_e(true)) break;
                        AntiVoid.mc.field_71439_g.func_184598_c(EnumHand.MAIN_HAND);
                        break;
                    }
                    case "Glitch": {
                        AntiVoid.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(AntiVoid.mc.field_71439_g.field_70165_t, AntiVoid.mc.field_71439_g.field_70163_u + 69.0, AntiVoid.mc.field_71439_g.field_70161_v, AntiVoid.mc.field_71439_g.field_70122_E));
                        break;
                    }
                    case "Catch": {
                        int oldSlot = AntiVoid.mc.field_71439_g.field_71071_by.field_70461_c;
                        int newSlot = -1;
                        for (int i = 0; i < 9; ++i) {
                            ItemStack stack = AntiVoid.mc.field_71439_g.field_71071_by.func_70301_a(i);
                            if (stack == ItemStack.field_190927_a || !(stack.func_77973_b() instanceof ItemBlock) || !Block.func_149634_a((Item)stack.func_77973_b()).func_176223_P().func_185913_b() || ((ItemBlock)stack.func_77973_b()).func_179223_d() instanceof BlockFalling) continue;
                            newSlot = i;
                            break;
                        }
                        if (newSlot == -1) {
                            newSlot = 1;
                            MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getDisabledColor() + "Out of valid blocks. Disabling!");
                            this.disable();
                        }
                        AntiVoid.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(newSlot));
                        PlacementUtil.place(new BlockPos(AntiVoid.mc.field_71439_g.field_70165_t, 0.0, AntiVoid.mc.field_71439_g.field_70161_v), EnumHand.MAIN_HAND, true);
                        if (AntiVoid.mc.field_71441_e.func_180495_p(new BlockPos(AntiVoid.mc.field_71439_g.field_70165_t, 0.0, AntiVoid.mc.field_71439_g.field_70161_v)).func_185904_a().func_76222_j() && ((Boolean)this.packetfly.getValue()).booleanValue()) {
                            AntiVoid.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(AntiVoid.mc.field_71439_g.field_70165_t + AntiVoid.mc.field_71439_g.field_70159_w, AntiVoid.mc.field_71439_g.field_70163_u + 0.0624, AntiVoid.mc.field_71439_g.field_70161_v + AntiVoid.mc.field_71439_g.field_70179_y, AntiVoid.mc.field_71439_g.field_70177_z, AntiVoid.mc.field_71439_g.field_70125_A, false));
                            AntiVoid.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(AntiVoid.mc.field_71439_g.field_70165_t, AntiVoid.mc.field_71439_g.field_70163_u + 69420.0, AntiVoid.mc.field_71439_g.field_70161_v, AntiVoid.mc.field_71439_g.field_70177_z, AntiVoid.mc.field_71439_g.field_70125_A, false));
                        }
                        AntiVoid.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(oldSlot));
                        break;
                    }
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }, new Predicate[0]);
}

