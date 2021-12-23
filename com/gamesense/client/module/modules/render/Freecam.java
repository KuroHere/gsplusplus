/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.PlayerMoveEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.util.world.MotionUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;

@Module.Declaration(name="Freecam", category=Category.Render)
public class Freecam
extends Module {
    BooleanSetting source = this.registerBoolean("Source Engine", false);
    BooleanSetting noclip = this.registerBoolean("NoClip", true);
    BooleanSetting cancelPackets = this.registerBoolean("Cancel Packets", true);
    DoubleSetting speed = this.registerDouble("Speed", 10.0, 0.0, 20.0);
    private double posX;
    private double posY;
    private double posZ;
    private float pitch;
    private float yaw;
    private EntityOtherPlayerMP clonedPlayer;
    private boolean isRidingEntity;
    private Entity ridingEntity;
    @EventHandler
    private final Listener<PlayerMoveEvent> moveListener = new Listener<PlayerMoveEvent>(event -> {
        Freecam.mc.field_71439_g.field_70145_X = (Boolean)this.noclip.getValue();
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PlayerSPPushOutOfBlocksEvent> pushListener = new Listener<PlayerSPPushOutOfBlocksEvent>(event -> event.setCanceled(true), new Predicate[0]);
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener = new Listener<PacketEvent.Send>(event -> {
        if ((event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketInput) && ((Boolean)this.cancelPackets.getValue()).booleanValue()) {
            event.cancel();
        }
    }, new Predicate[0]);

    @Override
    public void onEnable() {
        if (Freecam.mc.field_71439_g != null) {
            boolean bl = this.isRidingEntity = Freecam.mc.field_71439_g.func_184187_bx() != null;
            if (Freecam.mc.field_71439_g.func_184187_bx() == null) {
                this.posX = Freecam.mc.field_71439_g.field_70165_t;
                this.posY = Freecam.mc.field_71439_g.field_70163_u;
                this.posZ = Freecam.mc.field_71439_g.field_70161_v;
            } else {
                this.ridingEntity = Freecam.mc.field_71439_g.func_184187_bx();
                Freecam.mc.field_71439_g.func_184210_p();
            }
            this.pitch = Freecam.mc.field_71439_g.field_70125_A;
            this.yaw = Freecam.mc.field_71439_g.field_70177_z;
            this.clonedPlayer = new EntityOtherPlayerMP((World)Freecam.mc.field_71441_e, mc.func_110432_I().func_148256_e());
            this.clonedPlayer.func_82149_j((Entity)Freecam.mc.field_71439_g);
            this.clonedPlayer.field_70759_as = Freecam.mc.field_71439_g.field_70759_as;
            Freecam.mc.field_71441_e.func_73027_a(-100, (Entity)this.clonedPlayer);
            Freecam.mc.field_71439_g.field_70145_X = (Boolean)this.noclip.getValue();
        }
    }

    @Override
    public void onDisable() {
        EntityPlayerSP localPlayer = Freecam.mc.field_71439_g;
        if (localPlayer != null) {
            Freecam.mc.field_71439_g.func_70080_a(this.posX, this.posY, this.posZ, this.yaw, this.pitch);
            Freecam.mc.field_71441_e.func_73028_b(-100);
            this.clonedPlayer = null;
            this.posZ = 0.0;
            this.posY = 0.0;
            this.posX = 0.0;
            this.yaw = 0.0f;
            this.pitch = 0.0f;
            Freecam.mc.field_71439_g.field_70145_X = false;
            Freecam.mc.field_71439_g.field_70179_y = 0.0;
            Freecam.mc.field_71439_g.field_70181_x = 0.0;
            Freecam.mc.field_71439_g.field_70159_w = 0.0;
            if (this.isRidingEntity) {
                Freecam.mc.field_71439_g.func_184205_a(this.ridingEntity, true);
            }
        }
    }

    @Override
    public void onUpdate() {
        Freecam.mc.field_71439_g.field_70122_E = true;
        if (!((Boolean)this.source.getValue()).booleanValue()) {
            Freecam.mc.field_71439_g.field_70181_x = Freecam.mc.field_71474_y.field_74314_A.func_151470_d() ? (Double)this.speed.getValue() : (Freecam.mc.field_71474_y.field_74311_E.func_151470_d() ? -((Double)this.speed.getValue()).doubleValue() : 0.0);
        } else {
            double pitchRad = (double)Freecam.mc.field_71439_g.field_70125_A * Math.PI / 180.0;
            Freecam.mc.field_71439_g.field_70181_x = MotionUtil.isMoving((EntityLivingBase)Freecam.mc.field_71439_g) ? -Math.sin(pitchRad) * (Double)this.speed.getValue() : 0.0;
        }
        if (MotionUtil.isMoving((EntityLivingBase)Freecam.mc.field_71439_g)) {
            MotionUtil.setSpeed((EntityLivingBase)Freecam.mc.field_71439_g, (Double)this.speed.getValue());
        } else {
            Freecam.mc.field_71439_g.field_70159_w = 0.0;
            Freecam.mc.field_71439_g.field_70179_y = 0.0;
        }
        Freecam.mc.field_71439_g.field_70145_X = (Boolean)this.noclip.getValue();
        Freecam.mc.field_71439_g.field_70122_E = false;
        Freecam.mc.field_71439_g.field_70143_R = 0.0f;
    }

    public static double degToRad(double deg) {
        return deg * 0.01745329238474369;
    }
}

