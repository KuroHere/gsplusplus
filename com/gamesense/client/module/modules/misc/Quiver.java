/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.SpoofRotationUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockAir;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;

@Module.Declaration(name="Quiver", category=Category.Misc, priority=250)
public class Quiver
extends Module {
    ArrayList<String> arrowType = new ArrayList<String>(){
        {
            this.add("none");
            this.add("strength");
            this.add("swiftness");
        }
    };
    ArrayList<String> disableWhen = new ArrayList<String>(){
        {
            this.add("none");
            this.add("moving");
            this.add("stand");
        }
    };
    ModeSetting firstArrow = this.registerMode("First Arrow", Arrays.asList(this.arrowType.toArray(new String[0])), "strength");
    ModeSetting disableFirst = this.registerMode("Disable First", Arrays.asList(this.disableWhen.toArray(new String[0])), "none");
    ModeSetting secondArrow = this.registerMode("Second Arrow", Arrays.asList(this.arrowType.toArray(new String[0])), "none");
    ModeSetting disableSecond = this.registerMode("Disable Second", Arrays.asList(this.disableWhen.toArray(new String[0])), "none", () -> !((String)this.secondArrow.getValue()).equals("none"));
    ModeSetting active = this.registerMode("Active", Arrays.asList("On Bow", "Switch"), "On Bow");
    IntegerSetting pitchMoving = this.registerInteger("Pitch Moving", -45, 0, -70);
    IntegerSetting standDrawLength = this.registerInteger("Stand Draw Length", 4, 0, 21);
    IntegerSetting movingDrawLength = this.registerInteger("Moving Draw Length", 3, 0, 21);
    IntegerSetting tickWait = this.registerInteger("Tick Retry Wait", 20, 1, 50);
    IntegerSetting tickWaitEnd = this.registerInteger("Tick Arrow Wait", 0, 0, 100);
    int[] slot;
    int oldslot;
    int firstWait;
    int secondWait;
    int slotCheck;
    int endWait;
    boolean blockedUp;
    boolean beforeActive;
    boolean hasBow;
    boolean isPowering;
    boolean isFirst;
    String arrow = "";
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
        if (Quiver.mc.field_71439_g == null || Quiver.mc.field_71441_e == null || !this.isPowering) {
            return;
        }
        PlayerPacket packet = !this.isMoving() ? new PlayerPacket((Module)this, new Vec2f(0.0f, -90.0f)) : new PlayerPacket((Module)this, new Vec2f(Quiver.mc.field_71439_g.field_70177_z, (float)((Integer)this.pitchMoving.getValue()).intValue()));
        PlayerPacketManager.INSTANCE.addPacket(packet);
    }, new Predicate[0]);

    boolean isMoving() {
        return Math.abs(Quiver.mc.field_71439_g.field_70159_w) + Math.abs(Quiver.mc.field_71439_g.field_70179_y) > 0.01;
    }

    @Override
    public void onEnable() {
        if (Quiver.mc.field_71441_e == null || Quiver.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        SpoofRotationUtil.ROTATION_UTIL.onEnable();
        this.resetValues();
    }

    void resetValues() {
        this.isPowering = false;
        this.beforeActive = false;
        this.blockedUp = false;
        this.isFirst = true;
        this.hasBow = true;
        this.secondWait = 0;
        this.firstWait = 0;
        this.endWait = 0;
        this.slotCheck = -1;
        this.oldslot = -1;
        this.arrow = "";
    }

    boolean playerCheck() {
        boolean bl = this.blockedUp = !(BlockUtil.getBlock(EntityUtil.getPosition((Entity)Quiver.mc.field_71439_g).func_177982_a(0, 2, 0)) instanceof BlockAir);
        if (this.blockedUp) {
            this.disable();
            return false;
        }
        return true;
    }

    @Override
    public void onDisable() {
        if (Quiver.mc.field_71441_e == null || Quiver.mc.field_71439_g == null) {
            return;
        }
        if (Quiver.mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBow && Quiver.mc.field_71439_g.func_184587_cr()) {
            Quiver.mc.field_71439_g.func_184597_cx();
            KeyBinding.func_74510_a((int)Quiver.mc.field_71474_y.field_74313_G.func_151463_i(), (boolean)false);
        }
        String output = "";
        if (this.blockedUp) {
            output = "There is a block above you";
        } else if (!this.hasBow) {
            output = "No bow detected";
        }
        this.setDisabledMessage(output + "Quiver turned OFF!");
        if (this.oldslot != -1) {
            Quiver.mc.field_71439_g.field_71071_by.field_70461_c = this.oldslot;
        }
    }

    boolean canArrow(boolean isMoving, String notWanted) {
        switch (notWanted) {
            case "none": {
                return true;
            }
            case "moving": {
                return !isMoving;
            }
            case "stand": {
                return isMoving;
            }
        }
        return false;
    }

    @Override
    public void onUpdate() {
        if (Quiver.mc.field_71441_e == null || Quiver.mc.field_71439_g == null) {
            return;
        }
        if (this.endWait > 0) {
            --this.endWait;
            return;
        }
        if (Quiver.mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() != Items.field_151031_f) {
            if (this.isPowering) {
                KeyBinding.func_74510_a((int)Quiver.mc.field_71474_y.field_74313_G.func_151463_i(), (boolean)false);
            }
            if (((String)this.active.getValue()).equals("Switch")) {
                int slot = InventoryUtil.findFirstItemSlot(Items.field_151031_f.getClass(), 0, 8);
                if (slot == -1) {
                    this.hasBow = false;
                    this.disable();
                    return;
                }
                this.oldslot = Quiver.mc.field_71439_g.field_71071_by.field_70461_c;
                Quiver.mc.field_71439_g.field_71071_by.field_70461_c = slot;
            } else if (((String)this.active.getValue()).equals("On Bow")) {
                this.isPowering = false;
                return;
            }
        }
        if (!this.beforeActive) {
            this.resetValues();
            if (!this.playerCheck()) {
                return;
            }
        }
        boolean isMoving = this.isMoving();
        if (!this.isPowering) {
            boolean enter;
            if (--this.firstWait < 0) {
                enter = this.canArrow(isMoving, (String)this.disableFirst.getValue());
                if (enter) {
                    this.slot = this.getSlotArrow((String)this.firstArrow.getValue());
                    this.isFirst = true;
                } else {
                    this.slot = new int[]{-1, -1};
                }
            }
            if (this.slot[1] == -1) {
                if (--this.secondWait < 0) {
                    enter = this.canArrow(isMoving, (String)this.disableFirst.getValue());
                    if (enter) {
                        this.slot = this.getSlotArrow((String)this.secondArrow.getValue());
                        this.secondWait = (Integer)this.tickWait.getValue();
                    } else {
                        this.slot = new int[]{-1, -1};
                    }
                }
                this.isFirst = false;
            } else {
                this.firstWait = (Integer)this.tickWait.getValue();
            }
            if (this.slot[1] == -1) {
                return;
            }
            this.switchArrow();
        }
        KeyBinding.func_74510_a((int)Quiver.mc.field_71474_y.field_74313_G.func_151463_i(), (boolean)true);
        this.beforeActive = true;
        this.isPowering = true;
        if (Quiver.mc.field_71439_g.func_184612_cw() >= (this.isMoving() ? (Integer)this.movingDrawLength.getValue() : (Integer)this.standDrawLength.getValue())) {
            KeyBinding.func_74510_a((int)Quiver.mc.field_71474_y.field_74313_G.func_151463_i(), (boolean)false);
            Quiver.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.field_177992_a, Quiver.mc.field_71439_g.func_174811_aO()));
            Quiver.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItem(Quiver.mc.field_71439_g.func_184600_cs()));
            Quiver.mc.field_71439_g.func_184597_cx();
            this.switchArrow();
            this.slot = new int[]{-1, -1};
            this.isPowering = false;
            this.arrow = "";
            this.endWait = (Integer)this.tickWaitEnd.getValue();
            if (this.isFirst) {
                this.isFirst = false;
                this.firstWait = (Integer)this.tickWait.getValue();
            } else {
                this.secondWait = (Integer)this.tickWait.getValue();
                if (((String)this.active.getValue()).equals("Switch")) {
                    this.disable();
                    return;
                }
            }
        }
    }

    private void switchArrow() {
        if (this.slot[0] != -1) {
            Quiver.mc.field_71442_b.func_187098_a(0, this.slot[0], 0, ClickType.PICKUP, (EntityPlayer)Quiver.mc.field_71439_g);
            Quiver.mc.field_71442_b.func_187098_a(0, this.slot[1], 0, ClickType.PICKUP, (EntityPlayer)Quiver.mc.field_71439_g);
            Quiver.mc.field_71442_b.func_187098_a(0, this.slot[0], 0, ClickType.PICKUP, (EntityPlayer)Quiver.mc.field_71439_g);
            this.slotCheck = this.slot[0];
            Quiver.mc.field_71442_b.func_78765_e();
        }
    }

    int[] getSlotArrow(String wanted) {
        int[] returnValeus = new int[]{-1, -1};
        if (this.haveEffect(wanted)) {
            return returnValeus;
        }
        for (int i = 0; i < Quiver.mc.field_71439_g.field_71071_by.func_70302_i_(); ++i) {
            Item temp = Quiver.mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
            if (returnValeus[0] == -1 && (temp == Items.field_151032_g || temp == Items.field_185166_h || temp == Items.field_185167_i && !Quiver.mc.field_71439_g.field_71071_by.func_70301_a(i).func_77978_p().func_74781_a("Potion").toString().contains(wanted))) {
                returnValeus[0] = i + (i < 9 ? 36 : 0);
            }
            if (temp != Items.field_185167_i || !Quiver.mc.field_71439_g.field_71071_by.func_70301_a(i).func_77978_p().func_74781_a("Potion").toString().contains(wanted)) continue;
            returnValeus[1] = i + (i < 9 ? 36 : 0);
            return returnValeus;
        }
        return returnValeus;
    }

    boolean haveEffect(String wanted) {
        this.arrow = wanted;
        for (int i = 0; i < Quiver.mc.field_71439_g.func_70651_bq().toArray().length; ++i) {
            PotionEffect effect = (PotionEffect)Quiver.mc.field_71439_g.func_70651_bq().toArray()[i];
            String name = I18n.func_135052_a((String)effect.func_188419_a().func_76393_a(), (Object[])new Object[0]);
            if (name.toLowerCase().contains(wanted.toLowerCase())) {
                return true;
            }
            if (!wanted.equals("swiftness") || !name.equals("Speed")) continue;
            return true;
        }
        return false;
    }

    @Override
    public String getHudInfo() {
        if (!this.arrow.equals("")) {
            return "[" + ChatFormatting.WHITE + this.arrow + ChatFormatting.GRAY + "]";
        }
        return "";
    }
}

