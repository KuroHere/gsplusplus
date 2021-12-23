/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.misc.Timer;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;

@Module.Declaration(name="AutoCreeper", category=Category.Combat)
public class AutoCreeper
extends Module {
    IntegerSetting delay = this.registerInteger("Delay", 3, 0, 20);
    DoubleSetting range = this.registerDouble("Range", 5.0, 0.0, 6.0);
    BooleanSetting rotate = this.registerBoolean("Rotate", true);
    BooleanSetting silent = this.registerBoolean("Silent Switch", true);
    EntityPlayer target = null;
    int oldSlot;
    int slot = -1;
    Vec2f rot;
    Timer delayTimer = new Timer();
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener = new Listener<PacketEvent.Send>(event -> {
        if (((Boolean)this.rotate.getValue()).booleanValue() && this.target != null) {
            this.rot = RotationUtil.getRotationTo(this.target.func_174791_d());
            if (event.getPacket() instanceof CPacketPlayer) {
                ((CPacketPlayer)event.getPacket()).field_149476_e = this.rot.field_189982_i;
                ((CPacketPlayer)event.getPacket()).field_149473_f = this.rot.field_189983_j;
            }
        }
    }, new Predicate[0]);

    @Override
    public void onUpdate() {
        if (this.target == null || (double)this.target.func_70032_d((Entity)AutoCreeper.mc.field_71439_g) > (Double)this.range.getValue()) {
            this.target = this.getTarget();
        } else if (AutoCreeper.mc.field_71439_g.field_70173_aa % (Integer)this.delay.getValue() == 0) {
            this.slot = this.getSlot();
            if (this.slot == -1) {
                this.disable();
            }
            this.oldSlot = AutoCreeper.mc.field_71439_g.field_71071_by.field_70461_c;
            if (((Boolean)this.silent.getValue()).booleanValue()) {
                AutoCreeper.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(this.slot));
            } else {
                AutoCreeper.mc.field_71439_g.field_71071_by.field_70461_c = this.slot;
            }
            AutoCreeper.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(new BlockPos(this.target.field_70165_t, Math.ceil(this.target.field_70163_u - 0.5) - 1.0, this.target.field_70161_v), EnumFacing.UP, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
            if (((Boolean)this.silent.getValue()).booleanValue()) {
                AutoCreeper.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(this.oldSlot));
            } else {
                AutoCreeper.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
            }
        }
    }

    public EntityPlayer getTarget() {
        this.target = PlayerUtil.findClosestTarget();
        return this.target;
    }

    public int getSlot() {
        int newSlot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = AutoCreeper.mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack == ItemStack.field_190927_a && stack.func_77973_b() == Items.field_151063_bx) continue;
            newSlot = i;
            break;
        }
        return newSlot;
    }
}

