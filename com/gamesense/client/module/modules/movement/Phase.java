/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.BoundingBoxEvent;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.PhaseUtil;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.MotionUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.Flight;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

@Module.Declaration(name="Phase", category=Category.Movement)
public class Phase
extends Module {
    ModeSetting mode = this.registerMode("Mode", Arrays.asList("NCP", "Vanilla", "Skip"), "NCP");
    DoubleSetting safety = this.registerDouble("Safety", 0.15, 0.0, 1.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Skip"));
    BooleanSetting bounded = this.registerBoolean("Bounded", false, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Skip"));
    BooleanSetting h = this.registerBoolean("Keep Floor", false, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Vanilla"));
    ModeSetting bound = this.registerMode("Bounds", PhaseUtil.bound, "Min", () -> ((String)this.mode.getValue()).equalsIgnoreCase("NCP") || ((String)this.mode.getValue()).equalsIgnoreCase("Skip") && (Boolean)this.bounded.getValue() != false);
    BooleanSetting twoBeePvP = this.registerBoolean("2b2tpvp", false, () -> ((String)this.mode.getValue()).equalsIgnoreCase("NCP"));
    BooleanSetting update = this.registerBoolean("Update Pos", false, () -> ((String)this.mode.getValue()).equalsIgnoreCase("NCP"));
    BooleanSetting clipCheck = this.registerBoolean("Clipped Check", false, () -> !((String)this.mode.getValue()).equalsIgnoreCase("Skip"));
    BooleanSetting sprint = this.registerBoolean("Sprint Force Enable", true, () -> !((String)this.mode.getValue()).equalsIgnoreCase("Skip"));
    int tpid = 0;
    boolean clipped = false;
    @EventHandler
    private final Listener<BoundingBoxEvent> boundingBoxEventListener = new Listener<BoundingBoxEvent>(event -> {
        try {
            if (((String)this.mode.getValue()).equalsIgnoreCase("Vanilla") && (this.clipped || !((Boolean)this.clipCheck.getValue()).booleanValue() || Phase.mc.field_71474_y.field_151444_V.func_151470_d() && ((Boolean)this.sprint.getValue()).booleanValue()) && (event.getPos().field_72448_b >= Phase.mc.field_71439_g.func_174791_d().field_72448_b || !((Boolean)this.h.getValue()).booleanValue() || Phase.mc.field_71474_y.field_74311_E.func_151470_d())) {
                event.setbb(Block.field_185506_k);
            }
        }
        catch (Exception e) {
            MessageBus.sendClientPrefixMessage(e.getMessage());
            this.disable();
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<PacketEvent.Receive>(event -> {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.tpid = ((SPacketPlayerPosLook)event.getPacket()).field_186966_g;
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener = new Listener<PacketEvent.Send>(event -> {
        if (event.getPacket() instanceof CPacketPlayer.PositionRotation || event.getPacket() instanceof CPacketPlayer.Position) {
            ++this.tpid;
        }
    }, new Predicate[0]);

    @Override
    public void onUpdate() {
        this.clipped = PlayerUtil.isPlayerClipped();
        if (((Phase.mc.field_71439_g.field_70123_F || Phase.mc.field_71474_y.field_74311_E.func_151470_d()) && ((String)this.mode.getValue()).equalsIgnoreCase("NCP") && !ModuleManager.getModule(Flight.class).isEnabled() && ((String)this.mode.getValue()).equalsIgnoreCase("NCP") && (this.clipped || !((Boolean)this.clipCheck.getValue()).booleanValue()) || Phase.mc.field_71474_y.field_151444_V.func_151470_d() && ((Boolean)this.sprint.getValue()).booleanValue() && Phase.mc.field_71439_g.field_70123_F) && ((String)this.mode.getValue()).equalsIgnoreCase("NCP")) {
            this.packetFly();
        }
        if (((String)this.mode.getValue()).equalsIgnoreCase("Skip")) {
            this.skip();
        }
    }

    void skip() {
        double[] dir = MotionUtil.forward(1.0, Math.round(Phase.mc.field_71439_g.field_70177_z / 8.0f) * 8);
        if (!Phase.mc.field_71439_g.field_70123_F || Phase.mc.field_71439_g.field_70159_w + Phase.mc.field_71439_g.field_70179_y != 0.0) {
            return;
        }
        float i = 0.1f;
        while (i < 1.0f) {
            double dirX = dir[0] * (double)i;
            double dirZ = dir[1] * (double)i;
            if (!Phase.mc.field_71441_e.func_184143_b(Phase.mc.field_71439_g.func_174813_aQ().func_72317_d(dirX, 0.0, dirZ))) {
                double[] safetyDir = MotionUtil.forward((double)i + (Double)this.safety.getValue(), Math.round((Phase.mc.field_71439_g.field_70177_z + Phase.mc.field_71439_g.field_70702_br * 45.0f + (float)(Phase.mc.field_71439_g.field_191988_bg < 0.0f ? 180 : 0)) / 8.0f) * 8);
                Phase.mc.field_71439_g.func_70107_b(Phase.mc.field_71439_g.field_70165_t + safetyDir[0], Phase.mc.field_71439_g.field_70163_u, Phase.mc.field_71439_g.field_70161_v + safetyDir[1]);
                if (((Boolean)this.bounded.getValue()).booleanValue()) {
                    PhaseUtil.doBounds((String)this.bound.getValue(), true);
                }
                Phase.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketConfirmTeleport(this.tpid - 1));
                Phase.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketConfirmTeleport(this.tpid));
                Phase.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketConfirmTeleport(this.tpid + 1));
            } else {
                MessageBus.sendClientPrefixMessageWithID("Pos: " + dirX + " " + dirZ, 70);
            }
            i = (float)((double)i + 0.1);
        }
    }

    void packetFly() {
        double[] clip = MotionUtil.forward(0.0624);
        if (Phase.mc.field_71474_y.field_74311_E.func_151470_d() && Phase.mc.field_71439_g.field_70122_E) {
            this.tp(0.0, -0.0624, 0.0, false);
        } else {
            this.tp(clip[0], 0.0, clip[1], true);
        }
    }

    void tp(double x, double y, double z, boolean onGround) {
        double[] dir = MotionUtil.forward(-0.0312);
        if (((Boolean)this.twoBeePvP.getValue()).booleanValue()) {
            Phase.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(Phase.mc.field_71439_g.field_70165_t + dir[0], Phase.mc.field_71439_g.field_70163_u, Phase.mc.field_71439_g.field_70161_v + dir[1], onGround));
        }
        Phase.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(((Boolean)this.twoBeePvP.getValue() != false ? x / 2.0 : x) + Phase.mc.field_71439_g.field_70165_t, y + Phase.mc.field_71439_g.field_70163_u, ((Boolean)this.twoBeePvP.getValue() != false ? z / 2.0 : z) + Phase.mc.field_71439_g.field_70161_v, onGround));
        Phase.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketConfirmTeleport(this.tpid - 1));
        Phase.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketConfirmTeleport(this.tpid));
        Phase.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketConfirmTeleport(this.tpid + 1));
        PhaseUtil.doBounds((String)this.bound.getValue(), true);
        if (((Boolean)this.update.getValue()).booleanValue()) {
            Phase.mc.field_71439_g.func_70107_b(x, y, z);
        }
    }
}

