/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.world.combat.ac.threads;

import com.gamesense.api.util.world.combat.ac.ACHelper;
import com.gamesense.api.util.world.combat.ac.ACSettings;
import com.gamesense.api.util.world.combat.ac.CrystalInfo;
import com.gamesense.api.util.world.combat.ac.PlayerInfo;
import com.gamesense.api.util.world.combat.ac.threads.ACSubThread;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class ACCalculate
implements Callable<List<CrystalInfo.PlaceInfo>> {
    private final ACSettings settings;
    private final List<PlayerInfo> targets;
    private final List<BlockPos> blocks;
    private final long globalTimeoutTime;

    public ACCalculate(ACSettings settings, List<PlayerInfo> targets, List<BlockPos> blocks, long globalTimeoutTime) {
        this.settings = settings;
        this.targets = targets;
        this.blocks = blocks;
        this.globalTimeoutTime = globalTimeoutTime;
    }

    @Override
    public List<CrystalInfo.PlaceInfo> call() {
        return this.getPlayers(this.startThreads());
    }

    @Nonnull
    private List<Future<CrystalInfo.PlaceInfo>> startThreads() {
        ArrayList<Future<CrystalInfo.PlaceInfo>> output = new ArrayList<Future<CrystalInfo.PlaceInfo>>();
        for (PlayerInfo target : this.targets) {
            output.add(ACHelper.executor.submit(new ACSubThread(this.settings, this.blocks, target)));
        }
        return output;
    }

    private List<CrystalInfo.PlaceInfo> getPlayers(List<Future<CrystalInfo.PlaceInfo>> input) {
        ArrayList<CrystalInfo.PlaceInfo> place = new ArrayList<CrystalInfo.PlaceInfo>();
        for (Future<CrystalInfo.PlaceInfo> future : input) {
            while (!future.isDone() && !future.isCancelled() && System.currentTimeMillis() <= this.globalTimeoutTime) {
            }
            if (future.isDone()) {
                CrystalInfo.PlaceInfo crystal = null;
                try {
                    crystal = future.get();
                }
                catch (InterruptedException | ExecutionException exception) {
                    // empty catch block
                }
                if (crystal == null) continue;
                place.add(crystal);
                continue;
            }
            future.cancel(true);
        }
        if (this.settings.crystalPriority.equalsIgnoreCase("Health")) {
            place.sort(Comparator.comparingDouble(i -> -i.target.health));
        } else if (this.settings.crystalPriority.equalsIgnoreCase("Closest")) {
            place.sort(Comparator.comparingDouble(i -> -this.settings.player.entity.func_70068_e((Entity)i.target.entity)));
        } else {
            place.sort(Comparator.comparingDouble(i -> i.damage));
        }
        return place;
    }
}

