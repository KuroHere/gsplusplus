/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.world.combat.ac;

import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.combat.CrystalUtil;
import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.api.util.world.combat.ac.ACSettings;
import com.gamesense.api.util.world.combat.ac.CrystalInfo;
import com.gamesense.api.util.world.combat.ac.PlayerInfo;
import com.gamesense.api.util.world.combat.ac.threads.ACCalculate;
import com.gamesense.client.GameSense;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public enum ACHelper implements Listenable
{
    INSTANCE;

    private static final Minecraft mc;
    private static final List<CrystalInfo.PlaceInfo> EMPTY_LIST;
    private static final EntityEnderCrystal GENERIC_CRYSTAL;
    public static final ThreadPoolExecutor executor;
    private static final ExecutorService mainExecutors;
    private Future<List<CrystalInfo.PlaceInfo>> mainThreadOutput;
    private final ConcurrentHashMap<BlockPos, EntityEnderCrystal> placedCrystals = new ConcurrentHashMap();
    private ACSettings settings = null;
    private List<BlockPos> possiblePlacements = new ArrayList<BlockPos>();
    private List<EntityEnderCrystal> targetableCrystals = new ArrayList<EntityEnderCrystal>();
    private final List<PlayerInfo> targetsInfo = new ArrayList<PlayerInfo>();
    private List<BlockPos> threadPlacements = new ArrayList<BlockPos>();
    @EventHandler
    private final Listener<EntityJoinWorldEvent> entitySpawnListener = new Listener<EntityJoinWorldEvent>(event -> {
        Entity entity = event.getEntity();
        if (entity instanceof EntityEnderCrystal && this.settings != null && this.settings.breakMode.equalsIgnoreCase("Own")) {
            EntityEnderCrystal crystal = (EntityEnderCrystal)entity;
            BlockPos crystalPos = EntityUtil.getPosition((Entity)crystal);
            ConcurrentHashMap<BlockPos, EntityEnderCrystal> concurrentHashMap = this.placedCrystals;
            synchronized (concurrentHashMap) {
                this.placedCrystals.computeIfPresent(crystalPos, (i, j) -> crystal);
            }
        }
    }, new Predicate[0]);

    public void startCalculations(long timeout) {
        if (this.mainThreadOutput != null) {
            this.mainThreadOutput.cancel(true);
        }
        this.mainThreadOutput = mainExecutors.submit(new ACCalculate(this.settings, this.targetsInfo, this.threadPlacements, timeout));
    }

    public List<CrystalInfo.PlaceInfo> getOutput(boolean wait) {
        if (this.mainThreadOutput == null) {
            return EMPTY_LIST;
        }
        if (wait) {
            while (!this.mainThreadOutput.isDone() && !this.mainThreadOutput.isCancelled()) {
            }
        } else {
            if (!this.mainThreadOutput.isDone()) {
                return null;
            }
            if (this.mainThreadOutput.isCancelled()) {
                return EMPTY_LIST;
            }
        }
        List<CrystalInfo.PlaceInfo> output = EMPTY_LIST;
        try {
            output = this.mainThreadOutput.get();
        }
        catch (InterruptedException | ExecutionException exception) {
            // empty catch block
        }
        this.mainThreadOutput = null;
        return output;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void recalculateValues(ACSettings settings, PlayerInfo self, float armourPercent, double enemyDistance) {
        this.settings = settings;
        double entityRangeSq = enemyDistance * enemyDistance;
        List targets = ACHelper.mc.field_71441_e.field_73010_i.stream().filter(entity -> self.entity.func_70068_e((Entity)entity) <= entityRangeSq).filter(entity -> !EntityUtil.basicChecksEntity((Entity)entity)).filter(entity -> entity.func_110143_aJ() > 0.0f).filter(entity -> entity.func_70005_c_().length() > 0).collect(Collectors.toList());
        this.targetableCrystals = ACHelper.mc.field_71441_e.field_72996_f.stream().filter(entity -> entity instanceof EntityEnderCrystal).map(entity -> (EntityEnderCrystal)entity).collect(Collectors.toList());
        boolean own = settings.breakMode.equalsIgnoreCase("Own");
        if (own) {
            ConcurrentHashMap<BlockPos, EntityEnderCrystal> concurrentHashMap = this.placedCrystals;
            synchronized (concurrentHashMap) {
                this.targetableCrystals.removeIf(crystal -> !this.placedCrystals.containsKey(EntityUtil.getPosition((Entity)crystal)));
                this.placedCrystals.values().removeIf(crystal -> crystal.field_70128_L);
            }
        }
        this.targetableCrystals.removeIf(crystal -> {
            float damage = DamageUtil.calculateDamageThreaded(crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v, self, false);
            if (damage > settings.maxSelfDamage) {
                return true;
            }
            return settings.antiSuicide && damage > self.health || self.entity.func_70068_e((Entity)crystal) >= settings.breakRangeSq;
        });
        this.possiblePlacements = CrystalUtil.findCrystalBlocks(settings.placeRange, settings.endCrystalMode);
        this.possiblePlacements.removeIf(crystal -> {
            float damage = DamageUtil.calculateDamageThreaded((double)crystal.func_177958_n() + 0.5, (double)crystal.func_177956_o() + 1.0, (double)crystal.func_177952_p() + 0.5, settings.player, false);
            if (damage > settings.maxSelfDamage) {
                return true;
            }
            return settings.antiSuicide && damage > settings.player.health;
        });
        this.threadPlacements = CrystalUtil.findCrystalBlocksExcludingCrystals(settings.placeRange, settings.endCrystalMode);
        this.targetsInfo.clear();
        for (EntityPlayer target : targets) {
            this.targetsInfo.add(new PlayerInfo(target, armourPercent));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onPlaceCrystal(BlockPos target) {
        if (this.settings.breakMode.equalsIgnoreCase("Own")) {
            BlockPos up = target.func_177984_a();
            ConcurrentHashMap<BlockPos, EntityEnderCrystal> concurrentHashMap = this.placedCrystals;
            synchronized (concurrentHashMap) {
                this.placedCrystals.put(up, GENERIC_CRYSTAL);
            }
        }
    }

    public void onEnable() {
        GameSense.EVENT_BUS.subscribe(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe(this);
        ConcurrentHashMap<BlockPos, EntityEnderCrystal> concurrentHashMap = this.placedCrystals;
        synchronized (concurrentHashMap) {
            this.placedCrystals.clear();
        }
        if (this.mainThreadOutput != null) {
            this.mainThreadOutput.cancel(true);
        }
    }

    public ACSettings getSettings() {
        return this.settings;
    }

    public List<BlockPos> getPossiblePlacements() {
        return this.possiblePlacements;
    }

    public List<EntityEnderCrystal> getTargetableCrystals() {
        return this.targetableCrystals;
    }

    static {
        mc = Minecraft.func_71410_x();
        EMPTY_LIST = new ArrayList<CrystalInfo.PlaceInfo>();
        GENERIC_CRYSTAL = new EntityEnderCrystal(null, 398.0, 398.0, 398.0);
        executor = (ThreadPoolExecutor)Executors.newCachedThreadPool();
        mainExecutors = Executors.newSingleThreadExecutor();
    }
}

