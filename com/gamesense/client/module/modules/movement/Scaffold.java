/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.PlayerJumpEvent;
import com.gamesense.api.event.events.PlayerMoveEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.misc.Timer;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.player.PredictUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.MotionUtil;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

@Module.Declaration(name="Scaffold", category=Category.Movement)
public class Scaffold
extends Module {
    ModeSetting logic = this.registerMode("Place Logic", Arrays.asList("Predict", "Player"), "Predict");
    IntegerSetting distance = this.registerInteger("Distance Predict", 2, 0, 20, () -> ((String)this.logic.getValue()).equalsIgnoreCase("Predict"));
    IntegerSetting distanceP = this.registerInteger("Distance Player", 2, 0, 20, () -> ((String)this.logic.getValue()).equalsIgnoreCase("Player"));
    ModeSetting towerMode = this.registerMode("Tower Mode", Arrays.asList("Jump", "Motion", "FakeJump", "None"), "Motion");
    DoubleSetting downSpeed = this.registerDouble("DownSpeed", 0.0, 0.0, 0.2);
    IntegerSetting delay = this.registerInteger("Jump Delay", 2, 1, 10);
    BooleanSetting rotate = this.registerBoolean("Rotate", false);
    private final Listener<PlayerJumpEvent> jumpEventListener = new Listener<PlayerJumpEvent>(event -> {
        if (((String)this.towerMode.getValue()).equalsIgnoreCase("FakeJump")) {
            event.cancel();
        }
    }, new Predicate[0]);
    int timer;
    int oldSlot;
    int newSlot;
    double oldTower;
    EntityPlayer predPlayer;
    BlockPos scaffold;
    BlockPos towerPos;
    BlockPos downPos;
    BlockPos rotateTo;
    Timer cancelTimer = new Timer();
    CPacketPlayer.Rotation RotVec = null;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener = new Listener<PacketEvent.Send>(event -> {
        if (event.getPacket() instanceof CPacketPlayer.PositionRotation && ((Boolean)this.rotate.getValue()).booleanValue()) {
            CPacketPlayer.PositionRotation e = (CPacketPlayer.PositionRotation)event.getPacket();
            if (this.RotVec == null) {
                return;
            }
            e.field_149473_f = this.RotVec.field_149473_f;
            e.field_149476_e = this.RotVec.field_149476_e;
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PlayerMoveEvent> moveEventListener = new Listener<PlayerMoveEvent>(event -> {
        this.oldSlot = Scaffold.mc.field_71439_g.field_71071_by.field_70461_c;
        this.towerPos = new BlockPos(Scaffold.mc.field_71439_g.field_70165_t, Scaffold.mc.field_71439_g.field_70163_u - 1.0, Scaffold.mc.field_71439_g.field_70161_v);
        this.downPos = new BlockPos(Scaffold.mc.field_71439_g.field_70165_t, Scaffold.mc.field_71439_g.field_70163_u - 2.0, Scaffold.mc.field_71439_g.field_70161_v);
        if (((String)this.logic.getValue()).equalsIgnoreCase("Predict")) {
            PredictUtil.PredictSettings predset = new PredictUtil.PredictSettings((Integer)this.distance.getValue(), false, 0, 0, 0, 0, 0, 0, false, 0, false, false, false, false, false, 0, 696969.0);
            this.predPlayer = PredictUtil.predictPlayer((EntityPlayer)Scaffold.mc.field_71439_g, predset);
            this.scaffold = new BlockPos(this.predPlayer.field_70165_t, this.predPlayer.field_70163_u - 1.0, this.predPlayer.field_70161_v);
        } else if (((String)this.logic.getValue()).equalsIgnoreCase("Player")) {
            double[] dir = MotionUtil.forward(MotionUtil.getMotion((EntityPlayer)Scaffold.mc.field_71439_g) * (double)((Integer)this.distanceP.getValue()).intValue());
            this.scaffold = new BlockPos(Scaffold.mc.field_71439_g.field_70165_t + dir[0], Scaffold.mc.field_71439_g.field_70163_u, Scaffold.mc.field_71439_g.field_70161_v + dir[1]).func_177977_b();
        }
        this.newSlot = -1;
        if (!Block.func_149634_a((Item)Scaffold.mc.field_71439_g.func_184614_ca().field_151002_e).func_176223_P().func_185913_b()) {
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = Scaffold.mc.field_71439_g.field_71071_by.func_70301_a(i);
                if (stack == ItemStack.field_190927_a || !(stack.func_77973_b() instanceof ItemBlock) || !Block.func_149634_a((Item)stack.func_77973_b()).func_176223_P().func_185913_b() || ((ItemBlock)stack.func_77973_b()).func_179223_d() instanceof BlockFalling && Scaffold.mc.field_71441_e.func_180495_p(this.scaffold).func_185904_a().func_76222_j()) continue;
                this.newSlot = i;
                break;
            }
        } else {
            this.newSlot = Scaffold.mc.field_71439_g.field_71071_by.field_70461_c;
        }
        if (this.newSlot == -1) {
            this.newSlot = 1;
            MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getDisabledColor() + "Out of valid blocks. Disabling!");
            this.disable();
        }
        switch ((String)this.towerMode.getValue()) {
            case "Jump": {
                if (Scaffold.mc.field_71439_g.field_70122_E) {
                    this.oldTower = Scaffold.mc.field_71439_g.field_70163_u;
                    Scaffold.mc.field_71439_g.func_70664_aZ();
                }
                if (Math.floor(Scaffold.mc.field_71439_g.field_70163_u) == this.oldTower + 1.0 && !Scaffold.mc.field_71439_g.field_70122_E) {
                    Scaffold.mc.field_71439_g.field_70181_x = -(Scaffold.mc.field_71439_g.field_70163_u - Math.floor(Scaffold.mc.field_71439_g.field_70163_u));
                }
                this.placeBlockPacket(this.towerPos, false);
                break;
            }
            case "FakeJump": {
                if (Scaffold.mc.field_71439_g.field_70173_aa % (Integer)this.delay.getValue() != 0 || !Scaffold.mc.field_71439_g.field_70122_E || !Scaffold.mc.field_71474_y.field_74314_A.func_151470_d()) break;
                PlayerUtil.fakeJump(3);
                Scaffold.mc.field_71439_g.func_70107_b(Scaffold.mc.field_71439_g.field_70165_t, Scaffold.mc.field_71439_g.field_70163_u + 1.0013359791121, Scaffold.mc.field_71439_g.field_70161_v);
                this.placeBlockPacket(this.towerPos, false);
            }
        }
        if (Scaffold.mc.field_71474_y.field_74314_A.func_151470_d()) {
            this.placeBlockPacket(this.towerPos, false);
        }
        if (!Scaffold.mc.field_71474_y.field_74314_A.func_151470_d() && !Scaffold.mc.field_71474_y.field_151444_V.func_151470_d()) {
            this.placeBlockPacket(this.scaffold, true);
        }
        double[] dir = MotionUtil.forward((Double)this.downSpeed.getValue());
        if (Scaffold.mc.field_71474_y.field_151444_V.func_151470_d()) {
            this.placeBlockPacket(this.downPos, false);
            Scaffold.mc.field_71439_g.field_70159_w = dir[0];
            Scaffold.mc.field_71439_g.field_70179_y = dir[1];
        }
    }, new Predicate[0]);

    @Override
    protected void onEnable() {
        this.timer = 0;
    }

    @Override
    public void onUpdate() {
        this.timer = Scaffold.mc.field_71439_g.field_70122_E ? 0 : ++this.timer;
        if (this.timer == (Integer)this.delay.getValue() && Scaffold.mc.field_71474_y.field_74314_A.func_151470_d() && !this.cancelTimer.hasReached(1200L, true)) {
            Scaffold.mc.field_71439_g.func_70664_aZ();
            this.timer = 0;
        }
    }

    boolean placeBlockPacket(BlockPos pos, boolean allowSupport) {
        Scaffold.mc.field_71439_g.field_70177_z = (float)((double)Scaffold.mc.field_71439_g.field_70177_z + (Scaffold.mc.field_71439_g.field_70173_aa % 2 == 0 ? 1.0E-5 : -1.0E-5));
        boolean shouldplace = Scaffold.mc.field_71441_e.func_180495_p(pos).func_177230_c().func_176200_f((IBlockAccess)Scaffold.mc.field_71441_e, pos) && BlockUtil.getPlaceableSide(pos) != null;
        this.rotateTo = pos;
        if (shouldplace) {
            boolean swap;
            boolean bl = swap = this.newSlot != Scaffold.mc.field_71439_g.field_71071_by.field_70461_c;
            if (swap) {
                Scaffold.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(this.newSlot));
                Scaffold.mc.field_71439_g.field_71071_by.field_70461_c = this.newSlot;
            }
            this.RotVec = PlacementUtil.placeBlockGetRotate(pos, EnumHand.MAIN_HAND, false, null, false);
            if (swap) {
                Scaffold.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(this.oldSlot));
                Scaffold.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
            }
            return this.RotVec != null;
        }
        if (allowSupport && BlockUtil.getPlaceableSide(pos) == null) {
            this.clutch();
        }
        return false;
    }

    public void clutch() {
        BlockPos xpPos = new BlockPos(Scaffold.mc.field_71439_g.field_70165_t + 1.0, Scaffold.mc.field_71439_g.field_70163_u - 1.0, Scaffold.mc.field_71439_g.field_70161_v);
        BlockPos xmPos = new BlockPos(Scaffold.mc.field_71439_g.field_70165_t - 1.0, Scaffold.mc.field_71439_g.field_70163_u - 1.0, Scaffold.mc.field_71439_g.field_70161_v);
        BlockPos zpPos = new BlockPos(Scaffold.mc.field_71439_g.field_70165_t, Scaffold.mc.field_71439_g.field_70163_u - 1.0, Scaffold.mc.field_71439_g.field_70161_v + 1.0);
        BlockPos zmPos = new BlockPos(Scaffold.mc.field_71439_g.field_70165_t, Scaffold.mc.field_71439_g.field_70163_u - 1.0, Scaffold.mc.field_71439_g.field_70161_v - 1.0);
        if (!(this.placeBlockPacket(xpPos, false) || this.placeBlockPacket(xmPos, false) || this.placeBlockPacket(zpPos, false))) {
            this.placeBlockPacket(zmPos, false);
        }
    }
}

