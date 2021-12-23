/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.ColorUtil;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.manager.managers.TotemPopManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(name="PvPInfo", category=Category.Misc)
public class PvPInfo
extends Module {
    BooleanSetting visualRange = this.registerBoolean("Visual Range", false);
    BooleanSetting pearlAlert = this.registerBoolean("Pearl Alert", false);
    BooleanSetting burrowAlert = this.registerBoolean("Burrow Alert", false);
    BooleanSetting strengthDetect = this.registerBoolean("Strength Detect", false);
    BooleanSetting weaknessDetect = this.registerBoolean("Weakness Detect", false);
    BooleanSetting popCounter = this.registerBoolean("Pop Counter", false);
    BooleanSetting countPops = this.registerBoolean("Count Pops", false);
    BooleanSetting countKills = this.registerBoolean("Count Kills", false);
    ModeSetting chatColor = this.registerMode("Color", ColorUtil.colors, "Light Purple");
    List<Entity> knownPlayers = new ArrayList<Entity>();
    List<Entity> antiPearlList = new ArrayList<Entity>();
    List<Entity> players;
    List<Entity> pearls;
    List<Entity> burrowedPlayers = new ArrayList<Entity>();
    List<Entity> strengthPlayers = new ArrayList<Entity>();
    List<Entity> weaknessPlayers = new ArrayList<Entity>();

    @Override
    public void onUpdate() {
        if (PvPInfo.mc.field_71439_g == null || PvPInfo.mc.field_71441_e == null) {
            return;
        }
        TotemPopManager.INSTANCE.sendMsgs = this.isToggleMsg();
        TotemPopManager.INSTANCE.sendCountPops = (Boolean)this.popCounter.getValue();
        TotemPopManager.INSTANCE.popCount = (Boolean)this.countPops.getValue();
        TotemPopManager.INSTANCE.sendCountKills = (Boolean)this.countKills.getValue();
        if (((Boolean)this.popCounter.getValue()).booleanValue()) {
            TotemPopManager.INSTANCE.chatFormatting = ColorUtil.textToChatFormatting(this.chatColor);
        }
        if (((Boolean)this.visualRange.getValue()).booleanValue()) {
            this.players = PvPInfo.mc.field_71441_e.field_72996_f.stream().filter(e -> e instanceof EntityPlayer).collect(Collectors.toList());
            try {
                for (Entity e2 : this.players) {
                    if (!(e2 instanceof EntityPlayer) || e2.func_70005_c_().equalsIgnoreCase(PvPInfo.mc.field_71439_g.func_70005_c_()) || this.knownPlayers.contains(e2)) continue;
                    this.knownPlayers.add(e2);
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.chatColor) + e2.func_70005_c_() + " has been spotted thanks to GameSense!");
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            try {
                for (Entity e2 : this.knownPlayers) {
                    if (!(e2 instanceof EntityPlayer) || e2.func_70005_c_().equalsIgnoreCase(PvPInfo.mc.field_71439_g.func_70005_c_()) || this.players.contains(e2)) continue;
                    this.knownPlayers.remove(e2);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (((Boolean)this.burrowAlert.getValue()).booleanValue()) {
            for (Entity entity : PvPInfo.mc.field_71441_e.field_72996_f.stream().filter(e -> e instanceof EntityPlayer).collect(Collectors.toList())) {
                if (!(entity instanceof EntityPlayer)) continue;
                if (!this.burrowedPlayers.contains(entity) && this.isBurrowed(entity)) {
                    this.burrowedPlayers.add(entity);
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.chatColor) + entity.func_70005_c_() + " has just burrowed!");
                    continue;
                }
                if (!this.burrowedPlayers.contains(entity) || this.isBurrowed(entity)) continue;
                this.burrowedPlayers.remove(entity);
                MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.chatColor) + entity.func_70005_c_() + " is no longer burrowed!");
            }
        }
        if (((Boolean)this.pearlAlert.getValue()).booleanValue()) {
            this.pearls = PvPInfo.mc.field_71441_e.field_72996_f.stream().filter(e -> e instanceof EntityEnderPearl).collect(Collectors.toList());
            try {
                for (Entity e2 : this.pearls) {
                    if (!(e2 instanceof EntityEnderPearl) || this.antiPearlList.contains(e2)) continue;
                    this.antiPearlList.add(e2);
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.chatColor) + e2.func_130014_f_().func_72890_a(e2, 3.0).func_70005_c_() + " has just thrown a pearl!");
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (((Boolean)this.strengthDetect.getValue()).booleanValue() || ((Boolean)this.weaknessDetect.getValue()).booleanValue()) {
            for (EntityPlayer player : PvPInfo.mc.field_71441_e.field_73010_i) {
                if (player.func_70644_a(MobEffects.field_76420_g) && !this.strengthPlayers.contains(player)) {
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.chatColor) + player.func_70005_c_() + " has (drank) strength!");
                    this.strengthPlayers.add((Entity)player);
                }
                if (player.func_70644_a(MobEffects.field_76437_t) && !this.weaknessPlayers.contains(player)) {
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.chatColor) + player.func_70005_c_() + " has (drank) wealness!");
                    this.weaknessPlayers.add((Entity)player);
                }
                if (!player.func_70644_a(MobEffects.field_76420_g) && this.strengthPlayers.contains(player)) {
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.chatColor) + player.func_70005_c_() + " no longer has strength!");
                    this.strengthPlayers.remove(player);
                }
                if (player.func_70644_a(MobEffects.field_76437_t) || !this.weaknessPlayers.contains(player)) continue;
                MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.chatColor) + player.func_70005_c_() + " no longer has weakness!");
                this.weaknessPlayers.remove(player);
            }
        }
    }

    private boolean isBurrowed(Entity entity) {
        BlockPos entityPos = new BlockPos(this.roundValueToCenter(entity.field_70165_t), entity.field_70163_u + 0.2, this.roundValueToCenter(entity.field_70161_v));
        return PvPInfo.mc.field_71441_e.func_180495_p(entityPos).func_177230_c() == Blocks.field_150343_Z || PvPInfo.mc.field_71441_e.func_180495_p(entityPos).func_177230_c() == Blocks.field_150477_bB;
    }

    private double roundValueToCenter(double inputVal) {
        double roundVal = Math.round(inputVal);
        if (roundVal > inputVal) {
            roundVal -= 0.5;
        } else if (roundVal <= inputVal) {
            roundVal += 0.5;
        }
        return roundVal;
    }

    @Override
    public void onDisable() {
        this.knownPlayers.clear();
        TotemPopManager.INSTANCE.sendMsgs = false;
    }
}

