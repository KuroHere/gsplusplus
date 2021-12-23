/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.Pair;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AutoCrystalRewrite;
import com.gamesense.client.module.modules.misc.AutoGG;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec2f;

@Module.Declaration(name="KillAura", category=Category.Combat)
public class KillAura
extends Module {
    BooleanSetting players = this.registerBoolean("Players", true);
    BooleanSetting hostileMobs = this.registerBoolean("Monsters", false);
    BooleanSetting passiveMobs = this.registerBoolean("Animals", false);
    ModeSetting itemUsed = this.registerMode("Item used", Arrays.asList("Sword", "Axe", "Both", "All"), "Sword");
    ModeSetting enemyPriority = this.registerMode("Enemy Priority", Arrays.asList("Closest", "Health"), "Closest");
    BooleanSetting swordPriority = this.registerBoolean("Prioritise Sword", true);
    BooleanSetting caCheck = this.registerBoolean("AC Check", false);
    BooleanSetting rotation = this.registerBoolean("Rotation", true);
    BooleanSetting autoSwitch = this.registerBoolean("Switch", false);
    DoubleSetting switchHealth = this.registerDouble("Min Switch Health", 0.0, 0.0, 20.0);
    DoubleSetting range = this.registerDouble("Range", 5.0, 0.0, 10.0);
    ModeSetting render = this.registerMode("Render", Arrays.asList("None", "Rectangle", "Circle"), "None");
    IntegerSetting life = this.registerInteger("Life", 300, 0, 1000, () -> !((String)this.render.getValue()).equals("None"));
    DoubleSetting circleRange = this.registerDouble("Circle Range", 1.0, 0.0, 3.0);
    ColorSetting color = this.registerColor("Color", new GSColor(255, 255, 255, 255), () -> true, true);
    BooleanSetting desyncCircle = this.registerBoolean("Desync Circle", false);
    IntegerSetting stepRainbowCircle = this.registerInteger("Step Rainbow Circle", 1, 1, 100);
    BooleanSetting increaseHeight = this.registerBoolean("Increase Height", true);
    DoubleSetting speedIncrease = this.registerDouble("Speed Increase", 0.01, 0.3, 0.001);
    ArrayList<renderClass> toRender = new ArrayList();
    boolean calcDelay = true;

    @Override
    public void onUpdate() {
        if (KillAura.mc.field_71439_g == null || !KillAura.mc.field_71439_g.func_70089_S()) {
            return;
        }
        this.toRender.removeIf(renderClass::update);
        for (int i = 0; i < this.toRender.size(); ++i) {
            if (!this.toRender.get(i).update()) continue;
            this.toRender.remove(i);
            --i;
        }
        double rangeSq = Math.pow((Double)this.range.getValue(), 2.0);
        Optional<Entity> optionalTarget = KillAura.mc.field_71441_e.field_72996_f.stream().filter(entity -> entity instanceof EntityLivingBase).filter(entity -> !EntityUtil.basicChecksEntity(entity)).filter(entity -> KillAura.mc.field_71439_g.func_70068_e(entity) <= rangeSq).min(Comparator.comparing(e -> ((String)this.enemyPriority.getValue()).equals("Closest") ? KillAura.mc.field_71439_g.func_70068_e(e) : (double)((EntityLivingBase)e).func_110143_aJ()));
        boolean sword = ((String)this.itemUsed.getValue()).equalsIgnoreCase("Sword");
        boolean axe = ((String)this.itemUsed.getValue()).equalsIgnoreCase("Axe");
        boolean both = ((String)this.itemUsed.getValue()).equalsIgnoreCase("Both");
        boolean all = ((String)this.itemUsed.getValue()).equalsIgnoreCase("All");
        if (optionalTarget.isPresent()) {
            Pair<Float, Integer> newSlot = new Pair<Float, Integer>(Float.valueOf(0.0f), -1);
            if (((Boolean)this.autoSwitch.getValue()).booleanValue() && (double)(KillAura.mc.field_71439_g.func_110143_aJ() + KillAura.mc.field_71439_g.func_110139_bj()) >= (Double)this.switchHealth.getValue()) {
                Pair<Float, Integer> possibleSlot;
                if (sword || both || all) {
                    newSlot = this.findSwordSlot();
                }
                if ((axe || both || all) && (!((Boolean)this.swordPriority.getValue()).booleanValue() || newSlot.getValue() == -1) && (possibleSlot = this.findAxeSlot()).getKey().floatValue() > newSlot.getKey().floatValue()) {
                    newSlot = possibleSlot;
                }
            }
            int temp = KillAura.mc.field_71439_g.field_71071_by.field_70461_c;
            if (newSlot.getValue() != -1) {
                KillAura.mc.field_71439_g.field_71071_by.field_70461_c = newSlot.getValue();
            }
            if (this.shouldAttack(sword, axe, both, all)) {
                Entity target = optionalTarget.get();
                if (!((String)this.render.getValue()).equals("None")) {
                    boolean found = false;
                    for (renderClass rend : this.toRender) {
                        if (!rend.reset(target.field_145783_c)) continue;
                        found = true;
                        break;
                    }
                    if (!found) {
                        this.toRender.add(new renderClass(target.field_145783_c, ((Integer)this.life.getValue()).intValue(), (String)this.render.getValue(), this.color.getValue(), (Double)this.circleRange.getValue(), (Boolean)this.desyncCircle.getValue(), (Integer)this.stepRainbowCircle.getValue(), (Double)this.circleRange.getValue(), (Integer)this.stepRainbowCircle.getValue(), (Boolean)this.increaseHeight.getValue(), (Double)this.speedIncrease.getValue()));
                    }
                }
                if (((Boolean)this.rotation.getValue()).booleanValue()) {
                    Vec2f rotation = RotationUtil.getRotationTo(target.func_174813_aQ());
                    PlayerPacket packet = new PlayerPacket((Module)this, rotation);
                    PlayerPacketManager.INSTANCE.addPacket(packet);
                }
                if (ModuleManager.isModuleEnabled(AutoGG.class)) {
                    AutoGG.INSTANCE.addTargetedPlayer(target.func_70005_c_());
                }
                this.attack(target);
            } else {
                KillAura.mc.field_71439_g.field_71071_by.field_70461_c = temp;
            }
        }
    }

    private Pair<Float, Integer> findSwordSlot() {
        List<Integer> items = InventoryUtil.findAllItemSlots(ItemSword.class);
        NonNullList inventory = KillAura.mc.field_71439_g.field_71071_by.field_70462_a;
        float bestModifier = 0.0f;
        int correspondingSlot = -1;
        for (Integer integer : items) {
            ItemStack stack;
            float modifier;
            if (integer > 8 || !((modifier = (EnchantmentHelper.func_152377_a((ItemStack)(stack = (ItemStack)inventory.get(integer)), (EnumCreatureAttribute)EnumCreatureAttribute.UNDEFINED) + 1.0f) * ((ItemSword)stack.func_77973_b()).func_150931_i()) > bestModifier)) continue;
            bestModifier = modifier;
            correspondingSlot = integer;
        }
        return new Pair<Float, Integer>(Float.valueOf(bestModifier), correspondingSlot);
    }

    private Pair<Float, Integer> findAxeSlot() {
        List<Integer> items = InventoryUtil.findAllItemSlots(ItemAxe.class);
        NonNullList inventory = KillAura.mc.field_71439_g.field_71071_by.field_70462_a;
        float bestModifier = 0.0f;
        int correspondingSlot = -1;
        for (Integer integer : items) {
            ItemStack stack;
            float modifier;
            if (integer > 8 || !((modifier = (EnchantmentHelper.func_152377_a((ItemStack)(stack = (ItemStack)inventory.get(integer)), (EnumCreatureAttribute)EnumCreatureAttribute.UNDEFINED) + 1.0f) * ((ItemAxe)stack.func_77973_b()).field_77865_bY) > bestModifier)) continue;
            bestModifier = modifier;
            correspondingSlot = integer;
        }
        return new Pair<Float, Integer>(Float.valueOf(bestModifier), correspondingSlot);
    }

    private boolean shouldAttack(boolean sword, boolean axe, boolean both, boolean all) {
        Item item = KillAura.mc.field_71439_g.func_184614_ca().func_77973_b();
        return !(!all && (!sword && !both || !(item instanceof ItemSword)) && (!axe && !both || !(item instanceof ItemAxe)) || (Boolean)this.caCheck.getValue() != false && ModuleManager.getModule(AutoCrystalRewrite.class).bestBreak.crystal == null);
    }

    private void attack(Entity e) {
        if (KillAura.mc.field_71439_g.func_184825_o(0.0f) >= 1.0f) {
            KillAura.mc.field_71442_b.func_78764_a((EntityPlayer)KillAura.mc.field_71439_g, e);
            KillAura.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
            this.calcDelay = true;
        }
    }

    private boolean attackCheck(Entity entity) {
        if (((Boolean)this.players.getValue()).booleanValue() && entity instanceof EntityPlayer && !SocialManager.isFriend(entity.func_70005_c_()) && ((EntityPlayer)entity).func_110143_aJ() > 0.0f) {
            return true;
        }
        if (((Boolean)this.passiveMobs.getValue()).booleanValue() && entity instanceof EntityAnimal) {
            return !(entity instanceof EntityTameable);
        }
        return (Boolean)this.hostileMobs.getValue() != false && entity instanceof EntityMob;
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        this.toRender.forEach(renderClass::render);
    }

    static class renderClass {
        final int id;
        long start;
        final long life;
        final String mode;
        final double circleRange;
        final GSColor color;
        final boolean desyncCircle;
        final int stepRainbowCircle;
        final double range;
        final int desync;
        final boolean increaseHeight;
        final double speedIncrease;
        double nowHeigth = 0.0;
        boolean up = true;

        public renderClass(int id, long life, String mode, GSColor color, double circleRange, boolean desyncCircle, int stepRainbowCircle, double range, int desync, boolean increaseHeight, double speedIncrease) {
            this.increaseHeight = increaseHeight;
            this.speedIncrease = speedIncrease;
            this.id = id;
            this.range = range;
            this.start = System.currentTimeMillis();
            this.life = life;
            this.mode = mode;
            this.desync = desync;
            this.circleRange = circleRange;
            this.color = color;
            this.desyncCircle = desyncCircle;
            this.stepRainbowCircle = stepRainbowCircle;
        }

        boolean update() {
            return System.currentTimeMillis() - this.start > this.life;
        }

        boolean reset(int id) {
            if (this.id == id) {
                this.start = System.currentTimeMillis();
                return true;
            }
            return false;
        }

        void render() {
            Entity e = mc.field_71441_e.func_73045_a(this.id);
            if (e != null) {
                switch (this.mode) {
                    case "Rectangle": {
                        RenderUtil.drawBox(e.func_174813_aQ(), false, e.field_70131_O, this.color, 63);
                        break;
                    }
                    case "Circle": {
                        double inc = 0.0;
                        if (this.increaseHeight) {
                            this.nowHeigth += this.speedIncrease * (double)(this.up ? 1 : -1);
                            if (this.nowHeigth > (double)e.field_70131_O) {
                                this.up = false;
                            } else if (this.nowHeigth < 0.0) {
                                this.up = true;
                            }
                            inc = this.nowHeigth;
                        }
                        if (this.desyncCircle) {
                            RenderUtil.drawCircle((float)e.field_70165_t, (float)(e.field_70163_u + inc), (float)e.field_70161_v, this.range, this.desync, this.color.getAlpha());
                            break;
                        }
                        RenderUtil.drawCircle((float)e.field_70165_t, (float)(e.field_70163_u + inc), (float)e.field_70161_v, this.range, this.color);
                    }
                }
            }
        }
    }
}

