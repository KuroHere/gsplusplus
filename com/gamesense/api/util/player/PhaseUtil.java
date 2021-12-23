/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.player;

import com.gamesense.api.util.world.MotionUtil;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

public class PhaseUtil {
    public static List<String> bound = Arrays.asList("Up", "Alternate", "Down", "Zero", "Min", "Forward", "Flat", "LimitJitter", "Constrict", "None");
    public static String normal = "Forward";
    private static final Minecraft mc = Minecraft.func_71410_x();

    public static CPacketPlayer doBounds(String mode, boolean send) {
        CPacketPlayer.PositionRotation packet = new CPacketPlayer.PositionRotation(0.0, 0.0, 0.0, 0.0f, 0.0f, false);
        switch (mode) {
            case "Up": {
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.field_71439_g.field_70165_t, PhaseUtil.mc.field_71439_g.field_70163_u + 69420.0, PhaseUtil.mc.field_71439_g.field_70161_v, PhaseUtil.mc.field_71439_g.field_70177_z, PhaseUtil.mc.field_71439_g.field_70125_A, false);
                break;
            }
            case "Down": {
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.field_71439_g.field_70165_t, PhaseUtil.mc.field_71439_g.field_70163_u - 69420.0, PhaseUtil.mc.field_71439_g.field_70161_v, PhaseUtil.mc.field_71439_g.field_70177_z, PhaseUtil.mc.field_71439_g.field_70125_A, false);
                break;
            }
            case "Zero": {
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.field_71439_g.field_70165_t, 0.0, PhaseUtil.mc.field_71439_g.field_70161_v, PhaseUtil.mc.field_71439_g.field_70177_z, PhaseUtil.mc.field_71439_g.field_70125_A, false);
                break;
            }
            case "Min": {
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.field_71439_g.field_70165_t, PhaseUtil.mc.field_71439_g.field_70163_u + 100.0, PhaseUtil.mc.field_71439_g.field_70161_v, PhaseUtil.mc.field_71439_g.field_70177_z, PhaseUtil.mc.field_71439_g.field_70125_A, false);
                break;
            }
            case "Alternate": {
                if (PhaseUtil.mc.field_71439_g.field_70173_aa % 2 == 0) {
                    packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.field_71439_g.field_70165_t, PhaseUtil.mc.field_71439_g.field_70163_u + 69420.0, PhaseUtil.mc.field_71439_g.field_70161_v, PhaseUtil.mc.field_71439_g.field_70177_z, PhaseUtil.mc.field_71439_g.field_70125_A, false);
                    break;
                }
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.field_71439_g.field_70165_t, PhaseUtil.mc.field_71439_g.field_70163_u - 69420.0, PhaseUtil.mc.field_71439_g.field_70161_v, PhaseUtil.mc.field_71439_g.field_70177_z, PhaseUtil.mc.field_71439_g.field_70125_A, false);
                break;
            }
            case "Forward": {
                double[] dir = MotionUtil.forward(67.0);
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.field_71439_g.field_70165_t + dir[0], PhaseUtil.mc.field_71439_g.field_70163_u + 33.4, PhaseUtil.mc.field_71439_g.field_70161_v + dir[1], PhaseUtil.mc.field_71439_g.field_70177_z, PhaseUtil.mc.field_71439_g.field_70125_A, false);
                break;
            }
            case "Flat": {
                double[] dir = MotionUtil.forward(100.0);
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.field_71439_g.field_70165_t + dir[0], PhaseUtil.mc.field_71439_g.field_70163_u, PhaseUtil.mc.field_71439_g.field_70161_v + dir[1], PhaseUtil.mc.field_71439_g.field_70177_z, PhaseUtil.mc.field_71439_g.field_70125_A, false);
                break;
            }
            case "Constrict": {
                double[] dir = MotionUtil.forward(67.0);
                packet = new CPacketPlayer.PositionRotation(PhaseUtil.mc.field_71439_g.field_70165_t + dir[0], PhaseUtil.mc.field_71439_g.field_70163_u + (PhaseUtil.mc.field_71439_g.field_70163_u > 64.0 ? -33.4 : 33.4), PhaseUtil.mc.field_71439_g.field_70161_v + dir[1], PhaseUtil.mc.field_71439_g.field_70177_z, PhaseUtil.mc.field_71439_g.field_70125_A, false);
            }
        }
        PhaseUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)packet);
        return packet;
    }
}

