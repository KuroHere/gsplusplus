/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEntityEvent;
import com.gamesense.api.event.events.TotemPopEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.render.ChamsUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

@Module.Declaration(name="Chams", category=Category.Render)
public class Chams
extends Module {
    ModeSetting chamsType = this.registerMode("Type", Arrays.asList("Texture", "Color", "WireFrame"), "Texture");
    IntegerSetting range = this.registerInteger("Range", 100, 10, 260);
    BooleanSetting player = this.registerBoolean("Player", true);
    BooleanSetting mob = this.registerBoolean("Mob", false);
    BooleanSetting crystal = this.registerBoolean("Crystal", false);
    IntegerSetting lineWidth = this.registerInteger("Line Width", 1, 1, 5);
    IntegerSetting colorOpacity = this.registerInteger("Color Opacity", 100, 0, 255);
    IntegerSetting wireOpacity = this.registerInteger("Wire Opacity", 200, 0, 255);
    ColorSetting playerColor = this.registerColor("Player Color", new GSColor(0, 255, 255, 255));
    ColorSetting mobColor = this.registerColor("Mob Color", new GSColor(255, 255, 0, 255));
    ColorSetting crystalColor = this.registerColor("Crystal Color", new GSColor(0, 255, 0, 255));
    BooleanSetting chamsPop = this.registerBoolean("Chams Pop", false);
    ModeSetting chamsPopType = this.registerMode("Chams Type Pop", Arrays.asList("Color", "WireFrame"), "WireFrame", () -> (Boolean)this.chamsPop.getValue());
    ColorSetting chamsColor = this.registerColor("Chams Color", new GSColor(255, 255, 255, 255), () -> (Boolean)this.chamsPop.getValue());
    IntegerSetting wireFramePop = this.registerInteger("WireFrame Pop", 4, 0, 10, () -> (Boolean)this.chamsPop.getValue());
    BooleanSetting gradientAlpha = this.registerBoolean("Gradient Alpha", true, () -> (Boolean)this.chamsPop.getValue());
    ModeSetting Movement = this.registerMode("Movement", Arrays.asList("None", "Heaven", "Hell"), "None", () -> (Boolean)this.chamsPop.getValue());
    DoubleSetting yMovement = this.registerDouble("Y Movement", 0.2, 0.0, 1.0, () -> (Boolean)this.chamsPop.getValue() != false && !((String)this.Movement.getValue()).equals("None"));
    IntegerSetting life = this.registerInteger("Time", 100, 10, 300, () -> (Boolean)this.chamsPop.getValue());
    private int fpNum = 0;
    ArrayList<Entity> toSpawn = new ArrayList();
    @EventHandler
    private final Listener<TotemPopEvent> totemPopEventListener = new Listener<TotemPopEvent>(event -> {
        if (((Boolean)this.chamsPop.getValue()).booleanValue() && event.getEntity() != null) {
            this.toSpawn.add(event.getEntity());
        }
    }, new Predicate[0]);
    @EventHandler
    private final Listener<RenderEntityEvent.Head> renderEntityHeadEventListener = new Listener<RenderEntityEvent.Head>(event -> {
        if (event.getType() == RenderEntityEvent.Type.COLOR && ((String)this.chamsType.getValue()).equalsIgnoreCase("Texture")) {
            return;
        }
        if (event.getType() == RenderEntityEvent.Type.TEXTURE && (((String)this.chamsType.getValue()).equalsIgnoreCase("Color") || ((String)this.chamsType.getValue()).equalsIgnoreCase("WireFrame"))) {
            return;
        }
        if (Chams.mc.field_71439_g == null || Chams.mc.field_71441_e == null) {
            return;
        }
        Entity entity1 = event.getEntity();
        if (entity1.func_70032_d((Entity)Chams.mc.field_71439_g) > (float)((Integer)this.range.getValue()).intValue()) {
            return;
        }
        if (((Boolean)this.player.getValue()).booleanValue() && entity1 instanceof EntityPlayer && entity1 != Chams.mc.field_71439_g) {
            if (((Boolean)this.chamsPop.getValue()).booleanValue() && entity1.func_70005_c_().length() == 0) {
                this.renderChamsPopPre(entity1);
            } else {
                this.renderChamsPre(new GSColor(this.playerColor.getValue(), 255), true);
            }
        }
        if (((Boolean)this.mob.getValue()).booleanValue() && (entity1 instanceof EntityCreature || entity1 instanceof EntitySlime || entity1 instanceof EntitySquid)) {
            this.renderChamsPre(new GSColor(this.mobColor.getValue(), 255), false);
        }
        if (((Boolean)this.crystal.getValue()).booleanValue() && entity1 instanceof EntityEnderCrystal) {
            this.renderChamsPre(new GSColor(this.crystalColor.getValue(), 255), false);
        }
    }, new Predicate[0]);
    ArrayList<playerChams> listPlayers = new ArrayList();
    @EventHandler
    private final Listener<RenderEntityEvent.Return> renderEntityReturnEventListener = new Listener<RenderEntityEvent.Return>(event -> {
        if (event.getType() == RenderEntityEvent.Type.COLOR && ((String)this.chamsType.getValue()).equalsIgnoreCase("Texture")) {
            return;
        }
        if (event.getType() == RenderEntityEvent.Type.TEXTURE && (((String)this.chamsType.getValue()).equalsIgnoreCase("Color") || ((String)this.chamsType.getValue()).equalsIgnoreCase("WireFrame"))) {
            return;
        }
        if (Chams.mc.field_71439_g == null || Chams.mc.field_71441_e == null) {
            return;
        }
        Entity entity1 = event.getEntity();
        if (entity1.func_70032_d((Entity)Chams.mc.field_71439_g) > (float)((Integer)this.range.getValue()).intValue()) {
            return;
        }
        if (((Boolean)this.player.getValue()).booleanValue() && entity1 instanceof EntityPlayer && entity1 != Chams.mc.field_71439_g) {
            if (entity1.func_70005_c_().length() == 0) {
                this.renderChamsPopPost();
            } else {
                this.renderChamsPost(true);
            }
        }
        if (((Boolean)this.mob.getValue()).booleanValue() && (entity1 instanceof EntityCreature || entity1 instanceof EntitySlime || entity1 instanceof EntitySquid)) {
            this.renderChamsPost(false);
        }
        if (((Boolean)this.crystal.getValue()).booleanValue() && entity1 instanceof EntityEnderCrystal) {
            this.renderChamsPost(false);
        }
        if (entity1.func_70005_c_().equals("")) {
            Chams.mc.field_71441_e.func_73028_b(entity1.func_145782_y());
        }
        boolean a = false;
    }, new Predicate[0]);

    @Override
    public void onUpdate() {
        if (Chams.mc.field_71441_e == null || Chams.mc.field_71439_g == null) {
            this.toSpawn.clear();
        }
        this.toSpawn.removeIf(this::spawnPlayer);
        for (int i = 0; i < this.listPlayers.size(); ++i) {
            if (this.listPlayers.get(i).onUpdate()) {
                try {
                    Chams.mc.field_71441_e.func_73028_b(this.listPlayers.get(i).id);
                }
                catch (NullPointerException nullPointerException) {
                    // empty catch block
                }
                this.listPlayers.remove(i);
                --i;
                continue;
            }
            this.spawnPlayer(this.listPlayers.get(i).id, this.listPlayers.get((int)i).coordinates);
        }
    }

    boolean spawnPlayer(Entity entity) {
        if (entity != null) {
            double movement = 0.0;
            switch ((String)this.Movement.getValue()) {
                case "Heaven": {
                    movement = (Double)this.yMovement.getValue();
                    break;
                }
                case "Hell": {
                    movement = -((Double)this.yMovement.getValue()).doubleValue();
                }
            }
            this.listPlayers.add(new playerChams(-1235 - this.fpNum, (Integer)this.life.getValue(), new double[]{entity.field_70165_t, entity.field_70163_u, entity.field_70161_v}, movement));
            ++this.fpNum;
        }
        return true;
    }

    void spawnPlayer(int num, double[] positions) {
        EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP((World)Chams.mc.field_71441_e, new GameProfile(UUID.fromString("fdee323e-7f0c-4c15-8d1c-0f277442342a"), ""));
        clonedPlayer.func_70107_b(positions[0], positions[1], positions[2]);
        clonedPlayer.func_71033_a(GameType.SPECTATOR);
        clonedPlayer.func_175149_v();
        clonedPlayer.func_70606_j(20.0f);
        clonedPlayer.func_70690_d(new PotionEffect(MobEffects.field_76429_m, 100, 100, false, false));
        ItemStack[] armors = new ItemStack[]{new ItemStack((Item)Items.field_151175_af), new ItemStack((Item)Items.field_151173_ae), new ItemStack((Item)Items.field_151163_ad), new ItemStack((Item)Items.field_151161_ac)};
        for (int i = 0; i < 4; ++i) {
            ItemStack item = armors[i];
            item.func_77966_a(i == 2 ? Enchantments.field_185297_d : Enchantments.field_180310_c, 50);
            clonedPlayer.field_71071_by.field_70460_b.set(i, (Object)item);
        }
        Chams.mc.field_71441_e.func_73027_a(num, (Entity)clonedPlayer);
    }

    private void renderChamsPre(GSColor color, boolean isPlayer) {
        switch ((String)this.chamsType.getValue()) {
            case "Texture": {
                ChamsUtil.createChamsPre();
                break;
            }
            case "Color": {
                ChamsUtil.createColorPre(new GSColor(color, (Integer)this.colorOpacity.getValue()), isPlayer);
                break;
            }
            case "WireFrame": {
                ChamsUtil.createWirePre(new GSColor(color, (Integer)this.wireOpacity.getValue()), (Integer)this.lineWidth.getValue(), isPlayer);
            }
        }
    }

    private void renderChamsPopPre(Entity player) {
        Optional<playerChams> prova;
        int alpha = this.chamsColor.getColor().getAlpha();
        if (((Boolean)this.gradientAlpha.getValue()).booleanValue() && (prova = this.listPlayers.stream().filter(e -> ((playerChams)e).id == player.field_145783_c).findAny()).isPresent()) {
            alpha = prova.get().returnGradient();
        }
        if (alpha < 0) {
            alpha = 0;
        }
        switch ((String)this.chamsPopType.getValue()) {
            case "Color": {
                ChamsUtil.createColorPre(new GSColor(this.chamsColor.getColor(), alpha), true);
                break;
            }
            case "WireFrame": {
                ChamsUtil.createWirePre(new GSColor(this.chamsColor.getColor(), alpha), (Integer)this.wireFramePop.getValue(), true);
            }
        }
    }

    private void renderChamsPopPost() {
        switch ((String)this.chamsPopType.getValue()) {
            case "Color": {
                ChamsUtil.createColorPost(true);
                break;
            }
            case "WireFrame": {
                ChamsUtil.createWirePost(true);
            }
        }
    }

    private void renderChamsPost(boolean isPlayer) {
        switch ((String)this.chamsType.getValue()) {
            case "Texture": {
                ChamsUtil.createChamsPost();
                break;
            }
            case "Color": {
                ChamsUtil.createColorPost(isPlayer);
                break;
            }
            case "WireFrame": {
                ChamsUtil.createWirePost(isPlayer);
            }
        }
    }

    static class playerChams {
        private int tick;
        private final int finalTick;
        private final int id;
        final double[] coordinates;
        final double movement;

        public playerChams(int id, int finalTick, double[] coordinates, double movement) {
            this.id = id;
            this.finalTick = finalTick;
            this.tick = 0;
            this.coordinates = coordinates;
            this.movement = movement;
        }

        public boolean onUpdate() {
            this.coordinates[1] = this.coordinates[1] + this.movement;
            return this.tick++ > this.finalTick;
        }

        public int returnGradient() {
            return 250 - (int)((float)this.tick / (float)this.finalTick * 250.0f);
        }
    }
}

