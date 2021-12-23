/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

@Module.Declaration(name="PhysicsSpammer", category=Category.Misc)
public class PhysicsSpammer
extends Module {
    IntegerSetting minDelay = this.registerInteger("Min Delay", 5, 1, 100);
    IntegerSetting maxDelay = this.registerInteger("Max Delay", 5, 1, 100);
    private final List<String> cache = new LinkedList<String>();
    private long lastTime;
    private long delay;
    private final Random random = new Random(System.currentTimeMillis());

    public PhysicsSpammer() {
        this.updateTimes();
    }

    @Override
    public void onUpdate() {
        if (this.delay > (long)Math.max((Integer)this.minDelay.getValue(), (Integer)this.maxDelay.getValue())) {
            this.delay = Math.max((Integer)this.minDelay.getValue(), (Integer)this.maxDelay.getValue());
        } else if (this.delay < (long)Math.min((Integer)this.minDelay.getValue(), (Integer)this.maxDelay.getValue())) {
            this.delay = Math.min((Integer)this.minDelay.getValue(), (Integer)this.maxDelay.getValue());
        }
        if (System.currentTimeMillis() >= this.lastTime + 1000L * this.delay) {
            if (this.cache.size() == 0) {
                try {
                    Scanner scanner = new Scanner(new URL("http://snarxiv.org/").openStream());
                    block2: while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (!line.startsWith("<p>") || line.startsWith("<p><a") || line.startsWith("<p>Links to:")) continue;
                        line = line.substring(3);
                        while (true) {
                            int pos;
                            if ((pos = line.indexOf(". ")) < 0) {
                                this.cache.add(line);
                                continue block2;
                            }
                            this.cache.add(line.substring(0, pos + 1));
                            line = line.substring(pos + 2);
                        }
                    }
                    scanner.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
            if (this.cache.size() == 0) {
                this.cache.add("Error! :(");
            }
            MessageBus.sendServerMessage("> " + this.cache.get(0));
            this.cache.remove(0);
            this.updateTimes();
        }
    }

    private void updateTimes() {
        this.lastTime = System.currentTimeMillis();
        int bound = Math.abs((Integer)this.maxDelay.getValue() - (Integer)this.minDelay.getValue());
        this.delay = (bound == 0 ? 0 : this.random.nextInt(bound)) + Math.min((Integer)this.maxDelay.getValue(), (Integer)this.minDelay.getValue());
    }
}

