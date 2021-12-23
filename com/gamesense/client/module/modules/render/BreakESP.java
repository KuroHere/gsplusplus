/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.DrawBlockDamageEvent;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Module.Declaration(name="BreakESP", category=Category.Render)
public class BreakESP
extends Module {
    ModeSetting renderType = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
    IntegerSetting lineWidth = this.registerInteger("Width", 1, 0, 5);
    IntegerSetting range = this.registerInteger("Range", 100, 1, 200);
    BooleanSetting showPacket = this.registerBoolean("Show possible packet mine", false);
    IntegerSetting tickPacket = this.registerInteger("Tick Packet", 50, 0, 200, () -> (Boolean)this.showPacket.getValue());
    IntegerSetting stillRender = this.registerInteger("Still Render", 20, 0, 500, () -> (Boolean)this.showPacket.getValue());
    BooleanSetting cancelAnimation = this.registerBoolean("No Animation", true);
    BooleanSetting showColor = this.registerBoolean("Show Color", true);
    ColorSetting colorNotReady = this.registerColor("Color Not Ready", new GSColor(255, 0, 0, 255), () -> (Boolean)this.showColor.getValue());
    ColorSetting colorReady = this.registerColor("Color Ready", new GSColor(0, 255, 0, 255), () -> (Boolean)this.showColor.getValue());
    BooleanSetting showPercentage = this.registerBoolean("Show Percentage", false);
    ColorSetting textColor = this.registerColor("Text Color", new GSColor(255, 255, 255), () -> (Boolean)this.showPercentage.getValue());
    ArrayList<ArrayList<Object>> possiblePacket = new ArrayList();
    @EventHandler
    private final Listener<PacketEvent.Receive> packetReceiveListener = new Listener<PacketEvent.Receive>(event -> {
        SPacketBlockBreakAnim pack;
        if (!((Boolean)this.showPacket.getValue()).booleanValue()) {
            return;
        }
        if (event.getPacket() instanceof SPacketBlockBreakAnim && !this.havePos((pack = (SPacketBlockBreakAnim)event.getPacket()).func_179821_b())) {
            this.possiblePacket.add(new ArrayList<Object>(){
                {
                    this.add(pack.func_179821_b());
                    this.add(0);
                }
            });
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<DrawBlockDamageEvent> drawBlockDamageEventListener = new Listener<DrawBlockDamageEvent>(event -> {
        if (((Boolean)this.cancelAnimation.getValue()).booleanValue()) {
            event.cancel();
        }
    }, new Predicate[0]);

    boolean havePos(BlockPos pos) {
        for (ArrayList<Object> part : this.possiblePacket) {
            BlockPos temp = (BlockPos)part.get(0);
            if (temp.func_177958_n() != pos.func_177958_n() || temp.func_177956_o() != pos.func_177956_o() || temp.func_177952_p() != pos.func_177952_p()) continue;
            return true;
        }
        return false;
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        ArrayList displayed = new ArrayList();
        BreakESP.mc.field_71438_f.field_72738_E.forEach((integer, destroyBlockProgress) -> {
            if (destroyBlockProgress != null) {
                BlockPos blockPos = destroyBlockProgress.func_180246_b();
                if (BreakESP.mc.field_71441_e.func_180495_p(blockPos).func_177230_c() == Blocks.field_150350_a) {
                    return;
                }
                if (blockPos.func_185332_f((int)BreakESP.mc.field_71439_g.field_70165_t, (int)BreakESP.mc.field_71439_g.field_70163_u, (int)BreakESP.mc.field_71439_g.field_70161_v) <= (double)((Integer)this.range.getValue()).intValue()) {
                    displayed.add(blockPos);
                    int progress = destroyBlockProgress.func_73106_e();
                    AxisAlignedBB axisAlignedBB = BreakESP.mc.field_71441_e.func_180495_p(blockPos).func_185918_c((World)BreakESP.mc.field_71441_e, blockPos);
                    if (((Boolean)this.showColor.getValue()).booleanValue()) {
                        this.renderESP(axisAlignedBB, progress, progress >= 8 ? this.colorReady.getColor() : this.colorNotReady.getValue(), 8);
                    }
                    if (((Boolean)this.showPercentage.getValue()).booleanValue()) {
                        float f;
                        String[] stringArray = new String[1];
                        Object[] objectArray = new Object[1];
                        float temp = (float)progress / 2.0f * 25.0f;
                        objectArray[0] = Float.valueOf(f >= 100.0f ? 100.0f : temp);
                        stringArray[0] = String.format("%.02f%%", objectArray);
                        this.showPercentage(blockPos, stringArray);
                    }
                }
            }
        });
        if (((Boolean)this.showPacket.getValue()).booleanValue()) {
            for (int i = 0; i < this.possiblePacket.size(); ++i) {
                BlockPos temp = (BlockPos)this.possiblePacket.get(i).get(0);
                int tick = (Integer)this.possiblePacket.get(i).get(1);
                if (BlockUtil.getBlock(temp) instanceof BlockAir) {
                    this.possiblePacket.remove(i);
                    --i;
                    continue;
                }
                if (!displayed.contains(temp)) {
                    if (temp.func_185332_f((int)BreakESP.mc.field_71439_g.field_70165_t, (int)BreakESP.mc.field_71439_g.field_70163_u, (int)BreakESP.mc.field_71439_g.field_70161_v) <= (double)((Integer)this.range.getValue()).intValue()) {
                        AxisAlignedBB axisAlignedBB = BreakESP.mc.field_71441_e.func_180495_p(temp).func_185918_c((World)BreakESP.mc.field_71441_e, temp);
                        this.renderESP(axisAlignedBB, tick >= (Integer)this.tickPacket.getValue() ? (Integer)this.tickPacket.getValue() : tick, tick > (Integer)this.tickPacket.getValue() ? this.colorReady.getColor() : this.colorNotReady.getValue(), (Integer)this.tickPacket.getValue());
                        if (((Boolean)this.showPercentage.getValue()).booleanValue()) {
                            this.showPercentage(temp, new String[]{String.format("%.02f%%", Float.valueOf((float)(tick >= (Integer)this.tickPacket.getValue() ? (Integer)this.tickPacket.getValue() : tick) / (float)((Integer)this.tickPacket.getValue()).intValue() * 100.0f))});
                        }
                    }
                } else {
                    this.possiblePacket.get(i).set(1, ++tick);
                }
                if (++tick > (Integer)this.tickPacket.getValue() + (Integer)this.stillRender.getValue()) {
                    this.possiblePacket.remove(i);
                    --i;
                    continue;
                }
                this.possiblePacket.get(i).set(1, tick);
            }
        }
    }

    void showPercentage(BlockPos pos, String[] perc) {
        RenderUtil.drawNametag((double)pos.func_177958_n() + 0.5, (double)pos.func_177956_o() + 0.5, (double)pos.func_177952_p() + 0.5, perc, this.textColor.getColor(), 1);
    }

    private void renderESP(AxisAlignedBB axisAlignedBB, int progress, GSColor color, int max) {
        GSColor fillColor = new GSColor(color, 50);
        GSColor outlineColor = new GSColor(color, 255);
        double centerX = axisAlignedBB.field_72340_a + (axisAlignedBB.field_72336_d - axisAlignedBB.field_72340_a) / 2.0;
        double centerY = axisAlignedBB.field_72338_b + (axisAlignedBB.field_72337_e - axisAlignedBB.field_72338_b) / 2.0;
        double centerZ = axisAlignedBB.field_72339_c + (axisAlignedBB.field_72334_f - axisAlignedBB.field_72339_c) / 2.0;
        double progressValX = (double)progress * ((axisAlignedBB.field_72336_d - centerX) / (double)max);
        double progressValY = (double)progress * ((axisAlignedBB.field_72337_e - centerY) / (double)max);
        double progressValZ = (double)progress * ((axisAlignedBB.field_72334_f - centerZ) / (double)max);
        AxisAlignedBB axisAlignedBB1 = new AxisAlignedBB(centerX - progressValX, centerY - progressValY, centerZ - progressValZ, centerX + progressValX, centerY + progressValY, centerZ + progressValZ);
        switch ((String)this.renderType.getValue()) {
            case "Fill": {
                RenderUtil.drawBox(axisAlignedBB1, true, 0.0, fillColor, 63);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(axisAlignedBB1, (double)((Integer)this.lineWidth.getValue()).intValue(), outlineColor);
                break;
            }
            default: {
                RenderUtil.drawBox(axisAlignedBB1, true, 0.0, fillColor, 63);
                RenderUtil.drawBoundingBox(axisAlignedBB1, (double)((Integer)this.lineWidth.getValue()).intValue(), outlineColor);
            }
        }
    }
}

