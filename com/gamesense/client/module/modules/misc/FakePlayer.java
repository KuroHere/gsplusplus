/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.TotemPopEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.StringSetting;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.client.GameSense;
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
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

@Module.Declaration(name="FakePlayer", category=Category.Misc)
public class FakePlayer
extends Module {
    private final ItemStack[] armors = new ItemStack[]{new ItemStack((Item)Items.field_151175_af), new ItemStack((Item)Items.field_151173_ae), new ItemStack((Item)Items.field_151163_ad), new ItemStack((Item)Items.field_151161_ac)};
    BooleanSetting copyInventory = this.registerBoolean("Copy Inventory", false);
    BooleanSetting playerStacked = this.registerBoolean("Player Stacked", false, () -> (Boolean)this.copyInventory.getValue() == false);
    BooleanSetting onShift = this.registerBoolean("On Shift", false);
    BooleanSetting simulateDamage = this.registerBoolean("Simulate Damage", false);
    StringSetting nameFakePlayer = this.registerString("Name FakePlayer", "fit");
    IntegerSetting vulnerabilityTick = this.registerInteger("Vulnerability Tick", 4, 0, 10);
    IntegerSetting resetHealth = this.registerInteger("Reset Health", 10, 0, 36);
    IntegerSetting tickRegenVal = this.registerInteger("Tick Regen", 4, 0, 30);
    IntegerSetting startHealth = this.registerInteger("Start Health", 20, 0, 30);
    ModeSetting moving = this.registerMode("Moving", Arrays.asList("None", "Line", "Circle", "Random"), "None");
    DoubleSetting speed = this.registerDouble("Speed", 0.36, 0.0, 4.0, () -> !((String)this.moving.getValue()).equals("None") || !((String)this.moving.getValue()).equals("Random"));
    DoubleSetting range = this.registerDouble("Range", 3.0, 0.0, 14.0, () -> ((String)this.moving.getValue()).equals("Circle"));
    BooleanSetting followPlayer = this.registerBoolean("Follow Player", true, () -> ((String)this.moving.getValue()).equals("Line"));
    BooleanSetting resistance = this.registerBoolean("Resistance", true);
    int incr;
    boolean beforePressed;
    ArrayList<playerInfo> listPlayers = new ArrayList();
    movingManager manager = new movingManager();
    @EventHandler
    private final Listener<PacketEvent.Receive> packetReceiveListener = new Listener<PacketEvent.Receive>(event -> {
        SPacketSoundEffect packetSoundEffect;
        Packet packet;
        if (((Boolean)this.simulateDamage.getValue()).booleanValue() && (packet = event.getPacket()) instanceof SPacketSoundEffect && (packetSoundEffect = (SPacketSoundEffect)packet).func_186977_b() == SoundCategory.BLOCKS && packetSoundEffect.func_186978_a() == SoundEvents.field_187539_bB) {
            for (Entity entity : new ArrayList(FakePlayer.mc.field_71441_e.field_72996_f)) {
                if (!(entity instanceof EntityEnderCrystal) || !(entity.func_70092_e(packetSoundEffect.func_149207_d(), packetSoundEffect.func_149211_e(), packetSoundEffect.func_149210_f()) <= 36.0)) continue;
                for (EntityPlayer entityPlayer : FakePlayer.mc.field_71441_e.field_73010_i) {
                    Optional<playerInfo> temp;
                    if (entityPlayer.func_70005_c_().split(this.nameFakePlayer.getText()).length != 2 || !(temp = this.listPlayers.stream().filter(e -> e.name.equals(entityPlayer.func_70005_c_())).findAny()).isPresent() || !temp.get().canPop()) continue;
                    float damage = DamageUtil.calculateDamage(packetSoundEffect.func_149207_d(), packetSoundEffect.func_149211_e(), packetSoundEffect.func_149210_f(), (Entity)entityPlayer, false);
                    if (damage > entityPlayer.func_110143_aJ()) {
                        entityPlayer.func_70606_j((float)((Integer)this.resetHealth.getValue()).intValue());
                        FakePlayer.mc.field_71452_i.func_191271_a((Entity)entityPlayer, EnumParticleTypes.TOTEM, 30);
                        FakePlayer.mc.field_71441_e.func_184134_a(entityPlayer.field_70165_t, entityPlayer.field_70163_u, entityPlayer.field_70161_v, SoundEvents.field_191263_gW, entity.func_184176_by(), 1.0f, 1.0f, false);
                        GameSense.EVENT_BUS.post(new TotemPopEvent((Entity)entityPlayer));
                    } else {
                        entityPlayer.func_70606_j(entityPlayer.func_110143_aJ() - damage);
                    }
                    temp.get().tickPop = 0;
                }
            }
        }
    }, new Predicate[0]);

    @Override
    public void onEnable() {
        this.incr = 0;
        this.beforePressed = false;
        if (FakePlayer.mc.field_71439_g == null || FakePlayer.mc.field_71439_g.field_70128_L) {
            this.disable();
            return;
        }
        if (!((Boolean)this.onShift.getValue()).booleanValue()) {
            this.spawnPlayer();
        }
    }

    void spawnPlayer() {
        EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP((World)FakePlayer.mc.field_71441_e, new GameProfile(UUID.fromString("fdee323e-7f0c-4c15-8d1c-0f277442342a"), this.nameFakePlayer.getText() + this.incr));
        clonedPlayer.func_82149_j((Entity)FakePlayer.mc.field_71439_g);
        clonedPlayer.field_70759_as = FakePlayer.mc.field_71439_g.field_70759_as;
        clonedPlayer.field_70177_z = FakePlayer.mc.field_71439_g.field_70177_z;
        clonedPlayer.field_70125_A = FakePlayer.mc.field_71439_g.field_70125_A;
        clonedPlayer.func_71033_a(GameType.SURVIVAL);
        clonedPlayer.func_70606_j((float)((Integer)this.startHealth.getValue()).intValue());
        FakePlayer.mc.field_71441_e.func_73027_a(-1234 + this.incr, (Entity)clonedPlayer);
        ++this.incr;
        if (((Boolean)this.copyInventory.getValue()).booleanValue()) {
            clonedPlayer.field_71071_by.func_70455_b(FakePlayer.mc.field_71439_g.field_71071_by);
        } else if (((Boolean)this.playerStacked.getValue()).booleanValue()) {
            for (int i = 0; i < 4; ++i) {
                ItemStack item = this.armors[i];
                item.func_77966_a(i == 3 ? Enchantments.field_185297_d : Enchantments.field_180310_c, 4);
                clonedPlayer.field_71071_by.field_70460_b.set(i, (Object)item);
            }
        }
        if (((Boolean)this.resistance.getValue()).booleanValue()) {
            clonedPlayer.func_70690_d(new PotionEffect(Potion.func_188412_a((int)11), 123456789, 0));
        }
        clonedPlayer.func_70030_z();
        this.listPlayers.add(new playerInfo(clonedPlayer.func_70005_c_()));
        if (!((String)this.moving.getValue()).equals("None")) {
            this.manager.addPlayer(clonedPlayer.field_145783_c, (String)this.moving.getValue(), (Double)this.speed.getValue(), ((String)this.moving.getValue()).equals("Line") ? this.getDirection() : -1, (Double)this.range.getValue(), (Boolean)this.followPlayer.getValue());
        }
    }

    @Override
    public void onUpdate() {
        if (((Boolean)this.onShift.getValue()).booleanValue() && FakePlayer.mc.field_71474_y.field_74311_E.func_151468_f() && !this.beforePressed) {
            this.beforePressed = true;
            this.spawnPlayer();
        } else {
            this.beforePressed = false;
        }
        for (int i = 0; i < this.listPlayers.size(); ++i) {
            if (!this.listPlayers.get(i).update()) continue;
            int finalI = i;
            Optional<EntityPlayer> temp = FakePlayer.mc.field_71441_e.field_73010_i.stream().filter(e -> e.func_70005_c_().equals(this.listPlayers.get((int)finalI).name)).findAny();
            if (!temp.isPresent() || !(temp.get().func_110143_aJ() < 20.0f)) continue;
            temp.get().func_70606_j(temp.get().func_110143_aJ() + 1.0f);
        }
        this.manager.update();
    }

    int getDirection() {
        int yaw = (int)RotationUtil.normalizeAngle(FakePlayer.mc.field_71439_g.func_189653_aC().field_189983_j);
        if (yaw < 0) {
            yaw += 360;
        }
        yaw += 22;
        return (yaw %= 360) / 45;
    }

    @Override
    public void onDisable() {
        if (FakePlayer.mc.field_71441_e != null) {
            for (int i = 0; i < this.incr; ++i) {
                FakePlayer.mc.field_71441_e.func_73028_b(-1234 + i);
            }
        }
        this.listPlayers.clear();
        this.manager.remove();
    }

    static class movingManager {
        private final ArrayList<movingPlayer> players = new ArrayList();

        movingManager() {
        }

        void addPlayer(int id, String type, double speed, int direction, double range, boolean follow) {
            this.players.add(new movingPlayer(id, type, speed, direction, range, follow));
        }

        void update() {
            this.players.forEach(movingPlayer::move);
        }

        void remove() {
            this.players.clear();
        }
    }

    static class movingPlayer {
        private final int id;
        private final String type;
        private final double speed;
        private final int direction;
        private final double range;
        private final boolean follow;
        int rad = 0;

        public movingPlayer(int id, String type, double speed, int direction, double range, boolean follow) {
            this.id = id;
            this.type = type;
            this.speed = speed;
            this.direction = Math.abs(direction);
            this.range = range;
            this.follow = follow;
        }

        void move() {
            Entity player = mc.field_71441_e.func_73045_a(this.id);
            if (player != null) {
                switch (this.type) {
                    case "Line": {
                        double posX = this.follow ? mc.field_71439_g.field_70165_t : player.field_70165_t;
                        double posY = this.follow ? mc.field_71439_g.field_70163_u : player.field_70163_u;
                        double posZ = this.follow ? mc.field_71439_g.field_70161_v : player.field_70161_v;
                        switch (this.direction) {
                            case 0: {
                                posZ += this.speed;
                                break;
                            }
                            case 1: {
                                posX -= this.speed / 2.0;
                                posZ += this.speed / 2.0;
                                break;
                            }
                            case 2: {
                                posX -= this.speed / 2.0;
                                break;
                            }
                            case 3: {
                                posZ -= this.speed / 2.0;
                                posX -= this.speed / 2.0;
                                break;
                            }
                            case 4: {
                                posZ -= this.speed;
                                break;
                            }
                            case 5: {
                                posX += this.speed / 2.0;
                                posZ -= this.speed / 2.0;
                                break;
                            }
                            case 6: {
                                posX += this.speed;
                                break;
                            }
                            case 7: {
                                posZ += this.speed / 2.0;
                                posX += this.speed / 2.0;
                            }
                        }
                        if (BlockUtil.getBlock(posX, posY, posZ) instanceof BlockAir) {
                            for (int i = 0; i < 5 && BlockUtil.getBlock(posX, posY - 1.0, posZ) instanceof BlockAir; ++i) {
                                posY -= 1.0;
                            }
                        } else {
                            for (int i = 0; i < 5 && !(BlockUtil.getBlock(posX, posY, posZ) instanceof BlockAir); ++i) {
                                posY += 1.0;
                            }
                        }
                        player.func_70634_a(posX, posY, posZ);
                        break;
                    }
                    case "Circle": {
                        double posXCir = Math.cos((double)this.rad / 100.0) * this.range + mc.field_71439_g.field_70165_t;
                        double posZCir = Math.sin((double)this.rad / 100.0) * this.range + mc.field_71439_g.field_70161_v;
                        double posYCir = mc.field_71439_g.field_70163_u;
                        if (BlockUtil.getBlock(posXCir, posYCir, posZCir) instanceof BlockAir) {
                            for (int i = 0; i < 5 && BlockUtil.getBlock(posXCir, posYCir - 1.0, posZCir) instanceof BlockAir; ++i) {
                                posYCir -= 1.0;
                            }
                        } else {
                            for (int i = 0; i < 5 && !(BlockUtil.getBlock(posXCir, posYCir, posZCir) instanceof BlockAir); ++i) {
                                posYCir += 1.0;
                            }
                        }
                        player.func_70634_a(posXCir, posYCir, posZCir);
                        this.rad = (int)((double)this.rad + this.speed * 10.0);
                        break;
                    }
                }
            }
        }
    }

    class playerInfo {
        final String name;
        int tickPop = -1;
        int tickRegen = 0;

        public playerInfo(String name) {
            this.name = name;
        }

        boolean update() {
            if (this.tickPop != -1 && ++this.tickPop >= (Integer)FakePlayer.this.vulnerabilityTick.getValue()) {
                this.tickPop = -1;
            }
            if (++this.tickRegen >= (Integer)FakePlayer.this.tickRegenVal.getValue()) {
                this.tickRegen = 0;
                return true;
            }
            return false;
        }

        boolean canPop() {
            return this.tickPop == -1;
        }
    }
}

