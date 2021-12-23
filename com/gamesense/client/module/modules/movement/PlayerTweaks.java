/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.EntityCollisionEvent;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.WaterPushEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.LongJump;
import com.gamesense.client.module.modules.movement.Timer;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.InputUpdateEvent;
import org.lwjgl.input.Keyboard;

@Module.Declaration(name="PlayerTweaks", category=Category.Movement)
public class PlayerTweaks
extends Module {
    boolean snk;
    boolean lastTickOG;
    public BooleanSetting guiMove = this.registerBoolean("Gui Move", false);
    public BooleanSetting noSlow = this.registerBoolean("No Slow", false);
    BooleanSetting strict = this.registerBoolean("No Slow Strict", false, () -> (Boolean)this.noSlow.getValue());
    BooleanSetting ice = this.registerBoolean("Ice Speed", false);
    DoubleSetting iceSpeed = this.registerDouble("Ice Slipperiness", 0.4, 0.0, 1.0, () -> (Boolean)this.ice.getValue());
    public BooleanSetting webT = this.registerBoolean("No Slow Web", false);
    public BooleanSetting noPushBlock = this.registerBoolean("No Push Block", false);
    public BooleanSetting portalChat = this.registerBoolean("Portal Chat", false);
    BooleanSetting noPushWater = this.registerBoolean("No Push Liquid", false);
    BooleanSetting noFall = this.registerBoolean("No Fall", false);
    ModeSetting noFallMode = this.registerMode("No Fall Mode", Arrays.asList("Packet", "OldFag", "Catch", "Glitch"), "Packet", () -> (Boolean)this.noFall.getValue());
    ModeSetting catchM = this.registerMode("Catch Material", Arrays.asList("Web", "Water"), "Water", () -> ((String)this.noFallMode.getValue()).equalsIgnoreCase("Catch"));
    BooleanSetting noFallDC = this.registerBoolean("Disconnect", false, () -> (Boolean)this.noFall.getValue());
    BooleanSetting antiKnockBack = this.registerBoolean("Velocity", false);
    BooleanSetting akbM = this.registerBoolean("Non 0 value", false);
    DoubleSetting veloXZ = this.registerDouble("XZ Multiplier", 0.0, -5.0, 5.0, () -> (Boolean)this.antiKnockBack.getValue() != false && (Boolean)this.akbM.getValue() != false);
    DoubleSetting veloY = this.registerDouble("Y Multiplier", 0.0, -5.0, 5.0, () -> (Boolean)this.antiKnockBack.getValue() != false && (Boolean)this.akbM.getValue() != false);
    BooleanSetting pistonPush = this.registerBoolean("Anti Piston Push", false);
    IntegerSetting postSecure = this.registerInteger("Post Secure", 15, 1, 40, () -> (Boolean)this.pistonPush.getValue());
    public boolean pauseNoFallPacket;
    Vec3d pos = new Vec3d(0.0, 0.0, 0.0);
    @EventHandler
    private final Listener<InputUpdateEvent> eventListener = new Listener<InputUpdateEvent>(event -> {
        if (PlayerTweaks.mc.field_71439_g.func_184587_cr() && !PlayerTweaks.mc.field_71439_g.func_184218_aH()) {
            if (((Boolean)this.strict.getValue()).booleanValue() && PlayerTweaks.mc.field_71439_g.field_71071_by.func_70448_g().field_151002_e instanceof ItemFood) {
                PlayerTweaks.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(PlayerTweaks.mc.field_71439_g.field_71071_by.field_70461_c));
            }
            PlayerTweaks.mc.field_71439_g.field_71158_b.field_192832_b = (float)((double)PlayerTweaks.mc.field_71439_g.field_71158_b.field_192832_b / 0.2);
            PlayerTweaks.mc.field_71439_g.field_71158_b.field_78902_a = (float)((double)PlayerTweaks.mc.field_71439_g.field_71158_b.field_78902_a / 0.2);
        }
        this.lastTickOG = PlayerTweaks.mc.field_71439_g.field_70122_E;
    }, new Predicate[0]);
    BooleanSetting noPush = this.registerBoolean("No Push", false);
    @EventHandler
    private final Listener<EntityCollisionEvent> entityCollisionEventListener = new Listener<EntityCollisionEvent>(event -> {
        if (((Boolean)this.noPush.getValue()).booleanValue()) {
            event.cancel();
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<WaterPushEvent> waterPushEventListener = new Listener<WaterPushEvent>(event -> {
        if (((Boolean)this.noPushWater.getValue()).booleanValue()) {
            event.cancel();
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<PacketEvent.Receive>(event -> {
        if (ModuleManager.getModule(LongJump.class).velo || !ModuleManager.getModule(LongJump.class).isEnabled()) {
            if (((Boolean)this.antiKnockBack.getValue()).booleanValue() && ((Boolean)this.akbM.getValue()).booleanValue()) {
                if (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity)event.getPacket()).func_149412_c() == PlayerTweaks.mc.field_71439_g.func_145782_y()) {
                    ((SPacketEntityVelocity)event.getPacket()).field_149415_b = (int)((double)((SPacketEntityVelocity)event.getPacket()).field_149415_b * (Double)this.veloXZ.getValue());
                    ((SPacketEntityVelocity)event.getPacket()).field_149416_c = (int)((double)((SPacketEntityVelocity)event.getPacket()).field_149416_c * (Double)this.veloY.getValue());
                    ((SPacketEntityVelocity)event.getPacket()).field_149414_d = (int)((double)((SPacketEntityVelocity)event.getPacket()).field_149414_d * (Double)this.veloXZ.getValue());
                }
                if (event.getPacket() instanceof SPacketExplosion) {
                    ((SPacketExplosion)event.getPacket()).field_149152_f = (float)((double)((SPacketExplosion)event.getPacket()).field_149152_f * (Double)this.veloXZ.getValue());
                    ((SPacketExplosion)event.getPacket()).field_149153_g = (float)((double)((SPacketExplosion)event.getPacket()).field_149153_g * (Double)this.veloY.getValue());
                    ((SPacketExplosion)event.getPacket()).field_149159_h = (float)((double)((SPacketExplosion)event.getPacket()).field_149159_h * (Double)this.veloXZ.getValue());
                }
            } else if (((Boolean)this.antiKnockBack.getValue()).booleanValue() && !((Boolean)this.akbM.getValue()).booleanValue()) {
                if (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity)event.getPacket()).func_149412_c() == PlayerTweaks.mc.field_71439_g.func_145782_y()) {
                    event.cancel();
                }
                if (event.getPacket() instanceof SPacketExplosion) {
                    event.cancel();
                }
            }
        }
    }, new Predicate[0]);
    int ticksBef;
    @EventHandler
    private final Listener<PacketEvent.Send> packetReceiveListener = new Listener<PacketEvent.Send>(event -> {
        if ((event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.PositionRotation) && HoleUtil.isHole(EntityUtil.getPosition((Entity)PlayerTweaks.mc.field_71439_g), true, true).getType() != HoleUtil.HoleType.NONE) {
            boolean found = this.isPushable(PlayerTweaks.mc.field_71439_g.field_70165_t, PlayerTweaks.mc.field_71439_g.field_70163_u, PlayerTweaks.mc.field_71439_g.field_70161_v);
            if (found) {
                event.cancel();
                this.ticksBef = (Integer)this.postSecure.getValue();
            } else if (--this.ticksBef > 0) {
                event.cancel();
            }
        }
    }, new Predicate[0]);
    BlockPos n1;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener = new Listener<PacketEvent.Send>(event -> {
        if (PlayerUtil.nullCheck()) {
            if (((Boolean)this.noFall.getValue()).booleanValue() && event.getPacket() instanceof CPacketPlayer && !PlayerTweaks.mc.field_71439_g.func_184613_cA()) {
                try {
                    PlayerTweaks.mc.field_71439_g.field_71174_a.func_147298_b().func_179293_l();
                    CPacketPlayer packet = (CPacketPlayer)event.getPacket();
                    if (((String)this.noFallMode.getValue()).equalsIgnoreCase("Packet")) {
                        if (PlayerTweaks.mc.field_71439_g.field_70122_E) {
                            return;
                        }
                        if (!this.pauseNoFallPacket) {
                            packet.field_149474_g = true;
                            PlayerTweaks.mc.field_71439_g.field_70143_R = 0.0f;
                        } else {
                            this.pauseNoFallPacket = false;
                        }
                    } else if (((String)this.noFallMode.getValue()).equalsIgnoreCase("OldFag")) {
                        if (this.predict(new BlockPos(PlayerTweaks.mc.field_71439_g.field_70165_t, PlayerTweaks.mc.field_71439_g.field_70163_u, PlayerTweaks.mc.field_71439_g.field_70161_v)) && PlayerTweaks.mc.field_71439_g.field_70143_R >= 3.0f) {
                            PlayerTweaks.mc.field_71439_g.field_70181_x = 0.0;
                            packet.field_149477_b = this.n1.func_177956_o();
                            PlayerTweaks.mc.field_71439_g.field_70143_R = 0.0f;
                        }
                    } else if (((String)this.noFallMode.getValue()).equalsIgnoreCase("Catch")) {
                        if (PlayerTweaks.mc.field_71439_g.field_70143_R >= 3.0f) {
                            int slot;
                            int oldSlot = PlayerTweaks.mc.field_71439_g.field_71071_by.field_70461_c;
                            int n = slot = ((String)this.catchM.getValue()).equalsIgnoreCase("Web") ? PlayerTweaks.getSlot(Blocks.field_150321_G) : PlayerTweaks.getSlot(Items.field_151131_as);
                            if (slot != -1) {
                                PlayerTweaks.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slot));
                                if (((String)this.catchM.getValue()).equalsIgnoreCase("Web")) {
                                    try {
                                        PlacementUtil.place(this.getDownPos(), EnumHand.MAIN_HAND, false);
                                    }
                                    catch (NullPointerException nullPointerException) {}
                                } else {
                                    PlayerTweaks.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(this.getDownPos(), EnumFacing.UP, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                                }
                                PlayerTweaks.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(oldSlot));
                            }
                        }
                    } else if (((String)this.noFallMode.getValue()).equalsIgnoreCase("Glitch")) {
                        if (PlayerTweaks.mc.field_71439_g.field_70143_R > 2.0f) {
                            PlayerTweaks.mc.field_71439_g.func_70107_b(this.pos.field_72450_a, this.pos.field_72448_b, this.pos.field_72449_c);
                        } else if (PlayerTweaks.mc.field_71439_g.field_70122_E) {
                            this.pos = PlayerTweaks.mc.field_71439_g.func_174791_d();
                        }
                    }
                }
                catch (Exception e) {
                    try {
                        MessageBus.sendClientPrefixMessageWithID(e.getMessage(), true);
                        for (StackTraceElement p : e.getStackTrace()) {
                            System.out.println(p.toString());
                        }
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
            if (((Boolean)this.noFallDC.getValue()).booleanValue() && (double)PlayerTweaks.mc.field_71439_g.field_70143_R - 2.1 >= (double)PlayerTweaks.mc.field_71439_g.func_110143_aJ()) {
                PlayerTweaks.mc.field_71439_g.field_71174_a.func_147298_b().func_150718_a((ITextComponent)new TextComponentString(ChatFormatting.GOLD + "Player would have taken fall damage"));
            }
        }
    }, new Predicate[0]);

    public static int getSlot(Block blockToFind) {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = PlayerTweaks.mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack == ItemStack.field_190927_a || !(stack.func_77973_b() instanceof ItemBlock) || !(block = ((ItemBlock)stack.func_77973_b()).func_179223_d()).equals(blockToFind)) continue;
            slot = i;
            break;
        }
        return slot;
    }

    public static int getSlot(Item blockToFind) {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = PlayerTweaks.mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack == ItemStack.field_190927_a || !(stack.func_77973_b() instanceof ItemBlock) || stack.func_77973_b() != blockToFind) continue;
            slot = i;
            break;
        }
        return slot;
    }

    boolean isPushable(double x, double y, double z) {
        Block temp = BlockUtil.getBlock(x, y += 1.0, z);
        if (temp == Blocks.field_150332_K || temp == Blocks.field_180384_M) {
            return true;
        }
        for (TileEntity entity : PlayerTweaks.mc.field_71441_e.field_147482_g) {
            AxisAlignedBB axisAlignedBB;
            TileEntityShulkerBox tileEntityShulkerBox;
            if (!(entity instanceof TileEntityShulkerBox)) continue;
            TileEntityShulkerBox tempShulker = (TileEntityShulkerBox)entity;
            if (!(tileEntityShulkerBox.func_190585_a(mc.func_184121_ak()) > 0.0f)) continue;
            AxisAlignedBB tempAxis = tempShulker.getRenderBoundingBox();
            if (!(axisAlignedBB.field_72338_b <= y && tempAxis.field_72337_e >= y && (double)((int)tempAxis.field_72340_a) <= x && tempAxis.field_72336_d >= x) && (!((double)((int)tempAxis.field_72339_c) <= z) || !(tempAxis.field_72334_f >= z))) continue;
            return true;
        }
        return false;
    }

    @Override
    protected void onDisable() {
        Blocks.field_150432_aD.setDefaultSlipperiness(0.4f);
        Blocks.field_150403_cj.setDefaultSlipperiness(0.4f);
        Blocks.field_185778_de.setDefaultSlipperiness(0.4f);
    }

    @Override
    public void onUpdate() {
        if (((Boolean)this.ice.getValue()).booleanValue()) {
            Blocks.field_150432_aD.setDefaultSlipperiness(((Double)this.iceSpeed.getValue()).floatValue());
            Blocks.field_150403_cj.setDefaultSlipperiness(((Double)this.iceSpeed.getValue()).floatValue());
            Blocks.field_185778_de.setDefaultSlipperiness(((Double)this.iceSpeed.getValue()).floatValue());
        } else {
            Blocks.field_150432_aD.setDefaultSlipperiness(0.4f);
            Blocks.field_150403_cj.setDefaultSlipperiness(0.4f);
            Blocks.field_185778_de.setDefaultSlipperiness(0.4f);
        }
        if (!ModuleManager.isModuleEnabled(Timer.class)) {
            if (PlayerTweaks.mc.field_71439_g.field_70134_J && !PlayerTweaks.mc.field_71439_g.field_70122_E && ((Boolean)this.webT.getValue()).booleanValue()) {
                PlayerTweaks.mc.field_71428_T.field_194149_e = 1.0f;
                PlayerTweaks.mc.field_71439_g.field_191988_bg = 0.0f;
                PlayerTweaks.mc.field_71439_g.field_70702_br = 0.0f;
            } else {
                PlayerTweaks.mc.field_71428_T.field_194149_e = 50.0f;
            }
        }
        if (((Boolean)this.guiMove.getValue()).booleanValue() && PlayerTweaks.mc.field_71462_r != null && !(PlayerTweaks.mc.field_71462_r instanceof GuiChat)) {
            if (Keyboard.isKeyDown((int)200)) {
                PlayerTweaks.mc.field_71439_g.field_70125_A -= 5.0f;
            }
            if (Keyboard.isKeyDown((int)208)) {
                PlayerTweaks.mc.field_71439_g.field_70125_A += 5.0f;
            }
            if (Keyboard.isKeyDown((int)205)) {
                PlayerTweaks.mc.field_71439_g.field_70177_z += 5.0f;
            }
            if (Keyboard.isKeyDown((int)203)) {
                PlayerTweaks.mc.field_71439_g.field_70177_z -= 5.0f;
            }
            if (PlayerTweaks.mc.field_71439_g.field_70125_A > 90.0f) {
                PlayerTweaks.mc.field_71439_g.field_70125_A = 90.0f;
            }
            if (PlayerTweaks.mc.field_71439_g.field_70125_A < -90.0f) {
                PlayerTweaks.mc.field_71439_g.field_70125_A = -90.0f;
            }
        }
    }

    private boolean predict(BlockPos blockPos) {
        this.n1 = blockPos.func_177982_a(0, -3, 0);
        return PlayerTweaks.mc.field_71441_e.func_180495_p(this.n1).func_177230_c() != Blocks.field_150350_a;
    }

    BlockPos getDownPos() {
        BlockPos e = null;
        for (int i = 0; i < 5; ++i) {
            if (PlayerTweaks.mc.field_71441_e.func_175623_d(new BlockPos(PlayerTweaks.mc.field_71439_g.func_174791_d()).func_177982_a(0, -i, 0))) continue;
            e = new BlockPos(PlayerTweaks.mc.field_71439_g.func_174791_d()).func_177982_a(0, -i + 1, 0);
            break;
        }
        return e;
    }
}

