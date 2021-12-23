/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Mouse;

@Module.Declaration(name="MouseClickAction", category=Category.Misc)
public class MouseClickAction
extends Module {
    BooleanSetting friend = this.registerBoolean("friend", true);
    ModeSetting friendButton = this.registerMode("FriendButton", Arrays.asList("MOUSE3", "MOUSE4", "MOUSE5"), "MOUSE3", () -> (Boolean)this.friend.getValue());
    BooleanSetting pearl = this.registerBoolean("pearl", true);
    ModeSetting pearlButton = this.registerMode("PearlButton", Arrays.asList("MOUSE3", "MOUSE4", "MOUSE5"), "MOUSE4", () -> (Boolean)this.pearl.getValue());
    BooleanSetting clipRotate = this.registerBoolean("clipRotate", false, () -> (Boolean)this.pearl.getValue());
    IntegerSetting pearlPitch = this.registerInteger("Pitch", 85, -90, 90, () -> (Boolean)this.clipRotate.getValue());
    int MCPButtonCode;
    int MCFButtonCode;
    int pearlInvSlot;
    @EventHandler
    final Listener<InputEvent.MouseInputEvent> listener = new Listener<InputEvent.MouseInputEvent>(event -> {
        if (Mouse.isButtonDown((int)this.MCFButtonCode) && MouseClickAction.mc.field_71476_x.field_72313_a.equals((Object)RayTraceResult.Type.ENTITY) && MouseClickAction.mc.field_71476_x.field_72308_g instanceof EntityPlayer && ((Boolean)this.friend.getValue()).booleanValue()) {
            if (SocialManager.isFriendForce(MouseClickAction.mc.field_71476_x.field_72308_g.func_70005_c_())) {
                SocialManager.delFriend(MouseClickAction.mc.field_71476_x.field_72308_g.func_70005_c_());
                MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getDisabledColor() + "Removed " + MouseClickAction.mc.field_71476_x.field_72308_g.func_70005_c_() + " from friends list");
            } else {
                SocialManager.addFriend(MouseClickAction.mc.field_71476_x.field_72308_g.func_70005_c_());
                MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getEnabledColor() + "Added " + MouseClickAction.mc.field_71476_x.field_72308_g.func_70005_c_() + " to friends list");
            }
        }
    }, new Predicate[0]);

    @Override
    public void onUpdate() {
        this.MCPButtonCode = ((String)this.pearlButton.getValue()).equalsIgnoreCase("MOUSE3") ? 2 : (((String)this.pearlButton.getValue()).equalsIgnoreCase("MOUSE4") ? 3 : (((String)this.pearlButton.getValue()).equalsIgnoreCase("MOUSE5") ? 4 : 2));
        this.MCFButtonCode = ((String)this.friendButton.getValue()).equalsIgnoreCase("MOUSE3") ? 2 : (((String)this.friendButton.getValue()).equalsIgnoreCase("MOUSE4") ? 3 : (((String)this.friendButton.getValue()).equalsIgnoreCase("MOUSE5") ? 4 : 2));
        if (Mouse.isButtonDown((int)this.MCPButtonCode)) {
            if (((Boolean)this.clipRotate.getValue()).booleanValue() && MouseClickAction.mc.field_71439_g.field_70122_E) {
                MouseClickAction.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(MouseClickAction.mc.field_71439_g.field_70177_z, ((Integer)this.pearlPitch.getValue()).floatValue(), MouseClickAction.mc.field_71439_g.field_70122_E));
            }
            if (((Boolean)this.clipRotate.getValue()).booleanValue() && !MouseClickAction.mc.field_71439_g.field_70122_E) {
                return;
            }
            this.pearlInvSlot = InventoryUtil.findFirstItemSlot(ItemEnderPearl.class, 0, 35);
            int pearlHotSlot = InventoryUtil.findFirstItemSlot(ItemEnderPearl.class, 0, 8);
            int currentItem = MouseClickAction.mc.field_71439_g.field_71071_by.field_70461_c;
            if (pearlHotSlot == -1) {
                InventoryUtil.swap(this.pearlInvSlot, currentItem + 36);
                MouseClickAction.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                InventoryUtil.swap(this.pearlInvSlot, currentItem + 36);
            } else {
                int oldSlot = MouseClickAction.mc.field_71439_g.field_71071_by.field_70461_c;
                MouseClickAction.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(pearlHotSlot));
                MouseClickAction.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                MouseClickAction.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(oldSlot));
            }
        }
    }
}

