/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.PlayerMoveEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.PhaseUtil;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.MotionUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.PlayerTweaks;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(name="Flight", category=Category.Movement)
public class Flight
extends Module {
    BooleanSetting autoSpeed = this.registerBoolean("WalkSpeed", false);
    public ModeSetting mode = this.registerMode("Mode", Arrays.asList("Vanilla", "Static", "Packet"), "Static");
    BooleanSetting damage = this.registerBoolean("Damage", false, () -> !((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    BooleanSetting jump = this.registerBoolean("Jump", false, () -> !((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    DoubleSetting speed = this.registerDouble("Speed", 2.0, 0.0, 10.0, () -> !((String)this.mode.getValue()).equalsIgnoreCase("Packet") && (Boolean)this.autoSpeed.getValue() == false);
    DoubleSetting ySpeed = this.registerDouble("Y Speed", 1.0, 0.0, 10.0, () -> !((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    DoubleSetting glideSpeed = this.registerDouble("Glide Speed", 0.0, -10.0, 10.0, () -> !((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    BooleanSetting antiKickFlight = this.registerBoolean("AntiKick", false, () -> !((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    IntegerSetting forceY = this.registerInteger("Force Y", 120, -1, 256, () -> !((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    DoubleSetting packetSpeed = this.registerDouble("Packet Speed", 1.0, 0.0, 10.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Packet") && (Boolean)this.autoSpeed.getValue() == false);
    DoubleSetting packetFactor = this.registerDouble("Packet Factor", 1.0, 1.0, 3.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    DoubleSetting packetY = this.registerDouble("Packet Y Speed", 1.0, 0.0, 5.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    ModeSetting bound = this.registerMode("Bounds", PhaseUtil.bound, PhaseUtil.normal, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    BooleanSetting wait = this.registerBoolean("Freeze", false, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    DoubleSetting reduction = this.registerDouble("Reduction", 0.5, 0.0, 1.0, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    BooleanSetting restrict = this.registerBoolean("Restrict Packets", false, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    ModeSetting antiKick = this.registerMode("AntiKick", Arrays.asList("None", "Down", "Bounce"), "Bounce", () -> ((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    IntegerSetting antiKickFreq = this.registerInteger("AntiKick Frequency", 4, 2, 8, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    BooleanSetting confirm = this.registerBoolean("Confirm IDs", false, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    BooleanSetting extra = this.registerBoolean("Extra IDs", false, () -> (Boolean)this.confirm.getValue());
    BooleanSetting debug = this.registerBoolean("Debug IDs", false, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Packet") && (Boolean)this.confirm.getValue() != false);
    BooleanSetting jitter = this.registerBoolean("Jitter", false, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    IntegerSetting jitterness = this.registerInteger("Jitter Amount", 6, 1, 16, () -> (Boolean)this.jitter.getValue());
    BooleanSetting speedup = this.registerBoolean("Accelerate", false, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    IntegerSetting speedTicks = this.registerInteger("Accelerate Ticks", 3, 1, 20, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Packet") && (Boolean)this.speedup.getValue() != false);
    BooleanSetting debugPackets = this.registerBoolean("Debug Packets", false, () -> ((String)this.mode.getValue()).equalsIgnoreCase("Packet"));
    BooleanSetting noclip = this.registerBoolean("NoClip", false);
    int tpid;
    float flyspeed;
    List<CPacketPlayer> packetList = new NonNullList<CPacketPlayer>(){};
    float mlt;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener = new Listener<PacketEvent.Send>(event -> {
        if (event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.PositionRotation) {
            ++this.tpid;
        }
        if (event.getPacket() instanceof CPacketPlayer && ((String)this.mode.getValue()).equalsIgnoreCase("Packet")) {
            CPacketPlayer packet = (CPacketPlayer)event.getPacket();
            if (((Boolean)this.debugPackets.getValue()).booleanValue()) {
                MessageBus.sendClientRawMessage(this.packetList.toString());
            }
            if (this.packetList.contains(packet) || !((Boolean)this.restrict.getValue()).booleanValue()) {
                this.packetList.remove(packet);
                ((CPacketPlayer)event.getPacket()).field_149473_f = 0.0f;
                ((CPacketPlayer)event.getPacket()).field_149476_e = 0.0f;
            } else {
                event.cancel();
            }
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
        if (!PlayerUtil.nullCheck()) {
            return;
        }
        if (!((String)this.mode.getValue()).equals("Packet") && (Integer)this.forceY.getValue() != -1 && Flight.mc.field_71439_g.field_70163_u != (double)((Integer)this.forceY.getValue()).intValue()) {
            Flight.mc.field_71439_g.func_70107_b(Flight.mc.field_71439_g.field_70165_t, (double)((Integer)this.forceY.getValue()).intValue(), Flight.mc.field_71439_g.field_70161_v);
        }
        if (((String)this.mode.getValue()).equalsIgnoreCase("Vanilla")) {
            Flight.mc.field_71439_g.field_71075_bZ.func_75092_a(this.flyspeed * ((Double)this.speed.getValue()).floatValue());
            Flight.mc.field_71439_g.field_71075_bZ.field_75100_b = true;
            if (((Boolean)this.antiKickFlight.getValue()).booleanValue() && Flight.mc.field_71439_g.field_70173_aa % 4 == 0 && !Flight.mc.field_71439_g.field_70122_E) {
                Flight.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(Flight.mc.field_71439_g.field_70165_t, Flight.mc.field_71439_g.field_70163_u - 0.01, Flight.mc.field_71439_g.field_70161_v, false));
                Flight.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(Flight.mc.field_71439_g.field_70165_t, Flight.mc.field_71439_g.field_70163_u, Flight.mc.field_71439_g.field_70161_v, false));
            }
        } else if (((String)this.mode.getValue()).equalsIgnoreCase("Static")) {
            if (Flight.mc.field_71474_y.field_74314_A.func_151470_d()) {
                event.setY((Double)this.ySpeed.getValue());
            } else if (Flight.mc.field_71474_y.field_74311_E.func_151470_d()) {
                event.setY(-((Double)this.ySpeed.getValue()).doubleValue());
            } else {
                event.setY(-((Double)this.glideSpeed.getValue()).doubleValue());
            }
            if (((Boolean)this.jump.getValue()).booleanValue() && Flight.mc.field_71439_g.field_70122_E && event.getY() == 0.0) {
                event.setY(0.42);
            }
            if (MotionUtil.isMoving((EntityLivingBase)Flight.mc.field_71439_g)) {
                double[] dir = MotionUtil.forward((Boolean)this.autoSpeed.getValue() != false ? MotionUtil.getBaseMoveSpeed() : (Double)this.speed.getValue());
                event.setX(dir[0]);
                event.setZ(dir[1]);
            } else {
                event.setX(0.0);
                event.setZ(0.0);
            }
            if (((Boolean)this.antiKickFlight.getValue()).booleanValue() && Flight.mc.field_71439_g.field_70173_aa % 4 == 0 && !Flight.mc.field_71439_g.field_70122_E) {
                Flight.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(Flight.mc.field_71439_g.field_70165_t, Flight.mc.field_71439_g.field_70163_u - 0.01, Flight.mc.field_71439_g.field_70161_v, false));
                Flight.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(Flight.mc.field_71439_g.field_70165_t, Flight.mc.field_71439_g.field_70163_u, Flight.mc.field_71439_g.field_70161_v, false));
            }
        } else if (((String)this.mode.getValue()).equalsIgnoreCase("Packet")) {
            event.setY(0.0);
            if (((Boolean)this.wait.getValue()).booleanValue()) {
                event.setX(0.0);
                event.setZ(0.0);
            }
            double x = 0.0;
            double y = 0.0;
            double z = 0.0;
            if (Flight.mc.field_71474_y.field_74311_E.func_151470_d() && !Flight.mc.field_71474_y.field_74314_A.func_151470_d()) {
                y -= PlayerUtil.isPlayerClipped() ? 0.0624 : 0.0624 * (Double)this.packetY.getValue();
            }
            if (Flight.mc.field_71474_y.field_74314_A.func_151470_d() && !MotionUtil.isMoving((EntityLivingBase)Flight.mc.field_71439_g)) {
                y += PlayerUtil.isPlayerClipped() ? 0.0624 : 0.0624 * (Double)this.packetY.getValue();
            }
            if (Flight.mc.field_71474_y.field_74351_w.func_151470_d() || Flight.mc.field_71474_y.field_74368_y.func_151470_d() || Flight.mc.field_71474_y.field_74370_x.func_151470_d() || Flight.mc.field_71474_y.field_74366_z.func_151470_d()) {
                double[] dir = MotionUtil.forward(PlayerUtil.isPlayerClipped() ? 0.0624 : ((Boolean)this.autoSpeed.getValue() != false ? MotionUtil.getBaseMoveSpeed() : ((Double)this.packetSpeed.getValue() == 0.0 ? 0.624 : 0.0624 * (Double)this.packetSpeed.getValue())));
                if (PlayerUtil.isPlayerClipped()) {
                    Flight.mc.field_71439_g.field_70159_w = MotionUtil.forward(0.0624)[0];
                    Flight.mc.field_71439_g.field_70179_y = MotionUtil.forward(0.0624)[1];
                }
                x += dir[0];
                z += dir[1];
            }
            if (Flight.mc.field_71441_e.func_175623_d(new BlockPos(Flight.mc.field_71439_g.func_174791_d()).func_177963_a(0.0, -0.1, 0.0))) {
                if (!((String)this.antiKick.getValue()).equalsIgnoreCase("None") && Flight.mc.field_71439_g.field_70173_aa % (Integer)this.antiKickFreq.getValue() == 0 && !Flight.mc.field_71439_g.field_70122_E) {
                    y -= 0.01;
                } else if (((String)this.antiKick.getValue()).equalsIgnoreCase("Bounce") && Flight.mc.field_71439_g.field_70173_aa % (Integer)this.antiKickFreq.getValue() == 1 && !Flight.mc.field_71439_g.field_70122_E && !MotionUtil.isMoving((EntityLivingBase)Flight.mc.field_71439_g)) {
                    y += 0.01;
                }
            }
            if (((Boolean)this.jitter.getValue()).booleanValue() && Flight.mc.field_71439_g.field_70173_aa % (Integer)this.jitterness.getValue() == 0) {
                Flight.mc.field_71439_g.func_70016_h(0.0, 0.0, 0.0);
                return;
            }
            if (Flight.mc.field_71439_g.field_70159_w == 0.0 && Flight.mc.field_71439_g.field_70179_y == 0.0) {
                this.mlt = 0.0f;
            }
            if (this.mlt < 1.0f) {
                this.mlt += 1.0f / (float)((Integer)this.speedTicks.getValue()).intValue();
            }
            if (this.mlt > 1.0f) {
                this.mlt = 1.0f;
            }
            if (((Boolean)this.speedup.getValue()).booleanValue()) {
                x *= (double)this.mlt;
                z *= (double)this.mlt;
            }
            NonNullList packet = NonNullList.func_191196_a();
            int i = 0;
            while ((double)i < Math.floor((Double)this.packetFactor.getValue())) {
                packet.add(new CPacketPlayer.PositionRotation(x * (double)(i + 1) + Flight.mc.field_71439_g.field_70165_t, y * (double)(i + 1) + Flight.mc.field_71439_g.field_70163_u, z * (double)(i + 1) + Flight.mc.field_71439_g.field_70161_v, Flight.mc.field_71439_g.field_70177_z, Flight.mc.field_71439_g.field_70125_A, false));
                ++i;
            }
            if ((Double)this.packetFactor.getValue() != Math.floor((Double)this.packetFactor.getValue())) {
                packet.add(new CPacketPlayer.PositionRotation(x * (Double)this.packetFactor.getValue() + Flight.mc.field_71439_g.field_70165_t, y * y > 0.0 ? (Double)this.packetFactor.getValue() : 1.0 + Flight.mc.field_71439_g.field_70163_u, z * (Double)this.packetFactor.getValue() + Flight.mc.field_71439_g.field_70161_v, Flight.mc.field_71439_g.field_70177_z, Flight.mc.field_71439_g.field_70125_A, false));
            }
            for (CPacketPlayer pkt : packet) {
                this.packetList.add(pkt);
                Flight.mc.field_71439_g.field_71174_a.func_147297_a((Packet)pkt);
            }
            if (((Boolean)this.confirm.getValue()).booleanValue()) {
                if (((Boolean)this.extra.getValue()).booleanValue()) {
                    Flight.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketConfirmTeleport(this.tpid - 1));
                }
                Flight.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketConfirmTeleport(this.tpid));
                if (((Boolean)this.extra.getValue()).booleanValue()) {
                    Flight.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketConfirmTeleport(this.tpid + 1));
                }
            }
            Flight.mc.field_71439_g.func_70016_h(x * (Double)this.reduction.getValue() * (Double)this.packetFactor.getValue(), y * (Double)this.reduction.getValue() * (Double)this.packetFactor.getValue(), z * (Double)this.reduction.getValue() * (Double)this.packetFactor.getValue());
            CPacketPlayer bounds = PhaseUtil.doBounds((String)this.bound.getValue(), false);
            this.packetList.add(bounds);
            Flight.mc.field_71439_g.field_71174_a.func_147297_a((Packet)bounds);
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<PacketEvent.Receive>(event -> {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            if (((Boolean)this.confirm.getValue()).booleanValue() && ((Boolean)this.debug.getValue()).booleanValue()) {
                MessageBus.sendClientPrefixMessageWithID(this.tpid - ((SPacketPlayerPosLook)event.getPacket()).field_186966_g + "", 69420);
            }
            this.tpid = ((SPacketPlayerPosLook)event.getPacket()).field_186966_g;
            ((SPacketPlayerPosLook)event.getPacket()).func_179834_f().remove(SPacketPlayerPosLook.EnumFlags.X_ROT);
            ((SPacketPlayerPosLook)event.getPacket()).func_179834_f().remove(SPacketPlayerPosLook.EnumFlags.Y_ROT);
        }
    }, new Predicate[0]);

    @Override
    public void onUpdate() {
        Flight.mc.field_71439_g.field_70145_X = (Boolean)this.noclip.getValue();
    }

    @Override
    protected void onEnable() {
        if (Flight.mc.field_71441_e == null || Flight.mc.field_71439_g == null) {
            return;
        }
        this.flyspeed = Flight.mc.field_71439_g.field_71075_bZ.func_75093_a();
        if (((Boolean)this.damage.getValue()).booleanValue() && !((String)this.mode.getValue()).equalsIgnoreCase("Packet")) {
            ModuleManager.getModule(PlayerTweaks.class).pauseNoFallPacket = true;
            for (int i = 0; i < 64; ++i) {
                Flight.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(Flight.mc.field_71439_g.field_70165_t, Flight.mc.field_71439_g.field_70163_u + 0.049, Flight.mc.field_71439_g.field_70161_v, false));
                Flight.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(Flight.mc.field_71439_g.field_70165_t, Flight.mc.field_71439_g.field_70163_u, Flight.mc.field_71439_g.field_70161_v, false));
            }
            Flight.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(Flight.mc.field_71439_g.field_70165_t, Flight.mc.field_71439_g.field_70163_u, Flight.mc.field_71439_g.field_70161_v, true));
            Flight.mc.field_71439_g.field_70143_R = 3.1f;
        }
    }

    @Override
    protected void onDisable() {
        Flight.mc.field_71439_g.field_71075_bZ.func_75092_a(this.flyspeed);
        Flight.mc.field_71439_g.field_71075_bZ.field_75100_b = false;
        Flight.mc.field_71439_g.field_70179_y = 0.0;
        Flight.mc.field_71439_g.field_70181_x = 0.0;
        Flight.mc.field_71439_g.field_70159_w = 0.0;
        Flight.mc.field_71439_g.field_70145_X = false;
    }
}

